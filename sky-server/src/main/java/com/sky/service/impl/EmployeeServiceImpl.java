package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.EmployeePwdDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // 对前端传入的明文密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        equalPassword(password, employee);

        if (employee.getStatus() == StatusConstant.DISABLE) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return employee;
    }

    /**
     * 根据传入的employeeDto添加员工
     *
     * @param employeeDto
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        // 1、设置员工状态为启用
        employee.setStatus(StatusConstant.ENABLE);
        // 2、设置默认密码并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        /*// 3、设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 4、设置创建人和更新人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        // 5、调用mapper添加员工
        employeeMapper.addEmployee(employee);
    }

    /**
     * 根据传入的empPageQueryDTO进行分页查询
     *
     * @param empPageQueryDTO
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO empPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(empPageQueryDTO.getPage(), empPageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(empPageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        PageHelper.clearPage();

        return new PageResult(total, records);
    }

    /**
     * 根据传入的id和status切换员工账号状态(启用/禁用)
     *
     * @param status 账号状态
     * @param id     员工id
     */
    @Override
    public void switchAccountStatus(Integer status, Long id) {
        employeeMapper.update(Employee.builder()
                .id(id)
                .status(status)
                .build());
    }

    /**
     * 更新员工信息
     *
     * @param employeeDto 员工DTO对象
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        /*// 设置更新时间和更新人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());*/
        employeeMapper.update(employee);
    }

    /**
     * 根据员工id查询员工信息
     *
     * @param id 员工id
     * @return 员工信息
     */
    @Override
    public Employee getById(Long id) {
        Employee emp = getEmpById(id);
        // 返回时不包含密码信息
        emp.setPassword(null);
        return emp;
    }

    /**
     * 修改员工密码
     *
     * @param employeePwdDTO 员工密码DTO对象
     */
    @Override
    public void editPassword(EmployeePwdDTO employeePwdDTO) {
        Employee emp = getEmpById(employeePwdDTO.getEmpId());
        if (emp == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        equalPassword(DigestUtils.md5DigestAsHex(employeePwdDTO.getOldPassword().getBytes()), emp);
        // 对新密码进行MD5加密
        emp.setPassword(DigestUtils.md5DigestAsHex(employeePwdDTO.getNewPassword().getBytes()));
        /* // 设置更新时间和更新人
        emp.setUpdateTime(LocalDateTime.now());
        emp.setUpdateUser(BaseContext.getCurrentId());*/

        employeeMapper.update(emp);
    }

    /**
     * 比较前端传入的密码和数据库中存储的密码是否一致
     * @param password 前端传入的明文密码
     * @param emp 数据库中查询到的员工对象
     * @return true表示密码一致，false表示密码不一致
     */
    private void equalPassword(String password, Employee emp) {
        boolean flag = password.equals(emp.getPassword());
        if (!flag) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
    }

    private Employee getEmpById(Long id){
        Employee emp = employeeMapper.getById(id);
        emp.setPassword(null);
        if (emp == null) {
            throw new AccountNotFoundException("emp.id:"+id+MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return emp;
    }

}
