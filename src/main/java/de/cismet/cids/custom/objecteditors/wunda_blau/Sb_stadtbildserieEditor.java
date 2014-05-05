/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import com.sun.jersey.api.client.UniformInterfaceException;

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

import org.openide.util.Exceptions;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.Sb_stadtbildUtils;
import de.cismet.cids.custom.utils.TifferDownload;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.wunda_blau.search.actions.Sb_stadtbildserieUpdatePruefhinweisAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableJCheckBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

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

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor.adjustScale;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieEditor extends JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final ImageIcon FOLDER_ICON = new ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inode-directory.png"));

    private static final ImageIcon ERROR_ICON = new ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/file-broken.png"));

    private static final ImageIcon TICK = new javax.swing.ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/wunda_blau/res/tick_32.png"));
    private static final ImageIcon TICK_BW = new javax.swing.ImageIcon(Sb_stadtbildserieEditor.class.getResource(
                "/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"));

    private static final String REPORT_STADTBILDSERIE_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/StadtbildserieA4Q.jasper";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Sb_stadtbildserieEditor.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private boolean rendererAndInternalUsage = true;
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
                    LOG.fatal(ex);
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
                    LOG.fatal(ex);
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
                LOG.fatal(".convertReverse: Not supported yet.", new Exception()); // NOI18N
                return null;
            }
        };

    private final PropertyChangeListener listRepaintListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                lstBildnummern.repaint();
            }
        };

    private CidsBean fotoCidsBean;

    private BufferedImage image;
    private boolean resizeListenerEnabled;
    private final Timer timer;
    private Sb_stadtbildserieEditor.ImageResizeWorker currentResizeWorker;
    private MappingComponent map;
    private boolean editable;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.FastBindableReferenceCombo bcbStrasse;
    private javax.swing.JButton btnAddImageNumber;
    private javax.swing.JButton btnAddSuchwort;
    private javax.swing.JButton btnCombineGeometries;
    private javax.swing.JButton btnDownloadHighResImage;
    private javax.swing.JButton btnNextImg;
    private javax.swing.JButton btnPrevImg;
    private javax.swing.JButton btnRemoveImageNumber;
    private javax.swing.JButton btnRemoveSuchwort;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSavePruefhinweis;
    private javax.swing.JCheckBox chbIntern;
    private javax.swing.JCheckBox chbPruefen;
    private javax.swing.JComboBox dbcAuftraggeber;
    private javax.swing.JComboBox dbcBildtyp;
    private javax.swing.JComboBox dbcFilmart;
    private javax.swing.JComboBox dbcFotograf;
    private de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor dbcGeom;
    private javax.swing.JComboBox dbcLager;
    private javax.swing.JComboBox dbcOrt;
    private org.jdesktop.swingx.JXDatePicker dpAufnahmedatum;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
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
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private org.jdesktop.swingx.JXBusyLabel lblBusyPruef;
    private javax.swing.JLabel lblDescAufnahmedatum;
    private javax.swing.JLabel lblDescAuftraggeber;
    private javax.swing.JLabel lblDescBildnummer;
    private javax.swing.JLabel lblDescBildtyp;
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
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblPruefhinweisVon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVorschau;
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
    private javax.swing.JPanel pnlCtrlBtn;
    private javax.swing.JPanel pnlCtrlButtons;
    private javax.swing.JPanel pnlCtrlButtons1;
    private javax.swing.JPanel pnlFoto;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel pnlVorschau;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    private de.cismet.tools.gui.RoundedPanel roundedPanel2;
    private de.cismet.tools.gui.RoundedPanel roundedPanel3;
    private de.cismet.tools.gui.RoundedPanel roundedPanel4;
    private de.cismet.tools.gui.RoundedPanel roundedPanel6;
    private de.cismet.tools.gui.RoundedPanel roundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel8;
    private de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter sqlDateToStringConverter;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JToggleButton tbtnIsPreviewImage;
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
        initComponents();
        final java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        if (!editable) { // renderer
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        } else {
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 2;
        }
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblGeomAus, gridBagConstraints);

        makeEditable();
        jScrollPane5.getViewport().setOpaque(false);
        title = "";
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            btnReport,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
        map = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(map, BorderLayout.CENTER);

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new Sb_stadtbildserieEditor.ImageResizeWorker();
                            currentResizeWorker.execute();
                        }
                    }
                });
        timer.setRepeats(false);

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

    //~ Methods ----------------------------------------------------------------

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
            RendererTools.makeReadOnly(dbcLager);
            RendererTools.makeTextBlackOfDisabledComboBox(dbcLager);
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
            RendererTools.makeReadOnly(tbtnIsPreviewImage);
            RendererTools.makeReadOnly(chbIntern);
        } else {
            ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
                tbtnIsPreviewImage,
                Cursor.HAND_CURSOR,
                Cursor.DEFAULT_CURSOR);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void decorateComboBoxes() {
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(bcbStrasse);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcAuftraggeber);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcBildtyp);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcFilmart);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcFotograf);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(dbcLager);
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
        lblTitle = new javax.swing.JLabel();
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
        chbIntern = new DefaultBindableJCheckBox();
        roundedPanel3 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        panDetails = new javax.swing.JPanel();
        lblDescFilmart = new javax.swing.JLabel();
        lblDescFotograf = new javax.swing.JLabel();
        lblDescAuftraggeber = new javax.swing.JLabel();
        dbcAuftraggeber = new FastBindableReferenceCombo();
        dbcFotograf = new FastBindableReferenceCombo();
        dbcFilmart = new FastBindableReferenceCombo();
        roundedPanel4 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        panDetails1 = new javax.swing.JPanel();
        if (editable) {
            lblDescGeometrie = new javax.swing.JLabel();
        }
        lblDescOrt = new javax.swing.JLabel();
        lblDescStrasse = new javax.swing.JLabel();
        dbcOrt = new FastBindableReferenceCombo();
        lblHausnummer = new javax.swing.JLabel();
        txtHausnummer = new de.cismet.cids.editors.DefaultBindableJTextField();
        if (editable) {
            btnCombineGeometries = new javax.swing.JButton();
        }
        if (editable) {
            dbcGeom = new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
        }
        bcbStrasse = new FastBindableReferenceCombo();
        jPanel2 = new javax.swing.JPanel();
        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        pnlFoto = new javax.swing.JPanel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        jPanel4 = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        pnlCtrlBtn = new javax.swing.JPanel();
        btnDownloadHighResImage = new EnableOnlyIfNotInternalUsageAndNotRendererJButton();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        tbtnIsPreviewImage = new javax.swing.JToggleButton();
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
        lblBusyPruef = new org.jdesktop.swingx.JXBusyLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitleString.add(lblTitle, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        panPrintButton.setOpaque(false);
        panPrintButton.setLayout(new java.awt.GridBagLayout());

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        btnReport.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnReport.text"));                                       // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnReport.toolTipText"));                                // NOI18N
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
                "MauerEditor.btnAddImg.text"));                                                                // NOI18N
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
                "MauerEditor.btnAddImg.text"));                                                                // NOI18N
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescLagerort, gridBagConstraints);

        lblDescAufnahmedatum.setText("Aufnahmedatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescAufnahmedatum, gridBagConstraints);

        lblDescInfo.setText("Kommentar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panContent.add(lblDescInfo, gridBagConstraints);

        lblDescBildtyp.setText("Bildtyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
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
        gridBagConstraints.gridy = 4;
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
        gridBagConstraints.gridy = 6;
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

        chbIntern.setText("nicht zur Publikation freigegeben");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.interner_gebrauch}"),
                chbIntern,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        binding.setConverter(((DefaultBindableJCheckBox)chbIntern).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(chbIntern, gridBagConstraints);

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

        ((FastBindableReferenceCombo)dbcAuftraggeber).setSorted(true);
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

        pnlVorschau.setPreferredSize(new java.awt.Dimension(140, 300));
        pnlVorschau.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        lblVorschau.setForeground(new java.awt.Color(255, 255, 255));
        lblVorschau.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.CardLayout());

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 60));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 60));
        pnlFoto.add(lblBusy, "busy");

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lblPicture.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.lblPicture.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(lblPicture, gridBagConstraints);

        pnlFoto.add(jPanel4, "image");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVorschau.add(pnlFoto, gridBagConstraints);

        pnlCtrlBtn.setOpaque(false);
        pnlCtrlBtn.setPreferredSize(new java.awt.Dimension(100, 50));
        pnlCtrlBtn.setLayout(new java.awt.GridBagLayout());

        btnDownloadHighResImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/tools/gui/downloadmanager/res/download.png"))); // NOI18N
        btnDownloadHighResImage.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnPrevImg.text"));                                                   // NOI18N
        btnDownloadHighResImage.setBorder(null);
        btnDownloadHighResImage.setBorderPainted(false);
        btnDownloadHighResImage.setContentAreaFilled(false);
        btnDownloadHighResImage.setEnabled(false);
        btnDownloadHighResImage.setFocusPainted(false);
        btnDownloadHighResImage.setMaximumSize(new java.awt.Dimension(30, 30));
        btnDownloadHighResImage.setMinimumSize(new java.awt.Dimension(30, 30));
        btnDownloadHighResImage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDownloadHighResImageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlCtrlBtn.add(btnDownloadHighResImage, gridBagConstraints);

        btnPrevImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png")));          // NOI18N
        btnPrevImg.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnPrevImg.text"));                                                           // NOI18N
        btnPrevImg.setBorder(null);
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setContentAreaFilled(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.setMaximumSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setMinimumSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setPreferredSize(new java.awt.Dimension(30, 30));
        btnPrevImg.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-pressed.png")));  // NOI18N
        btnPrevImg.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-selected.png"))); // NOI18N
        btnPrevImg.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-selected.png"))); // NOI18N
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png")));          // NOI18N
        btnNextImg.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnNextImg.text"));                                                            // NOI18N
        btnNextImg.setBorder(null);
        btnNextImg.setBorderPainted(false);
        btnNextImg.setContentAreaFilled(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.setMaximumSize(new java.awt.Dimension(30, 30));
        btnNextImg.setMinimumSize(new java.awt.Dimension(30, 30));
        btnNextImg.setPreferredSize(new java.awt.Dimension(30, 30));
        btnNextImg.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-pressed.png")));  // NOI18N
        btnNextImg.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-selected.png"))); // NOI18N
        btnNextImg.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right-selected.png"))); // NOI18N
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlCtrlBtn.add(btnNextImg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCtrlBtn.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCtrlBtn.add(filler2, gridBagConstraints);

        tbtnIsPreviewImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"))); // NOI18N
        tbtnIsPreviewImage.setToolTipText("Als Vorschaubild setzen");
        tbtnIsPreviewImage.setBorderPainted(false);
        tbtnIsPreviewImage.setContentAreaFilled(false);
        tbtnIsPreviewImage.setDisabledIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32bw.png"))); // NOI18N
        tbtnIsPreviewImage.setDisabledSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png")));   // NOI18N
        tbtnIsPreviewImage.setEnabled(false);
        tbtnIsPreviewImage.setMaximumSize(new java.awt.Dimension(30, 30));
        tbtnIsPreviewImage.setMinimumSize(new java.awt.Dimension(30, 30));
        tbtnIsPreviewImage.setPreferredSize(new java.awt.Dimension(32, 32));
        tbtnIsPreviewImage.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png")));   // NOI18N
        tbtnIsPreviewImage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnIsPreviewImageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCtrlBtn.add(tbtnIsPreviewImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlVorschau.add(pnlCtrlBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(pnlVorschau, gridBagConstraints);

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
    private void btnPrevImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPrevImgActionPerformed
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() - 1);
    }                                                                              //GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNextImgActionPerformed
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() + 1);
    }                                                                              //GEN-LAST:event_btnNextImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDownloadHighResImageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDownloadHighResImageActionPerformed
        if (DownloadManagerDialog.showAskingForUserTitle(
                        this)) {
            final String jobname = DownloadManagerDialog.getJobname();
            final String imageNumber = (String)((CidsBean)lstBildnummern.getSelectedValue()).getProperty("bildnummer");
            DownloadManager.instance()
                    .add(
                        new TifferDownload(
                            jobname,
                            "Stadtbild "
                            + imageNumber,
                            "stadtbild_"
                            + imageNumber,
                            lstBildnummern.getSelectedValue().toString(),
                            "1"));
        }
    }                                                                                           //GEN-LAST:event_btnDownloadHighResImageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstBildnummernValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstBildnummernValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (!lstBildnummern.isSelectionEmpty()) {
                final String imageNumber = lstBildnummern.getSelectedValue().toString();
                new CheckAccessibilityOfHighResImage(imageNumber).execute();
                loadFoto();
                final CidsBean oldPreviewImage = (CidsBean)cidsBean.getProperty("vorschaubild");
                final boolean isPreviewImage = (oldPreviewImage != null)
                            && oldPreviewImage.equals(lstBildnummern.getSelectedValue());
                tbtnIsPreviewImage.setSelected(isPreviewImage);
                tbtnIsPreviewImage.setEnabled(editable && !isPreviewImage);
                lstBildnummern.ensureIndexIsVisible(lstBildnummern.getSelectedIndex());
            } else {
                tbtnIsPreviewImage.setSelected(false);
                tbtnIsPreviewImage.setEnabled(false);
            }
        }
    }                                                                                         //GEN-LAST:event_lstBildnummernValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddImageNumberActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddImageNumberActionPerformed
        final Sb_stadtbildserieEditorAddBildnummerDialog dialog = new Sb_stadtbildserieEditorAddBildnummerDialog((Frame)
                SwingUtilities.getWindowAncestor(this),
                true);
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
        defineButtonStatus();
    }                                                                                     //GEN-LAST:event_btnAddImageNumberActionPerformed

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
                    image = null;
                    lblPicture.setIcon(FOLDER_ICON);
                }

                try {
                    final List<CidsBean> fotos = cidsBean.getBeanCollectionProperty("stadtbilder_arr");
                    if (fotos != null) {
                        fotos.remove(cidesBeanToRemove);
                    }
                    Sb_stadtbildUtils.removeFromImageCache(cidesBeanToRemove);
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
            defineButtonStatus();
        }
    } //GEN-LAST:event_btnRemoveImageNumberActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddSuchwortActionPerformed
        final Sb_stadtbildserieEditorAddSuchwortDialog dialog = new Sb_stadtbildserieEditorAddSuchwortDialog((Frame)
                SwingUtilities.getWindowAncestor(this),
                true);
        final CidsBean newSuchwort = dialog.showDialog();
        if (newSuchwort != null) {
            final List<CidsBean> suchwoerter = cidsBean.getBeanCollectionProperty("suchwort_arr");
            if (!suchwoerter.contains(newSuchwort)) {
                suchwoerter.add(newSuchwort);
            }
        }
    }                                                                                  //GEN-LAST:event_btnAddSuchwortActionPerformed

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
    private void tbtnIsPreviewImageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnIsPreviewImageActionPerformed
        if (tbtnIsPreviewImage.isSelected()) {
            try {
                cidsBean.setProperty("vorschaubild", lstBildnummern.getSelectedValue());
                tbtnIsPreviewImage.setEnabled(false);
            } catch (Exception e) {
                LOG.error("Error while setting the preview image of the CidsBean", e);
            }
        }
    }                                                                                      //GEN-LAST:event_tbtnIsPreviewImageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dbcOrtItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_dbcOrtItemStateChanged
        final Object selectedItem = dbcOrt.getSelectedItem();
        if (editable) {
            if ((selectedItem != null) && selectedItem.equals(Sb_stadtbildUtils.getWUPPERTAL())) {
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

        if (DownloadManagerDialog.showAskingForUserTitle(ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getJobname();
            final String vorschaubildnummer = (String)cidsBean.getProperty("vorschaubild.bildnummer");
            final String filename = "stadbildserie_" + vorschaubildnummer;
            final String downloadTitle = "Stadbildserie " + vorschaubildnummer;
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
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean);
            initMap();

            final boolean internalUsage = Boolean.TRUE.equals((Boolean)cidsBean.getProperty("interner_gebrauch"));
            rendererAndInternalUsage = !editable && internalUsage;

            bindingGroup.bind();
            if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                setDefaultValuesForNewCidsBean();
            }
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
        }
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
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
        this.title = "Stadtbildserie " + title;
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
     */
    private void loadFoto() {
        if (rendererAndInternalUsage) {
            indicateInternalUsage();
        } else {
            final Object stadtbild = lstBildnummern.getSelectedValue();
            if (fotoCidsBean != null) {
                fotoCidsBean.removePropertyChangeListener(listRepaintListener);
            }
            if (stadtbild instanceof CidsBean) {
                fotoCidsBean = (CidsBean)stadtbild;
                fotoCidsBean.addPropertyChangeListener(listRepaintListener);
                final String bildnummer = (String)fotoCidsBean.getProperty("bildnummer");
                if (bildnummer != null) {
                    new Sb_stadtbildserieEditor.LoadSelectedImageWorker(bildnummer).execute();
                }
            } else {
                image = null;
                lblPicture.setIcon(FOLDER_ICON);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        final int selectedIdx = lstBildnummern.getSelectedIndex();
        btnPrevImg.setEnabled(selectedIdx > 0);
        btnNextImg.setEnabled((selectedIdx < (lstBildnummern.getModel().getSize() - 1)) && (selectedIdx > -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (wait) {
            if (!lblBusy.isBusy()) {
                ((CardLayout)pnlFoto.getLayout()).show(pnlFoto, "busy");
//                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                btnPrevImg.setEnabled(false);
                btnNextImg.setEnabled(false);
            }
        } else {
            ((CardLayout)pnlFoto.getLayout()).show(pnlFoto, "image");
            lblBusy.setBusy(false);
            defineButtonStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Fehler beim Übertragen des Bildes!");
        lblPicture.setToolTipText(tooltip);
        showWait(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateNotAvailable(final String tooltip) {
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Kein Vorschaubild vorhanden.");
        lblPicture.setToolTipText(tooltip);
        showWait(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void indicateInternalUsage() {
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Bild ist nicht zur Publikation freigegeben!");
        lblPicture.setToolTipText("");
        showWait(false);
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
                        AlkisConstants.COMMONS.SRS_SERVICE);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ALKISConstatns.Commons.GeoBUffer: " + AlkisConstants.COMMONS.GEO_BUFFER);
                }
                final XBoundingBox box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                            AlkisConstants.COMMONS.GEO_BUFFER));
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
                            mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                            mappingModel.addHome(new XBoundingBox(
                                    bufferedBox.getX1(),
                                    bufferedBox.getY1(),
                                    bufferedBox.getX2(),
                                    bufferedBox.getY2(),
                                    AlkisConstants.COMMONS.SRS_SERVICE,
                                    true));
                            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                        AlkisConstants.COMMONS.MAP_CALL_STRING));
                            swms.setName("Stadtbildserie");
                            final StyledFeature dsf = new DefaultStyledFeature();
                            dsf.setGeometry(pureGeom);
                            dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));
                            dsf.setLineWidth(3);
                            dsf.setLinePaint(new Color(1, 0, 0, 1f));
                            // add the raster layer to the model
                            mappingModel.addLayer(swms);
                            // set the model
                            map.setMappingModel(mappingModel);
                            // initial positioning of the map
                            final int duration = map.getAnimationDuration();
                            map.setAnimationDuration(0);
                            map.gotoInitialBoundingBox();
                            // interaction mode
                            map.setInteractionMode(MappingComponent.ZOOM);
                            // finally when all configurations are done ...
                            map.unlock();
                            map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                                    @Override
                                    public void mouseClicked(final PInputEvent evt) {
                                        if (evt.getClickCount() > 1) {
                                            final CidsBean bean = cidsBean;
                                            ObjectRendererUtils.switchToCismapMap();
                                            ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                        }
                                    }
                                });
                            map.setInteractionMode("MUTE");
                            map.getFeatureCollection().addFeature(dsf);
                            map.setAnimationDuration(duration);
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
    private void setDefaultValuesForNewCidsBean() {
        try {
            cidsBean.setProperty("ort", Sb_stadtbildUtils.getWUPPERTAL());
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            cidsBean.setProperty("lager", Sb_stadtbildUtils.getR102());
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        dpAufnahmedatum.setDate(new Date());
        if (dbcBildtyp.getItemCount() > 0) {
            dbcBildtyp.setSelectedIndex(0);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

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
                            final Icon icon = lblPicture.getIcon();
                            if (icon instanceof ImageIcon) {
                                params.put("image", ((ImageIcon)icon).getImage());
                            } else {
                                params.put("image", ERROR_ICON.getImage());
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

            Image vorschaubild = ERROR_ICON.getImage();
            try {
                vorschaubild = Sb_stadtbildUtils.downloadImageForBildnummer((String)cidsBean.getProperty(
                            "vorschaubild.bildnummer"));
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
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
            if (image != null) {
                lblPicture.setText("Wird neu skaliert...");
            }
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
                final ImageIcon result = new ImageIcon(adjustScale(image, pnlFoto, 20, 20));
                return result;
            } else {
                return null;
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    lblPicture.setIcon(result);
                    lblPicture.setText("");
                    lblPicture.setToolTipText(null);
                } catch (InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (ExecutionException ex) {
                    LOG.error(ex, ex);
                    lblPicture.setText("Fehler beim Skalieren!");
                } finally {
                    showWait(false);
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String bildnummer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  toLoad  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final String toLoad) {
            this.bildnummer = toLoad;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((bildnummer != null) && (bildnummer.length() > 0)) {
                return Sb_stadtbildUtils.downloadImageForBildnummer(bildnummer);
            }
            return null;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    resizeListenerEnabled = true;
                    timer.restart();
                } else {
                    indicateNotAvailable("");
                }
            } catch (InterruptedException ex) {
                image = null;
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                LOG.error(ex, ex);
                if (ex.getCause() instanceof UniformInterfaceException) {
                    indicateNotAvailable("");
                } else {
                    indicateError(ex.getMessage());
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class CheckAccessibilityOfHighResImage extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String imageNumber;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CheckAccessibilityOfHighResImage object.
         *
         * @param  imageNumber  DOCUMENT ME!
         */
        public CheckAccessibilityOfHighResImage(final String imageNumber) {
            this.imageNumber = imageNumber;
            btnDownloadHighResImage.setEnabled(false);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Boolean doInBackground() throws Exception {
            if (rendererAndInternalUsage) {
                return false;
            } else {
                return Sb_stadtbildUtils.getFormatOfHighResPicture(imageNumber) != null;
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final boolean accessible = get();
                btnDownloadHighResImage.setEnabled(accessible);
                if (accessible) {
                    ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
                        btnDownloadHighResImage,
                        Cursor.HAND_CURSOR,
                        Cursor.DEFAULT_CURSOR);
                }
            } catch (InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                LOG.warn(ex, ex);
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
                        null,
                        paramComment,
                        paramSBSid);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                throw new InterruptedException();
            } catch (InterruptedException ex) {
                exceptionHandling(ex);
            } catch (ExecutionException ex) {
                exceptionHandling(ex);
            } finally {
                lblBusyPruef.setBusy(false);
            }
            final String username = SessionManager.getSession().getUser().toString();
            lblPruefhinweisVon.setText(username);

            try {
                final RootTreeNode rootTreeNode = new RootTreeNode(SessionManager.getProxy().getRoots());
                ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree().getModel()).setRoot(rootTreeNode);
                ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree().getModel()).reload();
            } catch (ConnectionException ex) {
                LOG.error("Problem while reloading the catalogue", ex);
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
                    "Beim Speicher des Prüfhinweises ist ein Fehler aufgetreten",
                    null,
                    null,
                    ex,
                    Level.WARNING,
                    null);
            JXErrorPane.showDialog(StaticSwingTools.getParentFrame(Sb_stadtbildserieEditor.this), ei);
            chbPruefen.setEnabled(true);
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
         * @param  shouldScroll {@code true} if the list should scroll to display the selected object, if one exists;
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
                        repaint(); /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
                        return;
                    }
                }
                setSelectedIndex(-1);
            }
            repaint();             /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
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

    /**
     * A JButton which gets only enabled if the shown Stadtbildserie is not in the renderer and not for internal usage
     * only.
     *
     * @version  $Revision$, $Date$
     */
    private class EnableOnlyIfNotInternalUsageAndNotRendererJButton extends JButton {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setEnabled(final boolean enable) {
            super.setEnabled(enable && !rendererAndInternalUsage);
        }
    }
}
