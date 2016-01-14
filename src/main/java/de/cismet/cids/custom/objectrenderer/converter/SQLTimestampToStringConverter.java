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
public class SQLTimestampToStringConverter extends Converter<java.sql.Timestamp, String> {

    //~ Static fields/initializers ---------------------------------------------

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:sss");

    //~ Instance fields --------------------------------------------------------

    private final DateFormat dateFormat;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SQLDateToStringConverter object.
     */
    public SQLTimestampToStringConverter() {
        this(DATE_FORMAT);
    }

    /**
     * Creates a new SQLDateToStringConverter object.
     *
     * @param  simpleDateFormatString  DOCUMENT ME!
     */
    public SQLTimestampToStringConverter(final String simpleDateFormatString) {
        this(new SimpleDateFormat(simpleDateFormatString));
    }

    /**
     * Creates a new SQLDateToStringConverter object.
     *
     * @param  dateFormat  DOCUMENT ME!
     */
    public SQLTimestampToStringConverter(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convertForward(final java.sql.Timestamp date) {
        if (date == null) {
            return "";
        }
        return dateFormat.format(date);
    }

    @Override
    public java.sql.Timestamp convertReverse(final String dateString) {
        final java.util.Date uDate;
        try {
            uDate = dateFormat.parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
        return new java.sql.Timestamp(uDate.getTime());
    }
}
