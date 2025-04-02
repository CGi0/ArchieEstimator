package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.cell.*;
import com.lbycpd2.archieestimator.dao.CostBookDAO;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.service.CurrencyFormatService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

import static com.lbycpd2.archieestimator.util.DefaultCostItem.DEFAULT_COST_ITEM;

@Slf4j
public class CostTableController {
    @FXML private TextField textFieldSubtotalCost;
    @FXML private TreeTableView<CostRow> treeTableView;
    @FXML private TreeTableColumn<CostRow, String> colCostItem;
    @FXML private TreeTableColumn<CostRow, BigDecimal> colQuantity;
    @FXML private TreeTableColumn<CostRow, String> colUnit;
    @FXML private TreeTableColumn<CostRow, BigDecimal> colMaterialUnitCost;
    @FXML private TreeTableColumn<CostRow, BigDecimal> colLaborUnitCost;
    @FXML private TreeTableColumn<CostRow, BigDecimal> colLaborAndMaterialCost;
    @FXML private TreeTableColumn<CostRow, BigDecimal> colTotalCost;

    private final CostBookDAO cbDAO = new CostBookDAO();
    private ContextMenu contextMenu;

    TreeItem<CostRow> root = new TreeItem<>(new CostRow(DEFAULT_COST_ITEM));
    TreeItem<CostRow> cost1 = new TreeItem<>(new CostRow(cbDAO.get(1)));
    TreeItem<CostRow> cost2 = new TreeItem<>(new CostRow(cbDAO.get(2)));
    TreeItem<CostRow> cost3 = new TreeItem<>(new CostRow(cbDAO.get(3)));

    private final ObservableList<TreeItem<CostRow>> costItemList = FXCollections.observableArrayList();

    public void initialize() {
        initializeTreeTable();
        initializeRows();
    }

    private void initializeTreeTable(){
        costItemList.addAll(cost1,cost2,cost3);
        root.getChildren().addAll(costItemList);
        treeTableView.setRoot(root);
        treeTableView.setEditable(true);
        treeTableView.setShowRoot(false);
    }

    private void initializeRows(){
        colQuantity.setCellFactory(param -> new TextFieldBigDecimalTreeTableCell<>(CostRow::setCostItemQuantity));
        colMaterialUnitCost.setCellFactory(param -> new TextFieldFinancialTreeTableCell<>(CostRow::setCostItemMaterialUnitCost));
        colLaborUnitCost.setCellFactory(param -> new TextFieldFinancialTreeTableCell<>(CostRow::setCostItemLaborUnitCost));
        colLaborAndMaterialCost.setCellFactory(param -> new FinancialTreeTableCell<>());
        colTotalCost.setCellFactory(param -> {
            FinancialTreeTableCell<CostRow> cell = new FinancialTreeTableCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> updateSubtotal());
            return cell;
        });


        colCostItem.setCellValueFactory(param -> param.getValue().getValue().costItemNameProperty());
        colMaterialUnitCost.setCellValueFactory(param -> param.getValue().getValue().costItemMaterialUnitCostProperty());
        colQuantity.setCellValueFactory(param -> param.getValue().getValue().costItemQuantityProperty());
        colUnit.setCellValueFactory(param -> param.getValue().getValue().costItemUnitProperty());
        colLaborUnitCost.setCellValueFactory(param -> param.getValue().getValue().costItemLaborUnitCostProperty());
        colLaborAndMaterialCost.setCellValueFactory(param -> param.getValue().getValue().costItemLaborAndMaterialCostProperty());
        colTotalCost.setCellValueFactory(param -> param.getValue().getValue().costItemTotalCostProperty());

        // Add mouse click event handler to each row
        treeTableView.setRowFactory(tv -> {
            TreeTableRow<CostRow> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    // Handle row click
                    log.info("Row clicked: {}", row.getItem().getCostItemName());
                } else {
                    // Deselect if clicked on empty space
                    treeTableView.getSelectionModel().clearSelection();
                    log.info("No row selected");
                }
            });
            row.setOnContextMenuRequested(this::onCellContextMenuRequested);
            return row;
        });
    }

    private void updateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (TreeItem<CostRow> item : root.getChildren()) {
            subtotal = subtotal.add(item.getValue().getCostItemTotalCost());
        }
        textFieldSubtotalCost.setText(CurrencyFormatService.getInstance().format(subtotal));
    }


    public void onCostBookAction(){
        try {
            log.info("Trying to open CostBook");
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
            log.info("Trying to open CostBook");
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
            TreeItem<CostRow> item = new TreeItem<>(new CostRow(costItem));
            costItemList.add(item);
            root.getChildren().add(item);
            log.info("Added CostRow: {}", item.getValue().getCostItemName());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostBookAddCostItemAction(){}

    private void removeCostRow(TreeItem<CostRow> costRowSelection){
        try{
            if (null == treeTableView.getSelectionModel().getSelectedItem()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("No Cost Row Item Selected");
                alert.setContentText("No cost row item selected or cost row item ID is null");
                alert.showAndWait();
                return;
            }
            log.info("Trying to remove CostRow");
            root.getChildren().remove(costRowSelection);
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public void onRemoveRowAction(){
        removeCostRow(treeTableView.getSelectionModel().getSelectedItem());
    }

    public void onCellContextMenuRequested(ContextMenuEvent event) {
        TreeTableRow<CostRow> row = (TreeTableRow<CostRow>) event.getSource();

        if (row != null && !row.isEmpty()) {
            if (contextMenu != null && contextMenu.isShowing()) {
                contextMenu.hide();
            }

            contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit Cost Item");
            MenuItem removeItem = new MenuItem("Remove Cost Item");

            editItem.setOnAction(e -> {
                TreeItem<CostRow> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    // Implement your edit logic here
                    log.info("Editing: {}", selectedItem.getValue().getCostItemName());
                }
            });
            removeItem.setOnAction(e -> {
                TreeItem<CostRow> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    // Implement your remove logic here
                    log.info("Removing: {}", selectedItem.getValue().getCostItemName());
                    removeCostRow(selectedItem);
                    log.info("Removed CostRow: {}", selectedItem.getValue().getCostItemName());
                }
            });

            contextMenu.getItems().add(editItem);
            contextMenu.getItems().add(removeItem);
            contextMenu.show(row, event.getScreenX(), event.getScreenY());

            // Hide the context menu when clicking outside
            treeTableView.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.SECONDARY && contextMenu != null && contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            });
        }
    }

}

