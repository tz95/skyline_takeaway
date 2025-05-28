package com.sky.utils;

import com.sky.exception.FileUploadFailureException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@Data
@AllArgsConstructor
@Slf4j
public class LocalOssUtil {

    private String localOssPath;
    private String localOssUrl;

    /**
     * 文件保存
     *
     * @param bytes      文件字节数组
     * @param objectName 文件名
     * @return 文件访问路径
     */
    public String save(byte[] bytes, String objectName){

        // 拼接文件路径
        String filePath = localOssPath + objectName;

        try {
            // 将字节数组写入文件
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), bytes);
        } catch (java.io.IOException e) {
            throw new FileUploadFailureException(e.getMessage());
        }

        log.info("文件上传到: {}", filePath);

        StringBuilder stringBuilder = new StringBuilder(localOssUrl)
                .append("/")
                .append("images/")
                .append(objectName);

        log.info("文件访问路径: {}", stringBuilder.toString());

        // 返回文件访问路径
        return stringBuilder.toString();

    }

}
