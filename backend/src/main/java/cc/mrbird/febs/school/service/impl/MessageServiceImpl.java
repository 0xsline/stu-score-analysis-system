package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.MessageMapper;
import cc.mrbird.febs.school.entity.Message;
import cc.mrbird.febs.school.service.IMessageService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    @CustomerInsert
    public boolean insert(Message entity) throws Exception {
        return messageMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Message entity) throws Exception {
        return messageMapper.updateById(entity) > 0;
    }

    @Override
    public IPage<Message> findMessages(QueryRequest request, Message message) {
        try {
            LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
            if (message.getStudentId() != null) {
                queryWrapper.eq(Message::getStudentId, message.getStudentId());
            }
            if (message.getTeacherId() != null) {
                queryWrapper.eq(Message::getTeacherId, message.getTeacherId());
            }
            if (StringUtils.isNotBlank(message.getStatus())) {
                queryWrapper.eq(Message::getStatus, message.getStatus());
            }

            if (StringUtils.isNotBlank(message.getMessageTitle())) {
                queryWrapper.like(Message::getMessageTitle, message.getMessageTitle());
            }
            if (StringUtils.isNotBlank(message.getCreateTimeFrom()) && StringUtils.isNotBlank(message.getCreateTimeTo())) {
                queryWrapper
                        .ge(Message::getCreateTime, message.getCreateTimeFrom())
                        .le(Message::getCreateTime, message.getCreateTimeTo());
            }
            Page<Message> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取留言失败", e);
            return null;
        }
    }

    @Override
    public void deleteMessages(String[] ids) throws FebsException {
        List<String> list = Arrays.asList(ids);
        messageMapper.deleteBatchIds(list);
    }

    @Override
    public int findCountByStudentId(Long studentId) {
        Wrapper<Message> wrapper = new QueryWrapper<Message>().lambda()
                .eq(Message::getStudentId, studentId);
        return this.count(wrapper);
    }

    @Override
    public int findCountByStudentIdList(List<Long> studentIdList) {
        Wrapper<Message> wrapper = new QueryWrapper<Message>().lambda()
                .in(Message::getStudentId, studentIdList);
        return this.count(wrapper);
    }

    @Override
    public int findCountByTeacherId(Long teacherId) {
        Wrapper<Message> wrapper = new QueryWrapper<Message>().lambda()
                .eq(Message::getTeacherId, teacherId);
        return this.count(wrapper);
    }

    @Override
    public int findCountByTeacherIdList(List<Long> teacherIdList) {
        Wrapper<Message> wrapper = new QueryWrapper<Message>().lambda()
                .in(Message::getTeacherId, teacherIdList);
        return this.count(wrapper);
    }
}
