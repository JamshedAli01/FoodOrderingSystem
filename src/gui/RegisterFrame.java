package gui;

import dao.UserDAO;
import model.User;
import ui.RoundedButton;
import ui.RoundedPanel;
import ui.Theme;
import util.WindowHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final JTextField name = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public RegisterFrame() {
        setTitle("Food Ordering System - Register");
        setSize(600, 540);
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.LIGHT);
        RoundedPanel card = new RoundedPanel(new GridBagLayout());
        card.setBorder(new EmptyBorder(22, 35, 22, 35));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7);
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(Theme.titleFont());
        title.setForeground(Theme.DARK);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; card.add(title, c);
        c.gridwidth = 1;
        addField(card, c, "Full name", name);
        addField(card, c, "Email", email);
        addField(card, c, "Username", username);
        addField(card, c, "Password", password);
        RoundedButton register = new RoundedButton("REGISTER");
        RoundedButton back = new RoundedButton("BACK TO LOGIN");
        c.gridx = 0; c.gridy++; card.add(register, c);
        c.gridx = 1; card.add(back, c);
        root.add(card);
        add(root);
        register.addActionListener(e -> register());
        back.addActionListener(e -> { dispose(); new LoginFrame(); });
        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints base, String label, JTextField field) {
        base.gridy++;
        base.gridx = 0;
        panel.add(new JLabel(label), base);
        base.gridx = 1;
        Theme.styleTextField(field);
        panel.add(field, base);
    }

    private void register() {
        String fullName = name.getText().trim();
        String mail = email.getText().trim();
        String userName = username.getText().trim();
        String pass = new String(password.getPassword());
        if (fullName.length() < 3 || userName.length() < 4 || pass.length() < 6 ||
                !mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter valid values in every field.");
            return;
        }
        try {
            if (new UserDAO().registerUser(new User(fullName, mail, userName, pass))) {
                JOptionPane.showMessageDialog(this, "Registration successful.");
                dispose();
                new LoginFrame();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
