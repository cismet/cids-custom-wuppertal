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
package de.cismet.cids.custom.wunda_blau.band.actions;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import de.cismet.cids.custom.wunda_blau.band.ElementResizedEvent;
import de.cismet.cids.custom.wunda_blau.band.LaufBand;
import de.cismet.cids.custom.wunda_blau.band.PodestBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppenBand;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.interfaces.Band;
import de.cismet.tools.gui.jbands.interfaces.BandMember;

import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class AddItem extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SplitItem.class);
    public static CidsBean exception;

    //~ Instance fields --------------------------------------------------------

    private final TreppeBandMember member;
    private final boolean after;
    private final String tableName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AddItem object.
     *
     * @param  member      DOCUMENT ME!
     * @param  after       DOCUMENT ME!
     * @param  tableName   DOCUMENT ME!
     * @param  objectName  DOCUMENT ME!
     */
    public AddItem(final TreppeBandMember member,
            final boolean after,
            final String tableName,
            final String objectName) {
        this.member = member;
        this.after = after;
        this.tableName = tableName;
        putValue(SHORT_DESCRIPTION, objectName);
        putValue(NAME, objectName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            final CidsBean objectBean = TreppenBand.createNewCidsBeanFromTableName(tableName);
            final TreppeBandMember bandMember;
            int elementSize = 1;

            if (objectBean.getClass().getName().endsWith("Treppe_podest")) {
                elementSize = (int)PodestBandMember.ELEMENT_WIDTH;
            }
            final double from = (after ? member.getMax() : (member.getMin()));
            final TreppenBand parentBand = member.getParentBand();

            if (!(parentBand instanceof LaufBand)) {
                member.getParentBand().moveAllMember(from, elementSize);
            }
            if (after) {
                exception = member.getCidsBean();
            } else {
                final BandMember nextMember = member.getParentBand().getNextLessElement(member);

                if (nextMember instanceof TreppeBandMember) {
                    exception = ((TreppeBandMember)nextMember).getCidsBean();
                }
            }
            bandMember = parentBand.addMember(objectBean, from, from + elementSize, parentBand.getSide());

            if ((parentBand instanceof LaufBand)) {
                final ElementResizedEvent event = new ElementResizedEvent(
                        bandMember,
                        true,
                        from,
                        from
                                + elementSize);
                final List<Band> exceptions = new ArrayList<Band>();
                exceptions.add(parentBand);
                event.setException(exceptions);
                event.setRefreshDummiesOnly(false);
                bandMember.fireElementResized(event);
            } else {
                final ElementResizedEvent event = new ElementResizedEvent(
                        bandMember,
                        true,
                        0,
                        0);
                event.setRefreshDummiesOnly(true);
                bandMember.fireElementResized(event);
            }
            exception = null;

            parentBand.getParent().setSelectedMember(bandMember);
        } catch (Exception ex) {
            LOG.error("Cannot create new band member", ex);
        }
    }
}
