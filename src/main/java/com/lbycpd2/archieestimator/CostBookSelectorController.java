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
import com.lbycpd2.archieestimator.service.CostItemService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.lbycpd2.archieestimator.util.DefaultCostCategory.DEFAULT_COST_CATEGORY;
import static com.lbycpd2.archieestimator.util.DefaultCostItem.DEFAULT_COST_ITEM;

@Slf4j
public class CostBookSelectorController {
    @Setter
    private CostTableController costTableController;

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
    @FXML private Button buttonApplyChanges;

    private CostGroup groupSelection;
    private CostCategory costCategorySelection;
    private CostItem costItemSelection;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxCostGroupManager;
    private ListViewNodeManager<CostCategory> listViewCostCategoryManager;
    private ListViewNodeManager<CostItem> listViewCostItemManager;
    private final SearchManager<CostCategory> costCategorySearchManager = new SearchManager<>();
    private final SearchManager<CostItem> costItemSearchManager = new SearchManager<>();

    private final CostItemService costItemService = CostItemService.getInstance();
    private final CostBookDAO cbDAO = new CostBookDAO();

    public void initialize() {
        choiceBoxCostGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup("No groups found"));
        listViewCostCategoryManager =
                new ListViewNodeManager<>(listViewCategory, textFieldCategory, DEFAULT_COST_CATEGORY, costCategorySearchManager);
        listViewCostItemManager =
                new ListViewNodeManager<>(listViewCostItem, textFieldCostItem, DEFAULT_COST_ITEM, costItemSearchManager);
        choiceBoxCostGroupManager.initialize(fetchCostGroups());
    }

    public void onApplyChangesAction() {
        try{
            if (costItemSelection != null) {
                costItemService.setCostItemSelection(costItemSelection);
                log.info("Selected Cost Item: {} ({})'", costItemSelection,costItemSelection.getCostItemID());
            }
            if (costTableController != null) {
                costTableController.addCostRow(costItemSelection);
            }
            Stage stage = (Stage) buttonApplyChanges.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("No Cost Item Selected");
            alert.setContentText("No cost item selected or item ID is null");
            alert.showAndWait();
            log.warn(e.getMessage());
        }
    }

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