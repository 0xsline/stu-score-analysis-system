package cc.mrbird.febs.common.domain;

import lombok.Data;


@Data
public class CourseAnalysisInfo extends AnalysisInfo {

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师编号
     */
    private Long teacherNo;

    /**
     * 教师名称
     */
    private String teacherName;
}
