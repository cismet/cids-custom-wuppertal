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

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
class ComposeEMailToAllLoginsOfKundenAction extends AbstractComposeEMailAction {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ComposeEMailToAllLoginsOfKundenAction object.
     */
    public ComposeEMailToAllLoginsOfKundenAction() {
        putValue(NAME, "E-Mail an Nutzer");
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
            final Collection<CidsBean> kunden_logins = kunde.getBeanCollectionProperty("benutzer_n");
            for (final CidsBean kunden_login : kunden_logins) {
                final String eMailAddress = (String)kunden_login.getProperty("kontakt");
                if (eMailAddress != null) {
                    eMailAddresses.add(eMailAddress);
                }
            }
        }

        return eMailAddresses;
    }
}
