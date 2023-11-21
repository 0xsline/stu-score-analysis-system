package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Course;
import cc.mrbird.febs.school.service.ICourseService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CourseServiceDaoTest extends AppTest{

    @Autowired
    private ICourseService iCourseService;


    @Test
    public void testSaveCourse() throws Exception {
        Course course = new Course();
        course.setCourseName("高等数学");
        course.setStudyScore(1.5f);
        course.setExamScore(100d);
        iCourseService.insert(course);
    }


    @Test
    public void testModifyCourse() throws Exception {
        Course course = new Course();
        course.setCourseId(1l);
        course.setCourseName("高等数学2");
        iCourseService.modify(course);
    }


    @Test
    public void testPageCourse() {
        QueryRequest queryRequest = new QueryRequest();
        Course course = new Course();
        IPage<?> pageInfo = iCourseService.findCourses(queryRequest, course);
        System.out.println(JSONUtil.toJsonPrettyStr(pageInfo));
    }


    @Test
    public void testGetMaxCourseNo() {
        Long maxCourseNo = iCourseService.getMaxCourseNo();
        System.out.println(maxCourseNo);
    }
}
