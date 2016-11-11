/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;
import de.cismet.cismap.navigatorplugin.CidsFeature;
import java.awt.Color;
import java.awt.Paint;
import org.apache.log4j.Logger;

/**
 *
 * @author therter
 */
public class KkKompensationFeatureRenderer extends CustomCidsFeatureRenderer {
    private static final Logger LOG = Logger.getLogger(KkKompensationFeatureRenderer.class);

    @Override
    public float getTransparency() {
        return 0.7f;
    }

    @Override
    public synchronized Paint getFillingStyle(final CidsFeature subFeature) {
        String katKlasse = getKatKlasse(subFeature);
        
        if (katKlasse != null) {
            if (katKlasse.equals("1")) {
                return Color.decode("#FF0000");
            } else if (katKlasse.equals("11")) {
                return Color.decode("#FF0000");
            } else if (katKlasse.equals("2")) {
                return Color.decode("#007300");
            } else if (katKlasse.equals("22")) {
                return Color.decode("#007300");
            } else if (katKlasse.equals("3")) {
                return Color.decode("#FFFF80");
            } else if (katKlasse.equals("33")) {
                return Color.decode("#FFFF80");
            } else if (katKlasse.equals("4")) {
                return Color.decode("#FAC864");
            } else if (katKlasse.equals("44")) {
                return Color.decode("#FAC864");
            } else if (katKlasse.equals("5")) {
                return Color.decode("#C000C0");
            } else if (katKlasse.equals("55")) {
                return Color.decode("#C000C0");
            }
        }
        
        return Color.decode("#80C0FF");
    }
    
    private String getKatKlasse(final CidsFeature subFeature) {
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
        return Color.BLACK;
    }

    @Override
    public Paint getFillingStyle() {
        return getFillingStyle(null);
    }

    @Override
    public Paint getLinePaint() {
        return Color.decode("#000000");
    }

    @Override
    public void assign() {
    }
    
}
