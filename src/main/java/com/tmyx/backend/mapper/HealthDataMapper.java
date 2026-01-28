package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.HealthData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthDataMapper {

    @Insert("insert into health_data(user_id,username,sex,age,height,weight,heart_rate,systolic,diastolic,blood_sugar,record_date)" +
            "values(#{userId},#{username},#{sex},#{age},#{height},#{weight},#{hearRate},#{systolic},#{diastolic},#{bloodSugar},now())")
    public int insert(HealthData data);

    @Select("select * from health_data where user_id=#{userId} order by record_date desc")
    public List<HealthData> findByUserId(@Param("userId") Integer userId);
}
