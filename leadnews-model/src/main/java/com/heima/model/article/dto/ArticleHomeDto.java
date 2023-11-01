package com.heima.model.article.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * Description 文章列表首页数据传输对象
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/29
 */

@Data
@ToString
@NoArgsConstructor
public class ArticleHomeDto {

    /**
     * 最大时间
     */
    Date maxBehotTime;
    /**
     * 最小时间
     */
    Date minBehotTime;
    /**
     * 大小
     */
    Integer size;
    /**
     * 频道ID
     */
    String tag;
}