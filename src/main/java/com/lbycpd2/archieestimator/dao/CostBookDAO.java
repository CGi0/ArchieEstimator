package com.lbycpd2.archieestimator.dao;

import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.util.SQLConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CostBookDAO implements DataAccessObject<CostItem>{
    @Override
    public void save(CostItem object) {
        final String INSERT_COST_ITEM_SQL = """
                        INSERT INTO CostBook (costItemName,
                        costCategoryID,
                        costItemNotes,
                        costItemUnit,
                        costItemMaterialUnitCost,
                        costItemLaborUnitCost) VALUES (?, ?, ?, ?, ?, ?)""";

        try(Connection conn = SQLConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_COST_ITEM_SQL)){
            pstmt.setString(1, object.getCostItemName());
            pstmt.setInt(2, object.getCostCategoryID());
            pstmt.setString(3, object.getCostItemNotes());
            pstmt.setString(4, object.getCostItemUnit());
            pstmt.setBigDecimal(5, object.getCostItemMaterialUnitCost());
            pstmt.setBigDecimal(6, object.getCostItemLaborUnitCost());

            pstmt.executeUpdate();
            log.info("Inserted cost item");

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void update(int id, CostItem object) {
        final String UPDATE_COST_ITEM_SQL = """
                UPDATE CostItems SET costItemName = ?,
                costCategoryID = ?,
                costItemNotes = ?,
                costItemUnit = ?,
                costItemMaterialUnitCost = ?,
                costItemLaborUnitCost = ?
                WHERE costItemID = ?""";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_COST_ITEM_SQL)) {

            pstmt.setString(1, object.getCostItemName());
            pstmt.setInt(2, object.getCostCategoryID());
            pstmt.setString(3, object.getCostItemNotes());
            pstmt.setString(4, object.getCostItemUnit());
            pstmt.setBigDecimal(5, object.getCostItemMaterialUnitCost());
            pstmt.setBigDecimal(6, object.getCostItemLaborUnitCost());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Cost item updated successfully!");

            } else {
                log.info("No cost item found with the given ID.");
            }

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        final String DELETE_COST_ITEM_SQL = "DELETE FROM CostItems WHERE costItemID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_COST_ITEM_SQL)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Cost item deleted successfully!");
            } else {
                log.info("No cost item found with the given ID.");
            }

        } catch (SQLException e) {
            log.error("Error deleting cost item: {}", e.getMessage());
        }
    }

    @Override
    public int getID(CostItem object) {
        final String GET_ID_SQL = "SELECT costItemID FROM CostItems WHERE costItemName = ? AND costCategoryID = ? AND costItemNotes = ? AND costItemUnit = ? AND costItemMaterialUnitCost = ? AND costItemLaborUnitCost = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ID_SQL)) {

            pstmt.setString(1, object.getCostItemName());
            pstmt.setInt(2, object.getCostCategoryID());
            pstmt.setString(3, object.getCostItemNotes());
            pstmt.setString(4, object.getCostItemUnit());
            pstmt.setBigDecimal(5, object.getCostItemMaterialUnitCost());
            pstmt.setBigDecimal(6, object.getCostItemLaborUnitCost());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("costItemID");
                } else {
                    log.info("No matching cost item found.");
                    return -1; // Return -1 if no matching record is found
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving cost item ID: {}", e.getMessage());
            return -1; // Return -1 in case of an error
        }
    }

    @Override
    public CostItem get(int id) {
        final String GET_COST_ITEM_BY_ID_SQL = "SELECT * FROM CostItems WHERE costItemID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_ITEM_BY_ID_SQL)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CostItem costItem = new CostItem();
                    costItem.setCostItemID(rs.getInt("costItemID"));
                    costItem.setCostItemName(rs.getString("costItemName"));
                    costItem.setCostCategoryID(rs.getInt("costCategoryID"));
                    costItem.setCostItemNotes(rs.getString("costItemNotes"));
                    costItem.setCostItemUnit(rs.getString("costItemUnit"));
                    costItem.setCostItemMaterialUnitCost(rs.getBigDecimal("costItemMaterialUnitCost"));
                    costItem.setCostItemLaborUnitCost(rs.getBigDecimal("costItemLaborUnitCost"));
                    return costItem;
                } else {
                    log.info("No cost item found with the given ID.");
                    return null; // Return null if no matching record is found
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving cost item: {}", e.getMessage());
            return null; // Return null in case of an error
        }
    }

    @Override
    public CostItem get(String name) {
        final String GET_COST_ITEM_BY_NAME_SQL = "SELECT * FROM CostItems WHERE costItemName LIKE ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_ITEM_BY_NAME_SQL)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CostItem costItem = new CostItem();
                    costItem.setCostItemID(rs.getInt("costItemID"));
                    costItem.setCostItemName(rs.getString("costItemName"));
                    costItem.setCostCategoryID(rs.getInt("costCategoryID"));
                    costItem.setCostItemNotes(rs.getString("costItemNotes"));
                    costItem.setCostItemUnit(rs.getString("costItemUnit"));
                    costItem.setCostItemMaterialUnitCost(rs.getBigDecimal("costItemMaterialUnitCost"));
                    costItem.setCostItemLaborUnitCost(rs.getBigDecimal("costItemLaborUnitCost"));
                    return costItem;
                } else {
                    log.info("No cost item found with the given name.");
                    return null; // Return null if no matching record is found
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving cost item: {}", e.getMessage());
            return null; // Return null in case of an error
        }
    }

    @Override
    public List<CostItem> getAll() {
        final String GET_ALL_COST_ITEMS_SQL = "SELECT * FROM CostItems";
        List<CostItem> costItems = new ArrayList<>();

        try (Connection conn = SQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_COST_ITEMS_SQL)) {

            while (rs.next()) {
                CostItem costItem = new CostItem();
                costItem.setCostItemID(rs.getInt("costItemID"));
                costItem.setCostItemName(rs.getString("costItemName"));
                costItem.setCostCategoryID(rs.getInt("costCategoryID"));
                costItem.setCostItemNotes(rs.getString("costItemNotes"));
                costItem.setCostItemUnit(rs.getString("costItemUnit"));
                costItem.setCostItemMaterialUnitCost(rs.getBigDecimal("costItemMaterialUnitCost"));
                costItem.setCostItemLaborUnitCost(rs.getBigDecimal("costItemLaborUnitCost"));
                costItems.add(costItem);
            }
        } catch (SQLException e) {
            log.error("Error retrieving all cost items: {}", e.getMessage());
        }
        return costItems;
    }
}
