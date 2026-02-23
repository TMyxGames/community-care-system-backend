package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.EmergencyCall;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmergencyCallMapper {

    // 插入一条紧急呼叫记录
    @Insert("insert into emergency_call (user_id, type, call_time, remark) " +
            "values (#{userId}, #{type}, now(), #{remark})")
    public int insertEmergencyCall(EmergencyCall emergencyCall);

    // 根据用户id查询所有紧急呼叫记录（降序排序）
    @Select("select * from emergency_call where user_id = #{userId} order by call_time desc")
    public List<EmergencyCall> selectEmergencyCallsByUserId(Integer userId);
}
