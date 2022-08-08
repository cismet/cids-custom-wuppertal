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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.clientutils.StadtbilderUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.connectioncontext.ConnectionContext;

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
    private static final ExecutorService bulletPointFetcherThreadPool = CismetExecutors.newFixedThreadPool(20);

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
    private Sb_RestrictionLevelUtils.BulletPointSettings bulletPointSettings = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieGridObject object.
     *
     * @param  model              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public Sb_stadtbildserieGridObject(final DefaultListModel model,
            final ConnectionContext connectionContext) {
        super(connectionContext);

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

    @Override
    public CidsBean getStadtbildserie() {
        return stadtbildserie;
    }

    /**
     * Returns the statdbild under the marker, if such an image does not exist the vorschaubild will be returned.
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getStadtbildUnderMarker() {
        CidsBean stadtbild;
        if (!imagesToShow.isEmpty() && (index < imagesToShow.size()) && (index != 0)) {
            stadtbild = imagesToShow.get(index);
            if (stadtbild.equals((CidsBean)stadtbildserie.getProperty("vorschaubild"))) {
                stadtbild = imagesToShow.get(0);
            }
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
            StadtbilderUtils.cacheImagesForStadtbilder(
                getStadtbildserie(),
                imagesToShow,
                getConnectionContext());
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
     * Get the stadtbilder which are in the warenkorb.
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
        selecteAllStadtbilder(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fireEvents  DOCUMENT ME!
     */
    public void selecteAllStadtbilder(final boolean fireEvents) {
        for (final CidsBean stadtbild : imagesToShow) {
            selectStadtbildOfSerie(stadtbild, fireEvents);
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
     * Select (Move to warenkorb) a stadtbild if the stadtbildserie is not in the bin and the serie actually contains
     * the stadtbild.
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void selectStadtbildOfSerie(final CidsBean bildnummer) {
        selectStadtbildOfSerie(bildnummer, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     * @param  fireEvents  DOCUMENT ME!
     */
    public void selectStadtbildOfSerie(final CidsBean bildnummer, final boolean fireEvents) {
        if (!this.isInBin && imagesToShow.contains(bildnummer)) {
            final boolean wasAdded = selectedBildnummernOfSerie.add(bildnummer);
            if (wasAdded && fireEvents) {
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

    /**
     * Returns the bildnummer corresponding to the this grid object. This means that usually the bildnummer of the image
     * under the marker is returned. If the Stadtbildserie has only one image, its number is returned.
     *
     * <p>If this does not work, the image number of the vorschaubild is returned.</p>
     *
     * @return  DOCUMENT ME!
     */
    @Override
    protected StadtbilderUtils.StadtbildInfo getStadtbildInfo() {
        CidsBean stadtbild;
        if (!imagesToShow.isEmpty() && (index < imagesToShow.size()) && (index != 0)) {
            stadtbild = (CidsBean)imagesToShow.get(index);
            if (stadtbild.equals(stadtbildserie.getProperty("vorschaubild"))) {
                stadtbild = (CidsBean)imagesToShow.get(0);
            }
        } else {
            stadtbild = (CidsBean)stadtbildserie.getProperty("vorschaubild");
        }
        return new StadtbilderUtils.StadtbildInfo(stadtbildserie, stadtbild);
    }

    @Override
    protected int getDownloadPrority() {
        int priority;
        if (marker) {
            priority = StadtbilderUtils.HIGH_PRIORITY;
        } else {
            priority = StadtbilderUtils.LOW_PRIORITY;
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
        return Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(
                    stadtbildserie,
                    getConnectionContext())
                    .isPreviewAllowed();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Sb_RestrictionLevelUtils.BulletPointSettings determineBulletPointColor() {
        if (bulletPointSettings != null) {
            return bulletPointSettings;
        } else {
            final SwingWorker worker = new SwingWorker<Sb_RestrictionLevelUtils.BulletPointSettings, Void>() {

                    @Override
                    protected Sb_RestrictionLevelUtils.BulletPointSettings doInBackground() throws Exception {
                        return Sb_RestrictionLevelUtils.determineBulletPointAndInfoText(Sb_stadtbildserieGridObject.this
                                        .getCidsBean(),
                                getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            bulletPointSettings = get();
                            notifyModel();
                        } catch (InterruptedException ex) {
                            LOG.error(ex, ex);
                        } catch (ExecutionException ex) {
                            LOG.error(ex, ex);
                        }
                    }
                };
            bulletPointFetcherThreadPool.submit(worker);
            return null;
        }
    }
}
