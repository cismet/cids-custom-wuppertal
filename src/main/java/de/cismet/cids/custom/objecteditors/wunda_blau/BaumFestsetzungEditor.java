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
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;

import org.apache.log4j.Logger;


import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
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

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.utils.CidsBeansTableModel;
import de.cismet.cids.custom.wunda_blau.search.server.BaumSchadenLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.MissingResourceException;
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
public class BaumFestsetzungEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener,
    BaumOrganizer{

    //~ Static fields/initializers ---------------------------------------------
    private MetaClass schadenMetaClass;
    private static final Logger LOG = Logger.getLogger(BaumFestsetzungEditor.class);
 
    private static final String[] SCHADEN_COL_NAMES = new String[] {  "Gebiet-Aktenzeichen", "Meldungsdatum", "Id", "gef. Art" };
    private static final String[] SCHADEN_PROP_NAMES = new String[] {
            "fk_meldung.fk_gebiet.aktenzeichen",
            "fk_meldung.datum",
            "id",
            "fk_art"
        };
    private static final Class[] SCHADEN_PROP_TYPES = new Class[] {
            CidsBean.class, 
            Date.class,
            Integer.class,
            CidsBean.class
        };
    private static final String[] LOADING_COL_NAMES = new String[] { "Die Daten werden geladen......"};
    private static final String[] MUSTSET_COL_NAMES = new String[] { "Die Daten bitte zuweisen......"};

    private static final String TITLE_NEW_FEST = "eine neue Festsetzung anlegen ....";

        
    public static final String FIELD__ID = "id";                                // baum_festsetzung
    public static final String FIELD__GEOREFERENZ = "fk_geom";                  // baum_festsetzung
    public static final String FIELD__ART = "fk_art.name";                      // baum_festsetzung
    public static final String FIELD__FK_SCHADEN = "fk_schaden";                // baum_festsetzung
    public static final String FIELD__SCHADEN_ID = "fk_schaden.id";             // baum_schaden
    public static final String FIELD__SCHADEN_ART = "fk_schaden.fk_art.name";   // baum_schaden
    public static final String FIELD__MELDUNG_DATUM = "fk_schaden.fk_meldung.datum";       // baum_meldung
    public static final String FIELD__GEBIET_AZ = "fk_schaden.fk_meldung.fk_gebiet.aktenzeichen";       // baum_gebiet
    
    
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_festsetzung_geom
    
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME__SCHADEN = "baum_schaden";
    public static final String TABLE__NAME = "baum_festsetzung";
    
    
    public static final String BUNDLE_NOSCHADEN = 
            "BaumFestsetzungEditor.prepareForSave().noSchaden";
    public static final String BUNDLE_PANE_PREFIX =
            "BaumFestsetzungEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
            "BaumFestsetzungEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = 
            "BaumFestsetzungEditor.prepareForSave().JOptionPane.title";
    
    


    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    

    //~ Instance fields --------------------------------------------------------
    
    private boolean editor = true;
    @Getter private final BaumChildrenLoader baumChildrenLoader = new BaumChildrenLoader(this);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BaumFestsetzungPanel baumFestsetzungPanel;
    private JButton btnChangeSchaden;
    private ComboBoxFilterDialog comboBoxFilterDialogSchaden;
    private JScrollPane jScrollPaneMeldung;
    private JLabel lblGebiet_Meldung;
    private JPanel panContent;
    private JPanel panFest;
    JPanel panFestMain;
    private JPanel panFillerUnten;
    private JXTable xtSchaden;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumFestsetzungEditor() {
    }

    /**
     * Creates a new BaumFestsetzungEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumFestsetzungEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        schadenMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME__SCHADEN,
                connectionContext);
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

        comboBoxFilterDialogSchaden = new ComboBoxFilterDialog(null, new BaumSchadenLightweightSearch(), "Gebiet-Meldung-Schaden auswählen", getConnectionContext());
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panFest = new JPanel();
        lblGebiet_Meldung = new JLabel();
        jScrollPaneMeldung = new JScrollPane();
        xtSchaden = new JXTable();
        btnChangeSchaden = new JButton();
        panFestMain = new JPanel();
        baumFestsetzungPanel = baumFestsetzungPanel = new BaumFestsetzungPanel(this.getBaumChildrenLoader());

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

        panFest.setOpaque(false);
        panFest.setLayout(new GridBagLayout());

        lblGebiet_Meldung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGebiet_Meldung.setText("Gebiet-Meldung-Schaden:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblGebiet_Meldung, gridBagConstraints);

        xtSchaden.setModel(new FestSchadenTableModel());
        xtSchaden.setVisibleRowCount(1);
        jScrollPaneMeldung.setViewportView(xtSchaden);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panFest.add(jScrollPaneMeldung, gridBagConstraints);

        btnChangeSchaden.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png"))); // NOI18N
        btnChangeSchaden.setToolTipText("Gebiet - Meldung - Schaden zuweisen");
        btnChangeSchaden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnChangeSchadenActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(0, 5, 5, 2);
        panFest.add(btnChangeSchaden, gridBagConstraints);
        btnChangeSchaden.setVisible(editor);

        panFestMain.setOpaque(false);
        panFestMain.setLayout(new GridBagLayout());

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean}"), baumFestsetzungPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFestMain.add(baumFestsetzungPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        panFest.add(panFestMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panFest, gridBagConstraints);

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

    private void btnChangeSchadenActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnChangeSchadenActionPerformed
        final Object selectedItem = comboBoxFilterDialogSchaden.showAndGetSelected();
        if (selectedItem instanceof CidsBean) {
            final CidsBean schadenBean = (CidsBean)selectedItem;
            setSchadenTable(schadenBean);

            xtSchaden.getTableHeader().setForeground(Color.BLACK);
            try {
                cidsBean.setProperty(FIELD__FK_SCHADEN, schadenBean);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_btnChangeSchadenActionPerformed

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        
        boolean noErrorOccured = baumFestsetzungPanel.prepareForSave(this.cidsBean);
        // Schaden muss angegeben werden
        try {
            if (this.cidsBean.getProperty(FIELD__FK_SCHADEN)== null) {
                LOG.warn("No schaden specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungEditor.class, BUNDLE_NOSCHADEN));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Schaden not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumFestsetzungEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumFestsetzungEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumFestsetzungEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save & noErrorOccured;
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
                cb,
                getConnectionContext());
            bindingGroup.bind();
            
            if (this.cidsBean != null){
             
            xtSchaden.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
            if(cidsBean.getProperty(FIELD__FK_SCHADEN) == null){
                xtSchaden.getTableHeader().setForeground(Color.red);
            }else{
                xtSchaden.getTableHeader().setForeground(Color.BLACK);
                setSchadenTable((CidsBean)cidsBean.getProperty(FIELD__FK_SCHADEN));
            }
            xtSchaden.addMouseMotionListener(new MouseAdapter(){
                @Override
		public void mouseMoved(MouseEvent e) {
                    int row=xtSchaden.rowAtPoint(e.getPoint());
                    int col=xtSchaden.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1){
                        Object value=xtSchaden.getValueAt(row, col);
                        if(null!=value && !"".equals(value)){
                            xtSchaden.setToolTipText(value.toString());
                        }else{
                            xtSchaden.setToolTipText(null);//keinTooltip anzeigen
                        }
                    }
                }
            });
        }
            
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }
    
    private void setSchadenTable(CidsBean schadenBean){
        List<CidsBean> schadenBeans = new ArrayList<>();
        schadenBeans.add(schadenBean);
        ((FestSchadenTableModel)xtSchaden.getModel()).setCidsBeans(schadenBeans);
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(editor)) {
            RendererTools.makeReadOnly(xtSchaden);
            btnChangeSchaden.setVisible(editor);
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
            TABLE__NAME,
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_FEST;
        } else {
            return String.format("G: %s - M: %s - S: %s, %s - E:%s, %s", cidsBean.getProperty(FIELD__GEBIET_AZ), cidsBean.getProperty(FIELD__MELDUNG_DATUM), cidsBean.getProperty(FIELD__SCHADEN_ID), cidsBean.getProperty(FIELD__SCHADEN_ART), cidsBean.getProperty(FIELD__ID), cidsBean.getProperty(FIELD__ART));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        baumFestsetzungPanel.dispose();
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
    
    
    /*
     * DOCUMENT ME!
     *
     * @param  tableName     DOCUMENT ME!
     * @param  whereClause   DOCUMENT ME!
     */

    @Override
    public boolean isEditor() {
        return this.editor;
    }
    
    
        
    class FestSchadenTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FestSchadenTableModel object.
         */
        public FestSchadenTableModel() {
            super(SCHADEN_PROP_NAMES, SCHADEN_COL_NAMES, SCHADEN_PROP_TYPES);
        }
    }  
    class LoadingTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public LoadingTableModel() {
            super( SCHADEN_PROP_NAMES,LOADING_COL_NAMES, SCHADEN_PROP_TYPES);
        }
    } 
    class MustSetTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public MustSetTableModel() {
            super( SCHADEN_PROP_NAMES,MUSTSET_COL_NAMES, SCHADEN_PROP_TYPES);
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
    
    
