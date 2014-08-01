/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieGridObject extends Sb_AbstractPictureGridObject implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridObject.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean stadtbildserie;

    private float fraction;

    private int index;
    private int amountImages = 1;

    private boolean marker;

    private DefaultListModel gridModel;
    private List<Sb_StadtbildChosenListener> stadtbildChosenListeners = new ArrayList<Sb_StadtbildChosenListener>();

    private final HashSet<CidsBean> selectedBildnummernOfSerie = new HashSet<CidsBean>();

    /**
     * imagesToShow contains the same Stadtbild-CidsBean as stadtbildserie.getBeanCollectionProperty("stadtbilder_arr"),
     * but might be ordered.
     */
    private List<CidsBean> imagesToShow;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieGridObject object.
     *
     * @param  model  DOCUMENT ME!
     */
    public Sb_stadtbildserieGridObject(final DefaultListModel model) {
        this.gridModel = model;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return stadtbildserie;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        stadtbildserie = cidsBean;
        imagesToShow = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!imagesToShow.isEmpty()) {
            amountImages = imagesToShow.size();
        } else {
            amountImages = 1;
        }
    }

    /**
     * Returns the statdbild under the marker, if such an image does not exist the vorschaubild will be returned.
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getStadtbildUnderMarker() {
        CidsBean stadtbild;
        if (!imagesToShow.isEmpty() && (index < imagesToShow.size())) {
            stadtbild = imagesToShow.get(index);
        } else {
            stadtbild = (CidsBean)stadtbildserie.getProperty("vorschaubild");
        }
        return stadtbild;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  marker  DOCUMENT ME!
     */
    public void setMarker(final boolean marker) {
        if (marker) {
            Sb_stadtbildUtils.cacheImagesForStadtbilder(imagesToShow);
        }
        this.marker = marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMarker() {
        return marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndex() {
        return index;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getFraction() {
        return fraction;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fraction  DOCUMENT ME!
     */
    public void setFraction(final float fraction) {
        this.fraction = Math.max(0.0f, Math.min(1.0f, fraction));
        this.index = (int)(this.fraction * (float)(amountImages));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmountImages() {
        return amountImages;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmountSelectedImages() {
        return selectedBildnummernOfSerie.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<CidsBean> getSelectedBildnummernOfSerie() {
        return selectedBildnummernOfSerie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void addSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        final boolean wasAdded = selectedBildnummernOfSerie.add(bildnummer);
        if (wasAdded) {
            fireStadtbildChosen(bildnummer);
            notifyModel();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void removeSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        final boolean wasRemoved = selectedBildnummernOfSerie.remove(bildnummer);
        if (wasRemoved) {
            fireStadtbildUnchosen(bildnummer);
            notifyModel();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isStadtbildSelected(final CidsBean bildnummer) {
        return selectedBildnummernOfSerie.contains(bildnummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void addOrRemoveSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        if (selectedBildnummernOfSerie.contains(bildnummer)) {
            removeSelectedBildnummerOfSerie(bildnummer);
        } else {
            addSelectedBildnummerOfSerie(bildnummer);
        }
    }

    @Override
    protected String getBildnummer() {
        final String bildnummer;
        if (!imagesToShow.isEmpty() && (index < imagesToShow.size())) {
            bildnummer = (String)imagesToShow.get(index).getProperty("bildnummer");
        } else {
            bildnummer = (String)stadtbildserie.getProperty("vorschaubild.bildnummer");
        }
        return bildnummer;
    }

    @Override
    protected int getDownloadPrority() {
        int priority;
        if (marker) {
            priority = Sb_stadtbildUtils.HIGH_PRIORITY;
        } else {
            priority = Sb_stadtbildUtils.LOW_PRIORITY;
        }
        return priority;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    protected void notifyModel() {
        // adds itself to the gridModel at the same position. to update the gridModel
        gridModel.setElementAt(
            this,
            gridModel.indexOf(this));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultListModel getModel() {
        return gridModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  model  DOCUMENT ME!
     */
    public void setModel(final DefaultListModel model) {
        this.gridModel = model;
    }

    /**
     * Modifies the order of the shown images.
     *
     * @param  sortedStatdbilder  DOCUMENT ME!
     */
    public void sortImagesToShow(final List<CidsBean> sortedStatdbilder) {
        if ((sortedStatdbilder != null) && !sortedStatdbilder.isEmpty()) {
            imagesToShow = sortedStatdbilder;
        } else {
            LOG.info("Sb_stadtbildserieGridObject.sortImagesToShow() got an empty list.");
            imagesToShow = imagesToShow = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addStadtbildChosenListener(final Sb_StadtbildChosenListener listener) {
        this.stadtbildChosenListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild  DOCUMENT ME!
     */
    public void fireStadtbildChosen(final CidsBean stadtbild) {
        for (final Sb_StadtbildChosenListener listener : this.stadtbildChosenListeners) {
            listener.stadtbildChosen(this, stadtbild);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild  DOCUMENT ME!
     */
    public void fireStadtbildUnchosen(final CidsBean stadtbild) {
        for (final Sb_StadtbildChosenListener listener : this.stadtbildChosenListeners) {
            listener.stadtbildUnchosen(this, stadtbild);
        }
    }
}
