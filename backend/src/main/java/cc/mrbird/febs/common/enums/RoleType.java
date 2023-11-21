package cc.mrbird.febs.common.enums;

/**
 * 角色类型
 */
public enum RoleType {
    SYSTEM(0, "系统角色"), TEACHER(1, "教师角色"), STUDENT(2, "学生角色");
    private int code;
    private String name;

    RoleType(int code, String name) {
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
