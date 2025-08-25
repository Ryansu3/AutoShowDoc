package com.cmcc.littlec.autoshowdoc.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: autorun
 * @ClassName: ApiModel
 * @author: suran
 * @create: 2025-08-13 15:23
 */
public class ApiModel {
    private String title;
    private String controllerTitle;
    private String dictionary;
    private String description;
    private String methodName;
    private String path;
    private String contextPath;
    // 默认请求类型为GET
    private String requestType = "GET";
    private List<ApiParam> params = new ArrayList<>();

    private Integer paramDepth;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if(StringUtils.isNotBlank(this.path)){
            String formattedPath = Arrays.stream(path.split("/")).filter(StringUtils::isNotBlank).collect(Collectors.joining("/"));
            this.path = this.path + "/" + formattedPath;
        }else{
            this.path = Arrays.stream(path.split("/")).filter(StringUtils::isNotBlank).collect(Collectors.joining("/"));
        }
    }

    public List<ApiParam> getParams() {
        return params;
    }

    public void setParams(List<ApiParam> params) {
        this.params = params;
    }

    public void addParam(ApiParam param) {
        this.params.add(param);
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getControllerTitle() {
        return controllerTitle;
    }

    public void setControllerTitle(String controllerTitle) {
        this.controllerTitle = controllerTitle;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public Integer getParamDepth() {
        return paramDepth;
    }

    public void setParamDepth(Integer paramDepth) {
        this.paramDepth = paramDepth;
    }
}