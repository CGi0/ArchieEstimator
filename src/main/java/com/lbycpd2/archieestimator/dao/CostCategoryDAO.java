package com.lbycpd2.archieestimator.dao;

import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.util.SQLConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CostCategoryDAO implements DataAccessObjectInterface<CostCategory> {
    @Override
    public void save(CostCategory object) {
        final String SAVE_COST_CATEGORY_SQL = "INSERT INTO CostCategories (costCategoryName, costGroupID) VALUES (?, ?)";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SAVE_COST_CATEGORY_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
            pstmt.setInt(2, object.getCostGroupID());
            pstmt.executeUpdate();
            log.info("Cost category saved successfully!");

        } catch (SQLException e) {
            log.error("Error saving cost category: {}", e.getMessage());
        }
    }

    @Override
    public void update(int id, CostCategory object) {
        final String UPDATE_COST_CATEGORY_SQL = "UPDATE CostCategories SET costCategoryName = ?, costGroupID = ? WHERE costCategoryID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_COST_CATEGORY_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
            pstmt.setInt(2, object.getCostGroupID());
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Cost category updated successfully!");
            } else {
                log.info("No cost category found with the given ID.");
            }

        } catch (SQLException e) {
            log.error("Error updating cost category: {}", e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        final String DELETE_COST_ITEMS_SQL = "DELETE FROM CostBook WHERE costCategoryID = ?";
        final String DELETE_COST_CATEGORY_SQL = "DELETE FROM CostCategories WHERE costCategoryID = ?";

        try (Connection conn = SQLConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmtDeleteItems = conn.prepareStatement(DELETE_COST_ITEMS_SQL);
                 PreparedStatement pstmtDeleteCategory = conn.prepareStatement(DELETE_COST_CATEGORY_SQL)) {

                // Delete associated cost items
                pstmtDeleteItems.setInt(1, id);
                pstmtDeleteItems.executeUpdate();

                // Delete cost category
                pstmtDeleteCategory.setInt(1, id);
                int rowsAffected = pstmtDeleteCategory.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    log.info("Cost category and associated cost items deleted successfully!");
                } else {
                    conn.rollback(); // Rollback transaction
                    log.info("No cost category found with the given ID.");
                }

            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on error
                log.error("Error deleting cost category and associated cost items: {}", e.getMessage());
            }

        } catch (SQLException e) {
            log.error("Database connection error: {}", e.getMessage());
        }
    }

    @Override
    public int getID(CostCategory object) {
        final String GET_ID_SQL = "SELECT costCategoryID FROM CostCategories WHERE costCategoryName = ? AND costGroupID = ?";
        int id = 0;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ID_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
            pstmt.setInt(2, object.getCostGroupID());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("costCategoryID");
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost category ID: {}", e.getMessage());
        }

        return id;
    }

    @Override
    public CostCategory get(int id) {
        final String GET_COST_CATEGORY_SQL = "SELECT * FROM CostCategories WHERE costCategoryID = ?";
        CostCategory costCategory = null;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_CATEGORY_SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"), rs.getInt("costGroupID"));
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost category: {}", e.getMessage());
        }

        return costCategory;
    }

    @Override
    public CostCategory get(String name) {
        final String GET_COST_CATEGORY_SQL = "SELECT * FROM CostCategories WHERE costCategoryName = ?";
        CostCategory costCategory = null;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_CATEGORY_SQL)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"), rs.getInt("costGroupID"));
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost category: {}", e.getMessage());
        }

        return costCategory;
    }

    @Override
    public List<CostCategory> getAll() {
        final String GET_ALL_COST_CATEGORIES_SQL = "SELECT * FROM CostCategories";
        List<CostCategory> costCategories = new ArrayList<>();

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ALL_COST_CATEGORIES_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CostCategory costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"), rs.getInt("costGroupID"));
                costCategories.add(costCategory);
            }

        } catch (SQLException e) {
            log.error("Error getting all cost categories: {}", e.getMessage());
        }

        return costCategories;
    }

    public List<CostCategory> getAll(int id) {
        final String GET_ALL_COST_CATEGORIES_SQL = "SELECT * FROM CostCategories WHERE costGroupID = ?";
        List<CostCategory> costCategories = new ArrayList<>();

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ALL_COST_CATEGORIES_SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CostCategory costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"), rs.getInt("costGroupID"));
                    costCategories.add(costCategory);
                }
            }

        } catch (SQLException e) {
            log.error("Error getting all cost categories: {}", e.getMessage());
        }

        return costCategories;
    }
}