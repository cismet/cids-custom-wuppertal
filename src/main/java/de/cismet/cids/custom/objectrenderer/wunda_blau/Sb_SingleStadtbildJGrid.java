/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.guigarage.jgrid.JGrid;
import com.guigarage.jgrid.renderer.GridCellRenderer;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import de.cismet.cids.custom.utils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.commons.concurrency.CismetExecutors;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_SingleStadtbildJGrid extends JGrid implements Sb_stadtbildserieGridObjectListener {

    //~ Instance fields --------------------------------------------------------

    /** Is used to avoid that a Stadtbild is shown twice in the grid. */
    HashSet<Sb_SingleStadtbildGridObject> modelProxy = new HashSet<Sb_SingleStadtbildGridObject>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_SelectedStadtbilderJGrid object.
     */
    public Sb_SingleStadtbildJGrid() {
        this.setModel(new DefaultListModel<Sb_SingleStadtbildGridObject>());
        this.getCellRendererManager().setDefaultRenderer(new Sb_SingleStadtbildGridRenderer());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  stadbilder  DOCUMENT ME!
     * @param  source      DOCUMENT ME!
     */
    public void addStadtbilder(final Collection<CidsBean> stadbilder, final Sb_stadtbildserieGridObject source) {
        for (final CidsBean b : stadbilder) {
            final Sb_SingleStadtbildGridObject gridObject = new Sb_SingleStadtbildGridObject(
                    b,
                    source,
                    ((DefaultListModel)this.getModel()));
            if (!modelProxy.contains(gridObject)) {
                gridObject.startThreadToDetermineIfHighResImageAvailable();
                ((DefaultListModel)this.getModel()).addElement(gridObject);
                modelProxy.add(gridObject);
            }
        }
    }

    @Override
    public void stadtbildChosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final Sb_SingleStadtbildGridObject gridObject = new Sb_SingleStadtbildGridObject(
                stadtbild,
                source,
                ((DefaultListModel)this.getModel()));
        if (!modelProxy.contains(gridObject)) {
            gridObject.startThreadToDetermineIfHighResImageAvailable();
            ((DefaultListModel)this.getModel()).addElement(gridObject);
            modelProxy.add(gridObject);
        }
    }

    @Override
    public void stadtbildUnchosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final Sb_SingleStadtbildGridObject objectToRemove = new Sb_SingleStadtbildGridObject(
                stadtbild,
                source,
                ((DefaultListModel)this.getModel()));
        final DefaultListModel model = (DefaultListModel)this.getModel();
        if (modelProxy.contains(objectToRemove)) {
            model.removeElement(objectToRemove);
            modelProxy.remove(objectToRemove);
        }
    }

    @Override
    public void sb_stadtbildserieGridObjectMoveToBin(final Sb_stadtbildserieGridObject source) {
        final DefaultListModel model = (DefaultListModel)this.getModel();
        for (final CidsBean chosenStadtbilder : source.getSelectedBildnummernOfSerie()) {
            final Sb_SingleStadtbildGridObject objectToRemove = new Sb_SingleStadtbildGridObject(
                    chosenStadtbilder,
                    source,
                    ((DefaultListModel)this.getModel()));
            model.removeElement(objectToRemove);
            modelProxy.remove(objectToRemove);
        }
    }

    @Override
    public void sb_stadtbildserieGridObjectRemovedFromBin(final Sb_stadtbildserieGridObject source) {
        final DefaultListModel model = (DefaultListModel)this.getModel();
        for (final CidsBean chosenStadtbilder : source.getSelectedBildnummernOfSerie()) {
            final Sb_SingleStadtbildGridObject objectToAdd = new Sb_SingleStadtbildGridObject(
                    chosenStadtbilder,
                    source,
                    ((DefaultListModel)this.getModel()));
            objectToAdd.startThreadToDetermineIfHighResImageAvailable();
            model.addElement(objectToAdd);
            modelProxy.add(objectToAdd);
        }
    }

    /**
     * Remove the selection of the selected Stadtbilder in the grid.
     */
    public void unchoseStadtbilderSelectedInTheGrid() {
        final List<Sb_SingleStadtbildGridObject> selectedObjects = this.getSelectedValuesList();
        for (final Sb_SingleStadtbildGridObject object : selectedObjects) {
            final Sb_stadtbildserieGridObject sb_stadtbildserieGridObject = object.getLocationOfStadtbild();
            sb_stadtbildserieGridObject.deselectStadtbildOfSerie(object.getStadtbild());
        }
    }
}
