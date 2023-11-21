package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Message;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IMessageService extends IService<Message> {


    /**
     * 分页查询
     *
     * @param request
     * @param message
     * @return
     */
    IPage<Message> findMessages(QueryRequest request, Message message);


    /**
     * 删除学院
     *
     * @param ids
     */
    void deleteMessages(String[] ids) throws FebsException;

    /**
     * 根据学生查询留言数量
     *
     * @param studentId
     * @return
     */
    int findCountByStudentId(Long studentId);

    /**
     * 根据学生ID集合列表查询留言数量
     *
     * @param studentIdList
     * @return
     */
    int findCountByStudentIdList(List<Long> studentIdList);

    /**
     * 根据教师查询留言数量
     *
     * @param teacherId
     * @return
     */
    int findCountByTeacherId(Long teacherId);

    /**
     * 根据教师ID集合列表查询留言数量
     *
     * @param teacherIdList
     * @return
     */
    int findCountByTeacherIdList(List<Long> teacherIdList);
}
