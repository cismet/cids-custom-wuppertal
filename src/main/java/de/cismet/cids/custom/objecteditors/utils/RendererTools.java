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

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.el.impl.ValueExpressionImpl;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.lang.reflect.Field;

import java.text.DecimalFormat;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
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

    private static final Logger LOG = Logger.getLogger(RendererTools.class);
    public static final Color ERROR_BACKGROUND = new Color(250, 215, 216);
    public static final Color ERROR_FORGROUND = new Color(145, 32, 32);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  bindingGroup  DOCUMENT ME!
     * @param  baseProp      DOCUMENT ME!
     */
    public static void makeReadOnly(final BindingGroup bindingGroup, final String baseProp) {
        final List<Binding> bindings = bindingGroup.getBindings();
        for (final Binding binding : bindings) {
            if ((binding != null) && (binding.getTargetObject() instanceof JComponent)) {
                final JComponent target = (JComponent)binding.getTargetObject();
                final ELProperty p = (ELProperty)binding.getSourceProperty();

                try {
                    final Field expressionField = p.getClass().getDeclaredField("expression"); // NOI18N
                    expressionField.setAccessible(true);

                    final ValueExpressionImpl valueExpression = (ValueExpressionImpl)expressionField.get(p);

                    final String expr = valueExpression.getExpressionString();
                    if (expr.substring(2, expr.length() - 1).startsWith(baseProp + ".")) {
                        makeReadOnly(target);
                    }
                } catch (final IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                            | SecurityException ex) {
                    LOG.warn("", ex);
                }
            }
        }
    }

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
        } else if (comp instanceof JSpinner) {
            final JSpinner sp = (JSpinner)comp;
            sp.setOpaque(false);
            sp.setBorder(null);
            sp.getEditor().setOpaque(false);
            ((JSpinner.DefaultEditor)sp.getEditor()).getTextField().setOpaque(false);
        } else if (comp instanceof DefaultBindableDateChooser) {
            final DefaultBindableDateChooser dc = (DefaultBindableDateChooser)comp;
            dc.setEditable(false);
            ((Component)dc.getComponents()[1]).setVisible(false);
            ((JFormattedTextField)dc.getComponents()[0]).setOpaque(false);
            ((JFormattedTextField)dc.getComponents()[0]).setBorder(null);
        } else if (comp instanceof JCheckBox) {
            ((JCheckBox)comp).setUI(new BasicCheckBoxUI() {

                    @Override
                    protected BasicButtonListener createButtonListener(final AbstractButton b) {
                        return null;
                    }
                });
        } else if (comp instanceof JXTable) {
            final JXTable jxt = (JXTable)comp;
            jxt.setEditable(false);
            ((DefaultTableRenderer)jxt.getDefaultRenderer(Object.class)).setBackground(new Color(0, 0, 0, 0));
            jxt.setOpaque(false);
            jxt.setGridColor(Color.GRAY);
            jxt.setBackground(new Color(0, 0, 0, 0));
        } else if (comp instanceof JList) {
            final JList jl = (JList)comp;
            jl.setOpaque(false);
            jl.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList<?> list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        // setForeground(Color.WHITE);
                        setOpaque(isSelected);
                        return this;
                    }
                });
        } else if (comp != null) {
            comp.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  spinner  DOCUMENT ME!
     * @param  digits   DOCUMENT ME!
     */
    public static void makeDoubleSpinnerWithoutButtons(final JSpinner spinner, final int digits) {
        spinner.setUI(new BasicSpinnerUI() {

                @Override
                protected Component createNextButton() {
                    return null;
                }

                @Override
                protected Component createPreviousButton() {
                    return null;
                }
            });

        final JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();

        final DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(digits);
        format.setMaximumFractionDigits(digits);
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
     * @param  spinner  DOCUMENT ME!
     */
    public static void jSpinnerShouldLookLikeLabel(final JSpinner spinner) {
        spinner.setEnabled(false);
        spinner.setUI(new EmtpySpinnerUI());
        final Component editorComponent = spinner.getEditor();
        if (editorComponent instanceof JSpinner.NumberEditor) {
            final JSpinner.NumberEditor editor = (JSpinner.NumberEditor)editorComponent;
            editor.getTextField().setDisabledTextColor(Color.BLACK);
            editor.getTextField().setOpaque(false);
            editor.getTextField().setBorder(null);
        }
        spinner.getEditor().setOpaque(false);
        spinner.getEditor().setBorder(null);
        spinner.setBorder(null);
        spinner.setOpaque(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  combobox  DOCUMENT ME!
     */
    public static void jComboboxShouldLookLikeLabel(final JComboBox combobox) {
        combobox.setEnabled(false);
        combobox.setUI(new EmptyComboBoxUI());
        final Component editorComponent = combobox.getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            final JTextComponent editor = (JTextComponent)editorComponent;
            editor.setDisabledTextColor(Color.BLACK);
            editor.setOpaque(false);
            editor.setBorder(null);
        }
        combobox.setBorder(null);
        combobox.setOpaque(false);
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class EmtpySpinnerUI extends BasicSpinnerUI {

        //~ Methods ------------------------------------------------------------

        @Override
        protected Component createNextButton() {
            return null;
        }

        @Override
        protected Component createPreviousButton() {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class EmptyComboBoxUI extends BasicComboBoxUI {

        //~ Methods ------------------------------------------------------------

        @Override
        protected JButton createArrowButton() {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class NoTabTextAreaKeyAdapter extends KeyAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void keyPressed(final KeyEvent evt) {
            final JTextArea textArea = (JTextArea)evt.getComponent();
            if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                if (evt.getModifiers() > 0) {
                    textArea.transferFocusBackward();
                } else {
                    textArea.transferFocus();
                }
                evt.consume();
            }
        }
    }
}
