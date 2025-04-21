# CS 166 Project Report

Jason Lee and Peter Natividad (jlee1667 pnati002)

# Initial Summary/Contributions
For this assignment, we used the provided Java template to build our PizzaStore system. We made minimal changes to the template. Mainly, we imported a Scanner for reading user inputs and modified some functions to accept a user string as a parameter. Our program is a console-based application that connects to a PostgreSQL database to manage pizza orders. It supports user registration and login with role-based access control, ensuring that customers can only view and update their own profiles and orders, while managers and drivers have extra privileges. Users can browse the menu with dynamic filtering and sorting options, place orders (with details stored in the FoodOrder and ItemsInOrder tables), and view order history or specific order details. We used JDBC for database connectivity along with a Scanner for interactive input.

The work was split up evenly, Jason took the first 6 functions that had to be implemented, and Peter took the last 5 functions. Jason implemented the register and login function as well. The first 3 indexes were made by Peter, and the last 4 were made by Jason. Jason and Peter individually wrote the explanations for each of the functions/indexes that were completed by them respectively.

# Problems/Finding

Thankfully, there weren't too many problems encountered during this lab. One issue that arose was dirty data. The type of item column in the Items table suffered from whitespace in the string, which caused queries to return nothing. Rather than fixing this within the csv file directly, we chose to use the trim() function to clean our data and get proper results.

# Explaining the Code

* CreateUser() (Jason)
```
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
   ```

   A very simple function, takes in a username, password, and phone number, and creates a new user in the table, with no favorite items and a default role of customer.
   The SQL query string does the job of inserting a new user using the INSERT clause. 

   * LoginUser() (Jason)

   ```
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
   ```

   Another simple function, after inputting a username and password a query is made to the users table. If a value is returned, then the credentials match and the user is logged in.
   The SQL query string here filters users by passwords, under the assumption that only the valid user would know the password.

* viewProfile() (Jason)
   ```
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
   ``` 
   Using the user parameter, we make a query to the Users table to retrieve all relevant information about the user who is currently logged in.
   In this SQL query, we refrain from using * as that would reveal the user's password, which is unintended behavior.

* updateProfile() (Jason)
```
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
   ```
   This function allows users to update the fields in their profile that they have access to. If a non-manager attempts to do a manager-restricted input, they will be promptly denied access. Otherwise, the user chooses which field to change, gives an input, and an update query is called to update the user's record in the table. This function shares some qualities with updateUser, this stemmed from a confusion during implementation where I chose to allow managers to change those fields in this fiunction.
   
   * "SELECT role FROM Users WHERE login = '" + user + "'"

 This query is used to verify that the user is allowed to update certain fields as a manager.

   * "SELECT login FROM Users WHERE login = '" + userToUpdate + "'";

This query checks to see if the user that is to be updated actually exists, the login is a bit redundant and could be replaced with *. 

* query = "UPDATE Users SET login = '" + updatedUser + "' WHERE login = '" + userToUpdate + "'";
* query = "UPDATE Users SET password = '" + updatedPassword + "' WHERE login = '" + user + "'";
* query = "UPDATE Users SET phoneNum = '" + updatedNumber + "' WHERE login = '" + user + "'";
* query = "UPDATE Users SET favoriteItems = '" + updatedFavItem + "' WHERE login = '" + user + "'";

These queries update the field chosen by the user with the new value.


* viewMenu() (Jason)
```
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
```
In this function, I fragmented an sql query based on it's primary components. The initial query will esentially just show all items. Depending on what the user wants to filter by, they give an input, which is put into a SQL query. This input filters the menu appropriately, whether it be price or item type. This function returns once the user explicitly chooses to.

* placeOrder() Jason
```
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
```

First, I use an SQL query to output all possible stores to choose from. Once the user inputs a valid storeID, the ordering process begins. A while loop runs, taking in valid items and their quantities while updating the total price. Once the user inputs "done", the orderID is determined from the last order placed. A new order is inserted into the FoodOrder table, and the items in the order along with the quantity are also inserted into the ItemsInOrder table through a for loop. The orderID is outputted along with the total price, and the function returns.

* query = "SELECT itemName FROM Items WHERE itemName = '" + item + "'"; 

This query is used to output all possible items so that the user orders items that actually exist

* String priceQuery = "SELECT price FROM Items WHERE itemName = '" + item + "'";

This query is used to get the price of an item that has been added to an order

* String nextOrderIdQuery = "SELECT COALESCE(MAX(orderid), 0) + 1 FROM FoodOrder";

