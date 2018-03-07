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
package de.cismet.cids.custom.objectrenderer.utils;

import javax.swing.JButton;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * The ActionTag 'BILLING_ACTION_TAG_REPORT' need to be present to activate this button. <code>
 * BillingKundeREnderer.btnRechnungsanlage</code> is such a button.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingRestrictedReportJButton extends JButton implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    public static final String BILLING_ACTION_TAG_REPORT = "custom.billing.reports@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingRestrictedReportJButton object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public BillingRestrictedReportJButton(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setEnabled(final boolean b) {
        if (ObjectRendererUtils.checkActionTag(BILLING_ACTION_TAG_REPORT, connectionContext)) {
            super.setEnabled(b);
        } else {
            super.setEnabled(false);
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
