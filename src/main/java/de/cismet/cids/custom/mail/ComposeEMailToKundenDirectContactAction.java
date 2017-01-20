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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;

import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
class ComposeEMailToKundenDirectContactAction extends AbstractComposeEMailAction {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ComposeEMailToKundenDirectContactAction object.
     */
    public ComposeEMailToKundenDirectContactAction() {
        putValue(NAME, "E-Mail an Ansprechpartner");
        final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/icons/mails-stack.png"));
        putValue(SMALL_ICON, icon);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public List<String> fetchEMailAddresses() {
        final CidsBean kundengruppenBean = this.getCidsBean();
        final Collection<CidsBean> kundenBeans = kundengruppenBean.getBeanCollectionProperty("kunden_arr");
        final List<String> eMailAddresses = new ArrayList<String>(kundenBeans.size());

        for (final CidsBean kunde : kundenBeans) {
            final String eMailAddress = (String)kunde.getProperty("direktkontakt");
            if (eMailAddress != null) {
                eMailAddresses.add(eMailAddress);
            }
        }

        return eMailAddresses;
    }
}
