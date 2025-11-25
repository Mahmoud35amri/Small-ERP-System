package com.minierp.ui.components;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.function.Consumer;

public class SearchBar extends HBox {
    private TextField searchField;

    public SearchBar(String prompt, Consumer<String> onSearch) {
        getStyleClass().add("search-bar");
        setSpacing(10);

        searchField = new TextField();
        searchField.setPromptText(prompt);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearch.accept(newVal));
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(searchField);
    }
}