This query is used to create the orderID of a new order. We find the max value of orderID in the table, and then increment it by 1.

* String insertOrder = "INSERT INTO FoodOrder(orderid, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
                              "VALUES (" + orderID + ", '" + user + "', '" + store + "', " + totalPrice + ", CURRENT_TIMESTAMP, 'incomplete')";

This query is used to insert a new order into the FoodOrder table, with all relevant information about the order.

* String insertItem = "INSERT INTO ItemsInOrder(orderID, itemName, quantity) " +
                                 "VALUES (" + orderID + ", '" + orderItem + "', " + qty + ")";

This query is used to insert a new order into the ItemInOrder table, which just keeps tracks of the items and their quantities in an order.

* viewAllOrders() (Jason)
```
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
```
In this function, we determine the role of the user that is passed through the function. We then create our query depending on the user's role. If the user is a manager/driver, all orders from all customers are outputted. If the user is a customer, only the customer's orders are outputted.

* orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder WHERE login = '" + user + "' ORDER BY orderTimestamp DESC";

This query is used to limit a customer from viewing orders that aren't their own.

* orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder ORDER BY orderTimestamp DESC";

This query is used to let managers see all orders that have placed in history.

* viewRecentOrders() (Jason)
```
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
```
Very similar to the last function, I simply got rid of the check for roles. I simply output the latest 5 orders from the user.

* String orderQuery = "SELECT orderID, totalPrice, orderTimestamp FROM FoodOrder WHERE login = '" + user + "' ORDER BY orderTimestamp DESC LIMIT 5";

This query lets users see their latest 5 orders from the pizza store.

* viewOrderInfo() (Peter)
```
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
```
 Allows users to view a specific order's information such as the price and status. It takes into account the user's role (manager, customer, driver) and gives them certain privileges based on that. If they're a customer then let them only see their orders. If they are a manager / driver then let them see all orders. To reach this end, the function checks the user's role using a query and then asks for the orderID of the order they would like to view. The constraints mentioned before are taken into account in this function.

* viewStores() (Peter)
```
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
```
Allows the customer to view all the pizza stores along with their associated information such as there location, storeID, etc. In order to do this, this function utilizes this query "SELECT * FROM Store;" to get and output all stores and their associated information. No additional input needed.

* updateOrderStatus() (Peter)
```
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
```
Allows drivers and managers to be able to update the status of orders (ex: Change from incomplete => complete). To do this, the function first checks if the user is a driver or a manager through a query. Then once verified as such, they are then asked for the orderID of the order they wish to modify the status of. If given a valid existing order, the user states the status they wish to change the order to (complete / incomplete) and an update query is used on the order to reflect these changes. 

* updateMenu() (Peter)
```
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
```
 Allows managers to add / update items in the menu. To reach this end, the function verifies if the user is a manager or not first by using a query with their login. Once validated, the user is asked to give the name of the item they wish to add / update. The function checks if the item exists through a query and if it does exist then allows the manager to change the information for that item. If it doesn't exist, then allows the manager to add the item along with its information. At the end, it asks for confirmation for if the manager would like to add / update the item. If yes then the item is added / updated using an insert / update query. If no then the process is cancelled and no changes are made.

* updateUser() (Peter)
```
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
```
Allows managers to update the information of all users (role, phone number, etc). To reach this end, the function verifies if the user is a manager or not first by using a query with their login. Once validated, the user is asked to give the login of the user they wish to update. The function checks if the login exists through a query and if it does exist then allows the manager to change the information for that user. At the end, it asks for confirmation for if the manager would like to update the user information. If yes then the information is updated using an update query. If no then the process is cancelled and no changes are made.

# Indexes
```
DROP INDEX IF EXISTS idx_order_status;
DROP INDEX IF EXISTS idx_role;
DROP INDEX IF EXISTS idx_order_id;
DROP INDEX IF EXISTS idx_login;
DROP INDEX IF EXISTS idx_itemName;
DROP INDEX IF EXISTS idx_itemType;
DROP INDEX IF EXISTS idx_price;


CREATE INDEX idx_order_status ON FoodOrder(orderStatus);
CREATE INDEX idx_role ON Users(role);
CREATE INDEX idx_order_id ON FoodOrder(orderID);

CREATE INDEX idx_login ON Users USING HASH (login);
CREATE INDEX idx_itemName ON Items USING HASH (itemName);
CREATE INDEX idx_itemType ON Items USING HASH (typeOfItem);
CREATE INDEX idx_price ON Items(Price);
```

First three indexes were done by Peter, and the last 4 were done by Jason.

