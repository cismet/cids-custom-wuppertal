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
public class LeitelementBand extends TreppenBand {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LeitelementBand object.
     *
     * @param  side    DOCUMENT ME!
     * @param  title   DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    public LeitelementBand(final Side side, final String title, final JBand parent) {
        super(side, title, parent);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Double getFixObjectLength() {
        return null;
    }

    @Override
    protected TreppeBandMember createBandMemberFromBean(final CidsBean bean) {
        final LeitelementBandMember m = new LeitelementBandMember(this, readOnly);

        return m;
    }

    @Override
    public float getBandWeight() {
        return 0.1f;
    }

    @Override
    public String[] getAllowedObjectNames() {
        // todo: i18n
        return new String[] { "Leitelement" };
    }

    @Override
    public String[] getAllowedObjectTableNames() {
        return new String[] { "treppe_absturzsicherung" };
    }
}