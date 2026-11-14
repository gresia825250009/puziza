package main.java.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Satu pintu koneksi JDBC ke PostgreSQL/pgAdmin. */
public final class PostgreSQLConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/puziza";
    private static final String USER = "gresia"; // ganti dengan username PostgreSQL milikmu
    private static final String PASSWORD = "Gresia12"; // ganti dengan password PostgreSQL milikmu

    private PostgreSQLConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
