package cc.mrbird.febs.common.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class StudentTotalScore implements Serializable {
    private Long studentId;
    private Long studentNo;
    private String studentName;
    private Double totalScore;
    private Double totalStudyScore;//总学分
    private Double totalPointScore;//总绩点
    private List<Double> scoreList;
}
