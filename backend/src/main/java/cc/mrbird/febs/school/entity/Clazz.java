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
@TableName("school_clazz")
@Excel("班级信息表")
public class Clazz extends BaseEntity {

    /**
     * 班级ID
     */
    @TableId(value = "CLAZZ_ID", type = IdType.AUTO)
    @ExcelField("班级ID")
    private Long clazzId;

    /**
     * 年级ID
     */
    @TableField(value = "GRADE_ID")
    @ExcelField("年级ID")
    private Long gradeId;

    /**
     * 年级信息
     */
    @TableField(exist = false)
    private Grade grade;

    /**
     * 班级编号
     */
    @TableField(value = "CLAZZ_NO")
    @ExcelField("班级编号")
    private Long clazzNo;

    /**
     * 班级名称
     */
    @TableField(value = "CLAZZ_NAME")
    @ExcelField("班级名称")
    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    private String clazzName;

    /**
     * 班主任ID
     */
    @TableField(value = "MANAGER_ID")
    private Long managerId;

    /**
     * 班主任名称
     */
    private transient String managerName;

    /**
     * 班主任信息
     */
    private transient Teacher teacher;

    /**
     * 班级下学生数量
     */
    private transient Integer studentCount = 0;
}
