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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.vividsolutions.jts.geom.Point;
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;


import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumSchadenPanel extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    private static final Comparator<Object> ID_COMPARATOR = new Comparator<Object>() {

            @Override
           /* public int compare(final Object o1, final Object o2) {
                return AlphanumComparator.getInstance().compare(String.valueOf(o1), String.valueOf(o2));
            }*/
            public int compare(final Object o1, final Object o2) {
                    final String o1String = String.valueOf(((CidsBean)o1).getProperty("id"));
                    final String o2String = String.valueOf(((CidsBean)o2).getProperty("id"));

                    try {
                        final Integer o1Int = Integer.parseInt(o1String);
                        final Integer o2Int = Integer.parseInt(o2String);

                        return o1Int.compareTo(o2Int);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }

                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
        };
    private List<CidsBean> ersatzBeans = new ArrayList<>();
    private final List<CidsBean> changedErsatzBeans = new ArrayList<>();
    private final List<CidsBean> deletedErsatzBeans = new ArrayList<>();
    private List<CidsBean> festBeans = new ArrayList<>();
    private final List<CidsBean> changedFestBeans = new ArrayList<>();
    private final List<CidsBean> deletedFestBeans = new ArrayList<>();
    private final Map <String, List<CidsBean>> ersatzBeanMap = new HashMap <>();
    private final Map <String, List<CidsBean>> festBeanMap = new HashMap <>();
    private static final Logger LOG = Logger.getLogger(BaumSchadenPanel.class);
    
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static final String PREFIX_LABEL = "LABEL:";
    private static final String PREFIX_INPUT = "INPUT:";
    
    private static final MetaClass MC__ART;
    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumErsatzPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
    }
    
    
    public static final String FIELD__ALTER = "alter";                          // baum_schaden
    public static final String FIELD__HOEHE = "hoehe";                          // baum_schaden
    public static final String FIELD__UMFANG = "umfang";                        // baum_schaden
    public static final String FIELD__ART = "fk_art";                           // baum_schaden
    public static final String FIELD__GEOM = "fk_geom";                         // baum_schaden
    public static final String FIELD__PRIVAT = "privatbaum";                    // baum_schaden
    public static final String FIELD__OHNE = "ohne_schaden";                    // baum_schaden
    public static final String FIELD__KRONE = "kronenschaden";                  // baum_schaden
    public static final String FIELD__STAMM = "stammschaden";                   // baum_schaden
    public static final String FIELD__WURZEL = "wurzelschaden";                 // baum_schaden
    public static final String FIELD__KRONE_ARR = "arr_krone";                  // baum_schaden
    public static final String FIELD__STAMM_ARR = "arr_stamm";                  // baum_schaden
    public static final String FIELD__WURZEL_ARR = "arr_wurzel";                // baum_schaden
    public static final String FIELD__MASSNAHME = "arr_massnahme";              // baum_schaden
    public static final String FIELD__STURM = "sturmschaden";                   // baum_schaden
    public static final String FIELD__ABGESTORBEN = "abgestorben";              // baum_schaden
    public static final String FIELD__BAU = "baumassnahme";                     // baum_schaden
    public static final String FIELD__GUTACHTEN = "gutachten";                  // baum_schaden
    public static final String FIELD__BERATUNG = "baumberatung";                // baum_schaden
    public static final String FIELD__BEMERKUNG = "bemerkung";                  // baum_schaden
    public static final String FIELD__BETRAG = "betrag";                        // baum_schaden
    public static final String FIELD__EINGANG = "eingegangen";                  // baum_schaden
    public static final String FIELD__FAELLUNG = "faellung";                    // baum_schaden
    public static final String FIELD__FK_SCHADEN = "fk_schaden";                // baum_ersatz
    public static final String FIELD__ID = "id";                                // baum_schaden
    
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_schaden_geom
    
    public static final String TABLE_GEOM = "geom";
    
    public static final String BUNDLE_W_QUESTION = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().question";
    public static final String BUNDLE_W_TITLE = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().title";
    public static final String BUNDLE_W_ERRORTITLE = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().errortitle";
    public static final String BUNDLE_W_ERRORTEXT = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().errortext";
    public static final String BUNDLE_S_QUESTION = "BaumSchadenPanel.btnRemoveStammActionPerformed().question";
    public static final String BUNDLE_S_TITLE = "BaumSchadenPanel.btnRemoveStammActionPerformed().title";
    public static final String BUNDLE_S_ERRORTITLE = "BaumSchadenPanel.btnRemoveStammActionPerformed().errortitle";
    public static final String BUNDLE_S_ERRORTEXT = "BaumSchadenPanel.btnRemoveStammActionPerformed().errortext";
    public static final String BUNDLE_K_QUESTION = "BaumSchadenPanel.btnRemoveKroneActionPerformed().question";
    public static final String BUNDLE_K_TITLE = "BaumSchadenPanel.btnRemoveKroneActionPerformed().title";
    public static final String BUNDLE_K_ERRORTITLE = "BaumSchadenPanel.btnRemoveKroneActionPerformed().errortitle";
    public static final String BUNDLE_K_ERRORTEXT = "BaumSchadenPanel.btnRemoveKroneActionPerformed().errortext";
    public static final String BUNDLE_M_QUESTION = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().question";
    public static final String BUNDLE_M_TITLE = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().title";
    public static final String BUNDLE_M_ERRORTITLE = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().errortitle";
    public static final String BUNDLE_M_ERRORTEXT = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().errortext";
    public static final String BUNDLE_NOAGE = "BaumSchadenPanel.prepareForSave().noAlter";
    public static final String BUNDLE_WRONGAGE = "BaumSchadenPanel.prepareForSave().wrongAlter";
    
    public static final String BUNDLE_PANE_PREFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumSchadenPanel.prepareForSave().JOptionPane.title";
    
    private static enum whatToDoForTree {

        //~ Enum constants -----------------------------------------------------

        ersatz, fest
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panSchaden = new JPanel();
        lblAlter = new JLabel();
        txtAlter = new JTextField();
        lblPrivat = new JLabel();
        chPrivat = new JCheckBox();
        lblHoehe = new JLabel();
        lblUmfang = new JLabel();
        lblArt = new JLabel();
        if(isEditor){
            cbArt = new DefaultBindableScrollableComboBox(MC__ART);
        }
        lblGeom = new JLabel();
        if (isEditor){
            cbGeomSchaden = new DefaultCismapGeometryComboBoxEditor();
        }
        panGeometrie = new JPanel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        lblOhne = new JLabel();
        chOhne = new JCheckBox();
        lblBau = new JLabel();
        chBau = new JCheckBox();
        lblAbgest = new JLabel();
        chAbgest = new JCheckBox();
        lblSturm = new JLabel();
        chSturm = new JCheckBox();
        lblBeratung = new JLabel();
        chBeratung = new JCheckBox();
        lblGutachten = new JLabel();
        chGutachten = new JCheckBox();
        lblKroneArr = new JLabel();
        chKrone = new JCheckBox();
        filler21 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpKrone = new DefaultBindableLabelsPanel(isEditor, "Kronenschaden:", SORTING_OPTION);
        lblStammArr = new JLabel();
        chStamm = new JCheckBox();
        filler22 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpStamm = new DefaultBindableLabelsPanel(isEditor, "Stammschaden:", SORTING_OPTION);
        lblWurzelArr = new JLabel();
        chWurzel = new JCheckBox();
        filler23 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpWurzel = new DefaultBindableLabelsPanel(isEditor, "Wurzelschaden:", SORTING_OPTION);
        lblMassnahmeArr = new JLabel();
        filler24 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpMassnahme = new DefaultBindableLabelsPanel(isEditor, "Maßnahmen:", SORTING_OPTION);
        filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblFaellung = new JLabel();
        chFaellung = new JCheckBox();
        spHoehe = new JSpinner();
        spUmfang = new JSpinner();
        filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelErsatzpflanzung = new JPanel();
        panErsatz = new JPanel();
        panErsatzMain = new JPanel();
        baumErsatzPanel = baumErsatzPanel = new BaumErsatzPanel(this, true, this.getConnectionContext());
        scpLaufendeErsatz = new JScrollPane();
        lstErsatz = new JList();
        panControlsNewErsatz = new JPanel();
        btnAddNewErsatz = new JButton();
        btnRemoveErsatz = new JButton();
        filler8 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelErsatzzahlung = new JPanel();
        panZahlung = new JPanel();
        lblBetrag = new JLabel();
        txtBetrag = new JTextField();
        lblEingang = new JLabel();
        chEingang = new JCheckBox();
        filler9 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelFestsetzung = new JPanel();
        panFest = new JPanel();
        panFestMain = new JPanel();
        baumFestsetzungPanel = baumFestsetzungPanel = new BaumFestsetzungPanel(this, true, this.getConnectionContext());
        scpLaufendeFest = new JScrollPane();
        lstFest = new JList();
        panControlsNewFest = new JPanel();
        btnAddNewFest = new JButton();
        btnRemoveFest = new JButton();

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        pnlCard1.setName("pnlCard1"); // NOI18N
        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jTabbedPane.setMinimumSize(new Dimension(849, 520));
        jTabbedPane.setName("jTabbedPane"); // NOI18N

        jPanelAllgemein.setName("jPanelAllgemein"); // NOI18N
        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panSchaden.setName("panSchaden"); // NOI18N
        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        lblAlter.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAlter, "Alter:");
        lblAlter.setName("lblAlter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAlter, gridBagConstraints);

        txtAlter.setMinimumSize(new Dimension(40, 19));
        txtAlter.setName("txtAlter"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.alter}"), txtAlter, BeanProperty.create("text"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(txtAlter, gridBagConstraints);

        lblPrivat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblPrivat, "privat:");
        lblPrivat.setName("lblPrivat"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblPrivat, gridBagConstraints);

        chPrivat.setContentAreaFilled(false);
        chPrivat.setName("chPrivat"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.privatbaum}"), chPrivat, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chPrivat, gridBagConstraints);

        lblHoehe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHoehe, "Höhe [m]:");
        lblHoehe.setName("lblHoehe"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblHoehe, gridBagConstraints);

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, "Umfang [cm]:");
        lblUmfang.setName("lblUmfang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblUmfang, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblArt, gridBagConstraints);

        if(isEditor){
            cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbArt.setName("cbArt"); // NOI18N
            cbArt.setPreferredSize(new Dimension(100, 24));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_art}"), cbArt, BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            bindingGroup.addBinding(binding);

        }
        if(isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbArt, gridBagConstraints);
        }

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
        lblGeom.setName("lblGeom"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGeom, gridBagConstraints);

        if (isEditor){
            cbGeomSchaden.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomSchaden.setName("cbGeomSchaden"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeomSchaden, BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbGeomSchaden, gridBagConstraints);
        }

        panGeometrie.setName("panGeometrie"); // NOI18N
        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setName("panLage"); // NOI18N
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        panPreviewMap.setName("panPreviewMap"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setName("semiRoundedPanel7"); // NOI18N
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        Mnemonics.setLocalizedText(lblKarte, NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblKarte.text")); // NOI18N
        lblKarte.setName("lblKarte"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(lblKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 2);
        panLage.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(panLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 3);
        panSchaden.add(panGeometrie, gridBagConstraints);

        lblOhne.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOhne, "ohne:");
        lblOhne.setName("lblOhne"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblOhne, gridBagConstraints);

        chOhne.setContentAreaFilled(false);
        chOhne.setName("chOhne"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ohne_schaden}"), chOhne, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chOhne, gridBagConstraints);

        lblBau.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBau, "Bau:");
        lblBau.setName("lblBau"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBau, gridBagConstraints);

        chBau.setContentAreaFilled(false);
        chBau.setName("chBau"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumassnahme}"), chBau, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBau, gridBagConstraints);

        lblAbgest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgest, "abgestorben:");
        lblAbgest.setName("lblAbgest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgest, gridBagConstraints);

        chAbgest.setContentAreaFilled(false);
        chAbgest.setName("chAbgest"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.abgestorben}"), chAbgest, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgest, gridBagConstraints);

        lblSturm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSturm, "Sturm:");
        lblSturm.setName("lblSturm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblSturm, gridBagConstraints);

        chSturm.setContentAreaFilled(false);
        chSturm.setName("chSturm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.sturmschaden}"), chSturm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chSturm, gridBagConstraints);

        lblBeratung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBeratung, "Beratung:");
        lblBeratung.setName("lblBeratung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBeratung, gridBagConstraints);

        chBeratung.setContentAreaFilled(false);
        chBeratung.setName("chBeratung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumberatung}"), chBeratung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBeratung, gridBagConstraints);

        lblGutachten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGutachten, "Gutachten vorh.:");
        lblGutachten.setName("lblGutachten"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGutachten, gridBagConstraints);

        chGutachten.setContentAreaFilled(false);
        chGutachten.setName("chGutachten"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.gutachten}"), chGutachten, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGutachten, gridBagConstraints);

        lblKroneArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKroneArr, "Kronenschaden:");
        lblKroneArr.setName("lblKroneArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKroneArr, gridBagConstraints);

        chKrone.setContentAreaFilled(false);
        chKrone.setName("chKrone"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.kronenschaden}"), chKrone, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKrone, gridBagConstraints);

        filler21.setName("filler21"); // NOI18N
        panSchaden.add(filler21, new GridBagConstraints());

        blpKrone.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpKrone.manageableButtonText")); // NOI18N
        blpKrone.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_krone}"), blpKrone, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpKrone, gridBagConstraints);

        lblStammArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStammArr, "Stammschaden:");
        lblStammArr.setName("lblStammArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblStammArr, gridBagConstraints);

        chStamm.setContentAreaFilled(false);
        chStamm.setName("chStamm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stammschaden}"), chStamm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chStamm, gridBagConstraints);

        filler22.setName("filler22"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panSchaden.add(filler22, gridBagConstraints);

        blpStamm.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpStamm.manageableButtonText")); // NOI18N
        blpStamm.setName("blpStamm"); // NOI18N
        blpStamm.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_stamm}"), blpStamm, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpStamm, gridBagConstraints);

        lblWurzelArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzelArr, "Wurzelschaden:");
        lblWurzelArr.setName("lblWurzelArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblWurzelArr, gridBagConstraints);

        chWurzel.setContentAreaFilled(false);
        chWurzel.setName("chWurzel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.wurzelschaden}"), chWurzel, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chWurzel, gridBagConstraints);

        filler23.setName("filler23"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        panSchaden.add(filler23, gridBagConstraints);

        blpWurzel.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpWurzel.manageableButtonText")); // NOI18N
        blpWurzel.setName("blpWurzel"); // NOI18N
        blpWurzel.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_wurzel}"), blpWurzel, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpWurzel, gridBagConstraints);

        lblMassnahmeArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblMassnahmeArr, "Maßnahme:");
        lblMassnahmeArr.setName("lblMassnahmeArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblMassnahmeArr, gridBagConstraints);

        filler24.setName("filler24"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        panSchaden.add(filler24, gridBagConstraints);

        blpMassnahme.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpMassnahme.manageableButtonText")); // NOI18N
        blpMassnahme.setName("blpMassnahme"); // NOI18N
        blpMassnahme.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_massnahme}"), blpMassnahme, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpMassnahme, gridBagConstraints);

        filler6.setName("filler6"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panSchaden.add(filler6, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 2);
        panSchaden.add(scpBemerkung, gridBagConstraints);

        lblFaellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFaellung, "Fällung:");
        lblFaellung.setName("lblFaellung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblFaellung, gridBagConstraints);

        chFaellung.setContentAreaFilled(false);
        chFaellung.setName("chFaellung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.faellung}"), chFaellung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chFaellung, gridBagConstraints);

        spHoehe.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spHoehe.setModel(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
        spHoehe.setName("spHoehe"); // NOI18N
        spHoehe.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hoehe}"), spHoehe, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spHoehe, gridBagConstraints);

        spUmfang.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spUmfang.setModel(new SpinnerNumberModel(0.0d, 0.0d, 1000.0d, 1.0d));
        spUmfang.setName("spUmfang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.umfang}"), spUmfang, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spUmfang, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelAllgemein.add(panSchaden, gridBagConstraints);

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelAllgemein.add(filler7, gridBagConstraints);

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelErsatzpflanzung.setName("jPanelErsatzpflanzung"); // NOI18N
        jPanelErsatzpflanzung.setOpaque(false);
        jPanelErsatzpflanzung.setLayout(new GridBagLayout());

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        panErsatzMain.setName("panErsatzMain"); // NOI18N
        panErsatzMain.setOpaque(false);
        panErsatzMain.setLayout(new GridBagLayout());

        baumErsatzPanel.setName("baumErsatzPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstErsatz, ELProperty.create("${selectedElement}"), baumErsatzPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        panErsatzMain.add(baumErsatzPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        panErsatz.add(panErsatzMain, gridBagConstraints);

        scpLaufendeErsatz.setName("scpLaufendeErsatz"); // NOI18N

        lstErsatz.setModel(new DefaultListModel());
        lstErsatz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstErsatz.setFixedCellWidth(75);
        lstErsatz.setName("lstErsatz"); // NOI18N
        scpLaufendeErsatz.setViewportView(lstErsatz);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panErsatz.add(scpLaufendeErsatz, gridBagConstraints);

        panControlsNewErsatz.setName("panControlsNewErsatz"); // NOI18N
        panControlsNewErsatz.setOpaque(false);
        panControlsNewErsatz.setLayout(new GridBagLayout());

        btnAddNewErsatz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewErsatz.setMaximumSize(new Dimension(39, 20));
        btnAddNewErsatz.setMinimumSize(new Dimension(39, 20));
        btnAddNewErsatz.setName("btnAddNewErsatz"); // NOI18N
        btnAddNewErsatz.setPreferredSize(new Dimension(39, 25));
        btnAddNewErsatz.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewErsatz.add(btnAddNewErsatz, gridBagConstraints);

        btnRemoveErsatz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveErsatz.setMaximumSize(new Dimension(39, 20));
        btnRemoveErsatz.setMinimumSize(new Dimension(39, 20));
        btnRemoveErsatz.setName("btnRemoveErsatz"); // NOI18N
        btnRemoveErsatz.setPreferredSize(new Dimension(39, 25));
        btnRemoveErsatz.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewErsatz.add(btnRemoveErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panErsatz.add(panControlsNewErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelErsatzpflanzung.add(panErsatz, gridBagConstraints);

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzpflanzung.add(filler8, gridBagConstraints);

        jTabbedPane.addTab("Ersatzpflanzung", jPanelErsatzpflanzung);

        jPanelErsatzzahlung.setName("jPanelErsatzzahlung"); // NOI18N
        jPanelErsatzzahlung.setOpaque(false);
        jPanelErsatzzahlung.setLayout(new GridBagLayout());

        panZahlung.setName("panZahlung"); // NOI18N
        panZahlung.setOpaque(false);
        panZahlung.setLayout(new GridBagLayout());

        lblBetrag.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBetrag, "Betrag:");
        lblBetrag.setName("lblBetrag"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panZahlung.add(lblBetrag, gridBagConstraints);

        txtBetrag.setName("txtBetrag"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.betrag}"), txtBetrag, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZahlung.add(txtBetrag, gridBagConstraints);

        lblEingang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblEingang, "eingegangen:");
        lblEingang.setName("lblEingang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panZahlung.add(lblEingang, gridBagConstraints);

        chEingang.setContentAreaFilled(false);
        chEingang.setName("chEingang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.eingegangen}"), chEingang, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZahlung.add(chEingang, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelErsatzzahlung.add(panZahlung, gridBagConstraints);

        filler9.setName("filler9"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzzahlung.add(filler9, gridBagConstraints);

        jTabbedPane.addTab("Ersatzzahlung", jPanelErsatzzahlung);

        jPanelFestsetzung.setName("jPanelFestsetzung"); // NOI18N
        jPanelFestsetzung.setOpaque(false);
        jPanelFestsetzung.setLayout(new GridBagLayout());

        panFest.setName("panFest"); // NOI18N
        panFest.setOpaque(false);
        panFest.setLayout(new GridBagLayout());

        panFestMain.setName("panFestMain"); // NOI18N
        panFestMain.setOpaque(false);
        panFestMain.setLayout(new GridBagLayout());

        baumFestsetzungPanel.setName("baumFestsetzungPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstFest, ELProperty.create("${selectedElement}"), baumFestsetzungPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFestMain.add(baumFestsetzungPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 10, 0);
        panFest.add(panFestMain, gridBagConstraints);

        scpLaufendeFest.setName("scpLaufendeFest"); // NOI18N

        lstFest.setModel(new DefaultListModel());
        lstFest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFest.setFixedCellWidth(75);
        lstFest.setName("lstFest"); // NOI18N
        scpLaufendeFest.setViewportView(lstFest);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFest.add(scpLaufendeFest, gridBagConstraints);

        panControlsNewFest.setName("panControlsNewFest"); // NOI18N
        panControlsNewFest.setOpaque(false);
        panControlsNewFest.setLayout(new GridBagLayout());

        btnAddNewFest.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewFest.setMaximumSize(new Dimension(39, 20));
        btnAddNewFest.setMinimumSize(new Dimension(39, 20));
        btnAddNewFest.setName("btnAddNewFest"); // NOI18N
        btnAddNewFest.setPreferredSize(new Dimension(39, 25));
        btnAddNewFest.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewFest.add(btnAddNewFest, gridBagConstraints);

        btnRemoveFest.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveFest.setMaximumSize(new Dimension(39, 20));
        btnRemoveFest.setMinimumSize(new Dimension(39, 20));
        btnRemoveFest.setName("btnRemoveFest"); // NOI18N
        btnRemoveFest.setPreferredSize(new Dimension(39, 25));
        btnRemoveFest.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewFest.add(btnRemoveFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panFest.add(panControlsNewFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelFestsetzung.add(panFest, gridBagConstraints);

        jTabbedPane.addTab("Festsetzung", jPanelFestsetzung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlCard1, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddNewErsatz) {
                BaumSchadenPanel.this.btnAddNewErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveErsatz) {
                BaumSchadenPanel.this.btnRemoveErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewFest) {
                BaumSchadenPanel.this.btnAddNewFestActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveFest) {
                BaumSchadenPanel.this.btnRemoveFestActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddNewErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewErsatzActionPerformed
        try{
        //ersatzBean erzeugen und vorbelegen:
            final CidsBean beanErsatz = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_ERSATZ",
                getConnectionContext());
            CidsBean beanSchaden = cidsBean;
            beanSchaden.getMetaObject().setStatus(MetaObject.MODIFIED);
            beanErsatz.setProperty(FIELD__FK_SCHADEN, beanSchaden);

            
            //Ersatzpflanzungen erweitern:
            if (ersatzBeans != null){
                ersatzBeans.add(beanErsatz);
            } else{
                List<CidsBean> tempList = new ArrayList<>();
                tempList.add(beanErsatz);
                ersatzBeanMap.replace(cidsBean.getProperty(FIELD__ID).toString(), tempList);
                ersatzBeans = tempList;
            }
            ((DefaultListModel)lstErsatz.getModel()).addElement(beanErsatz);
            changedErsatzBeans.add(beanErsatz);

            //Refresh:
            lstErsatz.setSelectedValue(beanErsatz, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);

        } catch (Exception e) {
            LOG.error("Cannot add new BaumErsatz object", e);
        }
    }//GEN-LAST:event_btnAddNewErsatzActionPerformed

    private void btnRemoveErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveErsatzActionPerformed
        final Object selectedObject = lstErsatz.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            String schadenValue = this.cidsBean.getProperty(FIELD__ID).toString();
            //String ersatzValue = ((CidsBean) selectedObject).getProperty(FIELD__ID).toString();
            //Integer indexErsatz = (ersatzBeanMap.get(schadenValue)).indexOf(selectedObject);
            Boolean deleteSuccess = (ersatzBeanMap.get(schadenValue)).remove((CidsBean)selectedObject);
            if (ersatzBeans != null) {
                ersatzBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstErsatz.getModel()).removeElement(selectedObject);
                deletedErsatzBeans.add((CidsBean)selectedObject);
                parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
                if (ersatzBeans != null && ersatzBeans.size() > 0) {
                    lstErsatz.setSelectedIndex(0);
                }else{
                    lstErsatz.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveErsatzActionPerformed

    private void btnAddNewFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewFestActionPerformed
        try{
        //festBean erzeugen und vorbelegen:
            final CidsBean beanFest = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_FESTSETZUNG",
                getConnectionContext());
            beanFest.setProperty(FIELD__FK_SCHADEN, cidsBean);

            //Festsetzungen erweitern:
            if (festBeans != null){
                festBeans.add(beanFest);
            } else{
                List<CidsBean> tempList = new ArrayList<>();
                tempList.add(beanFest);
                festBeanMap.replace(cidsBean.getProperty(FIELD__ID).toString(), tempList);
                festBeans = tempList;
            }
            
            ((DefaultListModel)lstFest.getModel()).addElement(beanFest);
            changedFestBeans.add(beanFest);
            
            //Refresh:
            lstFest.setSelectedValue(beanFest, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);

        } catch (Exception e) {
            LOG.error("Cannot add new BaumFest object", e);
        }
    }//GEN-LAST:event_btnAddNewFestActionPerformed

    private void btnRemoveFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveFestActionPerformed
        final Object selectedObject = lstFest.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            String schadenValue = this.cidsBean.getProperty(FIELD__ID).toString();
            Boolean deleteSuccess = (festBeanMap.get(schadenValue)).remove((CidsBean)selectedObject);
            if (festBeans != null) {
                festBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstFest.getModel()).removeElement(selectedObject);
                deletedFestBeans.add((CidsBean)selectedObject);
                parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
                if (festBeans != null && festBeans.size() > 0) {
                    lstFest.setSelectedIndex(0);
                }else{
                    lstFest.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveFestActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    public final BaumMeldungPanel parentPanel;
    public final BaumSchadenEditor parentEditor;
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (!(Objects.equals(evt.getOldValue(), evt.getNewValue()))){
                    if ((parentPanel != null) && (parentPanel.parentEditor != null) && (parentPanel.getCidsBean() != null)) {
                        parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
                        parentPanel.setChangedSchadenBeans(cidsBean);
                    }
                    if ((parentEditor != null) && (parentEditor.getCidsBean() != null)) {
                        parentEditor.getCidsBean().setArtificialChangeFlag(true);
                    }
                    if(FIELD__GEOM.equals(evt.getPropertyName())){
                        setMapWindow();
                    }
                }
            }
        };
    private SwingWorker worker_ersatz;
    private SwingWorker worker_fest;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumErsatzPanel baumErsatzPanel;
    BaumFestsetzungPanel baumFestsetzungPanel;
    DefaultBindableLabelsPanel blpKrone;
    DefaultBindableLabelsPanel blpMassnahme;
    DefaultBindableLabelsPanel blpStamm;
    DefaultBindableLabelsPanel blpWurzel;
    JButton btnAddNewErsatz;
    JButton btnAddNewFest;
    JButton btnRemoveErsatz;
    JButton btnRemoveFest;
    JComboBox<String> cbArt;
    public JComboBox cbGeomSchaden;
    JCheckBox chAbgest;
    JCheckBox chBau;
    JCheckBox chBeratung;
    JCheckBox chEingang;
    JCheckBox chFaellung;
    JCheckBox chGutachten;
    JCheckBox chKrone;
    JCheckBox chOhne;
    JCheckBox chPrivat;
    JCheckBox chStamm;
    JCheckBox chSturm;
    JCheckBox chWurzel;
    Box.Filler filler21;
    Box.Filler filler22;
    Box.Filler filler23;
    Box.Filler filler24;
    Box.Filler filler6;
    Box.Filler filler7;
    Box.Filler filler8;
    Box.Filler filler9;
    JPanel jPanelAllgemein;
    JPanel jPanelErsatzpflanzung;
    JPanel jPanelErsatzzahlung;
    JPanel jPanelFestsetzung;
    JTabbedPane jTabbedPane;
    JLabel lblAbgest;
    JLabel lblAlter;
    JLabel lblArt;
    JLabel lblBau;
    JLabel lblBemerkung;
    JLabel lblBeratung;
    JLabel lblBetrag;
    JLabel lblEingang;
    JLabel lblFaellung;
    JLabel lblGeom;
    JLabel lblGutachten;
    JLabel lblHoehe;
    JLabel lblKarte;
    JLabel lblKroneArr;
    JLabel lblMassnahmeArr;
    JLabel lblOhne;
    JLabel lblPrivat;
    JLabel lblStammArr;
    JLabel lblSturm;
    JLabel lblUmfang;
    JLabel lblWurzelArr;
    JList lstErsatz;
    JList lstFest;
    JPanel panControlsNewErsatz;
    JPanel panControlsNewFest;
    JPanel panErsatz;
    JPanel panErsatzMain;
    JPanel panFest;
    JPanel panFestMain;
    JPanel panGeometrie;
    JPanel panLage;
    DefaultPreviewMapPanel panPreviewMap;
    JPanel panSchaden;
    JPanel panZahlung;
    JPanel pnlCard1;
    RoundedPanel rpKarte;
    JScrollPane scpBemerkung;
    JScrollPane scpLaufendeErsatz;
    JScrollPane scpLaufendeFest;
    SemiRoundedPanel semiRoundedPanel7;
    JSpinner spHoehe;
    JSpinner spUmfang;
    JTextArea taBemerkung;
    JTextField txtAlter;
    JTextField txtBetrag;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumSchadenPanel object.
     */
    public BaumSchadenPanel() {
        this(null, null, false, ConnectionContext.createDeprecated());
    }

    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param parentEditor
     * @param  editable  DOCUMENT ME!
     */
  /*  public BaumSchadenPanel(final BaumMeldungPanel parentPanel, BaumSchadenEditor parentEditor, final boolean editable) {
        /*this.isEditor = editable;
        initComponents();
        this.connectionContext = ConnectionContext.createDeprecated();
        this.parentPanel = parentPanel;
        this.parentEditor = parentEditor;
       
        
        this(parentPanel, parentEditor, editable, null);
    }*/
 
    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param parentEditor
     * @param  editable             DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public BaumSchadenPanel(final BaumMeldungPanel parentPanel, 
                            BaumSchadenEditor parentEditor, 
                            final boolean editable,
                            final ConnectionContext connectionContext) {
        this.isEditor = editable;
        if (connectionContext != null){
            this.connectionContext = connectionContext;
        } else{
            this.connectionContext = ConnectionContext.createDeprecated();
        }
        initComponents();
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setLocalRenderFeatureString(FIELD__GEOM);
        }
        for (final DefaultBindableLabelsPanel labelsPanel : Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme)) {
            labelsPanel.initWithConnectionContext(connectionContext);
        }
        this.parentPanel = parentPanel;
        this.parentEditor = parentEditor;
    }
    
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        
        try {
            this.cidsBean.setProperty(FIELD__HOEHE, new DecimalFormat("00.0").format(spHoehe.getValue())); 
        } catch (final Exception ex) {
            LOG.warn("Height not formatted.", ex);
            save = false;
        }
        /*
        // Ampere muss angegeben werden
        try {
            if (ftxtAmpere.getText().trim().isEmpty()) {
                LOG.warn("No ampere specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOAMPERE));
            } else {
                try {
                    if (Integer.parseInt(ftxtAmpere.getText()) <= 0) {
                        errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGAMPERE));
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong ampere specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGAMPERE));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Ampere not given.", ex);
            save = false;
        }*/
        boolean errorOccured = false;
        List<CidsBean> toSaveListErsatz; 
                try {
                      for (final String idValue : ersatzBeanMap.keySet()){
                          List<CidsBean> listErsatz = ersatzBeanMap.get(idValue);
                          if (listErsatz != null){
                            for (final CidsBean beanErsatz : listErsatz){
                               /* if (beanErsatz.getMetaObject().getStatus() == MetaObject.TO_DELETE){
                                    beanErsatz.delete();
                                }*/
                                beanErsatz.persist(getConnectionContext());
                            }
                          }
                      }
                 } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }     
         /*if (ersatzBeans != null){
            for (final CidsBean ersatzBean : changedErsatzBeans) {
                try {
                    ersatzBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }*/
       if (deletedErsatzBeans != null){
            for (final CidsBean ersatzBean : deletedErsatzBeans) {
                try {
                    if (ersatzBean.getMetaObject().getStatus() != MetaObject.NEW){
                        ersatzBean.delete();
                        ersatzBean.persist(getConnectionContext());
                    }
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        /*
        if (festBeans != null){
            for (final CidsBean festBean : changedFestBeans) {
                try {
                    festBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }*/
        List<CidsBean> toSaveListFest; 
        try {
              for (final String idValue : festBeanMap.keySet()){
                  List<CidsBean> listFest = festBeanMap.get(idValue);
                  if (listFest != null){
                    for (final CidsBean beanFest : listFest){
                        beanFest.persist(getConnectionContext());
                    }
                  }
              }
         } catch (final Exception ex) {
            errorOccured = true;
            LOG.error(ex, ex);
        }
        if (deletedFestBeans != null){
            for (final CidsBean festBean : deletedFestBeans) {
                try {
                    if (festBean.getMetaObject().getStatus() != MetaObject.NEW){
                        festBean.delete();
                        festBean.persist(getConnectionContext());
                    }
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        // Anzahl muss angegeben werden
        /*try {
            if (txtAlter.getText().trim().isEmpty()) {
                LOG.warn("No count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOAGE));
            } else {
                try {
                    Integer.parseInt(txtAlter.getText());
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong count specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGAGE));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }*/

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }
    
    //~ Methods ----------------------------------------------------------------
   /* public void initWithConnectionContext(final ConnectionContext connectionContext) {
        
        //super.initWithConnectionContext(connectionContext);
        //initComponents();

        
        
        
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOM);
        }
        //setReadOnly();

   }*/
    
    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

     
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).dispose();
        }
        deletedErsatzBeans.clear();
        deletedFestBeans.clear();
        changedErsatzBeans.clear();
        changedFestBeans.clear();
        festBeans.clear();
        ersatzBeans.clear();
        baumFestsetzungPanel.dispose();
        baumErsatzPanel.dispose();
        ((DefaultListModel)lstFest.getModel()).clear();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        if (!(Objects.equals(this.cidsBean, cidsBean))){
            if (isEditor && (this.cidsBean != null)) {
                this.cidsBean.removePropertyChangeListener(changeListener);
            }

            labelsPanels.clear();
            blpKrone.clear();
            blpStamm.clear();
            blpWurzel.clear();
            blpMassnahme.clear();
            bindingGroup.unbind();
            this.cidsBean = cidsBean;
            if (this.cidsBean != null){
                final String WHERE = " where "
                        + cidsBean.getProperty(FIELD__ID).toString()
                        + " = "
                        + FIELD__FK_SCHADEN;
                //Aus Speicher holen bzw. dem Speicher hinzufuegen
                if (ersatzBeanMap.containsKey(cidsBean.getProperty(FIELD__ID).toString())){
                    setErsatzBeans(ersatzBeanMap.get(cidsBean.getProperty(FIELD__ID).toString()));
                } else {
                    valueFromOtherTable(BaumSchadenEditor.TABLE__ERSATZ, WHERE, whatToDoForTree.ersatz, cidsBean.getProperty(FIELD__ID).toString());  
                }
                if (festBeanMap.containsKey(cidsBean.getProperty(FIELD__ID).toString())){
                    setFestBeans(festBeanMap.get(cidsBean.getProperty(FIELD__ID).toString()));
                } else {
                    valueFromOtherTable(BaumSchadenEditor.TABLE__FEST, WHERE, whatToDoForTree.fest, cidsBean.getProperty(FIELD__ID).toString());
                }
                DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                    bindingGroup,
                    this.cidsBean,
                    getConnectionContext());
            } else {
                //worker stoppen
                if (worker_ersatz != null) {
                    worker_ersatz.cancel(true);
                }
                if (worker_fest != null) {
                    worker_fest.cancel(true);
                }
                setErsatzBeans(null);
                setFestBeans(null);
            }
            setMapWindow();
            bindingGroup.bind();
            if (isEditor && (this.cidsBean != null)) {
                    cidsBean.addPropertyChangeListener(changeListener);
            }
            lstErsatz.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        Object newValue = value;

                        if (value instanceof CidsBean) {
                            final CidsBean bean = (CidsBean)value;
                            newValue = bean.getProperty(FIELD__ID);

                            if (newValue == null) {
                                newValue = "unbenannt";
                            }
                        }
                        final Component compoTeil = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                        compoTeil.setForeground(new Color(87,175,54));
                        return compoTeil;
                    }
                });
            lstFest.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        Object newValue = value;

                        if (value instanceof CidsBean) {
                            final CidsBean bean = (CidsBean)value;
                            newValue = bean.getProperty(FIELD__ID);

                            if (newValue == null) {
                                newValue = "unbenannt";
                            }
                        }
                        final Component compoTeil = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                        compoTeil.setForeground(new Color(112,48,160));
                        return compoTeil;
                    }
                });

            if (ersatzBeans != null && ersatzBeans.size() > 0) {
                lstErsatz.setSelectedIndex(0);
            }

            if (festBeans != null && festBeans.size() > 0) {
                lstFest.setSelectedIndex(0);
            }

            if(cidsBean != null){
                labelsPanels.addAll(Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme));
            }
            cbGeomSchaden.updateUI();
        }
    }
    
    private void setReadOnly(){
        
            RendererTools.makeDoubleSpinnerWithoutButtons(spHoehe, 1);
            RendererTools.makeReadOnly(spHoehe);
    }
    
    public void prepareErsatz(){
        
        if (ersatzBeans != null && ersatzBeans.size() > 0) {
            lstErsatz.setSelectedIndex(0);
        }
        lstErsatz.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus) {
                Object newValue = value;

                if (value instanceof CidsBean) {
                    final CidsBean bean = (CidsBean)value;
                    newValue = bean.getProperty(FIELD__ID);

                }
                final Component compoId= super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoId.setForeground(new Color(87,175,54));
                return compoId;
            }
        });   
    }
    public void prepareFest(){
        
        if (festBeans != null && festBeans.size() > 0) {
            lstFest.setSelectedIndex(0);
        }
        lstFest.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus) {
                Object newValue = value;

                if (value instanceof CidsBean) {
                    final CidsBean bean = (CidsBean)value;
                    newValue = bean.getProperty(FIELD__ID);

                }
                final Component compoId= super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoId.setForeground(new Color(112,48,160));
                return compoId;
            }
        });   
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setErsatzBeans(final List<CidsBean> cidsBeans) {
        try {
            baumErsatzPanel.setCidsBean(null);
            ((DefaultListModel)lstErsatz.getModel()).clear(); 

            //this.ersatzBeans.clear();
            if (cidsBeans != null){
                cidsBeans.sort(ID_COMPARATOR);
                for(final Object bean:cidsBeans){
                    ((DefaultListModel)lstErsatz.getModel()).addElement(bean);
                }
            }
            this.ersatzBeans = cidsBeans;
            prepareErsatz();
        } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("ersatz list not cleared.", ex);
        }
    }
    public void setFestBeans(final List<CidsBean> cidsBeans) {
        try {
            baumFestsetzungPanel.setCidsBean(null);
            ((DefaultListModel)lstFest.getModel()).clear();
            //this.festBeans.clear();
            if (cidsBeans != null){
                cidsBeans.sort(ID_COMPARATOR);
                for(final Object bean:cidsBeans){
                    ((DefaultListModel)lstFest.getModel()).addElement(bean);
                }
            }
            this.festBeans = cidsBeans;
            prepareFest();
        } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("fest list not cleared.", ex);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getErsatzBeans() {
        return ersatzBeans;
    }
    public List<CidsBean> getFestBeans() {
        return festBeans;
    }
    public List<CidsBean> getChangedErsatzBeans() {
        return changedErsatzBeans;
    }
    public List<CidsBean> getChangedFestBeans() {
        return changedFestBeans;
    }
    
    public void setChangedErsatzBeans(CidsBean ersatzBean){
        if(this.changedErsatzBeans != null){
            int ersatzId = (Integer)ersatzBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedErsatzBeans){
                if (ersatzId == (Integer)bean.getProperty(FIELD__ID)){
                    changedErsatzBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedErsatzBeans.add(ersatzBean);
    }
    public void setChangedFestBeans(CidsBean festBean){
        if(this.changedFestBeans != null){
            int festId = (Integer)festBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedFestBeans){
                if (festId == (Integer)bean.getProperty(FIELD__ID)){
                    changedFestBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedFestBeans.add(festBean);
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        if (cb != null){
            try {
                Double bufferMeter = 0.0;
                try{
                    bufferMeter = BaumConfProperties.getInstance().getBufferMeter();
                } catch (final Exception ex) {
                    Exceptions.printStackTrace(ex);
                    LOG.warn("Get no conf properties.", ex);
                }
                if (cb.getProperty(FIELD__GEOM) != null) {
                    panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, bufferMeter);
                    lblKarte.setText("Lage");
                } else {
                    final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                    final BoundingBox initialBoundingBox;
                    initialBoundingBox = CismapBroker.getInstance().getMappingComponent().getMappingModel()
                                .getInitialBoundingBox();
                    final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                    final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            TABLE_GEOM,
                            getConnectionContext());
                    final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                    newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                    panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, bufferMeter);
                    lblKarte.setText("Lage nicht ausgewählt");
                }
            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("Map window not set.", ex);
            } 
        } else{
            lblKarte.setText("Lage nicht ausgewählt");
        }
    }
    
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "baum_schaden",
            1,
            800,
            600);
    }
    
    private void valueFromOtherTable(final String tableName, final String whereClause, final whatToDoForTree toDo, final String idValue){
            final SwingWorker<MetaObject[], Void> worker;
            worker = new SwingWorker<MetaObject[], Void>() {
            
            @Override
            protected MetaObject[] doInBackground() throws Exception {
                return TableUtils.getOtherTableValues(tableName, whereClause, connectionContext);
            }
            
            @Override
            protected void done() {
                final MetaObject[] check;
                List<CidsBean> oneToNList = new ArrayList<>();
                try {
                    if (!isCancelled()) {
                        check = get();
                        if (check != null) {
                            for (final MetaObject metaO : check){
                                oneToNList.add(metaO.getBean());
                            }
                            switch(toDo){
                                case ersatz: {
                                    // ersatzBeanMap ergaenzen
                                    ersatzBeanMap.put(idValue, oneToNList);
                                    setErsatzBeans(oneToNList);
                                    break;
                                }
                                case fest: {
                                    // festBeanMap ergaenzen
                                    festBeanMap.put(idValue, oneToNList);
                                    setFestBeans(oneToNList);
                                    break;
                                }
                            }
                            
                        } else {
                            switch(toDo){
                                case ersatz: {
                                    ersatzBeanMap.put(idValue, null);
                                    setErsatzBeans(null);
                                    break;
                                }
                                case fest: {
                                    festBeanMap.put(idValue, null);
                                    setFestBeans(null);
                                    break;
                                }
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.warn("problem in Worker: load values.", e);
                }
            }
        };
        if (toDo.equals(whatToDoForTree.ersatz)){
            if (worker_ersatz != null) {
                worker_ersatz.cancel(true);
            }
            worker_ersatz = worker;
            worker_ersatz.execute();
        } else{
            if (worker_fest != null) {
                worker_fest.cancel(true);
            }
            worker_fest = worker;
            worker_fest.execute();
        }
    }    
    }
    

