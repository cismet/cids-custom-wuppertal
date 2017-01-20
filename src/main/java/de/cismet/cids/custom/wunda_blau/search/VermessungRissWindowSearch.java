/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;

import de.cismet.cids.custom.objecteditors.utils.VermessungRissUtils;
import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungFlurstueckSelectionDialog;
import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungRissEditor;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.VermessungFlurstueckFinder;
import de.cismet.cids.custom.wunda_blau.search.server.CidsVermessungRissArtSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsVermessungRissSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.tools.gui.StaticSwingTools;

import static javax.swing.Action.NAME;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class VermessungRissWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungRissWindowSearch.class);
    private static final String ACTION_TAG = "custom.vermessungsriss.windowsearch@WUNDA_BLAU";
    private static final String VERAENDERUNGSART_ALL_CODE = " ";
    private static Collection<CidsBean> veraenderungsarts = new LinkedList<CidsBean>();

    //~ Instance fields --------------------------------------------------------

    private boolean geoSearchEnabled;
    private MetaClass metaClass;
    private ImageIcon icon;
    private MappingComponent mappingComponent;
    private SearchControlPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private VermessungFlurstueckSelectionDialog flurstueckDialog;
    private DefaultListModel flurstuecksvermessungFilterModel = null;
//    private ImageIcon icoPluginRectangle;
//    private ImageIcon icoPluginPolygon;
//    private ImageIcon icoPluginEllipse;
//    private ImageIcon icoPluginPolyline;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddFlurstueck;
    private javax.swing.JButton btnFilterSchluessel505To508;
    private javax.swing.JButton btnFilterSchluesselAll;
    private javax.swing.JButton btnNewSearch;
    private javax.swing.JButton btnRemoveFlurstueck;
    private javax.swing.JCheckBox chkFilterSchluessel501;
    private javax.swing.JCheckBox chkFilterSchluessel502;
    private javax.swing.JCheckBox chkFilterSchluessel503;
    private javax.swing.JCheckBox chkFilterSchluessel504;
    private javax.swing.JCheckBox chkFilterSchluessel505;
    private javax.swing.JCheckBox chkFilterSchluessel506;
    private javax.swing.JCheckBox chkFilterSchluessel507;
    private javax.swing.JCheckBox chkFilterSchluessel508;
    private javax.swing.JCheckBox chkFilterSchluessel600;
    private javax.swing.JCheckBox chkSearchInCismap;
    private javax.swing.JLabel lblBlatt;
    private javax.swing.JLabel lblFilterRissWildcardPercent;
    private javax.swing.JLabel lblFilterRissWildcardUnderline;
    private javax.swing.JLabel lblFilterRissWildcards;
    private javax.swing.JLabel lblFlur;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JList lstFlurstuecke;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFilterFlurstuecke;
    private javax.swing.JPanel pnlFilterRiss;
    private javax.swing.JPanel pnlFilterRissWildcards;
    private javax.swing.JPanel pnlFilterSchluessel;
    private javax.swing.JPanel pnlFilterSchluesselControls;
    private javax.swing.JPopupMenu popChangeVeraenderungsart;
    private javax.swing.JScrollPane scpFlurstuecke;
    private javax.swing.JTextField txtBlatt;
    private javax.swing.JTextField txtFlur;
    private javax.swing.JTextField txtGemarkung;
    private javax.swing.JTextField txtSchluessel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VermessungRissWindowSearch.
     */
    public VermessungRissWindowSearch() {
        try {
            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "vermessung_riss");

            if (metaClass != null) {
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

//                icoPluginRectangle = new ImageIcon(getClass().getResource("/images/pluginSearchRectangle.png"));
//                icoPluginPolygon = new ImageIcon(getClass().getResource("/images/pluginSearchPolygon.png"));
//                icoPluginEllipse = new ImageIcon(getClass().getResource("/images/pluginSearchEllipse.png"));
//                icoPluginPolyline = new ImageIcon(getClass().getResource("/images/pluginSearchPolyline.png"));

                initComponents();

                new CidsBeanDropTarget((DropAwareJList)lstFlurstuecke);

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

                if (geoSearchEnabled) {
                    final VermessungRissCreateSearchGeometryListener vermessungRissCreateSearchGeometryListener =
                        new VermessungRissCreateSearchGeometryListener(
                            mappingComponent,
                            new VermessungRissSearchTooltip(icon));
                    vermessungRissCreateSearchGeometryListener.addPropertyChangeListener(this);

                    pnlButtons.add(Box.createHorizontalStrut(5));

                    btnGeoSearch = new GeoSearchButton(
                            VermessungRissCreateSearchGeometryListener.VERMESSUNGRISS_CREATE_SEARCH_GEOMETRY,
                            mappingComponent,
                            null);
                    btnGeoSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                            VermessungRissWindowSearch.class,
                            "VermessungRissWindowSearch.btnGeoSearch.toolTipText"));
                    pnlButtons.add(btnGeoSearch);
                } else {
                    chkSearchInCismap.setVisible(false);
                }

                flurstuecksvermessungFilterModel = new DefaultListModel();
                lstFlurstuecke.setModel(flurstuecksvermessungFilterModel);

                flurstueckDialog = new VermessungFlurstueckSelectionDialog(false) {

                        @Override
                        public void okHook() {
                            flurstuecksvermessungFilterModel.clear();

                            for (final CidsBean flurstuecksvermessung : getCurrentListToAdd()) {
                                flurstuecksvermessungFilterModel.addElement(flurstuecksvermessung);
                            }
                        }
                    };

                flurstueckDialog.pack();

                // Initialize the popup menu to change the veraenderungsart. Since the set of available veraenderungsart
                // is very unlikely to change, we once load it and save it in a static Collection.
                if ((veraenderungsarts == null) || veraenderungsarts.isEmpty()) {
                    final Collection result;
                    try {
                        result = SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(),
                                            new CidsVermessungRissArtSearchStatement(
                                                SessionManager.getSession().getUser()));
                    } catch (final ConnectionException ex) {
                        LOG.warn(
                            "Could not fetch veranederungsart entries. Editing flurstuecksvermessung will not work.",
                            ex);
                        // TODO: USer feedback?
                        return;
                    }

                    for (final Object veraenderungsart : result) {
                        veraenderungsarts.add(((MetaObject)veraenderungsart).getBean());
                    }
                }

                // create the additional Veraenderungsart 'All', which has an empty code and is the neutral element
                // for the search.
                final CidsBean veraenderungsartAll = CidsBean.createNewCidsBeanFromTableName(
                        "WUNDA_BLAU",
                        VermessungFlurstueckFinder.VERMESSUNG_VERAENDERUNGSART_TABLE_NAME);
                veraenderungsartAll.setProperty(
                    VermessungFlurstueckFinder.VERMESSUNG_VERAENDERUNGSART_CODE,
                    VERAENDERUNGSART_ALL_CODE);
                final String text = org.openide.util.NbBundle.getMessage(
                        VermessungRissWindowSearch.class,
                        "VermessungRissWindowSearch.veraenderungsart_all.name");
                veraenderungsartAll.setProperty(VermessungFlurstueckFinder.VERMESSUNG_VERAENDERUNGSART_NAME, text);
                popChangeVeraenderungsart.add(new ChangeVeraenderungsartAction(veraenderungsartAll));

                for (final CidsBean veraenderungsart : veraenderungsarts) {
                    popChangeVeraenderungsart.add(new VermessungRissWindowSearch.ChangeVeraenderungsartAction(
                            veraenderungsart));
                }
            }
        } catch (Throwable e) {
            LOG.warn("Error in Constructor of VermessungsRissWindowSearch. Search will not work properly.", e);
            geoSearchEnabled = false;
            metaClass = null;
            mappingComponent = null;
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

        popChangeVeraenderungsart = new javax.swing.JPopupMenu();
        pnlFilterRiss = new javax.swing.JPanel();
        lblSchluessel = new javax.swing.JLabel();
        lblGemarkung = new javax.swing.JLabel();
        lblFlur = new javax.swing.JLabel();
        lblBlatt = new javax.swing.JLabel();
        txtSchluessel = new javax.swing.JTextField();
        txtGemarkung = new javax.swing.JTextField();
        txtFlur = new javax.swing.JTextField();
        txtBlatt = new javax.swing.JTextField();
        pnlFilterRissWildcards = new javax.swing.JPanel();
        lblFilterRissWildcards = new javax.swing.JLabel();
        lblFilterRissWildcardPercent = new javax.swing.JLabel();
        lblFilterRissWildcardUnderline = new javax.swing.JLabel();
        pnlFilterSchluessel = new javax.swing.JPanel();
        chkFilterSchluessel501 = new javax.swing.JCheckBox();
        chkFilterSchluessel502 = new javax.swing.JCheckBox();
        chkFilterSchluessel503 = new javax.swing.JCheckBox();
        chkFilterSchluessel504 = new javax.swing.JCheckBox();
        chkFilterSchluessel505 = new javax.swing.JCheckBox();
        chkFilterSchluessel506 = new javax.swing.JCheckBox();
        chkFilterSchluessel507 = new javax.swing.JCheckBox();
        chkFilterSchluessel508 = new javax.swing.JCheckBox();
        chkFilterSchluessel600 = new javax.swing.JCheckBox();
        pnlFilterSchluesselControls = new javax.swing.JPanel();
        btnFilterSchluesselAll = new javax.swing.JButton();
        btnFilterSchluessel505To508 = new javax.swing.JButton();
        pnlFilterFlurstuecke = new javax.swing.JPanel();
        scpFlurstuecke = new javax.swing.JScrollPane();
        lstFlurstuecke = new DropAwareJList();
        btnAddFlurstueck = new javax.swing.JButton();
        btnRemoveFlurstueck = new javax.swing.JButton();
        chkSearchInCismap = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        btnNewSearch = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        pnlFilterRiss.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VermessungRissWindowSearch.class,
                    "VermessungRissWindowSearch.pnlFilterRiss.border.title"))); // NOI18N
        pnlFilterRiss.setLayout(new java.awt.GridBagLayout());

        lblSchluessel.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblSchluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(lblSchluessel, gridBagConstraints);

        lblGemarkung.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblGemarkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(lblGemarkung, gridBagConstraints);

        lblFlur.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblFlur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(lblFlur, gridBagConstraints);

        lblBlatt.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblBlatt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(lblBlatt, gridBagConstraints);

        txtSchluessel.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.txtSchluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(txtSchluessel, gridBagConstraints);

        txtGemarkung.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.txtGemarkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(txtGemarkung, gridBagConstraints);

        txtFlur.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.txtFlur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(txtFlur, gridBagConstraints);

        txtBlatt.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.txtBlatt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRiss.add(txtBlatt, gridBagConstraints);

        pnlFilterRissWildcards.setLayout(new java.awt.GridBagLayout());

        lblFilterRissWildcards.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblFilterRissWildcards.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterRissWildcards.add(lblFilterRissWildcards, gridBagConstraints);

        lblFilterRissWildcardPercent.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblFilterRissWildcardPercent.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        pnlFilterRissWildcards.add(lblFilterRissWildcardPercent, gridBagConstraints);

        lblFilterRissWildcardUnderline.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.lblFilterRissWildcardUnderline.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
        pnlFilterRissWildcards.add(lblFilterRissWildcardUnderline, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlFilterRiss.add(pnlFilterRissWildcards, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterRiss, gridBagConstraints);

        pnlFilterSchluessel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VermessungRissWindowSearch.class,
                    "VermessungRissWindowSearch.pnlFilterSchluessel.border.title"))); // NOI18N
        pnlFilterSchluessel.setLayout(new java.awt.GridBagLayout());

        chkFilterSchluessel501.setSelected(true);
        chkFilterSchluessel501.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel501.text"));        // NOI18N
        chkFilterSchluessel501.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel501.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlFilterSchluessel.add(chkFilterSchluessel501, gridBagConstraints);

        chkFilterSchluessel502.setSelected(true);
        chkFilterSchluessel502.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel502.text"));        // NOI18N
        chkFilterSchluessel502.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel502.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlFilterSchluessel.add(chkFilterSchluessel502, gridBagConstraints);

        chkFilterSchluessel503.setSelected(true);
        chkFilterSchluessel503.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel503.text"));        // NOI18N
        chkFilterSchluessel503.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel503.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlFilterSchluessel.add(chkFilterSchluessel503, gridBagConstraints);

        chkFilterSchluessel504.setSelected(true);
        chkFilterSchluessel504.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel504.text"));        // NOI18N
        chkFilterSchluessel504.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel504.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlFilterSchluessel.add(chkFilterSchluessel504, gridBagConstraints);

        chkFilterSchluessel505.setSelected(true);
        chkFilterSchluessel505.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel505.text"));        // NOI18N
        chkFilterSchluessel505.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel505.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnlFilterSchluessel.add(chkFilterSchluessel505, gridBagConstraints);

        chkFilterSchluessel506.setSelected(true);
        chkFilterSchluessel506.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel506.text"));        // NOI18N
        chkFilterSchluessel506.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel506.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFilterSchluessel.add(chkFilterSchluessel506, gridBagConstraints);

        chkFilterSchluessel507.setSelected(true);
        chkFilterSchluessel507.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel507.text"));        // NOI18N
        chkFilterSchluessel507.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel507.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlFilterSchluessel.add(chkFilterSchluessel507, gridBagConstraints);

        chkFilterSchluessel508.setSelected(true);
        chkFilterSchluessel508.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel508.text"));        // NOI18N
        chkFilterSchluessel508.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel508.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlFilterSchluessel.add(chkFilterSchluessel508, gridBagConstraints);

        chkFilterSchluessel600.setSelected(true);
        chkFilterSchluessel600.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel600.text"));        // NOI18N
        chkFilterSchluessel600.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkFilterSchluessel600.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlFilterSchluessel.add(chkFilterSchluessel600, gridBagConstraints);

        btnFilterSchluesselAll.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnFilterSchluesselAll.text")); // NOI18N
        btnFilterSchluesselAll.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFilterSchluesselAllActionPerformed(evt);
                }
            });
        pnlFilterSchluesselControls.add(btnFilterSchluesselAll);

        btnFilterSchluessel505To508.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnFilterSchluessel505To508.text")); // NOI18N
        btnFilterSchluessel505To508.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFilterSchluessel505To508ActionPerformed(evt);
                }
            });
        pnlFilterSchluesselControls.add(btnFilterSchluessel505To508);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlFilterSchluessel.add(pnlFilterSchluesselControls, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterSchluessel, gridBagConstraints);

        pnlFilterFlurstuecke.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VermessungRissWindowSearch.class,
                    "VermessungRissWindowSearch.pnlFilterFlurstuecke.border.title"))); // NOI18N
        pnlFilterFlurstuecke.setLayout(new java.awt.GridBagLayout());

        scpFlurstuecke.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpFlurstuecke.setMinimumSize(new java.awt.Dimension(266, 138));
        scpFlurstuecke.setOpaque(false);

        lstFlurstuecke.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckeMouseClicked(evt);
                }
                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckeMousePressed(evt);
                }
                @Override
                public void mouseReleased(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckeMouseReleased(evt);
                }
            });
        lstFlurstuecke.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFlurstueckeValueChanged(evt);
                }
            });
        scpFlurstuecke.setViewportView(lstFlurstuecke);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 0.1;
        pnlFilterFlurstuecke.add(scpFlurstuecke, gridBagConstraints);

        btnAddFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddFlurstueck.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnAddFlurstueck.text"));                                          // NOI18N
        btnAddFlurstueck.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnAddFlurstueck.toolTipText"));                                   // NOI18N
        btnAddFlurstueck.setFocusPainted(false);
        btnAddFlurstueck.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddFlurstueck.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddFlurstueck.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 2);
        pnlFilterFlurstuecke.add(btnAddFlurstueck, gridBagConstraints);

        btnRemoveFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveFlurstueck.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnRemoveFlurstueck.text"));                                          // NOI18N
        btnRemoveFlurstueck.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnRemoveFlurstueck.toolTipText"));                                   // NOI18N
        btnRemoveFlurstueck.setEnabled(false);
        btnRemoveFlurstueck.setFocusPainted(false);
        btnRemoveFlurstueck.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveFlurstueck.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveFlurstueck.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 10, 10);
        pnlFilterFlurstuecke.add(btnRemoveFlurstueck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterFlurstuecke, gridBagConstraints);

        chkSearchInCismap.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.chkSearchInCismap.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(chkSearchInCismap, gridBagConstraints);

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlButtons, gridBagConstraints);

        btnNewSearch.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnNewSearch.text"));        // NOI18N
        btnNewSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissWindowSearch.class,
                "VermessungRissWindowSearch.btnNewSearch.toolTipText")); // NOI18N
        btnNewSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNewSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(btnNewSearch, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFilterSchluesselAllActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFilterSchluesselAllActionPerformed
        chkFilterSchluessel501.setSelected(true);
        chkFilterSchluessel502.setSelected(true);
        chkFilterSchluessel503.setSelected(true);
        chkFilterSchluessel504.setSelected(true);
        chkFilterSchluessel505.setSelected(true);
        chkFilterSchluessel506.setSelected(true);
        chkFilterSchluessel507.setSelected(true);
        chkFilterSchluessel508.setSelected(true);
        chkFilterSchluessel600.setSelected(true);
    }                                                                                          //GEN-LAST:event_btnFilterSchluesselAllActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFlurstueckeValueChanged
        if (!evt.getValueIsAdjusting()) {
            btnRemoveFlurstueck.setEnabled(lstFlurstuecke.getSelectedIndex() > -1);
        }
    }                                                                                         //GEN-LAST:event_lstFlurstueckeValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddFlurstueckActionPerformed
        final List<CidsBean> result = new ArrayList<CidsBean>(1);

        for (final Object flurstuecksvermessung : flurstuecksvermessungFilterModel.toArray()) {
            result.add((CidsBean)flurstuecksvermessung);
        }

        Collections.sort(result, AlphanumComparator.getInstance());

        flurstueckDialog.setCurrentListToAdd(result);

        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this),
            flurstueckDialog,
            true);
    } //GEN-LAST:event_btnAddFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveFlurstueckActionPerformed
        final Object[] selection = lstFlurstuecke.getSelectedValues();
        for (final Object flurstueck : selection) {
            flurstuecksvermessungFilterModel.removeElement(flurstueck);
        }
    }                                                                                       //GEN-LAST:event_btnRemoveFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFilterSchluessel505To508ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFilterSchluessel505To508ActionPerformed
        chkFilterSchluessel501.setSelected(false);
        chkFilterSchluessel502.setSelected(false);
        chkFilterSchluessel503.setSelected(false);
        chkFilterSchluessel504.setSelected(false);
        chkFilterSchluessel505.setSelected(true);
        chkFilterSchluessel506.setSelected(true);
        chkFilterSchluessel507.setSelected(true);
        chkFilterSchluessel508.setSelected(true);
        chkFilterSchluessel600.setSelected(false);
    }                                                                                               //GEN-LAST:event_btnFilterSchluessel505To508ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckeMouseClicked
    }                                                                              //GEN-LAST:event_lstFlurstueckeMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckeMousePressed
        if (popChangeVeraenderungsart.isPopupTrigger(evt)) {
            final int indexUnderMouse = lstFlurstuecke.locationToIndex(evt.getPoint());

            int[] selection = lstFlurstuecke.getSelectedIndices();

            boolean selectValueUnderMouse = true;
            if ((selection != null) && (selection.length > 0)) {
                for (final int index : selection) {
                    if (index == indexUnderMouse) {
                        selectValueUnderMouse = false;
                    }
                }
            }

            if (selectValueUnderMouse) {
                lstFlurstuecke.setSelectedIndex(lstFlurstuecke.locationToIndex(evt.getPoint()));
                selection = lstFlurstuecke.getSelectedIndices();
            }

            if ((selection != null) && (selection.length > 0)) {
                popChangeVeraenderungsart.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    } //GEN-LAST:event_lstFlurstueckeMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeMouseReleased(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckeMouseReleased
        // Hock for popup menu. The return value of JPopupMenu.isPopupTrigger() depends on the OS.
        lstFlurstueckeMousePressed(evt);
    } //GEN-LAST:event_lstFlurstueckeMouseReleased

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNewSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNewSearchActionPerformed
        txtBlatt.setText("");
        txtFlur.setText("");
        txtGemarkung.setText("");
        txtSchluessel.setText("");

        chkFilterSchluessel501.setSelected(true);
        chkFilterSchluessel502.setSelected(true);
        chkFilterSchluessel503.setSelected(true);
        chkFilterSchluessel504.setSelected(true);
        chkFilterSchluessel505.setSelected(true);
        chkFilterSchluessel506.setSelected(true);
        chkFilterSchluessel507.setSelected(true);
        chkFilterSchluessel508.setSelected(true);
        chkFilterSchluessel600.setSelected(true);

        chkSearchInCismap.setSelected(false);

        flurstuecksvermessungFilterModel.clear();
    } //GEN-LAST:event_btnNewSearchActionPerformed

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (VermessungRissCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search);
            }
        }
    }

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
        final String schluessel = txtSchluessel.getText();
        final String gemarkung = txtGemarkung.getText();
        final String flur = txtFlur.getText();
        final String blatt = txtBlatt.getText();
        final Collection<String> schluesselCollection = new LinkedList<String>();

        if (chkFilterSchluessel501.isSelected()) {
            schluesselCollection.add("501");
        }
        if (chkFilterSchluessel502.isSelected()) {
            schluesselCollection.add("502");
        }
        if (chkFilterSchluessel503.isSelected()) {
            schluesselCollection.add("503");
        }
        if (chkFilterSchluessel504.isSelected()) {
            schluesselCollection.add("504");
        }
        if (chkFilterSchluessel505.isSelected()) {
            schluesselCollection.add("505");
        }
        if (chkFilterSchluessel506.isSelected()) {
            schluesselCollection.add("506");
        }
        if (chkFilterSchluessel507.isSelected()) {
            schluesselCollection.add("507");
        }
        if (chkFilterSchluessel508.isSelected()) {
            schluesselCollection.add("508");
        }
        if (chkFilterSchluessel600.isSelected()) {
            schluesselCollection.add("600");
        }

        final Collection<Map<String, String>> flurstuecke = new LinkedList<Map<String, String>>();

        for (int i = 0; i < flurstuecksvermessungFilterModel.size(); i++) {
            final CidsBean flurstuecksvermessungBean = (CidsBean)flurstuecksvermessungFilterModel.getElementAt(i);
            final Map<String, String> flurstueckMap = new HashMap<String, String>();

            try {
                if (flurstuecksvermessungBean.getProperty("veraenderungsart") instanceof CidsBean) {
                    flurstueckMap.put(
                        CidsVermessungRissSearchStatement.FLURSTUECK_VERAENDERUNGSART,
                        flurstuecksvermessungBean.getProperty("veraenderungsart.id").toString());
                }

                final CidsBean flurstueckBean = (CidsBean)flurstuecksvermessungBean.getProperty("flurstueck");

                if ("VERMESSUNG_FLURSTUECK_KICKER".equalsIgnoreCase(
                                flurstueckBean.getMetaObject().getMetaClass().getTableName())) {
                    if (flurstueckBean.getProperty("gemarkung") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_GEMARKUNG,
                            flurstueckBean.getProperty("gemarkung.id").toString());
                    }
                    if (flurstueckBean.getProperty("flur") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_FLUR,
                            flurstueckBean.getProperty("flur").toString());
                    }
                    if (flurstueckBean.getProperty("zaehler") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_ZAEHLER,
                            flurstueckBean.getProperty("zaehler").toString());
                    }
                    if (flurstueckBean.getProperty("nenner") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_NENNER,
                            flurstueckBean.getProperty("nenner").toString());
                    }
                } else if ("FLURSTUECK".equalsIgnoreCase(
                                flurstueckBean.getMetaObject().getMetaClass().getTableName())) {
                    if (flurstueckBean.getProperty("gemarkungs_nr") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_GEMARKUNG,
                            flurstueckBean.getProperty("gemarkungs_nr").toString());
                    }
                    if (flurstueckBean.getProperty("flur") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_FLUR,
                            flurstueckBean.getProperty("flur").toString());
                    }
                    if (flurstueckBean.getProperty("fstnr_z") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_ZAEHLER,
                            flurstueckBean.getProperty("fstnr_z").toString());
                    }
                    if (flurstueckBean.getProperty("fstnr_n") != null) {
                        flurstueckMap.put(
                            CidsVermessungRissSearchStatement.FLURSTUECK_NENNER,
                            flurstueckBean.getProperty("fstnr_n").toString());
                    }
                }

                flurstuecke.add(flurstueckMap);
            } catch (Exception ex) {
                LOG.error("Can not parse information from Flurstueck bean: " + flurstuecksvermessungBean, ex);
            }
        }

        Geometry geometryToSearchFor = null;
        if (geometry != null) {
            geometryToSearchFor = geometry;
        } else {
            if (chkSearchInCismap.isSelected()) {
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

        return new CidsVermessungRissSearchStatement(
                schluessel,
                gemarkung,
                flur,
                blatt,
                schluesselCollection,
                transformedBoundingBox,
                flurstuecke);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(VermessungRissWindowSearch.class, "VermessungRissWindowSearch.name");
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
    public void searchDone(final int result) {
    }

    @Override
    public void searchCanceled() {
    }

    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ChangeVeraenderungsartAction extends AbstractAction {

        //~ Instance fields ----------------------------------------------------

        private final CidsBean veraenderungsart;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ChangeVeraenderungsartAction object.
         *
         * @param  veraenderungsart  DOCUMENT ME!
         */
        public ChangeVeraenderungsartAction(final CidsBean veraenderungsart) {
            this.veraenderungsart = veraenderungsart;
            final String veranderungsArtCode = (String)this.veraenderungsart.getProperty("code");

            if (veranderungsArtCode.equals(VERAENDERUNGSART_ALL_CODE)) {
                putValue(
                    NAME,
                    this.veraenderungsart.getProperty("name"));
            } else {
                putValue(
                    NAME,
                    veranderungsArtCode
                            + " - "
                            + this.veraenderungsart.getProperty("name"));
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            for (final Object flurstuecksvermessung : lstFlurstuecke.getSelectedValues()) {
                try {
                    final String veraenderungsArtCode = (String)veraenderungsart.getProperty(
                            VermessungFlurstueckFinder.VERMESSUNG_VERAENDERUNGSART_CODE);
                    // was the Veränderungsart 'All' choosen
                    if (veraenderungsArtCode.equals(VERAENDERUNGSART_ALL_CODE)) {
                        ((CidsBean)flurstuecksvermessung).setProperty("veraenderungsart", null);
                    } else {
                        ((CidsBean)flurstuecksvermessung).setProperty("veraenderungsart", veraenderungsart);
                    }
                    lstFlurstuecke.clearSelection();
                    lstFlurstuecke.revalidate();
                    lstFlurstuecke.repaint();
                } catch (final Exception ex) {
                    LOG.info("Couldn't set veraenderungsart to '" + veraenderungsart + "' for flurstuecksvermessung '"
                                + flurstuecksvermessung + "'.",
                        ex);
                    // TODO: User feedback?
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DropAwareJList extends JList implements CidsBeanDropListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DropAwareJList object.
         */
        public DropAwareJList() {
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  dataModel  DOCUMENT ME!
         */
        public DropAwareJList(final ListModel dataModel) {
            super(dataModel);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Object[] listData) {
            super(listData);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Vector listData) {
            super(listData);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            try {
                // final List<CidsBean> landparcels = cidsBean.getBeanCollectionProperty("flurstuecksvermessung");
                for (final CidsBean dropped : beans) {
                    final CidsBean newEntry = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "vermessung_flurstuecksvermessung");
                    newEntry.setProperty("veraenderungsart", null);
                    newEntry.setProperty("tmp_lp_orig", dropped);
                    VermessungRissUtils.setFluerstueckKickerInVermessung(newEntry);
                    flurstuecksvermessungFilterModel.addElement(newEntry);
                }
            } catch (Exception ex) {
                LOG.error("Problem when adding the DroppedBeans", ex);
            }
        }
    }
}