* CREATE INDEX idx_order_status ON FoodOrder(orderStatus);
 
 Used to speed up UpdateOrderStatus

* CREATE INDEX idx_role ON Users(role);

Used in all functions that required knowing the roles of a specified user (ex: If they are a manager so they can do manager only updates)

* CREATE INDEX idx_order_id ON FoodOrder(orderID);

Used to speed up all functions that updates/views orders

* CREATE INDEX idx_login ON Users USING HASH (login);

There are multiples queries in the program that check if the login field is equivalent to a certain user. Since we're doing constant equality checks, a hash index made sense.

* CREATE INDEX idx_itemName ON Items USING HASH (itemName);

Similar story as before, constantly checking to see if itemName matches with item in table, so a hash index is appropriate.

* CREATE INDEX idx_itemType ON Items USING HASH (typeOfItem);

Same as before, but this time see if type of item matches with the one in the User table, so a hash index is used.

* CREATE INDEX idx_price ON Items(Price);

When filtering the menu, we check to see if the price is under a certain range. This range check is what caused me to use a B-tree index.

# Example Run

Below is the output showcasing the succesful registration and sign in of a new user. The user calls the view profile function, which outputs a profile with his name, role, phone number, and favorite item. The user is able to change his favorite item to "Cheese Pizza", but when he tries to change his role he is denied access by the program. 

```
[jlee1667@xe-10 cs166_project_phase3]$ source java/scripts/compile.sh 


*******************************************************
              User Interface                           
*******************************************************

Connecting to database...Connection URL: jdbc:postgresql://localhost:32057/jlee1667_project_phase_3_DB

Done
MAIN MENU
---------
1. Create user
2. Log in
9. < EXIT
Please make your choice: 1


Enter a username: joe
Enter a password: 123
Enter a phone number: 714-679-3599
User created successfully.


MAIN MENU
---------
1. Create user
2. Log in
9. < EXIT
Please make your choice: 2


Enter your username: joe
Enter your password: 123

Login successful!


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 1


login   phonenum        role    favoriteitems
joe     714-679-3599    customer            


Press the enter key to return to menu
2


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 2


UPDATING PROFILE FOR joe
Choose your option: 
1)Update Login (Managers only!)
2)Update Role (Managers only!)
3)Update Password
4)Update Phone Number
5)Update Favorite Item
6)Exit
5

Update favorite item of joe to?
Cheese Pizza
Profile updated successfully.


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 1


login   phonenum        role    favoriteitems
joe     714-679-3599    customer                Cheese Pizza


Press the enter key to return to menu



MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 2


UPDATING PROFILE FOR joe
Choose your option: 
1)Update Login (Managers only!)
2)Update Role (Managers only!)
3)Update Password
4)Update Phone Number
5)Update Favorite Item
6)Exit
2

Only managers can update role!


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 
```

Below is an account that has been made manager. The manager can access the update role, and changes Joe to be a driver. Logging back into Joe, we can see that Joe is indeed now a driver.

```
[jlee1667@xe-10 cs166_project_phase3]$ source java/scripts/compile.sh 


*******************************************************
              User Interface                           
*******************************************************

Connecting to database...Connection URL: jdbc:postgresql://localhost:32057/jlee1667_project_phase_3_DB

Done
MAIN MENU
---------
1. Create user
2. Log in
9. < EXIT
Please make your choice: 2


Enter your username: jason
Enter your password: lee

Login successful!


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 1


login   phonenum        role    favoriteitems
jason   098-765-4321    manager                 7up


Press the enter key to return to menu



MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 2


UPDATING PROFILE FOR jason
Choose your option: 
1)Update Login (Managers only!)
2)Update Role (Managers only!)
3)Update Password
4)Update Phone Number
5)Update Favorite Item
6)Exit
2

Update role of which user?
joe
Update role of joe to? (driver, manager, customer) (CASE SENSITIVE)
driver 
Role of joe succesfully updated to driver.
Profile updated successfully.


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 20
MAIN MENU
---------
1. Create user
2. Log in
9. < EXIT
Please make your choice: 2


Enter your username: joe
Enter your password: 123

Login successful!


MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
Please make your choice: 1


login   phonenum        role    favoriteitems
joe     714-679-3599    driver                  Cheese Pizza


Press the enter key to return to menu



MAIN MENU
---------
1. View Profile
2. Update Profile
3. View Menu
4. Place Order
5. View Full Order ID History
6. View Past 5 Order IDs
7. View Order Information
8. View Stores
9. Update Order Status
10. Update Menu
11. Update User
.........................
20. Log out
```