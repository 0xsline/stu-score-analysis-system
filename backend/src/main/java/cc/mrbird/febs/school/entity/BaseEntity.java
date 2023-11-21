package cc.mrbird.febs.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
public class BaseEntity implements Serializable {


    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("MODIFY_TIME")
    private LocalDateTime modifyTime;

    /**
     * 创建人ID
     */
    @TableField("CREATE_USER_ID")
    private Long createUserId;

    /**
     * 创建人姓名
     */
    @TableField("CREATE_USERNAME")
    private String createUsername;

    /**
     * 更新人ID
     */
    @TableField("MODIFY_USER_ID")
    private Long modifyUserId;

    /**
     * 更新人姓名
     */
    @TableField("MODIFY_USERNAME")
    private String modifyUsername;

    @TableField(exist = false)
    private String createTimeFrom;

    @TableField(exist = false)
    private String createTimeTo;

}
