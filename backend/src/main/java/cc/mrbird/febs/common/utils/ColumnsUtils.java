package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.domain.Column;
import cc.mrbird.febs.school.entity.Exam;
import cc.mrbird.febs.school.entity.ExamPlan;
import cc.mrbird.febs.school.entity.TeachingArrange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnsUtils {

    public static List<Column> builderTotalScoreColumns(List<ExamPlan> examPlanList) {
        List<Column> columnList = new ArrayList<>();
        columnList.add(new Column("名次", "index", null, null));
        columnList.add(new Column("姓名", "studentName", null, null));
        for (int i = 0; i < examPlanList.size(); i++) {
            ExamPlan examPlan = examPlanList.get(i);
            columnList.add(new Column(examPlan.getCourse().getCourseName(), "EPI"
                    + examPlan.getExamPlanId(), null, null));
        }
        columnList.add(new Column("总分", "totalScore", null, null));
        return columnList;
    }


    public static List<Column> builderTotalScoreColumns2(List<TeachingArrange> teachingArrangeList) {
        List<Column> columnList = new ArrayList<>();
        columnList.add(new Column("名次", "index", null, null));
        columnList.add(new Column("姓名", "studentName", null, null));
        for (int i = 0; i < teachingArrangeList.size(); i++) {
            TeachingArrange teachingArrange = teachingArrangeList.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("customRender", "name");
            columnList.add(new Column(teachingArrange.getCourse().getCourseName(),
                    "EPI" + teachingArrange.getTeachingArrangeId(), map, null));
        }
        columnList.add(new Column("总学分", "totalStudyScore", null, null));
        columnList.add(new Column("总绩点", "totalPointScore", null, null));
        columnList.add(new Column("总成绩", "totalScore", null, null));
        return columnList;
    }


    public static List<Column> builderTotalScoreColumns3(List<Exam> examList) {
        List<Column> columnList = new ArrayList<>();
        columnList.add(new Column("名次", "index", null, null));
        columnList.add(new Column("学号", "studentNo", null, null));
        columnList.add(new Column("姓名", "studentName", null, null));
        for (int i = 0; i < examList.size(); i++) {
            Exam exam = examList.get(i);
            Column column = new Column(exam.getExamName(), null, null, null);
            Map<String, String> map1= new HashMap<>();
            map1.put("customRender", "name");

            Map<String, String> map = new HashMap<>();
            map.put("customRender", "degreeScore");
            column.setChildren(new ArrayList<>());
            column.getChildren().add(new Column("成绩", "EPI" + exam.getExamId() + "Score", map1, null));
            column.getChildren().add(new Column("学分", "EPI" + exam.getExamId() + "StudyScore", null, null));
            column.getChildren().add(new Column("绩点", "EPI" + exam.getExamId() + "PointScore", null, null));
            column.getChildren().add(new Column("等级", "EPI" + exam.getExamId() + "degreeScore", map, null));
            columnList.add(column);
        }
        columnList.add(new Column("总学分", "totalStudyScore", null, null));
        columnList.add(new Column("总绩点", "totalPointScore", null, null));
        columnList.add(new Column("总成绩", "totalScore", null, null));
        return columnList;
    }

    public static List<Column> builderTotalScoreColumns4(List<Exam> examList) {
        List<Column> columnList = new ArrayList<>();
        columnList.add(new Column("教学安排ID", "teachingArrangeId", null, null));
        columnList.add(new Column("学期", "semesterName", null, null));
        columnList.add(new Column("课程ID", "courseId", null, null));
        columnList.add(new Column("课程编号", "courseNo", null, null));
        columnList.add(new Column("课程名称", "courseName", null, null));
        for (int i = 0; i < examList.size(); i++) {
            Exam exam = examList.get(i);
            Column column = new Column(exam.getExamName(), null, null, null);
            Map<String, String> map1= new HashMap<>();
            map1.put("customRender", "name");

            Map<String, String> map = new HashMap<>();
            map.put("customRender", "degreeScore");
            column.setChildren(new ArrayList<>());
            column.getChildren().add(new Column("成绩", "EPI" + exam.getExamId() + "Score", map1, null));
            column.getChildren().add(new Column("学分", "EPI" + exam.getExamId() + "StudyScore", null, null));
            column.getChildren().add(new Column("绩点", "EPI" + exam.getExamId() + "PointScore", null, null));
            column.getChildren().add(new Column("等级", "EPI" + exam.getExamId() + "degreeScore", map, null));
            columnList.add(column);
        }
        columnList.add(new Column("总学分", "totalStudyScore", null, null));
        columnList.add(new Column("总绩点", "totalPointScore", null, null));
        columnList.add(new Column("总成绩", "totalScore", null, null));
        return columnList;
    }
}
