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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import java.sql.Date;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.JTextComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.CidsBeansTable;
import de.cismet.cids.custom.clientutils.CidsBeansTableModel;
import de.cismet.cids.custom.deprecated.TabbedPaneUITransparent;
import de.cismet.cids.custom.objecteditors.utils.IntegerNumberConverter;
import de.cismet.cids.custom.objecteditors.utils.NumberConverter;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.reports.wunda_blau.MauernReportGenerator;
import de.cismet.cids.custom.wunda_blau.search.server.MauerNummerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauerEditor extends javax.swing.JPanel implements RequestsFullSizeComponent,
    CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    BorderProvider,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_PROPERTIES = new String[] {
            "wann",
            "fk_art",
            "fk_objekt",
            "beschreibung",
            "ziel",
            "erledigt"
        };
    private static final String[] COLUMN_NAMES = new String[] {
            "Eintragung",
            "Maßnahme",
            "Gewerk",
            "Beschreibung",
            "Termin",
            "Erledigt"
        };
    private static final Class[] COLUMN_CLASSES = new Class[] {
            Date.class,
            CidsBean.class,
            CidsBean.class,
            String.class,
            Date.class,
            Boolean.class
        };
    private static final Boolean[] COLUMN_EDITABLES = new Boolean[] {
            false,
            true,
            true,
            true,
            true,
            true
        };

    private static final Color ROT = new Color(255, 0, 60);
    private static final Color GELB = new Color(250, 190, 40);
    private static final Color GRUEN = new Color(0, 193, 118);
    private static final Logger LOG = Logger.getLogger(MauerEditor.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private String title;
    private boolean editable;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final ZustandOverview overview = new ZustandOverview();

    private boolean filterLastFromType = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnReport;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtErstePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtLetztePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtNaechstePruefung1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEigentuemer;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbLastklasse;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbMaterialtyp;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbMauertyp;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbStuetzmauertyp;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbuchfertigstellung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcErstePruefung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcLetztePruefung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcNaechstePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dcSanierung;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    javax.swing.JLabel jLabel22;
    javax.swing.JLabel jLabel23;
    javax.swing.JLabel jLabel24;
    javax.swing.JLabel jLabel25;
    javax.swing.JLabel jLabel26;
    javax.swing.JLabel jLabel27;
    javax.swing.JLabel jLabel28;
    javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel30;
    javax.swing.JLabel jLabel31;
    javax.swing.JLabel jLabel32;
    javax.swing.JLabel jLabel33;
    javax.swing.JLabel jLabel34;
    javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    org.jdesktop.swingx.JXHyperlink jXHyperlink4;
    org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    org.jdesktop.swingx.JXHyperlink jXHyperlink6;
    org.jdesktop.swingx.JXHyperlink jXHyperlink7;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JScrollPane jspAllgemeineInfos;
    private javax.swing.JLabel lblBauwerksbuchfertigstellung;
    private javax.swing.JLabel lblBesonderheiten;
    private javax.swing.JLabel lblEigentuemer;
    private javax.swing.JLabel lblFiller10;
    private javax.swing.JLabel lblFiller11;
    private javax.swing.JLabel lblFiller7;
    private javax.swing.JLabel lblFiller8;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblHeaderAllgemein1;
    private javax.swing.JLabel lblHeaderAllgemein2;
    private javax.swing.JLabel lblHoeheMin;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblLaenge;
    private javax.swing.JLabel lblLagebeschreibung;
    private javax.swing.JLabel lblLagebezeichnung;
    private javax.swing.JLabel lblLastabstand;
    private javax.swing.JLabel lblLastklasse;
    private javax.swing.JLabel lblLetztePruefung;
    private javax.swing.JLabel lblMassnahmenHeader;
    private javax.swing.JLabel lblMaterialTyp;
    private javax.swing.JLabel lblMauerNummer;
    private javax.swing.JLabel lblMauertyp;
    private javax.swing.JLabel lblNaechstePruefung;
    private javax.swing.JLabel lblNeigung;
    private javax.swing.JLabel lblPruefung1;
    private javax.swing.JLabel lblSanierung;
    private javax.swing.JLabel lblStaerke;
    private javax.swing.JLabel lblStaerkeOben;
    private javax.swing.JLabel lblStaerkeUnten;
    private javax.swing.JLabel lblStuetzmauer;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUmgebung;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel2;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel3;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel4;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel5;
    private de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel
        mauerBauteilZustandKostenPanel7;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung;
    private javax.swing.JPanel panZusammenfassungContent;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle;
    private de.cismet.tools.gui.RoundedPanel pnlAllgemein;
    private de.cismet.tools.gui.RoundedPanel pnlAllgemein1;
    private javax.swing.JPanel pnlCard1;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderAllgemein;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderAllgemein1;
    private javax.swing.JPanel pnlHoehe;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlLeft1;
    private de.cismet.tools.gui.RoundedPanel pnlMassnahmen;
    private de.cismet.tools.gui.SemiRoundedPanel pnlMassnahmenHeader;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel1;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel2;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel3;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel4;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel5;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel6;
    de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel7;
    private javax.swing.JTextArea taBesonderheiten;
    private javax.swing.JTextArea taLagebeschreibung;
    private javax.swing.JTextArea taNeigung;
    private javax.swing.JTextField tfHoeheMax;
    private javax.swing.JTextField tfHoeheMin;
    private javax.swing.JTextField tfLaenge;
    private javax.swing.JTextField tfLagebezeichnung;
    private javax.swing.JTextField tfLastabstand;
    private javax.swing.JTextField tfMauerNummer;
    private javax.swing.JTextField tfStaerkeOben;
    private javax.swing.JTextField tfStaerke_unten;
    private javax.swing.JTextField tfUmgebung;
    private de.cismet.cids.custom.objecteditors.wunda_blau.WebDavPicturePanel webDavPicturePanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauerEditor.
     */
    public MauerEditor() {
        this(true);
    }

    /**
     * Creates a new MauerEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public MauerEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        mauerBauteilZustandKostenPanel1.initWithConnectionContext(connectionContext);
        mauerBauteilZustandKostenPanel2.initWithConnectionContext(connectionContext);
        mauerBauteilZustandKostenPanel3.initWithConnectionContext(connectionContext);
        mauerBauteilZustandKostenPanel4.initWithConnectionContext(connectionContext);
        mauerBauteilZustandKostenPanel5.initWithConnectionContext(connectionContext);
        mauerBauteilZustandKostenPanel7.initWithConnectionContext(connectionContext);

        jXTable1.setRowFilter(new LastFromTypeRowFilter());
        if (editable) {
            pnlLeft.setPreferredSize(new Dimension(500, 900));
        }
        jspAllgemeineInfos.getViewport().setOpaque(false);

        if (!editable) {
            RendererTools.makeReadOnly(jScrollPane1);
            RendererTools.makeReadOnly(jScrollPane2);
            RendererTools.makeReadOnly(jScrollPane17);
            RendererTools.makeReadOnly(taLagebeschreibung);
            RendererTools.makeReadOnly(taNeigung);
            RendererTools.makeReadOnly(tfUmgebung);
            RendererTools.makeReadOnly(tfLaenge);
            RendererTools.makeReadOnly(taLagebeschreibung);
            RendererTools.makeReadOnly(taNeigung);
            RendererTools.makeReadOnly(taBesonderheiten);
            RendererTools.makeReadOnly(tfLaenge);
            RendererTools.makeReadOnly(tfUmgebung);
            RendererTools.makeReadOnly(tfStaerkeOben);
            RendererTools.makeReadOnly(tfStaerke_unten);
            RendererTools.makeReadOnly(tfLastabstand);
            RendererTools.makeReadOnly(tfHoeheMax);
            RendererTools.makeReadOnly(tfHoeheMin);
            RendererTools.makeReadOnly(tfMauerNummer);
            RendererTools.makeReadOnly(tfLagebezeichnung);
            RendererTools.makeReadOnly(dcSanierung);
            RendererTools.makeReadOnly(cbEigentuemer);
            RendererTools.makeReadOnly(cbMaterialtyp);
            RendererTools.makeReadOnly(cbStuetzmauertyp);
            RendererTools.makeReadOnly(cbArtErstePruefung);
            RendererTools.makeReadOnly(cbArtLetztePruefung);
            RendererTools.makeReadOnly(cbArtNaechstePruefung1);
            RendererTools.makeReadOnly(cbLastklasse);
            RendererTools.makeReadOnly(cbMauertyp);
            RendererTools.makeReadOnly(dcErstePruefung);
            RendererTools.makeReadOnly(dcLetztePruefung);
            RendererTools.makeReadOnly(dcNaechstePruefung);
            RendererTools.makeReadOnly(dcBauwerksbuchfertigstellung);
            RendererTools.makeReadOnly(jTextArea1);
        }

        setLimitDocumentFilter(tfMauerNummer, 50);
        setLimitDocumentFilter(tfLagebezeichnung, 500);
        setLimitDocumentFilter(taBesonderheiten, 500);
        setLimitDocumentFilter(taLagebeschreibung, 500);
        setLimitDocumentFilter(taNeigung, 500);
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

        panFooter = new javax.swing.JPanel();
        panLeft = new javax.swing.JPanel();
        lblInfo = new javax.swing.JLabel();
        btnInfo = new javax.swing.JButton();
        panRight = new javax.swing.JPanel();
        btnImages = new javax.swing.JButton();
        lblImages = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        pnlCard1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        pnlAllgemein = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderAllgemein = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        jspAllgemeineInfos = new javax.swing.JScrollPane();
        pnlLeft = new javax.swing.JPanel();
        lblMauerNummer = new javax.swing.JLabel();
        tfMauerNummer = new javax.swing.JTextField();
        lblLagebezeichnung = new javax.swing.JLabel();
        tfLagebezeichnung = new javax.swing.JTextField();
        lblLagebeschreibung = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taLagebeschreibung = new javax.swing.JTextArea();
        lblUmgebung = new javax.swing.JLabel();
        tfUmgebung = new javax.swing.JTextField();
        lblNeigung = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taNeigung = new javax.swing.JTextArea();
        lblMauertyp = new javax.swing.JLabel();
        cbMauertyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblStuetzmauer = new javax.swing.JLabel();
        cbStuetzmauertyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblMaterialTyp = new javax.swing.JLabel();
        cbMaterialtyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblEigentuemer = new javax.swing.JLabel();
        cbEigentuemer = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblHoeheMin = new javax.swing.JLabel();
        pnlHoehe = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfHoeheMin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfHoeheMax = new javax.swing.JTextField();
        lblFiller11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblFiller7 = new javax.swing.JLabel();
        lblStaerke = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblStaerkeUnten = new javax.swing.JLabel();
        tfStaerke_unten = new javax.swing.JTextField();
        lblStaerkeOben = new javax.swing.JLabel();
        tfStaerkeOben = new javax.swing.JTextField();
        lblLaenge = new javax.swing.JLabel();
        tfLaenge = new javax.swing.JTextField();
        lblBesonderheiten = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        taBesonderheiten = new javax.swing.JTextArea();
        lblLastabstand = new javax.swing.JLabel();
        tfLastabstand = new javax.swing.JTextField();
        lblLastklasse = new javax.swing.JLabel();
        cbLastklasse = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblPruefung1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        dcErstePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel6 = new javax.swing.JLabel();
        cbArtErstePruefung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblLetztePruefung = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        dcLetztePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel14 = new javax.swing.JLabel();
        cbArtLetztePruefung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblNaechstePruefung = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        dcNaechstePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel16 = new javax.swing.JLabel();
        cbArtNaechstePruefung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblBauwerksbuchfertigstellung = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        dcBauwerksbuchfertigstellung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblFiller10 = new javax.swing.JLabel();
        lblSanierung = new javax.swing.JLabel();
        dcSanierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        if (editable) {
            lblGeom = new javax.swing.JLabel();
        }
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblFiller8 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel7 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Gelände oben",
                isEditable());
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel4 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel1 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Absturzsicherung",
                isEditable());
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel10 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel2 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Kopf",
                isEditable());
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel12 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel3 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Ansicht",
                isEditable());
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel13 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel4 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Gründung",
                isEditable());
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel14 = new javax.swing.JPanel();
        mauerBauteilZustandKostenPanel5 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.mauer.MauerBauteilZustandKostenPanel(
                this,
                "Gelände unten",
                isEditable());
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel18 = new javax.swing.JPanel();
        pnlAllgemein1 = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderAllgemein1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein2 = new javax.swing.JLabel();
        pnlLeft1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        pnlMassnahmen = new de.cismet.tools.gui.RoundedPanel();
        pnlMassnahmenHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMassnahmenHeader = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jXTable1 = new CidsBeansTable(isEditable());
        jPanel8 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        panZusammenfassung = new de.cismet.tools.gui.RoundedPanel();
        panZusammenfassungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein1 = new javax.swing.JLabel();
        panZusammenfassungContent = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jXHyperlink7 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink4 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink6 = new org.jdesktop.swingx.JXHyperlink();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        roundedPanel1 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel2 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel3 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel4 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel5 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel6 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel7 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        webDavPicturePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.WebDavPicturePanel(
                editable,
                "url_mauern",
                "bilder",
                "mauer_bilder",
                "mauer_nummer",
                "georeferenz.geo_field",
                getConnectionContext());

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                             // NOI18N
        lblInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblInfo.text")); // NOI18N
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png")));            // NOI18N
        btnInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnInfo.text")); // NOI18N
        btnInfo.setBorderPainted(false);
        btnInfo.setContentAreaFilled(false);
        btnInfo.setEnabled(false);
        btnInfo.setFocusPainted(false);
        btnInfo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnInfoActionPerformed(evt);
                }
            });
        panLeft.add(btnInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panFooter.add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);

        btnImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png")));             // NOI18N
        btnImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnImages.text")); // NOI18N
        btnImages.setBorderPainted(false);
        btnImages.setContentAreaFilled(false);
        btnImages.setFocusPainted(false);
        btnImages.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnImagesActionPerformed(evt);
                }
            });
        panRight.add(btnImages);

        lblImages.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                               // NOI18N
        lblImages.setForeground(new java.awt.Color(255, 255, 255));
        lblImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblImages.text")); // NOI18N
        panRight.add(lblImages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png")));                             // NOI18N
        btnReport.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnReport.text")); // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.btnReport.toolTipText"));                                                            // NOI18N
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setFocusPainted(false);
        btnReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

        setMaximumSize(new java.awt.Dimension(1190, 1625));
        setMinimumSize(new java.awt.Dimension(807, 485));
        setOpaque(false);
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jTabbedPane1StateChanged(evt);
                }
            });

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        pnlAllgemein.setMinimumSize(new java.awt.Dimension(540, 500));
        pnlAllgemein.setPreferredSize(new java.awt.Dimension(540, 800));
        pnlAllgemein.setLayout(new java.awt.GridBagLayout());

        pnlHeaderAllgemein.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderAllgemein.setMinimumSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein.setPreferredSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblHeaderAllgemein.text")); // NOI18N
        pnlHeaderAllgemein.add(lblHeaderAllgemein);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlAllgemein.add(pnlHeaderAllgemein, gridBagConstraints);

        jspAllgemeineInfos.setBorder(null);
        jspAllgemeineInfos.setMinimumSize(new java.awt.Dimension(500, 520));
        jspAllgemeineInfos.setOpaque(false);
        jspAllgemeineInfos.setPreferredSize(new java.awt.Dimension(500, 880));

        pnlLeft.setMinimumSize(new java.awt.Dimension(500, 790));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new java.awt.Dimension(500, 850));
        pnlLeft.setLayout(new java.awt.GridBagLayout());

        lblMauerNummer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblMauerNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblMauerNummer, gridBagConstraints);

        tfMauerNummer.setMinimumSize(new java.awt.Dimension(150, 20));
        tfMauerNummer.setPreferredSize(new java.awt.Dimension(150, 20));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mauer_nummer}"),
                tfMauerNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfMauerNummer, gridBagConstraints);

        lblLagebezeichnung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLagebezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLagebezeichnung, gridBagConstraints);

        tfLagebezeichnung.setMinimumSize(new java.awt.Dimension(100, 20));
        tfLagebezeichnung.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagebezeichnung}"),
                tfLagebezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLagebezeichnung, gridBagConstraints);

        lblLagebeschreibung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLagebeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLagebeschreibung, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(26, 40));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 50));
        jScrollPane1.setRequestFocusEnabled(false);

        taLagebeschreibung.setLineWrap(true);
        taLagebeschreibung.setMaximumSize(new java.awt.Dimension(500, 34));
        taLagebeschreibung.setMinimumSize(new java.awt.Dimension(500, 34));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagebeschreibung}"),
                taLagebeschreibung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(taLagebeschreibung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jScrollPane1, gridBagConstraints);

        lblUmgebung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblUmgebung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblUmgebung, gridBagConstraints);

        tfUmgebung.setMinimumSize(new java.awt.Dimension(100, 20));
        tfUmgebung.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebung}"),
                tfUmgebung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfUmgebung, gridBagConstraints);

        lblNeigung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblNeigung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblNeigung, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(26, 50));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(0, 50));

        taNeigung.setLineWrap(true);
        taNeigung.setMinimumSize(new java.awt.Dimension(500, 34));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.neigung}"),
                taNeigung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taNeigung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jScrollPane2, gridBagConstraints);

        lblMauertyp.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblMauertyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblMauertyp, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mauertyp}"),
                cbMauertyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbMauertyp, gridBagConstraints);

        lblStuetzmauer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblStuetzmauer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblStuetzmauer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauertyp}"),
                cbStuetzmauertyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbStuetzmauertyp, gridBagConstraints);

        lblMaterialTyp.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblMaterialTyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblMaterialTyp, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.materialtyp}"),
                cbMaterialtyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbMaterialtyp, gridBagConstraints);

        lblEigentuemer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEigentuemer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblEigentuemer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"),
                cbEigentuemer,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbEigentuemer, gridBagConstraints);

        lblHoeheMin.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblHoeheMin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblHoeheMin, gridBagConstraints);

        pnlHoehe.setOpaque(false);
        pnlHoehe.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlHoehe.add(jLabel1, gridBagConstraints);

        tfHoeheMin.setMinimumSize(new java.awt.Dimension(50, 20));
        tfHoeheMin.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe_min}"),
                tfHoeheMin,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 20);
        pnlHoehe.add(tfHoeheMin, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlHoehe.add(jLabel3, gridBagConstraints);

        tfHoeheMax.setMinimumSize(new java.awt.Dimension(50, 20));
        tfHoeheMax.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe_max}"),
                tfHoeheMax,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlHoehe.add(tfHoeheMax, gridBagConstraints);

        lblFiller11.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlHoehe.add(lblFiller11, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel3.add(jLabel4, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe}"),
                jLabel2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        lblFiller7.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lblFiller7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlHoehe.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(pnlHoehe, gridBagConstraints);

        lblStaerke.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblStaerke.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblStaerke, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblStaerkeUnten.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblStaerkeUnten.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(lblStaerkeUnten, gridBagConstraints);

        tfStaerke_unten.setMinimumSize(new java.awt.Dimension(50, 20));
        tfStaerke_unten.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.staerke_unten}"),
                tfStaerke_unten,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        tfStaerke_unten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tfStaerke_untenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel1.add(tfStaerke_unten, gridBagConstraints);

        lblStaerkeOben.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblStaerkeOben.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(lblStaerkeOben, gridBagConstraints);

        tfStaerkeOben.setMinimumSize(new java.awt.Dimension(50, 20));
        tfStaerkeOben.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.staerke_oben}"),
                tfStaerkeOben,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel1.add(tfStaerkeOben, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel1, gridBagConstraints);

        lblLaenge.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLaenge.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLaenge, gridBagConstraints);

        tfLaenge.setMinimumSize(new java.awt.Dimension(100, 20));
        tfLaenge.setPreferredSize(new java.awt.Dimension(50, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge}"),
                tfLaenge,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLaenge, gridBagConstraints);

        lblBesonderheiten.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblBesonderheiten.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblBesonderheiten, gridBagConstraints);

        jScrollPane17.setMinimumSize(new java.awt.Dimension(26, 50));
        jScrollPane17.setPreferredSize(new java.awt.Dimension(0, 50));

        taBesonderheiten.setLineWrap(true);
        taBesonderheiten.setMinimumSize(new java.awt.Dimension(500, 34));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.besonderheiten}"),
                taBesonderheiten,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane17.setViewportView(taBesonderheiten);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        pnlLeft.add(jScrollPane17, gridBagConstraints);

        lblLastabstand.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLastabstand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLastabstand, gridBagConstraints);

        tfLastabstand.setMinimumSize(new java.awt.Dimension(100, 20));
        tfLastabstand.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lastabstand}"),
                tfLastabstand,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLastabstand, gridBagConstraints);

        lblLastklasse.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLastklasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLastklasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lastklasse}"),
                cbLastklasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbLastklasse, gridBagConstraints);

        lblPruefung1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblPruefung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblPruefung1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel5, gridBagConstraints);

        dcErstePruefung.setMinimumSize(new java.awt.Dimension(124, 20));
        dcErstePruefung.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erste_pruefung}"),
                dcErstePruefung,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcErstePruefung.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel2.add(dcErstePruefung, gridBagConstraints);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel6, gridBagConstraints);

        cbArtErstePruefung.setMinimumSize(new java.awt.Dimension(120, 20));
        cbArtErstePruefung.setPreferredSize(new java.awt.Dimension(120, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_erste_pruefung}"),
                cbArtErstePruefung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(cbArtErstePruefung, gridBagConstraints);
        cbArtErstePruefung.setNullable(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel2, gridBagConstraints);

        lblLetztePruefung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLetztePruefung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLetztePruefung, gridBagConstraints);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel6.add(jLabel13, gridBagConstraints);

        dcLetztePruefung.setMinimumSize(new java.awt.Dimension(124, 20));
        dcLetztePruefung.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_letzte_pruefung}"),
                dcLetztePruefung,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcLetztePruefung.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel6.add(dcLetztePruefung, gridBagConstraints);

        jLabel14.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel6.add(jLabel14, gridBagConstraints);

        cbArtLetztePruefung.setMinimumSize(new java.awt.Dimension(120, 20));
        cbArtLetztePruefung.setPreferredSize(new java.awt.Dimension(120, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_letzte_pruefung}"),
                cbArtLetztePruefung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(cbArtLetztePruefung, gridBagConstraints);
        cbArtLetztePruefung.setNullable(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel6, gridBagConstraints);

        lblNaechstePruefung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblNaechstePruefung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblNaechstePruefung, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel15.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel7.add(jLabel15, gridBagConstraints);

        dcNaechstePruefung.setMinimumSize(new java.awt.Dimension(124, 20));
        dcNaechstePruefung.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_naechste_pruefung}"),
                dcNaechstePruefung,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcNaechstePruefung.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel7.add(dcNaechstePruefung, gridBagConstraints);

        jLabel16.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel16.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel7.add(jLabel16, gridBagConstraints);

        cbArtNaechstePruefung1.setMinimumSize(new java.awt.Dimension(120, 20));
        cbArtNaechstePruefung1.setPreferredSize(new java.awt.Dimension(120, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_naechste_pruefung}"),
                cbArtNaechstePruefung1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(cbArtNaechstePruefung1, gridBagConstraints);
        cbArtNaechstePruefung1.setNullable(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel7, gridBagConstraints);

        lblBauwerksbuchfertigstellung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblBauwerksbuchfertigstellung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblBauwerksbuchfertigstellung, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jLabel11, gridBagConstraints);

        dcBauwerksbuchfertigstellung.setMinimumSize(new java.awt.Dimension(124, 20));
        dcBauwerksbuchfertigstellung.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bauwerksbuchfertigstellung}"),
                dcBauwerksbuchfertigstellung,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcBauwerksbuchfertigstellung.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel5.add(dcBauwerksbuchfertigstellung, gridBagConstraints);

        lblFiller10.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(lblFiller10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel5, gridBagConstraints);

        lblSanierung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanierung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblSanierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sanierung}"),
                dcSanierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(dcSanierung, gridBagConstraints);

        if (editable) {
            lblGeom.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblGeom.text")); // NOI18N
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            pnlLeft.add(lblGeom, gridBagConstraints);
        }

        if (editable) {
            cbGeom.setMinimumSize(new java.awt.Dimension(41, 25));
            cbGeom.setPreferredSize(new java.awt.Dimension(41, 25));

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            pnlLeft.add(cbGeom, gridBagConstraints);
        }

        lblFiller8.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlLeft.add(lblFiller8, gridBagConstraints);

        jspAllgemeineInfos.setViewportView(pnlLeft);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAllgemein.add(jspAllgemeineInfos, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel9.add(pnlAllgemein, gridBagConstraints);

        jTabbedPane1.addTab("Allgemeine Informationen", jPanel9);

        jPanel17.setOpaque(false);
        jPanel17.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_gelaende_oben}"),
                mauerBauteilZustandKostenPanel7,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel17.add(mauerBauteilZustandKostenPanel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(filler11, gridBagConstraints);

        jTabbedPane1.addTab("Gelände oben", jPanel17);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_gelaender}"),
                mauerBauteilZustandKostenPanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel4.add(mauerBauteilZustandKostenPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(filler6, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jPanel4.TabConstraints.tabTitle"),
            jPanel4); // NOI18N

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_kopf}"),
                mauerBauteilZustandKostenPanel2,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel10.add(mauerBauteilZustandKostenPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel10.add(filler7, gridBagConstraints);

        jTabbedPane1.addTab("Kopf", jPanel10);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_ansicht}"),
                mauerBauteilZustandKostenPanel3,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel12.add(mauerBauteilZustandKostenPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(filler8, gridBagConstraints);

        jTabbedPane1.addTab("Ansicht", jPanel12);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_gruendung}"),
                mauerBauteilZustandKostenPanel4,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel13.add(mauerBauteilZustandKostenPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(filler5, gridBagConstraints);

        jTabbedPane1.addTab("Gründung", jPanel13);

        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zustand_gelaende}"),
                mauerBauteilZustandKostenPanel5,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel14.add(mauerBauteilZustandKostenPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel14.add(filler9, gridBagConstraints);

        jTabbedPane1.addTab("Gelände unten", jPanel14);

        jPanel18.setOpaque(false);
        jPanel18.setLayout(new java.awt.GridBagLayout());

        pnlAllgemein1.setLayout(new java.awt.GridBagLayout());

        pnlHeaderAllgemein1.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderAllgemein1.setMinimumSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein1.setPreferredSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein1.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein2.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblHeaderAllgemein2.text")); // NOI18N
        pnlHeaderAllgemein1.add(lblHeaderAllgemein2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlAllgemein1.add(pnlHeaderAllgemein1, gridBagConstraints);

        pnlLeft1.setOpaque(false);
        pnlLeft1.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        pnlLeft1.add(jLabel7, gridBagConstraints);

        jScrollPane3.setOpaque(false);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anlieger}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        pnlLeft1.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlAllgemein1.add(pnlLeft1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel18.add(pnlAllgemein1, gridBagConstraints);

        jTabbedPane1.addTab("Anlieger", jPanel18);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane1, gridBagConstraints);
        jTabbedPane1.setUI(new TabbedPaneUITransparent());

        pnlMassnahmen.setLayout(new java.awt.GridBagLayout());

        pnlMassnahmenHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlMassnahmenHeader.setLayout(new java.awt.FlowLayout());

        lblMassnahmenHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblMassnahmenHeader.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblMassnahmenHeader.text")); // NOI18N
        pnlMassnahmenHeader.add(lblMassnahmenHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlMassnahmen.add(pnlMassnahmenHeader, gridBagConstraints);

        jPanel16.setMinimumSize(new java.awt.Dimension(83, 150));
        jPanel16.setOpaque(false);
        jPanel16.setLayout(new java.awt.GridBagLayout());

        jScrollPane16.setOpaque(false);

        jXTable1.setModel(new MassnahmenTableModel());
        jScrollPane16.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel16.add(jScrollPane16, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jButton2, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jButton3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel16.add(jPanel8, gridBagConstraints);
        jPanel8.setVisible(isEditable());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMassnahmen.add(jPanel16, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlCard1.add(pnlMassnahmen, gridBagConstraints);

        panZusammenfassung.setLayout(new java.awt.GridBagLayout());

        panZusammenfassungTitle.setBackground(new java.awt.Color(51, 51, 51));
        panZusammenfassungTitle.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblHeaderAllgemein1.text")); // NOI18N
        panZusammenfassungTitle.add(lblHeaderAllgemein1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panZusammenfassung.add(panZusammenfassungTitle, gridBagConstraints);

        panZusammenfassungContent.setOpaque(false);
        panZusammenfassungContent.setLayout(new java.awt.GridBagLayout());

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jXHyperlink7.setText("Info");
        jXHyperlink7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink7.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink7ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink7, gridBagConstraints);

        jXHyperlink1.setText("Absturzsich.");
        jXHyperlink1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel11.add(jXHyperlink1, gridBagConstraints);

        jXHyperlink2.setText("Kopf");
        jXHyperlink2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink2, gridBagConstraints);

        jXHyperlink3.setText("Ansicht");
        jXHyperlink3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink3, gridBagConstraints);

        jXHyperlink4.setText("Gründung");
        jXHyperlink4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink4, gridBagConstraints);

        jXHyperlink5.setText("Gelände o.");
        jXHyperlink5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink5, gridBagConstraints);

        jXHyperlink6.setText("Gelände u.");
        jXHyperlink6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXHyperlink6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jXHyperlink6, gridBagConstraints);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("<html><b>Gesamt");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jLabel19, gridBagConstraints);

        jLabel21.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel21, gridBagConstraints);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel23.text")); // NOI18N
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel23, gridBagConstraints);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel24.text")); // NOI18N
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel24, gridBagConstraints);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel25.text")); // NOI18N
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel25, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel26.text")); // NOI18N
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel26, gridBagConstraints);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel27.text")); // NOI18N
        jLabel27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel27, gridBagConstraints);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel22.text")); // NOI18N
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel22, gridBagConstraints);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel28.text")); // NOI18N
        jLabel28.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel28, gridBagConstraints);

        jLabel20.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel20, gridBagConstraints);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel29.text")); // NOI18N
        jLabel29.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel29.toolTipText"));                                                           // NOI18N
        jLabel29.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel29, gridBagConstraints);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel30.text")); // NOI18N
        jLabel30.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel30.toolTipText"));                                                           // NOI18N
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel30, gridBagConstraints);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel31.text")); // NOI18N
        jLabel31.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel31.toolTipText"));                                                           // NOI18N
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel31, gridBagConstraints);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel32.text")); // NOI18N
        jLabel32.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel32.toolTipText"));                                                           // NOI18N
        jLabel32.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel32, gridBagConstraints);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel33.text")); // NOI18N
        jLabel33.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel33.toolTipText"));                                                           // NOI18N
        jLabel33.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel33, gridBagConstraints);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel34.text")); // NOI18N
        jLabel34.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel34.toolTipText"));                                                           // NOI18N
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel34, gridBagConstraints);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel35.text")); // NOI18N
        jLabel35.setToolTipText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.jLabel35.toolTipText"));                                                           // NOI18N
        jLabel35.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 5);
        jPanel11.add(jLabel35, gridBagConstraints);

        roundedPanel1.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel1, gridBagConstraints);

        roundedPanel2.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel2, gridBagConstraints);

        roundedPanel3.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel3.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel3, gridBagConstraints);

        roundedPanel4.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel4.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel4Layout = new javax.swing.GroupLayout(roundedPanel4);
        roundedPanel4.setLayout(roundedPanel4Layout);
        roundedPanel4Layout.setHorizontalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel4Layout.setVerticalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel4, gridBagConstraints);

        roundedPanel5.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel5.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel5, gridBagConstraints);

        roundedPanel6.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel6.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel6, gridBagConstraints);

        roundedPanel7.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel7.setMinimumSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel7Layout = new javax.swing.GroupLayout(roundedPanel7);
        roundedPanel7.setLayout(roundedPanel7Layout);
        roundedPanel7Layout.setHorizontalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel7Layout.setVerticalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZusammenfassungContent.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusammenfassung.add(panZusammenfassungContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlCard1.add(panZusammenfassung, gridBagConstraints);

        add(pnlCard1, "card1");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean}"),
                webDavPicturePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        add(webDavPicturePanel1, "card2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnImagesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnImagesActionPerformed
        ((CardLayout)getLayout()).show(this, "card2");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }                                                                             //GEN-LAST:event_btnImagesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnInfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnInfoActionPerformed
        ((CardLayout)getLayout()).show(this, "card1");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }                                                                           //GEN-LAST:event_btnInfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        final Collection<CidsBean> c = new LinkedList<>();
        c.add(cidsBean);
        MauernReportGenerator.generateKatasterBlatt(c, MauerEditor.this, getConnectionContext());
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTabbedPane1StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jTabbedPane1StateChanged
        overview.recalculateAll();
        updateLinks();
    }                                                                                //GEN-LAST:event_jTabbedPane1StateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink6ActionPerformed
        jumpToTab(6, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink5ActionPerformed
        jumpToTab(1, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink4ActionPerformed
        jumpToTab(5, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink3ActionPerformed
        jumpToTab(4, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink2ActionPerformed
        jumpToTab(3, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        jumpToTab(2, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final CidsBean massnahmeBean = ((MassnahmenTableModel)jXTable1.getModel()).getCidsBean(jXTable1.getRowSorter()
                        .convertRowIndexToModel(
                            jXTable1.getSelectedRow()));
        ((MassnahmenTableModel)jXTable1.getModel()).remove(massnahmeBean);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        try {
            final CidsBean massnahmeBean = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    "mauer_massnahme",
                    getConnectionContext());
            massnahmeBean.setProperty("wann", new Date(new java.util.Date().getTime()));
            ((MassnahmenTableModel)jXTable1.getModel()).add(massnahmeBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tfStaerke_untenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tfStaerke_untenActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_tfStaerke_untenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink7ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink7ActionPerformed
        jumpToTab(0, (JXHyperlink)evt.getSource());
    }                                                                                //GEN-LAST:event_jXHyperlink7ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  index  DOCUMENT ME!
     * @param  link   DOCUMENT ME!
     */
    private void jumpToTab(final int index, final JXHyperlink link) {
        jTabbedPane1.setSelectedIndex(index);
        updateLinks();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateLinks() {
        final JXHyperlink link;
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0: {
                link = jXHyperlink7;
            }
            break;
            case 1: {
                link = jXHyperlink5;
            }
            break;
            case 2: {
                link = jXHyperlink1;
            }
            break;
            case 3: {
                link = jXHyperlink2;
            }
            break;
            case 4: {
                link = jXHyperlink3;
            }
            break;
            case 5: {
                link = jXHyperlink4;
            }
            break;
            case 6: {
                link = jXHyperlink6;
            }
            break;
            default: {
                link = null;
            }
        }

        jXHyperlink1.setEnabled(!jXHyperlink1.equals(link));
        jXHyperlink2.setEnabled(!jXHyperlink2.equals(link));
        jXHyperlink3.setEnabled(!jXHyperlink3.equals(link));
        jXHyperlink4.setEnabled(!jXHyperlink4.equals(link));
        jXHyperlink5.setEnabled(!jXHyperlink5.equals(link));
        jXHyperlink6.setEnabled(!jXHyperlink6.equals(link));
        jXHyperlink7.setEnabled(!jXHyperlink7.equals(link));
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

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            this.cidsBean = cidsBean;
            final String lagebez = (String)cidsBean.getProperty("lagebezeichnung");
            this.title = NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblTitle.prefix")
                        + ((lagebez != null) ? lagebez : "");
            lblTitle.setText(this.title);
            ((MassnahmenTableModel)jXTable1.getModel()).setCidsBeans(cidsBean.getBeanCollectionProperty(
                    "n_massnahmen"));

            try {
                final MetaClass mcZustand = CidsBean.getMetaClassFromTableName(
                        "WUNDA_BLAU",
                        "mauer_zustand",
                        getConnectionContext());
                if (cidsBean.getProperty("fk_zustand_gelaender") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_gelaender",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                if (cidsBean.getProperty("fk_zustand_kopf") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_kopf",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                if (cidsBean.getProperty("fk_zustand_ansicht") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_ansicht",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                if (cidsBean.getProperty("fk_zustand_gruendung") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_gruendung",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                if (cidsBean.getProperty("fk_zustand_gelaende_oben") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_gelaende_oben",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                if (cidsBean.getProperty("fk_zustand_gelaende") == null) {
                    cidsBean.setProperty(
                        "fk_zustand_gelaende",
                        mcZustand.getEmptyInstance(getConnectionContext()).getBean());
                }
                recalculateOverview();
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        bindingGroup.bind();
        jumpToTab(0, jXHyperlink7);
    }

    /**
     * DOCUMENT ME!
     */
    public void recalculateOverview() {
        overview.recalculateAll();
    }

    @Override
    public void dispose() {
        webDavPicturePanel1.dispose();
        mauerBauteilZustandKostenPanel1.dispose();
        mauerBauteilZustandKostenPanel2.dispose();
        mauerBauteilZustandKostenPanel3.dispose();
        mauerBauteilZustandKostenPanel4.dispose();
        mauerBauteilZustandKostenPanel5.dispose();
        mauerBauteilZustandKostenPanel7.dispose();
        if (cbGeom != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        return String.valueOf(cidsBean);
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
        this.title = NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblTitle.prefix") + title;
        lblTitle.setText(this.title);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "mauer",
            1,
            1280,
            1024);
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        webDavPicturePanel1.editorClosed(event);
    }

    @Override
    public boolean prepareForSave() {
        try {
            LOG.info("prepare for save");
            final String mauerNummer = (String)cidsBean.getProperty("mauer_nummer");
            final String lagebezeichnung = (String)cidsBean.getProperty("lagebezeichnung");
            if ((lagebezeichnung == null) || lagebezeichnung.trim().equals("")) {
                LOG.warn("lagebezeichnung must not be null or empty");
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    "Das Feld Lagebezeichnung muss ausgefüllt sein.",
                    "Fehlerhafte Eingaben",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // check if the mauer nummer is already used for another mauer object
            if (mauerNummer != null) {
                final CidsServerSearch search = new MauerNummerSearch(mauerNummer);
                final Collection res = SessionManager.getProxy()
                            .customServerSearch(SessionManager.getSession().getUser(),
                                search,
                                getConnectionContext());

                final ArrayList<ArrayList> tmp = (ArrayList<ArrayList>)res;

                if (tmp.size() > 0) {
                    final ArrayList resMauer = tmp.get(0);
                    final Integer id = (Integer)resMauer.get(0);
                    final Integer objId = (Integer)cidsBean.getProperty("id");
                    if (id.intValue() != objId.intValue()) {
                        LOG.warn("mauernummer " + mauerNummer + "already exists");
                        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                            "Die angegebene Mauernummer existiert bereits.",
                            "Fehlerhafte Eingaben",
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
            return true;
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ta       DOCUMENT ME!
     * @param  maxSize  DOCUMENT ME!
     */
    public static void setLimitDocumentFilter(final JTextComponent ta, final int maxSize) {
        if (ta.getDocument() instanceof AbstractDocument) {
            final AbstractDocument sd = (AbstractDocument)ta.getDocument();
            sd.setDocumentFilter(new DocumentSizeFilter(maxSize));
        }
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bi         DOCUMENT ME!
     * @param   component  DOCUMENT ME!
     * @param   insetX     DOCUMENT ME!
     * @param   insetY     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image adjustScale(final BufferedImage bi,
            final JComponent component,
            final int insetX,
            final int insetY) {
        final double scalex = (double)component.getWidth() / bi.getWidth();
        final double scaley = (double)component.getHeight() / bi.getHeight();
        final double scale = Math.min(scalex, scaley);
        if (scale <= 1d) {
            return bi.getScaledInstance((int)(bi.getWidth() * scale) - insetX,
                    (int)(bi.getHeight() * scale)
                            - insetY,
                    Image.SCALE_SMOOTH);
        } else {
            return bi;
        }
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(new Insets(10, 20, 10, 25));
    }

    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(new Insets(0, 0, 10, 0));
    }

    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(new Insets(10, 10, 10, 10));
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MassnahmenTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VorgangFlaecheTableModel object.
         */
        public MassnahmenTableModel() {
            super(COLUMN_PROPERTIES, COLUMN_NAMES, COLUMN_CLASSES, COLUMN_EDITABLES);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LastFromTypeRowFilter extends RowFilter<TableModel, Integer> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public MassnahmenTableModel getModel() {
            return (MassnahmenTableModel)jXTable1.getModel();
        }

        @Override
        public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
            if (!filterLastFromType) {
                return true;
            }
            final List<CidsBean> cidsBeans = getModel().getCidsBeans();
            if (cidsBeans == null) {
                return false;
            }
            final Map<String, CidsBean> lastFrom = new HashMap<>();
            final CidsBean filterBean = cidsBeans.get(entry.getIdentifier());
            if (filterBean != null) {
                for (final CidsBean massnahmeBean : cidsBeans) {
                    final CidsBean artBean = (massnahmeBean != null) ? (CidsBean)massnahmeBean.getProperty("fk_art")
                                                                     : null;
                    final String artSchlussel = (artBean != null) ? (String)artBean.getProperty("schluessel") : null;
                    final CidsBean lastBean = (artSchlussel != null) ? lastFrom.get(artSchlussel) : null;
                    if (lastBean == null) {
                        lastFrom.put(artSchlussel, massnahmeBean);
                    } else {
                        final Date compareDate = (massnahmeBean != null) ? (Date)massnahmeBean.getProperty("ziel")
                                                                         : null;
                        final Date lastDate = (Date)lastBean.getProperty("wann");
                        if ((compareDate != null) && compareDate.after(lastDate)) {
                            lastFrom.put(artSchlussel, massnahmeBean);
                        }
                    }
                }
            }
            return (filterBean == null) || lastFrom.containsValue(filterBean);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    public class ZustandOverview {

        //~ Instance fields ----------------------------------------------------

        private double zustandGesamt = 0;
        private double kostenGesamt = 0;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void recalculateAll() {
            if (cidsBean != null) {
                recalculateGesamt();
            }
            refreshView();
        }

        /**
         * DOCUMENT ME!
         *
         * @param   backgroundColor  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private Color calculateBestForegroundColor(final Color backgroundColor) {
            final double luminance = (0.2126 * backgroundColor.getRed()) + (0.7152 * backgroundColor.getGreen())
                        + (0.0722 * backgroundColor.getBlue());
            return (luminance < 140) ? Color.WHITE : Color.BLACK;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  zustand  DOCUMENT ME!
         * @param  label    DOCUMENT ME!
         * @param  panel    DOCUMENT ME!
         */
        private void refreshZustand(final Double zustand, final JLabel label, final JPanel panel) {
            if (zustand != null) {
                final NumberFormat formatZustand = new DecimalFormat("#.#");
                if (zustand < 2) {
                    panel.setBackground(GRUEN);
                } else if (zustand < 3) {
                    panel.setBackground(GELB);
                } else {
                    panel.setBackground(ROT);
                }
                label.setForeground(calculateBestForegroundColor(panel.getBackground()));
                label.setText(formatZustand.format(zustand));
            } else {
                label.setText("-");
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  kosten  DOCUMENT ME!
         * @param  label   DOCUMENT ME!
         */
        private void refreshKosten(final Double kosten, final JLabel label) {
            if (kosten != null) {
                final NumberFormat formatKosten = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                formatKosten.setCurrency(Currency.getInstance("EUR"));
                label.setText(formatKosten.format(kosten));
            } else {
                label.setText("-");
            }
        }

        /**
         * DOCUMENT ME!
         */
        public void refreshView() {
            refreshZustand((getCidsBean() != null)
                    ? (Double)getCidsBean().getProperty("fk_zustand_gelaende_oben.gesamt") : null,
                jLabel27,
                roundedPanel5);
            refreshZustand((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaender.gesamt")
                                                   : null,
                jLabel23,
                roundedPanel1);
            refreshZustand((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_kopf.gesamt") : null,
                jLabel24,
                roundedPanel2);
            refreshZustand((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_ansicht.gesamt")
                                                   : null,
                jLabel25,
                roundedPanel3);
            refreshZustand((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gruendung.gesamt")
                                                   : null,
                jLabel26,
                roundedPanel4);
            refreshZustand((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende.gesamt")
                                                   : null,
                jLabel22,
                roundedPanel6);
            refreshZustand(getZustandGesamt(), jLabel28, roundedPanel7);

            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty(
                    "fk_zustand_gelaende_oben.kosten") : null,
                jLabel33);
            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaender.kosten")
                                                  : null,
                jLabel29);
            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_kopf.kosten") : null,
                jLabel30);
            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_ansicht.kosten")
                                                  : null,
                jLabel31);
            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gruendung.kosten")
                                                  : null,
                jLabel32);
            refreshKosten((getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende.kosten")
                                                  : null,
                jLabel34);
            refreshKosten(getKostenGesamt(), jLabel35);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateGesamt() {
            final Double[] kostenAll = new Double[] {
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende_oben.kosten")
                                            : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaender.kosten") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_kopf.kosten") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_ansicht.kosten") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gruendung.kosten") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende.kosten") : null
                };
            final Double[] zustandAll = new Double[] {
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende_oben.gesamt")
                                            : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaender.gesamt") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_kopf.gesamt") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_ansicht.gesamt") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gruendung.gesamt") : null,
                    (getCidsBean() != null) ? (Double)getCidsBean().getProperty("fk_zustand_gelaende.gesamt") : null
                };

            double kosten = 0;
            double zustand = 0;
            for (int i = 0; i < kostenAll.length; i++) {
                kosten += (kostenAll[i] != null) ? kostenAll[i] : 0;
                if ((zustandAll[i] != null) && (zustandAll[i] > zustand)) {
                    zustand = zustandAll[i];
                }
            }

            setKostenGesamt(kosten);
            setZustandGesamt(zustand);
            if (isEditable()) {
                try {
                    cidsBean.setProperty("zustand_gesamt", zustand);
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class IntegerToLongConverter extends Converter<Integer, Long> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Long convertForward(final Integer i) {
            if (i == null) {
                return null;
            }
            return i.longValue();
        }

        @Override
        public Integer convertReverse(final Long l) {
            if (l == null) {
                return null;
            }
            return l.intValue();
        }
    }
}
