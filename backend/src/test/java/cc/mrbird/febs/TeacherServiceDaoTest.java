package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.school.service.ITeacherService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeacherServiceDaoTest extends AppTest {

    @Autowired
    private ITeacherService iTeacherService;


    @Test
    public void testGetMaxTeacherNo() {
        Long maxTeacherNo = iTeacherService.getMaxTeacherNo();
        System.out.println("最大值teacherNo: " + maxTeacherNo);
    }


    @Test
    public void testInsertTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setTeacherNo(iTeacherService.getMaxTeacherNo());
        teacher.setTeacherName("谢中贵");
        teacher.setCollegeId(1l);
        iTeacherService.insert(teacher);
    }


    @Test
    public void testUpdateTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(1l);
        teacher.setTeacherName("谢中贵2");
        iTeacherService.modify(teacher);
    }


    @Test
    public void testPageTeacher() {
        QueryRequest queryRequest = new QueryRequest();
        Teacher teacher = new Teacher();
        IPage<Teacher> page = iTeacherService.findTeachers(queryRequest, teacher);
        System.out.println(JSONUtil.toJsonPrettyStr(page));
    }
}
