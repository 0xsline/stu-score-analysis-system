package cc.mrbird.febs.common.controller;

import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.enums.UserType;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.IStudentService;
import cc.mrbird.febs.school.service.ITeacherService;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.manager.UserManager;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    //每页的最大导出数量
    @Value("${export.maxCount}")
    public Integer exportMaxCount;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ITeacherService iTeacherService;

    @Autowired
    private IStudentService iStudentService;

    private Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    protected User getCurrentUser() {
        String token = (String) getSubject().getPrincipal();
        String username = JWTUtil.getUsername(token);
        return userManager.getUser(username);
    }

    /**
     * @return
     */
    protected Teacher getTeacher() {
        User user = getCurrentUser();
        return iTeacherService.findTeacherByUserId(user.getUserId());
    }

    /**
     * @return
     */
    protected Student getStudent() {
        User user = getCurrentUser();
        return iStudentService.findStudentByUserId(user.getUserId());
    }

    /**
     * 判断是不是系统用户
     * @return
     */
    protected boolean isSystem() {
        User user = getCurrentUser();
        return user.getUserType() == UserType.SYSTEM.getCode();
    }

    /**
     * 判断是不是教师
     *
     * @return
     */
    protected boolean isTeacher() {
        User user = getCurrentUser();
        return user.getUserType() == UserType.TEACHER.getCode();
    }

    /**
     * 判断是不是学生
     *
     * @return
     */
    protected boolean isStudent() {
        User user = getCurrentUser();
        return user.getUserType() == UserType.STUDENT.getCode();
    }

    protected Map<String, Object> getDataTable(IPage<?> pageInfo) {
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", pageInfo.getRecords());
        rspData.put("total", pageInfo.getTotal());
        return rspData;
    }


}
