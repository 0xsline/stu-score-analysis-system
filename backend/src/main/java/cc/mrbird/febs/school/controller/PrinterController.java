package cc.mrbird.febs.school.controller;

import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SuperExcelKit;
import cc.mrbird.febs.common.utils.SuperExcelMappingFactory;
import cc.mrbird.febs.common.views.ErrorView;
import cc.mrbird.febs.common.views.ExamPointScorePDFView;
import cc.mrbird.febs.school.entity.ExamPlan;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.entity.TeachingArrange;
import cc.mrbird.febs.school.service.IClazzService;
import cc.mrbird.febs.school.service.IExamScoreService;
import cc.mrbird.febs.school.service.IStudentScoreService;
import com.wuwenze.poi.pojo.ExcelMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("printer")
public class PrinterController extends BaseController {

    private String errorMsg;

    @Autowired
    private IExamScoreService iExamScoreService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private IClazzService iClazzService;


    /**
     * 根据考试计划加载学生考试成绩
     *
     * @param examPlanId
     */

    @RequestMapping("/examPlan/loadStudentScorePrinters/{examPlanId}")
    @RequiresPermissions("education:examPlan:loadStudentScorePrinters")
    public ModelAndView loadStudentScorePrinters(@PathVariable Long examPlanId) {
        Map<String, Object> map = iExamScoreService.findExamScorePrinterByExamPlanId(examPlanId);
        ExamPointScorePDFView examPointScorePDFView = new ExamPointScorePDFView();
        return new ModelAndView(examPointScorePDFView, map);
    }

    /**
     * 根据课程安排加载学生考试成绩
     *
     * @param teachingArrangeId
     */

    @RequestMapping("/teachingArrange/loadStudentScorePrinters/{teachingArrangeId}/{examId}")
    @RequiresPermissions("education:teachingArrange:loadStudentScorePrinters")
    public ModelAndView loadStudentScorePrinters2(@PathVariable Long teachingArrangeId, @PathVariable Long examId) {
        Map<String, Object> map = iStudentScoreService.findStudentScorePrinterByTeachingArrangeIdAndExamId(teachingArrangeId, examId);
        ExamPointScorePDFView examPointScorePDFView = new ExamPointScorePDFView();
        return new ModelAndView(examPointScorePDFView, map);
    }

    /**
     * 根据课程安排加载学生考试成绩
     *
     * @param teachingArrangeId
     */

    @PostMapping("/teachingArrange/excel/{teachingArrangeId}/{examId}")
    @RequiresPermissions("education:teachingArrange:excel")
    @ResponseBody
    public void export(@PathVariable Long teachingArrangeId, @PathVariable Long examId, HttpServletResponse response) {
        Map<String, Object> map = iStudentScoreService.findStudentScorePrinterByTeachingArrangeIdAndExamId(teachingArrangeId, examId);
        String[][] result = (String[][]) map.get("result");
        String title = (String) map.get("title");
        ExcelMapping excelMapping = SuperExcelMappingFactory.buildExcelMapping(title, result);
        List<Map<String, String>> data = SuperExcelMappingFactory.buildExcelData(result);
        SuperExcelKit.$Export(excelMapping, response).downXlsx(data,false);
    }

    /**
     * 打印班级总成绩列表
     *
     * @param examPlan
     * @return
     * @throws FebsException
     */
    @RequestMapping("/examScore/loadStudentTotalScorePrinters")
    @RequiresPermissions("education:examScore:loadStudentTotalScorePrinters")
    public ModelAndView loadStudentTotalScorePrinters(ExamPlan examPlan) throws FebsException {
        Map<String, Object> map = iExamScoreService.findTotalExamScorePrinterByCondition(examPlan.getExamId(),
                examPlan.getSemesterId(), examPlan.getClazzId());
        ExamPointScorePDFView examPointScorePDFView = new ExamPointScorePDFView();
        return new ModelAndView(examPointScorePDFView, map);
    }

    /**
     * 打印班级总成绩列表
     *
     * @param teachingArrange
     * @return
     * @throws FebsException
     */
    @RequestMapping("/studentScore/loadStudentTotalScorePrinters")
    @RequiresPermissions("education:studentScore:loadTotalScorePrinters")
    public ModelAndView loadStudentTotalScorePrinters2(TeachingArrange teachingArrange) throws FebsException {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            List<Long> clazzIdList = iClazzService.getClazzIdListByManagerId(teacher.getTeacherId());
            if (clazzIdList == null || !clazzIdList.contains(teachingArrange.getClazzId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "没有该班级的打印权限不足");
                return new ModelAndView(new ErrorView(), map);
            }
        }
        Map<String, Object> map = iStudentScoreService.findTotalStudentScorePrinterByCondition(teachingArrange.getExamId(),
                teachingArrange.getSemesterId(), teachingArrange.getClazzId());
        ExamPointScorePDFView examPointScorePDFView = new ExamPointScorePDFView();
        return new ModelAndView(examPointScorePDFView, map);
    }

    /**
     * 导出
     *
     * @param request
     * @param teachingArrange
     * @param response
     * @throws FebsException
     */
    @PostMapping("/studentScore/excel")
    @RequiresPermissions("education:studentScore:export")
    @ResponseBody
    public void export(QueryRequest request, @RequestBody TeachingArrange teachingArrange, HttpServletResponse response) throws FebsException {
        try {
            if (isTeacher()) {
                Teacher teacher = getTeacher();
                List<Long> clazzIdList = iClazzService.getClazzIdListByManagerId(teacher.getTeacherId());
                if (clazzIdList == null || !clazzIdList.contains(teachingArrange.getClazzId())) {
                    throw new FebsException("没有该班级的打印权限不足");
                }
            }
            Map<String, Object> map = iStudentScoreService.findTotalStudentScorePrinterByCondition(teachingArrange.getExamId(),
                    teachingArrange.getSemesterId(), teachingArrange.getClazzId());
            String[][] result = (String[][]) map.get("result");
            ExcelMapping excelMapping = SuperExcelMappingFactory.buildExcelMapping("班级总成绩列表", result);
            List<Map<String, String>> data = SuperExcelMappingFactory.buildExcelData(result);
            SuperExcelKit.$Export(excelMapping, response).downXlsx(data,false);
        } catch (Exception e) {
            errorMsg = "导出Excel失败";
            log.error(errorMsg, e);
            throw new FebsException(errorMsg);
        }
    }
}
