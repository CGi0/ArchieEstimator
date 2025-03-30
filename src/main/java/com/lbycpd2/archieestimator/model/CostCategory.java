package com.lbycpd2.archieestimator.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CostCategory {
    private int costCategoryID;
    private int costGroupID;
    private final String costCategoryName;
}
