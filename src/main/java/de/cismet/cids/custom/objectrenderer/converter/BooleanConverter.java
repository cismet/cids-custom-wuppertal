/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class BooleanConverter extends Converter<Boolean, Boolean> {

    @Override
    public Boolean convertForward(Boolean bool) {
        if (bool == null) {
            return Boolean.FALSE;
        } else {
            return bool;
        }
    }

    @Override
    public Boolean convertReverse(Boolean bool) {
        if (bool == null) {
            return Boolean.FALSE;
        } else {
            return bool;
        }
    }
}
