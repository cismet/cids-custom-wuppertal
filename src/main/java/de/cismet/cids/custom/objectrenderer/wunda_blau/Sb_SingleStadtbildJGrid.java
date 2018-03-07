/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.guigarage.jgrid.JGrid;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_SingleStadtbildJGrid extends JGrid implements Sb_stadtbildserieGridObjectListener,
    ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    /** Is used to avoid that a Stadtbild is shown twice in the grid. */
    private final HashSet<Sb_SingleStadtbildGridObject> modelProxy = new HashSet<>();
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_SelectedStadtbilderJGrid object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public Sb_SingleStadtbildJGrid(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.setModel(new DefaultListModel<>());
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
                    ((DefaultListModel)this.getModel()),
                    getConnectionContext());
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
                ((DefaultListModel)this.getModel()),
                getConnectionContext());
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
                ((DefaultListModel)this.getModel()),
                getConnectionContext());
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
                    ((DefaultListModel)this.getModel()),
                    getConnectionContext());
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
                    ((DefaultListModel)this.getModel()),
                    getConnectionContext());
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

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
