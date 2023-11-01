package com.heima.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * Description 网关应用
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/21
 */

@SpringBootApplication
@EnableDiscoveryClient
public class AppGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppGateWayApplication.class,args);
    }

}
