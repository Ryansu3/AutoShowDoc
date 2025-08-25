package com.cmcc.littlec.autoshowdoc.config;

import com.cmcc.littlec.autoshowdoc.ui.SettingsPanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nls;

import javax.swing.JComponent;


/**
 * @program: autorun
 * @ClassName: SettingsConfigurable
 * @author: suran
 * @create: 2025-08-13 15:45
 */
public class SettingsConfigurable implements Configurable {
    private SettingsPanel settingsPanel;
    private Project project;

    // 设置窗口中显示的标题
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AutoShowDoc";
    }

    // 创建配置面板
    @Override
    public JComponent createComponent() {
        // 在创建组件时获取当前活动项目
        project = getActiveProject();
        if (project != null) {
            settingsPanel = new SettingsPanel(project);
        }
        return settingsPanel != null ? settingsPanel.getPanel() : null;
    }

    // 检查用户是否修改了设置（用于启用Apply按钮）
    @Override
    public boolean isModified() {
        AppSettings settings = AppSettings.getInstance(project);
        return !settingsPanel.getServerHost().equals(settings.getServerHost()) ||
                !settingsPanel.getApiKey().equals(settings.getApiKey()) ||
                !settingsPanel.getToken().equals(settings.getToken()) ||
                !settingsPanel.getRelativePath().equals(settings.getRelativePath()) ||
                !settingsPanel.getContextPath().equals(settings.getContextPath());
    }

    // 用户点击Apply或OK时调用
    @Override
    public void apply() throws ConfigurationException{
        // 验证输入
        if (settingsPanel.getServerHost().trim().isEmpty()) {
            throw new ConfigurationException("服务器地址不能为空");
        }

        if (settingsPanel.getApiKey().trim().isEmpty()) {
            throw new ConfigurationException("API Key 不能为空");
        }

        if (settingsPanel.getToken().trim().isEmpty()) {
            throw new ConfigurationException("API Token 不能为空");
        }
        AppSettings settings = AppSettings.getInstance(project);
        settings.setServerHost(settingsPanel.getServerHost());
        settings.setApiKey(settingsPanel.getApiKey());
        settings.setToken(settingsPanel.getToken());
        settings.setRelativePath(settingsPanel.getRelativePath());
        settings.setContextPath(settingsPanel.getContextPath());
    }

    // 重置表单数据
    @Override
    public void reset() {
        AppSettings settings = AppSettings.getInstance(project);
        settingsPanel.setServerHost(settings.getServerHost());
        settingsPanel.setApiKey(settings.getApiKey());
        settingsPanel.setToken(settings.getToken());
        settingsPanel.setRelativePath(settings.getRelativePath());
        settingsPanel.setContextPath(settings.getContextPath());
    }

    // 辅助方法：获取当前活动项目
    private Project getActiveProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length > 0) {
            // 返回第一个打开的项目，或者根据需要选择特定项目
            return projects[0];
        }
        return null;
    }
}