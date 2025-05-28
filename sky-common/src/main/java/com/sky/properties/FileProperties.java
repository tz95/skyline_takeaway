package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@Component
@ConfigurationProperties(prefix = "sky.file")
@Data
public class FileProperties {
    private String localOssPath;
    private String localOssUrl;
}
