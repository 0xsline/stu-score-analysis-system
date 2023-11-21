package cc.mrbird.febs;

import cc.mrbird.febs.school.entity.StudentScore;
import cc.mrbird.febs.school.service.IStudentScoreService;
import cn.hutool.json.JSONUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StudentScoreServiceDaoTest extends AppTest {

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Test
    public void testSelect() {
        Map<Long, Long> map = iStudentScoreService.teachingArrangeIdStudentCount(Arrays.asList(15l), 5);
        System.out.println(map);
    }

    @Test
    public void testSelect2() {
        List<Long> examId = iStudentScoreService.findExamIdListByTeachingArrangeId(15l);
        System.out.println(examId);
    }

    @Test
    public void testSelect3() {
        List<StudentScore> studentScoreList = iStudentScoreService.findListByTeachingArrangeId(15l);
        studentScoreList.forEach(System.out::println);
    }

    @Test
    public void testSelect4() {
        Map<String, Object> map = iStudentScoreService.findStudentScoreMapByTeachingArrangeId(15l);
        System.out.println(map);
    }

    @Test
    public void testSelect5() {
        Map<String, Object> map = iStudentScoreService.findStudentScoreMapByStudentId(24l);
        System.out.println(JSONUtil.toJsonPrettyStr(map));
    }
}
