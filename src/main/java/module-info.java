module com.lbycpd2.archieestimator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.apache.logging.log4j.core;
    requires static lombok;


    opens com.lbycpd2.archieestimator to javafx.fxml;
    exports com.lbycpd2.archieestimator;
}