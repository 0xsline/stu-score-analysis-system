package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Semester;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface SemesterMapper extends BaseMapper<Semester> {


    @Select("select coalesce(max(SEMESTER_NO) + 1, 10001) from school_semester")
    Long getMaxSemesterNo();
}
