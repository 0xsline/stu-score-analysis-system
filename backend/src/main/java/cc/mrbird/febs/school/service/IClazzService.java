package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

public interface IClazzService extends IService<Clazz> {
    /**
     * 分页查询
     *
     * @param request
     * @param clazz
     * @return
     */
    IPage<Clazz> findClazzPage(QueryRequest request, Clazz clazz);


    /**
     * 获取最大的班级编号+1
     *
     * @return
     */
    Long getMaxClazzNo();

    /**
     * 删除班级
     *
     * @param ids
     */
    void deleteClazzList(String[] ids) throws FebsException;

    /**
     * 班级填充年级和学院
     *
     * @param clazzList
     */
    void clazzFillGradeAndCollege(List<Clazz> clazzList);

    /**
     * 班级携带班主任信息
     *
     * @param clazzList
     */
    void clazzWithManager(List<Clazz> clazzList);

    /**
     * 构造年级和学院的组织关系树
     *
     * @return
     */
    List<SchoolTree> buildOrganizationTreeList(Boolean select);

    /**
     * 查询年级下有多少班级
     *
     * @param gradeId
     * @return
     */
    Integer getClazzCountByGradeId(Long gradeId);

    /**
     * 查询年级下的班级ID列表
     *
     * @param gradeId
     * @return
     */
    List<Long> getClazzIdListByGradeId(Long gradeId);

    /**
     * 查询指定年级列表下的所有班级ID列表
     *
     * @param gradeIdList
     * @return
     */
    List<Long> getClazzIdListByGradeIdList(List<Long> gradeIdList);

    /**
     * 获取老师管理的班级数量
     *
     * @param managerIdList
     * @return
     */
    Integer getClazzCountByManagerIdList(List<Long> managerIdList);

    /**
     * 查询某个班级的成绩分析情况
     *
     * @param clazzId
     * @return
     */
    Map<String, Object> findClazzAnalysisDetail(Long clazzId);

    /**
     * 查询教师管理的班级
     *
     * @param managerId
     * @return
     */
    List<Long> getClazzIdListByManagerId(Long managerId);
}
