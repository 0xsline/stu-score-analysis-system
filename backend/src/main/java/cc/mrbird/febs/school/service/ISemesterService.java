package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.Semester;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author IU
 */
public interface ISemesterService extends IService<Semester> {

    /**
     * 分页查询
     *
     * @param request
     * @param semester
     * @return
     */
    IPage<Semester> findSemesters(QueryRequest request, Semester semester);


    /**
     * 获取最大的学期编号+1
     *
     * @return
     */
    Long getMaxSemesterNo();

    /**
     * 删除学期
     *
     * @param ids
     */
    void deleteSemesters(String[] ids);
}
