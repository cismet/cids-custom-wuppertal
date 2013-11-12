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
        final Template template = new EMailTemplateChooserDialog().showDialogAndReturnBody();
        final List<String> eMailAddresses = fetchEMailAddresses();

        if (template != null) {
            final EMailComposer mail = new EMailComposer();
            mail.setBcc(eMailAddresses);
            mail.setBody(template.getBody());
            mail.setSubject(template.getSubject());
            mail.addTo(template.getTo());

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
