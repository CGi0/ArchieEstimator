package com.lbycpd2.archieestimator.node;

import javafx.scene.control.ChoiceBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChoiceBoxNodeManager<T> extends AbstractNodeManager<T> {
    private final ChoiceBox<T> choiceBox;

    public ChoiceBoxNodeManager(ChoiceBox<T> choiceBox, T defaultItem) {
        super(defaultItem);
        this.choiceBox = choiceBox;
    }

    @Override
    protected void updateItems() {
        super.updateItems();
        log.debug("Updating {}", this.choiceBox.getId());
        choiceBox.setItems(itemList);
        choiceBox.getSelectionModel().selectFirst();
    }
}
