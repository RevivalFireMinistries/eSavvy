package za.org.rfm;

import za.org.rfm.utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/14/14
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataPump {

     public void runMemberPump(){
         Statement statement = null;
         try {
             DBConnection churchManagerOld = DBConnection.newInstance("doy08_churchmanager","jdbc:mysql://localhost:3306/doy08_churchmanager","root","");
             DBConnection churchManagerNew = DBConnection.newInstance("esavvy_dump", "jdbc:mysql://localhost:3306/esavvy_dump","root","");
             Connection connection = DBConnection.getInstance("doy08_churchmanager").getCon();
             Connection connection2 = DBConnection.getInstance("esavvy_dump").getCon();
             if(connection != null){

                 System.out.println(" we got a connaction to doy_08!!!");
                statement = connection.createStatement();
                 String sql;
                 sql = "Select * from rfm_member";
                 ResultSet rs = statement.executeQuery(sql);
                 while(rs.next()){//Retrieve by column name

                     String firstName = rs.getString("firstname");
                     String lastName = rs.getString("lastname");
                     String gender = rs.getString("gender");
                     String phone = rs.getString("phone");
                     String email = rs.getString("email");

                     //Display values
                     System.out.print(", FirstName: " + firstName);
                     System.out.println(", LastName: " + lastName);
                 }
                 //STEP 6: Clean-up environment
                 rs.close();
                 statement.close();
                 connection.close();
             } else{
                 System.out.println("...can't connect to doy_08");
             }
             if(connection2 != null){
                 System.out.println(" we got a connaction to esavvy!!!");
             } else{
                 System.out.println("...can't connect to esavvy");
             }
             System.out.println();
         } catch (SQLException e) {
             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
     }

    public static void main(String[] args) {
        new DataPump().runMemberPump();
    }
}
