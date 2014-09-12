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
    private List<Sb_stadtbildserieGridObjectListener> stadtbildChosenListeners =
        new ArrayList<Sb_stadtbildserieGridObjectListener>();

    private final HashSet<CidsBean> selectedBildnummernOfSerie = new HashSet<CidsBean>();

    private boolean isInBin = false;

    /**
     * imagesToShow contains the same Stadtbild-CidsBeans as
     * stadtbildserie.getBeanCollectionProperty("stadtbilder_arr"), but might be ordered.
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

    /**
     * Returns the Stadtbildserie.
     *
     * @return  DOCUMENT ME!
     */
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
    public boolean isIsInBin() {
        return isInBin;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isInBin  DOCUMENT ME!
     */
    public void setIsInBin(final boolean isInBin) {
        this.isInBin = isInBin;
        if (isInBin) {
            fireMovedToBin();
        } else {
            fireRemovedFromBin();
        }
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
     */
    public void selectAllStadtbilder() {
        for (final CidsBean stadtbild : imagesToShow) {
            selectStadtbildOfSerie(stadtbild);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void deselectAllStadtbilder() {
        for (final CidsBean stadtbild : imagesToShow) {
            deselectStadtbildOfSerie(stadtbild);
        }
    }

    /**
     * Select a stadtbild if the the stadtbildserie is not in the bin and the serie actually contains the stadtbild.
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void selectStadtbildOfSerie(final CidsBean bildnummer) {
        if (!this.isInBin && imagesToShow.contains(bildnummer)) {
            final boolean wasAdded = selectedBildnummernOfSerie.add(bildnummer);
            if (wasAdded) {
                fireStadtbildChosen(bildnummer);
                notifyModel();
            }
        }
    }

    /**
     * Deselect a Stadtbild if the stadtbildserie is not in the bin.
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void deselectStadtbildOfSerie(final CidsBean bildnummer) {
        if (!this.isInBin) {
            final boolean wasRemoved = selectedBildnummernOfSerie.remove(bildnummer);
            if (wasRemoved) {
                fireStadtbildUnchosen(bildnummer);
                notifyModel();
            }
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
    public void selectOrDeselectStadtbild(final CidsBean bildnummer) {
        if (selectedBildnummernOfSerie.contains(bildnummer)) {
            deselectStadtbildOfSerie(bildnummer);
        } else {
            selectStadtbildOfSerie(bildnummer);
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
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getImagesToShow() {
        return imagesToShow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  imagesToShow  DOCUMENT ME!
     */
    public void setImagesToShow(final List<CidsBean> imagesToShow) {
        this.imagesToShow = imagesToShow;
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
    public void addStadtbildChosenListener(final Sb_stadtbildserieGridObjectListener listener) {
        this.stadtbildChosenListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild  DOCUMENT ME!
     */
    public void fireStadtbildChosen(final CidsBean stadtbild) {
        for (final Sb_stadtbildserieGridObjectListener listener : this.stadtbildChosenListeners) {
            listener.stadtbildChosen(this, stadtbild);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild  DOCUMENT ME!
     */
    public void fireStadtbildUnchosen(final CidsBean stadtbild) {
        for (final Sb_stadtbildserieGridObjectListener listener : this.stadtbildChosenListeners) {
            listener.stadtbildUnchosen(this, stadtbild);
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void fireMovedToBin() {
        for (final Sb_stadtbildserieGridObjectListener listener : this.stadtbildChosenListeners) {
            listener.sb_stadtbildserieGridObjectMoveToBin(this);
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void fireRemovedFromBin() {
        for (final Sb_stadtbildserieGridObjectListener listener : this.stadtbildChosenListeners) {
            listener.sb_stadtbildserieGridObjectRemovedFromBin(this);
        }
    }

    @Override
    protected boolean isPreviewAllowed() {
        final Boolean internalUsage = (Boolean)stadtbildserie.getProperty("interner_gebrauch");
        return Boolean.TRUE.equals(internalUsage);
    }
}
