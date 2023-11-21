package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.ExamScore;

import java.util.List;
import java.util.Map;

public interface IExamScoreService extends IService<ExamScore> {

    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param examPlanId
     * @return
     */
    List<ExamScore> findExamScoreByExamPlanId(Long examPlanId);


    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param examPlanId
     * @return
     */
    Map<String, Object> findExamScoreMapByExamPlanId(Long examPlanId);


    /**
     * 根据考试ID，学期ID，班级ID统计查询学生成绩
     *
     * @param examId
     * @param semesterId
     * @param clazzId
     * @return
     */
    Map<String, Object> findTotalExamScoreMapByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException;


    /**
     * 总成绩打印
     *
     * @param examId
     * @param semesterId
     * @param clazzId
     * @return
     */
    Map<String, Object> findTotalExamScorePrinterByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException;


    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param examPlanId
     * @return
     */
    Map<String, Object> findExamScorePrinterByExamPlanId(Long examPlanId);

    /**
     * 保存考试成绩
     *
     * @param examScoreMap
     */
    void saveExamScoreMap(Map<String, String> examScoreMap);


    /**
     * @param examPlanIdList
     */
    void deleteByExamPlanIdList(List<Long> examPlanIdList);


    /**
     * 根据学生ID集合查询数量
     *
     * @param studentIdList
     * @return
     */
    Integer findCountByStudentIdList(List<Long> studentIdList);
}
