package cc.mrbird.febs.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_course")
@Excel("课程信息表")
public class Course extends BaseEntity {

    /**
     * 课程ID
     */
    @TableId(value = "COURSE_ID", type = IdType.AUTO)
    @ExcelField("课程ID")
    private Long courseId;

    /**
     * 课程编号
     */
    @TableField(value = "COURSE_NO")
    @ExcelField("课程编号")
    private Long courseNo;

    /**
     * 课程名称
     */
    @TableField(value = "COURSE_NAME")
    @ExcelField("课程名称")
    private String courseName;

    /**
     * 课程学分
     */
    @TableField(value = "STUDY_SCORE")
    @ExcelField("学分")
    private Float studyScore;

    /**
     * 课程满分
     */
    @TableField(value = "EXAM_SCORE")
    @ExcelField("满分")
    private Double examScore;

    /**
     * 课程类型
     */
    @TableField(value = "COURSE_TYPE")
    @ExcelField(value = "课程类型", writeConverterExp = "0=必修课,1=选修课")
    private String courseType;


    private transient Integer teachingArrangeCount = 0;

    private transient Integer teacherCount = 0;
}
