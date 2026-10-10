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
                    card_id INTEGER PRIMARY KEY,  -- each entry will automatically be assigned a unique INTEGER card_id
                    agency_id VARCHAR(30) NOT NULL,
                    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
                    expiry_from_last_use VARCHAR(20),
                    entry_record VARCHAR(60),
                    exit_record VARCHAR(60),
                    transactions TEXT
                )
            """);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Passes (
                    pass_id INTEGER PRIMARY KEY,  -- each entry will automatically be assigned a unique INTEGER pass_id
                    agency_id VARCHAR(30) NOT NULL,
                    pass_type INT NOT NULL,
                    trips_remaining INT NOT NULL DEFAULT -1,
                    has_entered_gate INT NOT NULL DEFAULT 0,  -- 0 for false, 1 for true
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

    public static FareMediaDatabase getFareMediaDatabase() {
        return fareMediaDatabase;
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


    // Database Operations - Lookup table values
    public boolean doesAgencyExist(String agencyID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT agency_id FROM CompanyRevenue WHERE agency_id = ?")) {
            preparedStatement.setString(1, agencyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }


    // Database Operations - Modify table values
    // Smartcards
    public boolean createSmartCard(String agencyID, String expiryFromLastUse) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO SmartCards (agency_id, expiry_from_last_use) VALUES (?, ?)")) {
            preparedStatement.setString(1, agencyID);
            preparedStatement.setString(2, expiryFromLastUse);
            int executeResult = preparedStatement.executeUpdate();  // executeResult will be 1 if the SQL database operation was successful

            return executeResult > 0;  // then the function will return true
        }
    }

    public boolean setCardBalance(String cardID, double moneyAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET balance = ? WHERE card_id = ?")) {
            preparedStatement.setDouble(1, moneyAmount);
            preparedStatement.setString(2, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

//    public void createEntryRecord(String cardID, String stationCode) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET entry_record = ? WHERE card_id = ?")) {
//            preparedStatement.setString(1, agencyID);
//            preparedStatement.setString(2, agencyID);
//            preparedStatement.executeUpdate();
//        }
//    }

    public boolean clearEntryExitRecords(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET entry_record = NULL, exit_record = NULL WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteSmartCard(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    // Passes
    public void createPass(String agencyID, String passType) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Passes (agency_id, pass_type) VALUES (?, ?)")) {
            preparedStatement.setString(1, agencyID);
            preparedStatement.setString(2, passType);
            preparedStatement.executeUpdate();
        }
    }

    public boolean deletePass(String passID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Passes WHERE pass_id = ?")) {
            preparedStatement.setString(1, passID);

            return preparedStatement.executeUpdate() > 0;
        }
    }



}
