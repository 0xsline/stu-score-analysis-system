package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {


    @Select("select coalesce(max(TEACHER_NO) + 1, 10001) from school_teacher")
    Long getMaxTeacherNo();
}
