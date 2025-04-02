package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.cell.*;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.service.CurrencyFormatService;
import com.lbycpd2.archieestimator.table.TabTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
public class CostTableController {

    @FXML private TabPane tabPane;
    @FXML private TextField textFieldSubtotalCost;

    private ContextMenu contextMenu;
    private final ObservableList<TabTable> tabTablesList = FXCollections.observableArrayList();
    private final ObservableList<TreeTableView<CostRow>> tablesList = FXCollections.observableArrayList();

    private Tab tabSelection;

    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tabSelection = newValue;
            updateSubtotal();
        });

        addNewTab("Default Tab");
    }

    public void updateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (TabTable tab : tabTablesList) {
            if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                for (TreeItem<CostRow> item : tab.getTreeTableView().getRoot().getChildren()) {
                    subtotal = subtotal.add(item.getValue().getCostItemTotalCost());
                }
            }
        }
        textFieldSubtotalCost.setText(CurrencyFormatService.getInstance().format(subtotal));
    }

    public void onAddTabAction(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter tab name:");
        dialog.setContentText("Input:");
        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            addNewTab(result.get());
        } else {
            log.info("No input provided or dialog was canceled.");
        }
    }

    private void addNewTab(String tabName) {
        TabTable tabTable = new TabTable(tabName, this);
        tabTablesList.add(tabTable);
        tablesList.add(tabTable.getTreeTableView());
        tabPane.getTabs().add(tabTable.getTab());
        tabPane.getSelectionModel().select(tabTable.getTab());
    }

    public void onRemoveTabAction(){
        removeTab();
    }

    private void removeTab(){
        try {
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDialog.setTitle("Confirmation Dialog");
                    confirmDialog.setHeaderText("Delete Tab?");
                    confirmDialog.setContentText(String.format("Are you sure you want to remove %s\nand all its associated items?", tab.getTab().getText()));

                    Optional<ButtonType> result = confirmDialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            tabPane.getTabs().remove(tabSelection);
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else {
                        log.info("User chose Cancel or closed the dialog");
                    }
                    return;
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostBookAction(){
        try {
            log.info("onCostBookAction: Trying to open CostBook");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-book.fxml"));
            Parent root = loader.load();
            CostBookController controller = loader.getController();
            controller.setCostTableController(this);

            Stage stage = new Stage();
            stage.setTitle("Cost Book");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostItemAddAction(){
        try {
            log.info("onCostItemAction: Trying to open CostBook");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-book-selector.fxml"));
            Parent root = loader.load();
            CostBookSelectorController controller = loader.getController();
            controller.setCostTableController(this);

            Stage stage = new Stage();
            stage.setTitle("Cost Book");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(event -> log.info("Closing CostBookSelector"));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void addCostRow(CostItem costItem) {
        try {
            log.info("Trying to add CostRow");
            TreeItem<CostRow> item = null;
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    item = new TreeItem<>(new CostRow(costItem));
                    tab.getCostRowList().add(item);
                    tab.getTreeTableView().getRoot().getChildren().add(item);
                }
            }
            assert item != null;
            log.info("Added CostRow: {}", item.getValue().getCostItemName());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostBookAddCostItemAction(){
        try {
            log.info("Trying to open Costbook Add");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-item-add.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Cost Item Add");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private void removeCostRow(TreeItem<CostRow> costRowSelection, TabTable tab){
        try{
            log.info("Trying to remove CostRow");
            if (null == tab.getTreeTableView().getSelectionModel().getSelectedItem()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("No Cost Row Item Selected");
                alert.setContentText("No cost row item selected or cost row item ID is null");
                alert.showAndWait();
                return;
            }
            tab.getTreeTableView().getRoot().getChildren().remove(costRowSelection);
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public void onRemoveRowAction(){
        for (TabTable tab : tabTablesList) {
            if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                removeCostRow(tab.getTreeTableView().getSelectionModel().getSelectedItem(), tab);
            }
        }
    }

    public void onCellContextMenuRequested(ContextMenuEvent event) {
        TreeTableRow<CostRow> row = (TreeTableRow<CostRow>) event.getSource();

        if (row != null && !row.isEmpty()) {
            if (contextMenu != null && contextMenu.isShowing()) {
                contextMenu.hide();
            }

            contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Remove Cost Item");

            removeItem.setOnAction(e -> onRemoveRowAction());

            contextMenu.getItems().add(removeItem);
            contextMenu.show(row, event.getScreenX(), event.getScreenY());

            // Hide the context menu when clicking outside
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    tab.getTreeTableView().setOnMouseClicked(e -> {
                        if (e.getButton() != MouseButton.SECONDARY && contextMenu != null && contextMenu.isShowing()) {
                            contextMenu.hide();
                        }
                    });
                }
            }

        }
    }

}

