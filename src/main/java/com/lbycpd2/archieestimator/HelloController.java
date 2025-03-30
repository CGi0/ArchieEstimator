package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.util.SQLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.*;

@Slf4j
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Adding!");

        if(tableExists("CostBook")){
            String createTableSQL = """
                    CREATE TABLE CostBook (
                        costItemID               INTEGER PRIMARY KEY,
                        costItemName,
                        costCategoryID           NUMERIC REFERENCES CostCategories (costCategoryID),
                        costItemNotes            TEXT,
                        costItemUnit             TEXT,
                        costItemMaterialUnitCost NUMERIC,
                        costItemLaborUnitCost    NUMERIC
                    );
                    CREATE TABLE CostCategories (
                        costCategoryID   INTEGER PRIMARY KEY,
                        costCategoryName TEXT
                    );
                    
                    """;
            log.info("Created table CostBook");
        } else {
            log.info("Table CostBook already exists!");
        }

        final String INSERT_COST_ITEM_SQL =
                "INSERT INTO CostBook (costItemID, " +
                        "costItemName, " +
                        "costCategoryID, " +
                        "costItemNotes, " +
                        "costItemUnit, " +
                        "costItemMaterialUnitCost, " +
                        "costItemLaborUnitCost) VALUES (?, ?, ?, ?, ?, ?, ?)";
        CostItem ci = new CostItem(1,"Block",0,"None.","sqm",new BigDecimal(100),new BigDecimal(100));

        try(Connection conn = SQLConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_COST_ITEM_SQL);){
                pstmt.setInt(1, ci.getCostItemID());
                pstmt.setString(2, ci.getCostItemName());
                pstmt.setInt(3, ci.getCostCategoryID());
                pstmt.setString(4, ci.getCostItemNotes());
                pstmt.setString(5, ci.getCostItemUnit());
                pstmt.setBigDecimal(6, ci.getCostItemMaterialUnitCost());
                pstmt.setBigDecimal(7, ci.getCostItemLaborUnitCost());

                pstmt.executeUpdate();
                System.out.println("Cost item inserted successfully!");

        } catch (SQLException throwables) {
            log.error(throwables.getMessage());
        }

    }

    private boolean tableExists(String tableName) {
        String checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '' AND table_name = '" + tableName + "'";

        try (Connection conn = SQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSQL)) {

            if (rs.next()) {
                String databaseName = rs.getString("table_schema");
                System.out.println("Table " + tableName + " exists in database: " + databaseName);
                return true;
            } else {
                System.out.println("Table " + tableName + " does not exist in any database.");
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
}