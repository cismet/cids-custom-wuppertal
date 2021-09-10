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
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;

import org.apache.log4j.Logger;


import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.utils.CidsBeansTableModel;
import de.cismet.cids.custom.wunda_blau.search.server.BaumMeldungLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.AfterClosingHook;
import de.cismet.cids.editors.hooks.AfterSavingHook;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumSchadenEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    AfterClosingHook,
    AfterSavingHook,
    BindingGroupStore,
    PropertyChangeListener,
    BaumParentPanel{

    //~ Static fields/initializers ---------------------------------------------
    
    
    
    private static final Logger LOG = Logger.getLogger(BaumSchadenEditor.class);
    private static final String[] MELDUNG_COL_NAMES = new String[] {  "Gebiet-Aktenzeichen", "Gebiet-Bemerkung", "Meldungsdatum", "Meldung-Bemerkung" };
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
    private static final String[] LOADING_COL_NAMES = new String[] { "Die Daten werden geladen......"};
    private static final String[] MUSTSET_COL_NAMES = new String[] { "Die Daten bitte zuweisen......"};
    
    private static final String TITLE_NEW_SCHADEN = "einen neuen Schaden anlegen ....";

    public static final String FIELD__ID = "id";                                // baum_schaden
    public static final String FIELD__GEOREFERENZ = "fk_geom";                  // baum_schaden
    public static final String FIELD__SCHADEN_PRIVAT = "privatbaum";            // baum_schaden
    public static final String FIELD__SCHADEN_OHNE = "ohne_schaden";            // baum_schaden
    public static final String FIELD__SCHADEN_KRONE = "kronenschaden";          // baum_schaden
    public static final String FIELD__SCHADEN_STAMM = "stammschaden";           // baum_schaden
    public static final String FIELD__SCHADEN_WURZEL = "wurzelschaden";         // baum_schaden
    public static final String FIELD__SCHADEN_STURM = "sturmschaden";           // baum_schaden
    public static final String FIELD__SCHADEN_ABGESTORBEN = "abgestorben";      // baum_schaden
    public static final String FIELD__SCHADEN_BAU = "baumassnahme";             // baum_schaden
    public static final String FIELD__SCHADEN_GUTACHTEN = "gutachten";          // baum_schaden
    public static final String FIELD__SCHADEN_BERATUNG = "baumberatung";        // baum_schaden
    public static final String FIELD__SCHADEN_EINGANG = "eingegangen";          // baum_schaden
    public static final String FIELD__SCHADEN_FAELLUNG = "faellung";            // baum_schaden
    public static final String FIELD__MELDUNG = "fk_meldung";                   // baum_ortstermin
    public static final String FIELD__SCHADEN = "fk_schaden";                   // baum_ersatz/fest
    public static final String FIELD__MELDUNG_ID = "fk_meldung.id";             // baum_meldung
    public static final String FIELD__MELDUNG_DATUM = "fk_meldung.datum";       // baum_meldung
    public static final String FIELD__GEBIET_AZ = "fk_meldung.fk_gebiet.aktenzeichen";       // baum_gebiet
    
    public static final String TABLE__ERSATZ = "baum_ersatz";
    public static final String TABLE__FEST = "baum_festsetzung";
    public static final String TABLE__MELDUNG = "baum_meldung";
    public static final String TABLE_NAME = "baum_schaden";
    
    public static final String BUNDLE_PANE_PREFIX =
        "BaumSchadenEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumSchadenEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumSchadenEditor.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_PERSIST = "BaumSchadenEditor.editorClose().JOptionPane.title";
    public static final String BUNDLE_PANE_PREFIX_ERSATZ = "BaumSchadenEditor.editorClose().JOptionPane.errorErsatz";
    public static final String BUNDLE_PANE_PREFIX_FEST = "BaumSchadenEditor.editorClose().JOptionPane.errorFest";
    public static final String BUNDLE_PANE_KONTROLLE = "BaumSchadenEditor.editorClose().JOptionPane.kontrolle";
    public static final String BUNDLE_PANE_ADMIN = "BaumSchadenEditor.editorClose().JOptionPane.admin";
    
    public static final String BUNDLE_W_QUESTION = "BaumSchadenEditor.btnRemoveWurzelActionPerformed().question";
    public static final String BUNDLE_W_TITLE = "BaumSchadenEditor.btnRemoveWurzelActionPerformed().title";
    public static final String BUNDLE_W_ERRORTITLE = "BaumSchadenEditor.btnRemoveWurzelActionPerformed().errortitle";
    public static final String BUNDLE_W_ERRORTEXT = "BaumSchadenEditor.btnRemoveWurzelActionPerformed().errortext";
    public static final String BUNDLE_S_QUESTION = "BaumSchadenEditor.btnRemoveStammActionPerformed().question";
    public static final String BUNDLE_S_TITLE = "BaumSchadenEditor.btnRemoveStammActionPerformed().title";
    public static final String BUNDLE_S_ERRORTITLE = "BaumSchadenEditor.btnRemoveStammActionPerformed().errortitle";
    public static final String BUNDLE_S_ERRORTEXT = "BaumSchadenEditor.btnRemoveStammActionPerformed().errortext";
    public static final String BUNDLE_K_QUESTION = "BaumSchadenEditor.btnRemoveKroneActionPerformed().question";
    public static final String BUNDLE_K_TITLE = "BaumSchadenEditor.btnRemoveKroneActionPerformed().title";
    public static final String BUNDLE_K_ERRORTITLE = "BaumSchadenEditor.btnRemoveKroneActionPerformed().errortitle";
    public static final String BUNDLE_K_ERRORTEXT = "BaumSchadenEditor.btnRemoveKroneActionPerformed().errortext";
    public static final String BUNDLE_M_QUESTION = "BaumSchadenEditor.btnRemoveMassnahmeActionPerformed().question";
    public static final String BUNDLE_M_TITLE = "BaumSchadenEditor.btnRemoveMassnahmeActionPerformed().title";
    public static final String BUNDLE_M_ERRORTITLE = "BaumSchadenEditorEditor.btnRemoveMassnahmeActionPerformed().errortitle";
    public static final String BUNDLE_M_ERRORTEXT = "BaumSchadenEditor.btnRemoveMassnahmeActionPerformed().errortext";
    public static final String BUNDLE_LOAD_ERROR =
    "BaumSchadenEditor.loadChildren().error";
    public static final String BUNDLE_NOMELDUNG = "BaumSchadenEditor.prepareForSave().noMeldung";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    
    
    
    //~ Instance fields --------------------------------------------------------
    @Getter private boolean editor = true;
    
    @Getter private final BaumChildrenLoader baumChildrenLoader = new BaumChildrenLoader(this);
    private boolean areChildrenLoad = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BaumSchadenPanel baumSchadenPanel;
    private JButton btnChangeGebiet;
    private ComboBoxFilterDialog comboBoxFilterDialogGebiet;
    private Box.Filler filler1;
    private JScrollPane jScrollPaneMeldung;
    private JLabel lblGebiet_Meldung;
    private JPanel panAll;
    private JPanel panContent;
    private JPanel panFillerUnten;
    JPanel panSchaden;
    private JXTable xtMeldung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumSchadenEditor() {
    }

    /**
     * Creates a new BaumSchadenEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumSchadenEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
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

        comboBoxFilterDialogGebiet = new ComboBoxFilterDialog(null, new BaumMeldungLightweightSearch(), "Gebiet-Meldung auswählen", getConnectionContext());
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panAll = new JPanel();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panSchaden = new JPanel();
        baumSchadenPanel = baumSchadenPanel = new BaumSchadenPanel(this.getBaumChildrenLoader());
        btnChangeGebiet = new JButton();
        jScrollPaneMeldung = new JScrollPane();
        xtMeldung = new JXTable();
        lblGebiet_Meldung = new JLabel();

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

        panAll.setOpaque(false);
        panAll.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panAll.add(filler1, gridBagConstraints);

        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean}"), baumSchadenPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(baumSchadenPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panAll.add(panSchaden, gridBagConstraints);

        btnChangeGebiet.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png"))); // NOI18N
        btnChangeGebiet.setToolTipText("Gebiet - Meldung zuweisen");
        btnChangeGebiet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnChangeGebietActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(2, 5, 5, 0);
        panAll.add(btnChangeGebiet, gridBagConstraints);
        btnChangeGebiet.setVisible(editor);

        xtMeldung.setModel(new SchadenMeldungTableModel());
        xtMeldung.setVisibleRowCount(1);
        jScrollPaneMeldung.setViewportView(xtMeldung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAll.add(jScrollPaneMeldung, gridBagConstraints);

        lblGebiet_Meldung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGebiet_Meldung.setText("Gebiet-Meldung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAll.add(lblGebiet_Meldung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panAll, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeGebietActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnChangeGebietActionPerformed
        final Object selectedItem = comboBoxFilterDialogGebiet.showAndGetSelected();
        if (selectedItem instanceof CidsBean) {
            final CidsBean meldungBean = (CidsBean)selectedItem;
            setMeldungTable(meldungBean);

            xtMeldung.getTableHeader().setForeground(Color.BLACK);
            try {
                cidsBean.setProperty(FIELD__MELDUNG, meldungBean);
            } catch (Exception ex) {
                LOG.warn("problem in setbeanproperty: fk_meldung.", ex);
            }
        }
    }//GEN-LAST:event_btnChangeGebietActionPerformed

    
    @Override
    public boolean isOkForSaving()  {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean noErrorOccured = baumSchadenPanel.isOkForSaving(this.cidsBean);
        try {
            if (cidsBean.getProperty(FIELD__MELDUNG_ID) == null) {
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
                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save && noErrorOccured;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
        try {
            if (editor && (this.cidsBean != null)) {
                LOG.info("remove propchange baum_schaden: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (editor && (this.cidsBean != null)) {
                LOG.info("add propchange baum_schaden: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
            bindingGroup.bind();
            if(this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
                setDefaultValues();
            }
            if(this.cidsBean != null && this.cidsBean.getMetaObject().getStatus() != MetaObject.NEW){
                loadChildren(this.cidsBean.getPrimaryKeyValue());
            }
            if (this.cidsBean != null){
            xtMeldung.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
            if(cidsBean.getProperty(FIELD__MELDUNG) == null){
                xtMeldung.getTableHeader().setForeground(Color.red);
            }else{
                xtMeldung.getTableHeader().setForeground(Color.BLACK);
                setMeldungTable((CidsBean)cidsBean.getProperty(FIELD__MELDUNG));
            }
            xtMeldung.getColumn(2).setMaxWidth(150);
            xtMeldung.addMouseMotionListener(new MouseAdapter(){
                @Override
		public void mouseMoved(MouseEvent e) {
                    int row=xtMeldung.rowAtPoint(e.getPoint());
                    int col=xtMeldung.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1){
                        Object value=xtMeldung.getValueAt(row, col);
                        if(null!=value && !"".equals(value)){
                            xtMeldung.setToolTipText(value.toString());
                        }else{
                            xtMeldung.setToolTipText(null);//keinTooltip anzeigen
                        }
                    }
                }
            });
        }
        } catch (final Exception ex) {
            LOG.error("Bean not set.", ex);
        }
    }
    
    private void setMeldungTable(CidsBean meldungBean){
        List<CidsBean> meldungBeans = new ArrayList<>();
        meldungBeans.add(meldungBean);
        ((SchadenMeldungTableModel)xtMeldung.getModel()).setCidsBeans(meldungBeans);
    }
    
    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(editor)) {
            btnChangeGebiet.setVisible(editor);
        }
    }
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            TABLE__MELDUNG,
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_SCHADEN;
        } else {
            return String.format("Gebiet: %s - Meldung: %s - Schaden: %s", cidsBean.getProperty(FIELD__GEBIET_AZ), cidsBean.getProperty(FIELD__MELDUNG_DATUM), cidsBean.getProperty(FIELD__ID));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        baumSchadenPanel.dispose();
    }
    
   public void setDefaultValues(){
        try {
            this.cidsBean.setProperty(FIELD__SCHADEN_ABGESTORBEN, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_BAU, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_BERATUNG, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_EINGANG, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_FAELLUNG, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_GUTACHTEN, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_KRONE, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_OHNE, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_PRIVAT, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_STAMM, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_STURM, false);
            this.cidsBean.setProperty(FIELD__SCHADEN_WURZEL, false);
        } catch (Exception ex) {
            LOG.warn("problem in set default values.", ex);
        }
   }
    
    public void clearBaumChildrenLoader(){
        getBaumChildrenLoader().clearAllMaps();
        getBaumChildrenLoader().setLoadingCompletedWithoutError(false);
    }
    
    @Override
    public void setTitle(final String string) {
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
    private void loadChildren(final Integer id) {
        new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return getBaumChildrenLoader().loadChildrenForSchaden(id, getConnectionContext());
                }

                @Override
                protected void done() {
                    try {
                        areChildrenLoad = get();
                        getBaumChildrenLoader().setLoadingCompletedWithoutError(areChildrenLoad);
                        if (!areChildrenLoad){
                            setTitle(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_LOAD_ERROR));
                        }
                    } catch (final InterruptedException | ExecutionException ex) {
                        LOG.error(ex, ex);
                    }  
                }
            }.execute();
    }



    /**
     *
     * @param event
     */
    @Override
    public void afterSaving(AfterSavingHook.Event event) {
        try{
            if(AfterSavingHook.Status.SAVE_SUCCESS == event.getStatus()){
                List<CidsBean> listErsatz = new ArrayList<>();
                final Map<Integer, List<CidsBean>> mapErsatz = getBaumChildrenLoader().getMapErsatz();
                for(final Integer key:mapErsatz.keySet()){
                    listErsatz = mapErsatz.get(key);
                }
                List<CidsBean> listFest = new ArrayList<>();
                final Map<Integer, List<CidsBean>> mapFest = getBaumChildrenLoader().getMapFest();
                for(final Integer key:mapFest.keySet()){
                    listFest = mapFest.get(key);
                }
                //Ersatzpflanzungen persisten
                if (listErsatz != null && !(listErsatz.isEmpty())){
                    for (final CidsBean ersatzBean : listErsatz) {
                        try {
                            ersatzBean.setProperty(FIELD__SCHADEN, event.getPersistedBean());
                            ersatzBean.persist(getConnectionContext());
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_PREFIX_ERSATZ)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_KONTROLLE)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_ADMIN)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_SUFFIX),
                                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                //Festsetzungen persisten
                if (listFest != null && !(listFest.isEmpty())){
                    for (final CidsBean festBean : listFest) {
                        try {
                            festBean.setProperty(FIELD__SCHADEN, event.getPersistedBean());
                            festBean.persist(getConnectionContext());
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_PREFIX_FEST)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_KONTROLLE)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_ADMIN)
                                        + NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_SUFFIX),
                                NbBundle.getMessage(BaumSchadenEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (final HeadlessException | MissingResourceException ex) {
            LOG.error(ex, ex);
        }
    }

    @Override
    public void afterClosing(AfterClosingHook.Event event) {
        clearBaumChildrenLoader();
    }

    
    
        
    class SchadenMeldungTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SchadenMeldungTableModel object.
         */
        public SchadenMeldungTableModel() {
            super(MELDUNG_PROP_NAMES, MELDUNG_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }  
    class LoadingTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public LoadingTableModel() {
            super( MELDUNG_PROP_NAMES,LOADING_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    } 
    class MustSetTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public MustSetTableModel() {
            super( MELDUNG_PROP_NAMES,MUSTSET_COL_NAMES, MELDUNG_PROP_TYPES);
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
    
    
