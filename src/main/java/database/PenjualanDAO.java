package main.java.database;
import main.java.model.OrderItem;
import main.java.model.Penjualan;

import java.sql.*;
import java.util.List;
public class PenjualanDAO {
    public int save(Penjualan sale) throws SQLException {
        String q="INSERT INTO sales(sale_code,created_at,customer_id,cashier_id,subtotal,tax,total) VALUES(?,?,?,?,?,?,?)";
        try(Connection c=PostgreSQLConnection.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)){
                p.setString(1,sale.getSaleCode()); p.setTimestamp(2,Timestamp.valueOf(sale.getCreatedAt()));
                if(sale.getCustomer()==null)p.setNull(3,Types.INTEGER);else p.setInt(3,sale.getCustomer().getId());
                if(sale.getCashier()==null)p.setNull(4,Types.INTEGER);else p.setInt(4,sale.getCashier().getId());
                p.setDouble(5,sale.getSubtotal());p.setDouble(6,sale.getTax());p.setDouble(7,sale.getTotal());p.executeUpdate();
                try(ResultSet k=p.getGeneratedKeys()){k.next();int id=k.getInt(1);
                    try(PreparedStatement d=c.prepareStatement("INSERT INTO sale_items(sale_id,pizza_id,quantity,unit_price) VALUES(?,?,?,?)")){
                        for(OrderItem item:sale.getItems()){d.setInt(1,id);d.setInt(2,item.getPizza().getId());d.setInt(3,item.getQuantity());d.setDouble(4,item.getPizza().getFinalPrice());d.addBatch();}
                        d.executeBatch();
                    } c.commit(); return id;
                }
            } catch(SQLException e){c.rollback();throw e;}
        }
    }
    public List<String> getHistory() throws SQLException {
        List<String> rows=new java.util.ArrayList<>();
        try(Connection c=PostgreSQLConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT s.sale_code,s.created_at,s.total,COALESCE(pc.full_name,'Walk-in'),COALESCE(pk.full_name,'-') FROM sales s LEFT JOIN persons pc ON pc.id=s.customer_id LEFT JOIN persons pk ON pk.id=s.cashier_id ORDER BY s.id DESC");ResultSet r=p.executeQuery()){
            while(r.next()) rows.add(r.getString(1)+" | "+r.getTimestamp(2)+" | Rp"+String.format("%,.0f",r.getDouble(3))+" | Customer: "+r.getString(4)+" | Kasir: "+r.getString(5));
        } return rows;
    }
}
