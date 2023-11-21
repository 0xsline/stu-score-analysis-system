package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Semester;
import cc.mrbird.febs.school.service.ISemesterService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SemesterServiceDaoTest extends AppTest{

    @Autowired
    private ISemesterService iSemesterService;


    @Test
    public void testSaveSemester() throws Exception {
        Semester semester = new Semester();
        semester.setSemesterName("大一上学期");
        semester.setSemesterSequence(1);
        iSemesterService.insert(semester);
    }


    @Test
    public void testModifySemester() throws Exception {
        Semester semester = new Semester();
        semester.setSemesterId(1l);
        semester.setSemesterName("大一下学期");
        iSemesterService.modify(semester);
    }


    @Test
    public void testPageSemester() {
        QueryRequest queryRequest = new QueryRequest();
        Semester semester = new Semester();
        IPage<?> pageInfo = iSemesterService.findSemesters(queryRequest, semester);
        System.out.println(JSONUtil.toJsonPrettyStr(pageInfo));
    }


    @Test
    public void testGetMaxSemesterNo() {
        Long maxSemesterNo = iSemesterService.getMaxSemesterNo();
        System.out.println(maxSemesterNo);
    }
}
