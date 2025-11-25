module com.hotel.management.javafx {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // JDBC API for MySQL access
    requires java.sql;

    // Open packages that contain FXML controllers to JavaFX
    opens com.hotel.management.javafx to javafx.fxml;
    opens com.hotel.management.javafx.controller to javafx.fxml;

    // Export main package so App is visible as entry point
    exports com.hotel.management.javafx;
}
