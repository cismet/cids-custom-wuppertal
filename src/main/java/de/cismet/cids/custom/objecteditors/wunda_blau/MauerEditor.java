/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.tools.gui.FooterComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauerEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider {

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private String title;
    private final Logger log = Logger.getLogger(MauerEditor.class);
    private MappingComponent map;
    private boolean editable;
    private CardLayout cardLayout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel lblBeschreibungGelaender;
    private javax.swing.JLabel lblEigentümer;
    private javax.swing.JLabel lblEingriffAnsicht;
    private javax.swing.JLabel lblEingriffAnsicht1;
    private javax.swing.JLabel lblEingriffGeleander;
    private javax.swing.JLabel lblEingriffKopf;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller1;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblFiller3;
    private javax.swing.JLabel lblFiller4;
    private javax.swing.JLabel lblGelaenderHeader;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblHoeheMin;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblKofpAnsicht;
    private javax.swing.JLabel lblKofpAnsicht1;
    private javax.swing.JLabel lblKofpHeader;
    private javax.swing.JLabel lblLaenge;
    private javax.swing.JLabel lblLagebeschreibung;
    private javax.swing.JLabel lblMaterialTyp;
    private javax.swing.JLabel lblNeigung;
    private javax.swing.JLabel lblPruefung1;
    private javax.swing.JLabel lblSanKostenAnsicht;
    private javax.swing.JLabel lblSanKostenAnsicht1;
    private javax.swing.JLabel lblSanKostenGelaender;
    private javax.swing.JLabel lblSanKostenKopf;
    private javax.swing.JLabel lblSanMassnahmenAnsicht;
    private javax.swing.JLabel lblSanMassnahmenGelaender;
    private javax.swing.JLabel lblSanMassnahmenGruendung;
    private javax.swing.JLabel lblSanMassnahmenKopf;
    private javax.swing.JLabel lblStuetzmauer;
    private javax.swing.JLabel lblUmgebung;
    private javax.swing.JLabel lblZustandAnsicht;
    private javax.swing.JLabel lblZustandGelaender;
    private javax.swing.JLabel lblZustandGruendung;
    private javax.swing.JLabel lblZustandKopf;
    private javax.swing.JLabel lblbeschreibungAnsicht;
    private javax.swing.JLabel lblbeschreibungGruendung;
    private javax.swing.JLabel lblbeschreibungKopf;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private de.cismet.tools.gui.RoundedPanel pnlAllgemein;
    private de.cismet.tools.gui.RoundedPanel pnlAnsicht;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private de.cismet.tools.gui.RoundedPanel pnlGelaender;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGelaenderHeader;
    private de.cismet.tools.gui.RoundedPanel pnlGruendung;
    private de.cismet.tools.gui.SemiRoundedPanel pnlGruendungHeader;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderAllgemein;
    private de.cismet.tools.gui.RoundedPanel pnlKopf;
    private de.cismet.tools.gui.SemiRoundedPanel pnlKopfAnsicht;
    private de.cismet.tools.gui.SemiRoundedPanel pnlKopfHeader;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel pnlScrollPane;
    private javax.swing.JTextArea taBeschreibungAnsicht;
    private javax.swing.JTextArea taBeschreibungGelaender;
    private javax.swing.JTextArea taBeschreibungGruendung;
    private javax.swing.JTextArea taBeschreibungKopf;
    private javax.swing.JTextArea taSanMassnahmeAnsicht;
    private javax.swing.JTextArea taSanMassnahmeGelaender;
    private javax.swing.JTextArea taSanMassnahmeGruendung;
    private javax.swing.JTextArea taSanMassnahmeKopf;
    private javax.swing.JTextField tfEingriffAnsicht;
    private javax.swing.JTextField tfEingriffGelaender;
    private javax.swing.JTextField tfEingriffGruendung;
    private javax.swing.JTextField tfEingriffKopf;
    private javax.swing.JTextField tfSanKostenAnsicht;
    private javax.swing.JTextField tfSanKostenGelaender;
    private javax.swing.JTextField tfSanKostenGruendung;
    private javax.swing.JTextField tfSanKostenKopf;
    private javax.swing.JTextField tfZustandAnsicht;
    private javax.swing.JTextField tfZustandGelaender;
    private javax.swing.JTextField tfZustandGruendung;
    private javax.swing.JTextField tfZustandKopf;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauerEditor.
     */
    public MauerEditor() {
        this(true);
    }

    /**
     * Creates a new MauerEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public MauerEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        map = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(map, BorderLayout.CENTER);

        setEditable();
        initMap();
        final LayoutManager layout = getLayout();
        if (layout instanceof CardLayout) {
            cardLayout = (CardLayout)layout;
            cardLayout.show(this, "card1");
        }
    }

    //~ Methods ----------------------------------------------------------------

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
        pnlCard1 = new javax.swing.JPanel();
        pnlAllgemein = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderAllgemein = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        lblLagebeschreibung = new javax.swing.JLabel();
        lblFiller2 = new javax.swing.JLabel();
        lblUmgebung = new javax.swing.JLabel();
        lblNeigung = new javax.swing.JLabel();
        lblStuetzmauer = new javax.swing.JLabel();
        lblMaterialTyp = new javax.swing.JLabel();
        lblHoeheMin = new javax.swing.JLabel();
        lblLaenge = new javax.swing.JLabel();
        lblEigentümer = new javax.swing.JLabel();
        lblPruefung1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        pnlMap = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlScrollPane = new de.cismet.tools.gui.RoundedPanel();
        pnlGelaender = new de.cismet.tools.gui.RoundedPanel();
        pnlGelaenderHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGelaenderHeader = new javax.swing.JLabel();
        lblFiller = new javax.swing.JLabel();
        lblBeschreibungGelaender = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        taBeschreibungGelaender = new javax.swing.JTextArea();
        lblZustandGelaender = new javax.swing.JLabel();
        lblSanKostenGelaender = new javax.swing.JLabel();
        lblSanMassnahmenGelaender = new javax.swing.JLabel();
        lblEingriffGeleander = new javax.swing.JLabel();
        tfZustandGelaender = new javax.swing.JTextField();
        tfSanKostenGelaender = new javax.swing.JTextField();
        tfEingriffGelaender = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        taSanMassnahmeGelaender = new javax.swing.JTextArea();
        pnlKopf = new de.cismet.tools.gui.RoundedPanel();
        pnlKopfHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpHeader = new javax.swing.JLabel();
        lblFiller1 = new javax.swing.JLabel();
        lblbeschreibungKopf = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        taBeschreibungKopf = new javax.swing.JTextArea();
        lblZustandKopf = new javax.swing.JLabel();
        lblSanMassnahmenKopf = new javax.swing.JLabel();
        lblSanKostenKopf = new javax.swing.JLabel();
        lblEingriffKopf = new javax.swing.JLabel();
        tfZustandKopf = new javax.swing.JTextField();
        tfSanKostenKopf = new javax.swing.JTextField();
        tfEingriffKopf = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        taSanMassnahmeKopf = new javax.swing.JTextArea();
        pnlAnsicht = new de.cismet.tools.gui.RoundedPanel();
        pnlKopfAnsicht = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht = new javax.swing.JLabel();
        lblFiller3 = new javax.swing.JLabel();
        lblbeschreibungAnsicht = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        taBeschreibungAnsicht = new javax.swing.JTextArea();
        lblZustandAnsicht = new javax.swing.JLabel();
        lblSanMassnahmenAnsicht = new javax.swing.JLabel();
        lblSanKostenAnsicht = new javax.swing.JLabel();
        lblEingriffAnsicht = new javax.swing.JLabel();
        tfZustandAnsicht = new javax.swing.JTextField();
        tfSanKostenAnsicht = new javax.swing.JTextField();
        tfEingriffAnsicht = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        taSanMassnahmeAnsicht = new javax.swing.JTextArea();
        pnlGruendung = new de.cismet.tools.gui.RoundedPanel();
        pnlGruendungHeader = new de.cismet.tools.gui.SemiRoundedPanel();
        lblKofpAnsicht1 = new javax.swing.JLabel();
        lblFiller4 = new javax.swing.JLabel();
        lblbeschreibungGruendung = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        taBeschreibungGruendung = new javax.swing.JTextArea();
        lblZustandGruendung = new javax.swing.JLabel();
        lblSanMassnahmenGruendung = new javax.swing.JLabel();
        lblSanKostenAnsicht1 = new javax.swing.JLabel();
        lblEingriffAnsicht1 = new javax.swing.JLabel();
        tfZustandGruendung = new javax.swing.JTextField();
        tfSanKostenGruendung = new javax.swing.JTextField();
        tfEingriffGruendung = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        taSanMassnahmeGruendung = new javax.swing.JTextArea();
        pnlCard2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        lblInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblInfo.text")); // NOI18N
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnInfo.text")); // NOI18N
        btnInfo.setBorderPainted(false);
        btnInfo.setContentAreaFilled(false);
        btnInfo.setEnabled(false);
        btnInfo.setFocusPainted(false);
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        btnImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnImages.text")); // NOI18N
        btnImages.setBorderPainted(false);
        btnImages.setContentAreaFilled(false);
        btnImages.setFocusPainted(false);
        btnImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImagesActionPerformed(evt);
            }
        });
        panRight.add(btnImages);

        lblImages.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        lblImages.setForeground(new java.awt.Color(255, 255, 255));
        lblImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblImages.text")); // NOI18N
        panRight.add(lblImages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        setMinimumSize(new java.awt.Dimension(807, 485));
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        pnlAllgemein.setPreferredSize(new java.awt.Dimension(600, 430));
        pnlAllgemein.setLayout(new java.awt.GridBagLayout());

        pnlHeaderAllgemein.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderAllgemein.setMinimumSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein.setPreferredSize(new java.awt.Dimension(109, 24));
        pnlHeaderAllgemein.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblHeaderAllgemein.text")); // NOI18N
        pnlHeaderAllgemein.add(lblHeaderAllgemein);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlAllgemein.add(pnlHeaderAllgemein, gridBagConstraints);

        lblLagebeschreibung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblLagebeschreibung, gridBagConstraints);

        lblFiller2.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(lblFiller2, gridBagConstraints);

        lblUmgebung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblUmgebung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblUmgebung, gridBagConstraints);

        lblNeigung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblNeigung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblNeigung, gridBagConstraints);

        lblStuetzmauer.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblStuetzmauer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblStuetzmauer, gridBagConstraints);

        lblMaterialTyp.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblMaterialTyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblMaterialTyp, gridBagConstraints);

        lblHoeheMin.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblHoeheMin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblHoeheMin, gridBagConstraints);

        lblLaenge.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLaenge.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblLaenge, gridBagConstraints);

        lblEigentümer.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblEigentümer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblEigentümer, gridBagConstraints);

        lblPruefung1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblPruefung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlAllgemein.add(lblPruefung1, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(26, 60));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(262, 60));
        jScrollPane1.setRequestFocusEnabled(false);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setPreferredSize(new java.awt.Dimension(260, 34));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagebeschreibung}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue("unreadable");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jScrollPane1, gridBagConstraints);

        jTextField2.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField2.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebung}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField2, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(26, 60));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(262, 60));

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setPreferredSize(new java.awt.Dimension(260, 34));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.neigung}"), jTextArea2, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jScrollPane2, gridBagConstraints);

        jTextField3.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField3.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauertyp}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue("unreadable");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField3, gridBagConstraints);

        jTextField4.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField4.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.materialtyp}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField4, gridBagConstraints);

        jTextField5.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField5.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe}"), jTextField5, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField5, gridBagConstraints);

        jTextField6.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField6.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField6, gridBagConstraints);

        jTextField7.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField7.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"), jTextField7, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(jTextField7, gridBagConstraints);

        defaultBindableDateChooser1.setPreferredSize(new java.awt.Dimension(124, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hauptpruefung_1}"), defaultBindableDateChooser1, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser1.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlAllgemein.add(defaultBindableDateChooser1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(pnlAllgemein, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(pnlMap, gridBagConstraints);

        jScrollPane3.setViewportBorder(null);
        jScrollPane3.setOpaque(false);

        pnlScrollPane.setOpaque(true);
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlGelaender.setMinimumSize(new java.awt.Dimension(470, 380));
        pnlGelaender.setPreferredSize(new java.awt.Dimension(470, 380));
        pnlGelaender.setLayout(new java.awt.GridBagLayout());

        pnlGelaenderHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlGelaenderHeader.setLayout(new java.awt.FlowLayout());

        lblGelaenderHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblGelaenderHeader.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblGelaenderHeader.text")); // NOI18N
        pnlGelaenderHeader.add(lblGelaenderHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlGelaender.add(pnlGelaenderHeader, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGelaender.add(lblFiller, gridBagConstraints);

        lblBeschreibungGelaender.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblBeschreibungGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(lblBeschreibungGelaender, gridBagConstraints);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(262, 87));

        taBeschreibungGelaender.setColumns(20);
        taBeschreibungGelaender.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_gelaender}"), taBeschreibungGelaender, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(taBeschreibungGelaender);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(jScrollPane4, gridBagConstraints);

        lblZustandGelaender.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblZustandGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(lblZustandGelaender, gridBagConstraints);

        lblSanKostenGelaender.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanKostenGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(lblSanKostenGelaender, gridBagConstraints);

        lblSanMassnahmenGelaender.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanMassnahmenGelaender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(lblSanMassnahmenGelaender, gridBagConstraints);

        lblEingriffGeleander.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblEingriffGeleander.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(lblEingriffGeleander, gridBagConstraints);

        tfZustandGelaender.setMinimumSize(new java.awt.Dimension(100, 27));
        tfZustandGelaender.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_gelaender}"), tfZustandGelaender, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(tfZustandGelaender, gridBagConstraints);

        tfSanKostenGelaender.setMinimumSize(new java.awt.Dimension(100, 27));
        tfSanKostenGelaender.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_gelaender}"), tfSanKostenGelaender, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(tfSanKostenGelaender, gridBagConstraints);

        tfEingriffGelaender.setMinimumSize(new java.awt.Dimension(100, 27));
        tfEingriffGelaender.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_gelaender}"), tfEingriffGelaender, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(tfEingriffGelaender, gridBagConstraints);

        jScrollPane5.setMinimumSize(new java.awt.Dimension(26, 87));

        taSanMassnahmeGelaender.setColumns(20);
        taSanMassnahmeGelaender.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBeansan_massnahme_gelaender}"), taSanMassnahmeGelaender, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(taSanMassnahmeGelaender);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGelaender.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlScrollPane.add(pnlGelaender, gridBagConstraints);

        pnlKopf.setMinimumSize(new java.awt.Dimension(470, 380));
        pnlKopf.setPreferredSize(new java.awt.Dimension(470, 380));
        pnlKopf.setLayout(new java.awt.GridBagLayout());

        pnlKopfHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlKopfHeader.setLayout(new java.awt.FlowLayout());

        lblKofpHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpHeader.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblKofpHeader.text")); // NOI18N
        pnlKopfHeader.add(lblKofpHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlKopf.add(pnlKopfHeader, gridBagConstraints);

        lblFiller1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlKopf.add(lblFiller1, gridBagConstraints);

        lblbeschreibungKopf.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblbeschreibungKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(lblbeschreibungKopf, gridBagConstraints);

        jScrollPane6.setMinimumSize(new java.awt.Dimension(262, 87));

        taBeschreibungKopf.setColumns(20);
        taBeschreibungKopf.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_kopf}"), taBeschreibungKopf, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(taBeschreibungKopf);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(jScrollPane6, gridBagConstraints);

        lblZustandKopf.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblZustandKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(lblZustandKopf, gridBagConstraints);

        lblSanMassnahmenKopf.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanMassnahmenKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(lblSanMassnahmenKopf, gridBagConstraints);

        lblSanKostenKopf.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanKostenKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(lblSanKostenKopf, gridBagConstraints);

        lblEingriffKopf.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblEingriffKopf.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(lblEingriffKopf, gridBagConstraints);

        tfZustandKopf.setMinimumSize(new java.awt.Dimension(100, 27));
        tfZustandKopf.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_kopf}"), tfZustandKopf, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(tfZustandKopf, gridBagConstraints);

        tfSanKostenKopf.setMinimumSize(new java.awt.Dimension(100, 27));
        tfSanKostenKopf.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_kopf}"), tfSanKostenKopf, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(tfSanKostenKopf, gridBagConstraints);

        tfEingriffKopf.setMinimumSize(new java.awt.Dimension(100, 27));
        tfEingriffKopf.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_kopf}"), tfEingriffKopf, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(tfEingriffKopf, gridBagConstraints);

        jScrollPane7.setMinimumSize(new java.awt.Dimension(26, 87));

        taSanMassnahmeKopf.setColumns(20);
        taSanMassnahmeKopf.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahmen_kopf}"), taSanMassnahmeKopf, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(taSanMassnahmeKopf);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlKopf.add(jScrollPane7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlScrollPane.add(pnlKopf, gridBagConstraints);

        pnlAnsicht.setMinimumSize(new java.awt.Dimension(470, 380));
        pnlAnsicht.setPreferredSize(new java.awt.Dimension(470, 380));
        pnlAnsicht.setLayout(new java.awt.GridBagLayout());

        pnlKopfAnsicht.setBackground(new java.awt.Color(51, 51, 51));
        pnlKopfAnsicht.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblKofpAnsicht.text")); // NOI18N
        pnlKopfAnsicht.add(lblKofpAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlAnsicht.add(pnlKopfAnsicht, gridBagConstraints);

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAnsicht.add(lblFiller3, gridBagConstraints);

        lblbeschreibungAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblbeschreibungAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(lblbeschreibungAnsicht, gridBagConstraints);

        jScrollPane8.setMinimumSize(new java.awt.Dimension(262, 87));

        taBeschreibungAnsicht.setColumns(20);
        taBeschreibungAnsicht.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_ansicht}"), taBeschreibungAnsicht, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane8.setViewportView(taBeschreibungAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(jScrollPane8, gridBagConstraints);

        lblZustandAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblZustandAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(lblZustandAnsicht, gridBagConstraints);

        lblSanMassnahmenAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanMassnahmenAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(lblSanMassnahmenAnsicht, gridBagConstraints);

        lblSanKostenAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanKostenAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(lblSanKostenAnsicht, gridBagConstraints);

        lblEingriffAnsicht.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblEingriffAnsicht.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(lblEingriffAnsicht, gridBagConstraints);

        tfZustandAnsicht.setMinimumSize(new java.awt.Dimension(100, 27));
        tfZustandAnsicht.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand_ansicht}"), tfZustandAnsicht, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(tfZustandAnsicht, gridBagConstraints);

        tfSanKostenAnsicht.setMinimumSize(new java.awt.Dimension(100, 27));
        tfSanKostenAnsicht.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_ansicht}"), tfSanKostenAnsicht, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(tfSanKostenAnsicht, gridBagConstraints);

        tfEingriffAnsicht.setMinimumSize(new java.awt.Dimension(100, 27));
        tfEingriffAnsicht.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_ansicht}"), tfEingriffAnsicht, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(tfEingriffAnsicht, gridBagConstraints);

        jScrollPane9.setMinimumSize(new java.awt.Dimension(26, 87));

        taSanMassnahmeAnsicht.setColumns(20);
        taSanMassnahmeAnsicht.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahmen_ansicht}"), taSanMassnahmeAnsicht, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane9.setViewportView(taSanMassnahmeAnsicht);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAnsicht.add(jScrollPane9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlScrollPane.add(pnlAnsicht, gridBagConstraints);

        pnlGruendung.setMinimumSize(new java.awt.Dimension(470, 380));
        pnlGruendung.setPreferredSize(new java.awt.Dimension(470, 380));
        pnlGruendung.setLayout(new java.awt.GridBagLayout());

        pnlGruendungHeader.setBackground(new java.awt.Color(51, 51, 51));
        pnlGruendungHeader.setLayout(new java.awt.FlowLayout());

        lblKofpAnsicht1.setForeground(new java.awt.Color(255, 255, 255));
        lblKofpAnsicht1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblKofpAnsicht1.text")); // NOI18N
        pnlGruendungHeader.add(lblKofpAnsicht1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlGruendung.add(pnlGruendungHeader, gridBagConstraints);

        lblFiller4.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGruendung.add(lblFiller4, gridBagConstraints);

        lblbeschreibungGruendung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblbeschreibungGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(lblbeschreibungGruendung, gridBagConstraints);

        jScrollPane10.setMinimumSize(new java.awt.Dimension(262, 87));

        taBeschreibungGruendung.setColumns(20);
        taBeschreibungGruendung.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_gruendung}"), taBeschreibungGruendung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane10.setViewportView(taBeschreibungGruendung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(jScrollPane10, gridBagConstraints);

        lblZustandGruendung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblZustandGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(lblZustandGruendung, gridBagConstraints);

        lblSanMassnahmenGruendung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanMassnahmenGruendung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(lblSanMassnahmenGruendung, gridBagConstraints);

        lblSanKostenAnsicht1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblSanKostenAnsicht1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(lblSanKostenAnsicht1, gridBagConstraints);

        lblEingriffAnsicht1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblEingriffAnsicht1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(lblEingriffAnsicht1, gridBagConstraints);

        tfZustandGruendung.setMinimumSize(new java.awt.Dimension(100, 27));
        tfZustandGruendung.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean}"), tfZustandGruendung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(tfZustandGruendung, gridBagConstraints);

        tfSanKostenGruendung.setMinimumSize(new java.awt.Dimension(100, 27));
        tfSanKostenGruendung.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_kosten_gruendung}"), tfSanKostenGruendung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(tfSanKostenGruendung, gridBagConstraints);

        tfEingriffGruendung.setMinimumSize(new java.awt.Dimension(100, 27));
        tfEingriffGruendung.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_eingriff_gruendung}"), tfEingriffGruendung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(tfEingriffGruendung, gridBagConstraints);

        jScrollPane11.setMinimumSize(new java.awt.Dimension(26, 87));

        taSanMassnahmeGruendung.setColumns(20);
        taSanMassnahmeGruendung.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.san_massnahme_gruendung}"), taSanMassnahmeGruendung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane11.setViewportView(taSanMassnahmeGruendung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGruendung.add(jScrollPane11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlScrollPane.add(pnlGruendung, gridBagConstraints);

        jScrollPane3.setViewportView(pnlScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(jScrollPane3, gridBagConstraints);

        add(pnlCard1, "card1");

        pnlCard2.setOpaque(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel2.text")); // NOI18N
        pnlCard2.add(jLabel2);

        add(pnlCard2, "card2");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnImagesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagesActionPerformed

        cardLayout.show(this, "card2");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }//GEN-LAST:event_btnImagesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnInfoActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        cardLayout.show(this, "card1");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }//GEN-LAST:event_btnInfoActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            bindingGroup.unbind();
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTitle() {
        return String.valueOf(cidsBean);
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        // new WrrlEditorTester("bewirtschaftungsende", BewirtschaftungsendeEditor.class, WRRLUtil.DOMAIN_NAME).run();
        DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "mauer",
            1,
            1280,
            1024);
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
//        throw new UnsupportedOperationException("Not supported yet.");
        log.info("editor closed");
    }

    @Override
    public boolean prepareForSave() {
//        throw new UnsupportedOperationException("Not supported yet.");
        log.info("prepare for save");
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        final Object geoObj = cidsBean.getProperty("georeferenz");
        if (geoObj instanceof Geometry) {
            final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                    AlkisConstants.COMMONS.SRS_SERVICE);
            final BoundingBox box = new BoundingBox(pureGeom.getEnvelope().buffer(AlkisConstants.COMMONS.GEO_BUFFER));

            final Runnable mapRunnable = new Runnable() {

                    @Override
                    public void run() {
                        final ActiveLayerModel mappingModel = new ActiveLayerModel();
                        mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                        mappingModel.addHome(new XBoundingBox(
                                box.getX1(),
                                box.getY1(),
                                box.getX2(),
                                box.getY2(),
                                AlkisConstants.COMMONS.SRS_SERVICE,
                                true));
                        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                    AlkisConstants.COMMONS.MAP_CALL_STRING));
                        swms.setName("Mauer");
                        final StyledFeature dsf = new DefaultStyledFeature();
                        dsf.setGeometry(pureGeom);
                        dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));
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

    /**
     * DOCUMENT ME!
     */
    private void setEditable() {
        jScrollPane1.getViewport().setOpaque(editable);
        jScrollPane1.setOpaque(editable);
        jScrollPane2.getViewport().setOpaque(editable);
        jScrollPane2.setOpaque(editable);
        setComponentEditable(jTextArea1);
        setComponentEditable(jTextArea2);
        setComponentEditable(jTextField2);
        setComponentEditable(jTextField3);
        setComponentEditable(jTextField4);
        setComponentEditable(jTextField5);
        setComponentEditable(jTextField6);
        setComponentEditable(jTextField7);
        defaultBindableDateChooser1.setEditable(editable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tComp  DOCUMENT ME!
     */
    private void setComponentEditable(final JTextComponent tComp) {
        tComp.setEditable(editable);
        tComp.setOpaque(editable);
        if (!editable) {
            tComp.setBorder(null);
        }
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }
}
