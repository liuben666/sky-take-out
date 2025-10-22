package com.sky.utils;

import com.sky.properties.MinioProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import io.minio.http.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioProperties minioProperties;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccesskey(), minioProperties.getSecretkey())
                .build();
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    public void createBucket(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        }
    }

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取文件信息
     *
     * @param bucketName 桶名称
     * @param objectName 文件名称
     * @return ObjectStat 文件信息
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @param stream     流
     * @param fileSize   文件大小
     * @param type       文件类型
     * @throws Exception
     */
    public void putObject(String bucketName, String objectName, InputStream stream, Long fileSize, String type) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                stream, fileSize, -1)
                        .contentType(type)
                        .build());
    }

    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 桶名称
     * @param prefix     文件夹名字
     * @return Boolean 是否存在
     */
    public Boolean folderExists(String bucketName, String prefix) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName)
                .prefix(prefix).recursive(false).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param bucketName 桶名称
     * @param path       路径
     */
    public void createFolder(String bucketName, String path) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(path)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1).build());
    }

    /**
     * 获取文件在minio在服务器上的外链
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @return String 外链URL
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}
