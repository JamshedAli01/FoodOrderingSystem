package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class Theme {

    public static final Color PRIMARY = new Color(230,126,34);      // Orange Buttons
    public static final Color PRIMARY_DARK = new Color(211, 105, 20);
    public static final Color DARK = new Color(44,62,80);           // Header
    public static final Color LIGHT = new Color(236,240,241);       // Window Background
    public static final Color CARD = Color.WHITE;                   // Cards
    public static final Color TEXT = new Color(33,37,41);           // Text
    public static final Color SUCCESS = new Color(39,174,96);

    public static Font titleFont(){
        return new Font("Segoe UI",Font.BOLD,28);
    }

    public static Font headingFont(){
        return new Font("Segoe UI",Font.BOLD,20);
    }

    public static Font normalFont(){
        return new Font("Segoe UI",Font.PLAIN,15);
    }

    public static Font buttonFont(){
        return new Font("Segoe UI",Font.BOLD,15);
    }

    public static void styleButton(JButton button){

        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setFont(buttonFont());

    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT);
        label.setFont(normalFont());
    }

    public static void styleTextField(JTextField txt){

        txt.setFont(normalFont());

        txt.setBorder(
                new CompoundBorder(
                        new LineBorder(new Color(220,220,220),1,true),
                        new EmptyBorder(8,10,8,10)
                )
        );

    }

}