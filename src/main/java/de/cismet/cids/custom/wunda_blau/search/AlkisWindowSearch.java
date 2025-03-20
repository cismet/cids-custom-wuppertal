/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * BaulastWindowSearch.java
 *
 * Created on 09.12.2010, 14:33:10
 */
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.actiontag.ActionTagProtected;
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.CardLayout;
import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsAlkisSearchStatement;
import de.cismet.cids.custom.wupp.client.alkis.GrundbuchblattInputField;
import de.cismet.cids.custom.wupp.client.alkis.GrundbuchblattInputFieldConfig;
import de.cismet.cids.custom.wupp.client.alkis.GrundbuchblattInputWindow;
import de.cismet.cids.custom.wupp.client.alkis.ParcelInputField;
import de.cismet.cids.custom.wupp.client.alkis.ParcelInputFieldConfig;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class AlkisWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisWindowSearch.class);
    private static final String ACTION_TAG = "custom.alkis.windowsearch@WUNDA_BLAU";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //~ Instance fields --------------------------------------------------------

    private MetaClass mc = null;
    private ImageIcon icon = null;
    private SearchControlPanel pnlSearchCancel = null;
    private final ParcelInputFieldConfig parcelInputFieldConfig;
    private GrundbuchblattInputFieldConfig grundbuchblattInputFieldConfig;
    private boolean fallbackConfigParcel = false;
    private boolean fallbackConfigGrundbuchblatt = false;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrNach;
    private javax.swing.ButtonGroup bgrOwner;
    private javax.swing.ButtonGroup bgrUeber;
    private javax.swing.JCheckBox chkGeomFilter;
    private de.cismet.cids.custom.wupp.client.alkis.GrundbuchblattInputField grundbuchblattInputField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel lblFallbackGrundbuchblatt;
    private javax.swing.JRadioButton optEigIstFirma;
    private javax.swing.JRadioButton optEigIstMaennlich;
    private javax.swing.JRadioButton optEigIstUnbekannt;
    private javax.swing.JRadioButton optEigIstWeiblich;
    private javax.swing.JRadioButton optSucheNachFlurstuecke;
    private javax.swing.JRadioButton optSucheNachGrundbuchblaetter;
    private javax.swing.JRadioButton optSucheUeberEigentuemer;
    private javax.swing.JRadioButton optSucheUeberFlurstueck;
    private javax.swing.JRadioButton optSucheUeberGrundbuchblatt;
    private javax.swing.JPanel panCommand;
    private javax.swing.JPanel panEingabe;
    private javax.swing.JPanel panEingabeEigentuemer;
    private javax.swing.JPanel panEingabeFlurstueck;
    private javax.swing.JPanel panEingabeGrundbuchblatt;
    private de.cismet.cids.custom.wupp.client.alkis.ParcelInputField panParcelInputField;
    private javax.swing.JPanel panSearch;
    private javax.swing.JPanel panSucheNach;
    private javax.swing.JPanel panSucheUeber;
    private javax.swing.JTextField txtGeburtsdatum;
    private javax.swing.JTextField txtGeburtsname;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtVorname;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BaulastWindowSearch.
     */
    public AlkisWindowSearch() {
        ParcelInputFieldConfig parcelInputFieldConfig = null;
        try {
            parcelInputFieldConfig = MAPPER.readValue(AlkisWindowSearch.class.getResourceAsStream(
                        "/de/cismet/cids/custom/wunda_blau/res/alkis/ParcelInputFieldConfig.json"),
                    ParcelInputFieldConfig.class);
        } catch (IOException ex) {
            LOG.warn("ParcelInputFieldConfig could not be loaded, use fallback configuration.", ex);
            parcelInputFieldConfig = ParcelInputFieldConfig.FallbackConfig;
            fallbackConfigParcel = true;
        }
        this.parcelInputFieldConfig = parcelInputFieldConfig;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            grundbuchblattInputFieldConfig =
                new ObjectMapper().readValue(GrundbuchblattInputWindow.class.getResourceAsStream(
                        "/de/cismet/cids/custom/wunda_blau/res/alkis/GrundbuchblattInputFieldConfig.json"),
                    GrundbuchblattInputFieldConfig.class);
            System.out.println(grundbuchblattInputFieldConfig.getDelimiter1AsString());
        } catch (IOException ex) {
            LOG.warn("GrundbuchblattInputFieldConfig could not be loaded, use fallback configuration.", ex);
            grundbuchblattInputFieldConfig = GrundbuchblattInputFieldConfig.FallbackConfig;
            fallbackConfigGrundbuchblatt = true;
        }

        try {
            mc = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "ALKIS_LANDPARCEL",
                    getConnectionContext());
            icon = new ImageIcon(mc.getIconData());
            initComponents();
            ((CardLayout)panEingabe.getLayout()).show(panEingabe, "eigentuemer");
            pnlSearchCancel = new SearchControlPanel(this, getConnectionContext());
            final Dimension max = pnlSearchCancel.getMaximumSize();
            final Dimension min = pnlSearchCancel.getMinimumSize();
            final Dimension pre = pnlSearchCancel.getPreferredSize();
            pnlSearchCancel.setMaximumSize(new java.awt.Dimension(
                    new Double(max.getWidth()).intValue(),
                    new Double(max.getHeight() + 5).intValue()));
            pnlSearchCancel.setMinimumSize(new java.awt.Dimension(
                    new Double(min.getWidth()).intValue(),
                    new Double(min.getHeight() + 5).intValue()));
            pnlSearchCancel.setPreferredSize(new java.awt.Dimension(
                    new Double(pre.getWidth() + 6).intValue(),
                    new Double(pre.getHeight() + 5).intValue()));
            panCommand.add(pnlSearchCancel);
            panCommand.add(Box.createHorizontalStrut(5));
            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                final AlkisCreateSearchGeometryListener alkisSearchGeometryListener =
                    new AlkisCreateSearchGeometryListener(mappingComponent, new AlkisSearchTooltip(icon));
                alkisSearchGeometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        AlkisCreateSearchGeometryListener.ALKIS_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            AlkisWindowSearch.class,
                            "AlkisWindowSearch.btnGeoSearch.toolTipText"));
                panCommand.add(btnGeoSearch);
            }
            if (fallbackConfigParcel) {
                jLabel1.setVisible(true);
            } else {
                jLabel1.setVisible(false);
            }
            if (fallbackConfigGrundbuchblatt) {
                lblFallbackGrundbuchblatt.setVisible(true);
            } else {
                lblFallbackGrundbuchblatt.setVisible(false);
            }
        } catch (Exception e) {
            LOG.warn("Error in Constructor of AlkisWindowSearch", e);
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bgrUeber = new javax.swing.ButtonGroup();
        bgrNach = new javax.swing.ButtonGroup();
        bgrOwner = new javax.swing.ButtonGroup();
        panSearch = new javax.swing.JPanel();
        panSucheNach = new javax.swing.JPanel();
        optSucheNachFlurstuecke = new javax.swing.JRadioButton();
        optSucheNachGrundbuchblaetter = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        panSucheUeber = new javax.swing.JPanel();
        optSucheUeberEigentuemer = new javax.swing.JRadioButton();
        optSucheUeberFlurstueck = new javax.swing.JRadioButton();
        optSucheUeberGrundbuchblatt = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        panCommand = new javax.swing.JPanel();
        panEingabe = new javax.swing.JPanel();
        panEingabeGrundbuchblatt = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        grundbuchblattInputField1 = new GrundbuchblattInputField(grundbuchblattInputFieldConfig);
        lblFallbackGrundbuchblatt = new javax.swing.JLabel();
        panEingabeFlurstueck = new javax.swing.JPanel();
        panParcelInputField = new ParcelInputField(parcelInputFieldConfig);
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        panEingabeEigentuemer = new javax.swing.JPanel();
        txtVorname = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        optEigIstWeiblich = new javax.swing.JRadioButton();
        optEigIstMaennlich = new javax.swing.JRadioButton();
        optEigIstFirma = new javax.swing.JRadioButton();
        optEigIstUnbekannt = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        txtGeburtsname = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtGeburtsdatum = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        chkGeomFilter = new javax.swing.JCheckBox();

        setMaximumSize(new java.awt.Dimension(325, 460));
        setMinimumSize(new java.awt.Dimension(325, 460));
        setPreferredSize(new java.awt.Dimension(325, 460));
        setLayout(new java.awt.BorderLayout());

        panSearch.setMaximumSize(new java.awt.Dimension(400, 150));
        panSearch.setMinimumSize(new java.awt.Dimension(400, 150));
        panSearch.setPreferredSize(new java.awt.Dimension(400, 150));
        panSearch.setLayout(new java.awt.GridBagLayout());

        panSucheNach.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Suche nach"),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panSucheNach.setLayout(new java.awt.GridBagLayout());

        bgrNach.add(optSucheNachFlurstuecke);
        optSucheNachFlurstuecke.setSelected(true);
        optSucheNachFlurstuecke.setText("Flurstücken");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panSucheNach.add(optSucheNachFlurstuecke, gridBagConstraints);

        bgrNach.add(optSucheNachGrundbuchblaetter);
        optSucheNachGrundbuchblaetter.setText("Grundbuchblättern");
        panSucheNach.add(optSucheNachGrundbuchblaetter, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panSucheNach.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        panSearch.add(panSucheNach, gridBagConstraints);

        panSucheUeber.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Suche über"),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panSucheUeber.setLayout(new java.awt.GridBagLayout());

        bgrUeber.add(optSucheUeberEigentuemer);
        optSucheUeberEigentuemer.setSelected(true);
        optSucheUeberEigentuemer.setText("Eigentümer");
        optSucheUeberEigentuemer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optSucheUeberEigentuemerActionPerformed(evt);
                }
            });
        panSucheUeber.add(optSucheUeberEigentuemer, new java.awt.GridBagConstraints());

        bgrUeber.add(optSucheUeberFlurstueck);
        optSucheUeberFlurstueck.setText("Flurstück");
        optSucheUeberFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optSucheUeberFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panSucheUeber.add(optSucheUeberFlurstueck, gridBagConstraints);

        bgrUeber.add(optSucheUeberGrundbuchblatt);
        optSucheUeberGrundbuchblatt.setText("Grundbuchblatt");
        optSucheUeberGrundbuchblatt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optSucheUeberGrundbuchblattActionPerformed(evt);
                }
            });
        panSucheUeber.add(optSucheUeberGrundbuchblatt, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panSucheUeber.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        panSearch.add(panSucheUeber, gridBagConstraints);

        panCommand.setLayout(new javax.swing.BoxLayout(panCommand, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(panCommand, gridBagConstraints);

        panEingabe.setLayout(new java.awt.CardLayout());

        panEingabeGrundbuchblatt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Grundbuchblatt Suchmaske"),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panEingabeGrundbuchblatt.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Grundbuchblattnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeGrundbuchblatt.add(jLabel12, gridBagConstraints);

        jLabel4.setText(
            "<html> <p>Beispiel: 053001-0003117</p><br><p>Platzhaltersymbole:</p><p>&nbsp;<b>%</b>&nbsp;&nbsp;&nbsp;&nbsp;eine beliebige Anzahl von Zeichen</p> <p>&nbsp;<b>_</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ein einzelnes Zeichen</p> </html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 5);
        panEingabeGrundbuchblatt.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeGrundbuchblatt.add(grundbuchblattInputField1, gridBagConstraints);

        lblFallbackGrundbuchblatt.setText(
            "<html><b><font color=\"#FF0000\">Es ist ein Fehler beim Laden der Konfiguration aufgetreten. <br />\nDie Funktionalität ist dadurch eingeschränkt.</font></b></html>");
        lblFallbackGrundbuchblatt.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 5);
        panEingabeGrundbuchblatt.add(lblFallbackGrundbuchblatt, gridBagConstraints);

        panEingabe.add(panEingabeGrundbuchblatt, "grundbuchblatt");

        panEingabeFlurstueck.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Flurstück Suchmaske"),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panEingabeFlurstueck.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeFlurstueck.add(panParcelInputField, gridBagConstraints);

        jLabel9.setText("Flurstücksnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeFlurstueck.add(jLabel9, gridBagConstraints);

        jLabel3.setText(
            "<html> <p>Beispiel: 053001-117-00058</p><br><p>Platzhaltersymbole:</p><p>&nbsp;<b>%</b>&nbsp;&nbsp;&nbsp;&nbsp;eine beliebige Anzahl von Zeichen</p> <p>&nbsp;<b>_</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ein einzelnes Zeichen</p> </html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 5);
        panEingabeFlurstueck.add(jLabel3, gridBagConstraints);

        jLabel1.setText(
            "<html><b><font color=\"#FF0000\">Es ist ein Fehler beim Laden der Konfiguration aufgetreten. <br />\nDie Funktionalität ist dadurch eingeschränkt.</font></b></html>");
        jLabel1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 5);
        panEingabeFlurstueck.add(jLabel1, gridBagConstraints);

        panEingabe.add(panEingabeFlurstueck, "flurstueck");

        panEingabeEigentuemer.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Eigentümer Suchmaske"),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panEingabeEigentuemer.setLayout(new java.awt.GridBagLayout());

        txtVorname.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtVornameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(txtVorname, gridBagConstraints);

        jLabel7.setText("Vorname:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(txtName, gridBagConstraints);

        jLabel8.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(jLabel8, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder("Eigentümer Anrede ist"),
                javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        bgrOwner.add(optEigIstWeiblich);
        optEigIstWeiblich.setText("Frau");
        optEigIstWeiblich.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optEigIstWeiblichActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(optEigIstWeiblich, gridBagConstraints);

        bgrOwner.add(optEigIstMaennlich);
        optEigIstMaennlich.setText("Herr");
        optEigIstMaennlich.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optEigIstMaennlichActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(optEigIstMaennlich, gridBagConstraints);

        bgrOwner.add(optEigIstFirma);
        optEigIstFirma.setText("Firma");
        optEigIstFirma.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optEigIstFirmaActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(optEigIstFirma, gridBagConstraints);

        bgrOwner.add(optEigIstUnbekannt);
        optEigIstUnbekannt.setSelected(true);
        optEigIstUnbekannt.setText("unbekannt");
        optEigIstUnbekannt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optEigIstUnbekanntActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(optEigIstUnbekannt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panEingabeEigentuemer.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(txtGeburtsname, gridBagConstraints);

        jLabel13.setText("Geburtsname:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(jLabel13, gridBagConstraints);

        jLabel14.setText("Geburtsdatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(jLabel14, gridBagConstraints);

        txtGeburtsdatum.setToolTipText("Bsp.: 18.01.1974");
        txtGeburtsdatum.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGeburtsdatumActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEingabeEigentuemer.add(txtGeburtsdatum, gridBagConstraints);

        jLabel2.setText(
            "<html><br><p>Platzhaltersymbole:</p><p>&nbsp;<b>%</b>&nbsp;&nbsp;&nbsp;&nbsp;eine beliebige Anzahl von Zeichen</p> <p>&nbsp;<b>_</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ein einzelnes Zeichen</p> </html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 5);
        panEingabeEigentuemer.add(jLabel2, gridBagConstraints);

        panEingabe.add(panEingabeEigentuemer, "eigentuemer");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        panSearch.add(panEingabe, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        panSearch.add(jPanel6, gridBagConstraints);

        chkGeomFilter.setText("nur im aktuellen Kartenausschnitt suchen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 17, 0, 0);
        panSearch.add(chkGeomFilter, gridBagConstraints);

        add(panSearch, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optSucheUeberEigentuemerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optSucheUeberEigentuemerActionPerformed
        ((CardLayout)panEingabe.getLayout()).show(panEingabe, "eigentuemer");
    }                                                                                            //GEN-LAST:event_optSucheUeberEigentuemerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtVornameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtVornameActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtVornameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGeburtsdatumActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtGeburtsdatumActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtGeburtsdatumActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optEigIstMaennlichActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optEigIstMaennlichActionPerformed
        txtVorname.setEnabled(true);
        txtGeburtsname.setEnabled(true);
        txtGeburtsdatum.setEnabled(true);
    }                                                                                      //GEN-LAST:event_optEigIstMaennlichActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optEigIstUnbekanntActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optEigIstUnbekanntActionPerformed
        txtVorname.setEnabled(true);
        txtGeburtsname.setEnabled(true);
        txtGeburtsdatum.setEnabled(true);
    }                                                                                      //GEN-LAST:event_optEigIstUnbekanntActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optEigIstFirmaActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optEigIstFirmaActionPerformed
        txtVorname.setEnabled(false);
        txtGeburtsname.setEnabled(false);
        txtGeburtsdatum.setEnabled(false);
    }                                                                                  //GEN-LAST:event_optEigIstFirmaActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optEigIstWeiblichActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optEigIstWeiblichActionPerformed
        txtVorname.setEnabled(true);
        txtGeburtsname.setEnabled(true);
        txtGeburtsdatum.setEnabled(true);
    }                                                                                     //GEN-LAST:event_optEigIstWeiblichActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optSucheUeberFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optSucheUeberFlurstueckActionPerformed
        ((CardLayout)panEingabe.getLayout()).show(panEingabe, "flurstueck");
    }                                                                                           //GEN-LAST:event_optSucheUeberFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optSucheUeberGrundbuchblattActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optSucheUeberGrundbuchblattActionPerformed
        ((CardLayout)panEingabe.getLayout()).show(panEingabe, "grundbuchblatt");
    }                                                                                               //GEN-LAST:event_optSucheUeberGrundbuchblattActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getName() {
        return "ALKIS-Suche";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFallbackConfig() {
        return fallbackConfigParcel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        Geometry searchgeom = null;
        if (geometry != null) {
            searchgeom = CrsTransformer.transformToDefaultCrs(geometry);
        } else {
            if (chkGeomFilter.isSelected()) {
                final Geometry g =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
                final Geometry transformed = CrsTransformer.transformToDefaultCrs(g);
                // Damits auch mit -1 funzt:
                transformed.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
                searchgeom = transformed;
            }
        }

        CidsAlkisSearchStatement.Resulttyp resulttype = null;
        if (optSucheNachFlurstuecke.isSelected()) {
            resulttype = CidsAlkisSearchStatement.Resulttyp.FLURSTUECK;
        } else {
            resulttype = CidsAlkisSearchStatement.Resulttyp.BUCHUNGSBLATT;
        }
        if (optSucheUeberEigentuemer.isSelected()) {
            CidsAlkisSearchStatement.Personentyp ptyp = null;
            if (optEigIstFirma.isSelected()) {
                ptyp = CidsAlkisSearchStatement.Personentyp.FIRMA;
            } else if (optEigIstMaennlich.isSelected()) {
                ptyp = CidsAlkisSearchStatement.Personentyp.MANN;
            } else if (optEigIstWeiblich.isSelected()) {
                ptyp = CidsAlkisSearchStatement.Personentyp.FRAU;
            }

            final String name = txtName.getText().trim();
            final String vorname = txtVorname.getText().trim();
            final String geburtsname = txtGeburtsname.getText().trim();
            final String geburtsdatum = txtGeburtsdatum.getText().trim();
            return new CidsAlkisSearchStatement(resulttype, name, vorname, geburtsname, geburtsdatum, ptyp, searchgeom);
        } else if (optSucheUeberFlurstueck.isSelected()) {
            return new CidsAlkisSearchStatement(
                    resulttype,
                    CidsAlkisSearchStatement.SucheUeber.FLURSTUECKSNUMMER,
                    panParcelInputField.getCurrentParcel(),
                    searchgeom);
        } else {
            return new CidsAlkisSearchStatement(
                    resulttype,
                    CidsAlkisSearchStatement.SucheUeber.BUCHUNGSBLATTNUMMER,
                    grundbuchblattInputField1.getGrundbuchblattnummer(),
                    searchgeom);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        return getServerSearch();
    }

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void searchStarted() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  results  DOCUMENT ME!
     */
    @Override
    public void searchDone(final int results) {
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void searchCanceled() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (AlkisCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }
}
