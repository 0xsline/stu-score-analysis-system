package cc.mrbird.febs;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.school.entity.Clazz;
import cc.mrbird.febs.school.service.IClazzService;
import cc.mrbird.febs.school.service.impl.ClazzServiceImpl;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ClazzServiceDaoTest extends AppTest {

    @Autowired
    private IClazzService iClazzService  ;


    @Test
    public void test() {
        //读取excel poi 第三封jar
        //for 每一行数据
        //处理每一行数据
    }


    @Test
    public void testInsert() throws Exception {
        Clazz clazz = new Clazz();
        clazz.setClazzName("1班");
        clazz.setGradeId(9l);
        iClazzService.insert(clazz);
    }


    @Test
    public void testPage() {
        QueryRequest queryRequest = new QueryRequest();
        Clazz clazz = new Clazz();
        IPage<Clazz> iPage = iClazzService.findClazzPage(queryRequest, clazz);
        System.out.println(JSONUtil.toJsonPrettyStr(iPage));
    }
}

