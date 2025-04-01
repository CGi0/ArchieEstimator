package com.lbycpd2.archieestimator.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CostGroup {
    private int costGroupID;
    private final String costGroupName;

    @Override
    public String toString() {
        return costGroupName;
    }
}
