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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.EmobConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

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

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValues;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.KkKompensationNextSchluesselSearch;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.converters.SqlDateToUtilDateConverter;
import de.cismet.cids.server.search.AbstractCidsServerSearch;
import java.awt.Component;
import java.sql.Date;
import java.text.DateFormat;
import java.time.Instant;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import org.openide.util.WeakListeners;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumGebietEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------
    private final ListSelectionListener messSelL = new MeldungSelectionListener();
    
    public static final String GEOMTYPE = "Point";
    public static final int FOTO_WIDTH = 150;

    private static final Logger LOG = Logger.getLogger(BaumGebietEditor.class);

    public static final String FIELD__NAME = "name";                            // baum_gebiet
    public static final String FIELD__ID = "id";                                    // baum_gebietation
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // baum_gebiet
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_gebiet_geombaum_gebiet
    public static final String TABLE_NAME = "baum_gebiet";
    public static final String TABLE_GEOM = "geom";

    public static final String BUNDLE_NOLOAD = "BaumGebietEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NONAME = "BaumGebietEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "BaumGebietEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "BaumGebietEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOGEOM = "BaumGebietEditor.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumGebietEditor.prepareForSave().wrongGeom";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumGebietEditor.prepareForSave().JOptionPane.title";


    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum OtherTableCases {

        //~ Enum constants -----------------------------------------------------

        SET_VALUE, REDUNDANT_ATT_NAME
    }

    //~ Instance fields --------------------------------------------------------

    private SwingWorker worker_loadFoto;
    private SwingWorker worker_name;
    private SwingWorker worker_foto;
    private SwingWorker worker_versatz;

    private Boolean redundantName = false;

    private boolean isEditor = true;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddLaufendeNummerMeldung;
    private JButton btnAddNewMeldung;
    private JButton btnCopyBaulast;
    private JButton btnMenAbortMeldung;
    private JButton btnMenAbortPfand;
    private JButton btnMenAbortStecker;
    private JButton btnMenOkMeldung;
    private JButton btnMenOkPfand;
    private JButton btnMenOkStecker;
    private JButton btnPasteBaulast;
    private JButton btnRemoveLaufendeNummerMeldung;
    private JButton btnRemoveMeldung;
    private JComboBox cbGeom;
    private JComboBox cbPfand;
    private DefaultBindableReferenceCombo cbStatus;
    private JComboBox cbStecker;
    private DefaultBindableDateChooser dcMeldung;
    private JDialog dlgAddMeldung;
    private JDialog dlgAddPfand;
    private JDialog dlgAddStecker;
    private Box.Filler filler3;
    private JLabel jLabel13;
    private JPanel jPanel1;
    private JPanel jPanel8;
    private JPanel jPanel9;
    private JLabel lblAktenzeichen;
    private JLabel lblAuswaehlenMeldung;
    private JLabel lblAuswaehlenPfand;
    private JLabel lblAuswaehlenStecker;
    private JLabel lblBemerkung;
    private JLabel lblGeom;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblMeldung;
    private JLabel lblMeldungen;
    private JLabel lblName;
    private JLabel lblStatus;
    private JLabel lblStrasse;
    private JList lstLaufendeNummernMeldung;
    private JList lstMeldungen;
    private JPanel panAddMeldung;
    private JPanel panAddPfand;
    private JPanel panAddStecker;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panControlsLaufendeNummernMeldung;
    private JPanel panControlsNewMeldungen;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panGeometrie;
    private JPanel panLage;
    private JPanel panMeldung;
    private JPanel panMeldungenMain;
    private JPanel panMeldungpan;
    private JPanel panMenButtonsMeldung;
    private JPanel panMenButtonsPfand;
    private JPanel panMenButtonsStecker;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panZusatz;
    private RoundedPanel rpKarte;
    private RoundedPanel rpLaufendeMeldungen;
    private RoundedPanel rpMeldunginfo;
    private RoundedPanel rpMeldungliste;
    private JScrollPane scpBemerkung;
    private JScrollPane scpLaufendeMeldungen;
    private JScrollPane scpLaufendeNummernMeldung;
    private SemiRoundedPanel semiRoundedPanel3;
    private SemiRoundedPanel semiRoundedPanel4;
    private SemiRoundedPanel semiRoundedPanel5;
    private SemiRoundedPanel semiRoundedPanel7;
    private JSeparator sepStatus;
    private SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private JTextArea taBemerkung;
    private JTextField txtAktenzeichen;
    private JTextField txtHnr;
    private JTextField txtName;
    private JTextField txtStrasse;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumGebietEditor() {
    }

    /**
     * Creates a new BaumGebietEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumGebietEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        txtAktenzeichen.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn der Name geändert wird, wird dieser überprüft.
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
        lstMeldungen.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty("datum");

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }

                    return super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                }
            });
        

        dlgAddMeldung.pack();
        dlgAddMeldung.getRootPane().setDefaultButton(btnMenOkMeldung);
        dlgAddStecker.pack();
        dlgAddStecker.getRootPane().setDefaultButton(btnMenOkStecker);
        dlgAddPfand.pack();
        dlgAddPfand.getRootPane().setDefaultButton(btnMenOkPfand);

        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        }
        setReadOnly();
         lstMeldungen.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                messSelL,
                lstMeldungen));
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

        dlgAddMeldung = new JDialog();
        panAddMeldung = new JPanel();
        lblAuswaehlenMeldung = new JLabel();
        panMenButtonsMeldung = new JPanel();
        btnMenAbortMeldung = new JButton();
        btnMenOkMeldung = new JButton();
        dcMeldung = new DefaultBindableDateChooser();
        dlgAddStecker = new JDialog();
        panAddStecker = new JPanel();
        lblAuswaehlenStecker = new JLabel();
        final MetaObject[] stecker = ObjectRendererUtils.getLightweightMetaObjectsForTable("emobrad_stecker", new String[]{"schluessel"}, getConnectionContext());
        if(stecker != null) {
            Arrays.sort(stecker);//||
            cbStecker = new JComboBox(stecker);
        }
        panMenButtonsStecker = new JPanel();
        btnMenAbortStecker = new JButton();
        btnMenOkStecker = new JButton();
        dlgAddPfand = new JDialog();
        panAddPfand = new JPanel();
        lblAuswaehlenPfand = new JLabel();
        final MetaObject[] pfand = ObjectRendererUtils.getLightweightMetaObjectsForTable("emobrad_pfand", new String[]{"name"}, getConnectionContext());
        if(pfand != null) {
            Arrays.sort(pfand);
            cbPfand = new JComboBox(pfand);
            panMenButtonsPfand = new JPanel();
            btnMenAbortPfand = new JButton();
            btnMenOkPfand = new JButton();
            rpLaufendeMeldungen = new RoundedPanel();
            scpLaufendeNummernMeldung = new JScrollPane();
            lstLaufendeNummernMeldung = new JList();
            semiRoundedPanel3 = new SemiRoundedPanel();
            jLabel13 = new JLabel();
            panControlsLaufendeNummernMeldung = new JPanel();
            btnAddLaufendeNummerMeldung = new JButton();
            btnRemoveLaufendeNummerMeldung = new JButton();
            btnCopyBaulast = new JButton();
            btnPasteBaulast = new JButton();
            sqlDateToUtilDateConverter = new SqlDateToUtilDateConverter();
            panFillerUnten = new JPanel();
            panContent = new RoundedPanel();
            jPanel1 = new JPanel();
            panFillerUnten1 = new JPanel();
            panDaten = new JPanel();
            lblAktenzeichen = new JLabel();
            txtAktenzeichen = new JTextField();
            lblStrasse = new JLabel();
            txtStrasse = new JTextField();
            filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
            lblHnr = new JLabel();
            txtHnr = new JTextField();
            lblName = new JLabel();
            txtName = new JTextField();
            panZusatz = new JPanel();
            lblBemerkung = new JLabel();
            panBemerkung = new JPanel();
            scpBemerkung = new JScrollPane();
            taBemerkung = new JTextArea();
            panFiller = new JPanel();
            lblGeom = new JLabel();
            if (isEditor){
                cbGeom = new DefaultCismapGeometryComboBoxEditor();
            }
            sepStatus = new JSeparator();
            lblStatus = new JLabel();
            cbStatus = new DefaultBindableReferenceCombo() ;
            panGeometrie = new JPanel();
            panLage = new JPanel();
            rpKarte = new RoundedPanel();
            panPreviewMap = new DefaultPreviewMapPanel();
            semiRoundedPanel7 = new SemiRoundedPanel();
            lblKarte = new JLabel();
            panMeldung = new JPanel();
            rpMeldungliste = new RoundedPanel();
            scpLaufendeMeldungen = new JScrollPane();
            lstMeldungen = new JList();
            semiRoundedPanel4 = new SemiRoundedPanel();
            lblMeldungen = new JLabel();
            jPanel8 = new JPanel();
            panControlsNewMeldungen = new JPanel();
            btnAddNewMeldung = new JButton();
            btnRemoveMeldung = new JButton();
            rpMeldunginfo = new RoundedPanel();
            semiRoundedPanel5 = new SemiRoundedPanel();
            lblMeldung = new JLabel();
            jPanel9 = new JPanel();
            panMeldungenMain = new JPanel();
            panMeldungpan = new BaumMeldungPanel(isEditor, getConnectionContext());

            dlgAddMeldung.setTitle("Zugangsart");
            dlgAddMeldung.setModal(true);

            panAddMeldung.setLayout(new GridBagLayout());

            lblAuswaehlenMeldung.setText("Bitte Meldungsdatum auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddMeldung.add(lblAuswaehlenMeldung, gridBagConstraints);

            panMenButtonsMeldung.setLayout(new GridBagLayout());

            btnMenAbortMeldung.setText("Abbrechen");
            btnMenAbortMeldung.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenAbortMeldungActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsMeldung.add(btnMenAbortMeldung, gridBagConstraints);

            btnMenOkMeldung.setText("Ok");
            btnMenOkMeldung.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenOkMeldungActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsMeldung.add(btnMenOkMeldung, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddMeldung.add(panMenButtonsMeldung, gridBagConstraints);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 4, 2, 2);
            panAddMeldung.add(dcMeldung, gridBagConstraints);

            dlgAddMeldung.getContentPane().add(panAddMeldung, BorderLayout.CENTER);

            dlgAddStecker.setTitle("Steckerverbindung");
            dlgAddStecker.setModal(true);

            panAddStecker.setLayout(new GridBagLayout());

            lblAuswaehlenStecker.setText("Bitte Steckerverbindung auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddStecker.add(lblAuswaehlenStecker, gridBagConstraints);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddStecker.add(cbStecker, gridBagConstraints);

            panMenButtonsStecker.setLayout(new GridBagLayout());

            btnMenAbortStecker.setText("Abbrechen");
            btnMenAbortStecker.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenAbortSteckerActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsStecker.add(btnMenAbortStecker, gridBagConstraints);

            btnMenOkStecker.setText("Ok");
            btnMenOkStecker.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenOkSteckerActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsStecker.add(btnMenOkStecker, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddStecker.add(panMenButtonsStecker, gridBagConstraints);

            dlgAddStecker.getContentPane().add(panAddStecker, BorderLayout.CENTER);

            dlgAddPfand.setTitle("Pfandmünze");
            dlgAddPfand.setModal(true);

            panAddPfand.setLayout(new GridBagLayout());

            lblAuswaehlenPfand.setText("Bitte Pfandmünze auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddPfand.add(lblAuswaehlenPfand, gridBagConstraints);

        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddPfand.add(cbPfand, gridBagConstraints);

        panMenButtonsPfand.setLayout(new GridBagLayout());

        btnMenAbortPfand.setText("Abbrechen");
        btnMenAbortPfand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenAbortPfandActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsPfand.add(btnMenAbortPfand, gridBagConstraints);

        btnMenOkPfand.setText("Ok");
        btnMenOkPfand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenOkPfandActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsPfand.add(btnMenOkPfand, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddPfand.add(panMenButtonsPfand, gridBagConstraints);

        dlgAddPfand.getContentPane().add(panAddPfand, BorderLayout.CENTER);

        rpLaufendeMeldungen.setLayout(new GridBagLayout());

        lstLaufendeNummernMeldung.setFixedCellWidth(75);
        lstLaufendeNummernMeldung.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                lstLaufendeNummernMeldungValueChanged(evt);
            }
        });
        scpLaufendeNummernMeldung.setViewportView(lstLaufendeNummernMeldung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpLaufendeMeldungen.add(scpLaufendeNummernMeldung, gridBagConstraints);

        semiRoundedPanel3.setBackground(Color.darkGray);
        semiRoundedPanel3.setLayout(new GridBagLayout());

        jLabel13.setForeground(new Color(255, 255, 255));
        jLabel13.setText(NbBundle.getMessage(BaumGebietEditor.class, "KkVerfahrenEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpLaufendeMeldungen.add(semiRoundedPanel3, gridBagConstraints);

        panControlsLaufendeNummernMeldung.setOpaque(false);
        panControlsLaufendeNummernMeldung.setLayout(new GridBagLayout());

        btnAddLaufendeNummerMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummerMeldung.setMaximumSize(new Dimension(43, 25));
        btnAddLaufendeNummerMeldung.setMinimumSize(new Dimension(43, 25));
        btnAddLaufendeNummerMeldung.setPreferredSize(new Dimension(43, 25));
        btnAddLaufendeNummerMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddLaufendeNummerMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsLaufendeNummernMeldung.add(btnAddLaufendeNummerMeldung, gridBagConstraints);

        btnRemoveLaufendeNummerMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummerMeldung.setMaximumSize(new Dimension(43, 25));
        btnRemoveLaufendeNummerMeldung.setMinimumSize(new Dimension(43, 25));
        btnRemoveLaufendeNummerMeldung.setPreferredSize(new Dimension(43, 25));
        btnRemoveLaufendeNummerMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveLaufendeNummerMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsLaufendeNummernMeldung.add(btnRemoveLaufendeNummerMeldung, gridBagConstraints);

        btnCopyBaulast.setIcon(new ImageIcon(getClass().getResource("/res/16/document-copy.png"))); // NOI18N
        btnCopyBaulast.setMaximumSize(new Dimension(43, 25));
        btnCopyBaulast.setMinimumSize(new Dimension(43, 25));
        btnCopyBaulast.setPreferredSize(new Dimension(43, 25));
        btnCopyBaulast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCopyBaulastActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsLaufendeNummernMeldung.add(btnCopyBaulast, gridBagConstraints);

        btnPasteBaulast.setIcon(new ImageIcon(getClass().getResource("/res/16/clipboard-paste.png"))); // NOI18N
        btnPasteBaulast.setMaximumSize(new Dimension(43, 25));
        btnPasteBaulast.setMinimumSize(new Dimension(43, 25));
        btnPasteBaulast.setPreferredSize(new Dimension(43, 25));
        btnPasteBaulast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnPasteBaulastActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsLaufendeNummernMeldung.add(btnPasteBaulast, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        rpLaufendeMeldungen.add(panControlsLaufendeNummernMeldung, gridBagConstraints);

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

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(panFillerUnten1, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblAktenzeichen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAktenzeichen.setText("Aktenzeichen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAktenzeichen, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.aktenzeichen}"), txtAktenzeichen, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtAktenzeichen, gridBagConstraints);

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStrasse, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.strasse}"), txtStrasse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtStrasse, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        txtHnr.setName(""); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hausnummer}"), txtHnr, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        panZusatz.setOpaque(false);
        panZusatz.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panZusatz, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        panDaten.add(panFiller, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 6;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 0, 5, 0);
        panDaten.add(sepStatus, gridBagConstraints);

        lblStatus.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStatus.setText("Status:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStatus, gridBagConstraints);

        cbStatus.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbStatus.setMaximumRowCount(6);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_status}"), cbStatus, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStatus, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        jPanel1.add(panDaten, gridBagConstraints);

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        jPanel1.add(panGeometrie, gridBagConstraints);

        panMeldung.setOpaque(false);
        panMeldung.setLayout(new GridBagLayout());

        rpMeldungliste.setMinimumSize(new Dimension(80, 202));
        rpMeldungliste.setPreferredSize(new Dimension(250, 202));
        rpMeldungliste.setLayout(new GridBagLayout());

        lstMeldungen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstMeldungen.setFixedCellWidth(75);
        lstMeldungen.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                lstMeldungenValueChanged(evt);
            }
        });
        scpLaufendeMeldungen.setViewportView(lstMeldungen);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpMeldungliste.add(scpLaufendeMeldungen, gridBagConstraints);

        semiRoundedPanel4.setBackground(Color.darkGray);
        semiRoundedPanel4.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanel4.setLayout(new GridBagLayout());

        lblMeldungen.setForeground(new Color(255, 255, 255));
        lblMeldungen.setText(NbBundle.getMessage(BaumGebietEditor.class, "BaumGebietEditor.lblMeldungen.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(lblMeldungen, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new Dimension(1, 1));

        GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 161, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel4.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpMeldungliste.add(semiRoundedPanel4, gridBagConstraints);

        panControlsNewMeldungen.setOpaque(false);
        panControlsNewMeldungen.setLayout(new GridBagLayout());

        btnAddNewMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewMeldung.setMaximumSize(new Dimension(39, 20));
        btnAddNewMeldung.setMinimumSize(new Dimension(39, 20));
        btnAddNewMeldung.setPreferredSize(new Dimension(39, 25));
        btnAddNewMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddNewMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewMeldungen.add(btnAddNewMeldung, gridBagConstraints);

        btnRemoveMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveMeldung.setMaximumSize(new Dimension(39, 20));
        btnRemoveMeldung.setMinimumSize(new Dimension(39, 20));
        btnRemoveMeldung.setPreferredSize(new Dimension(39, 25));
        btnRemoveMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewMeldungen.add(btnRemoveMeldung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpMeldungliste.add(panControlsNewMeldungen, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panMeldung.add(rpMeldungliste, gridBagConstraints);

        rpMeldunginfo.setLayout(new GridBagLayout());

        semiRoundedPanel5.setBackground(Color.darkGray);
        semiRoundedPanel5.setLayout(new GridBagLayout());

        lblMeldung.setForeground(new Color(255, 255, 255));
        lblMeldung.setText(NbBundle.getMessage(BaumGebietEditor.class, "BaumGebietEditor.lblMeldung.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblMeldung, gridBagConstraints);

        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new Dimension(1, 1));

        GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 623, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel5.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpMeldunginfo.add(semiRoundedPanel5, gridBagConstraints);

        panMeldungenMain.setOpaque(false);
        panMeldungenMain.setLayout(new GridBagLayout());

        GroupLayout panMeldungpanLayout = new GroupLayout(panMeldungpan);
        panMeldungpan.setLayout(panMeldungpanLayout);
        panMeldungpanLayout.setHorizontalGroup(panMeldungpanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 694, Short.MAX_VALUE)
        );
        panMeldungpanLayout.setVerticalGroup(panMeldungpanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 329, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        panMeldungenMain.add(panMeldungpan, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpMeldunginfo.add(panMeldungenMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panMeldung.add(rpMeldunginfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        jPanel1.add(panMeldung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(jPanel1, gridBagConstraints);

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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortMeldungActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortMeldungActionPerformed
        dlgAddMeldung.setVisible(false);
    }//GEN-LAST:event_btnMenAbortMeldungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkMeldungActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkMeldungActionPerformed
        try {
            final CidsBean beanMeldung = CidsBeanSupport.createNewCidsBeanFromTableName(
                "baum_meldung",
                getConnectionContext());
            final int gebietId = cidsBean.getPrimaryKeyValue();
            beanMeldung.setProperty("n_gebiet", cidsBean);
            
            final java.util.Date selDate = dcMeldung.getDate();
            java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date beanDate = new java.sql.Date(cal.getTime().getTime());
            
            beanMeldung.setProperty("datum", beanDate);
            final List<CidsBean> beanList = (List<CidsBean>) lstMeldungen.getModel();
            beanList.add(beanMeldung);
            final DefaultListModel<CidsBean> dlmMeldungen = new DefaultListModel<CidsBean>();
            //final CustomJListModel<CidsBean> dlmMeldungen = new DefaultListModel<CidsBean>();
            final List<CidsBean> c = cidsBean.getBeanCollectionProperty("datum");
            dlmMeldungen.setSize(c.size());
            for (int i = 0; i < c.size(); ++i) {
                dlmMeldungen.addElement(beanList.get(i));
            }
            lstMeldungen.setModel(dlmMeldungen);
            //((DivBeanTable)lstMeldungen.getModel()).addBean(beanMeldung);
            //((CustomJListModel)lstMeldungen.getModel()).refresh(); //
            lstMeldungen.setSelectedValue(beanMeldung, true);
            lstMeldungenValueChanged(null);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddMeldung.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkMeldungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkPfandActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkPfandActionPerformed
        try {
            final Object selItem = cbPfand.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__NAME,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__NAME);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddPfand.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkPfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortPfandActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortPfandActionPerformed
        dlgAddPfand.setVisible(false);
    }//GEN-LAST:event_btnMenAbortPfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkSteckerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkSteckerActionPerformed
        try {
            final Object selItem = cbStecker.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__NAME,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__NAME);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddStecker.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortSteckerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortSteckerActionPerformed
        dlgAddStecker.setVisible(false);
    }//GEN-LAST:event_btnMenAbortSteckerActionPerformed

    private void lstMeldungenValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstMeldungenValueChanged
        final Object o = lstMeldungen.getSelectedValue();
        if (o instanceof CidsBean) {
            final CidsBean bean = (CidsBean)o;
            setEdMeldungBean(bean);
        } else {
            setEdMeldungBean(null);
        }

        refreshLabels();
    }//GEN-LAST:event_lstMeldungenValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  edMeldungBean  DOCUMENT ME!
     */
    private void setEdMeldungBean(final CidsBean edMeldungBean) {
        //edMeldung.setCidsBean(edMeldungBean);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshLabels() {
    /*    final CidsBean bean = edMeldung.getCidsBean();

        if (bean != null) {
            lblMeldung.setText("Meldung: " + toString(bean.getProperty("schluessel")) + "  "
                        + toString(bean.getProperty("name")));
        } else {
            lblMeldung.setText("Fläche");
        }
        lstMeldungen.repaint();

        if (edMeldung.getCidsBean() != null) {
            lstMeldungen.setSelectedValue(edMeldung.getCidsBean(), true);
        }*/
    }

    private String toString(final Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }
   
    
    private void btnAddNewMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewMeldungActionPerformed
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumGebietEditor.this), dlgAddMeldung, true);
        /*    final CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName(
                "baum_meldung",
                getConnectionContext());
            final String schluessel = getSchluessel();

            if (schluessel == null) {
                LOG.error("Cannot determine new value for property schluessel");
                JOptionPane.showMessageDialog(
                    this,
                    NbBundle.getMessage(
                        BaumGebietEditor.class,
                        "BaumGebietEditor.btnAddLaufendeNummer1ActionPerformed.message"),
                    NbBundle.getMessage(
                        BaumGebietEditor.class,
                        "BaumGebietEditor.btnAddLaufendeNummer1ActionPerformed.title"),
                    JOptionPane.ERROR_MESSAGE);

                return;
            }

            bean.setProperty("schluessel", schluessel);

            cidsBean.addCollectionElement("meldungen", bean);
            ((CustomJListModel)lstMeldungen.getModel()).refresh();
            lstMeldungen.setSelectedValue(bean, true);
            lstMeldungenValueChanged(null);*/
        } catch (Exception e) {
            LOG.error("Cannot add new BaumGebiet object", e);
        }
    }//GEN-LAST:event_btnAddNewMeldungActionPerformed

    private void btnRemoveMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveMeldungActionPerformed
        final Object selectedObject = lstMeldungen.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            final List<CidsBean> meldungBeans = cidsBean.getBeanCollectionProperty("meldungen");

            if (meldungBeans != null) {
                meldungBeans.remove((CidsBean)selectedObject);
                ((CustomJListModel)lstMeldungen.getModel()).refresh();
                lstMeldungen.getSelectionModel().clearSelection();
                lstMeldungenValueChanged(null);
            }
        }
    }//GEN-LAST:event_btnRemoveMeldungActionPerformed

    private void lstLaufendeNummernMeldungValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstLaufendeNummernMeldungValueChanged

    }//GEN-LAST:event_lstLaufendeNummernMeldungValueChanged

    private void btnAddLaufendeNummerMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddLaufendeNummerMeldungActionPerformed

    }//GEN-LAST:event_btnAddLaufendeNummerMeldungActionPerformed

    private void btnRemoveLaufendeNummerMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveLaufendeNummerMeldungActionPerformed

    }//GEN-LAST:event_btnRemoveLaufendeNummerMeldungActionPerformed

    private void btnCopyBaulastActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCopyBaulastActionPerformed

    }//GEN-LAST:event_btnCopyBaulastActionPerformed

    private void btnPasteBaulastActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPasteBaulastActionPerformed

    }//GEN-LAST:event_btnPasteBaulastActionPerformed

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

    /**
     * DOCUMENT ME!
     */
 /*   private void doWithFotoUrl() {
        final String foto = EmobConfProperties.getInstance().getFotoUrlRaeder().concat(txtFoto.getText());
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
    }*/

    /**
     * DOCUMENT ME!
     */
    private void checkName() {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(TABLE_NAME,
            " where "
                    + FIELD__NAME
                    + " ilike '"
                    + txtAktenzeichen.getText().trim()
                    + "' and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            FIELD__NAME,
            OtherTableCases.REDUNDANT_ATT_NAME);
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtAktenzeichen.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_DUPLICATENAME));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Straße muss angegeben werden
        try {
            if (txtStrasse.getText().trim().isEmpty()) {
                LOG.warn("No street specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOSTREET));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Street not given.", ex);
            save = false;
        }
       

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGGEOM));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE),
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
                LOG.info("remove propchange baum_gebiet: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange baum_gebiet: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            
            //  Damit die Termine fuer die Meldungen sortiert in der Liste erscheinen.
            final String myWhere = " where "
                    + "n_gebiet"
                    + " = "
                    + cidsBean.getPrimaryKeyValue();
            final MetaObject[] metaObjectMeldungen = getOtherTableValues("baum_meldung", myWhere, getConnectionContext());
            //final List<CidsBean> c = cidsBean.getBeanCollectionProperty("berechnungen");
           
            final DefaultListModel<CidsBean> dlmMeldungen = new DefaultListModel<CidsBean>();
            //final CustomJListModel<CidsBean> dlmMeldungen = new DefaultListModel<CidsBean>();
            dlmMeldungen.setSize(metaObjectMeldungen.length);
            for (int i = 0; i < metaObjectMeldungen.length; ++i) {
                dlmMeldungen.addElement(metaObjectMeldungen[i].getBean());
            }
            lstMeldungen.setModel(dlmMeldungen);
            lstMeldungen.setSelectedIndex(1);
        
            

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            
            bindingGroup.bind();
         /*   lstMeldungen.setModel(new CustomJListModel("meldungen"));
            if (lstMeldungen.getModel().getSize() > 0) {
                lstMeldungen.setSelectedIndex(0);
            }*/
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
            RendererTools.makeReadOnly(txtAktenzeichen);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtHnr);
            RendererTools.makeReadOnly(taBemerkung);
            lblGeom.setVisible(isEditor);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CustomJListModel extends AbstractListModel<CidsBean> {

        //~ Instance fields ----------------------------------------------------

        private String listPropertyName;
        private Comparator beanComparator = new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final String o1String = String.valueOf(o1.getProperty("datum"));
                    final String o2String = String.valueOf(o2.getProperty("datum"));

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

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CustomJListModel object.
         *
         * @param  listPropertyName  DOCUMENT ME!
         */
        public CustomJListModel(final String listPropertyName) {
            this.listPropertyName = listPropertyName;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private List<CidsBean> getBeanList() {
            if ((cidsBean != null) && (listPropertyName != null)) {
                final Object colObj = cidsBean.getProperty(listPropertyName);
                if (colObj instanceof Collection) {
                    return (List<CidsBean>)colObj;
                }
            }
            return null;
        }

        /**
         * DOCUMENT ME!
         */
        public void refresh() {
            fireContentsChanged(this, 0, getBeanList().size() - 1);
        }

        @Override
        public int getSize() {
            return getBeanList().size();
        }

        @Override
        public CidsBean getElementAt(final int index) {
            final List<CidsBean> l = new ArrayList<CidsBean>();
            l.addAll(getBeanList());

            Collections.sort(l, beanComparator);

            return l.get(index);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ImageIcon loadPicture(final URL url) {
        try {
            final int bildZielBreite = FOTO_WIDTH;
            final BufferedImage originalBild = ImageIO.read(WebAccessManager.getInstance().doRequest(url));
            final Image skaliertesBild = originalBild.getScaledInstance(bildZielBreite, -1, Image.SCALE_SMOOTH);
            return new ImageIcon(skaliertesBild);
        } catch (final Exception ex) {
            LOG.error("Could not load picture.", ex);
            return null;
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = EmobConfProperties.getInstance().getBufferMeter();
            if (cb.getProperty(FIELD__GEOREFERENZ) != null) {
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

    @Override
    public String getTitle() {
        return cidsBean.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        dlgAddMeldung.dispose();
        dlgAddStecker.dispose();
        dlgAddPfand.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
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
    }
    
     private final class MeldungSelectionListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                final CidsBean bean = (CidsBean)lstMeldungen.getSelectedValue();
                if (bean == null) {
                    throw new IllegalStateException("no calculation selected, this is illegal"); // NOI18N
                }
                //BaumMeldungPanel.se
                //oab_berechnungEditor.setCidsBean(bean);
            }
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  showLabel  DOCUMENT ME!
     */
    private void checkUrl(final String url, final JLabel showLabel) {
        showLabel.setIcon(statusFalsch);
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return WebAccessManager.getInstance().checkIfURLaccessible(new URL(url));
                }

                @Override
                protected void done() {
                    final Boolean check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            if (check) {
                                showLabel.setIcon(statusOk);
                                showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            } else {
                                showLabel.setIcon(statusFalsch);
                                showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("URL Check Problem in Worker.", e);
                    }
                }
            };
        if (worker_foto != null) {
            worker_foto.cancel(true);
        }
        worker_foto = worker;
        worker_foto.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  showLabel  DOCUMENT ME!
     */
    private void loadPictureWithUrl(final String url, final JLabel showLabel) {
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {

                @Override
                protected ImageIcon doInBackground() throws Exception {
                    return loadPicture(new URL(url));
                }

                @Override
                protected void done() {
                    final ImageIcon check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            if (check != null) {
                                showLabel.setIcon(check);
                                showLabel.setText("");
                                showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            } else {
                                showLabel.setIcon(null);
                                showLabel.setText(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOLOAD));
                                showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setText(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOLOAD));
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("load picture Problem in Worker.", e);
                    }
                }
            };
        if (worker_loadFoto != null) {
            worker_loadFoto.cancel(true);
        }
        worker_loadFoto = worker;
        worker_loadFoto.execute();
    }
    /**
     * DOCUMENT ME!
     *
     * @param  tableName     DOCUMENT ME!
     * @param  whereClause   DOCUMENT ME!
     * @param  propertyName  DOCUMENT ME!
     * @param  fall          DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName,
            final String whereClause,
            final String propertyName,
            final OtherTableCases fall) {
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
                            if (check != null) {
                                switch (fall) {
                                    case SET_VALUE: {          // set default value
                                        try {
                                            cidsBean.setProperty(
                                                propertyName,
                                                check);
                                        } catch (Exception ex) {
                                            LOG.warn("setVersatz: Versatz not set.", ex);
                                        }
                                        break;
                                    }
                                    case REDUNDANT_ATT_NAME: { // check redundant name
                                        redundantName = true;
                                        break;
                                    }
                                }
                            } else {
                                switch (fall) {
                                    case REDUNDANT_ATT_NAME: { // check redundant name
                                        redundantName = false;
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
        if (fall.equals(OtherTableCases.REDUNDANT_ATT_NAME)) {
            if (worker_name != null) {
                worker_name.cancel(true);
            }
            worker_name = worker;
            worker_name.execute();
        } else {
            if (worker_versatz != null) {
                worker_versatz.cancel(true);
            }
            worker_versatz = worker;
            worker_versatz.execute();
        }
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
    }
