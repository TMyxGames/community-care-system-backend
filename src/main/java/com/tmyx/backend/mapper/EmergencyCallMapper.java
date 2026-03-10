package com.tmyx.backend.mapper;

import com.tmyx.backend.dto.CallDto;
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

    // 根据用户id查询所有已绑定老人的紧急呼叫记录（家属使用）
    @Select("select " +
            "e.id, " +
            "e.user_id as userId, " +
            "e.type, " +
            "e.call_time as callTime, " +
            "u.username, " +
            "u.real_name as realName, " +
            "u.avatar_url as avatarUrl " +
            "from emergency_call e " +
            "join user u on e.user_id = u.id " +
            "join binding b on e.user_id = b.elder_id " +
            "where b.follower_id = #{followerId} " +
            "order by e.call_time desc")
    public List<CallDto> findEmergencyCallsByUserId(Integer followerId);

    // 根据用户id查询所有紧急呼叫记录（老人使用）
    @Select("select " +
            "e.id, " +
            "e.user_id as userId, " +
            "e.type, " +
            "e.call_time as callTime, " +
            "u.username, " +
            "u.real_name as realName, " +
            "u.avatar_url as avatarUrl " +
            "from emergency_call e " +
            "join user u on e.user_id = u.id " +
            "where e.user_id = #{userId}")
    public List<CallDto> findEmergencyCallsByElderId(Integer userId);
}
