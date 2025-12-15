module com.minierp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;

    opens com.minierp to javafx.fxml;
    opens com.minierp.ai.model to com.google.gson;

    exports com.minierp;
    exports com.minierp.model;
    exports com.minierp.controller;
    exports com.minierp.service;
    exports com.minierp.ui;
    exports com.minierp.ui.login;
    exports com.minierp.ui.components;
    exports com.minierp.ai;
    exports com.minierp.ai.model;
    exports com.minierp.ai.llm;
    exports com.minierp.ai.prompt;
    exports com.minierp.ai.executor;
    exports com.minierp.ai.service;
    exports com.minierp.ui.ai;
}
