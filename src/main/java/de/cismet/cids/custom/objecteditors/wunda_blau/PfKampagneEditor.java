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

import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.permission.Policy;

import org.jdesktop.beansbinding.Converter;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.PotenzialflaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
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

    private static Converter<String, Color> COLORCODE_CONVERTER = new Converter<String, Color>() {

            @Override
            public String convertReverse(final Color color) {
                if (color == null) {
                    return null;
                }
                final String hex = Integer.toHexString(color.getRGB());
                return String.format("#%s", hex.substring(hex.length() - 6));
            }

            @Override
            public Color convertForward(final String string) {
                if (string == null) {
                    return null;
                }
                return Color.decode(string);
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArt2;
    private javax.swing.JButton btnRemoveArt2;
    private javax.swing.JButton btnReport;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbGeom;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVeroeffentlicht;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<CidsBean> jList1;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblBezeichnung1;
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
    private javax.swing.JPanel panTitle;
    private javax.swing.JTextArea taBeschreibung;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtBezeichnung1;
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
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

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
        RendererTools.makeUneditable(lstFlaechen, true);

        ((CidsSearchResultsList)this.lstFlaechen).initWithConnectionContext(connectionContext);
        try {
            new CidsBeanDropTarget(lstFlaechen);
        } catch (final Exception ex) {
            LOG.warn("Error while creating CidsBeanDropTarget", ex); // NOI18N
        }

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
            RendererTools.makeReadOnly(jList1);
            RendererTools.makeReadOnly(cbVeroeffentlicht);
            RendererTools.makeReadOnly(taBeschreibung);
            RendererTools.makeReadOnly(txtBezeichnung1);
            RendererTools.makeReadOnly(jCheckBox1);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        taBeschreibung = new javax.swing.JTextArea();
        lblBezeichnung1 = new javax.swing.JLabel();
        txtBezeichnung1 = new javax.swing.JTextField();
        lblVeroeffentlich = new javax.swing.JLabel();
        lblSteckbrief = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        cbVeroeffentlicht = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblGeometrie6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstFlaechen = new DroppedPfList();
        panArtControls2 = new javax.swing.JPanel();
        btnAddArt2 = new javax.swing.JButton();
        btnRemoveArt2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

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

        txtTitle1.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(txtTitle1, "Kampagne: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panTitle.add(txtTitle1, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                PfKampagneEditor.class,
                "PfPotenzialflaecheTitlePanel.btnReport.text"));                           // NOI18N
        btnReport.setToolTipText("Steckbriefe der Kampagne erzeugen.");
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

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
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle, "Allgemein");
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
        org.openide.awt.Mnemonics.setLocalizedText(lblBezeichnung, "Bezeichnung:");
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtBezeichnung, gridBagConstraints);

        lblBeschreibung.setFont(lblBeschreibung.getFont().deriveFont(
                lblBeschreibung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibung, "Beschreibung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBeschreibung, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        taBeschreibung.setColumns(20);
        taBeschreibung.setLineWrap(true);
        taBeschreibung.setRows(5);
        taBeschreibung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung}"),
                taBeschreibung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(taBeschreibung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane1, gridBagConstraints);

        lblBezeichnung1.setFont(lblBezeichnung1.getFont().deriveFont(
                lblBezeichnung1.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblBezeichnung1, "Farbcode (#rrggbb):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBezeichnung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.colorcode}"),
                txtBezeichnung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBezeichnung1.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtBezeichnung1FocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtBezeichnung1, gridBagConstraints);

        lblVeroeffentlich.setFont(lblVeroeffentlich.getFont().deriveFont(
                lblVeroeffentlich.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblVeroeffentlich, "veröffentlicht für:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblVeroeffentlich, gridBagConstraints);

        lblSteckbrief.setFont(lblSteckbrief.getFont().deriveFont(
                lblSteckbrief.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblSteckbrief, "Steckbrieftemplates:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblSteckbrief, gridBagConstraints);

        jList1.setVisibleRowCount(3);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jScrollPane3.setViewportView(jList1);
        jList1.setCellRenderer(new SteckbriefListCellRenderer());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane3, gridBagConstraints);

        lblGeometrie5.setFont(lblGeometrie5.getFont().deriveFont(
                lblGeometrie5.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblGeometrie5, "Geometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
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
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbGeom, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.veroeffentlichkeitsstatus}"),
                cbVeroeffentlicht,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbVeroeffentlicht, gridBagConstraints);

        lblGeometrie6.setFont(lblGeometrie6.getFont().deriveFont(
                lblGeometrie6.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblGeometrie6, "Zugeordnete Flächen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane6, gridBagConstraints);

        panArtControls2.setOpaque(false);
        panArtControls2.setLayout(new java.awt.GridBagLayout());

        btnAddArt2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddArt2.setBorderPainted(false);
        btnAddArt2.setContentAreaFilled(false);
        btnAddArt2.setFocusPainted(false);
        btnAddArt2.setMaximumSize(new java.awt.Dimension(32, 32));
        btnAddArt2.setMinimumSize(new java.awt.Dimension(32, 32));
        btnAddArt2.setPreferredSize(new java.awt.Dimension(32, 32));
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
        btnRemoveArt2.setBorderPainted(false);
        btnRemoveArt2.setContentAreaFilled(false);
        btnRemoveArt2.setFocusPainted(false);
        btnRemoveArt2.setMaximumSize(new java.awt.Dimension(32, 32));
        btnRemoveArt2.setMinimumSize(new java.awt.Dimension(32, 32));
        btnRemoveArt2.setPreferredSize(new java.awt.Dimension(32, 32));
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls2, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/treeicons/wunda_demo/star.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton1.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton1.setPreferredSize(new java.awt.Dimension(32, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jList1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"),
                jButton1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        jPanel7.add(jButton1, gridBagConstraints);
        jButton1.setVisible(isEditable());

        jLabel1.setOpaque(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.colorcode}"),
                jLabel1,
                org.jdesktop.beansbinding.BeanProperty.create("background"));
        binding.setSourceNullValue(java.awt.Color.red);
        binding.setSourceUnreadableValue(java.awt.Color.red);
        binding.setConverter(COLORCODE_CONVERTER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel7.add(jLabel1, gridBagConstraints);

        jCheckBox1.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.veroeffentlicht}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jCheckBox1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(panAllgemein, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMain.add(filler1, gridBagConstraints);

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
        createAndGotoPotenzialflaeche(null);
    }                                                                              //GEN-LAST:event_btnAddArt2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  origPfBean  DOCUMENT ME!
     */
    private void createAndGotoPotenzialflaeche(final CidsBean origPfBean) {
        try {
            final CidsBean copyBean = PfPotenzialflaecheEditor.createCopyOf(origPfBean, getConnectionContext());
            copyBean.setProperty("kampagne", getCidsBean());
            final MetaObjectNode metaObjectNode = new MetaObjectNode(
                    -1,
                    SessionManager.getSession().getUser().getDomain(),
                    copyBean.getMetaObject(),
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
    }
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
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (isEditable() && (evt.getClickCount() > 1)) {
            selectMainSteckbrief();
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        selectMainSteckbrief();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnung1FocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtBezeichnung1FocusLost
        // TODO add your handling code here:
    } //GEN-LAST:event_txtBezeichnung1FocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        PfPotenzialflaecheReportGenerator.startDownloadForKampagne(getCidsBean(), getConnectionContext());
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void selectMainSteckbrief() {
        final CidsBean steckbrieftemplateBean = jList1.getSelectedValue();
        if (steckbrieftemplateBean != null) {
            try {
                cidsBean.setProperty("haupt_steckbrieftemplate_id", steckbrieftemplateBean.getMetaObject().getId());
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
            jList1.repaint();
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            final DefaultListModel<CidsBean> model = new DefaultListModel<>();
            for (final CidsBean steckbriefBean : cidsBean.getBeanCollectionProperty("n_steckbrieftemplates")) {
                model.addElement(steckbriefBean);
            }
            jList1.setModel(model);
            final Integer mainSteckbriefId = (Integer)cidsBean.getProperty("haupt_steckbrieftemplate_id");
            CidsBean matchingSteckbriefBean = null;
            if (mainSteckbriefId != null) {
                for (final CidsBean steckbriefBean : cidsBean.getBeanCollectionProperty("n_steckbrieftemplates")) {
                    if ((steckbriefBean != null) && (steckbriefBean.getMetaObject().getId() == mainSteckbriefId)) {
                        matchingSteckbriefBean = steckbriefBean;
                        break;
                    }
                }
            }
            try {
                if (isEditable()) {
                    if (cidsBean.getBeanCollectionProperty("n_steckbrieftemplates").isEmpty()
                                || (matchingSteckbriefBean == null)) {
                        cidsBean.setProperty("haupt_steckbrieftemplate_id", null);
                    }
                    if (cidsBean.getBeanCollectionProperty("n_steckbrieftemplates").size() == 1) {
                        final CidsBean onlyOneSteckbriefBean = cidsBean.getBeanCollectionProperty(
                                "n_steckbrieftemplates")
                                    .iterator()
                                    .next();
                        cidsBean.setProperty(
                            "haupt_steckbrieftemplate_id",
                            onlyOneSteckbriefBean.getMetaObject().getId());
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }

            final PotenzialflaecheSearch search = new PotenzialflaecheSearch(true);
            final PotenzialflaecheSearch.Configuration configuration = new PotenzialflaecheSearch.Configuration();
            configuration.addFilter(PotenzialflaecheReportServerAction.Property.KAMPAGNE, new MetaObjectNode(cidsBean));
            search.setConfiguration(configuration);
            ((CidsSearchResultsList)lstFlaechen).setSearch(search);
            ((CidsSearchResultsList)this.lstFlaechen).refresh();
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DroppedPfList extends CidsSearchResultsList implements CidsBeanDropListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DroppedPfList object.
         */
        private DroppedPfList() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> origBeans) {
            if (isEditable() && (origBeans != null) && (origBeans.size() == 1)) {
                for (final CidsBean origPfBean : origBeans) {
                    if (origPfBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                    "pf_potenzialflaeche")) {
                        createAndGotoPotenzialflaeche(origPfBean);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SteckbriefListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component component = super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);
            if ((component instanceof JLabel)) {
                final JLabel label = (JLabel)component;
                if (value instanceof CidsBean) {
                    final CidsBean steackbriefBean = (CidsBean)value;
                    final Integer mainSteckbriefId = (Integer)getCidsBean().getProperty("haupt_steckbrieftemplate_id");
                    if ((mainSteckbriefId != null) && (steackbriefBean.getMetaObject().getId() == mainSteckbriefId)) {
                        label.setText("<html><b>" + (String)steackbriefBean.getProperty("bezeichnung") + "</b></html>");
                    } else {
                        label.setText((String)steackbriefBean.getProperty("bezeichnung"));
                    }
                } else if (value == null) {
                    label.setText(" ");
                }
            }
            return component;
        }
    }
}
