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
package de.cismet.cids.custom.wunda_blau.band;

import de.cismet.tools.gui.jbands.interfaces.BandMember;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ElementResizedEvent {

    //~ Instance fields --------------------------------------------------------

    private BandMember bandMember;
    private boolean max;
    private double oldValue;
    private double newValue;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ElementResizedEvent object.
     */
    public ElementResizedEvent() {
    }

    /**
     * Creates a new ElementResizedEvent object.
     *
     * @param  bandMember  DOCUMENT ME!
     * @param  max         DOCUMENT ME!
     * @param  oldValue    DOCUMENT ME!
     * @param  newValue    DOCUMENT ME!
     */
    public ElementResizedEvent(final BandMember bandMember,
            final boolean max,
            final double oldValue,
            final double newValue) {
        this.bandMember = bandMember;
        this.max = max;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the bandMember
     */
    public BandMember getBandMember() {
        return bandMember;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bandMember  the bandMember to set
     */
    public void setBandMember(final BandMember bandMember) {
        this.bandMember = bandMember;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the isMax
     */
    public boolean isMax() {
        return max;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  isMax the isMax to set
     */
    public void setMax(final boolean max) {
        this.max = max;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the oldValue
     */
    public double getOldValue() {
        return oldValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldValue  the oldValue to set
     */
    public void setOldValue(final double oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the newValue
     */
    public double getNewValue() {
        return newValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newValue  the newValue to set
     */
    public void setNewValue(final double newValue) {
        this.newValue = newValue;
    }
}
