package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmAutoScanNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Description TODO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/28
 */
@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private WmAutoScanNewsService wmAutoScanNewsService;

    @Override
    @Async
    public void addTask(Integer id, Date publishTime) {

        log.info("文章审核任务添加到延迟队列 -------begin");

        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        scheduleClient.addTask(task);

        log.info("文章审核任务添加到延迟队列 -------end");
    }

    //@Scheduled(fixedDelay = 1000)
    @Override
    public void pollTask() {

        log.info("【定时任务：消费文章审核任务开始--】");

        ResponseResult responseResult = scheduleClient.pollTask(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (responseResult.getCode().equals(200) && responseResult.getData() != null) {
            try {
                String taskJsonStr = JSON.toJSONString(responseResult.getData());
                Task task = JSON.parseObject(taskJsonStr, Task.class);
                byte[] parameters = task.getParameters();
                WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
                wmAutoScanNewsService.autoScanNews(wmNews.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.info("【定时任务：消费文章审核任务完成--completed】");

    }
}
