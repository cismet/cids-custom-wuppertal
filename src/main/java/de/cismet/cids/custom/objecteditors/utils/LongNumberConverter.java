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
 * @version  $Revision$, $Date$
 */
public class LongNumberConverter extends Converter<Integer, Long> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NumberConverter.class);
    private static final LongNumberConverter INSTANCE = new LongNumberConverter();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LongNumberConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public Integer convertReverse(final Long value) {
        if (value == null) {
            return null;
        }
        return value.intValue();
    }

    @Override
    public Long convertForward(final Integer value) {
        try {
            if (value == null) {
                return null;
            }

            return value.longValue();
        } catch (final NumberFormatException e) {
            LOG.warn("No valid long number: " + value, e); // NOI18N
            return null;
        }
    }
}
