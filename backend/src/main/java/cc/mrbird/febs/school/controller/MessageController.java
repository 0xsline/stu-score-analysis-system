package cc.mrbird.febs.school.controller;

import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.*;
import cc.mrbird.febs.school.service.IMessageService;
import cc.mrbird.febs.school.service.IStudentService;
import cc.mrbird.febs.school.service.ITeacherService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("help/message")
public class MessageController extends BaseController {

    private String errorMsg;

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private ITeacherService iTeacherService;

    /**
     * 学院列表
     *
     * @param request
     * @param message
     * @return
     */
    @GetMapping
    @RequiresPermissions("help:message:view")
    public Map<String, Object> messageList(QueryRequest request, Message message) {

        if (isStudent()) {
            Student student = getStudent();
            message.setStudentId(student.getStudentId());
        }

        if (isTeacher()) {
            Teacher teacher = getTeacher();
            message.setTeacherId(teacher.getTeacherId());
        }

        IPage<Message> messageIPage = this.iMessageService.findMessages(request, message);
        List<Message> messageList = messageIPage.getRecords();
        if (!CollectionUtils.isEmpty(messageList)) {
            messageList.forEach(obj -> {
                Student student = iStudentService.getById(obj.getStudentId());
                if (student != null) {
                    obj.setStudentName(student.getStudentName());
                }
                Teacher teacher = iTeacherService.getById(obj.getTeacherId());
                if (teacher != null) {
                    obj.setTeacherName(teacher.getTeacherName());
                }
            });
        }
        return getDataTable(messageIPage);
    }


    /**
     * 新增留言
     *
     * @param message
     * @throws FebsException
     */
    @Log("新增留言")
    @PostMapping
    @RequiresPermissions("help:message:add")
    public void addMessage(@RequestBody @Valid Message message) throws FebsException {
        try {
            if (isStudent()) {
                Student student = getStudent();
                message.setStudentId(student.getStudentId());
                message.setStatus("0");
                message.setMessageTime(new Date());
                this.iMessageService.insert(message);
            }
        } catch (Exception e) {
            errorMsg = "新增留言失败";
            log.error(errorMsg, e);
            throw new FebsException(errorMsg);
        }
    }

    /**
     * 回复留言
     *
     * @param message
     * @throws FebsException
     */
    @Log("回复留言")
    @PutMapping
    @RequiresPermissions("help:message:update")
    public void updateMessage(@RequestBody @Valid Message message) throws FebsException {
        try {
            message.setReplyTime(new Date());
            message.setStatus("1");
            this.iMessageService.modify(message);
        } catch (Exception e) {
            errorMsg = "回复留言失败";
            log.error(errorMsg, e);
            throw new FebsException(errorMsg);
        }
    }

    /**
     * 删除留言
     *
     * @param messageIds
     * @throws FebsException
     */
    @Log("删除留言")
    @DeleteMapping("/{messageIds}")
    @RequiresPermissions("help:message:delete")
    public void deleteCollege(@NotBlank(message = "{required}") @PathVariable String messageIds) throws FebsException {
        try {
            String[] ids = messageIds.split(StringPool.COMMA);
            this.iMessageService.deleteMessages(ids);
        } catch (Exception e) {
            errorMsg = "删除留言失败";
            log.error(errorMsg, e);
            throw new FebsException(errorMsg);
        }
    }
}
