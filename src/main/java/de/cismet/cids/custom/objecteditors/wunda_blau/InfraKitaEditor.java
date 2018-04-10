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


import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import de.cismet.cids.custom.objecteditors.utils.InspireUtils;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import javax.swing.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.tools.gui.RoundedPanel;

import de.cismet.tools.gui.StaticSwingTools;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class InfraKitaEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener, 
    RequestsFullSizeComponent{

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(InfraKitaEditor.class);

    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;
    private String urlAttribute; 
    private String telAttribute;
    private String onlineAttribute;
    private String geomAttribute;
    private Object versionAttribute;
    
    private ImageIcon statusFalsch = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private ImageIcon statusOK = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    private ImageIcon statusDefault = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-offline.png"));
    private ImageIcon inspired = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/inspire_logo_en_100x100px.png"));
    private ImageIcon notinspired = new javax.swing.ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/inspire_logo_en_100x100px_soft.png"));
    private DefaultListModel dLModel = new DefaultListModel();
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenOkName;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAlter;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbStunden;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbTraegertyp;
    private javax.swing.JCheckBox chInklusion;
    private javax.swing.JCheckBox chOnline;
    private javax.swing.JDialog dlgChangeKitaName;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaName;
    private javax.swing.JTextArea jTextAreaNein;
    private javax.swing.JLabel lblAdresse;
    private javax.swing.JLabel lblAlter;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblInklusion;
    private javax.swing.JLabel lblInspire;
    private javax.swing.JLabel lblInspire1;
    private javax.swing.JLabel lblLeitung;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOnline;
    private javax.swing.JLabel lblPlaetze;
    private javax.swing.JLabel lblPlz;
    private javax.swing.JLabel lblStunden;
    private javax.swing.JLabel lblTelefon;
    private javax.swing.JLabel lblTraegertyp;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblUrlCheck;
    private javax.swing.JLabel lblWarningTextJa;
    private javax.swing.JLabel lblWarningTextNein;
    private javax.swing.JPanel panAdresse;
    private javax.swing.JPanel panChangeKitaName;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panDaten;
    private javax.swing.JPanel panFiller;
    private javax.swing.JPanel panFillerRechtsLage;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUnten1;
    private javax.swing.JPanel panLage;
    private javax.swing.JPanel panMenButtonsName;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private javax.swing.JPanel panUrl;
    private de.cismet.tools.gui.RoundedPanel rpKarte;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter sqlDateToStringConverter;
    private javax.swing.JTextField txtAdresse;
    private javax.swing.JTextField txtBemerkung;
    private javax.swing.JTextField txtLeitung;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPlaetze;
    private javax.swing.JTextField txtPlz;
    private javax.swing.JTextField txtTelefon;
    private javax.swing.JTextField txtUrl;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public InfraKitaEditor() {
    }
    
    /**
     * Creates a new InfraKitaEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public InfraKitaEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString("georeferenz");
            dlgChangeKitaName.pack();
            dlgChangeKitaName.getRootPane().setDefaultButton(btnMenOkName);
        }
        setReadOnly();
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

        sqlDateToStringConverter = new de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter();
        dlgChangeKitaName = new javax.swing.JDialog();
        panChangeKitaName = new javax.swing.JPanel();
        lblWarningTextNein = new javax.swing.JLabel();
        jTextAreaName = new javax.swing.JTextArea();
        panMenButtonsName = new javax.swing.JPanel();
        btnMenOkName = new javax.swing.JButton();
        lblWarningTextJa = new javax.swing.JLabel();
        jTextAreaNein = new javax.swing.JTextArea();
        lblInspire1 = new javax.swing.JLabel();
        panFillerUnten = new javax.swing.JPanel();
        panContent = new RoundedPanel();
        panFillerUnten1 = new javax.swing.JPanel();
        panLage = new javax.swing.JPanel();
        rpKarte = new de.cismet.tools.gui.RoundedPanel();
        panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel17 = new javax.swing.JLabel();
        panFillerRechtsLage = new javax.swing.JPanel();
        panAdresse = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblGeom = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        txtAdresse = new javax.swing.JTextField();
        lblAdresse = new javax.swing.JLabel();
        lblInspire = new javax.swing.JLabel();
        panDaten = new javax.swing.JPanel();
        txtTelefon = new javax.swing.JTextField();
        txtPlz = new javax.swing.JTextField();
        txtPlaetze = new javax.swing.JTextField();
        txtLeitung = new javax.swing.JTextField();
        lblLeitung = new javax.swing.JLabel();
        lblTelefon = new javax.swing.JLabel();
        lblPlz = new javax.swing.JLabel();
        lblPlaetze = new javax.swing.JLabel();
        lblInklusion = new javax.swing.JLabel();
        lblStunden = new javax.swing.JLabel();
        lblAlter = new javax.swing.JLabel();
        lblTraegertyp = new javax.swing.JLabel();
        panFiller = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        chInklusion = new javax.swing.JCheckBox();
        txtBemerkung = new javax.swing.JTextField();
        lblOnline = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        panUrl = new javax.swing.JPanel();
        txtUrl = new javax.swing.JTextField();
        lblUrlCheck = new javax.swing.JLabel();
        cbTraegertyp = new DefaultBindableReferenceCombo(true) ;
        cbAlter = new DefaultBindableReferenceCombo(true) ;
        cbStunden = new DefaultBindableReferenceCombo(true) ;
        chOnline = new javax.swing.JCheckBox();

        dlgChangeKitaName.setTitle("Ist dies eine neue Kita?");
        dlgChangeKitaName.setMinimumSize(new java.awt.Dimension(215, 200));
        dlgChangeKitaName.setModal(true);
        dlgChangeKitaName.setPreferredSize(new java.awt.Dimension(260, 200));

        panChangeKitaName.setMaximumSize(new java.awt.Dimension(200, 150));
        panChangeKitaName.setMinimumSize(new java.awt.Dimension(200, 150));
        panChangeKitaName.setPreferredSize(new java.awt.Dimension(200, 150));
        panChangeKitaName.setLayout(new java.awt.GridBagLayout());

        lblWarningTextNein.setText("Nicht: ");
        lblWarningTextNein.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 10, 10);
        panChangeKitaName.add(lblWarningTextNein, gridBagConstraints);

        jTextAreaName.setColumns(20);
        jTextAreaName.setRows(3);
        jTextAreaName.setText("Löschen Sie diese Kita\nzuerst und legen dann \neine neue Kita an.");
        jTextAreaName.setMinimumSize(new java.awt.Dimension(140, 50));
        jTextAreaName.setOpaque(false);
        jTextAreaName.setPreferredSize(new java.awt.Dimension(140, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panChangeKitaName.add(jTextAreaName, gridBagConstraints);

        panMenButtonsName.setLayout(new java.awt.GridBagLayout());

        btnMenOkName.setText("Ok");
        btnMenOkName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenOkNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtonsName.add(btnMenOkName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panChangeKitaName.add(panMenButtonsName, gridBagConstraints);

        lblWarningTextJa.setText("Wenn ja: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panChangeKitaName.add(lblWarningTextJa, gridBagConstraints);

        jTextAreaNein.setColumns(20);
        jTextAreaNein.setRows(3);
        jTextAreaNein.setText("Dann korrigieren Sie ja\nnur einen Tippfehler....");
        jTextAreaNein.setToolTipText("");
        jTextAreaNein.setMinimumSize(new java.awt.Dimension(140, 50));
        jTextAreaNein.setOpaque(false);
        jTextAreaNein.setPreferredSize(new java.awt.Dimension(140, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panChangeKitaName.add(jTextAreaNein, gridBagConstraints);

        lblInspire1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"))); // NOI18N
        lblInspire1.setToolTipText("Warnung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panChangeKitaName.add(lblInspire1, gridBagConstraints);

        dlgChangeKitaName.getContentPane().add(panChangeKitaName, java.awt.BorderLayout.CENTER);

        setAutoscrolls(true);
        setMinimumSize(new java.awt.Dimension(600, 646));
        setPreferredSize(new java.awt.Dimension(600, 737));
        setLayout(new java.awt.GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        javax.swing.GroupLayout panFillerUntenLayout = new javax.swing.GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setMaximumSize(new java.awt.Dimension(450, 2147483647));
        panContent.setMinimumSize(new java.awt.Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new java.awt.Dimension(450, 961));
        panContent.setLayout(new java.awt.GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        javax.swing.GroupLayout panFillerUnten1Layout = new javax.swing.GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 5.0E-4;
        panContent.add(panFillerUnten1, gridBagConstraints);

        panLage.setMinimumSize(new java.awt.Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new java.awt.GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new java.awt.GridBagLayout());

        panPreviewMap.setMinimumSize(new java.awt.Dimension(600, 600));
        panPreviewMap.setPreferredSize(new java.awt.Dimension(500, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Lage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        javax.swing.GroupLayout panFillerRechtsLageLayout = new javax.swing.GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(
            panFillerRechtsLageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerRechtsLageLayout.setVerticalGroup(
            panFillerRechtsLageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panLage.add(panFillerRechtsLage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        panContent.add(panLage, gridBagConstraints);

        panAdresse.setOpaque(false);
        panAdresse.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panAdresse.add(lblName, gridBagConstraints);

        lblGeom.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panAdresse.add(lblGeom, gridBagConstraints);

        txtName.setToolTipText("");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panAdresse.add(txtName, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            }

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"), cbGeom, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
            panAdresse.add(cbGeom, gridBagConstraints);
        }

        txtAdresse.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.adresse}"), txtAdresse, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panAdresse.add(txtAdresse, gridBagConstraints);

        lblAdresse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAdresse.setText("Adresse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panAdresse.add(lblAdresse, gridBagConstraints);

        lblInspire.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard_100.png"))); // NOI18N
        lblInspire.setToolTipText("Der Datensatz ist inspired.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAdresse.add(lblInspire, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panContent.add(panAdresse, gridBagConstraints);

        panDaten.setMinimumSize(new java.awt.Dimension(374, 190));
        panDaten.setOpaque(false);
        panDaten.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.telefon}"), txtTelefon, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panDaten.add(txtTelefon, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.plz}"), txtPlz, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panDaten.add(txtPlz, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.plaetze}"), txtPlaetze, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panDaten.add(txtPlaetze, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.leitung}"), txtLeitung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panDaten.add(txtLeitung, gridBagConstraints);

        lblLeitung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLeitung.setText("Leitung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblLeitung, gridBagConstraints);

        lblTelefon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblTelefon, gridBagConstraints);

        lblPlz.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        lblPlaetze.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPlaetze.setText("Plätze:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblPlaetze, gridBagConstraints);

        lblInklusion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInklusion.setText("Inklusion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblInklusion, gridBagConstraints);

        lblStunden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStunden.setText("h/Woche:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblStunden, gridBagConstraints);

        lblAlter.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAlter.setText("Alter:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblAlter, gridBagConstraints);

        lblTraegertyp.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTraegertyp.setText("Trägertyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblTraegertyp, gridBagConstraints);

        panFiller.setMinimumSize(new java.awt.Dimension(20, 0));
        panFiller.setOpaque(false);
        panFiller.setPreferredSize(new java.awt.Dimension(20, 0));

        javax.swing.GroupLayout panFillerLayout = new javax.swing.GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        panDaten.add(panFiller, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 0, 2);
        panDaten.add(jSeparator1, gridBagConstraints);

        chInklusion.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.plaetze_fuer_behinderte}"), chInklusion, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        panDaten.add(chInklusion, gridBagConstraints);

        txtBemerkung.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"), txtBemerkung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panDaten.add(txtBemerkung, gridBagConstraints);

        lblOnline.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblOnline, gridBagConstraints);

        lblBemerkung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        lblUrl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("Homepage:");
        lblUrl.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panDaten.add(lblUrl, gridBagConstraints);

        panUrl.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.url}"), txtUrl, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panUrl.add(txtUrl, gridBagConstraints);

        lblUrlCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panDaten.add(panUrl, gridBagConstraints);

        cbTraegertyp.setMaximumSize(new java.awt.Dimension(200, 23));
        cbTraegertyp.setMinimumSize(new java.awt.Dimension(150, 23));
        cbTraegertyp.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.traegertyp}"), cbTraegertyp, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(cbTraegertyp, gridBagConstraints);

        cbAlter.setMaximumSize(new java.awt.Dimension(200, 23));
        cbAlter.setMinimumSize(new java.awt.Dimension(150, 23));
        cbAlter.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alter}"), cbAlter, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(cbAlter, gridBagConstraints);

        cbStunden.setMaximumSize(new java.awt.Dimension(200, 23));
        cbStunden.setMinimumSize(new java.awt.Dimension(150, 23));
        cbStunden.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stunden}"), cbStunden, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(cbStunden, gridBagConstraints);

        chOnline.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.online_stellen}"), chOnline, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        panDaten.add(chOnline, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panContent.add(panDaten, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenOkNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOkNameActionPerformed
       dlgChangeKitaName.setVisible(false);
    }//GEN-LAST:event_btnMenOkNameActionPerformed

    private void finishVersion(CidsBean versionBean, Timestamp timestamp){
        try {
            versionBean.setProperty("endlifespanversion", timestamp);
        } catch (Exception e) {
            LOG.fatal("Problem during closing kita", e);
            final ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Löschen",
                            "Beim Löschen der Version dieser Kita ist ein Fehler aufgetreten",
                            null,
                            null,
                            e,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
        }
    }
    
    private CidsBean getLastVersion(){
        CidsBean dateCidsBean = null;
        int version = 0;
        final Object o = cidsBean.getProperty("version_kita");
        if (o instanceof Collection){
            try{
                final Collection<CidsBean> col = (Collection)o;
                for (final CidsBean bean : col){
                    if (version < (int)bean.getProperty("versionnr")){
                        version = (int) bean.getProperty("versionnr");
                        dateCidsBean = bean;
                    }
                }
                
            }catch (Exception ex) {
                    LOG.error(ex, ex);
                }
        }
        return dateCidsBean;
    }
    
    private void checkInspireID(){
        try{
            if (cidsBean.getProperty("inspire_id") == null){
                lblInspire.setIcon(notinspired);
                lblInspire.setToolTipText("Der Datensatz ist nicht inspired.");
            }else{
                lblInspire.setIcon(inspired);
                lblInspire.setToolTipText("Der Datensatz ist inspired.");
            }
        }catch (Exception e) {
            lblInspire.setIcon(inspired);
            lblInspire.setToolTipText("Der Datensatz ist inspired.");
        } 
    }
   
    private void testUrlAndShowResult(){
        try {
            final URL url = new URL(txtUrl.getText());
            if (checkURL(url)) {
                lblUrlCheck.setIcon(statusOK);
            } else {
                lblUrlCheck.setIcon(statusFalsch);
            }
        } catch (Exception e) {
            lblUrlCheck.setIcon(statusFalsch);
        }        
    }
    
     private boolean checkURL(final URL url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean newVersion = false;
 
        // adresse vorhanden
       try {
            if (txtAdresse.getText().trim().isEmpty() ){              
                LOG.warn("No adress specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().noAdresse"));
            
            }
        } catch (final Exception ex) {
            LOG.warn("Adress not given.", ex);
            save = false;
        }
       
        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty() ){              
                LOG.warn("No name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().noName"));
            
            }
        } catch (final Exception ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
               
       // georeferenz muss gefüllt sein
       try {
            if (cidsBean.getProperty("georeferenz") == null){              
                LOG.warn("No geom specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().noGeom"));
            
            } else{
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty("georeferenz");
                if (! ((Geometry)geom_pos.getProperty("geo_field")).getGeometryType().equals( "Point")){
                    LOG.warn("Wrong geom specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().wrongGeom"));
                } else{
                    if (!(setNotNull(cidsBean.getProperty("georeferenz")).equals(geomAttribute)) && cidsBean.getMetaObject().getStatus() != MetaObject.NEW){
                        newVersion = true;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
       
        //Änderung der url
        if (  !(setNotNull(cidsBean.getProperty("url")).equals(urlAttribute)) && cidsBean.getMetaObject().getStatus() != MetaObject.NEW){
            newVersion = true;
        }
      
        //Änderung des Telefons
        if (!(setNotNull(cidsBean.getProperty("telefon")).equals(telAttribute)) && cidsBean.getMetaObject().getStatus() != MetaObject.NEW){
            newVersion = true;
        }
        
        //Soll eine neue Version erstellt werden?
        if (newVersion && chOnline.isSelected() && versionAttribute != null){
           createNewVersion(); 
        }
                
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(InfraKitaEditor.class,
                    "InfraKitaEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(InfraKitaEditor.class,
                            "InfraKitaEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(InfraKitaEditor.class,
                    "InfraKitaEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        
        //Erzeugung einer neuen eindeutigen uuid und Anlegen der ersten Version
        try{
            String uuid;
            if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW && chOnline.isSelected() ){
                uuid = InspireUtils.generateUuid(getConnectionContext());
                InspireUtils.writeUuid(uuid, cidsBean, "inspire_id", "infra_kita");
                createFirstVersion(1, new Timestamp(new Date().getTime()));
            } else {
                if (cidsBean.getMetaObject().getStatus() != MetaObject.NEW && !(setNotNull(cidsBean.getProperty("online_stellen")).equals(onlineAttribute)) && chOnline.isSelected()){
                    uuid = InspireUtils.generateUuid(getConnectionContext());
                    InspireUtils.writeUuid(uuid, cidsBean, "inspire_id", "infra_kita");
                    createFirstVersion(1, new Timestamp(new Date().getTime()));
                } else{
                    if (cidsBean.getMetaObject().getStatus() != MetaObject.NEW && !(setNotNull(cidsBean.getProperty("online_stellen")).equals(onlineAttribute))  && !(chOnline.isSelected())){
                        LOG.warn("Offline specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().wrongOffline"));
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("inspireid not given.", ex);
            save = false;
        }
        
        return save;
    }
    
    private void createFirstVersion(int versionnr, Timestamp timestamp){
        final MetaClass versionMetaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "inspire_infra_kita_version", getConnectionContext());
        CidsBean newVersionBean = versionMetaClass.getEmptyInstance(getConnectionContext()).getBean();
        try{
            newVersionBean.setProperty("versionnr", versionnr);
            newVersionBean.setProperty("beginlifespanversion", timestamp);
            final CidsBean geom_pos = (CidsBean)cidsBean.getProperty("georeferenz");
            if (geom_pos != null && ((Geometry)geom_pos.getProperty("geo_field")).getGeometryType().equals( "Point")){
                Coordinate geom_point = ((Geometry)geom_pos.getProperty("geo_field")).getCoordinate();
                Double point_x = geom_point.x;
                Double point_y = geom_point.y;
                newVersionBean.setProperty("point", point_x + " " + point_y);
            }
            if (cidsBean.getProperty("url") != null){
                newVersionBean.setProperty("website", cidsBean.getProperty("url").toString());
            }
            if (cidsBean.getProperty("telefon") != null){
                newVersionBean.setProperty("telephonevoice", cidsBean.getProperty("telefon").toString());
            }
            
            cidsBean = TableUtils.addBeanToCollection(cidsBean,"version_kita", newVersionBean);
        } catch (final Exception ex) {
            LOG.warn("inspireversion not created.", ex);
        }
    }
    
    private void createNewVersion(){
        final Timestamp timestamp = new Timestamp(new Date().getTime());
        CidsBean versionBean = getLastVersion();
        finishVersion(versionBean, timestamp);
        int version = (int)versionBean.getProperty("versionnr");
        createFirstVersion(version + 1, timestamp);
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
                LOG.info("remove propchange str_adr_strasse: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange str_adr_str: " + this.cidsBean);
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
            testUrlAndShowResult();
            saveFirstAttributes();
            checkInspireID();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void saveFirstAttributes(){
        urlAttribute = setNotNull(cidsBean.getProperty("url"));
        telAttribute = setNotNull(cidsBean.getProperty("telefon"));
        onlineAttribute = setNotNull(cidsBean.getProperty("online_stellen"));
        geomAttribute = setNotNull(cidsBean.getProperty("georeferenz"));
        versionAttribute = cidsBean.getProperty("version_kita");
    }
        
    //COALESCE
    private String setNotNull(Object notNullString){
        if (notNullString == null){
            return "";
        }
        return notNullString.toString();
    }
              
    private void setReadOnly(){
        if (!(isEditor)){ 
            cbStunden.setEnabled(false);
            cbTraegertyp.setEnabled(false);
            cbAlter.setEnabled(false);
            chInklusion.setEnabled(false);
            chOnline.setEnabled(false);
            txtAdresse.setEnabled(false);
            txtBemerkung.setEnabled(false);
            txtLeitung.setEnabled(false);
            txtName.setEnabled(false);
            txtPlaetze.setEnabled(false);
            txtPlz.setEnabled(false);
            txtTelefon.setEnabled(false);
            txtUrl.setEnabled(false);
            lblGeom.setVisible(false);
        }
    }
   
    public void setMapWindow(){
        CidsBean cb = this.getCidsBean();
        try{
            if (cb.getProperty("georeferenz") != null){
//panPreviewMap.initMap(cidsBean, "georeferenz.geo_field", 50.0, "http://s10221.wuppertal-intra.de:7098/alkis/services?&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=FALSE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=alf&STYLES=&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:25832");
                panPreviewMap.initMap(cb, "georeferenz.geo_field", 20.0);
            }else{
                final GeometryFactory factory = new GeometryFactory(
                                new PrecisionModel(),
                                CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));
                final Point point;
                point = factory.createPoint(new Coordinate(374420,5681660));
                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "geom",
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty("geo_field", point);
                panPreviewMap.initMap(newGeom, "geo_field", 20.0);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
        
    @Override
    public void dispose() {
        super.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            dlgChangeKitaName.dispose();
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
    }

    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       //throw new UnsupportedOperationException("Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
        if (evt.getPropertyName().equals("georeferenz")){
            setMapWindow();
        }
        
        if (evt.getPropertyName().equals("url")){
           testUrlAndShowResult();
        }
        if (evt.getPropertyName().equals("name")){
           if(cidsBean.getProperty("inspire_id")!= null){
               StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(InfraKitaEditor.this),
            dlgChangeKitaName,
            true);
           }
        }
    }
}


