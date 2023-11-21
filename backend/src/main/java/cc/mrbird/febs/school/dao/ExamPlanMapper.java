package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.ExamPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface ExamPlanMapper extends BaseMapper<ExamPlan> {


    @Select("select coalesce(max(EXAM_PLAN_NO) + 1, 10001) from school_exam_plan")
    Long getMaxExamPlanNo();
}
