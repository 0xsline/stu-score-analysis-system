package cc.mrbird.febs.common.domain;

import cc.mrbird.febs.school.entity.TeachingArrange;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class TeachingArrangeTotalScore implements Serializable {
    private Long teachingArrangeId;
    private TeachingArrange teachingArrange;
    private Double totalScore;
    private Double totalStudyScore;//总学分
    private Double totalPointScore;//总绩点

}
