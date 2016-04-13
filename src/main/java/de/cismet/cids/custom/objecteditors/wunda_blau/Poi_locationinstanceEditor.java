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
/*
 * Poi_locationinstanceEditor.java
 *
 * Created on 17.08.2009, 15:40:29
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Cursor;
import java.awt.EventQueue;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.wunda_blau.SignaturListCellRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Poi_locationinstanceEditor extends DefaultCustomObjectEditor implements Titled {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String title = "";

    private ImageIcon statusRed = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private ImageIcon statusGreen = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    private ImageIcon statusGrey = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-offline.png"));
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddThema;
    private javax.swing.JButton btnAddZusNamen;
    private javax.swing.JButton btnCreateAreaFromPoint;
    private javax.swing.JButton btnMenAbort;
    private javax.swing.JButton btnMenOk;
    private javax.swing.JButton btnNamesMenAbort;
    private javax.swing.JButton btnNamesMenOk;
    private javax.swing.JButton btnRemoveThema;
    private javax.swing.JButton btnRemoveZusNamen;
    private javax.swing.JComboBox cbGeomArea;
    private javax.swing.JComboBox cbGeomPoint;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbInfoArt;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbMainLocationType;
    private javax.swing.JComboBox cbSignatur;
    private javax.swing.JComboBox cbTypes;
    private javax.swing.JCheckBox chkVeroeffentlicht;
    private javax.swing.JDialog dlgAddLocationType;
    private javax.swing.JDialog dlgAddZusNamen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblArt;
    private javax.swing.JLabel lblAuswaehlen;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblGeomPoint;
    private javax.swing.JLabel lblGeomPoint1;
    private javax.swing.JLabel lblHeader1;
    private javax.swing.JLabel lblHeader2;
    private javax.swing.JLabel lblHeader3;
    private javax.swing.JLabel lblImageUrl;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblLocationTypes;
    private javax.swing.JLabel lblLocationTypes1;
    private javax.swing.JLabel lblMainLocationType;
    private javax.swing.JLabel lblNamesAuswaehlen;
    private javax.swing.JLabel lblPLZ;
    private javax.swing.JLabel lblSignatur;
    private javax.swing.JLabel lblStadt;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblTelefon;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblUrlCheckImage;
    private javax.swing.JLabel lblUrlCheckWebsite;
    private javax.swing.JLabel lblVeroeffentlicht;
    private javax.swing.JLabel lblWebsite;
    private javax.swing.JList lstLocationTypes;
    private javax.swing.JList lstZusNamen;
    private javax.swing.JPanel panAddLocationType;
    private javax.swing.JPanel panAddName;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panButtons1;
    private javax.swing.JPanel panCenter;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panContent2;
    private javax.swing.JPanel panMenButtons;
    private javax.swing.JPanel panMenNamesButtons;
    private javax.swing.JPanel panSpacing1;
    private javax.swing.JPanel panSpacing2;
    private javax.swing.JScrollPane scpLstLocationTypes;
    private javax.swing.JScrollPane scpTxtInfo;
    private javax.swing.JScrollPane scpZusNamen;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtPLZ;
    private javax.swing.JTextField txtStadt;
    private javax.swing.JTextField txtStrasse;
    private javax.swing.JTextField txtTelefon;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtZusNamen;
    private javax.swing.JTextArea txtaImageUrl;
    private javax.swing.JTextArea txtaInfo;
    private javax.swing.JTextArea txtaWebsiteUrl;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Poi_locationinstanceEditor.
     */
    public Poi_locationinstanceEditor() {
        initComponents();
        dlgAddLocationType.pack();
        dlgAddZusNamen.pack();
        dlgAddLocationType.getRootPane().setDefaultButton(btnMenOk);
        dlgAddZusNamen.getRootPane().setDefaultButton(btnNamesMenOk);

        ((DefaultCismapGeometryComboBoxEditor)cbGeomPoint).setLocalRenderFeatureString("pos");
        ((DefaultCismapGeometryComboBoxEditor)cbGeomArea).setLocalRenderFeatureString("geom_area");

        txtaImageUrl.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaImageUrl.getText(), lblUrlCheckImage);
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaImageUrl.getText(), lblUrlCheckImage);
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaImageUrl.getText(), lblUrlCheckImage);
                }
            });

        txtaWebsiteUrl.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaWebsiteUrl.getText(), lblUrlCheckWebsite);
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaWebsiteUrl.getText(), lblUrlCheckWebsite);
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkUrlAndIndicate(txtaWebsiteUrl.getText(), lblUrlCheckWebsite);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  indicator  DOCUMENT ME!
     */
    private void checkUrlAndIndicate(final String url, final JLabel indicator) {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                indicator.setIcon(statusGrey);
                                indicator.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        });
                    return checkURL(new URL(url));
                }

                @Override
                protected void done() {
                    final Boolean check;
                    try {
                        check = get();
                        if (check) {
                            indicator.setIcon(statusGreen);
                            indicator.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            indicator.setIcon(statusRed);
                            indicator.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (Exception e) {
                        indicator.setIcon(statusRed);
                        indicator.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkURL(final URL url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        super.dispose();
        dlgAddLocationType.dispose();
        dlgAddZusNamen.dispose();
        ((DefaultCismapGeometryComboBoxEditor)cbGeomPoint).dispose();
        ((DefaultCismapGeometryComboBoxEditor)cbGeomArea).dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propertyName           DOCUMENT ME!
     * @param  value                  DOCUMENT ME!
     * @param  andDeleteObjectFromDB  DOCUMENT ME!
     */
    private void deleteItemFromList(final String propertyName,
            final Object value,
            final boolean andDeleteObjectFromDB) {
        if ((value instanceof CidsBean) && (propertyName != null)) {
            final CidsBean bean = (CidsBean)value;
            if (andDeleteObjectFromDB) {
                try {
                    bean.delete();
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            } else {
                final Object coll = cidsBean.getProperty(propertyName);
                if (coll instanceof Collection) {
                    ((Collection)coll).remove(bean);
                }
            }
        }
    }

    @Override
    public synchronized void setCidsBean(final CidsBean cidsBean) {
        super.setCidsBean(cidsBean);
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

        dlgAddLocationType = new javax.swing.JDialog();
        panAddLocationType = new javax.swing.JPanel();
        lblAuswaehlen = new javax.swing.JLabel();
        final MetaObject[] locationtypes = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils
                    .getLightweightMetaObjectsForTable("poi_locationtype", new String[] { "identification" });
        if (locationtypes != null) {
            Arrays.sort(locationtypes);
            cbTypes = new javax.swing.JComboBox(locationtypes);
            panMenButtons = new javax.swing.JPanel();
            btnMenAbort = new javax.swing.JButton();
            btnMenOk = new javax.swing.JButton();
            dlgAddZusNamen = new javax.swing.JDialog();
            panAddName = new javax.swing.JPanel();
            lblNamesAuswaehlen = new javax.swing.JLabel();
            panMenNamesButtons = new javax.swing.JPanel();
            btnNamesMenAbort = new javax.swing.JButton();
            btnNamesMenOk = new javax.swing.JButton();
            txtZusNamen = new javax.swing.JTextField();
            panCenter = new javax.swing.JPanel();
            panContent = new RoundedPanel();
            lblFax = new javax.swing.JLabel();
            lblEmail = new javax.swing.JLabel();
            lblUrl = new javax.swing.JLabel();
            lblStrasse = new javax.swing.JLabel();
            lblTelefon = new javax.swing.JLabel();
            lblPLZ = new javax.swing.JLabel();
            lblInfo = new javax.swing.JLabel();
            lblStadt = new javax.swing.JLabel();
            lblArt = new javax.swing.JLabel();
            txtFax = new javax.swing.JTextField();
            txtStrasse = new javax.swing.JTextField();
            txtStadt = new javax.swing.JTextField();
            txtPLZ = new javax.swing.JTextField();
            txtTelefon = new javax.swing.JTextField();
            txtEmail = new javax.swing.JTextField();
            scpTxtInfo = new javax.swing.JScrollPane();
            txtaInfo = new javax.swing.JTextArea();
            txtUrl = new javax.swing.JTextField();
            lblBezeichnung = new javax.swing.JLabel();
            txtBezeichnung = new javax.swing.JTextField();
            panSpacing1 = new javax.swing.JPanel();
            lblHeader1 = new javax.swing.JLabel();
            lblGeomPoint = new javax.swing.JLabel();
            cbGeomPoint = new DefaultCismapGeometryComboBoxEditor();
            cbGeomArea = new DefaultCismapGeometryComboBoxEditor();
            lblGeomPoint1 = new javax.swing.JLabel();
            cbInfoArt = new DefaultBindableReferenceCombo(true);
            btnCreateAreaFromPoint = new javax.swing.JButton();
            panContent2 = new RoundedPanel();
            lblMainLocationType = new javax.swing.JLabel();
            lblLocationTypes = new javax.swing.JLabel();
            scpLstLocationTypes = new javax.swing.JScrollPane();
            lstLocationTypes = new javax.swing.JList();
            panButtons = new javax.swing.JPanel();
            btnAddThema = new javax.swing.JButton();
            btnRemoveThema = new javax.swing.JButton();
            panButtons1 = new javax.swing.JPanel();
            btnAddZusNamen = new javax.swing.JButton();
            btnRemoveZusNamen = new javax.swing.JButton();
            scpZusNamen = new javax.swing.JScrollPane();
            lstZusNamen = new javax.swing.JList();
            lblLocationTypes1 = new javax.swing.JLabel();
            lblSignatur = new javax.swing.JLabel();
            cbSignatur = new FastBindableReferenceCombo("%1$2s", new String[] { "definition", "filename" });
            panSpacing2 = new javax.swing.JPanel();
            lblVeroeffentlicht = new javax.swing.JLabel();
            chkVeroeffentlicht = new javax.swing.JCheckBox();
            lblHeader2 = new javax.swing.JLabel();
            cbMainLocationType = new DefaultBindableReferenceCombo(true);
            txtAuthor = new javax.swing.JTextField();
            lblUrlCheckImage = new javax.swing.JLabel();
            lblUrlCheckWebsite = new javax.swing.JLabel();
            lblHeader3 = new javax.swing.JLabel();
            lblWebsite = new javax.swing.JLabel();
            lblAuthor = new javax.swing.JLabel();
            lblImageUrl = new javax.swing.JLabel();
            jScrollPane1 = new javax.swing.JScrollPane();
            txtaImageUrl = new javax.swing.JTextArea();
            jScrollPane2 = new javax.swing.JScrollPane();
            txtaWebsiteUrl = new javax.swing.JTextArea();

            dlgAddLocationType.setModal(true);

            panAddLocationType.setMaximumSize(new java.awt.Dimension(180, 120));
            panAddLocationType.setMinimumSize(new java.awt.Dimension(180, 120));
            panAddLocationType.setPreferredSize(new java.awt.Dimension(180, 120));
            panAddLocationType.setLayout(new java.awt.GridBagLayout());

            lblAuswaehlen.setText("Bitte Thema auswählen:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            panAddLocationType.add(lblAuswaehlen, gridBagConstraints);
        }
        cbTypes.setMaximumSize(new java.awt.Dimension(100, 20));
        cbTypes.setMinimumSize(new java.awt.Dimension(100, 20));
        cbTypes.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddLocationType.add(cbTypes, gridBagConstraints);

        panMenButtons.setLayout(new java.awt.GridBagLayout());

        btnMenAbort.setText("Abbrechen");
        btnMenAbort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons.add(btnMenAbort, gridBagConstraints);

        btnMenOk.setText("Ok");
        btnMenOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOkActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons.add(btnMenOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddLocationType.add(panMenButtons, gridBagConstraints);

        dlgAddLocationType.getContentPane().add(panAddLocationType, java.awt.BorderLayout.CENTER);

        dlgAddZusNamen.setModal(true);

        panAddName.setMaximumSize(new java.awt.Dimension(180, 120));
        panAddName.setMinimumSize(new java.awt.Dimension(180, 120));
        panAddName.setPreferredSize(new java.awt.Dimension(180, 120));
        panAddName.setLayout(new java.awt.GridBagLayout());

        lblNamesAuswaehlen.setText("Bitte Namen eingeben:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panAddName.add(lblNamesAuswaehlen, gridBagConstraints);

        panMenNamesButtons.setLayout(new java.awt.GridBagLayout());

        btnNamesMenAbort.setText("Abbrechen");
        btnNamesMenAbort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNamesMenAbortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenNamesButtons.add(btnNamesMenAbort, gridBagConstraints);

        btnNamesMenOk.setText("Ok");
        btnNamesMenOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNamesMenOkActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenNamesButtons.add(btnNamesMenOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddName.add(panMenNamesButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddName.add(txtZusNamen, gridBagConstraints);

        dlgAddZusNamen.getContentPane().add(panAddName, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.GridBagLayout());

        panCenter.setOpaque(false);
        panCenter.setLayout(new java.awt.GridBagLayout());

        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        lblFax.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFax.setText("Fax:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblFax, gridBagConstraints);

        lblEmail.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEmail.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblEmail, gridBagConstraints);

        lblUrl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblUrl, gridBagConstraints);

        lblStrasse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Strasse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblStrasse, gridBagConstraints);

        lblTelefon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblTelefon, gridBagConstraints);

        lblPLZ.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPLZ.setText("PLZ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblPLZ, gridBagConstraints);

        lblInfo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInfo.setText("Info:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        panContent.add(lblInfo, gridBagConstraints);

        lblStadt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStadt.setText("Stadt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblStadt, gridBagConstraints);

        lblArt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArt.setText("Art der Info:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblArt, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fax}"),
                txtFax,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtFax, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                txtStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stadt}"),
                txtStadt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtStadt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.plz}"),
                txtPLZ,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtPLZ, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.telefon}"),
                txtTelefon,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtTelefon, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.email}"),
                txtEmail,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtEmail, gridBagConstraints);

        txtaInfo.setColumns(5);
        txtaInfo.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtaInfo.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.info}"),
                txtaInfo,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        scpTxtInfo.setViewportView(txtaInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(scpTxtInfo, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.url}"),
                txtUrl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtUrl, gridBagConstraints);

        lblBezeichnung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBezeichnung.setText("Bezeichnung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblBezeichnung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geographicidentifier}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(txtBezeichnung, gridBagConstraints);

        panSpacing1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panContent.add(panSpacing1, gridBagConstraints);

        lblHeader1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblHeader1.setText("Beschreibung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblHeader1, gridBagConstraints);

        lblGeomPoint.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeomPoint.setText("Flächengeometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblGeomPoint, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pos}"),
                cbGeomPoint,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomPoint).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(cbGeomPoint, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_area}"),
                cbGeomArea,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomArea).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(cbGeomArea, gridBagConstraints);

        lblGeomPoint1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeomPoint1.setText("Punktgeometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblGeomPoint1, gridBagConstraints);

        cbInfoArt.setMaximumSize(new java.awt.Dimension(200, 20));
        cbInfoArt.setMinimumSize(new java.awt.Dimension(150, 20));
        cbInfoArt.setPreferredSize(new java.awt.Dimension(150, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.info_art}"),
                cbInfoArt,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbInfoArt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbInfoArtActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        panContent.add(cbInfoArt, gridBagConstraints);

        btnCreateAreaFromPoint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
        btnCreateAreaFromPoint.setText(org.openide.util.NbBundle.getMessage(
                Poi_locationinstanceEditor.class,
                "VermessungRissEditor.btnCombineGeometries.text"));                                     // NOI18N
        btnCreateAreaFromPoint.setToolTipText(org.openide.util.NbBundle.getMessage(
                Poi_locationinstanceEditor.class,
                "PoiLocationsinstanceEditor.btnCreateAreaFromPoinr.toolTipText"));                      // NOI18N
        btnCreateAreaFromPoint.setFocusPainted(false);
        btnCreateAreaFromPoint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCreateAreaFromPointActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panContent.add(btnCreateAreaFromPoint, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCenter.add(panContent, gridBagConstraints);

        panContent2.setOpaque(false);
        panContent2.setLayout(new java.awt.GridBagLayout());

        lblMainLocationType.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMainLocationType.setText("Haupthema:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblMainLocationType, gridBagConstraints);

        lblLocationTypes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLocationTypes.setText("Themen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        panContent2.add(lblLocationTypes, gridBagConstraints);

        lstLocationTypes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.locationtypes}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstLocationTypes);
        bindingGroup.addBinding(jListBinding);

        scpLstLocationTypes.setViewportView(lstLocationTypes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(scpLstLocationTypes, gridBagConstraints);

        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        btnAddThema.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddThema.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddThemaActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(btnAddThema, gridBagConstraints);

        btnRemoveThema.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveThema.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveThemaActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(btnRemoveThema, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(panButtons, gridBagConstraints);

        panButtons1.setOpaque(false);
        panButtons1.setLayout(new java.awt.GridBagLayout());

        btnAddZusNamen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddZusNamen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddZusNamenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons1.add(btnAddZusNamen, gridBagConstraints);

        btnRemoveZusNamen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveZusNamen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveZusNamenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons1.add(btnRemoveZusNamen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(panButtons1, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alternativegeographicidentifier}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstZusNamen);
        bindingGroup.addBinding(jListBinding);

        scpZusNamen.setViewportView(lstZusNamen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(scpZusNamen, gridBagConstraints);

        lblLocationTypes1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLocationTypes1.setText("Zusätzliche Namen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        panContent2.add(lblLocationTypes1, gridBagConstraints);

        lblSignatur.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur.setText("Signatur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblSignatur, gridBagConstraints);

        ((FastBindableReferenceCombo)cbSignatur).setSorted(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.signatur}"),
                cbSignatur,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        cbSignatur.setRenderer(new SignaturListCellRenderer());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 4);
        panContent2.add(cbSignatur, gridBagConstraints);

        panSpacing2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panContent2.add(panSpacing2, gridBagConstraints);

        lblVeroeffentlicht.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVeroeffentlicht.setText("Veröffentlicht:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblVeroeffentlicht, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.to_publish}"),
                chkVeroeffentlicht,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 5);
        panContent2.add(chkVeroeffentlicht, gridBagConstraints);

        lblHeader2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblHeader2.setText("Einordnung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblHeader2, gridBagConstraints);

        cbMainLocationType.setMaximumSize(new java.awt.Dimension(200, 20));
        cbMainLocationType.setMinimumSize(new java.awt.Dimension(150, 20));
        cbMainLocationType.setPreferredSize(new java.awt.Dimension(150, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mainlocationtype}"),
                cbMainLocationType,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbMainLocationType.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbMainLocationTypeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        panContent2.add(cbMainLocationType, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.urheber_foto}"),
                txtAuthor,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(txtAuthor, gridBagConstraints);

        lblUrlCheckImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        lblUrlCheckImage.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblUrlCheckImageMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        panContent2.add(lblUrlCheckImage, gridBagConstraints);

        lblUrlCheckWebsite.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        lblUrlCheckWebsite.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblUrlCheckWebsiteMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        panContent2.add(lblUrlCheckWebsite, gridBagConstraints);

        lblHeader3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblHeader3.setText("Bilder");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblHeader3, gridBagConstraints);

        lblWebsite.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblWebsite.setText("Webseite des Bildes:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblWebsite, gridBagConstraints);

        lblAuthor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAuthor.setText("Urheber:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblAuthor, gridBagConstraints);

        lblImageUrl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblImageUrl.setText("Bild-URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(lblImageUrl, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtaImageUrl.setColumns(20);
        txtaImageUrl.setRows(1);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.foto}"),
                txtaImageUrl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtaImageUrl);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        panContent2.add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtaWebsiteUrl.setColumns(20);
        txtaWebsiteUrl.setRows(1);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fotostrecke}"),
                txtaWebsiteUrl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtaWebsiteUrl);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 7);
        panContent2.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCenter.add(panContent2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(panCenter, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddThemaActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddThemaActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(Poi_locationinstanceEditor.this),
            dlgAddLocationType,
            true);
    }                                                                               //GEN-LAST:event_btnAddThemaActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveThemaActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveThemaActionPerformed
        final Object selection = lstLocationTypes.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll das Thema wirklich gelöscht werden?",
                    "Thema entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    deleteItemFromList("locationtypes", selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Löschen",
                            "Beim Löschen des Themas ist ein Fehler aufgetreten",
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                                  //GEN-LAST:event_btnRemoveThemaActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbortActionPerformed
        dlgAddLocationType.setVisible(false);
    }                                                                               //GEN-LAST:event_btnMenAbortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOkActionPerformed
        try {
            final Object selItem = cbTypes.getSelectedItem();
            if (selItem instanceof MetaObject) {
                addBeanToCollection("locationtypes", ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        } finally {
            dlgAddLocationType.setVisible(false);
        }
    }                                                                            //GEN-LAST:event_btnMenOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddZusNamenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddZusNamenActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(Poi_locationinstanceEditor.this),
            dlgAddZusNamen,
            true);
    }                                                                                  //GEN-LAST:event_btnAddZusNamenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveZusNamenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveZusNamenActionPerformed
        final Object selection = lstZusNamen.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll der zusätzliche Name wirklich gelöscht werden?",
                    "Name entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    deleteItemFromList("alternativegeographicidentifier", selection, true);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Löschen",
                            "Beim Löschen des zusätzlichen Namens ist ein Fehler aufgetreten",
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                                     //GEN-LAST:event_btnRemoveZusNamenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNamesMenAbortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNamesMenAbortActionPerformed
        dlgAddZusNamen.setVisible(false);
        txtZusNamen.setText("");
    }                                                                                    //GEN-LAST:event_btnNamesMenAbortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNamesMenOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNamesMenOkActionPerformed
        try {
            final String addName = txtZusNamen.getText();
            if (addName.length() > 0) {
                final MetaClass alternativeGeoIdentifierMC = ClassCacheMultiple.getMetaClass(SessionManager.getSession()
                                .getUser().getDomain(),
                        "poi_alternativegeographicidentifier");
                final MetaObject newAGI = alternativeGeoIdentifierMC.getEmptyInstance();
                final CidsBean newAGIBean = newAGI.getBean();
                newAGIBean.setProperty("alternativegeographicidentifier", addName);
                addBeanToCollection("alternativegeographicidentifier", newAGIBean);
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        } finally {
            txtZusNamen.setText("");
            dlgAddZusNamen.setVisible(false);
        }
    }                                                                                 //GEN-LAST:event_btnNamesMenOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbInfoArtActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbInfoArtActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbInfoArtActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbMainLocationTypeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbMainLocationTypeActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbMainLocationTypeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreateAreaFromPointActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCreateAreaFromPointActionPerformed
        final CidsBean geom_pos = (CidsBean)cidsBean.getProperty("pos");
        final CidsBean geom_area = (CidsBean)cidsBean.getProperty("geom_area");
        if (geom_area != null) {
            final Object[] options = { "Ja, Geometrie überschreiben", "Abbrechen" };
            final int result = JOptionPane.showOptionDialog(StaticSwingTools.getParentFrame(this),
                    "Durch diese Aktion wird die digitalisierte Flächengeometrie überschrieben. Wollen Sie das wirklich?",
                    "Geometrie überschreiben?",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);
            if ((result == JOptionPane.CLOSED_OPTION) || (result == 1)) {
                return;
            }
        }

        try {
            if (geom_pos != null) {
                final Geometry pos_geometry = ((Geometry)geom_pos.getProperty("geo_field")).buffer(250).getEnvelope();
                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "geom");
                final CidsBean newGeom = geomMetaClass.getEmptyInstance().getBean();
                newGeom.setProperty("geo_field", pos_geometry);
                cidsBean.setProperty("geom_area", newGeom);
            }
        } catch (Exception e) {
            log.fatal("Problem during setting the area geom", e);
        }
    } //GEN-LAST:event_btnCreateAreaFromPointActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblUrlCheckImageMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblUrlCheckImageMouseClicked
        final String foto = (String)cidsBean.getProperty("foto");
        if (foto != null) {
            try {
                BrowserLauncher.openURL(foto);
            } catch (Exception ex) {
                final String message = "Fehler beim Öffnen des Fotos.";
                log.error(message, ex);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    message,
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                                //GEN-LAST:event_lblUrlCheckImageMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblUrlCheckWebsiteMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblUrlCheckWebsiteMouseClicked
        final String foto = (String)cidsBean.getProperty("fotostrecke");
        if (foto != null) {
            try {
                BrowserLauncher.openURL(foto);
            } catch (Exception ex) {
                final String message = "Fehler beim Öffnen der Fotostrecke.";
                log.error(message, ex);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    message,
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                                  //GEN-LAST:event_lblUrlCheckWebsiteMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  propName     DOCUMENT ME!
     * @param  newTypeBean  DOCUMENT ME!
     */
    private void addBeanToCollection(final String propName, final CidsBean newTypeBean) {
        if ((newTypeBean != null) && (propName != null)) {
            final Object o = cidsBean.getProperty(propName);
            if (o instanceof Collection) {
                try {
                    final Collection<CidsBean> col = (Collection)o;
                    for (final CidsBean bean : col) {
                        if (newTypeBean.equals(bean)) {
                            log.info("Bean " + newTypeBean + " already present in " + propName + "!");
                            return;
                        }
                    }
                    col.add(newTypeBean);
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        if ((title == null) || (title.length() == 0)) {
            title = getTitleBackup();
        }
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTitleBackup() {
        final CidsBean bean = cidsBean;
        if (bean != null) {
            return bean.getMetaObject().getMetaClass().getName();
        }
        return "Point of Interest (POI)";
    }
}
