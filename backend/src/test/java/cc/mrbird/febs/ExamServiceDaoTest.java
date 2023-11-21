package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Exam;
import cc.mrbird.febs.school.service.IExamService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExamServiceDaoTest extends AppTest{

    @Autowired
    private IExamService iExamService;


    @Test
    public void testSaveExam() throws Exception {
        Exam exam = new Exam();
        exam.setExamName("期中");
        exam.setExamSequence(1);
        iExamService.insert(exam);
    }


    @Test
    public void testModifyExam() throws Exception {
        Exam exam = new Exam();
        exam.setExamId(1l);
        exam.setExamName("期中2");
        iExamService.modify(exam);
    }


    @Test
    public void testPageExam() {
        QueryRequest queryRequest = new QueryRequest();
        Exam exam = new Exam();
        IPage<?> pageInfo = iExamService.findExams(queryRequest, exam);
        System.out.println(JSONUtil.toJsonPrettyStr(pageInfo));
    }


    @Test
    public void testGetMaxExamNo() {
        Long maxExamNo = iExamService.getMaxExamNo();
        System.out.println(maxExamNo);
    }
}
