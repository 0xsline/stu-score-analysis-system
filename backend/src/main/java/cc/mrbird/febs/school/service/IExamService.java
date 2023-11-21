package cc.mrbird.febs.school.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.school.entity.Exam;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author IU
 */
public interface IExamService extends IService<Exam> {

    /**
     * 分页查询
     *
     * @param request
     * @param exam
     * @return
     */
    IPage<Exam> findExams(QueryRequest request, Exam exam);


    /**
     * 获取最大的考试编号+1
     *
     * @return
     */
    Long getMaxExamNo();

    /**
     * 删除考试
     *
     * @param ids
     */
    void deleteExams(String[] ids);

    /**
     * 查询默认考试数量
     *
     * @return
     */
    int findCountExamByDefault();

    /**
     * 查询出examId之外的默认数量
     *
     * @param examId
     * @return
     */
    int findCountExamByDefaultUnless(Long examId);
}
