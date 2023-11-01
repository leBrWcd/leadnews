package com.heima.article.controller.v1;

import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description 文章控制器
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/29
 */
@RestController
@RequestMapping("/api/v1/article/")
public class ApArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 列表页默认加载10条
     * @param dto 默认频道展示10条文章信息
     * @return ResponseResult
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
        return ResponseResult.okResult(articleService.loadArticleList(dto, ArticleConstants.LOADTYPE_LOAD_MORE));
    }

    /**
     * 加载更多
     * @param dto （按照发布时间）本页文章列表中发布时间最小的时间为依据
     * @return ResponseResult
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return ResponseResult.okResult(articleService.loadArticleList(dto, ArticleConstants.LOADTYPE_LOAD_MORE));
    }

    /**
     * 加载最新
     * @param dto 本页文章列表中发布时间为最大的时间为依据
     * @return ResponseResult
     */
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return ResponseResult.okResult(articleService.loadArticleList(dto, ArticleConstants.LOADTYPE_LOAD_NEW));
    }

}
