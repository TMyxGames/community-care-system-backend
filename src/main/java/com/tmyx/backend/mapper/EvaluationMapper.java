package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Evaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    // 插入评论
    @Insert("insert into comment(user_id, order_id, service_id, staff_id, content, service_rate, staff_rate, create_time)" +
            "values(#{userId}, #{orderId}, #{serviceId}, #{staffId}, #{content}, #{serviceRate}, #{staffRate}, now())")
    public int insert(Evaluation evaluation);

    // 根据服务id查询评论
    @Select("select * from comment where service_id= #{serviceId}")
    public List<Evaluation> findByServiceId(Integer serviceId);
}
