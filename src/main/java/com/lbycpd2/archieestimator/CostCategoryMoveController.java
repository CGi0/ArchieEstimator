package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import com.lbycpd2.archieestimator.service.CostCategoryService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CostCategoryMoveController {
    @FXML Label labelCategoryName;
    @FXML Label labelExistingGroups;
    @FXML TextField textFieldCategoryName;
    @FXML ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML Button buttonMoveCategory;
    @FXML Button buttonBack;

    @Setter
    private CostCategoryController costCategoryController;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxCostGroupManager;
    private final CostGroupDAO cgDAO = new CostGroupDAO();
    private final CostCategoryDAO ccDAO = new CostCategoryDAO();

    private CostGroup groupSelection;

    private CostCategory categorySelection;
    private final CostCategoryService costCategoryService = CostCategoryService.getInstance();

    public void initialize(){
        choiceBoxCostGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup(""));
        initChoiceBoxManager();
        initChoiceBoxListener();

        try{
            categorySelection = costCategoryService.getCategorySelection();
            textFieldCategoryName.setText(categorySelection.getCostCategoryName());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onMoveCategoryAction(){
        try{
            ccDAO.update(categorySelection.getCostCategoryID(), new CostCategory(categorySelection.getCostCategoryName(), groupSelection.getCostGroupID()));
            onBackAction();
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public void onBackAction(){
        if (costCategoryController != null) {
            costCategoryController.initialize();
        }
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    private void initChoiceBoxManager(){
        choiceBoxCostGroupManager.initialize(cgDAO.getAll());
        choiceBoxGroup.getSelectionModel().selectFirst();
        this.groupSelection = choiceBoxGroup.getSelectionModel().getSelectedItem();
    }

    private void initChoiceBoxListener(){
        choiceBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            this.groupSelection = newValue);
    }
}
