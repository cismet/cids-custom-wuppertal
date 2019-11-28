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
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;

import java.awt.Color;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class PodestBandMember extends LaufBandMember {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PodestBandMember object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public PodestBandMember(final TreppenBand parent) {
        super(parent);
    }

    /**
     * Creates a new PodestBandMember object.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public PodestBandMember(final TreppenBand parent, final boolean readOnly) {
        super(parent, readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected double roundToNextValidPosition(final double pos) {
//        final double newPos = pos - oldStationValue;
//        return (Math.floor(newPos * (5 / 3)) / (5 / 3)) + oldStationValue;
        return oldStationValue;
    }

    @Override
    protected void determineBackgroundColour() {
        final Color secondColor = new Color(255, 66, 66);
        setBackgroundPainter(new CompoundPainter(
                new MattePainter(secondColor),
                new PinstripePainter(new Color(255, 255, 255), 45, 2, 5)));
        unselectedBackgroundPainter = getBackgroundPainter();
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

        setSelected(isSelected);
    }
}
