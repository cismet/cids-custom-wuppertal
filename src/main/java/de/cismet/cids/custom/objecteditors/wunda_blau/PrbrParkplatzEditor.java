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
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import lombok.Getter;

import org.apache.log4j.Logger;

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

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class PrbrParkplatzEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PrbrParkplatzEditor.class);

    public static final String FIELD__UEBER = "ueberdachung";                       // prbr_parkplatz
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // prbr_parkplatz
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // prbr_parkplatz
    public static final String FIELD__FOTO = "foto";                                // prbr_parkplatz
    public static final String FIELD__BUSLINIE = "arr_buslinien";                   // prbr_parkplatz
    public static final String FIELD__BAHNLINIE = "arr_bahnlinien";                 // prbr_parkplatz
    public static final String FIELD__NAME = "name";                                // prbr_parkplatz
    public static final String FIELD__TYP = "fk_typ";                               // prbr_parkplatz
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String TABLE_NAME = "prbr_parkplatz";
    public static final String TABLE_GEOM = "geom";

    private static PrbrProperties PROPERTIES;

    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOK = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddBahn;
    private JButton btnAddBus;
    private JButton btnMenAbortBahn;
    private JButton btnMenAbortBus;
    private JButton btnMenOkBahn;
    private JButton btnMenOkBus;
    private JButton btnRemoveBahn;
    private JButton btnRemoveBus;
    private JComboBox cbBahnlinie;
    private JComboBox cbBuslinie;
    private JComboBox cbGeom;
    private DefaultBindableReferenceCombo cbHaltestelle;
    private DefaultBindableReferenceCombo cbTyp;
    private JCheckBox chAnbindung;
    private JCheckBox chUeberdachung;
    private JDialog dlgAddBahnlinien;
    private JDialog dlgAddBuslinien;
    private JLabel lblAnbindung;
    private JLabel lblAuswaehlenBahn;
    private JLabel lblAuswaehlenBus;
    private JLabel lblBahnlinien;
    private JLabel lblBemerkung;
    private JLabel lblBuslinien;
    private JLabel lblFoto;
    private JLabel lblGeom;
    private JLabel lblHaltestelle;
    private JLabel lblKarte;
    private JLabel lblName;
    private JLabel lblPlaetze;
    private JLabel lblTyp;
    private JLabel lblUeberdachung;
    private JLabel lblUrlCheck;
    private JList lstBahnlinien;
    private JList lstBuslinien;
    private JPanel panAddBahnlinie;
    private JPanel panAddBuslinie;
    private JPanel panButtonsBahn;
    private JPanel panButtonsBus;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerRechtsLage;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panLage;
    private JPanel panMenButtonsBahn;
    private JPanel panMenButtonsBus;
    private JPanel panName;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUrl;
    private RoundedPanel rpKarte;
    private JScrollPane scpLstBahnlinie;
    private JScrollPane scpLstBuslinie;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextField txtBemerkung;
    private JTextField txtFoto;
    private JTextField txtName;
    private JTextField txtPlaetze;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public PrbrParkplatzEditor() {
    }

    /**
     * Creates a new PrbrParkplatzEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public PrbrParkplatzEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initProperties() {
        if (PROPERTIES == null) {
            try {
                PROPERTIES = loadPropertiesFromServerResources(getConnectionContext());
            } catch (final Exception ex) {
                LOG.warn("properties couldn't be loaded. Editor/Renderer might not working as expected !", ex);
            }
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initProperties();
        initComponents();
        dlgAddBuslinien.pack();
        dlgAddBuslinien.getRootPane().setDefaultButton(btnMenOkBus);
        dlgAddBahnlinien.pack();
        dlgAddBahnlinien.getRootPane().setDefaultButton(btnMenOkBahn);
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        }
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

        dlgAddBuslinien = new JDialog();
        panAddBuslinie = new JPanel();
        lblAuswaehlenBus = new JLabel();
        final MetaObject[] buslinie = ObjectRendererUtils.getLightweightMetaObjectsForTable(
                "prbr_buslinie",
                new String[] { "name" },
                getConnectionContext());
        if (buslinie != null) {
            Arrays.sort(buslinie);
            cbBuslinie = new JComboBox(buslinie);
            panMenButtonsBus = new JPanel();
            btnMenAbortBus = new JButton();
            btnMenOkBus = new JButton();
            dlgAddBahnlinien = new JDialog();
            panAddBahnlinie = new JPanel();
            lblAuswaehlenBahn = new JLabel();
            final MetaObject[] bahnlinie = ObjectRendererUtils.getLightweightMetaObjectsForTable(
                    "prbr_bahnlinie",
                    new String[] { "name" },
                    getConnectionContext());
            if (bahnlinie != null) {
                Arrays.sort(bahnlinie);
                cbBahnlinie = new JComboBox(bahnlinie);
                panMenButtonsBahn = new JPanel();
                btnMenAbortBahn = new JButton();
                btnMenOkBahn = new JButton();
                panFillerUnten = new JPanel();
                panContent = new RoundedPanel();
                panFillerUnten1 = new JPanel();
                panLage = new JPanel();
                rpKarte = new RoundedPanel();
                panPreviewMap = new DefaultPreviewMapPanel();
                semiRoundedPanel7 = new SemiRoundedPanel();
                lblKarte = new JLabel();
                panFillerRechtsLage = new JPanel();
                panName = new JPanel();
                lblName = new JLabel();
                txtName = new JTextField();
                if (isEditor) {
                    cbGeom = new DefaultCismapGeometryComboBoxEditor();
                }
                lblGeom = new JLabel();
                panDaten = new JPanel();
                txtPlaetze = new JTextField();
                txtFoto = new JTextField();
                lblFoto = new JLabel();
                lblPlaetze = new JLabel();
                lblUeberdachung = new JLabel();
                lblHaltestelle = new JLabel();
                lblTyp = new JLabel();
                panFiller = new JPanel();
                chUeberdachung = new JCheckBox();
                txtBemerkung = new JTextField();
                lblBemerkung = new JLabel();
                panUrl = new JPanel();
                lblUrlCheck = new JLabel();
                cbTyp = new DefaultBindableReferenceCombo(true);
                cbHaltestelle = new DefaultBindableReferenceCombo(true);
                lblAnbindung = new JLabel();
                chAnbindung = new JCheckBox();
                panButtonsBus = new JPanel();
                btnAddBus = new JButton();
                btnRemoveBus = new JButton();
                scpLstBuslinie = new JScrollPane();
                lstBuslinien = new JList();
                lblBuslinien = new JLabel();
                scpLstBahnlinie = new JScrollPane();
                lstBahnlinien = new JList();
                lblBahnlinien = new JLabel();
                panButtonsBahn = new JPanel();
                btnAddBahn = new JButton();
                btnRemoveBahn = new JButton();

                dlgAddBuslinien.setModal(true);

                panAddBuslinie.setMaximumSize(new Dimension(180, 120));
                panAddBuslinie.setMinimumSize(new Dimension(180, 120));
                panAddBuslinie.setPreferredSize(new Dimension(180, 120));
                panAddBuslinie.setLayout(new GridBagLayout());

                lblAuswaehlenBus.setText("Bitte Buslinie auswählen:");
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.insets = new Insets(10, 10, 10, 10);
                panAddBuslinie.add(lblAuswaehlenBus, gridBagConstraints);
            }
            cbBuslinie.setMaximumSize(new Dimension(100, 20));
            cbBuslinie.setMinimumSize(new Dimension(100, 20));
            cbBuslinie.setPreferredSize(new Dimension(100, 20));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddBuslinie.add(cbBuslinie, gridBagConstraints);

            panMenButtonsBus.setLayout(new GridBagLayout());

            btnMenAbortBus.setText("Abbrechen");
            btnMenAbortBus.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent evt) {
                        btnMenAbortBusActionPerformed(evt);
                    }
                });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsBus.add(btnMenAbortBus, gridBagConstraints);

            btnMenOkBus.setText("Ok");
            btnMenOkBus.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent evt) {
                        btnMenOkBusActionPerformed(evt);
                    }
                });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsBus.add(btnMenOkBus, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddBuslinie.add(panMenButtonsBus, gridBagConstraints);

            dlgAddBuslinien.getContentPane().add(panAddBuslinie, BorderLayout.CENTER);

            dlgAddBahnlinien.setModal(true);

            panAddBahnlinie.setMaximumSize(new Dimension(180, 120));
            panAddBahnlinie.setMinimumSize(new Dimension(180, 120));
            panAddBahnlinie.setPreferredSize(new Dimension(180, 120));
            panAddBahnlinie.setLayout(new GridBagLayout());

            lblAuswaehlenBahn.setText("Bitte Bahnlinie auswählen:");
            lblAuswaehlenBahn.setToolTipText("");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddBahnlinie.add(lblAuswaehlenBahn, gridBagConstraints);
        }
        cbBahnlinie.setMaximumSize(new Dimension(100, 20));
        cbBahnlinie.setMinimumSize(new Dimension(100, 20));
        cbBahnlinie.setPreferredSize(new Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddBahnlinie.add(cbBahnlinie, gridBagConstraints);

        panMenButtonsBahn.setLayout(new GridBagLayout());

        btnMenAbortBahn.setText("Abbrechen");
        btnMenAbortBahn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenAbortBahnActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsBahn.add(btnMenAbortBahn, gridBagConstraints);

        btnMenOkBahn.setText("Ok");
        btnMenOkBahn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenOkBahnActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsBahn.add(btnMenOkBahn, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddBahnlinie.add(panMenButtonsBahn, gridBagConstraints);

        dlgAddBahnlinien.getContentPane().add(panAddBahnlinie, BorderLayout.CENTER);

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setAutoscrolls(true);
        panContent.setMaximumSize(new Dimension(450, 2147483647));
        panContent.setMinimumSize(new Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(450, 961));
        panContent.setLayout(new GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panContent.add(panFillerUnten1, gridBagConstraints);

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        panPreviewMap.setMinimumSize(new Dimension(600, 600));
        panPreviewMap.setPreferredSize(new Dimension(500, 300));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 10, 5);
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        lblKarte.setText("Lage");
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
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        final GroupLayout panFillerRechtsLageLayout = new GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(panFillerRechtsLageLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerRechtsLageLayout.setVerticalGroup(panFillerRechtsLageLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panLage.add(panFillerRechtsLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 9.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panContent.add(panLage, gridBagConstraints);

        panName.setOpaque(false);
        panName.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblName, gridBagConstraints);

        txtName.setToolTipText("");

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                txtName,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        if (isEditor) {
            if (isEditor) {
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_geom}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panName.add(cbGeom, gridBagConstraints);
        }

        lblGeom.setFont(new Font("Dialog", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblGeom, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzahl_plaetze}"),
                txtPlaetze,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtPlaetze, gridBagConstraints);
        txtPlaetze.getAccessibleContext().setAccessibleName("");

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.foto}"),
                txtFoto,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtFoto, gridBagConstraints);

        lblFoto.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFoto.setText("Foto:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblFoto, gridBagConstraints);

        lblPlaetze.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlaetze.setText("Plätze:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblPlaetze, gridBagConstraints);

        lblUeberdachung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUeberdachung.setText("Überdachung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblUeberdachung, gridBagConstraints);

        lblHaltestelle.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHaltestelle.setText("Haltestelle:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblHaltestelle, gridBagConstraints);

        lblTyp.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTyp.setText("Typ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblTyp, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);
        panFiller.setPreferredSize(new Dimension(20, 0));

        final GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                20,
                Short.MAX_VALUE));
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDaten.add(panFiller, gridBagConstraints);

        chUeberdachung.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ueberdachung}"),
                chUeberdachung,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(4, 0, 4, 0);
        panDaten.add(chUeberdachung, gridBagConstraints);

        txtBemerkung.setToolTipText("");

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beschreibung}"),
                txtBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtBemerkung, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panUrl, gridBagConstraints);

        cbTyp.setMaximumSize(new Dimension(200, 23));
        cbTyp.setMinimumSize(new Dimension(150, 23));
        cbTyp.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_typ}"),
                cbTyp,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbTyp, gridBagConstraints);

        cbHaltestelle.setMaximumSize(new Dimension(200, 23));
        cbHaltestelle.setMinimumSize(new Dimension(150, 23));
        cbHaltestelle.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_haltestelle}"),
                cbHaltestelle,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbHaltestelle, gridBagConstraints);

        lblAnbindung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAnbindung.setText("Schwebebahn:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblAnbindung, gridBagConstraints);

        chAnbindung.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anbindung_schwebebahn}"),
                chAnbindung,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(4, 0, 4, 0);
        panDaten.add(chAnbindung, gridBagConstraints);

        panButtonsBus.setOpaque(false);
        panButtonsBus.setLayout(new GridBagLayout());

        btnAddBus.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBus.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddBusActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panButtonsBus.add(btnAddBus, gridBagConstraints);

        btnRemoveBus.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveBus.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemoveBusActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panButtonsBus.add(btnRemoveBus, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panDaten.add(panButtonsBus, gridBagConstraints);

        lstBuslinien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ELProperty eLProperty = ELProperty.create("${cidsBean.arr_buslinien}");
        JListBinding jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstBuslinien);
        bindingGroup.addBinding(jListBinding);

        scpLstBuslinie.setViewportView(lstBuslinien);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(scpLstBuslinie, gridBagConstraints);

        lblBuslinien.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBuslinien.setText("Buslinie(n):");
        lblBuslinien.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblBuslinien, gridBagConstraints);

        lstBahnlinien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        eLProperty = ELProperty.create("${cidsBean.arr_bahnlinien}");
        jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstBahnlinien);
        bindingGroup.addBinding(jListBinding);

        scpLstBahnlinie.setViewportView(lstBahnlinien);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(scpLstBahnlinie, gridBagConstraints);

        lblBahnlinien.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBahnlinien.setText("Bahnlinie(n):");
        lblBahnlinien.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblBahnlinien, gridBagConstraints);

        panButtonsBahn.setOpaque(false);
        panButtonsBahn.setLayout(new GridBagLayout());

        btnAddBahn.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBahn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddBahnActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panButtonsBahn.add(btnAddBahn, gridBagConstraints);

        btnRemoveBahn.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveBahn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemoveBahnActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panButtonsBahn.add(btnRemoveBahn, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panDaten.add(panButtonsBahn, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        panContent.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddBusActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddBusActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(PrbrParkplatzEditor.this),
            dlgAddBuslinien,
            true);
    }                                                              //GEN-LAST:event_btnAddBusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveBusActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveBusActionPerformed
        final Object selection = lstBuslinien.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die Buslinie wirklich gelöscht werden?",
                    "Buslinie entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    // deleteItemFromList(FIELD__BUSLINIE, selection, false);
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__BUSLINIE, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Löschen",
                            "Beim Löschen der Buslinie ist ein Fehler aufgetreten",
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    } //GEN-LAST:event_btnRemoveBusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortBusActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortBusActionPerformed
        dlgAddBuslinien.setVisible(false);
    }                                                                   //GEN-LAST:event_btnMenAbortBusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkBusActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkBusActionPerformed
        try {
            final Object selItem = cbBuslinie.getSelectedItem();
            if (selItem instanceof MetaObject) {
                // addBeanToCollection(FIELD__BUSLINIE, ((MetaObject)selItem).getBean());
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__BUSLINIE,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__BUSLINIE);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddBuslinien.setVisible(false);
        }
    } //GEN-LAST:event_btnMenOkBusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddBahnActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddBahnActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(PrbrParkplatzEditor.this),
            dlgAddBahnlinien,
            true);
    }                                                               //GEN-LAST:event_btnAddBahnActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveBahnActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveBahnActionPerformed
        final Object selection = lstBahnlinien.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die Bahnlinie wirklich gelöscht werden?",
                    "Bahnlinie entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    // deleteItemFromList(FIELD__BAHNLINIE, selection, false);
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__BAHNLINIE, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Löschen",
                            "Beim Löschen der Bahnlinie ist ein Fehler aufgetreten",
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    } //GEN-LAST:event_btnRemoveBahnActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortBahnActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortBahnActionPerformed
        dlgAddBahnlinien.setVisible(false);
    }                                                                    //GEN-LAST:event_btnMenAbortBahnActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkBahnActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkBahnActionPerformed
        try {
            final Object selItem = cbBahnlinie.getSelectedItem();
            if (selItem instanceof MetaObject) {
                // addBeanToCollection(FIELD__BAHNLINIE, ((MetaObject)selItem).getBean());
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__BAHNLINIE,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__BAHNLINIE);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddBahnlinien.setVisible(false);
        }
    } //GEN-LAST:event_btnMenOkBahnActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void testUrlAndShowResult() {
        try {
            final URL url = new URL(PROPERTIES.getFotoUrl().concat(txtFoto.getText()));
            if (WebAccessManager.getInstance().checkIfURLaccessible(url)) {
                lblUrlCheck.setIcon(statusOK);
            } else {
                lblUrlCheck.setIcon(statusFalsch);
            }
        } catch (final MalformedURLException e) {
            lblUrlCheck.setIcon(statusFalsch);
            LOG.warn("URL Ceck Problem.", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propName  DOCUMENT ME!
     */
    private void sortListNew(final String propName) {
        bindingGroup.unbind();
        final List<CidsBean> changeCol = CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                propName);
        Collections.sort(changeCol, AlphanumComparator.getInstance());
        bindingGroup.bind();
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        PrbrParkplatzEditor.class,
                        "PrbrParkplatzEditor.prepareForSave().noName"));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Typ muss angegeben werden
        try {
            if (cbTyp.getSelectedItem() == null) {
                LOG.warn("No typ specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        PrbrParkplatzEditor.class,
                        "PrbrParkplatzEditor.prepareForSave().noTyp"));
            } else {
                final String typ = cbTyp.getSelectedItem().toString();
                if (typ.equalsIgnoreCase("P")) {
                    final boolean bUeber = chUeberdachung.isSelected();
                    if (bUeber) {
                        LOG.warn("Wrong typ specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(
                                PrbrParkplatzEditor.class,
                                "PrbrParkplatzEditor.prepareForSave().wrongTyp"));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Typ not given.", ex);
            save = false;
        }
        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        PrbrParkplatzEditor.class,
                        "PrbrParkplatzEditor.prepareForSave().noGeom"));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(
                            PrbrParkplatzEditor.class,
                            "PrbrParkplatzEditor.prepareForSave().wrongGeom"));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    PrbrParkplatzEditor.class,
                    "PrbrParkplatzEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            PrbrParkplatzEditor.class,
                            "PrbrParkplatzEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(
                    PrbrParkplatzEditor.class,
                    "PrbrParkplatzEditor.prepareForSave().JOptionPane.title"),
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
                LOG.info("remove propchange prbr_parkplatz: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange prbr_parkplatz: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            // Damit die Linien sortiert in der Liste erscheinen.
            final List<CidsBean> busCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__BUSLINIE);
            Collections.sort(busCol, AlphanumComparator.getInstance());
            final List<CidsBean> bahnCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__BAHNLINIE);
            Collections.sort(bahnCol, AlphanumComparator.getInstance());
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            testUrlAndShowResult();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            cbTyp.setEnabled(false);
            cbHaltestelle.setEnabled(false);
            chUeberdachung.setEnabled(false);
            chAnbindung.setEnabled(false);
            txtBemerkung.setEnabled(false);
            txtFoto.setEnabled(false);
            txtName.setEnabled(false);
            txtPlaetze.setEnabled(false);
            // cbGeom.setVisible(false);
            lblGeom.setVisible(false);
            lstBuslinien.setEnabled(false);
            lstBahnlinien.setEnabled(false);
            btnAddBus.setEnabled(false);
            btnRemoveBus.setEnabled(false);
            btnAddBahn.setEnabled(false);
            btnRemoveBahn.setEnabled(false);
            // lblUrlCheck.setEnabled(false); Es soll noch erkennbar sein.
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOREFERENZ) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, PROPERTIES.getBufferMeter());
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
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, PROPERTIES.getBufferMeter());
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String makeTitle() {
        String myname;
        myname = String.valueOf(cidsBean.getProperty("name"));
        if ("null".equals(myname)) {
            myname = "Neue P+R bzw. B+R Anlage anlegen";
        }

        return myname;
    }

    @Override
    public void dispose() {
        super.dispose();
        dlgAddBahnlinien.dispose();
        dlgAddBuslinien.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public String getTitle() {
        return makeTitle();
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
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ)) {
            setMapWindow();
        }

        if (evt.getPropertyName().equals(FIELD__FOTO)) {
            testUrlAndShowResult();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testUeberdachung() {
        final String typ = cbTyp.getSelectedItem().toString();
        if (typ.equalsIgnoreCase("B")) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static PrbrProperties loadPropertiesFromServerResources(final ConnectionContext connectionContext)
            throws Exception {
        final Object ret;
        /* ret = SessionManager.getSession()
         *       .getConnection()      .executeTask(SessionManager.getSession().getUser(),
         * GetServerResourceServerAction.TASK_NAME,              "WUNDA_BLAU",
         * WundaBlauServerResources.PRBR_PROPERTIES.getValue(),              connectionContext); if (ret instanceof
         * Exception) {  throw (Exception)ret; }*/
        final Properties properties = new Properties();
        // properties.load(new StringReader((String)ret));
        try {
            properties.load(new StringReader(
                    "PICTURE_PATH=https://www.wuppertal.de/geoportal/prbr/fotos/"
                            + "\n"
                            + "BUFFER_METER=20.0"));
        } catch (IOException e) {
            LOG.warn("Fehler beim Laden der Properties", e);
        }

        return new PrbrProperties(properties);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class RegexPatternFormatter extends DefaultFormatter {

        //~ Instance fields ----------------------------------------------------

        protected java.util.regex.Matcher fillingMatcher;
        protected java.util.regex.Matcher matchingMatcher;
        private Object lastValid = null;

        //~ Methods ------------------------------------------------------------

        @Override
        public Object stringToValue(final String string) throws java.text.ParseException {
            if ((string == null) || string.isEmpty()) {
                lastValid = null;
                return null;
            }
            fillingMatcher.reset(string);

            if (!fillingMatcher.matches()) {
                throw new java.text.ParseException("does not match regex", 0);
            }

            final Object value = (String)super.stringToValue(string);

            matchingMatcher.reset(string);
            if (matchingMatcher.matches()) {
                lastValid = value;
            }
            return value;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object getLastValid() {
            return lastValid;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    static class PrbrProperties {

        //~ Instance fields ----------------------------------------------------

        private final Properties properties;

        private final String fotoUrl;
        private final Double bufferMeter;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PrbrProperties object.
         *
         * @param  properties  DOCUMENT ME!
         */
        public PrbrProperties(final Properties properties) {
            this.properties = properties;

            fotoUrl = readProperty("PICTURE_PATH", null);
            bufferMeter = Double.valueOf(readProperty("BUFFER_METER", null));
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   property      DOCUMENT ME!
         * @param   defaultValue  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private String readProperty(final String property, final String defaultValue) {
            String value = defaultValue;
            try {
                value = getProperties().getProperty(property, defaultValue);
            } catch (final Exception ex) {
                final String message = "could not read " + property + " from "
                            // + WundaBlauServerResources.PRBR_PROPERTIES.getValue()
                            + ". setting to default value: " + defaultValue;
                LOG.warn(message, ex);
            }
            return value;
        }
    }
}
