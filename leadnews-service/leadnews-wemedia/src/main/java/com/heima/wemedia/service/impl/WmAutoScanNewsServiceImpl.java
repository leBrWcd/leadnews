package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmAutoScanNewsService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 审核文章内容
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/22
 * @description :
 *  审核文章的业务流程：
 *  1、根据文章id查询自媒体文章
 *      1.1、如果查询不到，则抛出异常
 *  2、如果查询到了，进行文章的审核
 *      2.1 文章文本的审核（阿里云接口）
 *      2.2 如果审核失败，更新文章状态为审核失败，直接return，不需要再进行下面的操作
 *      2.3 文章图片的审核，包括内容图片和封面图片（阿里云接口、MinIO）
 *      2.4 如果审核失败，更新文章状态为审核失败，直接return，不需要再进行下面的操作
 *  3、都审核成功的话，进行app端文章相关保存（ApArticle、ApArticle_config，ApArticle_content），
 *  4、最终修改文章的状态为审核成功（9）
 *
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class WmAutoScanNewsServiceImpl implements WmAutoScanNewsService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Async  // 异步调用
    @Override
    public void autoScanNews(Integer id) throws IOException {

        // 1、查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmAutoScanNewsServiceImpl-id："+ id + "的自媒体文章不存在");
        }
        // 从自媒体文章中的content抽取文本和图片
        //{"type":"image","value":"http://192.168.200.130:9000/leadnews/2021/4/20210418/a3f0bc438c244f788f2df474ed8ecdc1.jpg"}]
        //{"type":"text","value":"测试测试"}]

        Map<String,Object> textAndImages = handleTextAndImages(wmNews);

        // 待审核状态才需要审核
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            // 2、审核文本内容
            String text = (String) textAndImages.get("text");
            // 新增需求：自管理的敏感词汇过滤
            boolean isSensitiveScan = handleSensitiveScan(text,wmNews);
            log.info("---------isSensitiveScan:{}",isSensitiveScan);
            if (!isSensitiveScan) {
                return;
            }
            boolean isTextScan = handleTextScan(text,wmNews);
            log.info("---------isTextScan:{}",isTextScan);
            if (!isTextScan){
                return;
            }
            // 3.审核图片内容
            List<String> images = (List<String>) textAndImages.get("images");
            boolean isImagesScan = handleImagesScan(images,wmNews);
            log.info("---------isImageScan:{}",isImagesScan);
            if (!isImagesScan){
                return;
            }
            // 4.保存相关文章
            ResponseResult responseResult = saveAppArticle(wmNews);
            log.info("=========response code : {}",responseResult.getCode());
            log.info("=========errorMsg========{}",responseResult.getErrorMessage());
            if(!responseResult.getCode().equals(200)){
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            // 保存成功
            // WmNews回填文章id
            wmNews.setArticleId((Long) responseResult.getData());
            updateNewsStatus(wmNews,WmNews.Status.PUBLISHED.getCode(), "审核成功");
        }


    }

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    /**
     * 自管理的敏感词汇过滤
     * @param text
     * @param wmNews
     * @return
     */
    private boolean handleSensitiveScan(String text, WmNews wmNews) {

        boolean flag = true;
        // 查询敏感词汇列表
        List<String> sensitiveList = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives))
                .stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        // 初始化敏感词汇字典
        SensitiveWordUtil.initMap(sensitiveList);
        // 匹配是否存在敏感词汇
        Map<String, Integer> map = SensitiveWordUtil.matchWords(text);
        if (map.size() > 0) {
            // 有敏感词汇
            updateNewsStatus(wmNews,WmNews.Status.FAIL.getCode(),"当前文本存在敏感内容:" + map);
            flag = false;
        }
        return flag;
    }

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper userMapper;

    /**
     * 保存app端文章
     * @param wmNews 文章
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {

        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);

        // 文章的布局
        articleDto.setLayout(wmNews.getType());
        // 频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            articleDto.setChannelName(wmChannel.getName());
        }
        // 作者
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = userMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            articleDto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if(wmNews.getArticleId() != null){
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());

        log.info("=====articleClient:{}",articleClient);
        // 自媒体微服务远程调用App端文章微服务保存文章
        ResponseResult responseResult = articleClient.saveArticle(articleDto);
        log.info("WmAutoScanNewsServiceImpl-saveAppArticle-responseresult:{}",responseResult.getData() );
        return responseResult;
    }

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Tess4jClient tess4jClient;

    /**
     * 审核图片内容
     * @param images 内容图片和封面图片
     * @param wmNews 自媒体文章
     * @return 布尔值
     */
    private boolean handleImagesScan(List<String> images, WmNews wmNews) throws IOException {
        boolean flag = true;
        if (images == null || images.size() == 0) {
            return flag;
        }

        List<byte[]> imageList = new ArrayList<>();
        // 从MinIO下载图片
        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);

            //从byte[]转换为butteredImage
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            //识别图片的文字
            String result = null;
            try {
                BufferedImage imageFile = ImageIO.read(in);
                result = tess4jClient.doOCR(imageFile);
            } catch (TesseractException e) {
                e.printStackTrace();
            }

            //审核是否包含自管理的敏感词
            boolean isSensitive = handleSensitiveScan(result, wmNews);
            if(!isSensitive){
                return isSensitive;
            }

            //图片识别文字审核---end-----
        }
        try {
            Map map = greenImageScan.imageScan(imageList);
            if(map != null){
                //审核失败
                if(map.get("suggestion").equals("block")){
                    flag = false;
                    updateNewsStatus(wmNews, WmNews.Status.FAIL.getCode(), "当前图片中存在违规内容");
                }

                //不确定信息  需要人工审核
                if(map.get("suggestion").equals("review")){
                    flag = false;
                    updateNewsStatus(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前图片中存在不确定内容");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    @Autowired
    private GreenTextScan greenTextScan;

    /**
     * 审核文本内容
     * @param text 文本
     * @param wmNews 自媒体文章
     * @return 布尔值
     */
    private boolean handleTextScan(String text, WmNews wmNews) {
        boolean flag = true;
        if ((wmNews.getTitle()+""+text).length() == 0 ) {
            // 标题和文本都为空，直接返回
            return flag;
        }
        // 审核文本
        try {
            Map map = greenTextScan.greeTextScan(wmNews.getTitle() + "" + text);
            if (map != null) {
                if ("block".equals(map.get("suggestion"))) {
                    // 审核失败
                    flag = false;
                    updateNewsStatus(wmNews,WmNews.Status.FAIL.getCode(), "当前文章存在违规内容");
                }
                if ("review".equals(map.get("suggestion"))) {
                    // 人工审核
                    flag = false;
                    updateNewsStatus(wmNews,WmNews.Status.ADMIN_AUTH.getCode(), "当前文章存在不确定内容，请人工审核");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 更新自媒体文章的状态和原因
     * @param wmNews 文章
     * @param status 状态
     * @param reason 原因
     */
    private void updateNewsStatus(WmNews wmNews,short status,String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 从自媒体文章抽取文本和图片（内容图片和封面图片）
     * @param wmNews 自媒体文章
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        StringBuilder text = new StringBuilder();
        List<String> images = new ArrayList<>();

        Map<String,Object> resultMap = new HashMap<>();

        String content = wmNews.getContent();
        if (!StringUtils.isEmpty(content)) {
            List<Map> maps = JSONArray.parseArray(content, Map.class);
            for (Map map : maps) {
                if ("text".equals(map.get("type"))) {
                    text.append(map.get("value"));
                }
                if ("image".equals(map.get("type"))) {
                    images.add((String) map.get("value"));
                }
            }
        }
        // 提取文章的封面图片
        String imagesStr = wmNews.getImages();
        if (!StringUtils.isEmpty(imagesStr)) {
            String[] split = imagesStr.split(",");
            images.addAll(Arrays.asList(split));
        }

        resultMap.put("text",text.toString());
        resultMap.put("images",images);

        return resultMap;
    }
}
