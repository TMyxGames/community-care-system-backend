package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 获取系统消息
    @Select("select * from message where type=0")
    public List<Message> findAllSysMsg();

    // 获取用户绑定请求
    @Select("select * from message where type=1")
    public List<Message> findAllBindMsg();

    // 获取安全提醒
    @Select("select * from message where type=2")
    public List<Message> findAllSafeMsg();

    // 插入一条消息
    @Insert("insert into message(from_id, to_id, type, status, content_url, send_time) " +
            "values(#{fromId}, #{toId}, #{type}, #{status}, #{contentUrl}, #{sendTime})")
    public int insert(Message message);



}
