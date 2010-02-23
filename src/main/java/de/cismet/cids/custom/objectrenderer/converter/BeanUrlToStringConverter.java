/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.dynamics.CidsBean;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class BeanUrlToStringConverter extends Converter<CidsBean, String> {


    public BeanUrlToStringConverter(String suffix) {
        if (suffix != null) {
            this.suffix = suffix;
        } else {
            this.suffix = "";
        }
    }

    public BeanUrlToStringConverter() {
        suffix = "";
    }
    private String suffix;

    @Override
    public String convertForward(CidsBean bean) {
        return ObjectRendererUtils.getUrlFromBean(bean, suffix);
    }

    @Override
    public CidsBean convertReverse(String arg0) {
        return null;
    }
}
