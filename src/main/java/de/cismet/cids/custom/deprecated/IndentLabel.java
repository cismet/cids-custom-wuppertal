package de.cismet.cids.custom.deprecated;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

/**
 * @author Daniel Spiewak
 */
public class IndentLabel extends JLabel {
    private String text;

    @InjectedResource
    private Color accentColor, textColor;

    private static final float OFFSET = 1;

    public IndentLabel() {
        this("");
    }

    public IndentLabel(String text) {
            this.text = text;
            ResourceInjector.get("coolpanel.style").inject(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD).deriveFont((float) g2.getFont().getSize() + 1));

            float x = 2;
            float y = getHeight() - ((getHeight() - g2.getFontMetrics().getHeight()) / 2) + OFFSET;

            g2.setColor(accentColor);
            g2.drawString(text, x, y);

            y -= OFFSET;
            g2.setColor(textColor);

            g2.drawString(text, x, y);
    }

    public String getText() {
            return text;
    }

    public void setText(String text) {
            this.text = text;
            repaint();
    }

    @Override
    public Dimension getMinimumSize() {
            return new Dimension(super.getMinimumSize().width, super.getMinimumSize().height+2);
    }

    @Override
    public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height+2);
    }

    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height+2));
    }
}
