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
        if (!password.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

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
        // 3、设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 4、设置创建人和更新人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        BaseContext.removeCurrentId();

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
        // 设置更新时间和更新人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
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
        Employee emp = employeeMapper.getById(id);
        if (emp == null) {
            throw new AccountNotFoundException("emp.id:"+id+MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return emp;
    }


}
