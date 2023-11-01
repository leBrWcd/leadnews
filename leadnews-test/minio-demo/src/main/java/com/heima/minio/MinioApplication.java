package com.heima.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description MinIO测试类启动
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@SpringBootApplication
public class MinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class,args);
    }
}
