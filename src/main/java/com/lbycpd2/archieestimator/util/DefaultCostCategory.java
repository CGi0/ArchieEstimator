package com.lbycpd2.archieestimator.util;

import com.lbycpd2.archieestimator.model.CostCategory;

public class DefaultCostCategory {
    public static final CostCategory DEFAULT_COST_CATEGORY = new CostCategory("No categories found", -1);

    private DefaultCostCategory() {
        // Private constructor to prevent instantiation
    }
}
