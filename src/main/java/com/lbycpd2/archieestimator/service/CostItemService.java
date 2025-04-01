package com.lbycpd2.archieestimator.service;

import com.lbycpd2.archieestimator.model.CostItem;
import lombok.Getter;
import lombok.Setter;

@Setter
public class CostItemService {
    @Getter
    private static final CostItemService instance = new CostItemService();

    @Getter
    private CostItem costItemSelection;

    private CostItemService() {
        // Private constructor to prevent instantiation
    }

}
