package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.entity.College;
import cc.mrbird.febs.school.entity.Grade;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.service.*;
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
import java.util.List;
import java.util.Map;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("organization/college")
public class CollegeController extends BaseController {

    private String message;

    @Autowired
    private ICollegeService iCollegeService;

    @Autowired
    private IGradeService iGradeService;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private IStudentService iStudentService;


    /**
     * 返回全部的学院信息
     *
     * @return
     */
    @GetMapping("/all")
    @RequiresUser
    public List<College> allCollegeList() {
        return iCollegeService.list();
    }


    /**
     * 学院列表
     *
     * @param request
     * @param college
     * @return
     */
    @GetMapping
    @RequiresPermissions("organization:college:view")
    public Map<String, Object> collegeList(QueryRequest request, College college) {
        if (isStudent()) {
            Student student = getStudent();
            Clazz clazz = iClazzService.getById(student.getClazzId());
            Grade grade = iGradeService.getById(clazz.getGradeId());
            College currentCollege = iCollegeService.getById(grade.getCollegeId());
            college.setCollegeId(currentCollege.getCollegeId());
        }

        IPage<College> collegeIPage = this.iCollegeService.findColleges(request, college);
        List<College> collegeList = collegeIPage.getRecords();
        if (CollectionUtils.isNotEmpty(collegeList)) {
            collegeList.forEach(obj -> {
                //学院下年级（专业）数量
                Integer gradeCount = iGradeService.getGradeCountByCollegeId(obj.getCollegeId());
                if (gradeCount != null) {
                    obj.setGradeCount(gradeCount);
                }
                //学院下教师数量
                Integer teacherCount = iTeacherService.getTeacherCountByCollegeId(obj.getCollegeId());
                if (teacherCount != null) {
                    obj.setTeacherCount(teacherCount);
                }

                //学院下的学生数量
                List<Long> gradeIdList = iGradeService.getGradeIdListByCollegeId(obj.getCollegeId());
                if (CollectionUtils.isNotEmpty(gradeIdList)) {
                    List<Long> clazzIdList = iClazzService.getClazzIdListByGradeIdList(gradeIdList);
                    if (CollectionUtils.isNotEmpty(clazzIdList)) {
                        Integer studentCount = iStudentService.studentCountByClazzIdList(clazzIdList);
                        if (studentCount != null) {
                            obj.setStudentCount(studentCount);
                        }
                    }
                }
            });
        }
        return getDataTable(collegeIPage);
    }


    /**
     * 新增学院
     *
     * @param college
     * @throws FebsException
     */
    @Log("新增学院")
    @PostMapping
    @RequiresPermissions("organization:college:add")
    public void addCollege(@RequestBody @Valid College college) throws FebsException {
        try {
            this.iCollegeService.insert(college);
        } catch (Exception e) {
            message = "新增学院失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改学院
     *
     * @param college
     * @throws FebsException
     */
    @Log("修改学院")
    @PutMapping
    @RequiresPermissions("organization:college:update")
    public void updateCollege(@RequestBody @Valid College college) throws FebsException {
        try {
            this.iCollegeService.modify(college);
        } catch (Exception e) {
            message = "修改学院失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除学院
     *
     * @param collegeIds
     * @throws FebsException
     */
    @Log("删除学院")
    @DeleteMapping("/{collegeIds}")
    @RequiresPermissions("organization:college:delete")
    public void deleteCollege(@NotBlank(message = "{required}") @PathVariable String collegeIds) throws FebsException {
        try {
            String[] ids = collegeIds.split(StringPool.COMMA);
            this.iCollegeService.deleteColleges(ids);
        } catch (Exception e) {
            message = "删除学院失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param college
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("organization:college:export")
    public void export(QueryRequest request, College college, HttpServletResponse response) throws FebsException {
        try {
            List<College> colleges = this.iCollegeService.findColleges(request, college).getRecords();
            ExcelKit.$Export(College.class, response).downXlsx(colleges, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
