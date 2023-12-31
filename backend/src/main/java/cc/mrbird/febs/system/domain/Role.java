package cc.mrbird.febs.system.domain;

import cc.mrbird.febs.common.converter.DataScopeReadConverter;
import cc.mrbird.febs.common.converter.DataScopeWriteConverter;
import cc.mrbird.febs.common.converter.TimeConverter;
import cc.mrbird.febs.common.options.DataScopeOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_role")
@Excel("角色信息表")
public class Role implements Serializable {

    private static final long serialVersionUID = -1714476694755654924L;

    @TableId(value = "ROLE_ID", type = IdType.AUTO)
    private Long roleId;

    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    @ExcelField(value = "角色名称")
    private String roleName;

    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "角色描述")
    private String remark;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @ExcelField(value = "修改时间", writeConverter = TimeConverter.class)
    private Date modifyTime;

    private transient String createTimeFrom;
    private transient String createTimeTo;
    private transient String menuId;
    @NotNull(message = "{required}")
    @ExcelField(value = "数据范围", writeConverter = DataScopeWriteConverter.class,readConverter = DataScopeReadConverter.class,options = DataScopeOptions.class )
    private Integer dataScope;

    /**
     * 角色类型（0：系统角色，1：教师角色，2：学生角色）
     */
    private Integer roleType;
}