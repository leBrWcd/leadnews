package com.heima.user;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Description 用户应用
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.user.mapper")
@Slf4j
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
        log.info("================项目启动，接口地址: http://localhost:51801/doc.html");
    }
}
