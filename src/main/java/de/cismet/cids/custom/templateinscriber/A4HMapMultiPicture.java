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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeListener;

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

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.clientutils.ConversionUtils;

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

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class A4HMapMultiPicture extends AbstractPrintingInscriber implements DropTargetListener,
    FilenamePrintingInscriber {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_HEAD = "Ueberschrift";
    public static final String KEY_TITLE = "Titel";
    public static final String KEY_DATE = "Datum";
    public static final String KEY_NAME = "Name";
    public static final String KEY_PUBLISHER = "Herausgeber";
    public static final String KEY_COPYRIGHT = "Copyright";
    public static final String KEY_COPYRIGHT_NAME = "Copyright Name";
    public static final String KEY_DATASOURCES = "Datenquellen";
    private static final String COPYRIGHT_PROPERTIES = "/copyrights.xml";
    private static final Logger LOG = Logger.getLogger(A4HMapMultiPicture.class);
    private static final String FILE_PROTOCOL_PREFIX = "file://";
    private static final String IMAGE1 = "Bild1";
    private static final String IMAGE2 = "Bild2";
    private static final String IMAGE3 = "Bild3";
    private static final String SIGNATURE1 = "Unterschrift1";
    private static final String SIGNATURE2 = "Unterschrift2";
    private static final String SIGNATURE3 = "Unterschrift3";

    //~ Instance fields --------------------------------------------------------

    String cacheFile = ""; // NOI18N
    Properties cache = new Properties();
    private final ArrayList<JCheckBox> chkDataSourcesList;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String lastPath = null;
    private CustomTableModel model = new CustomTableModel();
    private final Map<String, String> copyrightMap = new HashMap<>();
    private DropTarget dropTarget;
    private List<FilenamePrintingInscriberListener> listeners = new ArrayList<>();
    private String oldText = "";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboCopyright;
    private javax.swing.JPanel filePanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblData1;
    private javax.swing.JLabel lblDataSources;
    private javax.swing.JLabel lblENr;
    private javax.swing.JLabel lblHighlight;
    private javax.swing.JLabel lblHighlight1;
    private javax.swing.JLabel lblLocationDescription;
    private javax.swing.JLabel lblSignature;
    private javax.swing.JPanel pnlDataSources;
    private javax.swing.JTable tabFile;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtHead;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form A4H.
     */
    public A4HMapMultiPicture() {
        initComponents();
        cacheFile = CismapBroker.getInstance().getCismapFolderPath() + System.getProperty("file.separator")
                    + "multiPictureinscriberCache"; // NOI18N
        readInscriberCache();

        this.chkDataSourcesList = new ArrayList<>();

        this.setUpDataSourceChks();
        this.setUpDataCbo();
        tabFile.setModel(model);
        final Action a = new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final int modelRow = Integer.valueOf(e.getActionCommand());
                    final File file = StaticSwingTools.chooseFile(
                            lastPath,
                            false,
                            null,
                            "Alle Dateien",
                            A4HMapMultiPicture.this);

                    if (file != null) {
                        lastPath = file.getParent();
                        setFile(file, modelRow);
                    } else {
                        model.setFileName(modelRow, "");
                        model.setValueAt("", modelRow, 0);
                        model.fireContentsChanged();
                    }
                }
            };

        final ButtonColumn bc = new ButtonColumn(tabFile, a, 1);
        tabFile.getColumn(tabFile.getColumnName(1)).setCellRenderer(bc);
        tabFile.getColumn(tabFile.getColumnName(1)).setCellEditor(bc);
        tabFile.getColumn(tabFile.getColumnName(1)).setPreferredWidth(25);
        addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    setColWidth();
                }
            });
        tabFile.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    setColWidth();
                }
            });
        dropTarget = new DropTarget(this, this);
        new DropTarget(tabFile, this);
        oldText = txtTitle.getText();

        txtTitle.getDocument().addDocumentListener(new DocumentListener() {

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
                    final FileNameChangedEvent event = new FileNameChangedEvent(oldText, txtTitle.getText());

                    for (final FilenamePrintingInscriberListener listener : listeners) {
                        listener.fileNameChanged(event);
                    }

                    oldText = txtTitle.getText();
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSize(final Dimension d) {
        super.setSize(d);
        setColWidth();
    }

    @Override
    public void setSize(final int width, final int height) {
        super.setSize(width, height);
        setColWidth();
    }

    /**
     * The column with the buttons should be smaller as the other columns.
     */
    private void setColWidth() {
        if (tabFile.getSize().getWidth() > 30) {
            tabFile.getColumn(tabFile.getColumnName(0)).setWidth(((int)tabFile.getSize().getWidth() - 25) / 2);
            tabFile.getColumn(tabFile.getColumnName(1)).setWidth(25);
            tabFile.getColumn(tabFile.getColumnName(2)).setWidth(((int)tabFile.getSize().getWidth() - 25) / 2);
            tabFile.getColumn(tabFile.getColumnName(0)).setMinWidth(((int)tabFile.getSize().getWidth() - 25) / 2);
            tabFile.getColumn(tabFile.getColumnName(1)).setMinWidth(25);
            tabFile.getColumn(tabFile.getColumnName(2)).setMinWidth(((int)tabFile.getSize().getWidth() - 25) / 2);
        }
    }

    /**
     * Fills the data source field.
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
     * Fill the copyright combo box.
     */
    private void setUpDataCbo() {
        final InputStream in = this.getClass().getResourceAsStream(COPYRIGHT_PROPERTIES);
        if (in == null) {
            LOG.error("Can not configuration file '" + COPYRIGHT_PROPERTIES
                        + "' in classpath. -> data combo box is empty");
        } else {
            try {
                final SAXBuilder builder = new SAXBuilder(false);
                final Document doc = builder.build(in);

                final Element rootObject = doc.getRootElement();
                for (final Object entry : rootObject.getChildren("copyrightEntry")) {
                    if (entry instanceof Element) {
                        final Element copyrightEntry = (Element)entry;

                        final String name = copyrightEntry.getChildText("name");
                        final String text = copyrightEntry.getChildText("text");
                        this.cboCopyright.addItem(name);
                        copyrightMap.put(name, text);
                    }
                }
            } catch (final Exception e) {
                LOG.warn("Error while reading the list with the recently opened files", e);
            }
        }
    }

    /**
     * Get the selected data sources as string. Example: - layer 1 - layer 2
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
        hm.put(KEY_NAME, txtName.getText());
        hm.put(KEY_HEAD, txtHead.getText());
        hm.put(KEY_DATE, txtDate.getText());
        hm.put(KEY_TITLE, txtTitle.getText());
        hm.put(KEY_PUBLISHER, txtAuthor.getText());
        hm.put(KEY_COPYRIGHT_NAME, String.valueOf(cboCopyright.getSelectedItem()));
        hm.put(KEY_COPYRIGHT, copyrightMap.get(String.valueOf(cboCopyright.getSelectedItem())));
        hm.put(KEY_DATASOURCES, this.getSelectedDataSourcesString());
        String imageString = null;

        try {
            imageString = (String)model.getFileName(0);
            if ((imageString != null) && !imageString.equals("")) {
                final BufferedImage image = ImageIO.read(new File(imageString));
                hm.put(IMAGE1, ConversionUtils.image2String(image));
                hm.put(SIGNATURE1, (String)model.getValueAt(0, 2));
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
        }

        try {
            imageString = (String)model.getFileName(1);
            if ((imageString != null) && !imageString.equals("")) {
                final BufferedImage image = ImageIO.read(new File(imageString));
                hm.put(IMAGE2, ConversionUtils.image2String(image));
                hm.put(SIGNATURE2, (String)model.getValueAt(1, 2));
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
        }

        try {
            imageString = (String)model.getFileName(2);
            if ((imageString != null) && !imageString.equals("")) {
                final BufferedImage image = ImageIO.read(new File(imageString));
                hm.put(IMAGE3, ConversionUtils.image2String(image));
                hm.put(SIGNATURE3, (String)model.getValueAt(2, 2));
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
        }

        cache.setProperty(KEY_NAME, txtName.getText());
        cache.setProperty(KEY_HEAD, txtHead.getText());
        cache.setProperty(KEY_DATE, txtDate.getText());
        cache.setProperty(KEY_TITLE, txtTitle.getText());
        cache.setProperty(KEY_PUBLISHER, txtAuthor.getText());
        cache.setProperty(KEY_COPYRIGHT_NAME, String.valueOf(cboCopyright.getSelectedItem()));

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
        txtName = new javax.swing.JTextField();
        lblSignature = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        lblENr = new javax.swing.JLabel();
        txtHead = new javax.swing.JTextField();
        lblLocationDescription = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        lblData = new javax.swing.JLabel();
        cboCopyright = new javax.swing.JComboBox();
        lblDataSources = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlDataSources = new javax.swing.JPanel();
        lblData1 = new javax.swing.JLabel();
        filePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabFile = new javax.swing.JTable();
        lblHighlight1 = new javax.swing.JLabel();
        txtAuthor = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(312, 245));
        setLayout(new java.awt.GridBagLayout());

        lblHighlight.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblHighlight.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblHighlight, gridBagConstraints);
        lblHighlight.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblHighlight.AccessibleContext.accessibleName")); // NOI18N

        txtName.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtName, gridBagConstraints);

        lblSignature.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblSignature.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblSignature, gridBagConstraints);
        lblSignature.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblSignature.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtDate, gridBagConstraints);

        lblENr.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblENr.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 5, 0);
        add(lblENr, gridBagConstraints);
        lblENr.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblENr.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 10);
        add(txtHead, gridBagConstraints);

        lblLocationDescription.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblLocationDescription.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblLocationDescription, gridBagConstraints);
        lblLocationDescription.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblLocationDescription.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtTitle, gridBagConstraints);

        lblData.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblData.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblData, gridBagConstraints);
        lblData.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblData.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(cboCopyright, gridBagConstraints);

        lblDataSources.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblDataSources.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        add(lblDataSources, gridBagConstraints);
        lblDataSources.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.lblDataSources.AccessibleContext.accessibleName")); // NOI18N

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
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        lblData1.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblData1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblData1, gridBagConstraints);

        filePanel.setMaximumSize(new java.awt.Dimension(452, 70));
        filePanel.setMinimumSize(new java.awt.Dimension(452, 70));
        filePanel.setPreferredSize(new java.awt.Dimension(452, 70));
        filePanel.setLayout(new java.awt.GridBagLayout());

        tabFile.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        jScrollPane1.setViewportView(tabFile);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        filePanel.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(filePanel, gridBagConstraints);

        lblHighlight1.setText(org.openide.util.NbBundle.getMessage(
                A4HMapMultiPicture.class,
                "A4HMapMultiPicture.lblHighlight1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(lblHighlight1, gridBagConstraints);

        txtAuthor.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtAuthorActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(txtAuthor, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtNameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtNameActionPerformed
    }                                                                           //GEN-LAST:event_txtNameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtAuthorActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtAuthorActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtAuthorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  imageFile  DOCUMENT ME!
     * @param  index      DOCUMENT ME!
     */
    private void setFile(final File imageFile, final int index) {
        try {
            final BufferedImage image = ImageIO.read(imageFile);

            if (image != null) {
                model.setValueAt(imageFile.getName(), index, 0);
                model.setFileName(index, imageFile.getAbsolutePath());
                model.fireContentsChanged();
            } else {
                JOptionPane.showMessageDialog(
                    A4HMapMultiPicture.this,
                    NbBundle.getMessage(A4HMapMultiPicture.class, "A4HMapMultiPicture.setFile().null.message"),
                    NbBundle.getMessage(A4HMapMultiPicture.class, "A4HMapMultiPicture.setFile().null.title"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOG.error("Cannot read image", e);
            final ErrorInfo errorInfo = new ErrorInfo(
                    NbBundle.getMessage(A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.setFile().exception.title"),
                    NbBundle.getMessage(A4HMapMultiPicture.class,
                        "A4HMapMultiPicture.setFile().exception.message"),
                    null,
                    null,
                    e,
                    Level.ALL,
                    null);
            JXErrorPane.showDialog(CismapBroker.getInstance().getMappingComponent(), errorInfo);
        }
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
                        final int row = tabFile.rowAtPoint(dtde.getLocation());
                        setFile(files.get(0), row);
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
                    String tmp;
                    final List<File> fileList = new ArrayList<>();

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

                    if (fileList.size() > 0) {
                        final int row = tabFile.rowAtPoint(dtde.getLocation());
                        setFile(fileList.get(0), row);
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
     */
    private void readInscriberCache() {
        try {
            cache.load(new FileInputStream(cacheFile));
            final String n = cache.getProperty(KEY_NAME);
            final String d = cache.getProperty(KEY_DATE);
            final String t = cache.getProperty(KEY_TITLE);
            final String h = cache.getProperty(KEY_HEAD);
            final String a = cache.getProperty(KEY_PUBLISHER);
            final String copyright = cache.getProperty(KEY_COPYRIGHT_NAME);
            txtName.setText(n);
            txtDate.setText(d);
            txtHead.setText(h);
            txtTitle.setText(t);
            txtAuthor.setText(a);
            cboCopyright.setSelectedItem(copyright);
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
    public void addFilenameChangeListener(final FilenamePrintingInscriberListener listener) {
        listeners.add(listener);
    }

    @Override
    public String getFileName() {
        return txtTitle.getText();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CustomTableModel implements TableModel {

        //~ Instance fields ----------------------------------------------------

        protected final List<TableModelListener> listener = new ArrayList<TableModelListener>();
        private final String[] colNames = new String[] { "Bilddatei", " ", "Bildunterschrift" };
        private final String[][] values = new String[][] {
                new String[] { "", "" },
                new String[] { "", "" },
                new String[] { "", "" }
            };
        private String[] fileName = { "", "", "" };

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return values.length;
        }

        @Override
        public int getColumnCount() {
            return colNames.length;
        }

        @Override
        public String getColumnName(final int columnIndex) {
            return colNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 1) {
                return JButton.class;
            } else {
                return String.class;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  index  DOCUMENT ME!
         * @param  path   DOCUMENT ME!
         */
        public void setFileName(final int index, final String path) {
            fileName[index] = path;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   index  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getFileName(final int index) {
            return fileName[index];
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return (columnIndex == 2) || (columnIndex == 1);
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return values[rowIndex][0];
                }
                case 1: {
                    return "...";
                }
                case 2: {
                    return values[rowIndex][1];
                }
                default: {
                    break;
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            if (aValue instanceof String) {
                if (columnIndex == 2) {
                    values[rowIndex][1] = (String)aValue;
                }
                if (columnIndex == 0) {
                    values[rowIndex][0] = (String)aValue;
                }
            }
        }

        @Override
        public void addTableModelListener(final TableModelListener l) {
            listener.add(l);
        }

        @Override
        public void removeTableModelListener(final TableModelListener l) {
            listener.remove(l);
        }

        /**
         * DOCUMENT ME!
         */
        public void fireContentsChanged() {
            final TableModelEvent e = new TableModelEvent(this);

            for (final TableModelListener tmp : listener) {
                tmp.tableChanged(e);
            }
        }
    }

    /**
     * The ButtonColumn class provides a renderer and an editor that looks like a JButton. The renderer and editor will
     * then be used for a specified column in the table. The TableModel will contain the String to be displayed on the
     * button.
     *
     * <p>The button can be invoked by a mouse click or by pressing the space bar when the cell has focus. Optionally a
     * mnemonic can be set to invoke the button. When the button is invoked the provided Action is invoked. The source
     * of the Action will be the table. The action command will contain the model row number of the button that was
     * clicked.</p>
     *
     * @version  $Revision$, $Date$
     */
    public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer,
        TableCellEditor,
        ActionListener,
        MouseListener {

        //~ Instance fields ----------------------------------------------------

        private JTable table;
        private Action action;
        private int mnemonic;
        private Border originalBorder;
        private Border focusBorder;

        private JButton renderButton;
        private JButton editButton;
        private Object editorValue;
        private boolean isButtonColumnEditor;

        //~ Constructors -------------------------------------------------------

        /**
         * Create the ButtonColumn to be used as a renderer and editor. The renderer and editor will automatically be
         * installed on the TableColumn of the specified column.
         *
         * @param  table   the table containing the button renderer/editor
         * @param  action  the Action to be invoked when the button is invoked
         * @param  column  the column to which the button renderer/editor is added
         */
        public ButtonColumn(final JTable table, final Action action, final int column) {
            this.table = table;
            this.action = action;

            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            originalBorder = editButton.getBorder();
            setFocusBorder(new LineBorder(Color.BLUE));

            final TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
            table.addMouseListener(this);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Get foreground color of the button when the cell has focus.
         *
         * @return  the foreground color
         */
        public Border getFocusBorder() {
            return focusBorder;
        }

        /**
         * The foreground color of the button when the cell has focus.
         *
         * @param  focusBorder  the foreground color
         */
        public void setFocusBorder(final Border focusBorder) {
            this.focusBorder = focusBorder;
            editButton.setBorder(focusBorder);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getMnemonic() {
            return mnemonic;
        }

        /**
         * The mnemonic to activate the button when the cell has focus.
         *
         * @param  mnemonic  the mnemonic
         */
        public void setMnemonic(final int mnemonic) {
            this.mnemonic = mnemonic;
            renderButton.setMnemonic(mnemonic);
            editButton.setMnemonic(mnemonic);
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final int row,
                final int column) {
            if (value == null) {
                editButton.setText("");
                editButton.setIcon(null);
            } else if (value instanceof Icon) {
                editButton.setText("");
                editButton.setIcon((Icon)value);
            } else {
                editButton.setText(value.toString());
                editButton.setIcon(null);
            }

            this.editorValue = value;
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return editorValue;
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            if (isSelected) {
                renderButton.setForeground(table.getSelectionForeground());
                renderButton.setBackground(table.getSelectionBackground());
            } else {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }

            if (hasFocus) {
                renderButton.setBorder(focusBorder);
            } else {
                renderButton.setBorder(originalBorder);
            }

            if (value == null) {
                renderButton.setText("");
                renderButton.setIcon(null);
            } else if (value instanceof Icon) {
                renderButton.setText("");
                renderButton.setIcon((Icon)value);
            } else {
                renderButton.setText(value.toString());
                renderButton.setIcon(null);
            }

            return renderButton;
        }

        /*
         *      The button has been pressed. Stop editing and invoke the custom Action
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            final int row = table.convertRowIndexToModel(table.getEditingRow());
            fireEditingStopped();

            // Invoke the Action

            final ActionEvent event = new ActionEvent(
                    table,
                    ActionEvent.ACTION_PERFORMED,
                    ""
                            + row);
            action.actionPerformed(event);
        }

//
//  Implement MouseListener interface
//
        /*
         *  When the mouse is pressed the editor is invoked. If you then then drag the mouse to another cell before
         * releasing it, the editor is still active. Make sure editing is stopped when the mouse is released.
         */
        @Override
        public void mousePressed(final MouseEvent e) {
            if (table.isEditing()
                        && (table.getCellEditor() == this)) {
                isButtonColumnEditor = true;
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            if (isButtonColumnEditor
                        && table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }

            isButtonColumnEditor = false;
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
        }
        @Override
        public void mouseEntered(final MouseEvent e) {
        }
        @Override
        public void mouseExited(final MouseEvent e) {
        }
    }
}
