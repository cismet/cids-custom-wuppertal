/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMeasurementPointSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMeasurementPointSearchStatement.GST;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMeasurementPointSearchStatement.Pointtype;

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
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class MeasurementPointWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MeasurementPointWindowSearch.class);
    private static final String ACTION_TAG = "custom.measurement.pointsearch@WUNDA_BLAU";
    private static final String ACTION_POINTTYPE_ALLE = "cmdAllePunkte";
    private static final String ACTION_POINTTYPE_ANSCHLUSS = "cmdAnschlussPunkte";
    private static final String ACTION_POINTTYPE_GRENZUNDGEBAEUDE = "cmdGrenzUndGebaeudePunkte";
    private static final String ACTION_POINTTYPE_GEBAEUDEUNDBAUWERK = "cmdGebaeudeUndBauwerksPunkte";
    private static final String ACTION_POINTTYPE_HOEHENFEST = "cmdHoehenfestPunkte";

    //~ Instance fields --------------------------------------------------------

    private boolean geoSearchEnabled;
    private MetaClass metaClass;
    private ImageIcon icon;
    private MappingComponent mappingComponent;
    private SearchControlPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrFilterGST;
    private javax.swing.JButton btnAllePunkte;
    private javax.swing.JButton btnAnschlusspunkte;
    private javax.swing.JButton btnGebaeudeUndBauwerkspunkte;
    private javax.swing.JButton btnGrenzUndGebaudepunkte;
    private javax.swing.JButton btnHoehenfestpunkte;
    private javax.swing.JButton btnNewSearch;
    private javax.swing.JCheckBox chkAufnahmepunkte;
    private javax.swing.JCheckBox chkBesondereBauwerkspunkte;
    private javax.swing.JCheckBox chkBesondereGebaeudepunkte;
    private javax.swing.JCheckBox chkBesondereTopographischePunkte;
    private javax.swing.JCheckBox chkGrenzpunkte;
    private javax.swing.JCheckBox chkNivellementPunkte;
    private javax.swing.JCheckBox chkSearchInCismap;
    private javax.swing.JCheckBox chkSonstigeVermessungspunkte;
    private javax.swing.Box.Filler gluFiller;
    private javax.swing.JLabel lblPointcode;
    private javax.swing.JLabel lblPointcodeWildcardPercent;
    private javax.swing.JLabel lblPointcodeWildcardUnderline;
    private javax.swing.JLabel lblPointcodeWildcards;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFilterGST;
    private javax.swing.JPanel pnlFilterPointcode;
    private javax.swing.JPanel pnlFilterPointtype;
    private javax.swing.JPanel pnlPointcodeWildcards;
    private javax.swing.JPanel pnlPointtypeButtons;
    private javax.swing.JPanel pnlPointtypeCheckboxes;
    private javax.swing.JRadioButton rdoFilterGSTAll;
    private javax.swing.JRadioButton rdoFilterGSTLE10;
    private javax.swing.JRadioButton rdoFilterGSTLE2;
    private javax.swing.JRadioButton rdoFilterGSTLE3;
    private javax.swing.JRadioButton rdoFilterGSTLE6;
    private javax.swing.JTextField txtPointcode;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AlkisPointWindowSearch.
     */
    public MeasurementPointWindowSearch() {
        try {
            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "ALKIS_POINT");
            final byte[] iconDataFromMetaclass = metaClass.getIconData();

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

            initComponents();

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
                final MeasurementPointCreateSearchGeometryListener measurementPointCreateSearchGeometryListener =
                    new MeasurementPointCreateSearchGeometryListener(
                        mappingComponent,
                        new MeasurementPointSearchTooltip(icon));
                measurementPointCreateSearchGeometryListener.addPropertyChangeListener(this);

                pnlButtons.add(Box.createHorizontalStrut(5));

                btnGeoSearch = new GeoSearchButton(
                        MeasurementPointCreateSearchGeometryListener.MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null);
                btnGeoSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                        MeasurementPointWindowSearch.class,
                        "MeasurementPointWindowSearch.btnGeoSearch.toolTipText"));
                pnlButtons.add(btnGeoSearch);
            } else {
                chkSearchInCismap.setVisible(false);
            }
        } catch (Exception e) {
            LOG.warn("Error in constructor of MeasurementPointWindowSearch", e);
            mappingComponent = null;
            geoSearchEnabled = false;
            metaClass = null;
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

        bgrFilterGST = new javax.swing.ButtonGroup();
        pnlFilterPointcode = new javax.swing.JPanel();
        lblPointcode = new javax.swing.JLabel();
        txtPointcode = new javax.swing.JTextField();
        pnlPointcodeWildcards = new javax.swing.JPanel();
        lblPointcodeWildcards = new javax.swing.JLabel();
        lblPointcodeWildcardPercent = new javax.swing.JLabel();
        lblPointcodeWildcardUnderline = new javax.swing.JLabel();
        pnlFilterPointtype = new javax.swing.JPanel();
        pnlPointtypeButtons = new javax.swing.JPanel();
        btnAllePunkte = new javax.swing.JButton();
        btnAnschlusspunkte = new javax.swing.JButton();
        btnGrenzUndGebaudepunkte = new javax.swing.JButton();
        btnGebaeudeUndBauwerkspunkte = new javax.swing.JButton();
        btnHoehenfestpunkte = new javax.swing.JButton();
        pnlPointtypeCheckboxes = new javax.swing.JPanel();
        chkAufnahmepunkte = new javax.swing.JCheckBox();
        chkSonstigeVermessungspunkte = new javax.swing.JCheckBox();
        chkGrenzpunkte = new javax.swing.JCheckBox();
        chkBesondereGebaeudepunkte = new javax.swing.JCheckBox();
        chkBesondereBauwerkspunkte = new javax.swing.JCheckBox();
        chkBesondereTopographischePunkte = new javax.swing.JCheckBox();
        chkNivellementPunkte = new javax.swing.JCheckBox();
        pnlFilterGST = new javax.swing.JPanel();
        rdoFilterGSTLE2 = new javax.swing.JRadioButton();
        rdoFilterGSTLE3 = new javax.swing.JRadioButton();
        rdoFilterGSTLE6 = new javax.swing.JRadioButton();
        rdoFilterGSTLE10 = new javax.swing.JRadioButton();
        rdoFilterGSTAll = new javax.swing.JRadioButton();
        chkSearchInCismap = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        gluFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        btnNewSearch = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        pnlFilterPointcode.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MeasurementPointWindowSearch.class,
                    "MeasurementPointWindowSearch.pnlFilterPointcode.border.title"))); // NOI18N
        pnlFilterPointcode.setLayout(new java.awt.GridBagLayout());

        lblPointcode.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.lblPointcode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterPointcode.add(lblPointcode, gridBagConstraints);

        txtPointcode.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.txtPointcode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilterPointcode.add(txtPointcode, gridBagConstraints);

        pnlPointcodeWildcards.setLayout(new java.awt.GridBagLayout());

        lblPointcodeWildcards.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.lblPointcodeWildcards.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPointcodeWildcards.add(lblPointcodeWildcards, gridBagConstraints);

        lblPointcodeWildcardPercent.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.lblPointcodeWildcardPercent.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        pnlPointcodeWildcards.add(lblPointcodeWildcardPercent, gridBagConstraints);

        lblPointcodeWildcardUnderline.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.lblPointcodeWildcardUnderline.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
        pnlPointcodeWildcards.add(lblPointcodeWildcardUnderline, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlFilterPointcode.add(pnlPointcodeWildcards, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterPointcode, gridBagConstraints);

        pnlFilterPointtype.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MeasurementPointWindowSearch.class,
                    "MeasurementPointWindowSearch.pnlFilterPointtype.border.title"))); // NOI18N
        pnlFilterPointtype.setLayout(new java.awt.GridBagLayout());

        pnlPointtypeButtons.setLayout(new java.awt.GridBagLayout());

        btnAllePunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnAllePunkte.text")); // NOI18N
        btnAllePunkte.setActionCommand(ACTION_POINTTYPE_ALLE);
        btnAllePunkte.setFocusPainted(false);
        btnAllePunkte.setMaximumSize(new java.awt.Dimension(85, 25));
        btnAllePunkte.setMinimumSize(new java.awt.Dimension(85, 25));
        btnAllePunkte.setPreferredSize(new java.awt.Dimension(85, 25));
        btnAllePunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAllePunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 4, 5);
        pnlPointtypeButtons.add(btnAllePunkte, gridBagConstraints);

        btnAnschlusspunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnAnschlusspunkte.text")); // NOI18N
        btnAnschlusspunkte.setActionCommand(ACTION_POINTTYPE_ANSCHLUSS);
        btnAnschlusspunkte.setFocusPainted(false);
        btnAnschlusspunkte.setMaximumSize(new java.awt.Dimension(112, 25));
        btnAnschlusspunkte.setMinimumSize(new java.awt.Dimension(112, 25));
        btnAnschlusspunkte.setPreferredSize(new java.awt.Dimension(112, 25));
        btnAnschlusspunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAnschlusspunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        pnlPointtypeButtons.add(btnAnschlusspunkte, gridBagConstraints);

        btnGrenzUndGebaudepunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnGrenzUndGebaudepunkte.text")); // NOI18N
        btnGrenzUndGebaudepunkte.setActionCommand(ACTION_POINTTYPE_GRENZUNDGEBAEUDE);
        btnGrenzUndGebaudepunkte.setFocusPainted(false);
        btnGrenzUndGebaudepunkte.setMaximumSize(new java.awt.Dimension(150, 25));
        btnGrenzUndGebaudepunkte.setMinimumSize(new java.awt.Dimension(150, 25));
        btnGrenzUndGebaudepunkte.setPreferredSize(new java.awt.Dimension(150, 25));
        btnGrenzUndGebaudepunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGrenzUndGebaudepunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        pnlPointtypeButtons.add(btnGrenzUndGebaudepunkte, gridBagConstraints);

        btnGebaeudeUndBauwerkspunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnGebaeudeUndBauwerkspunkte.text")); // NOI18N
        btnGebaeudeUndBauwerkspunkte.setActionCommand(ACTION_POINTTYPE_GEBAEUDEUNDBAUWERK);
        btnGebaeudeUndBauwerkspunkte.setFocusPainted(false);
        btnGebaeudeUndBauwerkspunkte.setMaximumSize(new java.awt.Dimension(168, 25));
        btnGebaeudeUndBauwerkspunkte.setMinimumSize(new java.awt.Dimension(168, 25));
        btnGebaeudeUndBauwerkspunkte.setPreferredSize(new java.awt.Dimension(168, 25));
        btnGebaeudeUndBauwerkspunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGebaeudeUndBauwerkspunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        pnlPointtypeButtons.add(btnGebaeudeUndBauwerkspunkte, gridBagConstraints);

        btnHoehenfestpunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnHoehenfestpunkte.text")); // NOI18N
        btnHoehenfestpunkte.setActionCommand(ACTION_POINTTYPE_HOEHENFEST);
        btnHoehenfestpunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHoehenfestpunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 5);
        pnlPointtypeButtons.add(btnHoehenfestpunkte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        pnlFilterPointtype.add(pnlPointtypeButtons, gridBagConstraints);

        pnlPointtypeCheckboxes.setLayout(new java.awt.GridBagLayout());

        chkAufnahmepunkte.setSelected(true);
        chkAufnahmepunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkAufnahmepunkte.text")); // NOI18N
        chkAufnahmepunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkAufnahmepunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkAufnahmepunkte, gridBagConstraints);

        chkSonstigeVermessungspunkte.setSelected(true);
        chkSonstigeVermessungspunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkSonstigeVermessungspunkte.text")); // NOI18N
        chkSonstigeVermessungspunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSonstigeVermessungspunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkSonstigeVermessungspunkte, gridBagConstraints);

        chkGrenzpunkte.setSelected(true);
        chkGrenzpunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkGrenzpunkte.text")); // NOI18N
        chkGrenzpunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkGrenzpunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkGrenzpunkte, gridBagConstraints);

        chkBesondereGebaeudepunkte.setSelected(true);
        chkBesondereGebaeudepunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkBesondereGebaeudepunkte.text")); // NOI18N
        chkBesondereGebaeudepunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkBesondereGebaeudepunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkBesondereGebaeudepunkte, gridBagConstraints);

        chkBesondereBauwerkspunkte.setSelected(true);
        chkBesondereBauwerkspunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkBesondereBauwerkspunkte.text")); // NOI18N
        chkBesondereBauwerkspunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkBesondereBauwerkspunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkBesondereBauwerkspunkte, gridBagConstraints);

        chkBesondereTopographischePunkte.setSelected(true);
        chkBesondereTopographischePunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkBesondereTopographischePunkte.text")); // NOI18N
        chkBesondereTopographischePunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkBesondereTopographischePunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkBesondereTopographischePunkte, gridBagConstraints);

        chkNivellementPunkte.setSelected(true);
        chkNivellementPunkte.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkNivellementPunkte.text")); // NOI18N
        chkNivellementPunkte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkNivellementPunkteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        pnlPointtypeCheckboxes.add(chkNivellementPunkte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlFilterPointtype.add(pnlPointtypeCheckboxes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterPointtype, gridBagConstraints);

        pnlFilterGST.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MeasurementPointWindowSearch.class,
                    "MeasurementPointWindowSearch.pnlFilterGST.border.title"))); // NOI18N
        pnlFilterGST.setLayout(new java.awt.GridBagLayout());

        bgrFilterGST.add(rdoFilterGSTLE2);
        rdoFilterGSTLE2.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.rdoFilterGSTLE2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        pnlFilterGST.add(rdoFilterGSTLE2, gridBagConstraints);

        bgrFilterGST.add(rdoFilterGSTLE3);
        rdoFilterGSTLE3.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.rdoFilterGSTLE3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlFilterGST.add(rdoFilterGSTLE3, gridBagConstraints);

        bgrFilterGST.add(rdoFilterGSTLE6);
        rdoFilterGSTLE6.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.rdoFilterGSTLE6.text")); // NOI18N
        rdoFilterGSTLE6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlFilterGST.add(rdoFilterGSTLE6, gridBagConstraints);

        bgrFilterGST.add(rdoFilterGSTLE10);
        rdoFilterGSTLE10.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.rdoFilterGSTLE10.text")); // NOI18N
        rdoFilterGSTLE10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlFilterGST.add(rdoFilterGSTLE10, gridBagConstraints);

        bgrFilterGST.add(rdoFilterGSTAll);
        rdoFilterGSTAll.setSelected(true);
        rdoFilterGSTAll.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.rdoFilterGSTAll.text")); // NOI18N
        rdoFilterGSTAll.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 5);
        pnlFilterGST.add(rdoFilterGSTAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlFilterGST, gridBagConstraints);

        chkSearchInCismap.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.chkSearchInCismap.text")); // NOI18N
        chkSearchInCismap.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkSearchInCismap, gridBagConstraints);

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        add(gluFiller, gridBagConstraints);

        btnNewSearch.setText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnNewSearch.text"));        // NOI18N
        btnNewSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                MeasurementPointWindowSearch.class,
                "MeasurementPointWindowSearch.btnNewSearch.toolTipText")); // NOI18N
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
    private void btnAllePunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAllePunkteActionPerformed
        changeFilterPointtype(evt.getActionCommand());
    }                                                                                 //GEN-LAST:event_btnAllePunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAnschlusspunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAnschlusspunkteActionPerformed
        changeFilterPointtype(evt.getActionCommand());
    }                                                                                      //GEN-LAST:event_btnAnschlusspunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGrenzUndGebaudepunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGrenzUndGebaudepunkteActionPerformed
        changeFilterPointtype(evt.getActionCommand());
    }                                                                                            //GEN-LAST:event_btnGrenzUndGebaudepunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGebaeudeUndBauwerkspunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGebaeudeUndBauwerkspunkteActionPerformed
        changeFilterPointtype(evt.getActionCommand());
    }                                                                                                //GEN-LAST:event_btnGebaeudeUndBauwerkspunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkAufnahmepunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkAufnahmepunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                     //GEN-LAST:event_chkAufnahmepunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSonstigeVermessungspunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkSonstigeVermessungspunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                                //GEN-LAST:event_chkSonstigeVermessungspunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkGrenzpunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkGrenzpunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                  //GEN-LAST:event_chkGrenzpunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkBesondereGebaeudepunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkBesondereGebaeudepunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                              //GEN-LAST:event_chkBesondereGebaeudepunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkBesondereBauwerkspunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkBesondereBauwerkspunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                              //GEN-LAST:event_chkBesondereBauwerkspunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkBesondereTopographischePunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkBesondereTopographischePunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                                    //GEN-LAST:event_chkBesondereTopographischePunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHoehenfestpunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHoehenfestpunkteActionPerformed
        changeFilterPointtype(evt.getActionCommand());
    }                                                                                       //GEN-LAST:event_btnHoehenfestpunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkNivellementPunkteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkNivellementPunkteActionPerformed
        changeEnabledStateOfSearchButtons();
    }                                                                                        //GEN-LAST:event_chkNivellementPunkteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNewSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNewSearchActionPerformed
        txtPointcode.setText("");
        chkAufnahmepunkte.setSelected(true);
        chkBesondereBauwerkspunkte.setSelected(true);
        chkBesondereGebaeudepunkte.setSelected(true);
        chkBesondereTopographischePunkte.setSelected(true);
        chkGrenzpunkte.setSelected(true);
        chkNivellementPunkte.setSelected(true);
        chkSonstigeVermessungspunkte.setSelected(true);

        chkSearchInCismap.setSelected(false);

        rdoFilterGSTAll.setSelected(true);
    } //GEN-LAST:event_btnNewSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  actionCommand  DOCUMENT ME!
     */
    protected void changeFilterPointtype(final String actionCommand) {
        if (ACTION_POINTTYPE_ALLE.equals(actionCommand)) {
            chkAufnahmepunkte.setSelected(true);
            chkSonstigeVermessungspunkte.setSelected(true);
            chkGrenzpunkte.setSelected(true);
            chkBesondereGebaeudepunkte.setSelected(true);
            chkBesondereBauwerkspunkte.setSelected(true);
            chkBesondereTopographischePunkte.setSelected(true);
            chkNivellementPunkte.setSelected(false);
        } else if (ACTION_POINTTYPE_ANSCHLUSS.equals(actionCommand)) {
            chkAufnahmepunkte.setSelected(true);
            chkSonstigeVermessungspunkte.setSelected(true);
            chkGrenzpunkte.setSelected(false);
            chkBesondereGebaeudepunkte.setSelected(false);
            chkBesondereBauwerkspunkte.setSelected(false);
            chkBesondereTopographischePunkte.setSelected(false);
            chkNivellementPunkte.setSelected(false);
        } else if (ACTION_POINTTYPE_GRENZUNDGEBAEUDE.equals(actionCommand)) {
            chkAufnahmepunkte.setSelected(false);
            chkSonstigeVermessungspunkte.setSelected(false);
            chkGrenzpunkte.setSelected(true);
            chkBesondereGebaeudepunkte.setSelected(true);
            chkBesondereBauwerkspunkte.setSelected(true);
            chkBesondereTopographischePunkte.setSelected(false);
            chkNivellementPunkte.setSelected(false);
        } else if (ACTION_POINTTYPE_GEBAEUDEUNDBAUWERK.equals(actionCommand)) {
            chkAufnahmepunkte.setSelected(false);
            chkSonstigeVermessungspunkte.setSelected(false);
            chkGrenzpunkte.setSelected(false);
            chkBesondereGebaeudepunkte.setSelected(true);
            chkBesondereBauwerkspunkte.setSelected(true);
            chkBesondereTopographischePunkte.setSelected(false);
            chkNivellementPunkte.setSelected(false);
        } else if (ACTION_POINTTYPE_HOEHENFEST.equals(actionCommand)) {
            chkAufnahmepunkte.setSelected(false);
            chkSonstigeVermessungspunkte.setSelected(false);
            chkGrenzpunkte.setSelected(false);
            chkBesondereGebaeudepunkte.setSelected(false);
            chkBesondereBauwerkspunkte.setSelected(false);
            chkBesondereTopographischePunkte.setSelected(false);
            chkNivellementPunkte.setSelected(true);
        }

        changeEnabledStateOfSearchButtons();
    }

    /**
     * DOCUMENT ME!
     */
    protected void changeEnabledStateOfSearchButtons() {
        boolean enableSearchButtons = false;

        enableSearchButtons |= chkAufnahmepunkte.isSelected();
        enableSearchButtons |= chkSonstigeVermessungspunkte.isSelected();
        enableSearchButtons |= chkGrenzpunkte.isSelected();
        enableSearchButtons |= chkBesondereGebaeudepunkte.isSelected();
        enableSearchButtons |= chkBesondereBauwerkspunkte.isSelected();
        enableSearchButtons |= chkBesondereTopographischePunkte.isSelected();
        enableSearchButtons |= chkNivellementPunkte.isSelected();

        pnlSearchCancel.setEnabled(enableSearchButtons);

        if (geoSearchEnabled) {
            btnGeoSearch.setEnabled(enableSearchButtons);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (MeasurementPointCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search);
            }
        }
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
        final Collection<Pointtype> pointtypes = new LinkedList<Pointtype>();

        if (chkAufnahmepunkte.isSelected()) {
            pointtypes.add(Pointtype.AUFNAHMEPUNKTE);
        }
        if (chkSonstigeVermessungspunkte.isSelected()) {
            pointtypes.add(Pointtype.SONSTIGE_VERMESSUNGSPUNKTE);
        }
        if (chkGrenzpunkte.isSelected()) {
            pointtypes.add(Pointtype.GRENZPUNKTE);
        }
        if (chkBesondereGebaeudepunkte.isSelected()) {
            pointtypes.add(Pointtype.BESONDERE_GEBAEUDEPUNKTE);
        }
        if (chkBesondereBauwerkspunkte.isSelected()) {
            pointtypes.add(Pointtype.BESONDERE_BAUWERKSPUNKTE);
        }
        if (chkBesondereTopographischePunkte.isSelected()) {
            pointtypes.add(Pointtype.BESONDERE_TOPOGRAPHISCHE_PUNKTE);
        }
        if (chkNivellementPunkte.isSelected()) {
            pointtypes.add(Pointtype.NIVELLEMENT_PUNKTE);
        }

        GST gst = null;
        if (rdoFilterGSTLE2.isSelected()) {
            gst = GST.LE2;
        } else if (rdoFilterGSTLE3.isSelected()) {
            gst = GST.LE3;
        } else if (rdoFilterGSTLE6.isSelected()) {
            gst = GST.LE6;
        } else if (rdoFilterGSTLE10.isSelected()) {
            gst = GST.LE10;
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

        return new CidsMeasurementPointSearchStatement(txtPointcode.getText(), pointtypes, gst, transformedBoundingBox);
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
        return NbBundle.getMessage(MeasurementPointWindowSearch.class, "MeasurementPointWindowSearch.name");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG);
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
     * @param  result  DOCUMENT ME!
     */
    @Override
    public void searchDone(final int result) {
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
}
