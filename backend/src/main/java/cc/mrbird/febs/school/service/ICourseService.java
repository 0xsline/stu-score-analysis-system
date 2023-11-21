package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.Course;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * @author IU
 */
public interface ICourseService extends IService<Course> {

    /**
     * 分页查询
     *
     * @param request
     * @param course
     * @return
     */
    IPage<Course> findCourses(QueryRequest request, Course course);


    /**
     * 获取最大的学期编号+1
     *
     * @return
     */
    Long getMaxCourseNo();

    /**
     * 删除学期
     *
     * @param ids
     */
    void deleteCourses(String[] ids);

    /**
     * 查询某个课程的成绩分析情况
     *
     * @param courseId
     * @return
     */
    Map<String, Object> findCourseAnalysisDetail(Long courseId);
}
