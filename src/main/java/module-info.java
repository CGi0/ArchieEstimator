module com.lbycpd2.archieestimator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lbycpd2.archieestimator to javafx.fxml;
    exports com.lbycpd2.archieestimator;
}