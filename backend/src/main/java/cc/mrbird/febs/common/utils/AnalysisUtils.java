package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.domain.*;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.school.entity.StudentScore;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisUtils {

    public static AnalysisInfo buildAnalysisInfo(List<StudentScore> studentScoreList, AnalysisInfo analysisInfo) {
        analysisInfo.setTotalNumber(studentScoreList.size());
        Long firstCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.ONE.getCode());
        analysisInfo.setFirstNumber(firstCount.intValue());
        analysisInfo.setFirstRate(firstCount.doubleValue() / analysisInfo.getTotalNumber());
        Long secondCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.SECOND.getCode());
        analysisInfo.setSecondNumber(secondCount.intValue());
        analysisInfo.setSecondRate(secondCount.doubleValue() / analysisInfo.getTotalNumber());
        Long threeCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.THIRD.getCode());
        analysisInfo.setThreeNumber(threeCount.intValue());
        analysisInfo.setThreeRate(threeCount.doubleValue() / analysisInfo.getTotalNumber());
        Long fourCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FOUR.getCode());
        analysisInfo.setFourNumber(fourCount.intValue());
        analysisInfo.setFourRate(fourCount.doubleValue() / analysisInfo.getTotalNumber());
        Long fiveCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FIVE.getCode());
        analysisInfo.setFiveNumber(fiveCount.intValue());
        analysisInfo.setFiveRate(fiveCount.doubleValue() / analysisInfo.getTotalNumber());
        Double totalScore = studentScoreList.stream().mapToDouble(StudentScore::getScore).sum();
        analysisInfo.setTotalScore(totalScore);
        analysisInfo.setAvgScore(totalScore / analysisInfo.getTotalNumber());
        Double totalStudyScore = studentScoreList.stream().mapToDouble(StudentScore::getStudyScore).sum();
        analysisInfo.setTotalStudyScore(totalStudyScore);
        analysisInfo.setAvgStudyScore(totalStudyScore / analysisInfo.getTotalNumber());
        Double totalPointScore = studentScoreList.stream().mapToDouble(StudentScore::getPointScore).sum();
        analysisInfo.setTotalPointScore(totalPointScore);
        analysisInfo.setAvgPointScore(totalPointScore / analysisInfo.getTotalNumber());
        return analysisInfo;
    }

    /**
     * @param studentScoreList
     * @return
     */
    public static CourseAnalysisInfo buildCourseAnalysisInfo(List<StudentScore> studentScoreList) {
        return (CourseAnalysisInfo) buildAnalysisInfo(studentScoreList, new CourseAnalysisInfo());
    }


    /**
     * 构造不同教师的等级柱状图数据
     *
     * @param courseAnalysisInfoList
     * @return
     */
    public static List<List<Object>> buildBarData(List<CourseAnalysisInfo> courseAnalysisInfoList) {
        List<List<Object>> listList = buildData();
        courseAnalysisInfoList.forEach(courseAnalysisInfo -> {
            listList.get(0).add(courseAnalysisInfo.getTeacherName());
            fillData(listList, courseAnalysisInfo);
        });
        return listList;
    }

    public static List<List<Object>> buildBarData3(List<StudentAnalysisInfo> studentAnalysisInfoList) {
        List<List<Object>> listList = buildData();
        studentAnalysisInfoList.forEach(studentAnalysisInfo -> {
            listList.get(0).add(studentAnalysisInfo.getStudentName());
            fillData(listList, studentAnalysisInfo);
        });
        return listList;
    }


    /**
     * @param courseAnalysisInfoList
     * @return
     */
    public static List<Map<String, Object>> buildBarSeries(List<CourseAnalysisInfo> courseAnalysisInfoList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        courseAnalysisInfoList.forEach(courseAnalysisInfo -> {
            mapList.add(JSONUtil.parseObj(String.format(SchoolConstants.BAR_SERIES, courseAnalysisInfo.getTeacherName())));
        });
        return mapList;
    }

    public static List<Map<String, Object>> buildBarSeries3(List<StudentAnalysisInfo> studentAnalysisInfoList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        studentAnalysisInfoList.forEach(studentAnalysisInfo -> {
            mapList.add(JSONUtil.parseObj(String.format(SchoolConstants.BAR_SERIES, studentAnalysisInfo.getStudentName())));
        });
        return mapList;
    }


    public static TeacherAnalysisInfo buildTeacherAnalysisInfo(List<StudentScore> studentScoreList) {
        return (TeacherAnalysisInfo) buildAnalysisInfo(studentScoreList, new TeacherAnalysisInfo());
    }

    public static List<List<Object>> buildData() {
        List<List<Object>> listList = new ArrayList<>();
        List<Object> titleList = new ArrayList<>();
        List<Object> totalList = new ArrayList<>();
        List<Object> firstList = new ArrayList<>();
        List<Object> secondList = new ArrayList<>();
        List<Object> threeList = new ArrayList<>();
        List<Object> fourList = new ArrayList<>();
        List<Object> fiveList = new ArrayList<>();
        titleList.add("等级");
        totalList.add("总数");
        firstList.add("优秀");
        secondList.add("良好");
        threeList.add("一般");
        fourList.add("及格");
        fiveList.add("挂科");
        listList.add(titleList);
        listList.add(totalList);
        listList.add(firstList);
        listList.add(secondList);
        listList.add(threeList);
        listList.add(fourList);
        listList.add(fiveList);
        return listList;
    }

    /**
     * 构造不同教师的等级柱状图数据
     *
     * @param teacherAnalysisInfoList
     * @return
     */
    public static List<List<Object>> buildBarData2(List<TeacherAnalysisInfo> teacherAnalysisInfoList) {
        List<List<Object>> listList = buildData();
        teacherAnalysisInfoList.forEach(teacherAnalysisInfo -> {
            listList.get(0).add(teacherAnalysisInfo.getCourseName());
            fillData(listList, teacherAnalysisInfo);
        });
        return listList;
    }

    public static void fillData(List<List<Object>> listList, AnalysisInfo analysisInfo) {
        listList.get(1).add(analysisInfo.getTotalNumber());
        listList.get(2).add(analysisInfo.getFirstNumber());
        listList.get(3).add(analysisInfo.getSecondNumber());
        listList.get(4).add(analysisInfo.getThreeNumber());
        listList.get(5).add(analysisInfo.getFourNumber());
        listList.get(6).add(analysisInfo.getFiveNumber());
    }

    /**
     * @param teacherAnalysisInfoList
     * @return
     */
    public static List<Map<String, Object>> buildBarSeries2(List<TeacherAnalysisInfo> teacherAnalysisInfoList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        teacherAnalysisInfoList.forEach(teacherAnalysisInfo -> {
            mapList.add(JSONUtil.parseObj(String.format(SchoolConstants.BAR_SERIES, teacherAnalysisInfo.getCourseName())));
        });
        return mapList;
    }

    /**
     * @param studentScoreList
     * @return
     */
    public static StudentAnalysisInfo buildStudentAnalysisInfo(List<StudentScore> studentScoreList) {
        return (StudentAnalysisInfo) buildAnalysisInfo(studentScoreList, new StudentAnalysisInfo());
    }

    /**
     * @param ssList
     * @return
     */
    public static ExamAnalysisInfo buildExamAnalysisInfo(List<StudentScore> ssList) {
        return (ExamAnalysisInfo) buildAnalysisInfo(ssList, new ExamAnalysisInfo());
    }

    /**
     * @param examAnalysisInfoList
     * @return
     */
    public static Object buildBarData4(List<ExamAnalysisInfo> examAnalysisInfoList) {
        List<List<Object>> listList = buildData();
        examAnalysisInfoList.forEach(examAnalysisInfo -> {
            listList.get(0).add(examAnalysisInfo.getExamName());
            fillData(listList, examAnalysisInfo);
        });
        return listList;
    }

    public static Object buildBarSeries4(List<ExamAnalysisInfo> examAnalysisInfoList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        examAnalysisInfoList.forEach(examAnalysisInfo -> {
            mapList.add(JSONUtil.parseObj(String.format(SchoolConstants.BAR_SERIES, examAnalysisInfo.getExamName())));
        });
        return mapList;
    }
}
