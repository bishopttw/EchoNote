module com.example.echonote {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires vosk;

    opens com.example.echonote to javafx.fxml;
    exports com.example.echonote;
}