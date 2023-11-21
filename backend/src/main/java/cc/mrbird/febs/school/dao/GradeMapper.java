package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Grade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {


    @Select("select coalesce(max(GRADE_NO) + 1, 10001) from school_grade")
    Long getMaxGradeNo();
}
