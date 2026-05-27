package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /*
    * 新增菜品
    * */
    @Override
    @Transactional
    public void saveWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //插入一条套餐数据
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        //向菜品表插入多条数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //批量插入
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /*
    * 分页查询
    * */

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        Page<SetmealVO> p= (Page<SetmealVO>) page;
        return new PageResult(p.getTotal(), p.getResult());
    }

    /*
    *
    * 删除菜品
    * */

    @Override
    @Transactional
    public void delete(List<Long> ids) {
    //1.判断当前套餐是否可以删除--套餐状态
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        /*//2.判断当前套餐是否关联了菜品
            List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealIds(ids);
            if(dishIds != null && dishIds.size() > 0){
            //当前套餐有菜品关联，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }*/
        //根据套餐id批量的删除套餐数据
        setmealMapper.deleteByIds(ids);
        //根据套餐id批量的删除套餐关联的菜品数据
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    /*
    * 修改套餐
    * */

    @Override
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐基本信息
        setmealMapper.update(setmeal);
        //修改套餐关联的菜品信息，先删除后添加
        setmealDishMapper.deleteBySetmealIds(Arrays.asList(setmealDTO.getId()));
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty())
        {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public SetmealVO getByIdWithDishes(Long id) {
        //根据套餐id查询基本套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        //根据套餐id查询菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    /*
    * 起售或停售
    * */

    @Override
    public void startOrStop(Integer status, Long id) {
    Setmeal setmeal = Setmeal.builder()
            .id(id)
            .status(status)
            .build();
    setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }




}
