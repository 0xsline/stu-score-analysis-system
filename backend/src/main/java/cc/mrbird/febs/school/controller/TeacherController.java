package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.*;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("people/teacher")
public class TeacherController extends BaseController {

    private String message;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private IMessageService iMessageService;

    /**
     * 返回全部的教师信息
     *
     * @return
     */
    @GetMapping("/all")
    @RequiresUser
    public List<Teacher> allTeacherList() {
        if (isStudent()) {
            Set<Long> finalTeacherIdList = new HashSet<>();
            Student student = getStudent();
            Clazz clazz = iClazzService.getById(student.getClazzId());
            finalTeacherIdList.add(clazz.getManagerId());
            List<Long> teacherIdList = iTeachingArrangeService.findTeachIdListByClazzId(clazz.getClazzId());
            if (CollectionUtils.isNotEmpty(teacherIdList)) {
                finalTeacherIdList.addAll(teacherIdList);
            }
            return new ArrayList<>(iTeacherService.listByIds(teacherIdList));

        }
        return iTeacherService.list();
    }

    /**
     * 教师列表
     *
     * @param request
     * @param teacher
     * @return
     */
    @GetMapping
    @RequiresPermissions("people:teacher:view")
    public Map<String, Object> teacherList(QueryRequest request, Teacher teacher) {
        if (isTeacher()) {
            Teacher currentTeacher = getTeacher();
            teacher.setTeacherId(currentTeacher.getTeacherId());
        }

        return getDataTable(this.iTeacherService.findTeachers(request, teacher));
    }


    /**
     * 新增教师
     *
     * @param teacher
     * @throws FebsException
     */
    @Log("新增教师")
    @PostMapping
    @RequiresPermissions("people:teacher:add")
    public void addTeacher(@RequestBody @Valid Teacher teacher) throws FebsException {
        try {
            User user = getCurrentUser();
            teacher.setDeptId(user.getDeptId());
            this.iTeacherService.insert(teacher);
        } catch (Exception e) {
            message = "新增教师失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改教师
     *
     * @param teacher
     * @throws FebsException
     */
    @Log("修改教师")
    @PutMapping
    @RequiresPermissions("people:teacher:update")
    public void updateTeacher(@RequestBody @Valid Teacher teacher) throws FebsException {
        try {
            User user = getCurrentUser();
            teacher.setDeptId(user.getDeptId());
            this.iTeacherService.modify(teacher);
        } catch (Exception e) {
            message = "修改教师失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除教师
     *
     * @param teacherIds
     * @throws FebsException
     */
    @Log("删除教师")
    @DeleteMapping("/{teacherIds}")
    @RequiresPermissions("people:teacher:delete")
    public void deleteTeacher(@NotBlank(message = "{required}") @PathVariable String teacherIds) throws FebsException {
        try {
            String[] ids = teacherIds.split(StringPool.COMMA);
            List<Long> teacherIdList = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
            Integer examPlanCount = iExamPlanService.findCountByTeacherIdList(teacherIdList);
            if (examPlanCount > 0) {
                throw new FebsException("考试计划关联着老师，不能删除");
            }

            Integer teachingArrangeCount = iTeachingArrangeService.findCountByTeacherIdList(teacherIdList);
            if (teachingArrangeCount > 0) {
                throw new FebsException("教学安排关联着老师，不能删除");
            }

            int messageCount = iMessageService.findCountByTeacherIdList(teacherIdList);
            if (messageCount > 0) {
                throw new FebsException("留言关联着老师，不能删除");
            }

            this.iTeacherService.deleteTeachers(ids);
        } catch (Exception e) {
            message = "删除教师失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param teacher
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("people:teacher:export")
    public void export(QueryRequest request, Teacher teacher, HttpServletResponse response) throws FebsException {
        try {
            List<Teacher> teachers = this.iTeacherService.findTeachers(request, teacher).getRecords();
            ExcelKit.$Export(Teacher.class, response).downXlsx(teachers, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 根据教师分析课程教学情况
     *
     * @param teacherId
     */

    @GetMapping("/analysis/{teacherId}")
    @RequiresPermissions("people:teacher:analysis")
    public Map<String, Object> analysis(@PathVariable Long teacherId) {
        return iTeacherService.findTeacherAnalysisDetail(teacherId);
    }

    /**
     * 根据登录用户是教师查询考试情况
     */
    @GetMapping("/examInfo")
    public Map<String, Object> examInfo(@RequestParam(required = false) Long teacherId) {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            return iTeacherService.findTeacherDegreeCountMap(teacher.getTeacherId());
        }
        return iTeacherService.findTeacherDegreeCountMap(teacherId);
    }
}
