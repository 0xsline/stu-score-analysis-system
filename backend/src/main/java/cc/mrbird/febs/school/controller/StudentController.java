package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.*;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("people/student")
public class StudentController extends BaseController {

    private String message;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private IExamScoreService iExamScoreService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private IMessageService iMessageService;


    /**
     * 学生列表
     *
     * @param request
     * @param student
     * @return
     */
    @GetMapping
    @RequiresPermissions("people:student:view")
    public Map<String, Object> studentList(QueryRequest request, Student student) {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            List<Long> clazzIdList = iClazzService.getClazzIdListByManagerId(teacher.getTeacherId());
            if (CollectionUtils.isEmpty(clazzIdList)) {
                student.setClazzIdList(Collections.singletonList(-1L));
            } else {
                student.setClazzIdList(clazzIdList);
            }
        }

        if (isStudent()) {
            Student currentStu = getStudent();
            student.setStudentId(currentStu.getStudentId());
        }
        return getDataTable(this.iStudentService.findStudents(request, student));
    }


    /**
     * 新增学生
     *
     * @param student
     * @throws FebsException
     */
    @Log("新增学生")
    @PostMapping
    @RequiresPermissions("people:student:add")
    public void addStudent(@RequestBody @Valid Student student) throws FebsException {
        try {
            User user = getCurrentUser();
            student.setDeptId(user.getDeptId());
            this.iStudentService.insert(student);
        } catch (Exception e) {
            message = "新增学生失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改学生
     *
     * @param student
     * @throws FebsException
     */
    @Log("修改学生")
    @PutMapping
    @RequiresPermissions("people:student:update")
    public void updateStudent(@RequestBody @Valid Student student) throws FebsException {
        try {
            User user = getCurrentUser();
            student.setDeptId(user.getDeptId());
            this.iStudentService.modify(student);
        } catch (Exception e) {
            message = "修改学生失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除学生
     *
     * @param studentIds
     * @throws FebsException
     */
    @Log("删除学生")
    @DeleteMapping("/{studentIds}")
    @RequiresPermissions("people:student:delete")
    public void deleteStudent(@NotBlank(message = "{required}") @PathVariable String studentIds) throws FebsException {
        try {
            String[] ids = studentIds.split(StringPool.COMMA);
            List<Long> studentIdList = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
            Integer examScoreCount = iExamScoreService.findCountByStudentIdList(studentIdList);
            if (examScoreCount > 0) {
                throw new FebsException("考试计划考试成绩关联着学生，不能删除");
            }

            Integer studentScoreCount = iStudentScoreService.findCountByStudentIdList(studentIdList);
            if (studentScoreCount > 0) {
                throw new FebsException("教学安排考试成绩关联着学生，不能删除");
            }

            int messageCount = iMessageService.findCountByStudentIdList(studentIdList);
            if (messageCount > 0) {
                throw new FebsException("留言关联着学生，不能删除");
            }

            this.iStudentService.deleteStudents(ids);
        } catch (Exception e) {
            message = "删除学生失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param student
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("people:student:export")
    public void export(QueryRequest request, Student student, HttpServletResponse response) throws FebsException {
        try {
            List<Student> students = this.iStudentService.findStudents(request, student).getRecords();
            ExcelKit.$Export(Student.class, response).downXlsx(students, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 构造班级和年级和学院树
     *
     * @return
     */
    @GetMapping("/tree")
    @RequiresUser
    public List<SchoolTree> organizationTreeList() {
        List<Long> clazzIdList = null;
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            clazzIdList = iClazzService.getClazzIdListByManagerId(teacher.getTeacherId());

        }
        return iStudentService.buildOrganizationTreeList(clazzIdList);
    }


    /**
     * 根据学生查询考试情况
     *
     * @param studentId
     * @param semesterId
     * @param examId
     */

    @GetMapping("/analysis")
    @RequiresPermissions("people:student:analysis")
    public Map<String, Object> analysis(@RequestParam Long studentId, @RequestParam Long semesterId,
                                        @RequestParam Long examId) {
        return iStudentService.findScoreByCondition(studentId, semesterId, examId);
    }

    /**
     * 根据学生加载学生全年考试成绩
     *
     * @param studentId
     */

    @GetMapping("/loadStudentScoreMaps/{studentId}")
    @RequiresPermissions("people:student:loadStudentScoreMaps2")
    public Map<String, Object> loadStudentScoreMaps(@PathVariable Long studentId) {
        return iStudentScoreService.findStudentScoreMapByStudentId(studentId);
    }

    /**
     * 根据登录用户是学生查询考试情况
     *

     */
    @GetMapping("/examInfo")
    public Map<String, Object> examInfo(@RequestParam(required = false)Long studentId) {
        if (isStudent()) {
            Student student = getStudent();
            return iStudentService.findStudentDegreeCountMap(student.getStudentId());
        }
        return iStudentService.findStudentDegreeCountMap(studentId);
    }
}
