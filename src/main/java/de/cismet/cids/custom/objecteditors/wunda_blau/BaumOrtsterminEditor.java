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

import Sirius.server.middleware.types.MetaObject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;

import javax.swing.*;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.CidsBeansTableModel;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.wunda_blau.search.server.BaumMeldungLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumOrtsterminEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    BaumParentPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Comparator<Object> COMPARATOR = new Comparator<Object>() {

            @Override
            public int compare(final Object o1, final Object o2) {
                return String.valueOf(o1).compareTo(String.valueOf(o2));
            }
        };

    public static final String GEOMTYPE = "Point";
    public static final int FOTO_WIDTH = 150;
    private static final Logger LOG = Logger.getLogger(BaumOrtsterminEditor.class);
    private static final String[] MELDUNG_COL_NAMES = new String[] {
            "Gebiet-Aktenzeichen",
            "Gebiet-Bemerkung",
            "Meldungsdatum",
            "Meldung-Bemerkung"
        };
    private static final String[] MELDUNG_PROP_NAMES = new String[] {
            "fk_gebiet.aktenzeichen",
            "fk_gebiet.bemerkung",
            "datum",
            "bemerkung"
        };
    private static final Class[] MELDUNG_PROP_TYPES = new Class[] {
            CidsBean.class,
            CidsBean.class,
            Date.class,
            String.class
        };
    private static final String[] LOADING_COL_NAMES = new String[] { "Die Daten werden geladen......" };
    private static final String[] MUSTSET_COL_NAMES = new String[] { "Die Daten bitte zuweisen......" };

    private static final String TITLE_NEW_ORTSTERMIN = "einen neuen Ortstermin anlegen ....";

    public static final String FIELD__TEILNEHMER = "n_teilnehmer";                     // baum_ortstermin
    public static final String FIELD__ZEIT = "zeit";                                   // baum_ortstermin
    public static final String FIELD__ID = "id";                                       // baum_ortstermin
    public static final String FIELD__MELDUNG = "fk_meldung";                          // baum_ortstermin
    public static final String FIELD__MELDUNG_ID = "fk_meldung.id";                    // baum_meldung
    public static final String FIELD__MELDUNG_DATUM = "fk_meldung.datum";              // baum_meldung
    public static final String FIELD__GEBIET_AZ = "fk_meldung.fk_gebiet.aktenzeichen"; // baum_gebiet
    public static final String FIELD__NAME = "name";                                   // baum_teilnehmer
    public static final String FIELD__MELDUNGEN = "n_ortstermine";                     // baum_meldung
    public static final String FIELD__TEILNEHMER_OTSTERMIN = "fk_ortstermin";          // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_NAME = "name";                        // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_TELEFON = "telefon";                  // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_BEMERKUNG = "bemerkung";              // baum_teilnehmer
    public static final String TABLE_NAME__MELDUNG = "baum_meldung";
    public static final String TABLE_NAME__TEILNEHMER = "baum_teilnehmer";

    public static final String BUNDLE_PANE_PREFIX = "BaumOrtsterminEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumOrtsterminEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumOrtsterminEditor.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_TEIL_QUESTION = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().question";
    public static final String BUNDLE_TEIL_TITLE = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().title";
    public static final String BUNDLE_TEIL_ERRORTITLE =
        "BaumOrtsterminEditor.btnRemoveTeilrActionPerformed().errortitle";
    public static final String BUNDLE_TEIL_ERRORTEXT = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().errortext";
    public static final String BUNDLE_NOMELDUNG = "BaumOrtsterminEditor.isOkForSaving().noMeldung";
    public static final String BUNDLE_NOSAVE_MESSAGE = "BaumOrtsterminEditor.noSave().message";
    public static final String BUNDLE_NOSAVE_TITLE = "BaumOrtsterminEditor.noSave().title";
    @Getter @Setter private static Exception errorNoSave = null;

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

    @Getter private final BaumChildrenLoader baumChildrenLoader = new BaumChildrenLoader(this);

    private List<CidsBean> teilBeans;

    // private MetaClass teilnehmerMetaClass;

    private final boolean editor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BaumOrtsterminPanel baumOrtsterminPanel;
    private JButton btnChangeGebiet;
    private ComboBoxFilterDialog comboBoxFilterDialogGebiet;
    private JScrollPane jScrollPaneMeldung;
    private JLabel lblGebiet_Meldung;
    private JPanel panContent;
    JPanel panOrtstermin;
    JPanel panOrtstermineMain;
    private JXTable xtMeldung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumOrtsterminEditor() {
        this(true);
    }

    /**
     * Creates a new BaumOrtsterminEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumOrtsterminEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isEditor() {
        return this.editor;
    }

    @Override
    public boolean isOkForSaving() {
        if (getErrorNoSave() != null) {
            noSave();
            return false;
        } else {
            boolean save = true;
            final StringBuilder errorMessage = new StringBuilder();

            final boolean noErrorOccured = baumOrtsterminPanel.isOkayForSaving(getCidsBean());
            try {
                if (getCidsBean().getProperty(FIELD__MELDUNG_ID) == null) {
                    LOG.warn("No meldung specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_NOMELDUNG));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Meldung not given.", ex);
                save = false;
            }

            if (errorMessage.length() > 0) {
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_PREFIX)
                            + errorMessage.toString()
                            + NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_SUFFIX),
                    NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_TITLE),
                    JOptionPane.WARNING_MESSAGE);

                return false;
            }
            return save && noErrorOccured;
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        xtMeldung.getColumn(2).setWidth(100);
        xtMeldung.setSortable(false);
        xtMeldung.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        xtMeldung.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(final MouseEvent e) {
                    final int row = xtMeldung.rowAtPoint(e.getPoint());
                    final int col = xtMeldung.columnAtPoint(e.getPoint());
                    if ((row > -1) && (col > -1)) {
                        final Object value = xtMeldung.getValueAt(row, col);
                        if ((null != value) && !"".equals(value)) {
                            xtMeldung.setToolTipText(value.toString());
                        } else {
                            xtMeldung.setToolTipText(null); // keinTooltip anzeigen
                        }
                    }
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

        comboBoxFilterDialogGebiet = new ComboBoxFilterDialog(
                null,
                new BaumMeldungLightweightSearch(),
                "Gebiet-Meldung auswählen",
                getConnectionContext());
        panContent = new RoundedPanel();
        panOrtstermin = new JPanel();
        lblGebiet_Meldung = new JLabel();
        btnChangeGebiet = new JButton();
        jScrollPaneMeldung = new JScrollPane();
        xtMeldung = new JXTable();
        panOrtstermineMain = new JPanel();
        baumOrtsterminPanel = baumOrtsterminPanel = new BaumOrtsterminPanel(this.getBaumChildrenLoader());

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panOrtstermin.setOpaque(false);
        panOrtstermin.setLayout(new GridBagLayout());

        lblGebiet_Meldung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGebiet_Meldung.setText("Gebiet-Meldung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panOrtstermin.add(lblGebiet_Meldung, gridBagConstraints);

        btnChangeGebiet.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png"))); // NOI18N
        btnChangeGebiet.setToolTipText("Gebiet - Meldung zuweisen");
        btnChangeGebiet.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnChangeGebietActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(2, 5, 5, 2);
        panOrtstermin.add(btnChangeGebiet, gridBagConstraints);
        btnChangeGebiet.setVisible(isEditor());

        xtMeldung.setModel(new OrtsterminMeldungTableModel());
        xtMeldung.setVisibleRowCount(1);
        jScrollPaneMeldung.setViewportView(xtMeldung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(jScrollPaneMeldung, gridBagConstraints);

        panOrtstermineMain.setOpaque(false);
        panOrtstermineMain.setLayout(new GridBagLayout());

        final Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean}"),
                baumOrtsterminPanel,
                BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOrtstermineMain.add(baumOrtsterminPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        panOrtstermin.add(panOrtstermineMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panOrtstermin, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnChangeGebietActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnChangeGebietActionPerformed
        final Object selectedItem = comboBoxFilterDialogGebiet.showAndGetSelected();
        if (selectedItem instanceof CidsBean) {
            final CidsBean meldungBean = (CidsBean)selectedItem;
            setMeldungTable(meldungBean);

            xtMeldung.getTableHeader().setForeground(Color.BLACK);
            try {
                getCidsBean().setProperty(FIELD__MELDUNG, meldungBean);
            } catch (Exception ex) {
                LOG.warn("problem in setbeanproperty: fk_meldung.", ex);
            }
        }
    } //GEN-LAST:event_btnChangeGebietActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (teilBeans != null) {
                Collections.sort((List)teilBeans, COMPARATOR);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            bindingGroup.bind();

            if (getCidsBean().getProperty(FIELD__MELDUNG) == null) {
                xtMeldung.getTableHeader().setForeground(Color.red);
            } else {
                xtMeldung.getTableHeader().setForeground(Color.BLACK);
                setMeldungTable((CidsBean)getCidsBean().getProperty(FIELD__MELDUNG));
            }
        } catch (final Exception ex) {
            LOG.error("Bean not set.", ex);
            if (isEditor()) {
                setErrorNoSave(ex);
                noSave();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void noSave() {
        final ErrorInfo info = new ErrorInfo(
                NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_NOSAVE_TITLE),
                NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_NOSAVE_MESSAGE),
                null,
                null,
                getErrorNoSave(),
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(BaumOrtsterminEditor.this, info);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  meldungBean  DOCUMENT ME!
     */
    private void setMeldungTable(final CidsBean meldungBean) {
        final List<CidsBean> meldungBeans = new ArrayList<>();
        meldungBeans.add(meldungBean);
        ((OrtsterminMeldungTableModel)xtMeldung.getModel()).setCidsBeans(meldungBeans);
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(xtMeldung);
            btnChangeGebiet.setVisible(isEditor());
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
            "baum_ortstermin",
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_ORTSTERMIN;
        } else {
            final Calendar calDatumZeit = Calendar.getInstance();
            calDatumZeit.setTime((Date)getCidsBean().getProperty(FIELD__ZEIT));
            final java.util.Date datum = calDatumZeit.getTime();
            final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
            return String.format(
                    "G: %s - M: %s - Ortstermin: %s",
                    getCidsBean().getProperty(FIELD__GEBIET_AZ),
                    formatTag.format(getCidsBean().getProperty(FIELD__MELDUNG_DATUM)),
                    formatTag.format(datum));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        baumOrtsterminPanel.dispose();
    }

    @Override
    public void setTitle(final String string) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class OrtsterminMeldungTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new OrtsterminMeldungTableModel object.
         */
        public OrtsterminMeldungTableModel() {
            super(MELDUNG_PROP_NAMES, MELDUNG_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoadingTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public LoadingTableModel() {
            super(MELDUNG_PROP_NAMES, LOADING_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MustSetTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public MustSetTableModel() {
            super(MELDUNG_PROP_NAMES, MUSTSET_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }
}
