package com.heima.file.conf;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description Minio配置类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@Configuration
@EnableConfigurationProperties({MinioFileProperties.class})
@Data
@ConditionalOnClass(FileStorageService.class)
public class MinioConfig {

    @Autowired
    private MinioFileProperties minioFileProperties;

    @Bean
    public MinioClient buildMinioClient(){
        return MinioClient
                .builder()
                .credentials(minioFileProperties.getAccessKey(), minioFileProperties.getSecretKey())
                .endpoint(minioFileProperties.getEndpoint())
                .build();
    }

}
