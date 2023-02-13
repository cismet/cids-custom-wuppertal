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
import de.cismet.cids.custom.objecteditors.utils.RendererTools;

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


import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
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

import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.SaveVetoable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import lombok.Setter;
import org.jdesktop.swingx.JXTable;

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

    public static final String FIELD__BEARBEITUNG_STATUS_ALT = "fk_status_alt";
    public static final String FIELD__BEARBEITUNG_STATUS_NEU = "fk_status_neu";
    public static final String FIELD__BEARBEITUNG_BEARBEITUNG = "bearbeitung";
    public static final String FIELD__BEARBEITUNG_MARKER = "fk_marker";
    public static final String FIELD__BEARBEITUNG_DATUM = "datum";
    public static final String FIELD__STATUS = "status";
    public static final String FIELD__STATUS__SCHLUESSEL = "status.schluessel";
    public static final String STATUS_ERLEDIGT_SCHLUESSEL = "erledigt";    
    public static final String FIELD__BEARBEITUNG = "n_bearbeitung";
    public static final String FIELD__ID = "id";
    public static final String FIELD__LAGE = "lage";
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
    public static final String TABLE_NAME_BEARBEITUNG = "qsgeb_bearbeitung";
    public static final String FIELD__SCHLUESSEL = "schluessel";
    
    private static final String[] BEARBEITUNG_COL_NAMES = new String[] { 
            "Bearbeiter", 
            "Datum",
            "Status alt",
            "Status neu"
        };
    private static final String[] BEARBEITUNG_PROP_NAMES = new String[] {
            "bearbeitung",
            "datum",
            "fk_status_alt",
            "fk_status_neu"
        };
    private static final Class[] BEARBEITUNG_PROP_TYPES = new Class[] {
            String.class,
            Date.class,
            CidsBean.class,
            CidsBean.class
        };

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
    private CidsBean oldStatusBean;
    @Getter @Setter private List<CidsBean> bearbeitungBeans;
    @Getter @Setter private Integer year;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnImages;
    JButton btnInfo;
    private DefaultBindableReferenceCombo cbErgebnis;
    private JComboBox cbGeom;
    FastBindableReferenceCombo cbStatus;
    DefaultBindableDateChooser dcDatum;
    private JCheckBox jCkbHistorisch;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPaneBearbeitung;
    private JXBusyLabel jxLBusy;
    private JLabel lblBemerkung_txt;
    private JLabel lblDatum;
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
    private JXTable xtBearbeitung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public QsgebMarkerEditor() {
        this.bearbeitungBeans = new ArrayList<>();
    }

    /**
     * Creates a new QsgebMarkerEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public QsgebMarkerEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
        this.bearbeitungBeans = new ArrayList<>();
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

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initProperties();
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
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblGemarkung_txt = new JLabel();
        lblJahr = new JLabel();
        lblId_txt = new JLabel();
        lblId = new JLabel();
        lblGemarkung = new JLabel();
        lblFlur = new JLabel();
        lblFlurstueck = new JLabel();
        lblFlur_txt = new JLabel();
        lblFlurstueck_txt = new JLabel();
        lblStatus_txt = new JLabel();
        lblBemerkung_txt = new JLabel();
        cbErgebnis = new DefaultBindableReferenceCombo(true) ;
        lblLage_txt = new JLabel();
        lblErgebnis_txt = new JLabel();
        jScrollPane2 = new JScrollPane();
        taBemerkung = new JTextArea();
        cbStatus = new FastBindableReferenceCombo();
        lblHistorisch_txt = new JLabel();
        jCkbHistorisch = new JCheckBox();
        lblDatumHistorisch_txt = new JLabel();
        lblDatumHistorisch = new JLabel();
        jScrollPaneBearbeitung = new JScrollPane();
        xtBearbeitung = new JXTable();
        dcDatum = new DefaultBindableDateChooser();
        lblDatum = new JLabel();
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
            getConnectionContext()
        );
        jPanel2 = new JPanel();
        jxLBusy = new JXBusyLabel(new Dimension(64,64));
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
            public void actionPerformed(ActionEvent evt) {
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
            public void actionPerformed(ActionEvent evt) {
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

        panDaten.setMinimumSize(new Dimension(583, 273));
        panDaten.setOpaque(false);
        panDaten.setPreferredSize(new Dimension(340, 296));
        panDaten.setRequestFocusEnabled(false);
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

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.lage}"), txtLage, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 2);
        panDaten.add(txtLage, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.georeferenz}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.id}"), lblId, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.gemarkung}"), lblGemarkung, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.flur}"), lblFlur, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.flurstueck}"), lblFlurstueck, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 6, 4, 4);
        panDaten.add(lblFlurstueck, gridBagConstraints);

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

        cbErgebnis.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbErgebnis.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ergebnis}"), cbErgebnis, BeanProperty.create("selectedItem"));
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
        gridBagConstraints.gridx = 4;
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.status}"), cbStatus, BeanProperty.create("selectedItem"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.historisch}"), jCkbHistorisch, BeanProperty.create("selected"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum_historisch}"), lblDatumHistorisch, BeanProperty.create("text"));
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

        jScrollPaneBearbeitung.setOpaque(false);

        xtBearbeitung.setToolTipText("");
        xtBearbeitung.setOpaque(false);
        jScrollPaneBearbeitung.setViewportView(xtBearbeitung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(jScrollPaneBearbeitung, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.wiedervorlage}"), dcDatum, BeanProperty.create("date"));
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcDatum, gridBagConstraints);

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatum.setText("Vorlage 102.21:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(4, 5, 4, 5);
        panDaten.add(lblDatum, gridBagConstraints);

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
        lblHeaderPages.setText(NbBundle.getMessage(QsgebMarkerEditor.class, "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
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
    }// </editor-fold>//GEN-END:initComponents

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
            if (getCidsBean().getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.isOkForSaving().noGeom"));
            } else {
                final CidsBean geom_pos = (CidsBean)getCidsBean().getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(
                            QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.isOkForSaving().wrongGeom"));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        // Status
        try {
            if (getCidsBean().getProperty(FIELD__STATUS) == null) {
                LOG.warn("No status specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.isOkForSaving().noStatus"));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Status not given.", ex);
            save = false;
        }
        try {
            if ((getCidsBean().getProperty(FIELD__ERGEBNIS) == null)
                        && STATUS_ERLEDIGT_SCHLUESSEL.equals(
                                getCidsBean().getProperty(FIELD__STATUS__SCHLUESSEL))) {
                LOG.warn("Wrong Ergebnis specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.isOkForSaving().noErgebnis"));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Ergebnis not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.isOkForSaving().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.isOkForSaving().JOptionPane.message.suffix"),
                NbBundle.getMessage(QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.isOkForSaving().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        } else{
            prepareMarkerBearbeitung();
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
            if (isEditor && (getCidsBean() != null)) {
                LOG.info("remove propchange qsgeb_marker: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (getCidsBean() != null)) {
                LOG.info("add propchange qsgeb_marker: " + getCidsBean());
                getCidsBean().addPropertyChangeListener(this);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            if (getCidsBean()!= null){
                setBearbeitungBeans(getCidsBean().getBeanCollectionProperty(FIELD__BEARBEITUNG));
            } else {
                setBearbeitungBeans(null);
            }
            prepareIt();
            final DivBeanTable bearbeitungModel = new DivBeanTable(
                        isEditor,
                        getCidsBean(),
                        FIELD__BEARBEITUNG,
                        BEARBEITUNG_COL_NAMES,
                        BEARBEITUNG_PROP_NAMES,
                        BEARBEITUNG_PROP_TYPES);
            xtBearbeitung.setModel(bearbeitungModel);
            JScrollBar vertical = jScrollPaneBearbeitung.getVerticalScrollBar();
            vertical.setValue( vertical.getMaximum() );
            jScrollPaneBearbeitung.getVerticalScrollBar().setValue(
                    jScrollPaneBearbeitung.getVerticalScrollBar().getMaximum());
            xtBearbeitung.packAll();
            RendererTools.makeReadOnly(xtBearbeitung);
            if ((getCidsBean() != null) &&  (getCidsBean().getProperty(FIELD__STATUS) != null)){
                oldStatusBean = (CidsBean)getCidsBean().getProperty(FIELD__STATUS);
            }
            if (getCidsBean()!=null && getCidsBean().getPrimaryKeyValue() != -1){
                RendererTools.makeReadOnly(cbGeom);
            }
            if (getCidsBean()!=null && Boolean.TRUE.equals(getCidsBean().getProperty(FIELD__HISTORISCH))){
                setNoEditHistorisch();
            }
        } catch (final Exception ex) {
            LOG.warn("Error setCidsBean.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareIt() {
        if (getCidsBean() != null) {
            lookForYear();           // Ist nur ein Label, welches sich aus dem Anlegedatum ergibt.
            loadDocument();
        }

        setHistorischOnly(); // Historisch ist nur dann interessant, wenn der Marker historisiert wurde, um ihn
                             // wieder zu enthistorisieren.
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareMarkerBearbeitung() {
        try {
            CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName(
                    TABLE_NAME_BEARBEITUNG, getConnectionContext());
            try {
                bean.setProperty(
                FIELD__BEARBEITUNG_STATUS_ALT,
                oldStatusBean);
            } catch (Exception ex) {
                LOG.warn("old Status not set.", ex);
            }
            try {
                bean.setProperty(
                FIELD__BEARBEITUNG_STATUS_NEU,
                (CidsBean)getCidsBean().getProperty(FIELD__STATUS));
            } catch (Exception ex) {
                LOG.warn("new Status not set.", ex);
            }
            try {
                bean.setProperty(
                FIELD__BEARBEITUNG_MARKER,
                getCidsBean().getPrimaryKeyValue());
            } catch (Exception ex) {
                LOG.warn("marker not set.", ex);
            }
            try {
                bean.setProperty(
                FIELD__BEARBEITUNG_BEARBEITUNG,
                getCurrentUser());
            } catch (Exception ex) {
                LOG.warn("User not set.", ex);
            }
            try {
                bean.setProperty(
                FIELD__BEARBEITUNG_DATUM,
                new java.sql.Date(System.currentTimeMillis()));
            } catch (Exception ex) {
                LOG.warn("old Status not set.", ex);
            }
            ((DivBeanTable)xtBearbeitung.getModel()).addBean(bean);
        } catch (Exception e) {
            LOG.error("Cannot add new " + TABLE_NAME_BEARBEITUNG + " object", e);
        }
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
        if (getYear() == null){
            lookForYear();
        }
        final String id = cidsBean.getProperty(FIELD__ID).toString();
        return PROPERTIES.getRasterfariPath() + "/" + getYear() + "/qsgeb_" + getYear() + "_" + id + ".tif";
    }

    /**
     * DOCUMENT ME!
     */
    private void lookForYear() {
        if (getCidsBean()!= null) {
            final LocalDate datum = LocalDate.now();
            setYear(datum.getYear());
            if(!bearbeitungBeans.isEmpty()){
                for (final CidsBean bearbeitungBean : bearbeitungBeans) {
                    Calendar calendar =  Calendar.getInstance();
                    calendar.setTime((Date)bearbeitungBean.getProperty(FIELD__BEARBEITUNG_DATUM));
                    int beanYear = calendar.get(Calendar.YEAR);
                    if (beanYear < getYear()){
                        year = beanYear;
                    }
                }
            }
            lblJahr.setText(String.valueOf(getYear()));
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
        RendererTools.makeReadOnly(dcDatum);
    }
    
    private void setNoEditHistorisch(){
        RendererTools.makeReadOnly(dcDatum);
        RendererTools.makeReadOnly(cbStatus);
        RendererTools.makeReadOnly(cbErgebnis);
        RendererTools.makeReadOnly(txtLage);
        RendererTools.makeReadOnly(taBemerkung);
        RendererTools.makeReadOnly(lblGeom_txt);
    }

    /**
     * DOCUMENT ME!
     */
    private void setHistorischOnly() {
        if ((getCidsBean().getProperty(FIELD__HISTORISCH) == null)
                    || Boolean.FALSE.equals(getCidsBean().getProperty(FIELD__HISTORISCH))) {
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
        final CidsBean cb = getCidsBean();
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
    private String getCurrentUser() {
        return SessionManager.getSession().getUser().getName();
    }

   
    @Override
    public void dispose() {
        super.dispose();
        rasterfariDocumentLoaderPanel1.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        xtBearbeitung.removeAll();
    }

    @Override
    public String getTitle() {
        return getCidsBean().toString();
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
        if (evt.getPropertyName().equals(FIELD__STATUS) && (getCidsBean().getMetaObject().getStatus() != MetaObject.NEW)) {
            //setUserStatus();
        }
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ) && (getCidsBean().getProperty(FIELD__GEOREFERENZ) != null)) {
            setMapWindow();
            final Geometry myPoint = (Geometry)getCidsBean().getProperty(FIELD__GEOREFERENZ__GEO_FIELD);
            lookForLandparcel(myPoint);
        }
        if (evt.getPropertyName().equals(FIELD__HISTORISCH)) {
            if ((getCidsBean().getProperty(FIELD__HISTORISCH) == null)
                        || Boolean.FALSE.equals(getCidsBean().getProperty(FIELD__HISTORISCH))) {
                try {
                    getCidsBean().setProperty(FIELD__DATUM_HISTORISCH, null);
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
