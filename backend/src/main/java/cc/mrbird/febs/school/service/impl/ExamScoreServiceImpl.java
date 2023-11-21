package cc.mrbird.febs.school.service.impl;

import cc.mrbird.febs.common.annotation.CustomerInsert;
import cc.mrbird.febs.common.annotation.CustomerUpdate;
import cc.mrbird.febs.common.domain.Column;
import cc.mrbird.febs.common.domain.StudentTotalScore;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.CalculateUtil;
import cc.mrbird.febs.common.utils.ColumnsUtils;
import cc.mrbird.febs.common.utils.HistogramUtils;
import cc.mrbird.febs.common.utils.SchoolConstants;
import cc.mrbird.febs.school.dao.ExamScoreMapper;
import cc.mrbird.febs.school.entity.*;
import cc.mrbird.febs.school.service.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author IU
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExamScoreServiceImpl extends ServiceImpl<ExamScoreMapper, ExamScore> implements IExamScoreService {

    @Autowired
    private ExamScoreMapper examScoreMapper;

    @Autowired
    private IExamPlanService iExamPlanService;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private IExamScoreService iExamScoreService;

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
    public boolean insert(ExamScore entity) throws FebsException {
        return examScoreMapper.insert(entity) > 0;
    }

    @Override
    @CustomerUpdate
    public boolean modify(ExamScore entity) throws FebsException {
        return examScoreMapper.updateById(entity) > 0;
    }

    @Override
    public List<ExamScore> findExamScoreByExamPlanId(Long examPlanId) {
        ExamPlan examPlan = iExamPlanService.getById(examPlanId);
        List<ExamScore> examScoreList = examScoreMapper.selectList(new LambdaQueryWrapper<ExamScore>().eq(ExamScore::getExamPlanId, examPlanId));
        Map<Long, ExamScore> longExamScoreMap = examScoreList.stream().collect(Collectors.toMap(ExamScore::getStudentId, it -> it));
        List<Student> studentList = iStudentService.findByClazzId(examPlan.getClazzId());
        return studentList.stream().map(student -> {
            ExamScore examScore = new ExamScore();
            examScore.setExamPlanId(examPlanId);
            examScore.setStudentId(student.getStudentId());
            examScore.setScore(0d);
            examScore.setStudent(student);
            ExamScore db = longExamScoreMap.get(student.getStudentId());
            if (db != null) {
                examScore.setScore(db.getScore());
            }

            examScore.setRate(examScore.getScore() / examPlan.getExamScore());
            return examScore;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findExamScoreMapByExamPlanId(Long examPlanId) {
        ExamPlan examPlan = iExamPlanService.getById(examPlanId);
        Map<String, Object> map = new HashMap<>();
        List<ExamScore> examScoreList = findExamScoreByExamPlanId(examPlanId);
        List<Double> scoreList = examScoreList.stream().map(ExamScore::getScore).collect(Collectors.toList());
        Double maxScore = CalculateUtil.maxScore(scoreList);
        Double minScore = CalculateUtil.minScore(scoreList);
        Double avgScore = CalculateUtil.avgScore(scoreList);
        String passRatio = CalculateUtil.passRatio(scoreList, examPlan.getExamScore());
        long masterCount = CalculateUtil.masterCount(scoreList, examPlan.getExamScore());
        long commonlyCount = CalculateUtil.commonlyCount(scoreList, examPlan.getExamScore());
        long loserCount = CalculateUtil.loserCount(scoreList, examPlan.getExamScore());
        map.put("examScoreList", examScoreList);
        map.put("sortedExamScoreList", examScoreList.stream().sorted(Comparator.comparingDouble(ExamScore::getScore).reversed()).collect(Collectors.toList()));
        map.put("maxScore", maxScore);
        map.put("minScore", minScore);
        map.put("avgScore", avgScore);
        map.put("passRatio", passRatio);
        map.put("masterCount", masterCount);
        map.put("commonlyCount", commonlyCount);
        map.put("loserCount", loserCount);
        map.put("histogramData", HistogramUtils.buildHistogram(examPlan.getExamScore(), scoreList));
        map.put("lineTitleArray", HistogramUtils.buildLineTitle(examPlan.getExamScore()));
        map.put("examPlan", examPlan);
        return map;
    }

    @Override
    public Map<String, Object> findTotalExamScoreMapByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException {
        try {
            Map<String, Object> map = new HashMap<>();
            Map<Long, Student> studentMap = iStudentService.findByClazzId(clazzId).stream().collect(Collectors.toMap(Student::getStudentId, it -> it));
            //查询某次考试某个学期某个班级所有的考试计划安排
            List<ExamPlan> examPlanList = iExamPlanService.list(new LambdaQueryWrapper<ExamPlan>()
                    .eq(ExamPlan::getExamId, examId).eq(ExamPlan::getSemesterId, semesterId).eq(ExamPlan::getClazzId, clazzId));
            iExamPlanService.examPlanFillCourse(examPlanList);
            List<Long> examPlanIdList = examPlanList.stream().map(ExamPlan::getExamPlanId).collect(Collectors.toList());
            List<String> courseNameList = examPlanList.stream().map(ExamPlan::getCourse).map(Course::getCourseName)
                    .collect(Collectors.toList());

            //查询学生总成绩
            QueryWrapper<ExamScore> examScoreQueryWrapper = new QueryWrapper<>();
            examScoreQueryWrapper.select("STUDENT_ID as studentId,sum(SCORE) as totalScore");
            examScoreQueryWrapper.in("EXAM_PLAN_ID", examPlanIdList);
            examScoreQueryWrapper.groupBy("STUDENT_ID");
            List<Map<String, Object>> stuTotalScoreMapList = iExamScoreService.listMaps(examScoreQueryWrapper);
            Map<String, ExamScore> examScoreMap = iExamScoreService.list(new LambdaQueryWrapper<ExamScore>()
                            .in(ExamScore::getExamPlanId, examPlanIdList))
                    .stream().collect(Collectors.toMap(key -> key.getStudentId() + "_" + key.getExamPlanId(), value -> value));
            List<StudentTotalScore> studentTotalScoreList = stuTotalScoreMapList.stream().map(stuTotalScoreMap -> {
                StudentTotalScore studentTotalScore = new StudentTotalScore();
                studentTotalScore.setStudentId((Long) stuTotalScoreMap.get("studentId"));
                studentTotalScore.setTotalScore((Double) stuTotalScoreMap.get("totalScore"));
                studentTotalScore.setStudentName(studentMap.get(studentTotalScore.getStudentId()).getStudentName());
                for (int i = 0; i < examPlanIdList.size(); i++) {
                    Long examPlanId = examPlanIdList.get(i);
                    ExamScore examScore = examScoreMap.get(studentTotalScore.getStudentId() + "_" + examPlanId);
                    if (studentTotalScore.getScoreList() == null) {
                        studentTotalScore.setScoreList(new ArrayList<>());
                    }

                    if (examScore == null) {
                        studentTotalScore.getScoreList().add(0d);
                    } else {
                        studentTotalScore.getScoreList().add(examScore.getScore());
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
                for (int i = 0; i < examPlanList.size(); i++) {
                    ExamPlan examPlan = examPlanList.get(i);
                    objectMap.put("EPI" + examPlan.getExamPlanId(), studentTotalScore.getScoreList().get(i));
                }
                return objectMap;
            }).collect(Collectors.toList());

            for (int i = 0; i < mapList.size(); i++) {
                mapList.get(i).put("index", i + 1);
            }

            map.put("studentTotalScoreList", studentTotalScoreList);
            map.put("courseNameList", courseNameList);
            map.put("columns", ColumnsUtils.builderTotalScoreColumns(examPlanList));
            map.put("dataSource", mapList);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FebsException("没有查询相关成绩数据，请检查搜索条件");
        }
    }

    @Override
    public Map<String, Object> findTotalExamScorePrinterByCondition(Long examId, Long semesterId, Long clazzId) throws FebsException {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> map = this.findTotalExamScoreMapByCondition(examId, semesterId, clazzId);
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
    public Map<String, Object> findExamScorePrinterByExamPlanId(Long examPlanId) {

        Map<String, Object> map = new HashMap<>();
        ExamPlan examPlan = iExamPlanService.getById(examPlanId);
        //班级
        Clazz clazz = iClazzService.getById(examPlan.getClazzId());
        //年级
        Grade grade = iGradeService.getById(clazz.getGradeId());
        //学院
        College college = iCollegeService.getById(grade.getCollegeId());
        //学期
        Semester semester = iSemesterService.getById(examPlan.getSemesterId());
        //课程
        Course course = iCourseService.getById(examPlan.getCourseId());
        //考试
        Exam exam = iExamService.getById(examPlan.getExamId());
        List<ExamScore> examScoreList = findExamScoreByExamPlanId(examPlanId);
        examScoreList.sort(Comparator.comparingDouble(ExamScore::getScore).reversed());

        String[][] result = new String[examScoreList.size() + 1][3];
        //构造表头
        List<String> title = new ArrayList<>();
        title.add("名次");
        title.add("姓名");
        title.add(course.getCourseName());
        title.toArray(result[0]);

        //构造表体
        for (int i = 0; i < examScoreList.size(); i++) {
            result[i + 1][0] = String.valueOf(i + 1);
            result[i + 1][1] = examScoreList.get(i).getStudent().getStudentName();
            result[i + 1][2] = String.valueOf(examScoreList.get(i).getScore());
        }

        map.put("result", result);
        map.put("title", college.getCollegeName() + grade.getGradeName()
                + clazz.getClazzName() + semester.getSemesterName() + course.getCourseName()
                + exam.getExamName() + "考试" + "成绩公示");
        return map;
    }

    @Override
    public void saveExamScoreMap(Map<String, String> examScoreMap) {
        Long examPlanId = Long.valueOf(examScoreMap.get("examPlanId"));
        ExamPlan examPlan = iExamPlanService.getById(examPlanId);
        List<Student> studentList = iStudentService.findByClazzId(examPlan.getClazzId());
        studentList.forEach(student -> {
            Double score = Double.valueOf(examScoreMap.get(SchoolConstants.EXAM_SCORE_ + student.getStudentId()));
            ExamScore examScore = examScoreMapper.selectOne(new LambdaQueryWrapper<ExamScore>()
                    .eq(ExamScore::getExamPlanId, examPlanId).eq(ExamScore::getStudentId, student.getStudentId()));
            if (examScore != null) {
                examScore.setScore(score);
                try {
                    iExamScoreService.modify(examScore);
                } catch (FebsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ExamScore newExamScore = new ExamScore();
                newExamScore.setExamPlanId(examPlanId);
                newExamScore.setStudentId(student.getStudentId());
                newExamScore.setScore(score);
                try {
                    iExamScoreService.insert(newExamScore);
                } catch (FebsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void deleteByExamPlanIdList(List<Long> examPlanIdList) {
        Wrapper<ExamScore> wrapper = new QueryWrapper<ExamScore>().lambda()
                .in(ExamScore::getExamPlanId, examPlanIdList);
        remove(wrapper);
    }

    @Override
    public Integer findCountByStudentIdList(List<Long> studentIdList) {
        Wrapper<ExamScore> wrapper = new QueryWrapper<ExamScore>().lambda()
                .in(ExamScore::getStudentId, studentIdList);
        return this.count(wrapper);
    }
}
