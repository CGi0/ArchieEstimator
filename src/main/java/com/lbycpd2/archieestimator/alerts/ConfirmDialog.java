package com.lbycpd2.archieestimator.alerts;

import com.lbycpd2.archieestimator.alerts.AbstractDialogBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ConfirmDialog extends AbstractDialogBox {

    public ConfirmDialog() {
        super(Alert.AlertType.CONFIRMATION);
    }

    @Override
    public void showDialog(Stage primaryStage) {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.info("User chose OK");
        } else {
            log.info("User chose Cancel or closed the dialog");
        }
    }
}
