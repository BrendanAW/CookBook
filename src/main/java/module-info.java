module CookBookG.main {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens cookbook.ui to javafx.fxml;
    exports cookbook.ui;
}