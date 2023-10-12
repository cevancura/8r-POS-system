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

    // drink list per order
    static ArrayList<String> order_drinks = new ArrayList<>();

    static ArrayList<String> selectedItems = new ArrayList<>();


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
      JButton customizations = new JButton("Customizations");
      JButton order = new JButton("View Order");
      order.setBackground(Color.GREEN);

      p_emplo.add(milk_tea);
      p_emplo.add(brewed_tea);
      p_emplo.add(fruit_tea);
      p_emplo.add(fresh_milk);
      p_emplo.add(ice_blended);
      p_emplo.add(tea_mojito);
      p_emplo.add(creama);
      p_emplo.add(customizations);
      p_emplo.add(order);

      milk_tea.addActionListener(s);
      brewed_tea.addActionListener(s);
      fruit_tea.addActionListener(s);
      fresh_milk.addActionListener(s);
      ice_blended.addActionListener(s);
      tea_mojito.addActionListener(s);
      creama.addActionListener(s);
      customizations.addActionListener(s);
      order.addActionListener(s);

      employee_frame.add(p_emplo);

      // do not close connection until done with all orders
      // FIX ME (currently set to after close is clicked)
      while (f.isDisplayable()) {
        continue;
      }


      // get current order number (next after max)
      String prev_order_id_str = "";
      Integer current_order_id_int = 0;
      String current_order_id_str = "";
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sqlStatement = "SELECT MAX(order_id) FROM order_history;";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
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
      System.out.println(current_order_id_str);

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
      String order_str = current_order_id_str + "," + formatted_date + "," + formatted_time + "," + String.valueOf(num_drinks) + "," + String.valueOf(total_cost);
      for (String id : order_drinks) {
        order_str += "," + id;
      }

      // write order
      System.out.println(order_str);

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
      JFrame customizationsFrame = new JFrame("Customization Options");
      customizationsFrame.setSize(800, 800);
      JPanel customizationSubMenu = new JPanel(new GridLayout(4, 4));
  
      ArrayList<String> customizationNames = null;

      ArrayList<String> currentCustomizations = new ArrayList<>();

      try {
          customizationNames = getCustomizationNames("customs.csv");
      } catch (IOException error1) {
          error1.printStackTrace();
      }
  
      for (int i = 1; i < customizationNames.size(); i++) {
          String customization = customizationNames.get(i);
          JButton custom = new JButton(customization);
          customizationSubMenu.add(custom);


          // check if clicked
          custom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  // Extract the text from the clicked button
                  String selectedItem = custom.getText();

                  if (currentCustomizations.contains(selectedItem)) {
                    // if currently selected, deselect
                    currentCustomizations.remove(selectedItem);
                    custom.setBackground(null);
                  }
                  else {
                    currentCustomizations.add(selectedItem);
                    custom.setBackground(Color.BLUE);
                  }

                  // Add it to the ArrayList
                  // selectedItems.add(selectedItem);
                  // // change color 
                  // custom.setBackground(Color.BLUE);

                  // NOTE: ADD CUSTOMIZATIONS IN A CHECKBOX, FOR EVERY BOX THAT IS CHECKED, 
                  // ADD THAT TO selectedItems AND ADD THAT TO THE ORDER
                }
            });
      }

      // continue button 
      // JPanel continueSubMenu = new JPanel();
      JButton continueButton = new JButton("Continue");
      continueButton.setBackground(Color.RED);
      // continueSubMenu.add(continueButton);
      customizationSubMenu.add(continueButton);

      continueButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String s = e.getActionCommand();
          if (s == "Continue") {
            // once continue is clicked add all current customizations to order
            for (String custom : currentCustomizations) {
              selectedItems.add(custom);
              // update total cost
              try {
                total_cost += getCustomizationCost("customs.csv", custom);
              }
              catch (IOException error1) {
                error1.printStackTrace();
              }
            }
            // and close frame
            customizationsFrame.dispose();
          }
        }
      });
      
      // customizationsFrame.add(continueSubMenu);
  
      // Add the submenu panel to the customizationsFrame
      customizationsFrame.add(customizationSubMenu);
  
      // // Make the new frame visible
      // customizationsFrame.setVisible(true);

      return customizationsFrame;
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
        if (s.equals("View Order")) {
          JFrame orderFrame = new JFrame("Viewing Order");
          orderFrame.setSize(400,400);

          JTextArea order_text = new JTextArea();
          order_text.setEditable(false);

          ArrayList<String> drinkNames = null;
          try {
            drinkNames = getDrinkNames("drink_dictionary.csv");
          }
          catch (IOException error1) {
            error1.printStackTrace();
          }
          int index = 0;
          for (String selectedItem : selectedItems) {
            if (index == 0) {
              order_text.append(selectedItem);
            }
            else {
              if (drinkNames.contains(selectedItem)) {
                order_text.append("\n\n");
              }
              else {
                order_text.append("\n");
              }
              order_text.append(selectedItem);
            }
            index++;
          }
          
          orderFrame.add(order_text);
          orderFrame.setVisible(true);
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

          //ArrayList<String> selectedItems = new ArrayList<>();

          for (String drink : drinkNames) {
            if (drink.length() >= 8 && drink.substring(0, 8).equals("Milk Tea")) {
              JButton mt = new JButton(drink);
              mt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Extract the text from the clicked button
                    String selectedItem = mt.getText();
                    // Add it to the ArrayList
                    selectedItems.add(selectedItem);

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
                   

                    // Close the milkTeaFrame
                    milkTeaFrame.dispose();


                    // Open the new frame here (e.g., a new options frame)
                    JFrame customsFrame = new JFrame("Customizations");
                    try {
                      customsFrame = customizationWindow();
                    } catch (IOException error1) {
                        error1.printStackTrace();
                    }

                    customsFrame.setSize(800, 800);
                    customsFrame.setVisible(true);


                    // NOTE: ADD CUSTOMIZATIONS IN A CHECKBOX, FOR EVERY BOX THAT IS CHECKED, 
                    // ADD THAT TO selectedItems AND ADD THAT TO THE ORDER
                }
            });
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

        if (s.equals("Customizations")) {
          // Create a new frame for Customization options
          JFrame customizationsFrame = new JFrame("Customization Options");
          customizationsFrame.setSize(800, 800);
          JPanel customizationSubMenu = new JPanel(new GridLayout(4, 4));
      
          ArrayList<String> customizationNames = null;
          try {
              customizationNames = getCustomizationNames("customs.csv");
          } catch (IOException error1) {
              error1.printStackTrace();
          }
      
          for (int i = 1; i < customizationNames.size(); i++) {
              String customization = customizationNames.get(i);
              JButton custom = new JButton(customization);
              customizationSubMenu.add(custom);
          }
      
          // Add the submenu panel to the customizationsFrame
          customizationsFrame.add(customizationSubMenu);
      
          // Make the new frame visible
          customizationsFrame.setVisible(true);
      }
 
 
      }
    
    public static void fillIDList(int maxDrinks) {
      while (order_drinks.size() < maxDrinks) {
        order_drinks.add("0000");
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
      ArrayList<String> customizationNames = new ArrayList<>();
      File file = new File(filePath);
  
      Scanner scanner = new Scanner(file);
  
      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] parts = line.split(",");
          if (parts.length >= 3) {
              String customizationName = parts[2].trim();
              customizationNames.add(customizationName);
          }
      }
  
      scanner.close(); // Close the scanner explicitly.
  
      return customizationNames;
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

  
}
