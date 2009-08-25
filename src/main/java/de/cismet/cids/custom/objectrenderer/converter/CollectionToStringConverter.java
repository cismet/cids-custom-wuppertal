/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import de.cismet.cids.dynamics.CidsBean;
import java.util.List;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class CollectionToStringConverter extends Converter<List<CidsBean>, String> {

    private final String propName;
    private final String separator;
    private final String prefix;
    private final String postfix;

    public static final CollectionToStringConverter getHtmlTaggingCollectionConverter(String property, String separator) {
        return new CollectionToStringConverter(property, separator, "<html>", "</html>");
    }

    public CollectionToStringConverter(final String propName) {
        this(propName, ", ", "", "");
    }

    public CollectionToStringConverter(final String propName, final String separator) {
        this(propName, separator, "", "");
    }

    public CollectionToStringConverter(final String propName, final String separator, final String prefix, final String postfix) {
        if (propName == null || separator == null || prefix == null || postfix == null) {
            throw new IllegalArgumentException();
        }
        this.propName = propName;
        this.separator = separator;
        this.prefix = prefix;
        this.postfix = postfix;
    }

    @Override
    public String convertForward(final List<CidsBean> list) {
        if (list != null && !list.isEmpty()) {
            final StringBuffer sb = new StringBuffer(prefix);
            for (final CidsBean sw : list) {
                sb.append(sw.getProperty(propName)).append(separator);
            }
            if (sb.length() > 1 + prefix.length()) {
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append(postfix);
            return sb.toString();
        }
        return null;
    }

    @Override
    public List<CidsBean> convertReverse(String arg0) {
        throw new UnsupportedOperationException();
    }
}
