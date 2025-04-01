package com.lbycpd2.archieestimator.alerts;

import com.lbycpd2.archieestimator.alerts.DialogBoxInterface;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class AbstractDialogBox implements DialogBoxInterface {
    protected Alert alert;

    public AbstractDialogBox(Alert.AlertType alertType) {
        alert = new Alert(alertType);
    }

    @Override
    public void setTitle(String title) {
        alert.setTitle(title);
    }

    @Override
    public void setHeaderText(String headerText) {
        alert.setHeaderText(headerText);
    }

    @Override
    public void setContentText(String contentText) {
        alert.setContentText(contentText);
    }

    public Alert getAlert() {
        return alert;
    }

    @Override
    public abstract void showDialog(Stage primaryStage);
}
