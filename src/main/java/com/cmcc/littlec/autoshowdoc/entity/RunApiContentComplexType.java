package com.cmcc.littlec.autoshowdoc.entity;

/**
 * @ClassName RunApiContentComplexType
 * @Description runapi文档复杂类型
 * @Author suran
 * @Date 2025/8/23 17:24
 */
public enum RunApiContentComplexType {
    COMPLETE(1, "complete"),
    SIMPLE(2,"simple");

    private Integer type;
    private String typeName;

    RunApiContentComplexType(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
    public Integer getType() {
        return type;
    }
    public String getTypeName() {
        return typeName;
    }
    public static RunApiContentComplexType getRunApiContentComplexType(Integer type) {
        for (RunApiContentComplexType runApiContentComplexType : RunApiContentComplexType.values()) {
            if (runApiContentComplexType.getType().equals(type)) {
                return runApiContentComplexType;
            }
        }
        return null;
    }
}
