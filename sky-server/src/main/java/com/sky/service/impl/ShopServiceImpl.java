package com.sky.service.impl;

import com.sky.mapper.ShopMapper;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/1
 */
@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    //
    // @Autowired
    // private ShopMapper shopMapper;
    //
    // /**
    //  * 更新店铺状态
    //  *
    //  * @param status 店铺状态
    //  */
    // @Override
    // public void update(Short status) {
    //     shopMapper.update(status);
    // }
    //
    // /**
    //  * 获取店铺状态
    //  *
    //  * @return
    //  */
    // @Override
    // public short getStatus() {
    //     short status = shopMapper.getStatus();
    //     return status;
    // }
}
