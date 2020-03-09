/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.wunda_blau.search.VzkatStandortWindowSearch;

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
                ICONS.put(key, VzkatStandortWindowSearch.loadZeichenIcon(key));
            }

            return new FeatureAnnotationSymbol(ICONS.get(key).getImage());
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    @Override
    public float getTransparency() {
        return 1;
    }

    @Override
    public void assign() {
    }
}
