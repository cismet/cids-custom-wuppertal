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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.jdesktop.swingx.JXTable;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class No2MessungEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------
    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("schluessel");
    private static DefaultBindableReferenceCombo.Option MANAGEABLE_OPTION = null;
       // new DefaultBindableReferenceCombo.ManageableOption("name");
    
    private static final String TITLE_NEW_MESSUNG = "eine neue Messung anlegen...";
    private static final Logger LOG = Logger.getLogger(No2MessungEditor.class);
    
    public static final int COLUMN_WIDTH = 250;

    public static final String FIELD__ID = "id";                                // no2_messung
    public static final String FIELD__ZEIT = "fk_zeit";                         // no2_messung
    public static final String FIELD__VON = "von";                              // no2_standort
    public static final String FIELD__BIS = "bis";                              // no2_standort
    public static final String FIELD__MP = "mp";                                // no2_standort
    public static final String FIELD__ZEIT_NAME = "fk_zeit.name";               // no2_zeit
    public static final String FIELD__NAME = "name";                            // no2_zeit
    public static final String FIELD__ZEIT_SCHLUESSEL = "fk_zeit.schluessel";   // no2_zeit
    public static final String FIELD__SCHLUESSEL = "schluessel";                // no2_zeit
    public static final String FIELD__JAHR = "jahr";                            // no2_messung
    public static final String FIELD__WERTE = "n_werte";                        // no2_messung
    public static final String FIELD__WERT= "wert";                             // no2_wert
    public static final String FIELD__STANDORT = "fk_standort";                 // no2_wert
    public static final String FIELD__MESSUNG = "fk_messung";                   // no2_wert
    
    public static final String TABLE_NAME = "no2_messung";
    public static final String TABLE_NAME_ZEIT = "no2_zeit";
    public static final String TABLE_NAME_WERT = "no2_wert";
    public static final String TABLE_NAME_STANDORT = "no2_standort";

    public static final String BUNDLE_NOYEAR = 
            "No2MessungEditor.prepareForSave().noYear";
    public static final String BUNDLE_DUPLICATE = 
            "No2MessungEditor.prepareForSave().duplicate";
    public static final String BUNDLE_WRONGYEAR = 
            "No2MessungEditor.prepareForSave().wrongYear";
    public static final String BUNDLE_NOTIME = 
            "No2MessungEditor.prepareForSave().noTime";
    public static final String BUNDLE_NOLOCATION = 
            "No2MessungEditor.prepareForSave().noLocation";
    public static final String BUNDLE_TWICELOCATION = 
            "No2MessungEditor.prepareForSave().twiceLocation";
    public static final String BUNDLE_NOVALUE = 
            "No2MessungEditor.prepareForSave().noValue";
    public static final String BUNDLE_WRONGVALUE = 
            "No2MessungEditor.prepareForSave().wrongValue";
    public static final String BUNDLE_VONBIS = 
            "No2MessungEditor.prepareForSave().VonBis";
    public static final String BUNDLE_VONBISLOST = 
            "No2MessungEditor.prepareForSave().VonBisLost";
    public static final String BUNDLE_PANE_PREFIX = "No2MessungEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "No2MessungEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "No2MessungEditor.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_PANE_PREFIX_LOC = 
            "No2MessungEditor.btnCreateMeasureLocationsActionPerformed().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_TITLE_LOC = 
            "No2MessungEditor.btnCreateMeasureLocationsActionPerformed().JOptionPane.title.add";
    public static final String BUNDLE_PANE_LOCATION_MEASURE = 
            "No2MessungEditor.btnCreateMeasureLocationsActionPerformed().JOptionPane.message.add";
    public static final String BUNDLE_PANE_MESSAGE_DEL = 
            "No2MessungEditor.btnDeleteMeasureLocationsActionPerformed().JOptionPane.message";
    public static final String BUNDLE_PANE_TITLE_DEL = 
            "No2MessungEditor.btnDeleteMeasureLocationsActionPerformed().JOptionPane.title";
    public static final String BUNDLE_PANE_MESSAGE_ADD = 
            "No2MessungEditor.btnCreateMeasureLocationsActionPerformed().JOptionPane.message";
    public static final String BUNDLE_PANE_TITLE_ADD = 
            "No2MessungEditor.btnCreateMeasureLocationsActionPerformed().JOptionPane.title";

    private static final String[] WERTE_COL_NAMES = new String[] { "Werte", "Standorte" };
    private static final String[] WERTE_PROP_NAMES = new String[] {
            "wert",
            "fk_standort"
        };
    private static final Class[] WERTE_PROP_TYPES = new Class[] {
            Integer.class,
            CidsBean.class
        };
    
    private static enum otherTableCases {

        //~ Enum constants -----------------------------------------------------

        redundantAttName, setTime
    }
    
    //~ Instance fields --------------------------------------------------------

    private SwingWorker worker_name;
    private SwingWorker worker_time;

    private Boolean redundantName = false;

    private boolean isEditor = true;
    private MetaClass standortMetaClass;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddMesswert;
    private JButton btnCreateMeasureLocations;
    private JButton btnDeleteMeasureLocations;
    private JButton btnRemMesswert;
    private DefaultBindableReferenceCombo cbZeit;
    private DefaultBindableDateChooser dcBis;
    private DefaultBindableDateChooser dcVon;
    private Box.Filler filler2;
    private Box.Filler filler3;
    private JScrollPane jScrollPaneMesswerte;
    private JLabel lblBis;
    private JLabel lblJahr;
    private JLabel lblVon;
    private JLabel lblWerte;
    private JLabel lblZeit;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerUnten;
    private JPanel panMesswerte;
    private JPanel panMesswerteAdd;
    private JPanel panWerte;
    private JSeparator sepWerte;
    private JTextField txtJahr;
    private JXTable xtMesswerte;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public No2MessungEditor() {
    }

    /**
     * Creates a new No2MessungEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public No2MessungEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        
        standortMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME_STANDORT,
                connectionContext);
        cbZeit.setNullable(false);
        
        txtJahr.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn das Jahr oder der Monat geändert wird, wird die Kombination überprüft.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    checkName();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkName();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkName();
                }
            });
        cbZeit.addItemListener(new ItemListener() {

                // Immer, wenn das Jahr oder der Monat geändert wird, wird die Kombination überprüft.
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkName();
            }
            });

        setReadOnly();
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

        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panDaten = new JPanel();
        lblZeit = new JLabel();
        cbZeit = new DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION, SORTING_OPTION);
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblJahr = new JLabel();
        txtJahr = new JTextField();
        panFiller = new JPanel();
        lblVon = new JLabel();
        dcVon = new DefaultBindableDateChooser();
        lblBis = new JLabel();
        dcBis = new DefaultBindableDateChooser();
        sepWerte = new JSeparator();
        lblWerte = new JLabel();
        panWerte = new JPanel();
        panMesswerte = new JPanel();
        jScrollPaneMesswerte = new JScrollPane();
        xtMesswerte = new JXTable();
        panMesswerteAdd = new JPanel();
        btnAddMesswert = new JButton();
        btnRemMesswert = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        btnCreateMeasureLocations = new JButton();
        btnDeleteMeasureLocations = new JButton();

        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setMinimumSize(new Dimension(716, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(569, 488));
        panContent.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblZeit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblZeit.setText("Zeitpunkt:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblZeit, gridBagConstraints);

        cbZeit.setFont(new Font("Dialog", 0, 12)); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_zeit}"), cbZeit, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbZeit, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblJahr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblJahr.setText("Jahr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblJahr, gridBagConstraints);

        txtJahr.setName(""); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.jahr}"), txtJahr, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtJahr, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panDaten.add(panFiller, gridBagConstraints);

        lblVon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVon.setText("Von:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblVon, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.von}"), dcVon, BeanProperty.create("date"));
        binding.setConverter(dcVon.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcVon, gridBagConstraints);

        lblBis.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBis.setText("Bis:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBis, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bis}"), dcBis, BeanProperty.create("date"));
        binding.setConverter(dcBis.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcBis, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 0, 5, 0);
        panDaten.add(sepWerte, gridBagConstraints);

        lblWerte.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblWerte.setText("Messwerte:");
        lblWerte.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblWerte, gridBagConstraints);

        panWerte.setLayout(new GridBagLayout());

        panMesswerte.setMinimumSize(new Dimension(26, 80));
        panMesswerte.setLayout(new GridBagLayout());

        jScrollPaneMesswerte.setViewportView(xtMesswerte);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panMesswerte.add(jScrollPaneMesswerte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panWerte.add(panMesswerte, gridBagConstraints);

        panMesswerteAdd.setAlignmentX(0.0F);
        panMesswerteAdd.setAlignmentY(1.0F);
        panMesswerteAdd.setFocusable(false);
        panMesswerteAdd.setLayout(new GridBagLayout());

        btnAddMesswert.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddMesswert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddMesswertActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panMesswerteAdd.add(btnAddMesswert, gridBagConstraints);

        btnRemMesswert.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemMesswert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemMesswertActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panMesswerteAdd.add(btnRemMesswert, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMesswerteAdd.add(filler2, gridBagConstraints);

        btnCreateMeasureLocations.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
        btnCreateMeasureLocations.setToolTipText("Standorte anlegen");
        btnCreateMeasureLocations.setFocusPainted(false);
        btnCreateMeasureLocations.setMaximumSize(new Dimension(45, 21));
        btnCreateMeasureLocations.setMinimumSize(new Dimension(45, 21));
        btnCreateMeasureLocations.setPreferredSize(new Dimension(45, 21));
        btnCreateMeasureLocations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCreateMeasureLocationsActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panMesswerteAdd.add(btnCreateMeasureLocations, gridBagConstraints);

        btnDeleteMeasureLocations.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnDeleteMeasureLocations.setToolTipText("Standorte entfernen");
        btnDeleteMeasureLocations.setFocusPainted(false);
        btnDeleteMeasureLocations.setMaximumSize(new Dimension(45, 21));
        btnDeleteMeasureLocations.setMinimumSize(new Dimension(45, 21));
        btnDeleteMeasureLocations.setPreferredSize(new Dimension(45, 21));
        btnDeleteMeasureLocations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnDeleteMeasureLocationsActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panMesswerteAdd.add(btnDeleteMeasureLocations, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panWerte.add(panMesswerteAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panWerte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panContent.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddMesswertActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddMesswertActionPerformed
        TableUtils.addObjectToTable(xtMesswerte, TABLE_NAME_WERT, getConnectionContext());
    }//GEN-LAST:event_btnAddMesswertActionPerformed

    private void btnRemMesswertActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemMesswertActionPerformed
        TableUtils.removeObjectsFromTable(xtMesswerte);
    }//GEN-LAST:event_btnRemMesswertActionPerformed

    private void btnCreateMeasureLocationsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCreateMeasureLocationsActionPerformed
        try {
            if (txtJahr.getText().trim().isEmpty() || cbZeit.getSelectedItem() == null){
                //Meldung nicht moeglich
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_PREFIX_LOC)
                        + NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_LOCATION_MEASURE)
                        + NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_TITLE_LOC),
                JOptionPane.WARNING_MESSAGE);
            } else {
                //Meldung: wirklich mit loeschen?
                final int answer = JOptionPane.showConfirmDialog(
                                StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_MESSAGE_ADD),
                                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_TITLE_ADD),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    deleteMeasure();
                    createMeasure();
                }
            }
        } catch (HeadlessException | MissingResourceException e) {
            LOG.error("Cannot add new " + TABLE_NAME_WERT + " objects", e);
        }
                        

    
    }//GEN-LAST:event_btnCreateMeasureLocationsActionPerformed

    private void btnDeleteMeasureLocationsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnDeleteMeasureLocationsActionPerformed
        //Meldung: wirklich loeschen?
        final int answer = JOptionPane.showConfirmDialog(
                                StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_MESSAGE_DEL),
                                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_TITLE_DEL),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            deleteMeasure();
        }
    }//GEN-LAST:event_btnDeleteMeasureLocationsActionPerformed

    //Fuegt eine neue Zeile hinzu und setz die Bean fuer den Standort
    public static void addLocationsToTable(final JXTable table,
            final MetaObject standortMo,
            final ConnectionContext connectionContext) {
        CidsBean bean;
        try {
            bean = CidsBeanSupport.createNewCidsBeanFromTableName(TABLE_NAME_WERT, connectionContext);
            bean.setProperty(FIELD__STANDORT, standortMo.getBean());
            ((DivBeanTable)table.getModel()).addBean(bean);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /**
     * Entfernt alle Zeilen aus der Tabelle
     */
    public void deleteMeasure(){
        xtMesswerte.selectAll();
        TableUtils.removeObjectsFromTable(xtMesswerte);
    }
    
    /**
     * Erzeugt alle notwendigen Zeilen mit Vorbelegung des Standorts
     */
    public void createMeasure(){
        final MetaClass myClass;
        myClass = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                TABLE_NAME_STANDORT,
                getConnectionContext());
        StringBuffer myQuery = new StringBuffer("");
        if ((int)cidsBean.getProperty(FIELD__ZEIT_SCHLUESSEL) == 13){
            //Jahresdurchschnitt Jeder Standort, der in dem Jahr auftaucht
            myQuery = new StringBuffer("select ").append(myClass.getId())
                        .append(", ")
                        .append(myClass.getName())
                        .append(".")
                        .append(myClass.getPrimaryKey())
                        .append(" from ")
                        .append(myClass.getTableName())
                        .append(" where ")
                        .append(FIELD__ID)
                        .append(" in ( select ")
                        .append(TABLE_NAME_WERT)
                        .append(".")
                        .append(FIELD__STANDORT)
                        .append(" from ")
                        .append(TABLE_NAME_WERT)
                        .append(" left join ")
                        .append(TABLE_NAME)
                        .append(" on ")
                        .append(TABLE_NAME)
                        .append(".")
                        .append(FIELD__ID)
                        .append(" = ")
                        .append(TABLE_NAME_WERT)
                        .append(".")
                        .append(FIELD__MESSUNG)
                        .append(" where ")
                        .append(TABLE_NAME)
                        .append(".")
                        .append(FIELD__JAHR)
                        .append(" = ")
                        .append((int)cidsBean.getProperty(FIELD__JAHR))
                        .append(") order by ")
                        .append(FIELD__MP);
        } else {
            //Standort muss in diesem Monat aktiv sein
            String datum;
            if ((int)cidsBean.getProperty(FIELD__ZEIT_SCHLUESSEL) > 9){
                datum =  "15." + cidsBean.getProperty(FIELD__ZEIT_SCHLUESSEL).toString() + "." + cidsBean.getProperty(FIELD__JAHR).toString();
            } else {
                datum =  "15.0" + cidsBean.getProperty(FIELD__ZEIT_SCHLUESSEL).toString() + "." + cidsBean.getProperty(FIELD__JAHR).toString();
            }
            if (myClass != null) {
                myQuery = new StringBuffer("select ").append(myClass.getId())
                        .append(", ")
                        .append(myClass.getName())
                        .append(".")
                        .append(myClass.getPrimaryKey())
                        .append(" from ")
                        .append(myClass.getTableName())
                        .append(" where ")
                        .append(FIELD__VON)
                        .append(" < '")
                        .append(datum)
                        .append("' and ( ")
                        .append(FIELD__BIS)
                        .append(" is null or ")
                        .append(FIELD__BIS)
                        .append(" > '")
                        .append(datum)
                        .append("') order by ")
                        .append(FIELD__MP);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: myQuery:" + myQuery.toString());
        }
        final MetaObject[] myMetaObjects;
        try {
            myMetaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(myQuery.toString(), 0, getConnectionContext());
            if (myMetaObjects != null && myMetaObjects.length > 0){
                for (final MetaObject mo : myMetaObjects){
                    addLocationsToTable(xtMesswerte, mo, getConnectionContext());
                }
            } else {
                //keine Standorte vorhanden
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_PREFIX_LOC)
                        + NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_LOCATION_MEASURE)
                        + NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(No2MessungEditor.class, BUNDLE_PANE_TITLE_LOC),
                JOptionPane.WARNING_MESSAGE);
            }
        } catch (ConnectionException ex) {
            LOG.error(ex, ex);
        }
    }
    /**
     * redundante Messung
     */
    private void checkName() {
        // Worker Aufruf, ob die Kombination aus Monat und Jahr schon existiert
        valueFromOtherTable(
            TABLE_NAME,
                " left join "
                    + TABLE_NAME_ZEIT
                    + " on "
                    + TABLE_NAME_ZEIT
                    + "."
                    + FIELD__ID
                    + " = "
                    + TABLE_NAME
                    + "."
                    + FIELD__ZEIT
                    + " where "
                    + TABLE_NAME_ZEIT
                    + "."
                    + FIELD__NAME
                    + " = '"
                    + cbZeit.getSelectedItem()
                    + "' and "
                    + TABLE_NAME
                    + "."
                    + FIELD__JAHR
                    + " = "
                    + txtJahr.getText().trim()
                    + " and "
                    + TABLE_NAME
                    + "."
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            otherTableCases.redundantAttName);
    }
    
    /**
     * Ermittelt die aktuelle Messung
     */
    private void setMeasureValues() {
        try {
            final MetaClass myClass;
            myClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    TABLE_NAME,
                    getConnectionContext());
            if (myClass != null) {
                //Alle Messung aus dem zuletzt eingegebenen Jahr
                final StringBuffer myQuery = new StringBuffer("select ").append(myClass.getId())
                            .append(", ")
                            .append(myClass.getName())
                            .append(".")
                            .append(myClass.getPrimaryKey())
                            .append(" from ")
                            .append(myClass.getTableName())
                            .append(" where ")
                            .append(FIELD__JAHR)
                            .append(" in (select max(")
                            .append(FIELD__JAHR)
                            .append(") from ")
                            .append(myClass.getTableName())
                            .append(")");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: myQuery:" + myQuery.toString());
                }
                final MetaObject[] myMetaObject;
                try {
                    myMetaObject = SessionManager.getProxy()
                                .getMetaObjectByQuery(myQuery.toString(), 0, getConnectionContext());
                    if (myMetaObject != null){
                        int month = 0;
                        MetaObject moLast = null;
                        //Letzte eingegeben Messung
                        for (final MetaObject mo : myMetaObject){
                            if ((int)mo.getBean().getProperty(FIELD__ZEIT_SCHLUESSEL) > month){
                                month = (int)mo.getBean().getProperty(FIELD__ZEIT_SCHLUESSEL);
                                moLast = mo;
                            }
                        }
                        if (moLast != null){
                            setNewMeasure(moLast.getBean());
                        }
                    }
                    
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error(" kann nicht geladen werden ", ex);
        }
    }
    
    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // Jahr vorhanden
        try {
            if (txtJahr.getText().trim().isEmpty()) {
                LOG.warn("No year specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_NOYEAR));
            } else {
                //
                if (cbZeit.getSelectedItem() == null){
                    LOG.warn("No time specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_NOTIME));
                }else {
                    if (redundantName) {
                        LOG.warn("Duplicate name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_DUPLICATE));
                    } else {
                        if (txtJahr.getText().trim().length() == 4){
                            try {
                                Integer.parseInt(txtJahr.getText());
                            } catch (NumberFormatException e) {
                                LOG.warn("Wrong Mp specified. Skip persisting.", e);
                                errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_WRONGYEAR));
                            }
                        } else {
                            errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_WRONGYEAR));
                        }
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        
        //von muss angegeben werden
        try {
            if ((dcVon.getDate() == null  && dcBis.getDate() != null) || (dcBis.getDate() == null  && dcVon.getDate() != null)) {
                LOG.warn("No von specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_VONBISLOST));
            } else {
                if(dcVon.getDate() != null && dcBis.getDate() != null){
                    final LocalDate ldBis = dcBis.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    final LocalDate ldVon = dcVon.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (ldBis.isBefore(ldVon)) {
                        errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_VONBIS));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("VonBis not given.", ex);
            save = false;
        }
        //Liste ueberpruefen
        try {
            switch (checkValuesForMeasure()) {
                case 1: {
                    LOG.warn("Twice measure specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_TWICELOCATION));
                    break;
                }
                case 2: {
                    LOG.warn("No value specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_NOVALUE));
                    break;
                }
                case 3: {
                    LOG.warn("No location specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_NOLOCATION));
                    break;
                }
                case 4: {
                    LOG.warn("Wrong value specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(No2MessungEditor.class, BUNDLE_WRONGVALUE));
                    break;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Error in Tabele.", ex);
            save = false;
        }
           
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    No2MessungEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            No2MessungEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(
                    No2MessungEditor.class,
                    BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
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
                LOG.info("remove propchange no2_Messung: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange no2_Messung: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            bindingGroup.bind();
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
                setMeasureValues();
            } else {
                RendererTools.makeReadOnly(txtJahr);
                RendererTools.makeReadOnly(cbZeit);
            }

            final DivBeanTable wertModel = new DivBeanTable(
                    isEditor,
                    cidsBean,
                    FIELD__WERTE,
                    WERTE_COL_NAMES,
                    WERTE_PROP_NAMES,
                    WERTE_PROP_TYPES);
            xtMesswerte.setModel(wertModel);
            xtMesswerte.getColumn(1).setCellEditor(new DefaultBindableComboboxCellEditor(standortMetaClass));
            xtMesswerte.getColumn(1).setPreferredWidth(COLUMN_WIDTH);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }

    /**
     * Ueberprueft die Eingaben der Tabelle, sobald Fehler Abbruch
     */
    private int checkValuesForMeasure() {
        try {
            final List<CidsBean> listMeasureBeans = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__WERTE);

            if ((listMeasureBeans!= null) && (listMeasureBeans.size() > 0)) {
                for (int i = 0; i < listMeasureBeans.size(); i++) {
                    final CidsBean measureBean = listMeasureBeans.get(i);
                    // .......Überprüfen ob alle Einträge gefüllt.......
                    if ((null == measureBean.getProperty(FIELD__WERT))
                                || "".equals(measureBean.getProperty(FIELD__WERT).toString())) {
                        return 2;
                    }
                    if ((int)measureBean.getProperty(FIELD__WERT)< 0 && (int)measureBean.getProperty(FIELD__WERT) != -9999){
                        return 4;
                    }
                    if ((null == measureBean.getProperty(FIELD__STANDORT))
                                || "".equals(measureBean.getProperty(FIELD__STANDORT).toString())) {
                        return 3;
                    }
                    //alle Eintraege vorhanden?
                    
                    // Redundante Einträge
                    if (listMeasureBeans.size() > (i + 1)) {
                        for (int j = i + 1; j < listMeasureBeans.size(); j++) {
                            if (
                                measureBean.getProperty(FIELD__STANDORT).equals(
                                        listMeasureBeans.get(j).getProperty(FIELD__STANDORT))) {
                                return 1;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return 0;
    }
    /**
     * Mit Hilfe der zuletzt eingegeben Messung(Bean), wird die naechste Messung ermittelt
     *
     * @param  latestBean        DOCUMENT ME!
     */
    public void setNewMeasure(final CidsBean latestBean){
        final int latestYear = Integer.parseInt(latestBean.getProperty(FIELD__JAHR).toString());
        final int latestTime = Integer.parseInt(latestBean.getProperty(FIELD__ZEIT_SCHLUESSEL).toString());
        int nextYear;
        int nextTime;
        if (latestTime == 13){
            nextYear = latestYear + 1;
            nextTime = 1;
        } else {
            nextYear = latestYear;
            nextTime = latestTime + 1;
        }
        try {
            cidsBean.setProperty(FIELD__JAHR, nextYear);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        //CidsBean fuer die Zeit ermitteln und setzen
        valueFromOtherTable(
            TABLE_NAME_ZEIT,
            " where "
                    + FIELD__SCHLUESSEL
                    + " = "
                    + nextTime,
            otherTableCases.setTime);
    }
    /**
     * Fuer Renderer
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtJahr);
            RendererTools.makeReadOnly(cbZeit);
            RendererTools.makeReadOnly(dcVon);
            RendererTools.makeReadOnly(dcBis);
            RendererTools.makeReadOnly(xtMesswerte);
            panMesswerteAdd.setVisible(isEditor);
        }
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
            "no2_messung",
            92,
            800,
            600);
    }
    
    
    @Override
    public String getTitle() {
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_MESSUNG;
        } else {
            return cidsBean.getProperty(FIELD__ZEIT_NAME).toString() + "- " + cidsBean.getProperty(FIELD__JAHR).toString();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
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
    public void propertyChange(final PropertyChangeEvent evt) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // To change body of generated methods, choose Tools | Templates.
        
    }
    /**
     * Ermittelt, ob Werte fuer ein Select geliefert werden.
     * redundante Messung bei not null
     * set Value: Vorbelegung des Zeitpunktes
     *
     * @param  tableName    DOCUMENT ME!
     * @param  whereClause  DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName,
            final String whereClause,
            final otherTableCases fall) {
        final SwingWorker<CidsBean, Void> worker = new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    return getOtherTableValue(tableName, whereClause, getConnectionContext());
                }

                @Override
                protected void done() {
                    final CidsBean check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            switch (fall) {
                                case setTime: {  //set next time
                                    try {
                                        cidsBean.setProperty(
                                            FIELD__ZEIT,
                                            check);
                                    } catch (Exception ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                                case redundantAttName: { // check redundant name
                                    redundantName = check != null;
                                    break;
                                }
                            }
                            
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: chech name.", e);
                    }
                }
            };
        switch (fall){
            case redundantAttName:{
                if (worker_name != null) {
                    worker_name.cancel(true);
                }
                worker_name = worker;
                worker_name.execute();
                break;
            }
            case setTime:{
                if (worker_time != null) {
                    worker_time.cancel(true);
                }
                worker_time = worker;
                worker_time.execute();
                break;
            }
        }
        
    }

    //~ Inner Classes ----------------------------------------------------------
    
}
