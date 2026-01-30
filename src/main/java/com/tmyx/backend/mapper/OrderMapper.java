package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface OrderMapper {

    // 创建订单
    @Insert("insert into `order`(order_sn, user_id, " +
            "service_id, service_title, service_img, service_price, " +
            "address_id, address_shot, create_time, scheduled_time, complete_time, state, rate, comment) " +
            "values (#{orderSn}, #{userId}, #{serviceId}, #{serviceTitle}, " +
            "#{serviceImg}, #{servicePrice}, #{addressId}, #{addressShot}, " +
            "#{createTime}, #{scheduledTime}, #{completeTime}, #{state}, #{rate}, #{comment} )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertOrder(Order order);

    // 查询用户订单列表（按时间降序）
    @Select("select * from `order` where user_id=#{userId} order by create_time desc")
    public List<Order> findByUserId(Integer userId);

    // 更新订单状态
    @Update("update `order` set state=#{state} where id=#{id}")
    public int updateState(Integer id, Integer state);

    // 提交评价
    @Update("update `order` set rate=#{rate}, comment=#{comment}, state=2 where id=#{id}")
    public int updateComment(Integer id, Integer rate, String comment);
}
