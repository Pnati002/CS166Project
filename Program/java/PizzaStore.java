/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {
   public static Scanner scanner = new Scanner(System.in);
   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break; //save login to a variable so that it can be accessed by view/update profile
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                System.out.println("10. Update Menu");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break; //change function to take in username parameter
                   case 2: updateProfile(esql, authorisedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql, authorisedUser); break;
                   case 10: updateMenu(esql, authorisedUser); break;
                   case 11: updateUser(esql, authorisedUser); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(PizzaStore esql){
      try {   
         System.out.println("\n");
         System.out.print("Enter a username: ");
         String login = scanner.nextLine();

         System.out.print("Enter a password: ");
         String password = scanner.nextLine();

         System.out.print("Enter a phone number: ");
         String phoneNum = scanner.nextLine();

         String role = "customer";
         String favoriteItems = "";

         String query = "INSERT INTO Users(login, password, role, favoriteItems, phoneNum) " + 
                        "VALUES ('" + login + "', '" + password + "', '" + role + "', '" + favoriteItems + "', '" + phoneNum + "')";
        
         esql.executeUpdate(query);
         System.out.println("User created successfully.");
         System.out.println("\n");
      
         } 
      catch(Exception e) {
        System.out.println("Error creating user: " + e.getMessage());
       }

   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(PizzaStore esql){
      try {
         System.out.println("\n");
         System.out.print("Enter your username: ");
         String login = scanner.nextLine();

         System.out.print("Enter your password: ");
         String password = scanner.nextLine();

         String query = "SELECT login FROM Users WHERE login = '" + login + "' AND password = '" + password + "'";

         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         if(results.size() <= 0) {
            System.out.println("No login found.");
            return null;
         }
         System.out.println("\nLogin successful!");
         System.out.println("\n");
         return login;
      }
      catch(Exception e) {
         System.out.println("Error during login: " + e.getMessage());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql, String user) {
      try {
            
               System.out.println("\n");
               String query = "SELECT login, phoneNum, role, favoriteItems FROM Users WHERE login = '" + user + "'";
               int rowCount = esql.executeQueryAndPrintResult(query);
               if(rowCount == 0) {
                  System.out.println("No profile could be found for user " + user);
               }
               System.out.println("\n");
               System.out.println("Press the enter key to return to menu");
               String placeholder = scanner.nextLine();
               System.out.println("\n");
      }
      catch(Exception e) {
         System.out.println("Error during viewProfile: " + e.getMessage());
      }
   }


   public static void updateProfile(PizzaStore esql, String user) {
      try {
         System.out.println("\n");
         System.out.println("UPDATING PROFILE FOR " + user); 
         System.out.println("Choose your option: \n1)Update Login (Managers only!)\n2)Update Role (Managers only!)\n3)Update Password\n4)Update Phone Number\n5)Update Favorite Item\n6)Exit");
         int option = Integer.parseInt(scanner.nextLine());
         String query = "";
         switch(option) {
            case 1:
               
               String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "'";
               List<List<String>> roleResults = esql.executeQueryAndReturnResult(roleQuery);
               String role = roleResults.get(0).get(0);

               if(!role.trim().equals("manager")) {
                  System.out.println("\nOnly managers can update login!");
                  System.out.println("\n");
                  return;
               }
               System.out.println("\nUpdate login of which user?");
               String userToUpdate = scanner.nextLine();
               query = "SELECT login FROM Users WHERE login = '" + userToUpdate + "'"; 
               List<List<String>> queryResults = esql.executeQueryAndReturnResult(query);
               if(queryResults.size() <= 0) {
                  System.out.println("No such user found in database");
                  System.out.println("\n");
                  return;
               }

               System.out.println("Update login of " + userToUpdate + " to?");
               String updatedUser = scanner.nextLine();

               query = "UPDATE Users SET login = '" + updatedUser + "' WHERE login = '" + userToUpdate + "'";   
               System.out.println("Login of " + userToUpdate + " succesfully updated to " + updatedUser + ".");
               break;
            case 2:
               roleQuery = "SELECT role FROM Users WHERE login = '" + user + "'";
               roleResults = esql.executeQueryAndReturnResult(roleQuery);
               role = roleResults.get(0).get(0);

               if(!role.trim().equals("manager")) {
                  System.out.println("\nOnly managers can update role!");
                  System.out.println("\n");
                  return;
               }
               System.out.println("\nUpdate role of which user?");
               userToUpdate = scanner.nextLine();
               query = "SELECT login FROM Users WHERE login = '" + userToUpdate + "'"; 
               queryResults = esql.executeQueryAndReturnResult(query);
               if(queryResults.size() <= 0) {
                  System.out.println("No such user found in database");
                  System.out.println("\n");
                  return;
               }

               System.out.println("Update role of " + userToUpdate + " to? (driver, manager, customer) (CASE SENSITIVE)");
               String updatedRole = scanner.nextLine();
               if(!updatedRole.equals("driver") && !updatedRole.equals("manager") && !updatedRole.equals("customer")) {
                  System.out.println("Not a valid input.");
                  System.out.println("\n");
                  return;
               }

               query = "UPDATE Users SET role = '" + updatedRole + "' WHERE login = '" + userToUpdate + "'";   
               System.out.println("Role of " + userToUpdate + " succesfully updated to " + updatedRole + ".");
               break;
            case 3:
               System.out.println("\nUpdate password of " + user + " to?");
               String updatedPassword = scanner.nextLine();
               query = "UPDATE Users SET password = '" + updatedPassword + "' WHERE login = '" + user + "'";
               break;     
            case 4:
               System.out.println("\nUpdate phone number of " + user + " to?");
               String updatedNumber = scanner.nextLine();
               query = "UPDATE Users SET phoneNum = '" + updatedNumber + "' WHERE login = '" + user + "'";
               break;    
            case 5:
               System.out.println("\nUpdate favorite item of " + user + " to?");
               String updatedFavItem = scanner.nextLine();
               
               String checkQuery = "SELECT itemName FROM Items WHERE itemName = '" + updatedFavItem + "'";
               List<List<String>> checkResults = esql.executeQueryAndReturnResult(checkQuery);
              
               if(checkResults.size() == 0) {
                  System.out.println(updatedFavItem + " does not exist at the pizza store, input a valid item. Returning..");
                  System.out.println("\n");
                  return;
               }

               query = "UPDATE Users SET favoriteItems = '" + updatedFavItem + "' WHERE login = '" + user + "'";
               break;
            case 6:
               System.out.println("\nReturning..");
               System.out.println("\n");
               return;
            default:
               System.out.println("\nInvalid input, returning..");
               System.out.println("\n");
               return;
         }
         esql.executeUpdate(query);
         System.out.println("Profile updated successfully.");
         System.out.println("\n");
      }
      catch(Exception e) {
        System.out.println("Error updating profile: " + e.getMessage());
      }
   }


   public static void viewMenu(PizzaStore esql) {
      try {
         
         String itemTypeFilter = "";
         String itemPriceFilter = "";
         String orderFilter = "ORDER BY itemName";

         while(true) {
            System.out.println("\n");
            System.out.println("Menu:");

            String query = "SELECT itemName, price FROM Items WHERE 1=1 " + itemTypeFilter + " " + itemPriceFilter + " " + orderFilter;
            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println("\n");


            System.out.println("1)Filter by Type:\n2)Filter by Max Price:\n3)Sort By Price\n4)Clear Filters\n5)Exit");
            int option = Integer.parseInt(scanner.nextLine());
            switch(option) {
               case 1:
                  System.out.print("Enter type of item to filter by (entree, drinks, sides): ");
                  String type = scanner.nextLine();
                  if(!type.equals("entree") && !type.equals("drinks") && !type.equals("sides")) {
                     System.out.println("Not a valid type.");
                     break;
                  }
                  itemTypeFilter = "AND typeOfItem = ' " + type + "'";
                  break;
               case 2:
                  System.out.print("Enter maximum price to filter by: ");
                  String price = scanner.nextLine();
                  itemPriceFilter = "AND price <= " + price + " ";
                  break;
               case 3:
                  System.out.println("1)Sort by Ascending\n2)Sort by Descending\n");
                  Integer choice = Integer.parseInt(scanner.nextLine()); 
                  if(choice == 1) {
                     orderFilter = "ORDER BY price ASC ";
                  }   
                  else if(choice == 2) {
                     orderFilter = "ORDER BY price DESC ";
                  }
                  else {
                     System.out.println("Invalid input.");
                  }
                  break;
               case 4:
                  itemTypeFilter = "";
                  itemPriceFilter = "";
                  orderFilter = "ORDER BY itemName";
                  System.out.println("Filters cleared.");
                  break;
               case 5:
                  return;
               default:
                  System.out.println("Invalid input, returning...");
                  return;
            }
         }
      }
      catch(Exception e) {
        System.out.println("Error viewing menu: " + e.getMessage());
      }
   }


   public static void placeOrder(PizzaStore esql, String user) {
      try {
         System.out.println("\n");
         String query = "SELECT storeID, address, city, state FROM Store";
         int rowCount = esql.executeQueryAndPrintResult(query);

         System.out.println("\n");

         System.out.print("Input store ID from the store you want to order from: ");
         Integer store = Integer.parseInt(scanner.nextLine());


         List<String> orderItems = new ArrayList<>();
         List<Integer> orderQuantities = new ArrayList<>();
         double totalPrice = 0.0;
         double price = 0.0;
         System.out.println("\n");

         query = "SELECT DISTINCT itemName FROM Items";
         rowCount = esql.executeQueryAndPrintResult(query);

         String item = "";
         int quantity = 0;

         while(true) {
            System.out.print("\nPlace your order using these items above (case sensitive)\nType 'done' to finish the order: ");
            item = scanner.nextLine();
            if(item.equals("done")) {
               break;
            }
            query = "SELECT itemName FROM Items WHERE itemName = '" + item + "'"; 
            rowCount = esql.executeQuery(query);
            if(rowCount <= 0 ) {
               System.out.println("Not a valid item");
               continue;
            }
            
            System.out.print("Enter quantity for '" + item + "': ");
            quantity = Integer.parseInt(scanner.nextLine());
            
            String priceQuery = "SELECT price FROM Items WHERE itemName = '" + item + "'";
            List<List<String>> priceResult = esql.executeQueryAndReturnResult(priceQuery);
            
            price = Double.parseDouble(priceResult.get(0).get(0));
            totalPrice += price * quantity;
            
            orderItems.add(item);
            orderQuantities.add(quantity);
         }

         if(totalPrice == 0.0) {
            System.out.println("Invalid order, needs at least one item.");
            System.out.println("\n");
            return;
         }
        
         String nextOrderIdQuery = "SELECT COALESCE(MAX(orderid), 0) + 1 FROM FoodOrder";
         List<List<String>> orderIdResult = esql.executeQueryAndReturnResult(nextOrderIdQuery);
         int orderID = Integer.parseInt(orderIdResult.get(0).get(0));

         String insertOrder = "INSERT INTO FoodOrder(orderid, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
                              "VALUES (" + orderID + ", '" + user + "', '" + store + "', " + totalPrice + ", CURRENT_TIMESTAMP, 'incomplete')";
         esql.executeUpdate(insertOrder);

         for (int i = 0; i < orderItems.size(); i++) {
            String orderItem = orderItems.get(i);
            int qty = orderQuantities.get(i);
            String insertItem = "INSERT INTO ItemsInOrder(orderID, itemName, quantity) " +
                                 "VALUES (" + orderID + ", '" + orderItem + "', " + qty + ")";
            esql.executeUpdate(insertItem);
         }
        
         System.out.println("Order #" + orderID + " placed successfully!");
         System.out.printf("Total Price: $%.2f\n", totalPrice);

      }
      catch(Exception e) {
         System.out.println("Error placing order: " + e.getMessage());
      }
   }



   public static void viewAllOrders(PizzaStore esql, String user) {
      try {
         System.out.println("\n");
         String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "'";
         List<List<String>> roleResults = esql.executeQueryAndReturnResult(roleQuery);
         String role = roleResults.get(0).get(0);
         String orderQuery = "";

         if(role.trim().equals("customer")) {
            orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder WHERE login = '" + user + "' ORDER BY orderTimestamp DESC";
         }
         else {
            orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder ORDER BY orderTimestamp DESC";
         }

         int rowCount = esql.executeQueryAndPrintResult(orderQuery);
         if(rowCount == 0) {
            System.out.println("No orders found.");
         }
         System.out.println("\n");
      }
      catch(Exception e) {
         System.out.println("Error viewing all orders: " + e.getMessage());
      }
   }


   public static void viewRecentOrders(PizzaStore esql, String user) {
      try {
         System.out.println("\n");
         String orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder WHERE login = '" + user + "' ORDER BY orderTimestamp DESC LIMIT 5";

         int rowCount = esql.executeQueryAndPrintResult(orderQuery);
         if(rowCount == 0) {
            System.out.println("No orders found.");
         }
         System.out.println("\n");
      }
      catch(Exception e) {
         System.out.println("Error viewing all orders: " + e.getMessage());
      }
   }



public static void viewOrderInfo(PizzaStore esql, String user) {
      try {
        // Retrieve the role from the Users table
        System.out.println("\n");
        String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
        List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
        if (roleResult.isEmpty()) {
            System.out.println("\nInvalid login. Access denied.");
            return;
        }

        String role = roleResult.get(0).get(0);
        String query = "";
        int orderId = 0;
        int rowCount = 0;

        if(role.trim().equalsIgnoreCase("Customer")) {
            query = "SELECT OrderID FROM FoodOrder WHERE login = '" + user + "'";
            rowCount = esql.executeQueryAndPrintResult(query);
            if(rowCount <= 0 ) {
               System.out.println("No orders have been made by this user so far.");
               System.out.println("\n");
               return;
            }
            System.out.print("\nEnter Order ID from eligible orders (Can only view orders that you've made!): ");
            orderId = Integer.parseInt(scanner.nextLine());
            System.out.println("");

            String checkQuery = "SELECT login FROM FoodOrder WHERE OrderID = '" + orderId + "'";
            List<List<String>> checkResult = esql.executeQueryAndReturnResult(checkQuery);
            String login = checkResult.get(0).get(0);
            if (!login.trim().equals(user)) {
               System.out.println("\nThis order does not belong to you.");
               System.out.println("\n");
               return;
            }
            query = "SELECT * FROM FoodOrder WHERE orderId = '" + orderId + "'"; 
        }
        else {
            query = "SELECT OrderID FROM FoodOrder ORDER BY OrderID ASC";
            rowCount = esql.executeQueryAndPrintResult(query);
            if(rowCount <= 0 ) {
               System.out.println("No orders have been made by this user so far.");
               System.out.println("\n");
               return;
            }
            System.out.print("\nEnter Order ID: ");
            orderId = Integer.parseInt(scanner.nextLine());
            query = "SELECT * FROM FoodOrder WHERE orderId = '" + orderId + "'";    
        }

         rowCount = esql.executeQueryAndPrintResult(query);
         if(rowCount <= 0) {
            System.out.println("\nNo order found with such ID");
            System.out.println("\n");
            return;
         }
      
        String itemsQuery = "SELECT itemName, quantity FROM ItemsInOrder WHERE orderID = " + orderId;
        System.out.println("\nItems in this order:");
        esql.executeQueryAndPrintResult(itemsQuery);
        System.out.println("\n");
      }
      catch (Exception e) {
        System.err.println("\nError viewing order info: " + e.getMessage());
      }
   }


   public static void viewStores(PizzaStore esql) {
      try {
        System.out.println("\n");
        String query = "SELECT * FROM Store;";
        esql.executeQueryAndPrintResult(query);
        System.out.println("\n");
      } 

      catch (Exception e) {
        System.err.println("\nError viewing stores: " + e.getMessage());
      }
   }


   public static void updateOrderStatus(PizzaStore esql, String user) {
      try {
        String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
        List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
        
        if (roleResult.isEmpty()) {
            System.out.println("\nInvalid login. Access denied.\n");
            return;
        }
        
        String role = roleResult.get(0).get(0);
        
        // Only managers or drivers are allowed to update the order status
        if (!role.trim().equalsIgnoreCase("manager") && !role.trim().equalsIgnoreCase("Driver")) {
            System.out.println("\nYou do not have permission to update the order status.\n");
            return;
        }
        
        // Prompt for order ID
        System.out.println("");
        
         String query = "SELECT OrderID FROM FoodOrder ORDER BY OrderID ASC";
         int rowCount = esql.executeQueryAndPrintResult(query);
         if(rowCount <= 0 ) {
            System.out.println("No orders have been made.");
            System.out.println("\n");
            return;
         }

        System.out.print("\nEnter the order ID you want to update: ");
        int orderId = Integer.parseInt(in.readLine());
        
        // Check if the order exists
        String orderQuery = "SELECT * FROM FoodOrder WHERE orderID = " + orderId + ";";
        List<List<String>> orderResult = esql.executeQueryAndReturnResult(orderQuery);
        
        if (orderResult.isEmpty()) {
            System.out.println("\nOrder ID not found.");
            return;
        }
        
        // Prompt for new order status
        System.out.print("\nEnter the new order status (incomplete or complete): ");
        String newStatus = scanner.nextLine().trim().toLowerCase();
        
        // Validate the status input
        if (!newStatus.equals("incomplete") && !newStatus.equals("complete")) {
            System.out.println("\nInvalid status. Only 'incomplete' and 'complete' are allowed.");
            return;
        }
        
        // Update the order status
        String updateQuery = "UPDATE FoodOrder SET orderStatus = '" + newStatus + "' WHERE orderID = " + orderId;
        esql.executeUpdate(updateQuery);
        
        System.out.println("\nOrder status updated successfully.");
        System.out.println("\n");
        
      } 
      catch (Exception e) {
        System.err.println("\nError updating order status: " + e.getMessage());
      }
   }

   
   public static void updateMenu(PizzaStore esql, String user) {
      try {
        String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
        List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
        
        if (roleResult.isEmpty()) {
            System.out.println("\nInvalid login. Access denied.");
            return;
        }
        
        String role = roleResult.get(0).get(0);
        
        if (!role.trim().equalsIgnoreCase("Manager")) {
            System.out.println("\nYou do not have permission to update the menu.\n");
            return;
        }
        
        // asks for item to add / update
        System.out.print("\nEnter the name of the item you want to update or add: ");
        String itemName = scanner.nextLine().trim();
        
        // Check if item exists
        String itemQuery = "SELECT * FROM Items WHERE itemName = '" + itemName + "';";
        List<List<String>> itemResult = esql.executeQueryAndReturnResult(itemQuery);
        
        String ingredients = "";
        String typeOfItem = "";
        double price = 0.0;
        String description = "";
        
        // item exists => updating
        if (!itemResult.isEmpty()) {
            System.out.println("\nItem found. You can update the item details.");
            
            // current details of item
            ingredients = itemResult.get(0).get(1);
            typeOfItem = itemResult.get(0).get(2);
            price = Double.parseDouble(itemResult.get(0).get(3));
            description = itemResult.get(0).get(4);
            
            // new details
            System.out.print("\nEnter new ingredients (current: " + ingredients + "): ");
            ingredients = scanner.nextLine().trim();
            
            // Validate typeOfItem input
            while (true) {
                System.out.print("\nEnter new type of item (current: " + typeOfItem + ") (entree, drink, side): ");
                typeOfItem = scanner.nextLine().trim().toLowerCase();
                if (typeOfItem.equals("entree") || typeOfItem.equals("drink") || typeOfItem.equals("side")) {
                    break;
                } else {
                    System.out.println("\nInvalid input. Please enter 'entree', 'drink', or 'side'.");
                }
            }
            
            System.out.print("\nEnter new price (current: " + price + "): ");
            price = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("\nEnter new description (current: " + description + "): ");
            description = scanner.nextLine().trim();
        
        } else {
            // item doesn't exist => adding
            System.out.println("\nItem not found. You can add a new item.");
            
            // new item details
            System.out.print("\nEnter ingredients: ");
            ingredients = scanner.nextLine().trim();
            
            // Validate typeOfItem input
            while (true) {
                System.out.print("\nEnter type of item (entree, drink, side): ");
                typeOfItem = scanner.nextLine().trim().toLowerCase();
                if (typeOfItem.equals("entree") || typeOfItem.equals("drink") || typeOfItem.equals("side")) {
                    break;
                } else {
                    System.out.println("\nInvalid input. Please enter 'entree', 'drink', or 'side'.");
                }
            }
            
            System.out.print("\nEnter price: ");
            price = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("\nEnter description: ");
            description = scanner.nextLine().trim();
        }
        
        // Display updated item details
        System.out.println("\nPlease confirm the following item details:");
        System.out.println("\nItem Name: " + itemName);
        System.out.println("\nIngredients: " + ingredients);
        System.out.println("\nType of Item: " + typeOfItem);
        System.out.println("\nPrice: " + price);
        System.out.println("\nDescription: " + description);
        
        // Ask confirmation
        System.out.print("\nDo you want to proceed with these changes? (yes/no): ");
        String confirmation = in.readLine().trim().toLowerCase();
        
        if (confirmation.equals("yes")) {
            if (!itemResult.isEmpty()) {
                // Update item in the menu
                String updateQuery = "UPDATE Items SET ingredients = '" + ingredients + "', " +
                                     "typeOfItem = '" + typeOfItem + "', price = " + price + ", " +
                                     "description = '" + description + "' WHERE itemName = '" + itemName + "';";
                esql.executeUpdate(updateQuery);
                System.out.println("Item details updated successfully.");
            } else {
                // Insert new item into the menu
                String insertQuery = "INSERT INTO Items (itemName, ingredients, typeOfItem, price, description) " +
                                     "VALUES ('" + itemName + "', '" + ingredients + "', '" + typeOfItem + "', " +
                                     price + ", '" + description + "');";
                esql.executeUpdate(insertQuery);
                System.out.println("\nNew item added to the menu.");
            }
        } else {
            System.out.println("\nOperation cancelled. No changes were made.");
        }
        
      } 

      catch (Exception e) {
        System.err.println("\nError updating menu: " + e.getMessage());
      }
   }


   public static void updateUser(PizzaStore esql, String user) {
      try {
        // Retrieve the role of the user
        String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
        List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);

        if (roleResult.isEmpty()) {
            System.out.println("\nInvalid login. Access denied.");
            return;
        }

        String role = roleResult.get(0).get(0);

        // Only managers can update user information
        if (!role.trim().equalsIgnoreCase("Manager")) {
            System.out.println("\nYou do not have permission to update user information.\n");
            return;
        }

        // Prompt for the login of the user whose information will be updated
        System.out.print("\nEnter the login of the user you want to update: ");
        String userLogin = scanner.nextLine().trim();

        // Check if the user exists in the database
        String userQuery = "SELECT * FROM Users WHERE login = '" + userLogin + "';";
        List<List<String>> userResult = esql.executeQueryAndReturnResult(userQuery);

        if (userResult.isEmpty()) {
            System.out.println("\nUser not found.");
            return;
        }

        // Variables to store the current details for confirmation
        String currentRole = userResult.get(0).get(2);
        String currentFavoriteItems = userResult.get(0).get(3);
        String currentPhoneNum = userResult.get(0).get(4);

        // Display current user details for updating
        System.out.println("\nCurrent user details:");
        System.out.println("\nLogin: " + userLogin);
        System.out.println("\nRole: " + currentRole);
        System.out.println("\nFavorite Items: " + currentFavoriteItems);
        System.out.println("\nPhone Number: " + currentPhoneNum);

        // Ask for new values to update
        System.out.print("\nEnter new role (current: " + currentRole + "): ");
        String newRole = scanner.nextLine().trim();

        // Validate role input (it can only be one of 'Customer', 'Manager', or 'Driver')
        while (!newRole.equalsIgnoreCase("Customer") && !newRole.equalsIgnoreCase("Manager") && !newRole.equalsIgnoreCase("Driver")) {
            System.out.println("\nInvalid role. Please enter 'Customer', 'Manager', or 'Driver'.");
            System.out.print("\nEnter new role: ");
            newRole = scanner.nextLine().trim();
        }

        System.out.print("\nEnter new favorite items (current: " + currentFavoriteItems + "): ");
        String newFavoriteItems = scanner.nextLine().trim();

        System.out.print("\nEnter new phone number (current: " + currentPhoneNum + "): ");
        String newPhoneNum = scanner.nextLine().trim();

        // Display the updated details for confirmation
        System.out.println("\nPlease confirm the following user details:");
        System.out.println("\nLogin: " + userLogin);
        System.out.println("\nRole: " + newRole);
        System.out.println("\nFavorite Items: " + newFavoriteItems);
        System.out.println("\nPhone Number: " + newPhoneNum);

        // Ask for confirmation
        System.out.print("\nDo you want to proceed with these changes? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            // Update the user in the database
            String updateQuery = "UPDATE Users SET role = '" + newRole + "', favoriteItems = '" + newFavoriteItems + "', " +
                                 "phoneNum = '" + newPhoneNum + "' WHERE login = '" + userLogin + "';";
            esql.executeUpdate(updateQuery);
            System.out.println("\nUser details updated successfully.");
        } else {
            System.out.println("\nUpdate canceled.");
        }

    }

    catch (Exception e) {
        System.out.println("\nAn error occurred while updating user information: " + e.getMessage());
    }
   }

}//end PizzaStore

