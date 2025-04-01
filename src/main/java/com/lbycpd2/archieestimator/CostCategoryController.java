package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import com.lbycpd2.archieestimator.node.ListViewNodeManager;
import com.lbycpd2.archieestimator.node.SearchManager;
import com.lbycpd2.archieestimator.service.CostCategoryService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static com.lbycpd2.archieestimator.util.DefaultCostCategory.DEFAULT_COST_CATEGORY;

@Slf4j
public class CostCategoryController {
    @FXML private Label labelExistingGroups;
    @FXML private Label labelCategoryName;
    @FXML private ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML private TextField textFieldCategoryName;
    @FXML private ListView<CostCategory> listViewCategory;
    @FXML private Button buttonAddToGroup;
    @FXML private Button buttonRemoveCategory;
    @FXML private Button buttonRenameCategory;
    @FXML private Button buttonMoveToGroup;
    @FXML private Button buttonBack;

    @Setter
    private CostBookController costBookController;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxCostGroupManager;
    private ListViewNodeManager<CostCategory> listViewCostCategoryManager;

    private final CostGroupDAO cgDAO = new CostGroupDAO();
    private final CostCategoryDAO ccDAO = new CostCategoryDAO();
    private final SearchManager<CostCategory> costCategorySearchManager = new SearchManager<>();

    private CostGroup groupSelection;
    @Getter
    private CostCategory categorySelection;

    private final CostCategoryService costCategoryService = CostCategoryService.getInstance();

    public void initialize() {
        initializeManagers();
        initializeListeners();
        costCategoryService.setCategorySelection(categorySelection);
    }

    private void initializeManagers() {
        choiceBoxCostGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup(""));
        listViewCostCategoryManager = new ListViewNodeManager<>(listViewCategory, null, DEFAULT_COST_CATEGORY, costCategorySearchManager);
        initChoiceBoxManager();
        initListViewManager();
    }

    private void initializeListeners() {
        initChoiceBoxListener();
        initListViewListener();
    }

    public void onAddCategoryAction() {
        if (textFieldCategoryName.getText().isEmpty()) {
            showAlert("Empty Category Name", "Cannot have an empty category name");
            return;
        }

        if (groupSelection != null) {
            ccDAO.save(new CostCategory(textFieldCategoryName.getText(), groupSelection.getCostGroupID()));
            initListViewManager();
        } else {
            log.warn("Group selection is null");
        }
    }

    public void onRemoveCategoryAction() {
        if (groupSelection != null && categorySelection != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation Dialog");
            confirmDialog.setHeaderText("Delete Cost Category?");
            confirmDialog.setContentText(String.format("Are you sure you want to remove %s\nand all its associated items?", categorySelection.getCostCategoryName()));

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    ccDAO.delete(categorySelection.getCostCategoryID());
                    initListViewManager();
                    textFieldCategoryName.clear();
                    listViewCategory.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            } else {
                log.info("User chose Cancel or closed the dialog");
            }
        } else {
            showAlert("No Category Selected", "No category selected or category ID is null");
        }
    }

    public void onRenameCategoryAction() {
        if (categorySelection == null) {
            showAlert("No Category Selected", "No category selected or category ID is null");
            return;
        }

        if (groupSelection == null) {
            log.warn("Group selection is null");
            return;
        }

        try {
            CostCategory renameCC = new CostCategory(textFieldCategoryName.getText(), groupSelection.getCostGroupID());
            ccDAO.update(categorySelection.getCostCategoryID(), renameCC);
            int id = ccDAO.getID(renameCC);
            renameCC.setCostCategoryID(id);
            initListViewManager();
            listViewCategory.getSelectionModel().select(renameCC);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onMoveToGroupAction() {
        if (categorySelection == null) {
            showAlert("No Category Selected", "No category selected or category ID is null");
            return;
        }

        if (-1 == categorySelection.getCostGroupID()){
            showAlert("Invalid Category Selected", "No category selected or category ID is null");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-category-move.fxml"));
            costCategoryService.setCategorySelection(categorySelection);
            Parent root = loader.load();
            CostCategoryMoveController controller = loader.getController();
            controller.setCostCategoryController(this);

            Stage stage = new Stage();
            stage.setTitle("Move Cost Category");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(event -> initialize());
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void onBackAction() {
        if (costBookController != null) {
            costBookController.refreshCostGroups(); // Refresh cost groups in CostBookController
        }
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    private void initChoiceBoxManager() {
        choiceBoxCostGroupManager.initialize(cgDAO.getAll());
        choiceBoxGroup.getSelectionModel().selectFirst();
        this.groupSelection = choiceBoxGroup.getSelectionModel().getSelectedItem();
    }

    private void initListViewManager() {
        try {
            if (groupSelection != null) {
                listViewCostCategoryManager.initialize(ccDAO.getAll(groupSelection.getCostGroupID()));
                listViewCategory.getSelectionModel().clearSelection();
            } else {
                log.warn("Group selection is null");
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void initChoiceBoxListener() {
        choiceBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.groupSelection = newValue;
            initListViewManager();
        });
    }

    private void initListViewListener() {
        listViewCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.categorySelection = newValue;
            try {
                if (categorySelection != null) {
                    textFieldCategoryName.setText(categorySelection.getCostCategoryName());
                } else {
                    textFieldCategoryName.clear();
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

