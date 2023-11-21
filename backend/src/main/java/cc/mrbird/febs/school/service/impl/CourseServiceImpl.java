package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.CourseAnalysisInfo;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.utils.AnalysisUtils;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.CourseMapper;
import cc.mrbird.febs.school.entity.Course;
import cc.mrbird.febs.school.entity.StudentScore;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.ICourseService;
import cc.mrbird.febs.school.service.IStudentScoreService;
import cc.mrbird.febs.school.service.ITeacherService;
import cc.mrbird.febs.school.service.ITeachingArrangeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private ITeacherService iTeacherService;

    @Override
    @CustomerInsert
    public boolean insert(Course course) {
        //设置学生编号
        course.setCourseNo(courseMapper.getMaxCourseNo());
        return courseMapper.insert(course) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Course course) {
        return courseMapper.updateById(course) > 0;
    }

    @Override
    public IPage<Course> findCourses(QueryRequest request, Course course) {
        try {
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(course.getCourseName())) {
                queryWrapper.like(Course::getCourseName, course.getCourseName());
            }
            if (course.getCourseNo() != null) {
                queryWrapper.eq(Course::getCourseNo, course.getCourseNo());
            }

            if (StringUtils.isNotBlank(course.getCreateTimeFrom()) && StringUtils.isNotBlank(course.getCreateTimeTo())) {
                queryWrapper
                        .ge(Course::getCreateTime, course.getCreateTimeFrom())
                        .le(Course::getCreateTime, course.getCreateTimeTo());
            }
            Page<Course> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取学生失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxCourseNo() {
        return courseMapper.getMaxCourseNo();
    }

    @Override
    public void deleteCourses(String[] ids) {
        List<String> list = Arrays.asList(ids);
        courseMapper.deleteBatchIds(list);
    }

    @Override
    public Map<String, Object> findCourseAnalysisDetail(Long courseId) {

        Map<String, Object> map = new HashMap<>();
        List<Long> teacherIdList = iTeachingArrangeService.findTeachIdListByCourseId(courseId);
        List<CourseAnalysisInfo> courseAnalysisInfoList = new ArrayList<>(teacherIdList.size());
        //该课每个老师成绩
        teacherIdList.forEach(teacherId -> {
            Teacher teacher = iTeacherService.getById(teacherId);
            List<Long> teachingArrangeIdList = iTeachingArrangeService.findIdByTeacherIdAndCourseId(teacherId, courseId);
            List<StudentScore> studentScoreList = new ArrayList<>(iStudentScoreService.findListByTeachingArrangeIdList(teachingArrangeIdList));
            CourseAnalysisInfo courseAnalysisInfo = AnalysisUtils.buildCourseAnalysisInfo(studentScoreList);
            courseAnalysisInfo.setTeacherId(teacherId);
            courseAnalysisInfo.setTeacherNo(teacher.getTeacherNo());
            courseAnalysisInfo.setTeacherName(teacher.getTeacherName());
            courseAnalysisInfoList.add(courseAnalysisInfo);
        });
        map.put("courseAnalysisInfoList", courseAnalysisInfoList);
        map.put("barSource", AnalysisUtils.buildBarData(courseAnalysisInfoList));
        map.put("barSeries", AnalysisUtils.buildBarSeries(courseAnalysisInfoList));
        return map;
    }
}
