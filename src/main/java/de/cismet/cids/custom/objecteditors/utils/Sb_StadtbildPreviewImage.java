/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.utils;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import de.cismet.cids.custom.clientutils.StadtbilderUtils;
import de.cismet.cids.custom.clientutils.TifferDownload;
import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditor;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor.adjustScale;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_StadtbildPreviewImage extends javax.swing.JPanel implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final ImageIcon FOLDER_ICON = new ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inode-directory.png"));
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_StadtbildPreviewImage.class);

    //~ Instance fields --------------------------------------------------------

    private Sb_StadtbildserieProvider stadtbildserieProvider;
    private Sb_StadtbildPreviewImage.ImageResizeWorker currentResizeWorker;
    private boolean resizeListenerEnabled;
    private final Timer timer;
    private BufferedImage image;
    private CidsBean fotoCidsBean;
    private final ConnectionContext connectionContext;
    private final PropertyChangeListener listRepaintListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                stadtbildserieProvider.previewImageChanged();
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownloadHighResImage;
    private javax.swing.JButton btnNextImg;
    private javax.swing.JButton btnPrevImg;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanel4;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JPanel pnlCtrlBtn;
    private javax.swing.JPanel pnlFoto;
    private de.cismet.tools.gui.RoundedPanel pnlVorschau;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private javax.swing.JToggleButton tbtnIsPreviewImage;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_StadtbildPreviewImage object.
     */
    public Sb_StadtbildPreviewImage() {
        this(ConnectionContext.createDummy());
    }

    /**
     * Creates new form Sb_StadtbildPreviewImage.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public Sb_StadtbildPreviewImage(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new Sb_StadtbildPreviewImage.ImageResizeWorker();
                            currentResizeWorker.execute();
                        }
                    }
                });
        timer.setRepeats(false);
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

        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        pnlFoto = new javax.swing.JPanel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        jPanel4 = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        pnlCtrlBtn = new javax.swing.JPanel();
        btnDownloadHighResImage = new EnableOnlyIfNotInternalUsageAndNotRendererJButton();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        tbtnIsPreviewImage = new javax.swing.JToggleButton();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlVorschau.setPreferredSize(new java.awt.Dimension(140, 300));
        pnlVorschau.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        lblVorschau.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorschau,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.CardLayout());

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 60));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 60));
        pnlFoto.add(lblBusy, "busy");

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPicture,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.lblPicture.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(lblPicture, gridBagConstraints);

        pnlFoto.add(jPanel4, "image");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVorschau.add(pnlFoto, gridBagConstraints);

        pnlCtrlBtn.setMinimumSize(new java.awt.Dimension(100, 50));
        pnlCtrlBtn.setOpaque(false);
        pnlCtrlBtn.setPreferredSize(new java.awt.Dimension(100, 50));
        pnlCtrlBtn.setLayout(new java.awt.GridBagLayout());

        btnDownloadHighResImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelDownload.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnDownloadHighResImage,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.btnDownloadHighResImage.text"));                   // NOI18N
        btnDownloadHighResImage.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.btnDownloadHighResImage.toolTipText"));            // NOI18N
        btnDownloadHighResImage.setBorder(null);
        btnDownloadHighResImage.setBorderPainted(false);
        btnDownloadHighResImage.setContentAreaFilled(false);
        btnDownloadHighResImage.setEnabled(false);
        btnDownloadHighResImage.setFocusPainted(false);
        btnDownloadHighResImage.setMaximumSize(new java.awt.Dimension(30, 30));
        btnDownloadHighResImage.setMinimumSize(new java.awt.Dimension(30, 30));
        btnDownloadHighResImage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDownloadHighResImageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlCtrlBtn.add(btnDownloadHighResImage, gridBagConstraints);

        btnPrevImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png")));          // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPrevImg,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.btnPrevImg.text"));                                              // NOI18N
        btnPrevImg.setBorder(null);
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setContentAreaFilled(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.setMaximumSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setMinimumSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setPreferredSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-pressed.png")));  // NOI18N
        btnPrevImg.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-selected.png"))); // NOI18N
        btnPrevImg.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-selected.png"))); // NOI18N
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png")));          // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnNextImg,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.btnNextImg.text"));                                               // NOI18N
        btnNextImg.setBorder(null);
        btnNextImg.setBorderPainted(false);
        btnNextImg.setContentAreaFilled(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.setMaximumSize(new java.awt.Dimension(30, 30));
        btnNextImg.setMinimumSize(new java.awt.Dimension(30, 30));
        btnNextImg.setPreferredSize(new java.awt.Dimension(30, 30));
        btnNextImg.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-pressed.png")));  // NOI18N
        btnNextImg.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-selected.png"))); // NOI18N
        btnNextImg.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-selected.png"))); // NOI18N
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlCtrlBtn.add(btnNextImg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCtrlBtn.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCtrlBtn.add(filler2, gridBagConstraints);

        tbtnIsPreviewImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"))); // NOI18N
        tbtnIsPreviewImage.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildPreviewImage.class,
                "Sb_StadtbildPreviewImage.tbtnIsPreviewImage.toolTipText"));                     // NOI18N
        tbtnIsPreviewImage.setBorderPainted(false);
        tbtnIsPreviewImage.setContentAreaFilled(false);
        tbtnIsPreviewImage.setDisabledIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"))); // NOI18N
        tbtnIsPreviewImage.setDisabledSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png")));   // NOI18N
        tbtnIsPreviewImage.setEnabled(false);
        tbtnIsPreviewImage.setMaximumSize(new java.awt.Dimension(30, 30));
        tbtnIsPreviewImage.setMinimumSize(new java.awt.Dimension(30, 30));
        tbtnIsPreviewImage.setPreferredSize(new java.awt.Dimension(32, 32));
        tbtnIsPreviewImage.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png")));   // NOI18N
        tbtnIsPreviewImage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnIsPreviewImageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCtrlBtn.add(tbtnIsPreviewImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlVorschau.add(pnlCtrlBtn, gridBagConstraints);

        add(pnlVorschau, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDownloadHighResImageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDownloadHighResImageActionPerformed
        try {
            if (
                BillingPopup.doBilling(
                            "stb",
                            "not.yet",
                            (Geometry)null,
                            getConnectionContext(),
                            new BillingProductGroupAmount("ea", 1))
                        && DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                final CidsBean stadtbildSerie = stadtbildserieProvider.getStadtbildserie();
                final CidsBean stadtbild = stadtbildserieProvider.getSelectedStadtbild();
                final String imageNumber = (String)stadtbild.getProperty("bildnummer");
                DownloadManager.instance()
                        .add(new TifferDownload(
                                jobname,
                                "Stadtbild "
                                + imageNumber,
                                "stadtbild_"
                                + imageNumber,
                                new StadtbilderUtils.StadtbildInfo(stadtbildSerie, stadtbild),
                                "1",
                                getConnectionContext()));
            }
        } catch (Exception ex) {
            LOG.error("Error when trying to download an high res image", ex);
        }
    }                                                                                           //GEN-LAST:event_btnDownloadHighResImageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPrevImgActionPerformed
        stadtbildserieProvider.previousImageSelected();
    }                                                                              //GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNextImgActionPerformed
        stadtbildserieProvider.nextImageSelected();
    }                                                                              //GEN-LAST:event_btnNextImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnIsPreviewImageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnIsPreviewImageActionPerformed
        if (tbtnIsPreviewImage.isSelected()) {
            stadtbildserieProvider.newPreviewImageSelected();
            tbtnIsPreviewImage.setEnabled(false);
        }
    }                                                                                      //GEN-LAST:event_tbtnIsPreviewImageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbildInfo  bildnummer DOCUMENT ME!
     */
    public void setStadtbildInfo(final StadtbilderUtils.StadtbildInfo stadtbildInfo) {
        if (stadtbildInfo.getBildnummer() != null) {
            new CheckAccessibilityOfHighResImage(stadtbildInfo).execute();
            loadPhoto();
            final String oldPreviewImage = (String)stadtbildserieProvider.getStadtbildserie()
                        .getProperty("vorschaubild.bildnummer");
            final boolean isPreviewImage = (oldPreviewImage != null)
                        && oldPreviewImage.equals(stadtbildInfo.getBildnummer());
            tbtnIsPreviewImage.setSelected(isPreviewImage);
            tbtnIsPreviewImage.setEnabled(stadtbildserieProvider.isEditable() && !isPreviewImage);
        } else {
            tbtnIsPreviewImage.setSelected(false);
            tbtnIsPreviewImage.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbildserieProvider  DOCUMENT ME!
     */
    public void setStadtbildserieProvider(final Sb_StadtbildserieProvider stadtbildserieProvider) {
        this.stadtbildserieProvider = stadtbildserieProvider;
    }

    /**
     * Loads the photo of the currently selected stadtbild. The photo is not loaded if the stadtbildserie is restricted.
     */
    private void loadPhoto() {
        if (stadtbildserieProvider.getRestrictionLevel().isPreviewAllowed()) {
            final Object stadtbild = stadtbildserieProvider.getSelectedStadtbild();
            if (fotoCidsBean != null) {
                fotoCidsBean.removePropertyChangeListener(listRepaintListener);
            }
            if (stadtbild instanceof CidsBean) {
                fotoCidsBean = (CidsBean)stadtbild;
                fotoCidsBean.addPropertyChangeListener(listRepaintListener);
                final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                        stadtbildserieProvider.getStadtbildserie(),
                        fotoCidsBean);
                new Sb_StadtbildPreviewImage.LoadSelectedImageWorker(stadtbildInfo).execute();
            } else {
                image = null;
                lblPicture.setIcon(FOLDER_ICON);
            }
        } else {
            indicateInternalUsage();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (wait) {
            if (!lblBusy.isBusy()) {
                ((CardLayout)pnlFoto.getLayout()).show(pnlFoto, "busy");
                lblBusy.setBusy(true);
                btnPrevImg.setEnabled(false);
                btnNextImg.setEnabled(false);
            }
        } else {
            ((CardLayout)pnlFoto.getLayout()).show(pnlFoto, "image");
            lblBusy.setBusy(false);
            defineButtonStatus();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        if (stadtbildserieProvider != null) {
            btnPrevImg.setEnabled(!stadtbildserieProvider.isFirstSelected());
            btnNextImg.setEnabled(!stadtbildserieProvider.isLastSelected());
        } else {
            btnPrevImg.setEnabled(false);
            btnNextImg.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        lblPicture.setIcon(new ImageIcon(StadtbilderUtils.ERROR_IMAGE));
        lblPicture.setText("<html>Fehler beim Übertragen des Bildes!</html>");
        lblPicture.setToolTipText(tooltip);
        showWait(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    public void indicateNotAvailable(final String tooltip) {
        indicateNotAvailable(
            tooltip,
            new ImageIcon(StadtbilderUtils.ERROR_IMAGE),
            "<html>Kein Vorschaubild vorhanden.</html>");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     * @param  icon     DOCUMENT ME!
     * @param  text     DOCUMENT ME!
     */
    public void indicateNotAvailable(final String tooltip, final Icon icon, final String text) {
        lblPicture.setIcon(icon);
        lblPicture.setText(text);
        lblPicture.setToolTipText(tooltip);
        showWait(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void indicateInternalUsage() {
        lblPicture.setIcon(new ImageIcon(StadtbilderUtils.ERROR_IMAGE));
        lblPicture.setText("<html>Bild ist nicht zur Publikation freigegeben!</html>");
        lblPicture.setToolTipText("");
        showWait(false);
    }

    /**
     * DOCUMENT ME!
     */
    public void removeImage() {
        image = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon getIcon() {
        return lblPicture.getIcon();
    }

    /**
     * DOCUMENT ME!
     */
    public void makeEditable() {
        if (!stadtbildserieProvider.isEditable()) { // is Renderer
            RendererTools.makeReadOnly(tbtnIsPreviewImage);
        } else {
            ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
                tbtnIsPreviewImage,
                Cursor.HAND_CURSOR,
                Cursor.DEFAULT_CURSOR);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  visible  DOCUMENT ME!
     */
    public void setBtnDownloadHighResImageVisible(final boolean visible) {
        btnDownloadHighResImage.setVisible(visible);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  visible  DOCUMENT ME!
     */
    public void setTbtnIsPreviewImageVisible(final boolean visible) {
        tbtnIsPreviewImage.setVisible(visible);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A JButton which gets only enabled if the shown Stadtbildserie is not in the renderer and not for internal usage
     * only.
     *
     * @version  $Revision$, $Date$
     */
    private class EnableOnlyIfNotInternalUsageAndNotRendererJButton extends JButton {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setEnabled(final boolean enable) {
            boolean isDownloadAllowed = false;
            if (stadtbildserieProvider != null) {
                isDownloadAllowed = stadtbildserieProvider.getRestrictionLevel().isDownloadAllowed();
            }

            super.setEnabled(enable && isDownloadAllowed);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
                final ImageIcon result = new ImageIcon(adjustScale(image, pnlFoto, 20, 20));
                return result;
            } else {
                return null;
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    lblPicture.setIcon(result);
                    lblPicture.setText("");
                    lblPicture.setToolTipText(null);
                } catch (InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (ExecutionException ex) {
                    LOG.error(ex, ex);
                    lblPicture.setIcon(null);
                    lblPicture.setText("<html>Fehler beim Skalieren!</html>");
                } finally {
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final StadtbilderUtils.StadtbildInfo stadtbildInfo;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  stadtbildInfo  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final StadtbilderUtils.StadtbildInfo stadtbildInfo) {
            this.stadtbildInfo = stadtbildInfo;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(!StadtbilderUtils.isBildnummerInCacheOrFailed(stadtbildInfo));
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected BufferedImage doInBackground() throws Exception {
            if (stadtbildInfo != null) {
                return StadtbilderUtils.downloadImageForBildnummer(stadtbildInfo);
            }
            return null;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    resizeListenerEnabled = true;
                    timer.setInitialDelay(0);
                    timer.restart();
                } else {
                    indicateNotAvailable("");
                }
            } catch (InterruptedException ex) {
                image = null;
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                LOG.error(ex, ex);
                if (ex.getCause() instanceof UniformInterfaceException) {
                    indicateNotAvailable("");
                } else {
                    indicateError(ex.getMessage());
                }
            } finally {
                showWait(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class CheckAccessibilityOfHighResImage extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        private final StadtbilderUtils.StadtbildInfo stadtbildInfo;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CheckAccessibilityOfHighResImage object.
         *
         * @param  stadtbildInfo  imageNumber DOCUMENT ME!
         */
        public CheckAccessibilityOfHighResImage(final StadtbilderUtils.StadtbildInfo stadtbildInfo) {
            this.stadtbildInfo = stadtbildInfo;
            btnDownloadHighResImage.setEnabled(false);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Boolean doInBackground() throws Exception {
            if (stadtbildserieProvider.getRestrictionLevel().isDownloadAllowed()) {
                return StadtbilderUtils.getFormatOfHighResPicture(stadtbildInfo)
                            != null;
            } else {
                return false;
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final boolean accessible = get();
                btnDownloadHighResImage.setEnabled(accessible);
                if (accessible) {
                    ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
                        btnDownloadHighResImage,
                        Cursor.HAND_CURSOR,
                        Cursor.DEFAULT_CURSOR);
                }
            } catch (InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                LOG.warn(ex, ex);
            }
        }
    }
}
