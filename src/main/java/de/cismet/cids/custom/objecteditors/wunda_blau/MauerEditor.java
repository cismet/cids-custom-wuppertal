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

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.JTextComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.DoubleNumberConverter;
import de.cismet.cids.custom.objecteditors.utils.IntegerNumberConverter;
import de.cismet.cids.custom.objecteditors.utils.NumberConverter;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.reports.wunda_blau.MauernReportGenerator;
import de.cismet.cids.custom.wunda_blau.search.server.MauerNummerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;
import de.cismet.cids.server.connectioncontext.ConnectionContextProvider;
import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

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
    ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private String title;
    private final Logger log = Logger.getLogger(MauerEditor.class);
    private boolean editable;
    private CardLayout cardLayout;
    private final ClientConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnReport;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtErstePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtLetztePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArtNaechstePruefung1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbDauerhaftigkeit;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEigentuemer;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffAnsicht;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffGelaende;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffGelaender;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffGruendung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffKopf;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbEingriffVerformung;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbLastklasse;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbMaterialtyp;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbMauertyp;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbStandsicherheit;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbStuetzmauertyp;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbVerkehrssicherheit;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauwerksbuchfertigstellung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcErstePruefung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcLetztePruefung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcNaechstePruefung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dcSanierung;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jspAllgemeineInfos;
    private javax.swing.JLabel lblBauwerksbuchfertigstellung;
    private javax.swing.JLabel lblBeschreibungGelaender;
    private javax.swing.JLabel lblBesonderheiten;
    private javax.swing.JLabel lblDauerhaftigkeit;
    private javax.swing.JLabel lblEigentuemer;
    private javax.swing.JLabel lblEingriffAnsicht;
    private javax.swing.JLabel lblEingriffAnsicht1;
    private javax.swing.JLabel lblEingriffAnsicht2;
    private javax.swing.JLabel lblEingriffAnsicht3;
    private javax.swing.JLabel lblEingriffGeleander;
    private javax.swing.JLabel lblEingriffKopf;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller1;
    private javax.swing.JLabel lblFiller10;
    private javax.swing.JLabel lblFiller11;
    private javax.swing.JLabel lblFiller3;
    private javax.swing.JLabel lblFiller4;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblFiller7;
    private javax.swing.JLabel lblFiller8;
    private javax.swing.JLabel lblGelaenderHeader;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblHoeheMin;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblKofpAnsicht;
    private javax.swing.JLabel lblKofpAnsicht1;
    private javax.swing.JLabel lblKofpAnsicht2;
    private javax.swing.JLabel lblKofpAnsicht3;
    private javax.swing.JLabel lblKofpHeader;
    private javax.swing.JLabel lblLaenge;
    private javax.swing.JLabel lblLagebeschreibung;
    private javax.swing.JLabel lblLagebezeichnung;
    private javax.swing.JLabel lblLastabstand;
    private javax.swing.JLabel lblLastklasse;
    private javax.swing.JLabel lblLetztePruefung;
    private javax.swing.JLabel lblMaterialTyp;
    private javax.swing.JLabel lblMauerNummer;
    private javax.swing.JLabel lblMauertyp;
    private javax.swing.JLabel lblNaechstePruefung;
    private javax.swing.JLabel lblNeigung;
    private javax.swing.JLabel lblPruefung1;
    private javax.swing.JLabel lblSanKostenAnsicht;
    private javax.swing.JLabel lblSanKostenAnsicht1;
    private javax.swing.JLabel lblSanKostenAnsicht2;
    private javax.swing.JLabel lblSanKostenAnsicht3;
    private javax.swing.JLabel lblSanKostenGelaender;
    private javax.swing.JLabel lblSanKostenKopf;
    private javax.swing.JLabel lblSanMassnahmenAnsicht;
    private javax.swing.JLabel lblSanMassnahmenGelaender;
    private javax.swing.JLabel lblSanMassnahmenGruendung;
    private javax.swing.JLabel lblSanMassnahmenGruendung1;
    private javax.swing.JLabel lblSanMassnahmenGruendung2;
    private javax.swing.JLabel lblSanMassnahmenKopf;
    private javax.swing.JLabel lblSanierung;
    private javax.swing.JLabel lblStaerke;
    private javax.swing.JLabel lblStaerkeOben;
    private javax.swing.JLabel lblStaerkeUnten;
    private javax.swing.JLabel lblStandsicherheit;
    private javax.swing.JLabel lblStuetzmauer;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUmgebung;
    private javax.swing.JLabel lblVerkehrssicherheit;
    private javax.swing.JLabel lblZustandAnsicht;
    private javax.swing.JLabel lblZustandGelaender;
    private javax.swing.JLabel lblZustandGesamt;
    private javax.swing.JLabel lblZustandGruendung;
    private javax.swing.JLabel lblZustandGruendung1;
    private javax.swing.JLabel lblZustandGruendung2;
    private javax.swing.JLabel lblZustandKopf;
    private javax.swing.JLabel lblbeschreibungAnsicht;
    private javax.swing.JLabel lblbeschreibungGruendung;
    private javax.swing.JLabel lblbeschreibungGruendung1;
    private javax.swing.JLabel lblbeschreibungGruendung2;
    private javax.swing.JLabel lblbeschreibungKopf;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.RoundedPanel pnlAllgemein;
    private de.cismet.tools.gui.RoundedPanel pnlAnsicht;
    private javax.swing.JPanel pnlCard1;
    private de.cismet.tools.gui.RoundedPanel pnlGelaende;
    private de.cismet.tools.gui.RoundedPanel pnlGelaender;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGelaenderHeader;
    private de.cismet.tools.gui.RoundedPanel pnlGruendung;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGruendungHeader;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGruendungHeader1;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGruendungHeader2;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderAllgemein;
    private javax.swing.JPanel pnlHoehe;
    private de.cismet.tools.gui.RoundedPanel pnlKopf;
    private de.cismet.tools.gui.SemiRoundedPanel pnlKopfAnsicht;
    private de.cismet.tools.gui.SemiRoundedPanel pnlKopfHeader;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlScrollPane;
    private de.cismet.tools.gui.RoundedPanel pnlVerformung;
    private de.cismet.tools.gui.RoundedPanel roundedScrollPanel;
    private javax.swing.JTextArea taBeschreibungAnsicht;
    private javax.swing.JTextArea taBeschreibungGelaender;
    private javax.swing.JTextArea taBeschreibungGruendung;
    private javax.swing.JTextArea taBeschreibungGruendung1;
    private javax.swing.JTextArea taBeschreibungGruendung2;
    private javax.swing.JTextArea taBeschreibungKopf;
    private javax.swing.JTextArea taBesonderheiten;
    private javax.swing.JTextArea taLagebeschreibung;
    private javax.swing.JTextArea taNeigung;
    private javax.swing.JTextArea taSanMassnahmeAnsicht;
    private javax.swing.JTextArea taSanMassnahmeGelaender;
    private javax.swing.JTextArea taSanMassnahmeGruendung;
    private javax.swing.JTextArea taSanMassnahmeGruendung1;
    private javax.swing.JTextArea taSanMassnahmeGruendung2;
    private javax.swing.JTextArea taSanMassnahmeKopf;
    private javax.swing.JTextField tfHoeheMax;
    private javax.swing.JTextField tfHoeheMin;
    private javax.swing.JTextField tfLaenge;
    private javax.swing.JTextField tfLagebezeichnung;
    private javax.swing.JTextField tfLastabstand;
    private javax.swing.JTextField tfMauerNummer;
    private javax.swing.JTextField tfSanKostenAnsicht;
    private javax.swing.JTextField tfSanKostenGelaender;
    private javax.swing.JTextField tfSanKostenGruendung;
    private javax.swing.JTextField tfSanKostenGruendung1;
    private javax.swing.JTextField tfSanKostenGruendung2;
    private javax.swing.JTextField tfSanKostenKopf;
    private javax.swing.JTextField tfStaerkeOben;
    private javax.swing.JTextField tfStaerke_unten;
    private javax.swing.JTextField tfUmgebung;
    private javax.swing.JTextField tfZustandAnsicht;
    private javax.swing.JTextField tfZustandGelaender;
    private javax.swing.JTextField tfZustandGesamt;
    private javax.swing.JTextField tfZustandGruendung;
    private javax.swing.JTextField tfZustandGruendung1;
    private javax.swing.JTextField tfZustandGruendung2;
    private javax.swing.JTextField tfZustandKopf;
    private de.cismet.cids.custom.objecteditors.wunda_blau.WebDavPicturePanel webDavPicturePanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauerEditor.
     */
    public MauerEditor() {
        this(true, null);
    }

    /**
     * Creates a new MauerEditor object.
     *
     * @param  editable           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public MauerEditor(final boolean editable, final ClientConnectionContext connectionContext) {
        this.editable = editable;
        this.connectionContext = connectionContext;

        initComponents();
        if (editable) {
            pnlLeft.setPreferredSize(new Dimension(500, 900));
        }
        jScrollPane3.getViewport().setOpaque(false);
        jspAllgemeineInfos.getViewport().setOpaque(false);

        if (!editable) {
            RendererTools.makeReadOnly(jScrollPane1);
            RendererTools.makeReadOnly(jScrollPane2);
            RendererTools.makeReadOnly(jScrollPane4);
            RendererTools.makeReadOnly(jScrollPane5);
            RendererTools.makeReadOnly(jScrollPane6);
            RendererTools.makeReadOnly(jScrollPane7);
            RendererTools.makeReadOnly(jScrollPane8);
            RendererTools.makeReadOnly(jScrollPane9);
            RendererTools.makeReadOnly(jScrollPane10);
            RendererTools.makeReadOnly(jScrollPane11);
            RendererTools.makeReadOnly(jScrollPane12);
            RendererTools.makeReadOnly(jScrollPane13);
            RendererTools.makeReadOnly(jScrollPane14);
            RendererTools.makeReadOnly(jScrollPane15);
            RendererTools.makeReadOnly(jScrollPane17);
            RendererTools.makeReadOnly(taLagebeschreibung);
            RendererTools.makeReadOnly(taNeigung);
            RendererTools.makeReadOnly(tfUmgebung);
            RendererTools.makeReadOnly(tfLaenge);
            RendererTools.makeReadOnly(taBeschreibungAnsicht);
            RendererTools.makeReadOnly(taBeschreibungGelaender);
            RendererTools.makeReadOnly(taBeschreibungGruendung);
            RendererTools.makeReadOnly(taBeschreibungGruendung1);
            RendererTools.makeReadOnly(taBeschreibungGruendung2);
            RendererTools.makeReadOnly(taBeschreibungKopf);
            RendererTools.makeReadOnly(taLagebeschreibung);
            RendererTools.makeReadOnly(taNeigung);
            RendererTools.makeReadOnly(taSanMassnahmeAnsicht);
            RendererTools.makeReadOnly(taSanMassnahmeGelaender);
            RendererTools.makeReadOnly(taSanMassnahmeGruendung);
            RendererTools.makeReadOnly(taSanMassnahmeGruendung1);
            RendererTools.makeReadOnly(taSanMassnahmeGruendung2);
            RendererTools.makeReadOnly(taSanMassnahmeKopf);
            RendererTools.makeReadOnly(taBesonderheiten);
            RendererTools.makeReadOnly(tfLaenge);
            RendererTools.makeReadOnly(tfSanKostenAnsicht);
            RendererTools.makeReadOnly(tfSanKostenGelaender);
            RendererTools.makeReadOnly(tfSanKostenGruendung);
            RendererTools.makeReadOnly(tfSanKostenGruendung1);
            RendererTools.makeReadOnly(tfSanKostenGruendung2);
            RendererTools.makeReadOnly(tfSanKostenKopf);
            RendererTools.makeReadOnly(tfUmgebung);
            RendererTools.makeReadOnly(tfZustandAnsicht);
            RendererTools.makeReadOnly(tfZustandGelaender);
            RendererTools.makeReadOnly(tfZustandGruendung);
            RendererTools.makeReadOnly(tfZustandGruendung1);
            RendererTools.makeReadOnly(tfZustandGruendung2);
            RendererTools.makeReadOnly(tfZustandKopf);
            RendererTools.makeReadOnly(tfStaerkeOben);
            RendererTools.makeReadOnly(tfStaerke_unten);
            RendererTools.makeReadOnly(tfLastabstand);
            RendererTools.makeReadOnly(tfHoeheMax);
            RendererTools.makeReadOnly(tfHoeheMin);
            RendererTools.makeReadOnly(tfMauerNummer);
            RendererTools.makeReadOnly(tfLagebezeichnung);
            RendererTools.makeReadOnly(dcSanierung);
            RendererTools.makeReadOnly(tfZustandGesamt);
            RendererTools.makeReadOnly(cbEigentuemer);
            RendererTools.makeReadOnly(cbMaterialtyp);
            RendererTools.makeReadOnly(cbStuetzmauertyp);
            RendererTools.makeReadOnly(cbArtErstePruefung);
            RendererTools.makeReadOnly(cbArtLetztePruefung);
            RendererTools.makeReadOnly(cbArtNaechstePruefung1);
            RendererTools.makeReadOnly(cbStandsicherheit);
            RendererTools.makeReadOnly(cbVerkehrssicherheit);
            RendererTools.makeReadOnly(cbDauerhaftigkeit);
            RendererTools.makeReadOnly(cbLastklasse);
            RendererTools.makeReadOnly(cbMauertyp);
            RendererTools.makeReadOnly(cbEingriffAnsicht);
            RendererTools.makeReadOnly(cbEingriffGelaende);
            RendererTools.makeReadOnly(cbEingriffGelaender);
            RendererTools.makeReadOnly(cbEingriffGruendung);
            RendererTools.makeReadOnly(cbEingriffKopf);
            RendererTools.makeReadOnly(cbEingriffVerformung);
            RendererTools.makeReadOnly(dcErstePruefung);
            RendererTools.makeReadOnly(dcLetztePruefung);
            RendererTools.makeReadOnly(dcNaechstePruefung);
            RendererTools.makeReadOnly(dcBauwerksbuchfertigstellung);
        }

        setLimitDocumentFilter(tfMauerNummer, 50);
        setLimitDocumentFilter(tfLagebezeichnung, 500);
        setLimitDocumentFilter(taBeschreibungAnsicht, 500);
        setLimitDocumentFilter(taBeschreibungGelaender, 500);
        setLimitDocumentFilter(taBeschreibungGruendung, 500);
        setLimitDocumentFilter(taBeschreibungGruendung1, 500);
        setLimitDocumentFilter(taBeschreibungGruendung2, 500);
        setLimitDocumentFilter(taBeschreibungKopf, 500);
        setLimitDocumentFilter(taBesonderheiten, 500);
        setLimitDocumentFilter(taLagebeschreibung, 500);
        setLimitDocumentFilter(taNeigung, 500);
        setLimitDocumentFilter(taSanMassnahmeAnsicht, 500);
        setLimitDocumentFilter(taSanMassnahmeGelaender, 500);
        setLimitDocumentFilter(taSanMassnahmeGruendung, 500);
        setLimitDocumentFilter(taSanMassnahmeGruendung1, 500);
        setLimitDocumentFilter(taSanMassnahmeGruendung2, 500);
        setLimitDocumentFilter(taSanMassnahmeKopf, 500);
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
        pnlAllgemein = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderAllgemein = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        jspAllgemeineInfos = new javax.swing.JScrollPane();
        pnlLeft = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taNeigung = new javax.swing.JTextArea();
        cbMaterialtyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblStaerke = new javax.swing.JLabel();
        lblStuetzmauer = new javax.swing.JLabel();
        cbEigentuemer = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblLagebeschreibung = new javax.swing.JLabel();
        lblHoeheMin = new javax.swing.JLabel();
        tfUmgebung = new javax.swing.JTextField();
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
        lblEigentuemer = new javax.swing.JLabel();
        lblNeigung = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taLagebeschreibung = new javax.swing.JTextArea();
        lblLaenge = new javax.swing.JLabel();
        lblUmgebung = new javax.swing.JLabel();
        tfLaenge = new javax.swing.JTextField();
        lblMaterialTyp = new javax.swing.JLabel();
        cbStuetzmauertyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel1 = new javax.swing.JPanel();
        lblStaerkeUnten = new javax.swing.JLabel();
        tfStaerke_unten = new javax.swing.JTextField();
        lblStaerkeOben = new javax.swing.JLabel();
        tfStaerkeOben = new javax.swing.JTextField();
        lblBesonderheiten = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        taBesonderheiten = new javax.swing.JTextArea();
        lblLagebezeichnung = new javax.swing.JLabel();
        tfLagebezeichnung = new javax.swing.JTextField();
        lblMauerNummer = new javax.swing.JLabel();
        tfMauerNummer = new javax.swing.JTextField();
        lblMauertyp = new javax.swing.JLabel();
        cbMauertyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblLastabstand = new javax.swing.JLabel();
        tfLastabstand = new javax.swing.JTextField();
        lblLastklasse = new javax.swing.JLabel();
        lblDauerhaftigkeit = new javax.swing.JLabel();
        lblVerkehrssicherheit = new javax.swing.JLabel();
        lblStandsicherheit = new javax.swing.JLabel();
        lblPruefung1 = new javax.swing.JLabel();
        lblLetztePruefung = new javax.swing.JLabel();
        lblNaechstePruefung = new javax.swing.JLabel();
        lblBauwerksbuchfertigstellung = new javax.swing.JLabel();
        lblSanierung = new javax.swing.JLabel();
        if (editable) {
            lblGeom = new javax.swing.JLabel();
        }
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        dcBauwerksbuchfertigstellung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblFiller10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        dcNaechstePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel16 = new javax.swing.JLabel();
        cbArtNaechstePruefung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        dcLetztePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel14 = new javax.swing.JLabel();
        cbArtLetztePruefung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        dcErstePruefung = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel6 = new javax.swing.JLabel();
        cbArtErstePruefung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbStandsicherheit = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbVerkehrssicherheit = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbDauerhaftigkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbLastklasse = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblZustandGesamt = new javax.swing.JLabel();
        tfZustandGesamt = new javax.swing.JTextField();
        lblFiller8 = new javax.swing.JLabel();
        dcSanierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        roundedScrollPanel = new de.cismet.tools.gui.RoundedPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlGelaender = new de.cismet.tools.gui.RoundedPanel();
        pnlGelaenderHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGelaenderHeader = new javax.swing.JLabel();
        lblFiller = new javax.swing.JLabel();
        lblBeschreibungGelaender = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        taBeschreibungGelaender = new javax.swing.JTextArea();
        lblZustandGelaender = new javax.swing.JLabel();
        lblSanKostenGelaender = new javax.swing.JLabel();
        lblSanMassnahmenGelaender = new javax.swing.JLabel();
        lblEingriffGeleander = new javax.swing.JLabel();
        tfZustandGelaender = new javax.swing.JTextField();
        tfSanKostenGelaender = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        taSanMassnahmeGelaender = new javax.swing.JTextArea();
        cbEingriffGelaender = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlKopf = new de.cismet.tools.gui.RoundedPanel();
        pnlKopfHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpHeader = new javax.swing.JLabel();
        lblFiller1 = new javax.swing.JLabel();
        lblbeschreibungKopf = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        taBeschreibungKopf = new javax.swing.JTextArea();
        lblZustandKopf = new javax.swing.JLabel();
        lblSanMassnahmenKopf = new javax.swing.JLabel();
        lblSanKostenKopf = new javax.swing.JLabel();
        lblEingriffKopf = new javax.swing.JLabel();
        tfZustandKopf = new javax.swing.JTextField();
        tfSanKostenKopf = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        taSanMassnahmeKopf = new javax.swing.JTextArea();
        cbEingriffKopf = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlAnsicht = new de.cismet.tools.gui.RoundedPanel();
        pnlKopfAnsicht = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht = new javax.swing.JLabel();
        lblFiller3 = new javax.swing.JLabel();
        lblbeschreibungAnsicht = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        taBeschreibungAnsicht = new javax.swing.JTextArea();
        lblZustandAnsicht = new javax.swing.JLabel();
        lblSanMassnahmenAnsicht = new javax.swing.JLabel();
        lblSanKostenAnsicht = new javax.swing.JLabel();
        lblEingriffAnsicht = new javax.swing.JLabel();
        tfZustandAnsicht = new javax.swing.JTextField();
        tfSanKostenAnsicht = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        taSanMassnahmeAnsicht = new javax.swing.JTextArea();
        cbEingriffAnsicht = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlGruendung = new de.cismet.tools.gui.RoundedPanel();
        pnlGruendungHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht1 = new javax.swing.JLabel();
        lblFiller4 = new javax.swing.JLabel();
        lblbeschreibungGruendung = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        taBeschreibungGruendung = new javax.swing.JTextArea();
        lblZustandGruendung = new javax.swing.JLabel();
        lblSanMassnahmenGruendung = new javax.swing.JLabel();
        lblSanKostenAnsicht1 = new javax.swing.JLabel();
        lblEingriffAnsicht1 = new javax.swing.JLabel();
        tfZustandGruendung = new javax.swing.JTextField();
        tfSanKostenGruendung = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        taSanMassnahmeGruendung = new javax.swing.JTextArea();
        cbEingriffGruendung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlGelaende = new de.cismet.tools.gui.RoundedPanel();
        pnlGruendungHeader1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht2 = new javax.swing.JLabel();
        lblFiller5 = new javax.swing.JLabel();
        lblbeschreibungGruendung1 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        taBeschreibungGruendung1 = new javax.swing.JTextArea();
        lblZustandGruendung1 = new javax.swing.JLabel();
        lblSanMassnahmenGruendung1 = new javax.swing.JLabel();
        lblSanKostenAnsicht2 = new javax.swing.JLabel();
        lblEingriffAnsicht2 = new javax.swing.JLabel();
        tfZustandGruendung1 = new javax.swing.JTextField();
        tfSanKostenGruendung1 = new javax.swing.JTextField();
        jScrollPane13 = new javax.swing.JScrollPane();
        taSanMassnahmeGruendung1 = new javax.swing.JTextArea();
        cbEingriffGelaende = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlVerformung = new de.cismet.tools.gui.RoundedPanel();
        pnlGruendungHeader2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht3 = new javax.swing.JLabel();
        lblFiller6 = new javax.swing.JLabel();
        lblbeschreibungGruendung2 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        taBeschreibungGruendung2 = new javax.swing.JTextArea();
        lblZustandGruendung2 = new javax.swing.JLabel();
        lblSanMassnahmenGruendung2 = new javax.swing.JLabel();
        lblSanKostenAnsicht3 = new javax.swing.JLabel();
        lblEingriffAnsicht3 = new javax.swing.JLabel();
        tfZustandGruendung2 = new javax.swing.JTextField();
        tfSanKostenGruendung2 = new javax.swing.JTextField();
        jScrollPane15 = new javax.swing.JScrollPane();
        taSanMassnahmeGruendung2 = new javax.swing.JTextArea();
        cbEingriffVerformung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
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
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.gridwidth = 3;
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

        jScrollPane2.setMinimumSize(new java.awt.Dimension(26, 50));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(0, 50));

        taNeigung.setLineWrap(true);
        taNeigung.setMinimumSize(new java.awt.Dimension(500, 34));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.neigung}"),
                taNeigung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taNeigung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jScrollPane2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.materialtyp}"),
                cbMaterialtyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbMaterialtyp, gridBagConstraints);

        lblStaerke.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblStaerke.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblStaerke, gridBagConstraints);

        lblStuetzmauer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblStuetzmauer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblStuetzmauer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"),
                cbEigentuemer,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbEigentuemer, gridBagConstraints);

        lblLagebeschreibung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLagebeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLagebeschreibung, gridBagConstraints);

        lblHoeheMin.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblHoeheMin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblHoeheMin, gridBagConstraints);

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfUmgebung, gridBagConstraints);

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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(pnlHoehe, gridBagConstraints);

        lblEigentuemer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEigentuemer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblEigentuemer, gridBagConstraints);

        lblNeigung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblNeigung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblNeigung, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jScrollPane1, gridBagConstraints);

        lblLaenge.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLaenge.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLaenge, gridBagConstraints);

        lblUmgebung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblUmgebung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblUmgebung, gridBagConstraints);

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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLaenge, gridBagConstraints);

        lblMaterialTyp.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblMaterialTyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblMaterialTyp, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauertyp}"),
                cbStuetzmauertyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbStuetzmauertyp, gridBagConstraints);

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
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel1, gridBagConstraints);

        lblBesonderheiten.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblBesonderheiten.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
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
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        pnlLeft.add(jScrollPane17, gridBagConstraints);

        lblLagebezeichnung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLagebezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLagebezeichnung, gridBagConstraints);

        lblMauerNummer.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblMauerNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblMauerNummer, gridBagConstraints);

        tfMauerNummer.setMinimumSize(new java.awt.Dimension(150, 20));
        tfMauerNummer.setPreferredSize(new java.awt.Dimension(150, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mauer_nummer}"),
                tfMauerNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfMauerNummer, gridBagConstraints);

        lblMauertyp.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblMauertyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbMauertyp, gridBagConstraints);

        lblLastabstand.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLastabstand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
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
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfLastabstand, gridBagConstraints);

        lblLastklasse.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLastklasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLastklasse, gridBagConstraints);

        lblDauerhaftigkeit.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblDauerhaftigkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblDauerhaftigkeit, gridBagConstraints);

        lblVerkehrssicherheit.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblVerkehrssicherheit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblVerkehrssicherheit, gridBagConstraints);

        lblStandsicherheit.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblStandsicherheit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblStandsicherheit, gridBagConstraints);

        lblPruefung1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblPruefung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblPruefung1, gridBagConstraints);

        lblLetztePruefung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblLetztePruefung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblLetztePruefung, gridBagConstraints);

        lblNaechstePruefung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblNaechstePruefung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblNaechstePruefung, gridBagConstraints);

        lblBauwerksbuchfertigstellung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblBauwerksbuchfertigstellung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblBauwerksbuchfertigstellung, gridBagConstraints);

        lblSanierung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanierung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblSanierung, gridBagConstraints);

        if (editable) {
            lblGeom.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblGeom.text")); // NOI18N
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 24;
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
            gridBagConstraints.gridy = 24;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            pnlLeft.add(cbGeom, gridBagConstraints);
        }

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
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel5, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel7, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel6, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(jPanel2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.standsicherheit}"),
                cbStandsicherheit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbStandsicherheit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verkehrssicherheit}"),
                cbVerkehrssicherheit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbVerkehrssicherheit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dauerhaftigkeit}"),
                cbDauerhaftigkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbDauerhaftigkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lastklasse}"),
                cbLastklasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(cbLastklasse, gridBagConstraints);

        lblZustandGesamt.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandGesamt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(lblZustandGesamt, gridBagConstraints);

        tfZustandGesamt.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandGesamt.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_gesamt}"),
                tfZustandGesamt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DoubleNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(tfZustandGesamt, gridBagConstraints);

        lblFiller8.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        pnlLeft.add(lblFiller8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sanierung}"),
                dcSanierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlLeft.add(dcSanierung, gridBagConstraints);

        jspAllgemeineInfos.setViewportView(pnlLeft);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAllgemein.add(jspAllgemeineInfos, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(pnlAllgemein, gridBagConstraints);

        roundedScrollPanel.setBackground(new java.awt.Color(254, 254, 254));
        roundedScrollPanel.setForeground(new java.awt.Color(254, 254, 254));
        roundedScrollPanel.setMinimumSize(new java.awt.Dimension(500, 26));
        roundedScrollPanel.setPreferredSize(new java.awt.Dimension(500, 120));
        roundedScrollPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setBackground(new java.awt.Color(254, 254, 254));
        jScrollPane3.setBorder(null);
        jScrollPane3.setFocusable(false);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(500, 26));
        jScrollPane3.setOpaque(false);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(600, 120));

        pnlScrollPane.setBackground(new java.awt.Color(254, 254, 254));
        pnlScrollPane.setFocusable(false);
        pnlScrollPane.setOpaque(false);
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlGelaender.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlGelaender.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlGelaender.setLayout(new java.awt.GridBagLayout());

        pnlGelaenderHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlGelaenderHeader.setLayout(new java.awt.FlowLayout());

        lblGelaenderHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblGelaenderHeader.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblGelaenderHeader.text")); // NOI18N
        pnlGelaenderHeader.add(lblGelaenderHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlGelaender.add(pnlGelaenderHeader, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGelaender.add(lblFiller, gridBagConstraints);

        lblBeschreibungGelaender.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblBeschreibungGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlGelaender.add(lblBeschreibungGelaender, gridBagConstraints);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungGelaender.setLineWrap(true);
        taBeschreibungGelaender.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_gelaender}"),
                taBeschreibungGelaender,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(taBeschreibungGelaender);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlGelaender.add(jScrollPane4, gridBagConstraints);

        lblZustandGelaender.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaender.add(lblZustandGelaender, gridBagConstraints);

        lblSanKostenGelaender.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaender.add(lblSanKostenGelaender, gridBagConstraints);

        lblSanMassnahmenGelaender.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaender.add(lblSanMassnahmenGelaender, gridBagConstraints);

        lblEingriffGeleander.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffGeleander.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaender.add(lblEingriffGeleander, gridBagConstraints);

        tfZustandGelaender.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandGelaender.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_gelaender}"),
                tfZustandGelaender,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(tfZustandGelaender, gridBagConstraints);

        tfSanKostenGelaender.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenGelaender.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_gelaender}"),
                tfSanKostenGelaender,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(tfSanKostenGelaender, gridBagConstraints);

        jScrollPane5.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(0, 70));

        taSanMassnahmeGelaender.setLineWrap(true);
        taSanMassnahmeGelaender.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_gelaender}"),
                taSanMassnahmeGelaender,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(taSanMassnahmeGelaender);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGelaender.add(jScrollPane5, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_gelaender}"),
                cbEingriffGelaender,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGelaender.add(cbEingriffGelaender, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlGelaender, gridBagConstraints);

        pnlKopf.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlKopf.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlKopf.setLayout(new java.awt.GridBagLayout());

        pnlKopfHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlKopfHeader.setLayout(new java.awt.FlowLayout());

        lblKofpHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpHeader.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblKofpHeader.text")); // NOI18N
        pnlKopfHeader.add(lblKofpHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlKopf.add(pnlKopfHeader, gridBagConstraints);

        lblFiller1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlKopf.add(lblFiller1, gridBagConstraints);

        lblbeschreibungKopf.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblbeschreibungKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlKopf.add(lblbeschreibungKopf, gridBagConstraints);

        jScrollPane6.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungKopf.setLineWrap(true);
        taBeschreibungKopf.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_kopf}"),
                taBeschreibungKopf,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(taBeschreibungKopf);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlKopf.add(jScrollPane6, gridBagConstraints);

        lblZustandKopf.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlKopf.add(lblZustandKopf, gridBagConstraints);

        lblSanMassnahmenKopf.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlKopf.add(lblSanMassnahmenKopf, gridBagConstraints);

        lblSanKostenKopf.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlKopf.add(lblSanKostenKopf, gridBagConstraints);

        lblEingriffKopf.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlKopf.add(lblEingriffKopf, gridBagConstraints);

        tfZustandKopf.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandKopf.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_kopf}"),
                tfZustandKopf,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(tfZustandKopf, gridBagConstraints);

        tfSanKostenKopf.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenKopf.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_kopf}"),
                tfSanKostenKopf,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(tfSanKostenKopf, gridBagConstraints);

        jScrollPane7.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(0, 70));

        taSanMassnahmeKopf.setLineWrap(true);
        taSanMassnahmeKopf.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_kopf}"),
                taSanMassnahmeKopf,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(taSanMassnahmeKopf);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlKopf.add(jScrollPane7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_kopf}"),
                cbEingriffKopf,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlKopf.add(cbEingriffKopf, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlKopf, gridBagConstraints);

        pnlAnsicht.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlAnsicht.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlAnsicht.setLayout(new java.awt.GridBagLayout());

        pnlKopfAnsicht.setBackground(new java.awt.Color(51, 51, 51));
        pnlKopfAnsicht.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblKofpAnsicht.text")); // NOI18N
        pnlKopfAnsicht.add(lblKofpAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlAnsicht.add(pnlKopfAnsicht, gridBagConstraints);

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAnsicht.add(lblFiller3, gridBagConstraints);

        lblbeschreibungAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblbeschreibungAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlAnsicht.add(lblbeschreibungAnsicht, gridBagConstraints);

        jScrollPane8.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane8.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungAnsicht.setLineWrap(true);
        taBeschreibungAnsicht.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_ansicht}"),
                taBeschreibungAnsicht,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane8.setViewportView(taBeschreibungAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlAnsicht.add(jScrollPane8, gridBagConstraints);

        lblZustandAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAnsicht.add(lblZustandAnsicht, gridBagConstraints);

        lblSanMassnahmenAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAnsicht.add(lblSanMassnahmenAnsicht, gridBagConstraints);

        lblSanKostenAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAnsicht.add(lblSanKostenAnsicht, gridBagConstraints);

        lblEingriffAnsicht.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAnsicht.add(lblEingriffAnsicht, gridBagConstraints);

        tfZustandAnsicht.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandAnsicht.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_ansicht}"),
                tfZustandAnsicht,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(tfZustandAnsicht, gridBagConstraints);

        tfSanKostenAnsicht.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenAnsicht.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_ansicht}"),
                tfSanKostenAnsicht,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(tfSanKostenAnsicht, gridBagConstraints);

        jScrollPane9.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane9.setPreferredSize(new java.awt.Dimension(0, 70));

        taSanMassnahmeAnsicht.setLineWrap(true);
        taSanMassnahmeAnsicht.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_ansicht}"),
                taSanMassnahmeAnsicht,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane9.setViewportView(taSanMassnahmeAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAnsicht.add(jScrollPane9, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_ansicht}"),
                cbEingriffAnsicht,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAnsicht.add(cbEingriffAnsicht, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlAnsicht, gridBagConstraints);

        pnlGruendung.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlGruendung.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlGruendung.setLayout(new java.awt.GridBagLayout());

        pnlGruendungHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlGruendungHeader.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht1.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblKofpAnsicht1.text")); // NOI18N
        pnlGruendungHeader.add(lblKofpAnsicht1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlGruendung.add(pnlGruendungHeader, gridBagConstraints);

        lblFiller4.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGruendung.add(lblFiller4, gridBagConstraints);

        lblbeschreibungGruendung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblbeschreibungGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlGruendung.add(lblbeschreibungGruendung, gridBagConstraints);

        jScrollPane10.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane10.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungGruendung.setLineWrap(true);
        taBeschreibungGruendung.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_gruendung}"),
                taBeschreibungGruendung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane10.setViewportView(taBeschreibungGruendung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlGruendung.add(jScrollPane10, gridBagConstraints);

        lblZustandGruendung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGruendung.add(lblZustandGruendung, gridBagConstraints);

        lblSanMassnahmenGruendung.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGruendung.add(lblSanMassnahmenGruendung, gridBagConstraints);

        lblSanKostenAnsicht1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenAnsicht1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGruendung.add(lblSanKostenAnsicht1, gridBagConstraints);

        lblEingriffAnsicht1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffAnsicht1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGruendung.add(lblEingriffAnsicht1, gridBagConstraints);

        tfZustandGruendung.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandGruendung.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_gruendung}"),
                tfZustandGruendung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(tfZustandGruendung, gridBagConstraints);

        tfSanKostenGruendung.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenGruendung.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_gruendung}"),
                tfSanKostenGruendung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(tfSanKostenGruendung, gridBagConstraints);

        jScrollPane11.setMinimumSize(new java.awt.Dimension(26, 87));
        jScrollPane11.setPreferredSize(new java.awt.Dimension(262, 70));

        taSanMassnahmeGruendung.setLineWrap(true);
        taSanMassnahmeGruendung.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_gruendung}"),
                taSanMassnahmeGruendung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane11.setViewportView(taSanMassnahmeGruendung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGruendung.add(jScrollPane11, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_gruendung}"),
                cbEingriffGruendung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGruendung.add(cbEingriffGruendung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlGruendung, gridBagConstraints);

        pnlGelaende.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlGelaende.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlGelaende.setLayout(new java.awt.GridBagLayout());

        pnlGruendungHeader1.setBackground(new java.awt.Color(51, 51, 51));
        pnlGruendungHeader1.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht2.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblKofpAnsicht2.text")); // NOI18N
        pnlGruendungHeader1.add(lblKofpAnsicht2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlGelaende.add(pnlGruendungHeader1, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGelaende.add(lblFiller5, gridBagConstraints);

        lblbeschreibungGruendung1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblbeschreibungGruendung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlGelaende.add(lblbeschreibungGruendung1, gridBagConstraints);

        jScrollPane12.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane12.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungGruendung1.setLineWrap(true);
        taBeschreibungGruendung1.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_gelaende}"),
                taBeschreibungGruendung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane12.setViewportView(taBeschreibungGruendung1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlGelaende.add(jScrollPane12, gridBagConstraints);

        lblZustandGruendung1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandGruendung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaende.add(lblZustandGruendung1, gridBagConstraints);

        lblSanMassnahmenGruendung1.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenGruendung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaende.add(lblSanMassnahmenGruendung1, gridBagConstraints);

        lblSanKostenAnsicht2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenAnsicht2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaende.add(lblSanKostenAnsicht2, gridBagConstraints);

        lblEingriffAnsicht2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffAnsicht2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGelaende.add(lblEingriffAnsicht2, gridBagConstraints);

        tfZustandGruendung1.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandGruendung1.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_gelaende}"),
                tfZustandGruendung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaende.add(tfZustandGruendung1, gridBagConstraints);

        tfSanKostenGruendung1.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenGruendung1.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_gelaende}"),
                tfSanKostenGruendung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaende.add(tfSanKostenGruendung1, gridBagConstraints);

        jScrollPane13.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane13.setPreferredSize(new java.awt.Dimension(0, 70));

        taSanMassnahmeGruendung1.setLineWrap(true);
        taSanMassnahmeGruendung1.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_gelaende}"),
                taSanMassnahmeGruendung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane13.setViewportView(taSanMassnahmeGruendung1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGelaende.add(jScrollPane13, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_gelaende}"),
                cbEingriffGelaende,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGelaende.add(cbEingriffGelaende, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlGelaende, gridBagConstraints);

        pnlVerformung.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlVerformung.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlVerformung.setLayout(new java.awt.GridBagLayout());

        pnlGruendungHeader2.setBackground(new java.awt.Color(51, 51, 51));
        pnlGruendungHeader2.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht3.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht3.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblKofpAnsicht3.text")); // NOI18N
        pnlGruendungHeader2.add(lblKofpAnsicht3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlVerformung.add(pnlGruendungHeader2, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVerformung.add(lblFiller6, gridBagConstraints);

        lblbeschreibungGruendung2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblbeschreibungGruendung2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlVerformung.add(lblbeschreibungGruendung2, gridBagConstraints);

        jScrollPane14.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane14.setPreferredSize(new java.awt.Dimension(0, 70));

        taBeschreibungGruendung2.setLineWrap(true);
        taBeschreibungGruendung2.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_verformung}"),
                taBeschreibungGruendung2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane14.setViewportView(taBeschreibungGruendung2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlVerformung.add(jScrollPane14, gridBagConstraints);

        lblZustandGruendung2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblZustandGruendung2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlVerformung.add(lblZustandGruendung2, gridBagConstraints);

        lblSanMassnahmenGruendung2.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanMassnahmenGruendung2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlVerformung.add(lblSanMassnahmenGruendung2, gridBagConstraints);

        lblSanKostenAnsicht3.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblSanKostenAnsicht3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlVerformung.add(lblSanKostenAnsicht3, gridBagConstraints);

        lblEingriffAnsicht3.setText(org.openide.util.NbBundle.getMessage(
                MauerEditor.class,
                "MauerEditor.lblEingriffAnsicht3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlVerformung.add(lblEingriffAnsicht3, gridBagConstraints);

        tfZustandGruendung2.setMinimumSize(new java.awt.Dimension(100, 20));
        tfZustandGruendung2.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_verformung}"),
                tfZustandGruendung2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new NumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVerformung.add(tfZustandGruendung2, gridBagConstraints);

        tfSanKostenGruendung2.setMinimumSize(new java.awt.Dimension(100, 20));
        tfSanKostenGruendung2.setPreferredSize(new java.awt.Dimension(100, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_verformung}"),
                tfSanKostenGruendung2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntegerNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVerformung.add(tfSanKostenGruendung2, gridBagConstraints);

        jScrollPane15.setMinimumSize(new java.awt.Dimension(26, 70));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(262, 70));

        taSanMassnahmeGruendung2.setLineWrap(true);
        taSanMassnahmeGruendung2.setMinimumSize(new java.awt.Dimension(500, 70));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_verformung}"),
                taSanMassnahmeGruendung2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane15.setViewportView(taSanMassnahmeGruendung2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlVerformung.add(jScrollPane15, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_verformung}"),
                cbEingriffVerformung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlVerformung.add(cbEingriffVerformung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlScrollPane.add(pnlVerformung, gridBagConstraints);

        jScrollPane3.setViewportView(pnlScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedScrollPanel.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(roundedScrollPanel, gridBagConstraints);

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

        cardLayout.show(this, "card2");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    } //GEN-LAST:event_btnImagesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnInfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnInfoActionPerformed
        cardLayout.show(this, "card1");
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
    private void tfStaerke_untenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tfStaerke_untenActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_tfStaerke_untenActionPerformed

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
                cidsBean);
            this.cidsBean = cidsBean;
            final String lagebez = (String)cidsBean.getProperty("lagebezeichnung");
            this.title = NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblTitle.prefix")
                        + ((lagebez != null) ? lagebez : "");
            lblTitle.setText(this.title);
        }
        bindingGroup.bind();
        final LayoutManager layout = getLayout();
        if (layout instanceof CardLayout) {
            cardLayout = (CardLayout)layout;
            cardLayout.show(this, "card1");
        }
    }

    @Override
    public void dispose() {
        webDavPicturePanel1.dispose();
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
            log.info("prepare for save");
            final String mauerNummer = (String)cidsBean.getProperty("mauer_nummer");
            final String lagebezeichnung = (String)cidsBean.getProperty("lagebezeichnung");
            if ((lagebezeichnung == null) || lagebezeichnung.trim().equals("")) {
                log.warn("lagebezeichnung must not be null or empty");
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
                        log.warn("mauernummer " + mauerNummer + "already exists");
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
    private void setLimitDocumentFilter(final JTextComponent ta, final int maxSize) {
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
    public final ClientConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentSizeFilter extends DocumentFilter {

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
}
