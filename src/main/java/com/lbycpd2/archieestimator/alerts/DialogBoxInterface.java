package com.lbycpd2.archieestimator.alerts;

import javafx.stage.Stage;

public interface DialogBoxInterface {
    void setTitle(String title);
    void setHeaderText(String headerText);
    void setContentText(String contentText);
    void showDialog(Stage primaryStage);
}
