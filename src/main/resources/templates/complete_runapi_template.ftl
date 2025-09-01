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
<#if param.level gte 1>
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
* @json_param <#if api.params?has_content><#macro renderJsonParam param level=1><#if param.isCollection()><#if param.children?has_content>[{<#list param.children as child>"${child.name}": <#if child.isObject?? && child.isObject()><@renderJsonParam child level + 1/><#elseif child.type == "int" || child.type == "Integer" || child.type == "long" || child.type == "Long" || child.type == "short" || child.type == "Short" || child.type == "byte" || child.type == "Byte" || child.type == "float" || child.type == "Float" || child.type == "double" || child.type == "Double">0<#else>""</#if><#if child_has_next>,</#if></#list>}]<#else>[]</#if><#elseif param.isObject()><#if param.children?has_content>{<#list param.children as child>"${child.name}": <#if child.isObject?? && child.isObject()><@renderJsonParam child level + 1/><#elseif child.type == "int" || child.type == "Integer" || child.type == "long" || child.type == "Long" || child.type == "short" || child.type == "Short" || child.type == "byte" || child.type == "Byte" || child.type == "float" || child.type == "Float" || child.type == "double" || child.type == "Double">0<#else>""</#if><#if child_has_next>,</#if></#list>}<#else>{}</#if><#else><#if param.type == "int" || param.type == "Integer" || param.type == "long" || param.type == "Long" || param.type == "short" || param.type == "Short" || param.type == "byte" || param.type == "Byte" || param.type == "float" || param.type == "Float" || param.type == "double" || param.type == "Double">0<#else>""</#if></#if></#macro><#list api.params as param><#if param.isObject?? && param.isObject()><@renderJsonParam param/></#if></#list></#if></#if>
* @return {}
* @remark 无
* @number 99
*/