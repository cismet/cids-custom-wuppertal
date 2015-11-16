/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CoolThemaRenderer.java
 *
 * Created on 10. November 3508, 11:56
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;

import java.awt.CardLayout;
import java.awt.event.MouseListener;

import java.sql.Date;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.annotations.AggregationRenderer;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * de.cismet.cids.objectrenderer.CoolThemaRenderer.
 *
 * <p>Renderer for the "Thema"-theme</p>
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
@AggregationRenderer
public class Alb_baulastEditor extends JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(Alb_baulastEditor.class);
    public static final String TITLE_AGR_PREFIX = "Baulasten";
    private static final String ACTION_TAG = "custom.baulast.document@WUNDA_BLAU";
    private static final Converter<java.sql.Date, String> DATE_TO_STRING = new Converter<Date, String>() {

            @Override
            public String convertForward(final Date value) {
                if (value != null) {
                    return DateFormat.getDateInstance().format(value);
                } else {
                    return "";
                }
            }

            @Override
            public Date convertReverse(final String value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private final CardLayout cardLayout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.objecteditors.wunda_blau.Alb_picturePanel alb_picturePanel;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblBearbeiter;
    private javax.swing.JLabel lblBearbeitetAm;
    private javax.swing.JLabel lblDurch;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblLetzteBearbeitung;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastEditorPanel panMain;
    private javax.swing.JPanel panTitle;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolThemaRenderer.
     */
    public Alb_baulastEditor() {
        this(true);
    }

    /**
     * Creates new form CoolThemaRenderer.
     *
     * @param  editable  DOCUMENT ME!
     */
    public Alb_baulastEditor(final boolean editable) {
        this.editable = editable;
        this.alb_picturePanel = new de.cismet.cids.custom.objecteditors.wunda_blau.Alb_picturePanel(!editable, false);
        alb_picturePanel.getDocTypePanel().setVisible(false);
        this.initComponents();
        initFooterElements();
        cardLayout = (CardLayout)getLayout();
        lblBearbeiter.setVisible(editable);
        lblLetzteBearbeitung.setVisible(editable);
        lblDurch.setVisible(editable);
        lblBearbeitetAm.setVisible(editable);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public static void addPruefungsInfoToBean(final CidsBean cidsBean) {
        try {
            if ((cidsBean != null) && (cidsBean.getMetaObject().getStatus() == MetaObject.MODIFIED)) {
                final Object geprueftObj = cidsBean.getProperty("geprueft");
                if ((geprueftObj == null) || ((geprueftObj instanceof Boolean) && !((Boolean)geprueftObj))) {
                    cidsBean.setProperty("bearbeitet_von", SessionManager.getSession().getUser().getName());
                    cidsBean.setProperty("bearbeitungsdatum", new Date(System.currentTimeMillis()));
                }
            }
        } catch (Exception ex) {
            LOG.error("Can not set Pruefunfsinfo for Bean!", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initFooterElements() {
        ObjectRendererUtils.decorateJLabelAndButtonSynced(
            lblForw,
            btnForward,
            ObjectRendererUtils.FORWARD_SELECTED,
            ObjectRendererUtils.FORWARD_PRESSED);
        ObjectRendererUtils.decorateJLabelAndButtonSynced(
            lblBack,
            btnBack,
            ObjectRendererUtils.BACKWARD_SELECTED,
            ObjectRendererUtils.BACKWARD_PRESSED);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selection  DOCUMENT ME!
     */
    public void setAllSelectedMetaObjects(final Collection<MetaObject> selection) {
        this.panMain.setAllSelectedMetaObjects(selection);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            bindingGroup.unbind();
            this.cidsBean = cidsBean;
            disableSecondPageIfNoPermission();
            this.panMain.setCidsBean(cidsBean);
            this.alb_picturePanel.setCidsBean(cidsBean);
            final Object laufendeNr = cidsBean.getProperty("laufende_nummer");
            final Object blattNummer = cidsBean.getProperty("blattnummer");
            lblTitle.setText("Baulastblatt " + blattNummer + ": lfd. Nummer " + laufendeNr);
            bindingGroup.bind();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void disableSecondPageIfNoPermission() {
        if (!ObjectRendererUtils.checkActionTag(ACTION_TAG)) {
            for (final MouseListener l : lblForw.getMouseListeners()) {
                lblForw.removeMouseListener(l);
            }
            lblForw.setEnabled(false);
            btnForward.setEnabled(false);
            for (final MouseListener l : lblBack.getMouseListeners()) {
                lblBack.removeMouseListener(l);
            }
            lblBack.setEnabled(false);
            btnBack.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  true if the fields/boxes are editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 32767));
        lblLetzteBearbeitung = new javax.swing.JLabel();
        lblBearbeiter = new javax.swing.JLabel();
        lblDurch = new javax.swing.JLabel();
        lblBearbeitetAm = new javax.swing.JLabel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = alb_picturePanel.getDocTypePanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        panMain = new de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastEditorPanel(editable);
        alb_picturePanel = alb_picturePanel;

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Baulast");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.add(filler1);

        lblLetzteBearbeitung.setForeground(new java.awt.Color(204, 204, 204));
        lblLetzteBearbeitung.setText("Letzte Bearbeitung am");
        jPanel2.add(lblLetzteBearbeitung);

        lblBearbeiter.setForeground(new java.awt.Color(204, 204, 204));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bearbeitungsdatum}"),
                lblBearbeiter,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("(unbekannt)");
        binding.setSourceUnreadableValue("(unbekannt)");
        binding.setConverter(Alb_baulastEditor.DATE_TO_STRING);
        bindingGroup.addBinding(binding);

        jPanel2.add(lblBearbeiter);

        lblDurch.setForeground(new java.awt.Color(204, 204, 204));
        lblDurch.setText("durch");
        jPanel2.add(lblDurch);

        lblBearbeitetAm.setForeground(new java.awt.Color(204, 204, 204));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bearbeitet_von}"),
                lblBearbeitetAm,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("(unbekannt)");
        binding.setSourceUnreadableValue("(unbekannt)");
        bindingGroup.addBinding(binding);

        jPanel2.add(lblBearbeitetAm);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.01;
        jPanel1.add(jPanel2, gridBagConstraints);

        panFooterLeft.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBack.setText("Info");
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblBackMouseClicked(evt);
                }
            });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png"))); // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(panFooterLeft, gridBagConstraints);

        panButtons.add(jPanel1);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        panFooterRight.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png"))); // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        lblForw.setText("Dokumente");
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(panFooterRight, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel4.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel6, gridBagConstraints);

        jPanel7.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel7, gridBagConstraints);

        panButtons.add(jPanel3);

        panFooter.add(panButtons, java.awt.BorderLayout.CENTER);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panMain, "card1");
        add(alb_picturePanel, "card2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, "card1");
        alb_picturePanel.getDocTypePanel().setVisible(false);
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, "card2");
        alb_picturePanel.getDocTypePanel().setVisible(true);
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
        alb_picturePanel.updateIfPicturePathsChanged();
        final String fileCollisionWarning = alb_picturePanel.getCollisionWarning();
        if (fileCollisionWarning.length() > 0) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                fileCollisionWarning,
                "Unterschiedliche Dateiformate",
                JOptionPane.WARNING_MESSAGE);
        }
        alb_picturePanel.clearCollisionWarning();
    }                                                                              //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblForwMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(0, 5, 0, 5);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
        panMain.dispose();
        alb_picturePanel.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    @Override
    public boolean prepareForSave() {
        try {
            final ArrayList<String> errors = new ArrayList<String>();

            final Object laufendeNrObj = cidsBean.getProperty("laufende_nummer");
            final Object blattNrObj = cidsBean.getProperty("blattnummer");
            final boolean unique = Alb_Constraints.checkUniqueBaulastNummer(String.valueOf(blattNrObj),
                    String.valueOf(laufendeNrObj),
                    cidsBean.getMetaObject().getID());
            if (!unique) {
                errors.add(
                    "Die Laufende Nummer "
                            + laufendeNrObj
                            + " existiert bereits unter Baulastblatt "
                            + blattNrObj
                            + "! Bitte geben Sie eine andere Nummer ein.");
            }
            if (!Alb_Constraints.checkBaulastHasBelastetesFlurstueck(cidsBean)) {
                errors.add(
                    "Der Baulast ist noch kein belastetes Flurstück zugeordnet!\n"
                            + "Bitte ordnen Sie mind. ein belastetes Flurstück zu, erst dann kann der Datensatz gespeichert werden.");
            }
            if (!Alb_Constraints.checkEintragungsdatum(cidsBean)) {
                errors.add("Die Baulast muss ein Eintragungsdatum haben.");
            }
            if (!Alb_Constraints.checkBaulastDates(cidsBean)) {
                errors.add(
                    "Sie haben unplausible Datumsangaben vorgenommen (Eingabedatum fehlt oder liegt nach dem Lösch- Schließ oder Befristungsdatum).\n"
                            + "Bitte korrigieren Sie die fehlerhaften Datumsangaben, erst dann kann der Datensatz gespeichert werden.");
            }

            final List baulastArt = (List)cidsBean.getProperty("art");

            if ((baulastArt == null) || baulastArt.isEmpty()) {
                errors.add("Die Baulast muss mindestens eine Baulastart haben");
            }

            if (errors.size() > 0) {
                String errorOutput = "";
                for (final String s : errors) {
                    errorOutput += s + "\n";
                }
                errorOutput = errorOutput.substring(0, errorOutput.length() - 1);
                JOptionPane.showMessageDialog(
                    StaticSwingTools.getParentFrame(this),
                    errorOutput,
                    "Fehler aufgetreten",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                addPruefungsInfoToBean(cidsBean);
                return true;
            }
        } catch (Exception ex) {
            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Speichern", ex, this);
            throw new RuntimeException(ex);
        }
    }
}
