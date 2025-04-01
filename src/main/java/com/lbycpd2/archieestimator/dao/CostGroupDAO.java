package com.lbycpd2.archieestimator.dao;

import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.util.SQLConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class CostGroupDAO implements DataAccessObjectInterface<CostGroup> {
    @Override
    public void save(CostGroup object) {
        final String SAVE_COST_GROUP_SQL = "INSERT INTO CostGroups (costGroupName) VALUES (?)";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SAVE_COST_GROUP_SQL)) {

            pstmt.setString(1, object.getCostGroupName());
            pstmt.executeUpdate();
            log.info("Cost group saved successfully!");

        } catch (SQLException e) {
            log.error("Error saving cost group: {}", e.getMessage());
        }
    }

    @Override
    public void update(int id, CostGroup object) {
        final String UPDATE_COST_GROUP_SQL = "UPDATE CostGroups SET costGroupName = ? WHERE costGroupID = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_COST_GROUP_SQL)) {

            pstmt.setString(1, object.getCostGroupName());
            pstmt.setInt(2, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Cost group updated successfully!");
            } else {
                log.info("No cost group found with the given ID.");
            }

        } catch (SQLException e) {
            log.error("Error updating cost group: {}", e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        final String DELETE_COST_ITEMS_SQL = "DELETE FROM CostBook WHERE costCategoryID IN (SELECT costCategoryID FROM CostCategories WHERE costGroupID = ?)";
        final String DELETE_COST_CATEGORIES_SQL = "DELETE FROM CostCategories WHERE costGroupID = ?";
        final String DELETE_COST_GROUP_SQL = "DELETE FROM CostGroups WHERE costGroupID = ?";

        try (Connection conn = SQLConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmtDeleteItems = conn.prepareStatement(DELETE_COST_ITEMS_SQL);
                 PreparedStatement pstmtDeleteCategories = conn.prepareStatement(DELETE_COST_CATEGORIES_SQL);
                 PreparedStatement pstmtDeleteGroup = conn.prepareStatement(DELETE_COST_GROUP_SQL)) {

                // Delete cost items
                pstmtDeleteItems.setInt(1, id);
                pstmtDeleteItems.executeUpdate();

                // Delete cost categories
                pstmtDeleteCategories.setInt(1, id);
                pstmtDeleteCategories.executeUpdate();

                // Delete cost group
                pstmtDeleteGroup.setInt(1, id);
                int rowsAffected = pstmtDeleteGroup.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    log.info("Cost group and associated categories and items deleted successfully!");
                } else {
                    conn.rollback(); // Rollback transaction
                    log.info("No cost group found with the given ID.");
                }

            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on error
                log.error("Error deleting cost group and associated data: {}", e.getMessage());
            }

        } catch (SQLException e) {
            log.error("Database connection error: {}", e.getMessage());
        }
    }


    @Override
    public int getID(CostGroup object) {
        final String GET_ID_SQL = "SELECT costGroupID FROM CostGroups WHERE costGroupName = ?";
        int id = 0;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ID_SQL)) {

            pstmt.setString(1, object.getCostGroupName());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("costGroupID");
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost group ID: {}", e.getMessage());
        }

        return id;
    }

    @Override
    public CostGroup get(int id) {
        final String GET_COST_GROUP_SQL = "SELECT * FROM CostGroups WHERE costGroupID = ?";
        CostGroup costGroup = null;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_GROUP_SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    costGroup = new CostGroup(rs.getInt("costGroupID"), rs.getString("costGroupName"));
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost group: {}", e.getMessage());
        }

        return costGroup;
    }

    @Override
    public CostGroup get(String name) {
        final String GET_COST_GROUP_SQL = "SELECT * FROM CostGroups WHERE costGroupName = ?";
        CostGroup costGroup = null;

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COST_GROUP_SQL)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    costGroup = new CostGroup(rs.getInt("costGroupID"), rs.getString("costGroupName"));
                }
            }

        } catch (SQLException e) {
            log.error("Error getting cost group: {}", e.getMessage());
        }

        return costGroup;
    }

    @Override
    public List<CostGroup> getAll() {
        final String GET_ALL_COST_GROUPS_SQL = "SELECT * FROM CostGroups";
        ObservableList<CostGroup> costGroups = FXCollections.observableArrayList();

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ALL_COST_GROUPS_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CostGroup costGroup = new CostGroup(rs.getInt("costGroupID"), rs.getString("costGroupName"));
                costGroups.add(costGroup);
            }

        } catch (SQLException e) {
            log.error("Error getting all cost groups: {}", e.getMessage());
        }

        return costGroups;
    }
}