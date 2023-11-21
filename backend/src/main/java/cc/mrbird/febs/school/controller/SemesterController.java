package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Semester;
import cc.mrbird.febs.school.service.IExamPlanService;
import cc.mrbird.febs.school.service.ISemesterService;
import cc.mrbird.febs.school.service.ITeachingArrangeService;
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
@RequestMapping("education/semester")
public class SemesterController extends BaseController {

    private String message;

    @Autowired
    private ISemesterService iSemesterService;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;


    /**
     * 返回全部的学期信息
     *
     * @return
     */
    @GetMapping("/all")
    @RequiresUser
    public List<Semester> allSemesterList() {
        return iSemesterService.list();
    }


    /**
     * 学期列表
     *
     * @param request
     * @param semester
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:semester:view")
    public Map<String, Object> semesterList(QueryRequest request, Semester semester) {
        return getDataTable(this.iSemesterService.findSemesters(request, semester));
    }


    /**
     * 新增学期
     *
     * @param semester
     * @throws FebsException
     */
    @Log("新增学期")
    @PostMapping
    @RequiresPermissions("education:semester:add")
    public void addSemester(@RequestBody @Valid Semester semester) throws FebsException {
        try {
            this.iSemesterService.insert(semester);
        } catch (Exception e) {
            message = "新增学期失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改学期
     *
     * @param semester
     * @throws FebsException
     */
    @Log("修改学期")
    @PutMapping
    @RequiresPermissions("education:semester:update")
    public void updateSemester(@RequestBody @Valid Semester semester) throws FebsException {
        try {
            this.iSemesterService.modify(semester);
        } catch (Exception e) {
            message = "修改学期失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除学期
     *
     * @param semesterIds
     * @throws FebsException
     */
    @Log("删除学期")
    @DeleteMapping("/{semesterIds}")
    @RequiresPermissions("education:semester:delete")
    public void deleteSemester(@NotBlank(message = "{required}") @PathVariable String semesterIds) throws FebsException {
        try {
            String[] ids = semesterIds.split(StringPool.COMMA);
            List<Long> semesterIdList = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
            Integer teachingArrangeCount = iTeachingArrangeService.findCountBySemesterIdList(semesterIdList);
            if (teachingArrangeCount > 0) {
                throw new FebsException("教学安排关联这学期，不能删除学期");
            }

            Integer examPlanCount = iExamPlanService.findCountBySemesterIdList(semesterIdList);
            if (examPlanCount > 0) {
                throw new FebsException("考试计划关联这学期，不能删除学期");
            }
            this.iSemesterService.deleteSemesters(ids);
        } catch (Exception e) {
            message = "删除学期失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param semester
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("education:semester:export")
    public void export(QueryRequest request, Semester semester, HttpServletResponse response) throws FebsException {
        try {
            List<Semester> semesters = this.iSemesterService.findSemesters(request, semester).getRecords();
            ExcelKit.$Export(Semester.class, response).downXlsx(semesters, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
