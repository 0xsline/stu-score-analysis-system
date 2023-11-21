package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Grade;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author IU
 */
public interface IGradeService extends IService<Grade> {

    /**
     * 分页查询
     *
     * @param request
     * @param grade
     * @return
     */
    IPage<Grade> findGrades(QueryRequest request, Grade grade);


    /**
     * 获取最大的班级编号+1
     *
     * @return
     */
    Long getMaxGradeNo();

    /**
     * 删除年级
     *
     * @param ids
     */
    void deleteGrades(String[] ids) throws FebsException;

    /**
     * 年级填充学院
     *
     * @param gradeList
     */
    void gradeFillCollege(List<Grade> gradeList);

    /**
     * @return
     */
    Integer getGradeCountByCollegeId(Long collegeId);

    /**
     * 查询指定学院下所有的年级ID列表
     *
     * @param collegeId
     * @return
     */
    List<Long> getGradeIdListByCollegeId(Long collegeId);
}
