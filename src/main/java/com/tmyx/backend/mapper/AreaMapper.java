package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AreaMapper {

    // 插入新安全区域
    @Insert("insert into safe_area(user_id, area_name, scope_path, region, center_lng, center_lat)" +
            "values(#{userId}, #{areaName}, #{scopePath}, ST_GeomFromText(#{region}), #{centerLng}, #{centerLat})")
    public int insertSafeArea(SafeArea safeArea);

    // 查询所有安全区域
    @Select("select id, user_id, area_name, scope_path, ST_AsText(region) as region, " +
            "center_lng, center_lat, is_active from safe_area")
    public List<SafeArea> findAllSafeArea();

    // 根据用户id查询安全区域（家属使用）
    @Select("select * from safe_area where user_id= #{userId}")
    public List<SafeArea> findSafeAreaByUserId(Integer userId);

    // 根据用户id查询已绑定用户的安全区域（老人使用）
    @Select("select a.id, " +
            "a.user_id as userId, " +
            "a.area_name as areaName, " +
            "a.scope_path as scopePath, " +
            "ST_AsText(a.region) as region, " +
            "a.center_lng as centerLng, " +
            "a.center_lat as centerLat " +
            "from safe_area a " +
            "join binding b on a.user_id = b.follower_id " +
            "where b.elder_id = #{elderId}")
    public List<SafeArea> findSafeAreaByElderId(Integer elderId);

    // 删除安全区域
    @Delete("delete from safe_area where id=#{id}")
    int deleteSafeArea(Integer id);

    // ===================================================================

    // 插入新服务区域
    @Insert("insert into service_area(admin_id, area_name, scope_path, region, center_lng, center_lat)" +
            "values(#{adminId}, #{areaName}, #{scopePath}, ST_GeomFromText(#{region}), #{centerLng}, #{centerLat})")
    public int insertServiceArea(ServiceArea serviceArea);

    // 查询所有服务区域
    @Select("select * from service_area")
    public List<ServiceArea> findAllServiceArea();

    // 根据id查询服务区域
    @Select("select * from service_area where id= #{id}")
    public ServiceArea findServiceAreaById(Integer id);

    // 根据经纬度查询服务区域
    // 该方法来自AreaMapper.xml
    ServiceArea findAreaByLngLat(@Param("pointWkt") String pointWkt);

    // 更新服务区域信息
    @Update("update service_area set area_name=#{areaName}, scope_path=#{scopePath}, " +
            "center_lng=#{centerLng}, center_lat=#{centerLat} where id=#{id}")
    public int updateServiceArea(ServiceArea serviceArea);

    // 删除服务区域
    @Delete("delete from service_area where id=#{id}")
    int deleteServiceArea(Integer id);

}
