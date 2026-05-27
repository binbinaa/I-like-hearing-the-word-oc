package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /*
    * 根据菜品id集合查询对应套餐id集合
    * */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /*
    * 批量插入套餐和菜品的关联关系
    * */
    void insertBatch(List<SetmealDish> setmealDishes);
    /*
    * 根据套餐id得到菜品id集合
    * */
    List<Long> getDishIdsBySetmealIds(List<Long> ids);
    /*
    * 根据套餐id删除套餐和菜品的关联关系
    * */
    void deleteBySetmealIds(List<Long> ids);
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}
