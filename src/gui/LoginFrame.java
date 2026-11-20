package gui;

import dao.UserDAO;
import model.User;
import ui.RoundedButton;
import ui.RoundedPanel;
import ui.Theme;
import util.Session;
import util.WindowHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public LoginFrame() {
        setTitle("Food Ordering System - Login");
        setSize(560, 440);
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.LIGHT);
        RoundedPanel card = new RoundedPanel(new GridBagLayout());
        card.setBorder(new EmptyBorder(25, 35, 25, 35));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(Theme.titleFont());
        title.setForeground(Theme.DARK);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);
        c.gridwidth = 1; c.gridy++;
        card.add(new JLabel("Username"), c);
        c.gridx = 1; Theme.styleTextField(username); card.add(username, c);
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Password"), c);
        c.gridx = 1; Theme.styleTextField(password); card.add(password, c);
        RoundedButton login = new RoundedButton("LOGIN");
        RoundedButton register = new RoundedButton("REGISTER");
        c.gridx = 0; c.gridy++; card.add(login, c);
        c.gridx = 1; card.add(register, c);
        root.add(card);
        add(root);
        login.addActionListener(e -> login());
        register.addActionListener(e -> { dispose(); new RegisterFrame(); });
        setVisible(true);
    }

    private void login() {
        try {
            User user = new UserDAO().loginUser(username.getText().trim(),
                    new String(password.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
                return;
            }
            Session.currentUser = user;
            dispose();
            new HomeFrame();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
