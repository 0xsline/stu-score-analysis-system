package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.TeacherAnalysisInfo;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.common.enums.UserType;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.*;
import cc.mrbird.febs.school.dao.TeacherMapper;
import cc.mrbird.febs.school.entity.College;
import cc.mrbird.febs.school.entity.Course;
import cc.mrbird.febs.school.entity.StudentScore;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.*;
import cc.mrbird.febs.system.domain.Role;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.service.RoleService;
import cc.mrbird.febs.system.service.UserService;
import cn.hutool.core.date.DateUtil;
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
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ICollegeService iCollegeService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Override
    @CustomerInsert
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(Teacher entity) throws Exception {
        Role role = roleService.findByTeacher();
        User user = SchoolUtils.teacherToUser(entity, null);
        user.setRoleId(role.getRoleId().toString());
        user.setUserType(UserType.TEACHER.getCode());
        Long userId = this.userService.createUser(user);
        entity.setUserId(userId);
        entity.setTeacherNo(teacherMapper.getMaxTeacherNo());
        return teacherMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(Teacher entity) throws Exception {
        Role role = roleService.findByTeacher();
        User user = SchoolUtils.teacherToUser(entity, null);
        user.setUserId(entity.getUserId());
        user.setRoleId(role.getRoleId().toString());
        user.setUserType(UserType.TEACHER.getCode());
        this.userService.updateUser(user);
        return teacherMapper.updateById(entity) > 0;
    }

    @Override
    public IPage<Teacher> findTeachers(QueryRequest request, Teacher teacher) {
        try {
            LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(teacher.getTeacherName())) {
                queryWrapper.like(Teacher::getTeacherName, teacher.getTeacherName());
            }
            if (teacher.getTeacherNo() != null) {
                queryWrapper.eq(Teacher::getTeacherNo, teacher.getTeacherNo());
            }

            if (teacher.getCollegeId() != null) {
                queryWrapper.eq(Teacher::getCollegeId, teacher.getCollegeId());
            }

            if (teacher.getTeacherId() != null) {
                queryWrapper.eq(Teacher::getTeacherId, teacher.getTeacherId());
            }

            if (StringUtils.isNotBlank(teacher.getCreateTimeFrom()) && StringUtils.isNotBlank(teacher.getCreateTimeTo())) {
                queryWrapper
                        .ge(Teacher::getCreateTime, teacher.getCreateTimeFrom())
                        .le(Teacher::getCreateTime, teacher.getCreateTimeTo());
            }
            Page<Teacher> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<Teacher> teacherList = iPage.getRecords();
            if (CollectionUtils.isEmpty(teacherList)) {
                return iPage;
            }
            //教师填充学院
            teacherFillCollege(teacherList);
            //教师填充用户信息
            teacherWithUser(teacherList);

            teacherList.forEach(teacher1 -> {
                List<Long> teachingArrangeIdList = iTeachingArrangeService.findTeachingArrangeIdListByTeacherId(teacher1
                        .getTeacherId());
                if (!CollectionUtils.isEmpty(teachingArrangeIdList)) {
                    Integer count = iStudentScoreService.findCount(teachingArrangeIdList);
                    if (count != null) {
                        teacher1.setExamCount(count);
                    }
                }

                if (teacher1.getBirthDate() != null) {
                    teacher1.setAge(DateUtil.ageOfNow(teacher1.getBirthDate()) + "");
                }
            });
            return iPage;
        } catch (Exception e) {
            log.error("获取教师失败", e);
            return null;
        }
    }

    @Override
    public Map<String, Object> findTeacherAnalysisDetail(Long teacherId) {
        Map<String, Object> map = new HashMap<>();
        List<Long> courseIdList = iTeachingArrangeService.findCourseIdListByTeacherId(teacherId);
        List<TeacherAnalysisInfo> teacherAnalysisInfoList = new ArrayList<>(courseIdList.size());
        //该教师每个课程成绩
        courseIdList.forEach(courseId -> {
            Course course = iCourseService.getById(courseId);
            List<Long> teachingArrangeIdList = iTeachingArrangeService.findIdByTeacherIdAndCourseId(teacherId, courseId);
            List<StudentScore> studentScoreList = new ArrayList<>(iStudentScoreService.findListByTeachingArrangeIdList(teachingArrangeIdList));
            TeacherAnalysisInfo teacherAnalysisInfo = AnalysisUtils.buildTeacherAnalysisInfo(studentScoreList);
            teacherAnalysisInfo.setCourseId(courseId);
            teacherAnalysisInfo.setCourseNo(course.getCourseNo());
            teacherAnalysisInfo.setCourseName(course.getCourseName());
            teacherAnalysisInfo.setCourseType(course.getCourseType());
            teacherAnalysisInfoList.add(teacherAnalysisInfo);
        });
        map.put("teacherAnalysisInfoList", teacherAnalysisInfoList);
        map.put("barSource", AnalysisUtils.buildBarData2(teacherAnalysisInfoList));
        map.put("barSeries", AnalysisUtils.buildBarSeries2(teacherAnalysisInfoList));
        return map;
    }

    @Override
    public Long getMaxTeacherNo() {
        return teacherMapper.getMaxTeacherNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeachers(String[] ids) throws Exception {
        List<String> list = Arrays.asList(ids);
        Integer clazzCount = iClazzService.getClazzCountByManagerIdList(list.stream().map(Long::valueOf).collect(Collectors.toList()));
        if (clazzCount > 0) {
            throw new FebsException("该老师还是某个班级的班主任，不能删除");
        }
        List<Teacher> teacherList = teacherMapper.selectBatchIds(list);
        List<String> userIdList = teacherList.stream().map(teacher -> teacher.getUserId().toString()).collect(Collectors.toList());
        String[] userIdArray = userIdList.toArray(new String[userIdList.size()]);
        userService.deleteUsers(userIdArray);
        teacherMapper.deleteBatchIds(list);
    }

    @Override
    public void teacherFillCollege(List<Teacher> teacherList) {
        List<Long> collegeIds = teacherList.stream().map(Teacher::getCollegeId).distinct().collect(Collectors.toList());
        Map<Long, College> longCollegeMap = iCollegeService.listByIds(collegeIds).stream().collect(Collectors.toMap(College::getCollegeId, it -> it));
        teacherList.forEach(obj -> {
            obj.setCollege(longCollegeMap.get(obj.getCollegeId()));
            obj.setCollegeName(obj.getCollege().getCollegeName());
        });
    }

    @Override
    public void teacherWithUser(List<Teacher> teacherList) {
        List<Long> userIds = teacherList.stream().map(Teacher::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> longUserMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getUserId, it -> it));
        teacherList.forEach(obj -> {
            User user = longUserMap.get(obj.getUserId());
            if (user != null) {
                SchoolUtils.userToTeacher(user, obj);
            }
        });
    }

    @Override
    public Integer getTeacherCountByCollegeId(Long collegeId) {
        Wrapper<Teacher> wrapper = new QueryWrapper<Teacher>().lambda()
                .eq(Teacher::getCollegeId, collegeId);
        return this.count(wrapper);
    }

    @Override
    public Integer getTeacherCountByCollegeIdList(List<Long> collegeIdList) {
        Wrapper<Teacher> wrapper = new QueryWrapper<Teacher>().lambda()
                .in(Teacher::getCollegeId, collegeIdList);
        return this.count(wrapper);
    }

    @Override
    public Teacher findTeacherByUserId(Long userId) {
        Wrapper<Teacher> wrapper = new QueryWrapper<Teacher>().lambda().eq(Teacher::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public Map<String, Object> findTeacherDegreeCountMap(Long teacherId) {
        List<Long> teachingArrangeIdList = iTeachingArrangeService.findTeachingArrangeIdListByTeacherId(teacherId);
        if (CollectionUtils.isEmpty(teachingArrangeIdList)) return null;
        int firstCount = iStudentScoreService.findDegreeCount(teachingArrangeIdList, ScoreType.ONE.getCode());
        int secondCount = iStudentScoreService.findDegreeCount(teachingArrangeIdList, ScoreType.SECOND.getCode());
        int thirdCount = iStudentScoreService.findDegreeCount(teachingArrangeIdList, ScoreType.THIRD.getCode());
        int fourCount = iStudentScoreService.findDegreeCount(teachingArrangeIdList, ScoreType.FOUR.getCode());
        int fiveCount = iStudentScoreService.findDegreeCount(teachingArrangeIdList, ScoreType.FIVE.getCode());
        int totalExamCount = iStudentScoreService.findCount(teachingArrangeIdList);
        Map<String, Object> map = new HashMap<>();
        map.put("teacherFirstCount", firstCount);
        map.put("teacherSecondCount", secondCount);
        map.put("teacherThirdCount", thirdCount);
        map.put("teacherFourCount", fourCount);
        map.put("teacherFiveCount", fiveCount);
        map.put("teacherTotalExamCount", totalExamCount);
        return map;
    }
}
