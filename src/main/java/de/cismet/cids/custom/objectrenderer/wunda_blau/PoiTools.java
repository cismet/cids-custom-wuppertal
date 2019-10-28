/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.net.URL;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objectrenderer.utils.ClientStaticProperties;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class PoiTools {

    //~ Static fields/initializers ---------------------------------------------

    private static final ImageIcon DEFAULT_ICON = getIconFromResourceString(ClientStaticProperties.getInstance()
                    .getPoiSignaturDefaultIcon());

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PoiTools.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon createPoiIconFromFileName(String in) {
        if ((in != null) && (in.length() > 0)) {
            in = ClientStaticProperties.getInstance().getPoiSignaturUrlPrefix() + in
                        + ClientStaticProperties.getInstance().getPoiSignaturUrlSuffix();
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
                LOG.warn(ex, ex);
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getIconFromResourceString(final String in) {
        if ((in != null) && (in.length() > 0)) {
            try {
                // first try to load from jar
                URL symbolURL = Object.class.getResource(in);

                if (symbolURL == null) {
                    // otherwise try to resolve directly
                    symbolURL = new URL(in);
                }
                if (symbolURL != null) {
                    final ImageIcon ret = new ImageIcon(symbolURL);
                    if (ret == null) {
                        LOG.error("could not create icon from :" + symbolURL);
                    }
                    return ret;
                } else {
                    LOG.error("could not create icon from :" + symbolURL);
                }
            } catch (Exception ex) {
                LOG.warn("Error in getSymbolFromResourceString(" + in + ")", ex);
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getPoiSignatureIcon(final CidsBean cidsBean) {
        String iconUrl = "";
        ImageIcon symbol = null;
        if (cidsBean != null) {
            Object o = cidsBean.getProperty("signatur");
            if (o instanceof CidsBean) {
                iconUrl = getUrlStringFromSignature(o);
                if (iconUrl != null) {
                    symbol = getIconFromResourceString(iconUrl);
                }
            }
            if (symbol == null) {
                o = cidsBean.getProperty("mainlocationtype");
                if (o instanceof CidsBean) {
                    iconUrl = getUrlStringFromSignature(((CidsBean)o).getProperty("signatur"));
                    if (iconUrl != null) {
                        symbol = getIconFromResourceString(iconUrl);
                    }
                }
                if (symbol == null) {
                    o = cidsBean.getProperty("mainlocationtype");
                    if (o instanceof CidsBean) {
                        final CidsBean mainLocationType = (CidsBean)o;
                        o = mainLocationType.getProperty("signatur");
                        iconUrl = getUrlStringFromSignature(o);
                        if (iconUrl != null) {
                            symbol = getIconFromResourceString(iconUrl);
                        }
                    }
                }
                if (symbol == null) {
                    symbol = DEFAULT_ICON;
                }
            }
        }
        return symbol;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   signature  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getUrlStringFromSignature(final Object signature) {
        if (signature instanceof CidsBean) {
            final CidsBean signatur = (CidsBean)signature;
            try {
                final Object fileName = signatur.getProperty("filename");
                if (fileName != null) {
                    final String ret = ClientStaticProperties.getInstance().getPoiSignaturUrlPrefix() + fileName
                                + ClientStaticProperties.getInstance().getPoiSignaturUrlSuffix();
                    LOG.info("Signatur Url: " + ret);
                    return ret;
                }
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
        }
        return null;
    }
}
