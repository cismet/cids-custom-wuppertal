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
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsTreppenSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsTreppenSearchStatement.FilterKey;
import de.cismet.cids.custom.wunda_blau.search.server.CidsTreppenSearchStatement.SearchMode;

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
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class TreppenWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppenWindowSearch.class);
    // End of variables declaration
    private static final String ACTION_TAG = "custom.treppen.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private ImageIcon icon;
    private JPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbMapSearch;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefVon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEntwaesserung;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblHandlaeufe;
    private javax.swing.JLabel lblLeitelemente;
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
    private javax.swing.JLabel lblPodeste;
    private javax.swing.JLabel lblPruefTil;
    private javax.swing.JLabel lblPruefVon;
    private javax.swing.JLabel lblStuetzmauern;
    private javax.swing.JLabel lblTreppenlaeufe;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlNoten;
    private javax.swing.JPanel pnlPruefung;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchMode;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbOne;
    private javax.swing.JTextField tfEntwaesserungBis;
    private javax.swing.JTextField tfEntwaesserungVon;
    private javax.swing.JTextField tfHandlaeufeBis;
    private javax.swing.JTextField tfHandlaeufeVon;
    private javax.swing.JTextField tfLeitelementeBis;
    private javax.swing.JTextField tfLeitelementeVon;
    private javax.swing.JTextField tfPodesteBis;
    private javax.swing.JTextField tfPodesteVon;
    private javax.swing.JTextField tfStuetzmauernBis;
    private javax.swing.JTextField tfStuetzmauernVon;
    private javax.swing.JTextField tfTreppenlaeufeBis;
    private javax.swing.JTextField tfTreppenlaeufeVon;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TreppenWindowSearch.
     */
    public TreppenWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();
            // todo just for debug
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
            pnlButtons.add(pnlSearchCancel);

            metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "treppe", getConnectionContext());

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
                final TreppenCreateSearchGeometryListener treppenSearchGeometryListener =
                    new TreppenCreateSearchGeometryListener(mappingComponent,
                        new TreppenSearchTooltip(icon));
                treppenSearchGeometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        TreppenCreateSearchGeometryListener.TREPPEN_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            TreppenWindowSearch.class,
                            "TreppenWindowSearch.btnGeoSearch.toolTipText"));
                pnlButtons.add(btnGeoSearch);
            }
        } catch (final Throwable e) {
            LOG.warn("Error in Constructor of TreppenWindowSearch. Search will not work properly.", e);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlNoten = new javax.swing.JPanel();
        tfPodesteVon = new javax.swing.JTextField();
        lblNotenBis = new javax.swing.JLabel();
        tfPodesteBis = new javax.swing.JTextField();
        lblFiller2 = new javax.swing.JLabel();
        lblTreppenlaeufe = new javax.swing.JLabel();
        lblPodeste = new javax.swing.JLabel();
        lblLeitelemente = new javax.swing.JLabel();
        lblHandlaeufe = new javax.swing.JLabel();
        lblEntwaesserung = new javax.swing.JLabel();
        lblStuetzmauern = new javax.swing.JLabel();
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
        tfLeitelementeVon = new javax.swing.JTextField();
        tfHandlaeufeVon = new javax.swing.JTextField();
        tfEntwaesserungVon = new javax.swing.JTextField();
        tfStuetzmauernVon = new javax.swing.JTextField();
        tfTreppenlaeufeVon = new javax.swing.JTextField();
        tfLeitelementeBis = new javax.swing.JTextField();
        tfHandlaeufeBis = new javax.swing.JTextField();
        tfEntwaesserungBis = new javax.swing.JTextField();
        tfStuetzmauernBis = new javax.swing.JTextField();
        tfTreppenlaeufeBis = new javax.swing.JTextField();
        pnlSearchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rbAll = new javax.swing.JRadioButton();
        rbOne = new javax.swing.JRadioButton();
        lblFiller5 = new javax.swing.JLabel();
        pnlPruefung = new javax.swing.JPanel();
        lblPruefVon = new javax.swing.JLabel();
        lblPruefTil = new javax.swing.JLabel();
        dcPruefVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcPruefBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblFiller = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        lblFiller6 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlNoten.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    TreppenWindowSearch.class,
                    "TreppenWindowSearch.pnlNoten.border.title"))); // NOI18N
        pnlNoten.setLayout(new java.awt.GridBagLayout());

        tfPodesteVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfPodesteVon.text")); // NOI18N
        tfPodesteVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfPodesteVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfPodesteVon, gridBagConstraints);

        lblNotenBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis, gridBagConstraints);

        tfPodesteBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfPodesteBis.text")); // NOI18N
        tfPodesteBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfPodesteBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfPodesteBis, gridBagConstraints);

        lblFiller2.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlNoten.add(lblFiller2, gridBagConstraints);

        lblTreppenlaeufe.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblTreppenlaeufe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblTreppenlaeufe, gridBagConstraints);

        lblPodeste.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblPodeste.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblPodeste, gridBagConstraints);

        lblLeitelemente.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblLeitelemente.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblLeitelemente, gridBagConstraints);

        lblHandlaeufe.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblHandlaeufe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblHandlaeufe, gridBagConstraints);

        lblEntwaesserung.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblEntwaesserung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblEntwaesserung, gridBagConstraints);

        lblStuetzmauern.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblStuetzmauern.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblStuetzmauern, gridBagConstraints);

        lblNotenFrom1.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom1, gridBagConstraints);

        lblNotenFrom2.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom2, gridBagConstraints);

        lblNotenFrom3.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom3, gridBagConstraints);

        lblNotenFrom4.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom4, gridBagConstraints);

        lblNotenFrom5.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom5, gridBagConstraints);

        lblNotenFrom6.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom6, gridBagConstraints);

        lblNotenBis1.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis1, gridBagConstraints);

        lblNotenBis2.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis2, gridBagConstraints);

        lblNotenBis3.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis3, gridBagConstraints);

        lblNotenBis4.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis4, gridBagConstraints);

        lblNotenBis5.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis5, gridBagConstraints);

        tfLeitelementeVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfLeitelementeVon.text")); // NOI18N
        tfLeitelementeVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfLeitelementeVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfLeitelementeVon, gridBagConstraints);

        tfHandlaeufeVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfHandlaeufeVon.text")); // NOI18N
        tfHandlaeufeVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHandlaeufeVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfHandlaeufeVon, gridBagConstraints);

        tfEntwaesserungVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfEntwaesserungVon.text")); // NOI18N
        tfEntwaesserungVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfEntwaesserungVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfEntwaesserungVon, gridBagConstraints);

        tfStuetzmauernVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfStuetzmauernVon.text")); // NOI18N
        tfStuetzmauernVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfStuetzmauernVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfStuetzmauernVon, gridBagConstraints);

        tfTreppenlaeufeVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfTreppenlaeufeVon.text")); // NOI18N
        tfTreppenlaeufeVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfTreppenlaeufeVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfTreppenlaeufeVon, gridBagConstraints);

        tfLeitelementeBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfLeitelementeBis.text")); // NOI18N
        tfLeitelementeBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfLeitelementeBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfLeitelementeBis, gridBagConstraints);

        tfHandlaeufeBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfHandlaeufeBis.text")); // NOI18N
        tfHandlaeufeBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHandlaeufeBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfHandlaeufeBis, gridBagConstraints);

        tfEntwaesserungBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfEntwaesserungBis.text")); // NOI18N
        tfEntwaesserungBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfEntwaesserungBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfEntwaesserungBis, gridBagConstraints);

        tfStuetzmauernBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfStuetzmauernBis.text")); // NOI18N
        tfStuetzmauernBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfStuetzmauernBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfStuetzmauernBis, gridBagConstraints);

        tfTreppenlaeufeBis.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.tfTreppenlaeufeBis.text")); // NOI18N
        tfTreppenlaeufeBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfTreppenlaeufeBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfTreppenlaeufeBis, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlNoten, gridBagConstraints);

        pnlSearchMode.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSearchMode.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.rbAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlSearchMode.add(rbAll, gridBagConstraints);

        buttonGroup1.add(rbOne);
        rbOne.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.rbOne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSearchMode.add(rbOne, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblFiller5.text")); // NOI18N
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

        pnlPruefung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    TreppenWindowSearch.class,
                    "TreppenWindowSearch.pnlPruefung.border.title"))); // NOI18N
        pnlPruefung.setLayout(new java.awt.GridBagLayout());

        lblPruefVon.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblPruefVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlPruefung.add(lblPruefVon, gridBagConstraints);

        lblPruefTil.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblPruefTil.text")); // NOI18N
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
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblFiller.text")); // NOI18N
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
                TreppenWindowSearch.class,
                "TreppenWindowSearch.cbMapSearch.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(cbMapSearch, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(
                TreppenWindowSearch.class,
                "TreppenWindowSearch.lblFiller6.text")); // NOI18N
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
        final SearchMode mode = rbAll.isSelected() ? SearchMode.AND_SEARCH : SearchMode.OR_SEARCH;

        final Geometry geometryToSearchFor;
        if (geometry != null) {
            geometryToSearchFor = geometry;
        } else {
            if (cbMapSearch.isSelected()) {
                geometryToSearchFor =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
            } else {
                geometryToSearchFor = null;
            }
        }
        final Geometry transformedBoundingBox;
        if (geometryToSearchFor != null) {
            transformedBoundingBox = CrsTransformer.transformToDefaultCrs(geometryToSearchFor);
            transformedBoundingBox.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedBoundingBox = null;
        }

        final HashMap<FilterKey, Object> filterProps = new FilterPropAppender().appendDate(
                    FilterKey.NAECHSTE_PRUEFUNG_VON,
                    dcPruefVon.getDate())
                    .appendDate(FilterKey.NAECHSTE_PRUEFUNG_BIS, dcPruefBis.getDate())
                    .appendDouble(FilterKey.ZUSTAND_TREPPENLAEUFE_VON, tfTreppenlaeufeVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_TREPPENLAEUFE_BIS, tfTreppenlaeufeBis.getText())
                    .appendDouble(FilterKey.ZUSTAND_PODESTE_VON, tfPodesteVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_PODESTE_BIS, tfPodesteBis.getText())
                    .appendDouble(FilterKey.ZUSTAND_LEITELEMENTE_VON, tfLeitelementeVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_LEITELEMENTE_BIS, tfLeitelementeBis.getText())
                    .appendDouble(FilterKey.ZUSTAND_HANDLAEUFE_VON, tfHandlaeufeVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_HANDLAEUFE_BIS, tfHandlaeufeBis.getText())
                    .appendDouble(FilterKey.ZUSTAND_ENTWAESSERUNG_VON, tfEntwaesserungVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_ENTWAESSERUNG_BIS, tfEntwaesserungBis.getText())
                    .appendDouble(FilterKey.ZUSTAND_STUETZMAUERN_VON, tfStuetzmauernVon.getText())
                    .appendDouble(FilterKey.ZUSTAND_STUETZMAUERN_BIS, tfStuetzmauernBis.getText())
                    .getFilterProps();

        return new CidsTreppenSearchStatement(
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

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
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
        return NbBundle.getMessage(TreppenWindowSearch.class, "TreppenWindowSearch.name");
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (TreppenCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FilterPropAppender {

        //~ Instance fields ----------------------------------------------------

        private final HashMap<FilterKey, Object> filterProps = new HashMap<>();

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   key   DOCUMENT ME!
         * @param   text  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public FilterPropAppender appendDouble(final FilterKey key, final String text) {
            if ((text != null) && !text.equals("")) {
                try {
                    final double value = Double.parseDouble(text.replace(',', '.'));
                    filterProps.put(key, value);
                } catch (NumberFormatException ex) {
                    LOG.warn("Could not read Double value from " + text, ex);
                }
            }
            return this;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   key    DOCUMENT ME!
         * @param   value  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public FilterPropAppender appendDate(final FilterKey key, final Date value) {
            if (value != null) {
                filterProps.put(key, value);
            }
            return this;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public HashMap<FilterKey, Object> getFilterProps() {
            return filterProps;
        }
    }
}
