/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.jdesktop.beansbinding.Converter;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;

import java.net.URL;

import java.sql.Timestamp;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.AbstractJasperReportPrint;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.StadtbildJasperReportPrint;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableJCheckBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.security.WebAccessManager;

import de.cismet.security.exceptions.AccessMethodIsNotSupportedException;
import de.cismet.security.exceptions.NoHandlerForURLException;
import de.cismet.security.exceptions.RequestFailedException;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor.adjustScale;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieEditor extends JPanel implements CidsBeanRenderer, TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    public static final String REPORT_FILE = "/de/cismet/cids/custom/wunda_blau/res/StadtbildA4H.jasper";
    private static final ImageIcon FOLDER_ICON = new ImageIcon(MauerEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inode-directory.png"));

    private static final ImageIcon ERROR_ICON = new ImageIcon(MauerEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/file-broken.png"));

    private static final int CACHE_SIZE = 20;

    private static final Map<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new LinkedHashMap<String, SoftReference<BufferedImage>>(CACHE_SIZE) {

            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, SoftReference<BufferedImage>> eldest) {
                return size() >= CACHE_SIZE;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private CidsBean cidsBean;
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
    private de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor
        defaultCismapGeometryComboBoxEditor1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
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
    private javax.swing.JList lstBildnummern;
    private javax.swing.JList lstSuchworte;
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
        lstBildnummern = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSuchworte = new javax.swing.JList();
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
        defaultCismapGeometryComboBoxEditor1 =
            new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
        jPanel2 = new javax.swing.JPanel();
        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        pnlFoto = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        pnlCtrlBtn = new javax.swing.JPanel();
        btnPrevImg1 = new javax.swing.JButton();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
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
        chbPruefen = new DefaultBindableJCheckBox();
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

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(jScrollPane2, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bildtyp}"),
                defaultBindableReferenceCombo2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panContent.add(defaultBindableReferenceCombo2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lager}"),
                defaultBindableReferenceCombo3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kommentar}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufnahmedatum}"),
                jXDatePicker1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.auftraggeber}"),
                dbcAuftraggeber,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcAuftraggeber, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fotograf}"),
                dbcFotograf,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails.add(dbcFotograf, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.filmart}"),
                dbcFilmart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ort}"),
                dbcOrt,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnummer}"),
                defaultBindableJTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(defaultBindableJTextField1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom_aus}"),
                lblGeomAus,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(lblGeomAus, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails1.add(btnCombineGeometries, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom}"),
                defaultCismapGeometryComboBoxEditor1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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
        lblVorschau.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.GridBagLayout());

        lblPicture.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.lblPicture.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        pnlFoto.add(lblPicture, gridBagConstraints);

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 60));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlFoto.add(lblBusy, gridBagConstraints);

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
        btnPrevImg1.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnPrevImg.text"));                                                       // NOI18N
        btnPrevImg1.setBorderPainted(false);
        btnPrevImg1.setFocusPainted(false);
        btnPrevImg1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImg1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnPrevImg1, gridBagConstraints);

        btnPrevImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnPrevImg.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnPrevImg.text"));                                                      // NOI18N
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        btnNextImg.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditor.class,
                "MauerEditor.btnNextImg.text"));                                                       // NOI18N
        btnNextImg.setBorderPainted(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefen}"),
                chbPruefen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setConverter(((DefaultBindableJCheckBox)chbPruefen).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panDetails3.add(chbPruefen, gridBagConstraints);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefhinweis_von}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
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
    } // </editor-fold>//GEN-END:initComponents

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
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() - 1);
    }//GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImgActionPerformed
        lstBildnummern.setSelectedIndex(lstBildnummern.getSelectedIndex() + 1);
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
     * @param  evt  DOCUMENT ME!
     */
    private void lstBildnummernValueChanged(final javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstBildnummernValueChanged
        if (!evt.getValueIsAdjusting()) {
            loadFoto();
        }
    }//GEN-LAST:event_lstBildnummernValueChanged

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
            lstBildnummern.setSelectedValue(cidsBean.getProperty("vorschaubild"), true);

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
     */
    private void loadFoto() {
        final Object stadtbild = lstBildnummern.getSelectedValue();
        if (fotoCidsBean != null) {
            fotoCidsBean.removePropertyChangeListener(listRepaintListener);
        }
        if (stadtbild instanceof CidsBean) {
            fotoCidsBean = (CidsBean)stadtbild;
            fotoCidsBean.addPropertyChangeListener(listRepaintListener);
            final String bildnummer = (String)fotoCidsBean.getProperty("bildnummer");
            boolean cacheHit = false;
            if (bildnummer != null) {
                final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(bildnummer);
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
                    new Sb_stadtbildserieEditor.LoadSelectedImageWorker(bildnummer).execute();
                }
            }
        } else {
            image = null;
            lblPicture.setIcon(FOLDER_ICON);
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
                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                btnPrevImg.setEnabled(false);
                btnNextImg.setEnabled(false);
            }
        } else {
            lblBusy.setBusy(false);
            lblBusy.setVisible(false);
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

    //~ Inner Classes ----------------------------------------------------------

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
                lstBildnummern.setEnabled(false);
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
                final ImageIcon result = new ImageIcon(adjustScale(image, pnlFoto, 20, 20));
                return result;
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String bildnummer;
        private final String[] fileEndings = { ".jpg", ".tiff" };

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

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((bildnummer != null) && (bildnummer.length() > 0)) {
                return downloadImageFromUrl(bildnummer);
            }
            return null;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   bildnummer  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private BufferedImage downloadImageFromUrl(final String bildnummer) {
            for (final String fileEnding : fileEndings) {
                InputStream is = null;
                try {
                    final char firstCharacter = bildnummer.charAt(0);
                    final String locationOfPreviewImage = "VB/" + firstCharacter + "/VB_" + bildnummer + fileEnding;
                    final String urlName = "http://s102x003/archivar/" + locationOfPreviewImage;

                    System.out.println(urlName);
                    final URL url = new URL(urlName);

                    is = WebAccessManager.getInstance().doRequest(url);
                    final BufferedImage img = ImageIO.read(is);
                    return img;
                } catch (IOException ex) {
                    log.warn("Image could not be loaded.", ex);
                } catch (AccessMethodIsNotSupportedException ex) {
                    log.warn("Image could not be loaded.", ex);
                } catch (RequestFailedException ex) {
                    log.warn("Image could not be loaded.", ex);
                } catch (NoHandlerForURLException ex) {
                    log.warn("Image could not be loaded.", ex);
                } catch (Exception ex) {
                    log.warn("Image could not be loaded.", ex);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            log.warn("Error during closing InputStream.", ex);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    IMAGE_CACHE.put(bildnummer, new SoftReference<BufferedImage>(image));
                    resizeListenerEnabled = true;
                    timer.restart();
                } else {
                    indicateError("Bild konnte nicht geladen werden.");
                }
            } catch (InterruptedException ex) {
                image = null;
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                log.error(ex, ex);
                indicateError(ex.getMessage());
            } finally {
                if (image == null) {
                    showWait(false);
                }
            }
        }
    }
}
