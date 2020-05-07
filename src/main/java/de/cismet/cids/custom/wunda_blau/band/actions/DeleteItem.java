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

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;

import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DeleteItem extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SplitItem.class);

    //~ Instance fields --------------------------------------------------------

    private TreppeBandMember member;
    private boolean initialised = false;
    private boolean useIcon = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DeleteItem object.
     */
    public DeleteItem() {
        useIcon = true;
        initValues();
        setEnabled(false);
    }

    /**
     * Creates a new DeleteItem object.
     *
     * @param  member  DOCUMENT ME!
     */
    public DeleteItem(final TreppeBandMember member) {
        initValues();
        init(member);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  member  DOCUMENT ME!
     */
    public void init(final TreppeBandMember member) {
        this.member = member;
        setEnabled(true);
        this.initialised = true;
    }

    /**
     * DOCUMENT ME!
     */
    private void initValues() {
        putValue(SHORT_DESCRIPTION, "löschen");
        if (!useIcon) {
            putValue(NAME, "löschen");
        }

        if (useIcon) {
            final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                        "/res/remove-1.png"));
            putValue(SMALL_ICON, icon);
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
    public void actionPerformed(final ActionEvent event) {
        if (!initialised) {
            return;
        }
        member.getParentBand().deleteMember(member);
    }
}
