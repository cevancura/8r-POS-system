import java.sql.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

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
    static JTextArea out;
    static JTable table_menu;
    static JTable table_inventory;
    static JFrame add_menu;
    static JFrame add_inventory;
    static JFrame update_menu;
    static JFrame update_inventory;


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
      JButton employee_info = new JButton("Employee Information");
      JPanel p_man = new JPanel();
      p_man.add(employee_info);
      manager_frame.add(p_man);

      JButton drinks = new JButton("Menu");
      //JPanel p_man = new JPanel();
      drinks.addActionListener(s);
      p_man.add(drinks);
      //manager_frame.add(p_man);

      JButton inventory = new JButton("Inventory");
      inventory.addActionListener(s);
      //JPanel p_man = new JPanel();
      p_man.add(inventory);

      inventory_frame = new JFrame("Inventory Window");
      drinks_frame = new JFrame("Drinks Window");
      JPanel p_inventory = new JPanel();
      JPanel p_menu = new JPanel();


      //adding a save button 
      JButton save_btn = new JButton("Add Menu Item");
      JButton save_btn_inventory= new JButton("Add Inventory Item");

      //creating a pop up for when the user wants to add or update iteam 
      add_menu = new JFrame("Add Item Frame");
      JPanel p_add_menu = new JPanel();
      add_inventory = new JFrame("Add Item Inventory Frame");
      JPanel p_add_inventory = new JPanel();
      update_menu = new JFrame("Update Menu Item");
      JPanel p_update_menu = new JPanel();
      update_inventory= new JFrame("Update Inventory Item");
      JPanel p_updte_inventory = new JPanel();


      add_menu.setSize(200, 200);
      add_menu.add(p_add_menu);
      add_inventory.setSize(200, 200);
      add_inventory.add(p_add_inventory);
      update_menu.setSize(200, 200);
      update_inventory.setSize(200, 200);

      JButton add_menu_btn = new JButton("Save Menu Item");
      p_add_menu.add(add_menu_btn);
      JButton add_inventory_btn = new JButton("Save Inventory Item");
      p_add_inventory.add(add_inventory_btn);

      add_menu_btn.addActionListener(s);
      add_inventory_btn.addActionListener(s);

      
      p_inventory.add(save_btn_inventory);
      p_menu.add(save_btn);
      save_btn.addActionListener(s);
      save_btn_inventory.addActionListener(s);
      
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
      JButton update_menu = new JButton("Update Menu");
      JButton update_inventory = new JButton("Update Inventory");


      p_inventory.add(update_inventory);
      p_menu.add(update_menu);
      update_menu.addActionListener(s);
      update_inventory.addActionListener(s);

      
      String[] inventory_cols = {"product_id", "itemname", "total_amount", "current_amount", "restock"};
      String[] menu_cols = {"drink_id", "name", "price"};
      String inventory_items="";
      String menu_items = "";
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
        
          text_output.setText(text_input.getText());
          
          // set the text of field to blank
          text_input.setText("enter the text");

          if (!text_output.getText().equals("")) {
    
            try{
            
                Statement stmt = conn.createStatement();
                String[] splitted = text_output.getText().split("\\s+");
                
                String menu_update = "INSERT INTO drink_dictionary (drink_id, name, price) VALUES";
                menu_update += " (\'" + splitted[0] + "\', \'" + splitted[1] + "\', " + Double. parseDouble(splitted[2]) + ");";
                System.out.println(menu_update);
                stmt.execute(menu_update);
            
            }catch (Exception n){
                n.printStackTrace();
                System.err.println(n.getClass().getName()+": "+n.getMessage());
                JOptionPane.showMessageDialog(null,"Error executing command.");
            }
            closeConnection(conn);
          }
        }
      
        if (s.equals("Save Inventory Item")){
          // set the text of the label to the text of the field
          text_output_inventory.setText(text_input_inventory.getText());
          
          // set the text of field to blank
          text_input_inventory.setText("enter the text");

          if (!text_output_inventory.getText().equals("")) {
            
            try{
            //create a statement object
            Statement stmt = conn.createStatement();

            String[] splitted = text_output_inventory.getText().split("\\s+");
            
            String inventory_update = "INSERT INTO inventory (product_id, itemname, total_amount, current_amount, restock) VALUES";
            inventory_update += " (" + splitted[0] + ", \'" + splitted[1] + "\', " + splitted[2] + ", " + splitted[3] + ", \'" + splitted[4] + "\');";
            stmt.execute(inventory_update);
            
            }catch (Exception n){
              n.printStackTrace();
              System.err.println(n.getClass().getName()+": "+n.getMessage());
              JOptionPane.showMessageDialog(null,"Error executing command.");
            }
            closeConnection(conn);
          }
        }
        //update menu 
        if (s.equals("Update Menu")){
            // set the text of the label to the text of the field
          text_output.setText(text_input.getText());
          
          // set the text of field to blank
          text_input.setText("enter the text");

          if (!(text_output.getText().equals("") || text_output.getText().equals("enter the text"))) {
            
            try{
            
            Statement stmt = conn.createStatement();
            String[] splitted = text_output.getText().split("\\s+");
            
            String menu_update = "UPDATE drink_dictionary SET name = \'";
            int splitted_length = splitted.length;
            String drink_name = "";
            for (int i = 1; i < splitted_length - 1; ++i) {
                drink_name += splitted[i];
    
                if (i != splitted_length-2){

                    drink_name+= " ";

                }
            }
            menu_update += drink_name + "\', price = " + splitted[splitted_length -1] + "WHERE drink_id = \'" + splitted[0] + "\';";
            stmt.execute(menu_update);
            out.setText("The menu has been updated to " + text_output.getText());
            
            }catch (Exception n){
              n.printStackTrace();
              System.err.println(n.getClass().getName()+": "+n.getMessage());
              JOptionPane.showMessageDialog(null,"Error executing command.");
            }
            closeConnection(conn);
          }
        }

        if (s.equals("Update Inventory")){
            // set the text of the label to the text of the field
          text_output_inventory.setText(text_input_inventory.getText());
          
          // set the text of field to blank
          text_input_inventory.setText("enter the text");

          if (!(text_output_inventory.getText().equals("") || text_output_inventory.getText().equals("enter the text"))) {
            
            try{
            
            Statement stmt = conn.createStatement();
            String[] splitted = text_output_inventory.getText().split("\\s+");
            
            String inventory_update = "UPDATE inventory SET itemname = \'";
            int splitted_length = splitted.length;
            String item_name = "";
            for (int i = 1; i < splitted_length - 3; ++i) {
                item_name += splitted[i];
    
                if (i != splitted_length-4){

                    item_name+= " ";

                }
            }

            inventory_update += item_name + "\', total_amount = " + splitted[splitted_length -3] + ", current_amount = " + splitted[splitted_length -2] +  ", restock = \'" + splitted[splitted_length-1]+ "\' WHERE product_id = " + splitted[0] + ";";
            stmt.execute(inventory_update);
            out.setText("The inventory item has been updated to " + text_output_inventory.getText());
            
            }catch (Exception n){
              n.printStackTrace();
              System.err.println(n.getClass().getName()+": "+n.getMessage());
              JOptionPane.showMessageDialog(null,"Error executing command.");
            }
            closeConnection(conn);
          }
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