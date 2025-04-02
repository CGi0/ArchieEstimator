package com.lbycpd2.archieestimator.table;

import com.lbycpd2.archieestimator.CostTableController;
import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.cell.FinancialTreeTableCell;
import com.lbycpd2.archieestimator.cell.TextFieldBigDecimalTreeTableCell;
import com.lbycpd2.archieestimator.cell.TextFieldFinancialTreeTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.lbycpd2.archieestimator.util.DefaultCostItem.DEFAULT_COST_ITEM;

@Slf4j
public class TabTable {
    private final CostTableController controller;
    @Getter
    private final TreeTableView<CostRow> TreeTableView = createTreeTableView();
    @Getter
    private final Tab tab;
    @Getter
    private final ObservableList<TreeItem<CostRow>> costRowList = FXCollections.observableArrayList();

    public TabTable(String tabName, CostTableController controller){
        this.controller = controller;
        tab = new Tab(tabName);
        tab.setContent(TreeTableView);
    }

    private TreeTableView<CostRow> createTreeTableView() {
        TreeTableView<CostRow> treeTableView = new TreeTableView<>();
        TreeItem<CostRow> root = new TreeItem<>(new CostRow(DEFAULT_COST_ITEM));
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);

        // Initialize columns
        TreeTableColumn<CostRow, String> colCostItem = new TreeTableColumn<>("Cost Item");
        TreeTableColumn<CostRow, BigDecimal> colQuantity = new TreeTableColumn<>("Quantity");
        TreeTableColumn<CostRow, String> colUnit = new TreeTableColumn<>("Unit");
        TreeTableColumn<CostRow, BigDecimal> colMaterialUnitCost = new TreeTableColumn<>("Material Unit Cost");
        TreeTableColumn<CostRow, BigDecimal> colLaborUnitCost = new TreeTableColumn<>("Labor Unit Cost");
        TreeTableColumn<CostRow, BigDecimal> colLaborAndMaterialCost = new TreeTableColumn<>("Labor & Material Unit Cost");
        TreeTableColumn<CostRow, BigDecimal> colTotalCost = new TreeTableColumn<>("Total Cost");

        colCostItem.setPrefWidth(250);
        colQuantity.setPrefWidth(75);
        colUnit.setPrefWidth(75);
        colMaterialUnitCost.setPrefWidth(150);
        colLaborUnitCost.setPrefWidth(150);
        colLaborAndMaterialCost.setPrefWidth(200);
        colTotalCost.setPrefWidth(350);

        treeTableView.getColumns().addAll(colCostItem, colQuantity, colUnit, colMaterialUnitCost, colLaborUnitCost, colLaborAndMaterialCost, colTotalCost);

        colQuantity.setCellFactory(param -> new TextFieldBigDecimalTreeTableCell<>(CostRow::setCostItemQuantity));
        colMaterialUnitCost.setCellFactory(param -> new TextFieldFinancialTreeTableCell<>(CostRow::setCostItemMaterialUnitCost));
        colLaborUnitCost.setCellFactory(param -> new TextFieldFinancialTreeTableCell<>(CostRow::setCostItemLaborUnitCost));
        colLaborAndMaterialCost.setCellFactory(param -> new FinancialTreeTableCell<>());
        colTotalCost.setCellFactory(param -> {
            FinancialTreeTableCell<CostRow> cell = new FinancialTreeTableCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> controller.updateSubtotal());
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
            row.setOnContextMenuRequested(controller::onCellContextMenuRequested);
            return row;
        });

        return treeTableView;
    }
}
