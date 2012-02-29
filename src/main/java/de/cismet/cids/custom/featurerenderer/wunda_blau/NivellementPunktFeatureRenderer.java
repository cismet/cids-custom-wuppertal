/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import java.net.URL;

import javax.swing.ImageIcon;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class NivellementPunktFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(NivellementPunktFeatureRenderer.class);

    private static final String PATH2ICONS = "/de/cismet/cids/custom/featurerenderer/wunda_blau/";

    //~ Instance fields --------------------------------------------------------

    private Color pointColor = new Color(0xB9, 0x90, 0x53);
    private ImageIcon pointIcon;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setMetaObject(final MetaObject metaObject) throws ConnectionException {
        super.setMetaObject(metaObject);

        if (cidsBean != null) {
            final URL urlToIcon = getClass().getResource(PATH2ICONS + "pointicon_brass.png");
            if (urlToIcon != null) {
                pointIcon = new ImageIcon(urlToIcon);
            }
        }
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        return getFillingStyle();
    }

    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        return pointColor;
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        FeatureAnnotationSymbol result;

        if ((pointIcon != null) && (pointIcon != null)) {
            result = new FeatureAnnotationSymbol(pointIcon.getImage());
            result.setSweetSpotX(0.49D);
            result.setSweetSpotY(0.93D);
        } else {
            final int fallbackSymbolSize = 8;
            final BufferedImage bufferedImage = new BufferedImage(
                    fallbackSymbolSize,
                    fallbackSymbolSize,
                    BufferedImage.TYPE_INT_ARGB);

            final Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
            graphics.setColor(pointColor);
            graphics.fillOval(0, 0, fallbackSymbolSize, fallbackSymbolSize);

            result = new FeatureAnnotationSymbol(bufferedImage);
            result.setSweetSpotX(0.5);
            result.setSweetSpotY(0.5);
        }

        return result;
    }

    @Override
    public void assign() {
    }

    @Override
    public float getTransparency() {
        return 0.9F;
    }
}
