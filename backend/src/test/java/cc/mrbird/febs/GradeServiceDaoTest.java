package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Grade;
import cc.mrbird.febs.school.service.IGradeService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GradeServiceDaoTest extends AppTest {

    @Autowired
    private IGradeService iGradeService;


    @Test
    public void testGetMaxGradeNo() {
        Long maxGradeNo = iGradeService.getMaxGradeNo();
        System.out.println("最大值gradeNo: " + maxGradeNo);
    }


    @Test
    public void testInsertGrade() throws Exception {
        Grade grade = new Grade();
        grade.setGradeNo(iGradeService.getMaxGradeNo());
        grade.setGradeName("计算机专业");
        grade.setCollegeId(1l);
        iGradeService.insert(grade);
    }


    @Test
    public void testUpdateGrade() throws Exception {
        Grade grade = new Grade();
        grade.setGradeId(7l);
        grade.setGradeName("失败者");
        iGradeService.modify(grade);
    }


    @Test
    public void testPageGrade() {
        QueryRequest queryRequest = new QueryRequest();
        Grade grade = new Grade();
        IPage<Grade> page = iGradeService.findGrades(queryRequest, grade);
        System.out.println(JSONUtil.toJsonPrettyStr(page));
    }
}
