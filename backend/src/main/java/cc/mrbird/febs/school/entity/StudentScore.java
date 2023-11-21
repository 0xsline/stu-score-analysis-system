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
@TableName("school_student_score")
@Excel("考试成绩信息表")
public class StudentScore extends BaseEntity {

    /**
     * 考试成绩ID
     */
    @TableId(value = "STUDENT_SCORE_ID", type = IdType.AUTO)
    @ExcelField("考试成绩ID")
    private Long studentScoreId;

    /**
     * 教学安排ID
     */
    @TableField(value = "TEACHING_ARRANGE_ID")
    @ExcelField("教学安排ID")
    private Long teachingArrangeId;

    /**
     * 考试ID
     */
    @TableField(value = "EXAM_ID")
    private Long examId;

    /**
     * 学生ID
     */
    @TableField(value = "STUDENT_ID")
    @ExcelField("学生ID")
    private Long studentId;

    /**
     * 学生信息
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 分数
     */
    @TableField(value = "SCORE")
    @ExcelField("分数")
    private Double score;

    /**
     * 得分和满分比值
     */
    @TableField(exist = false)
    private Double rate;

    /**
     * 学分
     */
    @TableField("STUDY_SCORE")
    private Double studyScore;


    /**
     * 绩点
     */
    @TableField("POINT_SCORE")
    private Double pointScore;

    /**
     * 成绩等级
     */
    @TableField("DEGREE_SCORE")
    private Integer degreeScore;
}
