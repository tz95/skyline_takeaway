package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    /**
     * 批量更新套餐状态
     * @param setmealIds 套餐ID列表
     */
    @AutoFill(OperationType.UPDATE)
    void updateByIds(Setmeal setmeal, List<Long> setmealIds);

    /**
     * 插入新的套餐
     * @param setmeal 套餐实体
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    Page<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据ID查询套餐
     * @param id 套餐ID
     * @return 套餐视图对象
     */
    SetmealVO getById(Long id);

    /**
     * 根据id返回套餐列表
     * @param ids 套餐ID列表
     */
    List<Setmeal> getByIds(List<Long> ids);

    /**
     * 批量删除套餐
     * @param ids 套餐ID列表
     */
    void deleteByIds(List<Long> ids);
}
