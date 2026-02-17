package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface OrderMapper {

    // 创建订单
    @Insert("insert into `order`(order_sn, user_id, staff_id," +
            "service_id, service_title, service_img, service_price, " +
            "lng, lat, address_shot, create_time, start_time, complete_time, state) " +
            "values (#{orderSn}, #{userId}, #{staffId}, #{serviceId}, #{serviceTitle}, " +
            "#{serviceImg}, #{servicePrice}, #{lng}, #{lat}, #{addressShot}, " +
            "#{createTime}, #{startTime}, #{completeTime}, #{state})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertOrder(Order order);

    // 根据订单id查询订单
    @Select("select * from `order` where id=#{id}")
    public Order findById(Integer id);

    // 根据用户id查询订单列表（按时间降序）
    @Select("select * from `order` where user_id=#{userId} order by create_time desc")
    public List<Order> findByUserId(Integer userId);

    // 根据服务人员id查询待服务订单列表（按时间降序）
    @Select("select * from `order` where staff_id=#{staffId} and state=1 order by create_time desc")
    public List<Order> findPendingOrdersByStaffId(Integer staffId);

    // 根据服务人员id查询进行中订单列表（按时间降序）
    @Select("select * from `order` where staff_id=#{staffId} and state=2 order by create_time desc")
    public List<Order> findDoingOrdersByStaffId(Integer staffId);

    // 根据服务人员id查询历史订单列表（按时间降序）
    @Select("select * from `order` where staff_id=#{staffId} and state=3 or state=4 order by create_time desc")
    public List<Order> findHistoryOrdersByStaffId(Integer staffId);

    // 更新订单状态
    @Update("update `order` set state=#{state} where id=#{id}")
    public int updateState(Integer id, Integer state);

    // 更新订单开始服务时间
    @Update("update `order` set start_time= #{startTime} where id= #{id}")
    public int updateStartTime(Integer id, LocalDateTime startTime);

    // 更新订单完成服务时间
    @Update("update `order` set complete_time= #{completeTime} where id= #{id}")
    public int updateCompleteTime(Integer id, LocalDateTime completeTime);

    // 更新订单服务人员
    @Update("update `order` set staff_id= #{staffId} where id= #{id}")
    public int updateStaff(Integer id, Integer staffId);


}
