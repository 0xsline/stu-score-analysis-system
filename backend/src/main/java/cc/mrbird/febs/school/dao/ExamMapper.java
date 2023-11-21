package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Exam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {


    @Select("select coalesce(max(EXAM_NO) + 1, 10001) from school_exam")
    Long getMaxExamNo();
}
