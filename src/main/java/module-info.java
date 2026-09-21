module spotifymanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens spotifymanager to javafx.fxml;
    opens spotifymanager.controller to javafx.fxml;
    opens spotifymanager.model to javafx.base;

    exports spotifymanager;
    exports spotifymanager.model;
    exports spotifymanager.dao;
    exports spotifymanager.service;
}
