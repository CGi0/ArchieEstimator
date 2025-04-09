package com.lbycpd2.archieestimator.cell;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lbycpd2.archieestimator.model.CostItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class CostRow {

    @JsonProperty("costItem")
    @Getter
    private final CostItem costItem;
//    @Deprecated
//    @JsonProperty("costItemID")
//    private final int costItemID;
//    @JsonProperty("costCategoryID")
//    @Getter
//    private final int costCategoryID;
//    @JsonProperty("costItemNotes")
//    @Getter
//    private final String costItemNotes;
    @JsonProperty("costItemName")
    private final SimpleStringProperty costItemName;
    @JsonProperty("costItemUnit")
    private final SimpleStringProperty costItemUnit;
    @JsonProperty("costItemMaterialUnitCost")
    private final SimpleObjectProperty<BigDecimal> costItemMaterialUnitCost;
    @JsonProperty("costItemLaborUnitCost")
    private final SimpleObjectProperty<BigDecimal> costItemLaborUnitCost;
    @JsonProperty("costItemQuantity")
    private final SimpleObjectProperty<BigDecimal> costItemQuantity;
    @JsonProperty("costItemLaborAndMaterialCost")
    private final SimpleObjectProperty<BigDecimal> costItemLaborAndMaterialCost;
    @JsonProperty("costItemTotalCost")
    private final SimpleObjectProperty<BigDecimal> costItemTotalCost;

    public CostRow(CostItem costItem) {
        this.costItem = costItem;
//        @Deprecated
//        this.costItemID = costItem.getCostItemID();
//        this.costCategoryID = costItem.getCostCategoryID();
//        this.costItemNotes = costItem.getCostItemNotes();
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

    public String getCostItemUnit(){
        return costItemUnit.get();
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
