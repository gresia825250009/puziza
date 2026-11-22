package main.java.database;
/*
 * Class: PostgreSQLConnection
 * Deskripsi: Membuka koneksi aplikasi ke PostgreSQL.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// JDBC menyediakan tipe koneksi, pengelola driver PostgreSQL, dan exception SQL.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Satu pintu koneksi JDBC ke PostgreSQL/pgAdmin. */
// Menyediakan koneksi JDBC terpusat ke database PostgreSQL aplikasi.
public final class PostgreSQLConnection {
    // URL menunjuk server lokal PostgreSQL, port 5432, dan database puziza.
    private static final String URL = "jdbc:postgresql://localhost:5432/Puziza";
    // Kredensial database yang digunakan oleh driver JDBC.
    private static final String USER = "neta"; 
    private static final String PASSWORD = "B@543garneta"; 

    // Constructor privat karena kelas ini hanya menyediakan method statis.
    private PostgreSQLConnection() {
    }

    // Membuka koneksi baru; pemanggil wajib menutupnya setelah operasi selesai.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}