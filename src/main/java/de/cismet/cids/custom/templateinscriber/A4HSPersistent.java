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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.*;

import javax.swing.JCheckBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cismap.commons.MappingModel;
import de.cismet.cismap.commons.RetrievalServiceLayer;
import de.cismet.cismap.commons.ServiceLayer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.printing.AbstractPrintingInscriber;
import de.cismet.cismap.commons.gui.printing.FileNameChangedEvent;
import de.cismet.cismap.commons.gui.printing.FilenamePrintingInscriber;
import de.cismet.cismap.commons.gui.printing.FilenamePrintingInscriberListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.rasterservice.MapService;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class A4HSPersistent extends AbstractPrintingInscriber implements FilenamePrintingInscriber {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_HIGHLIGHT = "Ueberschrift";
    public static final String KEY_SIGNATURE = "Unterschrift";
    public static final String KEY_E_NR = "ENr";
    public static final String KEY_LOC_DESC = "Lagebezeichnung";
    public static final String KEY_DATA = "Datenart";
    public static final String KEY_DATASOURCES = "Datenquellen";
    private static final String CBO_DATA_PROPERTIES = "CboData.properties";
    private static final Logger LOG = Logger.getLogger(A4HSPersistent.class);

    //~ Instance fields --------------------------------------------------------

    String cacheFile = ""; // NOI18N
    Properties cache = new Properties();
    private final ArrayList<JCheckBox> chkDataSourcesList;
    private List<FilenamePrintingInscriberListener> listeners = new ArrayList<>();
    private String oldText = "";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboData;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDataSources;
    private javax.swing.JLabel lblENr;
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
    public A4HSPersistent() {
        initComponents();
        cacheFile = CismapBroker.getInstance().getCismapFolderPath() + System.getProperty("file.separator")
                    + "inscriberCache"; // NOI18N
        readInscriberCache();

        this.chkDataSourcesList = new ArrayList<>();

        this.setUpDataSourceChks();
        this.setUpDataCbo();

        oldText = txtHighlight.getText();

        txtHighlight.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    onChange(e);
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    onChange(e);
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    onChange(e);
                }

                private void onChange(final DocumentEvent e) {
                    final FileNameChangedEvent event = new FileNameChangedEvent(oldText, txtHighlight.getText());

                    for (final FilenamePrintingInscriberListener listener : listeners) {
                        listener.fileNameChanged(event);
                    }

                    oldText = txtHighlight.getText();
                }
            });
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
        final HashMap<String, String> hm = new HashMap<>();
        hm.put(KEY_HIGHLIGHT, txtHighlight.getText());
        hm.put(KEY_SIGNATURE, txtSignature.getText());
        hm.put(KEY_E_NR, txtENr.getText());
        hm.put(KEY_LOC_DESC, txtLocationDescription.getText());
        hm.put(KEY_DATA, String.valueOf(cboData.getSelectedItem()));
        hm.put(KEY_DATASOURCES, this.getSelectedDataSourcesString());

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

        setPreferredSize(new java.awt.Dimension(312, 245));

        lblHighlight.setText(org.openide.util.NbBundle.getMessage(
                A4HSPersistent.class,
                "A4HSPersistent.lblHighlight.text")); // NOI18N

        txtHighlight.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtHighlightActionPerformed(evt);
                }
            });

        lblSignature.setText(org.openide.util.NbBundle.getMessage(
                A4HSPersistent.class,
                "A4HSPersistent.lblSignature.text")); // NOI18N

        lblENr.setText(org.openide.util.NbBundle.getMessage(A4HSPersistent.class, "A4HSPersistent.lblENr.text")); // NOI18N

        lblLocationDescription.setText(org.openide.util.NbBundle.getMessage(
                A4HSPersistent.class,
                "A4HSPersistent.lblLocationDescription.text")); // NOI18N

        lblData.setText(org.openide.util.NbBundle.getMessage(A4HSPersistent.class, "A4HSPersistent.lblData.text")); // NOI18N

        lblDataSources.setText(org.openide.util.NbBundle.getMessage(
                A4HSPersistent.class,
                "A4HSPersistent.lblDataSources.text")); // NOI18N

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(125, 56));
        jScrollPane2.setOpaque(false);

        pnlDataSources.setFocusTraversalPolicyProvider(true);
        pnlDataSources.setMinimumSize(new java.awt.Dimension(123, 20));
        pnlDataSources.setLayout(new javax.swing.BoxLayout(pnlDataSources, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(pnlDataSources);

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(lblData).add(
                        lblSignature).add(lblHighlight).add(lblENr).add(lblLocationDescription).add(lblDataSources))
                            .add(
                                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                    layout.createSequentialGroup().add(7, 7, 7).add(
                                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                            txtLocationDescription).add(txtSignature).add(txtENr).add(
                                            cboData,
                                            0,
                                            198,
                                            Short.MAX_VALUE).add(txtHighlight))).add(
                                    org.jdesktop.layout.GroupLayout.TRAILING,
                                    layout.createSequentialGroup().addPreferredGap(
                                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                                        jScrollPane2,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblENr).add(
                        txtENr,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblLocationDescription)
                                .add(
                                    txtLocationDescription,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblSignature).add(
                        txtSignature,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblHighlight).add(
                        txtHighlight,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        cboData,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        lblData,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        27,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        layout.createSequentialGroup().add(lblDataSources).add(0, 106, Short.MAX_VALUE)).add(
                        jScrollPane2,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        lblHighlight.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblHighlight.AccessibleContext.accessibleName"));           // NOI18N
        lblSignature.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblSignature.AccessibleContext.accessibleName"));           // NOI18N
        lblENr.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblENr.AccessibleContext.accessibleName"));                 // NOI18N
        lblLocationDescription.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblLocationDescription.AccessibleContext.accessibleName")); // NOI18N
        lblData.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblData.AccessibleContext.accessibleName"));                // NOI18N
        lblDataSources.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HSPersistent.class,
                        "A4HSPersistent.lblDataSources.AccessibleContext.accessibleName"));         // NOI18N
    }                                                                                               // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtHighlightActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtHighlightActionPerformed
    }                                                                                //GEN-LAST:event_txtHighlightActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void readInscriberCache() {
        try {
            cache.load(new FileInputStream(cacheFile));
            final String h = cache.getProperty(KEY_HIGHLIGHT);
            final String s = cache.getProperty(KEY_SIGNATURE);
            final String l = cache.getProperty(KEY_LOC_DESC);
            final String e = cache.getProperty(KEY_E_NR);
            txtHighlight.setText(h);
            txtSignature.setText(s);
            txtENr.setText(e);
            txtLocationDescription.setText(l);
        } catch (Throwable t) {
            LOG.warn("Error while reading the InscriberCache", t); // NOI18N
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
                        LOG.warn("Error while writing the InscriberCache", t);                                // NOI18N
                    }
                }
            };
        CismetThreadPool.execute(r);
    }

    @Override
    public void addFilenameChangeListener(final FilenamePrintingInscriberListener listener) {
        listeners.add(listener);
    }

    @Override
    public String getFileName() {
        return txtHighlight.getText();
    }
}
