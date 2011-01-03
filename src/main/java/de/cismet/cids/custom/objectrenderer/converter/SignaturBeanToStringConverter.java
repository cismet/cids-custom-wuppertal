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

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class SignaturBeanToStringConverter extends Converter<CidsBean, String> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convertForward(final CidsBean value) {
        if (value != null) {
            return String.valueOf(value.getProperty("definition"));
        }
        return "-";
    }

    @Override
    public CidsBean convertReverse(final String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
