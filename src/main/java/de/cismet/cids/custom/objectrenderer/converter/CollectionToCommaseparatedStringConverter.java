/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import de.cismet.cids.dynamics.CidsBean;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class CollectionToCommaseparatedStringConverter extends Converter<List<CidsBean>, String> {

    private final String propName;

    public CollectionToCommaseparatedStringConverter(final String propName) {
        if (propName == null) {
            throw new IllegalArgumentException();
        }
        this.propName = propName;
    }

    @Override
    public String convertForward(final List<CidsBean> list) {
        final StringBuffer sb = new StringBuffer();
        for (final CidsBean sw : list) {
            sb.append(sw.getProperty(propName)).append(", ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    @Override
    public List<CidsBean> convertReverse(String arg0) {
        return new ArrayList<CidsBean>();
    }
}
