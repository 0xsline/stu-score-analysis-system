package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.StudentTotalScore;
import cc.mrbird.febs.common.domain.TeachingArrangeTotalScore;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.StudentScore;

import java.util.List;
import java.util.Map;

public interface IStudentScoreService extends IService<StudentScore> {

    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param teachingArrangeId
     * @param examId
     * @return
     */
    List<StudentScore> findStudentScoreByTeachingArrangeId(Long teachingArrangeId, Long examId);


    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param teachingArrangeId
     * @param examId
     * @return
     */
    Map<String, Object> findStudentScoreMapByTeachingArrangeId(Long teachingArrangeId, Long examId);

    /**
     * 查询某个教学安排的所有的考试记录
     *
     * @param teachingArrangeId
     * @return
     */
    Map<String, Object> findStudentScoreMapByTeachingArrangeId(Long teachingArrangeId);

    /**
     * 查询某个学生的所有成绩
     *
     * @param studentId
     * @return
     */
    Map<String, Object> findStudentScoreMapByStudentId(Long studentId);


    /**
     * 根据考试ID，学期ID，班级ID统计查询学生成绩
     *
     * @param examId
     * @param semesterId
     * @param clazzId
     * @return
     */
    Map<String, Object> findTotalStudentScoreMapByCondition(Long examId, Long semesterId, Long clazzId, boolean isPrinter) throws FebsException;


    /**
     * 总成绩打印
     *
     * @param examId
     * @param semesterId
     * @param clazzId
     * @return
     */
    Map<String, Object> findTotalStudentScorePrinterByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException;


    /**
     * 根据考试计划ID查询这个计划下学生考试情况
     *
     * @param teachingArrangeId
     * @param examId
     * @return
     */
    Map<String, Object> findStudentScorePrinterByTeachingArrangeIdAndExamId(Long teachingArrangeId, Long examId);

    /**
     * 保存考试成绩
     *
     * @param studentScoreMap
     */
    void saveStudentScoreMap(Map<String, String> studentScoreMap);

    /**
     * 根据考试ID查询数量
     *
     * @param examIdList
     * @return
     */
    Integer findCountByExamIdList(List<Long> examIdList);

    /**
     * 根据教学安排删除成绩信息
     *
     * @param teachingArrangeIdList
     */
    void deleteByTeachingArrangeIdList(List<Long> teachingArrangeIdList);

    /**
     * 根据学生ID集合列表查询数量
     *
     * @param studentIdList
     * @return
     */
    Integer findCountByStudentIdList(List<Long> studentIdList);

    /**
     * 等级学生数量
     *
     * @param teachingArrangeIdList
     * @return
     */
    Map<Long, Long> teachingArrangeIdStudentCount(List<Long> teachingArrangeIdList, int degree);

    /**
     * 查询总的等级数量
     *
     * @param degree
     * @return
     */
    int findDegreeCount(int degree);

    /**
     * @param teachingArrangeIdList
     * @param degree
     * @return
     */
    int findDegreeCount(List<Long> teachingArrangeIdList, int degree);

    /**
     *
     * @param teachingArrangeIdList
     * @return
     */
    int findCount(List<Long> teachingArrangeIdList);


    /**
     * 查询某个学生某个等级的数量
     *
     * @param studentId
     * @param degree
     * @return
     */
    int findStudentDegreeCount(Long studentId, int degree);

    /**
     * 查询学生的考试成绩数量
     *
     * @param studentId
     * @return
     */
    int findCountByStudentId(Long studentId);


    /**
     * 根据某个教学安排查询该教学安排所有的考试记录
     *
     * @param teachingArrangeId
     * @return
     */
    List<StudentScore> findListByTeachingArrangeId(Long teachingArrangeId);

    /**
     * 查询成绩
     *
     * @param teachingArrangeIdList
     * @return
     */
    List<StudentScore> findListByTeachingArrangeIdList(List<Long> teachingArrangeIdList);

    /**
     * 根据学生ID查询所有学生的考试情况
     *
     * @param studentId
     * @return
     */
    List<StudentScore> findListByStudentId(Long studentId);

    /**
     * 根据学生ID查询所有学生某次考试的考试情况
     *
     * @param studentId
     * @param examId
     * @return
     */
    List<StudentScore> findListByStudentIdAndExamId(Long studentId, Long examId);


    /**
     * 根据某个教学安排查询该教学安排所有的考试ID
     *
     * @param teachingArrangeId
     * @return
     */
    List<Long> findExamIdListByTeachingArrangeId(Long teachingArrangeId);

    /**
     * @param teachingArrangeId
     * @return
     */
    List<StudentTotalScore> findStudentTotalScoreByTeachingArrangeId(Long teachingArrangeId);

    /**
     * @param studentId
     * @return
     */
    List<TeachingArrangeTotalScore> findTeachingArrangeTotalScoreByStudentId(Long studentId);

    /**
     * 查询某个学生某次考试各科的成绩数据
     *
     * @param teachingArrangeIdList
     * @param examId
     * @param studentId
     * @return
     */
    List<StudentScore> findListByCondition(List<Long> teachingArrangeIdList, Long examId, Long studentId);
}
