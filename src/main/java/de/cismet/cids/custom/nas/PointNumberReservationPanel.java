/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservation;
import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservationRequest;
import de.cismet.cids.custom.wunda_blau.search.actions.PointNumberReservationServerAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.StatusListener;
import de.cismet.cismap.commons.interaction.events.StatusEvent;

import de.cismet.commons.gui.progress.BusyLoggingTextPane;
import de.cismet.commons.gui.progress.BusyLoggingTextPane.Styles;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class PointNumberReservationPanel extends javax.swing.JPanel implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PointNumberReservationPanel.class);
    private static final String SEVER_ACTION = "pointNumberReservation";
    private static final String WUPP_ZONEN_KENNZIFFER = "32";
    private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    private static final ArrayList<String> NBZ_WHITELISTE = new ArrayList<>();

    //~ Instance fields --------------------------------------------------------

    boolean showErrorLbl = false;
    private final PointNumberDialog pnrDialog;
    private BusyLoggingTextPane protokollPane;
    private ArrayList<String> nbz = new ArrayList<>();
    private int maxNbz = 4;
    private boolean anzahlWarnVisible = false;

    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnErstellen;
    private javax.swing.JButton btnRefreshNbz;
    private javax.swing.JComboBox cbNbz;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner jspAnzahl;
    private javax.swing.JLabel lblAnzWarn1;
    private javax.swing.JLabel lblAnzWarn2;
    private javax.swing.JLabel lblAnzWarnAnzahl;
    private javax.swing.JLabel lblAnzahl;
    private javax.swing.JLabel lblNbz;
    private javax.swing.JLabel lblNbzAnzahl;
    private javax.swing.JLabel lblNbzError;
    private javax.swing.JLabel lblNbzINfo;
    private javax.swing.JLabel lblStartwert;
    private javax.swing.JPanel pnlNbz;
    private javax.swing.JPanel pnlNbzInfo;
    private javax.swing.JTextField tfStartWert;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PointNumberReservationPanel object.
     */
    public PointNumberReservationPanel() {
        this(null, ConnectionContext.createDeprecated());
    }

    /**
     * Creates new form PointNumberReservationPanel.
     *
     * @param  pnrDialog          DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public PointNumberReservationPanel(final PointNumberDialog pnrDialog,
            final ConnectionContext connectionContext) {
        this.pnrDialog = pnrDialog;
        this.connectionContext = connectionContext;
        final Properties props = new Properties();
        try {
            props.load(PointNumberReservationPanel.class.getResourceAsStream("pointNumberSettings.properties"));
            maxNbz = Integer.parseInt(props.getProperty("maxNbz")); // NOI18N
            final BufferedReader whitelistReader = new BufferedReader(new InputStreamReader(
                        PointNumberReservationPanel.class.getResourceAsStream("pnr_nbz_whitelist.properties")));
            String nbzWhitelistEntry;
            while ((nbzWhitelistEntry = whitelistReader.readLine()) != null) {
                NBZ_WHITELISTE.add(nbzWhitelistEntry);
            }

            Collections.sort(NBZ_WHITELISTE);
            if (!loadNummerierungsbezirke()) {
                showErrorLbl = true;
            }
        } catch (final Exception e) {
            LOG.error("Error reading pointNUmberSetting.properties", e);
            showErrorLbl = true;
        }
        initComponents();
        if (!showErrorLbl) {
            cbNbz.setModel(new javax.swing.DefaultComboBoxModel(nbz.toArray(new String[nbz.size()])));
            lblNbzAnzahl.setText("" + nbz.size());
        }
        CismapBroker.getInstance().addStatusListener(new StatusListener() {

                @Override
                public void statusValueChanged(final StatusEvent e) {
                    final Runnable modifyControls = new Runnable() {

                            @Override
                            public void run() {
                                if (e.getName().equals(StatusEvent.RETRIEVAL_STARTED)) {
                                    btnRefreshNbz.setVisible(true);
                                }
                            }
                        };
                    if (EventQueue.isDispatchThread()) {
                        modifyControls.run();
                    } else {
                        EventQueue.invokeLater(modifyControls);
                    }
                }
            });

        cbNbz.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    checkButtonState();
                }
            });

        btnRefreshNbz.setVisible(false);
        lblAnzWarn1.setVisible(false);
        lblAnzWarn2.setVisible(false);
        lblAnzWarnAnzahl.setVisible(false);
        final JSpinner.NumberEditor ne = (JSpinner.NumberEditor)jspAnzahl.getEditor();
        ne.getTextField().getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    handleAnzahlSpinnderChanged();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    handleAnzahlSpinnderChanged();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    handleAnzahlSpinnderChanged();
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void showError() {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (protokollPane != null) {
                        protokollPane.addMessage(
                            "Während der Bearbeitung des Auftrags trat ein Fehler auf!",
                            Styles.ERROR);
                        pnrDialog.enableDoneButton(true);
                        btnErstellen.setEnabled(true);
                        protokollPane.setBusy(false);
                    }
                }
            }, 50);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean loadNummerierungsbezirke() {
        nbz = new ArrayList<>();
        final MappingComponent mapC = CismapBroker.getInstance().getMappingComponent();
        Geometry g = ((XBoundingBox)mapC.getCurrentBoundingBoxFromCamera()).getGeometry();
        if (!CrsTransformer.createCrsFromSrid(g.getSRID()).equals(ClientAlkisConf.getInstance().getSrsService())) {
            g = CrsTransformer.transformToGivenCrs(g, ClientAlkisConf.getInstance().getSrsService());
        }
        final XBoundingBox bb = new XBoundingBox(g);
        final int lowerX = ((Double)Math.floor(bb.getX1())).intValue() / 1000;
        final int upperX = ((Double)Math.floor(bb.getX2())).intValue() / 1000;
        final int lowerY = ((Double)Math.floor(bb.getY1())).intValue() / 1000;
        final int upperY = ((Double)Math.floor(bb.getY2())).intValue() / 1000;
        final int diffX = (((upperX - lowerX) + 1) == 0) ? 1 : ((upperX - lowerX) + 1);
        final int diffY = (((upperY - lowerY) + 1) == 0) ? 1 : ((upperY - lowerY) + 1);

        final ArrayList<String> mapNbz = new ArrayList<>();
        for (int i = 0; i < diffX; i++) {
            final int x = lowerX + i;
            for (int j = 0; j < diffY; j++) {
                final int y = lowerY + j;
                final String currNbz = WUPP_ZONEN_KENNZIFFER + x + y;
                if (Collections.binarySearch(NBZ_WHITELISTE, currNbz) > 0) {
                    mapNbz.add(currNbz);
                }
            }
        }
        if (mapNbz.size() > maxNbz) {
            return false;
        }
        nbz.addAll(mapNbz);
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblNbz = new javax.swing.JLabel();
        lblAnzahl = new javax.swing.JLabel();
        lblStartwert = new javax.swing.JLabel();
        tfStartWert = new javax.swing.JTextField();
        btnErstellen = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        if (showErrorLbl) {
            lblNbzError = new javax.swing.JLabel();
        }
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 48),
                new java.awt.Dimension(0, 32767));
        pnlNbz = new javax.swing.JPanel();
        cbNbz = new javax.swing.JComboBox();
        btnRefreshNbz = new javax.swing.JButton();
        pnlNbzInfo = new javax.swing.JPanel();
        lblNbzAnzahl = new javax.swing.JLabel();
        lblNbzINfo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0),
                new java.awt.Dimension(40, 0),
                new java.awt.Dimension(32767, 0));
        lblAnzWarn2 = new javax.swing.JLabel();
        jspAnzahl = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        lblAnzWarn1 = new javax.swing.JLabel();
        lblAnzWarnAnzahl = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblNbz,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblNbz.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(lblNbz, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnzahl,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblAnzahl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(lblAnzahl, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStartwert,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblStartwert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(lblStartwert, gridBagConstraints);

        tfStartWert.setText(org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.tfStartWert.text")); // NOI18N
        tfStartWert.setMinimumSize(new java.awt.Dimension(100, 27));
        tfStartWert.setPreferredSize(new java.awt.Dimension(100, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(tfStartWert, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnErstellen,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.btnErstellen.text")); // NOI18N
        btnErstellen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnErstellenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(btnErstellen, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(filler2, gridBagConstraints);

        if (showErrorLbl) {
            lblNbzError.setForeground(new java.awt.Color(255, 0, 0));
            org.openide.awt.Mnemonics.setLocalizedText(
                lblNbzError,
                org.openide.util.NbBundle.getMessage(
                    PointNumberReservationPanel.class,
                    "PointNumberReservationPanel.lblNbzError.text")); // NOI18N
        }
        if (showErrorLbl) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
            add(lblNbzError, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(filler3, gridBagConstraints);

        pnlNbz.setLayout(new java.awt.GridBagLayout());

        cbNbz.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbNbzActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        pnlNbz.add(cbNbz, gridBagConstraints);

        btnRefreshNbz.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-refresh.png"))); // NOI18N
        btnRefreshNbz.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.btnRefreshNbz.toolTipText"));               // NOI18N
        btnRefreshNbz.setLabel(org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.btnRefreshNbz.label"));                     // NOI18N
        btnRefreshNbz.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRefreshNbzActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 10);
        pnlNbz.add(btnRefreshNbz, gridBagConstraints);

        pnlNbzInfo.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblNbzAnzahl,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblNbzAnzahl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlNbzInfo.add(lblNbzAnzahl, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblNbzINfo,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblNbzINfo.text")); // NOI18N
        pnlNbzInfo.add(lblNbzINfo, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlNbz.add(pnlNbzInfo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlNbz.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pnlNbz, gridBagConstraints);

        lblAnzWarn2.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnzWarn2,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblAnzWarn2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(lblAnzWarn2, gridBagConstraints);

        jspAnzahl.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        jspAnzahl.setMinimumSize(new java.awt.Dimension(100, 28));
        jspAnzahl.setPreferredSize(new java.awt.Dimension(100, 28));
        jspAnzahl.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jspAnzahlStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jspAnzahl, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblAnzWarn1.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnzWarn1,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblAnzWarn1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(lblAnzWarn1, gridBagConstraints);

        lblAnzWarnAnzahl.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnzWarnAnzahl,
            org.openide.util.NbBundle.getMessage(
                PointNumberReservationPanel.class,
                "PointNumberReservationPanel.lblAnzWarnAnzahl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(lblAnzWarnAnzahl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnErstellenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnErstellenActionPerformed
        pnrDialog.warnIfNeeded();

// check anr
        final String anr = pnrDialog.getAnr();
        if ((anr == null) || anr.isEmpty()) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                org.openide.util.NbBundle.getMessage(
                    PointNumberReservationPanel.class,
                    "PointNumberReservationPanel.AnrExistsJOptionPane.message"),
                org.openide.util.NbBundle.getMessage(
                    PointNumberReservationPanel.class,
                    "PointNumberReservationPanel.AnrExistsJOptionPane.title"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        protokollPane = pnrDialog.getProtokollPane();
        try {
            protokollPane.getDocument().remove(0, protokollPane.getDocument().getLength());
        } catch (BadLocationException ex) {
            LOG.error("Could not clear Protokoll Pane", ex);
        }

        final String nummerierungsbezirk = (String)cbNbz.getSelectedItem();
        if (!anr.matches("[a-zA-Z0-9_-]*") || !nummerierungsbezirk.matches("[0-9]*")) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                org.openide.util.NbBundle.getMessage(
                    PointNumberReservationPanel.class,
                    "PointNumberReservationPanel.ValueCheckJOptionPane.message"),
                org.openide.util.NbBundle.getMessage(
                    PointNumberReservationPanel.class,
                    "PointNumberReservationPanel.ValueCheckJOptionPane.title"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        // disable the done Button of the Dialog
        pnrDialog.enableDoneButton(false);
        pnrDialog.memorizeAnrPrefix();
        btnErstellen.setEnabled(false);
        protokollPane.setBusy(true);
        final String anrPrefix = pnrDialog.getAnrPrefix();

        protokollPane.addMessage("Prüfe ob Antragsnummer " + anrPrefix + "_" + anr + " schon existiert.", Styles.INFO);

        final SwingWorker<PointNumberReservationRequest, Void> reservationWorker =
            new SwingWorker<PointNumberReservationRequest, Void>() {

                @Override
                protected PointNumberReservationRequest doInBackground() throws Exception {
                    final Integer anzahl = (Integer)jspAnzahl.getValue();
                    final Integer startwert;
                    final String swText = tfStartWert.getText();
                    if ((swText != null) && !swText.equals("") && swText.matches("[0-9]*")) {
                        startwert = Integer.parseInt(swText);
                    } else {
                        startwert = 0;
                    }

                    final ServerActionParameter<PointNumberReservationServerAction.Action> action;
                    if (pnrDialog.isErgaenzenMode()) {
                        action = new ServerActionParameter<>(
                                PointNumberReservationServerAction.Parameter.ACTION.toString(),
                                PointNumberReservationServerAction.Action.DO_ADDITION);
                    } else {
                        action = new ServerActionParameter<>(
                                PointNumberReservationServerAction.Parameter.ACTION.toString(),
                                PointNumberReservationServerAction.Action.DO_RESERVATION);
                    }
                    final ServerActionParameter<String> prefix = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.PREFIX.toString(),
                            anrPrefix);
                    final ServerActionParameter<String> aNummer = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.AUFTRAG_NUMMER.toString(),
                            anr);
                    final ServerActionParameter<String> nbz = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.NBZ.toString(),
                            nummerierungsbezirk);
                    final ServerActionParameter<Integer> amount = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.ANZAHL.toString(),
                            anzahl);
                    final ServerActionParameter startVal = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.STARTWERT.toString(),
                            startwert);

                    final PointNumberReservationRequest result = (PointNumberReservationRequest)SessionManager
                                .getProxy()
                                .executeTask(
                                        SEVER_ACTION,
                                        "WUNDA_BLAU",
                                        (Object)null,
                                        getConnectionContext(),
                                        action,
                                        prefix,
                                        aNummer,
                                        nbz,
                                        amount,
                                        startVal);

                    return result;
                }

                @Override
                protected void done() {
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                try {
                                    final PointNumberReservationRequest result = get();
                                    pnrDialog.setResult(result);
                                    if ((result == null) || !result.isSuccessfull()) {
                                        protokollPane.addMessage("Fehler beim Senden des Auftrags.", Styles.ERROR);
                                        protokollPane.addMessage("", Styles.INFO);
                                        if ((result != null) && (result.getErrorMessages() != null)) {
                                            for (final String s : result.getErrorMessages()) {
                                                protokollPane.addMessage(
                                                    s,
                                                    BusyLoggingTextPane.Styles.ERROR);
                                                protokollPane.addMessage("", Styles.INFO);
                                            }
                                            protokollPane.addMessage("", Styles.INFO);
                                            protokollPane.addMessage(
                                                "Die Protokolldatei mit Fehlerinformationen steht zum Download bereit.",
                                                Styles.ERROR);
                                        }
                                        pnrDialog.enableDoneButton(true);
                                        btnErstellen.setEnabled(true);
                                        protokollPane.setBusy(false);
                                        return;
                                    }

                                    protokollPane.addMessage("Ok.", Styles.SUCCESS);
                                    pnrDialog.setSuccess();
                                    protokollPane.setBusy(false);
                                    protokollPane.addMessage(
                                        "Reservierung für Antragsnummer: "
                                                + result.getAntragsnummer()
                                                + ". Folgende Punktnummern wurden reserviert: (gültig bis)",
                                        Styles.SUCCESS);
                                    protokollPane.addMessage("", Styles.INFO);
                                    for (final PointNumberReservation pnr : result.getPointNumbers()) {
                                        protokollPane.addMessage(
                                            pnr.getPunktnummer()
                                                    + " ("
                                                    + DATE_FORMATTER.format(DATE_PARSER.parse(pnr.getAblaufDatum()))
                                                    + ")",
                                            Styles.INFO);
                                    }
                                    if (!pnrDialog.isErgaenzenMode()) {
                                        final String anr = result.getAntragsnummer();
                                        final int underscorePos = anr.indexOf("_");
                                        pnrDialog.addAnr(anr.substring(underscorePos + 1));
                                    }
                                    pnrDialog.enableDoneButton(true);
                                    btnErstellen.setEnabled(true);
                                } catch (InterruptedException ex) {
                                    LOG.error("Swing worker that executes the reservation was interrupted", ex);
                                    showError();
                                } catch (ExecutionException ex) {
                                    LOG.error("Error in execution of Swing Worker that executes the reservation", ex);
                                    showError();
                                } catch (ParseException ex) {
                                    LOG.error("Error parsing the ablauf date of a reservation", ex);
                                    showError();
                                }
                            }
                        },
                        50);
                }
            };

        final SwingWorker<Boolean, Void> isAntragExistingWorker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    final ServerActionParameter<PointNumberReservationServerAction.Action> action =
                        new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.ACTION.toString(),
                            PointNumberReservationServerAction.Action.IS_ANTRAG_EXISTING);
                    final ServerActionParameter<String> prefix = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.PREFIX.toString(),
                            anrPrefix);
                    final ServerActionParameter<String> aNummer = new ServerActionParameter<>(
                            PointNumberReservationServerAction.Parameter.AUFTRAG_NUMMER.toString(),
                            anr);
                    final boolean isAntragExisting = (Boolean)SessionManager.getProxy()
                                .executeTask(
                                        SEVER_ACTION,
                                        "WUNDA_BLAU",
                                        (Object)null,
                                        getConnectionContext(),
                                        action,
                                        prefix,
                                        aNummer);

                    return isAntragExisting;
                }

                @Override
                protected void done() {
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                final boolean startReservationWorker = false;
                                try {
                                    final Boolean anrExists = get();
                                    if ((anrExists && pnrDialog.isErgaenzenMode())
                                                || (!anrExists && !pnrDialog.isErgaenzenMode())) {
                                        protokollPane.addMessage("Ok.", Styles.SUCCESS);
                                        pnrDialog.setSuccess();
                                        protokollPane.addMessage("Sende Reservierungsauftrag.", Styles.INFO);
//                                startReservationWorker = true;
                                        reservationWorker.run();
                                    } else {
                                        if (pnrDialog.isErgaenzenMode()) {
                                            protokollPane.addMessage(
                                                "Auftragsnummer existiert noch nicht!",
                                                Styles.ERROR);
                                        } else {
                                            protokollPane.addMessage("Auftragsnummer existiert bereits", Styles.ERROR);
                                        }
                                        pnrDialog.enableDoneButton(true);
                                        btnErstellen.setEnabled(true);
                                        protokollPane.setBusy(false);
                                    }
                                } catch (InterruptedException ex) {
                                    LOG.error(
                                        "Swing worker that checks if antragsnummer is existing was interrupted",
                                        ex);
                                    showError();
                                } catch (ExecutionException ex) {
                                    LOG.error(
                                        "Error in execution of Swing Worker that checks if antragsnummer is existing",
                                        ex);
                                    showError();
                                }
//                                pnrDialog.invalidate();
//                                pnrDialog.revalidate();
//                                pnrDialog.repaint();
                                if (startReservationWorker) {
                                    reservationWorker.run();
                                }
                            }
                        }, 50);
                }
            };

        isAntragExistingWorker.execute();
    } //GEN-LAST:event_btnErstellenActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void checkButtonState() {
        if ((cbNbz.getSelectedItem() == null) || ((String)cbNbz.getSelectedItem()).isEmpty()) {
            btnErstellen.setEnabled(false);
            return;
        }
        btnErstellen.setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     */
    public void checkNummerierungsbezirke() {
        if (loadNummerierungsbezirke()) {
            if (showErrorLbl) {
                this.remove(lblNbzError);
                lblNbzError = null;
                showErrorLbl = false;
            }
        } else {
            showErrorLbl = true;
            if (lblNbzError == null) {
                lblNbzError = new javax.swing.JLabel();
                lblNbzError.setForeground(new java.awt.Color(255, 0, 0));
                org.openide.awt.Mnemonics.setLocalizedText(
                    lblNbzError,
                    org.openide.util.NbBundle.getMessage(
                        PointNumberReservationPanel.class,
                        "PointNumberReservationPanel.lblNbzError.text")); // NOI18N
                final java.awt.GridBagConstraints gridBagConstraints;
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);

                add(lblNbzError, gridBagConstraints);
            }
        }
        cbNbz.setModel(new javax.swing.DefaultComboBoxModel(nbz.toArray(new String[nbz.size()])));
        lblNbzAnzahl.setText("" + nbz.size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRefreshNbzActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRefreshNbzActionPerformed
        checkNummerierungsbezirke();
        btnRefreshNbz.setVisible(false);
        this.invalidate();
        this.validate();
        this.repaint();
    }                                                                                 //GEN-LAST:event_btnRefreshNbzActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jspAnzahlStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jspAnzahlStateChanged
//        handleAnzahlSpinnderChanged();
    } //GEN-LAST:event_jspAnzahlStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbNbzActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbNbzActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbNbzActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void handleAnzahlSpinnderChanged() {
        // We do not allow reservations with more than 100 points
        final JSpinner.NumberEditor ne = (JSpinner.NumberEditor)jspAnzahl.getEditor();
        Number anzahl;
        try {
            anzahl = ne.getFormat().parse(ne.getTextField().getText());
        } catch (ParseException ex) {
            anzahl = (Number)jspAnzahl.getValue();
        }
        if (anzahl.intValue() > 100) {
            lblAnzWarnAnzahl.setText("" + anzahl.intValue());
            if (!anzahlWarnVisible) {
                anzahlWarnVisible = true;
                lblAnzWarn1.setVisible(true);
                lblAnzWarn2.setVisible(true);
                lblAnzWarnAnzahl.setVisible(true);
            }
        } else {
            if ((anzahlWarnVisible)) {
                anzahlWarnVisible = false;
                lblAnzWarn1.setVisible(false);
                lblAnzWarn2.setVisible(false);
                lblAnzWarnAnzahl.setVisible(false);
            }
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
