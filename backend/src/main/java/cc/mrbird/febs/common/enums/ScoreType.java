package cc.mrbird.febs.common.enums;

public enum ScoreType {
    ONE(1, "优秀"), SECOND(2, "良好"), THIRD(3, "一般"),
    FOUR(4, "及格"), FIVE(5, "挂科");
    private int code;
    private String name;

    ScoreType(int code, String name) {
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
