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
@TableName("school_semester")
@Excel("学期信息表")
public class Semester extends BaseEntity {

    /**
     * 学期ID
     */
    @TableId(value = "SEMESTER_ID", type = IdType.AUTO)
    @ExcelField("学期ID")
    private Long semesterId;

    /**
     * 学期编号
     */
    @TableField(value = "SEMESTER_NO")
    @ExcelField("学期编号")
    private Long semesterNo;

    /**
     * 学期名称
     */
    @TableField(value = "SEMESTER_NAME")
    @ExcelField("学期名称")
    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    private String semesterName;

    /**
     * 顺序序号
     */
    @TableField(value = "SEMESTER_SEQUENCE")
    @ExcelField("顺序序号")
    private Integer semesterSequence;
}
