package com.tmyx.backend.mapper;

import com.tmyx.backend.dto.BloodPressureDto;
import com.tmyx.backend.dto.BloodSugarDto;
import com.tmyx.backend.dto.BmiDto;
import com.tmyx.backend.entity.HealthDataBMI;
import com.tmyx.backend.entity.HealthDataBP;
import com.tmyx.backend.entity.HealthDataBS;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthDataMapper {

    // 插入健康数据
    @Insert("insert into health_data(user_id, age, " +
            "height, weight, heart_rate, systolic, diastolic, blood_sugar, record_date)" +
            "values(#{userId}, #{age}, #{height}, #{weight}, " +
            "#{hearRate}, #{systolic}, #{diastolic}, #{bloodSugar}, now())")
    public int insert(HealthDataBMI data);

    // 根据id获取用户全部健康数据
    @Select("select * from health_data where user_id=#{userId} order by record_date desc")
    public List<HealthDataBMI> findByUserId(@Param("userId") Integer userId);

    // 根据id获取用户特定天数的最新健康数据
    @Select("select * from health_data where user_id= #{userId} order by record_date desc limit #{limit}")
    public List<HealthDataBMI> findLatestData(@Param("userId") Integer userId, @Param("limit") Integer limit);

    // 根据id获取用户指定时间段的健康数据
    @Select("select * from health_data where user_id= #{userId} and record_date between #{startDate} and #{endDate}")
    public List<HealthDataBMI> findByUserIdAndDate(@Param("userId") Integer userId,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate);



    // 插入bmi数据
    @Insert("insert into health_data_bmi(user_id, height, weight, record_date) " +
            "values(#{userId}, #{height}, #{weight}, coalesce(#{recordDate, }now()))")
    public int insertBMI(HealthDataBMI data);

    // 插入血压数据
    @Insert("insert into health_data_bp(user_id, heart_rate, systolic, diastolic, record_date) " +
            "values(#{userId}, #{heartRate}, #{systolic}, #{diastolic}, coalesce(#{recordDate, }now()))")
    public int insertBP(HealthDataBP data);

    // 插入血糖数据
    @Insert("insert into health_data_bs(user_id, blood_sugar, meal_status, record_date)" +
            " values(#{userId}, #{bloodSugar}, #{mealStatus}, coalesce(#{recordDate, }now()))")
    public int insertBS(HealthDataBS data);



    // 根据id获取用户最新bmi数据（特定天数）

    @Select("select * from health_data_bmi where user_id = #{userId} order by record_date desc limit #{limit}")
    public List<BmiDto> findBMILatestData(@Param("userId") Integer userId, @Param("limit") Integer limit);

    // 根据id获取用户最新血压数据（特定天数）
    @Select("select * from health_data_bp where user_id = #{userId} order by record_date desc limit #{limit}")
    public List<BloodPressureDto> findBPLatestData(@Param("userId") Integer userId, @Param("limit") Integer limit);

    // 根据id获取用户最新血糖数据（包含空腹和餐后两条数据）
//    @Select("select * from health_data_bs where user_id = #{userId} and meal_status = 0 " +
//            "order by record_date desc limit #{limit} union all s")
//    public List<HealthDataBS> findBSLatestData(@Param("userId") Integer userId, @Param("limit") Integer limit);
    // 该方法来自HealthDataMapper.xml
    List<BloodSugarDto> findBSLatestData(@Param("userId") Integer userId);



    // 根据id获取用户bmi数据（近7天）
    @Select("select * from health_data_bmi where user_id = #{userId} " +
            "and record_date >= date_sub(curdate(), interval 7 day) " +
            "order by record_date asc")
    public List<BmiDto> findBMISevenDays(@Param("userId") Integer userId);

    // 根据id获取用户血压数据（近7天）
    @Select("select * from health_data_bp where user_id = #{userId} " +
            "and record_date >= date_sub(curdate(), interval 7 day) " +
            "order by record_date asc")
    public List<BloodPressureDto> findBPSevenDays(@Param("userId") Integer userId);

    // 根据id获取用户血糖数据（近7天）
    @Select("select * from health_data_bs where user_id = #{userId} " +
            "and record_date >= date_sub(curdate(), interval 7 day) " +
            "order by record_date asc")
    public List<BloodSugarDto> findBSSevenDays(@Param("userId") Integer userId);


}
