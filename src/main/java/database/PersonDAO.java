package main.java.database;
import main.java.model.Cashier;
import main.java.model.Customer;

import java.sql.*;
import java.util.*;
public class PersonDAO {
    public List<Customer> getCustomers() throws SQLException {
        List<Customer> out=new ArrayList<>();
        String q="SELECT p.id,p.full_name,p.phone,p.registered_at,c.email FROM persons p JOIN customers c ON c.person_id=p.id";
        try(Connection c=PostgreSQLConnection.getConnection(); PreparedStatement ps=c.prepareStatement(q); ResultSet r=ps.executeQuery()){
            while(r.next()) out.add(new Customer(r.getInt(1),r.getString(2),r.getString(3),r.getDate(4).toLocalDate(),r.getString(5)));
        } return out;
    }
    public void addCustomer(Customer x) throws SQLException {
        try(Connection c=PostgreSQLConnection.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("INSERT INTO persons(full_name,phone,registered_at,person_type) VALUES(?,?,?, 'CUSTOMER')",Statement.RETURN_GENERATED_KEYS)){
                p.setString(1,x.getName()); p.setString(2,x.getPhone()); p.setDate(3,java.sql.Date.valueOf(x.getRegisteredAt())); p.executeUpdate();
                try(ResultSet k=p.getGeneratedKeys()){ k.next(); try(PreparedStatement d=c.prepareStatement("INSERT INTO customers(person_id,email) VALUES(?,?)")){ d.setInt(1,k.getInt(1)); d.setString(2,x.getEmail()); d.executeUpdate(); }}
            } c.commit();
        }
    }
    public void deletePerson(int id) throws SQLException {
        try(Connection c=PostgreSQLConnection.getConnection(); PreparedStatement p=c.prepareStatement("DELETE FROM persons WHERE id=?")){p.setInt(1,id);p.executeUpdate();}
    }

    // ================= KASIR =================
    private static String hash(String pw) {
        try {
            byte[] d = java.security.MessageDigest.getInstance("SHA-256").digest(pw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    private static Cashier cashierFrom(ResultSet r) throws SQLException {
        return new Cashier(r.getInt(1), r.getString(2), r.getString(3), r.getDate(4).toLocalDate(), r.getString(5), null);
    }

    private static final String CASHIER_SQL =
        "SELECT p.id,p.full_name,p.phone,p.registered_at,k.username FROM persons p JOIN cashiers k ON k.person_id=p.id";

    public List<Cashier> getCashiers() throws SQLException {
        List<Cashier> out = new ArrayList<>();
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(CASHIER_SQL + " ORDER BY p.id"); ResultSet r = ps.executeQuery()) {
            while (r.next()) out.add(cashierFrom(r));
        }
        return out;
    }

    /** Mengembalikan kasir kalau username + password cocok, selain itu null. */
    public Cashier authenticate(String username, String password) throws SQLException {
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(CASHIER_SQL + " WHERE k.username=? AND k.password_hash=?")) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet r = ps.executeQuery()) { return r.next() ? cashierFrom(r) : null; }
        }
    }

    /** Membuat 2 kasir bawaan kalau belum ada. Aman dipanggil berulang. */
    public static void seedCashiers() throws SQLException {
        String[][] data = {{"Neta", "neta", "neta123"}, {"Gresia", "gresia", "gresia123"}};
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                for (String[] k : data) {
                    try (PreparedStatement chk = c.prepareStatement("SELECT 1 FROM cashiers WHERE username=?")) {
                        chk.setString(1, k[1]);
                        try (ResultSet r = chk.executeQuery()) { if (r.next()) continue; }
                    }
                    try (PreparedStatement p = c.prepareStatement("INSERT INTO persons(full_name,person_type) VALUES(?, 'CASHIER')", Statement.RETURN_GENERATED_KEYS)) {
                        p.setString(1, k[0]);
                        p.executeUpdate();
                        try (ResultSet key = p.getGeneratedKeys()) {
                            key.next();
                            try (PreparedStatement d = c.prepareStatement("INSERT INTO cashiers(person_id,username,password_hash) VALUES(?,?,?)")) {
                                d.setInt(1, key.getInt(1)); d.setString(2, k[1]); d.setString(3, hash(k[2])); d.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    /** Cari customer berdasarkan nama (tanpa peduli huruf besar/kecil); kalau belum ada dibuat baru. */
    public Customer findOrCreateCustomer(String name) throws SQLException {
        String q = "SELECT p.id,p.full_name,p.phone,p.registered_at,c.email FROM persons p JOIN customers c ON c.person_id=p.id WHERE LOWER(p.full_name)=LOWER(?) ORDER BY p.id LIMIT 1";
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return new Customer(r.getInt(1), r.getString(2), r.getString(3), r.getDate(4).toLocalDate(), r.getString(5));
            }
        }
        Customer baru = new Customer(0, name, null, java.time.LocalDate.now(), null);
        addCustomer(baru);
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet r = ps.executeQuery()) {
                r.next();
                return new Customer(r.getInt(1), r.getString(2), r.getString(3), r.getDate(4).toLocalDate(), r.getString(5));
            }
        }
    }
}
