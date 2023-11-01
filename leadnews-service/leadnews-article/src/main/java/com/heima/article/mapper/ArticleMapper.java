package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 文章mapper
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/29
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 文章列表
     * @param dto dto
     * @param type 类型
     * @return
     */
    public List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto,
                                           @Param("type") Short type);
}
