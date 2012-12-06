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
package de.cismet.cids.custom.reports.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernReportBean {

    //~ Instance fields --------------------------------------------------------

    CidsBean mauer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  mauer  DOCUMENT ME!
     */
    public MauernReportBean(final CidsBean mauer) {
        this.mauer = mauer;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getMauer() {
        return mauer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mauer  DOCUMENT ME!
     */
    public void setMauer(final CidsBean mauer) {
        this.mauer = mauer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReadyToProceed() {
        return (mauer != null);
    }
}
