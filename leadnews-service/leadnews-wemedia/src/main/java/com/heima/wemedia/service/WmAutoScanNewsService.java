package com.heima.wemedia.service;

import java.io.IOException;

/**
 * Description 自媒体文章审核服务
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/22
 */
public interface WmAutoScanNewsService {

    /**
     * 审核文章
     * @param id 文章id
     */
    void autoScanNews(Integer id) throws IOException;

}
