package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Carousel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CarouselMapper {

    // 查询所有轮播数据
    @Select("select * from carousel order by sort_order asc")
    public List<Carousel> findAll();

    // 更新轮播数据
    @Update("update carousel set " +
            "title=#{title}, sort_order=#{sortOrder}," +
            "img_url=#{imgUrl}, link=#{link} where id=#{id}")
    public int update(Carousel carousel);

    // 插入轮播数据
    @Insert("insert into carousel(title, sort_order, img_url, link)" +
            "values(#{title}, #{sortOrder}, #{imgUrl}, #{link})")
    public int insert(Carousel carousel);

    // 删除轮播数据
    @Delete("delete from carousel where id=#{id}")
    public int delete(int id);
}