package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.SemesterMapper;
import cc.mrbird.febs.school.entity.Semester;
import cc.mrbird.febs.school.service.ISemesterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class SemesterServiceImpl extends ServiceImpl<SemesterMapper, Semester> implements ISemesterService {

    @Autowired
    private SemesterMapper semesterMapper;

    @Override
    @CustomerInsert
    public boolean insert(Semester semester) {
        //设置学院编号
        semester.setSemesterNo(semesterMapper.getMaxSemesterNo());
        return semesterMapper.insert(semester) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Semester semester) {
        return semesterMapper.updateById(semester) > 0;
    }

    @Override
    public IPage<Semester> findSemesters(QueryRequest request, Semester semester) {
        try {
            LambdaQueryWrapper<Semester> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(semester.getSemesterName())) {
                queryWrapper.like(Semester::getSemesterName, semester.getSemesterName());
            }
            if (semester.getSemesterNo() != null) {
                queryWrapper.eq(Semester::getSemesterNo, semester.getSemesterNo());
            }

            if (StringUtils.isNotBlank(semester.getCreateTimeFrom()) && StringUtils.isNotBlank(semester.getCreateTimeTo())) {
                queryWrapper
                        .ge(Semester::getCreateTime, semester.getCreateTimeFrom())
                        .le(Semester::getCreateTime, semester.getCreateTimeTo());
            }
            Page<Semester> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取学期失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxSemesterNo() {
        return semesterMapper.getMaxSemesterNo();
    }

    @Override
    public void deleteSemesters(String[] ids){
        List<String> list = Arrays.asList(ids);
        semesterMapper.deleteBatchIds(list);
    }
}
