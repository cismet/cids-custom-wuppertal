/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.objectrenderer.converter;

import de.cismet.cids.dynamics.CidsBean;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class SignaturBeanToStringConverter extends Converter<CidsBean, String>{

    @Override
    public String convertForward(CidsBean value) {
        if(value != null) {
            return String.valueOf(value.getProperty("definition"));
        }
        return "-";
    }

    @Override
    public CidsBean convertReverse(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
