package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ArticleMapper {

    // 获取全部文章
    @Select("select * from article")
    public List<Article> findAll();

    // 根据id查询文章
    @Select("select * from article where id = #{id}")
    public Article findById(String id);

    // 查询所有已发布文章（status=1）
    @Select("select * from article where status = 1")
    public List<Article> findPublished();

    // 查询所有草稿文章（status=0）
    @Select("select * from article where status = 0")
    public List<Article> findDrafts();

    // 添加文章
    @Insert("insert into article(id, up_id, title, content_url, status, upload_time) " +
            "values(#{id}, #{upId}, #{title}, #{contentUrl}, #{status}, #{uploadTime})")
    public int insert(Article article);

    // 更新文章
    @Update("update article set title = #{title}, content_url = #{contentUrl}, " +
            "status = #{status}, upload_time = #{uploadTime} where id = #{id}")
    public int update(Article article);

    // 删除文章
    @Delete("delete from article where id = #{id}")
    public int deleteById(String id);
}
