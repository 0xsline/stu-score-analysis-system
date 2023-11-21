package cc.mrbird.febs.school.entity;

import cc.mrbird.febs.common.converter.TimeConverter;
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
import java.util.Date;

/**
 * @author IU
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_teacher")
@Excel("教师信息表")
public class Teacher extends BaseEntity {
    /**
     * 教师ID
     */
    @TableId(value = "TEACHER_ID", type = IdType.AUTO)
    private Long teacherId;

    /**
     * 学院ID
     */
    @TableField(value = "COLLEGE_ID")
    private Long collegeId;

    @ExcelField("学院名称")
    private transient String collegeName;

    /**
     * 关联的学院信息
     */
    @TableField(exist = false)
    private College college;

    /**
     * 教师编号
     */
    @TableField(value = "TEACHER_NO")
    @ExcelField("教师编号")
    private Long teacherNo;

    /**
     * 教师名称
     */
    @TableField(value = "TEACHER_NAME")
    @ExcelField("教师名称")
    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    private String teacherName;

    /**
     * 关联绑定的用户ID
     */
    @TableField(value = "USER_ID")
    private Long userId;

    /**
     * 用户名（账号）
     */
    @ExcelField("用户名（账号）")
    private transient String username;

    /**
     * 密码
     */
    private transient String password;

    /**
     * 邮箱
     */
    @ExcelField("邮箱")
    private transient String email;

    /**
     * 手机号
     */
    @ExcelField("手机号")
    private transient String mobile;

    /**
     * 状态
     */
    @ExcelField(value = "状态", writeConverterExp = "0=锁定,1=有效")
    private transient String status;

    /**
     * 性别
     */
    @ExcelField(value = "性别", writeConverterExp = "0=男,1=女,2=保密")
    private transient String ssex;

    /**
     * 描述
     */
    @ExcelField("描述")
    private transient String description;

    /**
     * 头像
     */
    private transient String avatar;

    /**
     * 用户类型
     */
    private transient Integer userType;

    /**
     * 用户身份证号
     */
    @ExcelField("身份证号")
    private transient String identityCard;

    /**
     * 部门ID
     */
    private transient Long deptId;

    /**
     * 最后登录时间
     */
    @ExcelField(value = "最后登录时间", writeConverter = TimeConverter.class)
    private transient Date lastLoginTime;

    /**
     * 职称
     */
    @TableField(value = "TEACHER_ACADEMIC")
    private String teacherAcademic;

    /**
     * 毕业高校
     */
    @TableField(value = "HIGH_SCHOOL")
    @ExcelField("毕业高校")
    private String highSchool;

    /**
     * 生源地
     */
    @TableField(value = "ORIGIN_OF_TEACHER")
    @ExcelField("生源地")
    private String originOfTeacher;


    /**
     * 出生日期
     */
    @TableField(value = "BIRTH_DATE")
    @ExcelField("出生日期")
    private String birthDate;

    /**
     * 考试次数
     */
    private transient Integer examCount = 0;

    /**
     * 年龄
     */
    @TableField(exist = false)
    private String age;
}
