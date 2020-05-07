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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.JBand;
import de.cismet.tools.gui.jbands.interfaces.BandMember;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EntwaesserungBand extends TreppenBand {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LeitelementBand object.
     *
     * @param  side    DOCUMENT ME!
     * @param  title   DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    public EntwaesserungBand(final Side side, final String title, final JBand parent) {
        super(side, title, parent);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Double getFixObjectLength() {
        return null;
    }

    @Override
    protected void addDummies() {
        final List<BandMember> orderedMembers = new ArrayList<BandMember>(members);

        if (orderedMembers.isEmpty()) {
            final DummyBandMember dummy = new DummyBandMember(this, readOnly);
            dummy.setFrom(0);
            dummy.setTo(parent.getMaxValue());
            addMember(dummy);
        }
    }

    @Override
    protected TreppeBandMember createBandMemberFromBean(final CidsBean bean) {
        final EntwaesserungBandMember m = new EntwaesserungBandMember(this, readOnly);

        return m;
    }

    @Override
    public float getBandWeight() {
        return 0.1f;
    }

    @Override
    public String[] getAllowedObjectNames() {
        // todo: i18n
        return new String[] { "Entwässerung" };
    }

    @Override
    public String[] getAllowedObjectTableNames() {
        return new String[] { "treppe_entwaesserung" };
    }
}
