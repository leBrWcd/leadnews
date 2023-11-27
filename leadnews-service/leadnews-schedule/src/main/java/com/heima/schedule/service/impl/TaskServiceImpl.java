package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Description 任务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/27
 */
@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {


    @Override
    public long addTask(Task task) {
        // 1、添加任务到数据库
        boolean isSuccess = addTaskToDB(task);
        log.info("是否添加到数据库成功? {}",isSuccess );
        if (isSuccess) {
            // 2、添加任务到redis
            addTaskToCache(task);
        }
        // 返回任务的id
        return task.getTaskId();
    }


    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;
        // 1、删除任务，更新任务日志状态
        Task task  = updateTaskFromDb(taskId,ScheduleConstants.CANCELLED);
        // 2、删除redis中的数据
        if (task != null) {
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }


    @Override
    public Task pollTask(int type, int priority) {

        Task task = null;
        try {
            // 1.消费任务，从List中pop
            String key = ScheduleConstants.TOPIC + type + "_" + priority;
            String taskStr = cacheService.lRightPop(key);
            task = JSON.parseObject(taskStr, Task.class);
            // 2.删除任务，更新日志
            updateTaskFromDb(task.getTaskId(),ScheduleConstants.EXECUTED);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception,{}",e.getMessage());
        }
        return task;
    }

    /**
     * 定时任务，刷新未来任务数据到List
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refreshTask() {
        log.info("定时任务【刷新未来任务为可消费任务】开始执行...(每分钟执行1次)");
        // 1.获取所有的未来数据 scan ("future *)
        Set<String> futureTasks = cacheService.scan(ScheduleConstants.FUTURE + "*");

        // 2.根据task的分值进行判断，小于等于当前时间，则刷新为可消费任务
        futureTasks.forEach( e -> {
            String topicKey = ScheduleConstants.TOPIC + e.split(ScheduleConstants.FUTURE)[1]; // topic_100_20
            Set<String> tasks = cacheService.zRangeByScore(e, 0, System.currentTimeMillis());
            if (!tasks.isEmpty()) {
                //通过管道技术，将set中的数据放入到list
                cacheService.refreshWithPipeline(e,topicKey,tasks);
                log.info("成功的将" + e + "下的当前需要执行的任务数据刷新到" + topicKey + "下");
            }
        });
    }


    /**
     * 从缓存中删除任务
     * @param task 待删除的任务
     */
    private void removeTaskFromCache(Task task) {

        long executeTime = task.getExecuteTime();
        // 当前时间
        long currentTime = System.currentTimeMillis();
        String key = task.getTaskType() + "_" + task.getPriority();
        String value = JSON.toJSONString(task);
        // 2.1、如果任务的执行实际小于等于当前时间，存入list
        if (executeTime <= currentTime) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key,0,value);
        } else  {
            cacheService.zRemove(ScheduleConstants.FUTURE,value);
        }

    }

    /**
     * 删除任务，更新日志状态
     * @param taskId 任务id
     * @param status 状态
     * @return
     */
    private Task updateTaskFromDb(long taskId, int status) {
        // 1、删除任务
        taskinfoMapper.deleteById(taskId);
        // 2、更新日志
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);

        Task task = new Task();
        BeanUtils.copyProperties(taskinfoLogs,task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());

        return task;
    }

    @Autowired
    private CacheService cacheService;

    /**
     * 添加任务到缓存中（redis）
     * @param task
     */
    private void addTaskToCache(Task task) {
        // 任务执行时间
        long executeTime = task.getExecuteTime();
        // 当前时间
        long currentTime = System.currentTimeMillis();
        // 存入值
        String value = JSON.toJSONString(task);
        String key = task.getTaskType() + "_" + task.getPriority();

        // 预设时间（未来5分钟）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long futureTime = calendar.getTimeInMillis();

        // 2.1、如果任务的执行实际小于等于当前时间，存入list
        if (executeTime <= currentTime) {
            key = ScheduleConstants.TOPIC + key;
            cacheService.lLeftPush(key,value);
        } else if (executeTime <= futureTime) {
            // 2.2、如果任务的执行时间大于当前时间且小于等于预设时间，则存入zset（以执行时间为score）
            key = ScheduleConstants.FUTURE + key;
            cacheService.zAdd(key,value,executeTime);
        }

    }

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    /**
     * 保存任务到数据库中
     * @param task
     * @return
     */
    private boolean addTaskToDB(Task task) {

        boolean flag = false;

        try {
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            // 回填taskId
            task.setTaskId(taskinfo.getTaskId());

            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo,taskinfoLogs);
            // 版本号，初始状态为1
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
