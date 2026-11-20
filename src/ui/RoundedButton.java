package ui;

import javax.swing.*;
import java.awt.*;

/** A simple button with rounded corners and a hover color. */
public class RoundedButton extends JButton {
    private boolean mouseOver;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(Theme.buttonFont());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                mouseOver = true;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                mouseOver = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(mouseOver ? Theme.PRIMARY_DARK : Theme.PRIMARY);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g.dispose();
        super.paintComponent(graphics);
    }
}
