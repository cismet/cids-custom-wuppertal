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
package de.cismet.cids.custom.objectrenderer.utils;

import javax.swing.JLabel;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class LoaderLabel extends JLabel {

    //~ Instance fields --------------------------------------------------------

    private int maxY;
    private int maxX;
    private int shadowSize;
    private String pictureURL;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LoaderLabel object.
     */
    public LoaderLabel() {
        this(300, 300, 4);
    }

    /**
     * Creates a new LoaderLabel object.
     *
     * @param  macPixelX   DOCUMENT ME!
     * @param  maxPixelY   DOCUMENT ME!
     * @param  shadowSize  DOCUMENT ME!
     */
    public LoaderLabel(final int macPixelX, final int maxPixelY, final int shadowSize) {
        this.maxY = maxPixelY;
        this.maxX = macPixelX;
        this.shadowSize = shadowSize;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the pictureURL
     */
    public String getPictureURL() {
        return pictureURL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pictureURL  the pictureURL to set
     */
    public void setPictureURL(final String pictureURL) {
        this.pictureURL = pictureURL;
        if (pictureURL != null) {
            ObjectRendererUtils.loadPictureAndSet(pictureURL, maxX, maxY, shadowSize, this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the maxY
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxY  the maxY to set
     */
    public void setMaxY(final int maxY) {
        this.maxY = maxY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the maxX
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxX  the maxX to set
     */
    public void setMaxX(final int maxX) {
        this.maxX = maxX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the shadowSize
     */
    public int getShadowSize() {
        return shadowSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  shadowSize  the shadowSize to set
     */
    public void setShadowSize(final int shadowSize) {
        this.shadowSize = shadowSize;
    }
}
