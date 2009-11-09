/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.converter;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.tools.collections.TypeSafeCollections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdesktop.beansbinding.Converter;

/**
 * inputExpression can be
 * - a single, simple property name, like : "strasse"
 * - a pattern with property names as variables marked by ${}, like: "${strasse} - ${nummer}"
 * @author srichter
 */
public class CollectionToStringConverter extends Converter<List<CidsBean>, String> {
    //matches expessions like ${xyz} and groups their content xyz

    private static final Pattern REGEX = Pattern.compile("\\$\\{(.*?)\\}");
    private final Set<String> foundPropertyNames;
    private final String inputExpression;
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
        this.foundPropertyNames = TypeSafeCollections.newHashSet();
        this.inputExpression = propName;
        this.separator = separator;
        this.prefix = prefix;
        this.postfix = postfix;
        parseStringForReplaceToken();

    }

    private final void parseStringForReplaceToken() {
        final Matcher matcher = REGEX.matcher(inputExpression);
        while (matcher.find()) {
            final String toAdd = matcher.group(matcher.groupCount());
            foundPropertyNames.add(toAdd);
        }
    }

    private final String insertValuesIntoPatternString(CidsBean bean) {
        if (!foundPropertyNames.isEmpty()) {
            String workingCopy = inputExpression;
            for (final String propertyName : foundPropertyNames) {
                final StringBuilder variableToReplace = new StringBuilder("${").append(propertyName).append("}");
                final Object content = bean.getProperty(propertyName);
                final String contentString = content != null ? content.toString() : "";
                workingCopy = workingCopy.replace(variableToReplace, contentString);
            }
            return workingCopy;
        } else {
            return String.valueOf(bean.getProperty(inputExpression));
        }
    }

    @Override
    public String convertForward(final List<CidsBean> list) {
        if (list != null && !list.isEmpty()) {
            final StringBuffer sb = new StringBuffer(prefix);
            for (final CidsBean bean : list) {
                if (inputExpression != null) {
                    sb.append(insertValuesIntoPatternString(bean)).append(separator);
                }
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
