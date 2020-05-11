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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

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

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

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
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
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
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobradLadestationEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TEXT_OPEN = "24 Stunden / 7 Tage";
    public static final String TEXT_BOX = "0";
    public static final String GEOMTYPE = "Point";
    public static final int FOTO_WIDTH = 150;

    private static final Logger LOG = Logger.getLogger(EmobradLadestationEditor.class);

    public static final String FIELD__NAME = "standort";                            // emobrad_ladestation
    public static final String FIELD__ID = "id";                                    // emobrad_ladestation
    public static final String FIELD__FACH = "anzahl_schliessfaeche";               // emobrad_ladestation
    public static final String FIELD__DOSE = "anzahl_fach_steckdosen";              // emobrad_ladestation
    public static final String FIELD__ZUGANG = "arr_zugangsart";                    // emobrad_ladestation
    public static final String FIELD__STECKER = "arr_stecker";                      // emobrad_ladestation
    public static final String FIELD__PFAND = "arr_pfand";                          // emobrad_ladestation
    public static final String FIELD__VERSATZ = "fk_versatz";                       // emobrad_ladestation
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // emobrad_ladestation
    public static final String FIELD__SCHLUESSEL = "schluessel";                    // emobrad_versatz
    public static final String VERSATZ_ZENTRAL_SCHLUESSEL = "0";                    // emobrad_versatz.schluessel
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // emobrad_ladestation_geom
    public static final String FIELD__FOTO = "foto";                                // emobrad_ladestation
    public static final String FIELD__LADEKOSTEN = "fk_ladekosten";                 // emobrad_ladestation
    public static final String FIELD__LADEKOSTEN_NAME = "fk_ladekosten.name";       // emobrad_ladekosten
    public static final String TABLE_NAME = "emobrad_ladestation";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME_VERSATZ = "emob_versatz";

    public static final String BUNDLE_NOLOAD = "EmobradLadestationEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NONAME = "EmobradLadestationEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "EmobradLadestationEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "EmobradLadestationEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOCOUNT = "EmobradLadestationEditor.prepareForSave().noAnzahl";
    public static final String BUNDLE_WRONGCOUNT = "EmobradLadestationEditor.prepareForSave().wrongAnzahl";
    public static final String BUNDLE_NOOPEN = "EmobradLadestationEditor.prepareForSave().noOpen";
    public static final String BUNDLE_NOGEOM = "EmobradLadestationEditor.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "EmobradLadestationEditor.prepareForSave().wrongGeom";
    public static final String BUNDLE_NOSCHLIESSFACH = "EmobradLadestationEditor.prepareForSave().noSchliessfach";
    public static final String BUNDLE_WRONGSCHLIESSFACH = "EmobradLadestationEditor.prepareForSave().wrongSchliessfach";
    public static final String BUNDLE_NOSTECKDOSE = "EmobradLadestationEditor.prepareForSave().noSteckdose";
    public static final String BUNDLE_WRONGSTECKDOSE = "EmobradLadestationEditor.prepareForSave().wrongSteckdose";
    public static final String BUNDLE_NOOPERATOR = "EmobradLadestationEditor.prepareForSave().noBetreiber";
    public static final String BUNDLE_PANE_PREFIX =
        "EmobradLadestationEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "EmobradLadestationEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EmobradLadestationEditor.prepareForSave().JOptionPane.title";

    public static final String BUNDLE_REMZUG_QUESTION =
        "EmobradLadestationEditor.btnRemoveZugangActionPerformed().question";
    public static final String BUNDLE_REMZUG_TITLE = "EmobradLadestationEditor.btnRemoveZugangActionPerformed().title";
    public static final String BUNDLE_REMZUG_ERRORTITLE =
        "EmobradLadestationEditor.btnRemoveZugangActionPerformed().errortitle";
    public static final String BUNDLE_REMZUG_ERRORTEXT =
        "EmobradLadestationEditor.btnRemoveZugangActionPerformed().errortext";

    public static final String BUNDLE_REMSTE_QUESTION =
        "EmobradLadestationEditor.btnRemoveSteckerActionPerformed().question";
    public static final String BUNDLE_REMSTE_TITLE = "EmobradLadestationEditor.btnRemoveSteckerActionPerformed().title";
    public static final String BUNDLE_REMSTE_ERRORTITLE =
        "EmobradLadestationEditor.btnRemoveSteckerActionPerformed().errortitle";
    public static final String BUNDLE_REMSTE_ERRORTEXT =
        "EmobradLadestationEditor.btnRemoveSteckerActionPerformed().errortext";

    public static final String BUNDLE_REMPFA_QUESTION =
        "EmobradLadestationEditor.btnRemovePfandActionPerformed().question";
    public static final String BUNDLE_REMPFA_TITLE = "EmobradLadestationEditor.btnRemovePfandActionPerformed().title";
    public static final String BUNDLE_REMPFA_ERRORTITLE =
        "EmobradLadestationEditor.btnRemovePfandActionPerformed().errortitle";
    public static final String BUNDLE_REMPFA_ERRORTEXT =
        "EmobradLadestationEditor.btnRemovePfandActionPerformed().errortext";

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
    private javax.swing.JButton btnAddPfand;
    private javax.swing.JButton btnAddStecker;
    private javax.swing.JButton btnAddZugang;
    private javax.swing.JButton btnMenAbortPfand;
    private javax.swing.JButton btnMenAbortStecker;
    private javax.swing.JButton btnMenAbortZugang;
    private javax.swing.JButton btnMenOkPfand;
    private javax.swing.JButton btnMenOkStecker;
    private javax.swing.JButton btnMenOkZugang;
    private javax.swing.JButton btnRemovePfand;
    private javax.swing.JButton btnRemoveStecker;
    private javax.swing.JButton btnRemoveZugang;
    private javax.swing.JComboBox<String> cbAbrechnung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbBetreiber;
    private javax.swing.JComboBox cbGeom;
    private javax.swing.JComboBox cbPfand;
    private javax.swing.JComboBox cbStecker;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbVersatz;
    private javax.swing.JComboBox cbZugang;
    private javax.swing.JCheckBox chGruen;
    private javax.swing.JCheckBox chHalb;
    private javax.swing.JCheckBox chLadebox;
    private javax.swing.JCheckBox chOnline;
    private javax.swing.JDialog dlgAddPfand;
    private javax.swing.JDialog dlgAddStecker;
    private javax.swing.JDialog dlgAddZugang;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JFormattedTextField ftxtAnzahlLadepunkte;
    private javax.swing.JFormattedTextField ftxtSchliessfaecher;
    private javax.swing.JFormattedTextField ftxtSteckdosen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAbrechnung;
    private javax.swing.JLabel lblAnzahl;
    private javax.swing.JLabel lblAnzahlLadepunkte;
    private javax.swing.JLabel lblAuswaehlenPfand;
    private javax.swing.JLabel lblAuswaehlenStecker;
    private javax.swing.JLabel lblAuswaehlenZugang;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBetreiber;
    private javax.swing.JLabel lblDetailbeschreibung;
    private javax.swing.JLabel lblFoto;
    private javax.swing.JLabel lblFotoAnzeigen;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblGruen;
    private javax.swing.JLabel lblHalb;
    private javax.swing.JLabel lblHnr;
    private javax.swing.JLabel lblKarte;
    private javax.swing.JLabel lblLadebox;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOffen;
    private javax.swing.JLabel lblOnline;
    private javax.swing.JLabel lblPfand;
    private javax.swing.JLabel lblSchliessfaecher;
    private javax.swing.JLabel lblSteckdosen;
    private javax.swing.JLabel lblStecker;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblUrlCheck;
    private javax.swing.JLabel lblVersatz;
    private javax.swing.JLabel lblZugang;
    private javax.swing.JLabel lblZusatz;
    private javax.swing.JList lstPfand;
    private javax.swing.JList lstStecker;
    private javax.swing.JList lstZugang;
    private javax.swing.JPanel panAbrechnung;
    private javax.swing.JPanel panAddPfand;
    private javax.swing.JPanel panAddStecker;
    private javax.swing.JPanel panAddZugang;
    private javax.swing.JPanel panBemerkung;
    private javax.swing.JPanel panButtonsPfand;
    private javax.swing.JPanel panButtonsStecker;
    private javax.swing.JPanel panButtonsZugang;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panDaten;
    private javax.swing.JPanel panDetailbeschreibung;
    private javax.swing.JPanel panFiller;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUnten1;
    private javax.swing.JPanel panFillerUntenFoto;
    private javax.swing.JPanel panGeometrie;
    private javax.swing.JPanel panLage;
    private javax.swing.JPanel panMenButtonsPfand;
    private javax.swing.JPanel panMenButtonsStecker;
    private javax.swing.JPanel panMenButtonsZugang;
    private javax.swing.JPanel panOffen;
    private javax.swing.JPanel panOnline;
    private javax.swing.JPanel panPfand;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private javax.swing.JPanel panStecker;
    private javax.swing.JPanel panUrl;
    private javax.swing.JPanel panZugang;
    private javax.swing.JPanel panZusatz;
    private de.cismet.tools.gui.RoundedPanel rpKarte;
    private javax.swing.JScrollPane scpAbrechnung;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpDetailbeschreibung;
    private javax.swing.JScrollPane scpLstPfand;
    private javax.swing.JScrollPane scpLstStecker;
    private javax.swing.JScrollPane scpLstZugang;
    private javax.swing.JScrollPane scpOffen;
    private javax.swing.JScrollPane scpZusatz;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private javax.swing.JSeparator sepOnline;
    javax.swing.JSpinner spAnzahl;
    private javax.swing.JTextArea taAbrechnung;
    private javax.swing.JTextArea taBemerkung;
    private javax.swing.JTextArea taDetailbeschreibung;
    private javax.swing.JTextArea taOffen;
    private javax.swing.JTextArea taZusatz;
    private javax.swing.JTextField txtFoto;
    private javax.swing.JTextField txtHnr;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtStrasse;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EmobradLadestationEditor() {
    }

    /**
     * Creates a new EmobradLadestationEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EmobradLadestationEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        txtName.getDocument().addDocumentListener(new DocumentListener() {

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

        txtFoto.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn das Foto geändert wird, wird dieses überprüft und neu geladen.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }
            });

        dlgAddZugang.pack();
        dlgAddZugang.getRootPane().setDefaultButton(btnMenOkZugang);
        dlgAddStecker.pack();
        dlgAddStecker.getRootPane().setDefaultButton(btnMenOkStecker);
        dlgAddPfand.pack();
        dlgAddPfand.getRootPane().setDefaultButton(btnMenOkPfand);

        if (isEditor) {
            ((DefaultBindableScrollableComboBox)this.cbAbrechnung).setNullable(true);
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
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        dlgAddZugang = new javax.swing.JDialog();
        panAddZugang = new javax.swing.JPanel();
        lblAuswaehlenZugang = new javax.swing.JLabel();
        final MetaObject[] zugang = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils
                    .getLightweightMetaObjectsForTable(
                        "emobrad_zugangsart",
                        new String[] { "name" },
                        getConnectionContext());
        if (zugang != null) {
            Arrays.sort(zugang);
            cbZugang = new javax.swing.JComboBox(zugang);
        }
        panMenButtonsZugang = new javax.swing.JPanel();
        btnMenAbortZugang = new javax.swing.JButton();
        btnMenOkZugang = new javax.swing.JButton();
        dlgAddStecker = new javax.swing.JDialog();
        panAddStecker = new javax.swing.JPanel();
        lblAuswaehlenStecker = new javax.swing.JLabel();
        final MetaObject[] stecker = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils
                    .getLightweightMetaObjectsForTable(
                        "emobrad_stecker",
                        new String[] { "schluessel" },
                        getConnectionContext());
        if (stecker != null) {
            Arrays.sort(stecker); // ||
            cbStecker = new javax.swing.JComboBox(stecker);
        }
        panMenButtonsStecker = new javax.swing.JPanel();
        btnMenAbortStecker = new javax.swing.JButton();
        btnMenOkStecker = new javax.swing.JButton();
        dlgAddPfand = new javax.swing.JDialog();
        panAddPfand = new javax.swing.JPanel();
        lblAuswaehlenPfand = new javax.swing.JLabel();
        final MetaObject[] pfand = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils
                    .getLightweightMetaObjectsForTable(
                        "emobrad_pfand",
                        new String[] { "name" },
                        getConnectionContext());
        if (pfand != null) {
            Arrays.sort(pfand);
            cbPfand = new javax.swing.JComboBox(pfand);
            panMenButtonsPfand = new javax.swing.JPanel();
            btnMenAbortPfand = new javax.swing.JButton();
            btnMenOkPfand = new javax.swing.JButton();
            panFillerUnten = new javax.swing.JPanel();
            panContent = new RoundedPanel();
            jPanel1 = new javax.swing.JPanel();
            panFillerUnten1 = new javax.swing.JPanel();
            panDaten = new javax.swing.JPanel();
            lblName = new javax.swing.JLabel();
            txtName = new javax.swing.JTextField();
            lblStrasse = new javax.swing.JLabel();
            txtStrasse = new javax.swing.JTextField();
            filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(32767, 0));
            lblHnr = new javax.swing.JLabel();
            txtHnr = new javax.swing.JTextField();
            lblBetreiber = new javax.swing.JLabel();
            cbBetreiber = new DefaultBindableReferenceCombo(true);
            lblHalb = new javax.swing.JLabel();
            chHalb = new javax.swing.JCheckBox();
            lblOffen = new javax.swing.JLabel();
            panOffen = new javax.swing.JPanel();
            scpOffen = new javax.swing.JScrollPane();
            taOffen = new javax.swing.JTextArea();
            lblZusatz = new javax.swing.JLabel();
            panZusatz = new javax.swing.JPanel();
            scpZusatz = new javax.swing.JScrollPane();
            taZusatz = new javax.swing.JTextArea();
            lblDetailbeschreibung = new javax.swing.JLabel();
            panDetailbeschreibung = new javax.swing.JPanel();
            scpDetailbeschreibung = new javax.swing.JScrollPane();
            taDetailbeschreibung = new javax.swing.JTextArea();
            lblBemerkung = new javax.swing.JLabel();
            panBemerkung = new javax.swing.JPanel();
            scpBemerkung = new javax.swing.JScrollPane();
            taBemerkung = new javax.swing.JTextArea();
            lblFoto = new javax.swing.JLabel();
            txtFoto = new javax.swing.JTextField();
            panUrl = new javax.swing.JPanel();
            lblUrlCheck = new javax.swing.JLabel();
            lblFotoAnzeigen = new javax.swing.JLabel();
            filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(32767, 0));
            lblGruen = new javax.swing.JLabel();
            chGruen = new javax.swing.JCheckBox();
            lblAnzahl = new javax.swing.JLabel();
            spAnzahl = new javax.swing.JSpinner();
            lblAnzahlLadepunkte = new javax.swing.JLabel();
            ftxtAnzahlLadepunkte = new javax.swing.JFormattedTextField();
            lblLadebox = new javax.swing.JLabel();
            chLadebox = new javax.swing.JCheckBox();
            lblSchliessfaecher = new javax.swing.JLabel();
            ftxtSchliessfaecher = new javax.swing.JFormattedTextField();
            lblSteckdosen = new javax.swing.JLabel();
            ftxtSteckdosen = new javax.swing.JFormattedTextField();
            panFillerUntenFoto = new javax.swing.JPanel();
            lblAbrechnung = new javax.swing.JLabel();
            panAbrechnung = new javax.swing.JPanel();
            scpAbrechnung = new javax.swing.JScrollPane();
            taAbrechnung = new javax.swing.JTextArea();
            if (isEditor) {
                cbAbrechnung = new DefaultBindableScrollableComboBox();
            }
            panFiller = new javax.swing.JPanel();
            lblZugang = new javax.swing.JLabel();
            panZugang = new javax.swing.JPanel();
            scpLstZugang = new javax.swing.JScrollPane();
            lstZugang = new javax.swing.JList();
            panButtonsZugang = new javax.swing.JPanel();
            btnAddZugang = new javax.swing.JButton();
            btnRemoveZugang = new javax.swing.JButton();
            filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 32767));
            lblStecker = new javax.swing.JLabel();
            panStecker = new javax.swing.JPanel();
            scpLstStecker = new javax.swing.JScrollPane();
            lstStecker = new javax.swing.JList();
            panButtonsStecker = new javax.swing.JPanel();
            btnAddStecker = new javax.swing.JButton();
            btnRemoveStecker = new javax.swing.JButton();
            filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 32767));
            panPfand = new javax.swing.JPanel();
            scpLstPfand = new javax.swing.JScrollPane();
            lstPfand = new javax.swing.JList();
            panButtonsPfand = new javax.swing.JPanel();
            btnAddPfand = new javax.swing.JButton();
            btnRemovePfand = new javax.swing.JButton();
            filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(0, 32767));
            lblPfand = new javax.swing.JLabel();
            panGeometrie = new javax.swing.JPanel();
            lblGeom = new javax.swing.JLabel();
            if (isEditor) {
                cbGeom = new DefaultCismapGeometryComboBoxEditor();
            }
            panLage = new javax.swing.JPanel();
            rpKarte = new de.cismet.tools.gui.RoundedPanel();
            panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
            semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
            lblKarte = new javax.swing.JLabel();
            lblVersatz = new javax.swing.JLabel();
            cbVersatz = new DefaultBindableReferenceCombo(true);
            panOnline = new javax.swing.JPanel();
            sepOnline = new javax.swing.JSeparator();
            lblOnline = new javax.swing.JLabel();
            chOnline = new javax.swing.JCheckBox();

            dlgAddZugang.setTitle("Zugangsart");
            dlgAddZugang.setModal(true);

            panAddZugang.setLayout(new java.awt.GridBagLayout());

            lblAuswaehlenZugang.setText("Bitte Zugangsart auswählen:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            panAddZugang.add(lblAuswaehlenZugang, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panAddZugang.add(cbZugang, gridBagConstraints);

            panMenButtonsZugang.setLayout(new java.awt.GridBagLayout());

            btnMenAbortZugang.setText("Abbrechen");
            btnMenAbortZugang.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnMenAbortZugangActionPerformed(evt);
                    }
                });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtonsZugang.add(btnMenAbortZugang, gridBagConstraints);

            btnMenOkZugang.setText("Ok");
            btnMenOkZugang.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnMenOkZugangActionPerformed(evt);
                    }
                });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtonsZugang.add(btnMenOkZugang, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panAddZugang.add(panMenButtonsZugang, gridBagConstraints);

            dlgAddZugang.getContentPane().add(panAddZugang, java.awt.BorderLayout.CENTER);

            dlgAddStecker.setTitle("Steckerverbindung");
            dlgAddStecker.setModal(true);

            panAddStecker.setLayout(new java.awt.GridBagLayout());

            lblAuswaehlenStecker.setText("Bitte Steckerverbindung auswählen:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            panAddStecker.add(lblAuswaehlenStecker, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panAddStecker.add(cbStecker, gridBagConstraints);

            panMenButtonsStecker.setLayout(new java.awt.GridBagLayout());

            btnMenAbortStecker.setText("Abbrechen");
            btnMenAbortStecker.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnMenAbortSteckerActionPerformed(evt);
                    }
                });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtonsStecker.add(btnMenAbortStecker, gridBagConstraints);

            btnMenOkStecker.setText("Ok");
            btnMenOkStecker.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnMenOkSteckerActionPerformed(evt);
                    }
                });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtonsStecker.add(btnMenOkStecker, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panAddStecker.add(panMenButtonsStecker, gridBagConstraints);

            dlgAddStecker.getContentPane().add(panAddStecker, java.awt.BorderLayout.CENTER);

            dlgAddPfand.setTitle("Pfandmünze");
            dlgAddPfand.setModal(true);

            panAddPfand.setLayout(new java.awt.GridBagLayout());

            lblAuswaehlenPfand.setText("Bitte Pfandmünze auswählen:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            panAddPfand.add(lblAuswaehlenPfand, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddPfand.add(cbPfand, gridBagConstraints);

        panMenButtonsPfand.setLayout(new java.awt.GridBagLayout());

        btnMenAbortPfand.setText("Abbrechen");
        btnMenAbortPfand.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbortPfandActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtonsPfand.add(btnMenAbortPfand, gridBagConstraints);

        btnMenOkPfand.setText("Ok");
        btnMenOkPfand.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOkPfandActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtonsPfand.add(btnMenOkPfand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddPfand.add(panMenButtonsPfand, gridBagConstraints);

        dlgAddPfand.getContentPane().add(panAddPfand, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final javax.swing.GroupLayout panFillerUntenLayout = new javax.swing.GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final javax.swing.GroupLayout panFillerUnten1Layout = new javax.swing.GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(panFillerUnten1, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        txtName.setToolTipText("");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.standort}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblStrasse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                txtStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(txtStrasse, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        txtHnr.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnummer}"),
                txtHnr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblBetreiber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBetreiber.setText("Betreiber:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblBetreiber, gridBagConstraints);

        cbBetreiber.setNullable(false);
        cbBetreiber.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cbBetreiber.setMaximumRowCount(6);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_betreiber}"),
                cbBetreiber,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(cbBetreiber, gridBagConstraints);

        lblHalb.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHalb.setText("halb-öffentlich:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblHalb, gridBagConstraints);

        chHalb.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.halb_oeffentlich}"),
                chHalb,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chHalb.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chHalbStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(chHalb, gridBagConstraints);

        lblOffen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOffen.setText("Öffnungszeiten:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblOffen, gridBagConstraints);

        panOffen.setOpaque(false);
        panOffen.setLayout(new java.awt.GridBagLayout());

        taOffen.setColumns(20);
        taOffen.setLineWrap(true);
        taOffen.setRows(2);
        taOffen.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.oeffnungszeiten}"),
                taOffen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpOffen.setViewportView(taOffen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOffen.add(scpOffen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panOffen, gridBagConstraints);

        lblZusatz.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblZusatz.setText("Zusatzinfo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblZusatz, gridBagConstraints);

        panZusatz.setOpaque(false);
        panZusatz.setLayout(new java.awt.GridBagLayout());

        taZusatz.setColumns(20);
        taZusatz.setLineWrap(true);
        taZusatz.setRows(2);
        taZusatz.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zusatzinfo}"),
                taZusatz,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpZusatz.setViewportView(taZusatz);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusatz.add(scpZusatz, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panZusatz, gridBagConstraints);

        lblDetailbeschreibung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDetailbeschreibung.setText("Details:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblDetailbeschreibung, gridBagConstraints);

        panDetailbeschreibung.setOpaque(false);
        panDetailbeschreibung.setLayout(new java.awt.GridBagLayout());

        taDetailbeschreibung.setLineWrap(true);
        taDetailbeschreibung.setRows(2);
        taDetailbeschreibung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.detailbeschreibung}"),
                taDetailbeschreibung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpDetailbeschreibung.setViewportView(taDetailbeschreibung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDetailbeschreibung.add(scpDetailbeschreibung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panDetailbeschreibung, gridBagConstraints);

        lblBemerkung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new java.awt.GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblFoto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFoto.setText("Foto:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblFoto, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.foto}"),
                txtFoto,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(txtFoto, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new java.awt.GridBagLayout());

        lblUrlCheck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panUrl, gridBagConstraints);

        lblFotoAnzeigen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 4);
        panDaten.add(lblFotoAnzeigen, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblGruen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGruen.setText("Grüner Strom:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblGruen, gridBagConstraints);

        chGruen.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gruener_strom}"),
                chGruen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(chGruen, gridBagConstraints);

        lblAnzahl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAnzahl.setText("Stellplätze:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblAnzahl, gridBagConstraints);

        spAnzahl.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        spAnzahl.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spAnzahl.setName("spAnzahl");                         // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_plaetze}"),
                spAnzahl,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(spAnzahl, gridBagConstraints);

        lblAnzahlLadepunkte.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAnzahlLadepunkte.setText("Ladepunkte:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblAnzahlLadepunkte, gridBagConstraints);

        ftxtAnzahlLadepunkte.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#####"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_ladepunkte}"),
                ftxtAnzahlLadepunkte,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(ftxtAnzahlLadepunkte, gridBagConstraints);

        lblLadebox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLadebox.setText("Ladebox:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblLadebox, gridBagConstraints);

        chLadebox.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ladebox_zu}"),
                chLadebox,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chLadebox.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chLadeboxStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(chLadebox, gridBagConstraints);

        lblSchliessfaecher.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSchliessfaecher.setText("Schließfächer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblSchliessfaecher, gridBagConstraints);

        ftxtSchliessfaecher.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#####"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_schliessfaecher}"),
                ftxtSchliessfaecher,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        ftxtSchliessfaecher.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ftxtSchliessfaecherActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(ftxtSchliessfaecher, gridBagConstraints);

        lblSteckdosen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSteckdosen.setText("Steckdosen je S.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblSteckdosen, gridBagConstraints);

        ftxtSteckdosen.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#####"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_fach_steckdosen}"),
                ftxtSteckdosen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        ftxtSteckdosen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ftxtSteckdosenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(ftxtSteckdosen, gridBagConstraints);

        panFillerUntenFoto.setName(""); // NOI18N
        panFillerUntenFoto.setOpaque(false);

        final javax.swing.GroupLayout panFillerUntenFotoLayout = new javax.swing.GroupLayout(panFillerUntenFoto);
        panFillerUntenFoto.setLayout(panFillerUntenFotoLayout);
        panFillerUntenFotoLayout.setHorizontalGroup(
            panFillerUntenFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUntenFotoLayout.setVerticalGroup(
            panFillerUntenFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panFillerUntenFoto, gridBagConstraints);

        lblAbrechnung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAbrechnung.setText("Ladekosten:");
        lblAbrechnung.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblAbrechnung, gridBagConstraints);

        panAbrechnung.setOpaque(false);
        panAbrechnung.setLayout(new java.awt.GridBagLayout());

        taAbrechnung.setEditable(false);
        taAbrechnung.setLineWrap(true);
        taAbrechnung.setRows(2);
        taAbrechnung.setToolTipText("");
        taAbrechnung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_ladekosten.name}"),
                taAbrechnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpAbrechnung.setViewportView(taAbrechnung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panAbrechnung.add(scpAbrechnung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panDaten.add(panAbrechnung, gridBagConstraints);

        if (isEditor) {
            cbAbrechnung.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            if (isEditor) {
                cbAbrechnung.setPreferredSize(new java.awt.Dimension(100, 24));
            }

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_ladekosten}"),
                    cbAbrechnung,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 28;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            panDaten.add(cbAbrechnung, gridBagConstraints);
        }

        panFiller.setMinimumSize(new java.awt.Dimension(20, 0));
        panFiller.setOpaque(false);

        final javax.swing.GroupLayout panFillerLayout = new javax.swing.GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                20,
                Short.MAX_VALUE));
        panFillerLayout.setVerticalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        panDaten.add(panFiller, gridBagConstraints);

        lblZugang.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblZugang.setText("Zugang:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblZugang, gridBagConstraints);

        panZugang.setLayout(new java.awt.GridBagLayout());

        scpLstZugang.setMinimumSize(new java.awt.Dimension(258, 66));

        lstZugang.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lstZugang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstZugang.setVisibleRowCount(3);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.arr_zugangsart}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstZugang);
        bindingGroup.addBinding(jListBinding);

        scpLstZugang.setViewportView(lstZugang);
        lstZugang.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panZugang.add(scpLstZugang, gridBagConstraints);

        panButtonsZugang.setOpaque(false);
        panButtonsZugang.setLayout(new java.awt.GridBagLayout());

        btnAddZugang.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddZugang.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsZugang.add(btnAddZugang, gridBagConstraints);

        btnRemoveZugang.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveZugang.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsZugang.add(btnRemoveZugang, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsZugang.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 2, 2);
        panZugang.add(panButtonsZugang, gridBagConstraints);
        panButtonsZugang.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panZugang, gridBagConstraints);

        lblStecker.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStecker.setText("Stecker:");
        lblStecker.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 32;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblStecker, gridBagConstraints);

        panStecker.setLayout(new java.awt.GridBagLayout());

        scpLstStecker.setMinimumSize(new java.awt.Dimension(258, 66));

        lstStecker.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lstStecker.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstStecker.setVisibleRowCount(2);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_stecker}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstStecker);
        bindingGroup.addBinding(jListBinding);

        scpLstStecker.setViewportView(lstStecker);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panStecker.add(scpLstStecker, gridBagConstraints);

        panButtonsStecker.setOpaque(false);
        panButtonsStecker.setLayout(new java.awt.GridBagLayout());

        btnAddStecker.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddStecker.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddSteckerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsStecker.add(btnAddStecker, gridBagConstraints);

        btnRemoveStecker.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveStecker.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveSteckerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsStecker.add(btnRemoveStecker, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsStecker.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 2, 2);
        panStecker.add(panButtonsStecker, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 32;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panStecker, gridBagConstraints);

        panPfand.setLayout(new java.awt.GridBagLayout());

        scpLstPfand.setMinimumSize(new java.awt.Dimension(258, 66));

        lstPfand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lstPfand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPfand.setVisibleRowCount(3);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_pfand}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstPfand);
        bindingGroup.addBinding(jListBinding);

        scpLstPfand.setViewportView(lstPfand);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panPfand.add(scpLstPfand, gridBagConstraints);

        panButtonsPfand.setOpaque(false);
        panButtonsPfand.setLayout(new java.awt.GridBagLayout());

        btnAddPfand.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddPfand.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddPfandActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsPfand.add(btnAddPfand, gridBagConstraints);

        btnRemovePfand.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemovePfand.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemovePfandActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        panButtonsPfand.add(btnRemovePfand, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsPfand.add(filler6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 2, 2);
        panPfand.add(panButtonsPfand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 35;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panPfand, gridBagConstraints);

        lblPfand.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPfand.setText("Pfand:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 35;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panDaten.add(lblPfand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 5, 5);
        jPanel1.add(panDaten, gridBagConstraints);

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new java.awt.GridBagLayout());

        lblGeom.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        panGeometrie.add(lblGeom, gridBagConstraints);

        if (isEditor) {
            if (isEditor) {
                cbGeom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            }

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_geom}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
            panGeometrie.add(cbGeom, gridBagConstraints);
        }

        panLage.setMinimumSize(new java.awt.Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new java.awt.GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel7.setLayout(new java.awt.GridBagLayout());

        lblKarte.setForeground(new java.awt.Color(255, 255, 255));
        lblKarte.setText("Lage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(lblKarte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLage.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panGeometrie.add(panLage, gridBagConstraints);

        lblVersatz.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVersatz.setText("Versatz:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        panGeometrie.add(lblVersatz, gridBagConstraints);

        cbVersatz.setNullable(false);
        cbVersatz.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cbVersatz.setMaximumSize(new java.awt.Dimension(200, 23));
        cbVersatz.setMinimumSize(new java.awt.Dimension(150, 23));
        cbVersatz.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_versatz}"),
                cbVersatz,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
        panGeometrie.add(cbVersatz, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 5, 5);
        jPanel1.add(panGeometrie, gridBagConstraints);

        panOnline.setOpaque(false);
        panOnline.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        panOnline.add(sepOnline, gridBagConstraints);

        lblOnline.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panOnline.add(lblOnline, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.online}"),
                chOnline,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panOnline.add(chOnline, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 5, 5);
        jPanel1.add(panOnline, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panContent.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddZugangActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(EmobradLadestationEditor.this), dlgAddZugang, true);
    }                                                                 //GEN-LAST:event_btnAddZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveZugangActionPerformed
        final Object selection = lstZugang.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMZUG_QUESTION),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMZUG_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__ZUGANG, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMZUG_ERRORTITLE,
                            BUNDLE_REMZUG_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                    //GEN-LAST:event_btnRemoveZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortZugangActionPerformed
        dlgAddZugang.setVisible(false);
    }                                                                      //GEN-LAST:event_btnMenAbortZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkZugangActionPerformed
        try {
            final Object selItem = cbZugang.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__ZUGANG,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__ZUGANG);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddZugang.setVisible(false);
        }
    }                                                                   //GEN-LAST:event_btnMenOkZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chHalbStateChanged(final ChangeEvent evt) { //GEN-FIRST:event_chHalbStateChanged
        isOpen();
    }                                                        //GEN-LAST:event_chHalbStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtSchliessfaecherActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_ftxtSchliessfaecherActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_ftxtSchliessfaecherActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtSteckdosenActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_ftxtSteckdosenActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_ftxtSteckdosenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveSteckerActionPerformed
        final Object selection = lstStecker.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMSTE_QUESTION),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMSTE_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__STECKER, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMSTE_ERRORTITLE,
                            BUNDLE_REMSTE_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                     //GEN-LAST:event_btnRemoveSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddSteckerActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(EmobradLadestationEditor.this),
            dlgAddStecker,
            true);
    }                                                                  //GEN-LAST:event_btnAddSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemovePfandActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemovePfandActionPerformed
        final Object selection = lstPfand.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMPFA_QUESTION),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_REMPFA_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__PFAND, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMPFA_ERRORTITLE,
                            BUNDLE_REMPFA_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                   //GEN-LAST:event_btnRemovePfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddPfandActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddPfandActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(EmobradLadestationEditor.this), dlgAddPfand, true);
    }                                                                //GEN-LAST:event_btnAddPfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkPfandActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkPfandActionPerformed
        try {
            final Object selItem = cbPfand.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__PFAND,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__PFAND);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddPfand.setVisible(false);
        }
    }                                                                  //GEN-LAST:event_btnMenOkPfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortPfandActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortPfandActionPerformed
        dlgAddPfand.setVisible(false);
    }                                                                     //GEN-LAST:event_btnMenAbortPfandActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkSteckerActionPerformed
        try {
            final Object selItem = cbStecker.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__STECKER,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__STECKER);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddStecker.setVisible(false);
        }
    }                                                                    //GEN-LAST:event_btnMenOkSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortSteckerActionPerformed
        dlgAddStecker.setVisible(false);
    }                                                                       //GEN-LAST:event_btnMenAbortSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chLadeboxStateChanged(final ChangeEvent evt) { //GEN-FIRST:event_chLadeboxStateChanged
        isBox();
    }                                                           //GEN-LAST:event_chLadeboxStateChanged

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
    private void doWithFotoUrl() {
        final String foto = EmobConfProperties.getInstance().getFotoUrlRaeder().concat(txtFoto.getText());
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkName() {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(
            TABLE_NAME,
            " where "
                    + FIELD__NAME
                    + " ilike '"
                    + txtName.getText().trim()
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
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_DUPLICATENAME));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Betreiber muss angegeben werden
        try {
            if (cbBetreiber.getSelectedItem() == null) {
                LOG.warn("No operator specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOOPERATOR));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Operator not given.", ex);
            save = false;
        }
        // Straße muss angegeben werden
        try {
            if (txtStrasse.getText().trim().isEmpty()) {
                LOG.warn("No street specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOSTREET));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Street not given.", ex);
            save = false;
        }
        // Anzahl Ladepunkte muss angegeben werden
        try {
            if (ftxtAnzahlLadepunkte.getText().trim().isEmpty()) {
                LOG.warn("No count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOCOUNT));
            } else {
                try {
                    if (Integer.parseInt(ftxtAnzahlLadepunkte.getText()) <= 0) {
                        errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGCOUNT));
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong count specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGCOUNT));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }
        // Öffnungszeiten müssen angegeben werden, wenn halb-öffentlich
        try {
            if (taOffen.getText().trim().isEmpty() && chHalb.isSelected()) {
                LOG.warn("No open specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOOPEN));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Open not given.", ex);
            save = false;
        }
        // Wenn Ladebox: Schliessfaecher & Steckdosen > 0, wenn nicht: S & S= 0
        try {
            if (chLadebox.isSelected()) {
                if (ftxtSchliessfaecher.getText().trim().isEmpty()) {
                    LOG.warn("No schliessfaecher specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOSCHLIESSFACH));
                } else {
                    try {
                        if (Integer.parseInt(ftxtSchliessfaecher.getText()) <= 0) {
                            errorMessage.append(NbBundle.getMessage(
                                    EmobradLadestationEditor.class,
                                    BUNDLE_WRONGSCHLIESSFACH));
                        }
                    } catch (NumberFormatException e) {
                        LOG.warn("Wrong schliessfach specified. Skip persisting.", e);
                        errorMessage.append(NbBundle.getMessage(
                                EmobradLadestationEditor.class,
                                BUNDLE_WRONGSCHLIESSFACH));
                    }
                }
                if (ftxtSteckdosen.getText().trim().isEmpty()) {
                    LOG.warn("No schliessfaecher specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOSTECKDOSE));
                } else {
                    try {
                        if (Integer.parseInt(ftxtSteckdosen.getText()) <= 0) {
                            errorMessage.append(NbBundle.getMessage(
                                    EmobradLadestationEditor.class,
                                    BUNDLE_WRONGSTECKDOSE));
                        }
                    } catch (NumberFormatException e) {
                        LOG.warn("Wrong steckdose specified. Skip persisting.", e);
                        errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGSTECKDOSE));
                    }
                }
            } else {
                if (!(ftxtSchliessfaecher.getText().trim().equals(TEXT_BOX))) {
                    try {
                        cidsBean.setProperty(FIELD__FACH, Integer.getInteger(TEXT_BOX));
                    } catch (Exception ex) {
                        LOG.warn("Setting default value schliessfaecher.", ex);
                    }
                }
                if (!(ftxtSteckdosen.getText().trim().equals(TEXT_BOX))) {
                    try {
                        cidsBean.setProperty(FIELD__DOSE, Integer.getInteger(TEXT_BOX));
                    } catch (Exception ex) {
                        LOG.warn("Setting default value steckdosen.", ex);
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Error with Box.", ex);
            save = false;
        }

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGGEOM));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_PANE_TITLE),
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
                LOG.info("remove propchange emobrad_ladestation: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange emobrad_ladestation: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            // Damit die Zugänge sortiert in der Liste erscheinen.
            final List<CidsBean> zugangCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__ZUGANG);
            Collections.sort(zugangCol, AlphanumComparator.getInstance());

            // Damit die Stecker sortiert in der Liste erscheinen.
            final List<CidsBean> steckerCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__STECKER);
            Collections.sort(steckerCol, AlphanumComparator.getInstance());

            // Damit die moeglichen Muenzen fuer den Pfand sortiert in der Liste erscheinen.
            final List<CidsBean> pfandCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__PFAND);
            Collections.sort(pfandCol, AlphanumComparator.getInstance());

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            isOpen();
            isBox();
            setDefaultVersatz();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setDefaultVersatz() {
        if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            // Aufruf worker um default values zu setzen
            valueFromOtherTable(
                TABLE_NAME_VERSATZ,
                " where "
                        + FIELD__SCHLUESSEL
                        + " ilike '"
                        + VERSATZ_ZENTRAL_SCHLUESSEL
                        + "'",
                FIELD__VERSATZ,
                OtherTableCases.SET_VALUE);
        }
    }
    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtHnr);
            RendererTools.makeReadOnly(cbBetreiber);
            RendererTools.makeReadOnly(chHalb);
            RendererTools.makeReadOnly(taOffen);
            RendererTools.makeReadOnly(taZusatz);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taDetailbeschreibung);
            RendererTools.makeReadOnly(txtFoto);
            RendererTools.makeReadOnly(chGruen);
            RendererTools.makeDoubleSpinnerWithoutButtons(spAnzahl, 0);
            RendererTools.makeReadOnly(spAnzahl);
            RendererTools.makeReadOnly(ftxtAnzahlLadepunkte);
            RendererTools.makeReadOnly(chLadebox);
            RendererTools.makeReadOnly(ftxtSchliessfaecher);
            RendererTools.makeReadOnly(ftxtSteckdosen);
            RendererTools.makeReadOnly(cbAbrechnung);
            RendererTools.makeReadOnly(lstZugang);
            panButtonsZugang.setVisible(isEditor);
            RendererTools.makeReadOnly(lstStecker);
            panButtonsStecker.setVisible(isEditor);
            RendererTools.makeReadOnly(lstPfand);
            panButtonsPfand.setVisible(isEditor);
            lblGeom.setVisible(isEditor);
            RendererTools.makeReadOnly(cbVersatz);
            RendererTools.makeReadOnly(chOnline);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void isOpen() {
        final boolean isNotOpen = chHalb.isSelected();

        if (isEditor) {
            taOffen.setEnabled(isNotOpen);
            if (isNotOpen == false) {
                taOffen.setText(TEXT_OPEN);
            } else {
                if (taOffen.getText().equals(TEXT_OPEN)) {
                    taOffen.setText("");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void isBox() {
        final boolean isNotBox = chLadebox.isSelected();

        if (isEditor) {
            ftxtSchliessfaecher.setEnabled(isNotBox);
            ftxtSteckdosen.setEnabled(isNotBox);
            if (isNotBox == false) {
                ftxtSchliessfaecher.setText(TEXT_BOX);
                ftxtSteckdosen.setText(TEXT_BOX);
            } else {
                if (ftxtSchliessfaecher.getText().equals(TEXT_BOX)) {
                    ftxtSchliessfaecher.setText("");
                }
                if (ftxtSteckdosen.getText().equals(TEXT_BOX)) {
                    ftxtSteckdosen.setText("");
                }
            }
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
        dlgAddZugang.dispose();
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
                                showLabel.setText(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOLOAD));
                                showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setText(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOLOAD));
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
