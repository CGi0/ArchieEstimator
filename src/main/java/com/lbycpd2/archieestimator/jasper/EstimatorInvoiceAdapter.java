package com.lbycpd2.archieestimator.jasper;

import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.table.TabTable;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.ArrayList;
import java.util.HashMap;
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

    // Processes a single TabTable object using a lazy-loading cache
    private void processTabTable(TabTable tabTable) {
        String tabName = tabTable.getTab().getText(); // Get tab name
        List<CostRow> costRows = tabTable.getCostRowsExportList(); // Get cost rows
        CostCategoryDAO ccDAO = new CostCategoryDAO();

        // Create a cache to store cost categories keyed by their ID
        Map<Integer, CostCategory> costCategoryCache = new HashMap<>();

        for (CostRow costRow : costRows) {
            // Get the ID for the cost category from the current cost row
            Integer costCategoryID = costRow.getCostItem().getCostCategoryID();

            // Use computeIfAbsent to lazily load the cost category and cache it if not already present.
            CostCategory costCategory = costCategoryCache.computeIfAbsent(costCategoryID,
                    ccDAO::get);

            // Build the row data map including the cached cost category
            Map<String, Object> rowData = Map.of(
                    "tabName", tabName,
                    "costItemName", costRow.getCostItem().getCostItemName(),
                    "costItemCategory", costCategory.getCostCategoryName(),
                    "costItemQuantity", costRow.getCostItemQuantity(),
                    "costItemUnit", costRow.getCostItem().getCostItemUnit(),
                    "costItemMaterialUnitCost", costRow.getCostItemMaterialUnitCost(),
                    "costItemLaborUnitCost", costRow.getCostItemLaborUnitCost()
                    // Uncomment/add additional fields as needed
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
