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
package de.cismet.cids.custom.clientutils;

import java.text.ParseException;

import javax.swing.text.DefaultFormatter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class HexcolorFormatter extends DefaultFormatter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String valueToString(final Object value) throws ParseException {
        if ((value instanceof String) && ((String)value).matches("^#[a-fA-F0-9]{0,6}$")) {
            return ((String)value).toUpperCase();
        } else {
            return "";
        }
    }

    @Override
    public Object stringToValue(final String string) throws ParseException {
        if ((string == null) || string.trim().isEmpty()) {
            return null;
        } else if (string.matches("^#[a-fA-F0-9]{0,6}$")) {
            return string.toUpperCase();
        } else {
            throw new ParseException("invalid color hex code: " + string, -1);
        }
    }
}
