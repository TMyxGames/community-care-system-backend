package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Order;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.OrderMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.DispatchService;
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
    @Autowired
    private DispatchService dispatchService;
    @Autowired
    private UserMapper userMapper;

    // 创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody Order order, @RequestAttribute Integer userId) {
        // 设置用户id
        order.setUserId(userId);
        // 设置创建时间
        order.setCreateTime(LocalDateTime.now());
        // 生成订单号
        String timePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sn = timePrefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderSn(sn);
        // 设置订单初始状态（0: 待接单）
        order.setState(0);

        try {
            int result = orderMapper.insertOrder(order);
            if (result > 0) {
                dispatchService.dispatch(order.getId());
                return Result.success();
            }
            return Result.error("订单创建失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器内部错误");
        }
    }

    // 获取用户订单列表
    @GetMapping("/get")
    public Result getOrderList(@RequestAttribute Integer userId) {
        List<Order> list = orderMapper.findByUserId(userId);
        return Result.success(list);
    }

    // 获取服务人员待服务订单
    @GetMapping("/getPendingOrder")
    public Result getPendingOrder(@RequestAttribute Integer userId) {
        List<Order> list = orderMapper.findPendingOrdersByStaffId(userId);
        return Result.success(list);
    }

    // 获取服务人员进行中订单
    @GetMapping("/getDoingOrder")
    public Result getDoingOrder(@RequestAttribute Integer userId) {
        List<Order> list = orderMapper.findDoingOrdersByStaffId(userId);
        return Result.success(list);
    }

    // 获取服务人员历史订单
    @GetMapping("/getHistoryOrder")
    public Result getHistoryOrder(@RequestAttribute Integer userId) {
        List<Order> list = orderMapper.findHistoryOrdersByStaffId(userId);
        return Result.success(list);
    }

    // 更新订单状态
    @PutMapping("/updateState")
    public Result updateState(@RequestParam Integer orderId, @RequestAttribute Integer userId) {
        // 根据订单id获取订单信息
        Order order = orderMapper.findById(orderId);

        if (order.getState() == 1 ) {
            // 如果订单状态为1（待服务），则更新为2（服务中），并添加开始服务时间
            orderMapper.updateState(orderId, 2);
            orderMapper.updateStartTime(orderId, LocalDateTime.now());
            // 更新服务人员状态为3（活动中）
            userMapper.updateServiceStatus(userId, 3);
            return Result.success();
        } else if (order.getState() == 2) {
            // 如果订单状态为2（服务中），则更新为3（已完成），并添加完成服务时间
            orderMapper.updateState(orderId, 3);
            orderMapper.updateCompleteTime(orderId, LocalDateTime.now());
            // 更新服务人员状态为1（空闲中）
            userMapper.updateServiceStatus(userId, 1);
            return Result.success();
        }

        return Result.error("订单状态错误");
    }



    // 用户取消订单
    @PutMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Integer orderId) {
        int result = orderMapper.updateState(orderId, 5);
        return result > 0 ? "取消成功" : "取消失败";
    }
}
