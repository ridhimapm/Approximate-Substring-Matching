/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsing;

/**
 *
 * @author ahmadrasul
 */

import java.sql.*;

public class DBConn {
    
    // database information
    private static String url = "jdbc:mysql://localhost:3306/dblp";
    private static String user = "root";
    private static String password = "Seminole-19";

    public static Connection getConn() {
        
	try {
            // establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
	} 
        
        catch (Exception e) {
            System.out.println("Error while opening a conneciton to database server: " 
                    + e.getMessage());
            return null;
	}
    }
}
