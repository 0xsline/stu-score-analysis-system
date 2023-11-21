package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.domain.SchoolTree;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.service.IStudentService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StudentServiceDaoTest extends AppTest {

    @Autowired
    private IStudentService iStudentService;


    @Test
    public void testGetMaxStudentNo() {
        Long maxStudentNo = iStudentService.getMaxStudentNo();
        System.out.println("最大值studentNo: " + maxStudentNo);
    }


    @Test
    public void testInsertStudent() throws Exception {
        Student student = new Student();
        student.setStudentNo(iStudentService.getMaxStudentNo());
        student.setStudentName("ceicei");
        student.setClazzId(1l);
        iStudentService.insert(student);
    }


    @Test
    public void testUpdateStudent() throws Exception {
        Student student = new Student();
        student.setStudentId(1l);
        student.setStudentName("ceicei2");
        iStudentService.modify(student);
    }


    @Test
    public void testPageStudent() {
        QueryRequest queryRequest = new QueryRequest();
        Student student = new Student();
        IPage<Student> page = iStudentService.findStudents(queryRequest, student);
        System.out.println(JSONUtil.toJsonPrettyStr(page));
    }


    @Test
    public void testTreeData() {
        List<SchoolTree> schoolTreeList = iStudentService.buildOrganizationTreeList(null);
        System.out.println(JSONUtil.toJsonPrettyStr(schoolTreeList));
    }

    @Test
    public void test1(){
        Map<Long, Long> map = iStudentService.clazzStudentCount(Arrays.asList(5l, 6l));
        System.out.println(map);
    }
}
