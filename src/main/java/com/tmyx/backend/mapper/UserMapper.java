package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.User;
import com.tmyx.backend.dto.UserBindDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // ============================== 查询 ==============================
    // 查询所有用户
    @Select("select * from user")
    public List<User> findAll();

    // 根据id查询用户
    @Select("select * from user where id=#{id}")
    public User findById(int id);

    // 根据用户名查询用户
    @Select("select * from user where username=#{username}")
    public User findByName(String username);

    // 根据真实姓名查询用户
    @Select("select * from user where real_name= #{realName}")
    public User findByRealName(String realName);

    // 根据邮箱查询用户
    @Select("select * from user where email=#{email}")
    public User findByEmail(String email);

    // 根据用户身份查询用户
    @Select("select * from user where role=#{role}")
    public List<User> findByRole(int role);

    // 查询所有管理员
    @Select("select * from user where role=1")
    public List<User> findAllAdmins();

    // 查询所有服务人员
    @Select("select * from user where role=2")
    public List<User> findAllStaff();

    // 根据关键词搜索用户（id、用户名、真实姓名)
    // 该方法来自UserMapper.xml
    List<User> searchUsers(@Param("keyword") String keyword, @Param("currentUserId") Integer currentUserId );

    // ============================== 更新 ==============================
    // 更新位置
    @Update("update user set lng=#{lng}, lat=#{lat} where id=#{id}")
    public int updateLocation(@Param("id") Integer id, @Param("lng") Double lng, @Param("lat") Double lat);

    // ============================== 用户 ==============================
    // 插入用户
    @Insert("insert into user(username, real_name, sex, password, email, avatar_url, role, service_status, service_area_id)" +
            "values(#{username}, #{realName}, #{sex}, #{password}, #{email}, #{avatarUrl}, #{role}, #{serviceStatus}, #{serviceAreaId})")
    public int insert(User user);

    // 删除用户
    @Delete("delete from user where id=#{id}")
    public int delete(int id);

    // 更新基础账户数据
    @Update("update user set username=#{username}, real_name=#{realName}, sex=#{sex} where id = #{id}")
    public int updateBaseInfo(User user);

    // 更新密码
    @Update("update user set password=#{password} where id = #{id}")
    public int updatePassword(User user);

    // 更新头像
    @Update("update user set avatar_url=#{avatarUrl} where id = #{id}")
    public int updateAvatar(@Param("id") Integer id, @Param("avatarUrl") String avatarUrl );

    // 查询用户的所有绑定（家属使用）
    @Select("SELECT " +
            "u.id, u.username, u.real_name, u.avatar_url, b.relation, b.remark " +
            "FROM binding b JOIN user u ON b.elder_id = u.id " +
            "WHERE b.follower_id = #{userId}")
    public List<UserBindDto> findEldersByFollowerId(Integer userId);

    // 查询用户的所有绑定（老人使用）
    @Select("SELECT " +
            "u.id, u.username, u.real_name, u.avatar_url, b.relation, b.remark " +
            "FROM binding b JOIN user u ON b.follower_id = u.id " +
            "WHERE b.elder_id = #{userId}")
    public List<UserBindDto> findFollowersByElderId(Integer userId);

    // 添加用户绑定关系
    @Insert("insert into binding(follower_id, elder_id, relation, remark) " +
            "values(#{followerId}, #{elderId}, #{relation}, #{remark})")
    int insertBinding(@Param("followerId") int followerId,
                      @Param("elderId") int elderId,
                      @Param("relation") int relation,
                      @Param("remark") String remark);

    // 删除用户绑定关系（解绑）
    @Delete("delete from binding where follower_id = #{followerId} and elder_id = #{elderId} " +
                                "or follower_id = #{elderId} and elder_id = #{followerId}")
    int deleteBinding(@Param("followerId") int followerId, @Param("elderId") int elderId);

    // 检查是否已存在绑定关系
    @Select("select count(*) from binding where follower_id = #{followerId} and elder_id = #{elderId}")
    int countBinding(@Param("followerId") int followerId, @Param("elderId") int elderId);

    // ============================== 服务人员 ==============================
    // 更新服务人员工作区域（参数来自UserService）
    // service_area_id 接收的参数areaId来自StaffConfigDto接收的前端参数
    @Update("update user set service_area_id= #{areaId} where id = #{userId}")
    public int updateServiceArea(@Param("userId") Integer userId, @Param("areaId") Integer areaId);


}
