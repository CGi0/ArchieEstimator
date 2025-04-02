package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.util.InitializeTables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cost-table.fxml"));

        InitializeTables.start();

        Scene scene = new Scene(fxmlLoader.load());

        // TEMPORARY FOR COSTBOOK
        stage.setMaxWidth(1366);
        stage.setMaxHeight(768);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}