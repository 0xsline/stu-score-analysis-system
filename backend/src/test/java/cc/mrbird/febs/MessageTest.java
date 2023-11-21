package cc.mrbird.febs;

import cc.mrbird.febs.school.entity.Message;
import cc.mrbird.febs.school.service.IMessageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MessageTest extends AppTest{

    @Autowired
    private IMessageService iMessageService;

    @Test
    public void testInsert() throws Exception {
        Message message = new Message();
        message.setMessageTitle("老师你好，什么时候放假？");
        message.setMessageContent("请教老师，学校什么时候放假");
        message.setMessageTime(new Date());
        message.setStatus("0");
        message.setStudentId(25l);
        message.setTeacherId(8l);
        iMessageService.insert(message);
    }
}
