package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录",produces = "application/json")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDto
     * @return JSON形式的的员工添加的结果
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<String> addEmployee(@RequestBody EmployeeDTO employeeDto) {
        log.info("添加员工：{}", employeeDto);

        employeeService.addEmployee(employeeDto);

        return Result.success("员工添加成功");
    }

    /**
     * 员工分页查询
     * @param empPageQueryDTO
     * @return JSON格式的分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> page(@RequestBody EmployeePageQueryDTO empPageQueryDTO) {
        log.info("分页查询员工,参数为:{}", empPageQueryDTO);
        // 这里可以添加分页查询逻辑
        PageResult pageResult = employeeService.pageQuery(empPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 切换员工账号状态(启用/禁用)
     * @param status 状态
     * @param id 员工id
     * @return JSON格式的结果
     */
    @PostMapping( value = "/status/{status}", headers = "Accept=application/json")
    @ApiOperation("切换员工账号状态(启用/禁用)")
    public Result<String> switchAccountStatus(@PathVariable("status") Integer status,@RequestParam("id") Long id){
        log.info("切换员工状态信息: 传入参数 status = {}, 员工id = {}", status, id);
        // 调用服务层方法切换员工状态
        employeeService.switchAccountStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据员工id查询员工")
    public Result<Employee> getById(@PathVariable("id") Long id){
        log.info("根据员工id查询员工: {}", id);
        // 调用服务层方法根据id查询员工
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation("更新员工信息")
    public Result<String> updateEmployee(@RequestBody EmployeeDTO employeeDto) {
        log.info("更新员工信息: {}", employeeDto);
        // 调用服务层方法更新员工信息
        employeeService.updateEmployee(employeeDto);
        return Result.success();
    }

}
