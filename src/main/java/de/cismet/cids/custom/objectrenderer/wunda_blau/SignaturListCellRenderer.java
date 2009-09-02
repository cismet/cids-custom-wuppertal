/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaObject;
import de.cismet.cids.custom.wunda_blau.res.StaticProperties;
import de.cismet.cids.dynamics.CidsBean;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author srichter
 */
public class SignaturListCellRenderer implements ListCellRenderer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SignaturListCellRenderer.class);
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Font theFont = null;
        Color theForeground = null;
        Icon theIcon = null;
        String theText = null;

        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof CidsBean) {
            final CidsBean bean = (CidsBean) value;
            final String filename = "" + bean.getProperty("filename");
            theIcon = createIconFromFileName(filename);
        } else if (value instanceof LightweightMetaObject) {
            final String filename = "" + ((LightweightMetaObject) value).getAttribute("filename");
            theIcon = createIconFromFileName(filename);
        }
        theFont = list.getFont();
        theForeground = list.getForeground();
        theText = String.valueOf(value);
        if (!isSelected) {
            renderer.setForeground(theForeground);
        }
        if (theIcon != null) {
            renderer.setIcon(theIcon);
        }
        renderer.setText(theText);
        renderer.setFont(theFont);
        return renderer;
    }

    public Icon createIconFromFileName(String in) {
        if (in != null && in.length() > 0) {
            in = StaticProperties.POI_SIGNATUR_URL_PREFIX + in + StaticProperties.POI_SIGNATUR_URL_SUFFIX;
            try {
                //first try to load from jar
                URL symbolURL = Object.class.getResource(in);
                if (symbolURL == null) {
                    //otherwise try to resolve directly
                    symbolURL = new URL(in);
                }
                if (symbolURL != null) {
                    return new ImageIcon(symbolURL);
                }
            } catch (Exception ex) {
                log.warn(ex, ex);
            }
        }
        return null;
    }
}


