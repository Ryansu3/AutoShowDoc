package com.cmcc.littlec.autoshowdoc.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: autorun
 * @ClassName: ApiParam
 * @author: suran
 * @create: 2025-08-13 15:25
 */

public class ApiParam {
    private String name;
    private String type;
    // 可选描述
    private String description = "";
    // 默认必填
    private boolean required = true;
    // 子参数（用于对象类型参数的嵌套字段）
    private List<ApiParam> children = new ArrayList<>();
    // 是否是对象类型的参数
    private boolean isObject = false;
    private boolean isCollection = false;

    // 层级缩进
    private int level = 0;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<ApiParam> getChildren() {
        return children;
    }

    public void setChildren(List<ApiParam> children) {
        this.children = children;
    }

    public boolean isObject() {
        return isObject;
    }

    public void setObject(boolean object) {
        isObject = object;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}