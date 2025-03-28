package com.lbycpd2.archieestimator.model;

import java.math.BigDecimal;

public class CostItem {
    private int id;
    private String string;
    private CostCategory costCategory;
    private String notes;
    private Unit unit;
    private BigDecimal materialUnitCost;
    private BigDecimal LaborUnitCost;
}
