package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.cell.*;
import com.lbycpd2.archieestimator.jackson.JsonLoadReader;
import com.lbycpd2.archieestimator.jasper.EstimatorInvoiceAdapter;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.service.CostTableControllerService;
import com.lbycpd2.archieestimator.service.CurrencyFormatService;
import com.lbycpd2.archieestimator.service.DocumentSettingsService;
import com.lbycpd2.archieestimator.table.TabTable;
import com.lbycpd2.archieestimator.jackson.JsonSaveWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class CostTableController {

    @FXML private TreeTableView<SummaryRow> treeTableViewSummary;
    @FXML private TabPane tabPane;
    @FXML private TextField textFieldSubtotalCost;
    @FXML private TreeTableColumn<SummaryRow, String> colTableItem;
    @FXML private TreeTableColumn<SummaryRow, BigDecimal> colSubtotalCost;

    private ContextMenu contextMenu;
    private final ObservableList<TabTable> tabTablesList = FXCollections.observableArrayList();

    private Tab tabSelection;

    private boolean fileIsLoaded = false;
    private File loadFile;

    public void initialize() {
        CostTableControllerService.getInstance().setCostTableController(this);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tabSelection = newValue;
            if (tabPane.getTabs().getFirst() == tabSelection) {
                updateTreeTableViewSummary();
            }
            updateSubtotal();
        });

        addNewTab("Default Tab");
        TreeItem<SummaryRow> rootTreeItem = new TreeItem<>(new SummaryRow());
        TreeItem<SummaryRow> summaryRowTreeItem = new TreeItem<>(new SummaryRow(tabTablesList.getFirst()));

        treeTableViewSummary.setRoot(rootTreeItem);
        treeTableViewSummary.setShowRoot(false);
        treeTableViewSummary.getRoot().getChildren().add(summaryRowTreeItem);

        colSubtotalCost.setCellFactory(param -> new FinancialTreeTableCell<>());

        colTableItem.setCellValueFactory(param -> param.getValue().getValue().rowNameProperty());
        colSubtotalCost.setCellValueFactory(param -> param.getValue().getValue().rowTotalCostProperty());
    }

    private void updateTreeTableViewSummary(){
        treeTableViewSummary.getRoot().getChildren().clear();
        for (TabTable tabTable : tabTablesList) {
            TreeItem<SummaryRow> summaryRowTreeItem = new TreeItem<>(new SummaryRow(tabTable));
            treeTableViewSummary.getRoot().getChildren().add(summaryRowTreeItem);
        }
    }

    public void updateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        if (tabSelection == tabPane.getTabs().getFirst()){
            for (TreeItem<SummaryRow> item : treeTableViewSummary.getRoot().getChildren()) {
                subtotal = subtotal.add(item.getValue().getRowTotalCost());
            }
        } else {
            for (TabTable tab : tabTablesList) {
                if (tab.getTab() == tabPane.getSelectionModel().getSelectedItem()) {
                    for (TreeItem<CostRow> item : tab.getTreeTableView().getRoot().getChildren()) {
                        subtotal = subtotal.add(item.getValue().getCostItemTotalCost());
                    }
                }
            }
        }
        textFieldSubtotalCost.setText(CurrencyFormatService.getInstance().format(subtotal));
    }

    public void onNewFileAction(){
        if (fileIsLoaded){
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation Dialog");
            confirmDialog.setHeaderText("New File");
            confirmDialog.setContentText("Are you sure you want to create a new File?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.NO) {
                log.info("User chose Cancel or closed the dialog");
                return;
            }
        }

        tabTablesList.clear();
        tabPane.getTabs().subList(1, tabPane.getTabs().size()).clear();
        addNewTab("Default Tab");
        fileIsLoaded = false;
        loadFile = null;
        updateWindowTitle(null);
    }

    public void onSaveFileAction(){
        try{
            if(!fileIsLoaded){
                File loadFile = getFilePath("SAVE");
                if(null == loadFile){
                    return;
                } else {
                    this.loadFile = loadFile;
                }
            }

            JsonSaveWriter jsonWriter = new JsonSaveWriter();
            jsonWriter.writeTabsToJson(tabTablesList,loadFile.getAbsolutePath());
            log.info(tabTablesList.toString());
            fileIsLoaded = true;
            updateWindowTitle(loadFile.getName());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save File");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Saved %s Successfully", loadFile.getName()));
            alert.showAndWait();
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void onSaveFileAsAction(){
        try{
            File loadFile = getFilePath("SAVE");
            if(null == loadFile){
                return;
            } else {
                this.loadFile = loadFile;
            }
            JsonSaveWriter jsonWriter = new JsonSaveWriter();
            jsonWriter.writeTabsToJson(tabTablesList,loadFile.getAbsolutePath());
            log.info(tabTablesList.toString());
            fileIsLoaded = true;
            updateWindowTitle(loadFile.getName());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save File");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Saved %s Successfully", loadFile.getName()));
            alert.showAndWait();
        } catch (Exception e){
            log.error(e.getMessage());
        }

    }

    private File getFilePath(String mode){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON File", "*.json"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Documents"));
        if (mode.equalsIgnoreCase("SAVE")) {
            fileChooser.setInitialFileName("estimate.json");
            fileChooser.setTitle("Save File");
            return fileChooser.showSaveDialog(new Stage());
        } else if (mode.equalsIgnoreCase("LOAD")){
            fileChooser.setTitle("Load File");
            return fileChooser.showOpenDialog(new Stage());
        }
        return null;
    }

    public void onLoadFileAction(){
        try{
            JsonLoadReader jsonLoadReader = new JsonLoadReader();

            File loadFile = getFilePath("LOAD");
            if(null == loadFile){
                return;
            } else {
                this.loadFile = loadFile;
            }

            tabTablesList.clear();
            tabPane.getTabs().subList(1, tabPane.getTabs().size()).clear();

            ObservableList<TabTable> readList = FXCollections.observableArrayList();
            readList.addAll(jsonLoadReader.readJson(loadFile.getAbsolutePath()));
            for (TabTable tabTable : readList) {
                addNewTab(tabTable);
            }
            fileIsLoaded = true;
            updateWindowTitle(loadFile.getName());
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Loading File");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateWindowTitle(String fileName){
        Stage stage = (Stage) tabPane.getScene().getWindow();
        if(null == fileName || fileName.isEmpty()) {
            stage.setTitle("Archie Estimator");
        } else {
            stage.setTitle(String.format("Archie Estimator - %s", fileName));
        }
    }

    public void onPreviewDocumentAction(){
        try {
            JasperPrint jasperPrint = generateJasperReport();
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        } catch (JRException | IOException e){
            log.error(e.getMessage());
            log.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error previewing document");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            //e.printStackTrace();
        }
    }

    public void onExportDocumentAction(){
        try {
            // Step 0: Select FilePath
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File", "*.pdf"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Documents"));

            if(null == loadFile){

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Save File Before Exporting!");
                alert.setContentText("Please save the project file (.json) before exporting as PDF");
                alert.showAndWait();

                onSaveFileAsAction();
                return;
            }

            fileChooser.setInitialFileName(String.format("%s.pdf", getFileNameWithoutExtension(loadFile.getName())));

            fileChooser.setTitle("Save File");
            File exportFile = fileChooser.showSaveDialog(new Stage());

            // Step 1->3: Fill the Report
            JasperPrint jasperPrint = generateJasperReport();

            // Step 4: Export to PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, exportFile.getAbsolutePath());
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);

            log.info("PDF generated successfully!");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Document Export");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Exported %s Successfully", exportFile.getName()));
            alert.showAndWait();
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error exporting document");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private static String getFileNameWithoutExtension(String fileName) {
        return (fileName == null || !fileName.contains(".")) ? fileName : fileName.substring(0, fileName.lastIndexOf("."));
    }


    public void onDocumentSettingsAction(){
        try {
            log.info("onDocumentSettingsAction: Trying to open DocumentSettings");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("document-settings.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Document Settings");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
            //e.printStackTrace();
        }
    }

    private JasperPrint generateJasperReport() throws JRException, IOException {
        // Step 1: Load and Compile the JRXML file
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResource("DefaultInvoice.jrxml").openStream());

        // Step 2: Prepare the Data
        EstimatorInvoiceAdapter dataSource = new EstimatorInvoiceAdapter(tabTablesList);
        Map<String, Object> parametersInstance = new HashMap<>(DocumentSettingsService.getInstance().getParameters());

        return JasperFillManager.fillReport(jasperReport, parametersInstance, dataSource);
    }

    public void onAboutAction(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Archie Estimator");
        alert.setHeaderText("Archie Estimator");
        alert.setContentText("""
                Contact Information:
                    Arroyo, Lawrence Jame - lawrence_arroyo@dlsu.edu.ph
                    Binalla, Rojan Moirae - rojan_binalla@dlsu.edu.ph
                    Napao, Clarence Gio - clarence_napao@dlsu.edu.ph""");
        alert.showAndWait();

    }

    public void onAddTabAction(){
        TextInputDialog dialog = new TextInputDialog("New Tab");
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter tab name:");
        dialog.setContentText("Input:");
        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            addNewTab(result.get());
        } else {
            log.info("No input provided or dialog was canceled.");
        }
    }

    private void addNewTab(String tabName) {
        TabTable tabTable = new TabTable(tabName, this);
        tabTablesList.add(tabTable);
        tabPane.getTabs().add(tabTable.getTab());
        tabPane.getSelectionModel().select(tabTable.getTab());
    }

    private void addNewTab(TabTable tabTable) {
        tabTablesList.add(tabTable);
        tabPane.getTabs().add(tabTable.getTab());
        tabPane.getSelectionModel().select(tabTable.getTab());
    }

    public void onRenameTabAction(){
        renameTab();
    }

    private void renameTab(){
        try {
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    TextInputDialog dialog = new TextInputDialog(tab.getTab().getText());
                    dialog.setTitle("Input Dialog");
                    dialog.setHeaderText("Rename tab:");
                    dialog.setContentText("Input:");
                    // Show the dialog and capture the result
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent() && !result.get().trim().isEmpty()) {
                        tab.getTab().setText(result.get());
                    } else {
                        log.info("No input provided or dialog was canceled.");
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onRemoveTabAction(){
        removeTab();
    }

    private void removeTab(){
        try {
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDialog.setTitle("Confirmation Dialog");
                    confirmDialog.setHeaderText("Delete Tab?");
                    confirmDialog.setContentText(String.format("Are you sure you want to remove %s\nand all its associated items?", tab.getTab().getText()));

                    Optional<ButtonType> result = confirmDialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            tabTablesList.removeIf(tabTable -> tabTable.getTab() == tabSelection);
                            tabPane.getTabs().remove(tabSelection);
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else {
                        log.info("User chose Cancel or closed the dialog");
                    }
                    return;
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostBookAction(){
        try {
            log.info("onCostBookAction: Trying to open CostBook");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-book.fxml"));
            Parent root = loader.load();
            CostBookController controller = loader.getController();
            controller.setCostTableController(this);

            Stage stage = new Stage();
            stage.setTitle("Cost Book");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostItemAddAction(){
        try {
            log.info("onCostItemAction: Trying to open CostBook");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-book-selector.fxml"));
            Parent root = loader.load();
            CostBookSelectorController controller = loader.getController();
            controller.setCostTableController(this);

            Stage stage = new Stage();
            stage.setTitle("Cost Book");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(event -> log.info("Closing CostBookSelector"));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public void addCostRow(CostItem costItem) {
        try {
            log.info("Trying to add CostRow");
            TreeItem<CostRow> item = null;
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    item = new TreeItem<>(new CostRow(costItem));
                    //tab.getCostRowList().add(item);
                    tab.getTreeTableView().getRoot().getChildren().add(item);
                }
            }
            assert item != null;
            log.info("Added CostRow: {}", item.getValue().getCostItemName());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void onCostBookAddCostItemAction(){
        try {
            log.info("Trying to open Costbook Add");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cost-item-add.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Cost Item Add");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private void removeCostRow(TreeItem<CostRow> costRowSelection, TabTable tab){
        try{
            log.info("Trying to remove CostRow");
            if (null == tab.getTreeTableView().getSelectionModel().getSelectedItem()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("No Cost Row Item Selected");
                alert.setContentText("No cost row item selected or cost row item ID is null");
                alert.showAndWait();
                return;
            }
            tab.getTreeTableView().getRoot().getChildren().remove(costRowSelection);
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public void onRemoveRowAction(){
        for (TabTable tab : tabTablesList) {
            if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                removeCostRow(tab.getTreeTableView().getSelectionModel().getSelectedItem(), tab);
            }
        }
    }

    public void onCellContextMenuRequested(ContextMenuEvent event) {
        TreeTableRow<CostRow> row = (TreeTableRow<CostRow>) event.getSource();

        if (row != null && !row.isEmpty()) {
            if (contextMenu != null && contextMenu.isShowing()) {
                contextMenu.hide();
            }

            contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Remove Cost Item");

            removeItem.setOnAction(e -> onRemoveRowAction());

            contextMenu.getItems().add(removeItem);
            contextMenu.show(row, event.getScreenX(), event.getScreenY());

            // Hide the context menu when clicking outside
            for (TabTable tab : tabTablesList) {
                if(tab.getTab() == tabPane.getSelectionModel().getSelectedItem()){
                    tab.getTreeTableView().setOnMouseClicked(e -> {
                        if (e.getButton() != MouseButton.SECONDARY && contextMenu != null && contextMenu.isShowing()) {
                            contextMenu.hide();
                        }
                    });
                }
            }

        }
    }

}

