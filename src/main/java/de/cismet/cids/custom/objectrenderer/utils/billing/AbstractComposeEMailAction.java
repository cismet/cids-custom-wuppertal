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
package de.cismet.cids.custom.objectrenderer.utils.billing;

import java.awt.event.ActionEvent;

import java.util.EnumMap;
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

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        final EnumMap<EMailTemplateChooserDialog.E_MailParts, String> e_mailParts =
            new EMailTemplateChooserDialog().showDialogAndReturnBody();
        final List<String> eMailAddresses = fetchEMailAddresses();

        if (e_mailParts.get(EMailTemplateChooserDialog.E_MailParts.ABORT) == null) {
            final EMailComposer mail = new EMailComposer();
            mail.setBcc(eMailAddresses);
            mail.setBody(e_mailParts.get(EMailTemplateChooserDialog.E_MailParts.BODY));
            mail.setSubject(e_mailParts.get(EMailTemplateChooserDialog.E_MailParts.SUBJECT));

            mail.compose();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract List<String> fetchEMailAddresses();
}
