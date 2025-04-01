package com.lbycpd2.archieestimator.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Getter
public abstract class AbstractNodeManager<T> {
    protected ObservableList<T> itemList;
    protected T defaultItem;

    public AbstractNodeManager(T defaultItem) {
        this.defaultItem = defaultItem;
    }

    public void initialize(List<T> initialItems) {
        itemList = FXCollections.observableArrayList(initialItems);
        updateItems();
    }

    protected void updateItems() {
        if (itemList.isEmpty()) {
            itemList.add(defaultItem);
            log.debug("Node is empty");
        } else {
            itemList.sort(Comparator.comparing(Object::toString));
        }
    }
}
