module com.minierp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires transitive javafx.graphics;

    opens com.minierp to javafx.fxml;

    exports com.minierp;
    exports com.minierp.model;
    exports com.minierp.controller;
    exports com.minierp.service;
    exports com.minierp.ui;
    exports com.minierp.ui.login;
    exports com.minierp.ui.components;
}
