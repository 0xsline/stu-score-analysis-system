package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SchoolUtils;
import cc.mrbird.febs.school.entity.Course;
import cc.mrbird.febs.school.entity.StudentScore;
import cc.mrbird.febs.school.service.ICourseService;
import cc.mrbird.febs.school.service.IExamPlanService;
import cc.mrbird.febs.school.service.IStudentScoreService;
import cc.mrbird.febs.school.service.ITeachingArrangeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("education/course")
public class CourseController extends BaseController {

    private String message;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IStudentScoreService iStudentScoreService;


    /**
     * 返回全部的课程信息
     *
     * @return
     */
    @GetMapping("/all")
    @RequiresUser
    public List<Course> allCourseList() {
        return iCourseService.list();
    }


    /**
     * 课程列表
     *
     * @param request
     * @param course
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:course:view")
    public Map<String, Object> courseList(QueryRequest request, Course course) {
        IPage<Course> iPage = this.iCourseService.findCourses(request, course);
        List<Course> courseList = iPage.getRecords();

        if (CollectionUtils.isNotEmpty(courseList)) {
            courseList.forEach(obj -> {
                Integer teachingArrangeCount = iTeachingArrangeService.findCountByCourseId(obj.getCourseId());
                if (teachingArrangeCount != null) {
                    obj.setTeachingArrangeCount(teachingArrangeCount);
                }
                List<Long> userIdList = iTeachingArrangeService.findTeachIdListByCourseId(obj.getCourseId());
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    obj.setTeacherCount(userIdList.size());
                }
            });
        }

        return getDataTable(iPage);
    }


    /**
     * 新增课程
     *
     * @param course
     * @throws FebsException
     */
    @Log("新增课程")
    @PostMapping
    @RequiresPermissions("education:course:add")
    public void addCourse(@RequestBody @Valid Course course) throws FebsException {
        try {
            this.iCourseService.insert(course);
        } catch (Exception e) {
            message = "新增课程失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改课程
     *
     * @param course
     * @throws FebsException
     */
    @Log("修改课程")
    @PutMapping
    @RequiresPermissions("education:course:update")
    public void updateCourse(@RequestBody @Valid Course course) throws FebsException {
        try {
            this.iCourseService.modify(course);
            //修改课程满分同步更新该门课程关联的所有考试成绩的等级
            List<Long> teachingArrangeIdList = iTeachingArrangeService.findIdListByCourseId(course.getCourseId());
            if (CollectionUtils.isNotEmpty(teachingArrangeIdList)) {
                List<StudentScore> studentScoreList = iStudentScoreService
                        .findListByTeachingArrangeIdList(teachingArrangeIdList);
                if (CollectionUtils.isNotEmpty(studentScoreList)) {
                    studentScoreList.forEach(studentScore -> {
                        //学分
                        studentScore.setStudyScore(SchoolUtils.studyScore(studentScore.getScore(), course.getExamScore(), course.getStudyScore()));
                        //绩点
                        studentScore.setPointScore(SchoolUtils.calculatePoint(studentScore.getScore(), course.getExamScore()));
                        //等级
                        studentScore.setDegreeScore(SchoolUtils.scoreCategory(studentScore.getScore(), course.getExamScore()));
                    });
                    iStudentScoreService.updateBatchById(studentScoreList);
                    System.out.println("修改(" + course.getCourseId() + "|"
                            + course.getCourseName()+")课程满分，同步更新课程关联的所有课程的考试成绩等级记录数："
                            + studentScoreList.size());
                }
            }
        } catch (Exception e) {
            message = "修改课程失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除课程
     *
     * @param courseIds
     * @throws FebsException
     */
    @Log("删除课程")
    @DeleteMapping("/{courseIds}")
    @RequiresPermissions("education:course:delete")
    public void deleteCourse(@NotBlank(message = "{required}") @PathVariable String courseIds) throws FebsException {
        try {
            String[] ids = courseIds.split(StringPool.COMMA);
            List<Long> courseIdList = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
            Integer teachingArrangeCount = iTeachingArrangeService.findCountByCourseIdList(courseIdList);
            if (teachingArrangeCount > 0) {
                throw new FebsException("教学安排关联了课程，不能删除");
            }

            Integer examPlanCount = iExamPlanService.findCountByCourseIdList(courseIdList);
            if (examPlanCount > 0) {
                throw new FebsException("考试计划关联了课程，不能删除");
            }
            this.iCourseService.deleteCourses(ids);
        } catch (Exception e) {
            message = "删除课程失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param course
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("education:course:export")
    public void export(QueryRequest request, Course course, HttpServletResponse response) throws FebsException {
        try {
            List<Course> courses = this.iCourseService.findCourses(request, course).getRecords();
            ExcelKit.$Export(Course.class, response).downXlsx(courses, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 根据课程分析老师教学情况
     *
     * @param courseId
     */

    @GetMapping("/analysis/{courseId}")
    @RequiresPermissions("education:course:analysis")
    public Map<String, Object> analysis(@PathVariable Long courseId) {
        return iCourseService.findCourseAnalysisDetail(courseId);
    }
}
