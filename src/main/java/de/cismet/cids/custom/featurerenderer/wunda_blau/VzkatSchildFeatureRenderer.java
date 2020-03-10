/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.wunda_blau.search.VzkatWindowSearch;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VzkatSchildFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VzkatSchildFeatureRenderer.class);
    private static final Map<String, ImageIcon> ICONS = new HashMap<>();

    //~ Methods ----------------------------------------------------------------

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        try {
            final String key = String.format(
                    "%s_%s",
                    (String)cidsBean.getProperty("fk_zeichen.fk_stvo.schluessel"),
                    (String)cidsBean.getProperty("fk_zeichen.schluessel"));

            if (!ICONS.containsKey(key)) {
                ICONS.put(key, loadZeichenIcon(key));
            }

            final FeatureAnnotationSymbol fas = new FeatureAnnotationSymbol(ICONS.get(key).getImage());
            fas.setSweetSpotX(0.5d);
            fas.setSweetSpotY(0.5d);
            return fas;
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public ImageIcon loadZeichenIcon(final String key) throws Exception {
//        final String urlString = String.format(ICON_URL_TEMPLATE, key);
//        final InputStream is = WebAccessManager.getInstance().doRequest(new URL(urlString));
        final InputStream is = getClass().getResourceAsStream(String.format(VzkatWindowSearch.ICON_PATH_TEMPLATE, key));
        return (is != null) ? new ImageIcon(ImageIO.read(is)) : null;
    }

    @Override
    public float getTransparency() {
        return 1;
    }

    @Override
    public void assign() {
    }
}
