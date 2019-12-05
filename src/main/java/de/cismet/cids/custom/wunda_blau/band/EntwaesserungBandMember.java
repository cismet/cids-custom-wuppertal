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
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import de.cismet.cids.custom.wunda_blau.band.actions.DeleteItem;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EntwaesserungBandMember extends TreppeBandMember {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StuetzmauerBandMember object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public EntwaesserungBandMember(final TreppenBand parent) {
        super(parent);
    }

    /**
     * Creates a new StuetzmauerBandMember object.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public EntwaesserungBandMember(final TreppenBand parent, final boolean readOnly) {
        super(parent, readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bean = cidsBean;
        popup.removeAll();
        configurePopupMenu();
        von = getMin();
        bis = getMax();
        determineBackgroundColour();
    }

    @Override
    public double getMin() {
        return 0.0;
    }

    @Override
    public double getMax() {
        return ((parent.getParent().getMaxValue() > 1) ? parent.getParent().getMaxValue() : 1.0);
    }

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
    protected void configurePopupMenu() {
        popup.removeAll();
        final JMenuItem deleteItem = new JMenuItem();
        deleteItem.setAction(new DeleteItem(this));

        popup.add(deleteItem);
    }

    @Override
    protected void determineBackgroundColour() {
        unselectedBackgroundPainter = new MattePainter(new Color(102, 174, 243));
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
