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
public class DoubleNumberConverter extends Converter<Double, String> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(DoubleNumberConverter.class);
    private static final DoubleNumberConverter INSTANCE = new DoubleNumberConverter();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DoubleNumberConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String convertForward(final Double value) {
        return value.toString();
    }

    @Override
    public Double convertReverse(final String value) {
        try {
            if (value == null) {
                return null;
            }

            return new Double(value.replace(',', '.'));
        } catch (final NumberFormatException e) {
            LOG.warn("No valid number: " + value, e); // NOI18N

            return null;
        }
    }
}
