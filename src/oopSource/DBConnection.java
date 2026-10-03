package oopSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/";

    private static final String DATABASE = "enrollDB";

    private static final String USER = "root";

    private static final String PASSWORD = "SQLang@246"; // ur pass here


    public static Connection getConnection() {

        Connection conn = null;

        try {

            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("JDBC registered.");


            //connect to sqlServ
            conn = DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
            );

            System.out.println("Connected to MySQL server.");

            //create db if not existing
            Statement stmt = conn.createStatement();

            String createDatabase =
                "CREATE DATABASE IF NOT EXISTS " + DATABASE;

            stmt.executeUpdate(createDatabase);

            System.out.println(
                "Database " + DATABASE + " is ready."
            );

            stmt.close();


            //close server connnect
            conn.close();


            //connect to enrollDB
            conn = DriverManager.getConnection(
                URL + DATABASE,
                USER,
                PASSWORD
            );

            System.out.println(
                "Connected to " + DATABASE
            );

            //create all necessaryTables
            Statement tableStmt = conn.createStatement();

            
          
			
            String createTable = "CREATE TABLE IF NOT EXISTS userCreds("
            		+ "userID int(3) auto_increment not null primary key,"
					+ "userName varchar(50) unique not null,"
					+ "password varchar(64) not null,"
					+ "role VARCHAR(20) not null"
					+ ");";
			tableStmt.executeUpdate(createTable);
            
			String sql = "INSERT INTO userCreds(userName, password, role) "
			           + "VALUES (?, ?, ?) "
			           + "ON DUPLICATE KEY UPDATE userName = userName";
				
				PreparedStatement pstate = conn.prepareStatement(sql);
			
				
				
					
				 	pstate.setString(1, "root");
				    pstate.setString(2, "$2a$10$oUEqQQozPH3wk/gqsHqtLe6LFFmpMwqY2ZeTLDGLDsdxs1udYEdiS");
				    pstate.setString(3, "admin");
				
				    
				    pstate.executeUpdate();

	            

	            
            String createTableCourses = "CREATE TABLE IF NOT EXISTS courses ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "course_code VARCHAR(50) NOT NULL UNIQUE, "
                    + "course_name VARCHAR(150) NOT NULL UNIQUE, "
                    + "description TEXT"
                    + ");";

            tableStmt.executeUpdate(createTableCourses);
            
 
            
            String createTableStudents = "CREATE TABLE IF NOT EXISTS students ("
                    + "student_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_number varchar(12)NOT NULL default '',"
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "first_name VARCHAR(100) NOT NULL, "
                    + "last_name VARCHAR(100) NOT NULL, "
                    + "personal_email VARCHAR(150) NOT NULL UNIQUE, "
                    + "univ_email VARCHAR (150) not null unique, "
                    + "course VARCHAR(100) NOT NULL, "
                    + "year_level VARCHAR(50) NOT NULL, "
                    + "status VARCHAR(50) NOT NULL, "
                    + "FOREIGN KEY (course) REFERENCES courses(course_name) "
                    + "ON DELETE RESTRICT ON UPDATE CASCADE"
                 
                    + ");";

            tableStmt.executeUpdate(createTableStudents);
            
            
            
            tableStmt.close();


        } catch (ClassNotFoundException e) {

            JOptionPane.showMessageDialog(
                null,
                "MySQL Driver not found: " + e.getMessage(),
                "Driver Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Database Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }


        return conn;
    }
    
    public static void main(String[] args) {
    	Connection conn = DBConnection.getConnection();
    	
    }
}