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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



import java.util.Collection;


import javax.swing.*;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getMyWhere;
import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import static de.cismet.cids.custom.objecteditors.wunda_blau.WebDavPicturePanel.adjustScale;
import de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
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

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.tools.gui.FooterComponentProvider;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $1.0$, $31.05.2018$
 * Die TIFF Anzeige ist nicht gut und muss noch verbessert werden.
 */
public  class QsgebMarkerEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    BindingGroupStore,
    PropertyChangeListener,
    RequestsFullSizeComponent{

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(QsgebMarkerEditor.class);

    public static final String FIELD__STATUS = "status"; 
    public static final String FIELD__STATUS_ID = "status.id";
    public static final String FIELD__ID = "id"; 
    public static final String FIELD__LAGE = "lage";                    
    public static final String FIELD__ANGELEGT_DURCH = "angelegt_durch";                         
    public static final String FIELD__DATUM_ANGELEGT = "datum_angelegt";                    
    public static final String FIELD__GEPRUEFT_DURCH = "geprueft_durch";                         
    public static final String FIELD__DATUM_PRUEFUNG = "datum_pruefung";                        
    public static final String FIELD__DATUM_BEARBEITUNG = "datum_bearbeitung";                    
    public static final String FIELD__BEARBEITUNG_DURCH = "angelegt_bearbeitung";                         
    public static final String FIELD__BEARBEITUNG_ANGELEGT = "datum_bearbeitung"; 
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
  

    public static final Coordinate RATHAUS_POINT = new Coordinate(374420, 5681660);

    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;
    private int statusAttribute;
    private String pruefAttribute;
    private String bearbeitungAttribute;
    private String pruefdatumAttribute;
    private String bearbeitungsdatumAttribute;
    private QsgebStatusLightweightSearch statusSearch;
    private String pictureUrl;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnImages;
    private JButton btnInfo;
    private DefaultBindableReferenceCombo cbErgebnis;
    private JComboBox cbGeom;
    private FastBindableReferenceCombo cbStatus;
    private JCheckBox jCkbHistorisch;
    private JScrollPane jScrollPane2;
    private JScrollPane jspanBild;
    private JLabel lblAngelegtDurch;
    private JLabel lblAngelegt_txt;
    private JLabel lblBearbeitungDurch;
    private JLabel lblBearbeitung_txt;
    private JLabel lblBemerkung_txt;
    private JLabel lblBild;
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
    private JLabel lblImages;
    private JLabel lblInfo;
    private JLabel lblJahr;
    private JLabel lblJahr_txt;
    private JLabel lblKarte;
    private JLabel lblLage_txt;
    private JLabel lblReducedSize;
    private JLabel lblStatus_txt;
    private JList lstPages;
    private JPanel panBild;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFillerRechtsLage;
    private JPanel panFooter;
    private JPanel panLage;
    private JPanel panLeft;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panRight;
    private JPanel pnlBild;
    private RoundedPanel pnlDocument;
    private SemiRoundedPanel pnlHeaderDocument;
    private SemiRoundedPanel pnlHeaderPages;
    private RoundedPanel pnlPages;
    private RoundedPanel rpKarte;
    private JScrollPane scpPages;
    private SemiRoundedPanel semiRoundedPanel7;
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
    private void initStatus(){
        this.statusSearch = new QsgebStatusLightweightSearch(
                QsgebStatusLightweightSearch.SearchFor.STATUS,
                "%1$2s",
                new String[] { "NAME" });
        statusSearch.setStatusId(1);
    }
    
    
    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initStatus();
        initComponents();
        
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        }
        cbStatus.setMetaClassFromTableName("WUNDA_BLAU", TABLE_NAME_STATUS);
        setReadOnly();
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
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panFillerRechtsLage = new JPanel();
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
        cbErgebnis = new DefaultBindableReferenceCombo(true) ;
        lblLage_txt = new JLabel();
        lblErgebnis_txt = new JLabel();
        jScrollPane2 = new JScrollPane();
        taBemerkung = new JTextArea();
        cbStatus = new FastBindableReferenceCombo(
            statusSearch,
            statusSearch.getRepresentationPattern(),
            statusSearch.getRepresentationFields()
        );
        lblHistorisch_txt = new JLabel();
        jCkbHistorisch = new JCheckBox();
        lblDatumHistorisch_txt = new JLabel();
        lblDatumHistorisch = new JLabel();
        panBild = new JPanel();
        pnlDocument = new RoundedPanel();
        pnlHeaderDocument = new SemiRoundedPanel();
        lblReducedSize = new JLabel();
        jspanBild = new JScrollPane();
        pnlBild = new JPanel();
        lblBild = new JLabel();
        pnlPages = new RoundedPanel();
        pnlHeaderPages = new SemiRoundedPanel();
        lblHeaderPages = new JLabel();
        scpPages = new JScrollPane();
        lstPages = new JList();

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
        setPreferredSize(new Dimension(910, 737));
        setLayout(new CardLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(610, 600));
        panContent.setLayout(new GridBagLayout());

        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        panPreviewMap.setPreferredSize(new Dimension(500, 250));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 10, 5);
        rpKarte.add(panPreviewMap, gridBagConstraints);

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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        GroupLayout panFillerRechtsLageLayout = new GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(panFillerRechtsLageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerRechtsLageLayout.setVerticalGroup(panFillerRechtsLageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panLage.add(panFillerRechtsLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panContent.add(panLage, gridBagConstraints);

        panDaten.setName(""); // NOI18N
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

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.lage}"), txtLage, BeanProperty.create("text"));
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

        lblAngelegtDurch.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.angelegt_durch}"), lblAngelegtDurch, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum_angelegt}"), lblDatumAngelegt, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum_pruefung}"), lblDatumGeprueft, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum_bearbeitung}"), lblDatumBearbeitung, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.geprueft_durch}"), lblGeprueftDurch, BeanProperty.create("text"));
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bearbeitung_durch}"), lblBearbeitungDurch, BeanProperty.create("text"));
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
        jCkbHistorisch.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.historisch}"), jCkbHistorisch, BeanProperty.create("selected"));
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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panDaten, gridBagConstraints);

        add(panContent, "cardContent");

        panBild.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panBild.setMinimumSize(new Dimension(380, 30));
        panBild.setOpaque(false);
        panBild.setPreferredSize(new Dimension(380, 600));
        panBild.setLayout(new GridBagLayout());

        pnlDocument.setMinimumSize(new Dimension(163, 25));
        pnlDocument.setPreferredSize(new Dimension(380, 25));
        pnlDocument.setLayout(new GridBagLayout());

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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlDocument.add(pnlHeaderDocument, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBild.add(pnlDocument, gridBagConstraints);

        jspanBild.setBorder(null);
        jspanBild.setName("jspanBild"); // NOI18N
        jspanBild.setOpaque(false);

        pnlBild.setName("pnlBild"); // NOI18N
        pnlBild.setOpaque(false);
        pnlBild.setLayout(new GridBagLayout());

        lblBild.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlBild.add(lblBild, gridBagConstraints);

        jspanBild.setViewportView(pnlBild);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBild.add(jspanBild, gridBagConstraints);

        pnlHeaderPages.setBackground(new Color(51, 51, 51));
        pnlHeaderPages.setLayout(new FlowLayout());

        lblHeaderPages.setForeground(new Color(255, 255, 255));
        lblHeaderPages.setText(NbBundle.getMessage(QsgebMarkerEditor.class, "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, BorderLayout.PAGE_START);

        scpPages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new Dimension(85, 75));

        lstPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        lstPages.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                lstPagesValueChanged(evt);
            }
        });
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        panBild.add(pnlPages, gridBagConstraints);

        add(panBild, "cardBild");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void lstPagesValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstPagesValueChanged
        if (!evt.getValueIsAdjusting()) {
            final Object page = lstPages.getSelectedIndex();

            if (page instanceof Integer) {
               setDocument(((Integer)page));
            }
        }
    }//GEN-LAST:event_lstPagesValueChanged

    private void btnInfoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        ((CardLayout)getLayout()).show(this, "cardContent");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }//GEN-LAST:event_btnInfoActionPerformed

    private void btnImagesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnImagesActionPerformed
        ((CardLayout)getLayout()).show(this, "cardBild");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }//GEN-LAST:event_btnImagesActionPerformed

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                        "QsgebMarkerEditor.prepareForSave().noGeom"));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.prepareForSave().wrongGeom"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
    
        //Status
        errorMessage.append(setUserStatus());
        try{
            if (cidsBean.getProperty(FIELD__ERGEBNIS) == null && (int)cidsBean.getProperty(FIELD__STATUS_ID) == 3) {
                LOG.warn("Wrong Ergebnis specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                                "QsgebMarkerEditor.prepareForSave().noErgebnis"));
            }
        } catch (final Exception ex) {
            LOG.warn("Ergebnis not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(QsgebMarkerEditor.class,
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
            saveFirstAttributes();
            if (cb.getMetaObject().getStatus() == MetaObject.NEW) {
                CidsBean statusBean = getOtherTableValue(TABLE_NAME_STATUS, 
                                getMyWhere("Prüfen"),
                                getConnectionContext());
                cidsBean.setProperty(
                        FIELD__STATUS, 
                        statusBean
                );
                cbStatus.setEnabled(false);
                lblAngelegtDurch.setText(getCurrentUser());
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                Date datum = new Date();
                lblDatumAngelegt.setText(df.format(datum));
            }
            if (cidsBean != null) {
                setYear();
                if ((isEditor)) {
                    refreshStatus();
                }
                createPictureUrl();
                setDocument((int)0);
            }
            
            if ((isEditor)) {
                setEditableStatus();
            }   
            
            setHistorischOnly();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    
    private void setDocument(int pagenr){
        try{
            //Diese Methode muss angepasst werden. Das geht besser.
            URLConnection con = new URL(pictureUrl).openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.setRequestProperty("Authorization", "Basic "+new sun.misc.BASE64Encoder().encode ("blibla:Tralala".getBytes()));
            InputStream in = con.getInputStream();
            ImageInputStream is = ImageIO.createImageInputStream(in);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(is);
            // null abfangen
            if (iterator == null || !(iterator.hasNext())){
                throw new IOException("Image file format not supported by ImageIO: " + pictureUrl);
            }
            ImageReader reader = (ImageReader) iterator.next();
            iterator = null;
            reader.setInput(is);
            int pictures = reader.getNumImages(true);
            BufferedImage pageImage = reader.read(pagenr);
            final ImageIcon pageIcon = new ImageIcon(adjustScale(pageImage, lblBild, 20, 20));
            lblBild.setIcon(pageIcon);
            
            final DefaultListModel modelPictures = new DefaultListModel();
            for (int i = 0; i < pictures; i++) {
                modelPictures.add(i, "Seite " + (i+1));
            }
           
            lstPages.setModel(modelPictures);
            reader.dispose();
        }catch (final FileNotFoundException e) {
            LOG.warn("Document not produced.", e);
            lblBild.setText("Für diesen Marker ist kein Dokument vorhanden.");
            btnImages.setEnabled(false);
            btnInfo.setEnabled(false);
            lblImages.setEnabled(false);
            lblImages.setText("Kein Dokument vorhanden");
            lblInfo.setEnabled(false);
        }catch (final Exception ex) {
            LOG.warn("Document not available.", ex);
            lblBild.setText("Das Dokument für diesen Marker kann nicht geladen werden.");
        }
    }
    
    private void createPictureUrl(){
        final String serverUrl = "http://sw0040/qsgeb/";
        final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
        final String jahr = String.valueOf(sdf.format(datum));
        final String id = cidsBean.getProperty(FIELD__ID).toString();
        pictureUrl = serverUrl + jahr + "/qsgeb_" + jahr + "_" + id + ".tif"; 
    }
    
    private void setYear(){
        if (cidsBean.getProperty(FIELD__DATUM_ANGELEGT) != null){
            final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
            lblJahr.setText(String.valueOf(sdf.format(datum)));
        }
    }
    
    private void lookForLandparcel(Geometry myPoint){
       final MetaClass mc = ClassCacheMultiple.getMetaClass(
                            "WUNDA_BLAU",
                            TABLE_NAME_FLUR,
                            getConnectionContext());
        if (mc != null) {
            // Suche Konfigurieren 
            final BufferingGeosearch search = new BufferingGeosearch();
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(myPoint);
            try{
                // Suche ausführen
                final Collection<MetaObjectNode> mons = SessionManager.getProxy().customServerSearch(
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
                    if (flurstueckBean != null){
                        lblGemarkung.setText(flurstueckBean.getProperty(FIELD__GEMARKUNG).toString());
                        lblFlur.setText(flurstueckBean.getProperty(FIELD__FLUR).toString());
                        String zaehler = flurstueckBean.getProperty(FIELD__ZAEHLER).toString();
                        if (flurstueckBean.getProperty(FIELD__NENNER) != null){
                            lblFlurstueck.setText(zaehler + "/" + flurstueckBean.getProperty(FIELD__NENNER).toString());
                        }else{
                            lblFlurstueck.setText(zaehler);
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Geom Search Error.", ex);
            }

        } else {
            LOG.error("Could not find MetaClass for alkis_landparcel " );
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void saveFirstAttributes() {
        if (cidsBean == null){
            statusAttribute = -1;
            pruefAttribute = null;
            bearbeitungAttribute = null;
            pruefdatumAttribute = null;
            bearbeitungsdatumAttribute = null;
        } else{
            if (cidsBean.getProperty(FIELD__STATUS_ID)==null){
                statusAttribute = -1;
            } else{
                statusAttribute = (int) cidsBean.getProperty(FIELD__STATUS_ID);
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
        if (!(isEditor)) {
            cbStatus.setEnabled(false);
            cbErgebnis.setEnabled(false);
            txtLage.setEnabled(false);
            taBemerkung.setEnabled(false);
            lblGeom_txt.setVisible(false);
            jCkbHistorisch.setEnabled(false);
        }
    }
    
    private void setHistorischOnly() {
        if ((cidsBean.getProperty(FIELD__HISTORISCH) == null) || Boolean.FALSE.equals(cidsBean.getProperty(FIELD__HISTORISCH))) {
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
//panPreviewMap.initMap(cidsBean, "FIELD__GEOREFERENZ__GEO_FIELD", 50.0, "http://s10221.wuppertal-intra.de:7098/alkis/services?&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=FALSE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=alf&STYLES=&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:25832");
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
            Exceptions.printStackTrace(ex);
        }
    }
    
    public StringBuilder setUserStatus(){
        final StringBuilder errorMessage = new StringBuilder();
        
        if ((isEditor)) {
            if (cidsBean.getProperty(FIELD__STATUS_ID) != null){
                int status = (int)cidsBean.getProperty(FIELD__STATUS_ID);
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                Date datum = new Date();
                try{
                    switch (statusAttribute){
                        case 0://Prüfen
                            if (status == 1 || status == 4){ //--> Zur Bearbeitung || Keine Bearbeitung erforderlich
                                if ("".equals(pruefAttribute)){
                                    lblGeprueftDurch.setText(getCurrentUser());
                                    lblDatumGeprueft.setText(df.format(datum));
                                } else{
                                    lblGeprueftDurch.setText(pruefAttribute);
                                    lblDatumGeprueft.setText(pruefdatumAttribute);
                                }
                            } else{
                                if (status != 0){
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                                    "QsgebMarkerEditor.setUserStatus().wrongStatusPruefen"));
                                    cbStatus.setSelectedIndex(0);
                                } else{
                                    lblGeprueftDurch.setText(pruefAttribute);
                                    lblDatumGeprueft.setText(pruefdatumAttribute);
                                }
                            }
                            break;
                        case 1://ZurBearbeitung
                            if (status == 2){//--> In Bearbeitung
                                if ("".equals(bearbeitungAttribute)){
                                    lblBearbeitungDurch.setText(getCurrentUser());
                                } else{
                                    lblBearbeitungDurch.setText(null);
                                }
                            } else{
                                if (status != 1){
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                                    "QsgebMarkerEditor.setUserStatus().wrongStatusZurBearbeitung"));
                                    cbStatus.setSelectedIndex(1);
                                } else {
                                    lblBearbeitungDurch.setText(bearbeitungAttribute);
                                }
                            }
                            break;
                        case 2://In Bearbeitung
                            if (status == 3){//--> Erledigt
                                if ("".equals(bearbeitungsdatumAttribute)){
                                    lblDatumBearbeitung.setText(df.format(datum));
                                    cbErgebnis.setEnabled(true);
                                }
                            } else{
                                if (status != 2){
                                    LOG.warn("Wrong status specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(QsgebMarkerEditor.class,
                                    "QsgebMarkerEditor.setUserStatus().wrongStatusInBearbeitung"));
                                    cbStatus.setSelectedIndex(2);
                                } else{
                                    lblDatumBearbeitung.setText(bearbeitungsdatumAttribute);
                                    cbErgebnis.setEnabled(false);
                                    cidsBean.setProperty(FIELD__ERGEBNIS, null);
                                }
                            }
                            break;
                        default://hier keine Bearbeitung möglich
                    }
                } catch (final Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            } 
            if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.setUserStatus().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(QsgebMarkerEditor.class,
                            "QsgebMarkerEditor.setUserStatus().JOptionPane.message.suffix"),
                NbBundle.getMessage(QsgebMarkerEditor.class,
                    "QsgebMarkerEditor.setUserStatus().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);
            }
        }
        return errorMessage;
    }
    
    private String getCurrentUser() {
        return SessionManager.getSession().getUser().getName();
    }
    
    private void setEditableStatus() {
        if ((isEditor)) {
            switch (statusAttribute){
                        case 0://Prüfen
                            cbStatus.setEnabled(true);
                            cbGeom.setEnabled(false);
                            cbErgebnis.setEnabled(false);
                            break;
                        case 1: //Zur Bearbeitung
                            cbStatus.setEnabled(true);
                            cbGeom.setEnabled(false);
                            cbErgebnis.setEnabled(false);
                            break;
                        case 2: //In Bearbeitung
                            cbStatus.setEnabled(true);
                            cbGeom.setEnabled(false);
                            cbErgebnis.setEnabled(false);
                            break;
                        case 3: //Erledigt
                            cbStatus.setEnabled(false);
                            cbGeom.setEnabled(false);
                            cbErgebnis.setEnabled(false);
                            txtLage.setEnabled(false);
                            taBemerkung.setEnabled(false);
                            break;
                        case 4: //Keine Bearbeitung erforderlich
                            cbStatus.setEnabled(false);
                            cbGeom.setEnabled(false);
                            cbErgebnis.setEnabled(false);
                            break;
                        default:
                            cbStatus.setEnabled(false);
                            cbGeom.setEnabled(true);
                            cbErgebnis.setEnabled(false);
            }
        }
    }
    
    private void refreshStatus() {
        statusSearch.setStatusId(statusAttribute);

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    cbStatus.refreshModel();
                    return null;
                }
            }.execute();
    }
    
    @Override
    public void dispose() {
        super.dispose();
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
    public void editorClosed(final EditorClosedEvent ece) {
        //panBild.editorClosed(ece);
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
            if (cidsBean.getProperty(FIELD__HISTORISCH) == null ||  Boolean.FALSE.equals(cidsBean.getProperty(FIELD__HISTORISCH))) { 
                try {
                    cidsBean.setProperty(FIELD__DATUM_HISTORISCH, null);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                     LOG.warn("Could not set histdate.", ex);
                }
            }
        }
        
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}

