package com.lbycpd2.archieestimator.service;

import com.lbycpd2.archieestimator.CostTableController;
import lombok.Getter;
import lombok.Setter;

@Setter
public class CostTableControllerService {
    @Getter
    private static final CostTableControllerService instance = new CostTableControllerService();

    @Getter
    private CostTableController costTableController;

    private void costTableController() {
        // Private constructor to prevent instantiation
    }

}
