package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.entity.Order;
import com.example.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户提交订单
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Order order) {
        log.info("订单数据：{}", order);
        orderService.submit(order);
        return R.success("下单成功");
    }
}
