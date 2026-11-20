package ui;

import javax.swing.*;
import java.awt.*;

/** A reusable rounded card panel for restaurants and other content. */
public class RoundedPanel extends JPanel {
    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
    }

    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.CARD);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g.setColor(new Color(220, 224, 228));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g.dispose();
        super.paintComponent(graphics);
    }
}
