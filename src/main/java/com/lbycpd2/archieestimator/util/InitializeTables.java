package com.lbycpd2.archieestimator.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class InitializeTables {
    public void start(){
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
                costCategoryID   INTEGER PRIMARY KEY AUTOINCREMENT,
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
                log.warn("Table {} does not exist.", tableName);
                return false;
            }
        } catch (Exception e) {
            log.error("Error checking if table exists: {}", e.getMessage());
        }
        return false;
    }
}
