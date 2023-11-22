package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Description 文章服务调用接口，暴露给其他微服务调用的，实现方法在对应的服务上
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/20
 */
@FeignClient(value = "leadnews-article",fallback = IArticleClientFallback.class) //微服务名称
public interface IArticleClient {

    /**
     * 保存或修改文章
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto dto) ;
}
