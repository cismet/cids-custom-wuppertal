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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import org.jdesktop.beansbinding.Converter;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FilenameToIconConverter extends Converter<String, Icon> {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FilenameToIconConverter.class);

    //~ Instance fields --------------------------------------------------------

    private final String prefix;
    private final String postfix;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FilenameToIconConverter object.
     */
    public FilenameToIconConverter() {
        prefix = postfix = "";
    }

    /**
     * Creates a new FilenameToIconConverter object.
     *
     * @param  prefix   DOCUMENT ME!
     * @param  postfix  DOCUMENT ME!
     */
    public FilenameToIconConverter(final String prefix, final String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon convertForward(String in) {
        in = prefix + in + postfix;
        log.fatal("converting: " + in);
        try {
            // first try to load from jar
            URL symbolURL = Object.class.getResource(in);
            if (symbolURL == null) {
                // otherwise try to resolve directly
                symbolURL = new URL(in);
            }
            if (symbolURL != null) {
                log.fatal("returning: " + new ImageIcon(symbolURL));
                return new ImageIcon(symbolURL);
            }
        } catch (Exception ex) {
            log.warn(ex, ex);
        }
        return null;
    }

    @Override
    public String convertReverse(final Icon value) {
        return "";
    }
}
