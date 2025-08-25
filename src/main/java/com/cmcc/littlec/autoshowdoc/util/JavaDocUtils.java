package com.cmcc.littlec.autoshowdoc.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;

/**
 * javaDoc获取信息的方法
 * @program: autorun
 * @ClassName: JavaDocUtils
 * @author: suran
 * @create: 2025-08-14 15:06
 */
public class JavaDocUtils {
    private Logger logger;

    public JavaDocUtils(Logger logger){
        this.logger = logger;
    }

    /**
     * 从方法的JavaDoc中提取接口名称（首行内容）
     * @param method PsiMethod对象
     * @return 接口名称，如果无法获取则返回null
     */
    public String getMethodNameFromJavaDoc(PsiMethod method) {
        try {
            // 获取方法的JavaDoc注释
            PsiDocComment docComment = method.getDocComment();
            if (docComment == null) {
                return null;
            }

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
            String cleanDescription = cleanJavaDocText(description.toString());
            if (cleanDescription.isEmpty()) {
                return null;
            }

            // 获取第一行作为接口名
            String[] lines = cleanDescription.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    return trimmedLine;
                }
            }

            return null;
        } catch (Exception e) {
            // 记录异常但不中断执行
            logger.warn("解析JavaDoc时出错: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从类的JavaDoc中提取控制器标题（首行内容）
     * @param psiClass PsiClass对象
     * @return 控制器标题，如果无法获取则返回null
     */
    public String getControllerTitleFromJavaDoc(PsiClass psiClass) {
        try {
            // 获取类的JavaDoc注释
            PsiDocComment docComment = psiClass.getDocComment();
            if (docComment == null) {
                return null;
            }
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
            String cleanDescription = cleanJavaDocText(description.toString());
            if (cleanDescription.isEmpty()) {
                return null;
            }

            // 获取第一行作为控制器标题
            String[] lines = cleanDescription.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    return trimmedLine;
                }
            }

            return null;
        } catch (Exception e) {
            // 记录异常但不中断执行
            logger.warn("解析类JavaDoc时出错: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 清理JavaDoc文本，移除多余的格式字符
     * @param text 原始JavaDoc文本
     * @return 清理后的文本
     */
    private String cleanJavaDocText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 移除多余的空格和换行
        return text.replaceAll("\n\\s*\\*\n", "\n")  // 移除空行
                .replaceAll("^\\s*\\*\\s*", "")   // 移除行首的星号和空格
                .replaceAll("\\s*\\*\\s*$", "")   // 移除行尾的星号和空格
                .trim();
    }
}