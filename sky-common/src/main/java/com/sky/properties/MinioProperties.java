package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sky.minio")
public class MinioProperties {
    //连接url
    private String endpoint;
    //公钥
    private String accesskey;
    //私钥
    private  String secretkey;
    //存储桶名称
    private String bucketName;
}
