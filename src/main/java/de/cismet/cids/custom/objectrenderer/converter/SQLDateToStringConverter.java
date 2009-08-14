/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class SQLDateToStringConverter extends Converter<java.sql.Date, String> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String convertForward(java.sql.Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    @Override
    public java.sql.Date convertReverse(String dateString) {
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
