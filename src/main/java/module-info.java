module com.example.echonote {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    requires static jdk.jsobject;
    requires okhttp3;
    requires org.json;

    opens com.example.echonote to javafx.fxml;
    exports com.example.echonote;
}