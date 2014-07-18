/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.openide.util.Exceptions;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieAggregationRenderer extends javax.swing.JPanel implements RequestsFullSizeComponent,
    CidsBeanAggregationRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final HashMap<CidsBean, HashSet<CidsBean>> selectedBildnummernOfSerie =
        new HashMap<CidsBean, HashSet<CidsBean>>();

    private Collection<CidsBean> cidsBeans = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.guigarage.jgrid.JGrid grdStadtbildserien;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel infoPanel;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_stadtbildserieAggregationRenderer.
     */
    public Sb_stadtbildserieAggregationRenderer() {
        initComponents();
        grdStadtbildserien.addMouseMotionListener(new MouseAdapter() {

                int lastIndex = -1;

                @Override
                public void mouseMoved(final MouseEvent e) {
                    if (lastIndex >= 0) {
                        final Object o = grdStadtbildserien.getModel().getElementAt(lastIndex);
                        if (o instanceof Sb_stadtbildserieGridObject) {
                            final Rectangle r = grdStadtbildserien.getCellBounds(lastIndex);
                            if ((r != null) && !r.contains(e.getPoint())) {
                                // remove the marker once
                                if (((Sb_stadtbildserieGridObject)o).isMarker()) {
                                    ((Sb_stadtbildserieGridObject)o).setMarker(false);
                                    grdStadtbildserien.repaint(r);
                                }
                            }
                        }
                    }

                    final int index = grdStadtbildserien.getCellAt(e.getPoint());
                    if (index >= 0) {
                        final Object o = grdStadtbildserien.getModel().getElementAt(index);
                        if (o instanceof Sb_stadtbildserieGridObject) {
                            final Rectangle r = grdStadtbildserien.getCellBounds(index);
                            if (r != null) {
                                ((Sb_stadtbildserieGridObject)o).setFraction(
                                    ((float)e.getPoint().x - (float)r.x)
                                            / (float)r.width);
                                ((Sb_stadtbildserieGridObject)o).setMarker(true);
                                lastIndex = index;
                                grdStadtbildserien.repaint(r);
                            }
                        }
                    }
                }
            });
        grdStadtbildserien.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int[] indexes;
                        final ListSelectionModel sm = grdStadtbildserien.getSelectionModel();
                        final int iMin = sm.getMinSelectionIndex();
                        final int iMax = sm.getMaxSelectionIndex();

                        if ((iMin < 0) || (iMax < 0)) {
                            indexes = new int[0];
                        } else {
                            final int[] rvTmp = new int[1 + (iMax - iMin)];
                            int n = 0;
                            for (int i = iMin; i <= iMax; i++) {
                                if (sm.isSelectedIndex(i)) {
                                    rvTmp[n++] = i;
                                }
                            }
                            final int[] rv = new int[n];
                            System.arraycopy(rvTmp, 0, rv, 0, n);
                            indexes = rv;
                        }
                        if (indexes.length == 1) {
                            final CidsBean selectedSerie = (CidsBean)
                                ((Sb_stadtbildserieGridObject)grdStadtbildserien.getModel().getElementAt(indexes[0]))
                                        .getCidsBean();
                            infoPanel.setCidsBean(selectedSerie);
                        }
                    }
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   stadtbildserie  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection getSelectedBildnummernOfSerie(final CidsBean stadtbildserie) {
        if (selectedBildnummernOfSerie.containsKey(stadtbildserie)) {
            return selectedBildnummernOfSerie.get(stadtbildserie);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbildserie  DOCUMENT ME!
     * @param  bildnummer      DOCUMENT ME!
     */
    public void putSelectedBildnummerOfSerie(final CidsBean stadtbildserie, final CidsBean bildnummer) {
        final HashSet<CidsBean> set;
        if (!selectedBildnummernOfSerie.containsKey(stadtbildserie)) {
            set = new HashSet<CidsBean>();
            set.add(bildnummer);
            selectedBildnummernOfSerie.put(stadtbildserie, set);
        } else {
            selectedBildnummernOfSerie.get(stadtbildserie).add(bildnummer);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbildserie  DOCUMENT ME!
     * @param  bildnummer      DOCUMENT ME!
     */
    public void removeSelectedBildnummerOfSerie(final CidsBean stadtbildserie, final CidsBean bildnummer) {
        if (selectedBildnummernOfSerie.containsKey(stadtbildserie)) {
            final HashSet<CidsBean> set = selectedBildnummernOfSerie.remove(bildnummer);
            set.remove(bildnummer);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        grdStadtbildserien = new com.guigarage.jgrid.JGrid();
        infoPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        roundedPanel1.setMinimumSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        grdStadtbildserien.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(grdStadtbildserien, gridBagConstraints);
        final DefaultListModel<Sb_stadtbildserieGridObject> gridModel =
            new DefaultListModel<Sb_stadtbildserieGridObject>();
        grdStadtbildserien.setModel(gridModel);
        grdStadtbildserien.getCellRendererManager()
                .setDefaultRenderer(new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridRenderer());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(roundedPanel1, gridBagConstraints);

        infoPanel.setMinimumSize(new java.awt.Dimension(100, 0));
        infoPanel.setPreferredSize(new java.awt.Dimension(100, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(infoPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        this.cidsBeans = beans;
        if (beans != null) {
            infoPanel.setAggregationRenderer(this);
            final DefaultListModel model = (DefaultListModel)grdStadtbildserien.getModel();
            for (final CidsBean bean : beans) {
                final Sb_stadtbildserieGridObject gridObject = new Sb_stadtbildserieGridObject(model);
                gridObject.setCidsBean(bean);
                model.addElement(gridObject);
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return "Leuchtkasten";
    }

    @Override
    public void setTitle(final String title) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                    "WUNDA_BLAU",
                    "Administratoren",
                    "admin",
                    "kif",
                    "sb_stadtbildserie",
                    " id = 5 or id = 6 or id = 285195 or id = 8 or id = 9 or id = 10 or id = 11 or  id = 285198",
                    10);

            DevelopmentTools.createAggregationRendererInFrameFromRMIConnectionOnLocalhost(Arrays.asList(beans),
                "Leuchtkasten",
                1024,
                800);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
