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

public class ShowDocService {
    private static final String SHOWDOC_API = "%s/server/index.php?s=/api/item/updateByApi";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 上传showdocwendang
     * @Author Ryansu3
     * @Description 上传showdocwendang
     * @Date 2025/8/19
     * [dictionary, title, content, project] void
     */
    public void uploadDocument(String dictionary, String title, String content, Project project) {
        AppSettings settings = AppSettings.getInstance(project);

        if (settings.getApiKey().isEmpty() || settings.getToken().isEmpty()) {
            showNotification(project, "ShowDoc配置不完整",
                    "请先配置API Key和Token", NotificationType.WARNING);
            return;
        }
        if (settings.getServerHost().isEmpty()) {
            showNotification(project, "ShowDoc配置不完整",
                    "请先配置ShowDoc服务器地址", NotificationType.WARNING);
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("api_key", settings.getApiKey())
                .add("api_token", settings.getToken())
                .add("cat_name", dictionary)
                .add("page_title", title)
                .add("page_content", content)
                .build();

        Request request = new Request.Builder()
                .url(String.format(SHOWDOC_API, settings.getServerHost()))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP错误: " + response.code());
            }

            String responseBody = response.body().string();
            if (!responseBody.contains("\"error_code\":0")) {
                throw new IOException("ShowDoc API错误: " + responseBody);
            }

            showNotification(project, "ShowDoc上传成功",
                    "文档已成功同步到ShowDoc", NotificationType.INFORMATION);
        } catch (IOException e) {
            showNotification(project, "ShowDoc上传失败",
                    "错误: " + e.getMessage(), NotificationType.ERROR);
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