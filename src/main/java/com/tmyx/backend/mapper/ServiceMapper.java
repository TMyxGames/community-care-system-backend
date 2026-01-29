package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Service;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceMapper {

    // 查询所有服务数据（按顺序）
    @Select("select * from service order by sort_order asc")
    public List<Service> findAll();

    // 查询所有服务数据（按价格）
    @Select("select * from service order by price asc")
    public List<Service> findAllByPrice();

    // 查询当前服务数据
    @Select("select * from service where id=#{id}")
    public Service findById(int id);

    // 插入服务数据
    @Insert("insert into service(sort_order, title, type," +
            "introduce, content_url, provider, work_time," +
            " location, price, total, img_url, link)" +
            "values(#{sortOrder}, #{title}, #{type}," +
            "#{introduce}, #{contentUrl}, #{provider}, #{workTime}," +
            " #{location}, #{price}, #{total}, #{imgUrl}, #{link})")
    public int insert(Service service);

    // 更新服务数据
    @Update("update service set " +
            "sort_order=#{sortOrder}, title=#{title}, type=#{type}," +
            "introduce=#{introduce}, content_url=#{contentUrl}, provider=#{provider}," +
            "work_time=#{workTime}, location=#{location}, price=#{price}, total=#{total}," +
            "img_url=#{imgUrl}, link=#{link} where id=#{id}")
    public int update(Service service);

    // 删除服务数据
    @Delete("delete from service where id=#{id}")
    public int delete(int id);
}
