package cc.mrbird.febs;

import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.service.ICourseService;
import cc.mrbird.febs.school.service.IStudentService;
import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTest  {

    @Autowired
    private IStudentService studentService ;

    @Autowired
    private ICourseService courseService;

    @Test
    public void test1() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        String value = BeanUtils.getProperty(map, "name");
        System.out.println(value);
    }
    private RowHandler createRowHandler() {
        return new RowHandler() {
            @Override
            public void handle(int i, long l, List<Object> list) {
                Console.log("[{}] [{}] {}", i, l, list);
            }

            @Override
            public void handleCell(int sheetIndex, long rowIndex, int cellIndex, Object value, CellStyle xssfCellStyle) {
                RowHandler.super.handleCell(sheetIndex, rowIndex, cellIndex, value, xssfCellStyle);
            }

            @Override
            public void doAfterAllAnalysed() {
                RowHandler.super.doAfterAllAnalysed();
            }

        };
    }

    @Test
    public void test2() throws Exception {
        Wrapper<Student> userQueryWrapper = new QueryWrapper<Student>().lambda().eq(Student::getStudentName, "林少春");
        Student student = studentService.getOne(userQueryWrapper);
        student.setClazzId(5L);
        studentService.insert(student);
        System.out.println(JSONUtil.toJsonPrettyStr(student));
        //处理课程
    }

    @Test
    public void test() {
        ExcelUtil.readBySax("D:\\Code\\gitee\\stu-score-analysis-vue\\backend\\doc\\预处理.xls", 0, createRowHandler());
    }


}
