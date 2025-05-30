package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐菜品关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    private Long setmealId;

    //菜品id
    @NotBlank
    private Long dishId;

    //菜品名称 （冗余字段）
    @NotBlank
    private String name;

    //菜品原价
    @NotBlank
    private BigDecimal price;

    //份数
    @NotBlank
    private Integer copies;
}
