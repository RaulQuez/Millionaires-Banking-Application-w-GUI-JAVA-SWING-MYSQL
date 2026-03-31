/*
 * Fetch Database data & Query data
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;
import java.util.Random;
public class DB {
	
	private static final String url = "jdbc:mysql://localhost:3306/millionares_banking_DB";
	private static final String pw = "myPassword123";
	private static final String root = "root";

	public static double existingUser(String username, int accountNumber) {
	
	double balance = -1;	// If balance stays -1 it means the user does not exist, if balance is updated user exists

    String sql = "SELECT balance FROM accounts WHERE username = ? AND accountNumber = ?";
    
	try (Connection conn = DriverManager.getConnection(url, root, pw);
			 PreparedStatement stmt = conn.prepareStatement(sql);){
	       
	        // compare user name and account Number from database
	        stmt.setString(1, username);
	        stmt.setInt(2,  accountNumber);
	        	        
	        try (ResultSet rs = stmt.executeQuery()) {
	        	// Check database
	        	if(rs.next()) {
	        	// Data Found in the Database
	        	balance = rs.getDouble("balance");
	        	return balance;
	        	}
	        } 
	        
		} catch (SQLException e) {
			e.printStackTrace();
		} // rs stmt conn automatically closed
	return balance;
		
	}
public static double generateBalance() {
	 // Using random class to give user a random balance (THIS IS A PROTOTYPE CAN BE UPDATED LATER)
    Random random = new Random();
    double balance = random.nextDouble()*100;
    BigDecimal rounded = new BigDecimal(balance)
            .setScale(2, RoundingMode.HALF_UP);

    return rounded.doubleValue();
}
public static int newUser(String username, double balance) {

	// Make sure user name has a user input
    if (username == null || username.trim().isEmpty()) {
        return -1;
    }
    String sql = "INSERT INTO accounts (username, balance) VALUES (?, ?)";
    // Establish connection to mySQL & Insert new values and retrieve new account number
   // try-with-resources
    try(Connection conn = DriverManager.getConnection(url, root, pw); 	   // Establish connection to MySQL
		   PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);)
   {
	// put values from user name & balance, ? ? in string sql 
	stmt.setString(1, username);	// first ?
    stmt.setDouble(2, balance);		// second ?
    
    // Check if insert in mysql worked
    int rowsAdded = stmt.executeUpdate();
    
    if(rowsAdded > 0) {
    	// try-with-resources
    	try (ResultSet check = stmt.getGeneratedKeys()) // Now get generated account number in check.getInt(1)
{ 
    		if(check.next()) {
    			return check.getInt(1);	// 1 = first column in result set check which is account number in database table
    		}
    	} // rs & conn & stmt are automatically closed
    }
   } catch (SQLException e) 
   {
		e.printStackTrace();
	}
   
   return -1;
}

public static double updateBalance(String username, int accountNumber, double balance) {
	 // DB Passwords, URL, root
 
    String sql = "UPDATE accounts SET balance = ? WHERE username = ? AND accountNumber = ?";
    
	try(Connection conn = DriverManager.getConnection(url,root,pw);
        PreparedStatement stmt = conn.prepareStatement(sql);) {
       
        stmt.setDouble(1, balance);	// first ? in order
        stmt.setString(2,  username);
        stmt.setInt(3, accountNumber);
        
        int rowsUpdated = stmt.executeUpdate();
        
        if(rowsUpdated == 1) {
        	return balance;
        } else if (rowsUpdated > 1) {
            System.out.println("Error there were multiple rows updated.");
            return -1;
        } else {
        	return -1;
        }
	
	} catch (SQLException e) {
			e.printStackTrace();
		} // stmt conn rs close automatically
	return -1;

}

public static boolean exists(String username) {
    String sql = "SELECT 1 FROM accounts WHERE username = ?";	// Select 1 because it is lighter and faster than * since we are not scanning all the data only username
   
	try (Connection conn = DriverManager.getConnection(url, root, pw); 
	PreparedStatement stmt = conn.prepareStatement(sql);) {
    
     
        stmt.setString(1, username);
        
     try (ResultSet rs = stmt.executeQuery();)
     {
    	 return rs.next();	// true = taken, false = available
     }	// rs close
} catch (SQLException e) {
	e.printStackTrace();
	
}
	
	return false;
	}



}



