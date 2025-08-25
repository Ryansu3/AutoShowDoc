package com.cmcc.littlec.autoshowdoc.ui;

import com.cmcc.littlec.autoshowdoc.config.AppSettings;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * @program: autorun
 * @ClassName: SettingsPanel
 * @author: suran
 * @create: 2025-08-13 15:32
 */
public class SettingsPanel {
    private JPanel mainPanel;
    private JTextField apiKeyField;
    private JTextField tokenField;
    private JTextField relativePathField;
    private JTextField contextPathField;
    private JTextField serverHostField;

    public SettingsPanel(Project project) {
        AppSettings settings = AppSettings.getInstance(project);

        JPanel mainConfigPanel = new JPanel(new BorderLayout(0, 10));
        mainConfigPanel.setLayout(new BoxLayout(mainConfigPanel, BoxLayout.Y_AXIS));
        mainConfigPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ShowDoc 配置部分
        JPanel showDocPanel = new JPanel(new GridBagLayout());
        showDocPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "ShowDoc 配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Server Host
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel serverHostLabel = new JLabel("Server Host:");
        serverHostLabel.setToolTipText("ShowDoc 平台的服务器地址");
        showDocPanel.add(serverHostLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        serverHostField = new JTextField(settings.getServerHost(), 30);
        serverHostField.setToolTipText("请输入 ShowDoc 私有化部署/公共服务的服务器地址，例如: https://showdoc.example.com");
        showDocPanel.add(serverHostField, gbc);

        // API Key
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel apiKeyLabel = new JLabel("API Key:");
        apiKeyLabel.setToolTipText("ShowDoc 平台的 API Key");
        showDocPanel.add(apiKeyLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        apiKeyField = new JTextField(settings.getApiKey(), 30);
        apiKeyField.setToolTipText("请输入 ShowDoc 平台的 API Key");
        showDocPanel.add(apiKeyField, gbc);

        // API Token
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel tokenLabel = new JLabel("API Token:");
        tokenLabel.setToolTipText("ShowDoc 平台的 API Token");
        showDocPanel.add(tokenLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        tokenField = new JTextField(settings.getToken(), 30);
        tokenField.setToolTipText("请输入 ShowDoc 平台的 API Token");
        showDocPanel.add(tokenField, gbc);

        // 路径配置部分
        JPanel pathPanel = new JPanel(new GridBagLayout());
        pathPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "路径配置"));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Relative Path
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel relativePathLabel = new JLabel("Relative Path:");
        relativePathLabel.setToolTipText("生成文件的相对路径");
        pathPanel.add(relativePathLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        relativePathField = new JTextField(settings.getRelativePath(), 30);
        relativePathField.setToolTipText("请输入生成文件的相对路径，例如: docs/api");
        pathPanel.add(relativePathField, gbc);

        // Context Path
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel contextPathLabel = new JLabel("Context Path:");
        contextPathLabel.setToolTipText("应用的上下文路径");
        pathPanel.add(contextPathLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        contextPathField = new JTextField(settings.getContextPath(), 30);
        contextPathField.setToolTipText("请输入应用的上下文路径，例如: /api/v1");
        pathPanel.add(contextPathField, gbc);

        // 添加面板到配置面板
        mainConfigPanel.add(showDocPanel);
        mainConfigPanel.add(Box.createVerticalStrut(10));
        mainConfigPanel.add(pathPanel);

        // 帮助信息
        JTextArea helpArea = new JTextArea(
                "配置说明:\n" +
                        "• Server Host: ShowDoc 平台的服务器地址\n" +
                        "• API Key 和 API Token: 可在 ShowDoc 平台的项目设置中获取\n" +
                        "• Relative Path: 生成的 API 文档文件存储的相对路径，为空则默认在项目根目录下创建\n" +
                        "• Context Path: 应用的上下文路径，用于构建完整的 API URL"
        );
        helpArea.setEditable(false);
        helpArea.setBackground(mainConfigPanel.getBackground());
        helpArea.setFont(mainConfigPanel.getFont().deriveFont(Font.PLAIN, 11));
        helpArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("帮助信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setPreferredSize(new Dimension(0, 120));

        // 添加帮助面板到内容面板
        mainConfigPanel.add(Box.createVerticalStrut(10));
        mainConfigPanel.add(helpArea);
        mainConfigPanel.add(Box.createVerticalGlue());

        // 使用 JScrollPane 包装主内容面板
        JBScrollPane scrollPane = new JBScrollPane(mainConfigPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 设置主面板
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JComponent getPanel() {
        return mainPanel;
    }

    public void applySettings(Project project) {
        AppSettings settings = AppSettings.getInstance(project);
        settings.setApiKey(apiKeyField.getText());
        settings.setToken(tokenField.getText());
        settings.setRelativePath(relativePathField.getText());
        settings.setContextPath(contextPathField.getText());
    }

    public String getApiKey() {
        return apiKeyField.getText();
    }

    public String getToken() {
        return tokenField.getText();
    }

    public void setApiKey(String apiKey) {
        apiKeyField.setText(apiKey);
    }

    public void setToken(String token) {
        tokenField.setText(token);
    }

    public void setRelativePath(String relativePath) {
        relativePathField.setText(relativePath);
    }

    public String getRelativePath() {
        return relativePathField.getText();
    }

    public void setContextPath(String contextPath) {
        contextPathField.setText(contextPath);
    }

    public String getContextPath() {
        return contextPathField.getText();
    }

    public String getServerHost() {
        return serverHostField.getText();
    }

    public void setServerHost(String serverHost) {
        serverHostField.setText(serverHost);
    }
}