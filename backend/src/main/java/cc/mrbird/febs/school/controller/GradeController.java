package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.entity.Grade;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.service.IClazzService;
import cc.mrbird.febs.school.service.IGradeService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@RequestMapping("organization/grade")
public class GradeController extends BaseController {

    private String message;

    @Autowired
    private IGradeService iGradeService;

    @Autowired
    private IClazzService iClazzService;


    /**
     * 年级列表
     *
     * @param request
     * @param grade
     * @return
     */
    @GetMapping
    @RequiresPermissions("organization:grade:view")
    public Map<String, Object> gradeList(QueryRequest request, Grade grade) {
        if (isStudent()) {
            Student student = getStudent();
            Clazz clazz = iClazzService.getById(student.getClazzId());
            grade.setGradeId(clazz.getGradeId());
        }
        return getDataTable(this.iGradeService.findGrades(request, grade));
    }


    /**
     * 新增年级
     *
     * @param grade
     * @throws FebsException
     */
    @Log("新增年级")
    @PostMapping
    @RequiresPermissions("organization:grade:add")
    public void addGrade(@RequestBody @Valid Grade grade) throws FebsException {
        try {
            this.iGradeService.insert(grade);
        } catch (Exception e) {
            message = "新增年级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改年级
     *
     * @param grade
     * @throws FebsException
     */
    @Log("修改年级")
    @PutMapping
    @RequiresPermissions("organization:grade:update")
    public void updateGrade(@RequestBody @Valid Grade grade) throws FebsException {
        try {
            this.iGradeService.modify(grade);
        } catch (Exception e) {
            message = "修改年级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除年级
     *
     * @param gradeIds
     * @throws FebsException
     */
    @Log("删除年级")
    @DeleteMapping("/{gradeIds}")
    @RequiresPermissions("organization:grade:delete")
    public void deleteGrade(@NotBlank(message = "{required}") @PathVariable String gradeIds) throws FebsException {
        try {
            String[] ids = gradeIds.split(StringPool.COMMA);
            this.iGradeService.deleteGrades(ids);
        } catch (Exception e) {
            message = "删除年级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param grade
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("organization:grade:export")
    public void export(QueryRequest request, Grade grade, HttpServletResponse response) throws FebsException {
        try {
            List<Grade> grades = this.iGradeService.findGrades(request, grade).getRecords();
            ExcelKit.$Export(Grade.class, response).downXlsx(grades, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
