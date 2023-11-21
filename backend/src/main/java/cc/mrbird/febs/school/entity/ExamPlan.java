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
@TableName("school_exam_plan")
@Excel("考试计划信息表")
public class ExamPlan extends BaseEntity {

    /**
     * 考试计划ID
     */
    @TableId(value = "EXAM_PLAN_ID", type = IdType.AUTO)
    @ExcelField("考试计划ID")
    private Long examPlanId;

    /**
     * 考试ID
     */
    @TableField(value = "EXAM_ID")
    @ExcelField("考试ID")
    private Long examId;

    /**
     * 考试详情
     */
    @TableField(exist = false)
    private Exam exam;

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
     * 考试计划编号
     */
    @TableField(value = "EXAM_PLAN_NO")
    @ExcelField("考试计划编号")
    private Long examPlanNo;

    /**
     * 考试计划名称
     */
    @TableField(value = "EXAM_PLAN_NAME")
    @ExcelField("考试计划名称")
    private String examPlanName;

    /**
     * 学分
     */
    @TableField(value = "STUDY_SCORE")
    @ExcelField("学分")
    private Float studyScore;

    /**
     * 满分
     */
    @TableField(value = "EXAM_SCORE")
    @ExcelField("满分")
    private Double examScore;

    /**
     * 考试计划类型
     */
    @TableField(value = "EXAM_PLAN_TYPE")
    @ExcelField("考试计划类型")
    private Integer examPlanType;
}
