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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingKundengruppeRenderer extends BillingKundeAggregationRenderer implements CidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingKundengruppeRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            final Collection<CidsBean> kundeBeans = cidsBean.getBeanCollectionProperty("kunden_arr");
            super.setCidsBeans(kundeBeans);
        }
    }

    @Override
    public String getTitle() {
        return getTitleLabel().getText();
    }

    @Override
    public void setTitle(final String title) {
        String desc = "Kundengruppe: ";
        if (cidsBean != null) {
            desc = "Kundengruppe " + cidsBean.toString() + ": ";
        }

        final Collection<CidsBean> beans = getCidsBeans();
        int amountBeans = 0;
        if ((beans != null) && (beans.size() > 0)) {
            amountBeans = beans.size();
        }

        desc += "beinhaltet " + amountBeans + " Kunden";

        getTitleLabel().setText(desc);
    }
}
