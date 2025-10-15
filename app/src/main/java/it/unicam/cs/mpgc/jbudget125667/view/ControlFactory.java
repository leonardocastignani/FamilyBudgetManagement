package it.unicam.cs.mpgc.jbudget125667.view;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;

import javafx.collections.*;
import javafx.scene.control.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public final class ControlFactory {

    private ControlFactory() {}

    public static Label createLabel(String text, String styleClass, double width) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setPrefWidth(width);
        return label;
    }

    public static Label createHeaderLabel(String text, double width) {
        return createLabel(text, "movements-header-label", width);
    }

    public static Label createDialogTitle(String text) {
        Label titleLabel = new Label(text);
        titleLabel.getStyleClass().add("add-movement-title");
        return titleLabel;
    }

    public static TextField createNumericTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("-?\\d*([.,])?\\d*")) {
                textField.setText(oldVal);
            }
        });
        return textField;
    }

    public static ComboBox<Category> createCategoryComboBox() {
        ComboBox<Category> comboBox = new ComboBox<Category>();
        List<Category> sortedCategories = new CategoryService().getAllCategoriesWithChildren().stream()
                .filter(c -> c.getParent() == null)
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .flatMap(parent -> Stream.concat(Stream.of(parent), parent.getChildren().stream()
                        .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))))
                .collect(Collectors.toList());

        comboBox.setItems(FXCollections.observableArrayList(sortedCategories));
        comboBox.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getParent() != null ? "    " + item.getName() : item.getName()));
            }
        });
        comboBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        return comboBox;
    }

    public static ComboBox<Integer> createYearComboBox() {
        int currentYear = LocalDate.now().getYear();
        return new ComboBox<Integer>(FXCollections.observableArrayList(
                IntStream.rangeClosed(currentYear - 5, currentYear + 5).boxed().collect(Collectors.toList())
        ));
    }

    public static ComboBox<Month> createMonthComboBox() {
        return new ComboBox<Month>(FXCollections.observableArrayList(Month.values()));
    }

    public static Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }
}