package cc.mrbird.febs.school.controller;

import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.entity.TeachingArrange;
import cc.mrbird.febs.school.service.IClazzService;
import cc.mrbird.febs.school.service.IStudentScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("education/studentScore")
public class StudentScoreController extends BaseController {

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private IClazzService iClazzService;

    /**
     * 考试成绩列表
     *
     * @param request
     * @param teachingArrange
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:studentScore:view")
    public Map<String, Object> examPlanList(QueryRequest request, TeachingArrange teachingArrange) throws FebsException {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            List<Long> clazzIdList = iClazzService.getClazzIdListByManagerId(teacher.getTeacherId());
            if (clazzIdList == null || !clazzIdList.contains(teachingArrange.getClazzId())) {
                return new HashMap<>();
            }
        }
        return iStudentScoreService.findTotalStudentScoreMapByCondition(teachingArrange.getExamId(),
                teachingArrange.getSemesterId(),
                teachingArrange.getClazzId(), false);
    }
}
