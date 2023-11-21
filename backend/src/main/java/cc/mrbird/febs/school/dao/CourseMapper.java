package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {


    @Select("select coalesce(max(COURSE_NO) + 1, 10001) from school_course")
    Long getMaxCourseNo();
}
