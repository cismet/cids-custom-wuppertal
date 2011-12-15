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
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.Node;
import Sirius.server.search.CidsServerSearch;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.FlurstueckSelectionDialoge;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class BaulastWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    CidsBeanDropListener,
    ActionTagProtected,
    SearchControlListener {

    //~ Static fields/initializers ---------------------------------------------

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaulastWindowSearch.class);

    //~ Instance fields --------------------------------------------------------

    private final MetaClass mc;
    private final ImageIcon icon;
    private final FlurstueckSelectionDialoge fsSelectionDialoge;
    private final DefaultListModel model;
    private SearchControlPanel pnlSearchCancel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddFS;
    private javax.swing.JButton btnAddFS1;
    private javax.swing.JButton btnAddFS2;
    private javax.swing.JButton btnAddFS3;
    private javax.swing.JButton btnFromMapFS;
    private javax.swing.JButton btnFromMapFS1;
    private javax.swing.JButton btnFromMapFS2;
    private javax.swing.JButton btnFromMapFS3;
    private javax.swing.JButton btnRemoveFS;
    private javax.swing.JButton btnRemoveFS1;
    private javax.swing.JButton btnRemoveFS2;
    private javax.swing.JButton btnRemoveFS3;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbArt;
    private javax.swing.JComboBox cbArt1;
    private javax.swing.JComboBox cbArt2;
    private javax.swing.JComboBox cbArt3;
    private javax.swing.JCheckBox chkBeguenstigt;
    private javax.swing.JCheckBox chkBeguenstigt1;
    private javax.swing.JCheckBox chkBeguenstigt2;
    private javax.swing.JCheckBox chkBeguenstigt3;
    private javax.swing.JCheckBox chkBelastet;
    private javax.swing.JCheckBox chkBelastet1;
    private javax.swing.JCheckBox chkBelastet2;
    private javax.swing.JCheckBox chkBelastet3;
    private javax.swing.JCheckBox chkGeloescht;
    private javax.swing.JCheckBox chkGeloescht1;
    private javax.swing.JCheckBox chkGeloescht2;
    private javax.swing.JCheckBox chkGeloescht3;
    private javax.swing.JCheckBox chkGueltig;
    private javax.swing.JCheckBox chkGueltig1;
    private javax.swing.JCheckBox chkGueltig2;
    private javax.swing.JCheckBox chkGueltig3;
    private javax.swing.JCheckBox chkKartenausschnitt;
    private javax.swing.JCheckBox chkKartenausschnitt1;
    private javax.swing.JCheckBox chkKartenausschnitt2;
    private javax.swing.JCheckBox chkKartenausschnitt3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList lstFlurstueck;
    private javax.swing.JList lstFlurstueck1;
    private javax.swing.JList lstFlurstueck2;
    private javax.swing.JList lstFlurstueck3;
    private javax.swing.JPanel panCommand;
    private javax.swing.JPanel panCommand1;
    private javax.swing.JPanel panCommand2;
    private javax.swing.JPanel panCommand3;
    private javax.swing.JPanel panSearch;
    private javax.swing.JPanel panSearch1;
    private javax.swing.JPanel panSearch2;
    private javax.swing.JPanel panSearch3;
    private javax.swing.JPanel pnlAttributeFilter;
    private javax.swing.JPanel pnlFlurstuecksfilter;
    private javax.swing.JPanel pnlSearchFor;
    private javax.swing.JRadioButton rbBaulastBlaetter;
    private javax.swing.JRadioButton rbBaulastBlaetter1;
    private javax.swing.JRadioButton rbBaulastBlaetter2;
    private javax.swing.JRadioButton rbBaulastBlaetter3;
    private javax.swing.JRadioButton rbBaulasten;
    private javax.swing.JRadioButton rbBaulasten1;
    private javax.swing.JRadioButton rbBaulasten2;
    private javax.swing.JRadioButton rbBaulasten3;
    private javax.swing.JTextField txtBlattnummer;
    private javax.swing.JTextField txtBlattnummer1;
    private javax.swing.JTextField txtBlattnummer2;
    private javax.swing.JTextField txtBlattnummer3;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BaulastWindowSearch.
     */
    public BaulastWindowSearch() {
        mc = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "ALB_BAULAST");
        icon = new ImageIcon(mc.getIconData());
        fsSelectionDialoge = new FlurstueckSelectionDialoge(false) {

                @Override
                public void okHook() {
                    final List<CidsBean> result = getCurrentListToAdd();
                    if (result.size() > 0) {
                        model.addElement(result.get(0));
                    }
                }
            };
        initComponents();
        final MetaClass artMC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALB_BAULAST_ART");
        final DefaultComboBoxModel cbArtModel;
        try {
            cbArtModel = DefaultBindableReferenceCombo.getModelByMetaClass(artMC, true);
            cbArt.setModel(cbArtModel);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        model = new DefaultListModel();
        lstFlurstueck.setModel(model);
        AutoCompleteDecorator.decorate(cbArt);
        new CidsBeanDropTarget(this);
        fsSelectionDialoge.pack();
        fsSelectionDialoge.setLocationRelativeTo(this);
//        cmdAbort.setVisible(false);
        pnlSearchCancel = new SearchControlPanel(this);
        panCommand.add(pnlSearchCancel);
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
        panSearch = new javax.swing.JPanel();
        panCommand = new javax.swing.JPanel();
        pnlAttributeFilter = new javax.swing.JPanel();
        chkGeloescht = new javax.swing.JCheckBox();
        chkGueltig = new javax.swing.JCheckBox();
        cbArt = new javax.swing.JComboBox();
        txtBlattnummer = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pnlFlurstuecksfilter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstFlurstueck = new javax.swing.JList();
        btnAddFS = new javax.swing.JButton();
        btnRemoveFS = new javax.swing.JButton();
        btnFromMapFS = new javax.swing.JButton();
        chkBeguenstigt = new javax.swing.JCheckBox();
        chkBelastet = new javax.swing.JCheckBox();
        pnlSearchFor = new javax.swing.JPanel();
        rbBaulastBlaetter = new javax.swing.JRadioButton();
        rbBaulasten = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        chkKartenausschnitt = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        panSearch1 = new javax.swing.JPanel();
        panCommand1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        chkGeloescht1 = new javax.swing.JCheckBox();
        chkGueltig1 = new javax.swing.JCheckBox();
        cbArt1 = new javax.swing.JComboBox();
        txtBlattnummer1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstFlurstueck1 = new javax.swing.JList();
        btnAddFS1 = new javax.swing.JButton();
        btnRemoveFS1 = new javax.swing.JButton();
        btnFromMapFS1 = new javax.swing.JButton();
        chkBeguenstigt1 = new javax.swing.JCheckBox();
        chkBelastet1 = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        rbBaulastBlaetter1 = new javax.swing.JRadioButton();
        rbBaulasten1 = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        chkKartenausschnitt1 = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        panSearch2 = new javax.swing.JPanel();
        panCommand2 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        chkGeloescht2 = new javax.swing.JCheckBox();
        chkGueltig2 = new javax.swing.JCheckBox();
        cbArt2 = new javax.swing.JComboBox();
        txtBlattnummer2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstFlurstueck2 = new javax.swing.JList();
        btnAddFS2 = new javax.swing.JButton();
        btnRemoveFS2 = new javax.swing.JButton();
        btnFromMapFS2 = new javax.swing.JButton();
        chkBeguenstigt2 = new javax.swing.JCheckBox();
        chkBelastet2 = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        rbBaulastBlaetter2 = new javax.swing.JRadioButton();
        rbBaulasten2 = new javax.swing.JRadioButton();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        chkKartenausschnitt2 = new javax.swing.JCheckBox();
        jPanel18 = new javax.swing.JPanel();
        panSearch3 = new javax.swing.JPanel();
        panCommand3 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        chkGeloescht3 = new javax.swing.JCheckBox();
        chkGueltig3 = new javax.swing.JCheckBox();
        cbArt3 = new javax.swing.JComboBox();
        txtBlattnummer3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstFlurstueck3 = new javax.swing.JList();
        btnAddFS3 = new javax.swing.JButton();
        btnRemoveFS3 = new javax.swing.JButton();
        btnFromMapFS3 = new javax.swing.JButton();
        chkBeguenstigt3 = new javax.swing.JCheckBox();
        chkBelastet3 = new javax.swing.JCheckBox();
        jPanel21 = new javax.swing.JPanel();
        rbBaulastBlaetter3 = new javax.swing.JRadioButton();
        rbBaulasten3 = new javax.swing.JRadioButton();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        chkKartenausschnitt3 = new javax.swing.JCheckBox();

        setMaximumSize(new java.awt.Dimension(325, 460));
        setMinimumSize(new java.awt.Dimension(325, 460));
        setPreferredSize(new java.awt.Dimension(325, 460));
        setLayout(new java.awt.BorderLayout());

        panSearch.setMaximumSize(new java.awt.Dimension(400, 150));
        panSearch.setMinimumSize(new java.awt.Dimension(400, 150));
        panSearch.setPreferredSize(new java.awt.Dimension(400, 150));
        panSearch.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(panCommand, gridBagConstraints);

        pnlAttributeFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributfilter"));
        pnlAttributeFilter.setLayout(new java.awt.GridBagLayout());

        chkGeloescht.setSelected(true);
        chkGeloescht.setText("gelöscht / geschlossen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(chkGeloescht, gridBagConstraints);

        chkGueltig.setSelected(true);
        chkGueltig.setText("gültig");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(chkGueltig, gridBagConstraints);

        cbArt.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(cbArt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(txtBlattnummer, gridBagConstraints);

        jLabel1.setText("Blattnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Art der Baulast:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAttributeFilter.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(pnlAttributeFilter, gridBagConstraints);

        pnlFlurstuecksfilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Flurstücksfilter"));
        pnlFlurstuecksfilter.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMaximumSize(new java.awt.Dimension(200, 100));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 100));

        jScrollPane1.setViewportView(lstFlurstueck);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(jScrollPane1, gridBagConstraints);

        btnAddFS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png"))); // NOI18N
        btnAddFS.setToolTipText("Flurstück hinzufügen");
        btnAddFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(btnAddFS, gridBagConstraints);

        btnRemoveFS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnRemoveFS.setToolTipText("Ausgewählte Flurstücke entfernen");
        btnRemoveFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(btnRemoveFS, gridBagConstraints);

        btnFromMapFS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/bookmark-new.png"))); // NOI18N
        btnFromMapFS.setToolTipText("Selektierte Flurstücke aus Karte hinzufügen");
        btnFromMapFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromMapFSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(btnFromMapFS, gridBagConstraints);

        chkBeguenstigt.setSelected(true);
        chkBeguenstigt.setText("begünstigt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(chkBeguenstigt, gridBagConstraints);

        chkBelastet.setSelected(true);
        chkBelastet.setText("belastet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFlurstuecksfilter.add(chkBelastet, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(pnlFlurstuecksfilter, gridBagConstraints);

        pnlSearchFor.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche nach"));
        pnlSearchFor.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(rbBaulastBlaetter);
        rbBaulastBlaetter.setText("Baulastblätter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSearchFor.add(rbBaulastBlaetter, gridBagConstraints);

        buttonGroup1.add(rbBaulasten);
        rbBaulasten.setText("Baulasten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSearchFor.add(rbBaulasten, gridBagConstraints);

        jPanel5.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        pnlSearchFor.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(pnlSearchFor, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        panSearch.add(jPanel6, gridBagConstraints);

        chkKartenausschnitt.setText("<html>Nur im aktuellen<br>Kartenausschnitt suchen</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panSearch.add(chkKartenausschnitt, gridBagConstraints);

        add(panSearch, java.awt.BorderLayout.CENTER);

        jPanel1.setMaximumSize(new java.awt.Dimension(325, 460));
        jPanel1.setMinimumSize(new java.awt.Dimension(325, 460));
        jPanel1.setPreferredSize(new java.awt.Dimension(325, 460));
        jPanel1.setLayout(new java.awt.BorderLayout());

        panSearch1.setMaximumSize(new java.awt.Dimension(400, 150));
        panSearch1.setMinimumSize(new java.awt.Dimension(400, 150));
        panSearch1.setPreferredSize(new java.awt.Dimension(400, 150));
        panSearch1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch1.add(panCommand1, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributfilter"));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        chkGeloescht1.setSelected(true);
        chkGeloescht1.setText("gelöscht / geschlossen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(chkGeloescht1, gridBagConstraints);

        chkGueltig1.setSelected(true);
        chkGueltig1.setText("gültig");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(chkGueltig1, gridBagConstraints);

        cbArt1.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(cbArt1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(txtBlattnummer1, gridBagConstraints);

        jLabel3.setText("Blattnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Art der Baulast:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch1.add(jPanel7, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Flurstücksfilter"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMaximumSize(new java.awt.Dimension(200, 100));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 100));

        jScrollPane2.setViewportView(lstFlurstueck1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jScrollPane2, gridBagConstraints);

        btnAddFS1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png"))); // NOI18N
        btnAddFS1.setToolTipText("Flurstück hinzufügen");
        btnAddFS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFS1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(btnAddFS1, gridBagConstraints);

        btnRemoveFS1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnRemoveFS1.setToolTipText("Ausgewählte Flurstücke entfernen");
        btnRemoveFS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFS1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(btnRemoveFS1, gridBagConstraints);

        btnFromMapFS1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/bookmark-new.png"))); // NOI18N
        btnFromMapFS1.setToolTipText("Selektierte Flurstücke aus Karte hinzufügen");
        btnFromMapFS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromMapFS1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(btnFromMapFS1, gridBagConstraints);

        chkBeguenstigt1.setSelected(true);
        chkBeguenstigt1.setText("begünstigt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(chkBeguenstigt1, gridBagConstraints);

        chkBelastet1.setSelected(true);
        chkBelastet1.setText("belastet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(chkBelastet1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch1.add(jPanel8, gridBagConstraints);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche nach"));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(rbBaulastBlaetter1);
        rbBaulastBlaetter1.setText("Baulastblätter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(rbBaulastBlaetter1, gridBagConstraints);

        buttonGroup1.add(rbBaulasten1);
        rbBaulasten1.setText("Baulasten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(rbBaulasten1, gridBagConstraints);

        jPanel10.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch1.add(jPanel9, gridBagConstraints);

        jPanel11.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        panSearch1.add(jPanel11, gridBagConstraints);

        chkKartenausschnitt1.setText("<html>Nur im aktuellen<br>Kartenausschnitt suchen</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panSearch1.add(chkKartenausschnitt1, gridBagConstraints);

        jPanel1.add(panSearch1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel12.setMaximumSize(new java.awt.Dimension(325, 460));
        jPanel12.setMinimumSize(new java.awt.Dimension(325, 460));
        jPanel12.setPreferredSize(new java.awt.Dimension(325, 460));
        jPanel12.setLayout(new java.awt.BorderLayout());

        panSearch2.setMaximumSize(new java.awt.Dimension(400, 150));
        panSearch2.setMinimumSize(new java.awt.Dimension(400, 150));
        panSearch2.setPreferredSize(new java.awt.Dimension(400, 150));
        panSearch2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch2.add(panCommand2, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributfilter"));
        jPanel13.setLayout(new java.awt.GridBagLayout());

        chkGeloescht2.setSelected(true);
        chkGeloescht2.setText("gelöscht / geschlossen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(chkGeloescht2, gridBagConstraints);

        chkGueltig2.setSelected(true);
        chkGueltig2.setText("gültig");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(chkGueltig2, gridBagConstraints);

        cbArt2.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(cbArt2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(txtBlattnummer2, gridBagConstraints);

        jLabel5.setText("Blattnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Art der Baulast:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch2.add(jPanel13, gridBagConstraints);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Flurstücksfilter"));
        jPanel14.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setMaximumSize(new java.awt.Dimension(200, 100));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(200, 100));

        jScrollPane3.setViewportView(lstFlurstueck2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jScrollPane3, gridBagConstraints);

        btnAddFS2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png"))); // NOI18N
        btnAddFS2.setToolTipText("Flurstück hinzufügen");
        btnAddFS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFS2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(btnAddFS2, gridBagConstraints);

        btnRemoveFS2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnRemoveFS2.setToolTipText("Ausgewählte Flurstücke entfernen");
        btnRemoveFS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFS2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(btnRemoveFS2, gridBagConstraints);

        btnFromMapFS2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/bookmark-new.png"))); // NOI18N
        btnFromMapFS2.setToolTipText("Selektierte Flurstücke aus Karte hinzufügen");
        btnFromMapFS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromMapFS2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(btnFromMapFS2, gridBagConstraints);

        chkBeguenstigt2.setSelected(true);
        chkBeguenstigt2.setText("begünstigt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(chkBeguenstigt2, gridBagConstraints);

        chkBelastet2.setSelected(true);
        chkBelastet2.setText("belastet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(chkBelastet2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch2.add(jPanel14, gridBagConstraints);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche nach"));
        jPanel15.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(rbBaulastBlaetter2);
        rbBaulastBlaetter2.setText("Baulastblätter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(rbBaulastBlaetter2, gridBagConstraints);

        buttonGroup1.add(rbBaulasten2);
        rbBaulasten2.setText("Baulasten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(rbBaulasten2, gridBagConstraints);

        jPanel16.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(jPanel16, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch2.add(jPanel15, gridBagConstraints);

        jPanel17.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        panSearch2.add(jPanel17, gridBagConstraints);

        chkKartenausschnitt2.setText("<html>Nur im aktuellen<br>Kartenausschnitt suchen</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panSearch2.add(chkKartenausschnitt2, gridBagConstraints);

        jPanel12.add(panSearch2, java.awt.BorderLayout.CENTER);

        jPanel18.setMaximumSize(new java.awt.Dimension(325, 460));
        jPanel18.setMinimumSize(new java.awt.Dimension(325, 460));
        jPanel18.setPreferredSize(new java.awt.Dimension(325, 460));
        jPanel18.setLayout(new java.awt.BorderLayout());

        panSearch3.setMaximumSize(new java.awt.Dimension(400, 150));
        panSearch3.setMinimumSize(new java.awt.Dimension(400, 150));
        panSearch3.setPreferredSize(new java.awt.Dimension(400, 150));
        panSearch3.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch3.add(panCommand3, gridBagConstraints);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributfilter"));
        jPanel19.setLayout(new java.awt.GridBagLayout());

        chkGeloescht3.setSelected(true);
        chkGeloescht3.setText("gelöscht / geschlossen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(chkGeloescht3, gridBagConstraints);

        chkGueltig3.setSelected(true);
        chkGueltig3.setText("gültig");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(chkGueltig3, gridBagConstraints);

        cbArt3.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(cbArt3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(txtBlattnummer3, gridBagConstraints);

        jLabel7.setText("Blattnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Art der Baulast:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch3.add(jPanel19, gridBagConstraints);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Flurstücksfilter"));
        jPanel20.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setMaximumSize(new java.awt.Dimension(200, 100));
        jScrollPane4.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(200, 100));

        jScrollPane4.setViewportView(lstFlurstueck3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jScrollPane4, gridBagConstraints);

        btnAddFS3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png"))); // NOI18N
        btnAddFS3.setToolTipText("Flurstück hinzufügen");
        btnAddFS3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFS3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(btnAddFS3, gridBagConstraints);

        btnRemoveFS3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnRemoveFS3.setToolTipText("Ausgewählte Flurstücke entfernen");
        btnRemoveFS3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFS3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(btnRemoveFS3, gridBagConstraints);

        btnFromMapFS3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/bookmark-new.png"))); // NOI18N
        btnFromMapFS3.setToolTipText("Selektierte Flurstücke aus Karte hinzufügen");
        btnFromMapFS3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromMapFS3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(btnFromMapFS3, gridBagConstraints);

        chkBeguenstigt3.setSelected(true);
        chkBeguenstigt3.setText("begünstigt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(chkBeguenstigt3, gridBagConstraints);

        chkBelastet3.setSelected(true);
        chkBelastet3.setText("belastet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(chkBelastet3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch3.add(jPanel20, gridBagConstraints);

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche nach"));
        jPanel21.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(rbBaulastBlaetter3);
        rbBaulastBlaetter3.setSelected(true);
        rbBaulastBlaetter3.setText("Baulastblätter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel21.add(rbBaulastBlaetter3, gridBagConstraints);

        buttonGroup1.add(rbBaulasten3);
        rbBaulasten3.setText("Baulasten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel21.add(rbBaulasten3, gridBagConstraints);

        jPanel22.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel21.add(jPanel22, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch3.add(jPanel21, gridBagConstraints);

        jPanel23.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        panSearch3.add(jPanel23, gridBagConstraints);

        chkKartenausschnitt3.setText("<html>Nur im aktuellen<br>Kartenausschnitt suchen</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panSearch3.add(chkKartenausschnitt3, gridBagConstraints);

        jPanel18.add(panSearch3, java.awt.BorderLayout.CENTER);

        jPanel12.add(jPanel18, java.awt.BorderLayout.PAGE_START);

        add(jPanel12, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddFSActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFSActionPerformed
        final List<CidsBean> result = new ArrayList<CidsBean>(1);
        fsSelectionDialoge.setCurrentListToAdd(result);
        fsSelectionDialoge.setVisible(true);
    }//GEN-LAST:event_btnAddFSActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveFSActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFSActionPerformed
        final Object[] selection = lstFlurstueck.getSelectedValues();
        for (final Object o : selection) {
            model.removeElement(o);
        }
    }//GEN-LAST:event_btnRemoveFSActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFromMapFSActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromMapFSActionPerformed
        final Collection<Feature> selFeatures = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getFeatureCollection()
                    .getSelectedFeatures();
        for (final Feature feature : selFeatures) {
            if (feature instanceof CidsFeature) {
                final CidsFeature cf = (CidsFeature)feature;
                final MetaObject mo = cf.getMetaObject();
                final String tableName = mo.getMetaClass().getTableName();
                if ("FLURSTUECK".equalsIgnoreCase(tableName) || "ALB_FLURSTUECK_KICKER".equalsIgnoreCase(tableName)) {
//                if ("FLURSTUECK".equalsIgnoreCase(tableName) || "ALB_FLURSTUECK_KICKER".equalsIgnoreCase(tableName) || "ALKIS_LANDPARCEL".equalsIgnoreCase(tableName)) {
                    model.addElement(mo.getBean());
                }
            }
        }
    }//GEN-LAST:event_btnFromMapFSActionPerformed

    private void btnAddFS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFS1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddFS1ActionPerformed

    private void btnRemoveFS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFS1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveFS1ActionPerformed

    private void btnFromMapFS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromMapFS1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFromMapFS1ActionPerformed

    private void btnAddFS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFS2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddFS2ActionPerformed

    private void btnRemoveFS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFS2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveFS2ActionPerformed

    private void btnFromMapFS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromMapFS2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFromMapFS2ActionPerformed

    private void btnAddFS3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFS3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddFS3ActionPerformed

    private void btnRemoveFS3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFS3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveFS3ActionPerformed

    private void btnFromMapFS3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromMapFS3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFromMapFS3ActionPerformed

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return "Baulast Suche";
    }

    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private BaulastSearchInfo getBaulastInfoFromGUI() {
        final BaulastSearchInfo bsi = new BaulastSearchInfo();
        bsi.setResult(rbBaulastBlaetter.isSelected() ? CidsBaulastSearchStatement.Result.BAULASTBLATT
                                                     : CidsBaulastSearchStatement.Result.BAULAST);
        if (chkKartenausschnitt.isSelected()) {
            final XBoundingBox bb = (XBoundingBox)CismapBroker.getInstance().getMappingComponent()
                        .getCurrentBoundingBox();
            final Geometry g = CrsTransformer.transformToDefaultCrs(bb.getGeometry());
            bsi.setBounds(g.toText());
        }
        bsi.setBelastet(chkBelastet.isSelected());
        bsi.setBeguenstigt(chkBeguenstigt.isSelected());
        bsi.setUngueltig(chkGeloescht.isSelected());
        bsi.setGueltig(chkGueltig.isSelected());
        bsi.setBlattnummer(txtBlattnummer.getText());
        final Object art = cbArt.getSelectedItem();
        if (art != null) {
            bsi.setArt(art.toString());
        }
        return bsi;
    }

    @Override
    public CidsServerSearch getServerSearch() {
        final BaulastSearchInfo bsi = getBaulastInfoFromGUI();
        for (int i = 0; i < model.size(); ++i) {
            final CidsBean fsBean = (CidsBean)model.getElementAt(i);
            try {
                if ("ALB_FLURSTUECK_KICKER".equalsIgnoreCase(fsBean.getMetaObject().getMetaClass().getTableName())) {
                    final FlurstueckInfo fi = new FlurstueckInfo((Integer)fsBean.getProperty("gemarkung"),
                            (String)fsBean.getProperty("flur"),
                            (String)fsBean.getProperty("zaehler"),
                            (String)fsBean.getProperty("nenner"));
                    bsi.getFlurstuecke().add(fi);
                } else if ("FLURSTUECK".equalsIgnoreCase(fsBean.getMetaObject().getMetaClass().getTableName())) {
                    final CidsBean gemarkung = (CidsBean)fsBean.getProperty("gemarkungs_nr");
                    final FlurstueckInfo fi = new FlurstueckInfo((Integer)gemarkung.getProperty("gemarkungsnummer"),
                            (String)fsBean.getProperty("flur"),
                            String.valueOf(fsBean.getProperty("fstnr_z")),
                            String.valueOf(fsBean.getProperty("fstnr_n")));
                    bsi.getFlurstuecke().add(fi);
                }
//                else if ("ALKIS_LANDPARCEL".equalsIgnoreCase(fsBean.getMetaObject().getMetaClass().getTableName())) {
//                //TODO: merke
//                    FlurstueckInfo fi = new FlurstueckInfo(Integer.parseInt(String.valueOf(fsBean.getProperty("gemarkung"))), (String) fsBean.getProperty("flur"), String.valueOf(fsBean.getProperty("fstck_zaehler")), String.valueOf(fsBean.getProperty("fstck_nenner")));
//                    bsi.getFlurstuecke().add(fi);
//                }
            } catch (Exception ex) {
                log.error("Can not parse information from Flurstueck bean: " + fsBean, ex);
            }
        }
        MetaClass mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALB_BAULAST");
        final int baulastClassID = mc.getID();
        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALB_BAULASTBLATT");
        final int baulastblattClassID = mc.getID();
        return new CidsBaulastSearchStatement(bsi, baulastClassID, baulastblattClassID);
    }

    @Override
    public void beansDropped(final ArrayList<CidsBean> beans) {
        for (final CidsBean bean : beans) {
            if ("FLURSTUECK".equalsIgnoreCase(bean.getMetaObject().getMetaClass().getTableName())) {
//            if ("FLURSTUECK".equalsIgnoreCase(bean.getMetaObject().getMetaClass().getTableName()) || "ALKIS_LANDPARCEL".equalsIgnoreCase(bean.getMetaObject().getMetaClass().getTableName())) {
                model.addElement(bean);
            }
            lstFlurstueck.repaint();
        }
    }

    @Override
    public boolean checkActionTag() {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(), "navigator.baulasten.search") != null;
        } catch (ConnectionException ex) {
            log.error("Can not validate ActionTag for Baulasten Suche!", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "krissenich");
        final BaulastWindowSearch bws = new BaulastWindowSearch();
        DevelopmentTools.showTestFrame(bws, 800, 600);
    }

    @Override
    public CidsServerSearch assembleSearch() {
        CidsServerSearch result = null;

        final BaulastSearchInfo bsi = getBaulastInfoFromGUI();
        final boolean keineBlattNummer = (bsi.getBlattnummer() == null) || (bsi.getBlattnummer().trim().length() == 0);
        final boolean keineArt = (bsi.getArt() == null) || (bsi.getArt().trim().length() == 0);
        final boolean keinFlurstueck = lstFlurstueck.getModel().getSize() == 0;
        final boolean keinKartenausschnitt = !chkKartenausschnitt.isSelected();

        if (keineBlattNummer && keinKartenausschnitt && keineArt && keinFlurstueck) {
            JOptionPane.showMessageDialog(
                this,
                "Ihre Suchanfrage ist nicht plausibel. Bitte präzisieren Sie die\n"
                        + "Suchanfrage durch weitere Angaben im Attribut- und Flurstücksfilter.",
                "Plausibilitätskontrolle",
                JOptionPane.WARNING_MESSAGE);
        } else {
            result = getServerSearch();
        }

        return result;
    }

    @Override
    public void searchStarted() {
    }

    @Override
    public void searchDone(final Node[] result) {
        if ((result != null) && (result.length > 0)) {
            txtBlattnummer.setText("");
            cbArt.setSelectedItem(null);
            chkKartenausschnitt.setSelected(false);
        }
    }

    @Override
    public void searchCancelled() {
    }

    @Override
    public boolean displaysEmptyResultMessage() {
        return true;
    }
}
