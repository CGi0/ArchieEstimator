package com.lbycpd2.archieestimator.service;

import com.lbycpd2.archieestimator.model.CostCategory;
import lombok.Getter;
import lombok.Setter;

@Setter
public class CostCategoryService {
    @Getter
    private static final CostCategoryService instance = new CostCategoryService();

    @Getter
    private CostCategory categorySelection;

    private CostCategoryService() {
        // Private constructor to prevent instantiation
    }

}
