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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatSchildEditor extends VzkatStandortEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatSchildEditor object.
     */
    public VzkatSchildEditor() {
        this(true);
    }

    /**
     * Creates a new VzkatSchildEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public VzkatSchildEditor(final boolean editable) {
        super(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        super.setCidsBean(cidsBean);
        setStandortBean((cidsBean != null) ? (CidsBean)cidsBean.getProperty("fk_standort") : null);

        if (cidsBean != null) {
            setSelectedSchildBean(cidsBean);
        }
    }
}
