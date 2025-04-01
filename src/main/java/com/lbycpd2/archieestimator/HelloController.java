package com.lbycpd2.archieestimator;

import com.lbycpd2.archieestimator.dao.CostBookDAO;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.util.InitializeTables;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Adding!");
        CostItem ci = new CostItem("Block", 0, "None.", "sqm", new BigDecimal("100.0"), new BigDecimal("100.0"));
        InitializeTables t = new InitializeTables();
        t.start();
        CostBookDAO dao = new CostBookDAO();
        dao.save(ci);
        dao.save(ci);
        dao.save(ci);
        dao.delete(1);
        dao.save(ci);
        dao.save(ci);
    }
}