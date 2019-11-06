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
package de.cismet.cids.custom.orbit;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */

public class CamState {

    //~ Instance fields --------------------------------------------------------

    private String crs = "25832";
    private double x;
    private double y;
    private double pan;
    private double tilt;
    private double fov;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StacResult object.
     */
    public CamState() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCrs() {
        return crs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  crs  DOCUMENT ME!
     */
    public void setCrs(final String crs) {
        this.crs = crs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getX() {
        return x;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  x  DOCUMENT ME!
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getY() {
        return y;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  y  DOCUMENT ME!
     */
    public void setY(final double y) {
        this.y = y;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getPan() {
        return pan;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pan  DOCUMENT ME!
     */
    public void setPan(final double pan) {
        this.pan = pan;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getTilt() {
        return tilt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tilt  DOCUMENT ME!
     */
    public void setTilt(final double tilt) {
        this.tilt = tilt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getFov() {
        return fov;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fov  DOCUMENT ME!
     */
    public void setFov(final double fov) {
        this.fov = fov;
    }
}
