package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    /*
    * 新增菜品
    * */
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO)
    {
        log.info("新增菜品，菜品数据：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //删除缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache( key);
        return Result.success();

    }

    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO)
    {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids)//@RequestParam注解表示将请求参数绑定到方法参数上
    {
        log.info("删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        //将所有的菜品缓存数据清理掉，所有以dish_开头的
        cleanCache("dish_*");
        return Result.success();
    }


    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的
        cleanCache("dish_*");

        return Result.success();
    }
    /*
    * 启用禁用
    * */
    @ApiOperation("启用禁用")
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用：{}", id);
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的
        cleanCache("dish_*");
        return Result.success();
    }

    /*
    * 根据分类ID查询菜品
    * */
    @ApiOperation("根据分类ID查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类ID查询菜品：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    private void cleanCache(String pattern){
        log.info("清理缓存数据");
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
