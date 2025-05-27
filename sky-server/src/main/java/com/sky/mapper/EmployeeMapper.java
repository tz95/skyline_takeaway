package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    /**
     * 根据Employee对象插入新员工记录
     * @param emp
     */
    @Insert("INSERT INTO " +
            "employee (`name`,`username`,`password`," +
            "`phone`,`sex`,`id_number`,`status`,`create_time`" +
            ",`update_time`,`create_user`,`update_user`) " +
            "VALUES " +
            "(#{name},#{username},#{password},#{phone}," +
            "#{sex},#{idNumber},#{status},#{createTime}," +
            "#{updateTime},#{createUser},#{updateUser})" )
    void addEmployee(Employee emp);

    /**
     * 分页查询员工
     * @param empPageQueryDTO
     * @return
     */

    Page<Employee> pageQuery(EmployeePageQueryDTO empPageQueryDTO);
}
