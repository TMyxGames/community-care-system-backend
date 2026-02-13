package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.SafeArea;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SafeAreaMapper {

    // 插入新安全区域
    @Insert("insert into safe_area(user_id, area_name, scope_path, center_lng, center_lat)" +
            "values(#{userId}, #{areaName}, #{scopePath}, #{centerLng}, #{centerLat})")
    public int insert(SafeArea safeArea);

    // 查询所有安全区域
    @Select("select * from safe_area")
    public List<SafeArea> findAll();

    // 根据用户id查询安全区域
    @Select("select * from safe_area where user_id= #{userId}")
    public List<SafeArea> findByUserId(Integer userId);

    // 删除安全区域
    @Delete("delete from safe_area where id=#{id}")
    int delete(Integer id);
}
