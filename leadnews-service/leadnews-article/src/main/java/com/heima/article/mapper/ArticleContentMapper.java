package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description 文章内容mapper
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@Mapper
public interface ArticleContentMapper extends BaseMapper<ApArticleContent> {

    //文章详情
    /**
     * 接口只需要提供一个文章id，即可生成文章详情页面，结合Freemarker + MinIo
     */

}
