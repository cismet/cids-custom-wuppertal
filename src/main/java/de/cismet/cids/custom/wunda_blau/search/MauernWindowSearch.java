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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.actiontag.ActionTagProtected;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMauernSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMauernSearchStatement.PropertyKeys;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMauernSearchStatement.SearchMode;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class MauernWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauernWindowSearch.class);
    // End of variables declaration
    private static final String ACTION_TAG = "custom.mauern.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private DefaultListModel eigentuemerListModel = new DefaultListModel();
    private DefaultListModel lastKlasseListModel = new DefaultListModel();
    private MetaClass metaClass;
    private ImageIcon icon;
    private JPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbMapSearch;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefVon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAnsicht;
    private javax.swing.JLabel lblBausubstanz;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblFiller3;
    private javax.swing.JLabel lblFiller4;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblGelaende;
    private javax.swing.JLabel lblGruendung;
    private javax.swing.JLabel lblHoeheBis;
    private javax.swing.JLabel lblHoeheVon;
    private javax.swing.JLabel lblNotenBis;
    private javax.swing.JLabel lblNotenBis1;
    private javax.swing.JLabel lblNotenBis2;
    private javax.swing.JLabel lblNotenBis3;
    private javax.swing.JLabel lblNotenBis4;
    private javax.swing.JLabel lblNotenBis5;
    private javax.swing.JLabel lblNotenFrom1;
    private javax.swing.JLabel lblNotenFrom2;
    private javax.swing.JLabel lblNotenFrom3;
    private javax.swing.JLabel lblNotenFrom4;
    private javax.swing.JLabel lblNotenFrom5;
    private javax.swing.JLabel lblNotenFrom6;
    private javax.swing.JLabel lblPruefTil;
    private javax.swing.JLabel lblPruefVon;
    private javax.swing.JLabel lblSanierung;
    private javax.swing.JLabel lblSelektierteLastklassen;
    private javax.swing.JLabel lblVerformung;
    private javax.swing.JLabel lblWandKopf;
    private javax.swing.JLabel lblselectedEigentuemer;
    private javax.swing.JList lstEigentuemer;
    private javax.swing.JList lstLastklasse;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlEigentuemerCtrlBtns;
    private javax.swing.JPanel pnlEigetuemer;
    private javax.swing.JPanel pnlGesamtnoten;
    private javax.swing.JPanel pnlHoehe;
    private javax.swing.JPanel pnlLastklasse;
    private javax.swing.JPanel pnlNoten;
    private javax.swing.JPanel pnlPruefung;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchMode;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbOne;
    private javax.swing.JScrollPane scpListEigentuemer;
    private javax.swing.JScrollPane scpListLastklasse;
    private javax.swing.JTextField tfAnsichtBis;
    private javax.swing.JTextField tfAnsichtVon;
    private javax.swing.JTextField tfBausubstanzBis;
    private javax.swing.JTextField tfBausubstanzVon;
    private javax.swing.JTextField tfGelaendeBis;
    private javax.swing.JTextField tfGelaendeVon;
    private javax.swing.JTextField tfGelaenderBis;
    private javax.swing.JTextField tfGelaenderVon;
    private javax.swing.JTextField tfGruendungBis;
    private javax.swing.JTextField tfGruendungVon;
    private javax.swing.JTextField tfHoeheBis;
    private javax.swing.JTextField tfHoeheVon;
    private javax.swing.JTextField tfSanierungBis;
    private javax.swing.JTextField tfSanierungVon;
    private javax.swing.JTextField tfVerformungBis;
    private javax.swing.JTextField tfVerformungVon;
    private javax.swing.JTextField tfWandkopfBis;
    private javax.swing.JTextField tfWandkopfVon;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauernWindowSearch.
     */
    public MauernWindowSearch() {
        try {
            initComponents();
            // todo just for debug
            pnlSearchCancel = new SearchControlPanel(this);
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
            pnlButtons.add(pnlSearchCancel);

            metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "mauer");

            byte[] iconDataFromMetaclass = new byte[] {};

            if (metaClass != null) {
                iconDataFromMetaclass = metaClass.getIconData();
            }

            if (iconDataFromMetaclass.length > 0) {
                LOG.info("Using icon from metaclass.");
                icon = new ImageIcon(metaClass.getIconData());
            } else {
                LOG.warn("Metaclass icon is not set. Trying to load default icon.");
                final URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png");

                if (urlToIcon != null) {
                    icon = new ImageIcon(urlToIcon);
                } else {
                    icon = new ImageIcon(new byte[] {});
                }
            }

            pnlButtons.add(Box.createHorizontalStrut(5));

            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                final MauernCreateSearchGeometryListener mauernSearchGeometryListener =
                    new MauernCreateSearchGeometryListener(mappingComponent,
                        new MauernSearchTooltip(icon));
                mauernSearchGeometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        MauernCreateSearchGeometryListener.MAUERN_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            MauernWindowSearch.class,
                            "MauernWindowSearch.btnGeoSearch.toolTipText"));
                pnlButtons.add(btnGeoSearch);
            }

            fillEigentuemerListModel();
            fillLastKlasseListModel();
            lstEigentuemer.setModel(eigentuemerListModel);
            lstEigentuemer.setCellRenderer(new CheckboxCellRenderer());
            lstEigentuemer.setSelectionModel(new ListToggleSelectionModel());
            lstEigentuemer.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent lse) {
                        lblselectedEigentuemer.setText("" + lstEigentuemer.getSelectedIndices().length);
                        if (lstEigentuemer.getSelectedIndices().length != 1) {
                            lblselectedEigentuemer.setText(
                                lblselectedEigentuemer.getText()
                                        + " selektierte Eigentümer");
                        } else {
                            lblselectedEigentuemer.setText(
                                lblselectedEigentuemer.getText()
                                        + " selektierter Eigentümer");
                        }
                    }
                });
            lstLastklasse.setModel(lastKlasseListModel);
            lstLastklasse.setCellRenderer(new CheckboxCellRenderer());
            lstLastklasse.setSelectionModel(new ListToggleSelectionModel());
            lstLastklasse.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent lse) {
                        lblSelektierteLastklassen.setText("" + lstLastklasse.getSelectedIndices().length);
                        if (lstLastklasse.getSelectedIndices().length != 1) {
                            lblSelektierteLastklassen.setText(
                                lblSelektierteLastklassen.getText()
                                        + " selektierte Lastklassen");
                        } else {
                            lblSelektierteLastklassen.setText(
                                lblSelektierteLastklassen.getText()
                                        + " selektierte Lastklasse");
                        }
                    }
                });
        } catch (Throwable e) {
            LOG.warn("Error in Constructor of MauernWindowSearch. Search will not work properly.", e);
        }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlNoten = new javax.swing.JPanel();
        tfWandkopfVon = new javax.swing.JTextField();
        lblNotenBis = new javax.swing.JLabel();
        tfWandkopfBis = new javax.swing.JTextField();
        lblFiller2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblWandKopf = new javax.swing.JLabel();
        lblAnsicht = new javax.swing.JLabel();
        lblGruendung = new javax.swing.JLabel();
        lblVerformung = new javax.swing.JLabel();
        lblGelaende = new javax.swing.JLabel();
        lblNotenFrom1 = new javax.swing.JLabel();
        lblNotenFrom2 = new javax.swing.JLabel();
        lblNotenFrom3 = new javax.swing.JLabel();
        lblNotenFrom4 = new javax.swing.JLabel();
        lblNotenFrom5 = new javax.swing.JLabel();
        lblNotenFrom6 = new javax.swing.JLabel();
        lblNotenBis1 = new javax.swing.JLabel();
        lblNotenBis2 = new javax.swing.JLabel();
        lblNotenBis3 = new javax.swing.JLabel();
        lblNotenBis4 = new javax.swing.JLabel();
        lblNotenBis5 = new javax.swing.JLabel();
        tfAnsichtVon = new javax.swing.JTextField();
        tfGruendungVon = new javax.swing.JTextField();
        tfVerformungVon = new javax.swing.JTextField();
        tfGelaendeVon = new javax.swing.JTextField();
        tfGelaenderVon = new javax.swing.JTextField();
        tfAnsichtBis = new javax.swing.JTextField();
        tfGruendungBis = new javax.swing.JTextField();
        tfVerformungBis = new javax.swing.JTextField();
        tfGelaendeBis = new javax.swing.JTextField();
        tfGelaenderBis = new javax.swing.JTextField();
        pnlLastklasse = new javax.swing.JPanel();
        scpListLastklasse = new javax.swing.JScrollPane();
        lstLastklasse = new javax.swing.JList();
        lblSelektierteLastklassen = new javax.swing.JLabel();
        pnlSearchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rbAll = new javax.swing.JRadioButton();
        rbOne = new javax.swing.JRadioButton();
        lblFiller5 = new javax.swing.JLabel();
        pnlEigetuemer = new javax.swing.JPanel();
        scpListEigentuemer = new javax.swing.JScrollPane();
        lstEigentuemer = new javax.swing.JList();
        pnlEigentuemerCtrlBtns = new javax.swing.JPanel();
        lblselectedEigentuemer = new javax.swing.JLabel();
        pnlGesamtnoten = new javax.swing.JPanel();
        lblBausubstanz = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tfBausubstanzVon = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfBausubstanzBis = new javax.swing.JTextField();
        lblSanierung = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tfSanierungVon = new javax.swing.JTextField();
        tfSanierungBis = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblFiller4 = new javax.swing.JLabel();
        pnlPruefung = new javax.swing.JPanel();
        lblPruefVon = new javax.swing.JLabel();
        lblPruefTil = new javax.swing.JLabel();
        dcPruefVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcPruefBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblFiller = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        pnlHoehe = new javax.swing.JPanel();
        lblHoeheVon = new javax.swing.JLabel();
        tfHoeheVon = new javax.swing.JTextField();
        lblHoeheBis = new javax.swing.JLabel();
        tfHoeheBis = new javax.swing.JTextField();
        lblFiller3 = new javax.swing.JLabel();
        lblFiller6 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlNoten.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlNoten.border.title"))); // NOI18N
        pnlNoten.setLayout(new java.awt.GridBagLayout());

        tfWandkopfVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfWandkopfVon.text")); // NOI18N
        tfWandkopfVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfWandkopfVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfWandkopfVon, gridBagConstraints);

        lblNotenBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis, gridBagConstraints);

        tfWandkopfBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfWandkopfBis.text")); // NOI18N
        tfWandkopfBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfWandkopfBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfWandkopfBis, gridBagConstraints);

        lblFiller2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlNoten.add(lblFiller2, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(jLabel2, gridBagConstraints);

        lblWandKopf.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblWandKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblWandKopf, gridBagConstraints);

        lblAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblAnsicht, gridBagConstraints);

        lblGruendung.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblGruendung, gridBagConstraints);

        lblVerformung.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblVerformung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblVerformung, gridBagConstraints);

        lblGelaende.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblGelaende.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblGelaende, gridBagConstraints);

        lblNotenFrom1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom1, gridBagConstraints);

        lblNotenFrom2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom2, gridBagConstraints);

        lblNotenFrom3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom3, gridBagConstraints);

        lblNotenFrom4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom4, gridBagConstraints);

        lblNotenFrom5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom5, gridBagConstraints);

        lblNotenFrom6.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom6, gridBagConstraints);

        lblNotenBis1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis1, gridBagConstraints);

        lblNotenBis2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis2, gridBagConstraints);

        lblNotenBis3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis3, gridBagConstraints);

        lblNotenBis4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis4, gridBagConstraints);

        lblNotenBis5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis5, gridBagConstraints);

        tfAnsichtVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfAnsichtVon.text")); // NOI18N
        tfAnsichtVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfAnsichtVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfAnsichtVon, gridBagConstraints);

        tfGruendungVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGruendungVon.text")); // NOI18N
        tfGruendungVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGruendungVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGruendungVon, gridBagConstraints);

        tfVerformungVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfVerformungVon.text")); // NOI18N
        tfVerformungVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfVerformungVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfVerformungVon, gridBagConstraints);

        tfGelaendeVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGelaendeVon.text")); // NOI18N
        tfGelaendeVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGelaendeVon, gridBagConstraints);

        tfGelaenderVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGelaenderVon.text")); // NOI18N
        tfGelaenderVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaenderVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGelaenderVon, gridBagConstraints);

        tfAnsichtBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfAnsichtBis.text")); // NOI18N
        tfAnsichtBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfAnsichtBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfAnsichtBis, gridBagConstraints);

        tfGruendungBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGruendungBis.text")); // NOI18N
        tfGruendungBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGruendungBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGruendungBis, gridBagConstraints);

        tfVerformungBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfVerformungBis.text")); // NOI18N
        tfVerformungBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfVerformungBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfVerformungBis, gridBagConstraints);

        tfGelaendeBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGelaendeBis.text")); // NOI18N
        tfGelaendeBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGelaendeBis, gridBagConstraints);

        tfGelaenderBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfGelaenderBis.text")); // NOI18N
        tfGelaenderBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaenderBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGelaenderBis, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlNoten, gridBagConstraints);

        pnlLastklasse.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlLastklasse.border.title"))); // NOI18N
        pnlLastklasse.setLayout(new java.awt.GridBagLayout());

        scpListLastklasse.setMinimumSize(new java.awt.Dimension(26, 150));
        scpListLastklasse.setPreferredSize(new java.awt.Dimension(45, 150));

        lstLastklasse.setModel(lastKlasseListModel);
        scpListLastklasse.setViewportView(lstLastklasse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlLastklasse.add(scpListLastklasse, gridBagConstraints);

        lblSelektierteLastklassen.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblSelektierteLastklassen.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlLastklasse.add(lblSelektierteLastklassen, gridBagConstraints);
        lblSelektierteLastklassen.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        MauernWindowSearch.class,
                        "MauernWindowSearch.lblSelektierteLastklassen.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlLastklasse, gridBagConstraints);

        pnlSearchMode.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSearchMode.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText(org.openide.util.NbBundle.getMessage(MauernWindowSearch.class, "MauernWindowSearch.rbAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlSearchMode.add(rbAll, gridBagConstraints);

        buttonGroup1.add(rbOne);
        rbOne.setText(org.openide.util.NbBundle.getMessage(MauernWindowSearch.class, "MauernWindowSearch.rbOne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSearchMode.add(rbOne, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlSearchMode.add(lblFiller5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(pnlSearchMode, gridBagConstraints);

        pnlEigetuemer.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlEigetuemer.border.title"))); // NOI18N
        pnlEigetuemer.setLayout(new java.awt.GridBagLayout());

        scpListEigentuemer.setMinimumSize(new java.awt.Dimension(22, 150));
        scpListEigentuemer.setPreferredSize(new java.awt.Dimension(278, 150));

        lstEigentuemer.setModel(eigentuemerListModel);
        scpListEigentuemer.setViewportView(lstEigentuemer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlEigetuemer.add(scpListEigentuemer, gridBagConstraints);

        pnlEigentuemerCtrlBtns.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlEigetuemer.add(pnlEigentuemerCtrlBtns, gridBagConstraints);

        lblselectedEigentuemer.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblselectedEigentuemer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlEigetuemer.add(lblselectedEigentuemer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlEigetuemer, gridBagConstraints);

        pnlGesamtnoten.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlGesamtnoten.border.title"))); // NOI18N
        pnlGesamtnoten.setLayout(new java.awt.GridBagLayout());

        lblBausubstanz.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblBausubstanz.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlGesamtnoten.add(lblBausubstanz, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlGesamtnoten.add(jLabel4, gridBagConstraints);

        tfBausubstanzVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jTextField3.text")); // NOI18N
        tfBausubstanzVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfBausubstanzVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlGesamtnoten.add(tfBausubstanzVon, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlGesamtnoten.add(jLabel5, gridBagConstraints);

        tfBausubstanzBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jTextField3.text")); // NOI18N
        tfBausubstanzBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfBausubstanzBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlGesamtnoten.add(tfBausubstanzBis, gridBagConstraints);

        lblSanierung.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblSanierung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        pnlGesamtnoten.add(lblSanierung, gridBagConstraints);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlGesamtnoten.add(jLabel7, gridBagConstraints);

        tfSanierungVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfSanierungVon.text")); // NOI18N
        tfSanierungVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfSanierungVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlGesamtnoten.add(tfSanierungVon, gridBagConstraints);

        tfSanierungBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jTextField3.text")); // NOI18N
        tfSanierungBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfSanierungBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlGesamtnoten.add(tfSanierungBis, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlGesamtnoten.add(jLabel8, gridBagConstraints);

        lblFiller4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlGesamtnoten.add(lblFiller4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlGesamtnoten, gridBagConstraints);

        pnlPruefung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlPruefung.border.title"))); // NOI18N
        pnlPruefung.setLayout(new java.awt.GridBagLayout());

        lblPruefVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblPruefVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlPruefung.add(lblPruefVon, gridBagConstraints);

        lblPruefTil.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblPruefTil.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlPruefung.add(lblPruefTil, gridBagConstraints);

        dcPruefVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlPruefung.add(dcPruefVon, gridBagConstraints);

        dcPruefBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlPruefung.add(dcPruefBis, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlPruefung.add(lblFiller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlPruefung, gridBagConstraints);

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlButtons, gridBagConstraints);

        cbMapSearch.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.cbMapSearch.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(cbMapSearch, gridBagConstraints);

        pnlHoehe.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearch.class,
                    "MauernWindowSearch.pnlHoehe.border.title"))); // NOI18N
        pnlHoehe.setLayout(new java.awt.GridBagLayout());

        lblHoeheVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblHoeheVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlHoehe.add(lblHoeheVon, gridBagConstraints);

        tfHoeheVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfNotenBis.text")); // NOI18N
        tfHoeheVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHoeheVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlHoehe.add(tfHoeheVon, gridBagConstraints);

        lblHoeheBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblHoeheBis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlHoehe.add(lblHoeheBis, gridBagConstraints);

        tfHoeheBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.tfNotenBis.text")); // NOI18N
        tfHoeheBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHoeheBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlHoehe.add(tfHoeheBis, gridBagConstraints);

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlHoehe.add(lblFiller3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlHoehe, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearch.class,
                "MauernWindowSearch.lblFiller6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(lblFiller6, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        Geometry geometryToSearchFor = null;
        SearchMode mode = null;
        LinkedList<Integer> eigentuemer = null;
        LinkedList<Integer> lastklassen = null;

        if (rbAll.isSelected()) {
            mode = SearchMode.AND_SEARCH;
        } else {
            mode = SearchMode.OR_SEARCH;
        }
        if (geometry != null) {
            geometryToSearchFor = geometry;
        } else {
            if (cbMapSearch.isSelected()) {
                geometryToSearchFor =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
            }
        }
        final Geometry transformedBoundingBox;
        if (geometryToSearchFor != null) {
            transformedBoundingBox = CrsTransformer.transformToDefaultCrs(geometryToSearchFor);
            transformedBoundingBox.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedBoundingBox = null;
        }

        final Object[] selectedEigentuemer = lstEigentuemer.getSelectedValues();
        if (selectedEigentuemer != null) {
            eigentuemer = new LinkedList<Integer>();
            for (int i = 0; i < selectedEigentuemer.length; i++) {
                final CidsBean bean = (CidsBean)selectedEigentuemer[i];
                final Integer id = (Integer)bean.getProperty("id");
                eigentuemer.add(id);
            }
        }

        final Object[] selectedLastKlasse = lstLastklasse.getSelectedValues();
        if (selectedEigentuemer != null) {
            lastklassen = new LinkedList<Integer>();
            for (int i = 0; i < selectedLastKlasse.length; i++) {
                final CidsBean bean = (CidsBean)selectedLastKlasse[i];
                final Integer id = (Integer)bean.getProperty("id");
                lastklassen.add(id);
            }
        }

        final HashMap<PropertyKeys, Double> filterProps = new HashMap<PropertyKeys, Double>();

        if ((tfHoeheVon.getText() != null) && !tfHoeheVon.getText().equals("")) {
            try {
                final double hv = Double.parseDouble(tfHoeheVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.HOEHE_VON, hv);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfHoeheVon.getText(), ex);
            }
        }

        if ((tfHoeheBis.getText() != null) && !tfHoeheBis.getText().equals("")) {
            try {
                final double hv = Double.parseDouble(tfHoeheBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.HOEHE_BIS, hv);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfHoeheBis.getText(), ex);
            }
        }

        if ((tfGelaenderVon.getText() != null) && !tfGelaenderVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfGelaenderVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GELAENDER_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGelaenderVon.getText(), ex);
            }
        }
        if ((tfGelaenderBis.getText() != null) && !tfGelaenderBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfGelaenderBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GELAENDER_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGelaenderBis.getText(), ex);
            }
        }

        if ((tfWandkopfVon.getText() != null) && !tfWandkopfVon.getText().equals("")) {
            try {
                final double wandkopfVon = Double.parseDouble(tfWandkopfVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.WANDKOPF_VON, wandkopfVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfWandkopfVon.getText(), ex);
            }
        }
        if ((tfWandkopfBis.getText() != null) && !tfWandkopfBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfWandkopfBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.WANDKOPF_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfWandkopfBis.getText(), ex);
            }
        }

        if ((tfAnsichtVon.getText() != null) && !tfAnsichtVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfAnsichtVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.ANSICHT_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfAnsichtVon.getText(), ex);
            }
        }
        if ((tfAnsichtBis.getText() != null) && !tfAnsichtBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfAnsichtBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.ANSICHT_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfAnsichtBis.getText(), ex);
            }
        }

        if ((tfGruendungVon.getText() != null) && !tfGruendungVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfGruendungVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GRUENDUNG_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGruendungVon.getText(), ex);
            }
        }
        if ((tfGruendungBis.getText() != null) && !tfGruendungBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfGruendungBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GRUENDUNG_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGruendungBis.getText(), ex);
            }
        }

        if ((tfVerformungVon.getText() != null) && !tfVerformungVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfVerformungVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.VERFORMUNG_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfVerformungVon.getText(), ex);
            }
        }
        if ((tfVerformungBis.getText() != null) && !tfVerformungBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfVerformungBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.VERFORMUNG_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfVerformungBis.getText(), ex);
            }
        }

        if ((tfGelaendeVon.getText() != null) && !tfGelaendeVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfGelaendeVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GELAENDE_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGelaendeVon.getText(), ex);
            }
        }
        if ((tfGelaendeBis.getText() != null) && !tfGelaendeBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfGelaendeBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.GELAENDE_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfGelaendeBis.getText(), ex);
            }
        }

        if ((tfBausubstanzVon.getText() != null) && !tfBausubstanzVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfBausubstanzVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.BAUSUBSTANZ_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfBausubstanzVon.getText(), ex);
            }
        }
        if ((tfBausubstanzBis.getText() != null) && !tfBausubstanzBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfBausubstanzBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.BAUSUBSTANZ_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfBausubstanzBis.getText(), ex);
            }
        }

        if ((tfSanierungVon.getText() != null) && !tfSanierungVon.getText().equals("")) {
            try {
                final double gelaenderVon = Double.parseDouble(tfSanierungVon.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.SANIERUNG_VON, gelaenderVon);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfSanierungVon.getText(), ex);
            }
        }
        if ((tfSanierungBis.getText() != null) && !tfSanierungBis.getText().equals("")) {
            try {
                final double gelaenderBis = Double.parseDouble(tfSanierungBis.getText().replace(',', '.'));
                filterProps.put(PropertyKeys.SANIERUNG_BIS, gelaenderBis);
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + tfSanierungBis.getText(), ex);
            }
        }

        return new CidsMauernSearchStatement(
                eigentuemer,
                dcPruefVon.getDate(),
                dcPruefBis.getDate(),
                lastklassen,
                transformedBoundingBox,
                mode,
                filterProps);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     */
    private void fillEigentuemerListModel() {
        try {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "mauer_eigentuemer");
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
            query += "FROM " + MB_MC.getTableName() + ";";
            final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
            for (int i = 0; i < metaObjects.length; i++) {
                final CidsBean b = metaObjects[i].getBean();
                eigentuemerListModel.addElement(b);
            }
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "leo");
            final JScrollPane jsp = new JScrollPane(new MauernWindowSearch());
            DevelopmentTools.showTestFrame(jsp, 800, 1000);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void fillLastKlasseListModel() {
        try {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "mauer_lastklasse");
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
            query += "FROM " + MB_MC.getTableName() + ";";
            final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
            for (int i = 0; i < metaObjects.length; i++) {
                final CidsBean b = metaObjects[i].getBean();
                lastKlasseListModel.addElement(b);
            }
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG);
    }

    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        return getServerSearch();
    }

    @Override
    public void searchStarted() {
    }

    @Override
    public void searchDone(final int numberOfResults) {
    }

    @Override
    public void searchCanceled() {
    }

    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(MauernWindowSearch.class, "MauernWindowSearch.name");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultListModel getEigentuemerListModel() {
        return eigentuemerListModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  eigentuemerListModel  DOCUMENT ME!
     */
    public void setEigentuemerListModel(final DefaultListModel eigentuemerListModel) {
        this.eigentuemerListModel = eigentuemerListModel;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (MauernCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CheckboxCellRenderer implements ListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList jlist,
                final Object o,
                final int i,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final JPanel pnl = new JPanel();
            pnl.setLayout(new BorderLayout());
            final JCheckBox cb = new JCheckBox();
            cb.setSelected(isSelected);
            cb.setOpaque(false);
//            cb.setBorder(new EmptyBorder(new Insets(1, 5, 1, 5)));
            pnl.add(cb, BorderLayout.WEST);
            pnl.add(new JLabel(o.toString()), BorderLayout.CENTER);
            pnl.setOpaque(false);
            pnl.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
            return pnl;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ListToggleSelectionModel extends DefaultListSelectionModel {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setSelectionInterval(final int index0, final int index1) {
            if (index0 == index1) {
                if (isSelectedIndex(index0)) {
                    removeSelectionInterval(index0, index0);
                    return;
                }
            }
            super.setSelectionInterval(index0, index1);
        }

        @Override
        public void addSelectionInterval(final int index0, final int index1) {
            if (index0 == index1) {
                if (isSelectedIndex(index0)) {
                    removeSelectionInterval(index0, index0);
                    return;
                }
                super.addSelectionInterval(index0, index1);
            }
        }
    }
}
