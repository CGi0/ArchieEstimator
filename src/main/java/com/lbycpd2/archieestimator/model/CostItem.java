package com.lbycpd2.archieestimator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CostItem {
    private int costItemID;
    private String costItemName;
    private int costCategoryID;
    private String costItemNotes;
    private String costItemUnit;
    private BigDecimal costItemMaterialUnitCost;
    private BigDecimal costItemLaborUnitCost;

    public CostItem(){};

    public CostItem(String costItemName,
                    int costCategoryID,
                    String costItemNotes,
                    String unit,
                    BigDecimal materialUnitCost,
                    BigDecimal LaborUnitCost) {
        this.costItemName = costItemName;
        this.costCategoryID = costCategoryID;
        this.costItemNotes = costItemNotes;
        this.costItemUnit = unit;
        this.costItemMaterialUnitCost = materialUnitCost;
        this.costItemLaborUnitCost = LaborUnitCost;
    }
}
