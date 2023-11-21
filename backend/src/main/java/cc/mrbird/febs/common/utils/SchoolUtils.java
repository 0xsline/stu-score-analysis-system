package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.enums.ScoreType;
import cc.mrbird.febs.school.entity.Student;
import cc.mrbird.febs.school.entity.Teacher;
import cc.mrbird.febs.system.domain.User;

public class SchoolUtils {

    /**
     * 教师基本信息转换成用户基本信息
     *
     * @param teacher
     * @param user
     * @return
     */
    public static User teacherToUser(Teacher teacher, User user) {
        if (user == null) {
            user = new User();
        }
        user.setUsername(teacher.getUsername());
        user.setPassword(teacher.getPassword());
        user.setEmail(teacher.getEmail());
        user.setMobile(teacher.getMobile());
        user.setStatus(teacher.getStatus());
        user.setSsex(teacher.getSsex());
        user.setDescription(teacher.getDescription());
        user.setAvatar(teacher.getAvatar());
        user.setUserType(teacher.getUserType());
        user.setIdentityCard(teacher.getIdentityCard());
        user.setDeptId(teacher.getDeptId());
        user.setLastLoginTime(teacher.getLastLoginTime());
        return user;
    }

    /**
     * 学生基本信息转换成用户基本信息
     *
     * @param student
     * @param user
     * @return
     */
    public static User studentToUser(Student student, User user) {
        if (user == null) {
            user = new User();
        }
        user.setUsername(student.getUsername());
        user.setPassword(student.getPassword());
        user.setEmail(student.getEmail());
        user.setMobile(student.getMobile());
        user.setStatus(student.getStatus());
        user.setSsex(student.getSsex());
        user.setDescription(student.getDescription());
        user.setAvatar(student.getAvatar());
        user.setUserType(student.getUserType());
        user.setIdentityCard(student.getIdentityCard());
        user.setDeptId(student.getDeptId());
        user.setLastLoginTime(student.getLastLoginTime());
        return user;
    }


    /**
     * 用户基本信息转换成教师基本信息
     *
     * @param user
     * @param teacher
     * @return
     */
    public static Teacher userToTeacher(User user, Teacher teacher) {
        if (teacher == null) {
            teacher = new Teacher();
        }

        teacher.setUsername(user.getUsername());
        teacher.setPassword(user.getPassword());
        teacher.setEmail(user.getEmail());
        teacher.setMobile(user.getMobile());
        teacher.setStatus(user.getStatus());
        teacher.setSsex(user.getSsex());
        teacher.setDescription(user.getDescription());
        teacher.setAvatar(user.getAvatar());
        teacher.setUserType(user.getUserType());
        teacher.setIdentityCard(user.getIdentityCard());
        teacher.setDeptId(user.getDeptId());
        teacher.setLastLoginTime(user.getLastLoginTime());
        return teacher;
    }


    /**
     * 用户基本信息转换成学生基本信息
     *
     * @param user
     * @param student
     * @return
     */
    public static Student userToStudent(User user, Student student) {
        if (student == null) {
            student = new Student();
        }

        student.setUsername(user.getUsername());
        student.setPassword(user.getPassword());
        student.setEmail(user.getEmail());
        student.setMobile(user.getMobile());
        student.setStatus(user.getStatus());
        student.setSsex(user.getSsex());
        student.setDescription(user.getDescription());
        student.setAvatar(user.getAvatar());
        student.setUserType(user.getUserType());
        student.setIdentityCard(user.getIdentityCard());
        student.setDeptId(user.getDeptId());
        student.setLastLoginTime(user.getLastLoginTime());
        return student;
    }

    /**
     * 计算绩点
     *
     * @param calculateScore
     * @param thresholdScore
     * @return
     */
    public static Double calculatePoint(Double calculateScore, Double thresholdScore) {
        Double rate = calculateScore / thresholdScore;
        if (rate >= 0.95 && rate <= 1) {
            return 4.0d;
        } else if (rate >= 0.90 && rate < 0.95) {
            return 3.5d;
        } else if (rate >= 0.85 && rate < 0.90) {
            return 3.0d;
        } else if (rate >= 0.80 && rate < 0.85) {
            return 2.5d;
        } else if (rate >= 0.75 && rate < 0.80) {
            return 2.1d;
        } else if (rate >= 0.70 && rate < 0.75) {
            return 1.7d;
        } else if (rate >= 0.65 && rate < 0.70) {
            return 1.3d;
        } else if (rate >= 0.60 && rate < 0.65) {
            return 1.0d;
        } else {
            return 0d;
        }
    }

    /**
     * 计算最终获得的学分
     * 及格即获得学分，不及格不能获得学分
     *
     * @param calculateScore
     * @param thresholdScore
     * @param studyScore
     * @return
     */
    public static Double studyScore(Double calculateScore, Double thresholdScore, Float studyScore) {
        Double rate = calculateScore / thresholdScore;
        if (rate >= 0.90 && rate <= 1) {
            return 4d * studyScore / 4;
        } else if (rate >= 0.80 && rate < 0.90) {
            return 3.5d * studyScore / 4;
        } else if (rate >= 0.70 && rate < 0.80) {
            return 3d * studyScore / 4;
        } else if (rate >= 0.60 && rate < 0.70) {
            return 2.5d * studyScore / 4;
        } else {
            return 2d * studyScore / 4;
        }
    }

    /**
     * 成绩分类
     * 90 - 100 优秀
     * 80 - 90 良好
     * 70 - 80 一般
     * 60 - 70 及格
     * 0 - 60 挂科
     *
     * @param calculateScore
     * @param thresholdScore
     * @return
     */
    public static Integer scoreCategory(Double calculateScore, Double thresholdScore) {
        Double rate = calculateScore / thresholdScore;
        if (rate >= 0.90 && rate <= 1) {
            return ScoreType.ONE.getCode();
        } else if (rate >= 0.80 && rate < 0.90) {
            return ScoreType.SECOND.getCode();
        } else if (rate >= 0.70 && rate < 0.80) {
            return ScoreType.THIRD.getCode();
        } else if (rate >= 0.60 && rate < 0.70) {
            return ScoreType.FOUR.getCode();
        } else {
            return ScoreType.FIVE.getCode();
        }
    }
}
