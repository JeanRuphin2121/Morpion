module aka.salako {
    requires javafx.controls;
    requires javafx.fxml;


    opens aka.salako to javafx.fxml;
    exports aka.salako;
}