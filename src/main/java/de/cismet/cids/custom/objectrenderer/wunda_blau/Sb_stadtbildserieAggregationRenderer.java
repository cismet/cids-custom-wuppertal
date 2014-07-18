/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import org.openide.util.Exceptions;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    private Collection<CidsBean> cidsBeans = null;
    private JXLayer layer;
    private LockableUI lockableUIInfoPanel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBin;
    private com.guigarage.jgrid.JGrid grdBin;
    private com.guigarage.jgrid.JGrid grdStadtbildserien;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel infoPanel;
    private javax.swing.JLabel lblAmounts;
    private javax.swing.JPanel pnlLeuchtkasten;
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
                    if ((lastIndex >= 0) && (lastIndex < grdStadtbildserien.getModel().getSize())) {
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
                        int[] indexes = new int[0];
                        final ListSelectionModel sm = grdStadtbildserien.getSelectionModel();
                        final int iMin = sm.getMinSelectionIndex();
                        final int iMax = sm.getMaxSelectionIndex();

                        if ((iMin >= 0) && (iMin == iMax)) {
                            indexes = new int[1];
                            indexes[0] = iMin;
                        }
                        if (indexes.length == 1) {
                            final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)
                                grdStadtbildserien.getModel().getElementAt(indexes[0]);
                            infoPanel.setGridObject(gridObject);
                            lockableUIInfoPanel.setLocked(false);
                            lockableUIInfoPanel.setEnabled(false);
                        } else {
                            // a new lockable UI has to be created every time, as otherwise rendering errors will occur
                            createNewLockableUI();
                        }
                    }
                }
            });
        configureLockableInfoPanel();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        btnBin = new javax.swing.JButton();
        lblAmounts = new javax.swing.JLabel();
        pnlLeuchtkasten = new javax.swing.JPanel();
        grdStadtbildserien = new com.guigarage.jgrid.JGrid();
        grdBin = new com.guigarage.jgrid.JGrid();
        infoPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        roundedPanel1.setMinimumSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        btnBin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_empty.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnBin,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBin.text"));                                       // NOI18N
        btnBin.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBin.toolTipText"));                                // NOI18N
        btnBin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBinActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        roundedPanel1.add(btnBin, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAmounts,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblAmounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        roundedPanel1.add(lblAmounts, gridBagConstraints);

        pnlLeuchtkasten.setLayout(new java.awt.CardLayout());

        grdStadtbildserien.setOpaque(false);
        pnlLeuchtkasten.add(grdStadtbildserien, "SERIEN");
        final DefaultListModel<Sb_stadtbildserieGridObject> gridModel =
            new DefaultListModel<Sb_stadtbildserieGridObject>();
        grdStadtbildserien.setModel(gridModel);
        grdStadtbildserien.getCellRendererManager()
                .setDefaultRenderer(new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridRenderer());

        grdBin.setOpaque(false);
        pnlLeuchtkasten.add(grdBin, "BIN");
        final DefaultListModel<Sb_stadtbildserieGridObject> gridBinModel =
            new DefaultListModel<Sb_stadtbildserieGridObject>();
        grdStadtbildserien.setModel(gridBinModel);
        grdStadtbildserien.getCellRendererManager()
                .setDefaultRenderer(new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridRenderer());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(pnlLeuchtkasten, gridBagConstraints);

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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBinActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBinActionPerformed
        final List<Sb_stadtbildserieGridObject> gridObjectsToRemove = grdStadtbildserien.getSelectedValuesList();

        for (final Sb_stadtbildserieGridObject gridObject : gridObjectsToRemove) {
            ((DefaultListModel)grdStadtbildserien.getModel()).removeElement(gridObject);
            ((DefaultListModel)grdBin.getModel()).addElement(gridObject);
        }

        grdStadtbildserien.getSelectionModel().clearSelection();
    } //GEN-LAST:event_btnBinActionPerformed

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
     * removes the info panel from this panel, wraps it in a lockable JXLayer and adds that layer again to the same
     * location on this panel.
     */
    private void configureLockableInfoPanel() {
        final GridBagConstraints gbc = ((GridBagLayout)this.getLayout()).getConstraints(infoPanel);
        remove(infoPanel);
        layer = new JXLayer(infoPanel);
        layer.setOpaque(false);
        add(layer, gbc);
        infoPanel.setOpaque(false);

        createNewLockableUI();
    }

    /**
     * DOCUMENT ME!
     */
    private void createNewLockableUI() {
        lockableUIInfoPanel = new LockableUI();

        final float[] blurKernel = { 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f };
        final BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));

        // wrap it with the jxlayer's BufferedImageOpEffect
        final BufferedImageOpEffect effect = new BufferedImageOpEffect(blur);
        // set it as the locked effect
        lockableUIInfoPanel.setLockedEffects(effect);

        lockableUIInfoPanel.setLockedCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // lock the layer
        lockableUIInfoPanel.setEnabled(true);
        lockableUIInfoPanel.setLocked(true);

        layer.setUI(lockableUIInfoPanel);
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
