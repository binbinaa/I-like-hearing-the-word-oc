package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
* 定时任务类，定时处理订单状态
*
* */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /*
    *
    * 处理超时订单
    * */
    @Scheduled(cron = "0 * * * * ?" )//每分钟执行一次
    //@Scheduled(cron = "1/5 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理超时订单", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }


    //每天凌晨一点触发一次
    @Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(cron = "0/5 * * * * ?") 5秒执行一次
    public void processDeliveryTimeOutOrders() {
        log.info("定时处理状态为派送中的订单",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);
        if(ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                //orders.setCancelReason("派送超时");
                //orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}
