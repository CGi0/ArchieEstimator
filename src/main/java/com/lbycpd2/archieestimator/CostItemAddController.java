package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostBookDAO;
import com.lbycpd2.archieestimator.dao.CostCategoryDAO;
import com.lbycpd2.archieestimator.dao.CostGroupDAO;
import com.lbycpd2.archieestimator.model.CostCategory;
import com.lbycpd2.archieestimator.model.CostGroup;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.node.ChoiceBoxNodeManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.lbycpd2.archieestimator.util.DefaultCostCategory.DEFAULT_COST_CATEGORY;

@Slf4j
public class CostItemAddController {
    @FXML private Label labelName;
    @FXML private Label labelGroup;
    @FXML private Label labelCategory;
    @FXML private Label labelNotes;
    @FXML private Label labelUnitMeasurement;
    @FXML private Label labelMaterialUnitCost;
    @FXML private Label labelLaborUnitCost;
    @FXML private TextField textFieldName;
    @FXML private ChoiceBox<CostGroup> choiceBoxGroup;
    @FXML private ChoiceBox<CostCategory> choiceBoxCategory;
    @FXML private TextArea textAreaNotes;
    @FXML private TextField textFieldUnitMeasurement;
    @FXML private TextField textFieldMaterialUnitCost;
    @FXML private TextField textFieldLaborUnitCost;
    @FXML private Button buttonConfirm;
    @FXML private Button buttonDelete;
    @FXML private Button buttonBack;

    private ChoiceBoxNodeManager<CostGroup> choiceBoxGroupManager;
    private ChoiceBoxNodeManager<CostCategory> choiceBoxCategoryManager;

    private final CostGroupDAO cgDAO = new CostGroupDAO();
    private final CostCategoryDAO ccDAO = new CostCategoryDAO();
    private final CostBookDAO cbDAO = new CostBookDAO();

    private CostGroup groupSelection;
    private CostCategory categorySelection;

    public void initialize() {
        initializeManagers();
        choiceBoxGroup.getSelectionModel().select(groupSelection);
        choiceBoxCategory.getSelectionModel().select(categorySelection);
        initializeListeners();
    }

    private void initializeManagers() {
        choiceBoxGroupManager = new ChoiceBoxNodeManager<>(choiceBoxGroup, new CostGroup("No group found"));
        choiceBoxCategoryManager = new ChoiceBoxNodeManager<>(choiceBoxCategory, DEFAULT_COST_CATEGORY);
        initChoiceBoxGroup();
        initChoiceBoxCategory();
    }

    private void initChoiceBoxGroup(){
        choiceBoxGroupManager.initialize(cgDAO.getAll());
        choiceBoxGroup.getSelectionModel().selectFirst();
        groupSelection = choiceBoxGroup.getSelectionModel().getSelectedItem();
    }

    private void initChoiceBoxCategory(){
        choiceBoxCategoryManager.initialize(ccDAO.getAll(groupSelection.getCostGroupID()));
        choiceBoxCategory.getSelectionModel().selectFirst();
        categorySelection = choiceBoxCategory.getSelectionModel().getSelectedItem();
    }

    private void initializeListeners(){
        choiceBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.groupSelection = newValue;
            initChoiceBoxCategory();
            log.debug("Group Selected: {}", groupSelection.toString());
        });
        choiceBoxCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if (null == newValue) {
                    choiceBoxCategory.getSelectionModel().selectFirst();
                }
                this.categorySelection = newValue;
                log.debug("Category Selected: {}", categorySelection);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    public void onConfirmAction(){
        if(textFieldsAreNull()){
            return;
        }
        try {
            CostItem newCostItem = new CostItem(textFieldName.getText(),
                    categorySelection.getCostCategoryID(),
                    textAreaNotes.getText(),
                    textFieldUnitMeasurement.getText(),
                    new BigDecimal(textFieldMaterialUnitCost.getText()),
                    new BigDecimal(textFieldLaborUnitCost.getText()));
            cbDAO.save(newCostItem);
            onBackAction();
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void onBackAction(){
        if(textFieldsAreNull()){
            return;
        }
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    private boolean textFieldsAreNull(){
        if (textFieldName.getText().isEmpty()
                || textFieldUnitMeasurement.getText().isEmpty()
                || textFieldMaterialUnitCost.getText().isEmpty()
                || textFieldLaborUnitCost.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Empty fields!");
            alert.setContentText("Please ensure all text fields are not empty");
            alert.showAndWait();
            return true;
        }
        return false;
    }
}
