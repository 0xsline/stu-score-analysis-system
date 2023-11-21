package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.College;
import cc.mrbird.febs.school.service.ICollegeService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CollegeServiceDaoTest  extends AppTest{

    @Autowired
    private ICollegeService iCollegeService;


    @Test
    public void testSaveCollege() throws Exception {
        College college = new College();
        college.setCollegeName("物理学院");
        iCollegeService.insert(college);
    }


    @Test
    public void testModifyCollege() throws Exception {
        College college = new College();
        college.setCollegeId(2l);
        college.setCollegeName("外国语学院2");
        iCollegeService.modify(college);
    }


    @Test
    public void testPageCollege() {
        QueryRequest queryRequest = new QueryRequest();
        College college = new College();
        IPage<?> pageInfo = iCollegeService.findColleges(queryRequest, college);
        System.out.println(JSONUtil.toJsonPrettyStr(pageInfo));
    }


    @Test
    public void testGetMaxCollegeNo() {
        Long maxCollegeNo = iCollegeService.getMaxCollegeNo();
        System.out.println(maxCollegeNo);
    }
}
