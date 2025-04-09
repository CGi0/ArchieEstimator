package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.service.DocumentSettingsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DocumentSettingsController {

    @FXML private TableView<Parameter> tableViewDocumentSettings;

    private final DocumentSettingsService DSS = DocumentSettingsService.getInstance();
    private Parameter selectedParameter;

    public void initialize() {
        // Define the columns
        TableColumn<Parameter, String> nameColumn = getParameterStringTableColumn();

        TableColumn<Parameter, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        valueColumn.setMinWidth(400);

        // Set the valueColumn to stretch to the right-most of the table
        valueColumn.prefWidthProperty().bind(
                tableViewDocumentSettings.widthProperty()
                        .subtract(nameColumn.widthProperty())
                        .subtract(2) // Optional: Adjust for padding or table borders
        );

        // Add columns to the table
        tableViewDocumentSettings.getColumns().addAll(nameColumn, valueColumn);

        // Create the data for the table
        ObservableList<Parameter> parametersList = FXCollections.observableArrayList();

        // Populate the ObservableList from the parameters map
        for (Map.Entry<String, Object> entry : DSS.getParameters().entrySet()) {
            parametersList.add(new Parameter(entry.getKey(), entry.getValue().toString()));
        }

        // Sort the parametersList alphabetically by Name using a lambda
        parametersList.sort((param1, param2) -> param1.getName().compareToIgnoreCase(param2.getName()));

        // Set the data in the TableView
        tableViewDocumentSettings.setItems(parametersList);

        tableViewDocumentSettings.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedParameter = newValue;
            if (selectedParameter != null) {
                log.info("Selected: {}", selectedParameter.getName());
            }
        });

        tableViewDocumentSettings.setRowFactory(tv -> {
            TableRow<Parameter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Check for a double click
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onEditStartAction();
                    // Perform action with the double-clicked row's data
                    log.debug("Double-clicked on: {}", selectedParameter.getName() );
                }
            });
            return row;
        });
    }

    private static TableColumn<Parameter, String> getParameterStringTableColumn() {
        TableColumn<Parameter, String> nameColumn = new TableColumn<>("Document Setting");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setMinWidth(200);

        // Set a CellFactory for formatting the parameter names
        nameColumn.setCellFactory(column -> new TableCell<Parameter, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null); // Clear any previous style
                } else {
                    // Replace the first underscore with " - "
                    int firstUnderscoreIndex = item.indexOf("_");
                    if (firstUnderscoreIndex != -1) {
                        item = item.substring(0, firstUnderscoreIndex) + " - " + item.substring(firstUnderscoreIndex + 1);
                    }

                    // Replace remaining underscores with spaces and format the text
                    String formatted = item.replace("_", " ");
                    formatted = capitalizeWords(formatted);

                    // Set the formatted text
                    setText(formatted);

                    // Apply styles: left alignment and internal padding
                    setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 5 10 5 5; -fx-font-weight: bold;");
                }
            }

            private String capitalizeWords(String text) {
                String[] words = text.split(" ");
                StringBuilder formattedText = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        formattedText.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1).toLowerCase())
                                .append(" ");
                    }
                }
                return formattedText.toString().trim();
            }
        });


        return nameColumn;
    }

    public void onConfirmAction(ActionEvent actionEvent) {
        Stage stage = (Stage) tableViewDocumentSettings.getScene().getWindow();
        stage.close();
    }

    public void onBackAction(ActionEvent actionEvent) {
        Stage stage = (Stage) tableViewDocumentSettings.getScene().getWindow();
        stage.close();
    }

    private void onEditStartAction() {
        try{
            if (null == selectedParameter) {
                log.info("No parameter selected");
            }

            if (selectedParameter.getName().equals("LOGO_IMAGE_PATH")) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF Files", "*.gif"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Files", "*.bmp"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Documents"));
                fileChooser.setTitle("Load File");
                File selectedFile = fileChooser.showOpenDialog(new Stage());
                DSS.updateSetting(selectedParameter.getName(), selectedFile.getAbsolutePath());
                selectedParameter.setName(selectedFile.getName());
            } else {
                editTextValue();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void editTextValue() {
        TextInputDialog dialog = new TextInputDialog(selectedParameter.getValue());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change Document Settings:");
        dialog.setContentText("Input:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Object setting = DSS.getSetting(selectedParameter.getName());

            try {
                if (setting instanceof BigDecimal) {
                    // Handle BigDecimal editing
                    BigDecimal inputValue = new BigDecimal(result.get());
                    if (inputValue.compareTo(BigDecimal.ONE) < 0 && inputValue.compareTo(BigDecimal.ZERO) >= 0) {
                        log.info("Valid percentage: {}", inputValue);
                        DSS.updateSetting(selectedParameter.getName(), new BigDecimal(inputValue.toString()));
                        selectedParameter.setValue(inputValue.toString());
                    } else {
                        log.warn("Invalid percentage: {}", inputValue);
                        showWarningDialog("Invalid percentage!", "Value must be from 0 to 0.99.");
                    }
                } else if (setting instanceof Locale) {
                    String[] localeParts = result.get().split("_");
                    if (localeParts.length == 2) {
                        DSS.updateSetting(selectedParameter.getName(), new Locale(localeParts[0], localeParts[1]));
                        selectedParameter.setValue(result.get());
                    } else {
                        log.warn("Invalid Locale Format: {}", result.get());
                        showWarningDialog("Invalid Locale Format!",
                                "Locale must follow [language]_[country] format.");
                    }
                } else {
                    // Handle normal String editing
                    DSS.updateSetting(selectedParameter.getName(), result.get());
                    selectedParameter.setValue(result.get());
                    log.info("Updated {} with String value: {}", selectedParameter.getName(), result.get());
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input!", "Please enter a valid numeric value.");
            }
        } else {
            log.info("No input provided or dialog was canceled.");
        }
    }

    private void showWarningDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Updated Parameter class with Properties
    public static class Parameter {
        private final StringProperty name;
        private final StringProperty value;

        public Parameter(String name, String value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value);
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public StringProperty valueProperty() {
            return value;
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }
    }
}
