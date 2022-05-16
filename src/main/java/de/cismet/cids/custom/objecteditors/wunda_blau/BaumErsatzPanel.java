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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.DefaultCismapGeometryComboBoxCellEditor;
import de.cismet.cids.custom.objecteditors.utils.FastBindableReferenceComboCellEditor;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BaumArtLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.attributetable.DateCellEditor;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumErsatzPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumErsatzPanel.class);
    private static final MetaClass MC__ART;
    private static final MetaClass MC__SORTE;

    public static final String GEOMTYPE = "Polygon";
    public static final String GEOM_POINT = "Point";

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumErsatzPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
        MC__SORTE = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_SORTE",
                connectionContext);
    }

    public static final String FIELD__KONTROLLE = "n_kontrolle";                 // baum_ersatz
    public static final String FIELD__BAUM = "n_ersatzbaum";                     // baum_ersatz
    public static final String FIELD__DATE = "datum";                            // baum_kontrolle
    public static final String FIELD__STRASSE = "fk_strasse.strassenschluessel"; // baum_ersatz
    public static final String FIELD__HNR = "fk_adresse";                        // baum_ersatz
    public static final String FIELD__DATUM_P = "pflanzdatum";                   // baum_ersatz
    public static final String FIELD__DATUM_U = "umsetzung_bis";                 // baum_ersatz
    public static final String FIELD__ART = "fk_art";                            // baum_ersatzbaum
    public static final String FIELD__GEOM = "fk_geom";                          // baum_ersatz
    public static final String FIELD__SELBST = "selbststaendig";                 // baum_ersatz
    public static final String FIELD__BIS = "umsetzung_bis";                     // baum_ersatz
    public static final String FIELD__ANZAHL = "anzahl";                         // baum_ersatz
    public static final String FIELD__FIRMA = "firma";                           // baum_ersatz
    public static final String FIELD__BEMERKUNG = "bemerkung";                   // baum_ersatz
    public static final String FIELD__BAUM_BEMERKUNG = "bemerkung";              // baum_ersatzbaum
    public static final String FIELD__BAUM_ART= "fk_art";                        // baum_ersatzbaum
    public static final String FIELD__BAUM_SORTE = "fk_sorte";                   // baum_ersatzbaum
    public static final String FIELD__BAUM_GEOM = "fk_geom";                     // baum_ersatzbaum
    public static final String FIELD__KONTROLLE_BEMERKUNG = "bemerkung";         // baum_ersatz
    public static final String FIELD__KONTROLLE_DATUM = "datum";                 // baum_ersatz
    public static final String FIELD__GEOREFERENZ = "fk_geom";                   // baum_ersatz
    public static final String FIELD__STRASSE_NAME = "name";                     // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";        // strasse
    public static final String FIELD__FK_SCHADEN = "fk_schaden";                 // baum_ersatz
    public static final String FIELD__FK_MELDUNG = "fk_meldung";                 // baum_schaden
    public static final String FIELD__MDATUM = "datum";                          // baum_meldung

    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_ersatz_geom

    public static final String TABLE__NAME = "baum_ersatz";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_SORTE = "baum_sorte";
    public static final String TABLE_NAME__KONTROLLE = "baum_kontrolle";
    public static final String TABLE_NAME__ERSATZBAUM = "baum_ersatzbaum";

    public static final String BUNDLE_NOSTREET = "BaumErsatzPanel.isOkForSaving().noStrasse";
    public static final String BUNDLE_NOBIS = "BaumErsatzPanel.isOkForSaving().noBis";
    public static final String BUNDLE_NODATE = "BaumErsatzPanel.isOkForSaving().noPflanzung";
    public static final String BUNDLE_NOGEOM = "BaumErsatzPanel.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumErsatzPanel.isOkForSaving().wrongGeom";
    public static final String BUNDLE_NOGEOMBAUM = "BaumErsatzPanel.isOkForSaving().noGeomBaum";
    public static final String BUNDLE_WRONGGEOMBAUM = "BaumErsatzPanel.isOkForSaving().wrongGeomBaum";
    public static final String BUNDLE_NOBAUM = "BaumErsatzPanel.isOkForSaving().noBaum";
    public static final String BUNDLE_WRONGANZAHLBAUM = "BaumErsatzPanel.isOkForSaving().wrongAnzahlBaum";
    public static final String BUNDLE_LOCATION_GEOMETRY = "BaumErsatzPanel.isOkForSaving().locationGeometry";
    public static final String BUNDLE_NOART = "BaumErsatzPanel.isOkForSaving().noArt";
    public static final String BUNDLE_WRONGCOUNT = "BaumErsatzPanel.isOkForSaving().wrongAnzahl";
    public static final String BUNDLE_NOCOUNT = "BaumErsatzPanel.isOkForSaving().noAnzahl";
    public static final String BUNDLE_NOCONTROLDATE = "BaumErsatzPanel.isOkForSaving().noKontrolleDatum";
    public static final String BUNDLE_FUTUREDATE = "BaumErsatzPanel.isOkForSaving().zukunftsDatum";
    public static final String BUNDLE_FUTUREDATEPLANT = "BaumErsatzPanel.isOkForSaving().zukunftsDatumPflanzung";
    public static final String BUNDLE_NOCONTROLTEXT = "BaumErsatzPanel.isOkForSaving().noControlText";
    public static final String BUNDLE_WHICH = "BaumErsatzPanel.isOkForSaving().welcheErsatz";
    public static final String BUNDLE_FAULT = "BaumErsatzPanel.isOkForSaving().welcherSchaden";
    public static final String BUNDLE_MESSAGE = "BaumErsatzPanel.isOkForSaving().welcheMeldung";
    public static final String BUNDLE_PANE_PREFIX = "BaumErsatzPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumErsatzPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumErsatzPanel.isOkForSaving().JOptionPane.title";
    
    private static final String[] KONTROLLE_COL_NAMES = new String[] {
            "Datum",
            "Bemerkung"
        };
    private static final String[] KONTROLLE_PROP_NAMES = new String[] {
            FIELD__KONTROLLE_DATUM,
            FIELD__KONTROLLE_BEMERKUNG
        };
    private static final Class[] KONTROLLE_PROP_TYPES = new Class[] {
            Date.class,
            String.class
        };
    
    private static final String[] BAUM_COL_NAMES = new String[] {
            "Geometrie",
            "Art",
            "Sorte",
            "Bemerkung"
        };
    private static final String[] BAUM_PROP_NAMES = new String[] {
            FIELD__BAUM_GEOM,
            FIELD__BAUM_ART,
            FIELD__BAUM_SORTE,
            FIELD__BAUM_BEMERKUNG
        };
    private static final Class[] BAUM_PROP_TYPES = new Class[] {
            CidsBean.class, 
            CidsBean.class,
            CidsBean.class,
            String.class
        };

    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = { AdresseLightweightSearch.Subject.HNR.toString() };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panDaten = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panErsatz = new JPanel();
        panInhalt = new JPanel();
        lblSelbst = new JLabel();
        chSelbst = new JCheckBox();
        if (!isEditor()){
            lblHNrRenderer = new JLabel();
        }
        lblDispens = new JLabel();
        chDispens = new JCheckBox();
        lblBis = new JLabel();
        dcBis = new DefaultBindableDateChooser();
        lblAnzahl = new JLabel();
        lblDatum = new JLabel();
        dcDatum = new DefaultBindableDateChooser();
        lblFirma = new JLabel();
        txtFirma = new JTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkungE = new JTextArea();
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeomErsatz = new DefaultCismapGeometryComboBoxEditor();
        }
        lblStrasse = new JLabel();
        cbStrasse = new FastBindableReferenceCombo();
        lblHnr = new JLabel();
        if (isEditor()){
            cbHNr = new FastBindableReferenceCombo(
                hnrSearch,
                hnrSearch.getRepresentationPattern(),
                hnrSearch.getRepresentationFields()
            );
        }
        spAnzahl = new JSpinner();
        panGeometrie = new JPanel();
        baumLagePanel = new BaumLagePanel();
        panKont = new JPanel();
        rpKont = new RoundedPanel();
        semiRoundedPanel8 = new SemiRoundedPanel();
        lblKont = new JLabel();
        panKontAdd = new JPanel();
        btnAddKont = new JButton();
        btnRemKont = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panKontDaten = new JPanel();
        jScrollPaneKont = new JScrollPane();
        xtKont = new JXTable();
        filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelErsatzpflanzung = new JPanel();
        panBaeume = new JPanel();
        panGeometrieBaum = new JPanel();
        baumErsatzLagePanel = new BaumLagePanel();
        panBaum = new JPanel();
        rpBaum = new RoundedPanel();
        semiRoundedPanel9 = new SemiRoundedPanel();
        lblBaum = new JLabel();
        panBaumAdd = new JPanel();
        btnAddBaum = new JButton();
        btnRemBaum = new JButton();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panBaumDaten = new JPanel();
        jScrollPaneBaum = new JScrollPane();
        xtBaum = new JXTable();
        filler8 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panDaten.setName("panDaten"); // NOI18N
        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        jTabbedPane.setMinimumSize(new Dimension(849, 520));
        jTabbedPane.setName("jTabbedPane"); // NOI18N

        jPanelAllgemein.setName("jPanelAllgemein"); // NOI18N
        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        panInhalt.setMinimumSize(new Dimension(100, 10));
        panInhalt.setName("panInhalt"); // NOI18N
        panInhalt.setOpaque(false);
        panInhalt.setPreferredSize(new Dimension(520, 270));
        panInhalt.setLayout(new GridBagLayout());

        lblSelbst.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSelbst, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblSelbst.text")); // NOI18N
        lblSelbst.setName("lblSelbst"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblSelbst, gridBagConstraints);

        chSelbst.setContentAreaFilled(false);
        chSelbst.setEnabled(false);
        chSelbst.setName("chSelbst"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.selbststaendig}"), chSelbst, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(chSelbst, gridBagConstraints);

        if (!isEditor()){
            lblHNrRenderer.setFont(new Font("Dialog", 0, 12)); // NOI18N
            lblHNrRenderer.setEnabled(false);
            lblHNrRenderer.setName("lblHNrRenderer"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse.hausnummer}"), lblHNrRenderer, BeanProperty.create("text"));
            binding.setSourceNullValue("null");
            binding.setSourceUnreadableValue("null");
            bindingGroup.addBinding(binding);

        }
        if (!isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 5, 2, 5);
            panInhalt.add(lblHNrRenderer, gridBagConstraints);
        }

        lblDispens.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblDispens, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblDispens.text")); // NOI18N
        lblDispens.setName("lblDispens"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panInhalt.add(lblDispens, gridBagConstraints);

        chDispens.setContentAreaFilled(false);
        chDispens.setEnabled(false);
        chDispens.setName("chDispens"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.dispensbau}"), chDispens, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(chDispens, gridBagConstraints);

        lblBis.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBis, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblBis.text")); // NOI18N
        lblBis.setName("lblBis"); // NOI18N
        lblBis.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblBis, gridBagConstraints);

        dcBis.setEnabled(false);
        dcBis.setName("dcBis"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.umsetzung_bis}"), dcBis, BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcBis.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(dcBis, gridBagConstraints);

        lblAnzahl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAnzahl, "Anzahl:");
        lblAnzahl.setName("lblAnzahl"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblAnzahl, gridBagConstraints);

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblDatum, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblDatum.text")); // NOI18N
        lblDatum.setName("lblDatum"); // NOI18N
        lblDatum.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panInhalt.add(lblDatum, gridBagConstraints);

        dcDatum.setEnabled(false);
        dcDatum.setName("dcDatum"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.pflanzdatum}"), dcDatum, BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(dcDatum, gridBagConstraints);

        lblFirma.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFirma, "Firma:");
        lblFirma.setName("lblFirma"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblFirma, gridBagConstraints);

        txtFirma.setEnabled(false);
        txtFirma.setName("txtFirma"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.firma}"), txtFirma, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(txtFirma, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkungE.setLineWrap(true);
        taBemerkungE.setRows(2);
        taBemerkungE.setWrapStyleWord(true);
        taBemerkungE.setEnabled(false);
        taBemerkungE.setName("taBemerkungE"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkungE, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkungE);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 0, 2);
        panInhalt.add(scpBemerkung, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
        lblGeom.setName("lblGeom"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblGeom, gridBagConstraints);

        if (isEditor()){
            cbGeomErsatz.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomErsatz.setEnabled(false);
            cbGeomErsatz.setName("cbGeomErsatz"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeomErsatz, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panInhalt.add(cbGeomErsatz, gridBagConstraints);
        }

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStrasse, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblStrasse.text")); // NOI18N
        lblStrasse.setName("lblStrasse"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblStrasse, gridBagConstraints);

        cbStrasse.setMaximumRowCount(20);
        cbStrasse.setModel(new LoadModelCb());
        cbStrasse.setEnabled(false);
        cbStrasse.setName("cbStrasse"); // NOI18N
        cbStrasse.setNullable(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_strasse}"), cbStrasse, BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cbStrasse.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(cbStrasse, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHnr, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblHnr.text")); // NOI18N
        lblHnr.setName("lblHnr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panInhalt.add(lblHnr, gridBagConstraints);

        if (isEditor()){
            cbHNr.setMaximumRowCount(20);
            cbHNr.setEnabled(false);
            cbHNr.setName("cbHNr"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse}"), cbHNr, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panInhalt.add(cbHNr, gridBagConstraints);
        }

        spAnzahl.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spAnzahl.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spAnzahl.setEnabled(false);
        spAnzahl.setName("spAnzahl"); // NOI18N
        spAnzahl.setOpaque(false);
        spAnzahl.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl}"), spAnzahl, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0
        );
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(spAnzahl, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        panErsatz.add(panInhalt, gridBagConstraints);

        panGeometrie.setMinimumSize(new Dimension(300, 142));
        panGeometrie.setName("panGeometrie"); // NOI18N
        panGeometrie.setOpaque(false);
        panGeometrie.setPreferredSize(new Dimension(205, 200));
        panGeometrie.setLayout(new GridBagLayout());

        baumLagePanel.setMinimumSize(new Dimension(52, 150));
        baumLagePanel.setName("baumLagePanel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(baumLagePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        panErsatz.add(panGeometrie, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanelAllgemein.add(panErsatz, gridBagConstraints);

        panKont.setName("panKont"); // NOI18N
        panKont.setOpaque(false);
        panKont.setLayout(new GridBagLayout());

        rpKont.setName("rpKont"); // NOI18N
        rpKont.setLayout(new GridBagLayout());

        semiRoundedPanel8.setBackground(Color.darkGray);
        semiRoundedPanel8.setName("semiRoundedPanel8"); // NOI18N
        semiRoundedPanel8.setLayout(new GridBagLayout());

        lblKont.setForeground(new Color(255, 255, 255));
        lblKont.setText("Kontrollen");
        lblKont.setName("lblKont"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 8, 2, 2);
        semiRoundedPanel8.add(lblKont, gridBagConstraints);

        panKontAdd.setAlignmentX(0.0F);
        panKontAdd.setAlignmentY(1.0F);
        panKontAdd.setFocusable(false);
        panKontAdd.setName("panKontAdd"); // NOI18N
        panKontAdd.setOpaque(false);
        panKontAdd.setLayout(new GridBagLayout());

        btnAddKont.setBackground(new Color(0, 0, 0));
        btnAddKont.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddKont.setBorder(null);
        btnAddKont.setBorderPainted(false);
        btnAddKont.setContentAreaFilled(false);
        btnAddKont.setName("btnAddKont"); // NOI18N
        btnAddKont.setPreferredSize(new Dimension(45, 13));
        btnAddKont.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panKontAdd.add(btnAddKont, gridBagConstraints);

        btnRemKont.setBackground(new Color(0, 0, 0));
        btnRemKont.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemKont.setBorder(null);
        btnRemKont.setBorderPainted(false);
        btnRemKont.setContentAreaFilled(false);
        btnRemKont.setName("btnRemKont"); // NOI18N
        btnRemKont.setPreferredSize(new Dimension(45, 13));
        btnRemKont.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panKontAdd.add(btnRemKont, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panKontAdd.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 0, 5, 15);
        semiRoundedPanel8.add(panKontAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKont.add(semiRoundedPanel8, gridBagConstraints);

        panKontDaten.setName("panKontDaten"); // NOI18N
        panKontDaten.setLayout(new GridBagLayout());

        jScrollPaneKont.setName("jScrollPaneKont"); // NOI18N

        xtKont.setName("xtKont"); // NOI18N
        xtKont.setVisibleRowCount(7);
        jScrollPaneKont.setViewportView(xtKont);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panKontDaten.add(jScrollPaneKont, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKont.add(panKontDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panKont.add(rpKont, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        jPanelAllgemein.add(panKont, gridBagConstraints);

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelAllgemein.add(filler7, gridBagConstraints);

        jTabbedPane.addTab(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.jPanelAllgemein.TabConstraints.tabTitle"), jPanelAllgemein); // NOI18N

        jPanelErsatzpflanzung.setName("jPanelErsatzpflanzung"); // NOI18N
        jPanelErsatzpflanzung.setOpaque(false);
        jPanelErsatzpflanzung.setLayout(new GridBagLayout());

        panBaeume.setName("panBaeume"); // NOI18N
        panBaeume.setOpaque(false);
        panBaeume.setLayout(new GridBagLayout());

        panGeometrieBaum.setMinimumSize(new Dimension(300, 142));
        panGeometrieBaum.setName("panGeometrieBaum"); // NOI18N
        panGeometrieBaum.setOpaque(false);
        panGeometrieBaum.setPreferredSize(new Dimension(205, 200));
        panGeometrieBaum.setLayout(new GridBagLayout());

        baumErsatzLagePanel.setMinimumSize(new Dimension(52, 150));
        baumErsatzLagePanel.setName("baumErsatzLagePanel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrieBaum.add(baumErsatzLagePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBaeume.add(panGeometrieBaum, gridBagConstraints);

        panBaum.setName("panBaum"); // NOI18N
        panBaum.setOpaque(false);
        panBaum.setLayout(new GridBagLayout());

        rpBaum.setName("rpBaum"); // NOI18N
        rpBaum.setLayout(new GridBagLayout());

        semiRoundedPanel9.setBackground(Color.darkGray);
        semiRoundedPanel9.setName("semiRoundedPanel9"); // NOI18N
        semiRoundedPanel9.setLayout(new GridBagLayout());

        lblBaum.setForeground(new Color(255, 255, 255));
        lblBaum.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblBaum.text")); // NOI18N
        lblBaum.setName("lblBaum"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 8, 2, 2);
        semiRoundedPanel9.add(lblBaum, gridBagConstraints);

        panBaumAdd.setAlignmentX(0.0F);
        panBaumAdd.setAlignmentY(1.0F);
        panBaumAdd.setFocusable(false);
        panBaumAdd.setName("panBaumAdd"); // NOI18N
        panBaumAdd.setOpaque(false);
        panBaumAdd.setLayout(new GridBagLayout());

        btnAddBaum.setBackground(new Color(0, 0, 0));
        btnAddBaum.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBaum.setBorder(null);
        btnAddBaum.setBorderPainted(false);
        btnAddBaum.setContentAreaFilled(false);
        btnAddBaum.setName("btnAddBaum"); // NOI18N
        btnAddBaum.setPreferredSize(new Dimension(45, 13));
        btnAddBaum.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panBaumAdd.add(btnAddBaum, gridBagConstraints);

        btnRemBaum.setBackground(new Color(0, 0, 0));
        btnRemBaum.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemBaum.setBorder(null);
        btnRemBaum.setBorderPainted(false);
        btnRemBaum.setContentAreaFilled(false);
        btnRemBaum.setName("btnRemBaum"); // NOI18N
        btnRemBaum.setPreferredSize(new Dimension(45, 13));
        btnRemBaum.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panBaumAdd.add(btnRemBaum, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panBaumAdd.add(filler3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 0, 5, 15);
        semiRoundedPanel9.add(panBaumAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpBaum.add(semiRoundedPanel9, gridBagConstraints);

        panBaumDaten.setName("panBaumDaten"); // NOI18N
        panBaumDaten.setLayout(new GridBagLayout());

        jScrollPaneBaum.setName("jScrollPaneBaum"); // NOI18N

        xtBaum.setName("xtBaum"); // NOI18N
        xtBaum.setVisibleRowCount(7);
        jScrollPaneBaum.setViewportView(xtBaum);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBaumDaten.add(jScrollPaneBaum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpBaum.add(panBaumDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panBaum.add(rpBaum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        panBaeume.add(panBaum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelErsatzpflanzung.add(panBaeume, gridBagConstraints);

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzpflanzung.add(filler8, gridBagConstraints);

        jTabbedPane.addTab(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.jPanelErsatzpflanzung.TabConstraints.tabTitle"), jPanelErsatzpflanzung); // NOI18N

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDaten.add(jTabbedPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panDaten, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == cbStrasse) {
                BaumErsatzPanel.this.cbStrasseActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddKont) {
                BaumErsatzPanel.this.btnAddKontActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemKont) {
                BaumErsatzPanel.this.btnRemKontActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddBaum) {
                BaumErsatzPanel.this.btnAddBaumActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemBaum) {
                BaumErsatzPanel.this.btnRemBaumActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    //~ Instance fields --------------------------------------------------------

    @Getter @Setter private List<CidsBean> kontrolleBeans;
    @Getter @Setter private List<CidsBean> baumBeans;
    
    private Boolean insideBox = false;
    private String outsidePoint = "";
    
    private SwingWorker worker_sorte;
    private SwingWorker worker_area;
    
    private final AdresseLightweightSearch hnrSearch = new AdresseLightweightSearch(
            AdresseLightweightSearch.Subject.HNR,
            ADRESSE_TOSTRING_TEMPLATE,
            ADRESSE_TOSTRING_FIELDS);
    private final boolean editor;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    private CidsBean cidsBean;
    private Integer saveHnr;
    private Integer saveGeom;
    private Date savePflanzung;
    private Date saveUmsetzung;
    @Getter FastBindableReferenceComboCellEditor sorteCellEditor;

    private final ActionListener hnrActionListener = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0)).getList();
                final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                final Object selectedValue = pop.getSelectedValue();
                txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
            }
        };

    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__HNR: {
                        if (evt.getNewValue() != saveHnr) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__GEOREFERENZ: {
                        if (evt.getNewValue() != saveGeom) {
                            setChangeFlag();
                        }
                        setMapWindow();
                        break;
                    }
                    case FIELD__DATUM_U: {
                        if (evt.getNewValue() != saveUmsetzung) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__DATUM_P: {
                        if (evt.getNewValue() != savePflanzung) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__SELBST: {
                        if (Objects.equals(evt.getNewValue(), true)){
                            txtFirma.setEnabled(false);
                            txtFirma.setText("");
                        } else {
                            txtFirma.setEnabled(true);
                        }
                        setChangeFlag();
                    }
                    default: {
                        setChangeFlag();
                    }
                }
            }
        };

    @Getter private final BaumArtLightweightSearch sorteArtSearch;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumLagePanel baumErsatzLagePanel;
    BaumLagePanel baumLagePanel;
    JButton btnAddBaum;
    JButton btnAddKont;
    JButton btnRemBaum;
    JButton btnRemKont;
    JComboBox cbGeomErsatz;
    private FastBindableReferenceCombo cbHNr;
    FastBindableReferenceCombo cbStrasse;
    JCheckBox chDispens;
    JCheckBox chSelbst;
    DefaultBindableDateChooser dcBis;
    DefaultBindableDateChooser dcDatum;
    Box.Filler filler2;
    Box.Filler filler3;
    Box.Filler filler7;
    Box.Filler filler8;
    JPanel jPanelAllgemein;
    JPanel jPanelErsatzpflanzung;
    JScrollPane jScrollPaneBaum;
    JScrollPane jScrollPaneKont;
    JTabbedPane jTabbedPane;
    JLabel lblAnzahl;
    JLabel lblBaum;
    JLabel lblBemerkung;
    JLabel lblBis;
    JLabel lblDatum;
    JLabel lblDispens;
    JLabel lblFirma;
    JLabel lblGeom;
    JLabel lblHNrRenderer;
    JLabel lblHnr;
    JLabel lblKont;
    JLabel lblSelbst;
    JLabel lblStrasse;
    JPanel panBaeume;
    JPanel panBaum;
    JPanel panBaumAdd;
    JPanel panBaumDaten;
    JPanel panDaten;
    JPanel panErsatz;
    JPanel panGeometrie;
    JPanel panGeometrieBaum;
    JPanel panInhalt;
    JPanel panKont;
    JPanel panKontAdd;
    JPanel panKontDaten;
    RoundedPanel rpBaum;
    RoundedPanel rpKont;
    JScrollPane scpBemerkung;
    SemiRoundedPanel semiRoundedPanel8;
    SemiRoundedPanel semiRoundedPanel9;
    JSpinner spAnzahl;
    JTextArea taBemerkungE;
    JTextField txtFirma;
    JXTable xtBaum;
    JXTable xtKont;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumErsatzPanel object.
     */
    public BaumErsatzPanel() {
        this(null);
        this.kontrolleBeans = new ArrayList<>();
        this.baumBeans = new ArrayList<>();
    }

    /**
     * Creates new form BaumErsatzPanel.
     *
     * @param  bclInstance  DOCUMENT ME!
     */
    public BaumErsatzPanel(final BaumChildrenLoader bclInstance) {
        this.kontrolleBeans = new ArrayList<>();
        this.baumBeans = new ArrayList<>();
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null) {
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        this.sorteArtSearch = new BaumArtLightweightSearch(
                "%1$2s",
                new String[] { "NAME" });
        initComponents();
        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbHNr);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddKontActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddKontActionPerformed
        if (getCidsBean() != null) {
            TableUtils.addObjectToTable(xtKont, TABLE_NAME__KONTROLLE, getConnectionContext());
            setChangeFlag();
        }
    }//GEN-LAST:event_btnAddKontActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemKontActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemKontActionPerformed
        if (getCidsBean() != null) {
            TableUtils.removeObjectsFromTable(xtKont);
            setChangeFlag();
        }
    }//GEN-LAST:event_btnRemKontActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrasseActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE) != null)) {
            cbHNr.setSelectedItem(null);
            if (isEditor()) {
                cbHNr.setEnabled(true);
            }
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    private void btnAddBaumActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddBaumActionPerformed
        if (getCidsBean() != null) {
            TableUtils.addObjectToTable(xtBaum, TABLE_NAME__ERSATZBAUM, getConnectionContext());
            setChangeFlag();
            getSorteCellEditor().getComboBox().setEnabled(true);
        }
    }//GEN-LAST:event_btnAddBaumActionPerformed

    private void btnRemBaumActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemBaumActionPerformed
        if (getCidsBean() != null) {
            TableUtils.removeObjectsFromTable(xtBaum);
            setChangeFlag();
        }
    }//GEN-LAST:event_btnRemBaumActionPerformed
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
            TABLE__NAME,
            1,
            800,
            600);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isEditor() {
        return this.editor;
    }

    
    private void refreshSorteColumn(Integer artId) {
        if (getCidsBean() != null) {
            getSorteArtSearch().setArtId(artId);
            final SwingWorker<Void, Void> worker = 
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        getSorteCellEditor().getComboBox().refreshModel();
                        return null;
                    }
            };
            if (worker_sorte != null) {
                worker_sorte.cancel(true);
            }
            worker_sorte = worker;
            worker_sorte.execute();       
            }
        
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            cbStrasse.setEnabled(false);
            RendererTools.makeReadOnly(dcBis);
            RendererTools.makeReadOnly(dcDatum);
            RendererTools.makeReadOnly(chDispens);
            RendererTools.makeReadOnly(chSelbst);
            RendererTools.makeDoubleSpinnerWithoutButtons(spAnzahl, 0);
            RendererTools.makeReadOnly(spAnzahl);
            RendererTools.makeReadOnly(txtFirma);
            RendererTools.makeReadOnly(taBemerkungE);
            RendererTools.makeReadOnly(xtKont);
            RendererTools.makeReadOnly(xtBaum);
            panBaumAdd.setVisible(false);
            panKontAdd.setVisible(isEditor());
            lblGeom.setVisible(isEditor());
            xtKont.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        if ((getBaumChildrenLoader() != null)
                    && (getBaumChildrenLoader().getParentOrganizer() != null)
                    && (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
            ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    @Override
    public void dispose() {
        baumLagePanel.dispose();
        baumErsatzLagePanel.dispose();
        cidsBean = null;
        if (isEditor() && (cbGeomErsatz != null)) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).setCidsMetaObject(null);
            cbGeomErsatz = null;
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveHnr = (getCidsBean().getProperty(FIELD__HNR) != null)
            ? ((CidsBean)getCidsBean().getProperty(FIELD__HNR)).getPrimaryKeyValue() : null;
        saveGeom = (getCidsBean().getProperty(FIELD__GEOREFERENZ) != null)
            ? ((CidsBean)getCidsBean().getProperty(FIELD__GEOREFERENZ)).getPrimaryKeyValue() : null;
        saveUmsetzung = (getCidsBean().getProperty(FIELD__DATUM_U) != null)
            ? (Date)getCidsBean().getProperty(FIELD__DATUM_U) : null;
        savePflanzung = (getCidsBean().getProperty(FIELD__DATUM_P) != null)
            ? (Date)getCidsBean().getProperty(FIELD__DATUM_P) : null;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))) {
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            if (isEditor()) {
                cbHNr.removeActionListener(hnrActionListener);
            }
            try {
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if ((getCidsBean() != null) && isEditor()) {
                    setSaveValues();
                }
                if (getCidsBean() != null) {
                    setKontrolleBeans(getCidsBean().getBeanCollectionProperty(FIELD__KONTROLLE));
                    setBaumBeans(getCidsBean().getBeanCollectionProperty(FIELD__BAUM));
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
                    // Wenn mit mehreren Geoms(Liste) gearbeitet wird
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).setCidsMetaObject(getCidsBean()
                                    .getMetaObject());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).initForNewBinding();
                    }
                } else {
                    setKontrolleBeans(null);
                    setBaumBeans(null);
                    cbHNr.setSelectedIndex(-1);
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).initForNewBinding();
                        cbGeomErsatz.setSelectedIndex(-1);
                    }
                }
                if (getCidsBean().getProperty(FIELD__ANZAHL) == null){
                    getCidsBean().setProperty(FIELD__ANZAHL, 0);
                }
                setMapWindow();
                bindingGroup.bind();
                final DivBeanTable kontrolleModel = new DivBeanTable(
                        isEditor(),
                        getCidsBean(),
                        FIELD__KONTROLLE,
                        KONTROLLE_COL_NAMES,
                        KONTROLLE_PROP_NAMES,
                        KONTROLLE_PROP_TYPES);
                xtKont.setModel(kontrolleModel);
                xtKont.getColumn(0).setCellEditor(new DateCellEditor());
                xtKont.getColumn(0).setMaxWidth(80);
                xtKont.getColumn(0).setMinWidth(80);
                xtKont.packAll();
                xtKont.addMouseMotionListener(new MouseAdapter() {

                        @Override
                        public void mouseMoved(final MouseEvent e) {
                            final int row = xtKont.rowAtPoint(e.getPoint());
                            final int col = xtKont.columnAtPoint(e.getPoint());
                            if ((row > -1) && (col > -1)) {
                                final Object value = xtKont.getValueAt(row, col);
                                if ((null != value) && !"".equals(value)) {
                                    xtKont.setToolTipText(value.toString());
                                } else {
                                    xtKont.setToolTipText(null); // keinTooltip anzeigen
                                }
                            }
                        }
                    });
                final DivBeanTable baumModel = new DivBeanTable(
                        isEditor(),
                        getCidsBean(),
                        FIELD__BAUM,
                        BAUM_COL_NAMES,
                        BAUM_PROP_NAMES,
                        BAUM_PROP_TYPES);
                
                xtBaum.setModel(baumModel);
                //Auswahlliste Art
                xtBaum.getColumn(1).setCellEditor(new DefaultBindableComboboxCellEditor(MC__ART));
                //Geometrie Auswahl Dialog
                xtBaum.getColumn(0).setCellEditor(new DefaultCismapGeometryComboBoxCellEditor());
                //Labeling mit Koordinate, ....
                xtBaum.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(final JTable table,
                            final Object value,
                            final boolean isSelected,
                            final boolean hasFocus,
                            final int row,
                            final int column) {
                        final Component component = super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column);
                        if (value != null) {
                            NumberFormat formatter = NumberFormat.getInstance();
                            formatter.setMaximumFractionDigits(2);
                            Object oGeom = xtBaum.getValueAt(row, column);
                            if (oGeom instanceof CidsBean){
                                final Object geoObj = ((CidsBean) oGeom).getProperty(FIELD__GEO_FIELD);
                                if (geoObj instanceof Geometry) {
                                    if (((Geometry) geoObj).getGeometryType().equals(GEOM_POINT)){
                                        String stringGeom = (String.valueOf(formatter.format(
                                                    ((Geometry) geoObj).getCoordinate().x)) 
                                                + "  "
                                                + String.valueOf(formatter.format(
                                                    ((Geometry) geoObj).getCoordinate().y)))
                                                .replaceAll("\\.", "");
                                        ((JLabel)component).setText(stringGeom);
                                    } else {
                                        ((JLabel)component).setText("kein Punkt");
                                    }
                                } else {
                                    ((JLabel)component).setText("kein Punkt");
                                }
                            }
                        } else {
                            ((JLabel)component).setText("Bitte einen Punkt auswählen...");
                        }

                        return component;
                    }
                });
                //Was soll mit einer geaenderten Zelle passieren
                Action action = new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        TableCellListener tcl = (TableCellListener)e.getSource();
                        System.out.println("Row   : " + tcl.getRow());
                        System.out.println("Column: " + tcl.getColumn());
                        System.out.println("Old   : " + tcl.getOldValue());
                        System.out.println("New   : " + tcl.getNewValue());
                        switch (tcl.getColumn()) {
                                case 0:
                                    Object oGeom = tcl.getNewValue(); 
                                    if (oGeom instanceof CidsBean){
                                        setErsatzMapWindow((CidsBean)oGeom);
                                        checkInsideArea();
                                    }
                                    break;
                                case 1:
                                    Object oBean = tcl.getNewValue();
                                    if (oBean != null && oBean instanceof CidsBean){
                                        if (tcl.getNewValue() != tcl.getOldValue() ){
                                            //Value Sorte null
                                            xtBaum.setValueAt(null, tcl.getRow(), 2);
                                            //Search
                                            refreshSorteColumn(((CidsBean)oBean).getPrimaryKeyValue());
                                            if (isEditor()){
                                                getSorteCellEditor().getComboBox().setEnabled(true);
                                            }
                                        }  
                                    } else {
                                        getSorteCellEditor().getComboBox().setEnabled(false);
                                    }
                                    break;
                        }//switch
                    }
                };

                TableCellListener tcl = new TableCellListener(xtBaum, action);
                //Wenn sich die Selektion einer Zeile aendert
                xtBaum.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        if (xtBaum.getSelectedRow() == -1){
                            setErsatzMapWindow(null);
                            getSorteCellEditor().getComboBox().setEnabled(false);
                        } else {
                            Object oGeom = xtBaum.getValueAt(xtBaum.getSelectedRow(), 0);
                            if (oGeom instanceof CidsBean){
                                setErsatzMapWindow((CidsBean)oGeom);
                            } else {
                                setErsatzMapWindow(null);
                            }
                            Object artBean = xtBaum.getValueAt(xtBaum.getSelectedRow(), 1);
                            if(artBean != null){
                                refreshSorteColumn(((CidsBean) artBean).getPrimaryKeyValue());
                                if (isEditor()){
                                    getSorteCellEditor().getComboBox().setEnabled(true);
                                }
                            } else {
                                getSorteCellEditor().getComboBox().setEnabled(false);
                            } 
                        }
                        
                    }
                });
                //Auswahlliste mit Hilfe der Art fuer Sorte
                xtBaum.getColumn(2).setCellEditor(new FastBindableReferenceComboCellEditor(
                        getSorteArtSearch(), 
                        getSorteArtSearch().getRepresentationPattern(),
                        getSorteArtSearch().getRepresentationFields()));
                sorteCellEditor = (FastBindableReferenceComboCellEditor)xtBaum.getColumn(2).getCellEditor();
                getSorteCellEditor().getComboBox().setMetaClass(MC__SORTE);
                
                xtBaum.getColumn(0).setMinWidth(80);
                xtBaum.packAll();
                //Tooltip
                xtBaum.addMouseMotionListener(new MouseAdapter() {

                        @Override
                        public void mouseMoved(final MouseEvent e) {
                            final int row = xtBaum.rowAtPoint(e.getPoint());
                            final int col = xtBaum.columnAtPoint(e.getPoint());
                            if ((row > -1) && (col > -1)) {
                                final Object value = xtBaum.getValueAt(row, col);
                                if ((null != value) && !"".equals(value)) {
                                    xtBaum.setToolTipText(value.toString());
                                } else {
                                    xtBaum.setToolTipText(null); // keinTooltip anzeigen
                                }
                            }
                        }
                });
               
                
                if (isEditor()) {
                    cbGeomErsatz.updateUI();
                }
                if (isEditor() && (getCidsBean() != null)) {
                    getCidsBean().addPropertyChangeListener(changeListener);
                }

                if (isEditor()) {
                    if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE) != null)) {
                        cbHNr.setEnabled(true);
                    } else {
                        cbHNr.setEnabled(false);
                    }
                    cbHNr.addActionListener(hnrActionListener);
                    refreshHnr();
                }
            } catch (final Exception ex) {
                LOG.warn("problem in setCidsBean.", ex);
            }
        }
        setReadOnly();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
        if(baumBeans.size() > 0){
            xtBaum.setRowSelectionInterval(0, 0);
            if(isEditor()){
                getSorteCellEditor().getComboBox().setEnabled(true);
                checkInsideArea();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        cbGeomErsatz.setEnabled(edit);
        cbStrasse.setEnabled(edit);
        dcBis.setEnabled(edit);
        dcDatum.setEnabled(edit);
        chDispens.setEnabled(edit);
        chSelbst.setEnabled(edit);
        spAnzahl.setEnabled(edit);
        txtFirma.setEnabled(edit);
        taBemerkungE.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshHnr() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE) != null)) {
            final String schluessel = getCidsBean().getProperty(FIELD__STRASSE).toString();
            if (schluessel != null) {
                hnrSearch.setKeyId(Integer.parseInt(schluessel.replaceFirst("0*", "")));

                hnrSearch.setKeyId(Integer.parseInt(schluessel));
                initComboboxHnr();
            }
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param   midpoint  DOCUMENT ME!
     * @param   geomArea  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean checkPointInsidePolygon(final Point midpoint, final Geometry geomArea) {
        return midpoint.intersects(geomArea);
    }
    
    private void checkInsideArea() {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    if(getCidsBean().getProperty(FIELD__GEOM) != null){
                        for (final CidsBean laufBean : baumBeans) {
                            CidsBean geomBean = (CidsBean)laufBean.getProperty(FIELD__GEOM);
                            if (geomBean == null
                                    || !(((Geometry) geomBean.getProperty(FIELD__GEO_FIELD))
                                            .getGeometryType().equals(GEOM_POINT) )) {
                                return false;
                            } else {
                                if ( ! checkPointInsidePolygon(
                                            (Point)geomBean.getProperty(FIELD__GEO_FIELD),
                                            (Geometry)getCidsBean().getProperty(FIELD__GEOREFERENZ__GEO_FIELD))) {
                                    NumberFormat formatter = NumberFormat.getInstance();
                                    formatter.setMaximumFractionDigits(2);
                                    outsidePoint = " Punkt: "
                                            + (String.valueOf(formatter.format(
                                                    ((Geometry) geomBean.getProperty(FIELD__GEO_FIELD))
                                                            .getCoordinate().x)) 
                                                + "  "
                                                + String.valueOf(formatter.format(
                                                    ((Geometry) geomBean.getProperty(FIELD__GEO_FIELD))
                                                            .getCoordinate().y)))
                                                .replaceAll("\\.", "");
                                    return false;
                                }
                            }
                        }
                    } else{
                        return false;
                    }
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        if (!isCancelled()) {
                            insideBox = get();  
                        }     
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: check InsideArea.", e);
                    }         // catch
                }             // done
        };                // worker

        if (worker_area != null) {
            worker_area.cancel(true);
        }
        worker_area = worker;
        outsidePoint = "";
        worker_area.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void setMapWindow() {
        baumLagePanel.setMapWindow(getCidsBean(), getConnectionContext());
    }
    
    /**
     * DOCUMENT ME!
     */
    private void setErsatzMapWindow(CidsBean ersatzBean) {
        baumErsatzLagePanel.setMapWindow(ersatzBean, getConnectionContext());
    }
    /**
     * DOCUMENT ME!
     */
    private void initComboboxHnr() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    cbHNr.refreshModel();
                    return null;
                }

                @Override
                protected void done() {
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   saveErsatzBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkForSaving(final CidsBean saveErsatzBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        final Collection<CidsBean> baumCollection = saveErsatzBean.getBeanCollectionProperty(FIELD__BAUM);               
        final Date jetztDatum = new java.sql.Date(System.currentTimeMillis());
        final boolean isSetStrasse = saveErsatzBean.getProperty(FIELD__STRASSE) != null;
        final boolean isSetHNr = saveErsatzBean.getProperty(FIELD__HNR) != null;
        final boolean isSetGeom = saveErsatzBean.getProperty(FIELD__GEOM) != null;
        final boolean isSetBis = saveErsatzBean.getProperty(FIELD__DATUM_U) != null;
        final boolean isSetFirma = saveErsatzBean.getProperty(FIELD__FIRMA) != null 
                && ! saveErsatzBean.getProperty(FIELD__FIRMA).toString().isEmpty();
        final boolean isSetSelbst = Objects.equals(saveErsatzBean.getProperty(FIELD__SELBST), true);
        final boolean isSetPflanzung = saveErsatzBean.getProperty(FIELD__DATUM_P) != null;
        final boolean isSetAnzahl = (saveErsatzBean.getProperty(FIELD__ANZAHL) != null)
                    && ((Integer)saveErsatzBean.getProperty(FIELD__ANZAHL) > 0);
        final boolean isSetBaum = baumCollection.size() > 0;
        final Integer iAnz = (Integer)saveErsatzBean.getProperty(FIELD__ANZAHL);

        if (isSetStrasse || isSetHNr || isSetGeom || isSetFirma || isSetSelbst || isSetPflanzung || isSetBaum) {
            // Straße muss angegeben werden
            try {
                if (! isSetStrasse) {
                    LOG.warn("No strasse specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOSTREET));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("strasse not given.", ex);
                save = false;
            }

            // georeferenz muss gefüllt sein
            try {
                if (! isSetGeom) {
                    LOG.warn("No geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOGEOM));
                    save = false;
                } else {
                    final CidsBean geom_pos = (CidsBean)saveErsatzBean.getProperty(FIELD__GEOM);
                    if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                        LOG.warn("Wrong geom specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WRONGGEOM));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Geom not given.", ex);
                save = false;
            }
            // Pflanzung muss angegeben werden
            try {
                if (! isSetPflanzung) {
                    LOG.warn("No pflanzung specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NODATE));
                    save = false;
                } else {
                    final Object plantDate = saveErsatzBean.getProperty(FIELD__DATUM_P);
                    if (plantDate instanceof Date) {
                        if (((Date)plantDate).after(jetztDatum)) {
                            LOG.warn("Wrong plandatum specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_FUTUREDATEPLANT));
                            save = false;
                        }
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("bis not given.", ex);
                save = false;
            }
            //Ersatzbaeume muessen angegeben werden
            try {
                if (! isSetBaum) {
                    LOG.warn("No ersatzbaum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOBAUM));
                    save = false;
                } else {
                    if (iAnz > 0 && baumCollection.size() != iAnz ){
                        LOG.warn("Wrong Anzahl ersatzbaum specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WRONGANZAHLBAUM));
                        save = false;
                    }
                    // Ersatzbaeume
                    try {
                        for (final CidsBean baumBean : baumCollection) {
                            // Geom
                            try {
                                if (baumBean.getProperty(FIELD__GEOM) == null) {
                                    LOG.warn("No geom baum specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOGEOMBAUM));
                                    save = false;
                                } else {
                                    final CidsBean geom_pos = (CidsBean)baumBean.getProperty(FIELD__GEOM);
                                    if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOM_POINT)) {
                                        LOG.warn("Wrong geom baum specified. Skip persisting.");
                                        errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WRONGGEOMBAUM));
                                        save = false;
                                    }
                                }
                            } catch (final MissingResourceException ex) {
                                LOG.warn("Geom  baum not given.", ex);
                                save = false;
                            }
                            // Art vorhanden
                            if (baumBean.getProperty(FIELD__ART) == null) {
                                LOG.warn("No art specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOART));
                                save = false;
                            }
                        }
                    } catch (final MissingResourceException ex) {
                        LOG.warn("ersatzbaum not correct.", ex);
                        save = false;
                    }
                    if (!(insideBox)) {
                        LOG.warn("Wrong Geometry Location. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, 
                                BUNDLE_LOCATION_GEOMETRY))
                                .append(outsidePoint);
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("ersatz not given.", ex);
                save = false;
            }
        } else {
            // Wenn keine Pflanzung, bis wann?
             if (! isSetBis) {
                LOG.warn("No bis specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOBIS));
                save = false;
            }
        }

        // Anzahl 
        try {
            if (! isSetAnzahl) {
                LOG.warn("No count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCOUNT));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Count not given.", ex);
            save = false;
        }


        // Kontrolle
        try {
            final Collection<CidsBean> controlCollection = saveErsatzBean.getBeanCollectionProperty(FIELD__KONTROLLE);
            for (final CidsBean controlBean : controlCollection) {
                // Datum vorhanden
                if (controlBean.getProperty(FIELD__KONTROLLE_DATUM) == null) {
                    LOG.warn("No kontolldatum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCONTROLDATE));
                    save = false;
                } else {
                    // Datum nicht in der Zukunft
                    final Object controllDate = controlBean.getProperty(FIELD__KONTROLLE_DATUM);
                    if (controllDate instanceof Date) {
                        if (((Date)controllDate).after(jetztDatum)) {
                            LOG.warn("Wrong kontolldatum specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_FUTUREDATE));
                            save = false;
                        }
                    }
                }
                // Bemerkung vorhanden
                if (controlBean.getProperty(FIELD__KONTROLLE_BEMERKUNG) == null) {
                    LOG.warn("No bemerkung specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCONTROLTEXT));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Kontrolle not correct.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            if (baumChildrenLoader.getParentOrganizer() instanceof BaumSchadenEditor) {
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WHICH))
                        .append(saveErsatzBean.getPrimaryKeyValue());
            } else {
                if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor) {
                    final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WHICH))
                            .append(saveErsatzBean.getPrimaryKeyValue());
                    final CidsBean schadenBean = (CidsBean)saveErsatzBean.getProperty(FIELD__FK_SCHADEN);
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_FAULT))
                            .append(schadenBean.getPrimaryKeyValue());
                    final CidsBean meldungBean = (CidsBean)schadenBean.getProperty(FIELD__FK_MELDUNG);
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_MESSAGE))
                            .append(formatTag.format(meldungBean.getProperty(FIELD__MDATUM)));
                }
            }
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......" });
        }
    }
    
    public class TableCellListener implements 
            PropertyChangeListener, 
            Runnable{
	@Getter private final JTable table;
	private Action action;

	@Getter private int row;
	@Getter private int column;
	@Getter private Object oldValue;
	@Getter private Object newValue;

	/**
	 *  Create a TableCellListener.
	 *
	 *  @param table   welche Tabelle beobachtet wird
	 *  @param action  was passiert, wenn Daten gendert werden
	 */
	public TableCellListener(JTable table, Action action)	{
            this.table = table;
            this.action = action;
            this.table.addPropertyChangeListener(this);
	}

	/**
	 *  Create a TableCellListener mit einer Kopie aller relevanten Daten
	 *
	 *  @param row  row der Zelle
	 *  @param column  column der Zelle
	 *  @param oldValue  vorher in der Zelle
	 *  @param newValue  jetzt in der Zelle
	 */
	private TableCellListener(
                JTable table, 
                int row, int column, 
                Object oldValue, 
                Object newValue) {
            this.table = table;
            this.row = row;
            this.column = column;
            this.oldValue = oldValue;
            this.newValue = newValue;
	}

        //
        //  
        //
	@Override
	public void propertyChange(PropertyChangeEvent e){
            if ("tableCellEditor".equals(e.getPropertyName())){
                if (table.isEditing()){
                    processEditingStarted();
                }else{
                    processEditingStopped();
                }
            }
	}

	/*
	 *  speichern der information 
	 */
	private void processEditingStarted(){
            //  Beim PropertyChangeEvent (fired), sind row und column nicht gesetzt
            SwingUtilities.invokeLater( this );
	}
        
	@Override
	public void run(){
            row = table.convertRowIndexToModel( table.getEditingRow() );
            column = table.convertColumnIndexToModel( table.getEditingColumn() );
            oldValue = table.getModel().getValueAt(row, column);
            newValue = null;
	}

	/*
	 *	Update  history 
	 */
	private void processEditingStopped(){
            newValue = table.getModel().getValueAt(row, column);
            if ((newValue != null) && (! newValue.equals(oldValue))){
                TableCellListener tcl = new TableCellListener(
                        getTable(), getRow(), getColumn(), getOldValue(), getNewValue());

                ActionEvent event = new ActionEvent(
                        tcl,
                        ActionEvent.ACTION_PERFORMED,
                        "");
                action.actionPerformed(event);
            }
	}
    }

}
