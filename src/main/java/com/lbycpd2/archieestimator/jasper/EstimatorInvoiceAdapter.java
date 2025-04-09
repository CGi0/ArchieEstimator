package com.lbycpd2.archieestimator.jasper;

import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.table.TabTable;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstimatorInvoiceAdapter implements JRDataSource {

    private final List<Map<String, Object>> crossTabData;
    private int currentIndex = -1;

    // Constructor accepts a list of TabTable objects
    public EstimatorInvoiceAdapter(List<TabTable> tabTables) {
        crossTabData = new ArrayList<>();

        for (TabTable tabTable : tabTables) {
            processTabTable(tabTable);
        }
    }

    // Processes a single TabTable object
    private void processTabTable(TabTable tabTable) {
        String tabName = tabTable.getTab().getText(); // Get tab name
        List<CostRow> costRows = tabTable.getCostRowsExportList(); // Get cost rows

        for (CostRow costRow : costRows) {
            Map<String, Object> rowData = Map.of(
                    "tabName", tabName,
                    "costItemName", costRow.getCostItem().getCostItemName(),
                    "costItemQuantity", costRow.getCostItemQuantity(),
                    "costItemUnit", costRow.getCostItem().getCostItemUnit(),
                    "costItemMaterialUnitCost", costRow.getCostItemMaterialUnitCost(),
                    "costItemLaborUnitCost", costRow.getCostItemLaborUnitCost()/*,
                    "laborAndMaterialUnitCost",
                    costRow.getCostItemMaterialUnitCost().add(costRow.getCostItemLaborUnitCost()),
                    "totalCost",
                    costRow.getCostItemMaterialUnitCost().add(costRow.getCostItemLaborUnitCost())
                            .multiply(costRow.getCostItemQuantity())*/
            );
            crossTabData.add(rowData);
        }
    }

    @Override
    public boolean next() throws JRException {
        currentIndex++;
        return currentIndex < crossTabData.size();
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        String fieldName = jrField.getName();
        return crossTabData.get(currentIndex).get(fieldName);
    }
}
