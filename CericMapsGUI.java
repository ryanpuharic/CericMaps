// package com.codebind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class CericMapsGUI
{
    static int numberOfPoints;
    static JFrame f;
    static JTextField jTF1;
    static JTextField jTF2;
    static JTextField jTF3;
    static JTextField jTF4;
    static JTextField jTF5;
    static ArrayList<String> addresses = new ArrayList<String>();
    static JLabel error;
    static boolean errorMessagePresent;
    static boolean firstLocation;
    static String selectedItem = "Select one";
    static String apiKey = "d1b5c54a-3b6b-41fb-ac46-6dd05eb15c61";
    static CericMaps cm = new CericMaps(apiKey);
    static JLabel answer1 = new JLabel();
    static JLabel answer2 = new JLabel();
    static JLabel answer3 = new JLabel();
    static JLabel answer4 = new JLabel();
    static JLabel answer5 = new JLabel();
    static JLabel time = new JLabel();

    public static void run()
    {
        // the bible: https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html
        f = new JFrame();
        f.getContentPane().setBackground(new Color(135,206,250,100));

        /*f.setBackground(new Color(0,0,0,0));
        JPanel panel = new javax.swing.JPanel() {
            protected void paintComponent(Graphics g) {
                Paint p = new GradientPaint(0.0f, 0.0f, new Color(45, 78, 98, 0),
                getWidth(), getHeight(), new Color(34, 67, 0, 255), true);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setPaint(p);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        f.setContentPane(panel);*/
        
        JLabel title = new JLabel("Ceric Maps");
        title.setBounds(50, 1, 100, 40);

        JTextArea instructions = new JTextArea("In this program, you will be able to enter a list of addresses and receive the most efficient route between them as output. This output will disregard the order that sets of coordinates were entered and simply create the route with the least total distance between them. FOR BEST RESULTS: Be as specific as possible when entering each address (i.e. add name of town or zip code after address). Limit of 5 addresses.");
        instructions.setEditable(false);
        instructions.setWrapStyleWord(true);
        instructions.setLineWrap(true);
		instructions.setBackground(new Color(135,206,250,0));
        instructions.setBounds(50, 36, 600, 80);

        JLabel startQuestion = new JLabel("Start the route at the first location?");
        startQuestion.setBounds(50, 115, 600, 40);

        String[] startRouteAtFirstPoint = {"Select one", "Yes", "No"};
        JComboBox startRouteBox = new JComboBox(startRouteAtFirstPoint);
        startRouteBox.setBounds(50,150,100,40);
        startRouteBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedItem = (String) startRouteBox.getSelectedItem();

                if (selectedItem.equals("Yes"))
                    firstLocation = true;
                else if(selectedItem.equals("No"))
                    firstLocation = false;
            }
        });

        error = new JLabel("Error: invalid points entered");
        error.setBounds(50, 310, 480, 40);

        startRouteBox.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        int width = 370;
        int height = 130;
        int difference = 50;

        answer1.setBounds(370, 240, 400, 40);
        answer2.setBounds(370, 260, 400, 40);
        answer3.setBounds(370, 280, 400, 40);
        answer4.setBounds(370, 300, 400, 40);
        answer5.setBounds(370, 320, 400, 40);
        time.setBounds(370, 340, 400, 40);

        jTF1 = new JTextField(50);
        jTF1.setBounds(width, height, 300, 20);
        jTF1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("Error: invalid points entered");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        jTF2 = new JTextField(50);
        jTF2.setBounds(width, height + 25, 300, 20);
        jTF2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("Error: invalid points entered");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        jTF3 = new JTextField(50);
        jTF3.setBounds(width, height + 50, 300, 20);
        jTF3.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("Error: invalid points entered");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        jTF4 = new JTextField(50);
        jTF4.setBounds(width, height + 75, 300, 20);
        jTF4.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("Error: invalid points entered");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        jTF5 = new JTextField(50);
        jTF5.setBounds(width, height + 100, 300, 20);
        jTF5.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorMessagePresent)
                {
                    f.remove(error);
                    error.setText("Error: invalid points entered");
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = false;
                }
            }
        });

        JLabel numPointsQuestion = new JLabel("How many points do you plan on entering?");
        numPointsQuestion.setBounds(50, 185, 600, 40);

        String[] numPoints = {"Select one", "Three", "Four", "Five"};
        JComboBox selectNumPoints = new JComboBox(numPoints);
        selectNumPoints.setBounds(50,220,100,40);
        selectNumPoints.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(((String) selectNumPoints.getSelectedItem()).equals("Three"))
                {
                    numberOfPoints = 3;
                    createAddressBoxes(3);
                }
                else if (((String) selectNumPoints.getSelectedItem()).equals("Four"))
                {
                    numberOfPoints = 4;
                    createAddressBoxes(4);
                }
                else if (((String) selectNumPoints.getSelectedItem()).equals("Five"))
                {
                    numberOfPoints = 5;
                    createAddressBoxes(5);
                }
                else
                {
                    createAddressBoxes(-1);
                }
            }
        });

        JButton go = new JButton("GO"); //creating instance of JButton
        go.setBounds(50, 290, 60, 30); //x axis, y axis, width, height

        go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                f.remove(error);
                f.remove(answer1);
                f.remove(answer2);
                f.remove(answer3);
                f.remove(answer4);
                f.remove(answer5);

                error.setText("Error: invalid points entered");
                SwingUtilities.updateComponentTreeUI(f);

                // if how many points is not 0 and if addresses are valid and
                if (!((String) selectNumPoints.getSelectedItem()).equals("Select one") && checkAddressesInTextBoxes(numberOfPoints) && !selectedItem.equals("Select one"))
                {
                    calculate(numberOfPoints);
                    time.setText(cm.getTime());
                    f.add(time);
                }
                else if (selectedItem.equals("Select one"))
                {
                    error.setText("Error: no selection made for the first dialogue.");
                    f.add(error);
                    SwingUtilities.updateComponentTreeUI(f);
                    errorMessagePresent = true;
                }
            }
        });

        

        f.add(title);
        f.add(startQuestion);
        f.add(numPointsQuestion);
        f.add(selectNumPoints);
        f.add(instructions);
        f.add(startRouteBox);
        f.add(go);

        f.setSize(720,405);
        f.setLayout(null);
        f.setVisible(true);
    }

    public static void createAddressBoxes(int num)
    {
        // clear all address boxes in the case that the user spams the point selector

        // not getting removed properly
        f.remove(jTF1);
        f.remove(jTF2);
        f.remove(jTF3);
        f.remove(jTF4);
        f.remove(jTF5);
        f.validate();
        f.repaint();

        if (num == 3)
        {
            f.add(jTF1);
            f.add(jTF2);
            f.add(jTF3);
        }
        else if (num == 4)
        {
            f.add(jTF1);
            f.add(jTF2);
            f.add(jTF3);
            f.add(jTF4);
        }
        else if (num == 5)
        {
            f.add(jTF1);
            f.add(jTF2);
            f.add(jTF3);
            f.add(jTF4);
            f.add(jTF5);
        }

        SwingUtilities.updateComponentTreeUI(f);
    }

    // true is good to go, false is problems
    public static boolean checkAddressesInTextBoxes(int numPo)
    {
        addresses.clear();
        addresses.add(jTF1.getText());
        addresses.add(jTF2.getText());
        addresses.add(jTF3.getText());
        if (numPo == 4)
        {
            addresses.add(jTF4.getText());
        }
        else if (numPo == 5)
        {
            addresses.add(jTF4.getText());
            addresses.add(jTF5.getText());
        }

        boolean flag = true;
        for (int i = 0; i < numPo; i++)
        {
            if (!flag && cm.isValidAddress(addresses.get(i)) == false)
            {
                flag = false;
                error.setText(error.getText() + ", " + (i + 1));
            }
            else if (cm.isValidAddress(addresses.get(i)) == false)
            {
                flag = false;
                error.setText(error.getText() + " at point(s): " + (i + 1));
            }
        }

        if (!flag) // bad points
        {
            errorMessagePresent = true;
            f.add(error);
            SwingUtilities.updateComponentTreeUI(f);
            return false;
        }

        return true;
    }

    public static void calculate(int num)
    {
        System.out.println("Calculate");
        System.out.println(addresses.size());

        for (int i = 0; i < addresses.size(); i++)
        {
            System.out.print(addresses.get(i) + " ");
        }

        double[][] coords = cm.getCoords(addresses); //ERROR CAUSER :(
        int[][] matrix = cm.getMatrix(coords, addresses);
        ArrayList<String> answers = cm.findRoute(addresses, matrix, firstLocation);

        for (int i = 0; i < answers.size(); i++)
        {
            System.out.println(answers.get(i));
        }

        answer1.setText(answers.get(0));
        f.add(answer1);

        answer2.setText(answers.get(1));
        f.add(answer2);

        answer3.setText(answers.get(2));
        f.add(answer3);

        if (answers.size() == 4)
        {
            answer4.setText(answers.get(3));
            f.add(answer4);
        }

        if (answers.size() == 5)
        {
            answer4.setText(answers.get(3));
            f.add(answer4);

            answer5.setText(answers.get(4));
            f.add(answer5);
        }
    }
}