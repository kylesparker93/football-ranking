package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	public static Connection getNFLDatabaseConnection() {
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/Kyle/Development/sr-stat-parsing/nfl_teams.sqlite";
            // create a connection to the database
            Connection conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
	}
	
	public static Connection getCFBDatabaseConnection() {
        try {
            // db parameters
        	String url = "jdbc:sqlite:C:/Users/Kyle/Development/sr-stat-parsing/cfb_data.sqlite";
            // create a connection to the database
            Connection conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
	}
}
