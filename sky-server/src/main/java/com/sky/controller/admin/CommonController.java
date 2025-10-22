package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags="通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private MinioUtil minioUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            //防止文件名重复，截取原始文件名后再用uuid
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            minioUtil.putObject("cangqiongwaimai", objectName, file.getInputStream(), file.getSize(), file.getContentType());

            String filepath = minioUtil.getObjectUrl("cangqiongwaimai", objectName);
            return Result.success(filepath);
        } catch (Exception e) {
            log.error("文件上传失败:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
