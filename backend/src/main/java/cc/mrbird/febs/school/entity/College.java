package cc.mrbird.febs.school.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("school_college")
@Excel("学院信息表")
public class College extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学院ID
     */
    @TableId(value = "COLLEGE_ID", type = IdType.AUTO)
    @ExcelField(value = "学院ID")
    private Long collegeId;

    /**
     * 学院编号
     */
    @TableField(value = "COLLEGE_NO")
    @ExcelField(value = "学院编号")
    private Long collegeNo;

    /**
     * 学院名称
     */

    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    @TableField("COLLEGE_NAME")
    @ExcelField(value = "学院名称")
    private String collegeName;

    /**
     * 年级数量
     */
    private transient Integer gradeCount = 0;

    /**
     * 教师数量
     */
    private transient Integer teacherCount = 0;

    /**
     * 学生数量
     */
    private transient Integer studentCount = 0;


}
