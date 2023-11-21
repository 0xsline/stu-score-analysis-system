package cc.mrbird.febs.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CourseScoreInfo implements Serializable {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程编号
     */
    private Long courseNo;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 得分
     */
    private Double score;

    /**
     * 学分
     */
    private Double studyScore;

    /**
     * 绩点
     */
    private Double pointScore;

    /**
     * 等级
     */
    private Integer degreeScore;

}
