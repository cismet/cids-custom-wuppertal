/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.tools.CacheException;
import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils.RestrictionLevel;
import de.cismet.cids.custom.clientutils.StadtbilderUtils;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.Sb_StadtbildserieProvider;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.wunda_blau.search.actions.Sb_stadtbildserieUpdatePruefhinweisAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BeanInitializer;
import de.cismet.cids.editors.BeanInitializerForcePaste;
import de.cismet.cids.editors.BeanInitializerProvider;
import de.cismet.cids.editors.DefaultBeanInitializer;
import de.cismet.cids.editors.DefaultBindableJCheckBox;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cids.utils.CidsBeanDeepPropertyListener;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieEditor extends JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider,
    BeanInitializerProvider,
    Sb_StadtbildserieProvider,
    EditorSaveListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ImageIcon TICK = new javax.swing.ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/wunda_blau/res/tick_32.png"));
    private static final ImageIcon TICK_BW = new javax.swing.ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"));

    private static final String REPORT_STADTBILDSERIE_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/StadtbildserieA4Q.jasper";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Sb_stadtbildserieEditor.class);

    private static final String GEOM_AUS_ADRESSE_QUERY =
        "select (select id from cs_class where table_name ilike 'sb_geom_aus') as class_id,0 as id";
    private static final String GEOM_AUS_STRASSE_QUERY =
        "select (select id from cs_class where table_name ilike 'sb_geom_aus') as class_id,1 as id";
    private static final String GEOM_AUS_DIGI_QUERY =
        "select (select id from cs_class where table_name ilike 'sb_geom_aus') as class_id,2 as id";

    private static final String GET_GEOM_FROM_ADRESSE =
        "SELECT (select id from cs_class where table_name ilike 'geom') as class_id, geom.id as id \n"
                + "FROM adresse,\n"
                + "     geom\n"
                + "WHERE \n"
                + "  adresse.strasse=%d\n"
                + "  AND adresse.hausnummer = '%s'\n"
                + "  AND adresse.umschreibendes_rechteck = geom.id\n";

    //~ Instance fields --------------------------------------------------------

    final StyledFeature previewGeometry = new DefaultStyledFeature();
    DigitizedSetterPropertyChangeListener digitizedSetter = new DigitizedSetterPropertyChangeListener();

    /** DOCUMENT ME! */
    Geometry lastRefreshedGeometry = null;
    XBoundingBox lastRefreshedBoundingBox = null;
    private CidsBean cidsBean;
    private RestrictionLevel restrictedLevel = new RestrictionLevel();
    private String title;
    private final Converter<Timestamp, Date> timeStampConverter = new Converter<Timestamp, Date>() {

            @Override
            public Date convertForward(final Timestamp value) {
                try {
                    if (value != null) {
                        return new java.util.Date(value.getTime());
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    LOG.error("Problem during Timestamp vonversion. Will return now().", ex);
                    return new java.util.Date(System.currentTimeMillis());
                }
            }

            @Override
            public Timestamp convertReverse(final Date value) {
                try {
                    if (value != null) {
                        return new Timestamp(value.getTime());
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    LOG.error("Problem during Timestamp vonversion. Will return now().", ex);
                    return new Timestamp(System.currentTimeMillis());
                }
            }
        };

    private final Converter<Timestamp, String> timeStampToStringConverter = new Converter<Timestamp, String>() {

            @Override
            public String convertForward(final Timestamp s) {
                final Date d = new java.util.Date(s.getTime());
                final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                final String formattedDate = df.format(d);
                return formattedDate;
            }

            @Override
            public Timestamp convertReverse(final String t) {
                LOG.error(".convertReverse: Not supported yet.", new Exception()); // NOI18N
                return null;
            }
        };

    private CidsBean geomFromDigitizedAction;
    private CidsBean geomFromAdresse;
    private CidsBean geomFromStrasse;

    private MappingComponent previewMap;
    private boolean editable;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.FastBindableReferenceCombo bcbStrasse;
    private javax.swing.JButton btnAddImageNumber;
    private javax.swing.JButton btnAddSuchwort;
    private javax.swing.JButton btnCombineGeometries;
    private javax.swing.JButton btnRemoveImageNumber;
    private javax.swing.JButton btnRemoveSuchwort;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSavePruefhinweis;
    private javax.swing.JCheckBox chbPruefen;
    private javax.swing.JComboBox dbcAuftraggeber;
    private javax.swing.JComboBox dbcBildtyp;
    private javax.swing.JComboBox dbcBlickrichtung;
    private javax.swing.JComboBox dbcFilmart;
    private javax.swing.JComboBox dbcFotograf;
    private de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor dbcGeom;
    private javax.swing.JComboBox dbcLager;
    private javax.swing.JComboBox dbcNutzungseinschraenkung;
    private javax.swing.JComboBox dbcOrt;
    private org.jdesktop.swingx.JXDatePicker dpAufnahmedatum;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private org.jdesktop.swingx.JXImagePanel imgpBulletPoint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private org.jdesktop.swingx.JXBusyLabel lblBusyPruef;
    private javax.swing.JLabel lblDescAufnahmedatum;
    private javax.swing.JLabel lblDescAuftraggeber;
    private javax.swing.JLabel lblDescBildnummer;
    private javax.swing.JLabel lblDescBildtyp;
    private javax.swing.JLabel lblDescBildtyp1;
    private javax.swing.JLabel lblDescFilmart;
    private javax.swing.JLabel lblDescFotograf;
    private javax.swing.JLabel lblDescGeometrie;
    private javax.swing.JLabel lblDescInfo;
    private javax.swing.JLabel lblDescLagerort;
    private javax.swing.JLabel lblDescOrt;
    private javax.swing.JLabel lblDescStrasse;
    private javax.swing.JLabel lblDescSuchworte;
    private javax.swing.JLabel lblEintragungsdatum;
    private javax.swing.JLabel lblGeomAus;
    private javax.swing.JLabel lblGeomAusAdresse;
    private javax.swing.JLabel lblGeomAusStrasse;
    private javax.swing.JLabel lblGeomDigitized;
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblPruefhinweisVon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstBildnummern;
    private javax.swing.JList lstSuchworte;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panDetails;
    private javax.swing.JPanel panDetails1;
    private javax.swing.JPanel panDetails3;
    private javax.swing.JPanel panDetails4;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panPrintButton;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlCtrlButtons;
    private javax.swing.JPanel pnlCtrlButtons1;
    private javax.swing.JPanel pnlMap;
    private de.cismet.cids.custom.objecteditors.utils.Sb_StadtbildPreviewImage previewImage;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    private de.cismet.tools.gui.RoundedPanel roundedPanel2;
    private de.cismet.tools.gui.RoundedPanel roundedPanel3;
    private de.cismet.tools.gui.RoundedPanel roundedPanel4;
    private de.cismet.tools.gui.RoundedPanel roundedPanel6;
    private de.cismet.tools.gui.RoundedPanel roundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel8;
    private de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter sqlDateToStringConverter;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private de.cismet.cids.editors.DefaultBindableJTextField txtHausnummer;
    private javax.swing.JTextArea txtaComment;
    private javax.swing.JTextArea txtaPruefhinweis;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieEditor object.
     */
    public Sb_stadtbildserieEditor() {
        this(true);
    }

    /**
     * Creates new form Arc_stadtbildRenderer.
     *
     * @param  editable  DOCUMENT ME!
     */
    public Sb_stadtbildserieEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        makeEditable();
        jScrollPane5.getViewport().setOpaque(false);
        title = "";
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            btnReport,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
        previewMap = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(previewMap, BorderLayout.CENTER);

        txtaPruefhinweis.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    selectCheckBox();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    selectCheckBox();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    selectCheckBox();
                }

                private void selectCheckBox() {
                    if (StringUtils.isNotBlank(txtaPruefhinweis.getText())) {
                        chbPruefen.setSelected(true);
                        if (!editable) {
                            btnSavePruefhinweis.setEnabled(true);
                        }
                    } else {
                        chbPruefen.setSelected(false);
                        if (!editable) {
                            btnSavePruefhinweis.setEnabled(false);
                        }
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void makeEditable() {
        if (!editable) { // is Renderer
            btnAddImageNumber.setVisible(false);
            btnRemoveImageNumber.setVisible(false);
            btnAddSuchwort.setVisible(false);
            btnRemoveSuchwort.setVisible(false);
            RendererTools.makeReadOnly(dpAufnahmedatum);
            dpAufnahmedatum.getEditor().setDisabledTextColor(Color.black);
            RendererTools.makeReadOnly(dbcBildtyp);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcBildtyp);
            RendererTools.makeReadOnly(dbcBlickrichtung);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcBlickrichtung);
            RendererTools.makeReadOnly(dbcLager);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcLager);
            RendererTools.makeReadOnly(dbcNutzungseinschraenkung);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcNutzungseinschraenkung);
            RendererTools.makeReadOnly(txtaComment);
            RendererTools.makeReadOnly(bcbStrasse);
            RendererTools.makeTextBlackOfDisabledComboBox(bcbStrasse);
            RendererTools.makeReadOnly(dbcOrt);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcOrt);
            RendererTools.makeReadOnly(txtHausnummer);
            RendererTools.makeReadOnly(dbcAuftraggeber);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcAuftraggeber);
            RendererTools.makeReadOnly(dbcFotograf);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcFotograf);
            RendererTools.makeReadOnly(dbcFilmart);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcFilmart);
        }
        previewImage.makeEditable();
    }

    /**
     * DOCUMENT ME!
     */
    private void decorateComboBoxes() {
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(bcbStrasse);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcAuftraggeber);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcBildtyp);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcBlickrichtung);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcFilmart);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcFotograf);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcLager);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcNutzungseinschraenkung);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcOrt);
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

        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        sqlDateToStringConverter = new de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter();
        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        imgpBulletPoint = new org.jdesktop.swingx.JXImagePanel();
        lblTitle = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        panPrintButton = new javax.swing.JPanel();
        btnReport = new javax.swing.JButton();
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        panFooter = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblEintragungsdatum = new javax.swing.JLabel();
        lblGeomAus = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        roundedPanel2 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        panContent = new javax.swing.JPanel();
        pnlCtrlButtons1 = new javax.swing.JPanel();
        btnAddSuchwort = new javax.swing.JButton();
        btnRemoveSuchwort = new javax.swing.JButton();
        pnlCtrlButtons = new javax.swing.JPanel();
        btnAddImageNumber = new javax.swing.JButton();
        btnRemoveImageNumber = new javax.swing.JButton();
        lblDescBildnummer = new javax.swing.JLabel();
        lblDescLagerort = new javax.swing.JLabel();
        lblDescAufnahmedatum = new javax.swing.JLabel();
        lblDescInfo = new javax.swing.JLabel();
        lblDescBildtyp = new javax.swing.JLabel();
        lblDescSuchworte = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstBildnummern = new JXListBugFixes();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSuchworte = new JXListBugFixes();
        dbcBildtyp = new FastBindableReferenceCombo();
        dbcLager = new FastBindableReferenceCombo();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtaComment = new javax.swing.JTextArea();
        dpAufnahmedatum = new org.jdesktop.swingx.JXDatePicker();
        jLabel9 = new javax.swing.JLabel();
        dbcNutzungseinschraenkung = new FastBindableReferenceCombo();
        lblDescBildtyp1 = new javax.swing.JLabel();
        dbcBlickrichtung = new FastBindableReferenceCombo();
        roundedPanel3 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        panDetails = new javax.swing.JPanel();
        lblDescFilmart = new javax.swing.JLabel();
        lblDescFotograf = new javax.swing.JLabel();
        lblDescAuftraggeber = new javax.swing.JLabel();
        dbcAuftraggeber = new DefaultBindableReferenceCombo();
        dbcFotograf = new FastBindableReferenceCombo();
        dbcFilmart = new FastBindableReferenceCombo();
        roundedPanel4 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblGeomDigitized = new javax.swing.JLabel();
        lblGeomAusAdresse = new javax.swing.JLabel();
        lblGeomAusStrasse = new javax.swing.JLabel();
        panDetails1 = new javax.swing.JPanel();
        if (editable) {
            lblDescGeometrie = new javax.swing.JLabel();
        }
        lblDescOrt = new javax.swing.JLabel();
        lblDescStrasse = new javax.swing.JLabel();
        dbcOrt = new FastBindableReferenceCombo();
        lblHausnummer = new javax.swing.JLabel();
        txtHausnummer = new de.cismet.cids.editors.DefaultBindableJTextField();
        btnCombineGeometries = new javax.swing.JButton();
        if (!editable) {
            btnCombineGeometries.setVisible(false);
        }
        if (editable) {
            dbcGeom = new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
        }
        bcbStrasse = new FastBindableReferenceCombo();
        jPanel2 = new javax.swing.JPanel();
        previewImage = new de.cismet.cids.custom.objecteditors.utils.Sb_StadtbildPreviewImage(getConnectionContext());
        roundedPanel7 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel8 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        panDetails4 = new javax.swing.JPanel();
        pnlMap = new javax.swing.JPanel();
        roundedPanel6 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        panDetails3 = new javax.swing.JPanel();
        chbPruefen = new DefaultBindableJCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtaPruefhinweis = new javax.swing.JTextArea();
        btnSavePruefhinweis = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblPruefhinweisVon = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        lblBusyPruef = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(20, 20));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        imgpBulletPoint.setMinimumSize(new java.awt.Dimension(16, 16));
        imgpBulletPoint.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(imgpBulletPoint, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(lblTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panTitleString.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panTitleString.add(filler1, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        panPrintButton.setOpaque(false);
        panPrintButton.setLayout(new java.awt.GridBagLayout());

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png"))); // NOI18N
        btnReport.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnReport.text"));                                            // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnReport.toolTipText"));                                     // NOI18N
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
        panPrintButton.add(btnReport, gridBagConstraints);

        panTitle.add(panPrintButton, java.awt.BorderLayout.EAST);

        roundedPanel1.add(semiRoundedPanel1, java.awt.BorderLayout.CENTER);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("eingetragen am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jLabel4, gridBagConstraints);

        lblEintragungsdatum.setForeground(new java.awt.Color(255, 255, 255));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eintragungsdatum}"),
                lblEintragungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(timeStampToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(lblEintragungsdatum, gridBagConstraints);

        panFooter.add(jPanel5, java.awt.BorderLayout.LINE_START);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus.abkuerzung}"),
                lblGeomAus,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus.comment}"),
                lblGeomAus,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setBorder(null);
        jScrollPane5.setOpaque(false);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        roundedPanel2.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel3.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel3.setLayout(new java.awt.FlowLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Allgemeine Informationen");
        semiRoundedPanel3.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel2.add(semiRoundedPanel3, gridBagConstraints);

        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        pnlCtrlButtons1.setOpaque(false);
        pnlCtrlButtons1.setLayout(new java.awt.GridBagLayout());

        btnAddSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddSuchwort.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnRemoveImg.text"));                                                             // NOI18N
        btnAddSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnAddSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        pnlCtrlButtons1.add(btnAddSuchwort, gridBagConstraints);

        btnRemoveSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveSuchwort.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnRemoveImg.text"));                                                                // NOI18N
        btnRemoveSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnRemoveSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 5, 10);
        pnlCtrlButtons1.add(btnRemoveSuchwort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panContent.add(pnlCtrlButtons1, gridBagConstraints);

        pnlCtrlButtons.setOpaque(false);
        pnlCtrlButtons.setLayout(new java.awt.GridBagLayout());

        btnAddImageNumber.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddImageNumber.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnRemoveImg.text"));                                                             // NOI18N
        btnAddImageNumber.setPreferredSize(new java.awt.Dimension(46, 21));
        btnAddImageNumber.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddImageNumberActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        pnlCtrlButtons.add(btnAddImageNumber, gridBagConstraints);

        btnRemoveImageNumber.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveImageNumber.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnRemoveImg.text"));                                                                // NOI18N
        btnRemoveImageNumber.setPreferredSize(new java.awt.Dimension(46, 21));
        btnRemoveImageNumber.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveImageNumberActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        pnlCtrlButtons.add(btnRemoveImageNumber, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panContent.add(pnlCtrlButtons, gridBagConstraints);

        lblDescBildnummer.setText("Bildnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panContent.add(lblDescBildnummer, gridBagConstraints);

        lblDescLagerort.setText("Lagerort:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescLagerort, gridBagConstraints);

        lblDescAufnahmedatum.setText("Aufnahmedatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescAufnahmedatum, gridBagConstraints);

        lblDescInfo.setText("Kommentar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panContent.add(lblDescInfo, gridBagConstraints);

        lblDescBildtyp.setText("Bildtyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescBildtyp, gridBagConstraints);

        lblDescSuchworte.setText("Suchworte:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescSuchworte, gridBagConstraints);

        lstBildnummern.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        lstBildnummern.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstBildnummern.setVisibleRowCount(5);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.stadtbilder_arr}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstBildnummern);
        bindingGroup.addBinding(jListBinding);

        lstBildnummern.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstBildnummernValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(lstBildnummern);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panContent.add(jScrollPane1, gridBagConstraints);

        lstSuchworte.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        lstSuchworte.setVisibleRowCount(5);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.suchwort_arr}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstSuchworte);
        bindingGroup.addBinding(jListBinding);

        jScrollPane2.setViewportView(lstSuchworte);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jScrollPane2, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcBildtyp).setSorted(true);
        dbcBildtyp.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bildtyp}"),
                dbcBildtyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(dbcBildtyp, gridBagConstraints);
        ((FastBindableReferenceCombo)dbcBildtyp).setNullable(false);

        ((FastBindableReferenceCombo)dbcLager).setSorted(true);
        dbcLager.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lager}"),
                dbcLager,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(dbcLager, gridBagConstraints);

        txtaComment.setColumns(20);
        txtaComment.setLineWrap(true);
        txtaComment.setRows(5);
        txtaComment.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kommentar}"),
                txtaComment,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(txtaComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panContent.add(jScrollPane3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufnahmedatum}"),
                dpAufnahmedatum,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(timeStampConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(dpAufnahmedatum, gridBagConstraints);

        jLabel9.setText("Nutzungseinschränkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jLabel9, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcNutzungseinschraenkung).setSorted(true);
        ((FastBindableReferenceCombo)dbcNutzungseinschraenkung).setNullable(false);
        dbcNutzungseinschraenkung.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nutzungseinschraenkung}"),
                dbcNutzungseinschraenkung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        dbcNutzungseinschraenkung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    dbcNutzungseinschraenkungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(dbcNutzungseinschraenkung, gridBagConstraints);

        lblDescBildtyp1.setText("Blickrichtung nach:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescBildtyp1, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcBildtyp).setSorted(true);
        dbcBlickrichtung.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.blickrichtung}"),
                dbcBlickrichtung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(dbcBlickrichtung, gridBagConstraints);
        ((FastBindableReferenceCombo)dbcBildtyp).setNullable(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel2.add(panContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(roundedPanel2, gridBagConstraints);

        roundedPanel3.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel4.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel4.setLayout(new java.awt.FlowLayout());

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Metainformationen");
        semiRoundedPanel4.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel3.add(semiRoundedPanel4, gridBagConstraints);

        panDetails.setOpaque(false);
        panDetails.setLayout(new java.awt.GridBagLayout());

        lblDescFilmart.setText("Filmart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescFilmart, gridBagConstraints);

        lblDescFotograf.setText("Fotograf:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescFotograf, gridBagConstraints);

        lblDescAuftraggeber.setText("Auftraggeber:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescAuftraggeber, gridBagConstraints);

        ((DefaultBindableReferenceCombo)dbcAuftraggeber).setSortingColumn("name");
        dbcAuftraggeber.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.auftraggeber}"),
                dbcAuftraggeber,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcAuftraggeber, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcFotograf).setSorted(true);
        dbcFotograf.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fotograf}"),
                dbcFotograf,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcFotograf, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcFilmart).setSorted(true);
        dbcFilmart.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.filmart}"),
                dbcFilmart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcFilmart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel3.add(panDetails, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(roundedPanel3, gridBagConstraints);

        roundedPanel4.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel5.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel5.setLayout(new java.awt.FlowLayout());

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Ortbezogene Informationen");
        semiRoundedPanel5.add(jLabel3);

        jLabel7.setText("    ");
        semiRoundedPanel5.add(jLabel7);

        lblGeomDigitized.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/digitized.png"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus.comment}"),
                lblGeomDigitized,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        semiRoundedPanel5.add(lblGeomDigitized);

        lblGeomAusAdresse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/adresse.gif"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus.comment}"),
                lblGeomAusAdresse,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        semiRoundedPanel5.add(lblGeomAusAdresse);

        lblGeomAusStrasse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/strasse.gif"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus.comment}"),
                lblGeomAusStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        semiRoundedPanel5.add(lblGeomAusStrasse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel4.add(semiRoundedPanel5, gridBagConstraints);

        panDetails1.setOpaque(false);
        panDetails1.setLayout(new java.awt.GridBagLayout());

        if (editable) {
            lblDescGeometrie.setText("Geometrie:");
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            panDetails1.add(lblDescGeometrie, gridBagConstraints);
        }

        lblDescOrt.setText("Ort:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblDescOrt, gridBagConstraints);

        lblDescStrasse.setText("Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblDescStrasse, gridBagConstraints);

        ((FastBindableReferenceCombo)dbcOrt).setSorted(true);
        dbcOrt.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ort}"),
                dbcOrt,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        dbcOrt.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    dbcOrtItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(dbcOrt, gridBagConstraints);

        lblHausnummer.setText("Hs.-Nr.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblHausnummer, gridBagConstraints);

        txtHausnummer.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnummer}"),
                txtHausnummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(txtHausnummer, gridBagConstraints);

        if (editable) {
            btnCombineGeometries.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
            btnCombineGeometries.setText(org.openide.util.NbBundle.getMessage(
                    Sb_stadtbildserieEditor.class,
                    "VermessungRissEditor.btnCombineGeometries.text"));                                     // NOI18N
            btnCombineGeometries.setToolTipText(org.openide.util.NbBundle.getMessage(
                    Sb_stadtbildserieEditor.class,
                    "VermessungRissEditor.btnCombineGeometries.toolTipText"));                              // NOI18N
            btnCombineGeometries.setEnabled(false);
            btnCombineGeometries.setFocusPainted(false);
        }
        btnCombineGeometries.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCombineGeometriesActionPerformed(evt);
                }
            });
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            panDetails1.add(btnCombineGeometries, gridBagConstraints);
        }

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom}"),
                    dbcGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)dbcGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
            panDetails1.add(dbcGeom, gridBagConstraints);
        }

        ((FastBindableReferenceCombo)bcbStrasse).setSorted(true);
        bcbStrasse.setEditable(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                bcbStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(bcbStrasse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel4.add(panDetails1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(roundedPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 3.0;
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(previewImage, gridBagConstraints);
        previewImage.setStadtbildserieProvider(this);

        roundedPanel7.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel8.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel8.setLayout(new java.awt.FlowLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Karte");
        semiRoundedPanel8.add(jLabel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel7.add(semiRoundedPanel8, gridBagConstraints);

        panDetails4.setOpaque(false);
        panDetails4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panDetails4.add(pnlMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel7.add(panDetails4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(roundedPanel7, gridBagConstraints);

        roundedPanel6.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel7.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel7.setLayout(new java.awt.FlowLayout());

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Prüfhinweis");
        semiRoundedPanel7.add(jLabel5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel6.add(semiRoundedPanel7, gridBagConstraints);

        panDetails3.setOpaque(false);
        panDetails3.setLayout(new java.awt.GridBagLayout());

        chbPruefen.setText("Prüfen");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefen}"),
                chbPruefen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        binding.setConverter(((DefaultBindableJCheckBox)chbPruefen).getConverter());
        bindingGroup.addBinding(binding);

        chbPruefen.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    chbPruefenItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panDetails3.add(chbPruefen, gridBagConstraints);

        txtaPruefhinweis.setColumns(20);
        txtaPruefhinweis.setLineWrap(true);
        txtaPruefhinweis.setRows(5);
        txtaPruefhinweis.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefen_kommentar}"),
                txtaPruefhinweis,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(txtaPruefhinweis);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panDetails3.add(jScrollPane4, gridBagConstraints);

        btnSavePruefhinweis.setText("Prüfhinweis speichern");
        btnSavePruefhinweis.setEnabled(false);
        btnSavePruefhinweis.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSavePruefhinweisActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panDetails3.add(btnSavePruefhinweis, gridBagConstraints);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel8.setText("erstellt von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel6.add(jLabel8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefhinweis_von}"),
                lblPruefhinweisVon,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel6.add(lblPruefhinweisVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panDetails3.add(jPanel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 0);
        panDetails3.add(lblBusyPruef, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panDetails3.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel6.add(panDetails3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(roundedPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 3.0;
        jPanel3.add(jPanel2, gridBagConstraints);

        jScrollPane5.setViewportView(jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane5, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final ArrayList beans = new ArrayList<CidsBean>();
                    beans.add(cidsBean);
                    final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(beans);
                    return beanArray;
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new StadtbildserieReportParameterGenerator();

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String vorschaubildnummer = (String)cidsBean.getProperty("vorschaubild.bildnummer");
            final String filename = "stadbildserie_"
                        + vorschaubildnummer;
            final String downloadTitle = "Stadbildserie "
                        + vorschaubildnummer;
            final String resourceName = REPORT_STADTBILDSERIE_URL;
            final JasperReportDownload download = new JasperReportDownload(
                    resourceName,
                    parametersGenerator,
                    dataSourceGenerator,
                    jobname,
                    downloadTitle,
                    filename);
            DownloadManager.instance().add(download);
        }
    } //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   evt  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private void btnCombineGeometriesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCombineGeometriesActionPerformed
        final CidsBean geom_aus = (CidsBean)cidsBean.getProperty("geom_aus");
        if ((geom_aus == null) || geom_aus.getPrimaryKeyValue().equals(geomFromDigitizedAction.getPrimaryKeyValue())) {
            final Object[] options = { "Ja, Geometrie überschreiben", "Abbrechen" };
            final int result = JOptionPane.showOptionDialog(StaticSwingTools.getParentFrame(this),
                    "Durch diese Aktion wird die digitalisierte Geometrie überschrieben. Wollen Sie das wirklich?",
                    "Geometrie überschreiben?",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);
            if ((result == JOptionPane.CLOSED_OPTION) || (result == 1)) {
                return;
            }
        }

        if (cidsBean.getProperty("strasse") != null) {
            final CidsBean sbGeom = (CidsBean)cidsBean.getProperty("geom");

            boolean hausnummerHit = false;
            if (cidsBean.getProperty("hausnummer") != null) {
                // Regel R2 aus https://github.com/cismet/wupp/issues/406
                // Serversearch
                // wenn Treffer dann hausnummerHit=true setzen
                try {
                    final String query = String.format(
                            GET_GEOM_FROM_ADRESSE,
                            ((CidsBean)cidsBean.getProperty("strasse")).getPrimaryKeyValue(),
                            txtHausnummer.getText(),
                            getConnectionContext());
                    final MetaObject[] results = SessionManager.getProxy()
                                .getMetaObjectByQuery(query, 0, getConnectionContext());
                    if (results.length > 0) {
                        final CidsBean result = results[0].getBean();
                        final Geometry geometry = (Geometry)result.getProperty("geo_field");
                        try {
                            if (geometry != null) {
                                digitizedSetter.setActivated(false);
                                final Geometry expanded = geometry.buffer(20).getEnvelope();
                                if (sbGeom != null) {
                                    cidsBean.setProperty("geom.geo_field", expanded);
                                    dbcGeom.getConverter().convertForward(sbGeom);
                                } else {
                                    final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                                            CidsBeanSupport.DOMAIN_NAME,
                                            "geom",
                                            getConnectionContext());
                                    final CidsBean geom = geomMetaClass.getEmptyInstance(getConnectionContext())
                                                .getBean();
                                    geom.setProperty("geo_field", expanded);
                                    cidsBean.setProperty("geom", geom);
                                }
                                cidsBean.setProperty("geom_aus", geomFromAdresse);
                                hausnummerHit = true;
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException("Error in Magic Wand Algorithm", ex);
                        } finally {
                            digitizedSetter.setActivated(true);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error in Magic Wand Algorithm", e);
                }
            }
            if (!hausnummerHit) {
                // Regel R1 aus https://github.com/cismet/wupp/issues/406
                final Geometry geometry = (Geometry)cidsBean.getProperty("strasse.bsa_bbox.geo_field");

                try {
                    if (geometry != null) {
                        digitizedSetter.setActivated(false);
                        if (sbGeom != null) {
                            cidsBean.setProperty("geom.geo_field", geometry.clone());
                            dbcGeom.getConverter().convertForward(sbGeom);
                        } else {
                            final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                                    CidsBeanSupport.DOMAIN_NAME,
                                    "geom",
                                    getConnectionContext());
                            final CidsBean geom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                            geom.setProperty("geo_field", geometry.clone());
                            cidsBean.setProperty("geom", geom);
                        }
                        cidsBean.setProperty("geom_aus", geomFromStrasse);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("Error in Magic Wand Algorithm", ex);
                } finally {
                    digitizedSetter.setActivated(true);
                }
            }
        }
    } //GEN-LAST:event_btnCombineGeometriesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dbcOrtItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_dbcOrtItemStateChanged
        final Object selectedItem = dbcOrt.getSelectedItem();
        if (editable) {
            if ((selectedItem != null) && selectedItem.equals(StadtbilderUtils.getWuppertal(getConnectionContext()))) {
                // inside of Wuppertal
                bcbStrasse.setEnabled(true);
                lblDescStrasse.setEnabled(true);
                txtHausnummer.setEnabled(true);
                lblHausnummer.setEnabled(true);
            } else {
                // outside of Wuppertal
                bcbStrasse.setEnabled(false);
                bcbStrasse.setSelectedItem(null);
                lblDescStrasse.setEnabled(false);
                txtHausnummer.setEnabled(false);
                txtHausnummer.setText("");
                lblHausnummer.setEnabled(false);
            }
        }
    } //GEN-LAST:event_dbcOrtItemStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dbcNutzungseinschraenkungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_dbcNutzungseinschraenkungActionPerformed
        try {
            cidsBean.setProperty("tmp_restriction_level", null);
        } catch (Exception ex) {
            LOG.warn(ex, ex);
        }
        determineBulletPoint();
    }                                                                                             //GEN-LAST:event_dbcNutzungseinschraenkungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstBildnummernValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstBildnummernValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (!lstBildnummern.isSelectionEmpty()) {
                final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                        cidsBean,
                        (CidsBean)lstBildnummern.getSelectedValue());
                previewImage.setStadtbildInfo(stadtbildInfo);
                lstBildnummern.ensureIndexIsVisible(lstBildnummern.getSelectedIndex());
            } else {
                previewImage.removeImage();
            }
        }
    }                                                                                         //GEN-LAST:event_lstBildnummernValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveImageNumberActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveImageNumberActionPerformed
        final Object selection = lstBildnummern.getSelectedValue();
        if ((selection != null) && (selection instanceof CidsBean)) {
            final CidsBean cidesBeanToRemove = (CidsBean)selection;
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die Bildnummer wirklich entfernt werden?",
                    "Bildernummern entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final int modelSize = lstBildnummern.getModel().getSize();
                if (modelSize >= 2) {
                    final int oldIndex = lstBildnummern.getSelectedIndex();
                    // select the second or second last element as new selected element
                    final int newIndex = (oldIndex == 0) ? 1 : (oldIndex - 1);
                    lstBildnummern.setSelectedIndex(newIndex);
                } else {
                    previewImage.removeImage();
                }

                try {
                    final List<CidsBean> fotos = cidsBean.getBeanCollectionProperty("stadtbilder_arr");
                    if (fotos != null) {
                        fotos.remove(cidesBeanToRemove);
                    }
                    final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                            cidsBean,
                            cidesBeanToRemove);
                    StadtbilderUtils.removeBildnummerFromImageCacheAndFailedSet(stadtbildInfo);
                } catch (Exception e) {
                    LOG.error(e, e);
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler",
                            "Beim Entfernen der Bildernummern ist ein Fehler aufgetreten",
                            null,
                            null,
                            e,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(StaticSwingTools.getParentFrame(this), ei);
                }
            }
            previewImage.defineButtonStatus();
        }
    } //GEN-LAST:event_btnRemoveImageNumberActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddImageNumberActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddImageNumberActionPerformed
        final Sb_stadtbildserieEditorAddBildnummerDialog dialog = new Sb_stadtbildserieEditorAddBildnummerDialog((Frame)
                SwingUtilities.getWindowAncestor(this),
                true,
                getConnectionContext());
        final Collection<CidsBean> bildnummern = dialog.showDialog();
        final List<CidsBean> fotos = cidsBean.getBeanCollectionProperty("stadtbilder_arr");
        for (final CidsBean stadtbild : bildnummern) {
            boolean alreadyAssigned = false;
            for (final CidsBean stadtbildAlreadyAssigned : fotos) {
                if (stadtbildAlreadyAssigned.getProperty("bildnummer").equals(stadtbild.getProperty("bildnummer"))) {
                    alreadyAssigned = true;
                    break;
                }
            }
            if (!alreadyAssigned) {
                fotos.add(stadtbild);
            }
        }
        previewImage.defineButtonStatus();
    }                                                                                     //GEN-LAST:event_btnAddImageNumberActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveSuchwortActionPerformed
        final Object[] selection = lstSuchworte.getSelectedValues();
        if ((selection != null) && (selection.length > 0)) {
            final int selectedIndex = lstSuchworte.getSelectedIndex();
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Suchwörter wirklich entfernt werden?",
                    "Suchwörter entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    final List<Object> removeList = Arrays.asList(selection);
                    final List<CidsBean> suchwoerter = cidsBean.getBeanCollectionProperty("suchwort_arr");
                    if (suchwoerter != null) {
                        suchwoerter.removeAll(removeList);
                    }
                    final int listSize = lstSuchworte.getModel().getSize();
                    if (listSize > 0) {
                        if (selectedIndex < listSize) {
                            lstSuchworte.setSelectedIndex(selectedIndex);
                        } else {
                            lstSuchworte.setSelectedIndex(listSize - 1);
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e, e);
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler",
                            "Beim Entfernen der Suchwörter ist ein Fehler aufgetreten",
                            null,
                            null,
                            e,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(StaticSwingTools.getParentFrame(this), ei);
                }
            }
        }
    }                                                                                     //GEN-LAST:event_btnRemoveSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddSuchwortActionPerformed
        for (final CidsBean newSuchwort : Sb_stadtbildserieEditorAddSuchwortDialog.getInstance().showDialog()) {
            final List<CidsBean> suchwoerter = cidsBean.getBeanCollectionProperty("suchwort_arr");
            if (newSuchwort != null) {
                if (!suchwoerter.contains(newSuchwort)) {
                    suchwoerter.add(newSuchwort);
                }
            }
        }
    }                                                                                  //GEN-LAST:event_btnAddSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSavePruefhinweisActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSavePruefhinweisActionPerformed
        if (StringUtils.isNotBlank(txtaPruefhinweis.getText())) {
            final ServerActionParameter paramComment = new ServerActionParameter(
                    Sb_stadtbildserieUpdatePruefhinweisAction.ParameterType.COMMENT.toString(),
                    txtaPruefhinweis.getText());
            final ServerActionParameter paramSBSid = new ServerActionParameter(
                    Sb_stadtbildserieUpdatePruefhinweisAction.ParameterType.STADTBILDSERIE_ID.toString(),
                    cidsBean.getPrimaryKeyValue());

            new SavePruefhinweisWorker(paramComment, paramSBSid).execute();
        } else {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                "Das Kommentarfeld für den Prüfhinweis ist leer.",
                "Kommentarfeld leer",
                JOptionPane.WARNING_MESSAGE);
        }
    } //GEN-LAST:event_btnSavePruefhinweisActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chbPruefenItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_chbPruefenItemStateChanged
        if (!chbPruefen.isSelected()) {
            txtaPruefhinweis.setText("");
            lblPruefhinweisVon.setText("");
        }
    }                                                                             //GEN-LAST:event_chbPruefenItemStateChanged

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
     */
    private void automaticallySortLists() {
        ((JXList)lstBildnummern).setAutoCreateRowSorter(true);
        ((JXList)lstBildnummern).setSortOrder(SortOrder.ASCENDING);
        ((JXList)lstSuchworte).setAutoCreateRowSorter(true);
        ((JXList)lstSuchworte).setSortOrder(SortOrder.ASCENDING);
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
        try {
            geomFromAdresse =
                MetaObjectCache.getInstance()
                        .getMetaObjectsByQuery(GEOM_AUS_ADRESSE_QUERY, "WUNDA_BLAU", getConnectionContext())[0]
                        .getBean();
            geomFromStrasse =
                MetaObjectCache.getInstance()
                        .getMetaObjectsByQuery(GEOM_AUS_STRASSE_QUERY, "WUNDA_BLAU", getConnectionContext())[0]
                        .getBean();
            geomFromDigitizedAction =
                MetaObjectCache.getInstance()
                        .getMetaObjectsByQuery(GEOM_AUS_DIGI_QUERY, "WUNDA_BLAU", getConnectionContext())[0].getBean();
        } catch (CacheException ex) {
            throw new RuntimeException("Geometry origin state could not be loaded. That should not happen.", ex);
        }
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
            initMap();

            bindingGroup.bind();
            if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                setDefaultValuesForNewCidsBean();
            }
            restrictedLevel = Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(
                    cidsBean,
                    getConnectionContext());

            decorateComboBoxes();
            automaticallySortLists();
            final String vorschaubild = (String)cidsBean.getProperty("vorschaubild.bildnummer");
            this.title = "Stadtbildserie "
                        + ((vorschaubild != null) ? vorschaubild : "");
            lblTitle.setText(this.title);
            lstBildnummern.setSelectedValue(cidsBean.getProperty("vorschaubild"), true);

            final String pruefhinweis = (String)cidsBean.getProperty("pruefen_kommentar");
            if (StringUtils.isNotBlank(pruefhinweis) && !editable) {
                // the Pruefhinweis can not be changed in the renderer if a pruefhinweis already exists.
                chbPruefen.setEnabled(false);
                btnSavePruefhinweis.setEnabled(false);
                txtaPruefhinweis.setEnabled(false);
            }
            if (chbPruefen.isSelected() && !editable) {
                chbPruefen.setEnabled(false);
            }

            determineBulletPoint();
        }
        handleVisibilityOfGeomAusIcons();
        if (editable) {
            handleEnabledStateOfBtnCombineGeometries();

            cidsBean.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("strasse")) {
                            handleEnabledStateOfBtnCombineGeometries();
                        }
                        if (evt.getPropertyName().equals("geom_aus")) {
                            handleVisibilityOfGeomAusIcons();
                        }
                        if (evt.getPropertyName().equals("geom")) {
                            if (evt.getOldValue() == null) {
                                try {
                                    initMap();
                                    refreshPreviewGeometry();
                                    cidsBean.setProperty("geom_aus", geomFromDigitizedAction);
                                } catch (Exception ex) {
                                    throw new RuntimeException("Error when setting geom origin.", ex);
                                }
                            }
                        }
                    }
                });

            new CidsBeanDeepPropertyListener(cidsBean, "geom.geo_field").addPropertyChangeListener(
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        refreshPreviewGeometry();
                    }
                });

            new CidsBeanDeepPropertyListener(cidsBean, "geom.geo_field").addPropertyChangeListener(digitizedSetter);
        }
    }

    /**
     * Retrieve the image and the tooltip for the bullet point, which follows the title.
     */
    private void determineBulletPoint() {
        imgpBulletPoint.setImage(null);
        imgpBulletPoint.setToolTipText("");
        new SwingWorker<Sb_RestrictionLevelUtils.BulletPointSettings, Void>() {

                @Override
                protected Sb_RestrictionLevelUtils.BulletPointSettings doInBackground() throws Exception {
                    return Sb_RestrictionLevelUtils.determineBulletPointAndInfoText(
                            cidsBean,
                            getConnectionContext());
                }

                @Override
                protected void done() {
                    try {
                        final Sb_RestrictionLevelUtils.BulletPointSettings imageAndInfo = get();
                        imgpBulletPoint.setImage(imageAndInfo.getColorImage());
                        imgpBulletPoint.setToolTipText(imageAndInfo.getTooltipText());
                    } catch (InterruptedException ex) {
                        LOG.error(ex, ex);
                    } catch (ExecutionException ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     */
    private void handleEnabledStateOfBtnCombineGeometries() {
        if (btnCombineGeometries != null) {
            if (bcbStrasse.getSelectedItem() != null) {
                btnCombineGeometries.setEnabled(true);
            } else {
                btnCombineGeometries.setEnabled(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void handleVisibilityOfGeomAusIcons() {
        try {
            final int id = (Integer)cidsBean.getProperty("geom_aus.id");
            if (id == 0) {
                lblGeomAusAdresse.setVisible(true);
                lblGeomAusStrasse.setVisible(false);
                lblGeomDigitized.setVisible(false);
            } else if (id == 1) {
                lblGeomAusAdresse.setVisible(false);
                lblGeomAusStrasse.setVisible(true);
                lblGeomDigitized.setVisible(false);
            } else if (id == 2) {
                lblGeomAusAdresse.setVisible(false);
                lblGeomAusStrasse.setVisible(false);
                lblGeomDigitized.setVisible(true);
            }
        } catch (Exception e) {
            lblGeomAusAdresse.setVisible(false);
            lblGeomAusStrasse.setVisible(false);
            lblGeomDigitized.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
        this.title = "Stadtbildserie "
                    + title;
        lblTitle.setText(this.title);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)dbcGeom).dispose();
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
        DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "sb_stadtbildserie",
            18, // id 161078 high res, id 18 = interval
            1280,
            1024);

//        final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
//                "WUNDA_BLAU",
//                "Administratoren",
//                "admin",
//                "kif",
//                "sb_stadtbildserie",
//                18);
//
//        DevelopmentTools.showReportForBeans(
//            REPORT_STADTBILDSERIE_URL,
//            Arrays.asList(beans));
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        if (cidsBean != null) {
            final Object geoObj = cidsBean.getProperty("geom.geo_field");
            if (geoObj instanceof Geometry) {
                final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                        ClientAlkisConf.getInstance().getSrsService());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ALKISConstatns.Commons.GeoBUffer: " + ClientAlkisConf.getInstance().getGeoBuffer());
                }
                final XBoundingBox box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                            ClientAlkisConf.getInstance().getGeoBuffer()));
                final double diagonalLength = Math.sqrt((box.getWidth() * box.getWidth())
                                + (box.getHeight() * box.getHeight()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Buffer for map: " + diagonalLength);
                }
                final XBoundingBox bufferedBox = new XBoundingBox(box.getGeometry().buffer(diagonalLength));
                final Runnable mapRunnable = new Runnable() {

                        @Override
                        public void run() {
                            final ActiveLayerModel mappingModel = new ActiveLayerModel();
                            mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                            mappingModel.addHome(new XBoundingBox(
                                    bufferedBox.getX1(),
                                    bufferedBox.getY1(),
                                    bufferedBox.getX2(),
                                    bufferedBox.getY2(),
                                    ClientAlkisConf.getInstance().getSrsService(),
                                    true));
                            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                        ClientAlkisConf.getInstance().getMapCallString()));
                            swms.setName("Stadtbildserie");

                            previewGeometry.setGeometry(pureGeom);
                            previewGeometry.setFillingPaint(new Color(1, 0, 0, 0.5f));
                            previewGeometry.setLineWidth(3);
                            previewGeometry.setLinePaint(new Color(1, 0, 0, 1f));
                            // add the raster layer to the model
                            mappingModel.addLayer(swms);
                            // set the model
                            previewMap.setMappingModel(mappingModel);
                            // initial positioning of the map
                            final int duration = previewMap.getAnimationDuration();
                            previewMap.setAnimationDuration(0);
                            previewMap.gotoInitialBoundingBox();
                            // interaction mode
                            previewMap.setInteractionMode(MappingComponent.ZOOM);
                            // finally when all configurations are done ...
                            previewMap.unlock();
                            previewMap.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                                    @Override
                                    public void mouseClicked(final PInputEvent evt) {
                                        if (evt.getClickCount() > 1) {
                                            final CidsBean bean = cidsBean;
                                            ObjectRendererUtils.switchToCismapMap();
                                            ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                        }
                                    }
                                });
                            previewMap.setInteractionMode("MUTE");
                            previewMap.getFeatureCollection().addFeature(previewGeometry);
                            previewMap.setAnimationDuration(duration);
                        }
                    };
                if (EventQueue.isDispatchThread()) {
                    mapRunnable.run();
                } else {
                    EventQueue.invokeLater(mapRunnable);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshPreviewGeometry() {
        final Geometry geoObj = (Geometry)cidsBean.getProperty("geom.geo_field");
        if (geoObj instanceof Geometry) {
            if (true || (lastRefreshedGeometry == null) || !geoObj.equals(lastRefreshedGeometry)) {
                lastRefreshedGeometry = (Geometry)geoObj;
                if (!previewMap.getFeatureCollection().contains(previewGeometry)) {
                    previewMap.getFeatureCollection().addFeature(previewGeometry);
                }
                final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                        ClientAlkisConf.getInstance().getSrsService());
                previewGeometry.setGeometry(pureGeom);
                previewMap.reconsiderFeature(previewGeometry);
                final XBoundingBox box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                            ClientAlkisConf.getInstance().getGeoBuffer()));
                final double diagonalLength = Math.sqrt((box.getWidth() * box.getWidth())
                                + (box.getHeight() * box.getHeight()));
                final XBoundingBox bufferedBox = new XBoundingBox(box.getGeometry().buffer(diagonalLength));
                LOG.fatal("gotoBoundingBox" + bufferedBox, new Exception());

                previewMap.gotoBoundingBox(bufferedBox, true, true, 1000);
            }
        } else {
            previewMap.getFeatureCollection().removeAllFeatures();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setDefaultValuesForNewCidsBean() {
        try {
            cidsBean.setProperty("ort", StadtbilderUtils.getWuppertal(getConnectionContext()));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            cidsBean.setProperty("lager", StadtbilderUtils.getR102(getConnectionContext()));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            cidsBean.setProperty(
                "nutzungseinschraenkung",
                Sb_RestrictionLevelUtils.getNoRestriction(getConnectionContext()));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        dpAufnahmedatum.setDate(new Date());
        if (dbcBildtyp.getItemCount() > 0) {
            dbcBildtyp.setSelectedIndex(0);
        }
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return new Sb_stadtbildserieInitializer(cidsBean, getConnectionContext());
    }

    @Override
    public CidsBean getStadtbildserie() {
        return cidsBean;
    }

    @Override
    public CidsBean getSelectedStadtbild() {
        return (CidsBean)lstBildnummern.getSelectedValue();
    }

    @Override
    public void previousImageSelected() {
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() - 1);
    }

    @Override
    public void nextImageSelected() {
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() + 1);
    }

    @Override
    public void newPreviewImageSelected() {
        try {
            cidsBean.setProperty("vorschaubild", lstBildnummern.getSelectedValue());
        } catch (Exception e) {
            LOG.error("Error while setting the preview image of the CidsBean", e);
        }
    }

    @Override
    public boolean isFirstSelected() {
        final int selectedIdx = lstBildnummern.getSelectedIndex();
        return selectedIdx
                    == 0;
    }

    @Override
    public boolean isLastSelected() {
        final int selectedIdx = lstBildnummern.getSelectedIndex();
        return (selectedIdx == (lstBildnummern.getModel().getSize() - 1))
                    && (selectedIdx != -1);
    }

    @Override
    public RestrictionLevel getRestrictionLevel() {
        return restrictedLevel;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void previewImageChanged() {
        lstBildnummern.repaint();
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    @Override
    public boolean prepareForSave() {
        try {
            cidsBean.setProperty("tmp_restriction_level", null);
            return true;
        } catch (Exception ex) {
            LOG.warn(ex, ex);
            return false;
        }
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
    class DigitizedSetterPropertyChangeListener implements PropertyChangeListener {

        //~ Instance fields ----------------------------------------------------

        private boolean activated = true;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isActivated() {
            return activated;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  activated  DOCUMENT ME!
         */
        public void setActivated(final boolean activated) {
            this.activated = activated;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (activated) {
                try {
                    cidsBean.setProperty("geom_aus", geomFromDigitizedAction);
                } catch (Exception ex) {
                    throw new RuntimeException("Having problems setting the geom origin", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class StadtbildserieReportParameterGenerator
            implements JasperReportDownload.JasperReportParametersGenerator {

        //~ Methods ------------------------------------------------------------

        @Override
        public Map generateParamters() {
            final HashMap params = new HashMap();

            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            final Icon icon = previewImage.getIcon();
                            if (icon instanceof ImageIcon) {
                                params.put("image", ((ImageIcon)icon).getImage());
                            } else {
                                params.put("image", StadtbilderUtils.ERROR_IMAGE);
                            }

                            params.put("bildnummer", lstBildnummern.getSelectedValue());

                            final int size = ((JXList)lstBildnummern).getElementCount();
                            params.put("isSerie", size > 1);
                            if (size > 1) {
                                params.put("serieAnfang", ((JXList)lstBildnummern).getElementAt(0));
                                params.put("serieEnde", ((JXList)lstBildnummern).getElementAt(size - 1));
                            }
                        }
                    });
            } catch (InterruptedException ex) {
                LOG.error(ex, ex);
            } catch (InvocationTargetException ex) {
                LOG.error(ex, ex);
            }

            Image vorschaubild = StadtbilderUtils.ERROR_IMAGE;
            try {
                final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                        cidsBean,
                        (CidsBean)cidsBean.getProperty("vorschaubild"));
                vorschaubild = StadtbilderUtils.downloadImageForBildnummer(stadtbildInfo);
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(ex, ex);
                }
            }
            params.put("vorschaubild", vorschaubild);

            final Collection suchwoerter = cidsBean.getBeanCollectionProperty("suchwort_arr");
            params.put("suchwoerter", StringUtils.join(suchwoerter, ", "));

            return params;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class Sb_stadtbildserieInitializer extends DefaultBeanInitializer implements BeanInitializerForcePaste {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Sb_stadtbildserieInitializer object.
         *
         * @param  template           DOCUMENT ME!
         * @param  connectionContext  DOCUMENT ME!
         */
        public Sb_stadtbildserieInitializer(final CidsBean template, final ConnectionContext connectionContext) {
            super(template, connectionContext);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected void processSimpleProperty(final CidsBean beanToInit,
                final String propertyName,
                final Object simpleValueToProcess) throws Exception {
            if (propertyName.equalsIgnoreCase("pruefen") || propertyName.equalsIgnoreCase("pruefen_kommentar")
                        || propertyName.equalsIgnoreCase("pruefhinweis_von")) {
                return;
            }
            super.processSimpleProperty(beanToInit, propertyName, simpleValueToProcess);
        }

        @Override
        protected void processArrayProperty(final CidsBean beanToInit,
                final String propertyName,
                final Collection<CidsBean> arrayValueToProcess) throws Exception {
            if (propertyName.equals("stadtbilder_arr")) {
                return;
            }

            final List<CidsBean> beans = CidsBeanSupport.getBeanCollectionFromProperty(
                    beanToInit,
                    propertyName);
            beans.clear();

            for (final CidsBean tmp : arrayValueToProcess) {
                beans.add(tmp);
            }
        }

        @Override
        protected void processComplexProperty(final CidsBean beanToInit,
                final String propertyName,
                final CidsBean complexValueToProcess) throws Exception {
            if (propertyName.equals("vorschaubild")) {
                return;
            } else if (complexValueToProcess.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                            GEOM_TABLE_NAME)) {
                final CidsBean geomBean = complexValueToProcess.getMetaObject()
                            .getMetaClass()
                            .getEmptyInstance(getConnectionContext())
                            .getBean();
                geomBean.setProperty(GEOM_FIELD_NAME, complexValueToProcess.getProperty(GEOM_FIELD_NAME));
                beanToInit.setProperty(propertyName, geomBean);
            } else {
                // flat copy
                beanToInit.setProperty(propertyName, complexValueToProcess);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class SavePruefhinweisWorker extends SwingWorker<Void, Void> {

        //~ Instance fields ----------------------------------------------------

        ServerActionParameter paramComment;
        ServerActionParameter paramSBSid;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SavePruefhinweisWorker object.
         *
         * @param  paramComment  DOCUMENT ME!
         * @param  paramSBSid    DOCUMENT ME!
         */
        public SavePruefhinweisWorker(final ServerActionParameter paramComment,
                final ServerActionParameter paramSBSid) {
            this.paramComment = paramComment;
            this.paramSBSid = paramSBSid;
            lblBusyPruef.setBusy(true);
            chbPruefen.setEnabled(false);
            btnSavePruefhinweis.setEnabled(false);
            txtaPruefhinweis.setEnabled(false);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            SessionManager.getProxy()
                    .executeTask(
                        Sb_stadtbildserieUpdatePruefhinweisAction.TASK_NAME,
                        "WUNDA_BLAU",
                        (Object)null,
                        getConnectionContext(),
                        paramComment,
                        paramSBSid);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                final String username = SessionManager.getSession().getUser().toString();
                lblPruefhinweisVon.setText(username);

                final TreeNode selectedNode = ComponentRegistry.getRegistry().getCatalogueTree().getSelectedNode();
                if (selectedNode != null) {
                    ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree().getModel()).nodeChanged(
                        selectedNode);
                }
            } catch (InterruptedException ex) {
                exceptionHandling(ex);
            } catch (ExecutionException ex) {
                exceptionHandling(ex);
            } catch (Exception ex) {
                exceptionHandling(ex);
            } finally {
                lblBusyPruef.setBusy(false);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  ex  DOCUMENT ME!
         */
        private void exceptionHandling(final Exception ex) {
            LOG.error("Problem while updating the Pruefhinweis", ex);
            final ErrorInfo ei = new ErrorInfo(
                    "Fehler",
                    "Beim Speichern des Prüfhinweises ist ein Fehler aufgetreten",
                    null,
                    null,
                    ex,
                    Level.WARNING,
                    null);
            JXErrorPane.showDialog(StaticSwingTools.getParentFrame(Sb_stadtbildserieEditor.this), ei);
            btnSavePruefhinweis.setEnabled(true);
            txtaPruefhinweis.setEnabled(true);
        }
    }

    /**
     * The JXList from SwingX 1.6 contains bugs in the methods JXList.getSelectedValue(), JXList.getSelectedValues() and
     * JXList.setSelectedValue(). See also:
     * https://java.net/jira/browse/SWINGX-1263?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aall-tabpanel
     * Therefor the method were copied from SwingX 1.6.5-1.
     *
     * @version  $Revision$, $Date$
     */
    public static class JXListBugFixes extends JXList {

        //~ Methods ------------------------------------------------------------

        /**
         * Returns the value for the smallest selected cell index; <i>the selected value</i> when only a single item is
         * selected in the list. When multiple items are selected, it is simply the value for the smallest selected
         * index. Returns {@code null} if there is no selection.
         *
         * <p>This is a convenience method that simply returns the model value for {@code getMinSelectionIndex}, taking
         * into account sorting and filtering.</p>
         *
         * @return  the first selected value
         *
         * @see     #getMinSelectionIndex
         * @see     #getModel
         * @see     #addListSelectionListener
         */
        @Override
        public Object getSelectedValue() {
            final int i = getSelectedIndex();
            return (i == -1) ? null : getElementAt(i);
        }

        /**
         * Selects the specified object from the list, taking into account sorting and filtering.
         *
         * @param  anObject      the object to select
         * @param  shouldScroll  {@code true} if the list should scroll to display the selected object, if one exists;
         *                       otherwise {@code false}
         */
        @Override
        public void setSelectedValue(final Object anObject, final boolean shouldScroll) {
            // Note: this method is a copy of JList.setSelectedValue,
            // including comments. It simply usues getElementCount() and getElementAt()
            // instead of the model.
            if (anObject == null) {
                setSelectedIndex(-1);
            } else if (!anObject.equals(getSelectedValue())) {
                int i;
                int c;
                for (i = 0, c = getElementCount(); i < c; i++) {
                    if (anObject.equals(getElementAt(i))) {
                        setSelectedIndex(i);
                        if (shouldScroll) {
                            ensureIndexIsVisible(i);
                        }
                        repaint();
                        /**
                         * FIX-ME setSelectedIndex does not redraw all the time
                         * with the basic l&f*
                         */
                        return;
                    }
                }
                setSelectedIndex(-1);
            }
            repaint();
            /**
             * FIX-ME setSelectedIndex does not redraw all the time with the
             * basic l&f*
             */
        }

        /**
         * Returns an array of all the selected values, in increasing order based on their indices in the list and
         * taking into account sourting and filtering.
         *
         * @return  the selected values, or an empty array if nothing is selected
         *
         * @see     #isSelectedIndex
         * @see     #getModel
         * @see     #addListSelectionListener
         */
        @Override
        public Object[] getSelectedValues() {
            final int[] selectedIndexes = getSelectedIndices();
            final Object[] selectedValues = new Object[selectedIndexes.length];
            for (int i = 0; i < selectedIndexes.length; i++) {
                selectedValues[i] = getElementAt(selectedIndexes[i]);
            }
            return selectedValues;
        }
    }
}
