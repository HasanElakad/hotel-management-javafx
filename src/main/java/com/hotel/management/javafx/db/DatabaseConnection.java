package com.hotel.management.javafx.db;

import com.hotel.management.javafx.App;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }

        Properties props = new Properties();
        try (InputStream in = App.class.getResourceAsStream("database.properties")) {
            if (in == null) {
                throw new IOException("database.properties not found");
            }
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String host = props.getProperty("db.host");
        String port = props.getProperty("db.port");
        String dbName = props.getProperty("db.name");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                   + "?useSSL=true&requireSSL=true&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
