package UITest1.UITest1;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBDoctor: Resolves DB path and ensures required columns exist.
 */
public class DBDoctor {

    public static String getDbUrl() {
        File f1 = new File("DataBase1.db");
        if (f1.exists()) return "jdbc:sqlite:" + f1.getAbsolutePath();

        File f2 = new File("target/DataBase1.db");
        if (f2.exists()) return "jdbc:sqlite:" + f2.getAbsolutePath();

        File f3 = new File("../DataBase1.db");
        if (f3.exists()) return "jdbc:sqlite:" + f3.getAbsolutePath();

        System.err.println("[DBDoctor] WARNING: DataBase1.db not found. Using working dir fallback.");
        return "jdbc:sqlite:DataBase1.db";
    }

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(getDbUrl());
        ensureColumns(conn); 
        return conn;
    }

    /**
     * Ensures optional columns exist without breaking existing data.
     * SQLite ignores "duplicate column" errors so we catch them silently.
     */
    private static void ensureColumns(Connection conn) {
        String[] migrations = {
            
            "ALTER TABLE review ADD COLUMN helpful_count   INTEGER DEFAULT 0",
            "ALTER TABLE review ADD COLUMN unhelpful_count INTEGER DEFAULT 0"
        };
        for (String sql : migrations) {
            try {
                conn.createStatement().execute(sql);
            } catch (SQLException e) {
                
                if (!e.getMessage().toLowerCase().contains("duplicate column")) {
                    System.err.println("[DBDoctor] Migration note: " + e.getMessage());
                }
            }
        }
    }
}
