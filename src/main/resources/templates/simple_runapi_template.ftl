/**
* showdoc
* @catalog ${api.dictionary}
* @title ${api.title}
* @description <#if api.description?has_content>${api.description}<#else>无</#if>
* @method ${api.requestType}
* @url ${api.contextPath}${api.path}
<#if api.requestType == "GET">
<#list api.params as param>* @param ${param.name} ${param.required?string("必选", "可选")} ${param.type} <#if param.description?has_content>${param.description}<#else>无</#if>
</#list>
<#elseif api.requestType == "POST">
<#macro renderParam param>
<#if param.level == 1>
* @param ${param.name} ${param.required?string("必选", "可选")} <#if param.isObject?? && param.isObject() && param.isCollection()>array<#elseif param.isObject?? && param.isObject()>object<#else>${param.type}</#if> <#if param.description?has_content>${param.description}<#else>无</#if>
</#if>
<#if param.isObject?? && param.isObject()>
<#list param.children as child>
<@renderParam child />
</#list>
</#if>
</#macro>
<#list api.params as param>
<#if param.isObject?? && !param.isObject() && !param.isCollection()>
* @param ${param.name} ${param.required?string("必选", "可选")} ${param.type} <#if param.description?has_content>${param.description}<#else>无</#if>
</#if>
<@renderParam param />
</#list>
* @json_param <#if api.params?has_content><#macro renderJsonParam param hasMore=false>"${param.name}": <#if param.isObject?? && param.isObject() && param.isCollection()>[]<#elseif param.isObject?? && param.isObject()>{}<#elseif param.type == "int" || param.type == "Integer" || param.type == "long" || param.type == "Long" || param.type == "short" || param.type == "Short" || param.type == "byte" || param.type == "Byte" || param.type == "float" || param.type == "Float" || param.type == "double" || param.type == "Double">0<#else>""</#if><#if hasMore>,</#if></#macro><#list api.params as param><#if param.isObject?? && param.isObject()>{<#list param.children as child><@renderJsonParam child child_has_next/></#list>}</#if></#list></#if></#if>
* @return {}
* @remark 无
* @number 99
*/