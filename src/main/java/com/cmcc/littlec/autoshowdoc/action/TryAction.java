package com.cmcc.littlec.autoshowdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;

public class TryAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取选中文本
        String text = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText();
        Messages.showInfoMessage("你选中了："+ text, "成功第一步");
    }
}
