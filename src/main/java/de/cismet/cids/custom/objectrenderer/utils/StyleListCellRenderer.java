/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * DOCUMENT ME!
 *
 * @author   flughafen
 * @version  $Revision$, $Date$
 */
public class StyleListCellRenderer extends DefaultListCellRenderer {

    //~ Instance fields --------------------------------------------------------

    private final Color COLOR_ODD;
    private final Color COLOR_EVEN;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StyleListCellRenderer object.
     */
    public StyleListCellRenderer() {
        COLOR_ODD = new Color(230, 230, 230);
        COLOR_EVEN = new Color(210, 210, 210);
    }

    /**
     * Creates a new StyleListCellRenderer object.
     *
     * @param  COLOR_ODD   DOCUMENT ME!
     * @param  COLOR_EVEN  DOCUMENT ME!
     */
    public StyleListCellRenderer(final Color COLOR_ODD, final Color COLOR_EVEN) {
        this.COLOR_ODD = COLOR_ODD;
        this.COLOR_EVEN = COLOR_EVEN;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (!isSelected) {
            c.setBackground(((index % 2) != 0) ? COLOR_ODD : COLOR_EVEN);
        }
        return c;
    }
}
