package com.tmyx.backend.controller;


import com.tmyx.backend.dto.EvaluationDto;
import com.tmyx.backend.entity.Evaluation;
import com.tmyx.backend.entity.Order;
import com.tmyx.backend.entity.Service;
import com.tmyx.backend.mapper.EvaluationMapper;
import com.tmyx.backend.common.Result;
import com.tmyx.backend.mapper.OrderMapper;
import com.tmyx.backend.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
@CrossOrigin
public class EvaluationController {
    @Autowired
    private EvaluationMapper evaluationMapper;
    @Autowired
    private OrderMapper orderMapper;

    // 添加评价
    @PostMapping("/add")
    public Result addEvaluation(@RequestAttribute Integer userId, @RequestBody Evaluation evaluation) {
        // 插入评价
        evaluation.setUserId(userId);
        evaluationMapper.insert(evaluation);
        // 将订单状态设置为已评价（4）
        orderMapper.updateState(evaluation.getOrderId(), 4);
        return Result.success(evaluation);
    }

    // 获取服务评价
    @GetMapping("/all/{serviceId}")
    public Result getAllEvaluation(@PathVariable("serviceId") Integer serviceId) {
        List<EvaluationDto> evaluations = evaluationMapper.findByServiceId(serviceId);
        return Result.success(evaluations);
    }
}
