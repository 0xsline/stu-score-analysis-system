package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.TeachingArrangeMapper;
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

import java.util.ArrayList;
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
public class TeachingArrangeServiceImpl extends ServiceImpl<TeachingArrangeMapper, TeachingArrange> implements ITeachingArrangeService {

    @Autowired
    private TeachingArrangeMapper teachingArrangeMapper;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private ISemesterService iSemesterService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private IStudentService iStudentService;

    @Override
    @CustomerInsert
    public boolean insert(TeachingArrange teachingArrange) throws FebsException {
        int count = this.count(new LambdaQueryWrapper<TeachingArrange>()
                .eq(TeachingArrange::getSemesterId, teachingArrange.getSemesterId())
                .eq(TeachingArrange::getClazzId, teachingArrange.getClazzId())
                .eq(TeachingArrange::getCourseId, teachingArrange.getCourseId()));
        if (count > 0) {
            throw new FebsException("该教学安排已存在");
        }
        return teachingArrangeMapper.insert(teachingArrange) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(TeachingArrange teachingArrange) throws FebsException {
        TeachingArrange dbTeachingArrange = this.getOne(new LambdaQueryWrapper<TeachingArrange>()
                .eq(TeachingArrange::getSemesterId, teachingArrange.getSemesterId())
                .eq(TeachingArrange::getClazzId, teachingArrange.getClazzId())
                .eq(TeachingArrange::getCourseId, teachingArrange.getCourseId()));
        if (dbTeachingArrange != null && dbTeachingArrange.getTeachingArrangeId() != teachingArrange.getTeachingArrangeId()) {
            throw new FebsException("该教学安排已存在");
        }
        return teachingArrangeMapper.updateById(teachingArrange) > 0;
    }

    @Override
    public IPage<TeachingArrange> findTeachingArranges(QueryRequest request, TeachingArrange teachingArrange) {
        try {
            LambdaQueryWrapper<TeachingArrange> queryWrapper = new LambdaQueryWrapper<>();
            if (teachingArrange.getSemesterId() != null) {
                queryWrapper.eq(TeachingArrange::getSemesterId, teachingArrange.getSemesterId());
            }

            if (teachingArrange.getTeacherId() != null) {
                queryWrapper.eq(TeachingArrange::getTeacherId, teachingArrange.getTeacherId());
            }

            if (teachingArrange.getClazzId() != null) {
                queryWrapper.eq(TeachingArrange::getClazzId, teachingArrange.getClazzId());
            }


            if (StringUtils.isNotBlank(teachingArrange.getCreateTimeFrom()) && StringUtils.isNotBlank(teachingArrange.getCreateTimeTo())) {
                queryWrapper
                        .ge(TeachingArrange::getCreateTime, teachingArrange.getCreateTimeFrom())
                        .le(TeachingArrange::getCreateTime, teachingArrange.getCreateTimeTo());
            }
            Page<TeachingArrange> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<TeachingArrange> teachingArrangeList = iPage.getRecords();
            if (CollectionUtils.isEmpty(teachingArrangeList)) {
                return iPage;
            }
            //考试计划填充班级年级学院信息
            teachingArrangeFillClazz(teachingArrangeList);
            //考试计划填充学期
            teachingArrangeFillSemester(teachingArrangeList);
            //考试计划填充课程
            teachingArrangeFillCourse(teachingArrangeList);
            //考试计划填充教师
            teachingArrangeFillTeacher(teachingArrangeList);

            //挂科数量
            List<Long> teachingArrangeIdList = teachingArrangeList.stream().map(TeachingArrange::getTeachingArrangeId)
                    .collect(Collectors.toList());
            Map<Long, Long> map = iStudentScoreService.teachingArrangeIdStudentCount(teachingArrangeIdList, ScoreType.FIVE.getCode());
            if (map != null) {
                teachingArrangeList.forEach(obj -> {
                    Long count = map.get(obj.getTeachingArrangeId());
                    if (count != null) {
                        obj.setFailStudentCount(count);
                    }
                });
            }
            return iPage;
        } catch (Exception e) {
            log.error("获取考试计划失败", e);
            return null;
        }
    }

    @Override
    public void teachingArrangeFillTeacher(List<TeachingArrange> teachingArrangeList) {
        List<Long> teacherIds = teachingArrangeList.stream()
                .map(TeachingArrange::getTeacherId).distinct()
                .collect(Collectors.toList());
        List<Teacher> teacherList = new ArrayList<>(iTeacherService.listByIds(teacherIds));
        Map<Long, Teacher> longTeacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getTeacherId, it -> it));
        teachingArrangeList.forEach(obj -> {
            obj.setTeacher(longTeacherMap.get(obj.getTeacherId()));
        });
    }

    @Override
    public Integer findCountBySemesterIdList(List<Long> semesterIdList) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().in(TeachingArrange::getSemesterId, semesterIdList);
        return this.count(wrapper);
    }

    @Override
    public Integer findCountByCourseIdList(List<Long> courseIdList) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().in(TeachingArrange::getCourseId, courseIdList);
        return this.count(wrapper);
    }

    @Override
    public Integer findCountByCourseId(Long courseId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().eq(TeachingArrange::getCourseId, courseId);
        return this.count(wrapper);
    }

    @Override
    public List<Long> findTeachIdListByCourseId(Long courseId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeacherId)
                .eq(TeachingArrange::getCourseId, courseId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findIdListByCourseId(Long courseId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>().lambda()
                .select(TeachingArrange::getTeachingArrangeId)
                .eq(TeachingArrange::getCourseId, courseId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findTeachIdListByClazzId(Long clazzId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeacherId)
                .eq(TeachingArrange::getClazzId, clazzId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findTeacherIdListByClazzId(Long clazzId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeacherId)
                .eq(TeachingArrange::getClazzId, clazzId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findCourseIdListByTeacherId(Long teacherId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getCourseId)
                .eq(TeachingArrange::getTeacherId, teacherId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findCourseIdListByClazzId(Long clazzId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getCourseId)
                .eq(TeachingArrange::getClazzId, clazzId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public List<Long> findIdByTeacherIdAndCourseId(Long teacherId, Long courseId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeachingArrangeId)
                .eq(TeachingArrange::getCourseId, courseId)
                .eq(TeachingArrange::getTeacherId, teacherId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findIdByClazzIdAndCourseId(Long clazzId, Long courseId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeachingArrangeId)
                .eq(TeachingArrange::getClazzId, clazzId)
                .eq(TeachingArrange::getCourseId, courseId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findIdByClazzIdAndTeacherId(Long clazzId, Long teacherId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().select(TeachingArrange::getTeachingArrangeId)
                .eq(TeachingArrange::getClazzId, clazzId)
                .eq(TeachingArrange::getTeacherId, teacherId);
        return this.listObjs(wrapper).stream().map(it -> (Long) it).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Integer findCountByTeacherIdList(List<Long> teacherIdList) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().in(TeachingArrange::getTeacherId, teacherIdList);
        return this.count(wrapper);
    }

    @Override
    public List<TeachingArrange> findTeachingArrangeByCondition(Long clazzId, Long semesterId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().eq(TeachingArrange::getClazzId, clazzId)
                .eq(TeachingArrange::getSemesterId, semesterId);
        return this.list(wrapper);
    }

    @Override
    public List<Long> findTeachingArrangeIdListByCondition(Long clazzId, Long semesterId) {
        List<TeachingArrange> teachingArrangeList = findTeachingArrangeByCondition(clazzId, semesterId);
        return teachingArrangeList.stream().map(TeachingArrange::getTeachingArrangeId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findTeachingArrangeIdListByTeacherId(Long teacherId) {
        Wrapper<TeachingArrange> wrapper = new QueryWrapper<TeachingArrange>()
                .lambda().eq(TeachingArrange::getTeacherId, teacherId);
        return this.list(wrapper).stream().map(TeachingArrange::getTeachingArrangeId).collect(Collectors.toList());
    }

    @Override
    public void teachingArrangeFillCourse(List<TeachingArrange> teachingArrangeList) {
        List<Long> courseIds = teachingArrangeList.stream()
                .map(TeachingArrange::getCourseId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(courseIds)) return;
        List<Course> courseList = new ArrayList<>(iCourseService.listByIds(courseIds));
        Map<Long, Course> longCourseMap = courseList.stream().collect(Collectors.toMap(Course::getCourseId, it -> it));
        teachingArrangeList.forEach(obj -> {
            obj.setCourse(longCourseMap.get(obj.getCourseId()));
        });
    }


    public void teachingArrangeFillSemester(List<TeachingArrange> teachingArrangeList) {
        List<Long> semesterIds = teachingArrangeList.stream().map(TeachingArrange::getSemesterId)
                .distinct().collect(Collectors.toList());
        List<Semester> semesterList = new ArrayList<>(iSemesterService.listByIds(semesterIds));
        Map<Long, Semester> longSemesterMap = semesterList.stream().collect(Collectors.toMap(Semester::getSemesterId, it -> it));
        teachingArrangeList.forEach(obj -> {
            obj.setSemester(longSemesterMap.get(obj.getSemesterId()));
        });
    }

    @Override
    public void teachingArrangeFillClazz(List<TeachingArrange> teachingArrangeList) {
        List<Long> clazzIds = teachingArrangeList.stream().map(TeachingArrange::getClazzId)
                .distinct().collect(Collectors.toList());
        List<Clazz> clazzList = new ArrayList<>(iClazzService.listByIds(clazzIds));
        Map<Long, Clazz> longClazzMap = clazzList.stream().collect(Collectors.toMap(Clazz::getClazzId, it -> it));
        Map<Long, Long> clazzStudentCountMap = iStudentService.clazzStudentCount(clazzIds);
        teachingArrangeList.forEach(obj -> {
            obj.setClazz(longClazzMap.get(obj.getClazzId()));
            if (clazzStudentCountMap.get(obj.getClazzId()) != null) {
                obj.setStudentCount(clazzStudentCountMap.get(obj.getClazzId()));
            }
        });
        iClazzService.clazzFillGradeAndCollege(clazzList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeachingArranges(String[] ids) {
        List<String> list = Arrays.asList(ids);
        iStudentScoreService.deleteByTeachingArrangeIdList(list.stream().map(Long::valueOf)
                .collect(Collectors.toList()));
        teachingArrangeMapper.deleteBatchIds(list);
    }
}
