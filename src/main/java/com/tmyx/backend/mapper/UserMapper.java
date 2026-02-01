package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.User;
import com.tmyx.backend.entity.UserBindDto;
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

    // 查询用户绑定关系
    @Select("select u.id, u.username, u.avatar_url, b.remark " +
            "from binding b " +
            "join user u on b.elder_id = u.id " +
            "where b.follower_id = #{followerId}")
    public List<UserBindDto> findBindingsByFollowerId(@Param("followerId") int followerId);

    // 添加用户绑定关系
    @Insert("insert into binding(follower_id, elder_id, remark) " +
            "values(#{followerId}, #{elderId}, #{remark})")
    int insertBinding(@Param("followerId") int followerId,
                      @Param("elderId") int elderId,
                      @Param("remark") String remark);

    // 删除用户绑定关系
    @Delete("delete from binding where follower_id = #{followerId} and elder_id = #{elderId}")
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
