package cc.mrbird.febs.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeacherAnalysisInfo extends AnalysisInfo {

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


}
