package com.lbycpd2.archieestimator.alerts;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AlertDialog extends AbstractDialogBox {

    public AlertDialog() {
        super(Alert.AlertType.INFORMATION);
    }

    @Override
    public void showDialog(Stage primaryStage) {
        alert.showAndWait();
    }
}
