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
public class Sb_SelectedStadtbilderJGrid extends JGrid implements Sb_StadtbildChosenListener {

    //~ Instance fields --------------------------------------------------------

    HashSet<GridObject> modelProxy = new HashSet<GridObject>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_SelectedStadtbilderJGrid object.
     */
    public Sb_SelectedStadtbilderJGrid() {
        this.setModel(new DefaultListModel<GridObject>());
        this.getCellRendererManager().setDefaultRenderer(new GridRenderer());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void stadtbildChosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final GridObject gridObject = new GridObject(stadtbild, source);
        if (!modelProxy.contains(gridObject)) {
            ((DefaultListModel)this.getModel()).addElement(gridObject);
            modelProxy.add(gridObject);
        }
    }

    @Override
    public void stadtbildUnchosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        GridObject objectToRemove = null;
        final DefaultListModel model = (DefaultListModel)this.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final GridObject object = (GridObject)model.elementAt(i);
            if (object.stadtbild.equals(stadtbild)) {
                objectToRemove = object;
                break;
            }
        }
        if (objectToRemove != null) {
            model.removeElement(objectToRemove);
            modelProxy.remove(objectToRemove);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void unchoseStadtbilderSelectedInTheGrid() {
        final List<GridObject> selectedObjects = this.getSelectedValuesList();
        for (final GridObject object : selectedObjects) {
            final Sb_stadtbildserieGridObject sb_stadtbildserieGridObject = object.locationOfStadtbild;
            sb_stadtbildserieGridObject.removeSelectedBildnummerOfSerie(object.stadtbild);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class GridObject extends Sb_AbstractPictureGridObject {

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
        public GridObject(final CidsBean stadtbild, final Sb_stadtbildserieGridObject locationOfStadtbild) {
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
            final DefaultListModel gridModel = (DefaultListModel)Sb_SelectedStadtbilderJGrid.this.getModel();
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
            final GridObject other = (GridObject)obj;
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
    private class GridRenderer extends javax.swing.JLabel implements GridCellRenderer {

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
            if (value instanceof GridObject) {
                image = ((GridObject)value).getImage(grid.getFixedCellDimension(), false);
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
