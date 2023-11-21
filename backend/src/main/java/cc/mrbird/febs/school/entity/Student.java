package cc.mrbird.febs.school.entity;

import cc.mrbird.febs.common.converter.TimeConverter;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author IU
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school_student")
@Excel("学生信息表")
public class Student extends BaseEntity {
    /**
     * 学生ID
     */
    @TableId(value = "STUDENT_ID", type = IdType.AUTO)
    @ExcelField("学生ID")
    private Long studentId;

    /**
     * 班级ID
     */
    @TableField(value = "CLAZZ_ID")
    @ExcelField("班级ID")
    private Long clazzId;

    /**
     * 关联的班级信息
     */
    @TableField(exist = false)
    private Clazz clazz;

    /**
     * 学生编号
     */
    @TableField(value = "STUDENT_NO")
    @ExcelField("学生编号")
    private Long studentNo;

    /**
     * 学生名称
     */
    @TableField(value = "STUDENT_NAME")
    @ExcelField("学生名称")
    @NotBlank(message = "{required}")
    @Size(max = 100, message = "{noMoreThan}")
    private String studentName;

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
     *
     */
    private transient List<Long> clazzIdList;

    /**
     * 政治面貌
     */
    @TableField(value = "STUDENT_COMMUNITY")
    private String studentCommunity;

    /**
     * 高中毕业学校
     */
    @TableField(value = "HIGH_SCHOOL")
    @ExcelField("高中毕业学校")
    private String highSchool;

    /**
     * 生源地
     */
    @TableField(value = "ORIGIN_OF_STUDENT")
    @ExcelField("生源地")
    private String originOfStudent;


    /**
     * 出生日期
     */
    @TableField(value = "BIRTH_DATE")
    @ExcelField("出生日期")
    private String birthDate;

    /**
     * 父亲姓名
     */
    @TableField(value = "FATHER_NAME")
    private String fatherName;

    /**
     * 父亲电话
     */
    @TableField(value = "FATHER_PHONE")
    private String fatherPhone;

    /**
     * 母亲姓名
     */
    @TableField(value = "MOTHER_NAME")
    private String motherName;

    /**
     * 母亲电话
     */
    @TableField(value = "MOTHER_PHONE")
    private String motherPhone;

    /**
     * 考试次数
     */
    private transient Integer examCount;


    /**
     * 高考总成绩
     */
    @TableField(value = "GAO_KAO_SCORE")
    private Double gaoKaoScore;

    /**
     * 高考语文成绩
     */
    @TableField(value = "CHINESE_SCORE")
    private Double chineseScore;

    /**
     * 高考数学成绩
     */
    @TableField(value = "MATH_SCORE")
    private Double mathScore;

    /**
     * 高考英语成绩
     */
    @TableField(value = "ENGLISH_SCORE")
    private Double englishScore;


    /**
     * 高考理综或者文综成绩
     */
    @TableField(value = "OTHER_SCORE")
    private Double otherScore;

    /**
     * 年龄
     */
    @TableField(exist = false)
    private String age;

}
