package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.exception.FileUploadFailureException;
import com.sky.result.Result;
import com.sky.utils.LocalOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共接口")
@Slf4j
public class CommonController {

    @Autowired
    private LocalOssUtil localOssUtil;

    /**
     * 文件上传
     * @param file 上传的文件
     * @return 文件资源url
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("上传文件：{}", file.getOriginalFilename());
        String url;
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成唯一的文件名
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            url = localOssUtil.save(file.getBytes(), uniqueFileName);

            return Result.success(url);
        } catch (Exception e) {
            throw new FileUploadFailureException(e.getMessage());
        }
    }

}
