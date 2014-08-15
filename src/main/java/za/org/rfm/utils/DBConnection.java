package za.org.rfm.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/14/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {

    public Connection conn = null;

    private static Map<String, DBConnection> mapInstances = new HashMap<String, DBConnection>(25);

    private String dbURL;
    private String dbUser;
    private String dbPassword;

    private DBConnection(String dbURL, String dbUser, String dbPassword) {
        this.dbPassword = dbPassword;
        this.dbURL = dbURL;
        this.dbUser = dbUser;
    }

    public synchronized static DBConnection newInstance(String name, String dbURL, String dbUser, String dbPassword) {
        DBConnection con = new DBConnection(dbURL, dbUser, dbPassword);
        mapInstances.put(name, con);
        return con;
    }

    public synchronized static DBConnection getInstance(String name) {
        return mapInstances.get(name);
    }

    public synchronized Connection getCon() throws SQLException {
        if (conn == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return this.conn;
    }

}