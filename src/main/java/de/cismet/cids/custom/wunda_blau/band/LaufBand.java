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

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.JBand;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LaufBand extends TreppenBand {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LaufBand object.
     *
     * @param  side    DOCUMENT ME!
     * @param  title   DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    public LaufBand(final Side side, final String title, final JBand parent) {
        super(side, title, parent);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Double getFixObjectLength() {
        return null;
    }

    @Override
    protected boolean hasDummyAfterEnd() {
        return false;
    }

    @Override
    protected TreppeBandMember createBandMemberFromBean(final CidsBean bean) {
        if (bean.getClass().getName().endsWith("Treppe_podest")) {
            final TreppeBandMember m = new PodestBandMember(this, readOnly);

            return m;
        } else {
            final LaufBandMember m = new LaufBandMember(this, readOnly);

            return m;
        }
    }

    @Override
    public float getBandWeight() {
        return 0.4f;
    }

    @Override
    public String[] getAllowedObjectNames() {
        // todo: i18n
        return new String[] { "Treppenlauf", "Podest" };
    }

    @Override
    public String[] getAllowedObjectTableNames() {
        return new String[] { "treppe_treppenlauf", "treppe_podest" };
    }

    @Override
    public double getMax() {
        fixMax = null;
        return ((super.getMax() < 1) ? 1.0 : super.getMax());
    }

    @Override
    public double getMin() {
        return 0;
    }
}
