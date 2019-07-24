/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.permission.Policy;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.PotenzialFlaechenPrintHelper;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfKampagneEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PfKampagneEditor.class);
//    private static String REPORT_POTENZIALFLAECHE_URL =
//        "/de/cismet/cids/custom/reports/wunda_blau/potenzialflaechen_full.jasper";

    //~ Instance fields --------------------------------------------------------

    private final MetaClass NUTZUNG_MC;
    private final MetaClass OEPNV_MC;

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private JEditorPane ep;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArt2;
    private javax.swing.JButton btnRemoveArt2;
    private javax.swing.JButton btnReport;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbGeom;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbSteckbrief;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVeroeffentlicht;
    private javax.swing.JEditorPane epFlaeche;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblGeometrie5;
    private javax.swing.JLabel lblGeometrie6;
    private javax.swing.JLabel lblSteckbrief;
    private javax.swing.JLabel lblVeroeffentlich;
    private javax.swing.JList<Object> lstFlaechen;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private javax.swing.JPanel panArtControls2;
    private javax.swing.JPanel panBeschreibungBody;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panPrintButton;
    private javax.swing.JPanel panTitle;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JLabel txtTitle;
    private javax.swing.JLabel txtTitle1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GrundwassermessstelleEditor.
     */
    public PfKampagneEditor() {
        this(true);
    }

    /**
     * Creates a new GrundwassermessstelleEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public PfKampagneEditor(final boolean editable) {
        NUTZUNG_MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_nutzung",
                getConnectionContext()); // NOI18N
        OEPNV_MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_oepnv",
                getConnectionContext()); // NOI18N

        this.editable = editable;
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
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "pf_potenzialflaeche",
            1,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        lstFlaechen.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList<?> list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final Component c = super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);

                    if (c instanceof JLabel) {
                        if (value instanceof CidsBean) {
                            final CidsBean flaeche = ((CidsBean)value);
                            String name = (String)flaeche.getProperty("bezeichnung");

                            if (name == null) {
                                name = "unbenannte Potenzialfläche";
                            }
                            ((JLabel)c).setText(name);
                        }
                    }

                    return c;
                }
            });

        if (!editable) {
            RendererTools.makeReadOnly(txtBezeichnung);
            RendererTools.makeReadOnly(cbSteckbrief);
            RendererTools.makeReadOnly(cbVeroeffentlicht);
            RendererTools.makeReadOnly(epFlaeche);
            panArtControls2.setVisible(false);
            cbGeom.setVisible(false);
            lblGeometrie5.setVisible(false);
        }
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

        panTitle = new javax.swing.JPanel();
        txtTitle = new javax.swing.JLabel();
        txtTitle1 = new javax.swing.JLabel();
        panPrintButton = new javax.swing.JPanel();
        btnReport = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        buttonGroup1 = new javax.swing.ButtonGroup();
        panMain = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        panBeschreibungBody = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        lblBeschreibung = new javax.swing.JLabel();
        lblVeroeffentlich = new javax.swing.JLabel();
        lblSteckbrief = new javax.swing.JLabel();
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        jScrollPane7 = new javax.swing.JScrollPane();
        epFlaeche = new javax.swing.JEditorPane();
        cbVeroeffentlicht = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbSteckbrief = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblGeometrie6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstFlaechen = new javax.swing.JList<>();
        panArtControls2 = new javax.swing.JPanel();
        btnAddArt2 = new javax.swing.JButton();
        btnRemoveArt2 = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panTitle.add(txtTitle, gridBagConstraints);

        txtTitle1.setFont(new java.awt.Font("DejaVu Sans", 1, 18));                                           // NOI18N
        txtTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            txtTitle1,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.txtTitle1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panTitle.add(txtTitle1, gridBagConstraints);

        panPrintButton.setOpaque(false);
        panPrintButton.setLayout(new java.awt.GridBagLayout());

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png")));                    // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.btnReport.text")); // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfKampagneEditor.class,
                "PfKampagneEditor.btnReport.toolTipText"));                                                   // NOI18N
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        panTitle.add(panPrintButton, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        panAllgemein.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setFont(lblBeschreibungTitle.getFont());
        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblBeschreibungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(lblBeschreibungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein.add(panBeschreibungTitle, gridBagConstraints);

        panBeschreibungBody.setOpaque(false);
        panBeschreibungBody.setLayout(new java.awt.GridBagLayout());

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setFont(lblBezeichnung.getFont().deriveFont(
                lblBezeichnung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBezeichnung,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBezeichnung, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBezeichnung.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtBezeichnungFocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtBezeichnung, gridBagConstraints);

        lblBeschreibung.setFont(lblBeschreibung.getFont().deriveFont(
                lblBeschreibung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibung,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblBeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBeschreibung, gridBagConstraints);

        lblVeroeffentlich.setFont(lblVeroeffentlich.getFont().deriveFont(
                lblVeroeffentlich.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVeroeffentlich,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblVeroeffentlich.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblVeroeffentlich, gridBagConstraints);

        lblSteckbrief.setFont(lblSteckbrief.getFont().deriveFont(
                lblSteckbrief.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSteckbrief,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblSteckbrief.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblSteckbrief, gridBagConstraints);

        lblGeometrie5.setFont(lblGeometrie5.getFont().deriveFont(
                lblGeometrie5.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie5,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblGeometrie5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblGeometrie5, gridBagConstraints);

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbGeom, gridBagConstraints);

        jScrollPane7.setMaximumSize(new java.awt.Dimension(80, 80));
        jScrollPane7.setMinimumSize(new java.awt.Dimension(80, 80));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(80, 100));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung}"),
                epFlaeche,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(epFlaeche);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.veroeffentlichkeitsstatus}"),
                cbVeroeffentlicht,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbVeroeffentlicht, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.steckbrieftemplate}"),
                cbSteckbrief,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbSteckbrief, gridBagConstraints);

        lblGeometrie6.setFont(lblGeometrie6.getFont().deriveFont(
                lblGeometrie6.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie6,
            org.openide.util.NbBundle.getMessage(PfKampagneEditor.class, "PfKampagneEditor.lblGeometrie6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblGeometrie6, gridBagConstraints);

        jScrollPane6.setMaximumSize(new java.awt.Dimension(254, 100));
        jScrollPane6.setMinimumSize(new java.awt.Dimension(254, 100));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(254, 100));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.zugeordnete_flaechen}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstFlaechen);
        bindingGroup.addBinding(jListBinding);

        jScrollPane6.setViewportView(lstFlaechen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane6, gridBagConstraints);

        panArtControls2.setOpaque(false);
        panArtControls2.setLayout(new java.awt.GridBagLayout());

        btnAddArt2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddArt2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddArt2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddArt2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddArt2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddArt2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        panArtControls2.add(btnAddArt2, gridBagConstraints);

        btnRemoveArt2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveArt2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveArt2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        panArtControls2.add(btnRemoveArt2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBeschreibungBody.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAllgemein.add(panBeschreibungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(panAllgemein, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMain.add(filler9, gridBagConstraints);

        add(panMain, "grunddaten");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnungFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtBezeichnungFocusLost
        txtTitle.setText(getTitle());
    }                                                                           //GEN-LAST:event_txtBezeichnungFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddArt2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddArt2ActionPerformed
        PfPotenzialflaecheEditor.setLastKampagne(cidsBean);

        final MetaClass MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_potenzialflaeche",
                getConnectionContext());

        try {
            final CidsBean cb = CidsBean.createNewCidsBeanFromTableName(MC.getDomain(),
                    MC.getTableName(),
                    getConnectionContext());

            final MetaObjectNode metaObjectNode = new MetaObjectNode(
                    -1,
                    SessionManager.getSession().getUser().getDomain(),
                    cb.getMetaObject(),
                    null,
                    null,
                    true,
                    Policy.createWIKIPolicy(),
                    -1,
                    null,
                    false);
            final DefaultMetaTreeNode metaTreeNode = new ObjectTreeNode(metaObjectNode, getConnectionContext());
            ComponentRegistry.getRegistry().showComponent(ComponentRegistry.ATTRIBUTE_EDITOR);
            ComponentRegistry.getRegistry().getAttributeEditor().setTreeNode(metaTreeNode);
        } catch (Exception e) {
            LOG.error("Error while creating a new object", e);
        }
    } //GEN-LAST:event_btnAddArt2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveArt2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveArt2ActionPerformed
        final List<Object> selection = lstFlaechen.getSelectedValuesList();
        if ((selection != null) && (selection.size() > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die Fläche wirklich gelöscht werden?",
                    "Fläche entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection artCol = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        "zugeordnete_flaechen");
                if (artCol != null) {
                    for (final Object cur : selection) {
                        try {
                            artCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                 //GEN-LAST:event_btnRemoveArt2ActionPerformed

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
                    return new JRBeanCollectionDataSource(new ArrayList<>(Arrays.asList(cidsBean)));
                }
            };

        final List<CidsBean> flaechen = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "zugeordnete_flaechen");
        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new PotenzialFlaechenPrintHelper.PotenzialflaecheReportParameterGenerator(flaechen.get(0));

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = "Potenzialflaeche"
                        + flaechen.get(0).toString();
            final String downloadTitle = "Potenzialflaeche "
                        + flaechen.get(0).toString();
            try {
                final JasperReport report = (JasperReport)SessionManager.getSession().getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                    GetServerResourceServerAction.TASK_NAME,
                                    CidsBeanSupport.DOMAIN_NAME,
                                    WundaBlauServerResources.POTENZIALFLAECHEN_JASPER.getValue(),
                                    connectionContext);

                final JasperReportDownload download = new JasperReportDownload(
                        report,
                        parametersGenerator,
                        dataSourceGenerator,
                        jobname,
                        downloadTitle,
                        filename);
                DownloadManager.instance().add(download);
            } catch (Exception e) {
                LOG.error("Cannot create report", e);
                ObjectRendererUtils.showExceptionWindowToUser("Fehler Erstellen des Reports", e, this);
            }
        }
    } //GEN-LAST:event_btnReportActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();
            txtTitle.setText(getTitle());
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public String getTitle() {
        if (cidsBean.getProperty("bezeichnung") == null) {
            return "";
        } else {
            return (String)cidsBean.getProperty("bezeichnung");
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
