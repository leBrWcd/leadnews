package com.heima.file.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Description Minio配置文件解析
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioFileProperties implements Serializable {

    private String accessKey;   //公钥
    private String secretKey;   //密钥
    private String bucket;      //桶名称
    private String endpoint;    //节点
    private String readPath;    //路径

}
