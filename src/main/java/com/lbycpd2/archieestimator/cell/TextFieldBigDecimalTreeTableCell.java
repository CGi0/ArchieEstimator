package com.lbycpd2.archieestimator.cell;

import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;
import java.math.BigDecimal;
import java.util.function.BiConsumer;

public class TextFieldBigDecimalTreeTableCell<T> extends TextFieldTreeTableCell<T, BigDecimal> {

    private final BiConsumer<T, BigDecimal> updateFunction;

    public TextFieldBigDecimalTreeTableCell(BiConsumer<T, BigDecimal> updateFunction) {
        super(new StringConverter<>() {
            @Override
            public String toString(BigDecimal number) {
                return number == null ? "" : number.toString();
            }

            @Override
            public BigDecimal fromString(String s) {
                try {
                    return new BigDecimal(s);
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
