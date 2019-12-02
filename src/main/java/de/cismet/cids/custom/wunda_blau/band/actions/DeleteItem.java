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

import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppenBand;

import de.cismet.tools.gui.jbands.BandMemberEvent;

import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;

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

    private final TreppeBandMember member;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DeleteItem object.
     *
     * @param  member  DOCUMENT ME!
     */
    public DeleteItem(final TreppeBandMember member) {
        this.member = member;
        putValue(SHORT_DESCRIPTION, "löschen");
        putValue(NAME, "löschen");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent event) {
        member.getParentBand().deleteMember(member);
    }
}
