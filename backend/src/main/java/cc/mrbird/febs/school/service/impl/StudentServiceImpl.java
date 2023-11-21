package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.CourseScoreInfo;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.common.enums.UserType;
import cc.mrbird.febs.common.utils.SchoolConstants;
import cc.mrbird.febs.common.utils.SchoolUtils;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.StudentMapper;
import cc.mrbird.febs.school.entity.*;
import cc.mrbird.febs.school.service.*;
import cc.mrbird.febs.system.domain.Role;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.service.RoleService;
import cc.mrbird.febs.system.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
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
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Override
    @CustomerInsert
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(Student entity) throws Exception {
        Role role = roleService.findByStudent();
        User user = SchoolUtils.studentToUser(entity, null);
        user.setRoleId(role.getRoleId().toString());
        user.setUserType(UserType.STUDENT.getCode());
        Long userId = this.userService.createUser(user);
        entity.setUserId(userId);
        entity.setStudentNo(studentMapper.getMaxStudentNo());
        return studentMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(Student entity) throws Exception {
        Role role = roleService.findByStudent();
        User user = SchoolUtils.studentToUser(entity, null);
        user.setUserId(entity.getUserId());
        user.setRoleId(role.getRoleId().toString());
        user.setUserType(UserType.STUDENT.getCode());
        this.userService.updateUser(user);
        return studentMapper.updateById(entity) > 0;
    }

    @Override
    public IPage<Student> findStudents(QueryRequest request, Student student) {
        try {
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(student.getStudentName())) {
                queryWrapper.like(Student::getStudentName, student.getStudentName());
            }
            if (student.getStudentNo() != null) {
                queryWrapper.eq(Student::getStudentNo, student.getStudentNo());
            }

            if (student.getClazzId() != null) {
                queryWrapper.eq(Student::getClazzId, student.getClazzId());
            }

            if (!CollectionUtils.isEmpty(student.getClazzIdList())) {
                queryWrapper.in(Student::getClazzId, student.getClazzIdList());
            }

            if (student.getStudentId() != null) {
                queryWrapper.eq(Student::getStudentId, student.getStudentId());
            }

            if (StringUtils.isNotBlank(student.getCreateTimeFrom()) && StringUtils.isNotBlank(student.getCreateTimeTo())) {
                queryWrapper
                        .ge(Student::getCreateTime, student.getCreateTimeFrom())
                        .le(Student::getCreateTime, student.getCreateTimeTo());
            }
            Page<Student> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<Student> studentList = iPage.getRecords();
            if (CollectionUtils.isEmpty(studentList)) {
                return iPage;
            }
            //学生填充学院
            studentFillClazz(studentList);

            //学生填充用户基本信息
            studentWithUser(studentList);

            //考试次数
            studentList.forEach(student1 -> {
                Integer count = iStudentScoreService.findCountByStudentId(student1.getStudentId());
                if (count != null) {
                    student1.setExamCount(count);
                }
                if (student1.getBirthDate() != null) {
                    student1.setAge(DateUtil.ageOfNow(student1.getBirthDate()) + "");
                }
            });

            return iPage;
        } catch (Exception e) {
            log.error("获取学生失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxStudentNo() {
        return studentMapper.getMaxStudentNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudents(String[] ids) throws Exception {
        List<String> list = Arrays.asList(ids);
        List<Student> studentList = studentMapper.selectBatchIds(list);
        List<String> userIdList = studentList.stream().map(student -> student.getUserId().toString()).collect(Collectors.toList());
        String[] userIdArray = userIdList.toArray(new String[userIdList.size()]);
        userService.deleteUsers(userIdArray);
        studentMapper.deleteBatchIds(list);
    }

    @Override
    public void studentFillClazz(List<Student> studentList) {
        List<Long> clazzIds = studentList.stream().map(Student::getClazzId).distinct().collect(Collectors.toList());
        List<Clazz> clazzList = iClazzService.listByIds(clazzIds).stream().collect(Collectors.toList());
        Map<Long, Clazz> longClazzMap = clazzList.stream().collect(Collectors.toMap(Clazz::getClazzId, it -> it));
        studentList.forEach(obj -> {
            obj.setClazz(longClazzMap.get(obj.getClazzId()));
        });
        iClazzService.clazzFillGradeAndCollege(clazzList);
    }

    @Override
    public List<SchoolTree> buildOrganizationTreeList(List<Long> clazzIdList) {
        List<SchoolTree> schoolTreeList = iClazzService.buildOrganizationTreeList(false);
        List<Clazz> clazzList = iClazzService.list();
        if (!CollectionUtils.isEmpty(clazzIdList)) {
            clazzList = clazzList.stream().filter(clazz -> clazzIdList.contains(clazz.getClazzId()))
                    .collect(Collectors.toList());
        }
        Iterator<SchoolTree> schoolTreeIterator = schoolTreeList.iterator();
        while (schoolTreeIterator.hasNext()) {
            SchoolTree schoolTree = schoolTreeIterator.next();
            if (CollectionUtils.isEmpty(schoolTree.getChildren())) {
                schoolTreeIterator.remove();
            } else {
                List<SchoolTree> children = schoolTree.getChildren();
                Iterator<SchoolTree> childrenIterator = children.iterator();
                while (childrenIterator.hasNext()) {
                    SchoolTree child = childrenIterator.next();
                    child.setSelectable(false);
                    for (int k = 0; k < clazzList.size(); k++) {
                        Clazz clazz = clazzList.get(k);
                        if ((SchoolConstants.GRADE + clazz.getGradeId().toString()).equals(child.getKey())) {
                            if (child.getChildren() == null) {
                                child.setChildren(new ArrayList<>());
                            }

                            SchoolTree clazzSchoolTree = new SchoolTree();
                            clazzSchoolTree.setKey(clazz.getClazzId().toString());
                            clazzSchoolTree.setValue(clazz.getClazzId().toString());
                            clazzSchoolTree.setTitle(clazz.getClazzName());
                            clazzSchoolTree.setSelectable(true);
                            child.getChildren().add(clazzSchoolTree);
                        }
                    }
                    if (CollectionUtils.isEmpty(child.getChildren())) {
                        childrenIterator.remove();
                    }
                }
            }
        }
        return schoolTreeList;
    }

    @Override
    public List<Student> findByClazzId(Long clazzId) {
        return studentMapper.selectList(new LambdaQueryWrapper<Student>().eq(Student::getClazzId, clazzId));
    }

    @Override
    public List<Long> findStudentIdListByClazzId(Long clazzId) {
        List<Student> studentList = this.findByClazzId(clazzId);
        return studentList.stream().map(Student::getStudentId).collect(Collectors.toList());
    }

    @Override
    public void studentWithUser(List<Student> studentList) {
        List<Long> userIds = studentList.stream().map(Student::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> longUserMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getUserId, it -> it));
        studentList.forEach(obj -> {
            User user = longUserMap.get(obj.getUserId());
            if (user != null) {
                SchoolUtils.userToStudent(user, obj);
            }
        });
    }

    @Override
    public Integer studentCountByClazzId(Long clazzId) {
        Wrapper<Student> wrapper = new QueryWrapper<Student>().lambda()
                .eq(Student::getClazzId, clazzId);
        return this.count(wrapper);
    }

    @Override
    public Integer studentCountByClazzIdList(List<Long> clazzIdList) {
        Wrapper<Student> wrapper = new QueryWrapper<Student>().lambda()
                .in(Student::getClazzId, clazzIdList);
        return this.count(wrapper);
    }

    @Override
    public Map<Long, Long> clazzStudentCount(List<Long> clazzIdList) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.select("CLAZZ_ID as clazzId,count(STUDENT_ID) as studentCount");
        wrapper.in("CLAZZ_ID", clazzIdList);
        wrapper.groupBy("CLAZZ_ID");
        List<Map<String, Object>> list = this.listMaps(wrapper);
        Map<Long, Long> result = new HashMap<>();
        list.forEach(map -> {
            Long clazzId = (Long) map.get("clazzId");
            Long count = (Long) map.get("studentCount");
            result.put(clazzId, count);
        });
        return result;
    }

    @Override
    public Map<String, Object> findScoreByCondition(Long studentId, Long semesterId, Long examId) {
        Student student = this.getById(studentId);
        List<TeachingArrange> teachingArrangeList = this.iTeachingArrangeService.findTeachingArrangeByCondition(student
                .getClazzId(), semesterId);
        this.iTeachingArrangeService.teachingArrangeFillCourse(teachingArrangeList);
        List<Long> teachingArrangeIdList = teachingArrangeList.stream().map(TeachingArrange::getTeachingArrangeId)
                .collect(Collectors.toList());

        Map<Long, TeachingArrange> longTeachingArrangeMap = teachingArrangeList.stream().collect(Collectors.toMap(TeachingArrange::getTeachingArrangeId,
                it -> it));

        List<StudentScore> studentScoreList = iStudentScoreService.findListByCondition(teachingArrangeIdList, examId, studentId);

        List<CourseScoreInfo> courseScoreInfoList = studentScoreList.stream().map(studentScore -> {
            CourseScoreInfo courseScoreInfo = new CourseScoreInfo();
            TeachingArrange teachingArrange = longTeachingArrangeMap.get(studentScore.getTeachingArrangeId());
            Course course = teachingArrange.getCourse();
            courseScoreInfo.setCourseId(teachingArrange.getCourseId());
            courseScoreInfo.setCourseNo(course.getCourseNo());
            courseScoreInfo.setCourseName(course.getCourseName());
            courseScoreInfo.setCourseType(course.getCourseType());
            courseScoreInfo.setScore(studentScore.getScore());
            courseScoreInfo.setStudyScore(studentScore.getStudyScore());
            courseScoreInfo.setPointScore(studentScore.getPointScore());
            courseScoreInfo.setDegreeScore(studentScore.getDegreeScore());
            return courseScoreInfo;
        }).collect(Collectors.toList());
        Double totalStudyScore = courseScoreInfoList.stream().mapToDouble(CourseScoreInfo::getStudyScore).sum();
        Double totalPointScore = courseScoreInfoList.stream().mapToDouble(CourseScoreInfo::getPointScore).sum();
        Map<String, Object> map = new HashMap<>();
        map.put("courseScoreInfoList", courseScoreInfoList);
        map.put("totalStudyScore", NumberUtil.round(totalStudyScore, 2));
        map.put("totalPointScore", NumberUtil.round(totalPointScore, 2));
        return map;
    }

    @Override
    public Student findStudentByUserId(Long userId) {
        Wrapper<Student> wrapper = new QueryWrapper<Student>().lambda()
                .eq(Student::getUserId, userId);
        return this.getOne(wrapper);
    }

    @Override
    public Map<String, Object> findStudentDegreeCountMap(Long studentId) {
        int firstCount = iStudentScoreService.findStudentDegreeCount(studentId, ScoreType.ONE.getCode());
        int secondCount = iStudentScoreService.findStudentDegreeCount(studentId, ScoreType.SECOND.getCode());
        int thirdCount = iStudentScoreService.findStudentDegreeCount(studentId, ScoreType.THIRD.getCode());
        int fourCount = iStudentScoreService.findStudentDegreeCount(studentId, ScoreType.FOUR.getCode());
        int fiveCount = iStudentScoreService.findStudentDegreeCount(studentId, ScoreType.FIVE.getCode());
        int totalExamCount = iStudentScoreService.findCountByStudentId(studentId);
        if (totalExamCount == 0) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("studentFirstCount", firstCount);
        map.put("studentSecondCount", secondCount);
        map.put("studentThirdCount", thirdCount);
        map.put("studentFourCount", fourCount);
        map.put("studentFiveCount", fiveCount);
        map.put("studentTotalExamCount", totalExamCount);
        return map;
    }
}
