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

import java.awt.Component;
import java.awt.Image;

import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_SingleStadtbildJGrid extends JGrid implements Sb_stadtbildserieGridObjectListener {

    //~ Instance fields --------------------------------------------------------

    /** Is used to avoid that a Stadtbild is shown twice in the grid. */
    HashSet<SingleStadtbildGridObject> modelProxy = new HashSet<SingleStadtbildGridObject>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_SelectedStadtbilderJGrid object.
     */
    public Sb_SingleStadtbildJGrid() {
        this.setModel(new DefaultListModel<SingleStadtbildGridObject>());
        this.getCellRendererManager().setDefaultRenderer(new SingleStadtbildGridRenderer());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void stadtbildChosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final SingleStadtbildGridObject gridObject = new SingleStadtbildGridObject(stadtbild, source);
        if (!modelProxy.contains(gridObject)) {
            ((DefaultListModel)this.getModel()).addElement(gridObject);
            modelProxy.add(gridObject);
        }
    }

    @Override
    public void stadtbildUnchosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final SingleStadtbildGridObject objectToRemove = new SingleStadtbildGridObject(stadtbild, source);
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
            final SingleStadtbildGridObject objectToRemove = new SingleStadtbildGridObject(chosenStadtbilder, source);
            model.removeElement(objectToRemove);
            modelProxy.remove(objectToRemove);
        }
    }

    @Override
    public void sb_stadtbildserieGridObjectRemovedFromBin(final Sb_stadtbildserieGridObject source) {
        final DefaultListModel model = (DefaultListModel)this.getModel();
        for (final CidsBean chosenStadtbilder : source.getSelectedBildnummernOfSerie()) {
            final SingleStadtbildGridObject objectToRemove = new SingleStadtbildGridObject(chosenStadtbilder, source);
            model.addElement(objectToRemove);
            modelProxy.add(objectToRemove);
        }
    }

    /**
     * Remove the selection of the selected Stadtbilder in the grid.
     */
    public void unchoseStadtbilderSelectedInTheGrid() {
        final List<SingleStadtbildGridObject> selectedObjects = this.getSelectedValuesList();
        for (final SingleStadtbildGridObject object : selectedObjects) {
            final Sb_stadtbildserieGridObject sb_stadtbildserieGridObject = object.locationOfStadtbild;
            sb_stadtbildserieGridObject.removeSelectedBildnummerOfSerie(object.stadtbild);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A container for a stadtbild and a Sb_stadtbildserieGridObject. The Sb_stadtbildserieGridObject is the gridObject
     * used in the Vorschau and the Bin for a Stadtbildserie which contains the stadtbild.
     *
     * @version  $Revision$, $Date$
     */
    private class SingleStadtbildGridObject extends Sb_AbstractPictureGridObject {

        //~ Instance fields ----------------------------------------------------

        CidsBean stadtbild;
        Sb_stadtbildserieGridObject locationOfStadtbild;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridObject object.
         *
         * @param  stadtbild            DOCUMENT ME!
         * @param  locationOfStadtbild  DOCUMENT ME!
         */
        public SingleStadtbildGridObject(final CidsBean stadtbild,
                final Sb_stadtbildserieGridObject locationOfStadtbild) {
            this.stadtbild = stadtbild;
            this.locationOfStadtbild = locationOfStadtbild;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getBildnummer() {
            return (String)stadtbild.getProperty("bildnummer");
        }

        @Override
        protected int getDownloadPrority() {
            return Sb_stadtbildUtils.NORMAL_PRIORITY;
        }

        @Override
        protected void notifyModel() {
            final DefaultListModel gridModel = (DefaultListModel)Sb_SingleStadtbildJGrid.this.getModel();
            // adds itself to the gridModel at the same position. to update the gridModel
            gridModel.setElementAt(
                this,
                gridModel.indexOf(this));
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = (11 * hash) + ((this.stadtbild != null) ? this.stadtbild.hashCode() : 0);
            hash = (11 * hash) + ((this.locationOfStadtbild != null) ? this.locationOfStadtbild.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SingleStadtbildGridObject other = (SingleStadtbildGridObject)obj;
            if ((this.stadtbild != other.stadtbild)
                        && ((this.stadtbild == null) || !this.stadtbild.equals(other.stadtbild))) {
                return false;
            }
            if ((this.locationOfStadtbild != other.locationOfStadtbild)
                        && ((this.locationOfStadtbild == null)
                            || !this.locationOfStadtbild.equals(other.locationOfStadtbild))) {
                return false;
            }
            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SingleStadtbildGridRenderer extends javax.swing.JLabel implements GridCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private Image image;

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getGridCellRendererComponent(final JGrid grid,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            image = null;
            if (value instanceof SingleStadtbildGridObject) {
                image = ((SingleStadtbildGridObject)value).getImage(grid.getFixedCellDimension(), false);
            }

            if (image != null) {
                this.setIcon(new ImageIcon(image));
            } else {
                this.setIcon(new ImageIcon(Sb_stadtbildUtils.ERROR_IMAGE));
            }

            return this;
        }
    }
}
