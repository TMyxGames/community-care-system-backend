package com.tmyx.backend.service;

import com.tmyx.backend.dto.WebSocketResult;
import com.tmyx.backend.entity.Order;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.OrderMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DispatchService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MessageHandler messageHandler;

    // 派单（订单id，订单经纬度，服务id，区域id）
    @Async
    public void dispatch(Integer orderId) {
        // 获取订单信息
        Order order = orderMapper.findById(orderId);
        Double orderLng = order.getLng();
        Double orderLat = order.getLat();
        Integer serviceId = order.getServiceId();

        // 构造坐标点的WKT格式
        String pointWkt = String.format("POINT(%f %f)", orderLng, orderLat);
        // 匹配服务区域
        ServiceArea area = areaMapper.findAreaByLngLat(pointWkt);
        if (area == null) {
            // 标记订单为“超出服务范围”，并通知用户
            orderMapper.updateState(orderId, -1);
            return;
        }
        // 寻找候选服务人员
        // 条件：同区域 + 角色是服务者 + 状态为空闲 + 具备对应业务技能
        List<User> staffList = userMapper.findQualifiedStaffs(area.getId(), serviceId);
        if (staffList.isEmpty()) {
            return;
        }

        for (User staff : staffList) {
            String redisKey = "location:cache:staff" + staff.getId();
            // 获取 Redis中的数据
            Map<Object, Object> realTimeLocation = redisTemplate.opsForHash().entries(redisKey);

            if (!realTimeLocation.isEmpty() && realTimeLocation.containsKey("lng")) {
                try {
                    staff.setLng(Double.parseDouble(realTimeLocation.get("lng").toString()));
                    staff.setLat(Double.parseDouble(realTimeLocation.get("lat").toString()));
                } catch (Exception e) {
                    // 如果格式转换失败，则保留数据库原有的位置数据作为兜底
                    System.err.println("Redis位置解析异常: " + staff.getId());
                }
            }
        }

        User bestStaff = staffList.stream()
                .min((s1, s2) -> Double.compare(
                        calculateDistance(orderLng, orderLat, s1.getLng(), s1.getLat()),
                        calculateDistance(orderLng, orderLat, s2.getLng(), s2.getLat())
                )).orElse(null);

        if (bestStaff != null) {
            // 绑定订单与人员
//            String verifyCode = String.format("%04d", (int)(Math.random() * 10000));
            orderMapper.updateStaff(orderId, bestStaff.getId());
            // 更新订单状态为待服务(state=1)
            orderMapper.updateState(orderId, 1);
            // 更新人员状态为已接单(service_status=2)
            userMapper.updateServiceStatus(bestStaff.getId(), 2);
            // 通过WebSocket实时推送订单消息
            WebSocketResult<Integer> result = WebSocketResult.build("new_order", orderId);
            messageHandler.sendMessageToUser(bestStaff.getId(), result);

            System.out.println("成功指派服务员：" + bestStaff.getUsername());
        }
    }

    // 经纬度计算方法
    private double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        return Math.sqrt(Math.pow(lng1 - lng2, 2) + Math.pow(lat1 - lat2, 2));
    }
}
