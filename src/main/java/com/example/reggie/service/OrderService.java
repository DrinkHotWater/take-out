package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Order;

public interface OrderService extends IService<Order> {
    /**
     * 用户提交订单
     * @param order
     */
    public void submit(Order order);
}
