package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotBlank(message = "员工账号不能为空")
    private String username;

    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @NotBlank(message = "员工手机不能为空")
    private String phone;

    @NotBlank(message = "员工性别不能为空")
    private String sex;

    @NotBlank(message = "员工身份证不能为空")
    private String idNumber;

}
