package com.cmcc.littlec.autoshowdoc.innerMethod;

import com.cmcc.littlec.autoshowdoc.config.AppSettings;
import com.cmcc.littlec.autoshowdoc.entity.ApiModel;
import com.cmcc.littlec.autoshowdoc.entity.ApiParam;
import com.cmcc.littlec.autoshowdoc.entity.ComplexTypeChoiceDialog;
import com.cmcc.littlec.autoshowdoc.entity.RunApiContentComplexType;
import com.cmcc.littlec.autoshowdoc.entity.UploadResult;
import com.cmcc.littlec.autoshowdoc.util.JavaDocUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTypesUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiDocGenerator {
    private static final Logger LOG = Logger.getInstance(ApiDocGenerator.class);

    private final Project project;
    private final ShowDocService showDocService;
    private final RunApiService runApiService;
    private final Configuration freemarkerConfig;
    private Integer depth = 0;
    // 用于存储批量操作时的选择
    private RunApiContentComplexType batchChoice = null;
    private boolean applyToAll = false;
    private static final Set<String> JSON_OBJECT_TYPES = Set.of(
            "com.alibaba.fastjson.JSONObject",
            "com.alibaba.fastjson2.JSONObject",
            "com.google.gson.JsonObject",
            "org.json.JSONObject",
            "net.sf.json.JSONObject"
    );

    public ApiDocGenerator(Project project) {
        this.project = project;
        this.showDocService = new ShowDocService();
        this.runApiService = new RunApiService();
        this.freemarkerConfig = createFreemarkerConfig();
    }

    /**
     * 生成doc
     * @Author Ryansu3
     * @Description 生成doc
     * @Date 2025/8/19
     * [method] void
     */
    public UploadResult generateDocs(PsiMethod method) {
        UploadResult result = new UploadResult(method.getName());
        // 1. 解析方法信息
        ApiModel apiModel = parseMethod(method);

        // 2. 生成ShowDoc格式
//        String showDocContent = generateShowDocContent(apiModel);

        // 3.1 根据apiModel的参数深度，如果深度大于1,则弹出选项，让用户选择是生成完整的参数文档，还是生成精简的（即只有1层参数深度的文档）
        RunApiContentComplexType type = generateRunApiContentComplexType(apiModel);
        String runApiContent;
        if(RunApiContentComplexType.COMPLETE.getType().equals(type.getType())){
            // 3.2(1) 生成完整的RunAPI调试内容
            runApiContent = generateCompleteRunApiContent(apiModel);
        }else {
            // 3.2(2) 生成精简的RunAPI调试内容
            runApiContent = generateSimpleRunApiContent(apiModel);
        }

        // 4. 上传到ShowDoc
//        uploadToShowDoc(apiModel.getDictionary(), apiModel.getTitle(), showDocContent);

        // 5. 保存RunAPI配置文件
        boolean runApiSuccess = uploadRunApiContent(apiModel.getTitle(), runApiContent);
        result.setRunApiSuccess(runApiSuccess);
        return result;
    }

    /**
     * 生成精简runapi文档内容
     * @Author Ryansu3
     * @Description 生成精简runapi文档内容
     * @Date 2025/8/19
     * [apiModel] java.lang.String
     */
    public String generateCompleteRunApiContent(ApiModel apiModel) {
        try {
            Template template = freemarkerConfig.getTemplate("complete_runapi_template.ftl");
            return processTemplate(template, apiModel);
        } catch (IOException | TemplateException e) {
            LOG.error("生成完整RunAPI内容失败", e);
            return "生成完整RunAPI内容失败: " + e.getMessage();
        }
    }

    /**
     * 生成完整runapi文档内容
     * @Author Ryansu3
     * @Description 生成完整runapi文档内容
     * @Date 2025/8/19
     * [apiModel] java.lang.String
     */
    private String generateSimpleRunApiContent(ApiModel apiModel) {
        try {
            Template template = freemarkerConfig.getTemplate("simple_runapi_template.ftl");
            return processTemplate(template, apiModel);
        } catch (IOException | TemplateException e) {
            LOG.error("生成精简RunAPI内容失败", e);
            return "生成精简RunAPI内容失败: " + e.getMessage();
        }
    }

    /**
     * 上传文档至showdoc
     * @Author Ryansu3
     * @Description 上传文档至showdoc
     * @Date 2025/8/19
     * [dictionary, title, markdownContent] void
     */
    public void uploadToShowDoc(String dictionary, String title, String markdownContent) {
        try {
            showDocService.uploadDocument(dictionary, title, markdownContent, project);
            LOG.info("ShowDoc文档上传成功");
        } catch (Exception e) {
            LOG.error("ShowDoc文档上传失败", e);
        }
    }

    /**
     * 上传文档至runapi
     * @Author Ryansu3
     * @Description 上传文档至runapi
     * @Date 2025/8/19
     * [dictionary, title, markdownContent] void
     */
    public boolean uploadRunApiContent(String methodTitle, String runApiContent) {
        try {
            boolean success = runApiService.uploadDocument(methodTitle, runApiContent, project);
            if (success) {
                LOG.info("RunApi文档上传成功");
            } else {
                LOG.error("RunApi文档上传失败");
            }
            return success;
        } catch (Exception e) {
            LOG.error("RunApi文档上传失败", e);
            return false;
        }
    }

    /**
     * 重置批量操作状态
     * 在批量处理开始前调用此方法
     */
    public void resetBatchOperationState() {
        this.batchChoice = null;
        this.applyToAll = false;
    }

    /**
     * 创建freemarker配置对象
     * @Author Ryansu3
     * @Description 创建freemarker配置对象
     * @Date 2025/8/19
     * [] freemarker.template.Configuration
     */
    private Configuration createFreemarkerConfig() {
        Configuration cfg = new Configuration(new Version(2, 3, 32));
        cfg.setClassForTemplateLoading(getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    /**
     * 通过psimethod生成apiModel对象，用于后续runapi、showdoc模板提取方法信息
     * @Author Ryansu3
     * @Description 通过psimethod生成apiModel对象，用于后续runapi、showdoc模板提取方法信息
     * @Date 2025/8/19
     * [] freemarker.template.Configuration
     */
    private ApiModel parseMethod(PsiMethod method) {
        AppSettings settings = AppSettings.getInstance(project);
        JavaDocUtils javaDocUtils = new JavaDocUtils(LOG);
        ApiModel model = new ApiModel();
        model.setMethodName(method.getName());
        // 获取方法名
        model.setTitle(method.getName());

        // 尝试从 JavaDoc 中获取接口名
        String methodTitleName = javaDocUtils.getMethodNameFromJavaDoc(method);
        if (methodTitleName != null && !methodTitleName.isEmpty()) {
            model.setTitle(methodTitleName);
        }

        // 获取方法所在类的JavaDoc注释第一行作为controllerTitle
        PsiClass containingClass = method.getContainingClass();
        if (containingClass != null) {
            String controllerTitle = javaDocUtils.getControllerTitleFromJavaDoc(containingClass);
            model.setControllerTitle(controllerTitle);
        }

        // 默认请求类型为GET
        String requestType = "GET";

        // 同时检查方法和类级别的注解
        PsiAnnotation[] methodAnnotations = method.getAnnotations();
        PsiAnnotation[] classAnnotations = containingClass != null ? containingClass.getAnnotations() : new PsiAnnotation[0];

        // 先处理类级别的路径注解
        for (PsiAnnotation annotation : classAnnotations) {
            String qualifiedName = annotation.getQualifiedName();
            if ("RequestMapping".equals(qualifiedName) ||
                    "org.springframework.web.bind.annotation.RequestMapping".equals(qualifiedName) ||
                    qualifiedName.endsWith(".RequestMapping")) {
                String basePath = getAnnotationValue(annotation, "value", "path");
                // 保存基础路径，后续与方法路径组合
                model.setPath(basePath);
            }
        }

        // 再处理方法级别的路径注解
        for (PsiAnnotation annotation : methodAnnotations) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null) {
                switch (qualifiedName) {
                    case "org.springframework.web.bind.annotation.RequestMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        // RequestMapping 可能有 method 属性指定请求类型
                        requestType = extractRequestTypeFromRequestMapping(annotation, "GET");
                        break;
                    case "org.springframework.web.bind.annotation.GetMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        requestType = "GET";
                        break;
                    case "org.springframework.web.bind.annotation.PostMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        requestType = "POST";
                        break;
                    case "org.springframework.web.bind.annotation.PutMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        requestType = "PUT";
                        break;
                    case "org.springframework.web.bind.annotation.DeleteMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        requestType = "DELETE";
                        break;
                    case "org.springframework.web.bind.annotation.PatchMapping":
                        model.setPath(getAnnotationValue(annotation, "value", "path"));
                        requestType = "PATCH";
                        break;
                }
            }
        }
        // 设置请求类型
        model.setRequestType(requestType);

        // 解析参数
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            List<ApiParam> params = parseParameter(parameter, requestType);
            for (ApiParam param : params) {
                model.addParam(param);
            }
        }

        if(StringUtils.isNotBlank(settings.getContextPath())){
            if(settings.getContextPath().endsWith("/")){
                model.setContextPath(settings.getContextPath());
            }else{
                model.setContextPath(settings.getContextPath() + "/");
            }
        }

        String dictionary = Arrays.stream((settings.getRelativePath() +"/"+ (StringUtils.isBlank(model.getControllerTitle())?"":model.getControllerTitle())).split("/")).filter(StringUtils::isNotBlank).collect(Collectors.joining("/"));
        model.setDictionary(dictionary);
        model.setParamDepth(depth);
        return model;
    }

    /**
     * 获取注解的value对应的字符串
     * @Author Ryansu3
     * @Description 获取注解的value对应的字符串
     * @Date 2025/8/19
     * [annotation, attributeNames] java.lang.String
     */
    private String getAnnotationValue(PsiAnnotation annotation, String... attributeNames) {
        for (String attr : attributeNames) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue(attr);
            if (value != null) {
                // 去除引号
                String text = value.getText().replaceAll("\"", "");
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }
        return "";
    }

    /**
     * 生成showdoc文档内容
     * @Author Ryansu3
     * @Description 生成showdoc文档内容
     * @Date 2025/8/19
     * [apiModel] java.lang.String
     */
    private String generateShowDocContent(ApiModel apiModel) {
        try {
            Template template = freemarkerConfig.getTemplate("showdoc_template.ftl");
            return processTemplate(template, apiModel);
        } catch (IOException | TemplateException e) {
            LOG.error("生成ShowDoc内容失败", e);
            return "生成ShowDoc内容失败: " + e.getMessage();
        }
    }

    private String processTemplate(Template template, ApiModel apiModel)
            throws TemplateException, IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("api", apiModel);

        StringWriter writer = new StringWriter();
        template.process(data, writer);
        return writer.toString();
    }


    /**
     * 从 @RequestMapping 注解中提取请求类型
     * @param annotation RequestMapping 注解
     * @param defaultType 默认请求类型
     * @return 请求类型字符串
     */
    private String extractRequestTypeFromRequestMapping(PsiAnnotation annotation, String defaultType) {
        String methodValue = getAnnotationValue(annotation, "method");
        if (!methodValue.isEmpty()) {
            // 处理 method = RequestMethod.POST 这样的情况
            if (methodValue.contains("RequestMethod.")) {
                return methodValue.substring(methodValue.lastIndexOf('.') + 1);
            } else if (methodValue.contains("RequestMethod")) {
                // 处理 RequestMethod.POST 这样的情况
                String[] parts = methodValue.split("\\.");
                if (parts.length > 1) {
                    return parts[parts.length - 1];
                }
            } else {
                // 直接返回方法名
                return methodValue.toUpperCase();
            }
        }
        return defaultType;
    }

    /**
     * 解析方法参数
     * @param parameter 方法参数
     * @param requestType 请求类型
     * @return ApiParam列表
     */
    private List<ApiParam> parseParameter(PsiParameter parameter, String requestType) {
        List<ApiParam> params = new ArrayList<>();
        ApiParam param = new ApiParam();
        param.setName(parameter.getName());
        param.setType(parameter.getType().getPresentableText());

        // 检查是否有@RequestBody注解（通常用于POST请求的对象参数）
        boolean isRequestBody = false;
        for (PsiAnnotation paramAnnotation : parameter.getAnnotations()) {
            String paramQualifiedName = paramAnnotation.getQualifiedName();
            if (paramQualifiedName != null) {
                if (paramQualifiedName.equals("org.springframework.web.bind.annotation.RequestParam")) {
                    String paramName = getAnnotationValue(paramAnnotation, "value", "name");
                    if (paramName != null && !paramName.isEmpty()) {
                        param.setName(paramName);
                    }
                    // 检查 required 属性
                    String requiredValue = getAnnotationValue(paramAnnotation, "required");
                    if (!requiredValue.isEmpty()) {
                        param.setRequired(Boolean.parseBoolean(requiredValue));
                    }
                } else if (paramQualifiedName.equals("org.springframework.web.bind.annotation.RequestBody")) {
                    // RequestBody 默认是必需的
                    param.setRequired(true);
                    isRequestBody = true;
                } else if (paramQualifiedName.equals("org.springframework.web.bind.annotation.RequestHeader")) {
                    String headerName = getAnnotationValue(paramAnnotation, "value", "name");
                    if (headerName != null && !headerName.isEmpty()) {
                        param.setName(headerName);
                    }
                    // 检查 required 属性
                    String requiredValue = getAnnotationValue(paramAnnotation, "required");
                    if (!requiredValue.isEmpty()) {
                        param.setRequired(Boolean.parseBoolean(requiredValue));
                    }
                }
            }
        }

        // 如果是POST方法且参数有@RequestBody注解，则递归解析对象字段
        if ("POST".equals(requestType) && isRequestBody) {
            param.setObject(true);
            // 如果是集合或数组，设置当前
            PsiType elementType = getCollectionElementType(parameter.getType());
            if (elementType != null) {
                param.setCollection(true);
            }
            List<ApiParam> children = parseObjectFields(parameter.getType(), param, 0);
            param.setChildren(children);
        }

        params.add(param);
        return params;
    }

    /**
     * 递归解析对象类型的字段
     * @param psiType 对象类型
     * @param parentParam 父级参数
     * @param level 当前层级
     * @return 对象字段列表
     */
    private List<ApiParam> parseObjectFields(PsiType psiType, ApiParam parentParam, int level) {
        List<ApiParam> fields = new ArrayList<>();

        try {
            // 1. 首先检查是否为数组或集合类型
            // 获取数组或集合中的泛型类型
            PsiType elementType = getCollectionElementType(psiType);
            if (elementType != null) {
                // 如果泛型仍然是一个数组或集合，我们需要继续解析其元素类型
                if(getCollectionElementType(elementType)!=null){
                    ApiParam fieldParam = new ApiParam();
                    PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
                    fieldParam.setName(psiClass.getName());
                    fieldParam.setType(psiClass.getName());
                    fieldParam.setLevel(level + 1); // 层级加1
                    fieldParam.setObject(true);
                    fieldParam.setCollection(true);
                    // 递归解析子对象的字段
                    List<ApiParam> childFields = parseObjectFields(elementType, fieldParam, level + 1);
                    fieldParam.getChildren().addAll(childFields);
                    fields.add(fieldParam);
                    return fields;
                }
                else if (isObjectType(elementType)) {
                    // 元素是对象类型，递归解析
                    return parseObjectFields(elementType, parentParam, level);
                }else{
                    // 如果是基础类型，则直接返回空
                    return null;
                }
            }
            // 2. 非数组/集合的情况
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            if (psiClass == null) {
                return fields;
            }
            // 如果为jsonObject，无法解析jsonObject内的字段名，则返回null
            if (JSON_OBJECT_TYPES.contains(psiClass.getQualifiedName())) {
                return null;
            }

            // 获取类的所有字段
            PsiField[] psiFields = psiClass.getAllFields();

            for (PsiField field : psiFields) {
                // 跳过静态字段
                if (field.hasModifierProperty(PsiModifier.STATIC)) {
                    continue;
                }

                ApiParam fieldParam = new ApiParam();
                fieldParam.setName(field.getName());
                fieldParam.setType(field.getType().getPresentableText());
                fieldParam.setLevel(level + 1); // 层级加1

                // 尝试从字段的JavaDoc或注解中获取描述
                String fieldDescription = getFieldDescription(field);
                fieldParam.setDescription(fieldDescription);

                // 默认设置为必填，可以根据注解调整
                fieldParam.setRequired(true);

                // 检查字段是否有相关的注解（如@NotNull, @NotBlank等）
                checkFieldAnnotations(field, fieldParam);

                // 如果字段本身是对象类型，递归解析
                if (isObjectType(field.getType())) {
                    fieldParam.setObject(true);
                    // 如果字段是collection，则设置属性为collection
                    if(getCollectionElementType(field.getType())!=null){
                        fieldParam.setCollection(true);
                    }
                    // 递归解析子对象的字段
                    List<ApiParam> childFields = parseObjectFields(field.getType(), fieldParam, level + 1);
                    if(!CollectionUtils.isEmpty(childFields)){
                        fieldParam.getChildren().addAll(childFields);
                    }
                }

                fields.add(fieldParam);
            }
        } catch (Exception e) {
            LOG.warn("解析对象字段时出错: " + e.getMessage(), e);
        }
        depth = Math.max(depth, level+1);
        return fields;
    }

    /**
     * 检查字段是否有相关的验证注解
     * @param field 字段
     * @param fieldParam 字段参数
     */
    private void checkFieldAnnotations(PsiField field, ApiParam fieldParam) {
        for (PsiAnnotation annotation : field.getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null) {
                switch (qualifiedName) {
                    case "javax.validation.constraints.NotNull":
                    case "jakarta.validation.constraints.NotNull":
                        fieldParam.setRequired(true);
                        break;
                    case "javax.validation.constraints.NotBlank":
                    case "jakarta.validation.constraints.NotBlank":
                        fieldParam.setRequired(true);
                        break;
                    case "javax.validation.constraints.NotEmpty":
                    case "jakarta.validation.constraints.NotEmpty":
                        fieldParam.setRequired(true);
                        break;
                }
            }
        }
    }

    /**
     * 获取字段的描述信息，从JavaDoc或注解中提取
     * @param field PsiField对象
     * @return 字段描述
     */
    private String getFieldDescription(PsiField field) {
        try {
            // 首先尝试从JavaDoc中获取描述
            PsiDocComment docComment = field.getDocComment();
            if (docComment != null) {
                // 获取JavaDoc的主要描述内容
                StringBuilder description = new StringBuilder();

                // 遍历JavaDoc的描述元素
                for (PsiElement element : docComment.getDescriptionElements()) {
                    if (element instanceof PsiDocToken) {
                        PsiDocToken token = (PsiDocToken) element;
                        // 只处理文档数据内容
                        if (token.getTokenType() == JavaDocTokenType.DOC_COMMENT_DATA) {
                            String text = token.getText();
                            description.append(text);
                        }
                    }
                }

                // 清理和格式化文本
                String cleanDescription = description.toString()
                        .replaceAll("\n\\s*\\*\n", "\n")  // 移除空行
                        .replaceAll("^\\s*\\*\\s*", "")   // 移除行首的星号和空格
                        .replaceAll("\\s*\\*\\s*$", "")   // 移除行尾的星号和空格
                        .trim();

                if (!cleanDescription.isEmpty()) {
                    return cleanDescription;
                }
            }

            // 如果没有JavaDoc，尝试从常见的注解中获取描述
            PsiAnnotation[] annotations = field.getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null) {
                    // 处理 Swagger 注解
                    if (qualifiedName.equals("io.swagger.annotations.ApiModelProperty")) {
                        String value = getAnnotationValue(annotation, "value");
                        if (!value.isEmpty()) {
                            return value;
                        }
                    }
                    // 处理 Jakarta Validation 注解
                    else if (qualifiedName.equals("jakarta.validation.constraints.NotBlank") ||
                            qualifiedName.equals("javax.validation.constraints.NotBlank")) {
                        // 可以从 message 属性获取描述
                        String message = getAnnotationValue(annotation, "message");
                        if (!message.isEmpty()) {
                            return "不能为空字符串 - " + message;
                        }
                    }
                    else if (qualifiedName.equals("jakarta.validation.constraints.NotNull") ||
                            qualifiedName.equals("javax.validation.constraints.NotNull")) {
                        String message = getAnnotationValue(annotation, "message");
                        if (!message.isEmpty()) {
                            return "不能为空 - " + message;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("获取字段描述时出错: " + e.getMessage(), e);
        }

        return ""; // 没有找到描述信息
    }

    /**
     * 判断PsiType是否为对象类型（非基本类型和其包装类）
     * @param psiType PsiType类型
     * @return 是否为对象类型
     */
    private boolean isObjectType(PsiType psiType) {
        if (psiType == null) {
            return false;
        }

        // 获取类型的标准名称
        String canonicalText = psiType.getCanonicalText();

        // 常见的非对象类型（基本类型及其包装类、字符串等）
        Set<String> nonObjectTypes = Set.of(
                "boolean", "byte", "char", "short", "int", "long", "float", "double",
                "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short",
                "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double",
                "java.lang.String", "java.util.Date", "java.time.LocalDate", "java.time.LocalDateTime",
                "java.time.LocalTime", "java.math.BigDecimal", "java.math.BigInteger"
        );

        // 如果在非对象类型列表中，则不是对象类型
        if (nonObjectTypes.contains(canonicalText)) {
            return false;
        }

        // 对于其他类型，如果能解析到PsiClass且不是枚举，则认为是对象类型
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
        if (psiClass != null) {
            // 枚举类型不视为对象类型（因为通常作为简单值处理）
            if (psiClass.isEnum()) {
                return false;
            }

            // 接口和类都视为对象类型
            return psiClass.isInterface() || psiClass instanceof PsiClass;
        }

        // 默认情况下，如果无法确定，不视为对象类型
        return false;
    }

    /**
     * 获取数组或集合的元素类型
     * @param psiType 数组或集合类型
     * @return 元素类型，如果psiType不是数组或集合则返回null
     */
    private PsiType getCollectionElementType(PsiType psiType) {
        // 处理数组类型
        if (psiType instanceof PsiArrayType) {
            PsiArrayType arrayType = (PsiArrayType) psiType;
            return arrayType.getComponentType();
        }

        // 处理集合类型
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();

            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                if (qualifiedName != null) {
                    // 检查是否为常见的集合类型
                    if (qualifiedName.startsWith("java.util.Collection") ||
                            qualifiedName.startsWith("java.util.List") ||
                            qualifiedName.startsWith("java.util.Set")) {

                        // 获取泛型参数
                        PsiType[] parameters = classType.getParameters();
                        if (parameters.length > 0) {
                            return parameters[0]; // 集合通常只有一个泛型参数
                        }
                    } else if (qualifiedName.startsWith("java.util.Map")) {
                        // Map类型有两个泛型参数 K, V，我们通常关心值类型 V
                        PsiType[] parameters = classType.getParameters();
                        if (parameters.length > 1) {
                            return parameters[1]; // 返回值类型
                        } else if (parameters.length > 0) {
                            return parameters[0]; // 返回键类型
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据用户选择返回生成文档简化程度类型
     * @Author Ryansu3
     * @Description 根据用户选择返回生成文档简化程度类型
     * @Date 2025/8/23
     * [paramDepth] com.cmcc.littlec.autoshowdoc.entity.RunApiContentComplexType
     */
    private RunApiContentComplexType generateRunApiContentComplexType(ApiModel apiModel) {
        // 如果是批量操作且用户选择了应用到所有方法
        if (batchChoice != null && applyToAll) {
            return batchChoice;
        }

        if(apiModel.getParamDepth()<=1){
            return RunApiContentComplexType.SIMPLE;
        }

        // 创建并显示自定义对话框
        ComplexTypeChoiceDialog dialog = new ComplexTypeChoiceDialog(apiModel.getTitle());
        dialog.show();
        if (dialog.isYes()) {
            // 用户选择了"是"
            RunApiContentComplexType choice = RunApiContentComplexType.SIMPLE;

            // 检查用户是否勾选了"对当前次批量生成保持该操作"
            if (dialog.isApplyToAll()) {
                this.applyToAll = true;
                this.batchChoice = choice;
            }
            return choice;
        } else if (dialog.isNo()) {
            // 用户选择了"否"
            RunApiContentComplexType choice = RunApiContentComplexType.COMPLETE;

            // 检查用户是否勾选了"对当前次批量生成保持该操作"
            if (dialog.isApplyToAll()) {
                this.applyToAll = true;
                this.batchChoice = choice;
            }
            return choice;
        }

        // 用户取消操作，默认返回简化版
        return RunApiContentComplexType.SIMPLE;
    }
}