package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

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

    /**
     * 保存或修改文章
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);
}
