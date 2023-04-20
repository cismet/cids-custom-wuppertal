/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.io.File;

import java.net.URL;
import java.net.URLEncoder;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import de.cismet.cids.client.tools.WebDavTunnelHelper;

import de.cismet.cids.custom.clientutils.CidsBeansTableModel;
import de.cismet.cids.custom.clientutils.ServerResourcesLoaderClient;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.MauernProperties;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.utils.serverresources.PropertiesServerResource;

import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.netutil.ProxyHandler;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauerDokumenteEditor extends javax.swing.JPanel implements RasterfariDocumentLoaderPanel.Listener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauerDokumenteEditor.class);

    public static final String GEOFIELD_PROPERTY = "georeferenz.geo_field";
    public static final String DOCUMENTS_TABLE = "mauer_dokument";
    public static final String DOCUMENTS_PROPERTY = "n_dokumente";
    public static final String POSITION_PROPERTY = "position";
    public static final String NAME_PROPERTY = "name";
    public static final String FILENAME_PROPERTY = "filename";
    public static final String ART_PROPERTY = "fk_art";

    private static final Pattern IMAGE_FILE_PATTERN = Pattern.compile(
            ".*\\.(bmp|png|jpg|jpeg|tif|tiff|wbmp)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PDF_FILE_PATTERN = Pattern.compile(
            ".*\\.pdf$",
            Pattern.CASE_INSENSITIVE);

    private static final String[] COLUMN_PROPERTIES = new String[] {
            POSITION_PROPERTY,
            NAME_PROPERTY,
            ART_PROPERTY
        };
    private static final String[] FLAECHE_COLUMN_NAMES = new String[] {
            "#",
            "Dateiname",
            "Art"
        };
    private static final Class[] FLAECHE_COLUMN_CLASSES = new Class[] {
            Integer.class, // laufende_nummer
            String.class,  // name
            String.class
        };

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum DocumentCard {

        //~ Enum constants -----------------------------------------------------

        BUSY, DOCUMENT, NO_DOCUMENT, ERROR, NO_PREVIEW
    }

    //~ Instance fields --------------------------------------------------------

    private boolean editable;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private final WebDavTunnelHelper webdavHelper;

    private final List<CidsBean> addedDocumentBeans = new ArrayList<>();
    private final List<CidsBean> removedDocumentBeans = new ArrayList<>();

    private CidsBean mauerBean;
    private final MauernProperties properties;

    private final FileFilter imageFileFilter = new FileFilter() {

            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || IMAGE_FILE_PATTERN.matcher(f.getName()).matches();
            }

            @Override
            public String getDescription() {
                return "Bilddateien";
            }
        };

    private final FileFilter pdfFileFilter = new FileFilter() {

            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || PDF_FILE_PATTERN.matcher(f.getName()).matches();
            }

            @Override
            public String getDescription() {
                return "Dokumente";
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnAddImg;
    javax.swing.JButton btnDown;
    private javax.swing.JButton btnOpen;
    javax.swing.JButton btnRemoveImg;
    javax.swing.JButton btnUp;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTable jXTable2;
    private org.jdesktop.swingx.JXBusyLabel jxLBusy;
    private javax.swing.JLabel lblCurrentViewTitle;
    private javax.swing.JLabel lblHeaderDocuments;
    private javax.swing.JLabel lblHeaderDocuments1;
    private de.cismet.tools.gui.RoundedPanel panRasterfari;
    private javax.swing.JPanel pnlBild;
    private de.cismet.tools.gui.RoundedPanel pnlDocuments;
    private de.cismet.tools.gui.RoundedPanel pnlDocuments1;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocuments;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocuments1;
    private de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauerEditor.
     */
    public MauerDokumenteEditor() {
        this(true);
    }
    /**
     * Creates a new MauerEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public MauerDokumenteEditor(final boolean editable) {
        this.editable = editable;
        WebDavTunnelHelper webdavHelper = null;
        MauernProperties properties = null;
        try {
            properties = (MauernProperties)ServerResourcesLoaderClient.getInstance()
                        .get((PropertiesServerResource)WundaBlauServerResources.MAUERN_PROPERTIES.getValue(), true);

            String pass = properties.getWebdavPassword();

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }

            final String user = properties.getWebdavUser();
            webdavHelper = new WebDavTunnelHelper(
                    "WUNDA_BLAU",
                    ProxyHandler.getInstance().getProxy(),
                    user,
                    pass,
                    false);
        } catch (final Exception ex) {
            final String message = "Fehler beim Initialisieren der Bilderablage.";
            LOG.error(message, ex);
            ObjectRendererUtils.showExceptionWindowToUser(message, ex, null);
        }
        this.webdavHelper = webdavHelper;
        this.properties = properties;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void deleteRemovedDocumentBeans() {
        deleteDocumentBeans(removedDocumentBeans);
    }

    /**
     * DOCUMENT ME!
     */
    public void deleteAddedDocumentBeans() {
        deleteDocumentBeans(addedDocumentBeans);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  documentBeans  DOCUMENT ME!
     */
    private void deleteDocumentBeans(final Collection<CidsBean> documentBeans) {
        for (final CidsBean deleteBean : documentBeans) {
            final String fileName = (String)deleteBean.getProperty("url_object_name");
            final String webdavUrl = properties.getWebdavUrl();
            try {
                webdavHelper.deleteFileFromWebDAV(fileName,
                    webdavUrl, getConnectionContext());
                deleteBean.delete();
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                showExceptionToUser(ex, this);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  card  DOCUMENT ME!
     */
    private void showDocumentCard(final DocumentCard card) {
        ((CardLayout)pnlBild.getLayout()).show(pnlBild, card.toString());
    }
    @Override
    public void showMeasureIsLoading() {
        showDocumentCard(DocumentCard.BUSY);
    }

    @Override
    public void showMeasurePanel() {
        showDocumentCard(DocumentCard.DOCUMENT);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        jDialog1.pack();

        jXTable2.addHighlighter(new ColorHighlighter(
                (final Component component1, final ComponentAdapter componentAdapter) -> {
                    if (!isEditable() || (componentAdapter.column != 1)) {
                        return false;
                    }
                    return removedDocumentBeans.contains(
                            getTableModel().getCidsBean(jXTable2.convertRowIndexToModel(componentAdapter.row)));
                },
                Color.WHITE,
                Color.RED));
        jXTable2.addHighlighter(new ColorHighlighter(
                (final Component component1, final ComponentAdapter componentAdapter) -> {
                    if (!isEditable() || (componentAdapter.column != 1)) {
                        return false;
                    }
                    return addedDocumentBeans.contains(
                            getTableModel().getCidsBean(jXTable2.convertRowIndexToModel(componentAdapter.row)));
                },
                Color.WHITE,
                Color.GREEN.darker()));
        jXTable2.addHighlighter(new BorderHighlighter(
                (final Component component1, final ComponentAdapter componentAdapter) -> {
                    if (componentAdapter.column != 0) {
                        return false;
                    }
                    final CidsBean documentBean = getTableModel().getCidsBean(
                            jXTable2.convertRowIndexToModel(componentAdapter.row));
                    if (documentBean == null) {
                        return false;
                    }
                    final List<CidsBean> sortedByPositionFotos = getDocumentBeans().stream().filter((b) -> {
                            return !removedDocumentBeans.contains(b);
                        }).filter((b) -> { return "foto".equals(b.getProperty("fk_art.schluessel")); }).sorted(
                            Comparator.comparing((b) -> { return (Integer)b.getProperty(POSITION_PROPERTY); },
                                Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());

                    final CidsBean lowest1 = (sortedByPositionFotos.size() > 0) ? sortedByPositionFotos.get(0) : null;
                    final CidsBean lowest2 = (sortedByPositionFotos.size() > 1) ? sortedByPositionFotos.get(1) : null;
                    return documentBean.equals(lowest1) || documentBean.equals(lowest2);
                },
                new LineBorder(Color.GRAY)));

        jXTable2.getColumnModel().getColumn(0).setMinWidth(30);
        jXTable2.getColumnModel().getColumn(0).setMaxWidth(30);
        jXTable2.getColumnModel().getColumn(0).setPreferredWidth(30);
        jXTable2.getSelectionModel().addListSelectionListener((final ListSelectionEvent e) -> {
            documentSelectionChanged();
        });

        final TableRowSorter tableRowSorter = new TableRowSorter(getTableModel());
        tableRowSorter.setComparator(2, Comparator.nullsLast(Comparator.comparing(CidsBean::toString)));
        tableRowSorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        jXTable2.setRowSorter(tableRowSorter);

        tableRowSorter.setRowFilter(new DocumentRowFilter());

        btnAddImg.setVisible(editable);
        btnRemoveImg.setVisible(editable);
        btnDown.setVisible(editable);
        btnUp.setVisible(editable);
        tableRowSorter.sort();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jFileChooser1 = new javax.swing.JFileChooser();
        jDialog1 = new javax.swing.JDialog();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new DefaultBindableReferenceCombo(new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "mauer_dokument_art",
                        getConnectionContext())));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel8 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        panRasterfari = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblCurrentViewTitle = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        pnlBild = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rasterfariDocumentLoaderPanel1 = new de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel(
                properties.getRasterfariUrl(),
                this,
                getConnectionContext());
        jPanel2 = new javax.swing.JPanel();
        jxLBusy = new JXBusyLabel(new Dimension(64, 64));
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnlDocuments = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocuments = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderDocuments = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        jPanel5 = new javax.swing.JPanel();
        btnAddImg = new javax.swing.JButton();
        btnRemoveImg = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        btnDown = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTable2 = new org.jdesktop.swingx.JXTable();
        pnlDocuments1 = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocuments1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderDocuments1 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        jPanel21 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = rasterfariDocumentLoaderPanel1.getLstPages();

        jFileChooser1.setMultiSelectionEnabled(true);

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                MauerDokumenteEditor.class,
                "MauerDokumenteEditor.jDialog1.title")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText(org.openide.util.NbBundle.getMessage(
                MauerDokumenteEditor.class,
                "MauerDokumenteEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel7.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jComboBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel7, gridBagConstraints);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setText(org.openide.util.NbBundle.getMessage(
                MauerDokumenteEditor.class,
                "MauerDokumenteEditor.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel8.add(jButton1);

        jButton2.setText(org.openide.util.NbBundle.getMessage(
                MauerDokumenteEditor.class,
                "MauerDokumenteEditor.jButton2.text")); // NOI18N

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jComboBox1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem != null}"),
                jButton2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel8.add(jButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jDialog1.getContentPane().add(jPanel8, gridBagConstraints);

        setMaximumSize(new java.awt.Dimension(1190, 1625));
        setMinimumSize(new java.awt.Dimension(807, 485));
        setOpaque(false);
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.CardLayout());

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridBagLayout());

        panRasterfari.setMinimumSize(new java.awt.Dimension(400, 200));
        panRasterfari.setPreferredSize(new java.awt.Dimension(400, 200));
        panRasterfari.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel1.setPreferredSize(new java.awt.Dimension(67, 32));
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        lblCurrentViewTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblCurrentViewTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCurrentViewTitle.setText("Vorschau"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(lblCurrentViewTitle, gridBagConstraints);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/inbox-download.png"))); // NOI18N
        btnOpen.setText(org.openide.util.NbBundle.getMessage(
                MauerDokumenteEditor.class,
                "MauerDokumenteEditor.btnOpen.text"));                                                          // NOI18N
        btnOpen.setToolTipText("Download zum Öffnen in externer Anwendung");                                    // NOI18N
        btnOpen.setBorderPainted(false);
        btnOpen.setContentAreaFilled(false);
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOpenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        semiRoundedPanel1.add(btnOpen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panRasterfari.add(semiRoundedPanel1, gridBagConstraints);

        pnlBild.setOpaque(false);
        pnlBild.setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(rasterfariDocumentLoaderPanel1, gridBagConstraints);

        pnlBild.add(jPanel1, "DOCUMENT");

        jPanel2.setLayout(new java.awt.BorderLayout());

        jxLBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jxLBusy.setPreferredSize(new java.awt.Dimension(64, 64));
        jPanel2.add(jxLBusy, java.awt.BorderLayout.CENTER);

        pnlBild.add(jPanel2, "BUSY");

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kein Dokument ausgewählt."); // NOI18N
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        pnlBild.add(jPanel3, "NO_DOCUMENT");

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Das Dokument konnte nicht geladen werden."); // NOI18N
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        pnlBild.add(jPanel4, "ERROR");

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Keine Vorschau verfügbar."); // NOI18N
        jPanel9.add(jLabel4, java.awt.BorderLayout.CENTER);

        pnlBild.add(jPanel9, "NO_PREVIEW");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panRasterfari.add(pnlBild, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panRasterfari, gridBagConstraints);

        pnlDocuments.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlDocuments.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlDocuments.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocuments.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments.setForeground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments.setPreferredSize(new java.awt.Dimension(74, 32));
        pnlHeaderDocuments.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocuments.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderDocuments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderDocuments.setText("Dokumente"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlHeaderDocuments.add(lblHeaderDocuments, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlHeaderDocuments.add(filler1, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnAddImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddImg.setToolTipText("Dokument hinzufügen");                                                       // NOI18N
        btnAddImg.setBorderPainted(false);
        btnAddImg.setContentAreaFilled(false);
        btnAddImg.setFocusPainted(false);
        btnAddImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddImgActionPerformed(evt);
                }
            });
        jPanel5.add(btnAddImg);

        btnRemoveImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveImg.setToolTipText("ausgewähltes Dokument entfernen");                                           // NOI18N
        btnRemoveImg.setBorderPainted(false);
        btnRemoveImg.setContentAreaFilled(false);
        btnRemoveImg.setEnabled(false);
        btnRemoveImg.setFocusPainted(false);
        btnRemoveImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveImgActionPerformed(evt);
                }
            });
        jPanel5.add(btnRemoveImg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlHeaderDocuments.add(jPanel5, gridBagConstraints);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnDown.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/arrow-270.png"))); // NOI18N
        btnDown.setToolTipText("laufende Nummer des ausgewählten Dokumentes inkrementieren");              // NOI18N
        btnDown.setBorderPainted(false);
        btnDown.setContentAreaFilled(false);
        btnDown.setEnabled(false);
        btnDown.setFocusPainted(false);
        btnDown.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDownActionPerformed(evt);
                }
            });
        jPanel6.add(btnDown);

        btnUp.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/arrow-090.png"))); // NOI18N
        btnUp.setToolTipText("laufende Nummer des ausgewählten Dokumentes dekrementieren");                // NOI18N
        btnUp.setBorderPainted(false);
        btnUp.setContentAreaFilled(false);
        btnUp.setEnabled(false);
        btnUp.setFocusPainted(false);
        btnUp.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnUpActionPerformed(evt);
                }
            });
        jPanel6.add(btnUp);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlHeaderDocuments.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlDocuments.add(pnlHeaderDocuments, gridBagConstraints);

        jPanel20.setOpaque(false);
        jPanel20.setLayout(new java.awt.GridBagLayout());

        jXTable2.setModel(new DokumenteTableModel());
        jXTable2.setMinimumSize(new java.awt.Dimension(250, 130));
        jXTable2.setPreferredSize(new java.awt.Dimension(250, 130));
        jScrollPane3.setViewportView(jXTable2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel20.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlDocuments.add(jPanel20, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(pnlDocuments, gridBagConstraints);

        pnlDocuments1.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlDocuments1.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlDocuments1.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocuments1.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments1.setForeground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments1.setPreferredSize(new java.awt.Dimension(74, 32));
        pnlHeaderDocuments1.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocuments1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderDocuments1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderDocuments1.setText("Seiten"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlHeaderDocuments1.add(lblHeaderDocuments1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlHeaderDocuments1.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlDocuments1.add(pnlHeaderDocuments1, gridBagConstraints);

        jPanel21.setOpaque(false);
        jPanel21.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel21.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlDocuments1.add(jPanel21, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(pnlDocuments1, gridBagConstraints);

        add(jPanel15, "card2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddImgActionPerformed
        ((DefaultBindableReferenceCombo)jComboBox1).reload();
        if (((DefaultBindableReferenceCombo)jComboBox1).getItemCount() == 0) {
            addForArt(null);
        } else if (((DefaultBindableReferenceCombo)jComboBox1).getItemCount() == 1) {
            addForArt((CidsBean)((DefaultBindableReferenceCombo)jComboBox1).getModel().getElementAt(0));
        } else {
            StaticSwingTools.showDialog(this, jDialog1, true);
        }
    }                                                                             //GEN-LAST:event_btnAddImgActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
        addedDocumentBeans.clear();
        removedDocumentBeans.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private DokumenteTableModel getTableModel() {
        return (DokumenteTableModel)jXTable2.getModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getSelectedDocumentBean() {
        return ((jXTable2.getSelectedRows().length == 1) && (jXTable2.getSelectedRow() >= 0))
            ? getTableModel().getCidsBean(
                jXTable2.convertRowIndexToModel(jXTable2.getSelectedRow())) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getSelectedDocumentBeans() {
        final List<CidsBean> selectedDocumentBeans = new ArrayList<>();
        final int[] selectionIndices = jXTable2.getSelectedRows();

        if ((selectionIndices != null) && (selectionIndices.length > 0)) {
            for (final int selectionIndex : selectionIndices) {
                final CidsBean selectedDocumentBean = getTableModel().getCidsBean(jXTable2.convertRowIndexToModel(
                            selectionIndex));
                selectedDocumentBeans.add(selectedDocumentBean);
            }
        }
        return selectedDocumentBeans;
    }
    /**
     * DOCUMENT ME!
     */
    private void documentSelectionChanged() {
        final CidsBean selectedDocumnentBean = getSelectedDocumentBean();
        if (selectedDocumnentBean != null) {
            final Integer position = (Integer)selectedDocumnentBean.getProperty(POSITION_PROPERTY);
            try {
                rasterfariDocumentLoaderPanel1.setDocument(
                    String.format(
                        "%s/%s",
                        properties.getRasterfariPath(),
                        URLEncoder.encode((String)selectedDocumnentBean.getProperty(FILENAME_PROPERTY),
                            "UTF-8")));
                /*if ("plan".equals(selectedDocumnentBean.getProperty("fk_art.schluessel"))) {
                 *  showDocumentCard(DocumentCard.NO_PREVIEW);} else {*/
                showDocumentCard(DocumentCard.DOCUMENT);
                // }
                btnOpen.setEnabled(true);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                showDocumentCard(DocumentCard.ERROR);
                btnOpen.setEnabled(false);
            }
            final List<CidsBean> unremovedDocumentBeans = new ArrayList<>(getDocumentBeans());
            unremovedDocumentBeans.removeAll(removedDocumentBeans);
            btnUp.setEnabled((position != null) && (position > 1));
            btnDown.setEnabled((position != null) && (position < unremovedDocumentBeans.size()));
        } else {
            rasterfariDocumentLoaderPanel1.setDocument(null);
            showDocumentCard(DocumentCard.NO_DOCUMENT);
            btnDown.setEnabled(false);
            btnUp.setEnabled(false);
            btnOpen.setEnabled(false);
        }

        btnRemoveImg.setEnabled(!getSelectedDocumentBeans().isEmpty());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveImgActionPerformed
        final List<CidsBean> selectedDocumentBeans = getSelectedDocumentBeans();
        if ((selectedDocumentBeans != null) && !selectedDocumentBeans.isEmpty()) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Dokumente wirklich gelöscht werden?",
                    "Dokumente entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    final List<Object> removeList = new ArrayList<>(selectedDocumentBeans);

                    reenumerate();

                    for (final Object toDeleteObj : removeList) {
                        if (toDeleteObj instanceof CidsBean) {
                            final CidsBean documentToDelete = (CidsBean)toDeleteObj;
                            documentToDelete.setProperty(POSITION_PROPERTY, null);
                            removedDocumentBeans.add(documentToDelete);
                        }
                    }
                    reenumerate();
                    getTableModel().fireTableDataChanged();
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                    showExceptionToUser(ex, this);
                } finally {
                    if (getTableModel().getRowCount() > 0) {
                        ((TableRowSorter)jXTable2.getRowSorter()).sort();
                        jXTable2.getSelectionModel().setSelectionInterval(0, 0);
                    } else {
                        showDocumentCard(DocumentCard.NO_DOCUMENT);
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemoveImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getRemovedDocumentBeans() {
        return removedDocumentBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getAddedDocumentBeans() {
        return addedDocumentBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void reenumerate() throws Exception {
        final List<CidsBean> sortedDocumentBeans = new ArrayList<>(getDocumentBeans());
        sortedDocumentBeans.removeAll(getRemovedDocumentBeans());
        Collections.sort(sortedDocumentBeans, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final Integer lfd1 = (o1 != null) ? (Integer)o1.getProperty(POSITION_PROPERTY) : null;
                    final Integer lfd2 = (o2 != null) ? (Integer)o2.getProperty(POSITION_PROPERTY) : null;
                    if ((lfd1 == null) || (lfd2 == null)) {
                        if (o1 == null) {
                            return -1;
                        } else if (o2 == null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                    return lfd1.compareTo(lfd2);
                }
            });

        int lfd = 1;
        for (final CidsBean sortedDocumentBean : sortedDocumentBeans) {
            sortedDocumentBean.setProperty(POSITION_PROPERTY, lfd++);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ex      DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    private static void showExceptionToUser(final Exception ex, final JComponent parent) {
        final ErrorInfo ei = new ErrorInfo(
                "Fehler",
                "Beim Vorgang ist ein Fehler aufgetreten",
                null,
                null,
                ex,
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(parent, ei);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        final String document = rasterfariDocumentLoaderPanel1.getCurrentDocument();
        if (document == null) {
            return;
        }

        final URL documentUrl = rasterfariDocumentLoaderPanel1.getDocumentUrl();

        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    final String filename = document.substring(document.lastIndexOf("/") + 1);
                    if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(MauerDokumenteEditor.this)) {
                        final String downloadExtension = filename.substring(filename.lastIndexOf("."));
                        final String downloadName = filename.substring(0, filename.lastIndexOf("."));
                        final Download download = new HttpDownload(
                                documentUrl,
                                "",
                                DownloadManagerDialog.getInstance().getJobName(),
                                String.format(
                                    "Stützmauer - Dokument #%d",
                                    getSelectedDocumentBean().getProperty(POSITION_PROPERTY)),
                                downloadName,
                                downloadExtension);
                        DownloadManager.instance().add(download);
                    }
                }
            });
    } //GEN-LAST:event_btnOpenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDownActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDownActionPerformed
        final List<CidsBean> unremovedDocumentBeans = new ArrayList<>(getDocumentBeans());
        unremovedDocumentBeans.removeAll(removedDocumentBeans);
        final CidsBean selectedDocumentBean = getSelectedDocumentBean();
        if (selectedDocumentBean != null) {
            final Integer lfd = (Integer)selectedDocumentBean.getProperty(POSITION_PROPERTY);
            if (lfd < (unremovedDocumentBeans.size())) {
                try {
                    for (final CidsBean otherDocumentBean : unremovedDocumentBeans) {
                        final Integer otherLfd = (Integer)otherDocumentBean.getProperty(POSITION_PROPERTY);
                        if (otherLfd == (lfd + 1)) {
                            otherDocumentBean.setProperty(POSITION_PROPERTY, lfd);
                            break;
                        }
                    }
                    selectedDocumentBean.setProperty(POSITION_PROPERTY, lfd + 1);
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
            getTableModel().fireTableDataChanged();
            final int index = unremovedDocumentBeans.indexOf(selectedDocumentBean);
            if (index >= 0) {
                final int viewIndex = jXTable2.convertRowIndexToView(index);
                jXTable2.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
            }
            ((TableRowSorter)jXTable2.getRowSorter()).sort();
        }
    }                                                                           //GEN-LAST:event_btnDownActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnUpActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnUpActionPerformed
        final List<CidsBean> otherDocumentBeans = getDocumentBeans();
        final CidsBean selectedDocumentBean = getSelectedDocumentBean();
        if (selectedDocumentBean != null) {
            final Integer lfd = (Integer)selectedDocumentBean.getProperty(POSITION_PROPERTY);
            if (lfd > 1) {
                try {
                    for (final CidsBean otherDocumentBean : otherDocumentBeans) {
                        final Integer otherLfd = (Integer)otherDocumentBean.getProperty(POSITION_PROPERTY);
                        if (otherLfd == (lfd - 1)) {
                            otherDocumentBean.setProperty(POSITION_PROPERTY, lfd);
                            break;
                        }
                    }
                    selectedDocumentBean.setProperty(POSITION_PROPERTY, lfd - 1);
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
            getTableModel().fireTableDataChanged();
            final int index = otherDocumentBeans.indexOf(selectedDocumentBean);
            if (index >= 0) {
                final int viewIndex = jXTable2.convertRowIndexToView(index);
                jXTable2.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
            }
            ((TableRowSorter)jXTable2.getRowSorter()).sort();
        }
    }                                                                         //GEN-LAST:event_btnUpActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        jDialog1.setVisible(false);
        addForArt((CidsBean)((DefaultBindableReferenceCombo)jComboBox1).getSelectedItem());
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed
    /**
     * DOCUMENT ME!
     *
     * @param  artBean  DOCUMENT ME!
     */
    private void addForArt(final CidsBean artBean) {
        final String artSchluessel = (String)artBean.getProperty("schluessel");
        jFileChooser1.setFileFilter("foto".equals(artSchluessel)
                ? imageFileFilter : ("plan".equals(artSchluessel) ? pdfFileFilter : null));
        if (JFileChooser.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
            final File[] selFiles = jFileChooser1.getSelectedFiles();
            if ((selFiles != null) && (selFiles.length > 0)) {
                CismetThreadPool.execute(new ImageUploadWorker(
                        Arrays.asList(selFiles),
                        artBean));
            }
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getMauerBean() {
        return mauerBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mauerBean  DOCUMENT ME!
     */
    public void setMauerBean(final CidsBean mauerBean) {
        this.mauerBean = mauerBean;
        getTableModel().setCidsBeans((mauerBean != null) ? getDocumentBeans() : null);
        showDocumentCard(DocumentCard.NO_DOCUMENT);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
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
    public List<CidsBean> getDocumentBeans() {
        return getMauerBean().getBeanCollectionProperty(DOCUMENTS_PROPERTY);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DocumentRowFilter extends RowFilter<TableModel, Integer> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
            final CidsBean documentBean = getTableModel().getCidsBean(entry.getIdentifier());
            return !(addedDocumentBeans.contains(documentBean) && removedDocumentBeans.contains(documentBean));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DokumenteTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VorgangFlaecheTableModel object.
         */
        public DokumenteTableModel() {
            super(COLUMN_PROPERTIES, FLAECHE_COLUMN_NAMES, FLAECHE_COLUMN_CLASSES);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageUploadWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        private final Collection<File> documentFiles;
        private final CidsBean artBean;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageUploadWorker object.
         *
         * @param  documentFiles  DOCUMENT ME!
         * @param  artBean        DOCUMENT ME!
         */
        public ImageUploadWorker(final Collection<File> documentFiles, final CidsBean artBean) {
            this.documentFiles = documentFiles;
            this.artBean = artBean;
            showDocumentCard(DocumentCard.BUSY);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            final Collection<CidsBean> newBeans = new ArrayList<>();
            final List<CidsBean> unremovedDocumentBeans = new ArrayList<>(getDocumentBeans());
            unremovedDocumentBeans.removeAll(removedDocumentBeans);
            int laufendeNummer = unremovedDocumentBeans.size() + 1;
            for (final File imageFile : documentFiles) {
                final String mauerNummer = (String)mauerBean.getProperty("mauer_nummer");
                final String filename = ((mauerNummer == null)
                        ? "____" : new DecimalFormat("#0000").format(Integer.parseInt(mauerNummer))) + "-"
                            + RandomStringUtils.randomAlphanumeric(8) + "_" + imageFile.getName();
                final String webdavUrl = properties.getWebdavUrl();
                webdavHelper.uploadFileToWebDAV(
                    filename,
                    imageFile,
                    webdavUrl,
                    MauerDokumenteEditor.this,
                    getConnectionContext());

                final CidsBean newDocumentBean = CidsBean.createNewCidsBeanFromTableName(
                        "WUNDA_BLAU",
                        DOCUMENTS_TABLE,
                        getConnectionContext());
                newDocumentBean.setProperty(POSITION_PROPERTY, laufendeNummer++);
                newDocumentBean.setProperty(NAME_PROPERTY, imageFile.getName());
                newDocumentBean.setProperty(FILENAME_PROPERTY, filename);
                newDocumentBean.setProperty(ART_PROPERTY, artBean);
                newBeans.add(newDocumentBean);
            }
            return newBeans;
        }

        @Override
        protected void done() {
            try {
                final Collection<CidsBean> newBeans = get();
                if (!newBeans.isEmpty()) {
                    final List<CidsBean> documentBeans = mauerBean.getBeanCollectionProperty(DOCUMENTS_PROPERTY);
                    documentBeans.addAll(newBeans);
                    addedDocumentBeans.addAll(newBeans);

                    getTableModel().fireTableDataChanged();

                    ((TableRowSorter)jXTable2.getRowSorter()).sort();
                    final List<CidsBean> dokumenteBeans = mauerBean.getBeanCollectionProperty(DOCUMENTS_PROPERTY);
                    final int index = dokumenteBeans.indexOf(newBeans.iterator().next());
                    final int viewIndex = jXTable2.convertRowIndexToView(index);
                    jXTable2.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
                } else {
                    showDocumentCard(DocumentCard.NO_DOCUMENT);
                }
            } catch (final Exception ex) {
                LOG.warn(ex, ex);
                showDocumentCard(DocumentCard.ERROR);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class DocumentSizeFilter extends DocumentFilter {

        //~ Instance fields ----------------------------------------------------

        int maxCharacters;
        boolean DEBUG = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DocumentSizeFilter object.
         *
         * @param  maxChars  DOCUMENT ME!
         */
        public DocumentSizeFilter(final int maxChars) {
            maxCharacters = maxChars;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
                throws BadLocationException {
            // This rejects the entire insertion if it would make
            // the contents too long. Another option would be
            // to truncate the inserted string so the contents
            // would be exactly maxCharacters in length.
            if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
                super.insertString(fb, offs, str, a);
            }
        }

        @Override
        public void replace(final FilterBypass fb,
                final int offs,
                final int length,
                final String str,
                final AttributeSet a) throws BadLocationException {
            // This rejects the entire replacement if it would make
            // the contents too long. Another option would be
            // to truncate the replacement string so the contents
            // would be exactly maxCharacters in length.
            if ((fb.getDocument().getLength() + str.length()
                            - length) <= maxCharacters) {
                super.replace(fb, offs, length, str, a);
            }
        }
    }
}
