package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.dtos.WmNewsUpDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmAutoScanNewsService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 自媒体端内容业务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/10
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMapper newsMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 查询文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {

        //1.检查参数
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //获取当前登录人的信息
        WmUser user = WmThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //2.分页条件查询
        IPage page = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //状态精确查询
        if(dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        //频道精确查询
        if(dto.getChannelId() != null){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        //时间范围查询
        if(dto.getBeginPubDate()!=null && dto.getEndPubDate()!=null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        //关键字模糊查询
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }
        // 逻辑删除
        //查询当前登录用户的文章
        lambdaQueryWrapper.eq(WmNews::getUserId,user.getId())
                .ne(WmNews::getDelFlag,"1");
        //发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        page = page(page,lambdaQueryWrapper);
        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult downOrUp(WmNewsUpDto dto) {
        Integer id = dto.getId();
        WmNews wmNew = lambdaQuery().eq(WmNews::getId, id).one();
        wmNew.setEnable(dto.getEnable());
        int update =  baseMapper.updateById(wmNew);
        if (update == 1) {
            log.info("更新成功");
            return ResponseResult.okResult(wmNew);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult deleteNews(Integer id) {
        WmNews wmNew = lambdaQuery().eq(WmNews::getId, id).one();
        int delete = baseMapper.deleteById(id);
        /**
         * 如果对象的逻辑删除字段添加了@TableLogic注解，则不需要手动更新该字段（以下两行代码），而是直接调用delete方法，底层自动更新逻辑删除字段
         */
        //wmNew.setDelFlag((short) 1);
        //int update = baseMapper.updateById(wmNew);
        if (delete == 1) {
            log.info("删除文章内容成功");
            return ResponseResult.okResult(wmNew);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Autowired
    private WmAutoScanNewsService wmAutoScanNewsService;

    /**
     * 发布修改文章或保存为草稿
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) throws IOException {

        //0.条件判断
        if(dto == null || dto.getContent() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //1.保存或修改文章

        WmNews wmNews = new WmNews();
        //属性拷贝 属性名词和类型相同才能拷贝
        BeanUtils.copyProperties(dto,wmNews);
        //封面图片  list---> string
        if(dto.getImages() != null && dto.getImages().size() > 0){
            //[1dddfsd.jpg,sdlfjldk.jpg]-->   1dddfsd.jpg,sdlfjldk.jpg
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        //如果当前封面类型为自动 -1
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }

        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿  如果为草稿结束当前方法
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        //3.不是草稿，保存文章内容图片与素材的关系
        //获取到文章内容中的图片信息
        List<String> materials =  ectractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials,wmNews.getId());

        //4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto,wmNews,materials);

        // 发布成功后异步调用文章审核方法
        wmAutoScanNewsService.autoScanNews(wmNews.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * 第二个功能：保存封面图片与素材的关系
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            if(materials.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if(materials.size() >= 1 && materials.size() < 3){
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if(images != null && images.size() > 0){
            saveRelativeInfo(images,wmNews.getId(),WemediaConstants.WM_COVER_REFERENCE);
        }

    }


    /**
     * 处理文章内容图片与素材的关系
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials,newsId,WemediaConstants.WM_CONTENT_REFERENCE);
    }

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    /**
     * 保存文章图片与素材的关系到数据库中
     * @param materials
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if(materials!=null && !materials.isEmpty()){
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            //判断素材是否有效
            if(dbMaterials==null || dbMaterials.size() == 0){
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            if(materials.size() != dbMaterials.size()){
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            wmNewsMaterialMapper.saveRelations(idList,newsId,type);
        }

    }


    /**
     * 提取文章内容中的图片信息
     * @param content
     * @return
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();

        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")){
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }

        return materials;
    }

    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setDelFlag((short)0); // 默认未删除
        wmNews.setEnable((short)1);//默认上架

        if(wmNews.getId() == null){
            //保存
            save(wmNews);
        }else {
            //修改
            //删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }

    }

    /*
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {

        // 判断dto是否为空
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 校验参数
        dto.checkParam();
        Integer userId = WmThreadLocalUtil.getUser().getId();
        //Integer userId = 1102;  //postman测试
        Map<String, Object> params = new HashMap<>();
        params.put("status",dto.getStatus());
        params.put("keywords",dto.getKeyword());
        params.put("channelId",dto.getChannelId());
        params.put("beginPubDate",dto.getBeginPubDate());
        params.put("endPubDate",dto.getEndPubDate());
        params.put("pageSize",dto.getSize());
        params.put("index",(dto.getPage()-1)*dto.getSize());
        params.put("userId",userId);

        log.info("分页参数: {}",params);

        // 分页
        Page<WmNews> page = new Page<>(dto.getPage(),dto.getSize());
        List<WmNews> list = newsMapper.selectListByCondition(params);

        if (list != null) {
            page.setRecords(list);
            page.setTotal(list.size());
        }
        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return ResponseResult.okResult(responseResult);
    }*/


}
