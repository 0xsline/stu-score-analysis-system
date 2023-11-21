package cc.mrbird.febs.school.controller;

import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.school.service.IStudentScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author IU
 */
@Slf4j
@Validated
@RestController
@RequestMapping("people/school")
public class SchoolController extends BaseController {

    @Autowired
    private IStudentScoreService iStudentScoreService;


    /**
     * 根据登录用户是系统查询考试情况
     *

     */
    @GetMapping("/examInfo")
    public Map<String, Object> examInfo() {
        if (isSystem()) {
            int firstCount = iStudentScoreService.findDegreeCount(ScoreType.ONE.getCode());
            int secondCount = iStudentScoreService.findDegreeCount(ScoreType.SECOND.getCode());
            int thirdCount = iStudentScoreService.findDegreeCount(ScoreType.THIRD.getCode());
            int fourCount = iStudentScoreService.findDegreeCount(ScoreType.FOUR.getCode());
            int fiveCount = iStudentScoreService.findDegreeCount(ScoreType.FIVE.getCode());
            int totalExamCount = iStudentScoreService.count();
            Map<String, Object> map = new HashMap<>();
            map.put("schoolFirstCount", firstCount);
            map.put("schoolSecondCount", secondCount);
            map.put("schoolThirdCount", thirdCount);
            map.put("schoolFourCount", fourCount);
            map.put("schoolFiveCount", fiveCount);
            map.put("schoolTotalExamCount", totalExamCount);
            return map;
        }
        return null;
    }
}
