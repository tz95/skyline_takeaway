package com.sky.config;

import com.sky.properties.FileProperties;
import com.sky.utils.LocalOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@Configuration
@Slf4j
public class LocalOssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalOssUtil localOssUtil(FileProperties fileProperties) {
        log.info("本地文件上传工具类创建：{}", fileProperties);
        return new LocalOssUtil(fileProperties.getLocalOssPath(),
                fileProperties.getLocalOssUrl());
    }
}
