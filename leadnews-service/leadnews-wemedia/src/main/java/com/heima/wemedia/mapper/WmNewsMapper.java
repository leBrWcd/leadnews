package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lebr7Wcd
 */
@Mapper
public interface WmNewsMapper  extends BaseMapper<WmNews> {

    /**
     * 根据条件进行分页查询
     * @param dto 分页条件
     * @return List
     */
    List<WmNews> selectListByCondition(@Param("map") Map dto);
}