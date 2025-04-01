package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import com.lbycpd2.archieestimator.node.ListViewNodeManager;
import com.lbycpd2.archieestimator.node.SearchManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.lbycpd2.archieestimator.util.DefaultCostCategory.DEFAULT_COST_CATEGORY;

@Slf4j
public class CostCategoryController {
    @FXML Label labelExistingGroups;
    @FXML Label labelCategoryName;
    @FXML ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML TextField textFieldCategoryName;
    @FXML ListView<CostCategory> listViewCategory;
    @FXML Button buttonAddToGroup;
    @FXML Button buttonRemoveCategory;
    @FXML Button buttonRenameCategory;
    @FXML Button buttonMoveToGroup;
    @FXML Button buttonBack;

    @Setter
    private CostBookController costBookController;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxCostGroupManager;
    private ListViewNodeManager<CostCategory> listViewCostCategoryManager;

    private final CostGroupDAO cgDAO = new CostGroupDAO();
    private final CostCategoryDAO ccDAO = new CostCategoryDAO();
    private final SearchManager<CostCategory> costCategorySearchManager = new SearchManager<>();

    private CostGroup groupSelection;
    private CostCategory categorySelection;

    public void initialize() {
        choiceBoxCostGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup(""));
        listViewCostCategoryManager =
                new ListViewNodeManager<>(listViewCategory, textFieldCategoryName, DEFAULT_COST_CATEGORY, costCategorySearchManager);
        initChoiceBoxManager();
        initChoiceBox();
        initListViewManager();
        initListView();
    }

    public void onAddCategoryAction(){

    }

    public void onRemoveCategoryAction(){}

    public void onRenameCategoryAction(){}

    public void onMoveToGroupAction(){}

    public void onBackAction() {
        if (costBookController != null) {
            costBookController.refreshCostGroups(); // Refresh cost groups in CostBookController
        }
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    private void initChoiceBoxManager(){
        choiceBoxCostGroupManager.initialize(cgDAO.getAll());
        choiceBoxGroup.getSelectionModel().selectFirst();
        this.groupSelection = choiceBoxGroup.getSelectionModel().getSelectedItem();
    }

    private void initListViewManager(){
        try {
            listViewCostCategoryManager.initialize(ccDAO.getAll(groupSelection.getCostGroupID()));
            listViewCategory.getSelectionModel().clearSelection();
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    private void initChoiceBox(){
        choiceBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.groupSelection = newValue;
            initListViewManager();
        });
    }

    private void initListView(){
        listViewCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                this.categorySelection = newValue);
    }
}
