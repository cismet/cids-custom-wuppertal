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
package de.cismet.cids.custom.objecteditors.commons;

import java.awt.Component;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class JaNeinNullCombo extends JComboBox {

    //~ Instance fields --------------------------------------------------------

    DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JaNeinNullCombo object.
     */
    public JaNeinNullCombo() {
        super(new DefaultComboBoxModel(new Vector(Arrays.asList("Ja", "Nein", null))));
        setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    if (value != null) {
                        return dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    } else {
                        final JLabel nullLabel = (JLabel)dlcr.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus);
                        nullLabel.setText("nicht ausgewählt");
                        return nullLabel;
                    }
                }
            });
        setSelectedItem(null);
    }
}
