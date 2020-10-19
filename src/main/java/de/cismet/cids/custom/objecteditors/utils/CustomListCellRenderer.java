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
package de.cismet.cids.custom.objecteditors.utils;

/**
 *
 * @author daniel
 */

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CustomListCellRenderer extends DefaultListCellRenderer {

    //~ Instance fields --------------------------------------------------------

    private final ListCellRenderer origRenderer;
    private final ColorLabel component = new ColorLabel();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomListCellRenderer object.
     *
     * @param  origRenderer  DOCUMENT ME!
     */
    public CustomListCellRenderer(final ListCellRenderer origRenderer) {
        this.origRenderer = origRenderer;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ListCellRenderer getOrigRenderer() {
        return origRenderer;
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        if (value != null) {
            component.setText(
                ((JLabel)origRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus))
                            .getText());
        } else {
            component.setText("");
        }
        return component;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class ColorLabel extends JLabel {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setForeground(final Color fg) {
            // ignore
        }
        @Override
        public void setBackground(final Color fg) {
            // ignore
        }
        /**
         * DOCUMENT ME!
         *
         * @param  bg  DOCUMENT ME!
         */
        public void setBackground_internal(final Color bg) {
            super.setBackground(bg);
        }
        /**
         * DOCUMENT ME!
         *
         * @param  fg  DOCUMENT ME!
         */
        public void setForeground_internal(final Color fg) {
            super.setForeground(fg);
        }
    }
}
