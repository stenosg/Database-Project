/*
 * Template JAVA User Interface
 * =======================================================
 *
-- Database Management Systems (Fall 2008)
-- Department of Computer Science & Engineering
-- University of California - Riverside
 *
 * Date		  Person		    Modification
 * -------------------------------------------------------
 * 02/18/2008	  Wanxing (Sarah) Xu	    Initial Version
 *
 * Target DBMS: 'Postgres'
 *
 */

//--------------------
// CS 166 Project
// Robert Doherty
// George Stenos
//--------------------

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




/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Travel4u {

	//----------------GLOBAL USER_NAME AND PASSWORD-------------------------//
	static public String USERNAME;
	static public String PASSWORD;
	static public int att = 0;

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

   /**
    * Creates a new instance of Travel4u
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Travel4u (String dbname, String dbport, String user, String passwd) throws SQLException {
      //System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         //System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end try
   }//end Travel4u

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
   public int executeQuery (String query) throws SQLException {
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
      while (rs.next ()) {
         for (int i=1; i<=numCol; ++i){
            System.out.println (rsmd.getColumnName (i) +
               " = " + rs.getString (i));
         }//end for
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery


//---------------------------FOR-LOGIN-------------------------//
	public int loginQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      while (rs.next ()) {
         ++rowCount;
      }//end while
      stmt.close ();

      return rowCount;
   }
//--------------------------------------------------------------//

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup () {
      try {
         if (this._connection != null)
            this._connection.close ();
      }catch(SQLException e){
         // ignored.
      }//end catch
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 2) {
         System.err.println ("Usage: " + "java [-classpath <classpath>] " + 
							 Travel4u.class.getName () + " <dbname> <port> ");
         return;
      }//end if

      Greeting();
      Travel4u esql = null;
     
	 try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Travel4u object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = "dohertyr";	// my username
         String passwd = "b8aRd,rw";  // my password
         esql = new Travel4u (dbname, dbport, user, passwd);

		boolean login = false;  // set to true when user is able to log in
		int trueUser = Login(esql);	// attempts to login and proceeds if successful

		while( !login ) {

			if( trueUser == 1 ) {
				//System.out.println("User is a Manager");
				login = true;
			}

			else if( trueUser == 2 ) {
				//System.out.println("User is a Broker");
				login = true;
			}

			else if( trueUser == 3 ) {
				//System.out.println("User is an Agent");
				login = true;
			}
			
			else {
				trueUser = Login(esql);
				login = false;
			}
		}

         boolean keepon = true;
         while(keepon){
            // These are sample SQL statements

			if( trueUser == 1 ) {
				keepon = managerMenu(esql);
			}
			else if( trueUser == 2) {
				keepon = brokerMenu(esql);
			}
			else if( trueUser == 3 ) {
				keepon = agentMenu(esql);
			}
            
			
         }//end while
		
		// LOGS USER OUT
		if( trueUser == 3 ) {
			String query = "UPDATE attendance SET check_out = now() WHERE attendanceid = " + att;
			//System.out.println(query);
			esql.executeUpdate(query);
		}

		System.out.println();
		System.out.println("*****************Exiting Travel4u*********************");
		System.out.println();


      }catch (Exception e){
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if (esql != null) {
               //System.out.print("Disconnecting from database...");
               esql.cleanup ();
               //System.out.println("Done\n\nBye !");
            }//end if
         }catch(Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   
   public static void Greeting(){
      System.out.println(
        "\n\n*******************************************************\n" +
        "             		Travel4u      	               \n" +
        "*******************************************************\n");
   }//end Greeting


	//------------------LOGIN------------------------------------//
	// Manager = 1
	// Broker  = 2
	// Agent   = 3
	//----------------------------------------------------------//
	public static int Login(Travel4u esql) {
		
		try{
			boolean trueUser = false;
			while( !trueUser ) {

				System.out.print("Enter username: ");
				USERNAME = in.readLine();

				System.out.print("Enter password: ");
				PASSWORD = in.readLine();

				// Checks user info
				String query = "SELECT * FROM employee WHERE user_name = '" + USERNAME + 
							   "' AND password = '" + PASSWORD + "'";
				int rowCount = esql.loginQuery(query);	

				// if this user exists, find what kind of employee they are
				if( rowCount > 0 ) {
					
					trueUser = true;					
					

					//--------------------CHECK-FOR-MANAGER-----------------------//
					query = "SELECT * FROM employee, manager WHERE employee.user_name = '" + USERNAME + 
							"' AND employee.employeeid = manager.employeeid";
					rowCount = esql.loginQuery(query);
					if( rowCount > 0 ) {
						return 1;	// user is a manager
					}
					

					//--------------------CHECK-FOR-BROKER------------------------//
					query = "SELECT * FROM employee, broker WHERE employee.user_name = '" + USERNAME + 
							"' AND employee.employeeid = broker.employeeid";
					rowCount = esql.loginQuery(query);
					if( rowCount > 0 ) {
						
						return 2;	// user is a broker
					}
					

					//--------------------CHECK-FOR-AGENT-------------------------//
					query = "SELECT * FROM employee, agent WHERE employee.user_name = '" + USERNAME + 
							"' AND employee.employeeid = agent.employeeid";
					rowCount = esql.loginQuery(query);
					if( rowCount > 0 ) {
						
						System.out.print("Enter Employee ID: ");
						String input = in.readLine();

						// Check if it is their employee ID
						query = "SELECT * FROM employee WHERE employee.user_name = '" + USERNAME + 
								"' AND employee.employeeid = " + input;
						rowCount = esql.loginQuery(query);

						// if the user enters wrong employee ID
						while( rowCount <= 0 ) {
							System.out.println("Wrong Employee ID!");
							System.out.println();
							System.out.print("Enter Employee ID: ");
							input = in.readLine();

							// Check if it is their employee ID
							query = "SELECT * FROM employee WHERE employee.user_name = '" + USERNAME + 
									"' AND employee.employeeid = " + input;
							rowCount = esql.loginQuery(query);
						}
						
						// Attendance Crap						
						query = "SELECT * FROM attendance";
						rowCount = esql.loginQuery(query);
						att = rowCount + 1;
						query = "INSERT INTO attendance (attendanceid, employeeid) VALUES(" 
								+ att + ", " + input + ")";
						//query = "INSERT INTO attendance (employeeid) VALUES( " + input + " )";
						//System.out.println(query);
						esql.executeUpdate(query);

						System.out.println("Successfully logged in!");

						return 3;	// user is an agent
					}
					


					return 0;
				}// end is user
				else {
					System.out.println("Wrong user name or password!");
					System.out.println();
					trueUser = false;
				}// end is not user

			} // end while
		} // end try

		catch (Exception e) {
			System.out.println("Your input is invalid!");
			return 0;
		}// end catch
		return 0;
	} // end Login
//----------------------------------------------------//



/*************************************************MENUS************************************************/

//---------------Manager Menu-------------------------//
	public static boolean managerMenu(Travel4u esql) {

		System.out.println();
			System.out.println("-------------------------------------------------");
			System.out.println("MANAGER MAIN MENU");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Find employee ");
			System.out.println("2. Create employee ");
			System.out.println("3. Delete employee ");
            System.out.println("4. Find employee with least hours ");
            System.out.println("5. Find most successful broker ");
			System.out.println("6. Find trip packages you supervise ");
			System.out.println("7. Favored customers ");
			System.out.println("8. Most productive broker ");
            System.out.println("0. < EXIT");

            switch (readChoice()){
               case 1:   Query1(esql); break;
			   case 2:   Query2(esql); break;
			   case 3:   Query22(esql); break;
               case 4:   Query11(esql); break; // change
               case 5:   Query14(esql); break; // change
               case 6:   Query21(esql); break; 
			   case 7:   Query23(esql); break;
			   case 8:   Query24(esql); break; 
               case 0:   return false;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch

			return true;

	}
//---------------------------------------------------//


//---------------Broker Menu-------------------------//
	public static boolean brokerMenu(Travel4u esql) {

		System.out.println();
			System.out.println("-------------------------------------------------");
			System.out.println("BROKER MAIN MENU");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Create trip packages ");
            System.out.println("2. Delete trip packages ");
            System.out.println("3. Find trip packages "); 
            System.out.println("4. Find popular trip packages ");
			System.out.println("5. Your trip packages ");
	        System.out.println("6. LOL most popular trips ");
            System.out.println("0. < EXIT");

            switch (readChoice()){
               case 1:   Query17(esql); break;
               case 2:   Query18(esql); break;
               case 3:   Query20(esql); break;
               case 4:   Query17(esql); break; // change
			   case 5:   Query19(esql); break; 
               case 6:   Query25(esql); break; 
               case 0:   return false;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
			return true;

	}
//----------------------------------------------------------//


//-----------------------Agent-Menu-------------------------//
	public static boolean agentMenu(Travel4u esql) {

		System.out.println();
			System.out.println("-------------------------------------------------");
			System.out.println("AGENT MAIN MENU");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Find customer");
            System.out.println("2. Create new customer");
			System.out.println("3. Delete customer");
			System.out.println("4. Find trip order");
			System.out.println("5. Status of trip order");
			System.out.println("6. Create trip order");
			System.out.println("7. Delete trip order");
            System.out.println("0. < EXIT");

            switch (readChoice()){
               case 1:   Query3(esql); break;
               case 2:   Query5(esql); break;
			   case 3:   Query6(esql); break;
			   case 4:   Query16(esql); break;
			   case 5:   Query15(esql); break;
			   case 6:   Query7(esql); break;
			   case 7:   Query8(esql); break;               
			   case 0:   return false;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
			return true;
	}

/***************************************READ_USER_CHOICE***********************************/
   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try{ // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e){
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice


/*****************************************QUERIES***********************************************/

	// FIND EMPLOYEE - FINISHED
   public static void Query1(Travel4u esql){
     try{
        
          int temp = 0;
         

          System.out.print("Select a search option for Employee:");
          System.out.println ();
          System.out.print("1: by First Name");
          System.out.println ();
          System.out.print("2: by Last Name");
          System.out.println ();
          System.out.print("3: by Employee ID");
          System.out.println ();
		  System.out.print("4: by SSN");
          System.out.println ();
		  System.out.print("5: by User Name");
          System.out.println ();
		  System.out.print("6: by Address ID");
          System.out.println ();

          System.out.print("Please enter your choice: ");
          String temp2 = in.readLine();
          temp = Integer.parseInt(temp2);


          if(temp == 1)
          {
           String query = "SELECT * " +
                         "FROM employee e  " +
                         "WHERE e.first_name = '";
 
           System.out.println ();        
           System.out.print("Enter First Name: ");
             String input = in.readLine();
             query += input;
			 query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that name!");

          }


          else if(temp == 2)
          {
           String query = "SELECT * " +
                         "FROM employee e  " +
                         "WHERE e.last_name = '";
 
           System.out.println ();        
           System.out.print("Enter Last Name: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that last name!");

          }

        
          else if(temp == 3)
          {
           String query = "SELECT * " +
                         "FROM employee e  " +
                         "WHERE e.employeeid = ";
 
             System.out.println ();        
             System.out.print("Enter Employee ID: ");
             String input = in.readLine();
             query += input;
		
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that ID!");

          }

		else if(temp == 4)
          {
           String query = "SELECT * " +
                         "FROM employee e" +
                         "WHERE e.ssn = '";
 
           System.out.println ();        
           System.out.print("Enter SSN: ");
             String input = in.readLine();
             query += input;
			query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that SSN!");

          }

		else if(temp == 5)
          {
           String query = "SELECT * " +
                         "FROM employee e  " +
                         "WHERE e.user_name = '";
 
           System.out.println ();        
           System.out.print("Enter User Name: ");
             String input = in.readLine();
             query += input;
			query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that user name!");

          }

		else if(temp == 6)
          {
           String query = "SELECT * " +
                         "FROM employee e " +
                         "WHERE e.addressid = ";
 
           System.out.println ();        
           System.out.print("Enter Address ID: ");
           String input = in.readLine();
           query += input;
 
           int rowCount = esql.executeQuery (query);
           //System.out.println ("total row(s): " + rowCount);
           System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that address ID!");
          }

        else
          {
            
             System.out.println ("Error, wrong key entered!");
             System.out.println ();

          }
       
      }catch(Exception e){
         //System.err.println (e.getMessage ());
      }
   }
   
   
	// CREATE EMPLOYEE - FINISHED
   public static void Query2(Travel4u esql){
      try{

		String query = "INSERT INTO employee " +
                       "VALUES (";
		// ID
		System.out.println ();         
		System.out.print("Enter Employee ID: ");
        String input = in.readLine();
        query += input;

		query += ", '";

		// PASSWORD
		System.out.println ();         
		System.out.print("Enter Password: ");
        input = in.readLine();
        query += input;

		query += "', '";

		// FIRST_NAME
		System.out.println ();         
		System.out.print("Enter First Name: ");
         input = in.readLine();
        query += input;

		query += "', '";

		// LAST_NAME
		System.out.println ();         
		System.out.print("Enter Last Name: ");
        input = in.readLine();
        query += input;

		query += "', '";

		//SSN
		System.out.println ();         
		System.out.print("Enter SSN: ");
        input = in.readLine();
        query += input;

		query += "', '";

		// EMAIL
		System.out.println ();         
		System.out.print("Enter Email: ");
        input = in.readLine();
        query += input;

		query += "', '";

		// PHONE
		System.out.println ();         
		System.out.print("Enter Phone Number: ");
        input = in.readLine();
        query += input;

		query += "', '";

		// USER_NAME
		System.out.println ();         
		System.out.print("Enter User Name: ");
        input = in.readLine();
        query += input;

		query += "', ";

		// ADDRESS ID
		System.out.println ();         
		System.out.print("Enter Address ID: ");
        input = in.readLine();
        query += input;
		query += ", ";

		// SALARY ID
		System.out.println ();         
		System.out.print("Enter Salary Rate ID: ");
        input = in.readLine();
        query += input;
		query += ")";

		System.out.println(query);
        esql.executeUpdate(query); // used for INSERT INTO

		System.out.println("Employee created successfully!");
       	
        	
      }catch(Exception e){
         //System.err.println (e.getMessage ());
		System.out.println("One or more fields was entered incorrectly!");
      }
   }

	// FIND CUSTOMER ALL FIELDS - FINISHED
   public static void Query3(Travel4u esql) {
      try{
        
          int temp = 0;
         

          System.out.print("Select a search option for Customers:");
          System.out.println ();
          System.out.print("1: by First Name");
          System.out.println ();
          System.out.print("2: by Last Name");
          System.out.println ();
          System.out.print("3: by Email");
          System.out.println ();
		  System.out.print("4: by Phone");
          System.out.println ();
		  System.out.print("5: by User Name");
          System.out.println ();
		  System.out.print("6: by Address ID");
          System.out.println ();

          System.out.print("Please enter your choice: ");
          String temp2 = in.readLine();
          temp = Integer.parseInt(temp2);


          if(temp == 1)
          {
           String query = "SELECT * " +
                         "FROM customer c  " +
                         "WHERE c.first_name = '";
 
           System.out.println ();        
           System.out.print("Enter First Name: ");
             String input = in.readLine();
             query += input;
			 query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that name!");

          }


          else if(temp == 2)
          {
           String query = "SELECT * " +
                         "FROM customer c  " +
                         "WHERE c.last_name = '";
 
           System.out.println ();        
           System.out.print("Enter Last Name: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that last name!");

          }

        
          else if(temp == 3)
          {
           String query = "SELECT * " +
                         "FROM customer c  " +
                         "WHERE c.email = '";
 
           System.out.println ();        
           System.out.print("Enter Email: ");
             String input = in.readLine();
             query += input;
			query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that email!");

          }

		else if(temp == 4)
          {
           String query = "SELECT * " +
                         "FROM customer c " +
                         "WHERE c.phone = '";
 
           System.out.println ();        
           System.out.print("Enter Phone Number: ");
             String input = in.readLine();
             query += input;
			query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that phone number!");

          }

		else if(temp == 5)
          {
           String query = "SELECT * " +
                         "FROM customer c  " +
                         "WHERE c.user_name = '";
 
           System.out.println ();        
           System.out.print("Enter User Name: ");
             String input = in.readLine();
             query += input;
			query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that user name!");

          }

		else if(temp == 6)
          {
           String query = "SELECT * " +
                         "FROM customer c " +
                         "WHERE c.addressid = ";
 
           System.out.println ();        
           System.out.print("Enter Address ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No customers with that address ID!");

          }

        else
          {
            
             System.out.println ("Error, wrong key entered!");
             System.out.println ();

          }
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// FIND CUSTOMER: NAME - FINISHED
   public static void Query4(Travel4u esql){
      try{
		String query = "SELECT * " +
                       "FROM customer  " +
                       "WHERE first_name = '";

		System.out.println ();         
		System.out.print("Enter first name: ");
        String input = in.readLine();
        query += input;

		query += "' AND last_name = '";

		System.out.println ();         
		System.out.print("Enter last name: ");
        input = in.readLine();
        query += input;
		query += "'";

         int rowCount = esql.executeQuery (query);
        // System.out.println ("total row(s): " + rowCount);
         System.out.println ();
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// CREATE NEW CUSTOMER - FINISHED
	public static void Query5(Travel4u esql){
      try{
		String query = "INSERT INTO customer " +
                       "VALUES (";
		// ID
		System.out.println ();         
		System.out.print("Enter customer id: ");
        String input = in.readLine();
        query += input;

		query += ", '";

		// PASSWORD
		System.out.println ();         
		System.out.print("Enter password: ");
        input = in.readLine();
        query += input;
		query += "'";

		query += ", '";

		// FIRST_NAME
		System.out.println ();         
		System.out.print("Enter first name: ");
         input = in.readLine();
        query += input;
		query += "'";

		query += ", '";

		// LAST_NAME
		System.out.println ();         
		System.out.print("Enter last name: ");
        input = in.readLine();
        query += input;
		query += "'";

		query += ", '";

		// EMAIL
		System.out.println ();         
		System.out.print("Enter email: ");
        input = in.readLine();
        query += input;
		query += "'";

		query += ", '";

		// PHONE
		System.out.println ();         
		System.out.print("Enter phone number: ");
        input = in.readLine();
        query += input;
		query += "'";

		query += ", '";

		// USER_NAME
		System.out.println ();         
		System.out.print("Enter user name: ");
        input = in.readLine();
        query += input;
		query += "'";

		query += ", ";

		// ADDRESS ID
		System.out.println ();         
		System.out.print("Enter address id: ");
        input = in.readLine();
        query += input;
		query += ")";


        esql.executeUpdate(query); // used for INSERT INTO

		System.out.println("Customer created successfully!");
       	
        	
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// DELETE CUSTOMER - FINISHED
	public static void Query6( Travel4u esql ) {

		try {
			String query = "DELETE FROM customer WHERE customerid = ";
			System.out.println("Enter customer id: ");
			String input = in.readLine();
			query += input;
			
			esql.executeUpdate( query );
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}
	
	// CREATE ORDER - FINISHED
    public static void Query7(Travel4u esql){
      try{

		// ID
		String input;
		int rowCount = 0;

		String query = "SELECT * FROM travel_order";
		rowCount = esql.loginQuery(query);
		rowCount++;

        query = "INSERT INTO travel_order VALUES (" + rowCount + ", ";


        // ORDER CREATION DATE
        query += "now()";
        query += ", ";

 
        //GROUP SIZE
        System.out.println ();        
        System.out.print("Enter group size: ");
        input = in.readLine();
        query += input;

        query += ", ";

          
         
          // PRICE
        System.out.println ();        
        System.out.print("Enter price: ");
        input = in.readLine();
        query += input;
        query += ", ";

 
           // COMISSION
        System.out.println ();        
        System.out.print("Enter comission: ");
        input = in.readLine();
        query += input;

        query += ", TIMESTAMP '";

          //TRIP DATE START
          System.out.println ();        
        System.out.print("Enter trip date start: ");
        input = in.readLine();
        query += input;
        query += "'";

        query += ", TIMESTAMP '";

        //TRIP DATE END
        System.out.println ();        
        System.out.print("Enter trip date end: ");
        input = in.readLine();
        query += input;
        query += "'";

        query += ", ";

        //AGENT ID
        System.out.println ();        
        System.out.print("Enter Agent ID: ");
        input = in.readLine();
        query += input;


        query += ", ";

          //CUSTOMER ID
          System.out.println ();        
        System.out.print("Enter Customer ID: ");
        input = in.readLine();
        query += input;

        query += ", ";


          //STATUS ID
          System.out.println ();        
        System.out.print("Enter Trip Status ID: ");
        input = in.readLine();
        query += input;

		query += ")";
		//System.out.println(query);

         
        esql.executeUpdate(query); // used for INSERT INTO

        System.out.println("Trip order created!");
            
             
      }catch(Exception e){
         //System.err.println (e.getMessage ());
		System.out.println("One or more fields was entered incorrectly!");
	  }
	} 

	// DELETE ORDER
	public static void Query8( Travel4u esql ) {

		try {
			String query = "DELETE FROM travel_order WHERE orderid = ";
			System.out.print("Enter order id: ");
			String input = in.readLine();
			query += input;
			
			esql.executeUpdate( query );
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}

	// FIND Order: ID - FINISHED
   public static void Query9(Travel4u esql) {
      try{
		String query = "SELECT * " +
                       "FROM travel_order  " +
                       "WHERE orderid = ";

		System.out.println ();         
		System.out.print("Enter order id: ");
        String input = in.readLine();
        query += input;

         int rowCount = esql.executeQuery (query);
        // System.out.println ("total row(s): " + rowCount);
         System.out.println ();
     
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   // FIND Order: CustomerID - FINISHED
   public static void Query10(Travel4u esql) {
      try{
		String query = "SELECT * " +
                       "FROM customer, travel_order   " +
                       "WHERE customer.customerid = travel_order.customerid AND " +
					   "customer.customerid = ";

		System.out.println ();         
		System.out.print("Enter customer id: ");
        String input = in.readLine();
        query += input;

         int rowCount = esql.executeQuery (query);
        // System.out.println ("total row(s): " + rowCount);
         System.out.println ();
     
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   	// I DONT KNOW
   public static void Query11(Travel4u esql){
      
	} 
		

   // FIND Employee with Min Hours
   public static void Query12(Travel4u esql) {
      try{
		String query = "SELECT * " +
                       "FROM employee  " +
                       "WHERE EXISTS(SELECT * FROM salary, employee WHERE min(hours) <= hours " +
                       "AND employee.employeeid == salary.employeeid) ";

		System.out.println ();         
		System.out.print("Employee with min hours: ");
        

         int rowCount = esql.executeQuery (query);
         System.out.println ("total row(s): " + rowCount);
         System.out.println ();
     
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   // FIND Broker with max orders
   public static void Query13(Travel4u esql) {
      try{
		String query = "SELECT * " +
                       "FROM employee  " +
                       "WHERE EXISTS(SELECT * FROM employee e, agent a WHERE max(a.comision) >= comision " + 
					   "AND e.employeeid == a.employeeid) ";

          System.out.println ();         
		System.out.print("Most successful broker: ");

         int rowCount = esql.executeQuery (query);
         System.out.println ("total row(s): " + rowCount);
         System.out.println ();
     
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }


   	// FIND CUSTOMER ADDRESS BY ALL FIELDS - FINSHED
   public static void Query14(Travel4u esql){
      try{
        
          int temp = 0;
         

          System.out.print("Select a search option for Employees:");
          System.out.println ();
          System.out.print("1: by State");
          System.out.println ();
          System.out.print("2: by Zip Code");
          System.out.println ();
          System.out.print("3: by SSN");
          System.out.println ();
          System.out.print("Please enter your choice: ");
          String temp2 = in.readLine();
          temp = Integer.parseInt(temp2);


          if(temp == 1)
          {
           String query = "SELECT * " +
                         "FROM employee, address  " +
                         "WHERE employee.addressid = address.addressid AND state = '";
 
           System.out.println ();        
           System.out.print("Enter STATE Abbreviation: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees from this state!");

          }


          else if(temp == 2)
          {
           String query = "SELECT * " +
                         "FROM employee, address  " +
                         "WHERE employee.addressid = address.addressid AND zip = '";
 
           System.out.println ();        
           System.out.print("Enter Zip Code: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees from this zipcode!");

          }

        
          else if(temp == 3)
          {
           String query = "SELECT * " +
                         "FROM employee, address  " +
                         "WHERE employee.addressid = address.addressid AND ssn = '";
 
           System.out.println ();        
           System.out.print("Enter SSN: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No employees with that SSN!");

          }


        else
          {
            
             System.out.println ("Error, wrong key entered!");
             System.out.println ();

          }
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// FIND TRIP STATUS - FINISHED
   public static void Query15(Travel4u esql){
      try{
		String query = "SELECT o.orderid, o.creation_date, o.group_size, o.price, " +
					   "o.commission, o.trip_start_date, o.trip_end_date, o.agentid, " +
					   "t.description" +
                       "FROM travel_order o, trip_status t, employee e WHERE " +
					   "travel_order.trip_statusid = trip_status.statusid AND " +
					   "employee.user_name = '" + USERNAME + "' AND " +
					   "trabel_order.agentid = employee.employeeid";

		System.out.println();
         int rowCount = esql.executeQuery (query);
         //System.out.println ("total row(s): " + rowCount);
         System.out.println ();
		if( rowCount <= 0 ) System.out.println("No trip with that status ID!");
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



	// FIND TRAVEL ORDER BY ALL FIELDS - FINSHED
   public static void Query16(Travel4u esql){
      try{
        
          int temp = 0;
         

          System.out.print("Select a search option for Travel Orders:");
          System.out.println ();
          System.out.print("1: by Order ID");
          System.out.println ();
          System.out.print("2: by Creation Date");
          System.out.println ();
          System.out.print("3: by Group Size");
          System.out.println ();
		  System.out.print("4: by Price");
          System.out.println ();
		  System.out.print("5: by Commission");
          System.out.println ();
		  System.out.print("6: by Agent ID");
          System.out.println ();
		  System.out.print("7: by Customer ID");
          System.out.println ();

          System.out.print("Please enter your choice: ");
          String temp2 = in.readLine();
          temp = Integer.parseInt(temp2);


          if(temp == 1)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.orderid = ";
 
           System.out.println ();        
           System.out.print("Enter Order ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this ID!");

          }


          else if(temp == 2)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.creation_date = '";
 
           System.out.println ();        
           System.out.print("Enter Creation Date: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this creation date!");

          }

        
          else if(temp == 3)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.group_size = ";
 
           System.out.println ();        
           System.out.print("Enter Group Size: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this group size!");

          }

		else if(temp == 4)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.price = ";
 
           System.out.println ();        
           System.out.print("Enter Price: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this price!");

          }

		else if(temp == 5)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.commission = ";
 
           System.out.println ();        
           System.out.print("Enter Commission: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this commission!");

          }

		else if(temp == 6)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.agentid = ";
 
           System.out.println ();        
           System.out.print("Enter Agent ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this ID!");

          }

		else if(temp == 7)
          {
           String query = "SELECT * " +
                         "FROM travel_order o  " +
                         "WHERE o.customerid = ";
 
           System.out.println ();        
           System.out.print("Enter Customer ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No trips with this ID!");

          }

        else
          {
            
             System.out.println ("Error, wrong key entered!");
             System.out.println ();

          }
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// CREATE TRIP PACKAGES - FINISHED
	public static void Query17(Travel4u esql) {

		try{

		// ID
		String input;
		int rowCount = 0;

		String query = "SELECT * FROM travel_order";
		rowCount = esql.loginQuery(query);
		rowCount++;

        query = "INSERT INTO trip_package VALUES (" + rowCount + ", '";
      

        // Name
        System.out.println ();        
        System.out.print("Enter name: ");
        input = in.readLine();
        query += input;

        query += "', '";

          
         
        // Description
        System.out.println ();        
        System.out.print("Enter description: ");
        input = in.readLine();
        query += input;
        query += "', ";

 
        // Price
        System.out.println ();        
        System.out.print("Enter price: ");
        input = in.readLine();
        query += input;

        query += ", ";

        // TRIP Length
        System.out.println ();        
        System.out.print("Enter length: ");
        input = in.readLine();
        query += input;


        query += ",  '";

        // Availability
        System.out.println ();        
        System.out.print("Enter availability: ");
        input = in.readLine();
        query += input;

        query += "', ";

        //BROKER ID
        System.out.println ();        
        System.out.print("Enter Broker ID: ");
        input = in.readLine();
        query += input;


        query += ", ";

        // Suptervisor ID
        System.out.println ();        
        System.out.print("Enter Supervisor ID: ");
        input = in.readLine();
        query += input;

        query += ", ";


          // Type ID
          System.out.println ();        
        System.out.print("Enter Type ID: ");
        input = in.readLine();
        query += input;

		query += ")";
		//System.out.println(query);

		//System.out.println(query);
         
        esql.executeUpdate(query); // used for INSERT INTO

        System.out.println("Trip Package created!");
            
             
      }catch(Exception e){
         //System.err.println (e.getMessage ());
		System.out.println("One or more fields was entered incorrectly!");
	  }
	}

	// DELETE Trip Package - FINISHED
	public static void Query18( Travel4u esql ) {

		try {
			String query = "DELETE FROM trip_package WHERE packageid = ";
			System.out.print("Enter Package ID: ");
			String input = in.readLine();
			query += input;
			
			esql.executeUpdate( query );
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}
	
	// Broker trip packages - FINISHED
	public static void Query19(Travel4u esql) {
		try {
			String query = "Select * from trip_package t, employee e " +
						   "WHERE t.brokerid = e.employeeid AND e.user_name = '" + 
						   USERNAME + "'";
			
			int rowCount = esql.executeQuery( query );
			if( rowCount <= 0 ) 
				System.out.println("You do not have any Trip Packages!");
	
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}

	// FIND TRIP PACKAGES - FINISHED
   public static void Query20(Travel4u esql){
      try{

		  int temp = 0;

          System.out.print("Select a search option for Trip Packages:");
          System.out.println ();
          System.out.print("1: by Package ID");
          System.out.println ();
          System.out.print("2: by Name");
          System.out.println ();
          System.out.print("3: by Price");
          System.out.println ();
		  System.out.print("4: by Trip Length");
          System.out.println ();
		  System.out.print("5: by Availability");
          System.out.println ();
		  System.out.print("6: by Broker ID");
          System.out.println ();
		  System.out.print("7: by Supervisor ID");
          System.out.println ();
		  System.out.print("8: by Type ID");
          System.out.println ();

          System.out.print("Please enter your choice: ");
          String temp2 = in.readLine();
          temp = Integer.parseInt(temp2);


          if(temp == 1)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.packageid = ";
 
           System.out.println ();        
           System.out.print("Enter Package ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this ID!");

          }


          else if(temp == 2)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.name = '";
 
           System.out.println ();        
           System.out.print("Enter Name: ");
             String input = in.readLine();
             query += input;

             query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this name!");

          }

        
          else if(temp == 3)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.price = ";
 
           System.out.println ();        
           System.out.print("Enter Price: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this price!");

          }

		else if(temp == 4)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.trip_length = ";
 
           System.out.println ();        
           System.out.print("Enter Trip Length: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this length!");

          }

		else if(temp == 5)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.availability = '";
 
           System.out.println ();        
           System.out.print("Enter Availability: ");
             String input = in.readLine();
             query += input;
			 query += "'";
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this availability!");

          }

		else if(temp == 6)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.brokerid = ";
 
           System.out.println ();        
           System.out.print("Enter Broker ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this ID!");

          }

		else if(temp == 7)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.supervisorid = ";
 
           System.out.println ();        
           System.out.print("Enter Supervisor ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this ID!");

          }

		  else if(temp == 8)
          {
           String query = "SELECT * " +
                         "FROM trip_package p  " +
                         "WHERE p.typeid = ";
 
           System.out.println ();        
           System.out.print("Enter Type ID: ");
             String input = in.readLine();
             query += input;
 
             int rowCount = esql.executeQuery (query);
             //System.out.println ("total row(s): " + rowCount);
             System.out.println ();

			if( rowCount <= 0 )
				System.out.println("No packages with this ID!");

          }

        else
          {
            
             System.out.println ("Error, wrong key entered!");
             System.out.println ();

          }
       
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

	// MANAGER trip packages - FINISHED
	public static void Query21(Travel4u esql) {
		try {
			String query = "Select * from trip_package t, employee e " +
						   "WHERE t.supervisorid = e.employeeid AND e.user_name = '" + 
						   USERNAME + "'";
			
			int rowCount = esql.executeQuery( query );
			if( rowCount <= 0 ) 
				System.out.println("You do not have any Trip Packages!");
	
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}

	// DELETE EMPLOYEE - FINISHED
	public static void Query22( Travel4u esql ) {

		try {
			String query = "DELETE FROM employee WHERE employeeid = ";
			System.out.print("Enter Employee ID: ");
			String input = in.readLine();
			query += input;
			
			esql.executeUpdate( query );
			System.out.println("Employee successfully deleteed!");
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}

	// FAVORED CUSTOMERS - FINISHED
	public static void Query23( Travel4u esql ) {
		try{
			String query = "SELECT c.customerid, c.first_name, c.last_name " +
						   "FROM customer c, travel_order t " +
						   "WHERE c.customerid = t.customerid AND t.price >= ALL(SELECT price FROM travel_order)";
			
			//System.out.println(query);
			System.out.println();
			esql.executeQuery(query);
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}


    // PRODUCTIVE AGENT - FINISHED
	public static void Query24( Travel4u esql ) {
		try{
			String query = "SELECT e.first_name, e.last_name, e.employeeid " +
						   "FROM employee e, agent a, travel_order o " +
						   "WHERE o.agentid = a.employeeid AND a.employeeid = e.employeeid AND " +
						   "o.price >= ALL( SELECT price FROM travel_order)";

			//System.out.println(query);
			System.out.println();
			esql.executeQuery(query);
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}


    //MOST SUCCESSFUL PACKAGE - FINISHED??
    // PRODUCTIVE AGENT - FINISHED
	public static void Query25( Travel4u esql ) {
		try{
			String query = "select packageid, count(packageid) " +
            "FROM order_packages  GROUP BY packageid having count(packageid)=(Select " +
            "MAX(GEKAS.CNT) FROM (Select count(packageid) as CNT FROM order_packages GROUP BY (packageid)) AS GEKAS)";

			//System.out.println(query);
			System.out.println();
			esql.executeQuery(query);
		}
		catch( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}

	//SALARY

// Create data for salary_rate
   
}//end Travel4u


