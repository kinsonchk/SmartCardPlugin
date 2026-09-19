package me.ksmc.smartCardPlugin.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FareMediaDatabase {

    private final Connection connection;

    public FareMediaDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS SmartCards (
                    card_id VARCHAR(50) PRIMARY KEY,
                    agency_id VARCHAR(30) NOT NULL,
                    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
                    entry_record VARCHAR(50),
                    exit_record VARCHAR(50),
                    transactions TEXT
                )
            """);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Passes (
                    pass_id VARCHAR(50) PRIMARY KEY,
                    agency_id VARCHAR(30) NOT NULL,
                    pass_type INT NOT NULL,
                    trips_remaining INT NOT NULL DEFAULT -1,
                    has_entered_gate INT NOT NULL DEFAULT 0,
                    expiry BIGINT
                )
            """);
        }

    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}
