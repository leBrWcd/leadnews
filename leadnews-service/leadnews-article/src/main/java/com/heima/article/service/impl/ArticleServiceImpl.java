package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Description 文章业务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/29
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService {

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     *
     * @param dto dto
     * @param type 类型 type == 1 加载更多/默认 type == 2 加载最新  tag =__all__ 默认
     * @return List
     */
    @Override
    public List<ApArticle> loadArticleList(ArticleHomeDto dto, Short type) {
        // 判断传入频道，如果为空或者传入参数不为1和2，默认是1
        if (type == null || (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW))) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        // 判断是否有设置分页大小
        Integer size = dto.getSize();
        if (size == null || size == 0) {
           size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);
        dto.setSize(size);
        // 判断频道是否有指定，如无指定，则为默认频道
        if (StringUtils.isEmpty(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        //时间校验
        if(dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        // 查询
        List<ApArticle> apArticles = articleMapper.loadArticleList(dto, type);
        if (!apArticles.isEmpty()) {
            return apArticles;
        }
        return null;
    }
}
