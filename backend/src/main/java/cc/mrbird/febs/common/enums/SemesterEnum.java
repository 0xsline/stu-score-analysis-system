package cc.mrbird.febs.common.enums;

/**
 * 学期枚举
 */
public enum SemesterEnum {
    ONE_UP(1, "大一上学期"), ONE_DOWN(2, "大一下学期"),
    TWO_UP(3, "大二上学期"), TWO_DOWN(4, "大二下学期"),
    THREE_UP(5, "大三上学期"), THREE_DOWN(6, "大三下学期"),
    FOUR_UP(7, "大四上学期"), FOUR_DOWN(8, "大四下学期");

    private int code;
    private String name;

    SemesterEnum(int code, String name) {
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
