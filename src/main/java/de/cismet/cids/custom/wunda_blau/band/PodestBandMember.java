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
import java.awt.event.MouseEvent;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class PodestBandMember extends LaufBandMember {

    //~ Static fields/initializers ---------------------------------------------

    public static double ELEMENT_WIDTH = 5.0;

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
    public void mouseDragged(final MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseDragged(final MouseEvent e, final double station) {
        // nothing to do
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        // nothing to do
    }

    @Override
    protected double roundToNextValidPosition(final double pos, final boolean till) {
        return oldStationValue;
    }

    @Override
    protected void determineBackgroundColour() {
//        final Color secondColor = new Color(255, 66, 66);
//        setBackgroundPainter(new CompoundPainter(
//                new MattePainter(secondColor),
//                new PinstripePainter(new Color(255, 255, 255), 45, 2, 5)));
//        unselectedBackgroundPainter = getBackgroundPainter();
//        selectedBackgroundPainter = new CompoundPainter(
//                unselectedBackgroundPainter,
//                new RectanglePainter(
//                    3,
//                    3,
//                    3,
//                    3,
//                    3,
//                    3,
//                    true,
//                    new Color(100, 100, 100, 100),
//                    2f,
//                    new Color(50, 50, 50, 100)));
//
//        setSelected(isSelected);
        if (alternativeColor) {
            setReadOnlyColor();
        } else {
            unselectedBackgroundPainter = new MattePainter(new Color(222, 184, 135));
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
}
