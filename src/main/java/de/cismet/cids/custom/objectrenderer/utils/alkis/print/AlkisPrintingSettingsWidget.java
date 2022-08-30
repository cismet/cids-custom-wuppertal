/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * PrintingSettingsWidget.java
 *
 * Created on 10. Juli 2006, 14:06
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis.print;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.butler.Butler2Dialog;
import de.cismet.cids.custom.butler.ButlerGeometryComboBox;
import de.cismet.cids.custom.butler.CoordWrapper;
import de.cismet.cids.custom.nas.NasFeeCalculator;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisProducts;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.AlkisProductDescription;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;
import de.cismet.cids.custom.wunda_blau.search.server.CidsAlkisSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class AlkisPrintingSettingsWidget extends javax.swing.JDialog implements CidsBeanDropListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AlkisPrintingSettingsWidget.class);
    private static final String ALKIS_LANDPARCEL_TABLE = "ALKIS_LANDPARCEL";
    private static final String ALKIS_BUCHUNGSBLATT_TABLE = "ALKIS_BUCHUNGSBLATT";
    private static final String X_POS = "X_POS";
    private static final String Y_POS = "Y_POS";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static HashMap<String, CoordWrapper> gkRahmenKartenMap = new HashMap<String, CoordWrapper>();
    private static HashMap<String, CoordWrapper> etrsRahmenKartenMap = new HashMap<String, CoordWrapper>();

    static {
        final Properties gkRahmenProp = new Properties();
        final Properties etrsRahmenProp = new Properties();
        try {
            gkRahmenProp.load(Butler2Dialog.class.getResourceAsStream("rahmenkarten_gk.properties"));
            loadPropertiesIntoMap(gkRahmenProp, gkRahmenKartenMap);
            etrsRahmenProp.load(Butler2Dialog.class.getResourceAsStream("rahmenkarten_etrs.properties"));
            loadPropertiesIntoMap(etrsRahmenProp, etrsRahmenKartenMap);
        } catch (IOException ex) {
            LOG.error("Could not read property file with defined boxes for butler 1", ex);
        }
    }

    private static Integer X = null;
    private static Integer Y = null;

    //~ Instance fields --------------------------------------------------------

    boolean coodChangedFlag = false;

    boolean updateGeomRunning = false;

    private final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
    private final DefaultListModel cidsObjectListModel = new DefaultListModel();
    private AlkisPrintListener mapPrintListener;
