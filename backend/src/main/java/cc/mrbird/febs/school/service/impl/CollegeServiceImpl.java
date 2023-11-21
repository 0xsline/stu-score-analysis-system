package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.CollegeMapper;
import cc.mrbird.febs.school.entity.College;
import cc.mrbird.febs.school.entity.Grade;
import cc.mrbird.febs.school.service.ICollegeService;
import cc.mrbird.febs.school.service.IGradeService;
import cc.mrbird.febs.school.service.ITeacherService;
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
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private IGradeService iGradeService;

    @Autowired
    private ITeacherService iTeacherService;

    @Override
    @CustomerInsert
    public boolean insert(College college) {
        //设置学院编号
        college.setCollegeNo(collegeMapper.getMaxCollegeNo());
        return collegeMapper.insert(college) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(College college) {
        return collegeMapper.updateById(college) > 0;
    }

    @Override
    public IPage<College> findColleges(QueryRequest request, College college) {
        try {
            LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(college.getCollegeName())) {
                queryWrapper.like(College::getCollegeName, college.getCollegeName());
            }
            if (college.getCollegeNo() != null) {
                queryWrapper.eq(College::getCollegeNo, college.getCollegeNo());
            }
            if (college.getCollegeId() != null) {
                queryWrapper.eq(College::getCollegeId, college.getCollegeId());
            }

            if (StringUtils.isNotBlank(college.getCreateTimeFrom()) && StringUtils.isNotBlank(college.getCreateTimeTo())) {
                queryWrapper
                        .ge(College::getCreateTime, college.getCreateTimeFrom())
                        .le(College::getCreateTime, college.getCreateTimeTo());
            }
            Page<College> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取学院失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxCollegeNo() {
        return collegeMapper.getMaxCollegeNo();
    }

    @Override
    public void deleteColleges(String[] ids) throws FebsException {
        List<String> list = Arrays.asList(ids);
        Integer count = iGradeService.count(new LambdaQueryWrapper<Grade>().in(Grade::getCollegeId, list));
        if (count > 0) {
            throw new FebsException("学院下存在年级信息，不能删除");
        }
        List<Long> collegeIdList = list.stream().map(Long::valueOf).collect(Collectors.toList());
        Integer teacherCount = iTeacherService.getTeacherCountByCollegeIdList(collegeIdList);
        if (teacherCount > 0) {
            throw new FebsException("学院下存在教师信息，不能删除");
        }
        collegeMapper.deleteBatchIds(list);
    }
}
