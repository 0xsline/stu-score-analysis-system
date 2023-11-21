package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.TeachingArrange;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author IU
 */
public interface ITeachingArrangeService extends IService<TeachingArrange> {

    /**
     * 分页查询
     *
     * @param request
     * @param teachingArrange
     * @return
     */
    IPage<TeachingArrange> findTeachingArranges(QueryRequest request, TeachingArrange teachingArrange);


    /**
     * 删除教学安排
     *
     * @param ids
     */
    void deleteTeachingArranges(String[] ids);

    /**
     * 教学安排填充班级信息
     *
     * @param teachingArrangeList
     */
    void teachingArrangeFillClazz(List<TeachingArrange> teachingArrangeList);


    /**
     * 教学安排填充学期信息
     *
     * @param teachingArrangeList
     */
    void teachingArrangeFillSemester(List<TeachingArrange> teachingArrangeList);


    /**
     * 教学安排填充课程信息
     *
     * @param teachingArrangeList
     */
    void teachingArrangeFillCourse(List<TeachingArrange> teachingArrangeList);

    /**
     * 教学安排填充教师信息
     *
     * @param teachingArrangeList
     */
    void teachingArrangeFillTeacher(List<TeachingArrange> teachingArrangeList);

    /**
     * 根据学期查询教学安排数量
     *
     * @param semesterIdList
     * @return
     */
    Integer findCountBySemesterIdList(List<Long> semesterIdList);

    /**
     * 根据课程查询教学安排数量
     *
     * @param courseIdList
     * @return
     */
    Integer findCountByCourseIdList(List<Long> courseIdList);

    /**
     * 根据课程ID查询教学数量
     *
     * @param courseId
     * @return
     */
    Integer findCountByCourseId(Long courseId);

    /**
     * 根据课程ID查询教师ID列表
     *
     * @param courseId
     * @return
     */
    List<Long> findTeachIdListByCourseId(Long courseId);

    /**
     * 根据课程ID查询教学安排ID列表
     * @param courseId
     * @return
     */
    List<Long> findIdListByCourseId(Long courseId);

    /**
     *
     * @param clazzId
     * @return
     */
    List<Long> findTeachIdListByClazzId(Long clazzId);

    /**
     * 根据班级查询授课教师ID列表
     *
     * @param clazzId
     * @return
     */
    List<Long> findTeacherIdListByClazzId(Long clazzId);

    /**
     * 根据教师ID查询课程ID列表
     *
     * @param teacherId
     * @return
     */
    List<Long> findCourseIdListByTeacherId(Long teacherId);

    /**
     * 根据班级查询班级课程ID集合列表
     *
     * @param clazzId
     * @return
     */
    List<Long> findCourseIdListByClazzId(Long clazzId);

    /**
     * 根据教师和课程查询教学安排ID集合
     *
     * @param teacherId
     * @param courseId
     * @return
     */
    List<Long> findIdByTeacherIdAndCourseId(Long teacherId, Long courseId);

    /**
     * 根据班级ID和课程ID查询教学安排ID集合
     *
     * @param clazzId
     * @param courseId
     * @return
     */
    List<Long> findIdByClazzIdAndCourseId(Long clazzId, Long courseId);

    /**
     * 根据班级ID和教师ID查询教学安排ID集合
     *
     * @param clazzId
     * @param teacherId
     * @return
     */
    List<Long> findIdByClazzIdAndTeacherId(Long clazzId, Long teacherId);

    /**
     * 根据教师ID集合查询数量
     *
     * @param teacherIdList
     * @return
     */
    Integer findCountByTeacherIdList(List<Long> teacherIdList);

    /**
     * 查询某个班级某个学期的教学安排
     *
     * @param clazzId
     * @param semesterId
     * @return
     */
    List<TeachingArrange> findTeachingArrangeByCondition(Long clazzId, Long semesterId);


    /**
     * 查询某个班级某个学期的教学安排ID集合
     *
     * @param clazzId
     * @param semesterId
     * @return
     */
    List<Long> findTeachingArrangeIdListByCondition(Long clazzId, Long semesterId);


    /**
     * 查询老师的教学安排ID集合列表
     *
     * @param teacherId
     * @return
     */
    List<Long> findTeachingArrangeIdListByTeacherId(Long teacherId);
}
