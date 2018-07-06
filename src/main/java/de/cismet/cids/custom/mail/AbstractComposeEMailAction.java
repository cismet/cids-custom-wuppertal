/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.mail;

import com.google.common.collect.Lists;

import java.awt.event.ActionEvent;

import java.util.List;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.tools.EMailComposer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractComposeEMailAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final int MAX_ADDRESSES_PER_MAIL = 50;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Template template = new EMailTemplateChooserDialog().showDialogAndReturnBody();
        final List<String> eMailAddresses = fetchEMailAddresses();

        if (template != null) {
            for (final List<String> partitionedEMailAddresses : Lists.partition(eMailAddresses, MAX_ADDRESSES_PER_MAIL)) {
                final EMailComposer mail = new EMailComposer();
                mail.setBcc(partitionedEMailAddresses);
                mail.setBody(template.getBody());
                mail.setSubject(template.getSubject());
                mail.addTo(template.getTo());

                mail.compose();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract List<String> fetchEMailAddresses();
}
