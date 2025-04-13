module com.lbycpd2.archieestimator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires static lombok;
    requires java.naming;
    requires jdk.compiler;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires jasperreports;
    requires java.desktop;


    opens com.lbycpd2.archieestimator to javafx.fxml;
    exports com.lbycpd2.archieestimator;
    exports com.lbycpd2.archieestimator.util;
}