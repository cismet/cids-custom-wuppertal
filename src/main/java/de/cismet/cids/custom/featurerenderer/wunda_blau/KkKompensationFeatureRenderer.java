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
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.JComponent;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.SelectionAwareTexturePaint;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KkKompensationFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KkKompensationFeatureRenderer.class);
    private static final Map<String, BufferedImage> IMAGE_MAP = new HashMap<String, BufferedImage>();

    static {
        final URL urlArt = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffArtenschutz.png");
        final URL urlArtSelected = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffArtenschutzSelected.png");
        final URL urlArtHighlighted = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffArtenschutzHighlighted.png");
        final URL urlDach = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffDachbegruenung.png");
        final URL urlDachSelected = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffDachbegruenungSelected.png");
        final URL urlDachHighlighted = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffDachbegruenungHighlighted.png");
        final URL urlForst = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffForst.png");
        final URL urlForstSelected = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffForstSelected.png");
        final URL urlForstHighlighted = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffForstHighlighted.png");
        final URL urlKomp = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffKompensation.png");
        final URL urlKompSelected = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffKompensationSelected.png");
        final URL urlKompHighlighted = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffKompensationHighlighted.png");
        final URL urlPlanung = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffPlanung.png");
        final URL urlPlanungSelected = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffPlanungSelected.png");
        final URL urlPlanungHighlighted = KkKompensationFeatureRenderer.class.getResource(
                "/de/cismet/cids/custom/featurerenderer/wunda_blau/SchraffPlanungHighlighted.png");

        IMAGE_MAP.put(generateMapKey(KatType.ART, SelectionType.UNSELECTED), readImage(urlArt));
        IMAGE_MAP.put(generateMapKey(KatType.ART, SelectionType.SELECTED), readImage(urlArtSelected));
        IMAGE_MAP.put(generateMapKey(KatType.ART, SelectionType.HIGHLIGHTED), readImage(urlArtHighlighted));

        IMAGE_MAP.put(generateMapKey(KatType.DACH, SelectionType.UNSELECTED), readImage(urlDach));
        IMAGE_MAP.put(generateMapKey(KatType.DACH, SelectionType.SELECTED), readImage(urlDachSelected));
        IMAGE_MAP.put(generateMapKey(KatType.DACH, SelectionType.HIGHLIGHTED), readImage(urlDachHighlighted));

        IMAGE_MAP.put(generateMapKey(KatType.FORST, SelectionType.UNSELECTED), readImage(urlForst));
        IMAGE_MAP.put(generateMapKey(KatType.FORST, SelectionType.SELECTED), readImage(urlForstSelected));
        IMAGE_MAP.put(generateMapKey(KatType.FORST, SelectionType.HIGHLIGHTED), readImage(urlForstHighlighted));

        IMAGE_MAP.put(generateMapKey(KatType.KOMP, SelectionType.UNSELECTED), readImage(urlKomp));
        IMAGE_MAP.put(generateMapKey(KatType.KOMP, SelectionType.SELECTED), readImage(urlKompSelected));
        IMAGE_MAP.put(generateMapKey(KatType.KOMP, SelectionType.HIGHLIGHTED), readImage(urlKompHighlighted));

        IMAGE_MAP.put(generateMapKey(KatType.PLANUNG, SelectionType.UNSELECTED), readImage(urlPlanung));
        IMAGE_MAP.put(generateMapKey(KatType.PLANUNG, SelectionType.SELECTED), readImage(urlPlanungSelected));
        IMAGE_MAP.put(generateMapKey(KatType.PLANUNG, SelectionType.HIGHLIGHTED), readImage(urlPlanungHighlighted));
    }

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum KatType {

        //~ Enum constants -----------------------------------------------------

        ART, DACH, FORST, KOMP, PLANUNG
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum SelectionType {

        //~ Enum constants -----------------------------------------------------

        SELECTED, UNSELECTED, HIGHLIGHTED
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * create a BufferedImage from the given url.
     *
     * @param   url  the image url
     *
     * @return  the generated image
     */
    private static BufferedImage readImage(final URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            LOG.error("Cannot load image: " + url, ex);
            return null;
        }
    }

    /**
     * Generates the key for the IMAGE_MAP.
     *
     * @param   kat        DOCUMENT ME!
     * @param   selection  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String generateMapKey(final KatType kat, final SelectionType selection) {
        return kat.toString() + "-" + selection.toString();
    }

    /**
     * creates the texture paint for the given category type.
     *
     * @param   type  DOCUMENT ME!
     *
     * @return  a texture paint or null, if the images for the texture paint do not exist
     */
    private Paint createTexturePaint(final KatType type) {
        final Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, 0.07, 0.07);

        final BufferedImage selected = IMAGE_MAP.get(generateMapKey(type, SelectionType.SELECTED));
        final BufferedImage highlighted = IMAGE_MAP.get(generateMapKey(type, SelectionType.HIGHLIGHTED));
        final BufferedImage unselected = IMAGE_MAP.get(generateMapKey(type, SelectionType.UNSELECTED));

        if ((selected != null) && (highlighted != null) && (unselected != null)) {
            return new SelectionAwareTexturePaint(unselected, highlighted, selected, r);
        } else {
            return null;
        }
    }

    @Override
    public float getTransparency() {
        return 0.7f;
    }

    @Override
    public synchronized Paint getFillingStyle(final CidsFeature subFeature) {
        final String katKlasse = getKatKlasse();

        if (katKlasse != null) {
            if (katKlasse.startsWith("1")) {
                return Color.decode("#FB858B");
            } else if (katKlasse.equals("11")) {
                return Color.decode("#F90921");
            } else if (katKlasse.startsWith("2")) {
                return Color.decode("#84BA84");
            } else if (katKlasse.equals("22")) {
                return Color.decode("#167500");
            } else if (katKlasse.startsWith("3")) {
                return Color.decode("#FCFF96");
            } else if (katKlasse.equals("33")) {
                return Color.decode("#FBFF22");
            } else if (katKlasse.startsWith("4")) {
                return Color.decode("#F9D990");
            } else if (katKlasse.equals("44")) {
                return Color.decode("#F5B121");
            } else if (katKlasse.startsWith("5")) {
                return Color.decode("#DC79DD");
            } else if (katKlasse.equals("55")) {
                return Color.decode("#BE00C1");
            }
        }

        return Color.decode("#80C0FF");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getKatKlasse() {
        String katKlasse = null;

        try {
            katKlasse = (String)metaObject.getBean().getProperty("kategorie.klasse");
        } catch (NullPointerException e) {
            LOG.error("Cannot retrieve field kategorie.klasse from kompensation object.", e);
        }

        return katKlasse;
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        final String katKlasse = getKatKlasse();

        if (katKlasse != null) {
            if (katKlasse.equals("1")) {
                return Color.decode("#FB858B");
            } else if (katKlasse.equals("11")) {
                return Color.BLACK;
            } else if (katKlasse.equals("2")) {
                return Color.decode("#84BA84");
            } else if (katKlasse.equals("22")) {
                return Color.BLACK;
            } else if (katKlasse.equals("3")) {
                return Color.decode("#FCFF96");
            } else if (katKlasse.equals("33")) {
                return Color.BLACK;
            } else if (katKlasse.equals("4")) {
                return Color.decode("#F9D990");
            } else if (katKlasse.equals("44")) {
                return Color.BLACK;
            } else if (katKlasse.equals("5")) {
                return Color.decode("#DC79DD");
            } else if (katKlasse.equals("55")) {
                return Color.BLACK;
            }
        }

        return Color.BLACK;
    }

    @Override
    public Stroke getLineStyle() {
        final String katKlasse = getKatKlasse();

        if ((katKlasse != null) && (katKlasse.length() == 2)) {
            return new BasicStroke(2.0f);
        }

        return null;
    }

    @Override
    public Paint getFillingStyle() {
        return getFillingStyle(null);
    }

    @Override
    public Paint getLinePaint() {
        return getLinePaint(null);
    }

    @Override
    public void assign() {
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }
}
