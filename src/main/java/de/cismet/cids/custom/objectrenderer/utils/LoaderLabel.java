/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import javax.swing.JLabel;

/**
 *
 * @author srichter
 */
public class LoaderLabel extends JLabel {

    public LoaderLabel(int macPixelX, int maxPixelY, int shadowSize) {
        this.maxY = maxPixelY;
        this.maxX = macPixelX;
        this.shadowSize = shadowSize;
    }

    public LoaderLabel() {
        this(300, 300, 4);
    }
    private int maxY;
    private int maxX;
    private int shadowSize;
    private String pictureURL;

    /**
     * @return the pictureURL
     */
    public String getPictureURL() {
        return pictureURL;
    }

    /**
     * @param pictureURL the pictureURL to set
     */
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
        if (pictureURL != null) {
            ObjectRendererUtils.loadPictureAndSet(pictureURL, maxX, maxY, shadowSize, this);
        }
    }

    /**
     * @return the maxY
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * @param maxY the maxY to set
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * @return the maxX
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the shadowSize
     */
    public int getShadowSize() {
        return shadowSize;
    }

    /**
     * @param shadowSize the shadowSize to set
     */
    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
    }
}
