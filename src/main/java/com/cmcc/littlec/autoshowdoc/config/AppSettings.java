package com.cmcc.littlec.autoshowdoc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;

/**
 * @program: autorun
 * @ClassName: AppSettings
 * @author: suran
 * @create: 2025-08-13 15:30
 */
@State(name = "ApiDocPluginSettings", storages = @Storage("api-doc-plugin.xml"))
public class AppSettings implements PersistentStateComponent<AppSettings.State> {
    private State state = new State();

    public static AppSettings getInstance(Project project) {
        return project.getService(AppSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public String getApiKey() {
        return state.apiKey;
    }

    public void setApiKey(String apiKey) {
        state.apiKey = apiKey;
    }

    public String getToken() {
        return state.token;
    }

    public void setToken(String token) {
        state.token = token;
    }

    public String getRelativePath() {
        return state.relativePath;
    }

    public void setRelativePath(String relativePath) {
        state.relativePath = relativePath;
    }

    public String getContextPath() {
        return state.contextPath;
    }

    public void setContextPath(String contextPath) {
        state.contextPath = contextPath;
    }

    public String getServerHost() {
        return state.serverHost;
    }

    public void setServerHost(String serverHost) {
        state.serverHost = serverHost;
    }

    public static class State {
        public String apiKey = "";
        public String token = "";
        public String relativePath = "";
        public String contextPath = "";
        public String serverHost = "";
    }
}