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
 *
 * @author srichter
 */
public class PropertyReader {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropertyReader.class);

    public PropertyReader(final String filename) {
        if (filename == null || filename.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.filename = filename;
        properties = new Properties();
        try {
            final InputStream is = new BufferedInputStream(PropertyReader.class.getResourceAsStream(filename));
            properties.load(is);
        } catch (IOException ex) {
            log.error(ex, ex);
        }
    }
    private final String filename;
    private final Properties properties;

    /**
     * 
     * @param key
     * @return
     */
    public final String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
}
