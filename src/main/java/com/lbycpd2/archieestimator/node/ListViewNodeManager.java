package com.lbycpd2.archieestimator.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ListViewNodeManager<T> extends AbstractNodeManager<T> {
    private final ListView<T> listView;
    private final TextField textField;
    private final SearchManager<T> searchManager;
    private ObservableList<T> cachedItemList;

    public ListViewNodeManager(ListView<T> listView, TextField textField, T defaultItem, SearchManager<T> searchManager) {
        super(defaultItem);
        this.listView = listView;
        this.textField = textField;
        this.searchManager = searchManager;
    }

    @Override
    public void initialize(List<T> initialItems) {
        super.initialize(initialItems);
        cachedItemList = FXCollections.observableArrayList(initialItems);
    }

    public void onSearch() {
        if (null == textField){
            UnsupportedOperationException e = new UnsupportedOperationException("No assigned Search Field");
            log.warn(e.getMessage());
            return;
        }

        try {
            String searchText = textField.getText();
            if (searchText == null || searchText.isEmpty()) {
                itemList.setAll(cachedItemList);
            } else {
                itemList.setAll(searchManager.search(searchText, cachedItemList));
            }
            updateItems();
            listView.setItems(itemList);
            listView.getSelectionModel().selectFirst();
            log.debug("Search filtered ListView");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    protected void updateItems() {
        super.updateItems();
        log.debug("Updating {}", this.listView.getId());
        listView.setItems(itemList);
        listView.getSelectionModel().selectFirst();
    }

    public T getSelected(){
        return listView.getSelectionModel().getSelectedItem();
    }
}
