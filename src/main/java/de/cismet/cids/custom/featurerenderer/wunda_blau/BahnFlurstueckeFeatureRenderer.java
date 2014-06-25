/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import javax.swing.JComponent;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   reinhard.verkennis
 * @version  $Revision$, $Date$
 */

public class BahnFlurstueckeFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BahnFlurstueckeFeatureRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    @Override
    public Paint getLinePaint() {
        return null;
    }

    @Override
    public Paint getFillingStyle() {
        final String status = (String)cidsBean.getProperty("aktenzeichen.status.value");

        Color c = new Color(132, 12, 232, 200);

        if (status != null) {
            if (status.contains("gewidmet")) {
                c = new Color(132, 12, 232, 200);
            } else if (status.contains("Verfahren")) {
                c = new Color(255, 0, 0, 200);
            } else if (status.contains("freigestellt")) {
                c = new Color(255, 235, 109, 200);
            }
        } else {
            LOG.warn("Status is not set for BahnFlurstueck");
        }
        return c;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public void assign() {
        cidsBean = metaObject.getBean();
    }
}
