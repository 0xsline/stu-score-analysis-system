package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.school.dao.GradeMapper;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.entity.College;
import cc.mrbird.febs.school.entity.Grade;
import cc.mrbird.febs.school.service.IClazzService;
import cc.mrbird.febs.school.service.ICollegeService;
import cc.mrbird.febs.school.service.IGradeService;
import cc.mrbird.febs.school.service.IStudentService;
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
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements IGradeService {

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private ICollegeService iCollegeService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private IStudentService iStudentService;


    @Override
    @CustomerInsert
    public boolean insert(Grade entity) {
        entity.setGradeNo(gradeMapper.getMaxGradeNo());
        return gradeMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(Grade entity) {
        return gradeMapper.updateById(entity) > 0;
    }

    @Override
    public IPage<Grade> findGrades(QueryRequest request, Grade grade) {
        try {
            LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(grade.getGradeName())) {
                queryWrapper.like(Grade::getGradeName, grade.getGradeName());
            }
            if (grade.getGradeNo() != null) {
                queryWrapper.eq(Grade::getGradeNo, grade.getGradeNo());
            }

            if (grade.getCollegeId() != null) {
                queryWrapper.eq(Grade::getCollegeId, grade.getCollegeId());
            }

            if (grade.getGradeId() != null) {
                queryWrapper.eq(Grade::getGradeId, grade.getGradeId());
            }

            if (StringUtils.isNotBlank(grade.getCreateTimeFrom()) && StringUtils.isNotBlank(grade.getCreateTimeTo())) {
                queryWrapper
                        .ge(Grade::getCreateTime, grade.getCreateTimeFrom())
                        .le(Grade::getCreateTime, grade.getCreateTimeTo());
            }
            Page<Grade> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handlePageSort(request, page, "createTime", FebsConstant.ORDER_DESC, true);
            IPage iPage = this.page(page, queryWrapper);
            List<Grade> gradeList = iPage.getRecords();
            if (CollectionUtils.isEmpty(gradeList)) {
                return iPage;
            }
            //年级填充学院
            gradeFillCollege(gradeList);

            gradeList.forEach(obj -> {
                List<Long> clazzIdList = iClazzService.getClazzIdListByGradeId(obj.getGradeId());
                if (!CollectionUtils.isEmpty(clazzIdList)) {
                    Integer studentCount = iStudentService.studentCountByClazzIdList(clazzIdList);
                    if (studentCount != null) {
                        obj.setStudentCount(studentCount);
                    }
                }
            });
            return iPage;
        } catch (Exception e) {
            log.error("获取年级失败", e);
            return null;
        }
    }

    @Override
    public Long getMaxGradeNo() {
        return gradeMapper.getMaxGradeNo();
    }

    @Override
    public void deleteGrades(String[] ids) throws FebsException {
        List<String> list = Arrays.asList(ids);
        Integer count = iClazzService.count(new LambdaQueryWrapper<Clazz>().in(Clazz::getGradeId, list));
        if (count > 0) {
            throw new FebsException("年级下存在班级信息，不能删除");
        }
        gradeMapper.deleteBatchIds(list);
    }

    @Override
    public void gradeFillCollege(List<Grade> gradeList) {
        List<Long> collegeIds = gradeList.stream().map(Grade::getCollegeId).distinct().collect(Collectors.toList());
        Map<Long, College> longCollegeMap = iCollegeService.listByIds(collegeIds).stream().collect(Collectors.toMap(College::getCollegeId, it -> it));
        gradeList.forEach(obj -> {
            obj.setCollege(longCollegeMap.get(obj.getCollegeId()));
            Integer clazzCount = iClazzService.getClazzCountByGradeId(obj.getGradeId());
            if (clazzCount != null) {
                obj.setClazzCount(clazzCount);
            }
        });
    }

    @Override
    public Integer getGradeCountByCollegeId(Long collegeId) {
        Wrapper<Grade> wrapper = new QueryWrapper<Grade>().lambda()
                .eq(Grade::getCollegeId, collegeId);
        return this.count(wrapper);
    }

    @Override
    public List<Long> getGradeIdListByCollegeId(Long collegeId) {
        Wrapper<Grade> wrapper = new QueryWrapper<Grade>().lambda()
                .eq(Grade::getCollegeId, collegeId);
        List<Grade> gradeList = this.list(wrapper);
        if (CollectionUtils.isEmpty(gradeList)) {
            return null;
        }
        return gradeList.stream().map(Grade::getGradeId).collect(Collectors.toList());
    }
}
