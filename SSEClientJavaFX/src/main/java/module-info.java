module org.dmiit3iy {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    requires static lombok;
    requires java.prefs;
    requires javafx.graphics;
    requires okhttp.eventsource;

    opens org.dmiit3iy to javafx.fxml;
    opens org.dmiit3iy.controllers to javafx.fxml;

    exports org.dmiit3iy;
    exports org.dmiit3iy.controllers;
    exports org.dmiit3iy.model to com.fasterxml.jackson.databind;
    exports org.dmiit3iy.repositories;
    exports org.dmiit3iy.dto;


}
