package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.ExamPlan;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author IU
 */
public interface IExamPlanService extends IService<ExamPlan> {

    /**
     * 分页查询
     *
     * @param request
     * @param examPlan
     * @return
     */
    IPage<ExamPlan> findExamPlans(QueryRequest request, ExamPlan examPlan);


    /**
     * 获取最大的考试计划编号+1
     *
     * @return
     */
    Long getMaxExamPlanNo();

    /**
     * 删除考试计划
     *
     * @param ids
     */
    void deleteExamPlans(String[] ids);

    /**
     * 考试计划填充班级信息
     *
     * @param examPlanList
     */
    void examPlanFillClazz(List<ExamPlan> examPlanList);


    /**
     * 考试计划填充学期信息
     *
     * @param examPlanList
     */
    void examPlanFillSemester(List<ExamPlan> examPlanList);


    /**
     * 考试计划填充考试信息
     *
     * @param examPlanList
     */
    void examPlanFillExam(List<ExamPlan> examPlanList);


    /**
     * 考试计划填充课程信息
     *
     * @param examPlanList
     */
    void examPlanFillCourse(List<ExamPlan> examPlanList);

    /**
     * 考试计划填充教师信息
     *
     * @param examPlanList
     */
    void examPlanFillTeacher(List<ExamPlan> examPlanList);

    /**
     * 根据学期查询相关学期的数量
     *
     * @param semesterIdList
     * @return
     */
    Integer findCountBySemesterIdList(List<Long> semesterIdList);

    /**
     * 根据课程查询相关课程的数量
     *
     * @param courseIdList
     * @return
     */
    Integer findCountByCourseIdList(List<Long> courseIdList);

    /**
     * 根据考试ID集合查询数量
     *
     * @param examIdList
     * @return
     */
    Integer findCountByExamIdList(List<Long> examIdList);


    /**
     * 根据老师ID集合查询数量
     *
     * @param teacherIdList
     * @return
     */
    Integer findCountByTeacherIdList(List<Long> teacherIdList);
}
