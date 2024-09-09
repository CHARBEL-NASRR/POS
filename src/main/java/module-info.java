module com.example.projectt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;


    opens com.example.projectt to javafx.fxml;
    exports com.example.projectt;
}