package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Lebr7Wcd
 */
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

     /**
      * 批量保存内容/封面图片与素材的关系
      * @param materialIds 素材Id
      * @param newsId 文章id
      * @param type 类型
      */
     void saveRelations(@Param("materialIds") List<Integer> materialIds,@Param("newsId") Integer newsId, @Param("type")Short type);
}