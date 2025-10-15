package it.unicam.cs.mpgc.jbudget125667;

import it.unicam.cs.mpgc.jbudget125667.controller.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class MainApp extends Application {
    private static Stage mainStage;
    private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        mainScene = new Scene(loadFXMLRoot("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyLogin.fxml", null));
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/it/unicam/cs/mpgc/jbudget125667/style/style.css")).toExternalForm());

        primaryStage.setTitle("Family Budget Manager");
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        loadAppIcon(primaryStage);
        setStageSizeFor("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyLogin.fxml");
        primaryStage.show();
    }

    public static void setRoot(String fxmlPath, Object data) throws IOException {
        Parent root = loadFXMLRoot(fxmlPath, data);
        mainScene.setRoot(root);
        setStageSizeFor(fxmlPath);
    }

    private static Parent loadFXMLRoot(String fxmlPath, Object data) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(MainApp.class.getResource(fxmlPath)));
        Parent root = loader.load();
        Object controller = loader.getController();

        if (data != null && controller != null) {
            if (data instanceof Family && controller instanceof FamilyAware) {
                ((FamilyAware) controller).setFamily((Family) data);
            } else if (data instanceof BankAccount && controller instanceof BankAccountHomeController) {
                BankAccount ba = (BankAccount) data;
                ((BankAccountHomeController) controller).setBankAccount(ba, ba.getFamily());
            }
        }
        return root;
    }

    private static void loadAppIcon(Stage stage) {
        try (InputStream is = MainApp.class.getResourceAsStream("/it/unicam/cs/mpgc/jbudget125667/icon/icon.png")) {
            if (is != null) {
                stage.getIcons().add(new Image(is));
            } else {
                System.err.println("Icon resource not found.");
            }
        } catch (IOException e) {
            System.err.println("Failed to load app icon: " + e.getMessage());
        }
    }

    private static void setStageSizeFor(String fxmlPath) {
        String fxmlName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
        boolean isDashboard = "FamilyHome.fxml".equals(fxmlName) || "BankAccountHome.fxml".equals(fxmlName);
        double width = isDashboard ? 1280 : 750;
        double height = isDashboard ? 800 : 500;

        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.setMinWidth(width);
        mainStage.setMinHeight(height);

        if (isDashboard) {
            mainStage.setMaxWidth(Double.MAX_VALUE);
            mainStage.setMaxHeight(Double.MAX_VALUE);
        } else {
            mainStage.setMaxWidth(width);
            mainStage.setMaxHeight(height);
        }
    }

    public static Stage getPrimaryStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        HibernateUtil.getSessionFactory();

        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
        scheduledTaskService.processDueMovements();

        launch(args);
    }
}