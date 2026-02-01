package com.tmyx.backend.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StaffWorkMapper {

    // 批量插入
    @Insert({
            "<script>",
            "insert into staff_work(staff_id, service_id) values",
            "<foreach collection='serviceIds' item='serviceId' separator=','>",
            "(#{staffId},#{serviceId})",
            "</foreach>",
            "</script>"
    })
    public int batchInsert(@Param("staffId") Integer staffId, @Param("serviceIds") List<Integer> serviceIds);

    // 删除服务人员所有关联服务（用于更新前清空）
    @Delete("delete from staff_work where staff_id=#{staffId}")
    public int deleteByStaffId(@Param("staffId") Integer staffId);

    // 查询服务人员所有关联服务
    @Select("select service_id from staff_work where staff_id= #{staffId}")
    public List<Integer> findServiceIdsByStaffId(Integer staffId);
}
