package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 根据会话id获取消息
    // 来自MessageMapper.xml
    public List<Message> findBySessionId(@Param("sessionId") Integer sessionId,
                                         @Param("currentUserId") Integer currentUserId);

    // 插入一条消息
    @Insert("insert into message(from_session_id, to_session_id, from_id, to_id, content, type, status, send_time) " +
            "values(#{fromSessionId}, #{toSessionId}, #{fromId}, #{toId}, #{content}, #{type}, #{status}, #{sendTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insert(Message message);

    // 根据id查询消息
    @Select("select * from message where id = #{id}")
    public Message findById(@Param("id") Integer id);

    // 根据id更新绑定请求处理状态
    // 来自MessageMapper.xml
    public int updateStatus(@Param("id") Integer id, @Param("status") Integer status);



}
