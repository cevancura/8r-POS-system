import java.sql.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;

// TO RUN::
// compile with 'javac *.java'
// run with 'java -cp ".;postgresql-42.2.8.jar" GUI'

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    static JFrame manager_frame;
    static JFrame employee_frame;
    static JFrame inventory_frame;    static Integer num_drinks = 0;
    static double total_cost = 0.0;
    static boolean paid = false;

    // drink list per order
    static ArrayList<String> order_drinks = new ArrayList<>();

    static ArrayList<String> selected_items = new ArrayList<>();

    // all customizations per order
    static ArrayList<String> order_customizations = new ArrayList<>();

    // drink names
    static ArrayList<String> drink_names = null;


    static JFrame drinks_frame;
    static JFrame reports_frame;
    static JTextField text_input;
    static JTextArea text_output;
    static JTextField text_input_inventory;
    static JTextArea text_output_inventory;
    static JTextField update_text_input;
    static JTextArea update_text_output;
    static JTextField update_input_inventory;
    static JTextArea update_output_inventory;
    static JTextArea out;
    static JTable table_menu;
    static JTable table_inventory;
    static JFrame add_menu;
    static JFrame add_inventory;
    static JFrame update_menu;
    static JFrame update_inventory;
    static JPanel p_inventory;
    static JPanel p_menu;
    static JPanel p_reports;
    static Boolean menu_check = false;
    static Boolean inventory_check = false;

    public static Connection createConnection() {
      Connection conn = null;
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
      return conn;
    }
    public static void closeConnection(Connection conn) {
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    public static void implementButton(String button_name, JPanel panel, GUI s) {
      JButton button = new JButton(button_name);
      panel.add(button);
      button.addActionListener(s);
    }

    public static String getDrinkName(String[] splitted, int formatting) {
      int splitted_length = splitted.length;
      String drink_name = "";
      for (int i = 1; i < splitted_length - formatting; ++i) {
          drink_name += splitted[i];

          if (i != splitted_length - formatting - 1){

              drink_name+= " ";

          }
      }
      return drink_name;
    }


    public static void dataFeature(JTextField text_in, JTextArea text_out, Connection conn, Boolean is_add, Boolean is_menu) {
      text_out.setText(text_in.getText());
      text_in.setText("enter the text");
      String update = "";

      String text = "Inventory";
      if (is_menu) {text = "Menu";}
      int formatting = 3;
      if (is_menu) {formatting = 1;}

      if (!(text_out.getText().equals("") || text_out.getText().equals("enter the text"))) {
        try{
            
            Statement stmt = conn.createStatement();
            String[] splitted = text_out.getText().split("\\s+");
            
            if (is_add) {
              if (is_menu) { update = "INSERT INTO drink_dictionary (drink_id, name, price) VALUES";}
              else {update = "INSERT INTO inventory (product_id, itemname, total_amount, current_amount, restock) VALUES";}
            }
            else {
              if(is_menu) { update = "UPDATE drink_dictionary SET name = \'"; }
              else {update = "UPDATE inventory SET itemname = \'";}
            }
            
            int splitted_length = splitted.length;
            String drink_name = getDrinkName(splitted, formatting);
            if (is_add) {
              if(is_menu) { update += " (\'" + splitted[0] + "\', \'" + drink_name + "\', " + splitted[splitted_length -1] + ");";}
              else { update += " (" + splitted[0] + ", \'" + drink_name + "\', " + splitted[splitted_length -3] + ", " + splitted[splitted_length -2] + ", \'" + splitted[splitted_length -1] + "\');"; }
            }
            else {
              if(is_menu) { update += drink_name + "\', price = " + splitted[splitted_length -1] + "WHERE drink_id = \'" + splitted[0] + "\';";}
              else {update += drink_name + "\', total_amount = " + splitted[splitted_length -3] + ", current_amount = " + splitted[splitted_length -2] +  ", restock = \'" + splitted[splitted_length-1]+ "\' WHERE product_id = " + splitted[0] + ";";}
            }
            
            stmt.execute(update);
            out.setText("The " + text + " has been updated to " + text_out.getText());
            
            }catch (Exception n){
              n.printStackTrace();
              System.err.println(n.getClass().getName()+": "+n.getMessage());
              JOptionPane.showMessageDialog(null,"Error executing command.");
            }
      }
      updateTable(conn);
    }

    public static void updateTable(Connection conn) {
      
      if (menu_check) {
        Component[] component_list_menu = p_menu.getComponents();

        //Loop through the components
        for(Component c : component_list_menu){

            //Find the components to remove
            if(!(c instanceof JButton)){

                //Remove it
                p_menu.remove(c);
            }
        }
        p_menu.revalidate();
        p_menu.repaint();
      }
      if(inventory_check) {
        Component[] component_list = p_inventory.getComponents();

        //Loop through the components
        for(Component c : component_list){

          //Find the components to remove
          if(!(c instanceof JButton)){

              //Remove it
              p_inventory.remove(c);
          }
        }
        p_inventory.revalidate();
        p_inventory.repaint();
      }

      String[] inventory_cols = {"product_id", "itemname", "total_amount", "current_amount", "restock"};
      String[] menu_cols = {"drink_id", "name", "price"};
      ArrayList<ArrayList<String>> data_inventory = new ArrayList<>();
      ArrayList<ArrayList<String>> data_menu = new ArrayList<>();
      
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        if (inventory_check) {
          //create a SQL statement
          String sql_statement_m = "SELECT * FROM inventory;";
          ResultSet result = stmt.executeQuery(sql_statement_m);
          while (result.next()) {
            ArrayList<String> curr = new ArrayList<>();
            curr.add(result.getString("product_id"));
            curr.add(result.getString("itemname"));
            curr.add(result.getString("total_amount"));
            curr.add(result.getString("current_amount"));
            curr.add(result.getString("restock"));

            data_inventory.add(curr);          
          }
          String[][] arr = data_inventory.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new);
          table_inventory = new JTable(arr, inventory_cols);
          table_inventory.setBounds(30,40,200,500);
          JScrollPane sp = new JScrollPane(table_inventory);
          inventory_frame.getContentPane().add(sp);
          p_inventory.add(sp);

          p_inventory.revalidate();
          p_inventory.repaint();
        }
        if (menu_check) {
          //getting menu items from drinks dictionary
          String menu_command = "SELECT * FROM drink_dictionary;";
          ResultSet menuresult = stmt.executeQuery(menu_command);
          while (menuresult.next()) {          
            ArrayList<String> curr = new ArrayList<>();
            curr.add(menuresult.getString("drink_id"));
            curr.add(menuresult.getString("name"));
            curr.add(menuresult.getString("price"));

            data_menu.add(curr); 
          }

          String[][] arr_menu = data_menu.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new);
          table_menu = new JTable(arr_menu, menu_cols);
          table_menu.setBounds(30,40,400,500);
          JScrollPane sp_menu = new JScrollPane(table_menu);
          drinks_frame.getContentPane().add(sp_menu);
          p_menu.add(sp_menu);

          p_menu.revalidate();
          p_menu.repaint();
        }
      } catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        JOptionPane.showMessageDialog(null,"Error accessing drink and inventory.");
      }
    }

    public static void checkInventoryLevels(Connection conn) {
      // for each item in inventory get current data
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
          single_item.add(result.getString("restock"));

          // make list of lists with all id, total, and current amount included for each item
          inventory_list.add(single_item);
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      // update values
      for (ArrayList<String> item : inventory_list) {
        //create a SQL statement
        String sql_statement = "UPDATE inventory";
        sql_statement += " SET restock = ";
        
        if (Float.valueOf(item.get(2)) < Float.valueOf(item.get(1))) {
          // if current amount < needed amount update restock to "t"
          sql_statement += "true";
        }
        else {
          // update restock to "f"
          sql_statement += "false";
        }

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

    public static void main(String[] args)
    {
      Connection conn = createConnection();
      JOptionPane.showMessageDialog(null,"Opened database successfully");
      String name = "";
      // create a new frame
      f = new JFrame("DB GUI");
      // close connection when click "x" button
      f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

      // check inventory to start
      checkInventoryLevels(conn);

      manager_frame = new JFrame("Manager GUI");
      employee_frame = new JFrame("Employee GUI");

      // create a object
      GUI s = new GUI();

      // create a panel
      JPanel p = new JPanel();

      //creating buttons for the main page
      JButton b = new JButton("Close");
      JButton manager = new JButton("Manager");
      JButton employee = new JButton("Employee");

      // add actionlistener to button
      b.addActionListener(s);
      manager.addActionListener(s);
      employee.addActionListener(s);
      
      JTextArea text = new JTextArea(name);
      p.add(text);
      p.add(b);
      p.add(manager);
      p.add(employee);
      // add panel to frame
      f.add(p);
      f.setSize(400, 400);
      f.setVisible(true);

      // manager frame
      manager_frame.setSize(400, 400);
      JPanel p_man = new JPanel();


      implementButton("Menu", p_man, s);
      implementButton("Inventory", p_man, s);

      // add reports button
      implementButton("Reports", p_man, s);


      inventory_frame = new JFrame("Inventory Window");
      drinks_frame = new JFrame("Drinks Window");

      try {
        reports_frame = reportsWindow(conn);
      } 
      catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      p_inventory = new JPanel();
      p_menu = new JPanel();
      p_reports = new JPanel();

      implementButton("Add Menu Item", p_menu, s);
      implementButton("Add Inventory Item", p_inventory, s);

      //creating a pop up for when the user wants to add or update iteam 
      add_menu = new JFrame("Add Item Frame");
      JPanel p_add_menu = new JPanel();
      add_inventory = new JFrame("Add Item Inventory Frame");
      JPanel p_add_inventory = new JPanel();
      update_menu = new JFrame("Update Menu Item");
      JPanel p_update_menu = new JPanel();
      update_inventory= new JFrame("Update Inventory Item");
      JPanel p_update_inventory = new JPanel();


      add_menu.setSize(200, 200);
      add_menu.add(p_add_menu);
      add_inventory.setSize(200, 200);
      add_inventory.add(p_add_inventory);
      update_menu.setSize(200, 200);
      update_menu.add(p_update_menu);
      update_inventory.setSize(200, 200);
      update_inventory.add(p_update_inventory);

      implementButton("Save Menu Item", p_add_menu, s);
      implementButton("Save Updates for Menu Item", p_update_menu, s);
      implementButton("Save Inventory Item", p_add_inventory, s);
      implementButton("Save Updates for Inventory Item", p_update_inventory, s);

      
      
      
      //inserting into the database
      text_input = new JTextField("enter the text");
      text_output = new JTextArea("");
      p_add_menu.add(text_input);
      p_add_menu.add(text_output);


      text_input_inventory = new JTextField("enter the text");
      text_output_inventory = new JTextArea("");
      p_add_inventory.add(text_input_inventory);
      p_add_inventory.add(text_output_inventory);


      implementButton("Update Menu", p_menu, s);
      implementButton("Update Inventory", p_inventory, s);


      //text area for UPDATES to menu and inventory 
      update_text_input = new JTextField("enter the text");
      update_text_output = new JTextArea("");
      p_update_menu.add(update_text_input);
      p_update_menu.add(update_text_output);


      update_input_inventory = new JTextField("enter the text");
      update_output_inventory = new JTextArea("");
      p_update_inventory.add(update_input_inventory);
      p_update_inventory.add(update_output_inventory);

      menu_check = true;
      inventory_check = true;
      updateTable(conn);
      menu_check = false;
      inventory_check = false;

      //output changes
      out = new JTextArea();
      p_menu.add(out);

      inventory_frame.add(p_inventory);
      inventory_frame.setSize(800, 800);
      drinks_frame.add(p_menu);
      drinks_frame.setSize(800, 800);

      manager_frame.add(p_man);

      employee_frame.setSize(800, 800);

      JPanel p_emplo = new JPanel(new GridLayout(2, 4));

      JButton milk_tea = new JButton("Milk Tea");
      JButton brewed_tea = new JButton("Brewed Tea");
      JButton fruit_tea = new JButton("Fruit Tea");
      JButton fresh_milk = new JButton("Fresh Milk");
      JButton ice_blended = new JButton("Ice Blended");
      JButton tea_mojito = new JButton("Tea Mojito");
      JButton creama = new JButton("Creama");

      //seasonal item
      JButton seasonal = new JButton("Seasonal");
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
      //seasonal
      p_emplo.add(seasonal);
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
      seasonal.addActionListener(s);

      employee_frame.add(p_emplo);


      // update drink array
      try {
        drink_names = getDrinkNamesTable(conn);
      }
      catch (IOException error1) {
        error1.printStackTrace();
      }

      // do not close connection until done with all orders
      // currently set to after close is clicked
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
          // System.out.println(order_str);

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
      closeConnection(conn);
    }
  

    /*
    @param e Action event to show button is clicked
    @param s String comparing to specific action performed
    @return None, void function
    */

    // restock window
    public static JFrame restockWindow(Connection conn) throws IOException {
      JFrame restock_frame = new JFrame();
      restock_frame.setSize(400, 400);

      JPanel restock_panel = new JPanel();
      JPanel scrollable_panel = new JPanel();

      // SELECT productid from Inventory WHERE Restock = 't';
      JTextArea restock_text = new JTextArea();
      restock_text.setEditable(false);

      //create a statement object
      try {
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sql_statement = "SELECT * FROM inventory WHERE restock = 't' ORDER BY product_id asc ;";
        //send statement to DBMS

        ResultSet result = stmt.executeQuery(sql_statement);
        while (result.next()) {
          restock_text.append(result.getString("itemname"));
          restock_text.append("\n");
        }
      } catch (Exception e){
        System.out.println(e.toString());
        JOptionPane.showMessageDialog(null,"Error calling restock.");
      }

      restock_panel.add(restock_text);
      JScrollPane scrollable_pane = new JScrollPane(restock_panel);
      restock_frame.add(scrollable_pane);

      return restock_frame;
    }

    // reports window
    public static JFrame reportsWindow(Connection conn) throws IOException {
      // create frame for report
      JFrame reports_frame = new JFrame("Reports Window");
      reports_frame.setSize(400, 400);

      // panel
      JPanel reports_panel = new JPanel();

      // create buttons for sales, excess, restock
      JButton sales = new JButton("Sales");
      JButton excess = new JButton("Excess");
      JButton restock = new JButton("Restock");

      // check if clicked
      sales.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // call sales function
        }
      });
      excess.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // call excess function
        }
      });
      restock.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // call restock function
          JFrame restock_frame = new JFrame();
          try {
            restock_frame = restockWindow(conn);
          } catch (Exception f){
            JOptionPane.showMessageDialog(null,"Error restock window.");
          }
          restock_frame.setVisible(true);
        }
      });

      reports_panel.add(sales);
      reports_panel.add(excess);
      reports_panel.add(restock);

      reports_frame.add(reports_panel);

      return reports_frame;
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

            }
        });
      }

      // continue button 
      JButton continue_button = new JButton("Continue");
      continue_button.setBackground(Color.GREEN);
      continue_button.setOpaque(true);
      continue_button.setBorderPainted(false);
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
        
      // Add the submenu panel to the customizations_frame
      customizations_frame.add(customization_sub_menu);


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
      JPanel button_sub_menu = new JPanel(new GridLayout(0,2));

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

      button_sub_menu.add(employee_exit);
      button_sub_menu.add(another_order);

      outside_frame.add(button_sub_menu);
      outside_frame.setVisible(true);
    }

    public static void cancelWindow() {
      JFrame outside_frame = new JFrame("Cancelled Order");
      outside_frame.setSize(400, 400);
      JPanel cancel_sub_menu = new JPanel(new BorderLayout());

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

      cancel_sub_menu.add(cancel_text);
      cancel_sub_menu.add(exit_button, BorderLayout.PAGE_END);

      outside_frame.add(cancel_sub_menu);

      outside_frame.setVisible(true);
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e) {   
        
        String s = e.getActionCommand();
        Connection conn = null;
        try {
              conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_08r_db",
                "csce315_971_navya_0215",
                "password");
            } catch (Exception k) {
              System.exit(0);
            }

        if (s.equals("Close")) {
            f.dispose();
        }
        else if (s.equals("Manager")) {
          manager_frame.setVisible(true);
        }
        else if (s.equals("Employee")) {
          employee_frame.setVisible(true);
        }

        if (s.equals("Inventory")) {
          inventory_frame.setVisible(true);
        }
        if (s.equals("Menu")) {
          drinks_frame.setVisible(true);
        }
        if (s.equals("Reports")) {
          reports_frame.setVisible(true);
        }
        if (s.equals("Add Menu Item")){ 
          // set the text of the label to the text of the field
          add_menu.setVisible(true);
        }
        if (s.equals("Add Inventory Item")){
          add_inventory.setVisible(true);
        }

        if(s.equals("Save Menu Item")){
          menu_check = true;
          dataFeature(text_input, text_output, conn, true, true);
          menu_check = false;
        }

        if (s.equals("Save Inventory Item")){
          inventory_check = true;
          dataFeature(text_input_inventory, text_output_inventory, conn, true, false);
          inventory_check = false;
        }
        //update menu 
        if (s.equals("Update Menu")){
          update_menu.setVisible(true);
        }
        if(s.equals("Save Updates for Menu Item")) {
          menu_check = true;
          dataFeature(update_text_input, update_text_output, conn, false, true);
          menu_check = false;
        }

        if (s.equals("Update Inventory")){
          update_inventory.setVisible(true);
        }

        if(s.equals("Save Updates for Inventory Item")){
          inventory_check = true;
          dataFeature(update_input_inventory, update_output_inventory, conn, false, false);
          inventory_check = false;
        }

        if (s.equals("View Order")) {
          JFrame order_frame = new JFrame("Viewing Order");
          order_frame.setSize(400,400);
          
          JPanel total_sub_menu = new JPanel(new BorderLayout());
          JPanel button_sub_menu = new JPanel(new GridLayout(0,3));
          JPanel order_sub_menu = new JPanel(new GridLayout(0, 2));

          
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
                order_frame.dispose();
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

                order_frame.dispose();
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
                order_frame.dispose();

                payWindow();
              }
            }
          }); 
          
          order_sub_menu.add(order_text);
          order_sub_menu.add(prices_text);

          JScrollPane scrollable_order = new JScrollPane(order_sub_menu);  
          scrollable_order.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
  
          button_sub_menu.add(more_drinks);
          button_sub_menu.add(cancel_order);
          button_sub_menu.add(finish_and_pay);

          total_sub_menu.add(scrollable_order);
          total_sub_menu.add(button_sub_menu, BorderLayout.PAGE_END);

          order_frame.add(total_sub_menu);

          order_frame.setVisible(true);
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
        if(s.equals("Seasonal")){
          try{
            typeWindow("Seasonal", 4, 4);
          }
          catch (IOException error1){
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
    
    public static void fillIDList(int max_drinks) {
      while (order_drinks.size() < max_drinks) {
        order_drinks.add("0000");
      }
    }

    public static ArrayList<String> getDrinkNames(String file_path) throws IOException {
      ArrayList<String> drink_names = new ArrayList<>();
      File file = new File(file_path);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String drink_name = parts[1].trim();
          drink_names.add(drink_name);
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

    public static double getDrinkCost(String file_path, String drink_name) throws IOException {
      double drink_cost = 0;
      File file = new File(file_path);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 3) {
          String current_drink = parts[1].trim();
          String current_cost = parts[2].trim();

          if (current_drink.equals(drink_name)) {
            drink_cost = Double.valueOf(current_cost);
          }
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drink_cost;
    }

    public static String getDrinkID(String file_path, String drink_name) throws IOException {
      String drink_ID = "0000";
      File file = new File(file_path);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String current_drink = parts[1].trim();
          String current_ID = parts[0].trim();

          if (current_drink.equals(drink_name)) {
            drink_ID = current_ID;
          }
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drink_ID;
    }

    public static ArrayList<String> getCustomizationNames(String file_path) throws IOException {
      ArrayList<String> customization_names = new ArrayList<>();
      File file = new File(file_path);
  
      Scanner scanner = new Scanner(file);
  
      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] parts = line.split(",");
          if (parts.length >= 3) {
              String customization_name = parts[2].trim();
              customization_names.add(customization_name);
          }
      }
  
      scanner.close(); // Close the scanner explicitly.
  
      return customization_names;
    }

    public static double getCustomizationCost(String file_path, String custom_name) throws IOException {
      double custom_cost = 0;
      File file = new File(file_path);
  
      Scanner scanner = new Scanner(file);
  
      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] parts = line.split(",");
          if (parts.length >= 4) {
              String current_custom = parts[2].trim();
              String current_cost = parts[3].trim();

              if (current_custom.equals(custom_name)) {
                custom_cost = Double.valueOf(current_cost);
              }
          }
      }
  
      scanner.close(); // Close the scanner explicitly.
  
      return custom_cost;
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


      // check inventory values
      checkInventoryLevels(conn);
    }
  
}
