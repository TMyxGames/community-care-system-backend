package com.tmyx.backend.mapper;

import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface LocationMapper {
    // 获取用户位置
    @Select("select * from location where user_id= #{userId}")
    public Location findByUserId(Integer userId);

    // 根据用户id批量获取位置
    // 来自LocationMapper.xml
    public List<Location> findLocationsByUserIds(@Param("userIds") List<Integer> userIds);

    // 获取所有普通用户的位置
    // 来自LocationMapper.xml
    public List<LocationDto> findAllUserLocations();

    // 获取所有服务人员的位置
    // 来自LocationMapper.xml
    public List<LocationDto> findAllStaffLocations();

    // 更新单个位置
    // 来自LocationMapper.xml
    public int updateLocation(@Param("userId") Integer userId,
                              @Param("lng") Double lng,
                              @Param("lat") Double lat,
                              @Param("updateTime") Date updateTime);

}
