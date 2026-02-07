package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.SystemNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemNoticeMapper {

    // 查询所有系统通知
    @Select("select * from system_notice")
    public List<SystemNotice> findAll();

    // 查询所有系统通知并按时间排序
    @Select("select * from system_notice order by create_time desc")
    public List<SystemNotice> findAllOrderByTime();
}
