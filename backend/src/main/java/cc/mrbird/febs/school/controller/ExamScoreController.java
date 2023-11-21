package cc.mrbird.febs.school.controller;

import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.ExamPlan;
import cc.mrbird.febs.school.service.IExamScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("education/examScore")
public class ExamScoreController extends BaseController {

    @Autowired
    private IExamScoreService iExamScoreService;

    /**
     * 考试成绩列表
     *
     * @param request
     * @param examPlan
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:examScore:view")
    public Map<String, Object> examPlanList(QueryRequest request, ExamPlan examPlan) throws FebsException {
        return iExamScoreService.findTotalExamScoreMapByCondition(examPlan.getExamId(),
                examPlan.getSemesterId(),
                examPlan.getClazzId());
    }
}
