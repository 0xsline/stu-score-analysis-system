package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.*;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.*;
import cc.mrbird.febs.school.dao.StudentScoreMapper;
import cc.mrbird.febs.school.entity.*;
import cc.mrbird.febs.school.service.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StudentScoreServiceImpl extends ServiceImpl<StudentScoreMapper, StudentScore> implements IStudentScoreService {

    @Autowired
    private StudentScoreMapper studentScoreMapper;

    @Autowired
    private ITeachingArrangeService iTeachingArrangeService;

    @Autowired
    private IStudentScoreService iStudentScoreService;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private IClazzService iClazzService;

    @Autowired
    private IGradeService iGradeService;

    @Autowired
    private ICollegeService iCollegeService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private ISemesterService iSemesterService;

    @Autowired
    private IExamService iExamService;

    @Override
    @CustomerInsert
    public boolean insert(StudentScore entity) throws FebsException {
        return studentScoreMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(StudentScore entity) throws FebsException {
        return studentScoreMapper.updateById(entity) > 0;
    }

    @Override
    public List<StudentScore> findStudentScoreByTeachingArrangeId(Long teachingArrangeId, Long examId) {
        TeachingArrange teachingArrange = iTeachingArrangeService.getById(teachingArrangeId);
        Course course = iCourseService.getById(teachingArrange.getCourseId());
        List<StudentScore> studentScoreList = studentScoreMapper.selectList(new LambdaQueryWrapper<StudentScore>()
                .eq(StudentScore::getTeachingArrangeId, teachingArrangeId)
                .eq(StudentScore::getExamId, examId));
        Map<Long, StudentScore> longExamScoreMap = studentScoreList.stream().collect(Collectors.toMap(StudentScore::getStudentId, it -> it));
        List<Student> studentList = iStudentService.findByClazzId(teachingArrange.getClazzId());
        return studentList.stream().map(student -> {
            StudentScore studentScore = new StudentScore();
            studentScore.setTeachingArrangeId(teachingArrangeId);
            studentScore.setExamId(examId);
            studentScore.setStudentId(student.getStudentId());
            studentScore.setScore(0d);
            studentScore.setStudent(student);
            StudentScore db = longExamScoreMap.get(student.getStudentId());
            if (db != null) {
                studentScore.setScore(db.getScore());
                //学分
                studentScore.setStudyScore(db.getStudyScore());
                //绩点
                studentScore.setPointScore(db.getPointScore());
                //等级
                studentScore.setDegreeScore(db.getDegreeScore());
            }

            studentScore.setRate(studentScore.getScore() / course.getExamScore());
            return studentScore;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findStudentScoreMapByTeachingArrangeId(Long teachingArrangeId, Long examId) {
        TeachingArrange teachingArrange = iTeachingArrangeService.getById(teachingArrangeId);
        Course course = iCourseService.getById(teachingArrange.getCourseId());
        Map<String, Object> map = new HashMap<>();
        List<StudentScore> studentScoreList = findStudentScoreByTeachingArrangeId(teachingArrangeId, examId);
        map.put("teachingArrange", teachingArrange);
        map.put("course", course);
        //构造统计数据
        buildTotalAnalysisData(map, studentScoreList, course);
        map.put("studentScoreList", studentScoreList);
        map.put("sortedStudentScoreList", studentScoreList.stream()
                .sorted(Comparator.comparingDouble(StudentScore::getScore)
                        .reversed()).collect(Collectors.toList()));
        //等级数量
        buildDegreeCount(map, studentScoreList);
        return map;
    }

    /**
     * 构造统计数据
     *
     * @param map
     * @param studentScoreList
     */
    private void buildTotalAnalysisData(Map<String, Object> map, List<StudentScore> studentScoreList, Course course) {
        List<Double> scoreList = studentScoreList.stream().map(StudentScore::getScore).collect(Collectors.toList());
        Double maxScore = CalculateUtil.maxScore(scoreList);
        Double minScore = CalculateUtil.minScore(scoreList);
        Double avgScore = CalculateUtil.avgScore(scoreList);
        String passRatio = CalculateUtil.passRatio(scoreList, course.getExamScore());
        long masterCount = CalculateUtil.masterCount(scoreList, course.getExamScore());
        long commonlyCount = CalculateUtil.commonlyCount(scoreList, course.getExamScore());
        long loserCount = CalculateUtil.loserCount(scoreList, course.getExamScore());
        map.put("maxScore", maxScore);
        map.put("minScore", minScore);
        map.put("avgScore", avgScore);
        map.put("passRatio", passRatio);
        map.put("masterCount", masterCount);
        map.put("commonlyCount", commonlyCount);
        map.put("loserCount", loserCount);
        map.put("histogramData", HistogramUtils.buildHistogram(course.getExamScore(), scoreList));
        map.put("lineTitleArray", HistogramUtils.buildLineTitle(course.getExamScore()));
    }

    /**
     * 构造等级数据
     *
     * @param map
     * @param studentScoreList
     */
    private void buildDegreeCount(Map<String, Object> map, List<StudentScore> studentScoreList) {
        long firstCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.ONE.getCode());
        long secondCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.SECOND.getCode());
        long thirdCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.THIRD.getCode());
        long fourCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FOUR.getCode());
        long fiveCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FIVE.getCode());
        map.put("firstCount", firstCount);
        map.put("secondCount", secondCount);
        map.put("thirdCount", thirdCount);
        map.put("fourCount", fourCount);
        map.put("fiveCount", fiveCount);
    }

    @Override
    public Map<String, Object> findStudentScoreMapByTeachingArrangeId(Long teachingArrangeId) {
        TeachingArrange teachingArrange = iTeachingArrangeService.getById(teachingArrangeId);
        Course course = iCourseService.getById(teachingArrange.getCourseId());
        List<Long> examIdList = iStudentScoreService.findExamIdListByTeachingArrangeId(teachingArrangeId);
        List<Student> studentList = iStudentService.findByClazzId(teachingArrange.getClazzId());
        //成绩列表
        List<StudentScore> studentScoreList = iStudentScoreService.findListByTeachingArrangeId(teachingArrangeId);

        Map<String, StudentScore> stringStudentScoreMap = studentScoreList.stream().collect(Collectors.toMap(studentScore ->
                studentScore.getStudentId() + "_" + studentScore.getExamId(), it -> it));
        //考试列表
        List<Exam> examList = new ArrayList<>(iExamService.listByIds(examIdList));
        List<List<Object>> objListList = buildEChartsSource(examList, studentList, studentScoreList);
        List<Map<String, Object>> seriesList = buildEchartsSeries(examList);
        Map<String, Object> map = new HashMap<>();
        map.put("objListList", objListList);
        map.put("seriesList", seriesList);
        map.put("course", course);
        buildTotalAnalysisData(map, studentScoreList, course);
        buildDegreeCount(map, studentScoreList);
        List<StudentTotalScore> studentTotalScoreList = this.findStudentTotalScoreByTeachingArrangeId(teachingArrangeId);
        Map<Long, Student> studentMap = studentList.stream().collect(Collectors.toMap(Student::getStudentId, it -> it));
        studentTotalScoreList.forEach(studentTotalScore -> {
            Student student = studentMap.get(studentTotalScore.getStudentId());
            studentTotalScore.setStudentName(student.getStudentName());
            studentTotalScore.setStudentNo(student.getStudentNo());
        });

        map.put("studentTotalScoreList", studentTotalScoreList);
        //表格表头
        map.put("columns", ColumnsUtils.builderTotalScoreColumns3(examList));

        List<Map<String, Object>> mapList = studentTotalScoreList.stream().sorted(Comparator
                .comparingDouble(StudentTotalScore::getTotalScore).reversed()).map(studentTotalScore -> {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("studentId", studentTotalScore.getStudentId());
            objectMap.put("studentNo", studentTotalScore.getStudentNo());
            objectMap.put("studentName", studentTotalScore.getStudentName());
            objectMap.put("totalScore", studentTotalScore.getTotalScore());
            objectMap.put("totalStudyScore", studentTotalScore.getTotalStudyScore());
            objectMap.put("totalPointScore", studentTotalScore.getTotalPointScore());
            examList.forEach(exam -> {
                StudentScore studentScore = stringStudentScoreMap.get(studentTotalScore.getStudentId() + "_" + exam.getExamId());
                if (studentScore != null) {
                    if (studentScore.getScore() / course.getExamScore() < 0.6) {
                        objectMap.put("EPI" + exam.getExamId() + "Score", "<span style='color: white;" +
                                "background-color: #f5222d;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentScore.getScore() + "</span>");
                    } else if (studentScore.getScore() / course.getExamScore() >= 0.9) {
                        objectMap.put("EPI" + exam.getExamId() + "Score", "<span style='color: white;" +
                                "background-color: #42b983;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentScore.getScore() + "</span>");
                    } else {
                        objectMap.put("EPI" + exam.getExamId() + "Score", studentScore.getScore());
                    }

                    objectMap.put("EPI" + exam.getExamId() + "StudyScore", studentScore.getStudyScore());
                    objectMap.put("EPI" + exam.getExamId() + "PointScore", studentScore.getPointScore());
                    objectMap.put("EPI" + exam.getExamId() + "degreeScore", studentScore.getDegreeScore());
                }
//                else {
//                    objectMap.put("EPI" + exam.getExamId() + "Score", 0);
//                    objectMap.put("EPI" + exam.getExamId() + "StudyScore", 0);
//                    objectMap.put("EPI" + exam.getExamId() + "PointScore", 0);
//                    objectMap.put("EPI" + exam.getExamId() + "degreeScore", 0);
//                }
            });

            return objectMap;
        }).collect(Collectors.toList());

        for (int i = 0; i < mapList.size(); i++) {
            mapList.get(i).put("index", i + 1);
        }
        map.put("dataSource", mapList);
        return map;
    }

    @Override
    public Map<String, Object> findStudentScoreMapByStudentId(Long studentId) {
        Map<String, Object> map = new HashMap<>();
        //成绩列表
        List<StudentScore> studentScoreList = iStudentScoreService.findListByStudentId(studentId);
        List<Long> examIdList = studentScoreList.stream().map(StudentScore::getExamId)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(examIdList))
            return null;
        //考试列表
        List<Exam> examList = new ArrayList<>(iExamService.listByIds(examIdList));
        List<Long> teachingArrangeIdList = studentScoreList.stream().map(StudentScore::getTeachingArrangeId)
                .distinct().collect(Collectors.toList());
        List<TeachingArrange> teachingArrangeList = new ArrayList<>(iTeachingArrangeService
                .listByIds(teachingArrangeIdList));
        iTeachingArrangeService.teachingArrangeFillCourse(teachingArrangeList);
        iTeachingArrangeService.teachingArrangeFillSemester(teachingArrangeList);
        Map<Long, TeachingArrange> longTeachingArrangeMap = teachingArrangeList.stream()
                .collect(Collectors.toMap(TeachingArrange::getTeachingArrangeId, it -> it));
        List<TeachingArrangeTotalScore> teachingArrangeTotalScoreList = this.findTeachingArrangeTotalScoreByStudentId(studentId);
        teachingArrangeTotalScoreList.forEach(teachingArrangeTotalScore -> {
            TeachingArrange teachingArrange = longTeachingArrangeMap.get(teachingArrangeTotalScore.getTeachingArrangeId());
            teachingArrangeTotalScore.setTeachingArrange(teachingArrange);
        });

        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, StudentScore> stringStudentScoreMap = studentScoreList.stream()
                .collect(Collectors.toMap(studentScore ->
                        studentScore.getTeachingArrangeId() + "_" + studentScore.getExamId(), it -> it));
        teachingArrangeTotalScoreList.forEach(teachingArrangeTotalScore -> {
            Course course = teachingArrangeTotalScore.getTeachingArrange().getCourse();
            Semester semester = teachingArrangeTotalScore.getTeachingArrange().getSemester();
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("teachingArrangeId", teachingArrangeTotalScore.getTeachingArrangeId());
            objectMap.put("semesterName", semester.getSemesterName());
            objectMap.put("courseId", course.getCourseId());
            objectMap.put("courseNo", course.getCourseNo());
            objectMap.put("courseName", course.getCourseName());
            objectMap.put("totalScore", teachingArrangeTotalScore.getTotalScore());
            objectMap.put("totalStudyScore", teachingArrangeTotalScore.getTotalStudyScore());
            objectMap.put("totalPointScore", teachingArrangeTotalScore.getTotalPointScore());
            examList.forEach(exam -> {
                StudentScore studentScore = stringStudentScoreMap.get(teachingArrangeTotalScore.getTeachingArrangeId()
                        + "_" + exam.getExamId());
                if (studentScore != null) {
                    if (studentScore.getScore() / course.getExamScore() < 0.6) {
                        objectMap.put("EPI" + exam.getExamId() + "Score", "<span style='color: white;" +
                                "background-color: #f5222d;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentScore.getScore() + "</span>");
                    } else if (studentScore.getScore() / course.getExamScore() >= 0.9) {
                        objectMap.put("EPI" + exam.getExamId() + "Score", "<span style='color: white;" +
                                "background-color: #42b983;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentScore.getScore() + "</span>");
                    } else {
                        objectMap.put("EPI" + exam.getExamId() + "Score", studentScore.getScore());
                    }

                    objectMap.put("EPI" + exam.getExamId() + "StudyScore", studentScore.getStudyScore());
                    objectMap.put("EPI" + exam.getExamId() + "PointScore", studentScore.getPointScore());
                    objectMap.put("EPI" + exam.getExamId() + "degreeScore", studentScore.getDegreeScore());
                }
            });
            mapList.add(objectMap);
        });
        buildDegreeCount(map, studentScoreList);
        map.put("columns", ColumnsUtils.builderTotalScoreColumns4(examList));
        map.put("dataSource", mapList);

        List<ExamAnalysisInfo> examAnalysisInfoList = new ArrayList<>(examList.size());

        examList.forEach(exam -> {
            List<StudentScore> ssList = new ArrayList<>(iStudentScoreService.findListByStudentIdAndExamId(studentId, exam.getExamId()));
            ExamAnalysisInfo examAnalysisInfo = AnalysisUtils.buildExamAnalysisInfo(ssList);
            examAnalysisInfo.setExamId(exam.getExamId());
            examAnalysisInfo.setExamNo(exam.getExamNo());
            examAnalysisInfo.setExamName(exam.getExamName());
            examAnalysisInfoList.add(examAnalysisInfo);
        });

        map.put("examDataSource", examAnalysisInfoList);
        map.put("barSource", AnalysisUtils.buildBarData4(examAnalysisInfoList));
        map.put("barSeries", AnalysisUtils.buildBarSeries4(examAnalysisInfoList));
        return map;
    }

    private List<Map<String, Object>> buildEchartsSeries(List<Exam> examList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        examList.forEach(exam -> {
            mapList.add(JSONUtil.parseObj(String.format(SchoolConstants.LINE_SERIES, exam.getExamName())));
        });
        return mapList;
    }

    /**
     * 构造echarts数据
     * [
     * ['product', '2015', '2016', '2017'],
     * ['Matcha Latte', 43.3, 85.8, 93.7],
     * ['Milk Tea', 83.1, 73.4, 55.1],
     * ['Cheese Cocoa', 86.4, 65.2, 82.5],
     * ['Walnut Brownie', 72.4, 53.9, 39.1]
     * ]
     *
     * @param examList
     * @param studentList
     * @param studentScoreList
     * @return
     */
    private List<List<Object>> buildEChartsSource(List<Exam> examList, List<Student> studentList, List<StudentScore> studentScoreList) {
        Map<String, StudentScore> stringStudentScoreMap = studentScoreList.stream().collect(Collectors.toMap(studentScore ->
                studentScore.getStudentId() + "_" + studentScore.getExamId(), it -> it));
        List<List<Object>> result = new ArrayList<>();
        //构造第一行数据即是标题行
        List<Object> titleList = new ArrayList<>();
        titleList.add("姓名");
        examList.forEach(exam -> titleList.add(exam.getExamName()));
        result.add(titleList);
        studentList.forEach(student -> {
            List<Object> rowList = new ArrayList<>();
            rowList.add(student.getStudentName());
            examList.forEach(exam -> {
                StudentScore studentScore = stringStudentScoreMap.get(student.getStudentId() + "_" + exam.getExamId());
                if (studentScore == null) {
                    rowList.add(0l);
                } else {
                    rowList.add(studentScore.getScore());
                }
            });
            result.add(rowList);
        });
        return result;
    }

    @Override
    public Map<String, Object> findTotalStudentScoreMapByCondition(Long examId, Long semesterId, Long clazzId, boolean isPrinter) throws FebsException {
        try {
            Map<String, Object> map = new HashMap<>();
            Map<Long, Student> studentMap = iStudentService.findByClazzId(clazzId).stream()
                    .collect(Collectors.toMap(Student::getStudentId, it -> it));
            //查询某次考试某个学期某个班级所有的考试计划安排
            List<TeachingArrange> teachingArrangeList = iTeachingArrangeService.list(new LambdaQueryWrapper<TeachingArrange>()
                    .eq(TeachingArrange::getSemesterId, semesterId)
                    .eq(TeachingArrange::getClazzId, clazzId));
            iTeachingArrangeService.teachingArrangeFillCourse(teachingArrangeList);
            List<Long> teachingArrangeIdList = teachingArrangeList.stream().map(TeachingArrange::getTeachingArrangeId).collect(Collectors.toList());
            List<String> courseNameList = teachingArrangeList.stream().map(TeachingArrange::getCourse).map(Course::getCourseName)
                    .collect(Collectors.toList());

            //查询学生总成绩
            QueryWrapper<StudentScore> studentScoreQueryWrapper = new QueryWrapper<>();
            studentScoreQueryWrapper.select("STUDENT_ID as studentId,sum(SCORE) as totalScore, sum(POINT_SCORE) as totalPointScore, sum(STUDY_SCORE) as totalStudyScore");
            studentScoreQueryWrapper.in("TEACHING_ARRANGE_ID", teachingArrangeIdList);
            studentScoreQueryWrapper.eq("EXAM_ID", examId);
            studentScoreQueryWrapper.groupBy("STUDENT_ID");
            List<Map<String, Object>> stuTotalScoreMapList = iStudentScoreService.listMaps(studentScoreQueryWrapper);
            Map<String, StudentScore> examScoreMap = iStudentScoreService.list(new LambdaQueryWrapper<StudentScore>()
                            .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList)
                            .eq(StudentScore::getExamId, examId))
                    .stream().collect(Collectors.toMap(key -> key.getStudentId() + "_" + key.getTeachingArrangeId(), value -> value));
            List<StudentTotalScore> studentTotalScoreList = stuTotalScoreMapList.stream().map(stuTotalScoreMap -> {
                StudentTotalScore studentTotalScore = new StudentTotalScore();
                studentTotalScore.setStudentId((Long) stuTotalScoreMap.get("studentId"));
                studentTotalScore.setTotalScore((Double) stuTotalScoreMap.get("totalScore"));
                studentTotalScore.setTotalStudyScore((Double) stuTotalScoreMap.get("totalStudyScore"));
                studentTotalScore.setTotalPointScore((Double) stuTotalScoreMap.get("totalPointScore"));
                studentTotalScore.setStudentName(studentMap.get(studentTotalScore.getStudentId()).getStudentName());
                for (int i = 0; i < teachingArrangeIdList.size(); i++) {
                    Long teachingArrangeId = teachingArrangeIdList.get(i);
                    StudentScore studentScore = examScoreMap.get(studentTotalScore.getStudentId() + "_" + teachingArrangeId);
                    if (studentTotalScore.getScoreList() == null) {
                        studentTotalScore.setScoreList(new ArrayList<>());
                    }

                    if (studentScore == null) {
                        studentTotalScore.getScoreList().add(0d);
                    } else {
                        studentTotalScore.getScoreList().add(studentScore.getScore());
                    }
                }
                return studentTotalScore;
            }).collect(Collectors.toList());

            List<Map<String, Object>> mapList = studentTotalScoreList.stream().sorted(Comparator
                    .comparingDouble(StudentTotalScore::getTotalScore).reversed()).map(studentTotalScore -> {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("studentId", studentTotalScore.getStudentId());
                objectMap.put("studentName", studentTotalScore.getStudentName());
                objectMap.put("totalScore", studentTotalScore.getTotalScore());
                objectMap.put("totalStudyScore", studentTotalScore.getTotalStudyScore());
                objectMap.put("totalPointScore", studentTotalScore.getTotalPointScore());
                for (int i = 0; i < teachingArrangeList.size(); i++) {
                    TeachingArrange teachingArrange = teachingArrangeList.get(i);
                    if (studentTotalScore.getScoreList().get(i) / teachingArrange.getCourse().getExamScore() < 0.6 && !isPrinter) {
                        objectMap.put("EPI" + teachingArrange.getTeachingArrangeId(), "<span style='color: white;" +
                                "background-color: #f5222d;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentTotalScore.getScoreList().get(i) + "</span>");
                    } else if (studentTotalScore.getScoreList().get(i) / teachingArrange.getCourse().getExamScore() >= 0.9 && !isPrinter) {
                        objectMap.put("EPI" + teachingArrange.getTeachingArrangeId(), "<span style='color: white;" +
                                "background-color: #42b983;" +
                                "border-radius: 50%;" +
                                "padding: 8px 5px;'>" + studentTotalScore.getScoreList().get(i) + "</span>");
                    } else {
                        objectMap.put("EPI" + teachingArrange.getTeachingArrangeId(), studentTotalScore.getScoreList().get(i));
                    }
                }
                return objectMap;
            }).collect(Collectors.toList());

            for (int i = 0; i < mapList.size(); i++) {
                mapList.get(i).put("index", i + 1);
            }

            map.put("studentTotalScoreList", studentTotalScoreList);
            map.put("courseNameList", courseNameList);
            map.put("columns", ColumnsUtils.builderTotalScoreColumns2(teachingArrangeList));
            map.put("dataSource", mapList);

            Wrapper<StudentScore> conditionWrapper = new QueryWrapper<StudentScore>().lambda()
                    .eq(StudentScore::getExamId, examId)
                    .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList);
            List<StudentScore> studentScoreList = iStudentScoreService.list(conditionWrapper);
            Long firstCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.ONE.getCode());
            Long secondCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.SECOND.getCode());
            Long thirdCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.THIRD.getCode());
            Long fourCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FOUR.getCode());
            Long fiveCount = CalculateUtil.degreeCount(studentScoreList, ScoreType.FIVE.getCode());
            map.put("firstCount", firstCount);
            map.put("secondCount", secondCount);
            map.put("thirdCount", thirdCount);
            map.put("fourCount", fourCount);
            map.put("fiveCount", fiveCount);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FebsException("没有查询相关成绩数据，请检查搜索条件");
        }
    }

    @Override
    public Map<String, Object> findTotalStudentScorePrinterByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> map = iStudentScoreService.findTotalStudentScoreMapByCondition(examId, semesterId, clazzId, true);
        List<Map<String, Object>> dataSource = (List<Map<String, Object>>) map.get("dataSource");
        List<Column> columns = (List<Column>) map.get("columns");
        //名次 姓名 课程 总分
        String[][] result = new String[dataSource.size() + 1][columns.size()];
        //构造表头
        List<String> title = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            title.add(columns.get(i).getTitle());
        }
        title.toArray(result[0]);
        //构造表体
        for (int i = 0; i < dataSource.size(); i++) {
            Map<String, Object> scoreMap = dataSource.get(i);
            for (int j = 0; j < columns.size(); j++) {
                result[i + 1][j] = String.valueOf(scoreMap.get(columns.get(j).getDataIndex()));
            }
        }

        resultMap.put("result", result);
        //班级
        Clazz clazz = iClazzService.getById(clazzId);
        //年级
        Grade grade = iGradeService.getById(clazz.getGradeId());
        //学院
        College college = iCollegeService.getById(grade.getCollegeId());
        //学期
        Semester semester = iSemesterService.getById(semesterId);
        //考试
        Exam exam = iExamService.getById(examId);
        resultMap.put("title", college.getCollegeName() + grade.getGradeName()
                + clazz.getClazzName() + semester.getSemesterName()
                + exam.getExamName() + "考试" + "总成绩公示");
        return resultMap;
    }

    @Override
    public Map<String, Object> findStudentScorePrinterByTeachingArrangeIdAndExamId(Long teachingArrangeId, Long examId) {

        Map<String, Object> map = new HashMap<>();
        TeachingArrange teachingArrange = iTeachingArrangeService.getById(teachingArrangeId);
        //班级
        Clazz clazz = iClazzService.getById(teachingArrange.getClazzId());
        //年级
        Grade grade = iGradeService.getById(clazz.getGradeId());
        //学院
        College college = iCollegeService.getById(grade.getCollegeId());
        //学期
        Semester semester = iSemesterService.getById(teachingArrange.getSemesterId());
        //课程
        Course course = iCourseService.getById(teachingArrange.getCourseId());
        //考试
        Exam exam = iExamService.getById(examId);
        List<StudentScore> studentScoreList = findStudentScoreByTeachingArrangeId(teachingArrangeId, examId);
        studentScoreList.sort(Comparator.comparingDouble(StudentScore::getScore).reversed());

        String[][] result = new String[studentScoreList.size() + 1][3];
        //构造表头
        List<String> title = new ArrayList<>();
        title.add("名次");
        title.add("姓名");
        title.add(course.getCourseName());
        title.toArray(result[0]);

        //构造表体
        for (int i = 0; i < studentScoreList.size(); i++) {
            result[i + 1][0] = String.valueOf(i + 1);
            result[i + 1][1] = studentScoreList.get(i).getStudent().getStudentName();
            result[i + 1][2] = String.valueOf(studentScoreList.get(i).getScore());
        }

        map.put("result", result);
        map.put("title", college.getCollegeName() + grade.getGradeName()
                + clazz.getClazzName() + semester.getSemesterName() + course.getCourseName()
                + exam.getExamName() + "考试" + "成绩公示");
        return map;
    }

    @Override
    public void saveStudentScoreMap(Map<String, String> studentScoreMap) {
        Long teachingArrangeId = Long.valueOf(studentScoreMap.get("teachingArrangeId"));
        Long examId = Long.valueOf(studentScoreMap.get("examId"));
        TeachingArrange teachingArrange = iTeachingArrangeService.getById(teachingArrangeId);
        List<Student> studentList = iStudentService.findByClazzId(teachingArrange.getClazzId());
        Course course = iCourseService.getById(teachingArrange.getCourseId());
        studentList.forEach(student -> {
            Double score = Double.valueOf(studentScoreMap.get(SchoolConstants.STUDENT_SCORE_ + student.getStudentId()));
            StudentScore studentScore = studentScoreMapper.selectOne(new LambdaQueryWrapper<StudentScore>()
                    .eq(StudentScore::getTeachingArrangeId, teachingArrangeId)
                    .eq(StudentScore::getExamId, examId)
                    .eq(StudentScore::getStudentId, student.getStudentId()));
            if (studentScore != null) {
                studentScore.setScore(score);
                try {
                    //学分
                    studentScore.setStudyScore(SchoolUtils.studyScore(score, course.getExamScore(), course.getStudyScore()));
                    //绩点
                    studentScore.setPointScore(SchoolUtils.calculatePoint(score, course.getExamScore()));
                    //等级
                    studentScore.setDegreeScore(SchoolUtils.scoreCategory(score, course.getExamScore()));
                    if (score == -1d) {
                        iStudentScoreService.removeById(studentScore.getStudentScoreId());
                        System.out.println(student.getStudentName() + "执行了删除成绩操作(-1)");
                    } else {
                        iStudentScoreService.modify(studentScore);
                    }

                } catch (FebsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (score != -1d) {
                    StudentScore newStudentScore = new StudentScore();
                    newStudentScore.setTeachingArrangeId(teachingArrangeId);
                    newStudentScore.setExamId(examId);
                    newStudentScore.setStudentId(student.getStudentId());
                    newStudentScore.setScore(score);
                    try {
                        //学分
                        newStudentScore.setStudyScore(SchoolUtils.studyScore(score, course.getExamScore(), course.getStudyScore()));
                        //绩点
                        newStudentScore.setPointScore(SchoolUtils.calculatePoint(score, course.getExamScore()));
                        //等级
                        newStudentScore.setDegreeScore(SchoolUtils.scoreCategory(score, course.getExamScore()));
                        iStudentScoreService.insert(newStudentScore);
                    } catch (FebsException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(student.getStudentName() + "成绩为-1,不执行插入操作");
                }

            }
        });
    }

    @Override
    public Integer findCountByExamIdList(List<Long> examIdList) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getExamId, examIdList);
        return this.count(wrapper);
    }

    @Override
    public void deleteByTeachingArrangeIdList(List<Long> teachingArrangeIdList) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList);
        remove(wrapper);
    }

    @Override
    public Integer findCountByStudentIdList(List<Long> studentIdList) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getStudentId, studentIdList);
        return this.count(wrapper);
    }

    @Override
    public Map<Long, Long> teachingArrangeIdStudentCount(List<Long> teachingArrangeIdList, int degree) {
        QueryWrapper<StudentScore> wrapper = new QueryWrapper<>();
        wrapper.select("TEACHING_ARRANGE_ID as teachingArrangeId,count(distinct STUDENT_ID) as studentCount");
        wrapper.in("TEACHING_ARRANGE_ID", teachingArrangeIdList);
        wrapper.eq("DEGREE_SCORE", degree);
        wrapper.groupBy("TEACHING_ARRANGE_ID");
        List<Map<String, Object>> list = this.listMaps(wrapper);
        Map<Long, Long> result = new HashMap<>();
        list.forEach(map -> {
            Long teachingArrangeId = (Long) map.get("teachingArrangeId");
            Long count = (Long) map.get("studentCount");
            result.put(teachingArrangeId, count);
        });
        return result;
    }

    @Override
    public int findDegreeCount(int degree) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getDegreeScore, degree);
        return this.count(wrapper);
    }

    @Override
    public int findDegreeCount(List<Long> teachingArrangeIdList, int degree) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList)
                .eq(StudentScore::getDegreeScore, degree);
        return this.count(wrapper);
    }

    @Override
    public int findCount(List<Long> teachingArrangeIdList) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList);
        return this.count(wrapper);
    }

    @Override
    public int findStudentDegreeCount(Long studentId, int degree) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getStudentId, studentId)
                .eq(StudentScore::getDegreeScore, degree);
        return this.count(wrapper);
    }

    @Override
    public int findCountByStudentId(Long studentId) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getStudentId, studentId);
        return this.count(wrapper);
    }

    @Override
    public List<StudentScore> findListByTeachingArrangeId(Long teachingArrangeId) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getTeachingArrangeId, teachingArrangeId);
        return this.list(wrapper);
    }

    @Override
    public List<StudentScore> findListByTeachingArrangeIdList(List<Long> teachingArrangeIdList) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList);
        return this.list(wrapper);
    }

    @Override
    public List<StudentScore> findListByStudentId(Long studentId) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getStudentId, studentId);
        return this.list(wrapper);
    }

    @Override
    public List<StudentScore> findListByStudentIdAndExamId(Long studentId, Long examId) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .eq(StudentScore::getStudentId, studentId)
                .eq(StudentScore::getExamId, examId);
        return this.list(wrapper);
    }

    @Override
    public List<Long> findExamIdListByTeachingArrangeId(Long teachingArrangeId) {
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().select("distinct EXAM_ID as examId").lambda()
                .eq(StudentScore::getTeachingArrangeId, teachingArrangeId);
        return this.listObjs(wrapper).stream().map(examId -> (Long) examId).collect(Collectors.toList());
    }

    @Override
    public List<StudentTotalScore> findStudentTotalScoreByTeachingArrangeId(Long teachingArrangeId) {
        QueryWrapper<StudentScore> studentScoreQueryWrapper = new QueryWrapper<>();
        studentScoreQueryWrapper.select("STUDENT_ID as studentId,sum(SCORE) as totalScore, sum(STUDY_SCORE) as totalStudyScore, sum(POINT_SCORE) as totalPointScore");
        studentScoreQueryWrapper.eq("TEACHING_ARRANGE_ID", teachingArrangeId);
        studentScoreQueryWrapper.groupBy("STUDENT_ID");
        List<Map<String, Object>> stuTotalScoreMapList = iStudentScoreService.listMaps(studentScoreQueryWrapper);
        List<StudentTotalScore> studentTotalScoreList = stuTotalScoreMapList.stream().map(stuTotalScoreMap -> {
            StudentTotalScore studentTotalScore = new StudentTotalScore();
            studentTotalScore.setStudentId((Long) stuTotalScoreMap.get("studentId"));
            studentTotalScore.setTotalScore((Double) stuTotalScoreMap.get("totalScore"));
            studentTotalScore.setTotalStudyScore((Double) stuTotalScoreMap.get("totalStudyScore"));
            studentTotalScore.setTotalPointScore((Double) stuTotalScoreMap.get("totalPointScore"));
            return studentTotalScore;
        }).collect(Collectors.toList());
        return studentTotalScoreList;
    }

    @Override
    public List<TeachingArrangeTotalScore> findTeachingArrangeTotalScoreByStudentId(Long studentId) {
        QueryWrapper<StudentScore> wrapper = new QueryWrapper<>();
        wrapper.select("TEACHING_ARRANGE_ID as teachingArrangeId,sum(SCORE) as totalScore, sum(STUDY_SCORE) as totalStudyScore, sum(POINT_SCORE) as totalPointScore");
        wrapper.eq("STUDENT_ID", studentId);
        wrapper.groupBy("TEACHING_ARRANGE_ID");
        List<Map<String, Object>> teachingArrangeTotalScoreMapList = iStudentScoreService.listMaps(wrapper);
        List<TeachingArrangeTotalScore> teachingArrangeTotalScoreList = teachingArrangeTotalScoreMapList.stream().map(stuTotalScoreMap -> {
            TeachingArrangeTotalScore teachingArrangeTotalScore = new TeachingArrangeTotalScore();
            teachingArrangeTotalScore.setTeachingArrangeId((Long) stuTotalScoreMap.get("teachingArrangeId"));
            teachingArrangeTotalScore.setTotalScore((Double) stuTotalScoreMap.get("totalScore"));
            teachingArrangeTotalScore.setTotalStudyScore((Double) stuTotalScoreMap.get("totalStudyScore"));
            teachingArrangeTotalScore.setTotalPointScore((Double) stuTotalScoreMap.get("totalPointScore"));
            return teachingArrangeTotalScore;
        }).collect(Collectors.toList());
        return teachingArrangeTotalScoreList;
    }

    @Override
    public List<StudentScore> findListByCondition(List<Long> teachingArrangeIdList, Long examId, Long studentId) {
        if (CollectionUtils.isEmpty(teachingArrangeIdList)) {
            return new ArrayList<>();
        }
        Wrapper<StudentScore> wrapper = new QueryWrapper<StudentScore>().lambda()
                .in(StudentScore::getTeachingArrangeId, teachingArrangeIdList)
                .eq(StudentScore::getExamId, examId)
                .eq(StudentScore::getStudentId, studentId);
        return this.list(wrapper);
    }
}
