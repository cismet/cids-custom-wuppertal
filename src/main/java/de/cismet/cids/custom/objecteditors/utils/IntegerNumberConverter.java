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
package de.cismet.cids.custom.objecteditors.utils;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class IntegerNumberConverter extends Converter<Integer, String> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NumberConverter.class);
    private static final IntegerNumberConverter INSTANCE = new IntegerNumberConverter();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static IntegerNumberConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String convertForward(final Integer value) {
        return value.toString();
    }

    @Override
    public Integer convertReverse(final String value) {
        try {
            if (value == null) {
                return null;
            }

            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            LOG.warn("No valid integer number: " + value, e); // NOI18N
            return null;
        }
    }
}
