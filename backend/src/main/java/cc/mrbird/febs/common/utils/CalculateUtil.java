package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.school.entity.StudentScore;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public class CalculateUtil {
    /**
     * 求最大值
     *
     * @param scoreList
     * @return
     */
    public static Double maxScore(List<Double> scoreList) {
        if (CollectionUtils.isEmpty(scoreList)) return 0D;
        Double aDouble = scoreList.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        BigDecimal bg = new BigDecimal(aDouble);
        aDouble = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return aDouble;
    }


    /**
     * 求最大值
     *
     * @param scoreList
     * @return
     */
    public static Double minScore(List<Double> scoreList) {
        if (CollectionUtils.isEmpty(scoreList)) return 0D;
        Double aDouble = scoreList.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        BigDecimal bg = new BigDecimal(aDouble);
        aDouble = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return aDouble;
    }


    /**
     * 求平均分
     *
     * @param scoreList
     * @return
     */
    public static Double avgScore(List<Double> scoreList) {
        if (CollectionUtils.isEmpty(scoreList)) return 0D;
        Double aDouble = scoreList.stream().filter(it -> it != 0).mapToDouble(Double::doubleValue).average().getAsDouble();
        BigDecimal bg = new BigDecimal(aDouble);
        aDouble = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return aDouble;
    }

    /**
     * 及格率
     *
     * @param scoreList
     * @param thresholdScore
     * @return
     */
    public static String passRatio(List<Double> scoreList, Double thresholdScore) {
        if (CollectionUtils.isEmpty(scoreList)) {
            return "100%";
        }
        int count = scoreList.size();
        long passCount = scoreList.stream().filter(it -> (it / thresholdScore >= 0.6)).count();
        double passRate = ((double) passCount / (double) count) * 100;
        BigDecimal bg = new BigDecimal(passRate);
        passRate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return passRate + "%";
    }

    /**
     * 等级数量
     *
     * @param studentScoreList
     * @param degree
     * @return
     */
    public static long degreeCount(List<StudentScore> studentScoreList, int degree) {
        if (CollectionUtils.isEmpty(studentScoreList)) {
            return 0;
        }

        return studentScoreList.stream().filter(studentScore ->
                (studentScore.getDegreeScore() == null && degree == ScoreType.FIVE.getCode()) ||
                        (studentScore.getDegreeScore() != null && studentScore.getDegreeScore() == degree)).count();
    }


    /**
     * 得分掌握的数量
     *
     * @param scoreList
     * @param thresholdScore
     * @return
     */
    public static long masterCount(List<Double> scoreList, Double thresholdScore) {
        if (CollectionUtils.isEmpty(scoreList)) {
            return 0;
        }
        return scoreList.stream().filter(it -> (it / thresholdScore) >= 0.8 && (it / thresholdScore) <= 1.0).count();
    }

    /**
     * 得分一般的数量
     *
     * @param scoreList
     * @param thresholdScore
     * @return
     */
    public static long commonlyCount(List<Double> scoreList, Double thresholdScore) {
        if (CollectionUtils.isEmpty(scoreList)) {
            return 0;
        }
        return scoreList.stream().filter(it -> (it / thresholdScore) >= 0.6 && (it / thresholdScore) < 0.8).count();
    }

    /**
     * 得分未掌握的数量
     *
     * @param scoreList
     * @param thresholdScore
     * @return
     */
    public static long loserCount(List<Double> scoreList, Double thresholdScore) {
        if (CollectionUtils.isEmpty(scoreList)) {
            return 0;
        }
        return scoreList.stream().filter(it -> (it / thresholdScore) < 0.6).count();
    }

}
