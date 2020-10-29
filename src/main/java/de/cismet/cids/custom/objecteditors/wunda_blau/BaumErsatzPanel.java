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
import de.cismet.cids.custom.objecteditors.utils.EmobConfProperties;
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

import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumErsatzPanel extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    private static final Comparator<Object> DATE_COMPARATOR = new Comparator<Object>() {

            @Override
           /* public int compare(final Object o1, final Object o2) {
                return AlphanumComparator.getInstance().compare(String.valueOf(o1), String.valueOf(o2));
            }*/
            public int compare(final Object o1, final Object o2) {
                    final String o1String = String.valueOf(((CidsBean)o1).getProperty("datum"));
                    final String o2String = String.valueOf(((CidsBean)o2).getProperty("datum"));

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
    
    private List<CidsBean> kontrolleBeans;
    private static final Logger LOG = Logger.getLogger(BaumErsatzPanel.class);
    
    
    public static final String FIELD__KONTROLLE = "n_kontrolle";                // baum_ersatz
    public static final String FIELD__DATE = "datum";                           // baum_kontrolle
    public static final String FIELD__DATUM = "pflanzdatum";                    // baum_ersatz
    public static final String FIELD__ART = "fk_art";                           // baum_ersatz
    public static final String FIELD__GEOM = "fk_geom";                         // baum_ersatz
    public static final String FIELD__SELBST = "selbststaendig";                // baum_ersatz
    public static final String FIELD__BIS = "umsetzung_bis";                    // baum_ersatz
    public static final String FIELD__ANZAHL = "anzahl";                        // baum_ersatz
    public static final String FIELD__FIRMA = "firma";                          // baum_ersatz
    public static final String FIELD__BEMERKUNG = "bemerkung";                  // baum_ersatz
    
    public static final String FIELD__GEO_FIELD = "geo_field";                  // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_ersatz_geom
    
    public static final String TABLE_GEOM = "geom";
    
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        dlgAddKontrolle = new JDialog();
        panAddKontrolle = new JPanel();
        lblAuswaehlenKontrolle = new JLabel();
        panMenButtonsKontrolle = new JPanel();
        btnMenAbortKontrolle = new JButton();
        btnMenOkKontrolle = new JButton();
        dcKontrolle = new DefaultBindableDateChooser();
        panErsatz = new JPanel();
        JLabel lblBis = new JLabel();
        dcBis = new DefaultBindableDateChooser();
        JLabel lblGeom = new JLabel();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        JLabel lblDatum = new JLabel();
        JLabel lblArt = new JLabel();
        cbArt = new DefaultBindableScrollableComboBox();
        JLabel lblAnzahl = new JLabel();
        spAnzahl = new JSpinner();
        JLabel lblSelbst = new JLabel();
        chSelbst = new JCheckBox();
        JLabel lblFirma = new JLabel();
        txtFirma = new JTextField();
        JPanel panGeometrie = new JPanel();
        JPanel panLage = new JPanel();
        RoundedPanel rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        SemiRoundedPanel semiRoundedPanel7 = new SemiRoundedPanel();
        JLabel lblKarte = new JLabel();
        JLabel lblBemerkung = new JLabel();
        JScrollPane scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panKontrolle = new JPanel();
        rpKontrolleliste = new RoundedPanel();
        scpLaufendeKontrolle = new JScrollPane();
        lstKontrollen = new JList();
        SemiRoundedPanel semiRoundedPanelKontrolle = new SemiRoundedPanel();
        lblKontrolle = new JLabel();
        JPanel panControlsNewKontrolle = new JPanel();
        btnAddNewKontrolle = new JButton();
        btnRemoveKontrolle = new JButton();
        RoundedPanel rpKontrolleinfo = new RoundedPanel();
        semiRoundedPanel5 = new SemiRoundedPanel();
        lblKontrolleanzeige = new JLabel();
        panKontrolleMain = new JPanel();
        Box.Filler filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        BaumKontrollePanel baumKontrollePanel1 = new BaumKontrollePanel();
        dcDatum = new DefaultBindableDateChooser();
        Box.Filler filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));

        FormListener formListener = new FormListener();

        dlgAddKontrolle.setTitle(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.dlgAddErsatz.title")); // NOI18N
        dlgAddKontrolle.setModal(true);
        dlgAddKontrolle.setName("dlgAddKontrolle"); // NOI18N

        panAddKontrolle.setName("panAddKontrolle"); // NOI18N
        panAddKontrolle.setLayout(new GridBagLayout());

        lblAuswaehlenKontrolle.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblAuswaehlenErsatz.text")); // NOI18N
        lblAuswaehlenKontrolle.setName("lblAuswaehlenKontrolle"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddKontrolle.add(lblAuswaehlenKontrolle, gridBagConstraints);

        panMenButtonsKontrolle.setName("panMenButtonsKontrolle"); // NOI18N
        panMenButtonsKontrolle.setLayout(new GridBagLayout());

        btnMenAbortKontrolle.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.btnMenAbortKontrolle.text")); // NOI18N
        btnMenAbortKontrolle.setName("btnMenAbortKontrolle"); // NOI18N
        btnMenAbortKontrolle.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsKontrolle.add(btnMenAbortKontrolle, gridBagConstraints);

        btnMenOkKontrolle.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.btnMenOkErsatz.text")); // NOI18N
        btnMenOkKontrolle.setName("btnMenOkKontrolle"); // NOI18N
        btnMenOkKontrolle.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsKontrolle.add(btnMenOkKontrolle, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddKontrolle.add(panMenButtonsKontrolle, gridBagConstraints);

        dcKontrolle.setName("dcKontrolle"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAddKontrolle.add(dcKontrolle, gridBagConstraints);

        dlgAddKontrolle.getContentPane().add(panAddKontrolle, BorderLayout.CENTER);

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        lblBis.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBis, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblBis.text")); // NOI18N
        lblBis.setName("lblBis"); // NOI18N
        lblBis.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblBis, gridBagConstraints);

        dcBis.setName("dcBis"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BIS + "}"), dcBis, BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(dcBis, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblGeom.text")); // NOI18N
        lblGeom.setName("lblGeom"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblGeom, gridBagConstraints);

        if (isEditor){
            cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeom.setName("cbGeom"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__GEOM + "}"), cbGeom, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panErsatz.add(cbGeom, gridBagConstraints);
        }

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblDatum, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblDatum.text")); // NOI18N
        lblDatum.setName("lblDatum"); // NOI18N
        lblDatum.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblDatum, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblArt.text")); // NOI18N
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblArt, gridBagConstraints);

        cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArt.setName("cbArt"); // NOI18N
        cbArt.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__ART + "}"), cbArt, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(cbArt, gridBagConstraints);

        lblAnzahl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAnzahl, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblAlter.text")); // NOI18N
        lblAnzahl.setName("lblAnzahl"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblAnzahl, gridBagConstraints);

        spAnzahl.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spAnzahl.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spAnzahl.setName("spAnzahl"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__ANZAHL + "}"), spAnzahl, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(spAnzahl, gridBagConstraints);

        lblSelbst.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSelbst, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblPrivat.text")); // NOI18N
        lblSelbst.setName("lblSelbst"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblSelbst, gridBagConstraints);

        chSelbst.setContentAreaFilled(false);
        chSelbst.setName("chSelbst"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__SELBST + "}"), chSelbst, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(chSelbst, gridBagConstraints);

        lblFirma.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFirma, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblFirma.text")); // NOI18N
        lblFirma.setName("lblFirma"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblFirma, gridBagConstraints);

        txtFirma.setName("txtFirma"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__FIRMA + "}"), txtFirma, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(txtFirma, gridBagConstraints);

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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setName("semiRoundedPanel7"); // NOI18N
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        Mnemonics.setLocalizedText(lblKarte, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblKarte.text")); // NOI18N
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panErsatz.add(panGeometrie, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblBemerkung.text")); // NOI18N
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BEMERKUNG + "}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(scpBemerkung, gridBagConstraints);

        panKontrolle.setName("panKontrolle"); // NOI18N
        panKontrolle.setOpaque(false);
        panKontrolle.setLayout(new GridBagLayout());

        rpKontrolleliste.setMinimumSize(new Dimension(80, 202));
        rpKontrolleliste.setName("rpKontrolleliste"); // NOI18N
        rpKontrolleliste.setPreferredSize(new Dimension(100, 202));
        rpKontrolleliste.setLayout(new GridBagLayout());

        scpLaufendeKontrolle.setName("scpLaufendeKontrolle"); // NOI18N

        lstKontrollen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstKontrollen.setFixedCellWidth(75);
        lstKontrollen.setName("lstKontrollen"); // NOI18N

        ELProperty eLProperty = ELProperty.create("${cidsBean." + FIELD__KONTROLLE + "}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstKontrollen);
        bindingGroup.addBinding(jListBinding);

        scpLaufendeKontrolle.setViewportView(lstKontrollen);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpKontrolleliste.add(scpLaufendeKontrolle, gridBagConstraints);

        semiRoundedPanelKontrolle.setBackground(Color.darkGray);
        semiRoundedPanelKontrolle.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanelKontrolle.setName("semiRoundedPanelKontrolle"); // NOI18N
        semiRoundedPanelKontrolle.setLayout(new GridBagLayout());

        lblKontrolle.setForeground(new Color(255, 255, 255));
        lblKontrolle.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblErsatz.text")); // NOI18N
        lblKontrolle.setName("lblKontrolle"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelKontrolle.add(lblKontrolle, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKontrolleliste.add(semiRoundedPanelKontrolle, gridBagConstraints);

        panControlsNewKontrolle.setName("panControlsNewKontrolle"); // NOI18N
        panControlsNewKontrolle.setOpaque(false);
        panControlsNewKontrolle.setLayout(new GridBagLayout());

        btnAddNewKontrolle.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewKontrolle.setMaximumSize(new Dimension(39, 20));
        btnAddNewKontrolle.setMinimumSize(new Dimension(39, 20));
        btnAddNewKontrolle.setName("btnAddNewKontrolle"); // NOI18N
        btnAddNewKontrolle.setPreferredSize(new Dimension(39, 25));
        btnAddNewKontrolle.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewKontrolle.add(btnAddNewKontrolle, gridBagConstraints);

        btnRemoveKontrolle.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveKontrolle.setMaximumSize(new Dimension(39, 20));
        btnRemoveKontrolle.setMinimumSize(new Dimension(39, 20));
        btnRemoveKontrolle.setName("btnRemoveKontrolle"); // NOI18N
        btnRemoveKontrolle.setPreferredSize(new Dimension(39, 25));
        btnRemoveKontrolle.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewKontrolle.add(btnRemoveKontrolle, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpKontrolleliste.add(panControlsNewKontrolle, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panKontrolle.add(rpKontrolleliste, gridBagConstraints);

        rpKontrolleinfo.setName("rpKontrolleinfo"); // NOI18N
        rpKontrolleinfo.setLayout(new GridBagLayout());

        semiRoundedPanel5.setBackground(Color.darkGray);
        semiRoundedPanel5.setName("semiRoundedPanel5"); // NOI18N
        semiRoundedPanel5.setLayout(new GridBagLayout());

        lblKontrolleanzeige.setForeground(new Color(255, 255, 255));
        lblKontrolleanzeige.setText(NbBundle.getMessage(BaumErsatzPanel.class, "BaumErsatzPanel.lblErsatzanzeige.text")); // NOI18N
        lblKontrolleanzeige.setName("lblKontrolleanzeige"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblKontrolleanzeige, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKontrolleinfo.add(semiRoundedPanel5, gridBagConstraints);

        panKontrolleMain.setName("panKontrolleMain"); // NOI18N
        panKontrolleMain.setOpaque(false);
        panKontrolleMain.setLayout(new GridBagLayout());

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panKontrolleMain.add(filler3, gridBagConstraints);

        baumKontrollePanel1.setName("baumKontrollePanel1"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstKontrollen, ELProperty.create("${selectedElement}"), baumKontrollePanel1, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panKontrolleMain.add(baumKontrollePanel1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKontrolleinfo.add(panKontrolleMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panKontrolle.add(rpKontrolleinfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panErsatz.add(panKontrolle, gridBagConstraints);

        dcDatum.setName("dcDatum"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__DATUM + "}"), dcDatum, BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panErsatz.add(dcDatum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(panErsatz, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(filler4, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddNewKontrolle) {
                BaumErsatzPanel.this.btnAddNewKontrolleActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveKontrolle) {
                BaumErsatzPanel.this.btnRemoveKontrolleActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortKontrolle) {
                BaumErsatzPanel.this.btnMenAbortKontrolleActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkKontrolle) {
                BaumErsatzPanel.this.btnMenOkKontrolleActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddNewKontrolleActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewKontrolleActionPerformed
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumErsatzPanel.this), dlgAddKontrolle, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumKontrolle object", e);
        }
    }//GEN-LAST:event_btnAddNewKontrolleActionPerformed

    private void btnRemoveKontrolleActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveKontrolleActionPerformed
        final Object selectedObject = lstKontrollen.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (kontrolleBeans != null) {
                kontrolleBeans.remove((CidsBean)selectedObject);
                if (kontrolleBeans != null) {
                    lstKontrollen.setSelectedIndex(0);
                }else{
                    lstKontrollen.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveKontrolleActionPerformed

    private void btnMenAbortKontrolleActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortKontrolleActionPerformed
        dlgAddKontrolle.setVisible(false);
    }//GEN-LAST:event_btnMenAbortKontrolleActionPerformed

    private void btnMenOkKontrolleActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkKontrolleActionPerformed
        try {
            //meldungsBean erzeugen und vorbelegen:
            final CidsBean beanKontrolle = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_KONTROLLE",
                getConnectionContext());

            final java.util.Date selDate = dcKontrolle.getDate();
            java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date beanDate = new java.sql.Date(cal.getTime().getTime());

            beanKontrolle.setProperty("datum", beanDate);

            //Kontrollen erweitern:
            kontrolleBeans.add(beanKontrolle);

            //Refresh:

            bindingGroup.unbind();
            Collections.sort((List)kontrolleBeans, DATE_COMPARATOR);
            bindingGroup.bind();
            lstKontrollen.setSelectedValue(beanKontrolle, true);

        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddKontrolle.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkKontrolleActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    private final BaumSchadenPanel parentPanel;
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnAddNewKontrolle;
    JButton btnMenAbortKontrolle;
    JButton btnMenOkKontrolle;
    JButton btnRemoveKontrolle;
    JComboBox<String> cbArt;
    JComboBox cbGeom;
    JCheckBox chSelbst;
    DefaultBindableDateChooser dcBis;
    DefaultBindableDateChooser dcDatum;
    DefaultBindableDateChooser dcKontrolle;
    JDialog dlgAddKontrolle;
    JLabel lblAuswaehlenKontrolle;
    JLabel lblKontrolle;
    JLabel lblKontrolleanzeige;
    JList lstKontrollen;
    JPanel panAddKontrolle;
    JPanel panErsatz;
    JPanel panKontrolle;
    JPanel panKontrolleMain;
    JPanel panMenButtonsKontrolle;
    DefaultPreviewMapPanel panPreviewMap;
    RoundedPanel rpKontrolleliste;
    JScrollPane scpLaufendeKontrolle;
    SemiRoundedPanel semiRoundedPanel5;
    JSpinner spAnzahl;
    JTextArea taBemerkung;
    JTextField txtFirma;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumMeldungPanel object.
     */
    public BaumErsatzPanel() {
        this(null,true);
    }

    
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentPanel
     * @param  editable  DOCUMENT ME!
     */
    public BaumErsatzPanel(final BaumSchadenPanel parentPanel, final boolean editable) {
        this.isEditor = editable;
        initComponents();
        this.connectionContext = null;
        this.parentPanel = parentPanel;
    }
 
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentPanel
     * @param  editable             DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public BaumErsatzPanel(final BaumSchadenPanel parentPanel, final boolean editable,
            final ConnectionContext connectionContext) {
        this.isEditor = editable;
        this.connectionContext = connectionContext;
        initComponents();
        this.parentPanel = parentPanel;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
        
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        dlgAddKontrolle.dispose();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (this.cidsBean != null){
            setKontrolleBeans(cidsBean.getBeanCollectionProperty(FIELD__KONTROLLE)); 
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
        }
        
        
        setMapWindow();
        bindingGroup.bind();
        
        if (kontrolleBeans != null) {
            lstKontrollen.setSelectedIndex(0);
        }
        lstKontrollen.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty(FIELD__DATE);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoTeil = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                    compoTeil.setForeground(Color.red);
                    return compoTeil;
                }
            });
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setKontrolleBeans(final List<CidsBean> cidsBeans) {
        this.kontrolleBeans = cidsBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getKontrolleBeans() {
        return kontrolleBeans;
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = EmobConfProperties.getInstance().getBufferMeter();
            if (cb.getProperty(FIELD__GEOM) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, bufferMeter);
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
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }
}
