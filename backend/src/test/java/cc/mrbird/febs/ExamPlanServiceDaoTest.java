package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.ExamPlan;
import cc.mrbird.febs.school.service.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExamPlanServiceDaoTest extends AppTest{

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private IExamService iExamService;

    @Autowired
    private ISemesterService iSemesterService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private ITeacherService iTeacherService;

    @Test
    public void testSaveExamPlan() throws Exception {
        ExamPlan examPlan = new ExamPlan();
        examPlan.setExamPlanName("高一上学期语文期末考试");
        examPlan.setExamPlanType(1);
        //考试
        examPlan.setExamId(iExamService.list().get(0).getExamId());
        //学期
        examPlan.setSemesterId(iSemesterService.list().get(0).getSemesterId());
        //班级
        examPlan.setClazzId(iClazzService.list().get(0).getClazzId());
        //课程
        examPlan.setCourseId(iCourseService.list().get(0).getCourseId());
        //教师
        examPlan.setTeacherId(iTeacherService.list().get(0).getTeacherId());
        //学分
        examPlan.setStudyScore(iCourseService.list().get(0).getStudyScore());
        //满分
        examPlan.setExamScore(iCourseService.list().get(0).getExamScore());
        iExamPlanService.insert(examPlan);
    }


    @Test
    public void testModifyExamPlan() throws Exception {
        ExamPlan examPlan = new ExamPlan();
        examPlan.setExamPlanId(1l);
        examPlan.setExamPlanName("期中33332");
        iExamPlanService.modify(examPlan);
    }


    @Test
    public void testPageExamPlan() {
        QueryRequest queryRequest = new QueryRequest();
        ExamPlan examPlan = new ExamPlan();
        IPage<?> pageInfo = iExamPlanService.findExamPlans(queryRequest, examPlan);
        System.out.println(JSONUtil.toJsonPrettyStr(pageInfo));
    }


    @Test
    public void testGetMaxExamPlanNo() {
        Long maxExamPlanNo = iExamPlanService.getMaxExamPlanNo();
        System.out.println(maxExamPlanNo);
    }
}
