package cc.mrbird.febs.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnalysisInfo implements Serializable {
    /**
     * 总的样本数
     */
    private Integer totalNumber = 0;

    /**
     * 总分数
     */
    private Double totalScore = 0d;

    /**
     * 总学分
     */
    private Double totalStudyScore = 0d;

    /**
     * 总绩点
     */
    private Double totalPointScore = 0d;

    /**
     * 平均成绩
     */
    private Double avgScore = 0d;

    /**
     * 平均学分
     */
    private Double avgStudyScore = 0d;

    /**
     * 平均绩点
     */
    private Double avgPointScore = 0d;

    /**
     * 优秀数量
     */
    private Integer firstNumber = 0;

    /**
     * 优秀率
     */
    private Double firstRate = 0d;

    /**
     * 良好数量
     */
    private Integer secondNumber = 0;

    /**
     * 良好率
     */
    private Double secondRate = 0d;

    /**
     * 一般数量
     */
    private Integer threeNumber = 0;

    /**
     * 一般率
     */
    private Double threeRate = 0d;

    /**
     * 及格数量
     */
    private Integer fourNumber = 0;

    /**
     * 及格率
     */
    private Double fourRate = 0d;

    /**
     * 挂科数量
     */
    private Integer fiveNumber = 0;

    /**
     * 挂科率
     */
    private Double fiveRate = 0d;
}
