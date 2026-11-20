package gui;

import model.CartItem;
import ui.RoundedButton;
import ui.RoundedPanel;
import ui.Theme;
import util.CartManager;
import util.WindowHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CartFrame extends JFrame {
    private final JPanel itemsPanel = new JPanel();
    private final JLabel totalLabel = new JLabel();

    public CartFrame() {
        setTitle("Shopping Cart");
        setSize(750, 650);
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Theme.LIGHT);
        root.setBorder(new EmptyBorder(15, 18, 15, 18));
        JLabel title = new JLabel("Your Cart");
        title.setFont(Theme.titleFont());
        title.setForeground(Theme.DARK);
        root.add(title, BorderLayout.NORTH);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Theme.LIGHT);
        root.add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        totalLabel.setFont(Theme.headingFont());
        bottom.add(totalLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        RoundedButton continueShopping = new RoundedButton("Continue Shopping");
        RoundedButton home = new RoundedButton("Back to Restaurants");
        RoundedButton checkout = new RoundedButton("Checkout");
        continueShopping.addActionListener(e -> {
            dispose();
            if (CartManager.getLastRestaurant() == null) new HomeFrame();
            else new MenuFrame(CartManager.getLastRestaurant());
        });
        home.addActionListener(e -> { dispose(); new HomeFrame(); });
        checkout.addActionListener(e -> {
            if (CartManager.getCart().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.");
                return;
            }
            dispose();
            new ReceiptFrame();
        });
        buttons.add(continueShopping);
        buttons.add(home);
        buttons.add(checkout);
        bottom.add(buttons, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);
        add(root);
        refreshItems();
        setVisible(true);
    }

    private void refreshItems() {
        itemsPanel.removeAll();
        for (Map.Entry<String, List<CartItem>> group : CartManager.groupByRestaurant().entrySet()) {
            JLabel restaurant = new JLabel(group.getKey());
            restaurant.setFont(Theme.headingFont());
            restaurant.setForeground(Theme.DARK);
            restaurant.setBorder(new EmptyBorder(10, 4, 5, 4));
            itemsPanel.add(restaurant);
            for (CartItem item : group.getValue()) itemsPanel.add(createItemRow(item));
        }
        totalLabel.setText("Total: Rs. " + CartManager.getTotal());
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel createItemRow(CartItem item) {
        RoundedPanel row = new RoundedPanel(new BorderLayout(8, 0));
        row.add(new JLabel(item.getItemName() + "  |  Rs. " + item.getPrice()), BorderLayout.CENTER);
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(item.getQuantity(), 1, 20, 1));
        quantity.addChangeListener(e -> {
            item.setQuantity((Integer) quantity.getValue());
            totalLabel.setText("Total: Rs. " + CartManager.getTotal());
        });
        RoundedButton remove = new RoundedButton("Remove");
        remove.addActionListener(e -> {
            CartManager.removeItem(item.getMenuId());
            refreshItems();
        });
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setOpaque(false);
        controls.add(quantity);
        controls.add(remove);
        row.add(controls, BorderLayout.EAST);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }
}
