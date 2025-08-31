package com.cmcc.littlec.autoshowdoc.entity;

import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * 选择生成文档精简程度的对话框
 * @program: AutoShowDoc
 * @ClassName: ComplexTypeChoiceDialog
 * @author: suran
 * @create: 2025-08-31 19:06
 */
public class ComplexTypeChoiceDialog extends DialogWrapper {
    private final String methodName;
    private JCheckBox applyToAllCheckBox;
    // -1: 取消, 0: 是, 1: 否
    private int result = -1;

    public ComplexTypeChoiceDialog(String methodName) {
        super(true);
        this.methodName = methodName;
        setTitle("简化参数结构文档生成确认");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));

        // 消息文本，包含方法名
        JLabel messageLabel = new JLabel(
                "<html><b>方法名为" + methodName + "</b> 参数对象结构复杂，是否生成简化的对象参数文档？<br>" +
                        "选择\"是\"将生成简化参数文档，选择\"否\"将生成完整参数文档。</html>"
        );
        panel.add(messageLabel, BorderLayout.CENTER);

        // 添加勾选框
        applyToAllCheckBox = new JCheckBox("对当前次批量生成保持该操作");
        panel.add(applyToAllCheckBox, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected Action[] createActions() {
        // 创建"是"和"否"按钮
        Action okAction = getOKAction();
        okAction.putValue(Action.NAME, "是");

        Action cancelAction = getCancelAction();
        cancelAction.putValue(Action.NAME, "否");

        // 返回按钮数组
        return new Action[]{okAction, cancelAction};
    }

    @Override
    protected void doOKAction() {
        // "是"
        result = 0;
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        // "否"
        result = 1;
        super.doCancelAction();
    }

    public boolean isYes() {
        return result == 0;
    }

    public boolean isNo() {
        return result == 1;
    }

    public boolean isApplyToAll() {
        return applyToAllCheckBox != null && applyToAllCheckBox.isSelected();
    }
}