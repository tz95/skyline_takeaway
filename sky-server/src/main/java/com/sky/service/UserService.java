package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/2
 */
public interface UserService {


    /**
     * 微信登录
     * @param userLoginDTO 用户登录数据传输对象
     * @return User对象
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
