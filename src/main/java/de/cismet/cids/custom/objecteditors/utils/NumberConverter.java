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

import org.jdesktop.beansbinding.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class NumberConverter extends Converter<Float, String> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NumberConverter.class);
    private static final NumberConverter INSTANCE = new NumberConverter();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NumberConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String convertForward(final Float value) {
        return value.toString();
    }

    @Override
    public Float convertReverse(final String value) {
        try {
            if (value == null) {
                return null;
            }

            return new Float(value.replace(',', '.'));
        } catch (final NumberFormatException e) {
            LOG.warn("No valid number: " + value, e); // NOI18N

            return null;
        }
    }
}
