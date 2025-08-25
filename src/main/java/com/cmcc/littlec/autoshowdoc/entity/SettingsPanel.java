package com.cmcc.littlec.autoshowdoc.entity;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @program: autorun
 * @ClassName: SettingsPanel
 * @author: suran
 * @create: 2025-08-13 15:45
 */
public class SettingsPanel {
    private JPanel mainPanel;
    private JTextField apiKeyField;
    private JTextField tokenField;

    public SettingsPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel keyPanel = new JPanel();
        keyPanel.add(new JLabel("API Key:"));
        apiKeyField = new JTextField(40);
        keyPanel.add(apiKeyField);

        JPanel tokenPanel = new JPanel();
        tokenPanel.add(new JLabel("API Token:"));
        tokenField = new JTextField(40);
        tokenPanel.add(tokenField);

        mainPanel.add(keyPanel);
        mainPanel.add(tokenPanel);
    }

    public JComponent getPanel() {
        return mainPanel;
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
}