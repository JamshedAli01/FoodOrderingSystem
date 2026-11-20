package gui;

import dao.RestaurantDAO;
import model.Restaurant;
import ui.RoundedButton;
import ui.RoundedPanel;
import ui.Theme;
import util.CartManager;
import util.WindowHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class HomeFrame extends JFrame {
    private final JPanel cards = new JPanel(new GridLayout(0, 3, 18, 18));
    private final JTextField search = new JTextField();
    private final ArrayList<Restaurant> restaurants;

    public HomeFrame() {
        setTitle("Food Ordering System");
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        restaurants = new RestaurantDAO().getRestaurants();

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(Theme.LIGHT);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));
        root.add(createHeader(), BorderLayout.NORTH);
        cards.setBackground(Theme.LIGHT);
        cards.setBorder(new EmptyBorder(4, 4, 4, 4));
        JScrollPane scroll = new JScrollPane(cards);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
        add(root);
        refreshCards("");
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 8));
        header.setOpaque(false);
        JLabel title = new JLabel("Food Ordering System");
        title.setFont(Theme.titleFont());
        title.setForeground(Theme.DARK);
        header.add(title, BorderLayout.WEST);
        JPanel controls = new JPanel(new BorderLayout(8, 0));
        controls.setOpaque(false);
        search.setToolTipText("Search by restaurant name or cuisine");
        search.setColumns(18);
        Theme.styleTextField(search);
        controls.add(new JLabel("Search:"), BorderLayout.WEST);
        controls.add(search, BorderLayout.CENTER);
        RoundedButton searchButton = new RoundedButton("Search");
        searchButton.addActionListener(e -> refreshCards(search.getText()));
        RoundedButton cart = new RoundedButton("View Cart (" + CartManager.getCart().size() + ")");
        cart.addActionListener(e -> { dispose(); new CartFrame(); });
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);
        rightControls.add(searchButton);
        rightControls.add(cart);
        controls.add(rightControls, BorderLayout.EAST);
        header.add(controls, BorderLayout.EAST);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshCards(search.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshCards(search.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshCards(search.getText()); }
        });
        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        JLabel text = new JLabel("Choose a restaurant. Your cart stays saved while you browse.");
        text.setForeground(Theme.TEXT);
        footer.add(text);
        return footer;
    }

    private void refreshCards(String text) {
        cards.removeAll();
        String query = text == null ? "" : text.trim().toLowerCase();
        for (Restaurant restaurant : restaurants) {
            String searchable = restaurant.getRestaurantName() + " " +
                    (restaurant.getCuisine() == null ? "" : restaurant.getCuisine());
            if (searchable.toLowerCase().contains(query)) cards.add(createCard(restaurant));
        }
        cards.revalidate();
        cards.repaint();
    }

    private JPanel createCard(Restaurant restaurant) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(8, 8));
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(restaurant.getRestaurantName());
        name.setFont(Theme.headingFont());
        name.setForeground(Theme.DARK);
        details.add(name);
        details.add(Box.createVerticalStrut(8));
        details.add(new JLabel("Cuisine: " + safe(restaurant.getCuisine())));
        details.add(new JLabel("Location: " + restaurant.getLocation()));
        details.add(new JLabel("Menu items: " + restaurant.getItemCount()));
        details.add(new JLabel(String.format("Rating: %.1f / 5.0", restaurant.getAverageRating())));
        card.add(details, BorderLayout.CENTER);
        RoundedButton open = new RoundedButton("Open Menu");
        open.addActionListener(e -> {
            CartManager.setLastRestaurant(restaurant);
            dispose();
            new MenuFrame(restaurant);
        });
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "Not specified" : value;
    }
}
