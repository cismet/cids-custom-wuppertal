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
import javax.swing.ImageIcon;

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
import static javax.swing.Action.SMALL_ICON;

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

    private TreppeBandMember member;
    private boolean after = true;
    private boolean secondIcon = false;
    private String tableName;
    private boolean initialised = false;
    private boolean useIcon = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AddItem object.
     */
    public AddItem() {
        setEnabled(false);
        useIcon = true;
    }

    /**
     * Creates a new AddItem object.
     *
     * @param  after       DOCUMENT ME!
     * @param  secondIcon  DOCUMENT ME!
     */
    public AddItem(final boolean after, final boolean secondIcon) {
        this.after = after;
        this.secondIcon = secondIcon;
        useIcon = true;
        initValues(null, after);
        setEnabled(false);
    }

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
        init(member, after, tableName, objectName);
        initValues(objectName, after);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  member      DOCUMENT ME!
     * @param  after       DOCUMENT ME!
     * @param  tableName   DOCUMENT ME!
     * @param  objectName  DOCUMENT ME!
     */
    public void init(final TreppeBandMember member,
            final boolean after,
            final String tableName,
            final String objectName) {
        this.member = member;
        this.after = after;
        this.tableName = tableName;
        setEnabled(true);
        initValues(objectName, after);
        this.initialised = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objectName  DOCUMENT ME!
     * @param  after       DOCUMENT ME!
     */
    private void initValues(final String objectName, final boolean after) {
        String object = objectName;

        if (objectName == null) {
            object = "Objekt";
        }

        if (after) {
            putValue(SHORT_DESCRIPTION, object + " dahinter einfügen");
            if (!useIcon) {
                putValue(NAME, object + " dahinter einfügen");
            }
        } else {
            putValue(SHORT_DESCRIPTION, object + " davor einfügen");
            if (!useIcon) {
                putValue(NAME, object + " davor einfügen");
            }
        }

        if (useIcon) {
            if (after) {
                if (secondIcon) {
                    final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                                "/res/fast-forward.7.png"));
                    putValue(SMALL_ICON, icon);
                } else {
                    final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                                "/res/next.4.png"));
                    putValue(SMALL_ICON, icon);
                }
            } else {
                if (secondIcon) {
                    final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                                "/res/fast-backward.png"));
                    putValue(SMALL_ICON, icon);
                } else {
                    final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                                "/res/previous.2.png"));
                    putValue(SMALL_ICON, icon);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void deactivate() {
        setEnabled(false);
        initialised = false;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (!initialised) {
            return;
        }
        try {
            final CidsBean objectBean = TreppenBand.createNewCidsBeanFromTableName(tableName);
            final CidsBean zustandBean = TreppenBand.createNewCidsBeanFromTableName("treppe_zustand");
            objectBean.setProperty("zustand", zustandBean);
            final TreppeBandMember bandMember;
            int elementSize = 1;

            if (objectBean.getClass().getName().endsWith("Treppe_podest")) {
                elementSize = (int)PodestBandMember.ELEMENT_WIDTH;
            }
            double from = (after ? member.getMax() : (member.getMin()));
            final TreppenBand parentBand = member.getParentBand();

//            if (!(parentBand instanceof LaufBand)) {
//                member.getParentBand().moveAllMember(from, elementSize);
//            }
            if (after) {
                exception = member.getCidsBean();
            } else {
                final BandMember nextMember = member.getParentBand().getNextLessElement(member);

                if (nextMember instanceof TreppeBandMember) {
                    exception = ((TreppeBandMember)nextMember).getCidsBean();
                } else {
                    if (!after && !(parentBand instanceof LaufBand)) {
                        from = from - 1.0;
                    }
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
