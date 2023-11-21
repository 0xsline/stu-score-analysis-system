package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Exam;
import cc.mrbird.febs.school.service.IExamPlanService;
import cc.mrbird.febs.school.service.IExamService;
import cc.mrbird.febs.school.service.IStudentScoreService;
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
@RequestMapping("education/exam")
public class ExamController extends BaseController {

    private String message;

    @Autowired
    private IExamService iExamService;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private IStudentScoreService iStudentScoreService;


    /**
     * 返回全部的考试信息
     *
     * @return
     */
    @GetMapping("/all")
    @RequiresUser
    public List<Exam> allExamList() {
        return iExamService.list();
    }


    /**
     * 考试列表
     *
     * @param request
     * @param exam
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:exam:view")
    public Map<String, Object> examList(QueryRequest request, Exam exam) {
        return getDataTable(this.iExamService.findExams(request, exam));
    }


    /**
     * 新增考试
     *
     * @param exam
     * @throws FebsException
     */
    @Log("新增考试")
    @PostMapping
    @RequiresPermissions("education:exam:add")
    public void addExam(@RequestBody @Valid Exam exam) throws FebsException {
        try {
            int count = iExamService.findCountExamByDefault();
            if (count > 0 && exam.getExamType().equals("1")) {
                throw new FebsException("默认的考试只能设置一个");
            }
            this.iExamService.insert(exam);
        } catch (Exception e) {
            message = "新增考试失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改考试
     *
     * @param exam
     * @throws FebsException
     */
    @Log("修改考试")
    @PutMapping
    @RequiresPermissions("education:exam:update")
    public void updateExam(@RequestBody @Valid Exam exam) throws FebsException {
        try {
            int count = iExamService.findCountExamByDefaultUnless(exam.getExamId());
            if (count > 0 && exam.getExamType().equals("1")) {
                throw new FebsException("默认的考试只能设置一个");
            }

            this.iExamService.modify(exam);
        } catch (Exception e) {
            message = "修改考试失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除考试
     *
     * @param examIds
     * @throws FebsException
     */
    @Log("删除考试")
    @DeleteMapping("/{examIds}")
    @RequiresPermissions("education:exam:delete")
    public void deleteExam(@NotBlank(message = "{required}") @PathVariable String examIds) throws FebsException {
        try {
            String[] ids = examIds.split(StringPool.COMMA);
            List<Long> examIdList = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
            Integer studentScoreCount = iStudentScoreService.findCountByExamIdList(examIdList);
            if (studentScoreCount > 0) {
                throw new FebsException("教学安排成绩关联考试，不能删除");
            }


            Integer examPlanCount = iExamPlanService.findCountByExamIdList(examIdList);
            if (examPlanCount > 0) {
                throw new FebsException("考试计划关联考试，不能删除");
            }
            this.iExamService.deleteExams(ids);
        } catch (Exception e) {
            message = "删除考试失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param exam
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("education:exam:export")
    public void export(QueryRequest request, Exam exam, HttpServletResponse response) throws FebsException {
        try {
            List<Exam> exams = this.iExamService.findExams(request, exam).getRecords();
            ExcelKit.$Export(Exam.class, response).downXlsx(exams, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
