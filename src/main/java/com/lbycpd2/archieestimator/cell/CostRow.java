package com.lbycpd2.archieestimator.cell;

import com.lbycpd2.archieestimator.model.CostItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class CostRow {

    @Getter
    private final CostItem costItem;
    @Getter
    private final int costItemID;
    @Getter
    private final int costCategoryID;
    @Getter
    private final String costItemNotes;

    private final SimpleStringProperty costItemName;
    private final SimpleStringProperty costItemUnit;
    private final SimpleObjectProperty<BigDecimal> costItemMaterialUnitCost;
    private final SimpleObjectProperty<BigDecimal> costItemLaborUnitCost;
    private final SimpleObjectProperty<BigDecimal> costItemQuantity;
    private final SimpleObjectProperty<BigDecimal> costItemLaborAndMaterialCost;
    private final SimpleObjectProperty<BigDecimal> costItemTotalCost;

    public CostRow(CostItem costItem) {
        this.costItem = costItem;
        this.costItemID = costItem.getCostItemID();
        this.costCategoryID = costItem.getCostCategoryID();
        this.costItemNotes = costItem.getCostItemNotes();
        this.costItemName = new SimpleStringProperty(costItem.getCostItemName());
        this.costItemUnit = new SimpleStringProperty(costItem.getCostItemUnit());
        this.costItemMaterialUnitCost = new SimpleObjectProperty<>(costItem.getCostItemMaterialUnitCost());
        this.costItemLaborUnitCost = new SimpleObjectProperty<>(costItem.getCostItemLaborUnitCost());
        this.costItemQuantity = new SimpleObjectProperty<>(new BigDecimal("1"));
        this.costItemLaborAndMaterialCost = new SimpleObjectProperty<>();
        this.costItemTotalCost = new SimpleObjectProperty<>();

        // Add listeners to update total cost when material or labor unit cost changes
        ChangeListener<BigDecimal> costChangeListener = (observable, oldValue, newValue) -> updateTotalCost();
        costItemQuantity.addListener(costChangeListener);
        costItemMaterialUnitCost.addListener(costChangeListener);
        costItemLaborUnitCost.addListener(costChangeListener);

        // Initialize total cost
        updateTotalCost();
    }

    private void updateTotalCost() {
        try {
            BigDecimal materialCost = costItemMaterialUnitCost.get().multiply(costItemQuantity.get());
            BigDecimal laborCost = costItemLaborUnitCost.get().multiply(costItemQuantity.get());
            costItemLaborAndMaterialCost.set(materialCost.add(laborCost));
            costItemTotalCost.set(costItemLaborAndMaterialCost.get());
            log.debug("Calculating {} total cost: {}", costItemName.get(), costItemTotalCost.get());
        } catch (Exception e) {
            log.error("Error calculating total cost: {}", e.getMessage());
        }
    }


    // Property methods
    public SimpleStringProperty costItemNameProperty() {
        return costItemName;
    }

    public SimpleStringProperty costItemUnitProperty() {
        return costItemUnit;
    }

    public SimpleObjectProperty<BigDecimal> costItemMaterialUnitCostProperty() {
        return costItemMaterialUnitCost;
    }

    public SimpleObjectProperty<BigDecimal> costItemLaborUnitCostProperty() {
        return costItemLaborUnitCost;
    }

    public SimpleObjectProperty<BigDecimal> costItemQuantityProperty() {
        return costItemQuantity;
    }

    public SimpleObjectProperty<BigDecimal> costItemLaborAndMaterialCostProperty() {
        return costItemLaborAndMaterialCost;
    }

    public SimpleObjectProperty<BigDecimal> costItemTotalCostProperty() {
        return costItemTotalCost;
    }

    public String getCostItemName(){
        return costItemName.get();
    }

    public BigDecimal getCostItemMaterialUnitCost() {
        return costItemMaterialUnitCost.get();
    }

    public BigDecimal getCostItemLaborUnitCost() {
        return costItemLaborUnitCost.get();
    }

    public BigDecimal getCostItemQuantity() {
        return costItemQuantity.get();
    }

    public BigDecimal getCostItemLaborAndMaterialCost() {
        return costItemLaborAndMaterialCost.get();
    }

    public BigDecimal getCostItemTotalCost() {
        return costItemTotalCost.get();
    }

    // Setters

    public void setCostItemMaterialUnitCost(BigDecimal materialCost) {
        this.costItemMaterialUnitCost.set(materialCost);
    }

    public void setCostItemLaborUnitCost(BigDecimal laborCost) {
        this.costItemLaborUnitCost.set(laborCost);
    }

    public void setCostItemQuantity(BigDecimal quantity) {
        this.costItemQuantity.set(quantity);
    }

    public void setCostItemLaborAndMaterialCost(BigDecimal laborAndMaterialCost) {
        this.costItemLaborAndMaterialCost.set(laborAndMaterialCost);
    }

    public void setCostItemTotalCost(BigDecimal totalCost) {
        this.costItemTotalCost.set(totalCost);
    }
}
