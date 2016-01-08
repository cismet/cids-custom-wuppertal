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
package de.cismet.cids.custom.objectrenderer.converter;

import org.jdesktop.beansbinding.Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class SQLDateToStringConverter extends Converter<java.sql.Date, String> {

    //~ Static fields/initializers ---------------------------------------------

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private final DateFormat dateFormat;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SQLDateToStringConverter object.
     */
    public SQLDateToStringConverter() {
        this(DATE_FORMAT);
    }

    /**
     * Creates a new SQLDateToStringConverter object.
     *
     * @param  simpleDateFormatString  DOCUMENT ME!
     */
    public SQLDateToStringConverter(final String simpleDateFormatString) {
        this(new SimpleDateFormat(simpleDateFormatString));
    }

    /**
     * Creates a new SQLDateToStringConverter object.
     *
     * @param  dateFormat  DOCUMENT ME!
     */
    public SQLDateToStringConverter(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convertForward(final java.sql.Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    @Override
    public java.sql.Date convertReverse(final String dateString) {
        final java.util.Date uDate;
        try {
            uDate = DATE_FORMAT.parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
        final java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }
}
