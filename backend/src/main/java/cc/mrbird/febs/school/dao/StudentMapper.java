package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {


    @Select("select coalesce(max(STUDENT_NO) + 1, 10001) from school_student")
    Long getMaxStudentNo();
}
