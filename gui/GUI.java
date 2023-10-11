import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


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
        String sqlStatement = "SELECT * FROM drink_dictionary;";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
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

      p_emplo.add(milk_tea);
      p_emplo.add(brewed_tea);
      p_emplo.add(fruit_tea);
      p_emplo.add(fresh_milk);
      p_emplo.add(ice_blended);
      p_emplo.add(tea_mojito);
      p_emplo.add(creama);

      milk_tea.addActionListener(s);
      brewed_tea.addActionListener(s);
      fruit_tea.addActionListener(s);
      fresh_milk.addActionListener(s);
      ice_blended.addActionListener(s);
      tea_mojito.addActionListener(s);
      creama.addActionListener(s);

      employee_frame.add(p_emplo);


      //closing the connection
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

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
        if (s.equals("Milk Tea")) {
          // Create a new frame for Milk Tea options
          JFrame milkTeaFrame = new JFrame("Milk Tea Options");
          milkTeaFrame.setSize(800, 800);
          JPanel milkSubMenu = new JPanel(new GridLayout(4, 4));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.length() >= 8 && drink.substring(0, 8).equals("Milk Tea")) {
              JButton mt = new JButton(drink);
              milkSubMenu.add(mt);
            }
          }

          // Add the submenu panel to the employee_frame
          milkTeaFrame.add(milkSubMenu);

          // Make the new frame visible
          milkTeaFrame.setVisible(true);
        }
        if (s.equals("Brewed Tea")) {
          // Create a new frame for Brewed Tea options
          JFrame brewedTeaFrame = new JFrame("Brewed Tea Options");
          brewedTeaFrame.setSize(800, 800);
          JPanel brewedSubMenu = new JPanel(new GridLayout(2, 4));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Brewed Tea")) {
              JButton bt = new JButton(drink);
              brewedSubMenu.add(bt);
            }
          }

          // Add the submenu panel to the employee_frame
          brewedTeaFrame.add(brewedSubMenu);

          // Make the new frame visible
          brewedTeaFrame.setVisible(true);
        }
        if (s.equals("Fruit Tea")) {
          // Create a new frame for Fruit Tea options
          JFrame fruitTeaFrame = new JFrame("Fruit Tea Options");
          fruitTeaFrame.setSize(800, 800);
          JPanel fruitSubMenu = new JPanel(new GridLayout(3, 4));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Fruit Tea")) {
              JButton ft = new JButton(drink);
              fruitSubMenu.add(ft);
            }
          }

          // Add the submenu panel to the employee_frame
          fruitTeaFrame.add(fruitSubMenu);

          // Make the new frame visible
          fruitTeaFrame.setVisible(true);
        }
        if (s.equals("Fresh Milk")) {
          // Create a new frame for Fresh Milk options
          JFrame freshMilkFrame = new JFrame("Fresh Milk Options");
          freshMilkFrame.setSize(800, 800);
          JPanel freshMilkSubMenu = new JPanel(new GridLayout(3, 3));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Fresh Milk")) {
              JButton fm = new JButton(drink);
              freshMilkSubMenu.add(fm);
            }
          }

          // Add the submenu panel to the employee_frame
          freshMilkFrame.add(freshMilkSubMenu);

          // Make the new frame visible
          freshMilkFrame.setVisible(true);
        }
        if (s.equals("Ice Blended")) {
          // Create a new frame for Ice Blended options
          JFrame blendedFrame = new JFrame("Ice Blended Options");
          blendedFrame.setSize(800, 800);
          JPanel blendedSubMenu = new JPanel(new GridLayout(3, 3));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Ice Blended")) {
              JButton ib = new JButton(drink);
              blendedSubMenu.add(ib);
            }
          }

          // Add the submenu panel to the employee_frame
          blendedFrame.add(blendedSubMenu);

          // Make the new frame visible
          blendedFrame.setVisible(true);
        }
        if (s.equals("Tea Mojito")) {
          // Create a new frame for Mojito options
          JFrame mojitoFrame = new JFrame("Tea Mojito Options");
          mojitoFrame.setSize(800, 800);
          JPanel mojitoSubMenu = new JPanel(new GridLayout(2, 2));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Mojito")) {
              JButton mj = new JButton(drink);
              mojitoSubMenu.add(mj);
            }
          }

          // Add the submenu panel to the employee_frame
          mojitoFrame.add(mojitoSubMenu);

          // Make the new frame visible
          mojitoFrame.setVisible(true);
        }
        if (s.equals("Creama")) {
          // Create a new frame for Creama options
          JFrame creamaFrame = new JFrame("Creama Options");
          creamaFrame.setSize(800, 800);
          JPanel creamaSubMenu = new JPanel(new GridLayout(2, 4));

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }

          for (String drink : drinkNames) {
            if (drink.contains("Creama")) {
              JButton cr = new JButton(drink);
              creamaSubMenu.add(cr);
            }
          }

          // Add the submenu panel to the employee_frame
          creamaFrame.add(creamaSubMenu);

          // Make the new frame visible
          creamaFrame.setVisible(true);
        }
 
 
      }

    public static ArrayList<String> getDrinkNames(String filePath) throws IOException {
      ArrayList<String> drinkNames = new ArrayList<>();
      File file = new File(filePath);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String drinkName = parts[1].trim();
          drinkNames.add(drinkName);
        }
      }

      scanner.close(); // Close the scanner explicitly.

      return drinkNames;
    }
      
}
