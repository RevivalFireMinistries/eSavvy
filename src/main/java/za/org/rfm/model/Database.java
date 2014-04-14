package za.org.rfm.model;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:49 AM
 */
public class Database {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/churchmanager-jetty",
                    "root", "");
            return con;
        } catch (Exception ex) {
            System.out.println("Database.getConnection() Error -->" + ex.getMessage());
            return null;
        }
    }

    public static void close(Connection con) {
        try {
            con.close();
        } catch (Exception ex) {
        }
    }
}
