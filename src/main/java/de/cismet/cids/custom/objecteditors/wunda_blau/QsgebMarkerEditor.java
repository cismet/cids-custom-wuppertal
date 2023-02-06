/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import lombok.Getter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXBusyLabel;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.StringReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;

import de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;
import de.cismet.cids.custom.wunda_blau.search.server.QsgebStatusLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import de.cismet.cids.editors.SaveVetoable;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $1.0$, $31.05.2018$ Die TIFF Anzeige ist nicht gut und muss noch verbessert werden.
 */
public class QsgebMarkerEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    FooterComponentProvider,
    BindingGroupStore,
    PropertyChangeListener,
    RequestsFullSizeComponent,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(QsgebMarkerEditor.class);

    public static final String FIELD__STATUS = "status";
    public static final String FIELD__STATUS__ID = "status.id";
    public static final String FIELD__STATUS__NAME = "status.name";
    public static final String FIELD__STATUS__SCHLUESSEL = "status.schluessel";
    public static final String STATUS_PRUEFEN_SCHLUESSEL = "pruefen";
    public static final String STATUS_ZUR_BEARBEITUNG_SCHLUESSEL = "zurBearbeitung";
    public static final String STATUS_IN_BEARBEITUNG_SCHLUESSEL = "inBearbeitung";
    public static final String STATUS_KEINE_BEARBEITUNG_SCHLUESSEL = "keineBearbeitung";
    public static final String STATUS_ERLEDIGT_SCHLUESSEL = "erledigt";
    public static final String FIELD__ID = "id";
    public static final String FIELD__LAGE = "lage";
    public static final String FIELD__ANGELEGT_DURCH = "angelegt_durch";
    public static final String FIELD__DATUM_ANGELEGT = "datum_angelegt";
    public static final String FIELD__GEPRUEFT_DURCH = "geprueft_durch";
    public static final String FIELD__DATUM_PRUEFUNG = "datum_pruefung";
    public static final String FIELD__BEARBEITUNG_DURCH = "bearbeitung_durch";
    public static final String FIELD__DATUM_BEARBEITUNG = "datum_bearbeitung";
    public static final String FIELD__GEOREFERENZ = "georeferenz";
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field";
    public static final String FIELD__ERGEBNIS = "ergebnis";
    public static final String FIELD__HISTORISCH = "historisch";
    public static final String FIELD__DATUM_HISTORISCH = "datum_historisch";
    public static final String FIELD__FLURSTUECK = "flurstueck";
    public static final String TABLE_NAME = "qsgeb_marker";
    public static final String TABLE_GEOM = "geom";
    public static final String FIELD__GEO_FIELD = "geo_field";
    public static final String TABLE_NAME_FLUR = "alkis_landparcel";
    public static final String FIELD__GEMARKUNG = "gemarkung";
    public static final String FIELD__FLUR = "flur";
    public static final String FIELD__ZAEHLER = "fstck_zaehler";
    public static final String FIELD__NENNER = "fstck_nenner";
    public static final String TABLE_NAME_STATUS = "qsgeb_status";
    public static final String FIELD__SCHLUESSEL = "schluessel";

    protected static XBoundingBox INITIAL_BOUNDINGBOX = new XBoundingBox(
            2583621.251964098d,
            5682507.032498134d,
            2584022.9413952776d,
            5682742.852810634d,
            ClientAlkisConf.getInstance().getSrsService(),
            true);
    protected static Crs CRS = new Crs(
            ClientAlkisConf.getInstance().getSrsService(),
            ClientAlkisConf.getInstance().getSrsService(),
            ClientAlkisConf.getInstance().getSrsService(),
            true,
            true);

    private static QsGebProperties PROPERTIES;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum DocumentCard {

        //~ Enum constants -----------------------------------------------------

        BUSY, DOCUMENT, NO_DOCUMENT, ERROR
    }

    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;
    private String statusSchluesselAttribute;
    private String pruefAttribute;
    private String bearbeitungAttribute;
    private String pruefdatumAttribute;
    private String bearbeitungsdatumAttribute;
    private QsgebStatusLightweightSearch statusSearch;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnImages;
    JButton btnInfo;
    private DefaultBindableReferenceCombo cbErgebnis;
    private JComboBox cbGeom;
    FastBindableReferenceCombo cbStatus;
    private JCheckBox jCkbHistorisch;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JScrollPane jScrollPane2;
    private JXBusyLabel jxLBusy;
    private JLabel lblAngelegtDurch;
    private JLabel lblAngelegt_txt;
    private JLabel lblBearbeitungDurch;
    private JLabel lblBearbeitung_txt;
    private JLabel lblBemerkung_txt;
    private JLabel lblDatumAngelegt;
    private JLabel lblDatumAngelegt_txt;
    private JLabel lblDatumBearbeitung;
    private JLabel lblDatumBearbeitung_txt;
    private JLabel lblDatumGeprueft;
    private JLabel lblDatumGeprueft_txt;
    private JLabel lblDatumHistorisch;
    private JLabel lblDatumHistorisch_txt;
    private JLabel lblErgebnis_txt;
    private JLabel lblFlur;
    private JLabel lblFlur_txt;
    private JLabel lblFlurstueck;
    private JLabel lblFlurstueck_txt;
    private JLabel lblGemarkung;
    private JLabel lblGemarkung_txt;
    private JLabel lblGeom_txt;
    private JLabel lblGeprueftDurch;
    private JLabel lblGeprueft_txt;
    private JLabel lblHeaderPages;
    private JLabel lblHistorisch_txt;
    private JLabel lblId;
    private JLabel lblId_txt;
    JLabel lblImages;
    JLabel lblInfo;
    private JLabel lblJahr;
    private JLabel lblJahr_txt;
    private JLabel lblKarte;
    private JLabel lblLage_txt;
    private JLabel lblMarkerInfo;
    private JLabel lblReducedSize;
    private JLabel lblStatus_txt;
    private JList lstPages;
    private JPanel panBild;
    private JPanel panContent;
    private JPanel panDaten;
    JPanel panFooter;
    private JPanel panLeft;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panRight;
    private JPanel pnlBild;
    private RoundedPanel pnlDocument;
    private SemiRoundedPanel pnlHeaderDocument;
    private SemiRoundedPanel pnlHeaderPages;
    private RoundedPanel pnlPages;
    private RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private RoundedPanel rpInfo;
    private RoundedPanel rpKarte;
    private JScrollPane scpPages;
    private SemiRoundedPanel semiRoundedPanel7;
    private SemiRoundedPanel semiRoundedPanel8;
    private SQLDateToStringConverter sqlDateToStringConverter;
    private JTextArea taBemerkung;
    private JTextField txtLage;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public QsgebMarkerEditor() {
    }

    /**
     * Creates a new QsgebMarkerEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public QsgebMarkerEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void showMeasureIsLoading() {
        showDocumentCard(DocumentCard.BUSY);
    }

    @Override
    public void showMeasurePanel() {
        showDocumentCard(DocumentCard.DOCUMENT);
        btnImages.setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     */
    private void initProperties() {
        if (PROPERTIES == null) {
            try {
                PROPERTIES = loadPropertiesFromServerResources(getConnectionContext());
            } catch (final Exception ex) {
                LOG.warn("properties could'nt be loaded. Editor/Renderer might not working as expected !", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initStatus() {
        this.statusSearch = new QsgebStatusLightweightSearch(
                "%1$2s",
                new String[] { "NAME" },
                statusSchluesselAttribute);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);

        initProperties();
        initStatus();
        initComponents();

        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        } else {
            setReadOnly();
        }
        cbStatus.setMetaClassFromTableName("WUNDA_BLAU", TABLE_NAME_STATUS);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        sqlDateToStringConverter = new SQLDateToStringConverter();
        panFooter = new JPanel();
        panLeft = new JPanel();
        lblInfo = new JLabel();
        btnInfo = new JButton();
        panRight = new JPanel();
        btnImages = new JButton();
        lblImages = new JLabel();
        panContent = new RoundedPanel();
        rpInfo = new RoundedPanel();
        semiRoundedPanel8 = new SemiRoundedPanel();
        lblMarkerInfo = new JLabel();
        panDaten = new JPanel();
        lblJahr_txt = new JLabel();
        lblGeom_txt = new JLabel();
        txtLage = new JTextField();
        if (isEditor) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblGemarkung_txt = new JLabel();
        lblJahr = new JLabel();
        lblId_txt = new JLabel();
        lblId = new JLabel();
        lblGemarkung = new JLabel();
        lblFlur = new JLabel();
        lblFlurstueck = new JLabel();
        lblAngelegtDurch = new JLabel();
        lblDatumAngelegt = new JLabel();
        lblDatumGeprueft = new JLabel();
        lblDatumBearbeitung = new JLabel();
        lblGeprueftDurch = new JLabel();
        lblBearbeitungDurch = new JLabel();
        lblFlur_txt = new JLabel();
        lblFlurstueck_txt = new JLabel();
        lblStatus_txt = new JLabel();
        lblBemerkung_txt = new JLabel();
        lblAngelegt_txt = new JLabel();
        lblGeprueft_txt = new JLabel();
        lblBearbeitung_txt = new JLabel();
        lblDatumAngelegt_txt = new JLabel();
        lblDatumGeprueft_txt = new JLabel();
        lblDatumBearbeitung_txt = new JLabel();
        cbErgebnis = new DefaultBindableReferenceCombo(true);
        lblLage_txt = new JLabel();
        lblErgebnis_txt = new JLabel();
        jScrollPane2 = new JScrollPane();
        taBemerkung = new JTextArea();
        cbStatus = new FastBindableReferenceCombo(
                statusSearch,
                statusSearch.getRepresentationPattern(),
                statusSearch.getRepresentationFields());
        lblHistorisch_txt = new JLabel();
        jCkbHistorisch = new JCheckBox();
        lblDatumHistorisch_txt = new JLabel();
        lblDatumHistorisch = new JLabel();
        rpKarte = new RoundedPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panPreviewMap = new DefaultPreviewMapPanel();
        panBild = new JPanel();
        pnlDocument = new RoundedPanel();
        pnlHeaderDocument = new SemiRoundedPanel();
        lblReducedSize = new JLabel();
        pnlBild = new JPanel();
        jPanel1 = new JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
                PROPERTIES.getRasterfariUrl(),
                this,
                getConnectionContext());
        jPanel2 = new JPanel();
        jxLBusy = new JXBusyLabel(new Dimension(64, 64));
        jPanel3 = new JPanel();
        jLabel1 = new JLabel();
        jPanel4 = new JPanel();
        jLabel2 = new JLabel();
        pnlPages = new RoundedPanel();
        pnlHeaderPages = new SemiRoundedPanel();
        lblHeaderPages = new JLabel();
        scpPages = new JScrollPane();
        lstPages = rasterfariDocumentLoaderPanel1.getLstPages();

        panFooter.setOpaque(false);
        panFooter.setLayout(new GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new Font("DejaVu Sans", 1, 14)); // NOI18N
        lblInfo.setForeground(new Color(255, 255, 255));
        lblInfo.setText("Info");
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnInfo.setBorderPainted(false);
        btnInfo.setContentAreaFilled(false);
        btnInfo.setEnabled(false);
        btnInfo.setFocusPainted(false);
        btnInfo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnInfoActionPerformed(evt);
                }
            });
        panLeft.add(btnInfo);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panFooter.add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);

        btnImages.setIcon(new ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        btnImages.setToolTipText("");
        btnImages.setBorderPainted(false);
        btnImages.setContentAreaFilled(false);
        btnImages.setFocusPainted(false);
        btnImages.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnImagesActionPerformed(evt);
                }
            });
        panRight.add(btnImages);

        lblImages.setFont(new Font("DejaVu Sans", 1, 14)); // NOI18N
        lblImages.setForeground(new Color(255, 255, 255));
        lblImages.setText("Dokument");
        panRight.add(lblImages);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        setAutoscrolls(true);
        setOpaque(false);
        setPreferredSize(new Dimension(910, 737));
        setLayout(new CardLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        rpInfo.setName(""); // NOI18N
        rpInfo.setLayout(new GridBagLayout());

        semiRoundedPanel8.setBackground(Color.darkGray);
        semiRoundedPanel8.setLayout(new GridBagLayout());

        lblMarkerInfo.setForeground(new Color(255, 255, 255));
        lblMarkerInfo.setText("Marker Information");
        lblMarkerInfo.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        semiRoundedPanel8.add(lblMarkerInfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpInfo.add(semiRoundedPanel8, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setPreferredSize(new Dimension(340, 236));
        panDaten.setLayout(new GridBagLayout());

        lblJahr_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblJahr_txt.setText("Jahr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblJahr_txt, gridBagConstraints);

        lblGeom_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom_txt.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblGeom_txt, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.lage}"),
                txtLage,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 2);
        panDaten.add(txtLage, gridBagConstraints);

        if (isEditor) {
            if (isEditor) {
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.georeferenz}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 4, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        lblGemarkung_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGemarkung_txt.setText("Gemarkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblGemarkung_txt, gridBagConstraints);

        lblJahr.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lblJahr.setText("neu");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblJahr, gridBagConstraints);

        lblId_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblId_txt.setText("lfd Nr.:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblId_txt, gridBagConstraints);

        lblId.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.id}"),
                lblId,
                BeanProperty.create("text"));
        binding.setSourceNullValue("- -");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 6, 4, 4);
        panDaten.add(lblId, gridBagConstraints);

        lblGemarkung.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lblGemarkung.setMinimumSize(new Dimension(120, 15));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.gemarkung}"),
                lblGemarkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 6, 4, 4);
        panDaten.add(lblGemarkung, gridBagConstraints);

        lblFlur.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lblFlur.setMinimumSize(new Dimension(60, 15));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.flur}"),
                lblFlur,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 6, 4, 4);
        panDaten.add(lblFlur, gridBagConstraints);

        lblFlurstueck.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lblFlurstueck.setMinimumSize(new Dimension(120, 15));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.flurstueck}"),
                lblFlurstueck,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 6, 4, 4);
        panDaten.add(lblFlurstueck, gridBagConstraints);

        lblAngelegtDurch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.angelegt_durch}"),
                lblAngelegtDurch,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblAngelegtDurch, gridBagConstraints);

        lblDatumAngelegt.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_angelegt}"),
                lblDatumAngelegt,
                BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblDatumAngelegt, gridBagConstraints);

        lblDatumGeprueft.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_pruefung}"),
                lblDatumGeprueft,
                BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblDatumGeprueft, gridBagConstraints);

        lblDatumBearbeitung.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_bearbeitung}"),
                lblDatumBearbeitung,
                BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblDatumBearbeitung, gridBagConstraints);

        lblGeprueftDurch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.geprueft_durch}"),
                lblGeprueftDurch,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblGeprueftDurch, gridBagConstraints);

        lblBearbeitungDurch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bearbeitung_durch}"),
                lblBearbeitungDurch,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 6, 2, 4);
        panDaten.add(lblBearbeitungDurch, gridBagConstraints);

        lblFlur_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFlur_txt.setText("Flur:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblFlur_txt, gridBagConstraints);

        lblFlurstueck_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFlurstueck_txt.setText("Flurstück:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblFlurstueck_txt, gridBagConstraints);

        lblStatus_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStatus_txt.setText("Status:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 5, 5);
        panDaten.add(lblStatus_txt, gridBagConstraints);

        lblBemerkung_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung_txt.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblBemerkung_txt, gridBagConstraints);

        lblAngelegt_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAngelegt_txt.setText("Angelegt von:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 5, 5);
        panDaten.add(lblAngelegt_txt, gridBagConstraints);

        lblGeprueft_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeprueft_txt.setText("Geprüft von:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblGeprueft_txt, gridBagConstraints);

        lblBearbeitung_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBearbeitung_txt.setText("Bearbeitung von:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblBearbeitung_txt, gridBagConstraints);

        lblDatumAngelegt_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatumAngelegt_txt.setText("am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblDatumAngelegt_txt, gridBagConstraints);

        lblDatumGeprueft_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatumGeprueft_txt.setText("am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblDatumGeprueft_txt, gridBagConstraints);

        lblDatumBearbeitung_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatumBearbeitung_txt.setText("am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblDatumBearbeitung_txt, gridBagConstraints);

        cbErgebnis.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbErgebnis.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ergebnis}"),
                cbErgebnis,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(cbErgebnis, gridBagConstraints);

        lblLage_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLage_txt.setText("Lage:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblLage_txt, gridBagConstraints);

        lblErgebnis_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblErgebnis_txt.setText("Ergebnis:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblErgebnis_txt, gridBagConstraints);

        taBemerkung.setColumns(1);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(5);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(jScrollPane2, gridBagConstraints);

        ((FastBindableReferenceCombo)cbStatus).setNullable(false);
        cbStatus.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.status}"),
                cbStatus,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(cbStatus, gridBagConstraints);

        lblHistorisch_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHistorisch_txt.setText("Historisch:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblHistorisch_txt, gridBagConstraints);

        jCkbHistorisch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.historisch}"),
                jCkbHistorisch,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 4, 0, 0);
        panDaten.add(jCkbHistorisch, gridBagConstraints);

        lblDatumHistorisch_txt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatumHistorisch_txt.setText("am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblDatumHistorisch_txt, gridBagConstraints);

        lblDatumHistorisch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_historisch}"),
                lblDatumHistorisch,
                BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(lblDatumHistorisch, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        rpInfo.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panContent.add(rpInfo, gridBagConstraints);

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        lblKarte.setText("Lage");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(lblKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        panPreviewMap.setPreferredSize(new Dimension(500, 250));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panContent.add(rpKarte, gridBagConstraints);

        add(panContent, "cardContent");

        panBild.setMinimumSize(new Dimension(380, 30));
        panBild.setOpaque(false);
        panBild.setLayout(new GridBagLayout());

        pnlHeaderDocument.setBackground(Color.darkGray);
        pnlHeaderDocument.setPreferredSize(new Dimension(163, 25));
        pnlHeaderDocument.setLayout(new GridBagLayout());

        lblReducedSize.setForeground(new Color(254, 254, 254));
        lblReducedSize.setText("Vorschau Zusatzdokument");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        pnlHeaderDocument.add(lblReducedSize, gridBagConstraints);

        pnlDocument.add(pnlHeaderDocument, BorderLayout.NORTH);

        pnlBild.setOpaque(false);
        pnlBild.setLayout(new CardLayout());

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(rasterfariDocumentLoaderPanel1, BorderLayout.CENTER);

        pnlBild.add(jPanel1, "DOCUMENT");

        jPanel2.setLayout(new BorderLayout());

        jxLBusy.setHorizontalAlignment(SwingConstants.CENTER);
        jxLBusy.setPreferredSize(new Dimension(64, 64));
        jPanel2.add(jxLBusy, BorderLayout.CENTER);

        pnlBild.add(jPanel2, "BUSY");

        jPanel3.setLayout(new BorderLayout());

        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Für diesen Marker ist kein Dokument vorhanden.");
        jPanel3.add(jLabel1, BorderLayout.CENTER);

        pnlBild.add(jPanel3, "NO_DOCUMENT");

        jPanel4.setLayout(new BorderLayout());

        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Das Dokument für diesen Marker kann nicht geladen werden.");
        jPanel4.add(jLabel2, BorderLayout.CENTER);

        pnlBild.add(jPanel4, "ERROR");

        pnlDocument.add(pnlBild, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panBild.add(pnlDocument, gridBagConstraints);

        pnlHeaderPages.setBackground(new Color(51, 51, 51));
        pnlHeaderPages.setLayout(new FlowLayout());

        lblHeaderPages.setForeground(new Color(255, 255, 255));
        lblHeaderPages.setText(NbBundle.getMessage(
                QsgebMarkerEditor.class,
                "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, BorderLayout.NORTH);

        scpPages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new Dimension(85, 75));

        lstPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panBild.add(pnlPages, gridBagConstraints);

        add(panBild, "cardBild");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnInfoActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        ((CardLayout)getLayout()).show(this, "cardContent");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }//GEN-LAST:event_btnInfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnImagesActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnImagesActionPerformed
        ((CardLayout)getLayout()).show(this, "cardBild");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }//GEN-LAST:event_btnImagesActionPerformed

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.prepareForSave().noGeom"));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(
                            QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.prepareForSave().wrongGeom"));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        // Status
        errorMessage.append(setUserStatus());
        try {
            if ((cidsBean.getProperty(FIELD__ERGEBNIS) == null)
                        && STATUS_ERLEDIGT_SCHLUESSEL.equals(cidsBean.getProperty(FIELD__STATUS__SCHLUESSEL))) {
                LOG.warn("Wrong Ergebnis specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.prepareForSave().noErgebnis"));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Ergebnis not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
        try {
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("remove propchange qsgeb_marker: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange qsgeb_marker: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            prepareIt();
        } catch (final Exception ex) {
            LOG.warn("Error setCidsBean.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareIt() {
        saveFirstAttributes();
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            prepareFirstMarker();
        }
        if (cidsBean != null) {
            setYear();           // Ist nur ein Label, welches sich aus dem Anlegedatum ergibt.
            if ((isEditor)) {
                refreshStatus(); // setzt den Status auf Ausgangsstatus.
            }
            loadDocument();
        }

        if ((isEditor)) {
            setEditableStatus(); // Darf Status editiert werden.
        }

        setHistorischOnly(); // Historisch ist nur dann interessant, wenn der Marker historisiert wurde, um ihn
                             // wieder zu enthistorisieren.
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareFirstMarker() {
        final CidsBean statusBean = getOtherTableValue(
                TABLE_NAME_STATUS,
                " where "
                        + FIELD__SCHLUESSEL
                        + " ilike '"
                        + STATUS_PRUEFEN_SCHLUESSEL
                        + "'",
                getConnectionContext());
        try {
            cidsBean.setProperty(
                FIELD__STATUS,
                statusBean);
        } catch (Exception ex) {
            LOG.warn("prepareFirstMarker: Status not set.", ex);
        }
        cbStatus.setEnabled(false);
        lblAngelegtDurch.setText(getCurrentUser());
        final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        final Date datum = new Date();
        lblDatumAngelegt.setText(df.format(datum));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  card  DOCUMENT ME!
     */
    private void showDocumentCard(final DocumentCard card) {
        ((CardLayout)pnlBild.getLayout()).show(pnlBild, card.toString());
    }

    /**
     * DOCUMENT ME!
     */
    private void loadDocument() {
        btnImages.setEnabled(false);
        rasterfariDocumentLoaderPanel1.setDocument(getPictureUrl());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getPictureUrl() {
        final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        final String jahr = String.valueOf(sdf.format(datum));
        final String id = cidsBean.getProperty(FIELD__ID).toString();
        return PROPERTIES.getRasterfariPath() + "/" + jahr + "/qsgeb_" + jahr + "_" + id + ".tif";
    }

    /**
     * DOCUMENT ME!
     */
    private void setYear() {
        if (cidsBean.getProperty(FIELD__DATUM_ANGELEGT) != null) {
            final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            lblJahr.setText(String.valueOf(sdf.format(datum)));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  myPoint  DOCUMENT ME!
     */
    private void lookForLandparcel(final Geometry myPoint) {
        final MetaClass mc = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                TABLE_NAME_FLUR,
                getConnectionContext());
        if (mc != null) {
            // Suche Konfigurieren
            final BufferingGeosearch search = new BufferingGeosearch();
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(myPoint);

            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        // Suche ausführen
                        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                    .customServerSearch(
                                        SessionManager.getSession().getUser(),
                                        search,
                                        getConnectionContext());
                        if ((mons != null) && !mons.isEmpty()) {
                            final MetaObjectNode mon = mons.toArray(new MetaObjectNode[0])[0];

                            final MetaObject mo = SessionManager.getProxy()
                                        .getMetaObject(mon.getObjectId(),
                                            mon.getClassId(),
                                            mon.getDomain(),
                                            getConnectionContext());
                            final CidsBean flurstueckBean = mo.getBean();
                            return flurstueckBean;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean flurstueckBean = get();
                            if (flurstueckBean != null) {
                                lblGemarkung.setText(flurstueckBean.getProperty(FIELD__GEMARKUNG).toString());
                                lblFlur.setText(flurstueckBean.getProperty(FIELD__FLUR).toString());
                                final String zaehler = flurstueckBean.getProperty(FIELD__ZAEHLER).toString();
                                if (flurstueckBean.getProperty(FIELD__NENNER) != null) {
                                    lblFlurstueck.setText(zaehler + "/"
                                                + flurstueckBean.getProperty(FIELD__NENNER).toString());
                                } else {
                                    lblFlurstueck.setText(zaehler);
                                }
                            }
                        } catch (final InterruptedException | ExecutionException ex) {
                            LOG.warn("Geom Search Error.", ex);
                        }
                    }
                }.execute();
        } else {
            LOG.error("Could not find MetaClass for alkis_landparcel ");
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void saveFirstAttributes() {
        if (cidsBean == null) {
            statusSchluesselAttribute = null;
            pruefAttribute = null;
            bearbeitungAttribute = null;
            pruefdatumAttribute = null;
            bearbeitungsdatumAttribute = null;
        } else {
            if (cidsBean.getProperty(FIELD__STATUS__ID) == null) {
                statusSchluesselAttribute = "";
            } else {
                statusSchluesselAttribute = cidsBean.getProperty(FIELD__STATUS__SCHLUESSEL).toString();
            }
            pruefAttribute = setNotNull(cidsBean.getProperty(FIELD__GEPRUEFT_DURCH));
            bearbeitungAttribute = setNotNull(cidsBean.getProperty(FIELD__BEARBEITUNG_DURCH));
            pruefdatumAttribute = setNotNull(cidsBean.getProperty(FIELD__DATUM_PRUEFUNG));
            bearbeitungsdatumAttribute = setNotNull(cidsBean.getProperty(FIELD__DATUM_BEARBEITUNG));
        }
    }
    /**
     * COALESCE.
     *
     * @param   notNullString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String setNotNull(final Object notNullString) {
        if (notNullString == null) {
            return "";
        }
        return notNullString.toString();
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        cbStatus.setEnabled(false);
        cbErgebnis.setEnabled(false);
        txtLage.setEnabled(false);
        taBemerkung.setEnabled(false);
        lblGeom_txt.setVisible(false);
        jCkbHistorisch.setEnabled(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void setHistorischOnly() {
        if ((cidsBean.getProperty(FIELD__HISTORISCH) == null)
                    || Boolean.FALSE.equals(cidsBean.getProperty(FIELD__HISTORISCH))) {
            lblHistorisch_txt.setVisible(false);
            jCkbHistorisch.setVisible(false);
            lblDatumHistorisch_txt.setVisible(false);
            lblDatumHistorisch.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOREFERENZ) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, 20.0);
            } else {
                final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                final BoundingBox initialBoundingBox;
                initialBoundingBox = CismapBroker.getInstance().getMappingComponent().getMappingModel()
                            .getInitialBoundingBox();
                final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_GEOM,
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, 20.0);
            }
        } catch (final Exception ex) {
            LOG.warn("Can't load Overview.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StringBuilder setUserStatus() {
        final StringBuilder errorMessage = new StringBuilder();

        if ((isEditor)) {
            if (cidsBean.getProperty(FIELD__STATUS__ID) != null) {
                final String statusSchluessel = cidsBean.getProperty(FIELD__STATUS__SCHLUESSEL).toString();
                final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                final Date datum = new Date();
                CidsBean schluesselBean;
                try {
                    switch (statusSchluesselAttribute) {
                        case STATUS_PRUEFEN_SCHLUESSEL: {                                                  // Prüfen
                            if (statusSchluessel.equals(STATUS_ZUR_BEARBEITUNG_SCHLUESSEL)
                                        || statusSchluessel.equals(STATUS_KEINE_BEARBEITUNG_SCHLUESSEL)) { // --> Zur Bearbeitung || Keine Bearbeitung erforderlich
                                if ("".equals(pruefAttribute)) {
                                    lblGeprueftDurch.setText(getCurrentUser());
                                    lblDatumGeprueft.setText(df.format(datum));
                                } else {
                                    lblGeprueftDurch.setText(pruefAttribute);
                                    lblDatumGeprueft.setText(pruefdatumAttribute);
                                }
                            } else {
                                if (!(statusSchluessel.equals(STATUS_PRUEFEN_SCHLUESSEL))) {
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(
                                            QsgebMarkerEditor.class,
                                            "QsgebMarkerEditor.setUserStatus().wrongStatusPruefen"));
                                    schluesselBean = getOtherTableValue(
                                            TABLE_NAME_STATUS,
                                            " where "
                                                    + FIELD__SCHLUESSEL
                                                    + " ilike '"
                                                    + STATUS_PRUEFEN_SCHLUESSEL
                                                    + "'",
                                            getConnectionContext());
                                    cbStatus.setSelectedIndex((int)schluesselBean.getProperty(FIELD__STATUS__ID));
                                } else {
                                    lblGeprueftDurch.setText(pruefAttribute);
                                    lblDatumGeprueft.setText(pruefdatumAttribute);
                                }
                            }
                            break;
                        }
                        case STATUS_ZUR_BEARBEITUNG_SCHLUESSEL: {                                          // ZurBearbeitung
                            if (statusSchluessel.equals(STATUS_IN_BEARBEITUNG_SCHLUESSEL)) {               // --> In Bearbeitung
                                if ("".equals(bearbeitungAttribute)) {
                                    lblBearbeitungDurch.setText(getCurrentUser());
                                } else {
                                    lblBearbeitungDurch.setText(bearbeitungAttribute);
                                }
                            } else {
                                if (!(statusSchluessel.equals(STATUS_ZUR_BEARBEITUNG_SCHLUESSEL))) {
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(
                                            QsgebMarkerEditor.class,
                                            "QsgebMarkerEditor.setUserStatus().wrongStatusZurBearbeitung"));
                                    schluesselBean = getOtherTableValue(
                                            TABLE_NAME_STATUS,
                                            " where "
                                                    + FIELD__SCHLUESSEL
                                                    + " ilike '"
                                                    + STATUS_ZUR_BEARBEITUNG_SCHLUESSEL
                                                    + "'",
                                            getConnectionContext());
                                    cbStatus.setSelectedIndex((int)schluesselBean.getProperty(FIELD__STATUS__ID));
                                } else {
                                    lblBearbeitungDurch.setText(bearbeitungAttribute);
                                }
                            }
                            break;
                        }
                        case STATUS_IN_BEARBEITUNG_SCHLUESSEL: {                                           // In Bearbeitung
                            if (statusSchluessel.equals(STATUS_ERLEDIGT_SCHLUESSEL)) {                     // --> Erledigt
                                if ("".equals(bearbeitungsdatumAttribute)) {
                                    lblDatumBearbeitung.setText(df.format(datum));
                                    cbErgebnis.setEnabled(true);
                                }
                            } else {
                                if (!(statusSchluessel.equals(STATUS_IN_BEARBEITUNG_SCHLUESSEL))) {
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(
                                            QsgebMarkerEditor.class,
                                            "QsgebMarkerEditor.setUserStatus().wrongStatusInBearbeitung"));
                                    schluesselBean = getOtherTableValue(
                                            TABLE_NAME_STATUS,
                                            " where "
                                                    + FIELD__SCHLUESSEL
                                                    + " ilike '"
                                                    + STATUS_IN_BEARBEITUNG_SCHLUESSEL
                                                    + "'",
                                            getConnectionContext());
                                    cbStatus.setSelectedIndex((int)schluesselBean.getProperty(FIELD__STATUS__ID));
                                } else {
                                    lblDatumBearbeitung.setText(bearbeitungsdatumAttribute);
                                    cbErgebnis.setEnabled(false);
                                    cidsBean.setProperty(FIELD__ERGEBNIS, null);
                                }
                            }
                            break;
                        }
                        default: {
                            break;                                                                         // hier keine Bearbeitung möglich
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Error setting user or/and date.", ex);
                }
            }
            if (errorMessage.length() > 0) {
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.setUserStatus().JOptionPane.message.prefix")
                            + errorMessage.toString()
                            + NbBundle.getMessage(
                                QsgebMarkerEditor.class,
                                "QsgebMarkerEditor.setUserStatus().JOptionPane.message.suffix"),
                    NbBundle.getMessage(QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.setUserStatus().JOptionPane.title"),
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        return errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentUser() {
        return SessionManager.getSession().getUser().getName();
    }

    /**
     * DOCUMENT ME!
     */
    private void setEditableStatus() {
        if ((isEditor)) {
            switch (statusSchluesselAttribute) {
                case STATUS_PRUEFEN_SCHLUESSEL: {           // Prüfen
                    cbStatus.setEnabled(true);
                    cbGeom.setEnabled(false);
                    cbErgebnis.setEnabled(false);
                    break;
                }
                case STATUS_ZUR_BEARBEITUNG_SCHLUESSEL: {   // Zur Bearbeitung
                    cbStatus.setEnabled(true);
                    cbGeom.setEnabled(false);
                    cbErgebnis.setEnabled(false);
                    break;
                }
                case STATUS_IN_BEARBEITUNG_SCHLUESSEL: {    // In Bearbeitung
                    cbStatus.setEnabled(true);
                    cbGeom.setEnabled(false);
                    cbErgebnis.setEnabled(false);
                    break;
                }
                case STATUS_ERLEDIGT_SCHLUESSEL: {          // Erledigt
                    cbStatus.setEnabled(false);
                    cbGeom.setEnabled(false);
                    cbErgebnis.setEnabled(false);
                    txtLage.setEnabled(false);
                    taBemerkung.setEnabled(false);
                    break;
                }
                case STATUS_KEINE_BEARBEITUNG_SCHLUESSEL: { // Keine Bearbeitung erforderlich
                    cbStatus.setEnabled(false);
                    cbGeom.setEnabled(false);
                    cbErgebnis.setEnabled(false);
                    break;
                }
                default: {
                    cbStatus.setEnabled(false);
                    cbGeom.setEnabled(true);
                    cbErgebnis.setEnabled(false);
                    break;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshStatus() {
        statusSearch.setStatusSchluessel(statusSchluesselAttribute);
        cbStatus.refreshModel();
    }

    @Override
    public void dispose() {
        super.dispose();
        rasterfariDocumentLoaderPanel1.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public String getTitle() {
        return cidsBean.toString();
    }

    @Override
    public void setTitle(final String string) {
    }


    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FIELD__STATUS) && (cidsBean.getMetaObject().getStatus() != MetaObject.NEW)) {
            setUserStatus();
        }
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ) && (cidsBean.getProperty(FIELD__GEOREFERENZ) != null)) {
            setMapWindow();
            final Geometry myPoint = (Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD);
            lookForLandparcel(myPoint);
        }
        if (evt.getPropertyName().equals(FIELD__HISTORISCH)) {
            if ((cidsBean.getProperty(FIELD__HISTORISCH) == null)
                        || Boolean.FALSE.equals(cidsBean.getProperty(FIELD__HISTORISCH))) {
                try {
                    cidsBean.setProperty(FIELD__DATUM_HISTORISCH, null);
                } catch (Exception ex) {
                    LOG.warn("Could not set histdate.", ex);
                }
            }
        }
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static QsGebProperties loadPropertiesFromServerResources(final ConnectionContext connectionContext)
            throws Exception {
        final Object ret = SessionManager.getSession()
                    .getConnection()
                    .executeTask(SessionManager.getSession().getUser(),
                        GetServerResourceServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        WundaBlauServerResources.QSGEB_PROPERTIES.getValue(),
                        connectionContext);
        if (ret instanceof Exception) {
            throw (Exception)ret;
        }
        final Properties properties = new Properties();
        properties.load(new StringReader((String)ret));

        return new QsGebProperties(properties);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    static class QsGebProperties {

        //~ Instance fields ----------------------------------------------------

        private final Properties properties;

        private final String rasterfariUrl;
        private final String rasterfariPath;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VermessungsunterlagenProperties object.
         *
         * @param  properties  DOCUMENT ME!
         */
        public QsGebProperties(final Properties properties) {
            this.properties = properties;

            rasterfariUrl = readProperty("PICTURE_SERVER", null);
            rasterfariPath = readProperty("PICTURE_PATH", null);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   property      DOCUMENT ME!
         * @param   defaultValue  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private String readProperty(final String property, final String defaultValue) {
            String value = defaultValue;
            try {
                value = getProperties().getProperty(property, defaultValue);
            } catch (final Exception ex) {
                final String message = "could not read " + property + " from "
                            + WundaBlauServerResources.QSGEB_PROPERTIES.getValue()
                            + ". setting to default value: " + defaultValue;
                LOG.warn(message, ex);
            }
            return value;
        }
    }
}
