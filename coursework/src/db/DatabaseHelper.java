package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Database helper for managing bank account operations.
 * Handles account number generation and account record persistence.
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:ucanaccess://data/bank.accdb";

    private DatabaseHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets a database connection.
     * 
     * @return Connection to the database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Generates a unique account number: BRANCHCODE-YYYY-xxxxx (5-digit
     * sequential).
     * Example: KLA-2026-00001
     */
    public static synchronized String generateAccountNumber(String branch, int year) throws SQLException {
        String branchCode = branch.substring(0, 3).toUpperCase(); // e.g. Kampala -> KLA
        String sql = "SELECT LastCounter FROM Counters WHERE BranchCode=? AND Year=?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branchCode);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            int counter;
            if (rs.next()) {
                counter = rs.getInt("LastCounter") + 1;
                String update = "UPDATE Counters SET LastCounter=? WHERE BranchCode=? AND Year=?";
                try (PreparedStatement upd = conn.prepareStatement(update)) {
                    upd.setInt(1, counter);
                    upd.setString(2, branchCode);
                    upd.setInt(3, year);
                    upd.executeUpdate();
                }
            } else {
                counter = 1;
                String insert = "INSERT INTO Counters (BranchCode, Year, LastCounter) VALUES (?,?,?)";
                try (PreparedStatement ins = conn.prepareStatement(insert)) {
                    ins.setString(1, branchCode);
                    ins.setInt(2, year);
                    ins.setInt(3, counter);
                    ins.executeUpdate();
                }
            }
            return String.format("%s-%04d-%05d", branchCode, year, counter);
        }
    }

    /**
     * Saves a new account record to the database.
     */
    /**
     * Saves a new account record to the database.
     * 
     * @param accNo       Account number
     * @param firstName   Customer first name
     * @param lastName    Customer last name
     * @param nin         National identification number
     * @param email       Email address
     * @param phone       Phone number
     * @param pin         Personal identification number
     * @param dob         Date of birth
     * @param accountType Type of account
     * @param branch      Branch location
     * @param deposit     Opening deposit amount
     * @throws SQLException if database operation fails
     */
    public static void saveAccount(String accNo, String firstName, String lastName, String nin,
            String email, String phone, String pin, LocalDate dob,
            String accountType, String branch, double deposit) throws SQLException {
        String sql = "INSERT INTO Accounts (AccountNumber, FirstName, LastName, NIN, Email, Phone, PIN, DOB, AccountType, Branch, OpeningDeposit, CreatedDate) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accNo);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, nin);
            stmt.setString(5, email);
            stmt.setString(6, phone);
            stmt.setString(7, pin);
            stmt.setDate(8, Date.valueOf(dob));
            stmt.setString(9, accountType);
            stmt.setString(10, branch);
            stmt.setDouble(11, deposit);
            stmt.setDate(12, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        }
    }
}