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
package de.cismet.cids.custom.nas;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CheckListItem {

    //~ Instance fields --------------------------------------------------------

    private final String pnr;
    private final String ablaufdatum;
    private boolean isSelected = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CheckListItem object.
     *
     * @param  label        DOCUMENT ME!
     * @param  ablaufdatum  DOCUMENT ME!
     */
    public CheckListItem(final String label, final String ablaufdatum) {
        this.pnr = label;
        this.ablaufdatum = ablaufdatum;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isSelected  DOCUMENT ME!
     */
    public void setSelected(final boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPnr() {
        return pnr;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAblaufdatum() {
        return ablaufdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String toString() {
        return pnr + " (" + ablaufdatum + ")";
    }
}
