package me.ksmc.smartcardplugin.database;

import me.ksmc.smartcardplugin.Main;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.logging.Level;

public class FareMediaDatabase {
    private static FareMediaDatabase fareMediaDatabase;
    private final Connection connection;

    public FareMediaDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS SmartCards (
                    card_id VARCHAR(50) PRIMARY KEY,
                    agency_id VARCHAR(30) NOT NULL,
                    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
                    expiry_from_lastuse VARCHAR(20),
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

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS CompanyRevenue (
                    agency_id VARCHAR(30) PRIMARY KEY,
                    revenue DECIMAL(10, 2) NOT NULL DEFAULT 0.0
                )
            """);
        }
    }

    public FareMediaDatabase getFareMediaDatabase() {
        return this;
    }

    public static void initializeDatabase() {
        // Creates the database .db file (if not already created yet) and connect to it
        try {
            fareMediaDatabase = new FareMediaDatabase(Main.getPlugin().getDataFolder().getAbsolutePath() + "/fare_media.db");
        } catch (SQLException e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().log(Level.SEVERE, "Failed to load the database! " + e.getMessage());

            // Disable the plugin if couldn't connect to the database
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    public static void disableDatabase() {
        // Close database connection
        if (fareMediaDatabase != null) {
            try {
                fareMediaDatabase.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    // Database Operations
    public void createSmartCard(String agencyID, String expiryFromLastUse) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO SmartCards (card_id, expiry_from_lastuse) VALUES (?, ?)")) {
            preparedStatement.setString(1, agencyID);
            preparedStatement.setString(2, expiryFromLastUse);
            preparedStatement.executeUpdate();
        }
    }

}
