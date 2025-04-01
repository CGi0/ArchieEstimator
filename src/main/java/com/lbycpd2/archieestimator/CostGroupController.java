package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class CostGroupController {
    @FXML Label labelGroupName;
    @FXML Label labelExistingGroups;
    @FXML TextField textFieldGroupName;
    @FXML ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML Button buttonAddGroup;
    @FXML Button buttonRemoveGroup;
    @FXML Button buttonRenameGroup;
    @FXML Button buttonBack;

    @Setter
    private CostBookController costBookController;
    private ChoiceBoxNodeManager<CostGroup> costGroupNodeManager;
    private final CostGroupDAO cgDAO = new CostGroupDAO();
    private CostGroup groupSelection;

    public void initialize(){
        costGroupNodeManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup(""));
        initChoiceBoxManager();
        initChoiceBox();
    }

    private void onGroupSelection(CostGroup groupSelection){
        this.groupSelection = groupSelection;
        textFieldGroupName.setText(String.valueOf(groupSelection));
    }

    public void onAddGroupAction(){
        if (textFieldGroupName.getText().isEmpty()) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Warning Dialog");
            warningAlert.setHeaderText("Empty Group Name");
            warningAlert.setContentText("Cannot have an empty group name");
            warningAlert.showAndWait();
            return;
        }

        CostGroup addCG = new CostGroup(textFieldGroupName.getText());
        cgDAO.save(addCG);
        int id = cgDAO.getID(addCG);
        addCG.setCostGroupID(id);
        initChoiceBoxManager();
        choiceBoxGroup.getSelectionModel().select(addCG);
    }

    public void onRenameGroupAction(){
        try {
            CostGroup renameCG = new CostGroup(groupSelection.getCostGroupID(), textFieldGroupName.getText());
            cgDAO.update(renameCG.getCostGroupID(), renameCG);
            initChoiceBoxManager();
            choiceBoxGroup.getSelectionModel().select(renameCG);
        } catch (Exception e) {
            log.warn(e.getMessage());

            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Warning Dialog");
            warningAlert.setHeaderText("No Group Selected");
            warningAlert.setContentText("No group selected or group ID is null");
            warningAlert.showAndWait();
        }
    }

    public void onRemoveGroupAction() {
        if (groupSelection != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation Dialog");
            confirmDialog.setHeaderText("Delete Cost Group?");
            confirmDialog.setContentText(String.format("Are you sure you want to remove %s\nand all its associated categories and items?", groupSelection.getCostGroupName()));

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    cgDAO.delete(groupSelection.getCostGroupID());
                    initChoiceBoxManager();
                    textFieldGroupName.clear();
                    choiceBoxGroup.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            } else {
                log.info("User chose Cancel or closed the dialog");
            }
        } else {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Warning Dialog");
            warningAlert.setHeaderText("No Group Selected");
            warningAlert.setContentText("No group selected or group ID is null");
            warningAlert.showAndWait();
        }
    }

    public void onBackAction() {
        if (costBookController != null) {
            costBookController.refreshCostGroups(); // Refresh cost groups in CostBookController
        }
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }


    private void initChoiceBoxManager(){
        costGroupNodeManager.initialize(cgDAO.getAll());
        choiceBoxGroup.getSelectionModel().clearSelection();
    }

    private void initChoiceBox(){
        choiceBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                onGroupSelection(newValue));
    }
}