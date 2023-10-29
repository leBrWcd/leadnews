package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;

import java.util.List;

/**
 * Description 文章服务类接口
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/29
 */
public interface ArticleService extends IService<ApArticle> {

    /**
     * 文章列表
     * @param dto dto
     * @param type 类型
     * @return list
     */
    List<ApArticle> loadArticleList(ArticleHomeDto dto, Short type);
}
