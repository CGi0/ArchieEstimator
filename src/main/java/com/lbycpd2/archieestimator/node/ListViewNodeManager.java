package com.lbycpd2.archieestimator.node;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListViewNodeManager<T> extends AbstractNodeManager<T> {
    private final ListView<T> listView;
    private final TextField textField;
    private final SearchManager<T> searchManager;

    public ListViewNodeManager(ListView<T> listView, TextField textField, T defaultItem, SearchManager<T> searchManager) {
        super(defaultItem);
        this.listView = listView;
        this.textField = textField;
        this.searchManager = searchManager;
    }

    public void onSearch() {
        try {
            String searchText = textField.getText();
            itemList.setAll(searchManager.search(searchText, itemList));
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
}
