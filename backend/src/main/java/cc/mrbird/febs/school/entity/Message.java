package cc.mrbird.febs.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author IU
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_message")
@Excel("留言信息表")
public class Message extends BaseEntity {

    /**
     * 消息ID
     */
    @TableId(value = "MESSAGE_ID", type = IdType.AUTO)
    private Long messageId;

    /**
     * 学生ID
     */
    @TableField("STUDENT_ID")
    private Long studentId;

    /**
     * 学生姓名
     */
    private transient String studentName;

    /**
     * 消息标题
     */
    @TableField("MESSAGE_TITLE")
    private String messageTitle;

    /**
     * 消息内容
     */
    @TableField("MESSAGE_CONTENT")
    private String messageContent;

    /**
     * 消息时间
     */
    @TableField("MESSAGE_TIME")
    private Date messageTime;

    /**
     * 教师ID
     */
    @TableField("TEACHER_ID")
    private Long teacherId;

    /**
     * 教师名称
     */
    private transient String teacherName;

    /**
     * 回复内容
     */
    @TableField("REPLY_CONTENT")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("REPLY_TIME")
    private Date replyTime;

    /**
     * 状态
     */
    @TableField("status")
    private String status;
}
