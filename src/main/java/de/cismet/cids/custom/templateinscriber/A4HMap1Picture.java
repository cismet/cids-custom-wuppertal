/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * A4H.java
 *
 * Created on 11. Juli 2006, 12:19
 */
package de.cismet.cids.custom.templateinscriber;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLDecoder;

import java.util.*;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.clientutils.ConversionUtils;

import de.cismet.cismap.actions.MergeFeatureAction;

import de.cismet.cismap.commons.MappingModel;
import de.cismet.cismap.commons.RetrievalServiceLayer;
import de.cismet.cismap.commons.ServiceLayer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.printing.AbstractPrintingInscriber;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;
import de.cismet.cismap.commons.rasterservice.MapService;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class A4HMap1Picture extends AbstractPrintingInscriber implements DropTargetListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_HIGHLIGHT = "Ueberschrift";
    public static final String KEY_SIGNATURE = "Unterschrift";
    public static final String KEY_E_NR = "ENr";
    public static final String KEY_LOC_DESC = "Lagebezeichnung";
    public static final String KEY_DATA = "Datenart";
    public static final String KEY_DATASOURCES = "Datenquellen";
    private static final String CBO_DATA_PROPERTIES = "CboData.properties";
    private static final Logger LOG = Logger.getLogger(A4HMap1Picture.class);
    private static final String FILE_PROTOCOL_PREFIX = "file://";
    private static final String IMAGE = "image";

    //~ Instance fields --------------------------------------------------------

    String cacheFile = ""; // NOI18N
    Properties cache = new Properties();
    private final ArrayList<JCheckBox> chkDataSourcesList;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private DropTarget dropTarget;
    private String lastPath = null;
    private File file = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboData;
    private javax.swing.JPanel filePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblData1;
    private javax.swing.JLabel lblDataSources;
    private javax.swing.JLabel lblENr;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblHighlight;
    private javax.swing.JLabel lblLocationDescription;
    private javax.swing.JLabel lblSignature;
    private javax.swing.JPanel pnlDataSources;
    private javax.swing.JTextField txtENr;
    private javax.swing.JTextField txtHighlight;
    private javax.swing.JTextField txtLocationDescription;
    private javax.swing.JTextField txtSignature;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form A4H.
     */
    public A4HMap1Picture() {
        initComponents();
        cacheFile = CismapBroker.getInstance().getCismapFolderPath() + System.getProperty("file.separator")
                    + "inscriberCache"; // NOI18N
        readInscriberCache();

        this.chkDataSourcesList = new ArrayList<JCheckBox>();

        this.setUpDataSourceChks();
        this.setUpDataCbo();
        dropTarget = new DropTarget(this, this);
        new DropTarget(lblFile, this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void setUpDataSourceChks() {
        final CismapBroker broker = CismapBroker.getInstance();
        final MappingComponent mapComp = broker.getMappingComponent();
        final MappingModel mapModel = mapComp.getMappingModel();
        final TreeMap rasterServices = mapModel.getRasterServices();

        // Note: the sets iterator returns entries in key ascending order,
        // BUT we need it in reversed order to reflect the order of the layer component
        final Iterator it = rasterServices.values().iterator();
        JCheckBox chkDataSource;
        Object v;
        while (it.hasNext()) {
            v = it.next();

            final boolean serviceLayerCheck = ((v instanceof ServiceLayer) && ((ServiceLayer)(v)).isEnabled()
                            && (((ServiceLayer)(v)).getTranslucency() > 0)) || !(v instanceof ServiceLayer);

            final boolean retrievalServiceLayerCheck = ((v instanceof RetrievalServiceLayer)
                            && ((RetrievalServiceLayer)v).getPNode().getVisible())
                        || !(v instanceof MapService);

            if (serviceLayerCheck && retrievalServiceLayerCheck) {
                chkDataSource = new JCheckBox(v.toString());
                chkDataSource.setSelected(true);

                this.pnlDataSources.add(chkDataSource, 0);
                this.chkDataSourcesList.add(chkDataSource);
            }
        }

        // more efficient than always adding elements on the first position
        // because this approach would trigger array copy operations after each insert
        Collections.reverse(this.chkDataSourcesList);
    }

    /**
     * DOCUMENT ME!
     */
    private void setUpDataCbo() {
        final InputStream in = this.getClass().getResourceAsStream(CBO_DATA_PROPERTIES);
        if (in == null) {
            LOG.error("Can not configuration file '" + CBO_DATA_PROPERTIES
                        + "' in classpath. -> data combo box is empty");
        } else {
            final Properties prop = new Properties();
            try {
                prop.load(in);

                final TreeMap propTreeMap = new TreeMap(prop);
                final Iterator it = propTreeMap.values().iterator();

                while (it.hasNext()) {
                    this.cboData.addItem(it.next());
                }

                this.cboData.setSelectedIndex(0);
            } catch (final Exception ex) {
                LOG.error("An error occurred while reading configuration file '" + CBO_DATA_PROPERTIES
                            + "' -> data combo box is empty",
                    ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getSelectedDataSourcesString() {
        final StringBuilder stringBuilder = new StringBuilder();

        boolean isFirstIter = true;

        for (final JCheckBox chk : this.chkDataSourcesList) {
            if (chk.isSelected()) {
                if (isFirstIter) {
                    isFirstIter = false;
                } else {
                    stringBuilder.append('\n');
                }

                stringBuilder.append("- ").append(chk.getText());
            }
        }

        return stringBuilder.toString();
    }

    /**
     * This Method should return the values in the Form<br>
     * key: placeholderName value: value
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public HashMap<String, String> getValues() {
        final HashMap<String, String> hm = new HashMap<String, String>();
        hm.put(KEY_HIGHLIGHT, txtHighlight.getText());
        hm.put(KEY_SIGNATURE, txtSignature.getText());
        hm.put(KEY_E_NR, txtENr.getText());
        hm.put(KEY_LOC_DESC, txtLocationDescription.getText());
        hm.put(KEY_DATA, String.valueOf(cboData.getSelectedItem()));
        hm.put(KEY_DATASOURCES, this.getSelectedDataSourcesString());

        try {
            if (file != null) {
                final BufferedImage image = ImageIO.read(file);
                hm.put(IMAGE, ConversionUtils.image2String(image));
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
        }

        cache.setProperty(KEY_HIGHLIGHT, txtHighlight.getText());          // NOI18N
        cache.setProperty(KEY_SIGNATURE, txtSignature.getText());          // NOI18N
        cache.setProperty(KEY_E_NR, txtENr.getText());                     // NOI18N
        cache.setProperty(KEY_LOC_DESC, txtLocationDescription.getText()); // NOI18N
        writeInscriberCache();
        return hm;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblHighlight = new javax.swing.JLabel();
        txtHighlight = new javax.swing.JTextField();
        lblSignature = new javax.swing.JLabel();
        txtSignature = new javax.swing.JTextField();
        lblENr = new javax.swing.JLabel();
        txtENr = new javax.swing.JTextField();
        lblLocationDescription = new javax.swing.JLabel();
        txtLocationDescription = new javax.swing.JTextField();
        lblData = new javax.swing.JLabel();
        cboData = new javax.swing.JComboBox();
        lblDataSources = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlDataSources = new javax.swing.JPanel();
        lblData1 = new javax.swing.JLabel();
        filePanel = new javax.swing.JPanel();
        lblFile = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(312, 245));
        setLayout(new java.awt.GridBagLayout());

        lblHighlight.setText(org.openide.util.NbBundle.getMessage(
                A4HMap1Picture.class,
                "A4HMap1Picture.lblHighlight.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblHighlight, gridBagConstraints);
        lblHighlight.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblHighlight.AccessibleContext.accessibleName")); // NOI18N

        txtHighlight.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtHighlightActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtHighlight, gridBagConstraints);

        lblSignature.setText(org.openide.util.NbBundle.getMessage(
                A4HMap1Picture.class,
                "A4HMap1Picture.lblSignature.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblSignature, gridBagConstraints);
        lblSignature.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblSignature.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtSignature, gridBagConstraints);

        lblENr.setText(org.openide.util.NbBundle.getMessage(A4HMap1Picture.class, "A4HMap1Picture.lblENr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 12, 5, 0);
        add(lblENr, gridBagConstraints);
        lblENr.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblENr.AccessibleContext.accessibleName"));                               // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 10);
        add(txtENr, gridBagConstraints);

        lblLocationDescription.setText(org.openide.util.NbBundle.getMessage(
                A4HMap1Picture.class,
                "A4HMap1Picture.lblLocationDescription.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblLocationDescription, gridBagConstraints);
        lblLocationDescription.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblLocationDescription.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtLocationDescription, gridBagConstraints);

        lblData.setText(org.openide.util.NbBundle.getMessage(A4HMap1Picture.class, "A4HMap1Picture.lblData.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblData, gridBagConstraints);
        lblData.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblData.AccessibleContext.accessibleName"));                                // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(cboData, gridBagConstraints);

        lblDataSources.setText(org.openide.util.NbBundle.getMessage(
                A4HMap1Picture.class,
                "A4HMap1Picture.lblDataSources.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 0, 0);
        add(lblDataSources, gridBagConstraints);
        lblDataSources.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.lblDataSources.AccessibleContext.accessibleName")); // NOI18N

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(125, 56));
        jScrollPane2.setOpaque(false);

        pnlDataSources.setFocusTraversalPolicyProvider(true);
        pnlDataSources.setMinimumSize(new java.awt.Dimension(123, 20));
        pnlDataSources.setLayout(new javax.swing.BoxLayout(pnlDataSources, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(pnlDataSources);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        lblData1.setText(org.openide.util.NbBundle.getMessage(A4HMap1Picture.class, "A4HMap1Picture.lblData1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblData1, gridBagConstraints);

        filePanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        filePanel.add(lblFile, gridBagConstraints);

        jButton1.setText(org.openide.util.NbBundle.getMessage(
                A4HMap1Picture.class,
                "A4HMap1Picture.jButton1.text",
                new Object[] {})); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        filePanel.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(filePanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtHighlightActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtHighlightActionPerformed
    }                                                                                //GEN-LAST:event_txtHighlightActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final File file = StaticSwingTools.chooseFile(lastPath, false, null, "(*)", this);

        if (file != null) {
            lastPath = file.getParent();
            setFile(file);
        }
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void readInscriberCache() {
        try {
            cache.load(new FileInputStream(cacheFile));
            final String h = cache.getProperty(KEY_HIGHLIGHT).toString();
            final String s = cache.getProperty(KEY_SIGNATURE).toString();
            final String l = cache.getProperty(KEY_LOC_DESC).toString();
            final String e = cache.getProperty(KEY_E_NR).toString();
            txtHighlight.setText(h);
            txtSignature.setText(s);
            txtENr.setText(e);
            txtLocationDescription.setText(l);
        } catch (Throwable t) {
            log.warn("Error while reading the InscriberCache", t); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void writeInscriberCache() {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        cache.store(new FileOutputStream(cacheFile), "Saved: " + System.currentTimeMillis()); // NOI18N
                    } catch (Throwable t) {
                        log.warn("Error while writing the InscriberCache", t);                                // NOI18N
                    }
                }
            };
        CismetThreadPool.execute(r);
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        try {
            final Transferable tr = dtde.getTransferable();
            final DataFlavor[] flavors = tr.getTransferDataFlavors();
            boolean isAccepted = false;
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType()) {
                    // zunaechst annehmen
                    dtde.acceptDrop(dtde.getDropAction());
                    final List<File> files = (List<File>)tr.getTransferData(flavors[i]);
                    if ((files != null) && (files.size() > 0)) {
                        setFile(files.get(0));
                    }
                    dtde.dropComplete(true);
                    return;
                } else if (flavors[i].isRepresentationClassInputStream()) {
                    // this is used under linux
                    if (!isAccepted) {
                        dtde.acceptDrop(dtde.getDropAction());
                        isAccepted = true;
                    }
                    final BufferedReader br = new BufferedReader(new InputStreamReader(
                                (InputStream)tr.getTransferData(flavors[i])));
                    String tmp = null;
                    final List<File> fileList = new ArrayList<File>();
                    while ((tmp = br.readLine()) != null) {
                        if (tmp.trim().startsWith(FILE_PROTOCOL_PREFIX)) {
                            File f = new File(tmp.trim().substring(FILE_PROTOCOL_PREFIX.length()));
                            if (f.exists()) {
                                fileList.add(f);
                            } else {
                                f = new File(URLDecoder.decode(
                                            tmp.trim().substring(FILE_PROTOCOL_PREFIX.length()),
                                            "UTF-8"));

                                if (f.exists()) {
                                    fileList.add(f);
                                } else {
                                    log.warn("File " + f.toString() + " does not exist.");
                                }
                            }
                        }
                    }
                    br.close();

                    if ((fileList != null) && (fileList.size() > 0)) {
                        setFile(fileList.get(0));
                        dtde.dropComplete(true);
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            log.warn(ex, ex);
        }
        // Problem ist aufgetreten
        dtde.rejectDrop();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  imageFile  DOCUMENT ME!
     */
    private void setFile(final File imageFile) {
        try {
            final BufferedImage image = ImageIO.read(imageFile);

            if (image != null) {
                file = imageFile;
                lblFile.setText(imageFile.getName());
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    NbBundle.getMessage(A4HMap1Picture.class, "A4HMap1Picture.setFile().null.message"),
                    NbBundle.getMessage(A4HMap1Picture.class, "A4HMap1Picture.setFile().null.title"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
            final ErrorInfo errorInfo = new ErrorInfo(
                    NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.setFile().exception.title"),
                    NbBundle.getMessage(
                        A4HMap1Picture.class,
                        "A4HMap1Picture.setFile().exception.message"),
                    null,
                    null,
                    e,
                    Level.ALL,
                    null);
            JXErrorPane.showDialog(CismapBroker.getInstance().getMappingComponent(), errorInfo);
        }
    }
}
