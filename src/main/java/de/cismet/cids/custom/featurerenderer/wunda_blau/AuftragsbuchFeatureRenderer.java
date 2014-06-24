/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import Sirius.server.localserver.attribute.Attribute;

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
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class AuftragsbuchFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Methods ----------------------------------------------------------------

    /*
     *Muss genauso implementiert werden, da Konstruktoren nicht vererbt werden können
     */

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
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public Paint getFillingStyle() {
        Color c = Color.ORANGE;

        try {
            final Object attrib = metaObject.getAttributeByName("AUFTRAGSART", 1).toArray()[0];
            final String aa = ((Attribute)attrib).toString();
            if (aa.contains("Bauvermessung")) {
                c = new Color(0.90f, 0.7f, 0.91f);
            } else if (aa.contains("Entwurfsvermessung")) {
                c = new Color(0.0f, 0.0f, 0.8f);
            } else if (aa.contains("Fortführung")) {
                c = new Color(1.0f, 1.0f, 0.5f);
            } else if (aa.contains("Gebäudeeinmessung")) {
                c = new Color(1.0f, 1.0f, 0.5f);
            } else if (aa.contains("Grenzvermessung")) {
                c = new Color(1.0f, 1.0f, 0.5f);
            } else if (aa.contains("Sonstige")) {
                c = new Color(0.0f, 1.0f, 0.0f);
            } else if (aa.contains("Sonstige (VermGebO)")) {
                c = new Color(1.0f, 1.0f, 0.5f);
            }
        } catch (Throwable t) {
            log.warn("Fehler in getFillingStyle()", t);
        }
        return c;
    }

    @Override
    public void assign() {
        return;
    }
}
