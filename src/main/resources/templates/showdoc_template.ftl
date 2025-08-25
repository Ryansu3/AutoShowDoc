##### 简要描述
<#if api.description?has_content>${api.description}<#else>无</#if>
##### 请求URL
- `${api.contextPath}${api.path}`

##### 请求方式
- ${api.requestType}

##### 请求参数

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
<#macro renderParam param>
<#assign indent = "">
<#list 0..param.level as i><#assign indent = indent + " "></#list>
|${indent}${param.name} |${param.required?string("是", "否")} |${param.type} |<#if param.description?has_content>${param.description}<#else>无</#if> |
<#if param.isObject?? && param.isObject()>
<#list param.children as child>
<@renderParam child />
</#list>
</#if>
</#macro>
<#list api.params as param>
<@renderParam param />
</#list>

##### 请求参数示例
```
无
```

##### 返回参数说明

|参数名|类型|说明|
|:-----  |:-----|-----|

##### 返回示例
```
无
```

##### 备注