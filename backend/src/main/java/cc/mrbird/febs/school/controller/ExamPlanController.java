package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.ExamPlan;
import cc.mrbird.febs.school.entity.ExamScore;
import cc.mrbird.febs.school.service.IExamPlanService;
import cc.mrbird.febs.school.service.IExamScoreService;
import cn.hutool.json.JSONUtil;
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
@RequestMapping("education/examPlan")
public class ExamPlanController extends BaseController {

    private String message;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private IExamScoreService iExamScoreService;


    /**
     * 考试计划列表
     *
     * @param request
     * @param examPlan
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:examPlan:view")
    public Map<String, Object> examPlanList(QueryRequest request, ExamPlan examPlan) {
        return getDataTable(this.iExamPlanService.findExamPlans(request, examPlan));
    }


    /**
     * 新增考试计划
     *
     * @param examPlan
     * @throws FebsException
     */
    @Log("新增考试计划")
    @PostMapping
    @RequiresPermissions("education:examPlan:add")
    public void addExamPlan(@RequestBody @Valid ExamPlan examPlan) throws Exception {
        try {
            this.iExamPlanService.insert(examPlan);
        } catch (Exception e) {
            if (e instanceof FebsException) {
                throw e;
            }
            message = "新增考试计划失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改考试计划
     *
     * @param examPlan
     * @throws FebsException
     */
    @Log("修改考试计划")
    @PutMapping
    @RequiresPermissions("education:examPlan:update")
    public void updateExamPlan(@RequestBody @Valid ExamPlan examPlan) throws Exception {
        try {
            this.iExamPlanService.modify(examPlan);
        } catch (Exception e) {
            if (e instanceof FebsException) {
                throw e;
            }
            message = "修改考试计划失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除考试计划
     *
     * @param examPlanIds
     * @throws FebsException
     */
    @Log("删除考试计划")
    @DeleteMapping("/{examPlanIds}")
    @RequiresPermissions("education:examPlan:delete")
    public void deleteExamPlan(@NotBlank(message = "{required}") @PathVariable String examPlanIds) throws FebsException {
        try {
            String[] ids = examPlanIds.split(StringPool.COMMA);
            this.iExamPlanService.deleteExamPlans(ids);
        } catch (Exception e) {
            message = "删除考试计划失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 根据考试计划加载学生考试成绩
     *
     * @param examPlanId
     */

    @GetMapping("/loadStudentScoreLists/{examPlanId}")
    @RequiresPermissions("education:examPlan:loadStudentScoreLists")
    public List<ExamScore> loadStudentScoreLists(@PathVariable Long examPlanId) {
        return iExamScoreService.findExamScoreByExamPlanId(examPlanId);
    }

    /**
     * 根据考试计划加载学生考试成绩
     *
     * @param examPlanId
     */

    @GetMapping("/loadStudentScoreMaps/{examPlanId}")
    @RequiresPermissions("education:examPlan:loadStudentScoreMaps")
    public Map<String, Object> loadStudentScoreMaps(@PathVariable Long examPlanId) {
        return iExamScoreService.findExamScoreMapByExamPlanId(examPlanId);
    }

    /**
     * 保存考试成绩
     *
     * @param examScoreMap
     * @throws FebsException
     */
    @Log("保存考试成绩")
    @PutMapping("/loadStudentScoreLists")
    @RequiresPermissions("education:examPlan:saveStudentScoreLists")
    public void saveStudentScoreLists(@RequestBody Map<String, String> examScoreMap) throws FebsException {
        System.out.println(JSONUtil.toJsonPrettyStr(examScoreMap));
        iExamScoreService.saveExamScoreMap(examScoreMap);
    }


    /**
     * 导出
     *
     * @param request
     * @param examPlan
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("education:examPlan:export")
    public void export(QueryRequest request, ExamPlan examPlan, HttpServletResponse response) throws FebsException {
        try {
            List<ExamPlan> examPlans = this.iExamPlanService.findExamPlans(request, examPlan).getRecords();
            ExcelKit.$Export(ExamPlan.class, response).downXlsx(examPlans, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
