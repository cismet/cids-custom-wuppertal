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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXDatePicker;

import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.abfrage.AbfragePanel;
import de.cismet.cids.custom.wunda_blau.search.abfrage.AbstractAbfragePanel;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMauernSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = AbfragePanel.class)
public class MauernWindowSearchPanel extends AbstractAbfragePanel<CidsMauernSearchStatement.Configuration> {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauernWindowSearchPanel.class);

    //~ Instance fields --------------------------------------------------------

    private DefaultListModel eigentuemerListModel = new DefaultListModel();
    private final DefaultListModel lastKlasseListModel = new DefaultListModel();

    private MetaClass mcGewerk;
    private MetaClass mcSanierung;

    @Getter private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbErledigtBegeh;
    private javax.swing.JComboBox<String> cbErledigtBesichtigt;
    private javax.swing.JComboBox<String> cbErledigtDurchge;
    private javax.swing.JComboBox<String> cbErledigtDurchzu;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbGewerkDurchge;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbGewerkDurchzu;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbSanierung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbegehBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbegehVon;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbesichtigBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbesichtigVon;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcDurchgeSanBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcDurchgeSanVon;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcDurchzuSanBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcDurchzuSanVon;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefVon;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAnsicht;
    private javax.swing.JLabel lblBausubstanz;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblFiller3;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblGelaende;
    private javax.swing.JLabel lblGelaender;
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
    private javax.swing.JLabel lblPruefTil1;
    private javax.swing.JLabel lblPruefTil2;
    private javax.swing.JLabel lblPruefTil3;
    private javax.swing.JLabel lblPruefTil4;
    private javax.swing.JLabel lblPruefVon;
    private javax.swing.JLabel lblPruefVon1;
    private javax.swing.JLabel lblPruefVon2;
    private javax.swing.JLabel lblPruefVon3;
    private javax.swing.JLabel lblPruefVon4;
    private javax.swing.JLabel lblSanierung;
    private javax.swing.JLabel lblSelektierteLastklassen;
    private javax.swing.JLabel lblVerformung;
    private javax.swing.JLabel lblWandKopf;
    private javax.swing.JLabel lblselectedEigentuemer;
    private javax.swing.JList lstEigentuemer;
    private javax.swing.JList lstLastklasse;
    private javax.swing.JPanel pnlEigentuemerCtrlBtns;
    private javax.swing.JPanel pnlEigetuemer;
    private javax.swing.JPanel pnlHoehe;
    private javax.swing.JPanel pnlLastklasse;
    private javax.swing.JPanel pnlNoten;
    private javax.swing.JPanel pnlPruefung;
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
    private javax.swing.JTextField tfGelaendeObenBis;
    private javax.swing.JTextField tfGelaendeObenVon;
    private javax.swing.JTextField tfGelaendeVon;
    private javax.swing.JTextField tfGelaenderBis;
    private javax.swing.JTextField tfGelaenderVon;
    private javax.swing.JTextField tfGruendungBis;
    private javax.swing.JTextField tfGruendungVon;
    private javax.swing.JTextField tfHoeheBis;
    private javax.swing.JTextField tfHoeheVon;
    private javax.swing.JTextField tfWandkopfBis;
    private javax.swing.JTextField tfWandkopfVon;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauernWindowSearch.
     */
    public MauernWindowSearchPanel() {
        this(true);
    }

    /**
     * Creates a new MauernWindowSearchPanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public MauernWindowSearchPanel(final boolean editable) {
        super(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        try {
            mcGewerk = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "MAUER_MASSNAHME_OBJEKT",
                    getConnectionContext());
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            mcSanierung = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "MAUER_EINGRIFF",
                    getConnectionContext());
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }

        try {
            initComponents();

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

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlNoten = new javax.swing.JPanel();
        lblVerformung = new javax.swing.JLabel();
        lblNotenFrom5 = new javax.swing.JLabel();
        tfGelaendeObenVon = new javax.swing.JTextField();
        lblNotenBis4 = new javax.swing.JLabel();
        tfGelaendeObenBis = new javax.swing.JTextField();
        lblGelaender = new javax.swing.JLabel();
        lblNotenFrom1 = new javax.swing.JLabel();
        tfGelaenderVon = new javax.swing.JTextField();
        lblNotenBis1 = new javax.swing.JLabel();
        tfGelaenderBis = new javax.swing.JTextField();
        lblWandKopf = new javax.swing.JLabel();
        lblNotenFrom2 = new javax.swing.JLabel();
        tfWandkopfVon = new javax.swing.JTextField();
        lblNotenBis2 = new javax.swing.JLabel();
        tfWandkopfBis = new javax.swing.JTextField();
        lblAnsicht = new javax.swing.JLabel();
        lblNotenFrom3 = new javax.swing.JLabel();
        tfAnsichtVon = new javax.swing.JTextField();
        lblNotenBis = new javax.swing.JLabel();
        tfAnsichtBis = new javax.swing.JTextField();
        lblGruendung = new javax.swing.JLabel();
        lblNotenFrom4 = new javax.swing.JLabel();
        tfGruendungVon = new javax.swing.JTextField();
        lblNotenBis3 = new javax.swing.JLabel();
        tfGruendungBis = new javax.swing.JTextField();
        lblGelaende = new javax.swing.JLabel();
        lblNotenFrom6 = new javax.swing.JLabel();
        tfGelaendeVon = new javax.swing.JTextField();
        lblNotenBis5 = new javax.swing.JLabel();
        tfGelaendeBis = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblBausubstanz = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tfBausubstanzVon = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfBausubstanzBis = new javax.swing.JTextField();
        lblSanierung = new javax.swing.JLabel();
        lblFiller2 = new javax.swing.JLabel();
        cbSanierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(mcSanierung, true, false);
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
        pnlPruefung = new javax.swing.JPanel();
        lblPruefVon = new javax.swing.JLabel();
        dcPruefVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblPruefTil = new javax.swing.JLabel();
        dcPruefBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        pnlHoehe = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblHoeheVon = new javax.swing.JLabel();
        tfHoeheVon = new javax.swing.JTextField();
        lblHoeheBis = new javax.swing.JLabel();
        tfHoeheBis = new javax.swing.JTextField();
        lblFiller3 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        lblPruefVon1 = new javax.swing.JLabel();
        dcDurchzuSanVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblPruefTil1 = new javax.swing.JLabel();
        dcDurchzuSanBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        cbGewerkDurchzu = new de.cismet.cids.editors.DefaultBindableReferenceCombo(mcGewerk, true, false);
        cbErledigtDurchzu = new javax.swing.JComboBox<>();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel7 = new javax.swing.JPanel();
        lblPruefVon2 = new javax.swing.JLabel();
        dcDurchgeSanVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcDurchgeSanBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jPanel8 = new javax.swing.JPanel();
        cbErledigtDurchge = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cbGewerkDurchge = new de.cismet.cids.editors.DefaultBindableReferenceCombo(mcGewerk, true, false);
        lblPruefTil2 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel9 = new javax.swing.JPanel();
        lblPruefVon4 = new javax.swing.JLabel();
        dcBauwerksbegehVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblPruefTil4 = new javax.swing.JLabel();
        dcBauwerksbegehBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        cbErledigtBegeh = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();
        lblPruefVon3 = new javax.swing.JLabel();
        dcBauwerksbesichtigVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblPruefTil3 = new javax.swing.JLabel();
        dcBauwerksbesichtigBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel11 = new javax.swing.JPanel();
        cbErledigtBesichtigt = new javax.swing.JComboBox<>();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlNoten.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.pnlNoten.border.title"))); // NOI18N
        pnlNoten.setOpaque(false);
        pnlNoten.setLayout(new java.awt.GridBagLayout());

        lblVerformung.setText("Gelände oben:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblVerformung, gridBagConstraints);

        lblNotenFrom5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom5, gridBagConstraints);

        tfGelaendeObenVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaendeObenVon.text")); // NOI18N
        tfGelaendeObenVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeObenVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGelaendeObenVon, gridBagConstraints);

        lblNotenBis4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis4, gridBagConstraints);

        tfGelaendeObenBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaendeObenBis.text")); // NOI18N
        tfGelaendeObenBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeObenBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGelaendeObenBis, gridBagConstraints);

        lblGelaender.setText("Absturzsicherung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblGelaender, gridBagConstraints);

        lblNotenFrom1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblNotenFrom1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom1, gridBagConstraints);

        tfGelaenderVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaenderVon.text")); // NOI18N
        tfGelaenderVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaenderVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGelaenderVon, gridBagConstraints);

        lblNotenBis1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis1, gridBagConstraints);

        tfGelaenderBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaenderBis.text")); // NOI18N
        tfGelaenderBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaenderBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGelaenderBis, gridBagConstraints);

        lblWandKopf.setText("Wandkopf:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblWandKopf, gridBagConstraints);

        lblNotenFrom2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom2, gridBagConstraints);

        tfWandkopfVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfWandkopfVon.text")); // NOI18N
        tfWandkopfVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfWandkopfVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfWandkopfVon, gridBagConstraints);

        lblNotenBis2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis2, gridBagConstraints);

        tfWandkopfBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfWandkopfBis.text")); // NOI18N
        tfWandkopfBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfWandkopfBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfWandkopfBis, gridBagConstraints);

        lblAnsicht.setText("Ansicht:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblAnsicht, gridBagConstraints);

        lblNotenFrom3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom3, gridBagConstraints);

        tfAnsichtVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfAnsichtVon.text")); // NOI18N
        tfAnsichtVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfAnsichtVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfAnsichtVon, gridBagConstraints);

        lblNotenBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis, gridBagConstraints);

        tfAnsichtBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfAnsichtBis.text")); // NOI18N
        tfAnsichtBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfAnsichtBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfAnsichtBis, gridBagConstraints);

        lblGruendung.setText("Gründung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblGruendung, gridBagConstraints);

        lblNotenFrom4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom4, gridBagConstraints);

        tfGruendungVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGruendungVon.text")); // NOI18N
        tfGruendungVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGruendungVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGruendungVon, gridBagConstraints);

        lblNotenBis3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis3, gridBagConstraints);

        tfGruendungBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGruendungBis.text")); // NOI18N
        tfGruendungBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGruendungBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGruendungBis, gridBagConstraints);

        lblGelaende.setText("Gelände unten:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblGelaende, gridBagConstraints);

        lblNotenFrom6.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom6, gridBagConstraints);

        tfGelaendeVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaendeVon.text")); // NOI18N
        tfGelaendeVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfGelaendeVon, gridBagConstraints);

        lblNotenBis5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(lblNotenBis5, gridBagConstraints);

        tfGelaendeBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.tfGelaendeBis.text")); // NOI18N
        tfGelaendeBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfGelaendeBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfGelaendeBis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlNoten.add(jSeparator1, gridBagConstraints);

        lblBausubstanz.setText("Zustand gesamte Bausubstanz:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblBausubstanz, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(jLabel4, gridBagConstraints);

        tfBausubstanzVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.jTextField3.text")); // NOI18N
        tfBausubstanzVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfBausubstanzVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlNoten.add(tfBausubstanzVon, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(jLabel5, gridBagConstraints);

        tfBausubstanzBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.jTextField3.text")); // NOI18N
        tfBausubstanzBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfBausubstanzBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(tfBausubstanzBis, gridBagConstraints);

        lblSanierung.setText("Notwendigkeit einer Sanierung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblSanierung, gridBagConstraints);

        lblFiller2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlNoten.add(lblFiller2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnlNoten.add(cbSanierung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlNoten, gridBagConstraints);

        pnlLastklasse.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.pnlLastklasse.border.title"))); // NOI18N
        pnlLastklasse.setOpaque(false);
        pnlLastklasse.setLayout(new java.awt.GridBagLayout());

        scpListLastklasse.setMinimumSize(new java.awt.Dimension(1, 100));
        scpListLastklasse.setPreferredSize(new java.awt.Dimension(1, 100));

        lstLastklasse.setModel(lastKlasseListModel);
        lstLastklasse.setVisibleRowCount(6);
        scpListLastklasse.setViewportView(lstLastklasse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlLastklasse.add(scpListLastklasse, gridBagConstraints);

        lblSelektierteLastklassen.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblSelektierteLastklassen.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlLastklasse.add(lblSelektierteLastklassen, gridBagConstraints);
        lblSelektierteLastklassen.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        MauernWindowSearchPanel.class,
                        "MauernWindowSearchPanel.lblSelektierteLastklassen.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlLastklasse, gridBagConstraints);

        pnlSearchMode.setOpaque(false);
        pnlSearchMode.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSearchMode.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.rbAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlSearchMode.add(rbAll, gridBagConstraints);

        buttonGroup1.add(rbOne);
        rbOne.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.rbOne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSearchMode.add(rbOne, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblFiller5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlSearchMode.add(lblFiller5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        add(pnlSearchMode, gridBagConstraints);

        pnlEigetuemer.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.pnlEigetuemer.border.title"))); // NOI18N
        pnlEigetuemer.setOpaque(false);
        pnlEigetuemer.setLayout(new java.awt.GridBagLayout());

        scpListEigentuemer.setMinimumSize(new java.awt.Dimension(1, 100));
        scpListEigentuemer.setPreferredSize(new java.awt.Dimension(1, 100));

        lstEigentuemer.setModel(eigentuemerListModel);
        lstEigentuemer.setVisibleRowCount(6);
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
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblselectedEigentuemer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlEigetuemer.add(lblselectedEigentuemer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlEigetuemer, gridBagConstraints);

        pnlPruefung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.pnlPruefung.border.title"))); // NOI18N
        pnlPruefung.setOpaque(false);
        pnlPruefung.setLayout(new java.awt.GridBagLayout());

        lblPruefVon.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlPruefung.add(lblPruefVon, gridBagConstraints);

        dcPruefVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlPruefung.add(dcPruefVon, gridBagConstraints);

        lblPruefTil.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlPruefung.add(lblPruefTil, gridBagConstraints);

        dcPruefBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlPruefung.add(dcPruefBis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlPruefung.add(filler1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        pnlPruefung.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());
        pnlPruefung.add(jPanel3, new java.awt.GridBagConstraints());

        jPanel4.setLayout(new java.awt.GridBagLayout());
        pnlPruefung.add(jPanel4, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlPruefung, gridBagConstraints);

        pnlHoehe.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.pnlHoehe.border.title"))); // NOI18N
        pnlHoehe.setOpaque(false);
        pnlHoehe.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlHoehe.add(jLabel2, gridBagConstraints);

        lblHoeheVon.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlHoehe.add(lblHoeheVon, gridBagConstraints);

        tfHoeheVon.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.tfNotenBis.text")); // NOI18N
        tfHoeheVon.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHoeheVon.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlHoehe.add(tfHoeheVon, gridBagConstraints);

        lblHoeheBis.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlHoehe.add(lblHoeheBis, gridBagConstraints);

        tfHoeheBis.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearch.tfNotenBis.text")); // NOI18N
        tfHoeheBis.setMinimumSize(new java.awt.Dimension(70, 27));
        tfHoeheBis.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlHoehe.add(tfHoeheBis, gridBagConstraints);

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlHoehe.add(lblFiller3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlHoehe, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(filler3, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.jPanel5.border.title"))); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblPruefVon1.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel5.add(lblPruefVon1, gridBagConstraints);

        dcDurchzuSanVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        jPanel5.add(dcDurchzuSanVon, gridBagConstraints);

        lblPruefTil1.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel5.add(lblPruefTil1, gridBagConstraints);

        dcDurchzuSanBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel5.add(dcDurchzuSanBis, gridBagConstraints);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("Gewerk:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel6.add(jLabel13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel6.add(cbGewerkDurchzu, gridBagConstraints);

        cbErledigtDurchzu.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "", "nicht erledigt", "erledigt" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 15);
        jPanel6.add(cbErledigtDurchzu, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(jPanel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel5, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.jPanel7.border.title"))); // NOI18N
        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        lblPruefVon2.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel7.add(lblPruefVon2, gridBagConstraints);

        dcDurchgeSanVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        jPanel7.add(dcDurchgeSanVon, gridBagConstraints);

        dcDurchgeSanBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(dcDurchgeSanBis, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        cbErledigtDurchge.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "", "nicht erledigt", "erledigt" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 15);
        jPanel8.add(cbErledigtDurchge, gridBagConstraints);

        jLabel12.setText(org.openide.util.NbBundle.getMessage(
                MauernWindowSearchPanel.class,
                "MauernWindowSearchPanel.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel8.add(jLabel12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel8.add(cbGewerkDurchge, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        jPanel7.add(jPanel8, gridBagConstraints);

        lblPruefTil2.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblPruefTil2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel7, gridBagConstraints);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.jPanel9.border.title"))); // NOI18N
        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        lblPruefVon4.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel9.add(lblPruefVon4, gridBagConstraints);

        dcBauwerksbegehVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        jPanel9.add(dcBauwerksbegehVon, gridBagConstraints);

        lblPruefTil4.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel9.add(lblPruefTil4, gridBagConstraints);

        dcBauwerksbegehBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(dcBauwerksbegehBis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(filler6, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        cbErledigtBegeh.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "", "nicht erledigt", "erledigt" }));
        jPanel1.add(cbErledigtBegeh);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel9, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    MauernWindowSearchPanel.class,
                    "MauernWindowSearchPanel.jPanel10.border.title"))); // NOI18N
        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        lblPruefVon3.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel10.add(lblPruefVon3, gridBagConstraints);

        dcBauwerksbesichtigVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        jPanel10.add(dcBauwerksbesichtigVon, gridBagConstraints);

        lblPruefTil3.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel10.add(lblPruefTil3, gridBagConstraints);

        dcBauwerksbesichtigBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel10.add(dcBauwerksbesichtigBis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel10.add(filler7, gridBagConstraints);

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        cbErledigtBesichtigt.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "", "nicht erledigt", "erledigt" }));
        jPanel11.add(cbErledigtBesichtigt);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel10.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel10, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param   vonTextField  DOCUMENT ME!
     * @param   bisTextField  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsMauernSearchStatement.ZustandInfo createZustandInfo(final JTextField vonTextField,
            final JTextField bisTextField) {
        return new CidsMauernSearchStatement.ZustandInfo(extractDouble(vonTextField),
                extractDouble(bisTextField));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   textField  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Double extractDouble(final JTextField textField) {
        if ((textField.getText() != null) && !textField.getText().equals("")) {
            try {
                return Double.parseDouble(textField.getText().replace(',', '.'));
            } catch (NumberFormatException ex) {
                LOG.warn("Could not read Double value from " + textField.getText(), ex);
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   datePickerVon  DOCUMENT ME!
     * @param   datePickerBis  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsMauernSearchStatement.MassnahmeInfo createMassnahmeInfo(final JXDatePicker datePickerVon,
            final JXDatePicker datePickerBis) {
        return createMassnahmeInfo(datePickerVon, datePickerBis, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   datePickerVon  DOCUMENT ME!
     * @param   datePickerBis  DOCUMENT ME!
     * @param   cbErledigt     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsMauernSearchStatement.MassnahmeInfo createMassnahmeInfo(final JXDatePicker datePickerVon,
            final JXDatePicker datePickerBis,
            final JComboBox cbErledigt) {
        return createMassnahmeInfo(datePickerVon, datePickerBis, null, cbErledigt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   datePickerVon  DOCUMENT ME!
     * @param   datePickerBis  propKey DOCUMENT ME!
     * @param   cbGewerk       filterProps DOCUMENT ME!
     * @param   cbErledigt     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsMauernSearchStatement.MassnahmeInfo createMassnahmeInfo(final JXDatePicker datePickerVon,
            final JXDatePicker datePickerBis,
            final JComboBox cbGewerk,
            final JComboBox cbErledigt) {
        final Date von = (datePickerVon != null) ? datePickerVon.getDate() : null;
        final Date bis = (datePickerBis != null) ? datePickerBis.getDate() : null;
        final Integer gewerk = ((cbGewerk != null) && (cbGewerk.getSelectedItem() != null))
            ? ((CidsBean)cbGewerk.getSelectedItem()).getMetaObject().getId() : null;
        final Boolean erledigt;
        if ((cbErledigt != null) && (cbErledigt.getSelectedItem() != null)) {
            switch ((String)cbErledigt.getSelectedItem()) {
                case "nicht erledigt": {
                    erledigt = Boolean.FALSE;
                }
                break;
                case "erledigt": {
                    erledigt = Boolean.TRUE;
                }
                break;
                default: {
                    erledigt = null;
                }
            }
        } else {
            erledigt = null;
        }

        final CidsMauernSearchStatement.MassnahmeInfo massnahme = new CidsMauernSearchStatement.MassnahmeInfo(
                von,
                bis,
                gewerk,
                erledigt);
        return massnahme;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsMauernSearchStatement.Configuration createConfiguration() {
        final CidsMauernSearchStatement.Configuration configuration = new CidsMauernSearchStatement.Configuration();
        configuration.setSearchMode(rbAll.isSelected() ? CidsMauernSearchStatement.SearchMode.AND
                                                       : CidsMauernSearchStatement.SearchMode.OR);

        final Object[] selectedEigentuemer = lstEigentuemer.getSelectedValues();
        if (selectedEigentuemer != null) {
            final LinkedList<Integer> eigentuemer = new LinkedList<>();
            for (int i = 0; i < selectedEigentuemer.length; i++) {
                final CidsBean bean = (CidsBean)selectedEigentuemer[i];
                final Integer id = (Integer)bean.getProperty("id");
                eigentuemer.add(id);
            }
            configuration.setEigentuemer(eigentuemer);
        }

        final Object[] selectedLastKlasse = lstLastklasse.getSelectedValues();
        if (selectedEigentuemer != null) {
            final LinkedList<Integer> lastklassen = new LinkedList<>();
            for (int i = 0; i < selectedLastKlasse.length; i++) {
                final CidsBean bean = (CidsBean)selectedLastKlasse[i];
                final Integer id = (Integer)bean.getProperty("id");
                lastklassen.add(id);
            }
            configuration.setLastKlasseIds(lastklassen);
        }

        final CidsMauernSearchStatement.ZustaendeInfo zustaende = new CidsMauernSearchStatement.ZustaendeInfo();
        zustaende.setGelaender(createZustandInfo(tfGelaenderVon, tfGelaenderBis));
        zustaende.setWandkopf(createZustandInfo(tfWandkopfVon, tfWandkopfBis));
        zustaende.setAnsicht(createZustandInfo(tfAnsichtVon, tfAnsichtBis));
        zustaende.setGruendung(createZustandInfo(tfGruendungVon, tfGruendungBis));
        zustaende.setGelaendeOben(createZustandInfo(tfGelaendeObenVon, tfGelaendeObenBis));
        zustaende.setGelaende(createZustandInfo(tfGelaendeVon, tfGelaendeBis));
        zustaende.setBausubstanz(createZustandInfo(tfBausubstanzVon, tfBausubstanzBis));
        configuration.setZustaende(zustaende);

        configuration.setSanierung((cbSanierung.getSelectedItem() != null)
                ? ((CidsBean)cbSanierung.getSelectedItem()).getMetaObject().getId() : null);

        final CidsMauernSearchStatement.MassnahmenInfo massnahmen = new CidsMauernSearchStatement.MassnahmenInfo();
        massnahmen.setPruefung(createMassnahmeInfo(dcPruefVon, dcPruefBis));
        massnahmen.setSanierungGeplant(createMassnahmeInfo(
                dcDurchzuSanVon,
                dcDurchzuSanBis,
                cbGewerkDurchzu,
                cbErledigtDurchzu));
        massnahmen.setSanierungDurchgefuehrt(createMassnahmeInfo(
                dcDurchgeSanVon,
                dcDurchgeSanBis,
                cbGewerkDurchge,
                cbErledigtDurchge));
        massnahmen.setBauwerksbegehung(createMassnahmeInfo(dcBauwerksbegehVon, dcBauwerksbegehBis, cbErledigtBegeh));
        massnahmen.setBauwerksbesichtigung(createMassnahmeInfo(
                dcBauwerksbesichtigVon,
                dcBauwerksbesichtigBis,
                cbErledigtBesichtigt));
        configuration.setMassnahmen(massnahmen);

        configuration.setHoeheVon(extractDouble(tfHoeheVon));
        configuration.setHoeheBis(extractDouble(tfHoeheBis));

        return configuration;
    }

    /**
     * DOCUMENT ME!
     */
    private void fillEigentuemerListModel() {
        try {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "mauer_eigentuemer",
                    getConnectionContext());
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
            query += "FROM " + MB_MC.getTableName() + ";";
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(query, 0, getConnectionContext());
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
            final JScrollPane jsp = new JScrollPane(new MauernWindowSearchPanel());
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
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "mauer_lastklasse",
                    getConnectionContext());
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
            query += "FROM " + MB_MC.getTableName() + ";";
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(query, 0, getConnectionContext());
            for (int i = 0; i < metaObjects.length; i++) {
                final CidsBean b = metaObjects[i].getBean();
                lastKlasseListModel.addElement(b);
            }
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
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

    /**
     * DOCUMENT ME!
     *
     * @param   cb  DOCUMENT ME!
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getById(final DefaultBindableReferenceCombo cb, final Integer id) {
        if (id != null) {
            for (int i = 0; i < cb.getModel().getSize(); i++) {
                final Object o = cb.getModel().getElementAt(i);
                if ((o instanceof CidsBean) && (((CidsBean)o).getPrimaryKeyValue() == id)) {
                    return (CidsBean)o;
                }
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  configuration  DOCUMENT ME!
     */
    @Override
    public void initFromConfiguration(final CidsMauernSearchStatement.Configuration configuration) {
        if (configuration != null) {
            if (CidsMauernSearchStatement.SearchMode.OR.equals(configuration.getSearchMode())) {
                rbOne.setSelected(true);
            } else {
                rbAll.setSelected(true);
            }
            final Collection<Integer> eigentuemerIndices = new ArrayList<>();
            if (configuration.getEigentuemer() != null) {
                for (final Integer eigentuemer : configuration.getEigentuemer()) {
                    for (int index = 0; index < lstEigentuemer.getModel().getSize(); index++) {
                        final Object o = lstEigentuemer.getModel().getElementAt(index);
                        if ((o instanceof CidsBean)
                                    && (Objects.equals(((CidsBean)o).getPrimaryKeyValue(), eigentuemer))) {
                            eigentuemerIndices.add(index);
                        }
                    }
                }
            }
            lstEigentuemer.setSelectedIndices(ArrayUtils.toPrimitive(eigentuemerIndices.toArray(new Integer[0])));

            final Collection<Integer> lastKlasseIndices = new ArrayList<>();
            if (configuration.getLastKlasseIds() != null) {
                for (final Integer lastKlasse : configuration.getLastKlasseIds()) {
                    for (int index = 0; index < lstLastklasse.getModel().getSize(); index++) {
                        final Object o = lstLastklasse.getModel().getElementAt(index);
                        if ((o instanceof CidsBean)
                                    && (Objects.equals(((CidsBean)o).getPrimaryKeyValue(), lastKlasse))) {
                            lastKlasseIndices.add(index);
                        }
                    }
                }
            }
            lstLastklasse.setSelectedIndices(ArrayUtils.toPrimitive(lastKlasseIndices.toArray(new Integer[0])));

            final CidsMauernSearchStatement.MassnahmenInfo massnahmen = configuration.getMassnahmen();
            if (massnahmen != null) {
                final CidsMauernSearchStatement.MassnahmeInfo pruefung = massnahmen.getPruefung();
                dcPruefVon.setDate((pruefung != null) ? pruefung.getVon() : null);
                dcPruefBis.setDate((pruefung != null) ? pruefung.getBis() : null);
                final CidsMauernSearchStatement.MassnahmeInfo geplant = massnahmen.getSanierungGeplant();
                dcDurchzuSanVon.setDate((geplant != null) ? geplant.getVon() : null);
                dcDurchzuSanBis.setDate((geplant != null) ? geplant.getBis() : null);
                cbGewerkDurchzu.setSelectedItem(((geplant != null) && (geplant.getGewerk() != null))
                        ? getById(cbGewerkDurchzu, geplant.getGewerk()) : null);
                cbErledigtDurchzu.setSelectedItem(((geplant != null) && (geplant.getErledigt() != null))
                        ? (geplant.getErledigt() ? "erledigt" : "nicht erledigt") : null);
                final CidsMauernSearchStatement.MassnahmeInfo durchgefuehrt = massnahmen.getSanierungDurchgefuehrt();
                dcDurchgeSanVon.setDate((durchgefuehrt != null) ? durchgefuehrt.getVon() : null);
                dcDurchgeSanBis.setDate((durchgefuehrt != null) ? durchgefuehrt.getBis() : null);
                cbGewerkDurchge.setSelectedItem(((durchgefuehrt != null) && (durchgefuehrt.getGewerk() != null))
                        ? getById(cbGewerkDurchge, durchgefuehrt.getGewerk()) : null);
                cbErledigtDurchge.setSelectedItem(((durchgefuehrt != null) && (durchgefuehrt.getErledigt() != null))
                        ? (durchgefuehrt.getErledigt() ? "erledigt" : "nicht erledigt") : null);
                final CidsMauernSearchStatement.MassnahmeInfo begehung = massnahmen.getBauwerksbegehung();
                dcBauwerksbegehVon.setDate((begehung != null) ? begehung.getVon() : null);
                dcBauwerksbegehBis.setDate((begehung != null) ? begehung.getBis() : null);
                cbErledigtBegeh.setSelectedItem(((begehung != null) && (begehung.getErledigt() != null))
                        ? (begehung.getErledigt() ? "erledigt" : "nicht erledigt") : null);
                final CidsMauernSearchStatement.MassnahmeInfo besichtigung = massnahmen.getBauwerksbesichtigung();
                dcBauwerksbesichtigVon.setDate((besichtigung != null) ? besichtigung.getVon() : null);
                dcBauwerksbesichtigBis.setDate((besichtigung != null) ? besichtigung.getBis() : null);
                cbErledigtBesichtigt.setSelectedItem(((besichtigung != null) && (besichtigung.getErledigt() != null))
                        ? (besichtigung.getErledigt() ? "erledigt" : "nicht erledigt") : null);
            }
            final CidsMauernSearchStatement.ZustaendeInfo zustaende = configuration.getZustaende();
            if (zustaende != null) {
                final CidsMauernSearchStatement.ZustandInfo gelaendeOben = zustaende.getGelaendeOben();
                tfGelaendeObenVon.setText(((gelaendeOben != null) && (gelaendeOben.getVon() != null))
                        ? Double.toString(gelaendeOben.getVon()) : null);
                tfGelaendeObenBis.setText(((gelaendeOben != null) && (gelaendeOben.getBis() != null))
                        ? Double.toString(gelaendeOben.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo gelaender = zustaende.getGelaender();
                tfGelaenderVon.setText(((gelaender != null) && (gelaender.getVon() != null))
                        ? Double.toString(gelaender.getVon()) : null);
                tfGelaenderBis.setText(((gelaender != null) && (gelaender.getBis() != null))
                        ? Double.toString(gelaender.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo wandkopf = zustaende.getWandkopf();
                tfWandkopfVon.setText(((wandkopf != null) && (wandkopf.getVon() != null))
                        ? Double.toString(wandkopf.getVon()) : null);
                tfWandkopfBis.setText(((wandkopf != null) && (wandkopf.getBis() != null))
                        ? Double.toString(wandkopf.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo ansicht = zustaende.getAnsicht();
                tfAnsichtVon.setText(((ansicht != null) && (ansicht.getVon() != null))
                        ? Double.toString(ansicht.getVon()) : null);
                tfAnsichtBis.setText(((ansicht != null) && (ansicht.getBis() != null))
                        ? Double.toString(ansicht.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo gruendung = zustaende.getGruendung();
                tfGruendungVon.setText(((gruendung != null) && (gruendung.getVon() != null))
                        ? Double.toString(gruendung.getVon()) : null);
                tfGruendungBis.setText(((gruendung != null) && (gruendung.getBis() != null))
                        ? Double.toString(gruendung.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo gelaendeUnten = zustaende.getGelaende();
                tfGelaendeVon.setText(((gelaendeUnten != null) && (gelaendeUnten.getVon() != null))
                        ? Double.toString(gelaendeUnten.getVon()) : null);
                tfGelaendeBis.setText(((gelaendeUnten != null) && (gelaendeUnten.getBis() != null))
                        ? Double.toString(gelaendeUnten.getBis()) : null);
                final CidsMauernSearchStatement.ZustandInfo bausubstanz = zustaende.getBausubstanz();
                tfBausubstanzVon.setText(((bausubstanz != null) && (bausubstanz.getVon() != null))
                        ? Double.toString(bausubstanz.getVon()) : null);
                tfBausubstanzBis.setText(((bausubstanz != null) && (bausubstanz.getBis() != null))
                        ? Double.toString(bausubstanz.getBis()) : null);
            }
            cbSanierung.setSelectedItem(getById(cbSanierung, configuration.getSanierung()));

            tfHoeheVon.setText((configuration.getHoeheVon() != null) ? Double.toString(configuration.getHoeheVon())
                                                                     : null);
            tfHoeheBis.setText((configuration.getHoeheBis() != null) ? Double.toString(configuration.getHoeheBis())
                                                                     : null);
        } else {
            rbAll.setSelected(true);
            lstEigentuemer.clearSelection();
            lstLastklasse.clearSelection();
            dcPruefVon.setDate(null);
            dcPruefBis.setDate(null);
            dcDurchzuSanVon.setDate(null);
            dcDurchzuSanBis.setDate(null);
            cbGewerkDurchzu.setSelectedItem(null);
            cbErledigtDurchzu.setSelectedItem(null);
            dcDurchgeSanVon.setDate(null);
            dcDurchgeSanBis.setDate(null);
            cbGewerkDurchge.setSelectedItem(null);
            cbErledigtDurchge.setSelectedItem(null);
            dcBauwerksbegehVon.setDate(null);
            dcBauwerksbegehBis.setDate(null);
            cbErledigtBegeh.setSelectedItem(null);
            dcBauwerksbesichtigVon.setDate(null);
            dcBauwerksbesichtigBis.setDate(null);
            cbErledigtBesichtigt.setSelectedItem(null);
            tfGelaendeObenVon.setText(null);
            tfGelaendeObenBis.setText(null);
            tfGelaenderVon.setText(null);
            tfGelaenderBis.setText(null);
            tfWandkopfVon.setText(null);
            tfWandkopfBis.setText(null);
            tfAnsichtVon.setText(null);
            tfAnsichtBis.setText(null);
            tfGruendungVon.setText(null);
            tfGruendungBis.setText(null);
            tfGelaendeVon.setText(null);
            tfGelaendeBis.setText(null);
            tfGelaendeVon.setText(null);
            tfGelaendeBis.setText(null);
            tfBausubstanzVon.setText(null);
            tfBausubstanzBis.setText(null);
            cbSanierung.setSelectedItem(null);
            tfHoeheVon.setText(null);
            tfHoeheBis.setText(null);
        }
    }

    @Override
    public String getTableName() {
        return "mauer";
    }

    @Override
    public ObjectMapper getConfigurationMapper() {
        return new CidsMauernSearchStatement().getConfigurationMapper();
    }

    @Override
    public CidsMauernSearchStatement.Configuration readConfiguration(final String configuration) throws Exception {
        return getConfigurationMapper().readValue(configuration, CidsMauernSearchStatement.Configuration.class);
    }

    @Override
    public void initFromConfiguration(final Object configuration) {
        initFromConfiguration((CidsMauernSearchStatement.Configuration)configuration);
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
