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

import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.swingx.JXList;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils.RestrictionLevel;
import de.cismet.cids.custom.clientutils.StadtbilderUtils;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditor;
import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditorAddSuchwortDialog;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.MetaObjectNodesStadtbildSerieSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.MetaObjectNodesStadtbildSerieSearchStatement.Interval;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class Sb_StadtbildWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_StadtbildWindowSearch.class);
    private static final String ACTION_TAG = "custom.stadtbilder.search@WUNDA_BLAU";
    private static final Pattern SIMPLE_INTERVAL_PATTERN = Pattern.compile("(^\\d+$)|(^[A-Z]\\d+$)");
    private static final Pattern BILDNUMMER_PATTERN = Pattern.compile("^[A-Z]?\\d+[a-z]?$");
    private static final ArrayList<Integer> GREEN_NUTZUNGSEINSCHRAENKUNGEN = new ArrayList<Integer>();
    private static final ArrayList<Integer> YELLOW_NUTZUNGSEINSCHRAENKUNGEN = new ArrayList<Integer>();
    private static final ArrayList<Integer> RED_NUTZUNGSEINSCHRAENKUNGEN = new ArrayList<Integer>();

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private ImageIcon icon;
    private boolean geoSearchEnabled;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSuchwort;
    private javax.swing.JButton btnNewSearch;
    private javax.swing.JButton btnRemoveSuchwort;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.JComboBox cboOrt;
    private javax.swing.JComboBox cboStreet;
    private javax.swing.JCheckBox chbInternal;
    private javax.swing.JCheckBox chbInternalAndExternal;
    private javax.swing.JCheckBox chbNeitherInternalNorExternal;
    private javax.swing.JCheckBox chboBodennaheAufnahme;
    private javax.swing.JCheckBox chboLuftbildschraegaufnahme;
    private javax.swing.JCheckBox chboLuftbildsenkrechtaufnahme;
    private javax.swing.JCheckBox chboReihenschraegluftbilder;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblInternal;
    private javax.swing.JLabel lblInternalExternal;
    private javax.swing.JLabel lblNeitherInternalNorExternal;
    private javax.swing.JLabel lblOrtsname;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JList lstSuchworte;
    private javax.swing.JPanel pnlBildnummer;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCtrlButtons1;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlIntervall;
    private javax.swing.JPanel pnlKindOfImage;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchWords;
    private javax.swing.JPanel pnlStrassenzuordnung;
    private javax.swing.JRadioButton rbtnAllKeywords;
    private javax.swing.JRadioButton rbtnOneKeyword;
    private javax.swing.ButtonGroup rbtngKeywords;
    private de.cismet.cids.custom.wunda_blau.search.Sb_StadtbildTimeTabs sb_StadtbilderTimeTabs;
    private javax.swing.JTabbedPane tabBildnummern;
    private javax.swing.JTextField txtBildnummer;
    private javax.swing.JTextField txtHausnummer;
    private javax.swing.JTextField txtImageNrFrom;
    private javax.swing.JTextField txtImageNrTo;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_StadtbildWindowSearch.
     */
    public Sb_StadtbildWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();
            if (ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext())) {
                // do only if really needed because this is time consuming
                setModelForComboBoxes();
            }

            final JPanel pnlSearchCancel = new CountSearchResultsSearchControlPanel(this, getConnectionContext());
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

            final java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            pnlButtons.add(pnlSearchCancel, gridBagConstraints);

            metaClass = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "sb_stadtbildserie",
                    getConnectionContext()); // NOI18N

            byte[] iconDataFromMetaclass = new byte[] {};

            if (metaClass != null) {
                iconDataFromMetaclass = metaClass.getIconData();
            }

            if (iconDataFromMetaclass.length > 0) {
                LOG.info("Using icon from metaclass.");                                                              // NOI18N
                icon = new ImageIcon(metaClass.getIconData());
            } else {
                LOG.warn("Metaclass icon is not set. Trying to load default icon.");                                 // NOI18N
                final URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png"); // NOI18N

                if (urlToIcon != null) {
                    icon = new ImageIcon(urlToIcon);
                } else {
                    icon = new ImageIcon(new byte[] {});
                }
            }

            gridBagConstraints.gridx = 3;
            pnlButtons.add(Box.createHorizontalStrut(5), gridBagConstraints);

            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                final Sb_StadtbildserieCreateSearchGeometryListener stadtbildserieCreateSearchGeometryListener =
                    new Sb_StadtbildserieCreateSearchGeometryListener(
                        mappingComponent,
                        new Sb_StadtbildSearchTooltip(icon));
                stadtbildserieCreateSearchGeometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        Sb_StadtbildserieCreateSearchGeometryListener.STADTBILDSERIE_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            Sb_StadtbildWindowSearch.class,
                            "Sb_StadtbildWindowSearch.btnGeoSearch.toolTipText")); // NOI18N

                gridBagConstraints.gridx = 4;
                pnlButtons.add(btnGeoSearch, gridBagConstraints);
            }

            cboOrt.setSelectedItem(StadtbilderUtils.getWuppertal(getConnectionContext()));

            fetchAndClassifyNutzungseinschraenkungen();
        } catch (Throwable e) {
            LOG.warn("Error in Constructor of Sb_StadtbildWindowSearch. Search will not work properly.", e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rbtngKeywords = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlKindOfImage = new javax.swing.JPanel();
        chboLuftbildschraegaufnahme = new javax.swing.JCheckBox();
        chboLuftbildsenkrechtaufnahme = new javax.swing.JCheckBox();
        chboBodennaheAufnahme = new javax.swing.JCheckBox();
        chboReihenschraegluftbilder = new javax.swing.JCheckBox();
        tabBildnummern = new javax.swing.JTabbedPane();
        pnlBildnummer = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtBildnummer = new javax.swing.JTextField();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlIntervall = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        txtImageNrTo = new javax.swing.JTextField();
        txtImageNrFrom = new javax.swing.JTextField();
        pnlSearchWords = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSuchworte = new Sb_stadtbildserieEditor.JXListBugFixes();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlCtrlButtons1 = new javax.swing.JPanel();
        btnAddSuchwort = new javax.swing.JButton();
        btnRemoveSuchwort = new javax.swing.JButton();
        rbtnAllKeywords = new javax.swing.JRadioButton();
        rbtnOneKeyword = new javax.swing.JRadioButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        sb_StadtbilderTimeTabs = new de.cismet.cids.custom.wunda_blau.search.Sb_StadtbildTimeTabs(
                getConnectionContext());
        pnlStrassenzuordnung = new javax.swing.JPanel();
        lblStrasse = new javax.swing.JLabel();
        cboStreet = new javax.swing.JComboBox();
        lblOrtsname = new javax.swing.JLabel();
        cboOrt = new javax.swing.JComboBox();
        lblHausnummer = new javax.swing.JLabel();
        txtHausnummer = new javax.swing.JTextField();
        pnlFooter = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        btnNewSearch = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        chbInternalAndExternal = new javax.swing.JCheckBox();
        lblInternalExternal = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        chbInternal = new javax.swing.JCheckBox();
        lblInternal = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        chbNeitherInternalNorExternal = new javax.swing.JCheckBox();
        lblNeitherInternalNorExternal = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlKindOfImage.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlKindOfImage.border.title"))); // NOI18N
        pnlKindOfImage.setLayout(new java.awt.GridBagLayout());

        chboLuftbildschraegaufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildschraegaufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboLuftbildschraegaufnahme.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlKindOfImage.add(chboLuftbildschraegaufnahme, gridBagConstraints);

        chboLuftbildsenkrechtaufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildsenkrechtaufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboLuftbildsenkrechtaufnahme.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlKindOfImage.add(chboLuftbildsenkrechtaufnahme, gridBagConstraints);

        chboBodennaheAufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboBodennaheAufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboBodennaheAufnahme.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnlKindOfImage.add(chboBodennaheAufnahme, gridBagConstraints);

        chboReihenschraegluftbilder.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboReihenschraegluftbilder,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboReihenschraegluftbilder.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlKindOfImage.add(chboReihenschraegluftbilder, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlKindOfImage, gridBagConstraints);

        pnlBildnummer.setPreferredSize(new java.awt.Dimension(238, 40));
        pnlBildnummer.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 7, 5);
        pnlBildnummer.add(jLabel3, gridBagConstraints);

        txtBildnummer.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtBildnummer.text"));        // NOI18N
        txtBildnummer.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtBildnummer.toolTipText")); // NOI18N
        txtBildnummer.setMinimumSize(new java.awt.Dimension(4, 23));
        txtBildnummer.setPreferredSize(new java.awt.Dimension(240, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 20);
        pnlBildnummer.add(txtBildnummer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlBildnummer.add(filler6, gridBagConstraints);

        tabBildnummern.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.pnlBildnummer.TabConstraints.tabTitle"),
            pnlBildnummer); // NOI18N

        pnlIntervall.setPreferredSize(new java.awt.Dimension(351, 40));
        pnlIntervall.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 7, 5);
        pnlIntervall.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 5);
        pnlIntervall.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlIntervall.add(filler2, gridBagConstraints);

        txtImageNrTo.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtImageNrTo.text")); // NOI18N
        txtImageNrTo.setMinimumSize(new java.awt.Dimension(120, 19));
        txtImageNrTo.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 20);
        pnlIntervall.add(txtImageNrTo, gridBagConstraints);

        txtImageNrFrom.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtImageNrFrom.text")); // NOI18N
        txtImageNrFrom.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 20);
        pnlIntervall.add(txtImageNrFrom, gridBagConstraints);

        tabBildnummern.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.pnlIntervall.TabConstraints.tabTitle"),
            pnlIntervall); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(tabBildnummern, gridBagConstraints);

        pnlSearchWords.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlSearchWords.border.title"))); // NOI18N
        pnlSearchWords.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(259, 131));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(259, 132));

        lstSuchworte.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(lstSuchworte);
        ((JXList)lstSuchworte).setAutoCreateRowSorter(true);
        ((JXList)lstSuchworte).setSortOrder(SortOrder.ASCENDING);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        pnlSearchWords.add(jScrollPane2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlSearchWords.add(filler3, gridBagConstraints);

        pnlCtrlButtons1.setOpaque(false);
        pnlCtrlButtons1.setLayout(new java.awt.GridBagLayout());

        btnAddSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnAddSuchwort,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnAddSuchwort.text"));                                              // NOI18N
        btnAddSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnAddSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        pnlCtrlButtons1.add(btnAddSuchwort, gridBagConstraints);

        btnRemoveSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnRemoveSuchwort,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnRemoveSuchwort.text"));                                              // NOI18N
        btnRemoveSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnRemoveSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 5, 10);
        pnlCtrlButtons1.add(btnRemoveSuchwort, gridBagConstraints);

        rbtngKeywords.add(rbtnAllKeywords);
        rbtnAllKeywords.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbtnAllKeywords,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.rbtnAllKeywords.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCtrlButtons1.add(rbtnAllKeywords, gridBagConstraints);

        rbtngKeywords.add(rbtnOneKeyword);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbtnOneKeyword,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.rbtnOneKeyword.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlCtrlButtons1.add(rbtnOneKeyword, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        pnlCtrlButtons1.add(filler7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlSearchWords.add(pnlCtrlButtons1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlSearchWords, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 5, 22);
        pnlScrollPane.add(sb_StadtbilderTimeTabs, gridBagConstraints);

        pnlStrassenzuordnung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlStrassenzuordnung.border.title"))); // NOI18N
        pnlStrassenzuordnung.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrasse,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(lblStrasse, gridBagConstraints);

        cboStreet.setEditable(true);
        cboStreet.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    cboStreetItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(cboStreet, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblOrtsname,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblOrtsname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(lblOrtsname, gridBagConstraints);

        cboOrt.setEditable(true);
        cboOrt.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    cboOrtItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(cboOrt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHausnummer,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblHausnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(lblHausnummer, gridBagConstraints);

        txtHausnummer.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtHausnummer.text"));        // NOI18N
        txtHausnummer.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtHausnummer.toolTipText")); // NOI18N
        txtHausnummer.setPreferredSize(new java.awt.Dimension(56, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(txtHausnummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlStrassenzuordnung, gridBagConstraints);

        pnlFooter.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            cbMapSearch,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.cbMapSearch.text")); // NOI18N
        cbMapSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbMapSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 5, 0);
        pnlFooter.add(cbMapSearch, gridBagConstraints);

        pnlButtons.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnNewSearch,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnNewSearch.text"));        // NOI18N
        btnNewSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnNewSearch.toolTipText")); // NOI18N
        btnNewSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNewSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        pnlButtons.add(btnNewSearch, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlButtons.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        pnlFooter.add(pnlButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFooter.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlFooter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(filler1, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.jPanel1.border.title"))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        chbInternalAndExternal.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chbInternalAndExternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chbInternalAndExternal.text")); // NOI18N
        jPanel2.add(chbInternalAndExternal, new java.awt.GridBagConstraints());

        lblInternalExternal.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_green.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInternalExternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblInternalExternal.text"));                                         // NOI18N
        lblInternalExternal.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblInternalExternal.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblInternalExternalMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(lblInternalExternal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chbInternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chbInternal.text")); // NOI18N
        jPanel3.add(chbInternal, new java.awt.GridBagConstraints());

        lblInternal.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_yellow.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblInternal.text"));                                                  // NOI18N
        lblInternal.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblInternal.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblInternalMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lblInternal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chbNeitherInternalNorExternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chbNeitherInternalNorExternal.text")); // NOI18N
        jPanel4.add(chbNeitherInternalNorExternal, new java.awt.GridBagConstraints());

        lblNeitherInternalNorExternal.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_red.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNeitherInternalNorExternal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblNeitherInternalNorExternal.text"));                             // NOI18N
        lblNeitherInternalNorExternal.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNeitherInternalNorExternal.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblNeitherInternalNorExternalMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(lblNeitherInternalNorExternal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel1.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(jPanel1, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbMapSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbMapSearchActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbMapSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddSuchwortActionPerformed
        final DefaultListModel dlm = (DefaultListModel)lstSuchworte.getModel();
        for (final CidsBean newSuchwort : Sb_stadtbildserieEditorAddSuchwortDialog.getInstance().showDialog()) {
            if (newSuchwort != null) {
                dlm.addElement(newSuchwort);
            }
        }
    }                                                                                  //GEN-LAST:event_btnAddSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveSuchwortActionPerformed
        final DefaultListModel dlm = (DefaultListModel)lstSuchworte.getModel();

        if (this.lstSuchworte.getSelectedIndices().length > 0) {
            final int selectedIndex = lstSuchworte.getSelectedIndex();
            final int[] selectedIndices = lstSuchworte.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(((JXList)lstSuchworte).convertIndexToModel(selectedIndices[i]));
            }

            final int listSize = lstSuchworte.getModel().getSize();
            if (listSize > 0) {
                if (selectedIndex < listSize) {
                    lstSuchworte.setSelectedIndex(selectedIndex);
                } else {
                    lstSuchworte.setSelectedIndex(listSize - 1);
                }
            }
        }
    } //GEN-LAST:event_btnRemoveSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboOrtItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_cboOrtItemStateChanged
        Object selectedItem = cboOrt.getSelectedItem();
        if (selectedItem instanceof LightweightMetaObject) {
            selectedItem = ((LightweightMetaObject)selectedItem).getBean();
            checkIfPlaceInsideWuppertal((CidsBean)selectedItem);
            RendererTools.showNormalState(cboOrt);
        } else if (selectedItem instanceof CidsBean) {
            checkIfPlaceInsideWuppertal((CidsBean)selectedItem);
            RendererTools.showNormalState(cboOrt);
        } else if (selectedItem == null) {
            checkIfPlaceInsideWuppertal(null);
            RendererTools.showNormalState(cboOrt);
        } else {
            checkIfPlaceInsideWuppertal(null);
            RendererTools.showErrorState(cboOrt);
        }
    }                                                                         //GEN-LAST:event_cboOrtItemStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNewSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNewSearchActionPerformed
        chboBodennaheAufnahme.setSelected(true);
        chboLuftbildschraegaufnahme.setSelected(true);
        chboLuftbildsenkrechtaufnahme.setSelected(true);
        chboReihenschraegluftbilder.setSelected(true);
        txtImageNrFrom.setText("");
        txtImageNrTo.setText("");
        final DefaultListModel dlm = (DefaultListModel)lstSuchworte.getModel();
        dlm.clear();
        sb_StadtbilderTimeTabs.clear();
        cboStreet.setSelectedItem(null);
        cboOrt.setSelectedItem(StadtbilderUtils.getWuppertal(getConnectionContext()));
        txtHausnummer.setText("");
        cbMapSearch.setSelected(false);
        chbInternalAndExternal.setSelected(true);
        chbInternal.setSelected(false);
        chbNeitherInternalNorExternal.setSelected(false);
    }                                                                                //GEN-LAST:event_btnNewSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboStreetItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_cboStreetItemStateChanged
        final Object selectedItem = cboStreet.getSelectedItem();
        if ((selectedItem == null) || (selectedItem instanceof CidsBean) || (selectedItem instanceof MetaObject)) {
            RendererTools.showNormalState(cboStreet);
        } else {
            RendererTools.showErrorState(cboStreet);
        }
    }                                                                            //GEN-LAST:event_cboStreetItemStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblInternalExternalMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblInternalExternalMouseClicked
        chbInternalAndExternal.setSelected(!chbInternalAndExternal.isSelected());
    }                                                                                   //GEN-LAST:event_lblInternalExternalMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblInternalMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblInternalMouseClicked
        chbInternal.setSelected(!chbInternal.isSelected());
    }                                                                           //GEN-LAST:event_lblInternalMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblNeitherInternalNorExternalMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblNeitherInternalNorExternalMouseClicked
        chbNeitherInternalNorExternal.setSelected(!chbNeitherInternalNorExternal.isSelected());
    }                                                                                             //GEN-LAST:event_lblNeitherInternalNorExternalMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  place  DOCUMENT ME!
     */
    private void checkIfPlaceInsideWuppertal(final CidsBean place) {
        if ((place != null) && place.equals(StadtbilderUtils.getWuppertal(getConnectionContext()))) {
            // inside of Wuppertal
            cboStreet.setEnabled(true);
            lblStrasse.setEnabled(true);
            txtHausnummer.setEnabled(true);
            lblHausnummer.setEnabled(true);
        } else {
            // outside of Wuppertal
            cboStreet.setEnabled(false);
            cboStreet.setSelectedItem(null);
            lblStrasse.setEnabled(false);
            txtHausnummer.setEnabled(false);
            txtHausnummer.setText("");
            lblHausnummer.setEnabled(false);
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
                "WUNDA_BLAU",      // NOI18N
                "Administratoren", // NOI18N
                "admin",           // NOI18N
                "kif");            // NOI18N
            final JScrollPane jsp = new JScrollPane(new Sb_StadtbildWindowSearch());
            DevelopmentTools.showTestFrame(jsp, 800, 1000);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setModelForComboBoxes() {
        StadtbilderUtils.setModelForComboBoxesAndDecorateIt(cboStreet, "STRASSE", getConnectionContext());
        StadtbilderUtils.setModelForComboBoxesAndDecorateIt(cboOrt, "SB_ORT", getConnectionContext());
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

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    public MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        final MetaObjectNodesStadtbildSerieSearchStatement stadtbildSerieSearchStatement =
            new MetaObjectNodesStadtbildSerieSearchStatement(SessionManager.getSession().getUser());

        try {
            setBildnummerInSearch(stadtbildSerieSearchStatement);
        } catch (NotAValidIntervalException ex) {
            showErrorDialog(NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.bildnummer.title"),
                NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.bildnummer.message"));
            return null;
        }

        final ArrayList<MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp> bildtypList = new ArrayList<>();
        if (chboBodennaheAufnahme.isSelected()) {
            bildtypList.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.BODENNAH);
        }
        if (chboLuftbildschraegaufnahme.isSelected()) {
            bildtypList.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.LUFTSCHRAEG);
        }
        if (chboLuftbildsenkrechtaufnahme.isSelected()) {
            bildtypList.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.LUFTSENK);
        }
        if (chboReihenschraegluftbilder.isSelected()) {
            bildtypList.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.REIHENSCHRAEG);
        }
        stadtbildSerieSearchStatement.setBildtypen(bildtypList);

        final ArrayList<Integer> nutzungseinschraenkungenIDs = new ArrayList<>();
        if (chbInternalAndExternal.isSelected()) {
            nutzungseinschraenkungenIDs.addAll(GREEN_NUTZUNGSEINSCHRAENKUNGEN);
        }
        if (chbInternal.isSelected()) {
            nutzungseinschraenkungenIDs.addAll(YELLOW_NUTZUNGSEINSCHRAENKUNGEN);
        }
        if (chbNeitherInternalNorExternal.isSelected()) {
            nutzungseinschraenkungenIDs.addAll(RED_NUTZUNGSEINSCHRAENKUNGEN);
        }
        stadtbildSerieSearchStatement.setNutzungseinschraenkungIDs(nutzungseinschraenkungenIDs);

        final ArrayList<Integer> suchwortIDs = new ArrayList<Integer>();
        for (final Object object : ((DefaultListModel<CidsBean>)lstSuchworte.getModel()).toArray()) {
            final Integer id = ((CidsBean)object).getPrimaryKeyValue();
            suchwortIDs.add(id);
        }
        stadtbildSerieSearchStatement.setSuchwoerterIDs(suchwortIDs);

        if (rbtnAllKeywords.isSelected()) {
            stadtbildSerieSearchStatement.setHasAllSuchworte(true);
        } else {
            stadtbildSerieSearchStatement.setHasAllSuchworte(false);
        }

        final Date[] fromDate_tillDate = sb_StadtbilderTimeTabs.chooseDates();
        stadtbildSerieSearchStatement.setFrom(fromDate_tillDate[0]);
        stadtbildSerieSearchStatement.setTill(fromDate_tillDate[1]);

        CidsBean strasse = null;
        final Object selectedStreet = cboStreet.getSelectedItem();
        // the street must be a CidsBean, LightweightMetaObject or null
        if (selectedStreet instanceof CidsBean) {
            strasse = (CidsBean)selectedStreet;
        } else if (selectedStreet instanceof LightweightMetaObject) {
            strasse = ((LightweightMetaObject)selectedStreet).getBean();
        } else if (selectedStreet != null) {
            showErrorDialog(NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.strasse.title"),
                NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.strasse.message"));
            return null;
        }
        if (strasse != null) {
            stadtbildSerieSearchStatement.setStreetID(strasse.getPrimaryKeyValue().toString());
        }

        CidsBean ort = null;
        final Object selectOrt = cboOrt.getSelectedItem();
        // the ort must be a CidsBean, LightweightMetaObject or null
        if (selectOrt instanceof CidsBean) {
            ort = (CidsBean)selectOrt;
        } else if (selectOrt instanceof LightweightMetaObject) {
            ort = ((LightweightMetaObject)selectOrt).getBean();
        } else if (selectOrt != null) {
            showErrorDialog(NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.ort.title"),
                NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.getServerSearch().dialog.ort.message"));
            return null;
        }

        if (ort != null) {
            stadtbildSerieSearchStatement.setOrtID(ort.getPrimaryKeyValue().toString());
        }

        final String hausnummer = txtHausnummer.getText().trim();
        stadtbildSerieSearchStatement.setHausnummer(hausnummer);

        // Geometry
        Geometry geometryToSearchFor = null;
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
        stadtbildSerieSearchStatement.setGeometryToSearchFor(transformedBoundingBox);

        return stadtbildSerieSearchStatement;
    }

    /**
     * Fetch all Einschraenkungen from SB_NUTZUNGSEINSCHRAENKUNG and classify them, thus add them to one of the three
     * lists e.g. {@code GREEN_NUTZUNGSEINSCHRAENKUNGEN}.
     */
    private void fetchAndClassifyNutzungseinschraenkungen() {
        try {
            final MetaClass mc = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "SB_NUTZUNGSEINSCHRAENKUNG",
                    getConnectionContext());
            final User user = SessionManager.getSession().getUser();
            final String query = "select " + mc.getID() + "," + mc.getPrimaryKey() + " from " + mc.getTableName();
            final MetaObject[] einschraenkungenMo = SessionManager.getProxy()
                        .getMetaObjectByQuery(user, query, "WUNDA_BLAU", getConnectionContext());
            final ArrayList<CidsBean> einschraenkungen = new ArrayList<CidsBean>(einschraenkungenMo.length);
            for (final MetaObject mo : einschraenkungenMo) {
                einschraenkungen.add(mo.getBean());
            }

            for (final CidsBean einschraenkung : einschraenkungen) {
                final RestrictionLevel level = Sb_RestrictionLevelUtils
                            .determineRestrictionLevelForNutzungseinschraenkung(
                                einschraenkung,
                                getConnectionContext());
                if (level.isInternalUsageAllowed()) {
                    if (level.isExternalUsageAllowed()) {
                        GREEN_NUTZUNGSEINSCHRAENKUNGEN.add(einschraenkung.getPrimaryKeyValue());
                    } else {
                        YELLOW_NUTZUNGSEINSCHRAENKUNGEN.add(einschraenkung.getPrimaryKeyValue());
                    }
                } else {
                    RED_NUTZUNGSEINSCHRAENKUNGEN.add(einschraenkung.getPrimaryKeyValue());
                }
            }
        } catch (ConnectionException ex) {
            LOG.error("Could not fetch and classify Nutzungseinschraenkungen", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title    DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    private void showErrorDialog(final String title, final String message) {
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Sets the bildnummern in a object of MetaObjectNodesStadtbildSerieSearchStatement.
     *
     * @param   stadtbildSerieSearchStatement  DOCUMENT ME!
     *
     * @throws  NotAValidIntervalException  DOCUMENT ME!
     */
    private void setBildnummerInSearch(final MetaObjectNodesStadtbildSerieSearchStatement stadtbildSerieSearchStatement)
            throws NotAValidIntervalException {
        if (tabBildnummern.getSelectedComponent().equals(pnlBildnummer)) {
            final String imageNr = txtBildnummer.getText().trim();
            if (StringUtils.isNotBlank(imageNr)) {
                stadtbildSerieSearchStatement.setImageNumberRule(imageNr);
            }
        } else {
            final String imageNrFrom = txtImageNrFrom.getText().trim();
            final String imageNrTo = txtImageNrTo.getText().trim();
            if (StringUtils.isNotBlank(imageNrFrom) && StringUtils.isNotBlank(imageNrTo)) {
                final Interval interval = getIntervalForSearch(imageNrFrom, imageNrTo);
                stadtbildSerieSearchStatement.setInterval(interval);
            }
        }
    }

    /**
     * Generates an Interval object for an interval like 4711-4712, N4711-N4712, 4711-4712c or N4711-N4712c.
     *
     * @param   imageNrFrom  the begin of the interval
     * @param   imageNrTo    the end of the interval
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotAValidIntervalException  DOCUMENT ME!
     *
     * @see     Sb_StadtbildWindowSearchTest
     */
    Interval getIntervalForSearch(
            String imageNrFrom,
            String imageNrTo) throws NotAValidIntervalException {
        if (Character.isLetter(imageNrFrom.charAt(0)) && (imageNrFrom.charAt(0) != imageNrTo.charAt(0))) {
            // the first letter of the two numbers is different. e.g. N4711 - A4711
            throw new NotAValidIntervalException();
        }

        if (imageNrFrom.contains("%") || imageNrFrom.contains("_") || imageNrTo.contains("%")
                    || imageNrTo.contains("_")) {
            // the numbers should not contain any wildcards
            throw new NotAValidIntervalException();
        }

        if (!BILDNUMMER_PATTERN.matcher(imageNrFrom).matches() && !BILDNUMMER_PATTERN.matcher(imageNrTo).matches()) {
            // the numbers do not have the right format
            throw new NotAValidIntervalException();
        }

        final ArrayList<String> listWithNumbers = new ArrayList<String>();
        final int comparedTo = imageNrFrom.compareTo(imageNrTo);
        if (comparedTo > 0) {
            final String swap = imageNrFrom;
            imageNrFrom = imageNrTo;
            imageNrTo = swap;
        } else if (comparedTo == 0) {
            // both numers are the same
            listWithNumbers.add(imageNrTo);
            return new Interval(null, null, listWithNumbers);
        }

        // this are the cases e.g. 004711-004713 or N04711-N04713

        if (SIMPLE_INTERVAL_PATTERN.matcher(imageNrFrom).matches()
                    && SIMPLE_INTERVAL_PATTERN.matcher(imageNrTo).matches()) {
            if (imageNrFrom.length() != imageNrTo.length()) {
                // the two numbers must have the same length
                throw new NotAValidIntervalException();
            }
            return new Interval(imageNrFrom, imageNrTo);
        }

        char lastCharacter = imageNrFrom.charAt(imageNrFrom.length() - 1);
        char letterOfNrFrom;
        if (Character.isLetter(lastCharacter)) {
            // remove the last letter and save it
            imageNrFrom = imageNrFrom.substring(0, imageNrFrom.length() - 1);
            letterOfNrFrom = lastCharacter;
        } else {
            letterOfNrFrom = '\0';
        }

        lastCharacter = imageNrTo.charAt(imageNrTo.length() - 1);
        char letterOfNrTo;
        if (Character.isLetter(lastCharacter)) {
            // remove the last letter and save it
            imageNrTo = imageNrTo.substring(0, imageNrTo.length() - 1);
            letterOfNrTo = lastCharacter;
        } else {
            letterOfNrTo = '\0';
        }

        if (imageNrFrom.length() != imageNrTo.length()) {
            // the two numbers must have the same length
            throw new NotAValidIntervalException();
        }

        final String prefix = greatestCommonPrefix(imageNrFrom, imageNrTo);
        final int prefix_length = prefix.length();
        String simpleIntervalStart = null;
        String simpleIntervalEnd = null;

        if (prefix.equals(imageNrFrom)) {
            // both numbers have the same digits, only the last character was different
            // If the letters are not set yet, they have to be set artificially, thus an iteration is possible
            // this is the case e.g. 4711c-4711f
            char startLetter;
            char targetLetter;
            if (letterOfNrFrom == '\0') {
                listWithNumbers.add(prefix);
                startLetter = 'a';
            } else {
                startLetter = letterOfNrFrom;
            }
            if (letterOfNrTo == '\0') {
                LOG.error(
                    "The second entry has no last letter and the digits are the same e.g.: 004711c-004711. This should not happen, because of the alphanumerical sort in the beginning.");
                listWithNumbers.add(prefix);
                targetLetter = 'z';
            } else {
                targetLetter = letterOfNrTo;
            }
            for (int j = startLetter; j <= targetLetter; j++) {
                listWithNumbers.add(prefix + (char)j);
            }
        } else {
            // both numbers have different digits and the last character was different
            // e.g.: 004711a - 004713c
            final String begin_str = imageNrFrom.substring(prefix_length);
            final String end_str = imageNrTo.substring(prefix_length);
            final int begin = Integer.parseInt(begin_str);
            final int end = Integer.parseInt(end_str);

            if (begin > end) {
                LOG.error(
                    "The last number was bigger than the first number. This should not happen, because of the alphanumerical sort in the beginning.");
                throw new NotAValidIntervalException();
            }

            // the string format is needed to fill it with zeros, in case the both suffixes have a different length
            // e.g.: begin = 1 and end = 10, then later on the formatted value of begin needs to be "01"
            final String intToStringFormat = "%0" + end_str.length() + "d";

            char startLetter;
            if (letterOfNrFrom == '\0') {
                // the first number does not have a letter. Add it to the list and set the letter to 'a'
                // Thus an iteration over letters is possible
                listWithNumbers.add(prefix + String.format(intToStringFormat, begin));
                startLetter = 'a';
            } else {
                startLetter = letterOfNrFrom;
            }
            for (int j = startLetter; j <= 'z'; j++) {
                listWithNumbers.add(prefix + String.format(intToStringFormat, begin) + (char)j);
            }

            final int secondNumber = begin + 1;
            if (secondNumber < end) {
                simpleIntervalStart = prefix + String.format(intToStringFormat, secondNumber);
            }

            final int secondLastNumber = end - 1;
            if (secondLastNumber > begin) {
                simpleIntervalEnd = prefix + String.format(intToStringFormat, secondLastNumber);
            }

            listWithNumbers.add(prefix + String.format(intToStringFormat, end));
            if (letterOfNrTo != '\0') {
                for (int j = 'a'; j <= letterOfNrTo; j++) {
                    listWithNumbers.add(prefix + String.format(intToStringFormat, end) + (char)j);
                }
            }
        }
        return new Interval(simpleIntervalStart, simpleIntervalEnd, listWithNumbers);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   a  DOCUMENT ME!
     * @param   b  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String greatestCommonPrefix(final String a, final String b) {
        final int minLength = Math.min(a.length(), b.length());
        for (int i = 0; i < minLength; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, minLength);
    }

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
        return NbBundle.getMessage(Sb_StadtbildWindowSearch.class, "Sb_StadtbildWindowSearch.name");
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

    /**
     * DOCUMENT ME!
     */
    @Override
    public void searchStarted() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  numberOfResults  DOCUMENT ME!
     */
    @Override
    public void searchDone(final int numberOfResults) {
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
     * @param  evt  DOCUMENT ME!
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (Sb_StadtbildserieCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
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
    public class NotAValidIntervalException extends Exception {
    }

    /**
     * A subclass of SearchControlPanel, which checks first how many results (Stadtbildserien) were found.
     *
     * @version  $Revision$, $Date$
     */
    private class CountSearchResultsSearchControlPanel extends SearchControlPanel {

        //~ Instance fields ----------------------------------------------------

        boolean showResults;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CountSearchResultsSearchControlPanel object.
         *
         * @param  listener           DOCUMENT ME!
         * @param  connectionContext  DOCUMENT ME!
         */
        public CountSearchResultsSearchControlPanel(final SearchControlListener listener,
                final ConnectionContext connectionContext) {
            super(listener, connectionContext);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean checkIfSearchShouldBeStarted(final SwingWorker calledBySwingWorker,
                final MetaObjectNodeServerSearch search) {
            if (search instanceof MetaObjectNodesStadtbildSerieSearchStatement) {
                Integer amountResults = 0;
                try {
                    ((MetaObjectNodesStadtbildSerieSearchStatement)search).setPreparationExecution(true);
                    final Collection searchResult = SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(),
                                    search,
                                    getConnectionContext());
                    if (!searchResult.isEmpty()) {
                        final Object firstResult = searchResult.toArray()[0];
                        if (firstResult instanceof Integer) {
                            amountResults = (Integer)firstResult;
                        }
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                } finally {
                    ((MetaObjectNodesStadtbildSerieSearchStatement)search).setPreparationExecution(false);
                }

                if (amountResults < 100) {
                    return true;
                } else {
                    showResults = false;
                    try {
                        final int amountOfResults = amountResults;
                        if (!calledBySwingWorker.isCancelled()) {
                            SwingUtilities.invokeAndWait(new Runnable() {

                                    @Override
                                    public void run() {
                                        final int choosenOption = JOptionPane.showConfirmDialog(
                                                StaticSwingTools.getParentFrame(
                                                    CountSearchResultsSearchControlPanel.this),
                                                "Es wurden "
                                                        + amountOfResults
                                                        + " Stadtbildserien gefunden. Sollen diese angezeigt werden?",
                                                "Große Anzahl an Suchergebnissen",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE);
                                        showResults = choosenOption == JOptionPane.YES_OPTION;
                                    }
                                });
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Search results will not be shown.", ex);
                    } catch (InvocationTargetException ex) {
                        LOG.error("Search results will not be shown.", ex);
                    }
                    return showResults;
                }
            } else {
                return true;
            }
        }
    }
}
