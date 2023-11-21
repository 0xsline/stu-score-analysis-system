package cc.mrbird.febs.common.enums;


/**
 * 用户类型
 */
public enum UserType {
    SYSTEM(0, "系统用户"), TEACHER(1, "教师"), STUDENT(2, "学生");
    private int code;
    private String name;

    UserType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
