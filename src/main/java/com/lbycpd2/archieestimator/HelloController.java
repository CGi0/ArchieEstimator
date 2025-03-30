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

        if (!tableExists("CostBook")) {
            String createTableSQL = """
            CREATE TABLE CostBook (
                costItemID               INTEGER PRIMARY KEY AUTOINCREMENT,
                costItemName             TEXT,
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

            try (Connection conn = SQLConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(createTableSQL);
                log.info("Created table CostBook and CostCategories");

            } catch (SQLException e) {
                log.error("Error creating tables: {}", e.getMessage());
            }
        }

        final String INSERT_COST_ITEM_SQL =
                "INSERT INTO CostBook (costItemName, " +
                        "costCategoryID, " +
                        "costItemNotes, " +
                        "costItemUnit, " +
                        "costItemMaterialUnitCost, " +
                        "costItemLaborUnitCost) VALUES (?, ?, ?, ?, ?, ?)";
        CostItem ci = new CostItem("Block",0,"None.","sqm",new BigDecimal("100.0"),new BigDecimal("100.0"));

        try(Connection conn = SQLConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_COST_ITEM_SQL);){
                pstmt.setString(1, ci.getCostItemName());
                pstmt.setInt(2, ci.getCostCategoryID());
                pstmt.setString(3, ci.getCostItemNotes());
                pstmt.setString(4, ci.getCostItemUnit());
                pstmt.setBigDecimal(5, ci.getCostItemMaterialUnitCost());
                pstmt.setBigDecimal(6, ci.getCostItemLaborUnitCost());

                pstmt.executeUpdate();
                log.info("Inserted cost item");

        } catch (SQLException throwables) {
            log.error(throwables.getMessage());
        }

    }

    private boolean tableExists(String tableName) {
        String checkTableSQL = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";

        try (Connection conn = SQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSQL)) {

            if (rs.next() && rs.getInt(1) > 0) {
                log.info("Table {} exists.", tableName);
                return true;
            } else {
                log.info("Table {} does not exist.", tableName);
                return false;
            }
        } catch (Exception e) {
            log.error("Error checking if table exists: {}", e.getMessage());
        }
        return false;
    }
}