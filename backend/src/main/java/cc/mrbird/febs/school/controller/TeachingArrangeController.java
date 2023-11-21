package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.StudentScore;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.entity.TeachingArrange;
import cc.mrbird.febs.school.service.IStudentScoreService;
import cc.mrbird.febs.school.service.ITeachingArrangeService;
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
@RequestMapping("education/teachingArrange")
public class TeachingArrangeController extends BaseController {

    private String message;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IStudentScoreService iStudentScoreService;


    /**
     * 教学安排列表
     *
     * @param request
     * @param teachingArrange
     * @return
     */
    @GetMapping
    @RequiresPermissions("education:teachingArrange:view")
    public Map<String, Object> teachingArrangeList(QueryRequest request, TeachingArrange teachingArrange) {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            teachingArrange.setTeacherId(teacher.getTeacherId());
        }

        if (isStudent()) {
            Student student = getStudent();
            teachingArrange.setClazzId(student.getClazzId());
        }
        return getDataTable(this.iTeachingArrangeService.findTeachingArranges(request, teachingArrange));
    }


    /**
     * 新增教学安排
     *
     * @param teachingArrange
     * @throws FebsException
     */
    @Log("新增教学安排")
    @PostMapping
    @RequiresPermissions("education:teachingArrange:add")
    public void addTeachingArrange(@RequestBody @Valid TeachingArrange teachingArrange) throws Exception {
        try {
            this.iTeachingArrangeService.insert(teachingArrange);
        } catch (Exception e) {
            if (e instanceof FebsException) {
                throw e;
            }
            message = "新增教学安排失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改教学安排
     *
     * @param teachingArrange
     * @throws FebsException
     */
    @Log("修改教学安排")
    @PutMapping
    @RequiresPermissions("education:teachingArrange:update")
    public void updateTeachingArrange(@RequestBody @Valid TeachingArrange teachingArrange) throws Exception {
        try {
            this.iTeachingArrangeService.modify(teachingArrange);
        } catch (Exception e) {
            if (e instanceof FebsException) {
                throw e;
            }
            message = "修改教学安排失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除教学安排
     *
     * @param teachingArrangeIds
     * @throws FebsException
     */
    @Log("删除教学安排")
    @DeleteMapping("/{teachingArrangeIds}")
    @RequiresPermissions("education:teachingArrange:delete")
    public void deleteTeachingArrange(@NotBlank(message = "{required}") @PathVariable String teachingArrangeIds) throws FebsException {
        try {
            String[] ids = teachingArrangeIds.split(StringPool.COMMA);
            this.iTeachingArrangeService.deleteTeachingArranges(ids);
        } catch (Exception e) {
            message = "删除教学安排失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 根据课程安排加载学生考试成绩
     *
     * @param teachingArrangeId
     * @param examId
     */

    @GetMapping("/loadStudentScoreLists/{teachingArrangeId}/{examId}")
    @RequiresPermissions("education:teachingArrange:loadStudentScoreLists")
    public List<StudentScore> loadStudentScoreLists(@PathVariable Long teachingArrangeId, @PathVariable Long examId) {
        return iStudentScoreService.findStudentScoreByTeachingArrangeId(teachingArrangeId, examId);
    }

    /**
     * 根据课程安排加载学生考试成绩
     *
     * @param teachingArrangeId
     */

    @GetMapping("/loadStudentScoreMaps/{teachingArrangeId}/{examId}")
    @RequiresPermissions("education:teachingArrange:loadStudentScoreMaps")
    public Map<String, Object> loadStudentScoreMaps(@PathVariable Long teachingArrangeId, @PathVariable Long examId) {
        return iStudentScoreService.findStudentScoreMapByTeachingArrangeId(teachingArrangeId, examId);
    }

    /**
     * 根据课程安排加载学生考试成绩
     *
     * @param teachingArrangeId
     */

    @GetMapping("/loadStudentScoreMaps/{teachingArrangeId}")
    @RequiresPermissions("education:teachingArrange:loadStudentScoreMaps2")
    public Map<String, Object> loadStudentScoreMaps(@PathVariable Long teachingArrangeId) {
        return iStudentScoreService.findStudentScoreMapByTeachingArrangeId(teachingArrangeId);
    }

    /**
     * 保存考试成绩
     *
     * @param examScoreMap
     * @throws FebsException
     */
    @Log("保存考试成绩")
    @PutMapping("/loadStudentScoreLists")
    @RequiresPermissions("education:teachingArrange:saveStudentScoreLists")
    public void saveStudentScoreLists(@RequestBody Map<String, String> examScoreMap) throws FebsException {
        System.out.println(JSONUtil.toJsonPrettyStr(examScoreMap));
        iStudentScoreService.saveStudentScoreMap(examScoreMap);
    }


    /**
     * 导出
     *
     * @param request
     * @param teachingArrange
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("education:teachingArrange:export")
    public void export(QueryRequest request, TeachingArrange teachingArrange, HttpServletResponse response) throws FebsException {
        try {
            List<TeachingArrange> teachingArrangeList = this.iTeachingArrangeService.
                    findTeachingArranges(request, teachingArrange).getRecords();
            ExcelKit.$Export(TeachingArrange.class, response).downXlsx(teachingArrangeList, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
