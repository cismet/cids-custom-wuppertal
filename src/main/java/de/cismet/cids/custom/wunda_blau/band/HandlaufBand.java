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
public class HandlaufBand extends TreppenBand {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HandlaufBand object.
     *
     * @param  side    DOCUMENT ME!
     * @param  title   DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    public HandlaufBand(final Side side, final String title, final JBand parent) {
        super(side, title, parent);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Double getFixObjectLength() {
        return null;
    }

    @Override
    protected TreppeBandMember createBandMemberFromBean(final CidsBean bean) {
        final HandlaufBandMember m = new HandlaufBandMember(this, readOnly);

        return m;
    }

    @Override
    public float getBandWeight() {
        return 0.1f;
    }

    @Override
    public String[] getAllowedObjectNames() {
        // todo: i18n
        return new String[] { "Handlauf" };
    }

    @Override
    public String[] getAllowedObjectTableNames() {
        return new String[] { "treppe_handlauf" };
    }
}
