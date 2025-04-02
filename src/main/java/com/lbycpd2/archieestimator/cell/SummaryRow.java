package com.lbycpd2.archieestimator.cell;

import com.lbycpd2.archieestimator.table.TabTable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@NoArgsConstructor
public class SummaryRow {
    private final SimpleStringProperty rowName = new SimpleStringProperty();
    private final SimpleObjectProperty<BigDecimal> rowTotalCost = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public SummaryRow(TabTable tab) {
        try {
            if (tab != null && tab.getTab() != null) {
                this.rowName.set(tab.getTab().getText());
                bindRowTotalCost(tab);
            } else {
                log.error("Tab or Tab text is null");
            }
        } catch (Exception e) {
            log.error("Error initializing SummaryRow: {}", e.getMessage(), e);
        }
    }

    private void bindRowTotalCost(TabTable tab) {
        try {
            if (tab != null && tab.getTreeTableView() != null && tab.getTreeTableView().getRoot() != null) {
                tab.getTreeTableView().getRoot().getChildren().forEach(item -> {
                    if (item != null && item.getValue() != null) {
                        item.getValue().costItemTotalCostProperty().addListener((obs, oldVal, newVal) -> updateTotalCost(tab));
                    }
                });
                updateTotalCost(tab);
            } else {
                log.error("TreeTableView or its root is null");
            }
        } catch (Exception e) {
            log.error("Error binding row total cost: {}", e.getMessage(), e);
        }
    }

    private void updateTotalCost(TabTable tab) {
        log.info("updateTotalCost method called");
        try {
            if (tab != null && tab.getTreeTableView() != null && tab.getTreeTableView().getRoot() != null) {
                BigDecimal totalCost = tab.getTreeTableView().getRoot().getChildren().stream()
                        .map(item -> item.getValue().costItemTotalCostProperty().get())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                rowTotalCost.set(totalCost);
            } else {
                log.error("TreeTableView or its root is null");
            }
        } catch (Exception e) {
            log.error("Error updating total cost: {}", e.getMessage(), e);
        }
    }

    public String getRowName() {
        return rowName.get();
    }

    public BigDecimal getRowTotalCost() {
        return rowTotalCost.get();
    }

    public SimpleStringProperty rowNameProperty() {
        return rowName;
    }

    public SimpleObjectProperty<BigDecimal> rowTotalCostProperty() {
        return rowTotalCost;
    }

    public void setRowName(String rowName) {
        this.rowName.set(rowName);
    }

    public void setRowTotalCost(BigDecimal rowTotalCost) {
        this.rowTotalCost.set(rowTotalCost);
    }
}
