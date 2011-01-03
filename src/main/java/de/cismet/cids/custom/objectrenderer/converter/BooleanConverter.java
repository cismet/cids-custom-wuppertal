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

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class BooleanConverter extends Converter<Boolean, Boolean> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Boolean convertForward(final Boolean bool) {
        if (bool == null) {
            return Boolean.FALSE;
        } else {
            return bool;
        }
    }

    @Override
    public Boolean convertReverse(final Boolean bool) {
        if (bool == null) {
            return Boolean.FALSE;
        } else {
            return bool;
        }
    }
}
