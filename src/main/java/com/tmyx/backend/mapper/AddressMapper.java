package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    // 根据用户id查询地址
    @Select("select * from address where user_id=#{userId}")
    public List<Address> findByUserId(Integer userId);

    // 插入地址
    @Insert("insert into address(user_id, contact, phone, area, detail, lng, lat, adcode, is_verified)" +
            "values(#{userId}, #{contact}, #{phone}, #{area}, #{detail}, #{lng}, #{lat}, #{adcode}, #{isVerified})")
    public int insert(Address address);

    // 更新地址
    @Update("update address set user_id=#{userId}, contact=#{contact}," +
            "phone=#{phone}, area=#{area}, detail=#{detail} where id=#{id}")
    public int update(Address address);

    // 删除地址（根据id）
    @Delete("delete from address where id=#{id}")
    public int delete(Integer id);

    // 将地址设置为默认
    @Update("update address set is_default=1 where id=#{id}")
    public int setDefaultById(Integer id);

    // 将所有地址设置为非默认
    @Update("update address set is_default=0 where user_id=#{userId}")
    public int resetDefaultByUserId(Integer id);
}
