import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

// TO RUN:
// compile with 'javac *.java'
// run with 'java -cp ".;postgresql-42.2.8.jar" GUI'

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    static JFrame manager_frame;
    static JFrame employee_frame;
    static Integer num_drinks = 0;
    static double total_cost = 0.0;
    static boolean paid = false;

    // drink list per order
    static ArrayList<String> order_drinks = new ArrayList<>();

    static ArrayList<String> selected_items = new ArrayList<>();

    // all customizations per order
    static ArrayList<String> order_customizations = new ArrayList<>();

    // drink names
    static ArrayList<String> drink_names = null;


    public static void main(String[] args)
    {
      //Building the connection
      Connection conn = null;
      //TODO STEP 1
      try {
        conn = DriverManager.getConnection(
          "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_08r_db",
          "csce315_971_cevancura",
          "password");
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
      JOptionPane.showMessageDialog(null,"Opened database successfully");

      String name = "";
      /*try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        //TODO Step 2
        String sql_statement = "SELECT * FROM drink_dictionary;";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sql_statement);
        while (result.next()) {
          name += result.getString("name")+"\n";
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }*/
      // create a new frame
      f = new JFrame("DB GUI");

      manager_frame = new JFrame("Manager GUI");
      employee_frame = new JFrame("Employee GUI");

      // create a object
      GUI s = new GUI();

      // create a panel
      JPanel p = new JPanel();

      JButton b = new JButton("Close");

      JButton manager = new JButton("Manager");
      JButton employee = new JButton("Employee");

      // add actionlistener to button
      b.addActionListener(s);

      manager.addActionListener(s);
      employee.addActionListener(s);

      //TODO Step 3 
      JTextArea text = new JTextArea(name);

      //TODO Step 4
      p.add(text);

      // add button to panel
      p.add(b);

      p.add(manager);
      p.add(employee);

      // add panel to frame
      f.add(p);

      // set the size of frame
      f.setSize(400, 400);

      f.setVisible(true);

      employee_frame.setSize(800, 800);

      JPanel p_emplo = new JPanel(new GridLayout(2, 4));

      JButton milk_tea = new JButton("Milk Tea");
      JButton brewed_tea = new JButton("Brewed Tea");
      JButton fruit_tea = new JButton("Fruit Tea");
      JButton fresh_milk = new JButton("Fresh Milk");
      JButton ice_blended = new JButton("Ice Blended");
      JButton tea_mojito = new JButton("Tea Mojito");
      JButton creama = new JButton("Creama");
      // JButton customizations = new JButton("Customizations");
      JButton employee_exit = new JButton("Employee Exit");
      employee_exit.setBackground(Color.GRAY);
      employee_exit.setOpaque(true);

      JButton cancel_order = new JButton("Cancel Order");
      cancel_order.setBackground(Color.RED);
      cancel_order.setOpaque(true);

      JButton order = new JButton("View Order");
      order.setBackground(Color.GREEN);
      order.setOpaque(true);
      // order.setBorderPainted(false);

      p_emplo.add(milk_tea);
      p_emplo.add(brewed_tea);
      p_emplo.add(fruit_tea);
      p_emplo.add(fresh_milk);
      p_emplo.add(ice_blended);
      p_emplo.add(tea_mojito);
      p_emplo.add(creama);
      // p_emplo.add(customizations);
      p_emplo.add(employee_exit);
      p_emplo.add(cancel_order);
      p_emplo.add(order);

      milk_tea.addActionListener(s);
      brewed_tea.addActionListener(s);
      fruit_tea.addActionListener(s);
      fresh_milk.addActionListener(s);
      ice_blended.addActionListener(s);
      tea_mojito.addActionListener(s);
      creama.addActionListener(s);
      // customizations.addActionListener(s);
      employee_exit.addActionListener(s);
      cancel_order.addActionListener(s);
      order.addActionListener(s);

      employee_frame.add(p_emplo);


      // update drink array
      try {
        drink_names = getDrinkNamesTable(conn);
      }
      catch (IOException error1) {
        error1.printStackTrace();
      }

      // do not close connection until done with all orders
      // FIX ME (currently set to after close is clicked)
      while (f.isDisplayable()) {
        if (paid) {
          // get current order number (next after max)
          String prev_order_id_str = "";
          Integer current_order_id_int = 0;
          String current_order_id_str = "";
          try{
            //create a statement object
            Statement stmt = conn.createStatement();
            //create a SQL statement
            String sql_statement = "SELECT MAX(order_id) FROM order_history;";
            //send statement to DBMS
            ResultSet result = stmt.executeQuery(sql_statement);
            if (result.next()) {
              prev_order_id_str += result.getString("max");
            }
            try {
              current_order_id_int = Integer.parseInt(prev_order_id_str) + 1;
              current_order_id_str = String.valueOf(current_order_id_int);
            }
            catch (NumberFormatException e) {}
          } catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }

          // get and format date and time
          LocalDate current_date = LocalDate.now();
          LocalTime current_time = LocalTime.now();
          DateTimeFormatter date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
          DateTimeFormatter time_format = DateTimeFormatter.ofPattern("HH:mm:ss");
          String formatted_date = current_date.format(date_format);
          String formatted_time = current_time.format(time_format);

          // drink codes for drinks 1-10 (0000 if none)
          fillIDList(10);

          // full order string
          String order_str = "('" + current_order_id_str + "', '" + formatted_date + "', '" + formatted_time + "', '" + String.valueOf(num_drinks) + "', '" + String.valueOf(total_cost);
          for (String id : order_drinks) {
            order_str += "', '" + id;
          }
          order_str += "');";

          // write order
          System.out.println(order_str);

          try{
            //create a statement object
            Statement stmt = conn.createStatement();
            //create a SQL statement
            String sql_statement = "INSERT INTO order_history VALUES " + order_str;
            //send statement to DBMS
            stmt.execute(sql_statement);
          } catch (Exception e){
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }

          // update inventory
          updateInventory(conn);

          // update paid
          paid = false;
          // reset values
          num_drinks = 0;
          total_cost = 0.0;
          order_drinks.clear();
          selected_items.clear();
          order_customizations.clear();
        }
      }

      //closing the connection
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    // make customization window
    public static JFrame customizationWindow() throws IOException {
      // Create a new frame for Customization options
      JFrame customizations_frame = new JFrame("Customization Options");
      customizations_frame.setSize(800, 800);
      JPanel customization_sub_menu = new JPanel(new GridLayout(4, 4));
  
      ArrayList<String> customization_names = null;

      ArrayList<String> current_customizations = new ArrayList<>();

      try {
          customization_names = getCustomizationNames("customs.csv");
      } catch (IOException error1) {
          error1.printStackTrace();
      }
  
      for (int i = 1; i < customization_names.size(); i++) {
          String customization = customization_names.get(i);
          JButton custom = new JButton(customization);
          customization_sub_menu.add(custom);


          // check if clicked
          custom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  // Extract the text from the clicked button
                  String selected_item = custom.getText();

                  if (current_customizations.contains(selected_item)) {
                    // if currently selected, deselect
                    current_customizations.remove(selected_item);
                    custom.setBackground(null);
                  }
                  else {
                    current_customizations.add(selected_item);
                    custom.setBackground(Color.BLUE);
                    custom.setOpaque(true);
                    custom.setBorderPainted(false);
                  }

                  // Add it to the ArrayList
                  // selected_items.add(selected_item);
                  // // change color 
                  // custom.setBackground(Color.BLUE);

                  // NOTE: ADD CUSTOMIZATIONS IN A CHECKBOX, FOR EVERY BOX THAT IS CHECKED, 
                  // ADD THAT TO selected_items AND ADD THAT TO THE ORDER
                }
            });
      }

      // continue button 
      // JPanel continueSubMenu = new JPanel();
      JButton continue_button = new JButton("Continue");
      continue_button.setBackground(Color.GREEN);
      continue_button.setOpaque(true);
      continue_button.setBorderPainted(false);
      // continueSubMenu.add(continue_button);
      customization_sub_menu.add(continue_button);

      continue_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String s = e.getActionCommand();
          if (s == "Continue") {
            // once continue is clicked add all current customizations to order
            for (String custom : current_customizations) {
              selected_items.add(custom);
              order_customizations.add(custom);
              // update total cost
              try {
                total_cost += getCustomizationCost("customs.csv", custom);
              }
              catch (IOException error1) {
                error1.printStackTrace();
              }
            }
            // and close frame
            customizations_frame.dispose();
          }
        }
      });
      
      // customizations_frame.add(continueSubMenu);
  
      // Add the submenu panel to the customizations_frame
      customizations_frame.add(customization_sub_menu);
  
      // // Make the new frame visible
      // customizations_frame.setVisible(true);

      return customizations_frame;
    }

    public static void typeWindow(String drink_type, int size_x, int size_y) throws IOException {
      // Create a new frame for type options
      JFrame outside_frame = new JFrame(drink_type + " Options");
      outside_frame.setSize(800, 800);
      JPanel sub_menu = new JPanel(new GridLayout(size_x, size_y));

      for (String drink : drink_names) {
        // if the right type
        if (drink.length() >= drink_type.length() && drink.substring(0, drink_type.length()).equals(drink_type)) {
          JButton mt = new JButton(drink);
          mt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // Extract the text from the clicked button
              String selected_item = mt.getText();
              // Add it to the ArrayList
              selected_items.add(selected_item);

              // add to number of drinks and total cost
              num_drinks += 1;
              try {
                  total_cost += getDrinkCost("drink_dictionary.csv", drink);
              }
              catch (IOException error1) {
                error1.printStackTrace();
              }
              try {
                order_drinks.add(getDrinkID("drink_dictionary.csv", drink));
              }
              catch (IOException error1) {
                error1.printStackTrace();
              }
              

              // Close the outside_frame
              outside_frame.dispose();

              // Open the new frame here (e.g., a new options frame)
              JFrame customs_frame = new JFrame("Customizations");
              try {
                customs_frame = customizationWindow();
              } catch (IOException error1) {
                  error1.printStackTrace();
              }

              customs_frame.setSize(800, 800);
              customs_frame.setVisible(true);
            }
          });
          sub_menu.add(mt);
        }
      }

      // add sub_menu to frame and make visible
      outside_frame.add(sub_menu);
      outside_frame.setVisible(true);
    }

    public static void payWindow() {
      JFrame outside_frame = new JFrame("Payment Processed");
      outside_frame.setSize(400, 400);
      JPanel buttonSubMenu = new JPanel(new GridLayout(0,2));

      JButton employee_exit = new JButton("Employee Exit");
      JButton another_order = new JButton("Another Order");

      employee_exit.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Close the outside_frame and employee frame
          outside_frame.dispose();
          employee_frame.dispose();
        }
      });

      another_order.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Close the outside_frame
          outside_frame.dispose();
        }
      });

      buttonSubMenu.add(employee_exit);
      buttonSubMenu.add(another_order);

      outside_frame.add(buttonSubMenu);
      outside_frame.setVisible(true);
    }

    public static void cancelWindow() {
      JFrame outside_frame = new JFrame("Cancelled Order");
      outside_frame.setSize(400, 400);
      JPanel cancelSubMenu = new JPanel(new BorderLayout());

      JTextArea cancel_text = new JTextArea("Order has been cancelled.");
      cancel_text.setEditable(false);

      JButton exit_button = new JButton("Exit");

      exit_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Close the outside_frame and employee frame
          outside_frame.dispose();
        }
      });

      cancelSubMenu.add(cancel_text);
      cancelSubMenu.add(exit_button, BorderLayout.PAGE_END);

      outside_frame.add(cancelSubMenu);

      outside_frame.setVisible(true);
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            f.dispose();
        }
        else if (s.equals("Manager")) {
          manager_frame.setVisible(true);
        }
        else if (s.equals("Employee")) {
          employee_frame.setVisible(true);
        }
        if (s.equals("View Order")) {
          JFrame orderFrame = new JFrame("Viewing Order");
          orderFrame.setSize(400,400);
          
          JPanel totalSubMenu = new JPanel(new BorderLayout());
          JPanel buttonSubMenu = new JPanel(new GridLayout(0,3));
          JPanel orderSubMenu = new JPanel(new GridLayout(0, 2));

          
          JTextArea order_text = new JTextArea();
          order_text.setEditable(false);

          JTextArea prices_text = new JTextArea();
          prices_text.setEditable(false);

          int index = 0;
          for (String selected_item : selected_items) {
            if (index == 0) {
              order_text.append(selected_item);
              try {
                prices_text.append(String.valueOf(getDrinkCost("drink_dictionary.csv", selected_item)));
              }
              catch (IOException error1) {
                error1.printStackTrace();
              }
            }
            else {
              if (drink_names.contains(selected_item)) {
                order_text.append("\n\n");
                prices_text.append("\n\n");
                try {
                  prices_text.append(String.valueOf(getDrinkCost("drink_dictionary.csv", selected_item)));
                }
                catch (IOException error1) {
                  error1.printStackTrace();
                }
              }
              else {
                order_text.append("\n");
                prices_text.append("\n");
                try {
                  prices_text.append(String.valueOf(getCustomizationCost("customs.csv", selected_item)));
                }
                catch (IOException error1) {
                  error1.printStackTrace();
                }
              }
              order_text.append(selected_item);
              
            }
            index++;
          }

          order_text.append("\n\n\n\tTotal Price");
          prices_text.append("\n\n\n" + String.valueOf(total_cost));

          // add buttons
          JButton more_drinks = new JButton("Add More Drinks");
          JButton cancel_order = new JButton("Cancel Order");
          JButton finish_and_pay = new JButton("Finish and Pay");

          // check if clicked
          more_drinks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              String s = e.getActionCommand();
              if (s == "Add More Drinks") {
                orderFrame.dispose();
              }
            }
          }); 
          cancel_order.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              String s = e.getActionCommand();
              if (s == "Cancel Order") {
                // reset values
                num_drinks = 0;
                total_cost = 0.0;
                order_drinks.clear();
                selected_items.clear();
                order_customizations.clear();

                orderFrame.dispose();
                // output window acknowledging cancelled order
                cancelWindow();
              }
            }
          });
          finish_and_pay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              String s = e.getActionCommand();
              if (s == "Finish and Pay") {
                // close order frame
                paid = true;
                orderFrame.dispose();

                payWindow();
              }
            }
          }); 
          
          orderSubMenu.add(order_text);
          orderSubMenu.add(prices_text);

          JScrollPane scrollable_order = new JScrollPane(orderSubMenu);  
          scrollable_order.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
  
          // orderFrame.getContentPane().add(scrollable_order);

          buttonSubMenu.add(more_drinks);
          buttonSubMenu.add(cancel_order);
          buttonSubMenu.add(finish_and_pay);

          totalSubMenu.add(scrollable_order);
          // totalSubMenu.add(orderSubMenu);
          totalSubMenu.add(buttonSubMenu, BorderLayout.PAGE_END);

          // orderFrame.add(orderSubMenu);

          orderFrame.add(totalSubMenu);
          // orderFrame.add(scrollable_order);

          orderFrame.setVisible(true);
        }
        if (s.equals("Milk Tea")) {
          // Create a new frame for Milk Tea options
          try {
            typeWindow("Milk Tea", 4, 4);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Brewed Tea")) {
          // Create a new frame for Brewed Tea options
          try {
            typeWindow("Brewed Tea", 2, 4);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Fruit Tea")) {
          // Create a new frame for Fruit Tea options
          try {
            typeWindow("Fruit Tea", 3, 4);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Fresh Milk")) {
          // Create a new frame for Fresh Milk options
          try {
            typeWindow("Fresh Milk", 3, 3);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Ice Blended")) {
          // Create a new frame for Ice Blended options
          try {
            typeWindow("Ice Blended", 3, 3);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Tea Mojito")) {
          // Create a new frame for Mojito options
          try {
            typeWindow("Mojito", 2, 2);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }
        if (s.equals("Creama")) {
          // Create a new frame for Creama options
          try {
            typeWindow("Creama", 2, 4);
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
        }

        if (s.equals("Customizations")) {
          // Create a new frame for Customization options
          JFrame customizations_frame = new JFrame("Customization Options");
          customizations_frame.setSize(800, 800);
          JPanel customization_sub_menu = new JPanel(new GridLayout(4, 4));
      
          ArrayList<String> customization_names = null;
          try {
              customization_names = getCustomizationNames("customs.csv");
          } catch (IOException error1) {
              error1.printStackTrace();
          }
      
          for (int i = 1; i < customization_names.size(); i++) {
              String customization = customization_names.get(i);
              JButton custom = new JButton(customization);
              customization_sub_menu.add(custom);
          }
      
          // Add the submenu panel to the customizations_frame
          customizations_frame.add(customization_sub_menu);
      
          // Make the new frame visible
          customizations_frame.setVisible(true);
        }
        if (s.equals("Employee Exit")) {
          // reset values
          num_drinks = 0;
          total_cost = 0.0;
          order_drinks.clear();
          selected_items.clear();
          order_customizations.clear();
          // cancel order
          cancelWindow();
          // exit employee
          employee_frame.dispose();
        }
        if (s.equals("Cancel Order")) {
          // reset values
          num_drinks = 0;
          total_cost = 0.0;
          order_drinks.clear();
          selected_items.clear();
          order_customizations.clear();

          // output window acknowledging cancelled order
          cancelWindow();
        }

      
 
    }
    
    public static void fillIDList(int maxDrinks) {
      while (order_drinks.size() < maxDrinks) {
        order_drinks.add("0000");
      }
    }

    public static ArrayList<String> getDrinkNames(String filePath) throws IOException {
      ArrayList<String> drink_names = new ArrayList<>();
      File file = new File(filePath);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String drinkName = parts[1].trim();
          drink_names.add(drinkName);
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drink_names;
    }

    public static ArrayList<String> getDrinkNamesTable(Connection conn) throws IOException {
      ArrayList<String> drink_names = new ArrayList<>();

      //create a statement object
      try {
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sql_statement = "SELECT * FROM drink_dictionary ORDER BY drink_id asc;";
        //send statement to DBMS

        ResultSet result = stmt.executeQuery(sql_statement);
        while (result.next()) {
          drink_names.add(result.getString("name"));
        }
      } catch (Exception e){
        System.out.println(e.toString());
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      return drink_names;
    }

    public static double getDrinkCost(String filePath, String drinkName) throws IOException {
      double drinkCost = 0;
      File file = new File(filePath);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 3) {
          String currentDrink = parts[1].trim();
          String currentCost = parts[2].trim();

          if (currentDrink.equals(drinkName)) {
            drinkCost = Double.valueOf(currentCost);
          }
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drinkCost;
    }

    public static String getDrinkID(String filePath, String drinkName) throws IOException {
      String drinkID = "0000";
      File file = new File(filePath);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String currentDrink = parts[1].trim();
          String currentID = parts[0].trim();

          if (currentDrink.equals(drinkName)) {
            drinkID = currentID;
          }
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drinkID;
    }

    public static ArrayList<String> getCustomizationNames(String filePath) throws IOException {
      ArrayList<String> customization_names = new ArrayList<>();
      File file = new File(filePath);
  
      Scanner scanner = new Scanner(file);
  
      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] parts = line.split(",");
          if (parts.length >= 3) {
              String customizationName = parts[2].trim();
              customization_names.add(customizationName);
          }
      }
  
      scanner.close(); // Close the scanner explicitly.
  
      return customization_names;
    }

    public static double getCustomizationCost(String filePath, String customName) throws IOException {
      double customCost = 0;
      File file = new File(filePath);
  
      Scanner scanner = new Scanner(file);
  
      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] parts = line.split(",");
          if (parts.length >= 4) {
              String currentCustom = parts[2].trim();
              String currentCost = parts[3].trim();

              if (currentCustom.equals(customName)) {
                customCost = Double.valueOf(currentCost);
              }
          }
      }
  
      scanner.close(); // Close the scanner explicitly.
  
      return customCost;
    }

    public static void updateInventory(Connection conn) {
      // for each item in inventory find current amount
      ArrayList<ArrayList<String>> inventory_list = new ArrayList<ArrayList<String>>();

      //create a statement object
      try {
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sql_statement = "SELECT * FROM inventory ORDER BY product_id asc;";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sql_statement);
        while (result.next()) {
          ArrayList<String> single_item = new ArrayList<String>();

          single_item.add(result.getString("product_id"));
          single_item.add(result.getString("total_amount"));
          single_item.add(result.getString("current_amount"));

          // make list of lists with all id, total, and current amount included for each item
          inventory_list.add(single_item);
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      // update values in inventory list
      // 500001-500021 are drink types
      // 500022-500031 are add-ins
      // 600001-600008 are misc, often used in every drink

      for (String id : order_drinks) {
        if (id.equals("0000")) {
          // no update, null value
          continue;
        }
        if (id.equals("0001")) {
          // milk tea classic black
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // black tea
            if (item.contains("500001")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0002")) {
          // milk tea classic green
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0003")) {
          // milk tea classic oolong
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // oolong tea
            if (item.contains("500003")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0004")) {
          // milk tea honey black
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // black tea
            if (item.contains("500001")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0005")) {
          // milk tea honey green
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0006")) {
          // milk tea honey oolong
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // oolong tea
            if (item.contains("500003")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0007")) {
          // milk tea classic coffee
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // coffee
            if (item.contains("500004")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0008")) {
          // milk tea ginger
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // ginger
            if (item.contains("500005")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0009")) {
          // milk tea hokkaido
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // oolong tea
            if (item.contains("500006")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0010")) {
          // milk tea okinawa
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // okinawa tea
            if (item.contains("500007")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0011")) {
          // milk tea thai
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // thai tea
            if (item.contains("500008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0012")) {
          // milk tea taro
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // taro
            if (item.contains("500009")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0013")) {
          // milk tea mango green
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // mango
            if (item.contains("500010")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0014")) {
          // milk tea QQ Happy Family
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // tea
            if (item.contains("500011")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0015")) {
          // milk tea matcha red bean
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // matcha
            if (item.contains("500012")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // red bean
            if (item.contains("500026")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0016")) {
          // brewed tea classic black
          for (ArrayList<String> item : inventory_list) {
            // black tea
            if (item.contains("500001")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0017")) {
          // brewed tea classic green
          for (ArrayList<String> item : inventory_list) {
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0018")) {
          // brewed tea classic oolong
          for (ArrayList<String> item : inventory_list) {
            // oolong tea
            if (item.contains("500003")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0019")) {
          // brewed tea wintermelon
          for (ArrayList<String> item : inventory_list) {
            // wintermelon
            if (item.contains("500013")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0020")) {
          // brewed tea honey black
          for (ArrayList<String> item : inventory_list) {
            // black tea
            if (item.contains("500001")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0021")) {
          // brewed tea honey green
          for (ArrayList<String> item : inventory_list) {
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0022")) {
          // brewed tea honey oolong
          for (ArrayList<String> item : inventory_list) {
            // oolong tea
            if (item.contains("500003")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0023")) {
          // brewed tea ginger
          for (ArrayList<String> item : inventory_list) {
            // ginger
            if (item.contains("500005")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0024")) {
          // fruit tea mango green
          for (ArrayList<String> item : inventory_list) {
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // mango
            if (item.contains("500010")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0025")) {
          // fruit tea wintermelon lemonade
          for (ArrayList<String> item : inventory_list) {
            // wintermelon
            if (item.contains("500013")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }            
        if (id.equals("0026")) {
          // fruit tea strawberry
          for (ArrayList<String> item : inventory_list) {
            // strawberry
            if (item.contains("500015")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }      
        if (id.equals("0027")) {
          // fruit tea peach
          for (ArrayList<String> item : inventory_list) {
            // peach
            if (item.contains("500016")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }    
        if (id.equals("0028")) {
          // fruit tea peach kiwi
          for (ArrayList<String> item : inventory_list) {
            // peach
            if (item.contains("500016")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // kiwi
            if (item.contains("500017")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0029")) {
          // fruit tea kiwi
          for (ArrayList<String> item : inventory_list) {
            // kiwi
            if (item.contains("500017")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }  
        if (id.equals("0030")) {
          // fruit tea mango and passionfruit
          for (ArrayList<String> item : inventory_list) {
            // mango
            if (item.contains("500010")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // passionfruit
            if (item.contains("500018")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }  
        if (id.equals("0031")) {
          // fruit tea tropical fruit
          for (ArrayList<String> item : inventory_list) {
            // tropical fruit
            if (item.contains("500019")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }     
        if (id.equals("0032")) {
          // fruit tea hawaii fruit
          for (ArrayList<String> item : inventory_list) {
            // hawaii fruit
            if (item.contains("500020")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }   
        if (id.equals("0033")) {
          // fruit tea passionfruit orange and grapefruit
          for (ArrayList<String> item : inventory_list) {
            // passionfruit
            if (item.contains("500018")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // orange and grapefruit
            if (item.contains("500021")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }   
        if (id.equals("0034")) {
          // fresh milk
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0035")) {
          // fresh milk classic black
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // black tea
            if (item.contains("500001")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0036")) {
          // fresh milk classic green
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // green tea
            if (item.contains("500002")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0037")) {
          // fresh milk classic oolong
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // oolong tea
            if (item.contains("500003")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0038")) {
          // fresh milk tea wintermelon
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // wintermelon
            if (item.contains("500013")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0039")) {
          // fresh milk cocoa lover
          for (ArrayList<String> item : inventory_list) {
            // fresh milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0040")) {
          // fresh milk QQ Happy Family
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // tea
            if (item.contains("500011")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0041")) {
          // fresh milk milk tea matcha
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // matcha
            if (item.contains("500012")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (id.equals("0042")) {
          // fresh milk taro
          for (ArrayList<String> item : inventory_list) {
            // milk
            if (item.contains("600008")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
            // taro
            if (item.contains("500009")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
      }

      // found in every drink (cups, straws, napkins)
      for (ArrayList<String> item : inventory_list) {
        // cups
        if (item.contains("600001")) {
          item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - num_drinks));
        }
        // straws
        if (item.contains("600003")) {
          item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - num_drinks));
        }
        // napkins
        if (item.contains("600005")) {
          item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - num_drinks));
        }
      }

      // all customizations
      for (String customization : order_customizations) {
        // customizations array is off in ids
        if (customization.equals("pearl")) {
          // pearl
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500022")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("mini pearl")) {
          // mini pearl
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500025")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("ice cream")) {
          // ice cream
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500028")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("pudding")) {
          // pudding
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500031")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("aloe vera")) {
          // aloe vera
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500023")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("red bean")) {
          // red bean
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500026")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("herb jelly")) {
          // herb jelly
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500029")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("aiyu jelly")) {
          // aiyu jelly
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500030")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("lychee jelly")) {
          // lychee jelly
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500024")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("creama")) {
          // creama
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("500027")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("regular ice")) {
          // regular ice
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600006")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 2));
            }
          }
        }
        if (customization.equals("less ice")) {
          // less ice
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600006")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
        if (customization.equals("normal sweet")) {
          // normal sweet
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600007")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 4));
            }
          }
        }
        if (customization.equals("less sweet")) {
          // less sweet
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600007")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 3));
            }
          }
        }
        if (customization.equals("half sweet")) {
          // half sweet
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600007")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 2));
            }
          }
        }
        if (customization.equals("light sweet")) {
          // light sweet
          for (ArrayList<String> item : inventory_list) {
            if (item.contains("600007")) {
              item.set(2, String.valueOf(Integer.valueOf(item.get(2)) - 1));
            }
          }
        }
      }

      // update values
      for (ArrayList<String> item : inventory_list) {
        //create a SQL statement
        String sql_statement = "UPDATE inventory";
        sql_statement += " SET current_amount = ";
        sql_statement += item.get(2);
        sql_statement += " WHERE product_id = ";
        sql_statement += item.get(0);
        sql_statement += ";";

        try{
          //create a statement object
          Statement stmt = conn.createStatement();
          //send statement to DBMS
          stmt.execute(sql_statement);
        } catch (Exception e){
          JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
      }
    }
  
}
