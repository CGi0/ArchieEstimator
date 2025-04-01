package com.lbycpd2.archieestimator.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class SearchManager<T> {
    public ObservableList<T> search(String searchText, List<T> itemList) {
        return FXCollections.observableArrayList(
                itemList.stream()
                        .filter(item -> item.toString().toLowerCase().contains(searchText.toLowerCase()))
                        .collect(Collectors.toList())
        );
    }
}
