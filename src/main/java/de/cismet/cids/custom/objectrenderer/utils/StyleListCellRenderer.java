/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author flughafen
 */
public class StyleListCellRenderer extends DefaultListCellRenderer {

    public StyleListCellRenderer(Color COLOR_ODD, Color COLOR_EVEN) {
        this.COLOR_ODD = COLOR_ODD;
        this.COLOR_EVEN = COLOR_EVEN;
    }

    public StyleListCellRenderer() {
        COLOR_ODD = new Color(230, 230, 230);
        COLOR_EVEN = new Color(210, 210, 210);
    }
    private final Color COLOR_ODD;
    private final Color COLOR_EVEN;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (!isSelected) {
            c.setBackground(index % 2 != 0 ? COLOR_ODD : COLOR_EVEN);
        }
        return c;
    }
}
