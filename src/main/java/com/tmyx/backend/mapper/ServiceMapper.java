package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Service;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceMapper {

    // 查询所有服务数据（按排列顺序）
    @Select("select * from service order by sort_order asc")
    public List<Service> findAll();

    // 查询所有服务数据（按价格）
    @Select("select * from service order by price asc")
    public List<Service> findAllByPrice();

    // 查询当前服务数据
    @Select("select * from service where id=#{id}")
    public Service findById(Integer id);

    // 根据id查询md文件
    @Select("select content_url from service where id=#{id}")
    public String findContentUrlById(Integer id);

    // 根据id查询图片文件
    @Select("select img_url from service where id= #{id}")
    public String findImgUrlById(Integer id);

    // 插入服务数据
    @Insert("insert into service(sort_order, title, type, " +
            "introduce, content_url, work_time, price, img_url)" +
            "values(#{sortOrder}, #{title}, #{type}, " +
            "#{introduce}, #{contentUrl}, #{workTime}, " +
            "#{price}, #{imgUrl})")
    public Integer insert(Service service);

    // 更新服务数据
    @Update("update service set " +
            "sort_order=#{sortOrder}, title=#{title}, type=#{type}, " +
            "introduce=#{introduce}, content_url=#{contentUrl}, " +
            "work_time=#{workTime}, price=#{price},  " +
            "img_url=#{imgUrl} where id=#{id}")
    public Integer update(Service service);

    // 删除服务数据
    @Delete("delete from service where id=#{id}")
    public Integer delete(Integer id);
}
