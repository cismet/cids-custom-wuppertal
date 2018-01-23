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

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.deprecated.TabbedPaneUITransparent;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.reports.wunda_blau.TreppenReportGenerator;
import de.cismet.cids.custom.utils.alkisconstants.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.netutil.Proxy;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final ImageIcon ERROR_ICON = new ImageIcon(TreppeEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/file-broken.png"));
    private static final Logger log = Logger.getLogger(TreppeEditor.class);
    private static final String WEB_DAV_DIRECTORY;
    private static final String WEB_DAV_USER;
    private static final String WEB_DAV_PASSWORD;
    private static final ImageIcon FOLDER_ICON = new ImageIcon(TreppeEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inode-directory.png"));
    private static final Logger LOG = Logger.getLogger(TreppeEditor.class);
    private static final Pattern IMAGE_FILE_PATTERN = Pattern.compile(
            ".*\\.(bmp|png|jpg|jpeg|tif|tiff|wbmp)$",
            Pattern.CASE_INSENSITIVE);
    private static final Color ROT = new Color(255, 0, 60);
    private static final Color GELB = new Color(250, 190, 40);
    private static final Color GRUEN = new Color(0, 193, 118);

    static {
        String pass = null;
        String user = null;
        String directory = null;
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
            pass = bundle.getString("password");

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }
            user = bundle.getString("user");
            directory = bundle.getString("url");
        } catch (final Exception ex) {
        }
        WEB_DAV_PASSWORD = pass;
        WEB_DAV_USER = user;
        WEB_DAV_DIRECTORY = directory;
    }

    private static final int CACHE_SIZE = 20;
    private static final Map<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new LinkedHashMap<String, SoftReference<BufferedImage>>(CACHE_SIZE) {

            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, SoftReference<BufferedImage>> eldest) {
                return size() >= CACHE_SIZE;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final Timer timer;
    private ImageResizeWorker currentResizeWorker;
    private boolean resizeListenerEnabled;
    private final boolean editable;
    private CidsBean cidsBean;
    private final MappingComponent map = new MappingComponent();
    private final ZustandOverview overview = new ZustandOverview();
    private final WebDavHelper webDavHelper;
    private boolean listListenerEnabled = true;
    private final JFileChooser fileChooser;
    private final List<CidsBean> removeNewAddedFotoBean = new ArrayList<CidsBean>();
    private final List<CidsBean> removedFotoBeans = new ArrayList<CidsBean>();
    private BufferedImage image;
    private CidsBean fotoCidsBean;
    private final PropertyChangeListener listRepaintListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddImg;
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnNextImg;
    private javax.swing.JButton btnPrevImg;
    private javax.swing.JButton btnRemoveImg;
    private javax.swing.JButton btnReport;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser2;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser3;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser4;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser5;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser6;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo2;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo3;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo4;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo5;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo6;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo7;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink6;
    private javax.swing.JScrollPane jspFotoList;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private javax.swing.JLabel lblFiller9;
    private javax.swing.JLabel lblFotos;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblHeaderAllgemein1;
    private javax.swing.JLabel lblHeaderAllgemein7;
    private javax.swing.JLabel lblHeaderFotos;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JList lstFotos;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private de.cismet.tools.gui.RoundedPanel panAllgemein6;
    private javax.swing.JPanel panBeschreibungContent;
    private javax.swing.JPanel panBeschreibungContent6;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle6;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung;
    private javax.swing.JPanel panZusammenfassungContent;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private javax.swing.JPanel pnlCtrlBtn;
    private javax.swing.JPanel pnlCtrlButtons;
    private javax.swing.JPanel pnlFoto;
    private de.cismet.tools.gui.RoundedPanel pnlFotos;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderFotos;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel pnlVorschau;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel1;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel2;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel3;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel4;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel5;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel6;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBauteilZustandKostenPanel
        treppeBauteilZustandKostenPanel5;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaeufePanel treppeHandlaeufePanel2;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaeufePanel treppeLaeufePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementePanel treppeLeitelementePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestePanel treppePodestePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauernPanel treppeStuetzmauernPanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeEditor object.
     */
    public TreppeEditor() {
        this(true);
    }

    /**
     * Creates new form TreppenEditor.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppeEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        try {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE"));
        } catch (final Exception ex) {
        }

        webDavHelper = new WebDavHelper(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD, false);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.isDirectory() || IMAGE_FILE_PATTERN.matcher(f.getName()).matches();
                }

                @Override
                public String getDescription() {
                    return "Bilddateien";
                }
            });
        fileChooser.setMultiSelectionEnabled(true);
        listRepaintListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    lstFotos.repaint();
                }
            };

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
//                    if (isShowing()) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
//                    } else {
//                        timer.restart();
//                    }
                        }
                    }
                });
        timer.setRepeats(false);
        if (!editable) {
            RendererTools.makeReadOnly(jTextArea6);
            RendererTools.makeReadOnly(jCheckBox1);
            RendererTools.makeReadOnly(jCheckBox2);
            RendererTools.makeReadOnly(jCheckBox3);
            RendererTools.makeReadOnly(jCheckBox4);
            RendererTools.makeReadOnly(jCheckBox5);
            RendererTools.makeReadOnly(jCheckBox6);
            RendererTools.makeReadOnly(jCheckBox7);
            RendererTools.makeReadOnly(jCheckBox8);
            RendererTools.makeReadOnly(jCheckBox9);
            RendererTools.makeReadOnly(jCheckBox10);
            RendererTools.makeReadOnly(jCheckBox11);
            RendererTools.makeReadOnly(jTextField1);
            RendererTools.makeReadOnly(jTextField2);
            RendererTools.makeReadOnly(jTextField3);
            RendererTools.makeReadOnly(jTextField4);
            RendererTools.makeReadOnly(jTextField5);
            RendererTools.makeReadOnly(jTextField6);
            RendererTools.makeReadOnly(jTextField7);
            RendererTools.makeReadOnly(jTextField8);
            RendererTools.makeReadOnly(jTextField9);
            RendererTools.makeReadOnly(jTextField10);
            RendererTools.makeReadOnly(jTextField11);
            RendererTools.makeReadOnly(jTextField12);
            RendererTools.makeReadOnly(jTextField13);
            RendererTools.makeReadOnly(jTextField14);
            RendererTools.makeReadOnly(defaultBindableDateChooser1);
            RendererTools.makeReadOnly(defaultBindableDateChooser2);
            RendererTools.makeReadOnly(defaultBindableDateChooser3);
            RendererTools.makeReadOnly(defaultBindableDateChooser4);
            RendererTools.makeReadOnly(defaultBindableDateChooser5);
            RendererTools.makeReadOnly(defaultBindableDateChooser6);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo2);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo3);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo4);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo5);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo6);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo7);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo8);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser1);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser2);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser3);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser4);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser5);
            RendererTools.jxDatePickerShouldLookLikeLabel(defaultBindableDateChooser6);
            RendererTools.jComboboxShouldLookLikeLabel(defaultBindableReferenceCombo2);
            RendererTools.jComboboxShouldLookLikeLabel(defaultBindableReferenceCombo3);
            RendererTools.jComboboxShouldLookLikeLabel(defaultBindableReferenceCombo4);
        }
        btnAddImg.setVisible(editable);
        btnRemoveImg.setVisible(editable);

        jScrollPane3.getViewport().setOpaque(false);

        jTabbedPane1.setUI(new TabbedPaneUITransparent());
        jTabbedPane1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent e) {
                    if (e.getSource() instanceof JTabbedPane) {
                        final JTabbedPane pane = (JTabbedPane)e.getSource();
                        if (pane.getSelectedIndex() == 0) {
                            overview.recalculateAll();
                        }
                    }
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
//        System.setProperty("cismet.beansdebugging", "true");
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        DevelopmentTools.createEditorInFrameFromRestfulConnection(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "xxx",
            "treppe",
            4,
            1000,
            1000);
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
        jPanel1 = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panBeschreibungContent = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        defaultBindableDateChooser3 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        defaultBindableDateChooser5 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        defaultBindableDateChooser6 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        defaultBindableDateChooser2 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        defaultBindableDateChooser4 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jCheckBox11 = new javax.swing.JCheckBox();
        if (editable) {
            lblGeom = new javax.swing.JLabel();
        }
        cbGeom = new DefaultCismapGeometryComboBoxEditor();
        jLabel84 = new javax.swing.JLabel();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        defaultBindableReferenceCombo2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        defaultBindableReferenceCombo3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel85 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        defaultBindableReferenceCombo5 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        defaultBindableReferenceCombo6 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel52 = new javax.swing.JLabel();
        defaultBindableReferenceCombo7 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel53 = new javax.swing.JLabel();
        defaultBindableReferenceCombo8 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel30 = new javax.swing.JPanel();
        panZusammenfassung = new de.cismet.tools.gui.RoundedPanel();
        panZusammenfassungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        panZusammenfassungContent = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink4 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink6 = new org.jdesktop.swingx.JXHyperlink();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
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
        jPanel2 = new javax.swing.JPanel();
        treppeLaeufePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaeufePanel(editable);
        jPanel3 = new javax.swing.JPanel();
        treppePodestePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestePanel(editable);
        jPanel4 = new javax.swing.JPanel();
        treppeLeitelementePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementePanel(editable);
        jPanel5 = new javax.swing.JPanel();
        treppeHandlaeufePanel2 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaeufePanel(editable);
        jPanel6 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        treppeBauteilZustandKostenPanel5 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBauteilZustandKostenPanel(editable);
        jPanel28 = new javax.swing.JPanel();
        panAllgemein6 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle6 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein7 = new javax.swing.JLabel();
        panBeschreibungContent6 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        defaultBindableReferenceCombo4 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel13 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        treppeStuetzmauernPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauernPanel(editable);
        pnlCard2 = new javax.swing.JPanel();
        pnlFotos = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderFotos = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderFotos = new javax.swing.JLabel();
        lblFiller9 = new javax.swing.JLabel();
        lblFotos = new javax.swing.JLabel();
        jspFotoList = new javax.swing.JScrollPane();
        lstFotos = new javax.swing.JList();
        pnlCtrlButtons = new javax.swing.JPanel();
        btnAddImg = new javax.swing.JButton();
        btnRemoveImg = new javax.swing.JButton();
        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        pnlFoto = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        pnlCtrlBtn = new javax.swing.JPanel();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();
        pnlMap = new javax.swing.JPanel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                   // NOI18N
        lblInfo.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInfo,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblInfo.text")); // NOI18N
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png")));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnInfo,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnInfo.text")); // NOI18N
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

        btnImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnImages,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnImages.text")); // NOI18N
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

        lblImages.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                   // NOI18N
        lblImages.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblImages,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblImages.text")); // NOI18N
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
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png")));                 // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnReport.text")); // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.btnReport.toolTipText"));                                               // NOI18N
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

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panAllgemein.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(new java.awt.Color(51, 51, 51));
        panBeschreibungTitle.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein1,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblHeaderAllgemein1.text")); // NOI18N
        panBeschreibungTitle.add(lblHeaderAllgemein1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panAllgemein.add(panBeschreibungTitle, gridBagConstraints);

        jScrollPane3.setBorder(null);
        jScrollPane3.setOpaque(false);

        panBeschreibungContent.setOpaque(false);
        panBeschreibungContent.setLayout(new java.awt.GridBagLayout());

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel1, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jTextField1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                jTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel10, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel11.text")); // NOI18N
        jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel11, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagebeschreibung}"),
                jTextField3,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebung}"),
                jTextField4,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField4, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.baujahr_ca}"),
                jTextField5,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox1.text")); // NOI18N
        jCheckBox1.setContentAreaFilled(false);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_gesperrt}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 20, 2, 2);
        jPanel9.add(jCheckBox1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_naechste_bauwerksbesichtigung}"),
                defaultBindableDateChooser3,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser3.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox2,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox2.text")); // NOI18N
        jCheckBox2.setContentAreaFilled(false);
        jCheckBox2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_denkmalschutz}"),
                jCheckBox2,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        jPanel9.add(jCheckBox2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox3,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox3.text")); // NOI18N
        jCheckBox3.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_zugaenge}"),
                jCheckBox3,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jCheckBox3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox4,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox4.text")); // NOI18N
        jCheckBox4.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_gebaeude}"),
                jCheckBox4,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jCheckBox4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox5,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox5.text")); // NOI18N
        jCheckBox5.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_beleuchtung}"),
                jCheckBox5,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jCheckBox5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel14,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel14.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox3,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel14,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel14, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel15,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel15.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox4,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel15,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel15, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel16,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel16.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox5,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel16,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel16, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_zugaenge}"),
                jTextField7,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox3,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jTextField7,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_gebaeude}"),
                jTextField8,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox4,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jTextField8,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_beleuchtung}"),
                jTextField9,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox5,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jTextField9,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField9, gridBagConstraints);

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridLayout(0, 3, 0, 4));

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox6,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox6.text")); // NOI18N
        jCheckBox6.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_handlauf_einseitig}"),
                jCheckBox6,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox6);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox8,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox8.text")); // NOI18N
        jCheckBox8.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_handlauf_beidseitig}"),
                jCheckBox8,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox8);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox10,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox10.text")); // NOI18N
        jCheckBox10.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_handlauf_durchgaengig}"),
                jCheckBox10,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox10);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox7,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox7.text")); // NOI18N
        jCheckBox7.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_rampen}"),
                jCheckBox7,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox7);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox9,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox9.text")); // NOI18N
        jCheckBox9.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_taktile_elemente}"),
                jCheckBox9,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel10, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel12, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel13,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 20, 2, 10);
        jPanel9.add(jLabel13, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_naechste_pruefung}"),
                defaultBindableDateChooser5,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser5.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser5, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_gesperrt_seit}"),
                defaultBindableDateChooser6,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser6.getConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox1,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                defaultBindableDateChooser6,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser6, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_letzte_bauwerksbesichtigung}"),
                defaultBindableDateChooser2,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser2.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_letzte_pruefung}"),
                defaultBindableDateChooser4,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser4.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel17,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel17, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel18,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 20, 2, 10);
        jPanel9.add(jLabel18, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel43,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel43.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel43, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel44,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel44.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel44, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel45,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel45.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel45, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel46,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel46.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel46, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"),
                jTextField10,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField10, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.baulast}"),
                jTextField11,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField11, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.unterhaltungspflicht}"),
                jTextField12,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField12, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verkehrssicherungspflicht}"),
                jTextField13,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField13, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox11,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jCheckBox11.text")); // NOI18N
        jCheckBox11.setContentAreaFilled(false);
        jCheckBox11.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ist_din1076}"),
                jCheckBox11,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        jPanel9.add(jCheckBox11, gridBagConstraints);

        if (editable) {
            org.openide.awt.Mnemonics.setLocalizedText(
                lblGeom,
                org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblGeom.text")); // NOI18N
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
            jPanel9.add(lblGeom, gridBagConstraints);
        }

        if (editable) {
            cbGeom.setMinimumSize(new java.awt.Dimension(41, 25));
            cbGeom.setPreferredSize(new java.awt.Dimension(41, 25));

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 10;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
            jPanel9.add(cbGeom, gridBagConstraints);
        }

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel84,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel84.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel84, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_letzte_sanierung}"),
                defaultBindableDateChooser1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser1.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableDateChooser1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_naechste_pruefung}"),
                defaultBindableReferenceCombo2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_letzte_pruefung}"),
                defaultBindableReferenceCombo3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel85,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel85.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox1,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel85,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 4);
        jPanel9.add(jLabel85, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel48,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel48.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        jPanel9.add(jLabel48, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel49,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel49.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        jPanel9.add(jLabel49, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel47,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel47.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel9.add(jLabel47, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(jTextField14, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beurteilung_standsicherheit}"),
                defaultBindableReferenceCombo5,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel50,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel50.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel50, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel51,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel51.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel51, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beurteilung_verkehrssicherheit}"),
                defaultBindableReferenceCombo6,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel52,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel52.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel52, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beurteilung_dauerhaftigkeit}"),
                defaultBindableReferenceCombo7,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel53,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel53.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel9.add(jLabel53, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.notwendigkeit_eingriff}"),
                defaultBindableReferenceCombo8,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel9.add(defaultBindableReferenceCombo8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panBeschreibungContent.add(jPanel9, gridBagConstraints);

        jPanel30.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungContent.add(jPanel30, gridBagConstraints);

        jScrollPane3.setViewportView(panBeschreibungContent);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel1.add(panAllgemein, gridBagConstraints);

        panZusammenfassung.setLayout(new java.awt.GridBagLayout());

        panZusammenfassungTitle.setBackground(new java.awt.Color(51, 51, 51));
        panZusammenfassungTitle.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblHeaderAllgemein.text")); // NOI18N
        panZusammenfassungTitle.add(lblHeaderAllgemein);

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

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel19,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel19.text")); // NOI18N
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jLabel19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel20,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel20, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink1,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink1.text")); // NOI18N
        jXHyperlink1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink2,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink2.text")); // NOI18N
        jXHyperlink2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink3,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink3.text")); // NOI18N
        jXHyperlink3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink4,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink4.text")); // NOI18N
        jXHyperlink4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink5,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink5.text")); // NOI18N
        jXHyperlink5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink6,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink6.text")); // NOI18N
        jXHyperlink6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel21,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel21, gridBagConstraints);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandStuetzmauern}"),
                jLabel22,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel22, gridBagConstraints);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandTreppen}"),
                jLabel23,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel23, gridBagConstraints);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandPodeste}"),
                jLabel24,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel24, gridBagConstraints);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandAbsturzsicherung}"),
                jLabel25,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel25, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandHandlauf}"),
                jLabel26,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel26, gridBagConstraints);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandEntwaesserung}"),
                jLabel27,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel27, gridBagConstraints);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.zustandGesamt}"),
                jLabel28,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel28, gridBagConstraints);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenTreppen}"),
                jLabel29,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0,00 €");
        binding.setSourceUnreadableValue("0,00 €");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel29, gridBagConstraints);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenPodeste}"),
                jLabel30,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel30, gridBagConstraints);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenAbsturzsicherung}"),
                jLabel31,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel31, gridBagConstraints);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenHandlauf}"),
                jLabel32,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel32, gridBagConstraints);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenEntwaesserung}"),
                jLabel33,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel33, gridBagConstraints);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenStuetzmauern}"),
                jLabel34,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel34, gridBagConstraints);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${overview.kostenGesamt}"),
                jLabel35,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel35, gridBagConstraints);

        roundedPanel1.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel1, gridBagConstraints);

        roundedPanel2.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel2, gridBagConstraints);

        roundedPanel3.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel3.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel3.setPreferredSize(new java.awt.Dimension(32, 32));

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel3, gridBagConstraints);

        roundedPanel4.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel4.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel4.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel4Layout = new javax.swing.GroupLayout(roundedPanel4);
        roundedPanel4.setLayout(roundedPanel4Layout);
        roundedPanel4Layout.setHorizontalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel4Layout.setVerticalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel4, gridBagConstraints);

        roundedPanel5.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel5.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel5.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel5, gridBagConstraints);

        roundedPanel6.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel6.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel6.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel6, gridBagConstraints);

        roundedPanel7.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel7.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel7.setPreferredSize(new java.awt.Dimension(32, 32));

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
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(panZusammenfassung, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel1.TabConstraints.tabTitle"),
            jPanel1); // NOI18N

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.treppenlaeufe}"),
                treppeLaeufePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel2.add(treppeLaeufePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel2.TabConstraints.tabTitle"),
            jPanel2); // NOI18N

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.podeste}"),
                treppePodestePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel3.add(treppePodestePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel3.TabConstraints.tabTitle"),
            jPanel3); // NOI18N

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.absturzsicherungen}"),
                treppeLeitelementePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel4.add(treppeLeitelementePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel4.TabConstraints.tabTitle"),
            jPanel4); // NOI18N

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlaeufe}"),
                treppeHandlaeufePanel2,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel5.add(treppeHandlaeufePanel2, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel5.TabConstraints.tabTitle"),
            jPanel5); // NOI18N

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel20.setOpaque(false);
        jPanel20.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwaesserung.zustand}"),
                treppeBauteilZustandKostenPanel5,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel20.add(treppeBauteilZustandKostenPanel5, gridBagConstraints);

        jPanel28.setOpaque(false);
        jPanel28.setLayout(new java.awt.GridBagLayout());

        panAllgemein6.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle6.setBackground(new java.awt.Color(51, 51, 51));
        panBeschreibungTitle6.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein7.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein7,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblHeaderAllgemein7.text")); // NOI18N
        panBeschreibungTitle6.add(lblHeaderAllgemein7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panAllgemein6.add(panBeschreibungTitle6, gridBagConstraints);

        panBeschreibungContent6.setOpaque(false);
        panBeschreibungContent6.setLayout(new java.awt.GridBagLayout());

        jPanel29.setOpaque(false);
        jPanel29.setLayout(new java.awt.GridBagLayout());

        jTextArea6.setLineWrap(true);
        jTextArea6.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwaesserung.bemerkung}"),
                jTextArea6,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(jTextArea6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel29.add(jScrollPane7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel78,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel78.text")); // NOI18N
        jLabel78.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel29.add(jLabel78, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel79,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel79.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel29.add(jLabel79, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwaesserung.art}"),
                defaultBindableReferenceCombo4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel29.add(defaultBindableReferenceCombo4, gridBagConstraints);

        jPanel13.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel29.add(jPanel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungContent6.add(jPanel29, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAllgemein6.add(panBeschreibungContent6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel28.add(panAllgemein6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel20.add(jPanel28, gridBagConstraints);

        jPanel34.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel20.add(jPanel34, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel6.add(jPanel20, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel6.TabConstraints.tabTitle"),
            jPanel6); // NOI18N

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauern}"),
                treppeStuetzmauernPanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel7.add(treppeStuetzmauernPanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel7.TabConstraints.tabTitle"),
            jPanel7); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane1, gridBagConstraints);

        add(pnlCard1, "card1");

        pnlCard2.setOpaque(false);
        pnlCard2.setLayout(new java.awt.GridBagLayout());

        pnlFotos.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlFotos.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlFotos.setLayout(new java.awt.GridBagLayout());

        pnlHeaderFotos.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderFotos.setForeground(new java.awt.Color(51, 51, 51));
        pnlHeaderFotos.setLayout(new java.awt.FlowLayout());

        lblHeaderFotos.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderFotos,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblHeaderFotos.text")); // NOI18N
        pnlHeaderFotos.add(lblHeaderFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlFotos.add(pnlHeaderFotos, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFiller9,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblFiller9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlFotos.add(lblFiller9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFotos,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblFotos.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlFotos.add(lblFotos, gridBagConstraints);

        jspFotoList.setMinimumSize(new java.awt.Dimension(250, 130));

        lstFotos.setMinimumSize(new java.awt.Dimension(250, 130));
        lstFotos.setPreferredSize(new java.awt.Dimension(250, 130));
        lstFotos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFotosValueChanged(evt);
                }
            });
        jspFotoList.setViewportView(lstFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlFotos.add(jspFotoList, gridBagConstraints);

        pnlCtrlButtons.setOpaque(false);
        pnlCtrlButtons.setLayout(new java.awt.GridBagLayout());

        btnAddImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnAddImg,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnAddImg.text"));          // NOI18N
        btnAddImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCtrlButtons.add(btnAddImg, gridBagConstraints);

        btnRemoveImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnRemoveImg,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnRemoveImg.text"));          // NOI18N
        btnRemoveImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        pnlCtrlButtons.add(btnRemoveImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlFotos.add(pnlCtrlButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 10);
        pnlCard2.add(pnlFotos, gridBagConstraints);

        pnlVorschau.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        lblVorschau.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorschau,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPicture,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblPicture.text")); // NOI18N
        pnlFoto.add(lblPicture, new java.awt.GridBagConstraints());

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 60));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 60));
        pnlFoto.add(lblBusy, new java.awt.GridBagConstraints());

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

        btnPrevImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png")));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPrevImg,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnPrevImg.text")); // NOI18N
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnNextImg,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnNextImg.text")); // NOI18N
        btnNextImg.setBorderPainted(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnNextImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlVorschau.add(pnlCtrlBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 15);
        pnlCard2.add(pnlVorschau, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlMap.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 10);
        pnlCard2.add(pnlMap, gridBagConstraints);
        pnlMap.add(map, BorderLayout.CENTER);

        add(pnlCard2, "card2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        try {
            if (cidsBean != null) {
                final Object geoObj = cidsBean.getProperty("geometrie.geo_field");
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
                                swms.setName("Treppe");
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
        } catch (final Exception ex) {
            LOG.error("error while init map", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink2ActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }                                                                                //GEN-LAST:event_jXHyperlink2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink3ActionPerformed
        jTabbedPane1.setSelectedIndex(3);
    }                                                                                //GEN-LAST:event_jXHyperlink3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink4ActionPerformed
        jTabbedPane1.setSelectedIndex(4);
    }                                                                                //GEN-LAST:event_jXHyperlink4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink5ActionPerformed
        jTabbedPane1.setSelectedIndex(5);
    }                                                                                //GEN-LAST:event_jXHyperlink5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink6ActionPerformed
        jTabbedPane1.setSelectedIndex(6);
    }                                                                                //GEN-LAST:event_jXHyperlink6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_jTextField1ActionPerformed

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
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        TreppenReportGenerator.generateKatasterBlatt(Arrays.asList(new CidsBean[] { cidsBean }), TreppeEditor.this);
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFotosValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFotosValueChanged
        if (!evt.getValueIsAdjusting() && listListenerEnabled) {
            loadFoto();
        }
    }                                                                                   //GEN-LAST:event_lstFotosValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddImgActionPerformed
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
            final File[] selFiles = fileChooser.getSelectedFiles();
            if ((selFiles != null) && (selFiles.length > 0)) {
                CismetThreadPool.execute(new ImageUploadWorker(Arrays.asList(selFiles)));
            }
        }
    }                                                                             //GEN-LAST:event_btnAddImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!initCopm
     */
    private void btnRemoveImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveImgActionPerformed
        final Object[] selection = lstFotos.getSelectedValues();
        if ((selection != null) && (selection.length > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Fotos wirklich gelöscht werden?",
                    "Fotos entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    listListenerEnabled = false;
                    final List<Object> removeList = Arrays.asList(selection);
                    final List<CidsBean> fotos = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "bilder");
                    if (fotos != null) {
                        fotos.removeAll(removeList);
                    }
                    // TODO set the laufende_nr
                    for (int i = 0; i < lstFotos.getModel().getSize(); i++) {
                        final CidsBean foto = (CidsBean)lstFotos.getModel().getElementAt(i);
                        foto.setProperty("laufende_nummer", i + 1);
                    }

                    for (final Object toDeleteObj : removeList) {
                        if (toDeleteObj instanceof CidsBean) {
                            final CidsBean fotoToDelete = (CidsBean)toDeleteObj;
                            final String file = String.valueOf(fotoToDelete.getProperty("url.object_name"));
                            IMAGE_CACHE.remove(file);
                            removedFotoBeans.add(fotoToDelete);
                        }
                    }
                } catch (Exception e) {
                    log.error(e, e);
                    showExceptionToUser(e, this);
                } finally {
                    // TODO check the laufende_nummer attribute
                    listListenerEnabled = true;
                    final int modelSize = lstFotos.getModel().getSize();
                    if (modelSize > 0) {
                        lstFotos.setSelectedIndex(0);
                    } else {
                        image = null;
                        lblPicture.setIcon(FOLDER_ICON);
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemoveImgActionPerformed
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
    /**
     * DOCUMENT ME!
     */
    private void loadFoto() {
        final Object fotoObj = lstFotos.getSelectedValue();
        if (fotoCidsBean != null) {
            fotoCidsBean.removePropertyChangeListener(listRepaintListener);
        }
        if (fotoObj instanceof CidsBean) {
            fotoCidsBean = (CidsBean)fotoObj;
            fotoCidsBean.addPropertyChangeListener(listRepaintListener);
            final String fileObj = (String)fotoCidsBean.getProperty("url.object_name");
            boolean cacheHit = false;
            if (fileObj != null) {
//                final String[] file = fileObj.toString().split("/");
//                final String object_name = file[file.length - 1];
                final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(fileObj);
                if (cachedImageRef != null) {
                    final BufferedImage cachedImage = cachedImageRef.get();
                    if (cachedImage != null) {
                        cacheHit = true;
                        image = cachedImage;
                        showWait(true);
                        resizeListenerEnabled = true;
                        timer.restart();
                    }
                }
                if (!cacheHit) {
                    CismetThreadPool.execute(new LoadSelectedImageWorker(fileObj));
                }
            }
        } else {
            image = null;
            lblPicture.setIcon(FOLDER_ICON);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPrevImgActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() - 1);
    }                                                                              //GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNextImgActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() + 1);
    }                                                                              //GEN-LAST:event_btnNextImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_jCheckBox2ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        lblTitle.setText("Treppe: " + cidsBean);
        this.cidsBean = cidsBean;
        if (editable && (cidsBean != null)) {
            CidsBean entwaesserungBean = (CidsBean)cidsBean.getProperty("entwaesserung");
            if (entwaesserungBean == null) {
                try {
                    entwaesserungBean = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "treppe_entwaesserung");
                    entwaesserungBean.setProperty(
                        "zustand",
                        CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "treppe_zustand"));
                    cidsBean.setProperty("entwaesserung", entwaesserungBean);
                } catch (final Exception ex) {
                    LOG.error("could not create entwaesserung bean", ex);
                }
            }
        }
        initMap();
        overview.recalculateAll();
        bindingGroup.bind();

        lstFotos.getModel().addListDataListener(new ListDataListener() {

                @Override
                public void intervalAdded(final ListDataEvent e) {
                    defineButtonStatus();
                }

                @Override
                public void intervalRemoved(final ListDataEvent e) {
                    defineButtonStatus();
                }

                @Override
                public void contentsChanged(final ListDataEvent e) {
                    defineButtonStatus();
                }
            });
        if (lstFotos.getModel().getSize() > 0) {
            lstFotos.setSelectedIndex(0);
        }

        ((CardLayout)getLayout()).show(this, "card1");
    }

    @Override
    public void dispose() {
        if (cbGeom != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        if (EditorSaveStatus.SAVE_SUCCESS == event.getStatus()) {
            for (final CidsBean deleteBean : removedFotoBeans) {
                final String fileName = (String)deleteBean.getProperty("url_object_name");
                final StringBuilder fileDir = new StringBuilder();
                fileDir.append(deleteBean.getProperty("url.url_base_id.prot_prefix").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.server").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.path").toString());

                try {
                    webDavHelper.deleteFileFromWebDAV(fileName,
                        fileDir.toString());
                    deleteBean.delete();
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        } else {
            for (final CidsBean deleteBean : removeNewAddedFotoBean) {
                final String fileName = (String)deleteBean.getProperty("url.object_name");
                final StringBuilder fileDir = new StringBuilder();
                fileDir.append(deleteBean.getProperty("url.url_base_id.prot_prefix").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.server").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.path").toString());
                webDavHelper.deleteFileFromWebDAV(fileName,
                    fileDir.toString());
            }
        }
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }
    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        final int selectedIdx = lstFotos.getSelectedIndex();
        btnPrevImg.setEnabled(selectedIdx > 0);
        btnNextImg.setEnabled((selectedIdx < (lstFotos.getModel().getSize() - 1)) && (selectedIdx > -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (wait) {
            if (!lblBusy.isBusy()) {
//                cardLayout.show(pnlFoto, "busy");
                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                btnAddImg.setEnabled(false);
                btnRemoveImg.setEnabled(false);
                lstFotos.setEnabled(false);
                btnPrevImg.setEnabled(false);
                btnNextImg.setEnabled(false);
            }
        } else {
//            cardLayout.show(pnlFoto, "preview");
            lblBusy.setBusy(false);
            lblBusy.setVisible(false);
            btnAddImg.setEnabled(true);
            btnRemoveImg.setEnabled(true);
            lstFotos.setEnabled(true);
            defineButtonStatus();
        }
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

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Fehler beim Übertragen des Bildes!");
        lblPicture.setToolTipText(tooltip);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private BufferedImage downloadImageFromWebDAV(final String fileName) throws Exception {
        final InputStream iStream = webDavHelper.getFileFromWebDAV(fileName, WEB_DAV_DIRECTORY);
        try {
            final ImageInputStream iiStream = ImageIO.createImageInputStream(iStream);
            final Iterator<ImageReader> itReader = ImageIO.getImageReaders(iiStream);
            final ImageReader reader = itReader.next();
            final ProgressMonitor monitor = new ProgressMonitor(this, "Bild wird übertragen...", "", 0, 100);
//            monitor.setMillisToPopup(500);
            reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                    @Override
                    public void sequenceStarted(final ImageReader source, final int minIndex) {
                    }

                    @Override
                    public void sequenceComplete(final ImageReader source) {
                    }

                    @Override
                    public void imageStarted(final ImageReader source, final int imageIndex) {
                        monitor.setProgress(monitor.getMinimum());
                    }

                    @Override
                    public void imageProgress(final ImageReader source, final float percentageDone) {
                        if (monitor.isCanceled()) {
                            try {
                                iiStream.close();
                            } catch (IOException ex) {
                                // NOP
                            }
                        } else {
                            monitor.setProgress(Math.round(percentageDone));
                        }
                    }

                    @Override
                    public void imageComplete(final ImageReader source) {
                        monitor.setProgress(monitor.getMaximum());
                    }

                    @Override
                    public void thumbnailStarted(final ImageReader source,
                            final int imageIndex,
                            final int thumbnailIndex) {
                    }

                    @Override
                    public void thumbnailProgress(final ImageReader source, final float percentageDone) {
                    }

                    @Override
                    public void thumbnailComplete(final ImageReader source) {
                    }

                    @Override
                    public void readAborted(final ImageReader source) {
                        monitor.close();
                    }
                });

            final ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iiStream, true, true);
            final BufferedImage result;
            try {
                result = reader.read(0, param);
            } finally {
                reader.dispose();
                iiStream.close();
            }
            return result;
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getZustandStuetzmauern() {
        return overview.getZustandStuetzmauern();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getKostenStuetzmauern() {
        return overview.getKostenStuetzmauern();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, CidsBean> getMauerBeans() {
        return treppeStuetzmauernPanel1.getMauerBeans();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    public class ZustandOverview {

        //~ Instance fields ----------------------------------------------------

        private double zustandTreppen = 0;
        private double zustandPodeste = 0;
        private double zustandHandlauf = 0;
        private double zustandEntwaesserung = 0;
        private double zustandAbsturzsicherung = 0;
        private double zustandStuetzmauern = 0;
        private double zustandGesamt = 0;

        private double kostenTreppen = 0;
        private double kostenPodeste = 0;
        private double kostenHandlauf = 0;
        private double kostenEntwaesserung = 0;
        private double kostenAbsturzsicherung = 0;
        private double kostenStuetzmauern = 0;
        private double kostenGesamt = 0;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        private void recalculateAll() {
            recalculateTreppenlaeufe();
            recalculatePodeste();
            recalculateHandlauf();
            recalculateAbsturzsicherung();
            recalculateEntwaesserung();
            recalculateStuetzmauern();
            recalculateGesamt();

            refreshView();
        }

        /**
         * DOCUMENT ME!
         */
        private void refreshView() {
            final Currency eur = Currency.getInstance("EUR");
            final NumberFormat formatKosten = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatKosten.setCurrency(eur);
            jLabel29.setText(formatKosten.format(getKostenTreppen()));
            jLabel30.setText(formatKosten.format(getKostenPodeste()));
            jLabel31.setText(formatKosten.format(getKostenAbsturzsicherung()));
            jLabel32.setText(formatKosten.format(getKostenHandlauf()));
            jLabel33.setText(formatKosten.format(getKostenEntwaesserung()));

            jLabel34.setText(formatKosten.format(getKostenStuetzmauern()));
            jLabel35.setText(formatKosten.format(getKostenGesamt()));

            final NumberFormat formatZustand = new DecimalFormat("#.#");

            if (getZustandTreppen() < 2) {
                roundedPanel1.setBackground(GRUEN);
                roundedPanel1.setForeground(GRUEN);
            } else if (getZustandTreppen() < 3) {
                roundedPanel1.setBackground(GELB);
                roundedPanel1.setForeground(GELB);
            } else {
                roundedPanel1.setBackground(ROT);
                roundedPanel1.setForeground(ROT);
            }
            jLabel23.setText(formatZustand.format(getZustandTreppen()));

            if (getZustandPodeste() < 2) {
                roundedPanel2.setBackground(GRUEN);
                roundedPanel2.setForeground(GRUEN);
            } else if (getZustandPodeste() < 3) {
                roundedPanel2.setBackground(GELB);
                roundedPanel2.setForeground(GELB);
            } else {
                roundedPanel2.setBackground(ROT);
                roundedPanel2.setForeground(ROT);
            }
            jLabel24.setText(formatZustand.format(getZustandPodeste()));

            if (getZustandAbsturzsicherung() < 2) {
                roundedPanel3.setBackground(GRUEN);
                roundedPanel3.setForeground(GRUEN);
            } else if (getZustandAbsturzsicherung() < 3) {
                roundedPanel3.setBackground(GELB);
                roundedPanel3.setForeground(GELB);
            } else {
                roundedPanel3.setBackground(ROT);
                roundedPanel3.setForeground(ROT);
            }
            jLabel25.setText(formatZustand.format(getZustandAbsturzsicherung()));

            if (getZustandHandlauf() < 2) {
                roundedPanel4.setBackground(GRUEN);
                roundedPanel4.setForeground(GRUEN);
            } else if (getZustandHandlauf() < 3) {
                roundedPanel4.setBackground(GELB);
                roundedPanel4.setForeground(GELB);
            } else {
                roundedPanel4.setBackground(ROT);
                roundedPanel4.setForeground(ROT);
            }
            jLabel26.setText(formatZustand.format(getZustandHandlauf()));

            if (getZustandEntwaesserung() < 2) {
                roundedPanel5.setBackground(GRUEN);
                roundedPanel5.setForeground(GRUEN);
            } else if (getZustandEntwaesserung() < 3) {
                roundedPanel5.setBackground(GELB);
                roundedPanel5.setForeground(GELB);
            } else {
                roundedPanel5.setBackground(ROT);
                roundedPanel5.setForeground(ROT);
            }
            jLabel27.setText(formatZustand.format(getZustandEntwaesserung()));

            if (getZustandStuetzmauern() < 2) {
                roundedPanel6.setBackground(GRUEN);
                roundedPanel6.setForeground(GRUEN);
            } else if (getZustandStuetzmauern() < 3) {
                roundedPanel6.setBackground(GELB);
                roundedPanel6.setForeground(GELB);
            } else {
                roundedPanel6.setBackground(ROT);
                roundedPanel6.setForeground(ROT);
            }
            jLabel22.setText(formatZustand.format(getZustandStuetzmauern()));

            if (getZustandGesamt() < 2) {
                roundedPanel7.setBackground(GRUEN);
                roundedPanel7.setForeground(GRUEN);
            } else if (getZustandGesamt() < 3) {
                roundedPanel7.setBackground(GELB);
                roundedPanel7.setForeground(GELB);
            } else {
                roundedPanel7.setBackground(ROT);
                roundedPanel7.setForeground(ROT);
            }
            jLabel28.setText(formatZustand.format(getZustandGesamt()));
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateTreppenlaeufe() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("treppenlaeufe")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }
            setZustandTreppen(zustandGesamt);
            setKostenTreppen(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculatePodeste() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("podeste")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }
            setZustandPodeste(zustandGesamt);
            setKostenPodeste(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateHandlauf() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("handlaeufe")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandHandlauf(zustandGesamt);
            setKostenHandlauf(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateAbsturzsicherung() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("absturzsicherungen")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandAbsturzsicherung(zustandGesamt);
            setKostenAbsturzsicherung(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateEntwaesserung() {
            final Double zustand = (cidsBean != null) ? (Double)cidsBean.getProperty("entwaesserung.zustand.gesamt")
                                                      : null;
            final Double kosten = (cidsBean != null) ? (Double)cidsBean.getProperty("entwaesserung.zustand.kosten")
                                                     : null;

            setZustandEntwaesserung((zustand != null) ? zustand : 0);
            setKostenEntwaesserung((kosten != null) ? kosten : 0);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateStuetzmauern() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean zustandBean : treppeStuetzmauernPanel1.getZustandBeans()) {
                    final Double zustand = (zustandBean != null) ? (Double)zustandBean.getProperty("gesamt") : null;
                    final Double kosten = (zustandBean != null) ? (Double)zustandBean.getProperty("kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandStuetzmauern(zustandGesamt);
            setKostenStuetzmauern(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        private void recalculateGesamt() {
            final double[] kostenAll = new double[] {
                    kostenTreppen,
                    kostenPodeste,
                    kostenHandlauf,
                    kostenEntwaesserung,
                    kostenAbsturzsicherung,
                    kostenStuetzmauern
                };
            final double[] zustandAll = new double[] {
                    zustandTreppen,
                    zustandPodeste,
                    zustandHandlauf,
                    zustandEntwaesserung,
                    zustandAbsturzsicherung,
                    zustandStuetzmauern
                };

            double kosten = 0;
            double zustand = 0;
            for (int i = 0; i < kostenAll.length; i++) {
                kosten += kostenAll[i];
                if (zustandAll[i] > zustand) {
                    zustand = zustandAll[i];
                }
            }

            setKostenGesamt(kosten);
            setZustandGesamt(zustand);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageUploadWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        private final Collection<File> fotos;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageUploadWorker object.
         *
         * @param  fotos  DOCUMENT ME!
         */
        public ImageUploadWorker(final Collection<File> fotos) {
            this.fotos = fotos;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            final Collection<CidsBean> newBeans = new ArrayList<CidsBean>();
            int i = lstFotos.getModel().getSize() + 1;
            for (final File imageFile : fotos) {
//                final String webFileName = WebDavHelper.generateWebDAVFileName(FILE_PREFIX, imageFile);
                webDavHelper.uploadFileToWebDAV(
                    imageFile.getName(),
                    imageFile,
                    WEB_DAV_DIRECTORY,
                    TreppeEditor.this);

                final MetaClass MB_MC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "url_base");
                String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
                final String protokoll = WEB_DAV_DIRECTORY.substring(0, WEB_DAV_DIRECTORY.indexOf("://")) + "://";

                query += "FROM " + MB_MC.getTableName();
                query += " WHERE '" + protokoll + "' || server || path  = '" + WEB_DAV_DIRECTORY + "';  ";
                final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);

                final CidsBean url = CidsBeanSupport.createNewCidsBeanFromTableName("url");
                url.setProperty("url_base_id", metaObjects[0].getBean());
                url.setProperty("object_name", imageFile.getName());

                final CidsBean newFotoBean = CidsBeanSupport.createNewCidsBeanFromTableName("Treppe_bild");
                newFotoBean.setProperty("laufende_nummer", i);
                newFotoBean.setProperty("name", imageFile.getName());
                newFotoBean.setProperty("url", url);
                newBeans.add(newFotoBean);
                i++;
            }
            return newBeans;
        }

        @Override
        protected void done() {
            try {
                final Collection<CidsBean> newBeans = get();
                if (!newBeans.isEmpty()) {
                    final List<CidsBean> oldBeans = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "bilder");
                    oldBeans.addAll(newBeans);
                    removeNewAddedFotoBean.addAll(newBeans);
                    lstFotos.setSelectedValue(newBeans.iterator().next(), true);
                } else {
                    lblPicture.setIcon(FOLDER_ICON);
                }
            } catch (InterruptedException ex) {
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                log.error(ex, ex);
            } finally {
                showWait(false);
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

        private final String file;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  toLoad  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final String toLoad) {
            this.file = toLoad;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((file != null) && (file.length() > 0)) {
                return downloadImageFromWebDAV(file);
//                return downloadImageFromUrl(file);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    IMAGE_CACHE.put(file, new SoftReference<BufferedImage>(image));
                    resizeListenerEnabled = true;
                    timer.restart();
                } else {
                    indicateError("Bild konnte nicht geladen werden: Unbekanntes Bildformat");
                }
            } catch (InterruptedException ex) {
                image = null;
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                log.error(ex, ex);
                String causeMessage = "";
                final Throwable cause = ex.getCause();
                if (cause != null) {
                    causeMessage = cause.getMessage();
                }
                indicateError(causeMessage);
            } finally {
                if (image == null) {
                    showWait(false);
                }
            }
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
            // TODO image im EDT auslesen und final speichern!
            if (image != null) {
                lblPicture.setText("Wird neu skaliert...");
                lstFotos.setEnabled(false);
            }
//            log.fatal("RESIZE Image!", new Exception());
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
//                if (panButtons.getSize().getWidth() + 10 < panPreview.getSize().getWidth()) {
                // ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panPreview, 20, 20));
                final ImageIcon result = new ImageIcon(adjustScale(image, pnlFoto, 20, 20));
                return result;
//                } else {
//                    return new ImageIcon(image);
//                }
            } else {
                return null;
            }
        }

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
                    log.warn(ex, ex);
                } catch (ExecutionException ex) {
                    log.error(ex, ex);
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
}
