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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.jdesktop.swingx.calendar.DatePickerFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.text.DefaultFormatterFactory;

import de.cismet.cids.editors.DefaultBindableDateChooser;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class Alb_baulastBindableDateChooser extends DefaultBindableDateChooser {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            Alb_baulastBindableDateChooser.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastBindableDateChooser object.
     */
    public Alb_baulastBindableDateChooser() {
        super();
        log.fatal("Alb_baulastBindableDateChooser created");
        changeFormatter();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void changeFormatter() {
        final SimpleDateFormat longFormat = new SimpleDateFormat("dd.MM.yyyy");
        final SimpleDateFormat shortFormat = new SimpleDateFormat("dd.MM.yy");
        final Date startDate = new GregorianCalendar(1960, 0, 1).getTime();
        shortFormat.set2DigitYearStart(startDate);

        final DatePickerFormatter formatter = new DatePickerFormatter(

                // invers sequence for parsing to satisfy the year parsing rules
                new DateFormat[] { shortFormat, longFormat }) {

                @Override
                public String valueToString(final Object value) throws ParseException {
                    if (value == null) {
                        return null;
                    }
                    return getFormats()[1].format(value);
                }
            };

        final DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        this.getEditor().setFormatterFactory(factory);
    }
}
