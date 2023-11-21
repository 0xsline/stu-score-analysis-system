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
@TableName("school_exam")
@Excel("考试信息表")
public class Exam extends BaseEntity {

    /**
     * 考试ID
     */
    @TableId(value = "EXAM_ID", type = IdType.AUTO)
    @ExcelField("考试ID")
    private Long examId;

    /**
     * 考试编号
     */
    @TableField(value = "EXAM_NO")
    @ExcelField("考试编号")
    private Long examNo;

    /**
     * 考试名称
     */
    @TableField(value = "EXAM_NAME")
    @ExcelField("考试名称")
    private String examName;

    @TableField(value = "EXAM_TYPE")
    @ExcelField(value = "考试类型", writeConverterExp = "0=常规,1=默认")
    private String examType;

    /**
     * 排列序号
     */
    @TableField(value = "EXAM_SEQUENCE")
    @ExcelField("排列序号")
    private Integer examSequence;
}
