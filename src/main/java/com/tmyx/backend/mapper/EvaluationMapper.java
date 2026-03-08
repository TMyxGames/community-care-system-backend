package com.tmyx.backend.mapper;

import com.tmyx.backend.dto.EvaluationDto;
import com.tmyx.backend.entity.Evaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    // 插入评论
    @Insert("insert into evaluation(user_id, order_id, service_id, staff_id, content, service_rate, staff_rate, create_time)" +
            "values(#{userId}, #{orderId}, #{serviceId}, #{staffId}, #{content}, #{serviceRate}, #{staffRate}, now())")
    public int insert(Evaluation evaluation);

    // 根据服务id查询评论
    @Select("select " +
            "e.id, " +
            "e.user_id as userId, " +
            "e.service_id as serviceId," +
            "e.content, " +
            "e.service_rate as serviceRate, " +
            "e.create_time as createTime, " +
            "u.username as username, " +
            "u.avatar_url as avatarUrl " +
            "from evaluation e " +
            "left join user u on e.user_id = u.id " +
            "where e.service_id = #{serviceId} " +
            "order by e.create_time desc")
    public List<EvaluationDto> findByServiceId(@Param("serviceId") Integer serviceId);
}
