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

import javax.swing.JMenuItem;

import de.cismet.cids.custom.wunda_blau.band.actions.AddItem;
import de.cismet.cids.custom.wunda_blau.band.actions.DeleteItem;
import de.cismet.cids.custom.wunda_blau.band.actions.SplitItem;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class StuetzmauerBandMember extends TreppeBandMember {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StuetzmauerBandMember object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public StuetzmauerBandMember(final TreppenBand parent) {
        super(parent);
    }

    /**
     * Creates a new StuetzmauerBandMember object.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public StuetzmauerBandMember(final TreppenBand parent, final boolean readOnly) {
        super(parent, readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void configurePopupMenu() {
        popup.removeAll();
        if (!isReadOnly()) {
            final JMenuItem splitItem = new JMenuItem();
            splitItem.setAction(new SplitItem(this));
            final JMenuItem deleteItem = new JMenuItem();
            deleteItem.setAction(new DeleteItem(this));

            popup.add(splitItem);
            popup.addSeparator();
            popup.add(deleteItem);
        }
    }

    @Override
    protected void determineBackgroundColour() {
        if (alternativeColor) {
            setReadOnlyColor();
        } else {
            unselectedBackgroundPainter = new MattePainter(new Color(100, 100, 100));
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
