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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import org.jdesktop.swingx.JXDatePicker;

import org.openide.util.NbBundle;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class StichtagChooserDialog extends JDialog implements PropertyChangeListener {

    //~ Instance fields --------------------------------------------------------

    private JXDatePicker datepicker;
    private JOptionPane optionPane;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StichtagsChooser object.
     *
     * @param  owner  DOCUMENT ME!
     */
    public StichtagChooserDialog(final Frame owner) {
        super(owner, true);
        this.setSize(380, 120);
        setTitle(NbBundle.getMessage(StichtagChooserDialog.class, "StichtagChooserDialog.title"));
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        datepicker = new JXDatePicker(cal.getTime());
        datepicker.getMonthView().setUpperBound(cal.getTime());
        // this enables direct switching of the year...
        datepicker.getMonthView().setZoomable(true);
        final Object[] array = {
                NbBundle.getMessage(StichtagChooserDialog.class, "StichtagChooserDialog.message"),
                datepicker
            };
        optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        optionPane.addPropertyChangeListener(this);
        addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(final WindowEvent we) {
                    /*
                     * Instead of directly closing the window, we're going to change the JOptionPane's value property.
                     */
                    optionPane.setValue(new Integer(
                            JOptionPane.CLOSED_OPTION));
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getDate() {
        return datepicker.getDate();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent e) {
        final String prop = e.getPropertyName();

        if (isVisible()
                    && (e.getSource() == optionPane)
                    && (JOptionPane.VALUE_PROPERTY.equals(prop)
                        || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            final Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                // ignore reset
                return;
            }

            // Reset the JOptionPane's value.
            // If you don't do this, then if the user
            // presses the same button next time, no
            // property change event will be fired.
            optionPane.setValue(
                JOptionPane.UNINITIALIZED_VALUE);

            if (value.equals(JOptionPane.OK_OPTION)) {
                final Date d = datepicker.getDate();
                final GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                if (d.before(cal.getTime())) {
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        NbBundle.getMessage(
                            StichtagChooserDialog.class,
                            "StichtagChooserDialog.preventFutureStichtagMessageDialog.message"));
                }
            } else { // user closed dialog or clicked cancel
                datepicker.setDate(null);
                setVisible(false);
            }
        }
    }
}
