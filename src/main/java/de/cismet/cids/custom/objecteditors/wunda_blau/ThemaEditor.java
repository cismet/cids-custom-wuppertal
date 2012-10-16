/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CoolThemaRenderer.java
 *
 * Created on 10. November 3508, 11:56
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.cismet.cids.annotations.AggregationRenderer;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.tools.gui.DoNotWrap;
import de.cismet.tools.gui.PureCoolPanel;
import de.cismet.tools.gui.RoundedPanel;

/**
 * de.cismet.cids.objectrenderer.CoolThemaRenderer.
 *
 * <p>Renderer for the "Thema"-theme</p>
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
@AggregationRenderer
public class ThemaEditor extends PureCoolPanel implements DoNotWrap, DisposableCidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ThemaEditor.class);
    public static final String TITLE_PREFIX = "Thema:";
    public static final String TITLE_AGR_PREFIX = "Themen";
    public static final String CARD_1 = "CARD_1";
    public static final String CARD_2 = "CARD_2";
    public static final Color COLOR_TXT_BACK = new Color(230, 230, 230);
    public static final Color COLOR_TBL_SECOND = new Color(210, 210, 210);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final List<Integer> COLUMN_SIZES = new CopyOnWriteArrayList<Integer>();

    //~ Instance fields --------------------------------------------------------

    private LockableUI lockLayer1;
    private LockableUI lockLayer2;
    private final List<JComponent> inputFields;
    private final CardLayout cardLayout;
    private volatile boolean editable;
    private CidsBean cidsBean;
    private JXLayer<JComponent> layer1;
    private JXLayer<JComponent> layer2;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForwrd;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktAnspSpezif;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktGeometriemodell;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktImplementierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktKategorie;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktRealisierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktWartung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbDatenbereitstellung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbDatenquelle;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbDienst;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbLetzteAktualisierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbNutzungsart;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbWMS;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukAnspSpezif;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukGeometriemodell;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukImplementierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukKategorie;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukRealisierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZukWartung;
    private javax.swing.JCheckBox chkAktAggObjektRenderer;
    private javax.swing.JCheckBox chkAktEditor;
    private javax.swing.JCheckBox chkAktFeatureRenderer;
    private javax.swing.JCheckBox chkAktObjektRenderer;
    private javax.swing.JCheckBox chkAktReportTemplates;
    private javax.swing.JCheckBox chkAktSpezSuchen;
    private javax.swing.JCheckBox chkFachthema;
    private javax.swing.JCheckBox chkFachverfahren;
    private javax.swing.JCheckBox chkGeoPortal;
    private javax.swing.JCheckBox chkKaskadiert;
    private javax.swing.JCheckBox chkPubliziert;
    private javax.swing.JCheckBox chkWundaGui;
    private javax.swing.JCheckBox chkZukAggObjektRenderer;
    private javax.swing.JCheckBox chkZukEditor;
    private javax.swing.JCheckBox chkZukFeatureRenderer;
    private javax.swing.JCheckBox chkZukObjektRenderer;
    private javax.swing.JCheckBox chkZukReportTemplates;
    private javax.swing.JCheckBox chkZukSpezSuchen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel6;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblAenderungsdatum;
    private javax.swing.JLabel lblAktAngebFachverfahren;
    private javax.swing.JLabel lblAktAnspSpezif;
    private javax.swing.JLabel lblAktGeometriemodell;
    private javax.swing.JLabel lblAktImplementierung;
    private javax.swing.JLabel lblAktKategorie;
    private javax.swing.JLabel lblAktRealisierung;
    private javax.swing.JLabel lblAktWartung;
    private javax.swing.JLabel lblAnsprechpartner;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblDatenbereitstellung;
    private javax.swing.JLabel lblDatenquelle;
    private javax.swing.JLabel lblDescriptionBack;
    private javax.swing.JLabel lblDescriptionFor;
    private javax.swing.JLabel lblDienst;
    private javax.swing.JLabel lblInhalt;
    private javax.swing.JLabel lblLetzteAktualisierung;
    private javax.swing.JLabel lblNutzerzahl;
    private javax.swing.JLabel lblNutzungArt;
    private javax.swing.JLabel lblOrganisationseinheit;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWMS;
    private javax.swing.JLabel lblZukAngebFachverfahren;
    private javax.swing.JLabel lblZukAnspSpezif;
    private javax.swing.JLabel lblZukGeometriemodell;
    private javax.swing.JLabel lblZukImplementierung;
    private javax.swing.JLabel lblZukKategorie;
    private javax.swing.JLabel lblZukRealisierung;
    private javax.swing.JLabel lblZukWartung;
    private javax.swing.JPanel panAktUmsetzung;
    private javax.swing.JPanel panBemerkung;
    private javax.swing.JPanel panBezeichnung;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panCards;
    private javax.swing.JPanel panCenter;
    private javax.swing.JPanel panGazetteer;
    private javax.swing.JPanel panOperativ;
    private javax.swing.JPanel panOrganisationseinheit;
    private javax.swing.JPanel panSouth;
    private javax.swing.JPanel panTab1;
    private javax.swing.JPanel panTab2;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panWMS;
    private javax.swing.JPanel panZukUmsetzung;
    private javax.swing.JScrollPane scpAktAngebFachverfahren;
    private javax.swing.JScrollPane scpTxtBemerkung;
    private javax.swing.JScrollPane scpTxtInhalt;
    private javax.swing.JScrollPane scpZukAngebFachverfahren;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextArea txtAktAngebFachverfahren;
    private javax.swing.JTextField txtAnsprechpartner;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextArea txtInhalt;
    private javax.swing.JTextField txtLocationtypes;
    private javax.swing.JTextField txtNutzerzahl;
    private javax.swing.JTextField txtOrganisationseinheit;
    private javax.swing.JTextArea txtZukAngebFachverfahren;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolThemaRenderer.
     */
    public ThemaEditor() {
        this(true);
    }

    /**
     * Creates new form CoolThemaRenderer.
     *
     * @param  editable  DOCUMENT ME!
     */
    public ThemaEditor(final boolean editable) {
        // init graphics
        inputFields = new ArrayList<JComponent>();
        cardLayout = new CardLayout();
        initLockUIs();
        initComponents();
        initInputFieldList();

        setPanContent(panCenter);
        setPanInter(panSouth);
        setPanTitle(panTitle);

        btnBack.setEnabled(false);
        lblDescriptionBack.setEnabled(false);
        setEditable(editable);

        cbAktAnspSpezif.setNullValueRepresentation("nicht definiert");
        cbAktGeometriemodell.setNullValueRepresentation("nicht definiert");
        cbAktImplementierung.setNullValueRepresentation("nicht definiert");
        cbAktKategorie.setNullValueRepresentation("nicht definiert");
        cbAktRealisierung.setNullValueRepresentation("nicht definiert");
        cbAktWartung.setNullValueRepresentation("nicht definiert");
        cbDatenbereitstellung.setNullValueRepresentation("nicht definiert");
        cbDatenbereitstellung.setNullable(true);
        cbDatenquelle.setNullValueRepresentation("nicht definiert");
        cbDatenquelle.setNullable(true);
        cbDienst.setNullValueRepresentation("nicht definiert");
        cbLetzteAktualisierung.setNullValueRepresentation("nicht definiert");
        cbNutzungsart.setNullValueRepresentation("nicht definiert");
        cbWMS.setNullValueRepresentation("nicht definiert");
        cbZukAnspSpezif.setNullValueRepresentation("nicht definiert");
        cbZukGeometriemodell.setNullValueRepresentation("nicht definiert");
        cbZukImplementierung.setNullValueRepresentation("nicht definiert");
        cbZukKategorie.setNullValueRepresentation("nicht definiert");
        cbZukRealisierung.setNullValueRepresentation("nicht definiert");
        cbZukWartung.setNullValueRepresentation("nicht definiert");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;

        // initImagesAndMore();

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    lockLayer1.setLocked(false);
                    lockLayer2.setLocked(false);

                    try {
                        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                            bindingGroup,
                            cidsBean);

                        bindingGroup.bind();
                    } catch (Exception e) {
                        throw new RuntimeException("Error occured during binding", e);
                    }

                    initLockUIs();
                    layer1.setUI(lockLayer1);
                    layer2.setUI(lockLayer2);
                    lockLayer1.setLocked(!editable);
                    lockLayer2.setLocked(!editable);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void initLockUIs() {
        lockLayer1 = new LockableUI();
        lockLayer1.setLockedCursor(Cursor.getDefaultCursor());
        lockLayer2 = new LockableUI();
        lockLayer2.setLockedCursor(Cursor.getDefaultCursor());
    }

    /**
     * DOCUMENT ME!
     */
    private void initInputFieldList() {
        if (inputFields.isEmpty()) {
            inputFields.add(txtOrganisationseinheit);
            inputFields.add(txtBemerkung);
            inputFields.add(txtBezeichnung);
            inputFields.add(txtInhalt);
            inputFields.add(txtLocationtypes);
            inputFields.add(txtNutzerzahl);
            inputFields.add(txtAnsprechpartner);
            inputFields.add(scpTxtBemerkung);
            inputFields.add(scpTxtInhalt);
            inputFields.add(cbAktAnspSpezif);
            inputFields.add(cbAktGeometriemodell);
            inputFields.add(cbAktImplementierung);
            inputFields.add(cbAktKategorie);
            inputFields.add(cbAktRealisierung);
            inputFields.add(cbAktWartung);
            inputFields.add(cbDatenbereitstellung);
            inputFields.add(cbDatenquelle);
            inputFields.add(cbDienst);
            inputFields.add(cbLetzteAktualisierung);
            inputFields.add(cbNutzungsart);
            inputFields.add(cbWMS);
            inputFields.add(cbZukAnspSpezif);
            inputFields.add(cbZukGeometriemodell);
            inputFields.add(cbZukImplementierung);
            inputFields.add(cbZukKategorie);
            inputFields.add(cbZukRealisierung);
            inputFields.add(cbZukWartung);
            inputFields.add(txtZukAngebFachverfahren);
            inputFields.add(txtAktAngebFachverfahren);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  true if the fields/boxes are editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Enable/Disable editable mode of all input fields/boxes.
     *
     * <p>Use to switch between renderer- and editor-mode</p>
     *
     * @param  editable  DOCUMENT ME!
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
        final Runnable layerLockRunnable = new Runnable() {

                @Override
                public void run() {
                    lockLayer1.setLocked(!editable);
                    lockLayer2.setLocked(!editable);
//
//                jScrollPane2.getViewport().setVisible(editable);
//                jScrollPane3.getViewport().setVisible(editable);

//                scpTxtBemerkung.getViewport().setOpaque(editable);
//                scpTxtInhalt.getViewport().setOpaque(editable);

                    for (final JComponent inputField : inputFields) {
                        if ((inputField instanceof JTextField) || (inputField instanceof JTextArea)) {
                            inputField.setBorder(null);
                            inputField.setOpaque(editable);
                            if (!editable) {
                                inputField.setBackground(COLOR_TXT_BACK);
                            }
                        } else if (inputField instanceof DefaultBindableReferenceCombo) {
                            ((DefaultBindableReferenceCombo)inputField).setFakeModel(!editable);
                        }
                    }
                }
            };
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(layerLockRunnable);
        } else {
            layerLockRunnable.run();
        }
    }

    /**
     * DOCUMENT ME!
     */
