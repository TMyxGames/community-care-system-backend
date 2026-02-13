package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Order;
import com.tmyx.backend.mapper.OrderMapper;
import com.tmyx.backend.util.Result;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    // 创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody Order order) {
        // 设置创建时间
        order.setCreateTime(LocalDateTime.now());
        // 生成订单号
        String timePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sn = timePrefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderSn(sn);
        // 设置订单初始状态
        order.setState(1);

        try {
            int result = orderMapper.insertOrder(order);
            if (result > 0) {
                return Result.success();
            }
            return Result.error("订单创建失败");
        } catch (Exception e) {
            return Result.error("服务器内部错误：");
        }
    }

    // 获取用户订单列表
    @GetMapping("/get")
    public Result getOrderList(@RequestAttribute Integer userId) {
        List<Order> list = orderMapper.findByUserId(userId);
        return Result.success(list);
    }

    // 取消订单
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id) {
        int result = orderMapper.updateState(id, 3);
        return result > 0 ? "取消成功" : "取消失败";
    }
}
