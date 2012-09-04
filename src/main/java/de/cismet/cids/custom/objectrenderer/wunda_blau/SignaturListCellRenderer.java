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

import Sirius.server.middleware.types.LightweightMetaObject;

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

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class SignaturListCellRenderer implements ListCellRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            SignaturListCellRenderer.class);
    private static final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   list          DOCUMENT ME!
     * @param   value         DOCUMENT ME!
     * @param   index         DOCUMENT ME!
     * @param   isSelected    DOCUMENT ME!
     * @param   cellHasFocus  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Component getListCellRendererComponent(final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        Font theFont = null;
        Color theForeground = null;
        Icon theIcon = null;
        String theText = null;

        final JLabel renderer = (JLabel)defaultRenderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus);

        if (value instanceof CidsBean) {
            final CidsBean bean = (CidsBean)value;
            final String filename = String.valueOf(bean.getProperty("filename"));
            theIcon = createIconFromFileName(filename);
        } else if (value instanceof LightweightMetaObject) {
            final String filename = String.valueOf(((LightweightMetaObject)value).getLWAttribute("filename"));
            theIcon = createIconFromFileName(filename);
        }
        theFont = list.getFont();
        theForeground = list.getForeground();
        if (value == null) {
            theText = "keine Signatur ausgewählt";
        } else {
            theText = String.valueOf(value);
        }
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

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon createIconFromFileName(String in) {
        if ((in != null) && (in.length() > 0)) {
            in = StaticProperties.POI_SIGNATUR_URL_PREFIX + in + StaticProperties.POI_SIGNATUR_URL_SUFFIX;
            try {
                // first try to load from jar
                URL symbolURL = Object.class.getResource(in);
                if (symbolURL == null) {
                    // otherwise try to resolve directly
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
