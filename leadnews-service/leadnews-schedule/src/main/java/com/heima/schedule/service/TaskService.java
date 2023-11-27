package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * Description 任务服务接口
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/27
 */
public interface TaskService {

    /**
     * 添加任务
     * @param task
     * @return
     */
    long addTask(Task task);

    /**
     * 取消任务
     * @param taskId
     * @return
     */
    boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级来拉取任务
     * @param type
     * @param priority
     * @return
     */
    Task pollTask(int type,int priority);

}
