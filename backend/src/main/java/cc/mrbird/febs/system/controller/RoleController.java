package cc.mrbird.febs.system.controller;

import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.system.domain.Role;
import cc.mrbird.febs.system.domain.RoleMenu;
import cc.mrbird.febs.system.service.RoleMenuServie;
import cc.mrbird.febs.system.service.RoleService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuServie roleMenuServie;

    private String message;

    @GetMapping
    @RequiresPermissions("role:view")
    public Map<String, Object> roleList(QueryRequest queryRequest, Role role) {
        return getDataTable(roleService.findRoles(role, queryRequest));
    }

    @GetMapping("check/{roleName}")
    public boolean checkRoleName(@NotBlank(message = "{required}") @PathVariable String roleName) {
        Role result = this.roleService.findByName(roleName);
        return result == null;
    }

    @GetMapping("menu/{roleId}")
    public List<String> getRoleMenus(@NotBlank(message = "{required}") @PathVariable String roleId) {
        List<RoleMenu> list = this.roleMenuServie.getRoleMenusByRoleId(roleId);
        return list.stream().map(roleMenu -> String.valueOf(roleMenu.getMenuId())).collect(Collectors.toList());
    }

    @Log("新增角色")
    @PostMapping
    @RequiresPermissions("role:add")
    public FebsResponse addRole(@RequestBody @Valid Role role) throws FebsException {
        try {
            this.roleService.createRole(role);
            return new FebsResponse().code("200").message("新增角色成功").status("success");
        } catch (Exception e) {
            message = "新增角色失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @Log("删除角色")
    @DeleteMapping("/{roleIds}")
    @RequiresPermissions("role:delete")
    public FebsResponse deleteRoles(@NotBlank(message = "{required}") @PathVariable String roleIds) throws FebsException {
        try {
            String[] ids = roleIds.split(StringPool.COMMA);
            Role teacherRole = this.roleService.findByTeacher();
            Role studentRole = this.roleService.findByStudent();
            if (Arrays.asList(ids).contains(teacherRole.getRoleId().toString()) || Arrays.asList(ids).contains(studentRole.getRoleId().toString())) {
                return new FebsResponse().code("500").message("教师和学生角色禁止删除").status("error");
            }
            this.roleService.deleteRoles(ids);
            return new FebsResponse().code("200").message("删除角色成功").status("success");
        } catch (Exception e) {
            message = "删除角色失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @Log("修改角色")
    @PutMapping
    @RequiresPermissions("role:update")
    public FebsResponse updateRole(@RequestBody @Valid Role role) throws FebsException {
        try {
            this.roleService.updateRole(role);
            return new FebsResponse().code("200").message("修改角色成功").status("success");
        } catch (Exception e) {
            message = "修改角色失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("role:export")
    public void export(QueryRequest queryRequest, @RequestBody Role role, HttpServletResponse response) throws FebsException {
        try {
            List<Role> roles = this.roleService.findRoles(role, queryRequest).getRecords();
            ExcelKit.$Export(Role.class, response).downXlsx(roles, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
