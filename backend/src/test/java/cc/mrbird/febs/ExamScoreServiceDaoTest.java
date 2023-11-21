package cc.mrbird.febs;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.ExamScore;
import cc.mrbird.febs.school.service.IExamScoreService;
import cn.hutool.json.JSONUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ExamScoreServiceDaoTest extends AppTest {

    @Autowired
    private IExamScoreService iExamScoreService;


    @Test
    public void testInsert() throws Exception {
        ExamScore examScore = new ExamScore();
        examScore.setExamPlanId(1l);
        examScore.setStudentId(1l);
        examScore.setScore(88d);
        iExamScoreService.insert(examScore);
    }


    @Test
    public void testList() {
        List<ExamScore> examScoreList = iExamScoreService.findExamScoreByExamPlanId(1l);
        System.out.println(JSONUtil.toJsonPrettyStr(examScoreList));
    }

    @Test
    public void testMap() {
        Map<String, Object> examScoreMap = iExamScoreService.findExamScoreMapByExamPlanId(1l);
        System.out.println(JSONUtil.toJsonPrettyStr(examScoreMap));
    }

    @Test
    public void testTotalMap() throws FebsException {
        Map<String, Object> totalExamScoreMap = iExamScoreService.findTotalExamScoreMapByCondition(1l, 1l, 1l);
        System.out.println(JSONUtil.toJsonPrettyStr(totalExamScoreMap));
    }


    @Test
    public void testTotalPrinter() throws FebsException {
        Map<String, Object> totalExamScoreMap = iExamScoreService.findTotalExamScorePrinterByCondition(1l, 1l, 1l);
        System.out.println(JSONUtil.toJsonPrettyStr(totalExamScoreMap));
    }
}
