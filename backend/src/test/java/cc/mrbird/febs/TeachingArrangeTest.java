package cc.mrbird.febs;

import cc.mrbird.febs.school.service.ITeachingArrangeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TeachingArrangeTest extends AppTest{
    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Test
    public void testSelect() {
      List<Long> userIds = iTeachingArrangeService.findTeachIdListByCourseId(6l);
      System.out.println(userIds);
    }


    @Test
    public void testSelectId() {
        List<Long> ids = iTeachingArrangeService.findIdListByCourseId(6l);
        System.out.println(ids);
    }
}
