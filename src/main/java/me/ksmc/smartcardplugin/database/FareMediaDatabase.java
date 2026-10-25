package me.ksmc.smartcardplugin.database;

import me.ksmc.smartcardplugin.Main;
import org.bukkit.Bukkit;

import java.sql.*;
import java.time.Instant;
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

        // TODO
        // synchronize the list of agencies defined in config.yml with the CompanyRevenue table in the database
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


    // Database Operations
    public boolean agencyExists(String agencyID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT agency_id FROM CompanyRevenue WHERE agency_id = ?")) {
            preparedStatement.setString(1, agencyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    public Object getAgencyRevenue(String agencyID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT revenue FROM CompanyRevenue WHERE agency_id = ?")) {
            preparedStatement.setString(1, agencyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("revenue");
            }
        }
        return null;
    }

    public boolean setAgencyRevenue(String agencyID, double newMoneyAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE CompanyRevenue SET revenue = ? WHERE agency_id = ?")) {
            preparedStatement.setDouble(1, newMoneyAmount);
            preparedStatement.setString(2, agencyID);

            return preparedStatement.executeUpdate() > 0;
        }
    }


    public Object createSmartCard(String agencyID, String expiryFromLastUse) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO SmartCards (agency_id, expiry_from_last_use) VALUES (?, ?)")) {
            preparedStatement.setString(1, agencyID);
            preparedStatement.setString(2, expiryFromLastUse);

            if (preparedStatement.executeUpdate() > 0) {  // will be true if the SQL database operation was successful
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getString(1);  // returns the generated cardID
                }
            }

            return null;
        }
    }

    public Object getCardBalance(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        }
        return null;
    }

    public boolean setCardBalance(String cardID, double newMoneyAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET balance = ? WHERE card_id = ?")) {
            preparedStatement.setDouble(1, newMoneyAmount);
            preparedStatement.setString(2, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean hasEntryRecord(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT entry_record FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("entry_record") != null;
            }
        }
        return false;
    }

    public String getEntryRecord(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT entry_record FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("entry_record");
            }
        }
        return null;
    }

    public boolean createEntryRecord(String cardID, String agencyID, String stationCode) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET entry_record = ? WHERE card_id = ?")) {
            preparedStatement.setString(1, agencyID + ":" + stationCode + ":" + Instant.now().toString());
            preparedStatement.setString(2, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean hasExitRecord(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT exit_record FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("exit_record") != null;
            }
        }
        return false;
    }

    public String getExitRecord(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT exit_record FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("exit_record");
            }
        }
        return null;
    }

    public boolean createExitRecord(String cardID, String agencyID, String stationCode) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET exit_record = ? WHERE card_id = ?")) {
            preparedStatement.setString(1, agencyID + ":" + stationCode + ":" + Instant.now().toString());
            preparedStatement.setString(2, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean clearEntryExitRecords(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE SmartCards SET entry_record = NULL, exit_record = NULL WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }

//    public boolean createTransactionRecord()  throws SQLException {
//
//    }

    public boolean deleteSmartCard(String cardID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM SmartCards WHERE card_id = ?")) {
            preparedStatement.setString(1, cardID);

            return preparedStatement.executeUpdate() > 0;
        }
    }


    public Object createPass(String agencyID, String passType, int tripsRemaining, String expiry) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Passes (agency_id, pass_type, trips_remaining, expiry) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, agencyID);
            preparedStatement.setString(2, passType);
            preparedStatement.setInt(3, tripsRemaining);
            preparedStatement.setString(4, expiry);

            if (preparedStatement.executeUpdate() > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getString(1);  // returns the generated passID
                }
            }

            return null;
        }
    }

    public boolean deletePass(String passID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Passes WHERE pass_id = ?")) {
            preparedStatement.setString(1, passID);

            return preparedStatement.executeUpdate() > 0;
        }
    }


}
