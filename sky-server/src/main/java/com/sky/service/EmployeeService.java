package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.EmployeePwdDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 根据传入的employeeDto添加员工
     * @param employeeDto
     */
    void addEmployee(EmployeeDTO employeeDto);

    /**
     * 根据传入的empPageQueryDTO进行分页查询
     * @param empPageQueryDTO
     * @return 分页结果
     */
    PageResult pageQuery(EmployeePageQueryDTO empPageQueryDTO);

    /**
     * 根据传入的id和status切换员工账号状态(启用/禁用)
     * @param id 员工id
     * @param status 账号状态
     */
    void switchAccountStatus(Integer status, Long id);

    /**
     * 更新员工信息
     * @param employeeDto 员工DTO对象
     */
    void updateEmployee(EmployeeDTO employeeDto);

    /**
     * 根据员工id查询员工信息
     * @param id 员工id
     * @return 员工信息
     */
    Employee getById(Long id);

    /**
     * 修改员工密码
     * @param employeePwdDTO 员工密码DTO对象
     */
    void editPassword(EmployeePwdDTO employeePwdDTO);
}
