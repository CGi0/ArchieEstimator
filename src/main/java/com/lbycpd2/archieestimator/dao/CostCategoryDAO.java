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
public class CostCategoryDAO implements DataAccessObject<CostCategory> {
    @Override
    public void save(CostCategory object) {
        final String SAVE_COST_CATEGORY_SQL = "INSERT INTO CostCategories (costCategoryName) VALUES (?)";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SAVE_COST_CATEGORY_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
            pstmt.executeUpdate();
            log.info("Cost category saved successfully!");

        } catch (SQLException e) {
            log.error("Error saving cost category: {}", e.getMessage());
        }
    }

    @Override
    public void update(int id, CostCategory object) {
        final String UPDATE_COST_CATEGORY_SQL = "UPDATE CostCategories SET costCategoryName = ? WHERE costCategoryID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_COST_CATEGORY_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
            pstmt.setInt(2, id);
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
        final String DELETE_COST_CATEGORY_SQL = "DELETE FROM CostCategories WHERE costCategoryID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_COST_CATEGORY_SQL)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Cost category deleted successfully!");
            } else {
                log.info("No cost category found with the given ID.");
            }

        } catch (SQLException e) {
            log.error("Error deleting cost category: {}", e.getMessage());
        }
    }

    @Override
    public int getID(CostCategory object) {
        final String GET_ID_SQL = "SELECT costCategoryID FROM CostCategories WHERE costCategoryName = ?";
        int id = 0;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ID_SQL)) {

            pstmt.setString(1, object.getCostCategoryName());
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
                    costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"));
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
                    costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"));
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
                CostCategory costCategory = new CostCategory(rs.getInt("costCategoryID"), rs.getString("costCategoryName"));
                costCategories.add(costCategory);
            }

        } catch (SQLException e) {
            log.error("Error getting all cost categories: {}", e.getMessage());
        }

        return costCategories;
    }
}
