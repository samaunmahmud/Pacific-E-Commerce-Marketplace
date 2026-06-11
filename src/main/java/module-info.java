module UITest1.UITest1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens UITest1.UITest1 to javafx.fxml;
    exports UITest1.UITest1;
}