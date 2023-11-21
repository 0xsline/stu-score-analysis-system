package cc.mrbird.febs.common.enums;

/**
 * 课程类型
 */
public enum CourseType {
    MUST(0, "必修课"), SELECTIVE(1, "选修课");
    private int code;
    private String name;

    CourseType(int code, String name) {
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
