package cc.mrbird.febs.common.domain;

import lombok.Data;


@Data
public class StudentAnalysisInfo extends AnalysisInfo {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生编号
     */
    private Long studentNo;

    /**
     * 学生名称
     */
    private String studentName;
}
