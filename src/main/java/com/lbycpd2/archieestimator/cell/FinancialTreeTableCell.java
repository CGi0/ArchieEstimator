package com.lbycpd2.archieestimator.cell;

import com.lbycpd2.archieestimator.service.CurrencyFormatService;
import javafx.scene.control.TreeTableCell;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class FinancialTreeTableCell<T> extends TreeTableCell<T, BigDecimal> {

    private final NumberFormat currencyFormat = CurrencyFormatService.getInstance().getCurrenyFormat();

    @Override
    protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(currencyFormat.format(item));
        }
    }
}
