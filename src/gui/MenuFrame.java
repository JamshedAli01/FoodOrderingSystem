package gui;

import dao.MenuDAO;
import model.MenuItem;
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
import java.util.Comparator;
import java.util.Map;

public class MenuFrame extends JFrame {
    private final Restaurant restaurant;
    private final ArrayList<MenuItem> menu;
    private final JPanel menuPanel = new JPanel();
    private final JTextField search = new JTextField();
    private final JComboBox<String> category = new JComboBox<>();
    private final JComboBox<String> sort = new JComboBox<>(
            new String[]{"Name", "Price: Low to High", "Price: High to Low"});
    private final JLabel status = new JLabel(" ");
    private final RoundedButton cartButton = new RoundedButton("View Cart (0)");
    private Map<Integer, String> categories;

    public MenuFrame(Restaurant restaurant) {
        this.restaurant = restaurant;
        CartManager.setLastRestaurant(restaurant);
        setTitle(restaurant.getRestaurantName() + " Menu");
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        MenuDAO dao = new MenuDAO();
        menu = dao.getMenu(restaurant.getRestaurantId());
        categories = dao.getCategories();
        buildScreen();
        refreshMenu();
        setVisible(true);
    }

    private void buildScreen() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Theme.LIGHT);
        root.setBorder(new EmptyBorder(15, 18, 15, 18));
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setOpaque(false);
        JLabel title = new JLabel(restaurant.getRestaurantName() + " Menu");
        title.setFont(Theme.titleFont());
        title.setForeground(Theme.DARK);
        header.add(title, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        RoundedButton back = new RoundedButton("< Back to Restaurants");
        back.addActionListener(e -> { dispose(); new HomeFrame(); });
        cartButton.addActionListener(e -> { dispose(); new CartFrame(); });
        actions.add(back);
        actions.add(cartButton);
        header.add(actions, BorderLayout.EAST);
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);

        JPanel filters = new JPanel(new GridLayout(1, 6, 8, 0));
        filters.setOpaque(false);
        Theme.styleTextField(search);
        filters.add(new JLabel("Search item:"));
        filters.add(search);
        filters.add(new JLabel("Category:"));
        category.addItem("All categories");
        for (String name : categories.values()) category.addItem(name);
        filters.add(category);
        filters.add(new JLabel("Sort:"));
        filters.add(sort);
        top.add(filters, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Theme.LIGHT);
        JScrollPane scroll = new JScrollPane(menuPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        root.add(scroll, BorderLayout.CENTER);

        status.setForeground(Theme.SUCCESS);
        status.setBorder(new EmptyBorder(2, 4, 2, 4));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(status, BorderLayout.WEST);
        root.add(bottom, BorderLayout.PAGE_END);
        add(root);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshMenu(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshMenu(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshMenu(); }
        });
        category.addActionListener(e -> refreshMenu());
        sort.addActionListener(e -> refreshMenu());
    }

    private void refreshMenu() {
        menuPanel.removeAll();
        ArrayList<MenuItem> filtered = new ArrayList<>();
        String query = search.getText().trim().toLowerCase();
        String selectedCategory = (String) category.getSelectedItem();
        for (MenuItem item : menu) {
            String categoryName = categories.get(item.getCategoryId());
            boolean matchesText = item.getItemName().toLowerCase().contains(query);
            boolean matchesCategory = "All categories".equals(selectedCategory)
                    || selectedCategory.equals(categoryName);
            if (matchesText && matchesCategory) filtered.add(item);
        }
        String selectedSort = (String) sort.getSelectedItem();
        if ("Price: Low to High".equals(selectedSort)) filtered.sort(Comparator.comparing(MenuItem::getPrice));
        else if ("Price: High to Low".equals(selectedSort))
            filtered.sort(Comparator.comparing(MenuItem::getPrice).reversed());
        else filtered.sort(Comparator.comparing(MenuItem::getItemName));
        for (MenuItem item : filtered) menuPanel.add(createItemCard(item));
        cartButton.setText("View Cart (" + CartManager.getCart().size() + ")");
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private JPanel createItemCard(MenuItem item) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(10, 4));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(item.getItemName() + "  |  Rs. " + item.getPrice());
        name.setFont(Theme.headingFont());
        name.setForeground(Theme.DARK);
        text.add(name);
        text.add(new JLabel(item.getDescription() == null ? "" : item.getDescription()));
        card.add(text, BorderLayout.CENTER);
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        RoundedButton add = new RoundedButton("Add");
        add.addActionListener(e -> {
            CartManager.addItem(item.getItemId(), item.getRestaurantId(),
                    restaurant.getRestaurantName(), item.getItemName(), item.getPrice(),
                    (Integer) quantity.getValue());
            status.setText("Added: " + item.getItemName());
            cartButton.setText("View Cart (" + CartManager.getCart().size() + ")");
        });
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setOpaque(false);
        controls.add(quantity);
        controls.add(add);
        card.add(controls, BorderLayout.EAST);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }
}
