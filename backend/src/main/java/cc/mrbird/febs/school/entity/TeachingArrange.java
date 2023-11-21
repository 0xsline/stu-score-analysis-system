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

/**
 * @author IU
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_teaching_arrange")
@Excel("教学安排信息表")
public class TeachingArrange extends BaseEntity {

    /**
     * 教学计划ID
     */
    @TableId(value = "TEACHING_ARRANGE_ID", type = IdType.AUTO)
    @ExcelField("教学计划ID")
    private Long teachingArrangeId;

    /**
     * 班级ID
     */
    @TableField(value = "CLAZZ_ID")
    @ExcelField("班级ID")
    private Long clazzId;

    /**
     * 班级
     */
    @TableField(exist = false)
    private Clazz clazz;

    /**
     * 学期ID
     */
    @TableField(value = "SEMESTER_ID")
    @ExcelField("学期ID")
    private Long semesterId;

    /**
     * 学期
     */
    @TableField(exist = false)
    private Semester semester;

    /**
     * 课程ID
     */
    @TableField(value = "COURSE_ID")
    @ExcelField("课程ID")
    private Long courseId;

    /**
     * 课程
     */
    @TableField(exist = false)
    private Course course;

    /**
     * 教师ID
     */
    @TableField(value = "TEACHER_ID")
    @ExcelField("教师ID")
    private Long teacherId;

    /**
     * 教师
     */
    @TableField(exist = false)
    private Teacher teacher;

    /**
     * 哪一次考试
     */
    private transient Long examId;

    /**
     * 学生数量
     */
    private transient Long studentCount = 0l;

    /**
     * 挂科学生数量
     */
    private transient Long failStudentCount = 0l;
}
