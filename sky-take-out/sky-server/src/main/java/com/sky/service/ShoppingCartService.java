package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /*
    * 加入购物车
    * */
    void add(ShoppingCartDTO shoppingCartDTO);

    /*
    * 查看购物车
    * */
    List<ShoppingCart> list();

    /*
    * 清空购物车
    * */
    void cleanShoppingCart();

    /*
    * 删除一条数据
    * */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
