package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author lebrwcd
 * @date 2023/11/27
 * @note
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void addTask() {

        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100+i);
            task.setPriority(20);
            task.setParameters("task test".getBytes(StandardCharsets.UTF_8));
            task.setExecuteTime(new Date().getTime() + 500*i);

            long taskId = taskService.addTask(task);
            System.out.println("任务id" + taskId);
        }

    }

    @Test
    public void cancleTask() {

        taskService.cancelTask(1729051339352305665L);

    }

    @Test
    public void pollTest() {
        taskService.pollTask(100,20);
    }

}