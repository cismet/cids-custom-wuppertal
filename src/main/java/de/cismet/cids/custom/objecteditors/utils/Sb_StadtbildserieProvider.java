/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.utils;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils.RestrictionLevel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * This interface is needed to make the {@link Sb_StadtbildPreviewImage} reuseable, as it permits a possibility to
 * transfer information between the two classes.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public interface Sb_StadtbildserieProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CidsBean getStadtbildserie();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CidsBean getSelectedStadtbild();

    /**
     * DOCUMENT ME!
     */
    void previousImageSelected();

    /**
     * DOCUMENT ME!
     */
    void nextImageSelected();

    /**
     * DOCUMENT ME!
     */
    void newPreviewImageSelected();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isFirstSelected();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isLastSelected();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    RestrictionLevel getRestrictionLevel();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isEditable();

    /**
     * DOCUMENT ME!
     */
    void previewImageChanged();
}
