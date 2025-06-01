package com.sky.controller.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@RestController
@RequestMapping("/images")
@Api(tags = "本地资源接口")
@Slf4j
public class LocalResourceController {

    @GetMapping(value = "/{Photo}")
    @ApiOperation("获取本地PNG图片")
    public ResponseEntity<byte[]> getImage(@PathVariable("Photo") String file) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get("D:/images/" + file));
        } catch (IOException e) {
            log.error("文件读取失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }

}
