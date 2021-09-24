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
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BaumArtLightweightSearch;
import org.apache.log4j.Logger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.ComboPopup;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumErsatzPanel extends javax.swing.JPanel implements Disposable, 
        CidsBeanStore, 
        ConnectionContextProvider {
    @Getter @Setter private List<CidsBean> kontrolleBeans;;
//~ Static fields/initializers ---------------------------------------------
    private static final Logger LOG = Logger.getLogger(BaumErsatzPanel.class);
    private static final MetaClass MC__ART;

    public static final String GEOMTYPE = "Polygon";
    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumErsatzPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
    }
    
    private final AdresseLightweightSearch hnrSearch = new AdresseLightweightSearch(
            AdresseLightweightSearch.Subject.HNR,
            ADRESSE_TOSTRING_TEMPLATE,
            ADRESSE_TOSTRING_FIELDS);
    
    public static final String FIELD__KONTROLLE = "n_kontrolle";                // baum_ersatz
    public static final String FIELD__DATE = "datum";                           // baum_kontrolle
    public static final String FIELD__STRASSE = "fk_strasse.strassenschluessel";// baum_ersatz
    public static final String FIELD__HNR = "fk_adresse";                       // baum_ersatz
    public static final String FIELD__DATUM_P = "pflanzdatum";                  // baum_ersatz
    public static final String FIELD__DATUM_U = "umsetzung_bis";                // baum_ersatz
    public static final String FIELD__ART = "fk_art";                           // baum_ersatz 
    public static final String FIELD__SORTE = "fk_sorte";                           // baum_ersatz 
    public static final String FIELD__ART_ID = "fk_art.id";                     // baum_ersatz --> art
    public static final String FIELD__GEOM = "fk_geom";                         // baum_ersatz
    public static final String FIELD__SELBST = "selbststaendig";                // baum_ersatz
    public static final String FIELD__BIS = "umsetzung_bis";                    // baum_ersatz
    public static final String FIELD__ANZAHL = "anzahl";                        // baum_ersatz
    public static final String FIELD__FIRMA = "firma";                          // baum_ersatz
    public static final String FIELD__BEMERKUNG = "bemerkung";                  // baum_ersatz
    public static final String FIELD__KONTROLLE_BEMERKUNG = "bemerkung";        // baum_ersatz
    public static final String FIELD__KONTROLLE_DATUM = "datum";                // baum_ersatz
    public static final String FIELD__GEOREFERENZ = "fk_geom";                  // baum_ersatz
    public static final String FIELD__STRASSE_NAME = "name";                    // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";       // strasse
    public static final String FIELD__FK_SCHADEN= "fk_schaden";                 // baum_ersatz
    public static final String FIELD__FK_MELDUNG= "fk_meldung";                 // baum_schaden
    public static final String FIELD__MDATUM= "datum";                          // baum_meldung
    
    public static final String FIELD__GEO_FIELD = "geo_field";                  // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_ersatz_geom
    
    public static final String TABLE__NAME = "baum_ersatz";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_SORTE = "baum_sorte";
    public static final String TABLE_NAME__KONTROLLE = "baum_kontrolle";
    
    public static final String BUNDLE_NOSTREET = 
            "BaumErsatzPanel.isOkForSaving().noStrasse";
    public static final String BUNDLE_NOGEOM = 
            "BaumErsatzPanel.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = 
            "BaumErsatzPanel.isOkForSaving().wrongGeom";
    public static final String BUNDLE_NOART = 
            "BaumErsatzPanel.isOkForSaving().noArt";
    public static final String BUNDLE_WRONGCOUNT = 
            "BaumErsatzPanel.isOkForSaving().wrongAnzahl";
    public static final String BUNDLE_NOCOUNT = 
            "BaumErsatzPanel.isOkForSaving().noAnzahl";
    public static final String BUNDLE_NOCONTROLDATE = 
            "BaumErsatzPanel.isOkForSaving().noKontrolleDatum";
    public static final String BUNDLE_FUTUREDATE = 
            "BaumErsatzPanel.isOkForSaving().zukunftsDatum";
    public static final String BUNDLE_NOCONTROLTEXT = 
            "BaumErsatzPanel.isOkForSaving().noControlText";
    public static final String BUNDLE_WHICH = 
            "BaumErsatzPanel.isOkForSaving().welcheErsatz";
    public static final String BUNDLE_FAULT = 
            "BaumErsatzPanel.isOkForSaving().welcherSchaden";
    public static final String BUNDLE_MESSAGE = 
            "BaumErsatzPanel.isOkForSaving().welcheMeldung";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumErsatzPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumErsatzPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumErsatzPanel.isOkForSaving().JOptionPane.title";
    
    private static final String[] KONTROLLE_COL_NAMES = new String[] { "Datum", "Bemerkung"};
    private static final String[] KONTROLLE_PROP_NAMES = new String[] {
            FIELD__KONTROLLE_DATUM,
            FIELD__KONTROLLE_BEMERKUNG
        };
    private static final Class[] KONTROLLE_PROP_TYPES = new Class[] {
            Date.class,
            String.class
        };
    
    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = {
            AdresseLightweightSearch.Subject.HNR.toString()
        };
    
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
        lblArt = new JLabel();
        cbArtE = new DefaultBindableScrollableComboBox(MC__ART);
        //new de.cismet.cids.editors.DefaultBindableReferenceCombo(MC__ART);
        ;
        lblSorte = new JLabel();
        cbSorte = new FastBindableReferenceCombo(
            sorteArtSearch,
            sorteArtSearch.getRepresentationPattern(),
            sorteArtSearch.getRepresentationFields()
        );
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

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panDaten.setName("panDaten"); // NOI18N
        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

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
            lblHNrRenderer.setName("lblHNrRenderer"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse.hausnummer}"), lblHNrRenderer, BeanProperty.create("text"));
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

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblArt, gridBagConstraints);

        cbArtE.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArtE.setMaximumRowCount(15);
        cbArtE.setToolTipText(toString());
        cbArtE.setAutoscrolls(true);
        cbArtE.setName("cbArtE"); // NOI18N
        cbArtE.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_art}"), cbArtE, BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cbArtE.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(cbArtE, gridBagConstraints);

        lblSorte.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSorte, "Sorte:");
        lblSorte.setName("lblSorte"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInhalt.add(lblSorte, gridBagConstraints);

        cbSorte.setMaximumRowCount(12);
        cbSorte.setEnabled(false);
        cbSorte.setName("cbSorte"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_sorte}"), cbSorte, BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInhalt.add(cbSorte, gridBagConstraints);

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
        spAnzahl.setName("spAnzahl"); // NOI18N
        spAnzahl.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl}"), spAnzahl, BeanProperty.create("value"));
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
        panDaten.add(panErsatz, gridBagConstraints);

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
        panDaten.add(panKont, gridBagConstraints);

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
            if (evt.getSource() == cbArtE) {
                BaumErsatzPanel.this.cbArtEActionPerformed(evt);
            }
            else if (evt.getSource() == cbStrasse) {
                BaumErsatzPanel.this.cbStrasseActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddKont) {
                BaumErsatzPanel.this.btnAddKontActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemKont) {
                BaumErsatzPanel.this.btnRemKontActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddKontActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddKontActionPerformed
        TableUtils.addObjectToTable(xtKont, TABLE_NAME__KONTROLLE, getConnectionContext());
        setChangeFlag();
    }//GEN-LAST:event_btnAddKontActionPerformed

    private void btnRemKontActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemKontActionPerformed
        TableUtils.removeObjectsFromTable(xtKont);
        setChangeFlag();
    }//GEN-LAST:event_btnRemKontActionPerformed

    private void cbArtEActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbArtEActionPerformed
        if (getCidsBean() != null && getCidsBean().getProperty(FIELD__ART) != null){
            cbSorte.setSelectedItem(null);
            cbSorte.setEnabled(true);
            refreshSorte();
        }
    }//GEN-LAST:event_cbArtEActionPerformed

    private void cbStrasseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && getCidsBean()!= null && getCidsBean().getProperty(FIELD__STRASSE) != null){
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean editor;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    private CidsBean cidsBean;
    private Integer saveHnr;
    private Integer saveGeom;
    private Integer saveArt;
    private Integer saveSorte;
    private Date savePflanzung;
    private Date saveUmsetzung;
    
    
    private final ActionListener hnrActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0))
                                    .getList();
                        final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                final Object selectedValue = pop.getSelectedValue();
                txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
            }
    };
    
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__HNR:
                        if (evt.getNewValue() != saveHnr){
                            setChangeFlag();
                        }
                        break;
                    case FIELD__ART:
                        if (evt.getNewValue() != saveArt){
                            setChangeFlag();
                        }
                        break;
                    case FIELD__SORTE:
                        if (evt.getNewValue() != saveSorte){
                            setChangeFlag();
                        }
                        break;
                    case FIELD__GEOREFERENZ:
                        if (evt.getNewValue() != saveGeom){
                            setChangeFlag();
                        }
                        setMapWindow();
                        break;
                    case FIELD__DATUM_U:
                        if (evt.getNewValue() != saveUmsetzung){
                            setChangeFlag();
                        }
                        break;
                    case FIELD__DATUM_P:
                        if (evt.getNewValue() != savePflanzung){
                            setChangeFlag();
                        }
                        break;
                    default:
                        setChangeFlag();
                }
            }
        };
    private final BaumArtLightweightSearch sorteArtSearch;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumLagePanel baumLagePanel;
    JButton btnAddKont;
    JButton btnRemKont;
    JComboBox<String> cbArtE;
    JComboBox cbGeomErsatz;
    private FastBindableReferenceCombo cbHNr;
    FastBindableReferenceCombo cbSorte;
    FastBindableReferenceCombo cbStrasse;
    JCheckBox chDispens;
    JCheckBox chSelbst;
    DefaultBindableDateChooser dcBis;
    DefaultBindableDateChooser dcDatum;
    Box.Filler filler2;
    JScrollPane jScrollPaneKont;
    JLabel lblAnzahl;
    JLabel lblArt;
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
    JLabel lblSorte;
    JLabel lblStrasse;
    JPanel panDaten;
    JPanel panErsatz;
    JPanel panGeometrie;
    JPanel panInhalt;
    JPanel panKont;
    JPanel panKontAdd;
    JPanel panKontDaten;
    RoundedPanel rpKont;
    JScrollPane scpBemerkung;
    SemiRoundedPanel semiRoundedPanel8;
    JSpinner spAnzahl;
    JTextArea taBemerkungE;
    JTextField txtFirma;
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
    }

    
    /**
     * Creates new form BaumErsatzPanel.
     *
     * @param bclInstance
     */
    public BaumErsatzPanel(final BaumChildrenLoader bclInstance) {
        this.kontrolleBeans = new ArrayList<>();
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null){
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
            //cbHNr.addActionListener(hnrActionListener);
        }
    }

    //~ Methods ----------------------------------------------------------------
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
    
    private boolean isEditor(){
        return this.editor;
    }
    
    private void refreshSorte() {
        if (getCidsBean() != null){
            sorteArtSearch.setArtId((Integer)getCidsBean().getProperty(FIELD__ART_ID));

            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        cbSorte.refreshModel();
                        return null;
                    }
            }.execute();
        }
    }
    
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(cbStrasse);
            RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(cbGeomErsatz);
            RendererTools.makeReadOnly(dcBis);
            RendererTools.makeReadOnly(dcDatum);
            RendererTools.makeReadOnly(chDispens);
            RendererTools.makeReadOnly(chSelbst);
            RendererTools.makeReadOnly(cbArtE);
            RendererTools.makeReadOnly(cbSorte);
            RendererTools.makeReadOnly(spAnzahl);
            RendererTools.makeReadOnly(txtFirma);
            RendererTools.makeReadOnly(taBemerkungE);
            RendererTools.makeReadOnly(xtKont);
            panKontAdd.setVisible(isEditor());
            lblGeom.setVisible(isEditor());
        }
    }
    
    private void setChangeFlag(){
        if ((getBaumChildrenLoader() != null) && 
                (getBaumChildrenLoader().getParentOrganizer() != null) &&
                (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }
    
    @Override
    public ConnectionContext getConnectionContext() {
        return baumChildrenLoader != null && baumChildrenLoader.getParentOrganizer() != null 
                ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }
        
    @Override
    public void dispose() {
        baumLagePanel.dispose();
        cidsBean = null;
        if (isEditor() && cbGeomErsatz != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).setCidsMetaObject(null);
            cbGeomErsatz = null;
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }
    
    private void setSaveValues(){
        saveHnr = getCidsBean().getProperty(FIELD__HNR) != null
                ? ((CidsBean) getCidsBean().getProperty(FIELD__HNR)).getPrimaryKeyValue()
                : null;
        saveArt = getCidsBean().getProperty(FIELD__ART) != null
                ? ((CidsBean) getCidsBean().getProperty(FIELD__ART)).getPrimaryKeyValue()
                : null;
        saveSorte = getCidsBean().getProperty(FIELD__SORTE) != null
                ? ((CidsBean) getCidsBean().getProperty(FIELD__SORTE)).getPrimaryKeyValue()
                : null;
        saveGeom = getCidsBean().getProperty(FIELD__GEOREFERENZ) != null
                ? ((CidsBean) getCidsBean().getProperty(FIELD__GEOREFERENZ)).getPrimaryKeyValue()
                : null;
        saveUmsetzung = getCidsBean().getProperty(FIELD__DATUM_U) != null
                ? (Date) getCidsBean().getProperty(FIELD__DATUM_U)
                : null;
        savePflanzung = getCidsBean().getProperty(FIELD__DATUM_P) != null
                ? (Date) getCidsBean().getProperty(FIELD__DATUM_P)
                : null;
    }
   
    @Override
    public void setCidsBean(CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))){
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            if (isEditor()){
                for(final ActionListener hnrListener:cbHNr.getActionListeners()){
                    if(hnrListener == hnrActionListener){
                        cbHNr.removeActionListener(hnrListener);
                        break;
                    }
                }
            }
            try{
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if (getCidsBean() != null  && isEditor()){
                    setSaveValues();
                }
                if (getCidsBean() != null){
                    setKontrolleBeans(getCidsBean().getBeanCollectionProperty(FIELD__KONTROLLE)); 
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
                    //Wenn mit mehreren Geoms(Liste) gearbeitet wird
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).setCidsMetaObject(getCidsBean().getMetaObject());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).initForNewBinding();
                    }
                
                } else{
                    setKontrolleBeans(null);
                    cbSorte.setEnabled(false);
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomErsatz).initForNewBinding();
                        cbGeomErsatz.setSelectedIndex(-1);
                    }
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
                    //xtKont.getColumnModel().getColumn(0).setCellRenderer(xtKont.getDefaultRenderer(String.class));
                    xtKont.getColumn(0).setMaxWidth(80);
                    xtKont.getColumn(0).setMinWidth(80);
                    //Integer bemWidth = panGeometrie.getSize().width - panErsatz.getSize().width - 30;
                    //xtKont.getColumn(1).setMaxWidth(340);
                    //xtKont.getColumn(1).setPreferredWidth(300);
                    xtKont.packAll();
                    xtKont.addMouseMotionListener(new MouseAdapter(){
                        @Override
                        public void mouseMoved(MouseEvent e) {
                            int row=xtKont.rowAtPoint(e.getPoint());
                            int col=xtKont.columnAtPoint(e.getPoint());
                            if(row>-1 && col>-1){
                                Object value=xtKont.getValueAt(row, col);
                                if(null!=value && !"".equals(value)){
                                    xtKont.setToolTipText(value.toString());
                                }else{
                                    xtKont.setToolTipText(null);//keinTooltip anzeigen
                                }
                            }
                        }
                    });
                if(isEditor()){
                    cbGeomErsatz.updateUI();
                }
                cbSorte.updateUI();
                if (isEditor() && (getCidsBean() != null)) {
                    getCidsBean().addPropertyChangeListener(changeListener);
                    if(getCidsBean().getProperty(FIELD__ART) != null){
                        cbSorte.setEnabled(true);
                    }
                }
                
                
                if(isEditor()){
                    if(getCidsBean() != null && getCidsBean().getProperty(FIELD__STRASSE) != null){
                        cbHNr.setEnabled(true);
                    } else {
                        cbHNr.setEnabled(false);
                    }
                   /*StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbHNr);*/
                         cbHNr.addActionListener(hnrActionListener);
                    refreshHnr();
                }
            } catch (final Exception ex) {
                LOG.warn("problem in setCidsBean.", ex);
            }
        }
        setReadOnly();
        
    }
    
       
    private void refreshHnr() { 
        if (getCidsBean() != null && getCidsBean().getProperty(FIELD__STRASSE) != null){
            String schluessel = getCidsBean().getProperty(FIELD__STRASSE).toString();
            if (schluessel != null){

                hnrSearch.setKeyId(Integer.parseInt(schluessel.replaceFirst("0*","")));
                
                hnrSearch.setKeyId(Integer.parseInt(schluessel));
                initComboboxHnr();
            }
        }
    }
    
    private void setMapWindow() {
        baumLagePanel.setMapWindow(getCidsBean(), getConnectionContext());
    }
    
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
    
    public boolean isOkForSaving(CidsBean saveErsatzBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        
        // Straße muss angegeben werden
        try {
            if (saveErsatzBean.getProperty(FIELD__STRASSE) == null) {
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
            if (saveErsatzBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOGEOM));
                save = false;
            } else {
                final CidsBean geom_pos = (CidsBean)saveErsatzBean.getProperty(FIELD__GEOREFERENZ);
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
        
        // Anzahl muss, wenn angegeben, eine Ganzzahl sein; Pflichtattribut, wenn gepflanzt
        try {
            if (saveErsatzBean.getProperty(FIELD__DATUM_P) != null){
                if (saveErsatzBean.getProperty(FIELD__ANZAHL) == null || (Integer)saveErsatzBean.getProperty(FIELD__ANZAHL) == 0) {
                    LOG.warn("No count specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCOUNT));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Count not given.", ex);
            save = false;
        }
        
        //Art muss angegeben werden
        try {
            if (saveErsatzBean.getProperty(FIELD__DATUM_P) != null && saveErsatzBean.getProperty(FIELD__ART) == null) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOART));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }
        
        //Kontrolle
        try{
            Collection<CidsBean> controlCollection =  saveErsatzBean.getBeanCollectionProperty(FIELD__KONTROLLE);
            final Date jetztDatum = new java.sql.Date(System.currentTimeMillis());
            for (final CidsBean controlBean:controlCollection){
                //Datum vorhanden
                if (controlBean.getProperty(FIELD__KONTROLLE_DATUM)== null) {
                    LOG.warn("No kontolldatum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCONTROLDATE));
                    save = false;
                } else{
                    //Datum nicht in der Zukunft
                    final Object controllDate = controlBean.getProperty(FIELD__KONTROLLE_DATUM);
                    if (controllDate instanceof Date){
                        if (((Date)controllDate).after(jetztDatum)){
                            LOG.warn("Wrong kontolldatum specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_FUTUREDATE));
                            save = false;
                        }
                    }
                }
                //Bemerkung vorhanden
                if (controlBean.getProperty(FIELD__KONTROLLE_BEMERKUNG)== null) {
                    LOG.warn("No bemerkung specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_NOCONTROLTEXT));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Teilnehmer not correct.", ex);
            save = false;
        }
        
        if (errorMessage.length() > 0) {
            if (baumChildrenLoader.getParentOrganizer() instanceof BaumSchadenEditor){
                errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WHICH))
                        .append(saveErsatzBean.getPrimaryKeyValue());
            } else {
                if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor){
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_WHICH))
                            .append(saveErsatzBean.getPrimaryKeyValue());
                    CidsBean schadenBean = (CidsBean) saveErsatzBean.getProperty(FIELD__FK_SCHADEN);
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_FAULT))
                            .append(schadenBean.getPrimaryKeyValue());
                    CidsBean meldungBean = (CidsBean) schadenBean.getProperty(FIELD__FK_MELDUNG);
                    errorMessage.append(NbBundle.getMessage(BaumErsatzPanel.class, BUNDLE_MESSAGE))
                            .append(meldungBean.getProperty(FIELD__MDATUM));
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

    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......"});
        }
    } 
}
