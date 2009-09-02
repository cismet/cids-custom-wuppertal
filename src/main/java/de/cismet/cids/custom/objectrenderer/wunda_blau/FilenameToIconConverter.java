/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author srichter
 */
public class FilenameToIconConverter extends Converter<String, Icon> {

    public FilenameToIconConverter() {
        prefix = postfix = "";
    }
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FilenameToIconConverter.class);

    public FilenameToIconConverter(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }
    private final String prefix;
    private final String postfix;

    @Override
    public Icon convertForward(String in) {
        in = prefix + in + postfix;
        log.fatal("converting: " + in);
        try {
            //first try to load from jar
            URL symbolURL = Object.class.getResource(in);
            if (symbolURL == null) {
                //otherwise try to resolve directly
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
    public String convertReverse(Icon value) {
        return "";
    }
}
