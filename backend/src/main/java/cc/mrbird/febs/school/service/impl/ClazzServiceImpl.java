package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.*;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.AnalysisUtils;
import cc.mrbird.febs.common.utils.SchoolConstants;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.ClazzMapper;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements IClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Autowired
    private IGradeService iGradeService;

    @Autowired
    private ICollegeService iCollegeService;

    @Autowired
    private ITeacherService teacherService;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Override
    @CustomerInsert
    public boolean insert(Clazz entity) {
        entity.setClazzNo(clazzMapper.getMaxClazzNo());
        return clazzMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Clazz entity) {
        return clazzMapper.updateById(entity) > 0;
    }

    @Override
    public IPage<Clazz> findClazzPage(QueryRequest request, Clazz clazz) {
        try {
            LambdaQueryWrapper<Clazz> queryWrapper = new LambdaQueryWrapper<>();
            if (clazz.getManagerId() != null) {
                queryWrapper.eq(Clazz::getManagerId, clazz.getManagerId());
            }
            if (StringUtils.isNotBlank(clazz.getClazzName())) {
                queryWrapper.like(Clazz::getClazzName, clazz.getClazzName());
            }
            if (clazz.getClazzNo() != null) {
                queryWrapper.eq(Clazz::getClazzNo, clazz.getClazzNo());
            }

            if (clazz.getGradeId() != null) {
                queryWrapper.eq(Clazz::getGradeId, clazz.getGradeId());
            }

            if (clazz.getClazzId() != null) {
                queryWrapper.eq(Clazz::getClazzId, clazz.getClazzId());
            }

            if (StringUtils.isNotBlank(clazz.getCreateTimeFrom()) && StringUtils.isNotBlank(clazz.getCreateTimeTo())) {
                queryWrapper
                        .ge(Clazz::getCreateTime, clazz.getCreateTimeFrom())
                        .le(Clazz::getCreateTime, clazz.getCreateTimeTo());
            }
            Page<Clazz> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<Clazz> clazzList = iPage.getRecords();
            if (CollectionUtils.isEmpty(clazzList)) {
                return iPage;
            }

            //班级填充年级填充学院
            clazzFillGradeAndCollege(clazzList);

            //填充班主任
            clazzWithManager(clazzList);

            //班级下学生数量
            clazzList.forEach(obj -> {
                Integer studentCount = iStudentService.studentCountByClazzId(obj.getClazzId());
                if (studentCount != null) {
                    obj.setStudentCount(studentCount);
                }
            });

            return iPage;
        } catch (Exception e) {
            log.error("获取班级失败", e);
            return null;
        }
    }

    @Override
    public void clazzFillGradeAndCollege(List<Clazz> clazzList) {
        if (CollectionUtils.isEmpty(clazzList)) {
            return;
        }
        List<Long> gradeIds = clazzList.stream().map(Clazz::getGradeId).distinct().collect(Collectors.toList());
        List<Grade> gradeList = new ArrayList<>(iGradeService.listByIds(gradeIds));
        Map<Long, Grade> longGradeMap = gradeList.stream().collect(Collectors.toMap(Grade::getGradeId, it -> it));
        clazzList.forEach(obj -> {
            obj.setGrade(longGradeMap.get(obj.getGradeId()));
        });
        iGradeService.gradeFillCollege(gradeList);
    }

    @Override
    public void clazzWithManager(List<Clazz> clazzList) {
        List<Long> teacherIds = clazzList.stream().map(Clazz::getManagerId).distinct().collect(Collectors.toList());
        List<Teacher> teacherList = new ArrayList<>(teacherService.listByIds(teacherIds));
        Map<Long, Teacher> longTeacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getTeacherId,
                it -> it));
        clazzList.forEach(obj -> {
            obj.setTeacher(longTeacherMap.get(obj.getManagerId()));
            obj.setManagerName(obj.getTeacher().getTeacherName());
        });
    }

    @Override
    public List<SchoolTree> buildOrganizationTreeList(Boolean select) {
        List<College> collegeList = iCollegeService.list();
        List<Grade> gradeList = iGradeService.list();
        List<SchoolTree> schoolTreeList = new ArrayList<>();
        for (int i = 0; i < collegeList.size(); i++) {
            College college = collegeList.get(i);
            SchoolTree schoolTree = new SchoolTree();
            schoolTree.setKey(SchoolConstants.COLLEGE + college.getCollegeId().toString());
            schoolTree.setValue(SchoolConstants.COLLEGE + college.getCollegeId().toString());
            schoolTree.setTitle(college.getCollegeName());
            schoolTree.setSelectable(false);
            schoolTreeList.add(schoolTree);
        }

        for (int i = 0; i < gradeList.size(); i++) {
            Grade grade = gradeList.get(i);
            SchoolTree child = new SchoolTree();
            if (select) {
                child.setKey(grade.getGradeId().toString());
                child.setValue(grade.getGradeId().toString());
            } else {
                child.setKey(SchoolConstants.GRADE + grade.getGradeId().toString());
                child.setValue(SchoolConstants.GRADE + grade.getGradeId().toString());
            }

            child.setTitle(grade.getGradeName());
            child.setSelectable(true);
            for (int j = 0; j < schoolTreeList.size(); j++) {
                SchoolTree schoolTree = schoolTreeList.get(j);
                if ((SchoolConstants.COLLEGE + grade.getCollegeId().toString()).equals(schoolTree.getKey())) {
                    if (schoolTree.getChildren() == null) {
                        schoolTree.setChildren(new ArrayList<>());
                    }
                    schoolTree.getChildren().add(child);
                }
            }
        }
        return schoolTreeList;
    }

    @Override
    public Integer getClazzCountByGradeId(Long gradeId) {
        Wrapper<Clazz> wrapper = new QueryWrapper<Clazz>().lambda()
                .eq(Clazz::getGradeId, gradeId);
        return this.count(wrapper);
    }

    @Override
    public List<Long> getClazzIdListByGradeId(Long gradeId) {
        Wrapper<Clazz> wrapper = new QueryWrapper<Clazz>().lambda().select(Clazz::getClazzId)
                .eq(Clazz::getGradeId, gradeId);
        List<Clazz> clazzList = this.list(wrapper);
        if (CollectionUtils.isEmpty(clazzList)) {
            return null;
        }
        return clazzList.stream().map(Clazz::getClazzId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getClazzIdListByGradeIdList(List<Long> gradeIdList) {
        Wrapper<Clazz> wrapper = new QueryWrapper<Clazz>().lambda().select(Clazz::getClazzId)
                .in(Clazz::getGradeId, gradeIdList);
        List<Clazz> clazzList = this.list(wrapper);
        if (CollectionUtils.isEmpty(clazzList)) {
            return null;
        }
        return clazzList.stream().map(Clazz::getClazzId).collect(Collectors.toList());
    }

    @Override
    public Integer getClazzCountByManagerIdList(List<Long> managerIdList) {
        Wrapper<Clazz> wrapper = new QueryWrapper<Clazz>().lambda()
                .in(Clazz::getManagerId, managerIdList);
        return this.count(wrapper);
    }

    @Override
    public Map<String, Object> findClazzAnalysisDetail(Long clazzId) {
        Map<String, Object> map = new HashMap<>();
        //课程分析
        List<Long> courseIdList = iTeachingArrangeService.findCourseIdListByClazzId(clazzId);
        List<TeacherAnalysisInfo> teacherAnalysisInfoList = new ArrayList<>(courseIdList.size());
        courseIdList.forEach(courseId -> {
            Course course = iCourseService.getById(courseId);
            //班级和课程 学生成绩
            List<Long> teachingArrangeIdList = iTeachingArrangeService.findIdByClazzIdAndCourseId(clazzId, courseId);
            List<StudentScore> studentScoreList = new ArrayList<>(iStudentScoreService
                    .findListByTeachingArrangeIdList(teachingArrangeIdList));
            TeacherAnalysisInfo teacherAnalysisInfo = AnalysisUtils.buildTeacherAnalysisInfo(studentScoreList);
            teacherAnalysisInfo.setCourseId(courseId);
            teacherAnalysisInfo.setCourseNo(course.getCourseNo());
            teacherAnalysisInfo.setCourseName(course.getCourseName());
            teacherAnalysisInfo.setCourseType(course.getCourseType());
            teacherAnalysisInfoList.add(teacherAnalysisInfo);
        });
        map.put("courseAnalysisInfoList", teacherAnalysisInfoList);
        map.put("courseBarSource", AnalysisUtils.buildBarData2(teacherAnalysisInfoList));
        map.put("courseBarSeries", AnalysisUtils.buildBarSeries2(teacherAnalysisInfoList));

        //教师分析
        List<Long> teacherIdList = iTeachingArrangeService.findTeacherIdListByClazzId(clazzId);
        List<CourseAnalysisInfo> courseAnalysisInfoList = new ArrayList<>(teacherIdList.size());
        //该课每个老师成绩
        teacherIdList.forEach(teacherId -> {
            Teacher teacher = iTeacherService.getById(teacherId);
            List<Long> teachingArrangeIdList = iTeachingArrangeService.findIdByClazzIdAndTeacherId(clazzId, teacherId);
            List<StudentScore> studentScoreList = new ArrayList<>(iStudentScoreService
                    .findListByTeachingArrangeIdList(teachingArrangeIdList));
            CourseAnalysisInfo courseAnalysisInfo = AnalysisUtils.buildCourseAnalysisInfo(studentScoreList);
            courseAnalysisInfo.setTeacherId(teacherId);
            courseAnalysisInfo.setTeacherNo(teacher.getTeacherNo());
            courseAnalysisInfo.setTeacherName(teacher.getTeacherName());
            courseAnalysisInfoList.add(courseAnalysisInfo);
        });
        map.put("teacherAnalysisInfoList", courseAnalysisInfoList);
        map.put("teacherBarSource", AnalysisUtils.buildBarData(courseAnalysisInfoList));
        map.put("teacherBarSeries", AnalysisUtils.buildBarSeries(courseAnalysisInfoList));

        //学生分析
        List<Long> studentIdList = iStudentService.findStudentIdListByClazzId(clazzId);
        List<StudentAnalysisInfo> studentAnalysisInfoList = new ArrayList<>(studentIdList.size());
        studentIdList.forEach(studentId -> {
            Student student = iStudentService.getById(studentId);
            List<StudentScore> studentScoreList = new ArrayList<>(iStudentScoreService
                    .findListByStudentId(studentId));
            StudentAnalysisInfo studentAnalysisInfo = AnalysisUtils.buildStudentAnalysisInfo(studentScoreList);
            studentAnalysisInfo.setStudentId(studentId);
            studentAnalysisInfo.setStudentNo(student.getStudentNo());
            studentAnalysisInfo.setStudentName(student.getStudentName());
            studentAnalysisInfoList.add(studentAnalysisInfo);
        });

        map.put("studentAnalysisInfoList", studentAnalysisInfoList);
        map.put("studentBarSource", AnalysisUtils.buildBarData3(studentAnalysisInfoList));
        map.put("studentBarSeries", AnalysisUtils.buildBarSeries3(studentAnalysisInfoList));
        return map;
    }

    @Override
    public List<Long> getClazzIdListByManagerId(Long managerId) {
        Wrapper<Clazz> wrapper = new QueryWrapper<Clazz>().lambda().eq(Clazz::getManagerId, managerId);
        return this.list(wrapper).stream().map(Clazz::getClazzId).collect(Collectors.toList());
    }

    @Override
    public Long getMaxClazzNo() {
        return clazzMapper.getMaxClazzNo();
    }

    @Override
    public void deleteClazzList(String[] ids) throws FebsException {
        List<String> list = Arrays.asList(ids);
        List<Long> clazzIdList = list.stream().map(Long::valueOf).collect(Collectors.toList());
        Integer studentCount = iStudentService.studentCountByClazzIdList(clazzIdList);
        if (studentCount > 0) {
            throw new FebsException("班级下存在学生信息，不能删除");
        }
        clazzMapper.deleteBatchIds(list);
    }
}