// public ThemaBean getContent() {
// final ThemaBean.Builder builder = ThemaBean.builder();
// builder.aenderungsdatum(new Date(System.currentTimeMillis())).aktAggregierenderObjektRenderer(chkAktAggObjektRenderer.isSelected()).aktAnsprechpartnerImplementierung(evalCB(cbAktImplementierung)).aktAnsprechpartnerSpezifizierung(evalCB(cbAktAnspSpezif)).aktEditoren(chkAktEditor.isSelected()).aktFeatureRenderer(chkAktFeatureRenderer.isSelected()).aktGeometrieModell(evalCB(cbAktGeometriemodell)).aktKategorie(evalCB(cbAktKategorie)).aktObjektRenderer(chkAktObjektRenderer.isSelected()).aktRealisierungsstand(evalCB(cbAktRealisierung)).aktReportTemplates(chkAktReportTemplates.isSelected()).aktSpezifischesSuchen(chkAktSpezSuchen.isSelected()).aktWartungsvertrag(evalCB(cbAktWartung)).ansprechpartner(txtAnsprechpartner.getText()).bemerkungen(txtBemerkung.getText()).bezeichnung(txtBezeichnung.getText()).fachthema(chkFachthema.isSelected()).fachverfahren(chkFachverfahren.isSelected()).gazDienst(evalCB(cbDienst)).gazGeoportalGUI(chkGeoPortal.isSelected()).gazLocationtypes(txtLocationtypes.getText()).gazWundaGUI(chkWundaGui.isSelected()).inhalt(txtInhalt.getText()).letzteAenderungDurch(evalCB(cbLetzteAktualisierung)).nutzerzahl(txtNutzerzahl.getText()).nutzungsart(evalCB(cbNutzungsart)).organisationseinheit(txtOrganisationseinheit.getText()).wmsDatenbereitstellung(evalCB(cbDatenbereitstellung)).wmsDatenquelle(evalCB(cbDatenquelle)).wmsKaskadiertAufSims(chkKaskadiert.isSelected()).wmsPubliziertImGeoportal(chkPubliziert.isSelected()).wmsWms(evalCB(cbWMS)).zukAggregierenderObjektRenderer(chkZukAggObjektRenderer.isSelected()).zukAnsprechpartnerImplementierung(evalCB(cbZukImplementierung)).zukAnsprechpartnerSpezifizierung(evalCB(cbZukAnspSpezif)).zukEditoren(chkZukEditor.isSelected()).zukFeatureRenderer(chkZukFeatureRenderer.isSelected()).zukGeometrieModell(evalCB(cbZukGeometriemodell)).zukKategorie(evalCB(cbZukKategorie)).zukObjektRenderer(chkZukObjektRenderer.isSelected()).zukRealisierungsstand(evalCB(cbZukRealisierung)).zukReportTemplates(chkZukReportTemplates.isSelected()).zukSpezifischesSuchen(chkZukSpezSuchen.isSelected()).zukWartungsvertrag(evalCB(cbZukWartung));
//        return builder.build();
//    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panCenter = new JPanel();
        panCards = new javax.swing.JPanel();
        panTab1 = new javax.swing.JPanel();
        panBezeichnung = new RoundedPanel();
        lblBezeichnung = new javax.swing.JLabel();
        lblInhalt = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        scpTxtInhalt = new javax.swing.JScrollPane();
        txtInhalt = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        panOrganisationseinheit = new RoundedPanel();
        lblOrganisationseinheit = new javax.swing.JLabel();
        lblAnsprechpartner = new javax.swing.JLabel();
        lblNutzerzahl = new javax.swing.JLabel();
        lblNutzungArt = new javax.swing.JLabel();
        cbNutzungsart = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        txtOrganisationseinheit = new javax.swing.JTextField();
        txtNutzerzahl = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        txtAnsprechpartner = new javax.swing.JTextField();
        panWMS = new RoundedPanel();
        lblWMS = new javax.swing.JLabel();
        chkKaskadiert = new javax.swing.JCheckBox();
        chkPubliziert = new javax.swing.JCheckBox();
        lblDatenbereitstellung = new javax.swing.JLabel();
        lblDatenquelle = new javax.swing.JLabel();
        cbDatenbereitstellung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbDatenquelle = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbWMS = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        panGazetteer = new RoundedPanel();
        lblDienst = new javax.swing.JLabel();
        chkWundaGui = new javax.swing.JCheckBox();
        chkGeoPortal = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        cbDienst = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        txtLocationtypes = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        panBemerkung = new RoundedPanel();
        lblBemerkung = new javax.swing.JLabel();
        lblLetzteAktualisierung = new javax.swing.JLabel();
        lblAenderungsdatum = new javax.swing.JLabel();
        cbLetzteAktualisierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        scpTxtBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        panTab2 = new javax.swing.JPanel();
        panAktUmsetzung = new RoundedPanel();
        lblAktGeometriemodell = new javax.swing.JLabel();
        chkAktObjektRenderer = new javax.swing.JCheckBox();
        chkAktAggObjektRenderer = new javax.swing.JCheckBox();
        chkAktFeatureRenderer = new javax.swing.JCheckBox();
        chkAktEditor = new javax.swing.JCheckBox();
        chkAktReportTemplates = new javax.swing.JCheckBox();
        chkAktSpezSuchen = new javax.swing.JCheckBox();
        lblAktKategorie = new javax.swing.JLabel();
        cbAktKategorie = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbAktGeometriemodell = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel4 = new javax.swing.JLabel();
        lblAktAnspSpezif = new javax.swing.JLabel();
        lblAktImplementierung = new javax.swing.JLabel();
        lblAktRealisierung = new javax.swing.JLabel();
        lblAktWartung = new javax.swing.JLabel();
        cbAktWartung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbAktRealisierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbAktImplementierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbAktAnspSpezif = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblAktAngebFachverfahren = new javax.swing.JLabel();
        scpAktAngebFachverfahren = new javax.swing.JScrollPane();
        txtAktAngebFachverfahren = new javax.swing.JTextArea();
        panZukUmsetzung = new RoundedPanel();
        lblZukGeometriemodell = new javax.swing.JLabel();
        chkZukObjektRenderer = new javax.swing.JCheckBox();
        chkZukAggObjektRenderer = new javax.swing.JCheckBox();
        chkZukFeatureRenderer = new javax.swing.JCheckBox();
        chkZukEditor = new javax.swing.JCheckBox();
        chkZukReportTemplates = new javax.swing.JCheckBox();
        chkZukSpezSuchen = new javax.swing.JCheckBox();
        lblZukKategorie = new javax.swing.JLabel();
        cbZukGeometriemodell = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbZukKategorie = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel5 = new javax.swing.JLabel();
        lblZukAnspSpezif = new javax.swing.JLabel();
        lblZukImplementierung = new javax.swing.JLabel();
        lblZukRealisierung = new javax.swing.JLabel();
        lblZukWartung = new javax.swing.JLabel();
        cbZukWartung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbZukImplementierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbZukAnspSpezif = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbZukRealisierung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblZukAngebFachverfahren = new javax.swing.JLabel();
        scpZukAngebFachverfahren = new javax.swing.JScrollPane();
        txtZukAngebFachverfahren = new javax.swing.JTextArea();
        panOperativ = new RoundedPanel();
        jLabel12 = new javax.swing.JLabel();
        chkFachthema = new javax.swing.JCheckBox();
        chkFachverfahren = new javax.swing.JCheckBox();
        panSouth = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        lblDescriptionBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnForwrd = new javax.swing.JButton();
        lblDescriptionFor = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        panTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                lblTitle,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        panTitle.add(lblTitle);

        add(panTitle, java.awt.BorderLayout.NORTH);

        panCenter.setOpaque(false);
        panCenter.setLayout(new java.awt.BorderLayout());

        panCards.setOpaque(false);
        /*
         * panCards.setLayout(new java.awt.CardLayout());
         */
        panCards.setLayout(cardLayout);

        panTab1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panTab1.setOpaque(false);
        panTab1.setLayout(new java.awt.GridBagLayout());

        panBezeichnung.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panBezeichnung.setOpaque(false);
        panBezeichnung.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBezeichnung.setText("Bezeichnung ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBezeichnung.add(lblBezeichnung, gridBagConstraints);

        lblInhalt.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblInhalt.setText("Fachl. Inhalt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBezeichnung.add(lblInhalt, gridBagConstraints);

        txtBezeichnung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBezeichnung.add(txtBezeichnung, gridBagConstraints);

        scpTxtInhalt.setBorder(null);
        scpTxtInhalt.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtInhalt.setColumns(15);
        txtInhalt.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtInhalt.setLineWrap(true);
        txtInhalt.setRows(4);
        txtInhalt.setWrapStyleWord(true);
        txtInhalt.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.inhalt}"),
                txtInhalt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpTxtInhalt.setViewportView(txtInhalt);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBezeichnung.add(scpTxtInhalt, gridBagConstraints);
        scpTxtInhalt.getViewport().setOpaque(false);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.1;
        panBezeichnung.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab1.add(panBezeichnung, gridBagConstraints);

        panOrganisationseinheit.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panOrganisationseinheit.setOpaque(false);
        panOrganisationseinheit.setPreferredSize(new java.awt.Dimension(250, 273));
        panOrganisationseinheit.setLayout(new java.awt.GridBagLayout());

        lblOrganisationseinheit.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblOrganisationseinheit.setText("Organisationseinheit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(lblOrganisationseinheit, gridBagConstraints);

        lblAnsprechpartner.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAnsprechpartner.setText("Ansprechpartner");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(lblAnsprechpartner, gridBagConstraints);

        lblNutzerzahl.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblNutzerzahl.setText("Nutzeranzahl");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(lblNutzerzahl, gridBagConstraints);

        lblNutzungArt.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblNutzungArt.setText("Art der Nutzung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(lblNutzungArt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nutzungsart}"),
                cbNutzungsart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(cbNutzungsart, gridBagConstraints);

        txtOrganisationseinheit.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.organisationseinheit}"),
                txtOrganisationseinheit,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(txtOrganisationseinheit, gridBagConstraints);

        txtNutzerzahl.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nutzerzahl}"),
                txtNutzerzahl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(txtNutzerzahl, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Nutzer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panOrganisationseinheit.add(jLabel3, gridBagConstraints);

        jPanel12.setOpaque(false);
        jPanel12.setPreferredSize(new java.awt.Dimension(26, 26));

        final javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                252,
                Short.MAX_VALUE));
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                111,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        panOrganisationseinheit.add(jPanel12, gridBagConstraints);

        txtAnsprechpartner.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ansprechpartner}"),
                txtAnsprechpartner,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOrganisationseinheit.add(txtAnsprechpartner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab1.add(panOrganisationseinheit, gridBagConstraints);

        panWMS.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panWMS.setOpaque(false);
        panWMS.setPreferredSize(new java.awt.Dimension(250, 273));
        panWMS.setLayout(new java.awt.GridBagLayout());

        lblWMS.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblWMS.setText("WMS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panWMS.add(lblWMS, gridBagConstraints);

        chkKaskadiert.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkKaskadiert.setText("kaskadiert auf SIMS-Layer");
        chkKaskadiert.setContentAreaFilled(false);
        chkKaskadiert.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wms_kaskadiert_auf_sims}"),
                chkKaskadiert,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(chkKaskadiert, gridBagConstraints);

        chkPubliziert.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkPubliziert.setText("publiziert im GeoPortal");
        chkPubliziert.setContentAreaFilled(false);
        chkPubliziert.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wms_publiziert_im_geoportal}"),
                chkPubliziert,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(chkPubliziert, gridBagConstraints);

        lblDatenbereitstellung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDatenbereitstellung.setText("Datenbereitstellung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panWMS.add(lblDatenbereitstellung, gridBagConstraints);

        lblDatenquelle.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDatenquelle.setText("Datenquelle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(lblDatenquelle, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wms_datenbereitstellung}"),
                cbDatenbereitstellung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(cbDatenbereitstellung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wms_datenquelle}"),
                cbDatenquelle,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(cbDatenquelle, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wms_wms}"),
                cbWMS,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panWMS.add(cbWMS, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("WuNDa/DK");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panWMS.add(jLabel1, gridBagConstraints);

        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(26, 26));

        final javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                260,
                Short.MAX_VALUE));
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                49,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        panWMS.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab1.add(panWMS, gridBagConstraints);

        panGazetteer.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panGazetteer.setOpaque(false);
        panGazetteer.setLayout(new java.awt.GridBagLayout());

        lblDienst.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDienst.setText("Dienst");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(lblDienst, gridBagConstraints);

        chkWundaGui.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkWundaGui.setText("WuNDa/DK GUI");
        chkWundaGui.setContentAreaFilled(false);
        chkWundaGui.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gaz_wunda_dk_gui}"),
                chkWundaGui,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(chkWundaGui, gridBagConstraints);

        chkGeoPortal.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkGeoPortal.setText("GeoPortal GUI");
        chkGeoPortal.setContentAreaFilled(false);
        chkGeoPortal.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gaz_geo_portal_gui}"),
                chkGeoPortal,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(chkGeoPortal, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Locationtypes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(jLabel7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gaz_dienst}"),
                cbDienst,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(cbDienst, gridBagConstraints);

        txtLocationtypes.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gaz_locationtypes}"),
                txtLocationtypes,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGazetteer.add(txtLocationtypes, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Suchkataloge");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panGazetteer.add(jLabel2, gridBagConstraints);

        jPanel11.setOpaque(false);
        jPanel11.setPreferredSize(new java.awt.Dimension(26, 26));

        final javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                214,
                Short.MAX_VALUE));
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                96,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        panGazetteer.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab1.add(panGazetteer, gridBagConstraints);

        panBemerkung.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new java.awt.GridBagLayout());

        lblBemerkung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBemerkung.setText("Bemerkung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBemerkung.add(lblBemerkung, gridBagConstraints);

        lblLetzteAktualisierung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblLetzteAktualisierung.setText("letzte Aktualisierung durch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBemerkung.add(lblLetzteAktualisierung, gridBagConstraints);

        lblAenderungsdatum.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAenderungsdatum.setText("zuletzt geändert am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBemerkung.add(lblAenderungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.letzte_aenderung_durch}"),
                cbLetzteAktualisierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBemerkung.add(cbLetzteAktualisierung, gridBagConstraints);

        scpTxtBemerkung.setBorder(null);
        scpTxtBemerkung.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtBemerkung.setColumns(15);
        txtBemerkung.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(10);
        txtBemerkung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkungen}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpTxtBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBemerkung.add(scpTxtBemerkung, gridBagConstraints);
        scpTxtBemerkung.getViewport().setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.letzte_aenderung_am}"),
                jXDatePicker1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panBemerkung.add(jXDatePicker1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab1.add(panBemerkung, gridBagConstraints);

        /*
         *
         * panCards.add(panTab1, "card2");
         */
        layer1 = new JXLayer<JComponent>(panTab1);
        layer1.setUI(lockLayer1);
        panCards.add(layer1, CARD_1);
        setPanContent(layer1);

        panTab2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panTab2.setOpaque(false);
        panTab2.setLayout(new java.awt.GridBagLayout());

        panAktUmsetzung.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panAktUmsetzung.setOpaque(false);
        panAktUmsetzung.setLayout(new java.awt.GridBagLayout());

        lblAktGeometriemodell.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktGeometriemodell.setText("Geometriemodell");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktGeometriemodell, gridBagConstraints);

        chkAktObjektRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktObjektRenderer.setText("Objektrenderer");
        chkAktObjektRenderer.setContentAreaFilled(false);
        chkAktObjektRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_objektrenderer}"),
                chkAktObjektRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktObjektRenderer, gridBagConstraints);

        chkAktAggObjektRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktAggObjektRenderer.setText("aggregierende Objektrenderer");
        chkAktAggObjektRenderer.setContentAreaFilled(false);
        chkAktAggObjektRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_aggregierender_objektrenderer}"),
                chkAktAggObjektRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktAggObjektRenderer, gridBagConstraints);

        chkAktFeatureRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktFeatureRenderer.setText("Feature Renderer");
        chkAktFeatureRenderer.setContentAreaFilled(false);
        chkAktFeatureRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_feature_renderer}"),
                chkAktFeatureRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktFeatureRenderer, gridBagConstraints);

        chkAktEditor.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktEditor.setText("Editoren");
        chkAktEditor.setContentAreaFilled(false);
        chkAktEditor.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_editoren}"),
                chkAktEditor,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktEditor, gridBagConstraints);

        chkAktReportTemplates.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktReportTemplates.setText("Report Templates");
        chkAktReportTemplates.setContentAreaFilled(false);
        chkAktReportTemplates.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_report_templates}"),
                chkAktReportTemplates,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktReportTemplates, gridBagConstraints);

        chkAktSpezSuchen.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkAktSpezSuchen.setText("spezifisches Suchen");
        chkAktSpezSuchen.setContentAreaFilled(false);
        chkAktSpezSuchen.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_spezifisches_suchen}"),
                chkAktSpezSuchen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(chkAktSpezSuchen, gridBagConstraints);

        lblAktKategorie.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktKategorie.setText("Kategorie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktKategorie, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_kategorie}"),
                cbAktKategorie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(cbAktKategorie, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_geometriemodell}"),
                cbAktGeometriemodell,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(cbAktGeometriemodell, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel4.setText("Aktuelle Umsetzung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panAktUmsetzung.add(jLabel4, gridBagConstraints);

        lblAktAnspSpezif.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktAnspSpezif.setText("Ansprechpartner Spezifikation      ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panAktUmsetzung.add(lblAktAnspSpezif, gridBagConstraints);

        lblAktImplementierung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktImplementierung.setText("Implementierung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktImplementierung, gridBagConstraints);

        lblAktRealisierung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktRealisierung.setText("Realisierungsstand");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktRealisierung, gridBagConstraints);

        lblAktWartung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktWartung.setText("Wartungsvertrag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktWartung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_wartungsvertrag}"),
                cbAktWartung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(cbAktWartung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_realisierungsstand}"),
                cbAktRealisierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(cbAktRealisierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_ansprechpartner_implementierung}"),
                cbAktImplementierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(cbAktImplementierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_ansprechpartner_spezifizierung}"),
                cbAktAnspSpezif,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panAktUmsetzung.add(cbAktAnspSpezif, gridBagConstraints);

        lblAktAngebFachverfahren.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAktAngebFachverfahren.setText("angebundenes Fachverfahren");
        lblAktAngebFachverfahren.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAktUmsetzung.add(lblAktAngebFachverfahren, gridBagConstraints);

        scpAktAngebFachverfahren.setBorder(null);
        scpAktAngebFachverfahren.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtAktAngebFachverfahren.setColumns(15);
        txtAktAngebFachverfahren.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtAktAngebFachverfahren.setLineWrap(true);
        txtAktAngebFachverfahren.setRows(4);
        txtAktAngebFachverfahren.setWrapStyleWord(true);
        txtAktAngebFachverfahren.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.akt_angeb_fachverfahren}"),
                txtAktAngebFachverfahren,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpAktAngebFachverfahren.setViewportView(txtAktAngebFachverfahren);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 6);
        panAktUmsetzung.add(scpAktAngebFachverfahren, gridBagConstraints);
        scpTxtInhalt.getViewport().setOpaque(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        panTab2.add(panAktUmsetzung, gridBagConstraints);

        panZukUmsetzung.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panZukUmsetzung.setOpaque(false);
        panZukUmsetzung.setLayout(new java.awt.GridBagLayout());

        lblZukGeometriemodell.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukGeometriemodell.setText("Geometriemodell");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukGeometriemodell, gridBagConstraints);

        chkZukObjektRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukObjektRenderer.setText("Objektrenderer");
        chkZukObjektRenderer.setContentAreaFilled(false);
        chkZukObjektRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_objektrenderer}"),
                chkZukObjektRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukObjektRenderer, gridBagConstraints);

        chkZukAggObjektRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukAggObjektRenderer.setText("aggregierende Objektrenderer");
        chkZukAggObjektRenderer.setContentAreaFilled(false);
        chkZukAggObjektRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_aggregierender_objektrenderer}"),
                chkZukAggObjektRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukAggObjektRenderer, gridBagConstraints);

        chkZukFeatureRenderer.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukFeatureRenderer.setText("Feature Renderer");
        chkZukFeatureRenderer.setContentAreaFilled(false);
        chkZukFeatureRenderer.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_feature_renderer}"),
                chkZukFeatureRenderer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukFeatureRenderer, gridBagConstraints);

        chkZukEditor.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukEditor.setText("Editoren");
        chkZukEditor.setContentAreaFilled(false);
        chkZukEditor.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_editoren}"),
                chkZukEditor,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukEditor, gridBagConstraints);

        chkZukReportTemplates.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukReportTemplates.setText("Report Templates");
        chkZukReportTemplates.setContentAreaFilled(false);
        chkZukReportTemplates.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_report_templates}"),
                chkZukReportTemplates,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukReportTemplates, gridBagConstraints);

        chkZukSpezSuchen.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkZukSpezSuchen.setText("spezifisches Suchen");
        chkZukSpezSuchen.setContentAreaFilled(false);
        chkZukSpezSuchen.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_spezifisches_suchen}"),
                chkZukSpezSuchen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(chkZukSpezSuchen, gridBagConstraints);

        lblZukKategorie.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukKategorie.setText("Kategorie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukKategorie, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_geometriemodell}"),
                cbZukGeometriemodell,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(cbZukGeometriemodell, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_kategorie}"),
                cbZukKategorie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(cbZukKategorie, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("Entwicklungsziel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panZukUmsetzung.add(jLabel5, gridBagConstraints);

        lblZukAnspSpezif.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukAnspSpezif.setText("Ansprechpartner Spezifikation      ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panZukUmsetzung.add(lblZukAnspSpezif, gridBagConstraints);

        lblZukImplementierung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukImplementierung.setText("Implementierung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukImplementierung, gridBagConstraints);

        lblZukRealisierung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukRealisierung.setText("Realisierungsstand");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukRealisierung, gridBagConstraints);

        lblZukWartung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukWartung.setText("Wartungsvertrag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukWartung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_wartungsvertrag}"),
                cbZukWartung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(cbZukWartung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_ansprechpartner_implementierung}"),
                cbZukImplementierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(cbZukImplementierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_ansprechpartner_spezifizierung}"),
                cbZukAnspSpezif,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panZukUmsetzung.add(cbZukAnspSpezif, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_realisierungsstand}"),
                cbZukRealisierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(cbZukRealisierung, gridBagConstraints);

        lblZukAngebFachverfahren.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblZukAngebFachverfahren.setText("angebundenes Fachverfahren");
        lblZukAngebFachverfahren.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZukUmsetzung.add(lblZukAngebFachverfahren, gridBagConstraints);

        scpZukAngebFachverfahren.setBorder(null);
        scpZukAngebFachverfahren.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtZukAngebFachverfahren.setColumns(15);
        txtZukAngebFachverfahren.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtZukAngebFachverfahren.setLineWrap(true);
        txtZukAngebFachverfahren.setRows(4);
        txtZukAngebFachverfahren.setWrapStyleWord(true);
        txtZukAngebFachverfahren.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuk_angeb_fachverfahren}"),
                txtZukAngebFachverfahren,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpZukAngebFachverfahren.setViewportView(txtZukAngebFachverfahren);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 6);
        panZukUmsetzung.add(scpZukAngebFachverfahren, gridBagConstraints);
        scpTxtInhalt.getViewport().setOpaque(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTab2.add(panZukUmsetzung, gridBagConstraints);

        panOperativ.setOpaque(false);
        panOperativ.setLayout(new java.awt.GridBagLayout());

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Operatives System?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOperativ.add(jLabel12, gridBagConstraints);

        chkFachthema.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkFachthema.setText("Fachthema");
        chkFachthema.setContentAreaFilled(false);
        chkFachthema.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fachthema}"),
                chkFachthema,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOperativ.add(chkFachthema, gridBagConstraints);

        chkFachverfahren.setFont(new java.awt.Font("Tahoma", 1, 11));
        chkFachverfahren.setText("Fachverfahren");
        chkFachverfahren.setContentAreaFilled(false);
        chkFachverfahren.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fachverfahren}"),
                chkFachverfahren,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panOperativ.add(chkFachverfahren, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 6, 6);
        panTab2.add(panOperativ, gridBagConstraints);

        /*
         *
         * panCards.add(panTab2, "card3");
         */
        layer2 = new JXLayer<JComponent>(panTab2);
        layer2.setUI(lockLayer2);
        panCards.add(layer2, CARD_2);
        setPanContent(layer2);

        panCenter.add(panCards, java.awt.BorderLayout.CENTER);

        add(panCenter, java.awt.BorderLayout.CENTER);

        panSouth.setOpaque(false);
        panSouth.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 7, 0));
        panButtons.setOpaque(false);

        lblDescriptionBack.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblDescriptionBack.setForeground(new java.awt.Color(255, 255, 255));
        lblDescriptionBack.setText("Beschreibung und Dienste");
        panButtons.add(lblDescriptionBack);

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panButtons.add(btnBack);

        btnForwrd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        btnForwrd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnForwrd.setBorderPainted(false);
        btnForwrd.setContentAreaFilled(false);
        btnForwrd.setFocusPainted(false);
        btnForwrd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwrdActionPerformed(evt);
                }
            });
        panButtons.add(btnForwrd);

        lblDescriptionFor.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblDescriptionFor.setForeground(new java.awt.Color(255, 255, 255));
        lblDescriptionFor.setText("Umsetzung in WuNDa         ");
        panButtons.add(lblDescriptionFor);

        panSouth.add(panButtons, java.awt.BorderLayout.CENTER);

        add(panSouth, java.awt.BorderLayout.PAGE_END);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * Move-backward button action.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(panCards, CARD_1);
        btnBack.setEnabled(false);
        btnForwrd.setEnabled(true);
        lblDescriptionFor.setEnabled(true);
        lblDescriptionBack.setEnabled(false);
        // lblPages.setText("1 / 2");
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * Move forward button action.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwrdActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwrdActionPerformed
        cardLayout.show(panCards, CARD_2);
        btnBack.setEnabled(true);
        btnForwrd.setEnabled(false);
        lblDescriptionFor.setEnabled(false);
        lblDescriptionBack.setEnabled(true);
        // lblPages.setText("2 / 2");
    }                                                                             //GEN-LAST:event_btnForwrdActionPerformed

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
    }
}
