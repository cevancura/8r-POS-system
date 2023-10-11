import java.sql.*;
import java.awt.event.*;
import javax.swing.*;

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

      // inventory window
      inventory_frame = new JFrame("Inventory Window");
      drinks_frame = new JFrame("Drinks Window");

      //adding a save button 
      JButton save_btn = new JButton("Save");
      JButton save_btn_inventory= new JButton("Save Inventory");
     
      JPanel p_inventory = new JPanel();
      JPanel p_menu = new JPanel();

      p_inventory.add(save_btn_inventory);
      p_menu.add(save_btn);
      save_btn.addActionListener(s);
      save_btn_inventory.addActionListener(s);
      
      text_input = new JTextField("enter the text");
      text_output = new JTextArea("");
      p_menu.add(text_input);
      p_menu.add(text_output);

      text_input_inventory = new JTextField("enter the text");
      text_output_inventory = new JTextArea("");
      p_inventory.add(text_input_inventory);
      p_inventory.add(text_output_inventory);


      String inventory_items = "";
      String menu_items = "";
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sqlStatement = "SELECT * FROM inventory;";
        ResultSet result = stmt.executeQuery(sqlStatement);
        while (result.next()) {
          inventory_items += result.getString("product_id") + " ";
          inventory_items += result.getString("itemname")+" ";
          inventory_items += result.getString("total_amount")+" ";
          inventory_items += result.getString("current_amount")+" ";
          inventory_items += result.getString("restock")+"\n";
        }

        //getting menu items from drinks dictionary
        String menu_command = "SELECT * FROM drink_dictionary;";
        ResultSet menuresult = stmt.executeQuery(menu_command);
        while (menuresult.next()) {
          menu_items += menuresult.getString("drink_id") + " ";
          menu_items += menuresult.getString("name") + " ";
          menu_items += menuresult.getString("price") + "\n";
        }
      } catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        JOptionPane.showMessageDialog(null,"Error accessing drink and inventory.");
      }


      //add scroll bar for inventory
      JTextArea text_inventory = new JTextArea(inventory_items, 40 , 50);
      JScrollPane scroll_inventory = new JScrollPane(text_inventory);     
      scroll_inventory.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
      inventory_frame.getContentPane().add(scroll_inventory);
      p_inventory.add(scroll_inventory);
      
      //adding a scroll bar for menu
      JTextArea text_menu = new JTextArea(menu_items, 40, 50);
      JScrollPane scroll_menu = new JScrollPane(text_menu);     
      scroll_menu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
      drinks_frame.getContentPane().add(scroll_menu);
      p_menu.add(scroll_menu);

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
        if (s.equals("Save")){
            // set the text of the label to the text of the field
          text_output.setText(text_input.getText());
          
          // set the text of field to blank
          text_input.setText("enter the text");

          if (!text_output.getText().equals("")) {
            Connection conn = null;
            
            try {
              conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_08r_db",
                "csce315_971_navya_0215",
                "password");
            } catch (Exception k) {
              System.exit(0);
            }
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
        if (s.equals("Save Inventory")){
            // set the text of the label to the text of the field
          text_output_inventory.setText(text_input_inventory.getText());
          
          // set the text of field to blank
          text_input_inventory.setText("enter the text");

          if (!text_output_inventory.getText().equals("")) {
            Connection conn = null;
            //TODO STEP 1
            try {
              conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_08r_db",
                "csce315_971_navya_0215",
                "password");
            } catch (Exception k) {
              // e.printStackTrace();
              // System.err.println(e.getClass().getName()+": "+e.getMessage());
              System.exit(0);
            }
            try{
            //create a statement object
            
            Statement stmt = conn.createStatement();
            
            //create a SQL statement
            //TODO Step 2
            //String sqlStatement = sqlcommand("SELECT * FROM inventory;");
            //send statement to DBMS
            //ResultSet result = stmt.executeQuery(sqlStatement);
            System.out.println("-----------------------------");
            System.out.println(text_output_inventory.getText());
            System.out.println("-----------------------------");

            String[] splitted = text_output_inventory.getText().split("\\s+");
            
            String inventory_update = "INSERT INTO inventory (product_id, itemname, total_amount, current_amount, restock) VALUES";
            inventory_update += " (" + splitted[0] + ", \'" + splitted[1] + "\', " + splitted[2] + ", " + splitted[3] + ", \'" + splitted[4] + "\');";
            System.out.println(inventory_update);
            stmt.execute(inventory_update);
            
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
