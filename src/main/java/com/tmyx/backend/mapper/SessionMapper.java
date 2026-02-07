package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Session;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SessionMapper {

    // 根据会话id查询会话
    @Select("select * from session where id= #{id}")
    public Session findById(Integer id);

    // 根据用户id查询会话
    @Select("select * from session where user_id= #{userId}")
    public List<Session> findByUserId(Integer userId);

    // 根据用户id和消息类型查询会话
    @Select("select * from session where user_id= #{userId} and type= #{type}")
    public Session findByUserAndType(Integer userId, Integer type);

    // 插入会话
    @Insert("insert into session(user_id, target_id, name, type, last_msg, unread_count, update_time) " +
            "values(#{userId}, #{targetId}, #{name}, #{type}, #{lastMsg}, #{unreadCount}, #{updateTime})")
    public int insert(Session session);

}
