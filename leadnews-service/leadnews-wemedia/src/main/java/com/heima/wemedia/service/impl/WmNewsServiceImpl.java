package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }
}
