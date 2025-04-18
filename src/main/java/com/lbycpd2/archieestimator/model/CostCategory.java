package com.lbycpd2.archieestimator.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CostCategory {
    private int costCategoryID;
    private final String costCategoryName;
    private final int costGroupID;

    @Override
    public String toString() {
        return costCategoryName;
    }
}
