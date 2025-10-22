package com.sky.config;


import com.sky.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio核心配置类
 * 通过注入 MinIO 服务器的相关配置信息，得到 MinioClient 对象，我们上传文件依赖此对象
 */
@Configuration
@Slf4j
public class MinioConfiguration {
    @Autowired
    private MinioProperties minioProperties;

    @Bean
    @ConditionalOnMissingBean    //保证只创建了一个util对象，没有这种bean的时候再去创建
    public MinioClient minioClient() {
        log.info("开始创建MinioClient对象...");
        return MinioClient.builder().endpoint(minioProperties.getEndpoint()).
                credentials(minioProperties.getAccesskey(), minioProperties.getSecretkey()).build();
    }

}
