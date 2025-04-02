package com.lbycpd2.archieestimator.cell;

import com.lbycpd2.archieestimator.service.CurrencyFormatService;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiConsumer;

public class TextFieldFinancialTreeTableCell<T> extends TextFieldTreeTableCell<T, BigDecimal> {

    private final BiConsumer<T, BigDecimal> updateFunction;

    public TextFieldFinancialTreeTableCell(BiConsumer<T, BigDecimal> updateFunction) {
        super(new StringConverter<BigDecimal>() {
            private final NumberFormat currencyFormat = CurrencyFormatService.getInstance().getCurrenyFormat();

            @Override
            public String toString(BigDecimal number) {
                return number == null ? "" : currencyFormat.format(number);
            }

            @Override
            public BigDecimal fromString(String s) {
                try {
                    return new BigDecimal(s.replaceAll("[^\\d.]", ""));
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO; // or handle the error as needed
                }
            }
        });
        this.updateFunction = updateFunction;
    }

    @Override
    public void commitEdit(BigDecimal newValue) {
        super.commitEdit(newValue);
        TreeItem<T> currentEditingRow = getTreeTableView().getTreeItem(getIndex());
        updateFunction.accept(currentEditingRow.getValue(), newValue);
    }
}
