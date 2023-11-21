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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author IU
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_grade")
@Excel("年级信息表")
public class Grade extends BaseEntity {


    /**
     * 年级ID
     */
    @TableId(value = "GRADE_ID", type = IdType.AUTO)
    @ExcelField("年级ID")
    private Long gradeId;

    /**
     * 学院ID
     */
    @TableField(value = "COLLEGE_ID")
    @ExcelField("学院ID")
    private Long collegeId;

    /**
     * 关联的学院信息
     */
    @TableField(exist = false)
    private College college;

    /**
     * 年级编号
     */
    @TableField(value = "GRADE_NO")
    @ExcelField("年级编号")
    private Long gradeNo;

    /**
     * 年级名称
     */
    @TableField(value = "GRADE_NAME")
    @ExcelField("年级名称")
    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    private String gradeName;

    /**
     * 年级下班级数量
     */
    private transient Integer clazzCount = 0;

    /**
     * 年级下学生数量
     */
    private transient Integer studentCount = 0;
}
