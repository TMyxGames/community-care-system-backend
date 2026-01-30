package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.ServiceArea;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceAreaMapper {

    // 插入新服务区域
    @Insert("insert into service_area(admin_id, area_name, scope_path, center_lng, center_lat)" +
            "values(#{adminId}, #{areaName}, #{scopePath}, #{centerLng}, #{centerLat})")
    public int insert(ServiceArea serviceArea);

    // 查询所有服务区域
    @Select("select * from service_area")
    public List<ServiceArea> findAll();

    // 更新服务区域信息
    @Update("update service_area set area_name=#{areaName}, scope_path=#{scopePath}, " +
            "center_lng=#{centerLng}, center_lat=#{centerLat} where id=#{id}")
    public int update(ServiceArea serviceArea);

    // 删除服务区域
    @Delete("delete from service_area where id=#{id}")
    int delete(Integer id);

}
