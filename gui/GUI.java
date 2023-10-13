import java.sql.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.awt.Component;

// TO RUN::
// compile with 'javac *.java'
// run with 'java -cp ".;postgresql-42.2.8.jar" GUI'

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    static JFrame manager_frame;
    static JFrame employee_frame;
    static JFrame inventory_frame;
    static JFrame drinks_frame;
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


    public static void dataFeature(JTextField text_in, JTextArea text_out, Connection conn, Boolean isAdd, Boolean isMenu) {
      text_out.setText(text_in.getText());
      text_in.setText("enter the text");
      String update = "";

      String text = "Inventory";
      if (isMenu) {text = "Menu";}
      int formatting = 3;
      if (isMenu) {formatting = 1;}

      if (!(text_out.getText().equals("") || text_out.getText().equals("enter the text"))) {
        try{
            
            Statement stmt = conn.createStatement();
            String[] splitted = text_out.getText().split("\\s+");
            
            if (isAdd) {
              if (isMenu) { update = "INSERT INTO drink_dictionary (drink_id, name, price) VALUES";}
              else {update = "INSERT INTO inventory (product_id, itemname, total_amount, current_amount, restock) VALUES";}
            }
            else {
              if(isMenu) { update = "UPDATE drink_dictionary SET name = \'"; }
              else {update = "UPDATE inventory SET itemname = \'";}
            }
            
            int splitted_length = splitted.length;
            String drink_name = getDrinkName(splitted, formatting);
            if (isAdd) {
              if(isMenu) { update += " (\'" + splitted[0] + "\', \'" + drink_name + "\', " + splitted[splitted_length -1] + ");";}
              else { update += " (" + splitted[0] + ", \'" + drink_name + "\', " + splitted[splitted_length -3] + ", " + splitted[splitted_length -2] + ", \'" + splitted[splitted_length -1] + "\');"; }
            }
            else {
              if(isMenu) { update += drink_name + "\', price = " + splitted[splitted_length -1] + "WHERE drink_id = \'" + splitted[0] + "\';";}
              else {update += drink_name + "\', total_amount = " + splitted[splitted_length -3] + ", current_amount = " + splitted[splitted_length -2] +  ", restock = \'" + splitted[splitted_length-1]+ "\' WHERE product_id = " + splitted[0] + ";";}
            }
            
            stmt.execute(update);
            out.setText("The" + text + "has been updated to " + text_out.getText());
            
            }catch (Exception n){
              n.printStackTrace();
              System.err.println(n.getClass().getName()+": "+n.getMessage());
              JOptionPane.showMessageDialog(null,"Error executing command.");
            }
      }
      // p_inventory.remove(table_inventory);
      // p_menu.remove(table_menu);
      updateTable(conn, isMenu);
    }

    public static void updateTable(Connection conn, Boolean isMenu) {
      
      // if (isMenu) {
        Component[] componentListMenu = p_menu.getComponents();

        //Loop through the components
        for(Component c : componentListMenu){

            //Find the components to remove
            if(!(c instanceof JButton)){

                //Remove it
                p_menu.remove(c);
            }
        }
        p_menu.revalidate();
        p_menu.repaint();
      // }
      // else {
        Component[] componentList = p_inventory.getComponents();

        //Loop through the components
        for(Component c : componentList){

          //Find the components to remove
          if(!(c instanceof JButton)){

              //Remove it
              p_inventory.remove(c);
          }
        }
        p_inventory.revalidate();
        p_inventory.repaint();
      // }

      String[] inventory_cols = {"product_id", "itemname", "total_amount", "current_amount", "restock"};
      String[] menu_cols = {"drink_id", "name", "price"};
      ArrayList<ArrayList<String>> data_inventory = new ArrayList<>();
      ArrayList<ArrayList<String>> data_menu = new ArrayList<>();

      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sqlStatement = "SELECT * FROM inventory;";
        ResultSet result = stmt.executeQuery(sqlStatement);
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

      } catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        JOptionPane.showMessageDialog(null,"Error accessing drink and inventory.");
      }
    }

    public static void main(String[] args)
    {
      Connection conn = createConnection();
      JOptionPane.showMessageDialog(null,"Opened database successfully");
      String name = "";
      // create a new frame
      f = new JFrame("DB GUI");
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

      /*
      JButton employee_info = new JButton("Employee Information");      
      p_man.add(employee_info);
      manager_frame.add(p_man);
      */
      //JButton employee_info = addButtontoPanel("Employee Information", p_man);


      //JButton drinks = new JButton("Menu");
      //drinks.addActionListener(s);
      //p_man.add(drinks);
      implementButton("Menu", p_man, s);

      // JButton inventory = new JButton("Inventory");
      // inventory.addActionListener(s);
      // p_man.add(inventory);
      implementButton("Inventory", p_man, s);

      inventory_frame = new JFrame("Inventory Window");
      drinks_frame = new JFrame("Drinks Window");
      p_inventory = new JPanel();
      p_menu = new JPanel();


      // adding a save button 
      // JButton save_btn = new JButton("Add Menu Item");
      // JButton save_btn_inventory= new JButton("Add Inventory Item");
      // p_inventory.add(save_btn_inventory);
      // p_menu.add(save_btn);
      // save_btn.addActionListener(s);
      // save_btn_inventory.addActionListener(s);
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

      // JButton add_menu_btn = new JButton("Save Menu Item");
      // p_add_menu.add(add_menu_btn);
      // JButton add_inventory_btn = new JButton("Save Inventory Item");
      // p_add_inventory.add(add_inventory_btn);
      // JButton update_menu_btn = new JButton("Save Updates for Menu Item");
      // p_update_menu.add(update_menu_btn);
      // JButton update_inventory_btn = new JButton("Save Updates for Inventory Item");
      // p_update_inventory.add(update_inventory_btn);

      // add_menu_btn.addActionListener(s);
      // add_inventory_btn.addActionListener(s);
      // update_menu_btn.addActionListener(s);
      // update_inventory_btn.addActionListener(s);

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

      //updating the database
      // JButton update_menu = new JButton("Update Menu");
      // JButton update_inventory = new JButton("Update Inventory");
      // p_inventory.add(update_inventory);
      // p_menu.add(update_menu);
      // update_menu.addActionListener(s);
      // update_inventory.addActionListener(s);
      implementButton("Update Menu", p_menu, s);
      implementButton("Update Inventory", p_inventory, s);


      //text area for UPDATES to menu and inventory 
      update_text_input = new JTextField("enter the text");
      update_text_output = new JTextArea("");
      p_update_menu.add(update_text_input);
      p_update_inventory.add(update_text_output);


      update_input_inventory = new JTextField("enter the text");
      update_output_inventory = new JTextArea("");
      p_update_inventory.add(update_input_inventory);
      p_update_inventory.add(update_output_inventory);


      updateTable(conn, true);
      // String[] inventory_cols = {"product_id", "itemname", "total_amount", "current_amount", "restock"};
      // String[] menu_cols = {"drink_id", "name", "price"};
      // ArrayList<ArrayList<String>> data_inventory = new ArrayList<>();
      // ArrayList<ArrayList<String>> data_menu = new ArrayList<>();

      // try{
      //   //create a statement object
      //   Statement stmt = conn.createStatement();
      //   //create a SQL statement
      //   String sqlStatement = "SELECT * FROM inventory;";
      //   ResultSet result = stmt.executeQuery(sqlStatement);
      //   while (result.next()) {
      //     ArrayList<String> curr = new ArrayList<>();
      //     curr.add(result.getString("product_id"));
      //     curr.add(result.getString("itemname"));
      //     curr.add(result.getString("total_amount"));
      //     curr.add(result.getString("current_amount"));
      //     curr.add(result.getString("restock"));

      //     data_inventory.add(curr);          
      //   }
      //   String[][] arr = data_inventory.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new);
      //   table_inventory = new JTable(arr, inventory_cols);
      //   table_inventory.setBounds(30,40,200,500);
      //   JScrollPane sp = new JScrollPane(table_inventory);
      //   inventory_frame.getContentPane().add(sp);
      //   p_inventory.add(sp);

      //   //getting menu items from drinks dictionary
      //   String menu_command = "SELECT * FROM drink_dictionary;";
      //   ResultSet menuresult = stmt.executeQuery(menu_command);
      //   while (menuresult.next()) {          
      //     ArrayList<String> curr = new ArrayList<>();
      //     curr.add(menuresult.getString("drink_id"));
      //     curr.add(menuresult.getString("name"));
      //     curr.add(menuresult.getString("price"));

      //     data_menu.add(curr); 
      //   }

      //   String[][] arr_menu = data_menu.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new);
      //   table_menu = new JTable(arr_menu, menu_cols);
      //   table_menu.setBounds(30,40,400,500);
      //   JScrollPane sp_menu = new JScrollPane(table_menu);
      //   drinks_frame.getContentPane().add(sp_menu);
      //   p_menu.add(sp_menu);

      // } catch (Exception e){
      //   e.printStackTrace();
      //   System.err.println(e.getClass().getName()+": "+e.getMessage());
      //   JOptionPane.showMessageDialog(null,"Error accessing drink and inventory.");
      // }
      
      //output changes
      out = new JTextArea();
      p_menu.add(out);

      inventory_frame.add(p_inventory);
      inventory_frame.setSize(800, 800);
      drinks_frame.add(p_menu);
      drinks_frame.setSize(800, 800);

      manager_frame.add(p_man);

      

      //closing the connection
      closeConnection(conn);
    }
  

    /*
    @param e Action event to show button is clicked
    @param s String comparing to specific action performed
    @return None, void function
    */
  
    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {   
        
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
        if (s.equals("Menu")){
          drinks_frame.setVisible(true);
        }
        if (s.equals("Add Menu Item")){ 
          // set the text of the label to the text of the field
          add_menu.setVisible(true);
        }
        if (s.equals("Add Inventory Item")){
          add_inventory.setVisible(true);
        }

        if(s.equals("Save Menu Item")){
          dataFeature(text_input, text_output, conn, true, true);
        }
      
        if (s.equals("Save Inventory Item")){
          dataFeature(text_input_inventory, text_output_inventory, conn, true, false);
        }
        //update menu 
        if (s.equals("Update Menu")){
          update_menu.setVisible(true);
        }
        if(s.equals("Save Updates for Menu Item")){
          dataFeature(update_text_input, update_text_output, conn, false, true);
        }

        if (s.equals("Update Inventory")){
          update_inventory.setVisible(true);
        }

        if(s.equals("Save Updates for Inventory Item")){
          dataFeature(update_input_inventory, update_output_inventory, conn, false, false);
        }
    }
  

/*
@param result, name  A result set, and a column name as a string
@return string result from query
*/
public static String database(ResultSet result, String name) {
    String response = "";
    try{
    
    while (result.next()) {
      response += result.getString("name")+"\n";
    }

    }
    catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
    }
    return response;
  }
}
//delete the panel make a table and reload the table 