package cc.mrbird.febs.common.enums;

/**
 * 考试枚举
 */
public enum ExamEnum {
    MIDDLE(1, "期中"), FINAL(2, "期末");
    private int code;
    private String name;

    ExamEnum(int code, String name) {
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