//    private Geometry allObjectsGeometryUnion;
    private final ActionListener updatePrintingGeometryAction = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                mapPrintListener.refreshPreviewGeometry(
                    getSelectedProduct(),
                    getCurrentGeometry(),
                    chkRotation.isSelected());
            }
        };

    private AlkisProductDescription defaultProduct = null;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final java.text.DecimalFormat coordFormatter = new java.text.DecimalFormat("#.###");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox cbClazz;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JComboBox cbGeoms;
    private javax.swing.JComboBox cbProduct;
    private javax.swing.JComboBox cbScales;
    private javax.swing.JCheckBox chkRotation;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblPointGeoms;
    private javax.swing.JLabel lblRahmenkartenNr;
    private javax.swing.JList lstFlurstuecke;
    private javax.swing.JPanel panDesc;
    private javax.swing.JPanel panSettings;
    private javax.swing.JScrollPane scpAdditionalText;
    private javax.swing.JScrollPane scpFlurstuecke;
    private javax.swing.JTextArea taAdditionalText;
    private javax.swing.JTextField tfE;
    private javax.swing.JTextField tfN;
    private javax.swing.JTextField tfRahmenkartenNr;
    private javax.swing.JTextField txtAuftragsnummer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PrintingSettingsWidget.
     */
    public AlkisPrintingSettingsWidget() {
        super(StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()), false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Geometry getCurrentGeometry() {
        if (jPanel2.equals(jTabbedPane1.getSelectedComponent())) {
            return unionAllObjectsGeometries();
        } else {
            final Geometry geom = (cbGeoms.getSelectedItem() instanceof Geometry) ? (Geometry)cbGeoms.getSelectedItem()
                                                                                  : null;
            if (geom != null) {
                return geom;
            } else {
                try {
                    final int currentSrid = CrsTransformer.extractSridFromCrs(mappingComponent.getMappingModel()
                                    .getSrs().getCode());
                    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(
                                PrecisionModel.FLOATING),
                            currentSrid);
                    return geometryFactory.createPoint(new Coordinate(
                                Double.parseDouble(tfE.getText()),
                                Double.parseDouble(tfN.getText())));
                } catch (final Exception ex) {
                    return null;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  features  DOCUMENT ME!
     */
    private void featureChanged(final Collection<Feature> features) {
        if ((cbGeoms != null) && (features != null) && !features.isEmpty()
                    && !(features.iterator().next() instanceof AlkisPrintListener.PrintFeature)) {
            ((ButlerGeometryComboBox)cbGeoms).refresh();
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.mappingComponent.getFeatureCollection().addFeatureCollectionListener(new FeatureCollectionListener() {

                @Override
                public void featuresAdded(final FeatureCollectionEvent fce) {
                    featureChanged(fce.getEventFeatures());
                }

                @Override
                public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
                    featureChanged(fce.getEventFeatures());
                }

                @Override
                public void featuresRemoved(final FeatureCollectionEvent fce) {
                    featureChanged(fce.getEventFeatures());
                }

                @Override
                public void featuresChanged(final FeatureCollectionEvent fce) {
                    featureChanged(fce.getEventFeatures());
                }

                @Override
                public void featureSelectionChanged(final FeatureCollectionEvent fce) {
                }

                @Override
                public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
                }

                @Override
                public void featureCollectionChanged() {
                }
            });

        final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        coordFormatter.setDecimalFormatSymbols(formatSymbols);

        initComponents();
        getRootPane().setDefaultButton(cmdOk);
        cbClazz.setModel(getProductClassModel());
        if (defaultProduct != null) {
            cbClazz.setSelectedItem(defaultProduct.getClazz());
            cbProduct.setSelectedItem(defaultProduct.getType());
        } else {
            cbClazz.setSelectedIndex(cbClazz.getModel().getSize() - 1);
            cbProduct.setSelectedIndex(cbProduct.getModel().getSize() - 1);
        }
        this.panDesc.setBackground(new Color(216, 228, 248));
        // enable D&D
        new CidsBeanDropTarget(this);
        // refreshPreviewGeometry PrintListener
        this.mapPrintListener = new AlkisPrintListener(mappingComponent, this);
        this.mappingComponent.addInputListener(MappingComponent.ALKIS_PRINT, mapPrintListener);
//        updateFormatProposal();

        lstFlurstuecke.setCellRenderer(new ListCellRenderer() {

                DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final Component defaultC = dlcr.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value instanceof CidsBean) {
                        final ImageIcon icon = new ImageIcon(
                                ((CidsBean)value).getMetaObject().getMetaClass().getObjectIcon().getImageData());
                        if (defaultC instanceof JLabel) {
                            ((JLabel)defaultC).setIcon(icon);
                        }
                    }
                    return defaultC;
                }
            });

        tfRahmenkartenNr.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    updateForRahmenKartenNr();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    updateForRahmenKartenNr();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    updateForRahmenKartenNr();
                }
            });

        tfE.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }
            });

        tfN.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    coordinatesChanged();
                }
            });
    }
    /**
     * DOCUMENT ME!
     */
    private void coordinatesChanged() {
        if (!coodChangedFlag) {
            try {
                coodChangedFlag = true;
                if ((coordFormatter.parse(tfE.getText()) != null) && (coordFormatter.parse(tfN.getText()) != null)) {
                    updateFormatProposal();
                }
            } catch (final ParseException ex) {
                // do nothing
            } finally {
                coodChangedFlag = false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  prop  DOCUMENT ME!
     * @param  map   DOCUMENT ME!
     */
    private static void loadPropertiesIntoMap(final Properties prop, final Map map) {
        final Enumeration keys = prop.propertyNames();
        final ArrayList<String> keyList = new ArrayList<>();
        while (keys.hasMoreElements()) {
            final String key = (String)keys.nextElement();
            keyList.add(key);
        }
        for (final String key : keyList) {
            final String[] splittedVal = ((String)prop.getProperty(key)).split(";");
            final double middleE = Double.parseDouble(splittedVal[0]);
            final double middleN = Double.parseDouble(splittedVal[1]);
            final CoordWrapper coord = new CoordWrapper(middleE, middleN);
            map.put(key, coord);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateForRahmenKartenNr() {
        boolean inputError = false;
        boolean possibleEtrsNumber = false;

        CoordWrapper coord = null;
        final String enteredRahmenKartenNr = tfRahmenkartenNr.getText();
        if (enteredRahmenKartenNr.length() == 5) {
            // GK-Rahmenkartennummer
            coord = gkRahmenKartenMap.get(enteredRahmenKartenNr);
            if (coord != null) {
                tfE.setText("" + coord.getMiddleE());
                tfN.setText("" + coord.getMiddleN());
            } else {
                // we need to check if the entered number can be a valid etrs rahmenkartennummer
                inputError = true;
                for (int i = 1; i <= 9; i++) {
                    final String etrsRahmenNumber = enteredRahmenKartenNr + "" + i;
                    if (etrsRahmenKartenMap.get(etrsRahmenNumber) != null) {
                        possibleEtrsNumber = true;
                        break;
                    }
                }
            }
        } else if (enteredRahmenKartenNr.length() == 6) {
            // ETRS-Rahmenkartennummer
            coord = etrsRahmenKartenMap.get(enteredRahmenKartenNr);
            if (coord != null) {
                cbGeoms.setSelectedIndex(-1);
                tfE.setText("" + coord.getMiddleE());
                tfN.setText("" + coord.getMiddleN());
            } else {
                inputError = true;
            }
        } else if (enteredRahmenKartenNr.length() > 6) {
            inputError = true;
        }
        if (inputError && !possibleEtrsNumber) {
            setAlwaysOnTop(false);
            try {
                JOptionPane.showMessageDialog(
                    StaticSwingTools.getParentFrame(AlkisPrintingSettingsWidget.this),
                    org.openide.util.NbBundle.getMessage(
                        Butler2Dialog.class,
                        "Butler2Dialog.RahmenkartenNrCheck.JOptionPane.message"),
                    org.openide.util.NbBundle.getMessage(
                        Butler2Dialog.class,
                        "Butler2Dialog.RahmenkartenNrCheck.JOptionPane.title"),
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                setAlwaysOnTop(true);
            }
        } else if (!inputError && (coord != null)) {
            updateFormatProposal();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    @Override
    public void setVisible(final boolean b) {
        cidsObjectListModel.clear();
        tfE.setText("");
        tfN.setText("");
        tfRahmenkartenNr.setText("");
        cbGeoms.setSelectedItem(null);
        Collection<CidsBean> beansToPrint = getObjectBeansInMap();
        if (beansToPrint.isEmpty()) {
            beansToPrint = getBeansFromTreeSelection();
        } else if (beansToPrint.size() > 1) {
            final int dialogResult = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen alle ALKIS-Objekte der Karte gedruckt werden?",
                    "ALKIS-Objekte in Druckauswahl übernehmen",
                    JOptionPane.YES_NO_OPTION);
            if (JOptionPane.NO_OPTION == dialogResult) {
                beansToPrint.clear();
            }
        }

        for (final CidsBean currentBean : beansToPrint) {
            cidsObjectListModel.addElement(currentBean);
        }

        super.setVisible(b);

        updateFormatProposal();
        mapPrintListener.init();
        mapPrintListener.refreshPreviewGeometry(
            getSelectedProduct(),
            getCurrentGeometry(),
            chkRotation.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ComboBoxModel getProductClassModel() {
        final Set<String> classes = new HashSet<>();
        for (final AlkisProductDescription product : ClientAlkisProducts.getInstance().getAlkisMapProducts()) {
            classes.add(product.getClazz());
            if (product.isDefaultProduct()) {
                defaultProduct = product;
            }
        }
        return new DefaultComboBoxModel(classes.toArray());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ComboBoxModel getProductTypeModel() {
        final String clazz = String.valueOf(cbClazz.getSelectedItem());
        final Set<String> prodSet = new HashSet<>();
        final List<String> typesOrdered = new ArrayList<>();
        for (final AlkisProductDescription product : ClientAlkisProducts.getInstance().getAlkisMapProducts()) {
            if (clazz.equals(product.getClazz())) {
                if (prodSet.add(product.getType())) {
                    typesOrdered.add(product.getType());
                }
            }
        }
        return new DefaultComboBoxModel(typesOrdered.toArray());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Dimension getPreferredPositionOnScreen() {
//        final Preferences backingStore = Preferences.userNodeForPackage(AlkisPrintJButton.class);
//        final Dimension ret = new Dimension(-1, -1);
//        final int x = backingStore.getInt(X_POS, -1);
//        final int y = backingStore.getInt(Y_POS, -1);
//        ret.setSize(x, y);
//        return ret;
        final int x;
        final int y;
        if ((X != null) && (Y != null)) {
            x = X;
            y = Y;
        } else {
            x = -1;
            y = -1;
        }
        return new Dimension(x, y);
    }

    /**
     * DOCUMENT ME!
     */
    public void storePreferredPositionOnScreen() {
        X = getX();
        Y = getY();
//        final Preferences backingStore = Preferences.userNodeForPackage(AlkisPrintJButton.class);
//        backingStore.putInt(X_POS, this.getX());
//        backingStore.putInt(Y_POS, this.getY());
//        try {
//            backingStore.flush();
//        } catch (BackingStoreException ex) {
//            LOG.warn("Error when storing preferres position on screen", ex);
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ComboBoxModel[] getProductDetailModels() {
        final String clazz = String.valueOf(cbClazz.getSelectedItem());
        final String type = String.valueOf(cbProduct.getSelectedItem());
        final ComboBoxModel[] result = new ComboBoxModel[2];
        final Set<String> prodScale = new TreeSet<>(AlphanumComparator.getInstance());
        final Set<String> prodLayout = new HashSet<>();
        final List<LayoutMetaInfo> prodLayoutOrdered = new ArrayList<>();
        for (final AlkisProductDescription product : ClientAlkisProducts.getInstance().getAlkisMapProducts()) {
            if (clazz.equals(product.getClazz()) && type.equals(product.getType())) {
                prodScale.add(product.getMassstab());
                if (prodLayout.add(product.getDinFormat())) {
                    prodLayoutOrdered.add(new LayoutMetaInfo(
                            product.getDinFormat(),
                            product.getWidth(),
                            product.getHeight()));
                }
            }
        }
        result[0] = new DefaultComboBoxModel(prodLayoutOrdered.toArray());
        result[1] = new DefaultComboBoxModel(prodScale.toArray());
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private AlkisProductDescription getSelectedProduct() {
        final String clazz = String.valueOf(cbClazz.getSelectedItem());
        final String type = String.valueOf(cbProduct.getSelectedItem());
        final String scale = String.valueOf(cbScales.getSelectedItem());
        final String layout = String.valueOf(cbFormat.getSelectedItem());
        for (final AlkisProductDescription product : ClientAlkisProducts.getInstance().getAlkisMapProducts()) {
            if (clazz.equals(product.getClazz()) && type.equals(product.getType())
                        && scale.equals(product.getMassstab()) && layout.equals(product.getDinFormat())) {
                return product;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getObjectBeansInMap() {
        final Collection<CidsBean> result = TypeSafeCollections.newArrayList();
        for (final Feature feature : mappingComponent.getPFeatureHM().keySet()) {
            if (feature instanceof CidsFeature) {
                final CidsFeature cidsFeature = (CidsFeature)feature;
                if (cidsFeature.getGeometry() != null) {
                    result.add(cidsFeature.getMetaObject().getBean());
                }
            }
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getBeansFromTreeSelection() {
        final Collection<CidsBean> result = TypeSafeCollections.newArrayList();
        final Collection<?> nodes = ComponentRegistry.getRegistry().getActiveCatalogue().getSelectedNodes();
        if (nodes != null) {
            for (final Object nodeObj : nodes) {
                if (nodeObj instanceof ObjectTreeNode) {
                    try {
                        final ObjectTreeNode metaTreeNode = (ObjectTreeNode)nodeObj;
                        final CidsFeature cidsFeature = new CidsFeature(metaTreeNode.getMetaObject());
                        if (cidsFeature.getGeometry() != null) {
                            result.add(metaTreeNode.getMetaObject().getBean());
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }
        }
        if (!result.isEmpty()) {
            jTabbedPane1.setSelectedComponent(jPanel2);
        }
        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panDesc = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        panSettings = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        scpAdditionalText = new javax.swing.JScrollPane();
        taAdditionalText = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        cbScales = new javax.swing.JComboBox();
        cbFormat = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        chkRotation = new javax.swing.JCheckBox();
        cbProduct = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cbClazz = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtAuftragsnummer = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        scpFlurstuecke = new javax.swing.JScrollPane();
        lstFlurstuecke = new javax.swing.JList();
        btnRemove = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblRahmenkartenNr = new javax.swing.JLabel();
        tfRahmenkartenNr = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tfE = new javax.swing.JTextField();
        tfN = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lblPointGeoms = new javax.swing.JLabel();
        cbGeoms = new ButlerGeometryComboBox(null);
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cmdCancel = new javax.swing.JButton();
        cmdOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(750, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panDesc.setBackground(java.awt.SystemColor.inactiveCaptionText);
        panDesc.setMaximumSize(new java.awt.Dimension(150, 150));
        panDesc.setMinimumSize(new java.awt.Dimension(150, 150));
        panDesc.setPreferredSize(new java.awt.Dimension(150, 150));
        panDesc.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Schritte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        panDesc.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 319;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDesc.add(jSeparator2, gridBagConstraints);

        jLabel2.setText("1. Einstellungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        panDesc.add(jLabel2, gridBagConstraints);

        jLabel3.setText("2. Druckbereich");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        panDesc.add(jLabel3, gridBagConstraints);

        jLabel5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/frameprint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = -8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 7);
        panDesc.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 339;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panDesc.add(jSeparator3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 127;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panDesc, gridBagConstraints);

        panSettings.setMaximumSize(new java.awt.Dimension(425, 300));
        panSettings.setMinimumSize(new java.awt.Dimension(425, 300));
        panSettings.setPreferredSize(new java.awt.Dimension(425, 300));
        panSettings.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("1. Einstellungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        panSettings.add(jLabel6, gridBagConstraints);

        jSeparator1.setMaximumSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 411;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 5);
        panSettings.add(jSeparator1, gridBagConstraints);

        jSeparator4.setMaximumSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 421;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panSettings.add(jSeparator4, gridBagConstraints);

        scpAdditionalText.setMinimumSize(new java.awt.Dimension(250, 110));
        scpAdditionalText.setPreferredSize(new java.awt.Dimension(250, 110));

        taAdditionalText.setColumns(20);
        taAdditionalText.setRows(5);
        scpAdditionalText.setViewportView(taAdditionalText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(scpAdditionalText, gridBagConstraints);

        jLabel10.setText("Zusätzlicher Text:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panSettings.add(jLabel10, gridBagConstraints);

        cbScales.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbScalesActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(cbScales, gridBagConstraints);

        cbFormat.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbFormatActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(cbFormat, gridBagConstraints);

        jLabel8.setText("Maßstab:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jLabel8, gridBagConstraints);

        jLabel7.setText("Format:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jLabel7, gridBagConstraints);

        chkRotation.setText("Drehwinkel vorschlagen:");
        chkRotation.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(chkRotation, gridBagConstraints);

        cbProduct.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbProductActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(cbProduct, gridBagConstraints);

        jLabel9.setText("Produkt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jLabel9, gridBagConstraints);

        jLabel12.setText("Produktklasse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jLabel12, gridBagConstraints);

        cbClazz.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbClazzActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(cbClazz, gridBagConstraints);

        jLabel4.setText("Auftragsnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(txtAuftragsnummer, gridBagConstraints);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jTabbedPane1StateChanged(evt);
                }
            });

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        scpFlurstuecke.setMinimumSize(new java.awt.Dimension(250, 110));
        scpFlurstuecke.setPreferredSize(new java.awt.Dimension(250, 110));

        lstFlurstuecke.setModel(cidsObjectListModel);
        scpFlurstuecke.setViewportView(lstFlurstuecke);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(scpFlurstuecke, gridBagConstraints);

        btnRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/editors/edit_remove_mini.png"))); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(btnRemove, gridBagConstraints);

        jTabbedPane1.addTab("Objekte", jPanel2);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblRahmenkartenNr.setText("Rahmenkarten-Nr.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblRahmenkartenNr, gridBagConstraints);

        tfRahmenkartenNr.setMinimumSize(new java.awt.Dimension(70, 27));
        tfRahmenkartenNr.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(tfRahmenkartenNr, gridBagConstraints);

        jLabel13.setText(
            "<html>Beispiel für gültige Rahmenkartennummern:<table cellpadding=0 cellspacing=0><tr><td>&nbsp;GK-System:</td><td>&nbsp;83826</td></tr><tr><td>&nbsp;ETRS89 System:</td><td>&nbsp;374815</td></tr>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel13, gridBagConstraints);

        jLabel15.setText("Eingabe ohne Punkt, Komma oder Leerstelle !");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel15, gridBagConstraints);

        jTabbedPane1.addTab("Rahmenkarte", jPanel3);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        tfE.setMinimumSize(new java.awt.Dimension(70, 27));
        tfE.setPreferredSize(new java.awt.Dimension(90, 27));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${geomSelected}"),
                tfE,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(tfE, gridBagConstraints);

        tfN.setMinimumSize(new java.awt.Dimension(70, 27));
        tfN.setPreferredSize(new java.awt.Dimension(90, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${geomSelected}"),
                tfN,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(tfN, gridBagConstraints);

        jLabel14.setText("Mittelpunkt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel14, gridBagConstraints);

        lblPointGeoms.setText("Kartengeometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(lblPointGeoms, gridBagConstraints);

        cbGeoms.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbGeomsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(cbGeoms, gridBagConstraints);

        jTabbedPane1.addTab("Manuell", jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSettings.add(jTabbedPane1, gridBagConstraints);

        jLabel11.setText("Position über:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panSettings.add(jLabel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panSettings, gridBagConstraints);

        cmdCancel.setMnemonic('A');
        cmdCancel.setText("Abbrechen");
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        jPanel1.add(cmdCancel);

        cmdOk.setMnemonic('O');
        cmdOk.setText("Ok");
        cmdOk.setEnabled(false);
        cmdOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOkActionPerformed(evt);
                }
            });
        jPanel1.add(cmdOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        bindingGroup.bind();

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOkActionPerformed
        super.dispose();
    }                                                                         //GEN-LAST:event_cmdOkActionPerformed
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCancelActionPerformed
        dispose();
    }                                                                             //GEN-LAST:event_cmdCancelActionPerformed

    @Override
    public void dispose() {
        mapPrintListener.cleanUpAndRestoreFeatures();
        storePreferredPositionOnScreen();
        super.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveActionPerformed
        final int[] sel = lstFlurstuecke.getSelectedIndices();
        for (int i = sel.length; --i >= 0;) {
            cidsObjectListModel.removeElementAt(sel[i]);
        }
        updateFormatProposal();
    }                                                                             //GEN-LAST:event_btnRemoveActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbClazzActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbClazzActionPerformed
        cbProduct.setModel(getProductTypeModel());
        cbProductActionPerformed(null);
        updateFormatProposal();
    }                                                                           //GEN-LAST:event_cbClazzActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbProductActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbProductActionPerformed
        final ComboBoxModel[] models = getProductDetailModels();
        cbFormat.setModel(models[0]);
        cbScales.setModel(models[1]);
        updateFormatProposal();
    }                                                                             //GEN-LAST:event_cbProductActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbScalesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbScalesActionPerformed
    }                                                                            //GEN-LAST:event_cbScalesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbFormatActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbFormatActionPerformed
    }                                                                            //GEN-LAST:event_cbFormatActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbGeomsActionPerformed
        updateFormatProposal();
        tfE.setEnabled(cbGeoms.getSelectedItem() == null);
        tfN.setEnabled(cbGeoms.getSelectedItem() == null);
    }                                                                           //GEN-LAST:event_cbGeomsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTabbedPane1StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jTabbedPane1StateChanged
        if (jPanel3.equals(jTabbedPane1.getSelectedComponent())) {
            updateForRahmenKartenNr();
        } else {
            updateFormatProposal();
        }
    }                                                                                //GEN-LAST:event_jTabbedPane1StateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    private void setGeomForCenter(final Geometry geom) {
        if (geom != null) {
            try {
                cbFormat.removeActionListener(updatePrintingGeometryAction);
                cbScales.removeActionListener(updatePrintingGeometryAction);
                chkRotation.removeActionListener(updatePrintingGeometryAction);

                cbScales.setSelectedIndex(cbScales.getModel().getSize() - 1);

                //
                if (!coodChangedFlag) {
                    final Point centroid = geom.getEnvelope().getCentroid();
                    tfE.setText("" + coordFormatter.format(centroid.getX()));
                    tfN.setText("" + coordFormatter.format(centroid.getY()));
                }
                //

                final BoundingBox allGeomBB = new BoundingBox(geom);

                Integer productDefaultScale = null;
                if (getSelectedProduct() != null) {
                    productDefaultScale = getSelectedProduct().getProductDefaultScale();
                }
                final boolean hit = checkAndSet(productDefaultScale, allGeomBB);
                updatePrintingGeometryAction.actionPerformed(null);
                if (hit) {
                    return;
                }

                chkRotation.setSelected(true);
                final String formatHint;
                if (allGeomBB.getWidth() >= allGeomBB.getHeight()) {
                    formatHint = "Hoch";
                    cbFormat.setSelectedIndex(cbFormat.getModel().getSize() - 1);
                } else {
                    cbFormat.setSelectedIndex(cbFormat.getModel().getSize() - 2);
                    formatHint = "Quer";
                }

                for (int i = cbFormat.getModel().getSize(); --i >= 0;) {
                    if (String.valueOf(cbFormat.getItemAt(i)).equals(formatHint)) {
                        cbFormat.setSelectedIndex(i);
                        break;
                    }
                }
            } finally {
                if (isVisible()) {
                    cbFormat.addActionListener(updatePrintingGeometryAction);
                    cbScales.addActionListener(updatePrintingGeometryAction);
                    chkRotation.addActionListener(updatePrintingGeometryAction);
                } else {
                    cbFormat.removeActionListener(updatePrintingGeometryAction);
                    cbScales.removeActionListener(updatePrintingGeometryAction);
                    chkRotation.removeActionListener(updatePrintingGeometryAction);
                }
            }
        } else {
            chkRotation.setSelected(false);
            cbFormat.setSelectedIndex(cbFormat.getModel().getSize() - 1);
            cbScales.setSelectedIndex(cbScales.getModel().getSize() - 1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beans  DOCUMENT ME!
     */
    @Override
    public void beansDropped(final ArrayList<CidsBean> beans) {
        if (beans != null) {
            for (final CidsBean bean : beans) {
                if (!cidsObjectListModel.contains(bean)) {
                    cidsObjectListModel.addElement(bean);
                }
            }
            jTabbedPane1.setSelectedComponent(jPanel2);
            updateFormatProposal();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   defaultScale  DOCUMENT ME!
     * @param   allGeomBB     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkAndSet(final Integer defaultScale, final BoundingBox allGeomBB) {
        boolean hit = false;
        // firstofall: test whether prefereed scalle exists and if it matches any format
        if (defaultScale != null) {
            final int preferredScaleIndex = ((DefaultComboBoxModel)cbScales.getModel()).getIndexOf(
                    defaultScale.toString());
            if (preferredScaleIndex > -1) {
                hit = formatCheckAndSet(preferredScaleIndex, allGeomBB);
                if (hit) {
                    return true;
                }
            }
        }

        // after thet checking all formats starting with the smalles scale
        for (int j = 0; j < cbScales.getModel().getSize(); ++j) {
            hit = formatCheckAndSet(j, allGeomBB);
            if (hit) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   scaleIndex  DOCUMENT ME!
     * @param   allGeomBB   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean formatCheckAndSet(final int scaleIndex, final BoundingBox allGeomBB) {
        for (int i = 0; i < cbFormat.getModel().getSize(); ++i) {
            final LayoutMetaInfo currentLayout = (LayoutMetaInfo)cbFormat.getItemAt(i);
            final Integer currentMassstab = Integer.parseInt(String.valueOf(cbScales.getItemAt(scaleIndex)));
            if (doesBoundingBoxFitIntoLayout(
                            allGeomBB,
                            currentLayout.width,
                            currentLayout.heigth,
                            currentMassstab)) {
                cbFormat.setSelectedIndex(i);
                cbScales.setSelectedIndex(scaleIndex);
                chkRotation.setSelected(false);
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    private void updateFormatProposal() {
        if (!updateGeomRunning) {
            updateGeomRunning = true;
            try {
                setGeomForCenter(getCurrentGeometry());
                syncOkButtonWithListStatus();
            } finally {
                updateGeomRunning = false;
            }
        }
    }

    /**
     * Adds the selected product to the DownloadManager.
     *
     * @param  geom           DOCUMENT ME!
     * @param  rotationAngle  DOCUMENT ME!
     */
    public void downloadProduct(final Geometry geom, final double rotationAngle) {
        if (!mapPrintListener.isFeatureInCollection()) {
            return;
        }

        final AlkisProductDescription selectedProduct = getSelectedProduct();
        final boolean isObjekte = jPanel2.equals(jTabbedPane1.getSelectedComponent());
        final String auftragsnummer = txtAuftragsnummer.getText();
        final String additionalText = taAdditionalText.getText();
        String landParcelCode = null;

        for (int i = 0; i < cidsObjectListModel.size(); ++i) {
            final Object o = cidsObjectListModel.get(i);
            if (isObjekte && (o instanceof CidsBean)) {
                if (((CidsBean)o).getMetaObject().getMetaClass().getTableName().equals(ALKIS_LANDPARCEL_TABLE)) {
                    landParcelCode = AlkisProducts.getLandparcelCodeFromParcelBeanObject(o);
                    break;
                } else if (((CidsBean)o).getMetaObject().getMetaClass().getTableName().equals(
                                ALKIS_BUCHUNGSBLATT_TABLE)) {
                    landParcelCode = String.valueOf(((CidsBean)o).getProperty("landparcels[0].landparcelcode"));
                    break;
                }
            }
        }

        final String objectLandparcel = landParcelCode;

        final Point center = geom.getEnvelope().getCentroid();
        final AlkisProductDownloadHelper.AlkisKarteDownloadInfoCreator creator =
            new AlkisProductDownloadHelper.AlkisKarteDownloadInfoCreator() {

                @Override
                public AlkisProductDownloadHelper.AlkisKarteDownloadInfo createInfo() throws Exception {
                    final String landParcelCode;
                    if (objectLandparcel != null) {
                        landParcelCode = objectLandparcel;
                    } else {
                        final CidsAlkisSearchStatement search = new CidsAlkisSearchStatement(
                                CidsAlkisSearchStatement.Resulttyp.FLURSTUECK,
                                CidsAlkisSearchStatement.SucheUeber.FLURSTUECKSNUMMER,
                                "%",
                                center);
                        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                    .customServerSearch(search, getConnectionContext());
                        if ((mons != null) && !mons.isEmpty()) {
                            final MetaObjectNode mon = mons.iterator().next();
                            landParcelCode = mon.toString();
                        } else {
                            landParcelCode = null;
                        }
                    }

                    try {
                        final AlkisProductDownloadHelper.AlkisKarteDownloadInfo info =
                            new AlkisProductDownloadHelper.AlkisKarteDownloadInfo(
                                selectedProduct.getCode(),
                                landParcelCode,
                                auftragsnummer.replaceAll("\\?", ""),
                                null,
                                additionalText,
                                selectedProduct.getMassstab(),
                                selectedProduct.getMassstabMin(),
                                selectedProduct.getMassstabMax(),
                                toInt(rotationAngle),
                                toInt(center.getX()),
                                toInt(center.getY()));

                        final AlkisProductDownloadHelper.AlkisKarteDownloadInfo infoVermerk =
                            new AlkisProductDownloadHelper.AlkisKarteDownloadInfo(
                                info.getProduct(),
                                info.getLandparcelCode(),
                                info.getAuftragsnummer(),
                                AlkisUtils.getFertigungsVermerk(
                                    null,
                                    getConnectionContext()),
                                info.getZusatz(),
                                info.getMassstab(),
                                info.getMassstabMin(),
                                info.getMassstabMax(),
                                toInt(info.getWinkel()),
                                info.getX(),
                                info.getY());

                        final Map<String, String> requestPerUsage = new HashMap<>();
                        requestPerUsage.put("WV ein", MAPPER.writeValueAsString(infoVermerk));

                        try {
                            final String product;
                            final String dinFormat = selectedProduct.getDinFormat();
                            final boolean isDinA4 = dinFormat.equals("DINA4 Hochformat")
                                        || dinFormat.equals("DINA4 Querformat");
                            final boolean isDinA3 = dinFormat.equals("DINA3 Hochformat")
                                        || dinFormat.equals("DINA3 Querformat");
                            final boolean isDinA2 = dinFormat.equals("DINA2 Hochformat")
                                        || dinFormat.equals("DINA2 Querformat");
                            final boolean isDinA1 = dinFormat.equals("DINA1 Hochformat")
                                        || dinFormat.equals("DINA1 Querformat");
                            final boolean isDinA0 = dinFormat.equals("DINA0 Hochformat")
                                        || dinFormat.equals("DINA0 Querformat");
                            final String clazz = selectedProduct.getClazz();
                            final boolean isGdbNrwAmtlich = clazz.equals("Gdb-NRW-Amtlich");
                            final boolean isNrwKommunal = clazz.equals("NRW-Kommunal");
                            final boolean isWupKommunal = clazz.equals("WUP-Kommunal");
                            final String type = selectedProduct.getType();
                            final boolean isFeldvergleichsKarte = type.equals("Feldvergleichskarte, farbig")
                                        || type.equals("Feldvergleichskarte, schwarzweiß");
                            final boolean isLiegenschaftsKarte = type.equals("Liegenschaftskarte, farbig")
                                        || type.equals("Liegenschaftskarte, grau");
                            final boolean isStadtgrundkarteMKO = type.equals(
                                    "Stadtgrundkarte m. kom. Erg., farbig")
                                        || type.equals("Stadtgrundkarte m. kom. Erg., schwarz-weiß");
                            final boolean isSchaetzungskarte = type.equals("Schätzungskarte, farbig")
                                        || type.equals("Schätzungskarte, grau");
                            final boolean isAmtlicheBasiskarte = type.equals(
                                    "Amtliche Basiskarte (farbig)")
                                        || type.equals("Amtliche Basiskarte, grau");
                            final boolean isStadtgrundkarte = type.equals("Stadtgrundkarte, farbig")
                                        || type.equals("Stadtgrundkarte, grau");
                            final boolean isDgk = type.equals("DGK");
                            final boolean isOrthofoto = type.equals("Orthofoto");
                            final boolean isNivPUebersicht = type.equals("NivP-Übersicht");
                            final boolean isApUebersicht = type.equals("AP-Übersicht");
                            final boolean isPunktnummerierungsuebersicht = type.equals(
                                    "Punktnumerierungsübersicht");
                            final boolean isDgkMitHoehenlinien = type.equals("ABK mit Höhenlinien");
                            final boolean isStadtgrundkarteMitHoehenlinien = type.equals(
                                    "Stadtgrundkarte mit Höhenlinien");
                            final boolean isOrthofotoMitKatasterdarstellung = type.equals(
                                    "Orthofoto mit Katasterdarstellung");

                            final BillingProductGroupAmount[] groupAmounts;
                            if (isGdbNrwAmtlich && isLiegenschaftsKarte && isDinA4) {
                                product = "fknw4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isLiegenschaftsKarte && isDinA3) {
                                product = "fknw3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isLiegenschaftsKarte && isDinA2) {
                                product = "fknw2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isLiegenschaftsKarte && isDinA1) {
                                product = "fknw1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isLiegenschaftsKarte && isDinA0) {
                                product = "fknw0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarteMKO && isDinA4) {
                                product = "skmekom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarteMKO && isDinA3) {
                                product = "skmekom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarteMKO && isDinA2) {
                                product = "skmekom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarteMKO && isDinA1) {
                                product = "skmekom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarteMKO && isDinA0) {
                                product = "skmekom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isSchaetzungskarte && isDinA4) {
                                product = "schknw4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isSchaetzungskarte && isDinA3) {
                                product = "schknw3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isSchaetzungskarte && isDinA2) {
                                product = "schknw2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isSchaetzungskarte && isDinA1) {
                                product = "schknw1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isSchaetzungskarte && isDinA0) {
                                product = "schknw0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isAmtlicheBasiskarte && isDinA4) {
                                product = "abknw4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isAmtlicheBasiskarte && isDinA3) {
                                product = "abknw3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isAmtlicheBasiskarte && isDinA2) {
                                product = "abknw2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isAmtlicheBasiskarte && isDinA1) {
                                product = "abknw1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isGdbNrwAmtlich && isAmtlicheBasiskarte && isDinA0) {
                                product = "abknw0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarte && isDinA4) {
                                product = "skkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarte && isDinA3) {
                                product = "skkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarte && isDinA2) {
                                product = "skkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarte && isDinA1) {
                                product = "skkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isNrwKommunal && isStadtgrundkarte && isDinA0) {
                                product = "skkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgk && isDinA4) {
                                product = "dgkkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isDgk && isDinA3) {
                                product = "dgkkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isDgk && isDinA2) {
                                product = "dgkkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgk && isDinA1) {
                                product = "dgkkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgk && isDinA0) {
                                product = "dgkkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofoto && isDinA4) {
                                product = "ofkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isOrthofoto && isDinA3) {
                                product = "ofkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isOrthofoto && isDinA2) {
                                product = "ofkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofoto && isDinA1) {
                                product = "ofkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofoto && isDinA0) {
                                product = "ofkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isNivPUebersicht && isDinA4) {
                                product = "nivpükom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isNivPUebersicht && isDinA3) {
                                product = "nivpükom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isNivPUebersicht && isDinA2) {
                                product = "nivpükom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isNivPUebersicht && isDinA1) {
                                product = "nivpükom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isNivPUebersicht && isDinA0) {
                                product = "nivpükom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isApUebersicht && isDinA4) {
                                product = "apükom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isApUebersicht && isDinA3) {
                                product = "apükom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isApUebersicht && isDinA2) {
                                product = "apükom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isApUebersicht && isDinA1) {
                                product = "apükom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isApUebersicht && isDinA0) {
                                product = "apükom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isPunktnummerierungsuebersicht && isDinA4) {
                                product = "pnükom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isPunktnummerierungsuebersicht && isDinA3) {
                                product = "pnükom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isPunktnummerierungsuebersicht && isDinA2) {
                                product = "pnükom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isPunktnummerierungsuebersicht && isDinA1) {
                                product = "pnükom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isPunktnummerierungsuebersicht && isDinA0) {
                                product = "pnükom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgkMitHoehenlinien && isDinA4) {
                                product = "abkhkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isDgkMitHoehenlinien && isDinA3) {
                                product = "abkhkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isDgkMitHoehenlinien && isDinA2) {
                                product = "abkhkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgkMitHoehenlinien && isDinA1) {
                                product = "abkhkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isDgkMitHoehenlinien && isDinA0) {
                                product = "abkhkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isStadtgrundkarteMitHoehenlinien && isDinA4) {
                                product = "skhkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isStadtgrundkarteMitHoehenlinien && isDinA3) {
                                product = "skhkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isStadtgrundkarteMitHoehenlinien && isDinA2) {
                                product = "skhkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isStadtgrundkarteMitHoehenlinien && isDinA1) {
                                product = "skhkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isStadtgrundkarteMitHoehenlinien && isDinA0) {
                                product = "skhkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofotoMitKatasterdarstellung && isDinA4) {
                                product = "ofkkom4";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isOrthofotoMitKatasterdarstellung && isDinA3) {
                                product = "ofkkom3";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a3", 1)
                                    };
                            } else if (isWupKommunal && isOrthofotoMitKatasterdarstellung && isDinA2) {
                                product = "ofkkom2";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofotoMitKatasterdarstellung && isDinA1) {
                                product = "ofkkom1";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isWupKommunal && isOrthofotoMitKatasterdarstellung && isDinA0) {
                                product = "ofkkom0";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount("eakarte_a2-a0", 1)
                                    };
                            } else if (isFeldvergleichsKarte) {
                                product = "skmekomtiff";
                                groupAmounts = new BillingProductGroupAmount[] {
                                        new BillingProductGroupAmount(
                                            "eaflst_1000",
                                            NasFeeCalculator.getFlurstueckAmount(geom, getConnectionContext())),
                                        new BillingProductGroupAmount(
                                            "eageb_1000",
                                            NasFeeCalculator.getGebaeudeAmount(geom, getConnectionContext()))
                                    };
                            } else {
                                product = null;
                                groupAmounts = null;
                            }

                            if ((product != null)) {
                                if (BillingPopup.doBilling(
                                                product,
                                                MAPPER.writeValueAsString(info),
                                                requestPerUsage,
                                                (Geometry)null,
                                                getConnectionContext(),
                                                groupAmounts)) {
                                    return MAPPER.readValue(
                                            BillingPopup.getInstance().getCurrentRequest(),
                                            AlkisProductDownloadHelper.AlkisKarteDownloadInfo.class);
                                }
                            } else {
                                LOG.info("no product or productgroup is matching");
                                return info;
                            }
                        } catch (final Exception e) {
                            LOG.error("Error when trying to produce a alkis product", e);
                            // Hier noch ein Fehlerdialog
                        }
                    } catch (final Exception e) {
                        ObjectRendererUtils.showExceptionWindowToUser(
                            "Fehler beim Aufruf des Produkts: "
                                    + selectedProduct,
                            e,
                            AlkisPrintingSettingsWidget.this);
                        LOG.error(e);
                    }

                    return null;
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
            AlkisProductDownloadHelper.downloadKarteCustomProduct(
                creator,
                DownloadManagerDialog.getInstance().getJobName(),
                cidsObjectListModel.size()
                        > 1,
                getConnectionContext());
            dispose();
            mapPrintListener.cleanUpAndRestoreFeatures();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int toInt(final double input) {
        return Double.valueOf(input).intValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Geometry unionAllObjectsGeometries() {
        Geometry allGeomUnion = null;
        for (int i = cidsObjectListModel.size(); --i >= 0;) {
            final Object currentObj = cidsObjectListModel.get(i);
            if (currentObj instanceof CidsBean) {
                final CidsBean currentObjectBean = (CidsBean)currentObj;
                final CidsFeature cf = new CidsFeature(currentObjectBean.getMetaObject());
                final Geometry currentGeom = cf.getGeometry();
                if (currentGeom != null) {
                    if (allGeomUnion == null) {
                        allGeomUnion = currentGeom;
                    } else {
                        allGeomUnion = currentGeom.union(allGeomUnion);
                    }
                }
            }
        }
//        StyledFeature debug = new DefaultStyledFeature();
//        debug.setGeometry(allGeomUnion.getEnvelope());
//        debug.setFillingPaint(Color.RED);
//        mappingComponent.getFeatureCollection().addFeature(debug);
        return allGeomUnion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   box     DOCUMENT ME!
     * @param   width   DOCUMENT ME!
     * @param   height  DOCUMENT ME!
     * @param   scale   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean doesBoundingBoxFitIntoLayout(final BoundingBox box,
            final int width,
            final int height,
            final double scale) {
        final double realWorldLayoutWidth = ((double)width)
                    / 1000.0d
                    * scale;
        final double realWorldLayoutHeigth = ((double)height)
                    / 1000.0d
                    * scale;
        return (realWorldLayoutWidth >= box.getWidth())
                    && (realWorldLayoutHeigth >= box.getHeight());
    }

    /**
     * DOCUMENT ME!
     */
    private void syncOkButtonWithListStatus() {
        cmdOk.setEnabled((mapPrintListener != null) && mapPrintListener.isFeatureInCollection());
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
    static final class LayoutMetaInfo {

        //~ Instance fields ----------------------------------------------------

        String layoutDesc;
        String layoutCode;
        int width;
        int heigth;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LayoutMetaInfo object.
         *
         * @param  layoutDesc  DOCUMENT ME!
         * @param  width       DOCUMENT ME!
         * @param  heigth      DOCUMENT ME!
         */
        public LayoutMetaInfo(final String layoutDesc, final int width, final int heigth) {
            this.layoutDesc = layoutDesc;
            this.width = width;
            this.heigth = heigth;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public String toString() {
            return layoutDesc;
        }
    }
}
