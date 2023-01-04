/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.panels.AlertPanel;
import de.cismet.tools.gui.panels.LayeredAlertPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public abstract class AbstractVermessungEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractVermessungEditor.class);
    protected static final Map<Integer, Color> COLORS_GEOMETRIE_STATUS = new HashMap<Integer, Color>();
    private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "Wird geladen...");
            }
        };

    static {
        COLORS_GEOMETRIE_STATUS.put(new Integer(1), Color.green);
        COLORS_GEOMETRIE_STATUS.put(new Integer(2), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(3), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(4), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(5), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(6), Color.green);
    }

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private boolean readOnly;
    private String document;
    private AlertPanel alertPanel;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler gluGapControls;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private org.jdesktop.swingx.JXBusyLabel jxLBusyMeasure;
    private javax.swing.JLabel lblGeneralInformation;
    private javax.swing.JLabel lblHeaderControls;
    private javax.swing.JLabel lblHeaderDocument;
    private javax.swing.JLabel lblHeaderPages;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstPages;
    private de.cismet.tools.gui.panels.LayeredAlertPanel measureComponentPanel;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel pnlBusy;
    private de.cismet.tools.gui.RoundedPanel pnlControls;
    private de.cismet.tools.gui.RoundedPanel pnlDocument;
    private de.cismet.tools.gui.RoundedPanel pnlGeneralInformation;
    private javax.swing.JPanel pnlGrenzniederschriftAlert;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderControls;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocument;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderGeneralInformation;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderPages;
    private javax.swing.JPanel pnlMeasureComp;
    private javax.swing.JPanel pnlMeasureComponentWrapper;
    private de.cismet.tools.gui.RoundedPanel pnlPages;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlUmleitungHeader;
    private de.cismet.cids.custom.objecteditors.wunda_blau.RasterfariControlPanel rasterfariControlPanel1;
    private de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private javax.swing.JScrollPane scpPages;
    private javax.swing.Box.Filler strFooter;
    private javax.swing.JLabel warnMessage;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AbstractVermessungEditor.
     *
     * @param  readOnly  DOCUMENT ME!
     */
    protected AbstractVermessungEditor(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        document = null;
        initComponents();
        alertPanel = new AlertPanel(AlertPanel.TYPE.DANGER, warnMessage, true);
        initAlertPanel();

        this.rasterfariControlPanel1.initWithConnectionContext(connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected JPanel getGeneralInformationPanel() {
        return pnlGeneralInformation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract JPanel getInformationPanel();

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        strFooter = new javax.swing.Box.Filler(new java.awt.Dimension(0, 22),
                new java.awt.Dimension(0, 22),
                new java.awt.Dimension(32767, 22));
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlMeasureComponentWrapper = new javax.swing.JPanel();
        pnlBusy = new javax.swing.JPanel();
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));
        pnlMeasureComp = new javax.swing.JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
                ClientAlkisConf.getInstance().getRasterfariUrl(),
                this,
                connectionContext);
        pnlGrenzniederschriftAlert = new javax.swing.JPanel();
        warnMessage = new javax.swing.JLabel();
        panLeft = new javax.swing.JPanel();
        pnlDocument = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocument = new de.cismet.tools.gui.SemiRoundedPanel();
        pnlUmleitungHeader = new javax.swing.JPanel();
        lblHeaderDocument = new javax.swing.JLabel();
        measureComponentPanel = new LayeredAlertPanel(pnlMeasureComponentWrapper, pnlGrenzniederschriftAlert);
        pnlGeneralInformation = new de.cismet.tools.gui.RoundedPanel();
        jPanel1 = getInformationPanel();
        pnlHeaderGeneralInformation = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGeneralInformation = new javax.swing.JLabel();
        panRight = new javax.swing.JPanel();
        pnlPages = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderPages = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderPages = new javax.swing.JLabel();
        scpPages = new javax.swing.JScrollPane();
        lstPages = rasterfariDocumentLoaderPanel1.getLstPages();
        pnlControls = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderControls = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderControls = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rasterfariControlPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.RasterfariControlPanel(
                rasterfariDocumentLoaderPanel1,
                getVermessungName());
        gluGapControls = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(java.awt.Color.white);
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                AbstractVermessungEditor.class,
                "AbstractVermessungEditor.lblTitle.text"));   // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        pnlMeasureComponentWrapper.setLayout(new java.awt.CardLayout());

        pnlBusy.setBackground(new java.awt.Color(254, 254, 254));
        pnlBusy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlBusy.setLayout(new java.awt.GridBagLayout());

        jxLBusyMeasure.setPreferredSize(new java.awt.Dimension(64, 64));
        pnlBusy.add(jxLBusyMeasure, new java.awt.GridBagConstraints());

        pnlMeasureComponentWrapper.add(pnlBusy, "busyCard");

        pnlMeasureComp.setLayout(new java.awt.BorderLayout());
        pnlMeasureComp.add(rasterfariDocumentLoaderPanel1, java.awt.BorderLayout.CENTER);

        pnlMeasureComponentWrapper.add(pnlMeasureComp, "measureCard");

        pnlGrenzniederschriftAlert.setBackground(new java.awt.Color(254, 254, 254));
        pnlGrenzniederschriftAlert.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlGrenzniederschriftAlert.setLayout(new java.awt.BorderLayout());

        warnMessage.setText(org.openide.util.NbBundle.getMessage(
                AbstractVermessungEditor.class,
                "AbstractVermessungEditor.warnMessage.text")); // NOI18N

        setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);
        panLeft.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocument.setBackground(java.awt.Color.darkGray);
        pnlHeaderDocument.setLayout(new java.awt.GridBagLayout());

        pnlUmleitungHeader.setOpaque(false);
        pnlUmleitungHeader.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocument.setForeground(java.awt.Color.white);
        lblHeaderDocument.setText(getVermessungName());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlUmleitungHeader.add(lblHeaderDocument, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(pnlUmleitungHeader, gridBagConstraints);

        pnlDocument.add(pnlHeaderDocument, java.awt.BorderLayout.NORTH);

        measureComponentPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlDocument.add(measureComponentPanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLeft.add(pnlDocument, gridBagConstraints);

        pnlGeneralInformation.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGeneralInformation.add(jPanel1, gridBagConstraints);

        pnlHeaderGeneralInformation.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderGeneralInformation.setLayout(new java.awt.FlowLayout());

        lblGeneralInformation.setForeground(new java.awt.Color(255, 255, 255));
        lblGeneralInformation.setText(org.openide.util.NbBundle.getMessage(
                AbstractVermessungEditor.class,
                "AbstractVermessungEditor.lblGeneralInformation.text")); // NOI18N
        pnlHeaderGeneralInformation.add(lblGeneralInformation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlGeneralInformation.add(pnlHeaderGeneralInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 5);
        panLeft.add(pnlGeneralInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);
        panRight.setLayout(new java.awt.GridBagLayout());

        pnlHeaderPages.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderPages.setLayout(new java.awt.FlowLayout());

        lblHeaderPages.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderPages.setText(org.openide.util.NbBundle.getMessage(
                AbstractVermessungEditor.class,
                "AbstractVermessungEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, java.awt.BorderLayout.PAGE_START);

        scpPages.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new java.awt.Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new java.awt.Dimension(85, 75));

        lstPages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        panRight.add(pnlPages, gridBagConstraints);

        pnlHeaderControls.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderControls.setLayout(new java.awt.FlowLayout());

        lblHeaderControls.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderControls.setText(org.openide.util.NbBundle.getMessage(
                AbstractVermessungEditor.class,
                "AbstractVermessungEditor.lblHeaderControls.text")); // NOI18N
        pnlHeaderControls.add(lblHeaderControls);

        pnlControls.add(pnlHeaderControls, java.awt.BorderLayout.NORTH);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        rasterfariControlPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(rasterfariControlPanel1, gridBagConstraints);

        pnlControls.add(jPanel2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panRight.add(pnlControls, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panRight.add(gluGapControls, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(panRight, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void initAlertPanel() {
        warnMessage.setForeground(AlertPanel.dangerMessageColor);
        alertPanel.setContent(warnMessage);
        alertPanel.repaint();
        alertPanel.setPreferredSize(new Dimension(500, 50));
        alertPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // To change body of generated methods,
        // choose Tools | Templates.
        pnlGrenzniederschriftAlert.add(alertPanel, BorderLayout.CENTER);
        pnlGrenzniederschriftAlert.setBackground(new Color(1f, 1f, 1f, 0.8f));
        alertPanel.setVisible(false);
        alertPanel.addCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                rasterfariDocumentLoaderPanel1.reset();
                                showAlert(true);
                                pnlMeasureComponentWrapper.invalidate();
                                pnlMeasureComponentWrapper.revalidate();
                                pnlMeasureComponentWrapper.repaint();
                            }
                        });
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void showMeasureIsLoading() {
        jxLBusyMeasure.setBusy(true);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "busyCard");
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void showMeasurePanel() {
        jxLBusyMeasure.setBusy(false);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "measureCard");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  show  DOCUMENT ME!
     */
    private void showAlert(final boolean show) {
        // this means it is editable
        if (!readOnly) {
            alertPanel.setType(AlertPanel.TYPE.DANGER);
            alertPanel.setContent(warnMessage);
            alertPanel.setVisible(show);
            alertPanel.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getDocumentFilename();

    /**
     * DOCUMENT ME!
     */
    private void checkLinkInTitle() {
        checkLinkInTitle(document);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  url DOCUMENT ME!
     */
    private void checkLinkInTitle(final String document) {
        boolean isUmleitung = false;
        if (document != null) {
            final String filename = getDocumentFilename();

            if (!document.contains(filename)) {
                isUmleitung = true;
                pnlHeaderDocument.repaint();
            }
        }

        if (!readOnly && isUmleitung) {
            lblHeaderDocument.setText(String.format("Umleitung auf %s:", getVermessungName()));
        } else {
            lblHeaderDocument.setText(getVermessungName());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void successAlert() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.SUCCESS);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleNoDocumentFound() {
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        rasterfariDocumentLoaderPanel1.removeAllFeatures();
        this.invalidate();
        this.validate();
        this.repaint();
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
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            lblTitle.setText(generateTitle());
        }

        setCurrentDocumentNull();

        new RefreshDocumentWorker().execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getVermessungName();

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        rasterfariDocumentLoaderPanel1.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return pnlTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return strFooter;
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
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String generateTitle();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Integer getGemarkungOfCurrentCidsBean() {
        Integer result = Integer.valueOf(-1);

        if (cidsBean != null) {
            if (cidsBean.getProperty("gemarkung") != null) {
                final Object gemarkung = cidsBean.getProperty("gemarkung.id");
                if (gemarkung instanceof Integer) {
                    result = (Integer)gemarkung;
                }
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     */
    protected void loadDokument() {
        showMeasureIsLoading();
        checkLinkInTitle();
        showAlert(false);
        rasterfariDocumentLoaderPanel1.setDocument(document);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setCurrentDocumentNull() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
    }

    /**
     * DOCUMENT ME!
     */
    public void warnAlert() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.WARNING);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleDocumentDoesNotExists() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String findPicture();

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param    busy  DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class RefreshDocumentWorker extends SwingWorker<String, Object> {

        //~ Instance fields ----------------------------------------------------

        boolean refreshMeasuringComponent;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RefreshDocumentWorker object.
         */
        public RefreshDocumentWorker() {
            this(true);
        }

        /**
         * Creates a new RefreshDocumentWorker object.
         *
         * @param  refreshMeasuringComponent  DOCUMENT ME!
         */
        public RefreshDocumentWorker(final boolean refreshMeasuringComponent) {
            this.refreshMeasuringComponent = refreshMeasuringComponent;
            if (this.refreshMeasuringComponent) {
                lstPages.setModel(MODEL_LOAD);
//                setCurrentDocumentNull();

                showMeasureIsLoading();
            }
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Tries to find a working URL for the Bild (image) and Grenzniederschrift (boundary notes) and saves them to
         * the array documentURLs. This is done by doing a request to several possible URLs.
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected String doInBackground() throws Exception {
            return findPicture();
        }

        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    final String current = get();
//                    if (current != null) {
                    document = current;
//                    }
                }
            } catch (final InterruptedException ex) {
                LOG.warn("Was interrupted while refreshing document.", ex);
            } catch (final Exception ex) {
                LOG.warn("There was an exception while refreshing document.", ex);
            } finally {
                if (refreshMeasuringComponent) {
                    loadDokument();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class GeometrieStatusRenderer implements ListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final ListCellRenderer originalRenderer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GeometrieStatusRenderer object.
         *
         * @param  originalRenderer  DOCUMENT ME!
         */
        public GeometrieStatusRenderer(final ListCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component result = originalRenderer.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);

            if (isSelected) {
                result.setBackground(list.getSelectionBackground());
                result.setForeground(list.getSelectionForeground());
            } else {
                result.setBackground(list.getBackground());
                result.setForeground(list.getForeground());

                if (value instanceof CidsBean) {
                    final CidsBean geometrieStatus = (CidsBean)value;
                    if (geometrieStatus.getProperty("id") instanceof Integer) {
                        result.setBackground(COLORS_GEOMETRIE_STATUS.get((Integer)geometrieStatus.getProperty("id")));
                    }
                }
            }

            return result;
        }
    }
}
