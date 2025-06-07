package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/7
 */
@Component
@ConfigurationProperties(prefix = "sky.shop")
@Data
public class ShopProperties {

    // 店铺地址
    private String address;

}
