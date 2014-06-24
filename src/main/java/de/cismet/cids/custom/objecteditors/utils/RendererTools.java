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

import org.jdesktop.swingx.JXDatePicker;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.JTextComponent;

import de.cismet.cids.editors.DefaultBindableDateChooser;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class RendererTools {

    //~ Static fields/initializers ---------------------------------------------

    public static final Color ERROR_BACKGROUND = new Color(250, 215, 216);
    public static final Color ERROR_FORGROUND = new Color(145, 32, 32);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  comp  DOCUMENT ME!
     */
    public static void makeReadOnly(final JComponent comp) {
        if (comp instanceof JTextComponent) {
            final JTextComponent tComp = (JTextComponent)comp;
            tComp.setEditable(false);
            tComp.setOpaque(false);
            tComp.setBorder(null);
        } else if (comp instanceof JScrollPane) {
            final JScrollPane jsp = (JScrollPane)comp;
            jsp.setOpaque(false);
            jsp.getViewport().setOpaque(false);
        } else if (comp instanceof JComboBox) {
            final JComboBox cb = (JComboBox)comp;
            cb.setEnabled(false);
            cb.setRenderer(new CustomListCellRenderer());
        } else if (comp instanceof DefaultBindableDateChooser) {
            final DefaultBindableDateChooser dc = (DefaultBindableDateChooser)comp;
            dc.setEnabled(false);
        } else if (comp instanceof JCheckBox) {
            ((JCheckBox)comp).setEnabled(false);
        } else if (comp != null) {
            comp.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  comboBox  DOCUMENT ME!
     */
    public static void makeTextBlackOfDisabledComboBox(final JComboBox comboBox) {
        final Component editorComponent = comboBox.getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            ((JTextComponent)editorComponent).setDisabledTextColor(Color.black);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datePicker  DOCUMENT ME!
     */
    public static void jxDatePickerShouldLookLikeLabel(final JXDatePicker datePicker) {
        datePicker.setEnabled(false);
        datePicker.getEditor().setDisabledTextColor(Color.BLACK);
        datePicker.getEditor().setOpaque(false);
        datePicker.getEditor().setBorder(null);
        // make the button invisible
        datePicker.getComponent(1).setVisible(false);
    }

    /**
     * Changes the appearance of a JComponent in such way that the users sees that an error occurred.
     *
     * @param  comp  DOCUMENT ME!
     */
    public static void showErrorState(final JComponent comp) {
        if (comp instanceof JComboBox) {
            changeOnlyEditorBackgroundAndForegroundColorOfEditableJComboBox((JComboBox)comp,
                ERROR_BACKGROUND,
                ERROR_FORGROUND);
        } else {
            comp.setForeground(ERROR_FORGROUND);
            comp.setBackground(ERROR_BACKGROUND);
        }
    }

    /**
     * The counterpart of showErrorState(). Changes the JComponent back to its default appearance.
     *
     * @param  comp  DOCUMENT ME!
     */
    public static void showNormalState(final JComponent comp) {
        if (comp instanceof JComboBox) {
            changeOnlyEditorBackgroundAndForegroundColorOfEditableJComboBox((JComboBox)comp, Color.white, Color.black);
        } else {
            comp.setForeground(Color.black);
            comp.setBackground(Color.white);
        }
    }

    /**
     * Changes only the background and foreground color of the editor of an editable JComboBox. To achieve this the
     * list, the editor and the button are colored with the backgroundColor and foregroundColor. Afterwards the colors
     * are removed from the list and the button. This seems to be the only way to do that.
     *
     * @param  cb               DOCUMENT ME!
     * @param  backgroundColor  DOCUMENT ME!
     * @param  foregroundColor  DOCUMENT ME!
     */
    public static void changeOnlyEditorBackgroundAndForegroundColorOfEditableJComboBox(final JComboBox cb,
            final Color backgroundColor,
            final Color foregroundColor) {
        cb.getEditor().getEditorComponent().setBackground(backgroundColor);
        cb.getEditor().getEditorComponent().setForeground(foregroundColor);
        final Component[] comps = cb.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof JButton) {
                final JButton coloredArrowsButton = (JButton)comps[i];
                coloredArrowsButton.setBackground(null);
                coloredArrowsButton.setForeground(null);
                break;
            }
        }
        cb.setRenderer(new RemoveColorFromComboBoxListRenderer());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class RemoveColorFromComboBoxListRenderer extends BasicComboBoxRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }

            setFont(list.getFont());

            if (value instanceof Icon) {
                setIcon((Icon)value);
            } else {
                setText((value == null) ? "" : value.toString());
            }
            return this;
        }
    }
}
