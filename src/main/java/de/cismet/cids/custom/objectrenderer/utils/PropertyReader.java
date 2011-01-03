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
package de.cismet.cids.custom.objectrenderer.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class PropertyReader {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropertyReader.class);

    //~ Instance fields --------------------------------------------------------

    private final String filename;
    private final Properties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PropertyReader object.
     *
     * @param   filename  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public PropertyReader(final String filename) {
        if ((filename == null) || (filename.length() < 1)) {
            throw new IllegalArgumentException();
        }
        this.filename = filename;
        properties = new Properties();
        InputStream is = null;
        try {
            is = new BufferedInputStream(getClass().getResourceAsStream(filename));
            properties.load(is);
        } catch (IOException ex) {
            log.error(ex, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    log.warn(ex, ex);
                }
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public final String getProperty(final String key) {
        return properties.getProperty(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Properties getInternalProperties() {
        return properties;
    }
}
