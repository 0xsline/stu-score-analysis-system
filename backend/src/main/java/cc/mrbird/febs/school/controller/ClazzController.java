package cc.mrbird.febs.school.controller;


import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.IClazzService;
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
@RequestMapping("organization/clazz")
public class ClazzController extends BaseController {

    private String message;

    @Autowired
    private IClazzService iClazzService;


    /**
     * 班级列表
     *
     * @param request
     * @param clazz
     * @return
     */
    @GetMapping
    @RequiresPermissions("organization:clazz:view")
    public Map<String, Object> clazzList(QueryRequest request, Clazz clazz) {
        if (isTeacher()) {
            Teacher teacher = getTeacher();
            clazz.setManagerId(teacher.getTeacherId());
        }
        if (isStudent()) {
            Student student = getStudent();
            clazz.setClazzId(student.getClazzId());
        }
        return getDataTable(this.iClazzService.findClazzPage(request, clazz));
    }


    /**
     * 新增班级
     *
     * @param clazz
     * @throws FebsException
     */
    @Log("新增班级")
    @PostMapping
    @RequiresPermissions("organization:clazz:add")
    public void addClazz(@RequestBody @Valid Clazz clazz) throws FebsException {
        try {
            this.iClazzService.insert(clazz);
        } catch (Exception e) {
            message = "新增班级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 修改班级
     *
     * @param clazz
     * @throws FebsException
     */
    @Log("修改班级")
    @PutMapping
    @RequiresPermissions("organization:clazz:update")
    public void updateClazz(@RequestBody @Valid Clazz clazz) throws FebsException {
        try {
            this.iClazzService.modify(clazz);
        } catch (Exception e) {
            message = "修改班级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 删除班级
     *
     * @param clazzIds
     * @throws FebsException
     */
    @Log("删除班级")
    @DeleteMapping("/{clazzIds}")
    @RequiresPermissions("organization:clazz:delete")
    public void deleteClazz(@NotBlank(message = "{required}") @PathVariable String clazzIds) throws FebsException {
        try {
            String[] ids = clazzIds.split(StringPool.COMMA);
            this.iClazzService.deleteClazzList(ids);
        } catch (Exception e) {
            message = "删除班级失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }


    /**
     * 导出
     *
     * @param request
     * @param clazz
     * @param response
     * @throws FebsException
     */
    @PostMapping("excel")
    @RequiresPermissions("organization:clazz:export")
    public void export(QueryRequest request, Clazz clazz, HttpServletResponse response) throws FebsException {
        try {
            List<Clazz> clazzList = this.iClazzService.findClazzPage(request, clazz).getRecords();
            ExcelKit.$Export(Clazz.class, response).downXlsx(clazzList, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 构造年级和学院树
     *
     * @return
     */
    @GetMapping("/tree")
    @RequiresUser
    public List<SchoolTree> organizationTreeList() {
        return iClazzService.buildOrganizationTreeList(true);
    }

    /**
     * 根据班级分析课程教学情况
     *
     * @param clazzId
     */

    @GetMapping("/analysis/{clazzId}")
    @RequiresPermissions("organization:clazz:analysis")
    public Map<String, Object> analysis(@PathVariable Long clazzId) {
        return iClazzService.findClazzAnalysisDetail(clazzId);
    }
}
