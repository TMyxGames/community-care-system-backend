package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Evaluation;
import com.tmyx.backend.mapper.EvaluationMapper;
import com.tmyx.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
@CrossOrigin
public class EvaluationController {
    @Autowired
    private EvaluationMapper evaluationMapper;

    // 添加评论
    @PostMapping("/add")
    public Result addEvaluation(@RequestAttribute Integer userId, @RequestBody Evaluation evaluation) {
        evaluation.setUserId(userId);
        evaluationMapper.insert(evaluation);
        return Result.success(evaluation);
    }

    // 获取服务评论
    @GetMapping("/all/{id}")
    public Result getAllEvaluation(@RequestParam Integer serviceId) {
        List<Evaluation> evaluations = evaluationMapper.findByServiceId(serviceId);
        return Result.success(evaluations);
    }
}
