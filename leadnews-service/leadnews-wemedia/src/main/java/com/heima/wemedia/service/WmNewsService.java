package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.dtos.WmNewsUpDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @author Lebr7Wcd
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 上下架
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsUpDto dto);

    /**
     * 删除文章
     * @param id
     * @return
     */
    ResponseResult deleteNews(Integer id);

    /**
     * 发布/修改文章或保存为草稿
     * @param dto
     * @return
     */
    ResponseResult submitNews(WmNewsDto dto);
}