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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import java.awt.CardLayout;
import java.awt.Component;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import de.cismet.cids.custom.butler.ButlerDownload;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservation;
import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservationRequest;
import de.cismet.cids.custom.utils.pointnumberreservation.VermessungsStellenSearchResult;
import de.cismet.cids.custom.wunda_blau.search.actions.PointNumberReserverationServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.VermessungsStellenNummerSearch;

import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.commons.gui.progress.BusyLoggingTextPane;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class PointNumberDialog extends javax.swing.JDialog implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PointNumberDialog.class);
    private static final String SEVER_ACTION = "pointNumberReservation";

    //~ Instance fields --------------------------------------------------------

    private final Comparator<VermessungsStellenSearchResult> vnrComperator;
    private Timer loadAllAnrTimer = new Timer();
    private PointNumberReservationRequest result;
    private boolean hasFreigabeAccess = false;
    private boolean hasVerlaengernAccess = false;

    private AllAntragsnummernLoadWorker allAnrLoadWorker;
    private FreigebenWorker freigebenWorker;
    private VerlaengernWorker verlaengernWorker;
    private PointNumberLoadWorker pnrLoadWorker;

    private boolean useAutoCompleteDecorator = false;
    private List<String> priorityPrefixes = new ArrayList<>();
    private List<CheckListItem> punktnummern = new ArrayList<>();

    private PointNumberTableModel releaseModel = new PointNumberTableModel(punktnummern, false);
    private PointNumberTableModel prolongModel = new PointNumberTableModel(punktnummern, true);
    private boolean hasBeenDownloadedOrIgnoredYet = true;

    private String dontReloadPnrsForThisAnr = null;
    private final List<String> antragsNummern = new ArrayList<>();

    private String protokollAnrPrefix = null;
    private String protokollAnr = null;

    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeSelectAll;
    private javax.swing.JButton btnDeSelectAll1;
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnFreigeben;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JButton btnSelectAll1;
    private javax.swing.JButton btnVerlaengern;
    private javax.swing.JComboBox cbAntragPrefix;
    private javax.swing.JComboBox cbAntragsNummer;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXBusyLabel jxFreigebenWaitLabel;
    private org.jdesktop.swingx.JXBusyLabel jxVerlaengernWaitLabel;
    private javax.swing.JLabel lblAnrSeperator;
    private javax.swing.JLabel lblAntragsnummer;
    private javax.swing.JLabel lblFreigebenError;
    private javax.swing.JLabel lblFreigebenError1;
    private javax.swing.JLabel lblProtokoll;
    private javax.swing.JLabel lblPunktNummern;
    private javax.swing.JLabel lblPunktNummern1;
    private javax.swing.JLabel lblTabErgaenzen;
    private javax.swing.JLabel lblTabFreigeben;
    private javax.swing.JLabel lblTabReservieren;
    private javax.swing.JLabel lblTabVerlaengern;
    private javax.swing.JLabel lblVnr;
    private javax.swing.JPanel pnlControls;
    private de.cismet.cids.custom.nas.PointNumberReservationPanel pnlErgaenzen;
    private javax.swing.JPanel pnlFreigabeListControls;
    private javax.swing.JPanel pnlFreigabeListControls1;
    private javax.swing.JPanel pnlFreigeben;
    private javax.swing.JPanel pnlFreigebenCard;
    private javax.swing.JPanel pnlFreigebenError;
    private javax.swing.JPanel pnlLeft;
    private de.cismet.cids.custom.nas.PointNumberReservationPanel pnlReservieren;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlTabLabels;
    private javax.swing.JPanel pnlVerlaengern;
    private javax.swing.JPanel pnlVerlaengernCard;
    private javax.swing.JPanel pnlVerlaengernError;
    private javax.swing.JPanel pnlVerlaengernWait;
    private javax.swing.JPanel pnlWait;
    private de.cismet.commons.gui.progress.BusyLoggingTextPane protokollPane;
    private javax.swing.JTable tblPunktnummernFreigeben;
    private javax.swing.JTable tblPunktnummernVerlaengern;
    private javax.swing.JTabbedPane tbpModus;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PointNumberDialog.
     *
     * @param  parent             DOCUMENT ME!
     * @param  modal              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public PointNumberDialog(final java.awt.Frame parent,
            final boolean modal,
            final ConnectionContext connectionContext) {
        super(parent, modal);

        this.connectionContext = connectionContext;

        vnrComperator = new Comparator<VermessungsStellenSearchResult>() {

                @Override
                public int compare(final VermessungsStellenSearchResult o1, final VermessungsStellenSearchResult o2) {
                    return o1.getZulassungsNummer().compareTo(o2.getZulassungsNummer());
                }
            };

        this.setTitle(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.title"));
        initComponents();
        final Properties props = new Properties();
        try {
            props.load(PointNumberReservationPanel.class.getResourceAsStream("pointNumberSettings.properties"));
            useAutoCompleteDecorator = Boolean.parseBoolean(props.getProperty("autoCompletion"));
            final String[] splittedPrioPrefixes = props.getProperty("priorityPrefixes").split(",");
            for (int i = 0; i < splittedPrioPrefixes.length; i++) {
                final String tmp = splittedPrioPrefixes[i];
                priorityPrefixes.add(tmp);
            }
        } catch (IOException e) {
            LOG.error("Could not read pointnumberSettings.properties", e);
        }

        tbpModus.setTabComponentAt(0, pnlTabLabels);
        tbpModus.setTabComponentAt(1, lblTabErgaenzen);
        tbpModus.setTabComponentAt(2, lblTabFreigeben);
        tbpModus.setTabComponentAt(3, lblTabVerlaengern);

        tbpModus.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent e) {
                    warnIfNeeded();
                    if ((tbpModus.getSelectedComponent().equals(pnlFreigeben))
                                || (tbpModus.getSelectedComponent().equals(pnlVerlaengern))) {
                        final String fullAnr = getAnrPrefix() + getAnr();
                        if (!fullAnr.equals(dontReloadPnrsForThisAnr)) {
                            loadPointNumbers();
                        }
                    } else {
                        final PointNumberReservationPanel pnl = (PointNumberReservationPanel)
                            tbpModus.getSelectedComponent();
                        pnl.checkNummerierungsbezirke();
                    }

                    cbAntragsNummer.setEditable(tbpModus.getSelectedComponent().equals(pnlReservieren));
                    cbAntragsNummer.setEnabled(
                        tbpModus.getSelectedComponent().equals(pnlReservieren)
                                || !antragsNummern.isEmpty());
                }
            });

        final DefaultCaret caret = (DefaultCaret)protokollPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        cbAntragPrefix.setRenderer(new AnrPrefixItemRenderer());
        final JTextComponent tc = (JTextComponent)cbAntragPrefix.getEditor().getEditorComponent();
        tc.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    delayedLoadAllAntragsNummern();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    delayedLoadAllAntragsNummern();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    loadAllAntragsNummern();
                }
            });
        try {
            configureFreigebenTab();
            configureVerlaengernTab();
            configurePrefixBox();
        } catch (Exception e) {
            LOG.error("Error during determination of vermessungstellennummer", e);
        }

        if (useAutoCompleteDecorator) {
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbAntragsNummer);
        }
        ((DefaultComboBoxModel)cbAntragsNummer.getModel()).addElement("");
        if ((tblPunktnummernFreigeben != null) && (tblPunktnummernFreigeben.getColumnModel() != null)) {
            final TableColumn column = tblPunktnummernFreigeben.getColumnModel().getColumn(0);
            if (column != null) {
                column.setPreferredWidth(20);
                column.setMaxWidth(20);
            }
        }
        if ((tblPunktnummernVerlaengern != null) && (tblPunktnummernVerlaengern.getColumnModel() != null)) {
            final TableColumn column = tblPunktnummernVerlaengern.getColumnModel().getColumn(0);
            if (column != null) {
                column.setPreferredWidth(20);
                column.setMaxWidth(20);
            }
        }

        tblPunktnummernFreigeben.setTableHeader(null);
        tblPunktnummernFreigeben.setShowGrid(false);
        tblPunktnummernVerlaengern.setTableHeader(null);
        tblPunktnummernVerlaengern.setShowGrid(false);

        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, 18);
        jXDatePicker1.setDate(c.getTime());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void delayedLoadAllAntragsNummern() {
        loadAllAnrTimer.cancel();
        loadAllAnrTimer = new Timer();
        loadAllAnrTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    loadAllAntragsNummern();
                }
            }, 800);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private void configureFreigebenTab() throws ConnectionException {
        // if user does not have the right to do freigaben, remove the tab
        hasFreigabeAccess = SessionManager.getConnection()
                    .hasConfigAttr(SessionManager.getSession().getUser(),
                            "custom.nas.punktNummernFreigabe",
                            getConnectionContext());
        if (!hasFreigabeAccess) {
            tbpModus.remove(pnlFreigeben);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private void configureVerlaengernTab() throws ConnectionException {
        // if user does not have the right to do freigaben, remove the tab
        hasVerlaengernAccess = SessionManager.getConnection()
                    .hasConfigAttr(SessionManager.getSession().getUser(),
                            "custom.nas.punktNummernVerlaengern",
                            getConnectionContext());
        if (!hasVerlaengernAccess) {
            tbpModus.remove(pnlVerlaengern);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private void configurePrefixBox() throws ConnectionException {
        final User user = SessionManager.getSession().getUser();
        final VermessungsnummerLoadWorker vnrLoadWorker = new VermessungsnummerLoadWorker(user.getName()) {

                @Override
                protected void done() {
                    try {
                        final Collection res = get();

                        if ((res == null) || res.isEmpty()) {
                            final VermessungsnummerLoadWorker allVnrLoadWorker = new VermessungsnummerLoadWorker("%") {

                                    @Override
                                    protected void done() {
                                        try {
                                            final ArrayList<VermessungsStellenSearchResult> tmp = new ArrayList<>();
                                            final List<VermessungsStellenSearchResult> resultAllVnr =
                                                (List<VermessungsStellenSearchResult>)get();
                                            Collections.sort(resultAllVnr, vnrComperator);
                                            for (final String s : priorityPrefixes) {
                                                final int index = Collections.binarySearch(
                                                        resultAllVnr,
                                                        new VermessungsStellenSearchResult(s, s),
                                                        vnrComperator);
                                                if (index < 0) {
                                                    if (s.equals("3290")) {
                                                        tmp.add(new VermessungsStellenSearchResult(
                                                                s,
                                                                "Stadt Wuppertal"));
                                                    } else {
                                                        tmp.add(new VermessungsStellenSearchResult(s, ""));
                                                    }
                                                } else {
                                                    tmp.add(resultAllVnr.get(index));
                                                    resultAllVnr.remove(index);
                                                }
                                            }
                                            tmp.addAll(resultAllVnr);

                                            cbAntragPrefix.setModel(
                                                new DefaultComboBoxModel(tmp.toArray()));
                                            loadAllAntragsNummern();
                                        } catch (final Exception ex) {
                                            LOG.error(ex, ex);
                                        }
                                    }
                                };
                            allVnrLoadWorker.execute();
                        } else {
                            final ArrayList<VermessungsStellenSearchResult> tmp;
                            tmp = (ArrayList<VermessungsStellenSearchResult>)res;
                            final VermessungsStellenSearchResult firstRes = tmp.get(0);
                            if ((firstRes != null) && (firstRes.getZulassungsNummer() != null)) {
                                cbAntragPrefix.setModel(new DefaultComboBoxModel(tmp.toArray()));
                                RendererTools.makeReadOnly(cbAntragPrefix);
                                cbAntragPrefix.setEditable(false);
                            }
                            loadAllAntragsNummern();
                        }
                    } catch (InterruptedException ex) {
                        LOG.error(ex, ex);
                    } catch (ExecutionException ex) {
                        LOG.error(ex, ex);
                    }
                }
            };

        vnrLoadWorker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void loadPointNumbers() {
        cancelWorker(pnrLoadWorker);
        pnrLoadWorker = new PointNumberLoadWorker();

        final CardLayout cl = (CardLayout)(pnlFreigeben.getLayout());
        cl.show(pnlFreigeben, "card1");
        jxFreigebenWaitLabel.setBusy(true);

        final CardLayout cl1 = (CardLayout)(pnlVerlaengern.getLayout());
        cl1.show(pnlVerlaengern, "card1");
        jxVerlaengernWaitLabel.setBusy(true);

        pnrLoadWorker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAnr() {
        final String anr;
        if (cbAntragsNummer.isEditable()) {
            final String tmp;
            tmp = ((JTextComponent)cbAntragsNummer.getEditor().getEditorComponent()).getText();
            if (((tmp == null) || tmp.isEmpty()) && (cbAntragsNummer.getSelectedItem() != null)) {
                anr = cbAntragsNummer.getSelectedItem().toString();
            } else {
                anr = tmp;
            }
        } else {
            anr = cbAntragsNummer.getSelectedItem().toString();
        }
        return anr;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAnrPrefix() {
        final String anrPrefix;
        if (cbAntragPrefix.isEditable()) {
            final String tmp;
            tmp = ((JTextComponent)cbAntragPrefix.getEditor().getEditorComponent()).getText();
            if ((tmp == null) || tmp.isEmpty()) {
                anrPrefix = cbAntragPrefix.getSelectedItem().toString();
            } else {
                anrPrefix = tmp;
            }
        } else {
            anrPrefix = cbAntragPrefix.getSelectedItem().toString();
        }
        return anrPrefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isErgaenzenMode() {
        return tbpModus.getSelectedComponent().equals(pnlErgaenzen);
    }

    /**
     * DOCUMENT ME!
     */
    private void showFreigabeError() {
//        tfAntragsnummer.getDocument().addDocumentListener(this);
        jxFreigebenWaitLabel.setBusy(false);
        final CardLayout cl = (CardLayout)(pnlFreigeben.getLayout());
        cl.show(pnlFreigeben, "card3");
        jxFreigebenWaitLabel.setBusy(true);
    }

    /**
     * DOCUMENT ME!
     */
    private void showVerlaengernError() {
//        tfAntragsnummer.getDocument().addDocumentListener(this);
        jxVerlaengernWaitLabel.setBusy(false);
        final CardLayout cl1 = (CardLayout)(pnlVerlaengern.getLayout());
        cl1.show(pnlVerlaengern, "card3");
        jxVerlaengernWaitLabel.setBusy(true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlTabLabels = new javax.swing.JPanel();
        lblTabReservieren = new javax.swing.JLabel();
        lblTabErgaenzen = new javax.swing.JLabel();
        lblTabFreigeben = new javax.swing.JLabel();
        lblTabVerlaengern = new javax.swing.JLabel();
        pnlLeft = new javax.swing.JPanel();
        lblAntragsnummer = new javax.swing.JLabel();
        tbpModus = new javax.swing.JTabbedPane();
        pnlReservieren = new PointNumberReservationPanel(this, getConnectionContext());
        pnlErgaenzen = new PointNumberReservationPanel(this, getConnectionContext());
        pnlFreigeben = new javax.swing.JPanel();
        pnlWait = new javax.swing.JPanel();
        jxFreigebenWaitLabel = new org.jdesktop.swingx.JXBusyLabel();
        pnlFreigebenCard = new javax.swing.JPanel();
        lblPunktNummern = new javax.swing.JLabel();
        btnFreigeben = new javax.swing.JButton();
        pnlFreigabeListControls = new javax.swing.JPanel();
        btnSelectAll = new javax.swing.JButton();
        btnDeSelectAll = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPunktnummernFreigeben = new javax.swing.JTable();
        pnlFreigebenError = new javax.swing.JPanel();
        lblFreigebenError = new javax.swing.JLabel();
        pnlVerlaengern = new javax.swing.JPanel();
        pnlVerlaengernWait = new javax.swing.JPanel();
        jxVerlaengernWaitLabel = new org.jdesktop.swingx.JXBusyLabel();
        pnlVerlaengernCard = new javax.swing.JPanel();
        lblPunktNummern1 = new javax.swing.JLabel();
        pnlFreigabeListControls1 = new javax.swing.JPanel();
        btnSelectAll1 = new javax.swing.JButton();
        btnDeSelectAll1 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPunktnummernVerlaengern = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        btnVerlaengern = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlVerlaengernError = new javax.swing.JPanel();
        lblFreigebenError1 = new javax.swing.JLabel();
        cbAntragPrefix = new WideComboBox();
        lblAnrSeperator = new javax.swing.JLabel();
        lblVnr = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        cbAntragsNummer = new javax.swing.JComboBox();
        pnlRight = new javax.swing.JPanel();
        lblProtokoll = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        protokollPane = new BusyLoggingTextPane(50, 50);
        btnDownload = new javax.swing.JButton();
        pnlControls = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        btnDone = new javax.swing.JButton();

        pnlTabLabels.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlTabLabels.setOpaque(false);

        lblTabReservieren.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/glyphicons_153_unchecked.png")));                    // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTabReservieren,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblTabReservieren.text")); // NOI18N
        pnlTabLabels.add(lblTabReservieren);

        lblTabErgaenzen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/glyphicons_154_more_windows.png")));               // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTabErgaenzen,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblTabErgaenzen.text")); // NOI18N
        pnlTabLabels.add(lblTabErgaenzen);

        lblTabFreigeben.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/glyphicons_151_new_window.png")));                 // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTabFreigeben,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblTabFreigeben.text")); // NOI18N
        pnlTabLabels.add(lblTabFreigeben);

        lblTabVerlaengern.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/glyphicons_153_unchecked_054_clock.png")));          // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTabVerlaengern,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblTabVerlaengern.text")); // NOI18N
        pnlTabLabels.add(lblTabVerlaengern);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlLeft.setMinimumSize(new java.awt.Dimension(400, 27));
        pnlLeft.setPreferredSize(new java.awt.Dimension(500, 378));
        pnlLeft.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAntragsnummer,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblAntragsnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlLeft.add(lblAntragsnummer, gridBagConstraints);

        tbpModus.addTab(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.pnlReservieren.TabConstraints.tabTitle"),
            null,
            pnlReservieren,
            org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.pnlReservieren.TabConstraints.tabToolTip")); // NOI18N
        tbpModus.addTab(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.pnlReservieren.TabConstraints.tabTitle"),
            pnlErgaenzen);                                                      // NOI18N

        pnlFreigeben.setLayout(new java.awt.CardLayout());

        pnlWait.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jxFreigebenWaitLabel,
            org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.jxFreigebenWaitLabel.text")); // NOI18N
        pnlWait.add(jxFreigebenWaitLabel, new java.awt.GridBagConstraints());

        pnlFreigeben.add(pnlWait, "card1");

        pnlFreigebenCard.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPunktNummern,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblPunktNummern.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlFreigebenCard.add(lblPunktNummern, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnFreigeben,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnFreigeben.text")); // NOI18N
        btnFreigeben.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFreigebenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlFreigebenCard.add(btnFreigeben, gridBagConstraints);

        pnlFreigabeListControls.setLayout(new java.awt.GridBagLayout());

        btnSelectAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-selectionadd.png")));                      // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnSelectAll,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnSelectAll.text")); // NOI18N
        btnSelectAll.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.btnSelectAll.toolTipText"));                                                    // NOI18N
        btnSelectAll.setPreferredSize(new java.awt.Dimension(30, 28));
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSelectAllActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlFreigabeListControls.add(btnSelectAll, gridBagConstraints);

        btnDeSelectAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-selectionremove.png")));                     // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnDeSelectAll,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnDeSelectAll.text")); // NOI18N
        btnDeSelectAll.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.btnDeSelectAll.toolTipText"));                                                    // NOI18N
        btnDeSelectAll.setPreferredSize(new java.awt.Dimension(30, 28));
        btnDeSelectAll.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDeSelectAllActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFreigabeListControls.add(btnDeSelectAll, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFreigabeListControls.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        pnlFreigebenCard.add(pnlFreigabeListControls, gridBagConstraints);

        tblPunktnummernFreigeben.setModel(releaseModel);
        tblPunktnummernFreigeben.setFillsViewportHeight(true);
        tblPunktnummernFreigeben.setShowHorizontalLines(false);
        tblPunktnummernFreigeben.setShowVerticalLines(false);
        tblPunktnummernFreigeben.setTableHeader(null);
        jScrollPane2.setViewportView(tblPunktnummernFreigeben);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlFreigebenCard.add(jScrollPane2, gridBagConstraints);

        pnlFreigeben.add(pnlFreigebenCard, "card2");

        pnlFreigebenError.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFreigebenError,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblFreigebenError.text")); // NOI18N
        pnlFreigebenError.add(lblFreigebenError, new java.awt.GridBagConstraints());

        pnlFreigeben.add(pnlFreigebenError, "card3");
        pnlFreigebenError.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        PointNumberDialog.class,
                        "PointNumberDialog.pnlFreigebenError.AccessibleContext.accessibleName")); // NOI18N

        tbpModus.addTab(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.pnlReservieren.TabConstraints.tabTitle"),
            pnlFreigeben); // NOI18N

        pnlVerlaengern.setLayout(new java.awt.CardLayout());

        pnlVerlaengernWait.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jxVerlaengernWaitLabel,
            org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.jxVerlaengernWaitLabel.text")); // NOI18N
        pnlVerlaengernWait.add(jxVerlaengernWaitLabel, new java.awt.GridBagConstraints());

        pnlVerlaengern.add(pnlVerlaengernWait, "card1");

        pnlVerlaengernCard.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPunktNummern1,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblPunktNummern1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlVerlaengernCard.add(lblPunktNummern1, gridBagConstraints);

        pnlFreigabeListControls1.setLayout(new java.awt.GridBagLayout());

        btnSelectAll1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-selectionadd.png")));                       // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnSelectAll1,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnSelectAll1.text")); // NOI18N
        btnSelectAll1.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.btnSelectAll1.toolTipText"));                                                    // NOI18N
        btnSelectAll1.setPreferredSize(new java.awt.Dimension(30, 28));
        btnSelectAll1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSelectAll1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlFreigabeListControls1.add(btnSelectAll1, gridBagConstraints);

        btnDeSelectAll1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-selectionremove.png")));                      // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnDeSelectAll1,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnDeSelectAll1.text")); // NOI18N
        btnDeSelectAll1.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.btnDeSelectAll1.toolTipText"));                                                    // NOI18N
        btnDeSelectAll1.setPreferredSize(new java.awt.Dimension(30, 28));
        btnDeSelectAll1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDeSelectAll1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFreigabeListControls1.add(btnDeSelectAll1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFreigabeListControls1.add(filler3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());
        pnlFreigabeListControls1.add(jPanel4, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        pnlVerlaengernCard.add(pnlFreigabeListControls1, gridBagConstraints);

        tblPunktnummernVerlaengern.setModel(prolongModel);
        tblPunktnummernVerlaengern.setFillsViewportHeight(true);
        tblPunktnummernVerlaengern.setShowHorizontalLines(false);
        tblPunktnummernVerlaengern.setShowVerticalLines(false);
        tblPunktnummernVerlaengern.setTableHeader(null);
        jScrollPane4.setViewportView(tblPunktnummernVerlaengern);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlVerlaengernCard.add(jScrollPane4, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jXDatePicker1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnVerlaengern,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnVerlaengern.text")); // NOI18N
        btnVerlaengern.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnVerlaengernActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(btnVerlaengern, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlVerlaengernCard.add(jPanel2, gridBagConstraints);

        pnlVerlaengern.add(pnlVerlaengernCard, "card2");

        pnlVerlaengernError.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFreigebenError1,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblFreigebenError1.text")); // NOI18N
        pnlVerlaengernError.add(lblFreigebenError1, new java.awt.GridBagConstraints());

        pnlVerlaengern.add(pnlVerlaengernError, "card3");

        tbpModus.addTab(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.pnlReservieren.TabConstraints.tabTitle"),
            pnlVerlaengern); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlLeft.add(tbpModus, gridBagConstraints);

        cbAntragPrefix.setEditable(true);
        cbAntragPrefix.setMinimumSize(new java.awt.Dimension(70, 27));
        cbAntragPrefix.setPreferredSize(new java.awt.Dimension(70, 27));
        cbAntragPrefix.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbAntragPrefixActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pnlLeft.add(cbAntragPrefix, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnrSeperator,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblAnrSeperator.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlLeft.add(lblAnrSeperator, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVnr,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblVnr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnlLeft.add(lblVnr, gridBagConstraints);

        jProgressBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jProgressBar1.setBorderPainted(false);
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(10, 5));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(150, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 35);
        pnlLeft.add(jProgressBar1, gridBagConstraints);

        cbAntragsNummer.setEditable(true);
        cbAntragsNummer.setMinimumSize(new java.awt.Dimension(209, 29));
        cbAntragsNummer.setPreferredSize(new java.awt.Dimension(209, 29));
        cbAntragsNummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbAntragsNummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlLeft.add(cbAntragsNummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlLeft, gridBagConstraints);

        pnlRight.setBackground(new java.awt.Color(254, 254, 254));
        pnlRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlRight.setMinimumSize(new java.awt.Dimension(400, 0));
        pnlRight.setPreferredSize(new java.awt.Dimension(400, 0));
        pnlRight.setLayout(new java.awt.GridBagLayout());

        lblProtokoll.setFont(new java.awt.Font("FreeMono", 1, 20));                                                // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblProtokoll,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.lblProtokoll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 10, 10);
        pnlRight.add(lblProtokoll, gridBagConstraints);

        protokollPane.setEditable(false);
        jScrollPane1.setViewportView(protokollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlRight.add(jScrollPane1, gridBagConstraints);

        btnDownload.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/nas/icon-download-alt.png")));                     // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnDownload,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnDownload.text")); // NOI18N
        btnDownload.setToolTipText(org.openide.util.NbBundle.getMessage(
                PointNumberDialog.class,
                "PointNumberDialog.btnDownload.toolTipText"));                                                    // NOI18N
        btnDownload.setBorderPainted(false);
        btnDownload.setContentAreaFilled(false);
        btnDownload.setFocusPainted(false);
        btnDownload.setPreferredSize(new java.awt.Dimension(32, 32));
        btnDownload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDownloadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlRight.add(btnDownload, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlRight, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlControls.add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnDone,
            org.openide.util.NbBundle.getMessage(PointNumberDialog.class, "PointNumberDialog.btnDone.text")); // NOI18N
        btnDone.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDoneActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        pnlControls.add(btnDone, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(pnlControls, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDoneActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDoneActionPerformed
        this.dispose();
    }                                                                           //GEN-LAST:event_btnDoneActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFreigebenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFreigebenActionPerformed
        cancelWorker(freigebenWorker);
        freigebenWorker = new FreigebenWorker();
        executeWorker(
            freigebenWorker,
            releaseModel,
            btnFreigeben,
            "Sende Freigabeauftrag.",
            "Es wurden keine Punktnummern zur Freigabe selektiert.");
    }                                                                                //GEN-LAST:event_btnFreigebenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  worker         DOCUMENT ME!
     * @param  model          DOCUMENT ME!
     * @param  button         DOCUMENT ME!
     * @param  executeString  DOCUMENT ME!
     * @param  errorString    DOCUMENT ME!
     */
    private void executeWorker(final SwingWorker worker,
            final PointNumberTableModel model,
            final JButton button,
            final String executeString,
            final String errorString) {
        try {
            protokollPane.getDocument().remove(0, protokollPane.getDocument().getLength());
        } catch (BadLocationException ex) {
            LOG.error("Could not clear Protokoll Pane", ex);
        }
        if ((model.getSelectedValues() == 0)) {
            protokollPane.addMessage(errorString, BusyLoggingTextPane.Styles.ERROR);
            return;
        }
        enableDoneButton(false);
        button.setEnabled(false);
        memorizeAnrPrefix();
        protokollPane.setBusy(true);
        protokollPane.addMessage(executeString, BusyLoggingTextPane.Styles.INFO);

        warnIfNeeded();
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void memorizeAnrPrefix() {
        protokollAnrPrefix = getAnrPrefix();
        protokollAnr = getAnr();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDownloadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDownloadActionPerformed
        if (result == null) {
            return;
        }
        final Download download;
        final String jobname;
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        CismapBroker.getInstance().getMappingComponent())) {
            jobname = (!DownloadManagerDialog.getInstance().getJobName().equals(""))
                ? DownloadManagerDialog.getInstance().getJobName() : null;
        } else {
            jobname = "";
        }
        final ArrayList<Download> downloads = new ArrayList<>();
        downloads.add(new PointNumberTxtDownload(
                result,
                "Punktnummer Download (TXT)",
                jobname,
                protokollAnrPrefix
                        + "_"
                        + protokollAnr));
        downloads.add(new PointNumberXmlDownload(
                result,
                "Punktnummer Download (XML)",
                jobname,
                protokollAnrPrefix
                        + "_"
                        + protokollAnr));
        download = new MultipleDownload(downloads,
                "Punktnummer Download");

        DownloadManager.instance().add(download);
        hasBeenDownloadedOrIgnoredYet = true;
    } //GEN-LAST:event_btnDownloadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAntragPrefixActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbAntragPrefixActionPerformed
    }                                                                                  //GEN-LAST:event_cbAntragPrefixActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDeSelectAllActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDeSelectAllActionPerformed
        for (int i = 0; i < punktnummern.size(); i++) {
            final CheckListItem item = (CheckListItem)punktnummern.get(i);
            item.setSelected(true);
            releaseModel.setValueAt(item, i, 0);
        }
        tblPunktnummernFreigeben.repaint();
    }                                                                                  //GEN-LAST:event_btnDeSelectAllActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSelectAllActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSelectAllActionPerformed
        for (int i = 0; i < punktnummern.size(); i++) {
            final CheckListItem item = (CheckListItem)punktnummern.get(i);
            // inverts the selection for the item
            item.setSelected(false);
            releaseModel.setValueAt(item, i, 0);
        }
        tblPunktnummernFreigeben.repaint();
    } //GEN-LAST:event_btnSelectAllActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAntragsNummerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbAntragsNummerActionPerformed
        if (cbAntragsNummer.isEnabled()
                    && (tbpModus.getSelectedComponent().equals(pnlFreigeben)
                        || (tbpModus.getSelectedComponent().equals(pnlVerlaengern)))) {
            loadPointNumbers();
        }
    }                                                                                   //GEN-LAST:event_cbAntragsNummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnVerlaengernActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnVerlaengernActionPerformed
        cancelWorker(verlaengernWorker);
        verlaengernWorker = new VerlaengernWorker();
        executeWorker(
            verlaengernWorker,
            prolongModel,
            btnVerlaengern,
            "Sende Verlängerungseauftrag.",
            "Es wurden keine Punktnummern zum Verlängern selektiert.");
    }                                                                                  //GEN-LAST:event_btnVerlaengernActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSelectAll1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSelectAll1ActionPerformed
        for (int i = 0; i < punktnummern.size(); i++) {
            final CheckListItem item = (CheckListItem)punktnummern.get(i);
            // inverts the selection for the item
            item.setSelected(false);
            prolongModel.setValueAt(item, i, 0);
        }
        tblPunktnummernVerlaengern.repaint();
    } //GEN-LAST:event_btnSelectAll1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDeSelectAll1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDeSelectAll1ActionPerformed
        for (int i = 0; i < punktnummern.size(); i++) {
            final CheckListItem item = (CheckListItem)punktnummern.get(i);
            item.setSelected(true);
            prolongModel.setValueAt(item, i, 0);
        }
        tblPunktnummernVerlaengern.repaint();
    }                                                                                   //GEN-LAST:event_btnDeSelectAll1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void showError() {
        enableDoneButton(true);
        btnFreigeben.setEnabled(true);
        if (protokollPane != null) {
            protokollPane.addMessage(
                "Während der Bearbeitung des Auftrags trat ein Fehler auf!",
                BusyLoggingTextPane.Styles.ERROR);
            protokollPane.setBusy(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PointNumberDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PointNumberDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PointNumberDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PointNumberDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final PointNumberDialog dialog = new PointNumberDialog(
                            new javax.swing.JFrame(),
                            true,
                            ConnectionContext.createDeprecated());
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                    dialog.setVisible(true);
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BusyLoggingTextPane getProtokollPane() {
        return protokollPane;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  result  DOCUMENT ME!
     */
    public void setResult(final PointNumberReservationRequest result) {
        this.result = result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newAnr  DOCUMENT ME!
     */
    public void addAnr(final String newAnr) {
        final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)cbAntragsNummer.getModel();
        final List<String> tmp = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            tmp.add(model.getElementAt(i));
        }
        final int pos = Collections.binarySearch(tmp, newAnr);
        model.insertElementAt(newAnr, Math.abs(pos) - 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  worker  DOCUMENT ME!
     */
    private void cancelWorker(final SwingWorker worker) {
        if ((worker != null) && (worker.getState() != SwingWorker.StateValue.DONE)) {
            worker.cancel(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void loadAllAntragsNummern() {
        cancelWorker(allAnrLoadWorker);

        allAnrLoadWorker = new AllAntragsnummernLoadWorker();

        jProgressBar1.setIndeterminate(true);
        cbAntragsNummer.setEnabled(false);

        final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)cbAntragsNummer.getModel();
        final String insertedText = (String)((JTextComponent)cbAntragsNummer.getEditor().getEditorComponent())
                    .getText();
        model.removeAllElements();
        if (tbpModus.getSelectedComponent().equals(pnlReservieren)) {
            ((JTextComponent)cbAntragsNummer.getEditor().getEditorComponent()).setText(insertedText);
        } else {
            cbAntragsNummer.setEditable(false);
            final String ladeString = "lade Antragsnummern...";
            model.addElement(ladeString);
            model.setSelectedItem(ladeString);
        }

        cbAntragsNummer.repaint();
        this.repaint();
        allAnrLoadWorker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void enableDoneButton(final boolean enable) {
        if (enable) {
            btnDone.setEnabled(true);
        } else {
            btnDone.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setSuccess() {
        hasBeenDownloadedOrIgnoredYet = false;
    }

    @Override
    public void dispose() {
        warnIfNeeded();
        super.dispose();
    }

    /**
     * DOCUMENT ME!
     */
    public void warnIfNeeded() {
        if (!hasBeenDownloadedOrIgnoredYet) {
            final PointNumberWarnDialog d = new PointNumberWarnDialog(StaticSwingTools.getFirstParentFrame(this), true);
            StaticSwingTools.showDialog(d);
            if (d.isDownloadRequested()) {
                btnDownloadActionPerformed(null);
            }
            hasBeenDownloadedOrIgnoredYet = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private List<String> executeGetAllReservationsAction() throws ConnectionException {
        final ServerActionParameter<PointNumberReserverationServerAction.Action> action = new ServerActionParameter<>(
                PointNumberReserverationServerAction.Parameter.ACTION.toString(),
                PointNumberReserverationServerAction.Action.GET_ALL_RESERVATIONS);
        final ServerActionParameter<String> prefix = new ServerActionParameter<>(
                PointNumberReserverationServerAction.Parameter.PREFIX.toString(),
                getAnrPrefix());

        return (List<String>)SessionManager.getProxy()
                    .executeTask(
                            SEVER_ACTION,
                            "WUNDA_BLAU",
                            (Object)null,
                            getConnectionContext(),
                            action,
                            prefix);
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class AnrPrefixItemRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AnrPrefixItemRenderer object.
         */
        public AnrPrefixItemRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
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
            if ((value != null) && (list != null)) {
                final VermessungsStellenSearchResult item = (VermessungsStellenSearchResult)value;
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }

                if (index != -1) {
                    setText(item.getZulassungsNummer() + " " + item.getName());
                } else {
                    setText(item.getZulassungsNummer());
                }
            }
            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PointNumberLoadWorker extends SwingWorker<Collection<PointNumberReservation>, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Collection<PointNumberReservation> doInBackground() throws Exception {
            try {
                final String anr = getAnr();
                if ((anr == null) || anr.equals("") || !anr.matches("[a-zA-Z0-9_-]*")) {
                    showFreigabeError();
                    showVerlaengernError();
                    return null;
                }

                final String anrPrefix = (getAnrPrefix() == null) ? "3290" : getAnrPrefix();
                final ServerActionParameter<String> prefix = new ServerActionParameter<>(
                        PointNumberReserverationServerAction.Parameter.PREFIX.toString(),
                        anrPrefix);
                final ServerActionParameter<String> aNummer = new ServerActionParameter<>(
                        PointNumberReserverationServerAction.Parameter.AUFTRAG_NUMMER.toString(),
                        anr);
                final ServerActionParameter<PointNumberReserverationServerAction.Action> action =
                    new ServerActionParameter<>(
                        PointNumberReserverationServerAction.Parameter.ACTION.toString(),
                        PointNumberReserverationServerAction.Action.GET_POINT_NUMBERS);
                final Collection<PointNumberReservation> pointNumbers = (Collection<PointNumberReservation>)
                    SessionManager.getProxy()
                            .executeTask(
                                    SEVER_ACTION,
                                    "WUNDA_BLAU",
                                    (Object)null,
                                    getConnectionContext(),
                                    action,
                                    prefix,
                                    aNummer);

                dontReloadPnrsForThisAnr = anrPrefix + anr;
                return pointNumbers;
            } catch (Exception ex) {
                LOG.fatal(ex, ex);
                return null;
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final Collection<PointNumberReservation> result = get();
                if ((result == null)
                            || result.isEmpty()) {
                    // ToDo show error
                    showFreigabeError();
                    showVerlaengernError();
                    return;
                }

                final List<CheckListItem> listModel = new ArrayList<>();
                final SimpleDateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd");
                final SimpleDateFormat formatterTo = new SimpleDateFormat("dd.MM.yyyy");
                for (final PointNumberReservation pnr : result) {
                    try {
                        listModel.add(new CheckListItem(
                                pnr.getPunktnummer(),
                                formatterTo.format(formatterFrom.parse(pnr.getAblaufDatum()))));
                    } catch (ParseException ex) {
                        listModel.add(new CheckListItem(pnr.getPunktnummer(), pnr.getAblaufDatum()));
                    }
                }
                Collections.sort(listModel, new Comparator<CheckListItem>() {

                        @Override
                        public int compare(final CheckListItem o1, final CheckListItem o2) {
                            return o1.getPnr().compareTo(o2.getPnr());
                        }
                    });

                punktnummern.clear();
                punktnummern.addAll(listModel);
                tblPunktnummernFreigeben.repaint();
                tblPunktnummernVerlaengern.repaint();
                jScrollPane2.invalidate();
                jScrollPane2.validate();
                jScrollPane2.repaint();
                jScrollPane4.invalidate();
                jScrollPane4.validate();
                jScrollPane4.repaint();
                jxFreigebenWaitLabel.setBusy(false);
                jxVerlaengernWaitLabel.setBusy(false);
                final CardLayout cl = (CardLayout)(pnlFreigeben.getLayout());
                cl.show(pnlFreigeben, "card2");
                final CardLayout cl1 = (CardLayout)(pnlVerlaengern.getLayout());
                cl1.show(pnlVerlaengern, "card2");
//                        tfAntragsnummer.getDocument().removeDocumentListener(PointNumberDialog.this);
            } catch (InterruptedException ex) {
                LOG.error(
                    "Swing worker that retrieves pointnumbers for antragsnummer was interrupted",
                    ex);
                showFreigabeError();
                showVerlaengernError();
            } catch (ExecutionException ex) {
                LOG.error(
                    "Error in executing worker thread that retrieves pointnumbers for antragsnummer",
                    ex);
                showFreigabeError();
                showVerlaengernError();
            } catch (final CancellationException ex) {
                LOG.info("Worker was interrupted", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class VermessungsnummerLoadWorker extends SwingWorker<Collection, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String user;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VermessungsnummerLoadWorker object.
         *
         * @param  user  DOCUMENT ME!
         */
        public VermessungsnummerLoadWorker(final String user) {
            this.user = user;
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
        protected Collection doInBackground() throws Exception {
            final CidsServerSearch search = new VermessungsStellenNummerSearch(user);
            final Collection res = SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(),
                            search,
                            getConnectionContext());
            if ((res == null) || res.isEmpty()) {
            }
            return res;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class FreigebenWorker extends SwingWorker<PointNumberReservationRequest, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected PointNumberReservationRequest doInBackground() throws Exception {
            if ((releaseModel.getSelectedValues() == 0)) {
                return null;
            }

            final List<String> selectedValues = new ArrayList<>();
            for (final CheckListItem item : punktnummern) {
                if (item.isSelected()) {
                    selectedValues.add(item.getPnr());
                }
            }
            final HashMap<Integer, ArrayList<Long>> pnrIntervals = new HashMap<>();
            ArrayList<Long> pnrs = new ArrayList<>();
            int index = 0;
            // find coherent intervals
            for (final String pnr : selectedValues) {
                final int currIndex = selectedValues.indexOf(pnr);
                if (currIndex != (selectedValues.size() - 1)) {
                    final long currPnr = Long.parseLong(pnr);
                    final long nextPnr = Long.parseLong(selectedValues.get(currIndex + 1));
                    pnrs.add(currPnr);
                    if ((currPnr + 1) != nextPnr) {
                        pnrIntervals.put(index, pnrs);
                        index++;
                        pnrs = new ArrayList<>();
                    }
                } else {
                    pnrs.add(Long.parseLong(pnr));
                    pnrIntervals.put(index, pnrs);
                }
            }

            final String anr = getAnr();
            if (!anr.matches("[a-zA-Z0-9_-]*")) {
                return null;
            }

            final String anrPrefix = (getAnrPrefix() == null) ? "3290" : getAnrPrefix();
            final ServerActionParameter prefix = new ServerActionParameter(
                    PointNumberReserverationServerAction.Parameter.PREFIX.toString(),
                    anrPrefix);
            final ServerActionParameter aNummer = new ServerActionParameter(
                    PointNumberReserverationServerAction.Parameter.AUFTRAG_NUMMER.toString(),
                    anr);
            final ServerActionParameter action = new ServerActionParameter(
                    PointNumberReserverationServerAction.Parameter.ACTION.toString(),
                    PointNumberReserverationServerAction.Action.DO_STORNO);

            // do release for each interval...
            final ArrayList<PointNumberReservation> releasedPoints = new ArrayList<>();
            final PointNumberReservationRequest res = new PointNumberReservationRequest();
            res.setSuccessful(true);
            for (final ArrayList<Long> interval : pnrIntervals.values()) {
                final Long s = interval.get(0) % 1000000;
                final Long e = interval.get(interval.size() - 1) % 1000000;
                final Integer start = s.intValue();
                final Integer end = e.intValue();

                final String nummerierungsbezirk = "" + (interval.get(0) / 1000000);
                final ServerActionParameter on1 = new ServerActionParameter(
                        PointNumberReserverationServerAction.Parameter.ON1.toString(),
                        start);
                final ServerActionParameter on2 = new ServerActionParameter(
                        PointNumberReserverationServerAction.Parameter.ON2.toString(),
                        end);
                final ServerActionParameter nbz = new ServerActionParameter(
                        PointNumberReserverationServerAction.Parameter.NBZ.toString(),
                        nummerierungsbezirk);
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
                                    on1,
                                    on2);
                if (result != null) {
                    res.setRawResult(result.getRawResult());
                }
                if ((result != null) && !result.isSuccessfull()) {
                    res.setSuccessful(false);
                    res.setProtokoll(result.getProtokoll());
                }
                if ((result != null) && (result.getPointNumbers() != null)
                            && !result.getPointNumbers().isEmpty()) {
                    if (res.getAntragsnummer() == null) {
                        res.setAntragsnummer(result.getAntragsnummer());
                    }
                    releasedPoints.addAll(result.getPointNumbers());
                }
            }
            res.setPointNumbers(releasedPoints);
            return res;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final PointNumberReservationRequest result = get();
                setResult(result);
                if ((result == null) || !result.isSuccessfull()) {
                    protokollPane.addMessage(
                        "Fehler beim Senden des Auftrags",
                        BusyLoggingTextPane.Styles.ERROR);
                    protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                    if ((result != null) && (result.getErrorMessages() != null)) {
                        for (final String s : result.getErrorMessages()) {
                            protokollPane.addMessage(s, BusyLoggingTextPane.Styles.ERROR);
                            protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                        }
                        protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                        protokollPane.addMessage(
                            "Die Protokolldatei mit Fehlerinformationen steht zum Download bereit.",
                            BusyLoggingTextPane.Styles.ERROR);
                    }
                    enableDoneButton(true);
                    btnFreigeben.setEnabled(true);
                    protokollPane.setBusy(false);
                    return;
                }
                enableDoneButton(true);
                btnFreigeben.setEnabled(true);
                protokollPane.setBusy(false);
                protokollPane.addMessage(
                    "Freigabe für Antragsnummer: "
                            + result.getAntragsnummer()
                            + " erfolgreich. Folgende Punktnummern wurden freigegeben:",
                    BusyLoggingTextPane.Styles.SUCCESS);
                protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                for (final PointNumberReservation pnr : result.getPointNumbers()) {
                    protokollPane.addMessage("" + pnr.getPunktnummer(),
                        BusyLoggingTextPane.Styles.INFO);
                }
                int selectedValues = 0;
                for (final CheckListItem item : punktnummern) {
                    if (item.isSelected()) {
                        selectedValues++;
                    }
                }
                if (selectedValues == punktnummern.size()) {
                    cbAntragsNummer.removeItemAt(cbAntragsNummer.getSelectedIndex());
                    cbAntragsNummer.setSelectedItem(null);
                }
                loadPointNumbers();
            } catch (InterruptedException ex) {
                LOG.error("Swing worker that releases points was interrupted", ex);
                showError();
            } catch (ExecutionException ex) {
                LOG.error("Error in execution of Swing Worker that releases points", ex);
                showError();
            } catch (final CancellationException ex) {
                LOG.info("Worker was interrupted", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VerlaengernWorker extends SwingWorker<Object[], Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Object[] doInBackground() throws Exception {
            if ((prolongModel.getSelectedValues() == 0)) {
                return null;
            }

            final List<PointNumberReservation> selectedValues = new ArrayList<>();
            for (final CheckListItem item : punktnummern) {
                if (item.isSelected()) {
                    final PointNumberReservation pnr = new PointNumberReservation();
                    pnr.setAblaufDatum(item.getAblaufdatum());
                    pnr.setPunktnummer(item.getPnr());
                    selectedValues.add(pnr);
                }
            }

            final String anr = getAnr();
            if (!anr.matches("[a-zA-Z0-9_-]*")) {
                return null;
            }

            final Collection<ServerActionParameter> allSaps = new ArrayList<>();

            final String anrPrefix = (getAnrPrefix() == null) ? "3290" : getAnrPrefix();
            final ServerActionParameter<String> prefixSap = new ServerActionParameter<>(
                    PointNumberReserverationServerAction.Parameter.PREFIX.toString(),
                    anrPrefix);
            final ServerActionParameter<String> aNummerSap = new ServerActionParameter<>(
                    PointNumberReserverationServerAction.Parameter.AUFTRAG_NUMMER.toString(),
                    anr);
            final ServerActionParameter<PointNumberReserverationServerAction.Action> actionSap =
                new ServerActionParameter<>(
                    PointNumberReserverationServerAction.Parameter.ACTION.toString(),
                    PointNumberReserverationServerAction.Action.DO_PROLONGATION);

            final ServerActionParameter<Date> dateSap = new ServerActionParameter<>(
                    PointNumberReserverationServerAction.Parameter.PROLONG_DATE.toString(),
                    jXDatePicker1.getDate());

            allSaps.add(prefixSap);
            allSaps.add(aNummerSap);
            allSaps.add(actionSap);
            allSaps.add(dateSap);
            for (final PointNumberReservation pnr : selectedValues) {
                final ServerActionParameter<Long> pnrSap = new ServerActionParameter<>(
                        PointNumberReserverationServerAction.Parameter.POINT_NUMBER.toString(),
                        Long.parseLong(pnr.getPunktnummer()));
                LOG.info("adding pnr SAP: " + pnrSap.getValue());
                allSaps.add(pnrSap);
            }

            final PointNumberReservationRequest res = new PointNumberReservationRequest();
            res.setSuccessful(true);
            final PointNumberReservationRequest result = (PointNumberReservationRequest)SessionManager
                        .getProxy()
                        .executeTask(
                                SEVER_ACTION,
                                "WUNDA_BLAU",
                                (Object)null,
                                getConnectionContext(),
                                allSaps.toArray(new ServerActionParameter[0]));
            if (result != null) {
                res.setRawResult(result.getRawResult());
            }
            if ((result != null) && !result.isSuccessfull()) {
                res.setSuccessful(false);
                res.setProtokoll(result.getProtokoll());
            } else {
                final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                for (final PointNumberReservation pnr : selectedValues) {
                    pnr.setAblaufDatum(formatter.format(jXDatePicker1.getDate()));
                }
                res.setPointNumbers(selectedValues);
            }
            if ((result != null) && (result.getPointNumbers() != null)
                        && !selectedValues.isEmpty()) {
                if (res.getAntragsnummer() == null) {
                    res.setAntragsnummer(result.getAntragsnummer());
                }
            }
            return new Object[] { jXDatePicker1.getDate(), res };
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final Object[] ret = get();
                final Date datum = (Date)ret[0];
                final PointNumberReservationRequest result = (PointNumberReservationRequest)ret[1];
                for (final PointNumberReservation pnr : result.getPointNumbers()) {
                    LOG.fatal(pnr);
                }
                setResult(result);
                if ((result == null) || !result.isSuccessfull()) {
                    protokollPane.addMessage(
                        "Fehler beim Senden des Auftrags",
                        BusyLoggingTextPane.Styles.ERROR);
                    protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                    if ((result != null) && (result.getErrorMessages() != null)) {
                        for (final String s : result.getErrorMessages()) {
                            protokollPane.addMessage(s, BusyLoggingTextPane.Styles.ERROR);
                            protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                        }
                        protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                        protokollPane.addMessage(
                            "Die Protokolldatei mit Fehlerinformationen steht zum Download bereit.",
                            BusyLoggingTextPane.Styles.ERROR);
                    }
                    enableDoneButton(true);
                    btnVerlaengern.setEnabled(true);
                    protokollPane.setBusy(false);
                    return;
                }
                enableDoneButton(true);
                final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                btnVerlaengern.setEnabled(true);
                protokollPane.setBusy(false);
                protokollPane.addMessage(
                    "Verlängerung für Antragsnummer: "
                            + result.getAntragsnummer()
                            + " erfolgreich. Die Reservierung folgender Punktnummern wurde auf den "
                            + formatter.format(datum)
                            + " verlängert:",
                    BusyLoggingTextPane.Styles.SUCCESS);
                setSuccess();
                hasBeenDownloadedOrIgnoredYet = false;
                protokollPane.addMessage("", BusyLoggingTextPane.Styles.INFO);
                for (final PointNumberReservation pnr : result.getPointNumbers()) {
                    protokollPane.addMessage("" + pnr.getPunktnummer(), BusyLoggingTextPane.Styles.INFO);
                }
                loadPointNumbers();
            } catch (InterruptedException ex) {
                LOG.error("Swing worker that prolongs points was interrupted", ex);
                showError();
            } catch (ExecutionException ex) {
                LOG.error("Error in execution of Swing Worker that prolongs points", ex);
                showError();
            } catch (final CancellationException ex) {
                LOG.info("Worker was interrupted", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class AllAntragsnummernLoadWorker extends SwingWorker<Collection<String>, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Collection<String> doInBackground() throws Exception {
            return executeGetAllReservationsAction();
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final List<String> result = (List<String>)get();
                antragsNummern.clear();

                if ((result != null) && !result.isEmpty()) {
                    // remove prefix and sort
                    for (final String antragsNummer : result) {
                        antragsNummern.add(antragsNummer.substring(antragsNummer.indexOf("_") + 1));
                    }
                    Collections.sort(antragsNummern);
                }

                final DefaultComboBoxModel<String> cbAntragsNummerModel = (DefaultComboBoxModel<String>)
                    cbAntragsNummer.getModel();
                cbAntragsNummerModel.removeAllElements();
                if (antragsNummern.isEmpty()) {
                    cbAntragsNummerModel.addElement("keine Aufträge gefunden");
                } else {
                    final String insertedText = (String)
                        ((JTextComponent)cbAntragsNummer.getEditor().getEditorComponent()).getText();

                    cbAntragsNummerModel.removeAllElements();
                    cbAntragsNummerModel.addElement(null);
                    for (final String antragsNummer : antragsNummern) {
                        cbAntragsNummerModel.addElement(antragsNummer);
                    }

                    cbAntragsNummer.setEditable(tbpModus.getSelectedComponent().equals(pnlReservieren));

                    if (tbpModus.getSelectedIndex() == 0) {
                        ((JTextComponent)cbAntragsNummer.getEditor().getEditorComponent()).setText(insertedText);
                    }
                    cbAntragsNummer.repaint();
                    PointNumberDialog.this.repaint();
                }

                jProgressBar1.setIndeterminate(false);
                cbAntragsNummer.setEnabled(tbpModus.getSelectedComponent().equals(pnlReservieren)
                            || !antragsNummern.isEmpty());

                if (cbAntragsNummer.getModel().getSize() > 0) {
                    cbAntragsNummer.setSelectedIndex(0);
                } else {
                    cbAntragsNummer.setSelectedIndex(-1);
                }
            } catch (final InterruptedException ex) {
                LOG.error("Worker Thread that loads all existing Antragsnummern was interrupted", ex);
            } catch (final ExecutionException ex) {
                LOG.error("Error during executing Worker Thread that loads all existing Antragsnummern", ex);
            } catch (final CancellationException ex) {
                LOG.info("Worker Thread that loads all existing Antragsnummern was interrupted", ex);
            }
        }
    }
}
