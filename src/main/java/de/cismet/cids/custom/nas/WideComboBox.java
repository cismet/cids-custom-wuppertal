/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.nas;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WideComboBox extends JComboBox {

    //~ Instance fields --------------------------------------------------------

    private boolean layingOut = false;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Dimension getSize() {
        final Dimension dim = super.getSize();
        if (!layingOut) {
            dim.width = Math.max(dim.width, popupWider());
        }
        return dim;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected int popupWider() {
        final BasicComboPopup popup = (BasicComboPopup)this.getAccessibleContext().getAccessibleChild(0);
        final JList list = popup.getList();
        final Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

        final JScrollPane scrollPane = (JScrollPane)c;

        // Determine the maximimum width to use:
        // a) determine the popup preferred width
        // b) limit width to the maximum if specified
        // c) ensure width is not less than the scroll pane width
        int popupWidth = list.getPreferredSize().width
                    + 5 // make sure horizontal scrollbar doesn't appear
                    + getScrollBarWidth(scrollPane);

//            if (maximumWidth != -1) {
//                popupWidth = Math.min(popupWidth, maximumWidth);
//            }
        final Dimension scrollPaneSize = scrollPane.getPreferredSize();
        popupWidth = Math.max(popupWidth, scrollPaneSize.width);

        // Adjust the width
        return popupWidth;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   scrollPane  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected int getScrollBarWidth(final JScrollPane scrollPane) {
        int scrollBarWidth = 0;

        if (this.getItemCount() > this.getMaximumRowCount()) {
            final JScrollBar vertical = scrollPane.getVerticalScrollBar();
            scrollBarWidth = vertical.getPreferredSize().width;
        }

        return scrollBarWidth;
    }
}
