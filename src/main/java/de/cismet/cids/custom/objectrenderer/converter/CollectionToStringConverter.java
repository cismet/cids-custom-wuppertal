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

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.collections.TypeSafeCollections;

/**
 * inputExpression can be - a single, simple property name, like : "strasse" - a pattern with property names as
 * variables marked by ${}, like: "${strasse} - ${nummer}"
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class CollectionToStringConverter extends Converter<List<CidsBean>, String> {

    //~ Static fields/initializers ---------------------------------------------

    // matches expessions like ${xyz} and groups their content xyz

    private static final Pattern REGEX = Pattern.compile("\\$\\{(.*?)\\}");

    //~ Instance fields --------------------------------------------------------

    private final Set<String> foundPropertyNames;
    private final String inputExpression;
    private final String separator;
    private final String prefix;
    private final String postfix;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CollectionToStringConverter object.
     *
     * @param  propName  DOCUMENT ME!
     */
    public CollectionToStringConverter(final String propName) {
        this(propName, ", ", "", "");
    }

    /**
     * Creates a new CollectionToStringConverter object.
     *
     * @param  propName   DOCUMENT ME!
     * @param  separator  DOCUMENT ME!
     */
    public CollectionToStringConverter(final String propName, final String separator) {
        this(propName, separator, "", "");
    }

    /**
     * Creates a new CollectionToStringConverter object.
     *
     * @param   propName   DOCUMENT ME!
     * @param   separator  DOCUMENT ME!
     * @param   prefix     DOCUMENT ME!
     * @param   postfix    DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public CollectionToStringConverter(final String propName,
            final String separator,
            final String prefix,
            final String postfix) {
        if ((propName == null) || (separator == null) || (prefix == null) || (postfix == null)) {
            throw new IllegalArgumentException();
        }
        this.foundPropertyNames = TypeSafeCollections.newHashSet();
        this.inputExpression = propName;
        this.separator = separator;
        this.prefix = prefix;
        this.postfix = postfix;
        parseStringForReplaceToken();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   property   DOCUMENT ME!
     * @param   separator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final CollectionToStringConverter getHtmlTaggingCollectionConverter(final String property,
            final String separator) {
        return new CollectionToStringConverter(property, separator, "<html>", "</html>");
    }

    /**
     * DOCUMENT ME!
     */
    private void parseStringForReplaceToken() {
        final Matcher matcher = REGEX.matcher(inputExpression);
        while (matcher.find()) {
            final String toAdd = matcher.group(matcher.groupCount());
            foundPropertyNames.add(toAdd);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String insertValuesIntoPatternString(final CidsBean bean) {
        if (!foundPropertyNames.isEmpty()) {
            String workingCopy = inputExpression;
            for (final String propertyName : foundPropertyNames) {
                final StringBuilder variableToReplace = new StringBuilder("${").append(propertyName).append("}");
                final Object content = bean.getProperty(propertyName);
                final String contentString = (content != null) ? content.toString() : "";
                workingCopy = workingCopy.replace(variableToReplace, contentString);
            }
            return workingCopy;
        } else {
            return String.valueOf(bean.getProperty(inputExpression));
        }
    }

    @Override
    public String convertForward(final List<CidsBean> list) {
        if ((list != null) && !list.isEmpty()) {
            final StringBuffer sb = new StringBuffer(prefix);
            for (final CidsBean bean : list) {
                if (inputExpression != null) {
                    sb.append(insertValuesIntoPatternString(bean)).append(separator);
                }
            }
            if (sb.length() > (1 + prefix.length())) {
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append(postfix);
            return sb.toString();
        }
        return null;
    }

    @Override
    public List<CidsBean> convertReverse(final String arg0) {
        throw new UnsupportedOperationException();
    }
}
