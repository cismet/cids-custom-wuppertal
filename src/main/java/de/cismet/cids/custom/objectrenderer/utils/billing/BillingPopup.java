/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils.billing;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.wunda_blau.BillingKundeRenderer;
import de.cismet.cids.custom.utils.BerechtigungspruefungKonfiguration;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungBillingDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungDownloadInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungAnfrageServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BillingPopup extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Collection<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf",
            "bmp",
            "tiff",
            "tif",
            "png",
            "jpg",
            "jpeg",
            "jpg",
            "gif");
    public static final String MODE_CONFIG_ATTR = "billing.mode@WUNDA_BLAU";
    public static final String ALLOWED_USAGE_CONFIG_ATTR = "billing.allowed.usage@WUNDA_BLAU";
    public static final String RESTRICTED_USAGE_CONFIG_ATTR = "billing.restricted.usage@WUNDA_BLAU";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BillingPopup.class);

    //~ Instance fields --------------------------------------------------------

    Product currentProduct = null;
    Modus currentMode = null;
    Usage currentUsage = null;
    /**
     * E.g. an URL to a webservice such that an alkis product can be downloaded again in the
     * {@link BillingKundeRenderer}
     */
    String defaultRequest = null;
    Map<String, String> requestPerUsage;
    Geometry geom = null;
    CidsBean logEntry = null;
    String berechnungPrefix = "";
    private final BillingInfo billingInfo;
    private final HashMap<String, Modus> modi = new HashMap<String, Modus>();
    private final HashMap<String, Product> products = new HashMap<String, Product>();
    private final HashMap<String, Usage> usages = new HashMap<String, Usage>();
    private final HashMap<String, ProductGroup> productGroups = new HashMap<String, ProductGroup>();

    private final ImageIcon money = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/billing/money--exclamation.png"));
    private double rawPrice = 0;
    private double nettoPrice = 0;
    private double bruttoPrice = 0;
    private boolean shouldGoOn = false;
    private BerechtigungspruefungBillingDownloadInfo downloadInfo = null;

    private File file = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboUsage;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdOk;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblGebTitle;
    private javax.swing.JLabel lblGebuehr;
    private javax.swing.JLabel lblMoneyWarn;
    private javax.swing.JLabel lblMwst;
    private javax.swing.JLabel lblMwstTitle;
    private javax.swing.JPanel panControls;
    private javax.swing.JTextPane txtBerechnung;
    private javax.swing.JTextField txtGBuchNr;
    private javax.swing.JTextArea txtProjektbez;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BillingPopup.
     */
    private BillingPopup() {
        super(ComponentRegistry.getRegistry().getMainWindow(), true);
        initComponents();
        final SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(attribs, 10);
        txtBerechnung.setParagraphAttributes(attribs, true);

        BillingInfo billingInfoTmp;
        try {
            billingInfoTmp = MAPPER.readValue(BillingPopup.class.getResourceAsStream(
                        "/de/cismet/cids/custom/billing/billing.json"),
                    BillingInfo.class);

            final ArrayList<Modus> lm = billingInfoTmp.getModi();
            for (final Modus m : lm) {
                modi.put(m.getKey(), m);
            }
            final ArrayList<Product> lp = billingInfoTmp.getProducts();
            for (final Product p : lp) {
                products.put(p.getId(), p);
            }
            final ArrayList<Usage> lu = billingInfoTmp.getUsages();
            for (final Usage u : lu) {
                usages.put(u.getKey(), u);
            }
            final ArrayList<ProductGroup> lpg = billingInfoTmp.getProductGroups();
            for (final ProductGroup pg : lpg) {
                productGroups.put(pg.getKey(), pg);
            }
        } catch (IOException ioException) {
            LOG.error("Error when trying to read the billingInfo.json", ioException);
            billingInfoTmp = null;
        }
        billingInfo = billingInfoTmp;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   product  DOCUMENT ME!
     * @param   request  E.g. an URL to a webservice such that an alkis product can be downloaded again in the
     *                   {@link BillingKundeRenderer}
     * @param   geom     DOCUMENT ME!
     * @param   amounts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String request,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, request, (Map)null, geom, null, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product       DOCUMENT ME!
     * @param   request       DOCUMENT ME!
     * @param   geom          DOCUMENT ME!
     * @param   downloadInfo  DOCUMENT ME!
     * @param   amounts       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String request,
            final Geometry geom,
            final BerechtigungspruefungBillingDownloadInfo downloadInfo,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, request, (Map)null, geom, downloadInfo, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, defaultRequest, requestPerUsage, geom, null, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   downloadInfo     DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final Geometry geom,
            final BerechtigungspruefungBillingDownloadInfo downloadInfo,
            final ProductGroupAmount... amounts) throws Exception {
        final BillingPopup instance = getInstance();
        final User user = SessionManager.getSession().getUser();
        final String modus = SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR);
        if (modus != null) {
            instance.initialize(product, defaultRequest, requestPerUsage, geom, downloadInfo, amounts);
            return instance.shouldGoOn;
        } else {
            instance.defaultRequest = defaultRequest;
            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product  DOCUMENT ME!
     * @param   request  E.g. an URL to a webservice such that an alkis product can be downloaded again in the
     *                   {@link BillingKundeRenderer}
     * @param   gBuchNr  DOCUMENT ME!
     * @param   geom     DOCUMENT ME!
     * @param   amounts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String request,
            final String gBuchNr,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, request, gBuchNr, null, geom, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product     DOCUMENT ME!
     * @param   request     DOCUMENT ME!
     * @param   gBuchNr     DOCUMENT ME!
     * @param   projektbez  DOCUMENT ME!
     * @param   geom        DOCUMENT ME!
     * @param   amounts     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String request,
            final String gBuchNr,
            final String projektbez,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, request, null, gBuchNr, projektbez, geom, null, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product       DOCUMENT ME!
     * @param   request       DOCUMENT ME!
     * @param   gBuchNr       DOCUMENT ME!
     * @param   projektbez    DOCUMENT ME!
     * @param   geom          DOCUMENT ME!
     * @param   downloadInfo  DOCUMENT ME!
     * @param   amounts       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String request,
            final String gBuchNr,
            final String projektbez,
            final Geometry geom,
            final BerechtigungspruefungBillingDownloadInfo downloadInfo,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, request, null, gBuchNr, projektbez, geom, downloadInfo, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   gBuchNr          DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final String gBuchNr,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, defaultRequest, requestPerUsage, gBuchNr, null, geom, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   gBuchNr          DOCUMENT ME!
     * @param   projektbez       DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final String gBuchNr,
            final String projektbez,
            final Geometry geom,
            final ProductGroupAmount... amounts) throws Exception {
        return doBilling(product, defaultRequest, requestPerUsage, gBuchNr, projektbez, geom, null, amounts);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   gBuchNr          DOCUMENT ME!
     * @param   projektbez       DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   downloadInfo     DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean doBilling(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final String gBuchNr,
            final String projektbez,
            final Geometry geom,
            final BerechtigungspruefungBillingDownloadInfo downloadInfo,
            final ProductGroupAmount... amounts) throws Exception {
        final BillingPopup instance = getInstance();
        instance.txtGBuchNr.setText(gBuchNr);
        instance.txtProjektbez.setText(projektbez);
        if (hasUserBillingMode()) {
            instance.initialize(product, defaultRequest, requestPerUsage, geom, downloadInfo, amounts);
            return instance.shouldGoOn;
        } else {
            instance.defaultRequest = defaultRequest;
            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static boolean hasUserBillingMode() throws Exception {
        return SessionManager.getConnection().getConfigAttr(SessionManager.getSession().getUser(), MODE_CONFIG_ATTR)
                    != null;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        lblMoneyWarn = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        panControls = new javax.swing.JPanel();
        cmdCancel = new javax.swing.JButton();
        cmdOk = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtGBuchNr = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtProjektbez = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        cboUsage = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblGebuehr = new javax.swing.JLabel();
        lblGebTitle = new javax.swing.JLabel();
        lblMwstTitle = new javax.swing.JLabel();
        lblMwst = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtBerechnung = new javax.swing.JTextPane();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<String>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        jScrollPane3.setViewportView(jTextPane1);

        lblMoneyWarn.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/billing/money--exclamation.png"))); // NOI18N
        lblMoneyWarn.setText(org.openide.util.NbBundle.getMessage(
                BillingPopup.class,
                "BillingPopup.lblMoneyWarn.text"));                                                // NOI18N

        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.setFileFilter(new FileNameExtensionFilter(
                "PDF und Bild-Dateien",
                ALLOWED_EXTENSIONS.toArray(new String[0])));

        setTitle(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.title")); // NOI18N
        setMaximumSize(new java.awt.Dimension(2147483647, 600));
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jSeparator3, gridBagConstraints);

        panControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cmdCancel.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.cmdCancel.text")); // NOI18N
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        panControls.add(cmdCancel);

        cmdOk.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.cmdOk.text")); // NOI18N
        cmdOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOkActionPerformed(evt);
                }
            });
        panControls.add(cmdOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(panControls, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        txtGBuchNr.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.txtGBuchNr.text")); // NOI18N
        txtGBuchNr.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGBuchNrActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(txtGBuchNr, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 5);
        getContentPane().add(jLabel2, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(222, 77));

        txtProjektbez.setColumns(20);
        txtProjektbez.setRows(5);
        txtProjektbez.setMinimumSize(new java.awt.Dimension(0, 40));
        jScrollPane2.setViewportView(txtProjektbez);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 5);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 5, 5);
        getContentPane().add(jLabel3, gridBagConstraints);

        cboUsage.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboUsage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboUsageActionPerformed(evt);
                }
            });
        cboUsage.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    cboUsagePropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 5, 5);
        getContentPane().add(cboUsage, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(jLabel5, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(101, 35));
        jPanel1.setPreferredSize(new java.awt.Dimension(101, 35));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblGebuehr.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGebuehr.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.lblGebuehr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 5);
        jPanel1.add(lblGebuehr, gridBagConstraints);

        lblGebTitle.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.lblGebTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 5);
        jPanel1.add(lblGebTitle, gridBagConstraints);

        lblMwstTitle.setText(org.openide.util.NbBundle.getMessage(
                BillingPopup.class,
                "BillingPopup.lblMwstTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        jPanel1.add(lblMwstTitle, gridBagConstraints);

        lblMwst.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMwst.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.lblMwst.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        jPanel1.add(lblMwst, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        txtBerechnung.setEditable(false);
        txtBerechnung.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10));
        txtBerechnung.setMinimumSize(new java.awt.Dimension(0, 200));
        jScrollPane4.setViewportView(txtBerechnung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(jScrollPane4, gridBagConstraints);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(jLabel7, gridBagConstraints);

        jComboBox1.setModel(new DefaultComboBoxModel<String>());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 5);
        getContentPane().add(jComboBox1, gridBagConstraints);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 5, 5);
        getContentPane().add(jLabel6, gridBagConstraints);

        jScrollPane5.setMinimumSize(new java.awt.Dimension(222, 77));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setMinimumSize(new java.awt.Dimension(0, 40));
        jScrollPane5.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 5, 5);
        getContentPane().add(jScrollPane5, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jLabel4, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel8.text")); // NOI18N
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jLabel8, gridBagConstraints);

        jButton3.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jButton3, gridBagConstraints);

        jLabel9.setText(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.jLabel9.text")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(450, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabel9, gridBagConstraints);

        setSize(new java.awt.Dimension(468, 678));
        setLocationRelativeTo(null);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final int status = jFileChooser1.showOpenDialog(StaticSwingTools.getParentFrame(this));
        if (status == JFileChooser.APPROVE_OPTION) {
            final String extension = jFileChooser1.getSelectedFile()
                        .getName()
                        .substring(jFileChooser1.getSelectedFile().getName().lastIndexOf(".") + 1);
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                file = jFileChooser1.getSelectedFile();
            } else {
                file = null;
                JOptionPane.showMessageDialog(
                    this,
                    "Diese Datei-Endung ist nicht erlaubt.\n\nFolgende Datei-Endungen werden akzeptiert:\n"
                            + implode(",", ALLOWED_EXTENSIONS.toArray(new String[0])),
                    "Unerlaubte Datei-Endung",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            file = null;
        }
        updateFileLabel();
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void updateFileLabel() {
        jLabel8.setText((file == null) ? "<html><i>keine Datei ausgewählt" : file.getName());
    }
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGBuchNrActionPerformed(final java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOkActionPerformed(final java.awt.event.ActionEvent evt) {
        if ((downloadInfo == null) && (txtGBuchNr.getText().trim().length() == 0)) {
            JOptionPane.showMessageDialog(
                this,
                "Sie müssen eine Geschäftsbuchnummer eingeben, damit der Vorgang bearbeitet werden kann.",
                "Fehlende Eingabe",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // save the log entry
        try {
            final CidsBean cb = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "Billing_Billing");
            cb.setProperty("username", SessionManager.getSession().getUser().toString());
            cb.setProperty("angelegt_durch", getExternalUser());
            cb.setProperty("ts", new java.sql.Timestamp(System.currentTimeMillis()));
            cb.setProperty("angeschaeftsbuch", Boolean.FALSE);
            cb.setProperty("modus", currentMode.getKey());
            cb.setProperty("produktkey", currentProduct.getId());
            cb.setProperty("produktbezeichnung", currentProduct.getName());
            cb.setProperty("netto_summe", nettoPrice);
            cb.setProperty("mwst_satz", currentProduct.getMwst());
            cb.setProperty("brutto_summe", bruttoPrice);
            cb.setProperty("geschaeftsbuchnummer", txtGBuchNr.getText());
//        cb.setProperty("geometrie", null);
            cb.setProperty("modusbezeichnung", currentMode.getName());
            cb.setProperty("berechnung", txtBerechnung.getText().trim());
            cb.setProperty("verwendungszweck", currentUsage.getName());
            cb.setProperty("projektbezeichnung", txtProjektbez.getText());
            cb.setProperty("request", getCurrentRequest());
            cb.setProperty("verwendungskey", currentUsage.getKey());
            cb.setProperty("abgerechnet", Boolean.FALSE);
            final CidsBean persistedCb = cb.persist();

            if (downloadInfo != null) {
                try {
                    downloadInfo.setBillingId(persistedCb.getPrimaryKeyValue());
                    downloadInfo.setProduktbezeichnung(txtProjektbez.getText());
                    doAnfrage(
                        downloadInfo,
                        new Callback() {

                            @Override
                            public void callback(final String anfrageSchluessel) {
                                JOptionPane.showMessageDialog(
                                    BillingPopup.this,
                                    "<html>Ihre Anfrage wird unter dem Schlüssel \""
                                            + anfrageSchluessel
                                            + "\" bearbeitet."
                                            + "<br/>Sie werden benachrichtigt, sobald sie bearbeitet wurde.",
                                    "Ihre Anfrage wird bearbeitet",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
                shouldGoOn = false;
            } else {
                // Nebenläufigkeit my arse
                shouldGoOn = true;
            }
        } catch (Exception e) {
            LOG.error("Error during the persitence of the billing log.", e);
            shouldGoOn = false;
        }
        // the end
        setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCurrentRequest() {
        final String usageKey = (currentUsage != null) ? currentUsage.getKey() : null;
        if ((requestPerUsage != null) && requestPerUsage.containsKey(usageKey)) {
            return requestPerUsage.get(usageKey);
        } else {
            return defaultRequest;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getExternalUser() {
        return getExternalUser(SessionManager.getSession().getUser());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getExternalUser(final User user) {
        return getExternalUser(user.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   loginName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getExternalUser(final String loginName) {
        final MetaClass MB_MC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "billing_kunden_logins");
        if (MB_MC == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "The metaclass for billing_kunden_logins is null. The current user has probably not the needed rights.");
            }
            return null;
        }
        String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
        query += "FROM " + MB_MC.getTableName();
        query += " WHERE name = '" + loginName + "'";

        CidsBean externalUser = null;
        try {
            final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
            if ((metaObjects != null) && (metaObjects.length > 0)) {
                externalUser = metaObjects[0].getBean();
            }
        } catch (ConnectionException ex) {
            LOG.error("Error while retrieving the CidsBean of an external user.", ex);
        }
        return externalUser;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) {
        shouldGoOn = false;
        setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboUsageActionPerformed(final java.awt.event.ActionEvent evt) {
        calculateNettoPrice();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboUsagePropertyChange(final java.beans.PropertyChangeEvent evt) {
    }

    /**
     * DOCUMENT ME!
     */
    private void calculateNettoPrice() {
        final Object sel = cboUsage.getSelectedItem();
        if (sel instanceof Usage) {
            currentUsage = (Usage)sel;
            String berechungUsageDependent = "";

            final double discount = currentProduct.getDiscounts().get(currentUsage.getKey());
            final double absDiscount = (1.0 - discount) * rawPrice;

            berechungUsageDependent = "zweckabhängiger Rabatt (" + Math.round((1.0 - discount) * 100) + "%) : -"
                        + NumberFormat.getCurrencyInstance().format(absDiscount) + " \n";
            berechungUsageDependent += "---------\n";
            nettoPrice = rawPrice * discount;

            berechungUsageDependent += NumberFormat.getCurrencyInstance().format(nettoPrice) + " \n";

            txtBerechnung.setText(berechnungPrefix + "\n" + berechungUsageDependent);
            calculateBruttoPrice();
        } else {
            currentUsage = null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Usage getCurrentUsage() {
        return currentUsage;
    }

    /**
     * DOCUMENT ME!
     */
    private void calculateBruttoPrice() {
        final double mwst = nettoPrice * (currentProduct.getMwst() / 100);
        bruttoPrice = nettoPrice + mwst;
        bruttoPrice = Math.round(bruttoPrice * 100) / 100.;

        final DecimalFormat df = new DecimalFormat("0.#");
        lblMwstTitle.setText("zzgl. MwSt. (" + df.format(currentProduct.getMwst()) + "%):");
        lblMwst.setText(NumberFormat.getCurrencyInstance().format(mwst));
        lblGebuehr.setText(NumberFormat.getCurrencyInstance().format(bruttoPrice));
        if (bruttoPrice > 0) {
            lblGebTitle.setIcon(money);
        } else {
            lblGebTitle.setIcon(null);
        }
    }

    /**
     * DOCUMENT ME!modus.
     *
     * @param  product  DOCUMENT ME!
     * @param  amounts  DOCUMENT ME!
     */
    private void calculateRawPrice(final String product, final ProductGroupAmount... amounts) {
        rawPrice = 0;
        for (final ProductGroupAmount pga : amounts) {
            rawPrice += ((double)pga.getAmount()) * currentProduct.getPrices().get(pga.group);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  use isBillingAllowed(String) instead
     */
    @Deprecated
    public static boolean isBillingAllowed() {
        try {
            final User user = SessionManager.getSession().getUser();
            return (SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR) == null)
                        || ((SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR) != null)
                            && (SessionManager.getConnection().getConfigAttr(user, ALLOWED_USAGE_CONFIG_ATTR) != null));
        } catch (ConnectionException ex) {
            LOG.error("error while checking configAttr", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isBillingAllowed(final String product) {
        try {
            final User user = SessionManager.getSession().getUser();
            return (SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR) == null)
                        || ((SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR) != null)
                            && (getAllowedUsages(user, product).length > 0));
        } catch (ConnectionException ex) {
            LOG.error("error while checking configAttr", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   product  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private static String[] getAllowedUsages(final User user, final String product) throws ConnectionException {
        final Set<String> allowedUsages = new LinkedHashSet<String>();

        final String rawAllowedUsageLines = SessionManager.getConnection()
                    .getConfigAttr(user, ALLOWED_USAGE_CONFIG_ATTR);
        if (rawAllowedUsageLines != null) {
            for (final String rawAllowedUsageLine : rawAllowedUsageLines.split("\n")) {
                final int indexOfAllowed = rawAllowedUsageLine.indexOf(":");
                final String allowedProduct = (indexOfAllowed > -1) ? rawAllowedUsageLine.substring(0, indexOfAllowed)
                                                                    : null;
                if ((allowedProduct == null) || allowedProduct.equals(product)) {
                    allowedUsages.addAll(Arrays.asList(rawAllowedUsageLine.substring(indexOfAllowed + 1).split(",")));
                }
            }
        }

        if (!allowedUsages.isEmpty()) {
            final String rawRestrcitedUsageLines = SessionManager.getConnection()
                        .getConfigAttr(user, RESTRICTED_USAGE_CONFIG_ATTR);
            if (rawRestrcitedUsageLines != null) {
                for (final String rawRestrcitedUsageLine : rawRestrcitedUsageLines.split("\n")) {
                    final int indexOfRestricted = rawRestrcitedUsageLine.indexOf(":");
                    final String restrictedProduct = (indexOfRestricted > -1)
                        ? rawRestrcitedUsageLine.substring(0, indexOfRestricted) : null;
                    if ((restrictedProduct == null) || restrictedProduct.equals(product)) {
                        allowedUsages.removeAll(Arrays.asList(
                                rawRestrcitedUsageLine.substring(indexOfRestricted + 1).split(",")));
                    }
                }
            }
        }

        return allowedUsages.toArray(new String[0]);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBerechnungsProtokoll() {
        return txtBerechnung.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product          DOCUMENT ME!
     * @param   defaultRequest   DOCUMENT ME!
     * @param   requestPerUsage  DOCUMENT ME!
     * @param   geom             DOCUMENT ME!
     * @param   downloadInfo     DOCUMENT ME!
     * @param   amounts          DOCUMENT ME!
     *
     * @throws  Exception                 DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void initialize(
            final String product,
            final String defaultRequest,
            final Map<String, String> requestPerUsage,
            final Geometry geom,
            final BerechtigungspruefungBillingDownloadInfo downloadInfo,
            final ProductGroupAmount... amounts) throws Exception {
        final User user = SessionManager.getSession().getUser();

        // Auslesen des Modus für diesen User
        final String modus = SessionManager.getConnection().getConfigAttr(user, MODE_CONFIG_ATTR);

        currentMode = modi.get(modus);
        if (currentMode == null) {
            // Im Moment noch Dialog beenden, später Exception und Druck ablehnen
            LOG.info("mode " + modus + " not found in billing.json. will hide billing popup. reports for free ;-)");
            setVisible(false);
            return;
        }

        // Clear den Kram
        txtBerechnung.setText(null);
        berechnungPrefix = "";

        currentProduct = products.get(product);

        berechnungPrefix = "\nProdukt: " + currentProduct.getName() + "\n\n";

        final HashMap<String, Double> prices = new HashMap<String, Double>();

        // Check ob es die Produktid gibt
        if (currentProduct == null) {
            throw new IllegalArgumentException("Product " + product + " not in the configured productlist.");
        }

        // Check ob es jede Produktgruppe gibt
        for (final ProductGroupAmount pga : amounts) {
            if ((currentProduct.getPrices().get(pga.group) == null) || (productGroups.get(pga.group) == null)) {
                throw new IllegalArgumentException("Productgroup " + pga.group
                            + " not in the configured productgroups.");
            }
            berechnungPrefix += (pga.getAmount() + " " + productGroups.get(pga.group).getDescription() + " (a "
                            + NumberFormat.getCurrencyInstance().format(currentProduct.getPrices().get(pga.group))
                            + ")\n");
        }

        berechnungPrefix += "---------\n";
        calculateRawPrice(product, amounts);
        berechnungPrefix += NumberFormat.getCurrencyInstance().format(rawPrice) + " \n";
        berechnungPrefix += "\n";

        // Auslesen der gültigen Verwendungszwecke
        final String[] validUsages = getAllowedUsages(user, currentProduct.id);

        final Usage[] comboUsages = new Usage[validUsages.length];
        int i = 0;
        // Check ob dazu überhaupt ein Discount vorliegt
        for (final String usage : validUsages) {
            if ((currentProduct.getDiscounts().get(usage) == null) || (usages.get(usage) == null)) {
                throw new IllegalArgumentException("Usage " + usage + " not in the configured discounts for product "
                            + currentProduct.id);
            }
            comboUsages[i++] = usages.get(usage);
        }

        cboUsage.setModel(new DefaultComboBoxModel(comboUsages));

        setTitle(org.openide.util.NbBundle.getMessage(BillingPopup.class, "BillingPopup.title") + " (" + user + ")");

        calculateNettoPrice();

        this.defaultRequest = defaultRequest;
        this.requestPerUsage = requestPerUsage;
        this.downloadInfo = downloadInfo;

        txtGBuchNr.setEnabled(downloadInfo == null);

        final boolean pruefung = downloadInfo != null;
        jComboBox1.setVisible(pruefung);
        jScrollPane5.setVisible(pruefung);
        jButton3.setVisible(pruefung);
        jLabel4.setVisible(pruefung);
        jLabel6.setVisible(pruefung);
        jLabel7.setVisible(pruefung);
        jLabel8.setVisible(pruefung);
        jLabel9.setVisible(pruefung);
        jSeparator3.setVisible(pruefung);

        if (downloadInfo != null) {
            txtGBuchNr.setText("");
            setProdukt(downloadInfo.getProduktTyp());
        }

        pack();
        setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BillingInfo getBillingInfo() {
        return getInstance().billingInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HashMap<String, Product> getProducts() {
        return getInstance().products;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  the command line arguments
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "buggalo");
        final boolean t = doBilling(
                "fsnw",
                "request",
                null,
                new ProductGroupAmount("ea", 2),
                new ProductGroupAmount("ea", 1),
                new ProductGroupAmount("ea", 1),
                new ProductGroupAmount("ea", 1));

        System.out.println("schluss " + t);

        System.exit(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BillingPopup getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  produktbezeichnung  DOCUMENT ME!
     */
    public void setProdukt(final String produktbezeichnung) {
        ((DefaultComboBoxModel<String>)jComboBox1.getModel()).removeAllElements();
        for (final BerechtigungspruefungKonfiguration.ProduktTyp produkt
                    : BerechtigungspruefungKonfiguration.INSTANCE.getProdukte()) {
            if (produktbezeichnung.equals(produkt.getProduktbezeichnung())) {
                ((DefaultComboBoxModel<String>)jComboBox1.getModel()).addElement(
                    "<html><i>kein Berechtigungsgrund ausgewählt");
                for (final String berechtigungsgrund : produkt.getBerechtigungsgruende()) {
                    ((DefaultComboBoxModel<String>)jComboBox1.getModel()).addElement(berechtigungsgrund);
                }
                break;
            }
        }
        jTextArea1.setText("");
        file = null;
        updateFileLabel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   delimiter  DOCUMENT ME!
     * @param   strings    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String implode(final String delimiter, final String... strings) {
        if (strings.length == 0) {
            return "";
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append(strings[0]);
            for (int index = 1; index < strings.length; index++) {
                sb.append(delimiter);
                sb.append(strings[index]);
            }
            return sb.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileData            DOCUMENT ME!
     * @param   fileName            DOCUMENT ME!
     * @param   berechtigungsgrund  DOCUMENT ME!
     * @param   begruendung         DOCUMENT ME!
     * @param   downloadInfo        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String requestPruefung(
            final byte[] fileData,
            final String fileName,
            final String berechtigungsgrund,
            final String begruendung,
            final BerechtigungspruefungDownloadInfo downloadInfo) {
        final Collection<ServerActionParameter> params = new ArrayList<ServerActionParameter>();
        try {
            params.add(new ServerActionParameter<String>(
                    BerechtigungspruefungAnfrageServerAction.ParameterType.DATEINAME.toString(),
                    fileName));

            params.add(new ServerActionParameter<String>(
                    BerechtigungspruefungAnfrageServerAction.ParameterType.BERECHTIGUNGSGRUND.toString(),
                    berechtigungsgrund));

            params.add(new ServerActionParameter<String>(
                    BerechtigungspruefungAnfrageServerAction.ParameterType.BEGRUENDUNG.toString(),
                    begruendung));

            final ObjectMapper mapper = new ObjectMapper();
            params.add(new ServerActionParameter<String>(
                    BerechtigungspruefungAnfrageServerAction.ParameterType.DOWNLOADINFO_JSON.toString(),
                    mapper.writeValueAsString(downloadInfo)));

            final Object ret = SessionManager.getSession()
                        .getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                            BerechtigungspruefungAnfrageServerAction.TASK_NAME,
                            SessionManager.getSession().getUser().getDomain(),
                            fileData,
                            params.toArray(new ServerActionParameter[0]));
            return (String)ret;
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo  DOCUMENT ME!
     * @param  callback      DOCUMENT ME!
     */
    public void doAnfrage(final BerechtigungspruefungDownloadInfo downloadInfo, final Callback callback) {
        new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {
                    final String fileName = (file != null) ? file.getName() : null;
                    final byte[] fileData = (file != null) ? FileUtils.readFileToByteArray(file) : null;

                    final String anfrageSchluessel = requestPruefung(
                            fileData,
                            fileName,
                            jComboBox1.getSelectedItem().toString(),
                            jTextArea1.getText(),
                            downloadInfo);

                    return anfrageSchluessel;
                }

                @Override
                protected void done() {
                    try {
                        final String anfrageSchluessel = get();
                        callback.callback(anfrageSchluessel);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        // TODO SHOW ERROR
                    }
                }
            }.execute();
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static interface Callback {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  anfrageSchluessel  DOCUMENT ME!
         */
        void callback(final String anfrageSchluessel);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final BillingPopup INSTANCE = new BillingPopup();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
