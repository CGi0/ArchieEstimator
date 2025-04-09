package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.util.InitializeTables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class HelloApplication extends Application {

    private static final double STAGE_WIDTH = 1366;
    private static final double STAGE_HEIGHT = 768;


    @Override
    public void start(Stage stage) throws IOException, FontFormatException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cost-table.fxml"));

        InitializeTables.start();

        // Dependencies Go Brrrr (I'm in Spain without the S)
        InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans/DejaVuSans.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

        fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans/DejaVuSans-Bold.ttf");
        font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

        Scene scene = new Scene(fxmlLoader.load());
        stage.setMaxWidth(STAGE_WIDTH);
        stage.setMaxHeight(STAGE_HEIGHT);
        stage.setTitle("Archie Estimator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}