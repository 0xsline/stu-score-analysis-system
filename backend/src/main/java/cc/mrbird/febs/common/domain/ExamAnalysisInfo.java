package cc.mrbird.febs.common.domain;

import lombok.Data;

@Data
public class ExamAnalysisInfo extends AnalysisInfo {

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 考试编号
     */
    private Long examNo;

    /**
     * 考试编号
     */
    private String examName;
}
