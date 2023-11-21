package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.College;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author IU
 */
public interface ICollegeService extends IService<College> {

    /**
     * 分页查询
     *
     * @param request
     * @param college
     * @return
     */
    IPage<College> findColleges(QueryRequest request, College college);


    /**
     * 获取最大的学院编号+1
     *
     * @return
     */
    Long getMaxCollegeNo();

    /**
     * 删除学院
     *
     * @param ids
     */
    void deleteColleges(String[] ids) throws FebsException;
}
