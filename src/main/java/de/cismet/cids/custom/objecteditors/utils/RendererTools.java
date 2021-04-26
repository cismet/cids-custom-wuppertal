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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.lang.reflect.Field;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.text.JTextComponent;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.EditorAndRendererComponent;

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
     * @param   bindingGroup  DOCUMENT ME!
     * @param   baseProp      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Collection<JComponent> getAllBindedComponents(final BindingGroup bindingGroup,
            final String baseProp) {
        final Collection<JComponent> allBindedComponents = new ArrayList<>();
        if (bindingGroup != null) {
            final List<Binding> bindings = bindingGroup.getBindings();
            for (final Binding binding : bindings) {
                if ((binding != null) && (binding.getTargetObject() instanceof JComponent)) {
                    final JComponent bindedComponent = (JComponent)binding.getTargetObject();
                    final ELProperty p = (ELProperty)binding.getSourceProperty();

                    try {
                        final Field expressionField = p.getClass().getDeclaredField("expression"); // NOI18N
                        expressionField.setAccessible(true);

                        final ValueExpressionImpl valueExpression = (ValueExpressionImpl)expressionField.get(p);

                        final String expr = valueExpression.getExpressionString();
                        if (expr.substring(2, expr.length() - 1).startsWith(baseProp + ".")) {
                            allBindedComponents.add(bindedComponent);
                        }
                    } catch (final IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                                | SecurityException ex) {
                        LOG.warn("", ex);
                    }
                }
            }
        }
        return allBindedComponents;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bindingGroup  DOCUMENT ME!
     * @param  baseProp      DOCUMENT ME!
     */
    public static void makeReadOnly(final BindingGroup bindingGroup, final String baseProp) {
        makeReadOnly(bindingGroup, baseProp, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bindingGroup  DOCUMENT ME!
     * @param  baseProp      DOCUMENT ME!
     * @param  asRenderer    DOCUMENT ME!
     */
    public static void makeReadOnly(final BindingGroup bindingGroup,
            final String baseProp,
            final boolean asRenderer) {
        for (final JComponent component : getAllBindedComponents(bindingGroup, baseProp)) {
            makeReadOnly(component, asRenderer);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bindingGroup  DOCUMENT ME!
     * @param  baseProp      DOCUMENT ME!
     * @param  readOnly      DOCUMENT ME!
     */
    public static void makeUneditable(final BindingGroup bindingGroup, final String baseProp, final boolean readOnly) {
        for (final JComponent component : getAllBindedComponents(bindingGroup, baseProp)) {
            makeUneditable(component, readOnly);
        }
        if (bindingGroup != null) {
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
                        }
                    } catch (final IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                                | SecurityException ex) {
                        LOG.warn("", ex);
                    }
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
        makeReadOnly(comp, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  comp        DOCUMENT ME!
     * @param  asRenderer  DOCUMENT ME!
     */
    public static void makeReadOnly(final JComponent comp, final boolean asRenderer) {
        if (comp instanceof EditorAndRendererComponent) {
            final EditorAndRendererComponent er = (EditorAndRendererComponent)comp;
            er.setActingAsRenderer(asRenderer);
        } else {
            makeUneditable(comp, asRenderer);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  comp      DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public static void makeUneditable(final JComponent comp, final boolean readOnly) {
        if (comp != null) {
            final UneditableMarkerComp newUneditableMarker;
            UneditableMarkerComp oldUneditableMarker = null;

            if (readOnly) {
                newUneditableMarker = new UneditableMarkerComp();
                comp.addComponentListener(newUneditableMarker);
            } else {
                for (final ComponentListener subComp : comp.getComponentListeners()) {
                    if (subComp instanceof UneditableMarkerComp) {
                        oldUneditableMarker = (UneditableMarkerComp)subComp;
                        break;
                    }
                }
                if (oldUneditableMarker != null) {
                    comp.removeComponentListener(oldUneditableMarker);
                }
                newUneditableMarker = null;
            }

            if (comp instanceof JTextArea) {
                final JTextArea tComp = (JTextArea)comp;
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JTextArea dummy = new JTextArea();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setBorder(tComp.getBorder());
                    }
                    tComp.setBorder(new EmptyBorder(0, 0, 0, 0));
                } else {
                    if (oldUneditableMarker != null) {
                        final JTextArea dummy = (JTextArea)oldUneditableMarker.getDummy();
                        tComp.setBorder(dummy.getBorder());
                    }
                }
                tComp.setEditable(!readOnly);
                tComp.setOpaque(!readOnly);
            } else if (comp instanceof JTextField) {
                final JTextField tComp = (JTextField)comp;
                tComp.setEditable(!readOnly);
                tComp.setOpaque(!readOnly);
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JTextField dummy = new JTextField();
                        dummy.setBorder(tComp.getBorder());
                        newUneditableMarker.setDummy(dummy);
                    }
                    tComp.setBorder(new EmptyBorder(0, 0, 0, 0));
                } else {
                    if (oldUneditableMarker != null) {
                        final JTextField dummy = (JTextField)oldUneditableMarker.getDummy();
                        tComp.setBorder(dummy.getBorder());
                    }
                }
            } else if (comp instanceof JScrollPane) {
                final JScrollPane jsp = (JScrollPane)comp;
                jsp.setOpaque(!readOnly);
                jsp.getViewport().setOpaque(!false);
            } else if (comp instanceof JComboBox) {
                final JComboBox cb = (JComboBox)comp;
                cb.setEnabled(!readOnly);
                if (readOnly) {
                    cb.setRenderer(new CustomListCellRenderer((ListCellRenderer)cb.getRenderer()));
                } else if (cb.getRenderer() instanceof CustomListCellRenderer) {
                    cb.setRenderer(((CustomListCellRenderer)cb.getRenderer()).getOrigRenderer());
                }
            } else if (comp instanceof JSpinner) {
                final JSpinner sp = (JSpinner)comp;
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JSpinner dummy = new JSpinner();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setOpaque(sp.isOpaque());
                        dummy.setBorder(sp.getBorder());
                        dummy.getEditor().setOpaque(sp.getEditor().isOpaque());
                        ((JSpinner.DefaultEditor)dummy.getEditor()).getTextField()
                                .setOpaque(((JSpinner.DefaultEditor)sp.getEditor()).getTextField().isOpaque());
                    }
                    sp.setOpaque(false);
                    sp.setBorder(null);
                    sp.getEditor().setOpaque(false);
                    ((JSpinner.DefaultEditor)sp.getEditor()).getTextField().setOpaque(false);
                } else {
                    if (oldUneditableMarker != null) {
                        final JSpinner dummy = (JSpinner)oldUneditableMarker.getDummy();
                        sp.setOpaque(dummy.isOpaque());
                        sp.setBorder(dummy.getBorder());
                        sp.getEditor().setOpaque(dummy.getEditor().isOpaque());
                        ((JSpinner.DefaultEditor)sp.getEditor()).getTextField()
                                .setOpaque(((JSpinner.DefaultEditor)dummy.getEditor()).getTextField().isOpaque());
                    }
                }
            } else if (comp instanceof DefaultBindableDateChooser) {
                final DefaultBindableDateChooser dc = (DefaultBindableDateChooser)comp;
                final Component arrow = (Component)dc.getComponents()[1];
                final JFormattedTextField textField = (JFormattedTextField)dc.getComponents()[0];
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final DefaultBindableDateChooser dummy = new DefaultBindableDateChooser();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setOpaque(textField.isOpaque());
                        dummy.setBorder(textField.getBorder());
                    }

                    textField.setOpaque(false);
                    textField.setBorder(null);
                } else {
                    if (oldUneditableMarker != null) {
                        final DefaultBindableDateChooser dummy = (DefaultBindableDateChooser)
                            oldUneditableMarker.getDummy();
                        textField.setOpaque(dummy.isOpaque());
                        textField.setBorder(dummy.getBorder());
                    }
                }
                dc.setEditable(!readOnly);
                arrow.setVisible(!readOnly);
            } else if (comp instanceof JCheckBox) {
                final JCheckBox cb = (JCheckBox)comp;
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JCheckBox dummy = new JCheckBox();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setUI(cb.getUI());
                    }
                    cb.setUI(new BasicCheckBoxUI() {

                            @Override
                            protected BasicButtonListener createButtonListener(final AbstractButton b) {
                                return null;
                            }
                        });
                } else {
                    if (oldUneditableMarker != null) {
                        final JCheckBox dummy = (JCheckBox)oldUneditableMarker.getDummy();
                        cb.setUI(dummy.getUI());
                    }
                }
            } else if (comp instanceof JXTable) {
                final JXTable jxt = (JXTable)comp;
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JXTable dummy = new JXTable();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setGridColor(jxt.getGridColor());
                        dummy.setBackground(jxt.getBackground());
                    }
                    ((DefaultTableRenderer)jxt.getDefaultRenderer(Object.class)).setBackground(new Color(0, 0, 0, 0));
                    jxt.setGridColor(Color.GRAY);
                    jxt.setBackground(new Color(0, 0, 0, 0));
                } else {
                    if (oldUneditableMarker != null) {
                        final JXTable dummy = (JXTable)oldUneditableMarker.getDummy();
                        jxt.setGridColor(dummy.getGridColor());
                        jxt.setBackground(dummy.getBackground());
                    }
                }
                jxt.setEditable(!readOnly);
                jxt.setOpaque(!readOnly);
            } else if (comp instanceof JList) {
                final JList jl = (JList)comp;
                if (readOnly) {
                    if (newUneditableMarker != null) {
                        final JList dummy = new JList();
                        newUneditableMarker.setDummy(dummy);
                        dummy.setOpaque(jl.isOpaque());
                        dummy.setCellRenderer(jl.getCellRenderer());
                    }
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
                } else {
                    if (oldUneditableMarker != null) {
                        final JList dummy = (JList)oldUneditableMarker.getDummy();
                        jl.setOpaque(dummy.isOpaque());
                        jl.setCellRenderer(dummy.getCellRenderer());
                    }
                }
            } else {
                comp.setEnabled(!readOnly);
            }
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
    private static class UneditableMarkerComp extends ComponentAdapter {

        //~ Instance fields ----------------------------------------------------

        private Component dummy;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  compDummy  DOCUMENT ME!
         */
        public void setDummy(final Component compDummy) {
            this.dummy = compDummy;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Component getDummy() {
            return dummy;
        }
    }

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
