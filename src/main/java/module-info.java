module org.igirerwanda.igirepaywallet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;


    opens org.igirerwanda.igirepaywallet to javafx.fxml;
    opens org.igirerwanda.igirepaywallet.lab1 to javafx.fxml;
    opens org.igirerwanda.igirepaywallet.lab2 to javafx.fxml;
    opens org.igirerwanda.igirepaywallet.lab3.controllers to javafx.fxml;
    opens org.igirerwanda.igirepaywallet.lab3.services to javafx.fxml;
    
    exports org.igirerwanda.igirepaywallet;
    exports org.igirerwanda.igirepaywallet.lab1;
    exports org.igirerwanda.igirepaywallet.lab2;
    exports org.igirerwanda.igirepaywallet.lab3.controllers;
    exports org.igirerwanda.igirepaywallet.lab3.services;
}