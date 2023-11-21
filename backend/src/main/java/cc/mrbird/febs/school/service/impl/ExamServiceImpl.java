package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.ExamMapper;
import cc.mrbird.febs.school.entity.Exam;
import cc.mrbird.febs.school.service.IExamService;
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
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    @CustomerInsert
    public boolean insert(Exam exam) {
        //设置考试编号
        exam.setExamNo(examMapper.getMaxExamNo());
        return examMapper.insert(exam) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Exam exam) {
        return examMapper.updateById(exam) > 0;
    }

    @Override
    public IPage<Exam> findExams(QueryRequest request, Exam exam) {
        try {
            LambdaQueryWrapper<Exam> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(exam.getExamName())) {
                queryWrapper.like(Exam::getExamName, exam.getExamName());
            }
            if (exam.getExamNo() != null) {
                queryWrapper.eq(Exam::getExamNo, exam.getExamNo());
            }

            if (StringUtils.isNotBlank(exam.getCreateTimeFrom()) && StringUtils.isNotBlank(exam.getCreateTimeTo())) {
                queryWrapper
                        .ge(Exam::getCreateTime, exam.getCreateTimeFrom())
                        .le(Exam::getCreateTime, exam.getCreateTimeTo());
            }
            Page<Exam> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取考试失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxExamNo() {
        return examMapper.getMaxExamNo();
    }

    @Override
    public void deleteExams(String[] ids) {
        List<String> list = Arrays.asList(ids);
        examMapper.deleteBatchIds(list);
    }

    @Override
    public int findCountExamByDefault() {
        Wrapper<Exam> wrapper = new QueryWrapper<Exam>().lambda()
                .eq(Exam::getExamType, 1);
        return this.count(wrapper);
    }

    @Override
    public int findCountExamByDefaultUnless(Long examId) {
        Wrapper<Exam> wrapper = new QueryWrapper<Exam>().lambda()
                .eq(Exam::getExamType, 1).ne(Exam::getExamId, examId);
        return this.count(wrapper);
    }
}
