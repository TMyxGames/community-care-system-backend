package com.tmyx.backend.controller;

import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.common.Result;
import com.tmyx.backend.service.SecurityService;
import com.tmyx.backend.util.GeometryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area")
@CrossOrigin
public class AreaController {
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 获取所有服务区域
    @GetMapping("/service/all")
    public Result getAllServiceArea(@RequestAttribute Integer userId) {
        List<ServiceArea> areas = areaMapper.findAllServiceArea();
        return Result.success(areas);
    }

    // 添加服务区域
    @PostMapping("/service/add")
    public Result addServiceArea(@RequestBody ServiceArea area, @RequestAttribute Integer userId) {
        // 设置管理员id并转换为wkt
        area.setAdminId(userId);
        String region = GeometryUtil.parseScopePathToWkt(area.getScopePath());
        area.setRegion(region);
        // 保存到数据库
        int result = areaMapper.insertServiceArea(area);
        if (result > 0) {
            return Result.success("服务区域添加成功", null);
        }

        return Result.error("服务区域添加失败");
    }

    // 删除服务区域
    @DeleteMapping("/service/delete/{id}")
    public String removeServiceArea(@PathVariable Integer id, @RequestAttribute Integer userId) {
        int result = areaMapper.deleteServiceArea(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

    // 获取所有安全区域
    @GetMapping("/safe/all")
    public Result getAllSafeArea(@RequestAttribute Integer userId) {
        // 获取当前用户信息
        User currentUser = userMapper.findById(userId);
        // 判断当前用户身份
        if (currentUser.getRole() == 3) {
            // 如果当前用户是老人
            List<SafeArea> areas = areaMapper.findSafeAreaByElderId(userId);
            return Result.success(areas);
        } else {
            // 如果当前用户是家属
            List<SafeArea> areas = areaMapper.findSafeAreaByUserId(userId);
            return Result.success(areas);
        }
    }

    // 添加安全区域
    @PostMapping("/safe/add")
    public Result addSafeArea(@RequestBody SafeArea area, @RequestAttribute Integer userId) {
        // 设置家属id并转换为wkt
        area.setUserId(userId);
        String region = GeometryUtil.parseScopePathToWkt(area.getScopePath());
        area.setRegion(region);
        // 保存到数据库
        int result = areaMapper.insertSafeArea(area);
        if (result > 0) {
            // 获取家属绑定的所有老人
            List<UserBindDto> elders = userMapper.findEldersByFollowerId(userId);
            for (UserBindDto elder : elders) {
                // 在老人的安全区域列表中添加该安全区域
                redisTemplate.opsForSet().add("elder:areas:" + elder.getId(), area.getId().toString());
                // 清理老人的告警状态
                securityService.clearRedisAlarmStatus(elder.getId());
            }
            // 更新安全区域缓存
            redisTemplate.opsForHash().put("area:cache:safe", area.getId().toString(), region);
            return Result.success("安全区域添加成功", null);
        }

        return Result.error("安全区域添加失败");
    }

    // 删除安全区域
    @DeleteMapping("/safe/delete/{id}")
    public Result removeSafeArea(@PathVariable Integer id, @RequestAttribute Integer userId) {
        // 获取安全区域详情
        SafeArea area = areaMapper.findSafeAreaById(id);
        if (area == null) {
            return Result.error("安全区域不存在");
        }
        // 根据安全区域的创建者（家属）id查找该家属绑定的所有老人
        List<UserBindDto> elders = userMapper.findEldersByFollowerId(area.getUserId());
        // 权限判定
        boolean hasPermission = false;
        // 操作者要么是创建者本人，要么是和老人中至少有一个绑定关系的家属
        if (area.getUserId().equals(userId)) {
            hasPermission = true;
        } else {
            for (UserBindDto elder : elders) {
                if (userMapper.countBinding(userId, elder.getId()) > 0) {
                    hasPermission = true;
                    break;
                }
            }
        }
        if (!hasPermission) {
            return Result.error("您无权删除该安全区域");
        }
        // 删除数据库中的安全区域
        int result = areaMapper.deleteSafeArea(id);
        if (result > 0) {
            for (UserBindDto elder : elders) {
                // 在老人的安全区域列表中删除该安全区域
                redisTemplate.opsForSet().remove("elder:areas:" + elder.getId(), id.toString());
                // 清理老人的告警状态
                securityService.clearRedisAlarmStatus(elder.getId());
            }
            redisTemplate.opsForHash().delete("area:cache:safe", id.toString());
            return Result.success("删除成功");
        }

        return Result.error("删除失败");
    }


}
