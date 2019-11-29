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

import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.RectanglePainter;

import java.awt.Color;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.SimpleModifiableBand;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class HandlaufBandMember extends TreppeBandMember {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HandlaufBandMember object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public HandlaufBandMember(final TreppenBand parent) {
        super(parent);
    }

    /**
     * Creates a new HandlaufBandMember object.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public HandlaufBandMember(final TreppenBand parent, final boolean readOnly) {
        super(parent, readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void determineBackgroundColour() {
        setDefaultBackground();
        unselectedBackgroundPainter = new MattePainter(new Color(77, 157, 190));
        selectedBackgroundPainter = new CompoundPainter(
                unselectedBackgroundPainter,
                new RectanglePainter(
                    3,
                    3,
                    3,
                    3,
                    3,
                    3,
                    true,
                    new Color(100, 100, 100, 100),
                    2f,
                    new Color(50, 50, 50, 100)));
        setBackgroundPainter(unselectedBackgroundPainter);
    }
}
