package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.school.entity.Student;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

public interface IStudentService extends IService<Student> {

    /**
     * 分页查询
     *
     * @param request
     * @param student
     * @return
     */
    IPage<Student> findStudents(QueryRequest request, Student student);


    /**
     * 获取最大的学生编号+1
     *
     * @return
     */
    Long getMaxStudentNo();

    /**
     * 删除学生
     *
     * @param ids
     */
    void deleteStudents(String[] ids) throws Exception;

    /**
     * 学生填充班级
     *
     * @param studentList
     */
    void studentFillClazz(List<Student> studentList);

    /**
     * 构造班级和年级和学院的组织关系树
     *
     * @return
     */
    List<SchoolTree> buildOrganizationTreeList(List<Long> clazzIdList);

    /**
     * 根据班级查询班级的学生信息
     *
     * @param clazzId
     */
    List<Student> findByClazzId(Long clazzId);

    /**
     * @param clazzId
     * @return
     */
    List<Long> findStudentIdListByClazzId(Long clazzId);

    /**
     * 教师填充关联用户信息
     *
     * @param studentList
     */
    void studentWithUser(List<Student> studentList);

    /**
     * 班级下学生数量
     *
     * @param clazzId
     * @return
     */
    Integer studentCountByClazzId(Long clazzId);

    /**
     * 年级下学生数量
     *
     * @param clazzIdList
     * @return
     */
    Integer studentCountByClazzIdList(List<Long> clazzIdList);

    /**
     * 班级学生数量
     *
     * @param clazzIdList
     * @return
     */
    Map<Long, Long> clazzStudentCount(List<Long> clazzIdList);


    /**
     * 根据条件查询某个学生某个学期某次考试的成绩数据
     *
     * @param studentId
     * @param semesterId
     * @param examId
     * @return
     */
    Map<String, Object> findScoreByCondition(Long studentId, Long semesterId, Long examId);


    /**
     * @param userId
     * @return
     */
    Student findStudentByUserId(Long userId);

    /**
     * 查询学生的等级分布情况
     *
     * @param studentId
     * @return
     */
    Map<String, Object> findStudentDegreeCountMap(Long studentId);

}
