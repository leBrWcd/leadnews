package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ArticleService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description 文章服务远程调用
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/20
 */
@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ArticleService arrticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return arrticleService.saveArticle(dto);
    }
}
