package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /*
    * 新增套餐
    * */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO)
    {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDishes(setmealDTO);
        return Result.success();
    }

    /*
    * 套餐分页查询
    *
    * */

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO)
    {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    /*
    * 套餐批量删除
    *
    * */
    @DeleteMapping
    @ApiOperation("套餐批量删除")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids)//@RequestParam注解表示将请求参数绑定到方法参数上
    {
        log.info("批量删除套餐，ids：{}", ids);
        setmealService.delete(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO)
    {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateWithDishes(setmealDTO);
        return Result.success();
    }

    /*
    * 根据id获取套餐
    * */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")

    public Result<SetmealVO> getById(@PathVariable Long id)
    {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setmealService.getByIdWithDishes(id);
        return Result.success(setmealVO);
    }
    /*
    * 起售停售
    * */
    @ApiOperation("启用禁用")
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用：{}", id);
       setmealService.startOrStop(status, id);
        return Result.success();
    }


}
