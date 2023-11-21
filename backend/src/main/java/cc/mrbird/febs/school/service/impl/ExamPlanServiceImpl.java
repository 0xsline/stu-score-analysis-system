package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.ExamPlanMapper;
import cc.mrbird.febs.school.entity.*;
import cc.mrbird.febs.school.service.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExamPlanServiceImpl extends ServiceImpl<ExamPlanMapper, ExamPlan> implements IExamPlanService {

    @Autowired
    private ExamPlanMapper examPlanMapper;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private ISemesterService iSemesterService;

    @Autowired
    private IExamService iExamService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IExamScoreService iExamScoreService;

    @Override
    @CustomerInsert
    public boolean insert(ExamPlan examPlan) throws FebsException {
        int count = this.count(new LambdaQueryWrapper<ExamPlan>().eq(ExamPlan::getExamId, examPlan.getExamId())
                .eq(ExamPlan::getSemesterId, examPlan.getSemesterId()).eq(ExamPlan::getClazzId, examPlan.getClazzId())
                .eq(ExamPlan::getCourseId, examPlan.getCourseId()));
        if (count > 0) {
            throw new FebsException("该考试计划已存在");
        }
        //设置考试计划编号
        examPlan.setExamPlanNo(examPlanMapper.getMaxExamPlanNo());
        return examPlanMapper.insert(examPlan) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(ExamPlan examPlan) throws FebsException {
        ExamPlan dbExamPlan = this.getOne(new LambdaQueryWrapper<ExamPlan>().eq(ExamPlan::getExamId, examPlan.getExamId())
                .eq(ExamPlan::getSemesterId, examPlan.getSemesterId()).eq(ExamPlan::getClazzId, examPlan.getClazzId())
                .eq(ExamPlan::getCourseId, examPlan.getCourseId()));
        if (dbExamPlan.getExamPlanId() != examPlan.getExamPlanId()) {
            throw new FebsException("该考试计划已存在");
        }
        return examPlanMapper.updateById(examPlan) > 0;
    }

    @Override
    public IPage<ExamPlan> findExamPlans(QueryRequest request, ExamPlan examPlan) {
        try {
            LambdaQueryWrapper<ExamPlan> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(examPlan.getExamPlanName())) {
                queryWrapper.like(ExamPlan::getExamPlanName, examPlan.getExamPlanName());
            }
            if (examPlan.getExamPlanNo() != null) {
                queryWrapper.eq(ExamPlan::getExamPlanNo, examPlan.getExamPlanNo());
            }

            if (StringUtils.isNotBlank(examPlan.getCreateTimeFrom()) && StringUtils.isNotBlank(examPlan.getCreateTimeTo())) {
                queryWrapper
                        .ge(ExamPlan::getCreateTime, examPlan.getCreateTimeFrom())
                        .le(ExamPlan::getCreateTime, examPlan.getCreateTimeTo());
            }
            Page<ExamPlan> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<ExamPlan> examPlanList = iPage.getRecords();
            if (CollectionUtils.isEmpty(examPlanList)) {
                return iPage;
            }
            //考试计划填充班级年级学院信息
            examPlanFillClazz(examPlanList);
            //考试计划填充考试
            examPlanFillExam(examPlanList);
            //考试计划填充学期
            examPlanFillSemester(examPlanList);
            //考试计划填充课程
            examPlanFillCourse(examPlanList);
            //考试计划填充教师
            examPlanFillTeacher(examPlanList);

            return iPage;
        } catch (Exception e) {
            log.error("获取考试计划失败", e);
            return null;
        }
    }

    @Override
    public void examPlanFillTeacher(List<ExamPlan> examPlanList) {
        List<Long> teacherIds = examPlanList.stream().map(ExamPlan::getTeacherId).distinct().collect(Collectors.toList());
        List<Teacher> teacherList = iTeacherService.listByIds(teacherIds).stream().collect(Collectors.toList());
        Map<Long, Teacher> longTeacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getTeacherId, it -> it));
        examPlanList.forEach(obj -> {
            obj.setTeacher(longTeacherMap.get(obj.getTeacherId()));
        });
    }

    @Override
    public Integer findCountBySemesterIdList(List<Long> semesterIdList) {
        Wrapper<ExamPlan> wrapper = new QueryWrapper<ExamPlan>().lambda()
                .in(ExamPlan::getSemesterId, semesterIdList);
        return this.count(wrapper);
    }

    @Override
    public Integer findCountByCourseIdList(List<Long> courseIdList) {
        Wrapper<ExamPlan> wrapper = new QueryWrapper<ExamPlan>().lambda()
                .in(ExamPlan::getCourseId, courseIdList);
        return this.count(wrapper);
    }

    @Override
    public Integer findCountByExamIdList(List<Long> examIdList) {
        Wrapper<ExamPlan> wrapper = new QueryWrapper<ExamPlan>().lambda()
                .in(ExamPlan::getExamId, examIdList);
        return this.count(wrapper);
    }

    @Override
    public Integer findCountByTeacherIdList(List<Long> teacherIdList) {
        Wrapper<ExamPlan> wrapper = new QueryWrapper<ExamPlan>().lambda()
                .in(ExamPlan::getTeacherId, teacherIdList);
        return this.count(wrapper);
    }

    @Override
    public void examPlanFillCourse(List<ExamPlan> examPlanList) {
        List<Long> courseIds = examPlanList.stream().map(ExamPlan::getCourseId).distinct().collect(Collectors.toList());
        List<Course> courseList = iCourseService.listByIds(courseIds).stream().collect(Collectors.toList());
        Map<Long, Course> longCourseMap = courseList.stream().collect(Collectors.toMap(Course::getCourseId, it -> it));
        examPlanList.forEach(obj -> {
            obj.setCourse(longCourseMap.get(obj.getCourseId()));
        });
    }

    @Override
    public void examPlanFillExam(List<ExamPlan> examPlanList) {
        List<Long> examIds = examPlanList.stream().map(ExamPlan::getExamId).distinct().collect(Collectors.toList());
        List<Exam> examList = iExamService.listByIds(examIds).stream().collect(Collectors.toList());
        Map<Long, Exam> longExamMap = examList.stream().collect(Collectors.toMap(Exam::getExamId, it -> it));
        examPlanList.forEach(obj -> {
            obj.setExam(longExamMap.get(obj.getExamId()));
        });
    }

    public void examPlanFillSemester(List<ExamPlan> examPlanList) {
        List<Long> semesterIds = examPlanList.stream().map(ExamPlan::getSemesterId).distinct().collect(Collectors.toList());
        List<Semester> semesterList = iSemesterService.listByIds(semesterIds).stream().collect(Collectors.toList());
        Map<Long, Semester> longSemesterMap = semesterList.stream().collect(Collectors.toMap(Semester::getSemesterId, it -> it));
        examPlanList.forEach(obj -> {
            obj.setSemester(longSemesterMap.get(obj.getSemesterId()));
        });
    }

    @Override
    public void examPlanFillClazz(List<ExamPlan> examPlanList) {
        List<Long> clazzIds = examPlanList.stream().map(ExamPlan::getClazzId).distinct().collect(Collectors.toList());
        List<Clazz> clazzList = iClazzService.listByIds(clazzIds).stream().collect(Collectors.toList());
        Map<Long, Clazz> longClazzMap = clazzList.stream().collect(Collectors.toMap(Clazz::getClazzId, it -> it));
        examPlanList.forEach(obj -> {
            obj.setClazz(longClazzMap.get(obj.getClazzId()));
        });
        iClazzService.clazzFillGradeAndCollege(clazzList);
    }

    @Override
    public Long getMaxExamPlanNo() {
        return examPlanMapper.getMaxExamPlanNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExamPlans(String[] ids) {
        List<String> list = Arrays.asList(ids);
        iExamScoreService.deleteByExamPlanIdList(list.stream().map(Long::valueOf).collect(Collectors.toList()));
        examPlanMapper.deleteBatchIds(list);
    }
}
