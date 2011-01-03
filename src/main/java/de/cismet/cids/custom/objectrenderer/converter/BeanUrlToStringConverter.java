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

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class BeanUrlToStringConverter extends Converter<CidsBean, String> {

    //~ Instance fields --------------------------------------------------------

    private String suffix;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BeanUrlToStringConverter object.
     */
    public BeanUrlToStringConverter() {
        suffix = "";
    }

    /**
     * Creates a new BeanUrlToStringConverter object.
     *
     * @param  suffix  DOCUMENT ME!
     */
    public BeanUrlToStringConverter(final String suffix) {
        if (suffix != null) {
            this.suffix = suffix;
        } else {
            this.suffix = "";
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convertForward(final CidsBean bean) {
        return ObjectRendererUtils.getUrlFromBean(bean, suffix);
    }

    @Override
    public CidsBean convertReverse(final String arg0) {
        return null;
    }
}
