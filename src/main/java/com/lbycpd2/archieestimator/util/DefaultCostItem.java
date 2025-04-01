package com.lbycpd2.archieestimator.util;

import com.lbycpd2.archieestimator.model.CostItem;
import java.math.BigDecimal;

public class DefaultCostItem {
    public static final CostItem DEFAULT_COST_ITEM = new CostItem("No cost items found", -1, "", "", new BigDecimal("0"), new BigDecimal("0"));

    private DefaultCostItem() {
        // Private constructor to prevent instantiation
    }
}
