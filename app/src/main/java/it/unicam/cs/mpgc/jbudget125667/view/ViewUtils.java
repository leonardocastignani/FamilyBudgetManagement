package it.unicam.cs.mpgc.jbudget125667.view;

import it.unicam.cs.mpgc.jbudget125667.MainApp;

import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public final class ViewUtils {

    private ViewUtils() {}

    public static void navigateTo(String fxmlPath, Object data) {
        try {
            // Delega la navigazione e il ridimensionamento a MainApp
            MainApp.setRoot(fxmlPath, data);
        } catch (IOException e) {
            e.printStackTrace();
            // Gestire l'errore, ad esempio mostrando un alert
        }
    }

    public static Stage createBaseDialogStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(
                ViewUtils.class.getResourceAsStream("/it/unicam/cs/mpgc/jbudget125667/icon/icon.png"))));
        } catch (Exception e) {
            System.err.println("Icon not found for dialog.");
        }
        return stage;
    }

    public static void finalizeAndShowDialog(Stage stage, VBox root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(
            ViewUtils.class.getResource("/it/unicam/cs/mpgc/jbudget125667/style/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }
}