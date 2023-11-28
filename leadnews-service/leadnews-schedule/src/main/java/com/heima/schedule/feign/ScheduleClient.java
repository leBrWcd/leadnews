package com.heima.schedule.feign;

import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description 远程接口调用
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/28
 */
@RestController
public class ScheduleClient implements IScheduleClient {

    @Autowired
    private TaskService taskService;

    @PostMapping("/api/vi/task/add")
    @Override
    public ResponseResult addTask(@RequestBody Task task){
        return ResponseResult.okResult(taskService.addTask(task));
    }


    @GetMapping("/api/v1/task/{taskId}")
    @Override
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId){
        return ResponseResult.okResult(taskService.cancelTask(taskId));
    }


    @GetMapping("/api/v1/task/{type}/{priority}")
    @Override
    public ResponseResult pollTask(@PathVariable("type")int type,@PathVariable("priority") int priority) {
        return ResponseResult.okResult(taskService.pollTask(type,priority));
    }
}
