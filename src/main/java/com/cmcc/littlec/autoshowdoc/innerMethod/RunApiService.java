package com.cmcc.littlec.autoshowdoc.innerMethod;

import com.cmcc.littlec.autoshowdoc.config.AppSettings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * @program: autorun
 * @ClassName: RunApiService
 * @author: suran
 * @create: 2025-08-15 14:06
 */
public class RunApiService {

    private static final String SHOWDOC_API = "%s/server/?s=/api/open/fromComments";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 上传runapi数据
     * @Author Ryansu3
     * @Description 上传runapi数据
     * @Date 2025/8/19
     * [content, project] void
     */
    public boolean uploadDocument(String methodTitle, String content, Project project) {
        AppSettings settings = AppSettings.getInstance(project);
        try {
            // 构建表单请求体
            RequestBody formBody = new FormBody.Builder()
                    .add("from", "shell")
                    .add("api_key", settings.getApiKey())
                    .add("api_token", settings.getToken())
                    .add("content", content)
                    .build();

            // 构建完整的API URL
            String apiUrl = String.format(SHOWDOC_API, settings.getServerHost());

            // 构建请求
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(formBody)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .build();

            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return true;
                } else {
                    showNotification(project, methodTitle + "文档上传失败",
                            "上传失败，状态码: " + response.code(), NotificationType.ERROR);
                    return false;
                }
            }
        } catch (IOException e) {
            showNotification(project, methodTitle + "文档上传异常",
                    "上传过程中发生异常: " + e.getMessage(), NotificationType.ERROR);
            return false;
        }
    }

    private void showNotification(Project project, String title, String content, NotificationType type) {
        Notification notification = new Notification(
                "AutoShowDoc",
                title,
                content,
                type
        );
        Notifications.Bus.notify(notification, project);
    }
}