package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.Teacher;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

public interface ITeacherService extends IService<Teacher> {

    /**
     * 分页查询
     *
     * @param request
     * @param teacher
     * @return
     */
    IPage<Teacher> findTeachers(QueryRequest request, Teacher teacher);


    /**
     * 查询某个教师的成绩分析情况
     *
     * @param teacherId
     * @return
     */
    Map<String, Object> findTeacherAnalysisDetail(Long teacherId);


    /**
     * 获取最大的教师编号+1
     *
     * @return
     */
    Long getMaxTeacherNo();

    /**
     * 删除教师
     *
     * @param ids
     */
    void deleteTeachers(String[] ids) throws Exception;

    /**
     * 教师填充学院
     *
     * @param teacherList
     */
    void teacherFillCollege(List<Teacher> teacherList);

    /**
     * 教师填充关联用户信息
     *
     * @param teacherList
     */
    void teacherWithUser(List<Teacher> teacherList);


    /**
     * 查询学院下教师的数量
     *
     * @param collegeId
     * @return
     */
    Integer getTeacherCountByCollegeId(Long collegeId);

    /**
     * 查询给定学院下教师的数量
     *
     * @param collegeIdList
     * @return
     */
    Integer getTeacherCountByCollegeIdList(List<Long> collegeIdList);

    /**
     * 根据用户ID查询教师信息
     *
     * @param userId
     * @return
     */
    Teacher findTeacherByUserId(Long userId);

    /**
     * 查询教师的等级分布情况
     *
     * @param teacherId
     * @return
     */
    Map<String, Object> findTeacherDegreeCountMap(Long teacherId);
}
