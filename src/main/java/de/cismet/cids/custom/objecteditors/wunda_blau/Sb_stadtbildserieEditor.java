/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.AbstractJasperReportPrint;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.StadtbildJasperReportPrint;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.editors.DefaultBindableJCheckBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;
import java.sql.Timestamp;
import java.util.Date;
import org.jdesktop.beansbinding.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieEditor extends JPanel implements CidsBeanRenderer, TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    public static final String REPORT_FILE = "/de/cismet/cids/custom/wunda_blau/res/StadtbildA4H.jasper";

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private CidsBean cidsBean;
    private String title;
    private Converter<Timestamp, Date> timeStampConverter = new Converter<Timestamp, Date>() {

                @Override
                public Date convertForward(final Timestamp value) {
                    try {
                        if (value != null) {
                            return new java.util.Date(value.getTime());
                        } else {
                            return null;
                        }
                    } catch (Exception ex) {
                        log.fatal(ex);
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
                        log.fatal(ex);
                        return new Timestamp(System.currentTimeMillis());
                    }
                }
            }; 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCombineGeometries;
    private javax.swing.JButton btnNextImg;
    private javax.swing.JButton btnPrevImg;
    private javax.swing.JButton btnPrevImg1;
    private javax.swing.JCheckBox chbPruefen;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dbcAuftraggeber;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dbcFilmart;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dbcFotograf;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo dbcOrt;
    private de.cismet.cids.editors.DefaultBindableJTextField defaultBindableJTextField1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo2;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo3;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo7;
    private de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor defaultCismapGeometryComboBoxEditor1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
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
    private javax.swing.JLabel lblGeomAus;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblPrint;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panDetails;
    private javax.swing.JPanel panDetails1;
    private javax.swing.JPanel panDetails3;
    private javax.swing.JPanel panDetails4;
    private javax.swing.JPanel panPrintButton;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlCtrlBtn;
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
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Arc_stadtbildRenderer.
     */
    public Sb_stadtbildserieEditor() {
        initComponents();
        jScrollPane5.getViewport().setOpaque(false);
        title = "";
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            lblPicture,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            lblPrint,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
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

        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        sqlDateToStringConverter = new de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter();
        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panPrintButton = new javax.swing.JPanel();
        lblPrint = new javax.swing.JLabel();
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        roundedPanel2 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        panContent = new RoundedPanel();
        lblDescBildnummer = new javax.swing.JLabel();
        lblDescLagerort = new javax.swing.JLabel();
        lblDescAufnahmedatum = new javax.swing.JLabel();
        lblDescInfo = new javax.swing.JLabel();
        lblDescBildtyp = new javax.swing.JLabel();
        lblDescSuchworte = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        defaultBindableReferenceCombo2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        defaultBindableReferenceCombo3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        roundedPanel3 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        panDetails = new RoundedPanel();
        lblDescFilmart = new javax.swing.JLabel();
        lblDescFotograf = new javax.swing.JLabel();
        lblDescAuftraggeber = new javax.swing.JLabel();
        dbcAuftraggeber = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        dbcFotograf = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        dbcFilmart = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        roundedPanel4 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        panDetails1 = new RoundedPanel();
        lblDescGeometrie = new javax.swing.JLabel();
        lblDescOrt = new javax.swing.JLabel();
        lblDescStrasse = new javax.swing.JLabel();
        defaultBindableReferenceCombo7 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        dbcOrt = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel7 = new javax.swing.JLabel();
        defaultBindableJTextField1 = new de.cismet.cids.editors.DefaultBindableJTextField();
        lblGeomAus = new javax.swing.JLabel();
        btnCombineGeometries = new javax.swing.JButton();
        defaultCismapGeometryComboBoxEditor1 = new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
        jPanel2 = new javax.swing.JPanel();
        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        pnlFoto = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75,75));
        pnlCtrlBtn = new javax.swing.JPanel();
        btnPrevImg1 = new javax.swing.JButton();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jToggleButton1 = new javax.swing.JToggleButton();
        roundedPanel7 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel8 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        panDetails4 = new RoundedPanel();
        pnlMap = new javax.swing.JPanel();
        roundedPanel6 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        panDetails3 = new RoundedPanel();
        chbPruefen = new DefaultBindableJCheckBox()
        ;
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

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

        lblPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        lblPrint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPrintMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panPrintButton.add(lblPrint, gridBagConstraints);

        panTitle.add(panPrintButton, java.awt.BorderLayout.EAST);

        roundedPanel1.add(semiRoundedPanel1, java.awt.BorderLayout.CENTER);

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

        lblDescBildnummer.setText("Bildnummer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescBildnummer, gridBagConstraints);

        lblDescLagerort.setText("Lagerort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescLagerort, gridBagConstraints);

        lblDescAufnahmedatum.setText("Aufnahmedatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescAufnahmedatum, gridBagConstraints);

        lblDescInfo.setText("Kommentar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescInfo, gridBagConstraints);

        lblDescBildtyp.setText("Bildtyp");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescBildtyp, gridBagConstraints);

        lblDescSuchworte.setText("Suchworte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(lblDescSuchworte, gridBagConstraints);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jScrollPane1, gridBagConstraints);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jScrollPane2, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bildtyp}"), defaultBindableReferenceCombo2, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(defaultBindableReferenceCombo2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lager}"), defaultBindableReferenceCombo3, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(defaultBindableReferenceCombo3, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kommentar}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jScrollPane3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufnahmedatum}"), jXDatePicker1, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(timeStampConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jXDatePicker1, gridBagConstraints);

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

        lblDescFilmart.setText("Filmart");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescFilmart, gridBagConstraints);

        lblDescFotograf.setText("Fotograf");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescFotograf, gridBagConstraints);

        lblDescAuftraggeber.setText("Auftraggeber");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(lblDescAuftraggeber, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.auftraggeber}"), dbcAuftraggeber, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcAuftraggeber, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fotograf}"), dbcFotograf, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcFotograf, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.filmart}"), dbcFilmart, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
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

        lblDescGeometrie.setText("Geometrie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblDescGeometrie, gridBagConstraints);

        lblDescOrt.setText("Ort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblDescOrt, gridBagConstraints);

        lblDescStrasse.setText("Straße");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblDescStrasse, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(defaultBindableReferenceCombo7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ort}"), dbcOrt, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(dbcOrt, gridBagConstraints);

        jLabel7.setText("Hs.-Nr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(jLabel7, gridBagConstraints);

        defaultBindableJTextField1.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(defaultBindableJTextField1, gridBagConstraints);

        lblGeomAus.setText("jLabel8");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblGeomAus, gridBagConstraints);

        btnCombineGeometries.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
        btnCombineGeometries.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "VermessungRissEditor.btnCombineGeometries.text")); // NOI18N
        btnCombineGeometries.setToolTipText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "VermessungRissEditor.btnCombineGeometries.toolTipText")); // NOI18N
        btnCombineGeometries.setEnabled(false);
        btnCombineGeometries.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(btnCombineGeometries, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom}"), defaultCismapGeometryComboBoxEditor1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(defaultCismapGeometryComboBoxEditor1, gridBagConstraints);

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

        pnlVorschau.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        lblVorschau.setForeground(new java.awt.Color(255, 255, 255));
        lblVorschau.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "MauerEditor.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.GridBagLayout());

        lblPicture.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "MauerEditor.lblPicture.text")); // NOI18N
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

        btnPrevImg1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnPrevImg1.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "MauerEditor.btnPrevImg.text")); // NOI18N
        btnPrevImg1.setBorderPainted(false);
        btnPrevImg1.setFocusPainted(false);
        btnPrevImg1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevImg1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnPrevImg1, gridBagConstraints);

        btnPrevImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnPrevImg.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "MauerEditor.btnPrevImg.text")); // NOI18N
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevImgActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        btnNextImg.setText(org.openide.util.NbBundle.getMessage(Sb_stadtbildserieEditor.class, "MauerEditor.btnNextImg.text")); // NOI18N
        btnNextImg.setBorderPainted(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImgActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
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

        jToggleButton1.setText("jToggleButton1");
        pnlCtrlBtn.add(jToggleButton1, new java.awt.GridBagConstraints());

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
        gridBagConstraints.weighty = 1.0;
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
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefen}"), chbPruefen, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setConverter(((DefaultBindableJCheckBox)chbPruefen).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails3.add(chbPruefen, gridBagConstraints);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefhinweis_von}"), jTextArea2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails3.add(jScrollPane4, gridBagConstraints);

        jButton1.setText("Prüfhinweis speichern");
        jButton1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails3.add(jButton1, gridBagConstraints);

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
        gridBagConstraints.weighty = 1.0;
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
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblPrintMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPrintMouseClicked
        if ((evt != null) && !evt.isPopupTrigger()) {
            final CidsBean bean = cidsBean;
            if (bean != null) {
                final AbstractJasperReportPrint jp = new StadtbildJasperReportPrint(REPORT_FILE, bean);
                jp.print();
            }
        }
    }//GEN-LAST:event_lblPrintMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevImgActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevImgActionPerformed
//        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() - 1);
    }//GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImgActionPerformed
//        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() + 1);
    }//GEN-LAST:event_btnNextImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevImg1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevImg1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrevImg1ActionPerformed

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
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
                        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);
            this.cidsBean = cidsBean;
            bindingGroup.bind();
            final String obj = String.valueOf(cidsBean.getProperty("bildnummer"));
//            lblPicture.setPictureURL(StaticProperties.ARCHIVAR_URL_PREFIX + obj + StaticProperties.ARCHIVAR_URL_SUFFIX);
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
    public void setTitle(final String title) {
        this.title = "Stadtbild " + title;
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
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
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
            2,
            1280,
            1024);
    }
}
