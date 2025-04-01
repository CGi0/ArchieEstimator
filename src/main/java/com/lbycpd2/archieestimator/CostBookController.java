package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostBookDAO;
import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import com.lbycpd2.archieestimator.node.ListViewNodeManager;
import com.lbycpd2.archieestimator.node.SearchManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lbycpd2.archieestimator.util.DefaultCostCategory.DEFAULT_COST_CATEGORY;
import static com.lbycpd2.archieestimator.util.DefaultCostItem.DEFAULT_COST_ITEM;

@Slf4j
public class CostBookController {
    @FXML private Label labelGroup;
    @FXML private Label labelCategory;
    @FXML private Label labelCostItem;
    @FXML private Label labelName;
    @FXML private Label labelNotes;
    @FXML private Label labelUnitMeasurement;
    @FXML private Label labelMaterialUnitCost;
    @FXML private Label labelLaborUnitCost;
    @FXML private ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML private ListView<CostCategory> listViewCategory;
    @FXML private ListView<CostItem> listViewCostItem;
    @FXML private TextField textFieldCategory;
    @FXML private TextField textFieldCostItem;
    @FXML private TextField textFieldName;
    @FXML private TextArea textAreaNotes;
    @FXML private TextField textFieldUnitMeasurement;
    @FXML private TextField textFieldMaterialUnitCost;
    @FXML private TextField textFieldLaborUnitCost;
    @FXML private Button buttonEditGroups;
    @FXML private Button buttonEditCategories;
    @FXML private Button buttonEditCostItem;
    @FXML private Button buttonApplyChanges;

    private CostGroup groupSelection;
    private CostCategory costCategorySelection;
    private CostItem costItemSelection;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxCostGroupManager;
    private ListViewNodeManager<CostCategory> listViewCostCategoryManager;
    private ListViewNodeManager<CostItem> listViewCostItemManager;
    private final SearchManager<CostCategory> costCategorySearchManager = new SearchManager<>();
    private final SearchManager<CostItem> costItemSearchManager = new SearchManager<>();

    public void initialize() {
        choiceBoxCostGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup("No groups found"));
        listViewCostCategoryManager =
                new ListViewNodeManager<>(listViewCategory, textFieldCategory, DEFAULT_COST_CATEGORY, costCategorySearchManager);
        listViewCostItemManager =
                new ListViewNodeManager<>(listViewCostItem, textFieldCostItem, DEFAULT_COST_ITEM, costItemSearchManager);
        choiceBoxCostGroupManager.initialize(fetchCostGroups());
    }

    public void refreshCostGroups() {
        choiceBoxCostGroupManager.initialize(fetchCostGroups());
    }

    public void onEditGroupsAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-group.fxml"));
            Parent root = loader.load();
            CostGroupController controller = loader.getController();
            controller.setCostBookController(this); // Pass the reference of CostBookController to CostGroupController

            Stage stage = new Stage();
            stage.setTitle("Edit Cost Groups");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(event -> refreshCostGroups()); // Refresh cost groups when the window is closed
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void onEditCategoriesAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-category.fxml"));
            Parent root = loader.load();
            CostCategoryController controller = loader.getController();
            controller.setCostBookController(this); // Pass the reference of CostBookController to CostGroupController

            Stage stage = new Stage();
            stage.setTitle("Edit Cost Categories");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(event -> refreshCostGroups()); // Refresh cost groups when the window is closed
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void onEditCostItemAction() {}

    public void onApplyChangesAction() {}

    public void onCategorySearch() {
        try {
            listViewCostCategoryManager.onSearch();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostItemSearch() {
        try {
            listViewCostItemManager.onSearch();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onGroupAction() {
        try {
            groupSelection = choiceBoxGroup.getSelectionModel().getSelectedItem();
            listViewCostCategoryManager.initialize(fetchCostCategoryList());
            costCategorySelection = listViewCategory.getSelectionModel().getSelectedItem();
            listViewCostItemManager.initialize(fetchCostItemList());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCategoryClick() {
        try {
            costCategorySelection = listViewCategory.getSelectionModel().getSelectedItem();
            listViewCostItemManager.initialize(fetchCostItemList());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostItemClick() {
        try {
            if (null != costCategorySelection) {
                costItemSelection = listViewCostItem.getSelectionModel().getSelectedItem();
                updateCostItemInformation();
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private List<CostGroup> fetchCostGroups(){
        CostGroupDAO cgDAO = new CostGroupDAO();
        return cgDAO.getAll();
    }

    private List<CostCategory> fetchCostCategoryList() {
        CostCategoryDAO ccDAO = new CostCategoryDAO();
        List<CostCategory> costCategoryList = ccDAO.getAll(groupSelection.getCostGroupID());
        log.debug("Cost Category List: {}", costCategoryList);
        return costCategoryList;
    }

    private List<CostItem> fetchCostItemList() {
        try {
            CostBookDAO cbDAO = new CostBookDAO();
            List<CostItem> costItemList = new ArrayList<>(cbDAO.getAll(costCategorySelection.getCostCategoryID()));
            log.debug("Cost Item List: {}", costItemList);
            return costItemList;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return List.of(DEFAULT_COST_ITEM);
        }
    }

    private void updateCostItemInformation() {
        try {
            if (null != costItemSelection){
                textFieldName.setText(costItemSelection.getCostItemName());
                textAreaNotes.setText(costItemSelection.getCostItemNotes());
                textFieldUnitMeasurement.setText(costItemSelection.getCostItemUnit());
                textFieldMaterialUnitCost.setText(costItemSelection.getCostItemMaterialUnitCost().toString());
                textFieldLaborUnitCost.setText(costItemSelection.getCostItemLaborUnitCost().toString());
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}