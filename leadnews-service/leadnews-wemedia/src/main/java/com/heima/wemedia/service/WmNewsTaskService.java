package com.heima.wemedia.service;

import java.util.Date;

/**
 * Description 文章任务服务
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/28
 */
public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列
     * @param id 文章id
     * @param publishTime 文章发布时间
     */
    public void addTask(Integer id, Date publishTime);

    /**
     * 消费文章
     */
    public void pollTask();

}
