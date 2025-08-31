package com.cmcc.littlec.autoshowdoc.action;

import com.cmcc.littlec.autoshowdoc.config.AppSettings;
import com.cmcc.littlec.autoshowdoc.entity.UploadResult;
import com.cmcc.littlec.autoshowdoc.innerMethod.ApiDocGenerator;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: autorun
 * @ClassName: GenerateDocAction
 * @author: suran
 * @create: 2025-08-13 15:34
 */

public class GenerateDocAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(element instanceof PsiMethod) && !(element instanceof PsiClass)) {
            return;
        }
        // 前置判断必须的配置是否已经完成
        AppSettings settings = AppSettings.getInstance(project);
        if (settings.getApiKey().isEmpty() || settings.getToken().isEmpty()) {
            showNotification(project, "ShowDoc配置不完整",
                    "请先配置API Key和Token", NotificationType.WARNING);
            return ;
        }
        if (settings.getServerHost().isEmpty()) {
            showNotification(project, "ShowDoc配置不完整",
                    "请先配置ShowDoc服务器地址", NotificationType.WARNING);
            return;
        }
        ApiDocGenerator generator = new ApiDocGenerator(project);
        if(element instanceof PsiMethod){
            PsiMethod method = (PsiMethod) element;
            UploadResult uploadResult = generator.generateDocs(method);
            // 单次接口上传成功
            if(uploadResult.isRunApiSuccess()){
                showNotification(project, "文档上传成功",
                        "API文档已成功上传到RunApi", NotificationType.INFORMATION);
            }
        }else if(element instanceof PsiClass){
            PsiClass psiClass = (PsiClass) element;
            PsiMethod[] allMethods = psiClass.getAllMethods();
            // 用于收集操作结果
            List<UploadResult> results = new ArrayList<>();
            for (PsiMethod method : allMethods) {
                // 只处理public方法，并且确保是用户自己定义的方法（而不是继承的方法）
                if (method.hasModifierProperty("public") && isUserDefinedMethod(psiClass, method)) {
                    UploadResult result = generator.generateDocs(method);
                    results.add(result);
                }
            }
            
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        e.getPresentation().setEnabledAndVisible(element instanceof PsiMethod || element instanceof PsiClass);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * 添加辅助方法来判断是否是用户定义的方法
     * @Author Ryansu3
     * @Description 添加辅助方法来判断是否是用户定义的方法
     * @Date 2025/8/29
     * [psiClass, method] boolean
     */
    private boolean isUserDefinedMethod(PsiClass psiClass, PsiMethod method) {
        // 检查方法是否在当前类中定义，而不是继承的
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return false;
        }

        // 判断方法是否在当前类中定义（而不是继承自父类或接口）
        return containingClass.equals(psiClass);
    }

    /**
     * 提示到前台
     * @Author Ryansu3
     * @Description 提示到前台
     * @Date 2025/8/31
     * [project, title, content, type] void
     */
    private void showNotification(Project project, String title, String content, NotificationType type) {
        Notification notification = new Notification(
                "AutoShowDoc",
                title,
                content,
                type
        );
        Notifications.Bus.notify(notification, project);
    }

    /**
     * 显示操作结果的汇总信息
     * @param project 项目对象
     * @param results 操作结果列表
     */
    private void showSummaryResults(Project project, List<UploadResult> results) {
        if (results.isEmpty()) {
            showNotification(project, "上传结果", "没有找到需要处理的公共方法。", NotificationType.INFORMATION);
            return;
        }

        int totalMethods = results.size();
        int runApiSuccessCount = 0;

        StringBuilder details = new StringBuilder();
        details.append("总共处理了 ").append(totalMethods).append(" 个方法:\n\n");

        for (UploadResult result : results) {
            if (result.isRunApiSuccess()) {
                runApiSuccessCount++;
            }

            details.append("方法: ").append(result.getMethodName()).append("\n");
            details.append("  RunAPI: ").append(result.isRunApiSuccess() ? "成功" : "失败").append("\n\n");
        }

        details.append("RunAPI 成功: ").append(runApiSuccessCount).append("/").append(totalMethods);

        showNotification(project, "上传结果", details.toString(), NotificationType.INFORMATION);
    }
}