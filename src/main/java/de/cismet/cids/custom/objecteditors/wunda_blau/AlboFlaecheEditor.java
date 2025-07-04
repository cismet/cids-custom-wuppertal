/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CoolThemaRenderer.java
 *
 * Created on 10. November 3508, 11:56
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboFlaecheArbeitsstandPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboFlaecheBemerkungenPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboFlaecheMainPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboFlaecheMassnahmenPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboPicturePanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.SimpleAltlastWebDavPanel;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.actions.AlboExportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheLandesRegNrSearch;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheNummerUniqueSearch;
import de.cismet.cids.custom.wunda_blau.search.server.AlboTeilflaecheSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.BeanInitializer;
import de.cismet.cids.editors.BeanInitializerProvider;
import de.cismet.cids.editors.DefaultBeanInitializer;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.AfterSavingHook;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class AlboFlaecheEditor extends JPanel implements CidsBeanRenderer,
    DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    EditorSaveListener,
    ConnectionContextStore,
    PropertyChangeListener,
    SaveVetoable,
    AfterSavingHook,
    BeanInitializerProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlboFlaecheEditor.class);
    public static Converter<String, String> DATE_TO_STRING = null;

    /**
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panTitle = new JPanel();
        lblTitle = new JLabel();
        jToggleButton1 = new JToggleButton();
        btnLandRegNr = new JButton();
        btnReport1 = new JButton();
        panFooter = new JPanel();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        btnBack = new JButton();
        panButtons = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        btnForward = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        panMainCard = new JPanel();
        panCardFlaeche = new JPanel();
        panMain = new AlboFlaecheMainPanel(isEditable());
        panCardArbeitsstandUndBemerkungen = new JPanel();
        panBemerkungen = new AlboFlaecheBemerkungenPanel(isEditable());
        panArbeitsstand = new AlboFlaecheArbeitsstandPanel(isEditable());
        alboPicturePanel1 = new AlboPicturePanel(isEditable());
        simpleAltlastWebDavPanel1 = new SimpleAltlastWebDavPanel(isEditable());
        panCardMassnahmen = new JPanel();
        panMassnahmen = new AlboFlaecheMassnahmenPanel(isEditable());

        final FormListener formListener = new FormListener();

        panTitle.setName("panTitle"); // NOI18N
        panTitle.setOpaque(false);
        panTitle.setLayout(new GridBagLayout());

        lblTitle.setForeground(new Color(255, 255, 255));
        lblTitle.setName("lblTitle"); // NOI18N

        final Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create(
                    "<html><body><h2><nobr>Altlastenkataster - Erhebungsnummer: ${cidsBean.erhebungsnummer}"),
                lblTitle,
                BeanProperty.create("text"));
        binding.setSourceNullValue("<html><body><h2><nobr>Altlastenkataster - Erhebungsnummer: -");
        binding.setSourceUnreadableValue("<html><body><h2><nobr>Altlastenkataster - Erhebungsnummer: <i>[Fehler]");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        jToggleButton1.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock.png")));      // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setFocusPainted(false);
        jToggleButton1.setName("jToggleButton1");                                                          // NOI18N
        jToggleButton1.setRolloverIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_edit.png"))); // NOI18N
        jToggleButton1.setRolloverSelectedIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_go.png")));   // NOI18N
        jToggleButton1.setSelectedIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_open.png"))); // NOI18N
        jToggleButton1.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panTitle.add(jToggleButton1, gridBagConstraints);
        jToggleButton1.setVisible(isEditable());

        btnLandRegNr.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png"))); // NOI18N
        btnLandRegNr.setToolTipText(NbBundle.getMessage(
                AlboFlaecheEditor.class,
                "AlboFlaecheEditor.btnLandRegNr.toolTipText"));                                           // NOI18N
        btnLandRegNr.setBorderPainted(false);
        btnLandRegNr.setContentAreaFilled(false);
        btnLandRegNr.setFocusPainted(false);
        btnLandRegNr.setName("btnLandRegNr");                                                             // NOI18N
        btnLandRegNr.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panTitle.add(btnLandRegNr, gridBagConstraints);
        btnLandRegNr.setVisible(isEditable());

        btnReport1.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/table_export.png"))); // NOI18N
        btnReport1.setToolTipText(NbBundle.getMessage(
                AlboFlaecheEditor.class,
                "AlboFlaecheEditorEditor.btnReport.toolTipText"));                                                  // NOI18N
        btnReport1.setBorderPainted(false);
        btnReport1.setContentAreaFilled(false);
        btnReport1.setFocusPainted(false);
        btnReport1.setName("btnReport1");                                                                           // NOI18N
        btnReport1.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panTitle.add(btnReport1, gridBagConstraints);

        panFooter.setName("panFooter"); // NOI18N
        panFooter.setOpaque(false);
        panFooter.setLayout(new GridBagLayout());

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panFooter.add(filler1, gridBagConstraints);

        btnBack.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png"))); // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new Dimension(30, 30));
        btnBack.setMinimumSize(new Dimension(30, 30));
        btnBack.setName("btnBack");                                                                                     // NOI18N
        btnBack.setPreferredSize(new Dimension(30, 30));
        btnBack.setPressedIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-pressed.png")));               // NOI18N
        btnBack.setRolloverIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-sel.png")));                   // NOI18N
        btnBack.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panFooter.add(btnBack, gridBagConstraints);

        panButtons.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setName("panButtons"); // NOI18N
        panButtons.setOpaque(false);
        panButtons.setLayout(new GridLayout(1, 0, 10, 0));

        jLabel1.setForeground(new Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("<html><body><center><b><h3>Fläche");
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.addMouseListener(formListener);
        panButtons.add(jLabel1);

        jLabel2.setForeground(new Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("<html><body><center><b><h3>Arbeitsstand & Bemerkung");
        jLabel2.setEnabled(false);
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.addMouseListener(formListener);
        panButtons.add(jLabel2);

        jLabel3.setForeground(new Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("<html><body><center><b><h3>Maßnahmen");
        jLabel3.setEnabled(false);
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.addMouseListener(formListener);
        panButtons.add(jLabel3);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panFooter.add(panButtons, gridBagConstraints);

        btnForward.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png"))); // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new Dimension(30, 30));
        btnForward.setMinimumSize(new Dimension(30, 30));
        btnForward.setName("btnForward");                                                          // NOI18N
        btnForward.setPreferredSize(new Dimension(30, 30));
        btnForward.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panFooter.add(btnForward, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panFooter.add(filler2, gridBagConstraints);

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panMainCard.setName("panMainCard"); // NOI18N
        panMainCard.setOpaque(false);
        panMainCard.setLayout(new CardLayout());

        panCardFlaeche.setName("panCardFlaeche"); // NOI18N
        panCardFlaeche.setOpaque(false);
        panCardFlaeche.setLayout(new BorderLayout());

        panMain.setName("panMain"); // NOI18N
        panCardFlaeche.add(panMain, BorderLayout.CENTER);

        panMainCard.add(panCardFlaeche, "flaeche");

        panCardArbeitsstandUndBemerkungen.setName("panCardArbeitsstandUndBemerkungen"); // NOI18N
        panCardArbeitsstandUndBemerkungen.setOpaque(false);
        panCardArbeitsstandUndBemerkungen.setLayout(new GridBagLayout());

        panBemerkungen.setName("panBemerkungen"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panCardArbeitsstandUndBemerkungen.add(panBemerkungen, gridBagConstraints);

        panArbeitsstand.setName("panArbeitsstand"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panCardArbeitsstandUndBemerkungen.add(panArbeitsstand, gridBagConstraints);

        alboPicturePanel1.setName("alboPicturePanel1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panCardArbeitsstandUndBemerkungen.add(alboPicturePanel1, gridBagConstraints);

        simpleAltlastWebDavPanel1.setName("simpleAltlastWebDavPanel1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 5);
        panCardArbeitsstandUndBemerkungen.add(simpleAltlastWebDavPanel1, gridBagConstraints);

        panMainCard.add(panCardArbeitsstandUndBemerkungen, "arbeitsstand");

        panCardMassnahmen.setName("panCardMassnahmen"); // NOI18N
        panCardMassnahmen.setOpaque(false);
        panCardMassnahmen.setLayout(new BorderLayout());

        panMassnahmen.setName("panMassnahmen"); // NOI18N
        panCardMassnahmen.add(panMassnahmen, BorderLayout.CENTER);

        panMainCard.add(panCardMassnahmen, "massnahmen");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panMainCard, gridBagConstraints);

        bindingGroup.bind();
    }

    /**
     * Code for dispatching events from components to event handlers.
     *
     * @version  $Revision$, $Date$
     */
    private class FormListener implements ActionListener, MouseListener {

        /**
         * Creates a new FormListener object.
         */
        FormListener() {
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (evt.getSource() == jToggleButton1) {
                AlboFlaecheEditor.this.jToggleButton1ActionPerformed(evt);
            } else if (evt.getSource() == btnLandRegNr) {
                AlboFlaecheEditor.this.btnLandRegNrActionPerformed(evt);
            } else if (evt.getSource() == btnReport1) {
                AlboFlaecheEditor.this.btnReport1ActionPerformed(evt);
            } else if (evt.getSource() == btnBack) {
                AlboFlaecheEditor.this.btnBackActionPerformed(evt);
            } else if (evt.getSource() == btnForward) {
                AlboFlaecheEditor.this.btnForwardActionPerformed(evt);
            }
        }

        @Override
        public void mouseClicked(final MouseEvent evt) {
            if (evt.getSource() == jLabel1) {
                AlboFlaecheEditor.this.jLabel1MouseClicked(evt);
            } else if (evt.getSource() == jLabel2) {
                AlboFlaecheEditor.this.jLabel2MouseClicked(evt);
            } else if (evt.getSource() == jLabel3) {
                AlboFlaecheEditor.this.jLabel3MouseClicked(evt);
            }
        }

        @Override
        public void mouseEntered(final MouseEvent evt) {
        }

        @Override
        public void mouseExited(final MouseEvent evt) {
        }

        @Override
        public void mousePressed(final MouseEvent evt) {
        }

        @Override
        public void mouseReleased(final MouseEvent evt) {
        }
    } // </editor-fold>//GEN-END:initComponents

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private CardLayout cardLayout;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private AlboPicturePanel alboPicturePanel1;
    private JButton btnBack;
    private JButton btnForward;
    JButton btnLandRegNr;
    JButton btnReport1;
    private Box.Filler filler1;
    private Box.Filler filler2;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JToggleButton jToggleButton1;
    private JLabel lblTitle;
    private AlboFlaecheArbeitsstandPanel panArbeitsstand;
    private AlboFlaecheBemerkungenPanel panBemerkungen;
    private JPanel panButtons;
    private JPanel panCardArbeitsstandUndBemerkungen;
    private JPanel panCardFlaeche;
    private JPanel panCardMassnahmen;
    private JPanel panFooter;
    private AlboFlaecheMainPanel panMain;
    private JPanel panMainCard;
    private AlboFlaecheMassnahmenPanel panMassnahmen;
    private JPanel panTitle;
    private SimpleAltlastWebDavPanel simpleAltlastWebDavPanel1;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboFlaecheEditor object.
     */
    public AlboFlaecheEditor() {
        this(true);
    }

    /**
     * Creates a new AlboFlaecheEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AlboFlaecheEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        panMain.initWithConnectionContext(connectionContext);
        panArbeitsstand.initWithConnectionContext(connectionContext);
        panMassnahmen.initWithConnectionContext(connectionContext);
        panBemerkungen.initWithConnectionContext(connectionContext);
        simpleAltlastWebDavPanel1.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    final Object selectedObject = ((JList)e.getSource()).getSelectedValue();

                    if (selectedObject instanceof CidsBean) {
                        alboPicturePanel1.setWebDavHelper(simpleAltlastWebDavPanel1.getWebdavHelper());
                        alboPicturePanel1.setCidsBean((CidsBean)selectedObject);
                    }
                }
            });

        this.cardLayout = (CardLayout)panMainCard.getLayout();
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        try {
            bindingGroup.unbind();
        } catch (final Exception ex) {
        }

        if (editable && (this.cidsBean != null)) {
            this.cidsBean.removePropertyChangeListener(this);
        }

        if (cidsBean != null) {
            if (isEditable()) {
                if (cidsBean.getProperty("fk_massnahmen") == null) {
                    try {
                        cidsBean.setProperty(
                            "fk_massnahmen",
                            CidsBean.getMetaClassFromTableName(
                                "WUNDA_BLAU",
                                "ALBO_MASSNAHMEN",
                                getConnectionContext()).getEmptyInstance(getConnectionContext()).getBean());
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    }
                }
            }

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            this.cidsBean = cidsBean;
            bindingGroup.bind();
        }
        panMain.setCidsBean(cidsBean);
        panArbeitsstand.setCidsBean(cidsBean);
        panBemerkungen.setCidsBean(cidsBean);
        panMassnahmen.setCidsBean((CidsBean)cidsBean.getProperty("fk_massnahmen"));

        if (editable && (this.cidsBean != null)) {
            this.cidsBean.addPropertyChangeListener(this);
        }

        updateTitleControls();
        updateFooterControls();
        simpleAltlastWebDavPanel1.setCidsBean(cidsBean);
    }

    /**
     * DOCUMENT ME!
     */
    private void updateTitleControls() {
        btnLandRegNr.setVisible(jToggleButton1.isSelected());
        if ((cidsBean.getProperty("geodaten_id") == null) || cidsBean.getProperty("geodaten_id").equals("")) {
            btnLandRegNr.setIcon(new ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-add.png")));    // NOI18N
            btnLandRegNr.setToolTipText(NbBundle.getMessage(
                    AlboFlaecheEditor.class,
                    "AlboFlaecheEditor.btnLandRegNr.toolTipText"));                                              // NOI18N
        } else {
            btnLandRegNr.setIcon(new ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
            btnLandRegNr.setToolTipText(NbBundle.getMessage(
                    AlboFlaecheEditor.class,
                    "AlboFlaecheEditor.btnLandRegNr.toolTipText.remove"));                                       // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  true if the fields/boxes are editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.previous(panMainCard);
        updateFooterControls();
    }                                                            //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.next(panMainCard);
        updateFooterControls();
    }                                                               //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jLabel1MouseClicked(final MouseEvent evt) { //GEN-FIRST:event_jLabel1MouseClicked
        cardLayout.show(panMainCard, "flaeche");
        updateFooterControls();
    }                                                        //GEN-LAST:event_jLabel1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jLabel2MouseClicked(final MouseEvent evt) { //GEN-FIRST:event_jLabel2MouseClicked
        cardLayout.show(panMainCard, "arbeitsstand");
        updateFooterControls();
    }                                                        //GEN-LAST:event_jLabel2MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jLabel3MouseClicked(final MouseEvent evt) { //GEN-FIRST:event_jLabel3MouseClicked
        cardLayout.show(panMainCard, "massnahmen");
        updateFooterControls();
    }                                                        //GEN-LAST:event_jLabel3MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        if (isEditable()) {
            panMain.setUnlocked(jToggleButton1.isSelected());
            updateTitleControls();
        }
    }                                                                   //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReport1ActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnReport1ActionPerformed
        final AlboTeilflaecheSearch search = new AlboTeilflaecheSearch();

        try {
            final ArrayList<ArrayList<String>> number = (ArrayList<ArrayList<String>>)SessionManager
                        .getProxy().customServerSearch(search, connectionContext);
            final List<String> fisAlboNr = new ArrayList<String>();

            if ((number != null) && (number.size() > 0) && (number.get(0) != null) && (number.get(0).size() > 1)) {
                for (final ArrayList<String> tmp : number) {
                    fisAlboNr.add(tmp.get(0) + " (" + tmp.get(1) + ")");
                }

                final String fisAlboNrString = String.join(",\n", fisAlboNr);

                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    "Die folgenden Altflächen haben keine Hauptfläche: \n"
                            + fisAlboNrString,
                    "Fehlerhafter Bearbeitungsstand",
                    JOptionPane.WARNING_MESSAGE);

                return;
            }
        } catch (Exception e) {
            LOG.error("Cannot check landesregistriernummer", e);
        }

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        CismapBroker.getInstance().getMappingComponent())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            final String name = String.valueOf(System.currentTimeMillis());
            DownloadManager.instance()
                    .add(
                        new ByteArrayActionDownload(
                            AlboExportServerAction.TASK_NAME,
                            name,
                            null,
                            "Schnittstellen-Export",
                            jobname,
                            name,
                            ".zip",
                            getConnectionContext()));
        }
    } //GEN-LAST:event_btnReport1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLandRegNrActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnLandRegNrActionPerformed
        if ((cidsBean.getProperty("geodaten_id") == null) || cidsBean.getProperty("geodaten_id").equals("")) {
            createLandesregistriernummer();
        } else {
            try {
                cidsBean.setProperty("landesregistriernummer", null);
                cidsBean.setProperty("laufende_nummer", null);
                cidsBean.setProperty("geodaten_id", null);
            } catch (Exception e) {
                LOG.error("Cannot remove landesregistriernummer", e);
            }
        }
        updateTitleControls();
    }                                                                 //GEN-LAST:event_btnLandRegNrActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void createLandesregistriernummer() {
        final String geom = String.valueOf(cidsBean.getProperty("fk_geom.geo_field"));
        String landesRegNr = (String)cidsBean.getProperty("landesregistriernummer");

        if ((landesRegNr == null) || landesRegNr.equals("")) {
            landesRegNr = null;
        }

        final AlboFlaecheLandesRegNrSearch search = new AlboFlaecheLandesRegNrSearch(geom, landesRegNr);

        try {
            final ArrayList<ArrayList<String>> number = (ArrayList<ArrayList<String>>)SessionManager
                        .getProxy().customServerSearch(search, connectionContext);

            if ((number != null) && (number.size() > 0) && (number.get(0) != null) && (number.get(0).size() > 1)) {
                cidsBean.setProperty("landesregistriernummer", number.get(0).get(0));
                cidsBean.setProperty("laufende_nummer", number.get(0).get(1));
                cidsBean.setProperty(
                    "geodaten_id",
                    number.get(0).get(0).substring(1)
                            + number.get(0).get(1).substring(1));
            }
        } catch (Exception e) {
            LOG.error("Cannot set landesregistriernummer", e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateFooterControls() {
        for (final Component card : panMainCard.getComponents()) {
            if (card.isVisible()) {
                btnBack.setEnabled(!panCardFlaeche.equals(card));
                jLabel1.setEnabled(!panCardFlaeche.equals(card));
                jLabel2.setEnabled(!panCardArbeitsstandUndBemerkungen.equals(card));
                jLabel3.setEnabled(!panCardMassnahmen.equals(card));
                btnForward.setEnabled(!panCardMassnahmen.equals(card));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(0, 5, 0, 5);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        panMain.dispose();
        panArbeitsstand.dispose();
        panMassnahmen.dispose();
        panBemerkungen.dispose();
        alboPicturePanel1.dispose();
        simpleAltlastWebDavPanel1.dispose();
        bindingGroup.unbind();
        if (editable && (cidsBean != null)) {
            cidsBean.removePropertyChangeListener(this);
        }
        cidsBean = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @Override
    public void editorClosed(final EditorClosedEvent event) {
        panMain.editorClosed(event);
        panArbeitsstand.editorClosed(event);
        panMassnahmen.editorClosed(event);
        panBemerkungen.editorClosed(event);
        simpleAltlastWebDavPanel1.editorClosed(event);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean prepareForSave() {
        if (cidsBean.getProperty("loeschen") == null) {
            try {
                cidsBean.setProperty("loeschen", false);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                return false;
            }
        }

        if ((cidsBean.getProperty("fk_zuordnung.schluessel") != null)
                    && (cidsBean.getProperty("fk_zuordnung.schluessel").equals("altlastverdaechtig")
                        || cidsBean.getProperty("fk_zuordnung.schluessel").equals(
                            "erfassung_schaedliche_bodenveraenderungen"))) {
            if ((cidsBean.getProperty("geodaten_id") == null) || cidsBean.getProperty("geodaten_id").equals("")) {
                final int answer = JOptionPane.showConfirmDialog(
                        this,
                        NbBundle.getMessage(
                            AlboFlaecheEditor.class,
                            "AlboFlaecheEditor.prepareForSave().geodaten_id.message"),
                        NbBundle.getMessage(
                            AlboFlaecheEditor.class,
                            "AlboFlaecheEditor.prepareForSave().geodaten_id.title"),
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (answer == JOptionPane.CANCEL_OPTION) {
                    return false;
                } else if (answer == JOptionPane.YES_OPTION) {
                    createLandesregistriernummer();
                }
            }
        }

        return panMain.prepareForSave()
                    && panArbeitsstand.prepareForSave()
                    && panMassnahmen.prepareForSave()
                    && panBemerkungen.prepareForSave();
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
            "ALBO_FLAECHE",
            16521,
            1200,
            800);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("geodaten_id")) {
            updateTitleControls();
        }
    }

    @Override
    public boolean isOkForSaving() {
        Boolean prop = (Boolean)cidsBean.getProperty("entwurf");
        final List<String> errorList = new ArrayList<String>();
        final String zuordnung = (String)cidsBean.getProperty("fk_zuordnung.schluessel");

        if (prop == null) {
            prop = Boolean.FALSE;
        }

        if (!prop && (zuordnung != null) && !zuordnung.equalsIgnoreCase("verzeichnisflaeche")) {
            final String erhebungsnummer = (String)cidsBean.getProperty("erhebungsnummer");

            if ((erhebungsnummer == null) || erhebungsnummer.equals("")) {
                errorList.add("Die Erhebungsnummer muss gesetzt sein.");
            } else {
                checkErhebungsnummer(errorList, erhebungsnummer);
            }

            final Object geom = cidsBean.getProperty("fk_geom.geo_field");

            if (!((geom instanceof Polygon) || (geom instanceof MultiPolygon))) {
                errorList.add("Die Geometrie muss entweder ein Polygon oder ein Multipolygon sein.");
            }

            if ((cidsBean.getProperty("laufende_nummer") == null) || (cidsBean.getProperty("geodaten_id") == null)
                        || (cidsBean.getProperty("landesregistriernummer") == null)
                        || cidsBean.getProperty("laufende_nummer").equals("")
                        || cidsBean.getProperty("geodaten_id").equals("")
                        || cidsBean.getProperty("landesregistriernummer").equals("")) {
                errorList.add("Die Hauptnummer, laufende Nummer und FISAlBo-Nr müssen gesetzt sein.");
            } else {
                final String lrnr = String.valueOf(cidsBean.getProperty("landesregistriernummer"));
                final String lfdNr = String.valueOf(cidsBean.getProperty("laufende_nummer"));
                final String geodaten_id = String.valueOf(cidsBean.getProperty("geodaten_id"));

                if (!geodaten_id.equals(lrnr.substring(1) + lfdNr.substring(1))) {
                    errorList.add("Die FISAlBo-Nr passt nicht zur Hauptnummer und der laufenden Nummer.");
                } else {
                    try {
                        final AlboFlaecheNummerUniqueSearch search = new AlboFlaecheNummerUniqueSearch(
                                geodaten_id,
                                cidsBean.getMetaObject().getId(),
                                false);

                        final ArrayList<ArrayList> result = (ArrayList<ArrayList>)SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(),
                                            search,
                                            getConnectionContext());

                        if ((result != null) && (result.size() > 0)) {
                            errorList.add("Die FISAlBo-Nr ist nicht eindeutig.");
                        }
                    } catch (Exception e) {
                        LOG.error("Error while checking erhebungsnummer", e);
                    }
                }
            }

            final String art = (String)cidsBean.getProperty("fk_art.schluessel");
            String status = (String)cidsBean.getProperty("fk_status.schluessel");

            if (status == null) {
                status = "";
            }

            if (art == null) {
                errorList.add("Die Art der Fläche muss gesetzt sein.");
            } else if (art.equals("altstandort") || art.equals("betriebsstandort")) {
                final TreeSet<CidsBean> wz = getWz();
                boolean mbFound = false;

                if (cidsBean.getProperty("massgeblicher_wirtschaftszweig") != null) {
                    for (final CidsBean w : wz) {
                        if (w.getProperty("id").equals(cidsBean.getProperty("massgeblicher_wirtschaftszweig.id"))) {
                            mbFound = true;
                        }
                    }
                }

                final boolean isException = (zuordnung.equalsIgnoreCase("erfassung_schaedliche_bodenveraenderungen")
                                && (status.equalsIgnoreCase("verdachtsflaeche")
                                    || status.equalsIgnoreCase("kein_handlungsbedarf")));

                if ((wz.isEmpty() || !mbFound) && !isException) {
                    errorList.add(
                        "Wenn die Art der Fläche \"altstandort\" oder \"betriebsstandort\" ist,\n  muss mindestens eine Branche angegeben sein und der Wert für\n  branche_massgeblich gesetzt sein.");
                }
            } else if (art.equalsIgnoreCase("altablagerung")) {
                final List<CidsBean> aa = getAa();
                boolean ueberwFound = false;

                for (final CidsBean a : aa) {
                    if ((a.getProperty("ueberwiegend") != null) && (Boolean)a.getProperty("ueberwiegend")) {
                        ueberwFound = true;
                    }
                }

                final boolean isException = (zuordnung.equalsIgnoreCase("erfassung_schaedliche_bodenveraenderungen")
                                && (status.equalsIgnoreCase("verdachtsflaeche")
                                    || status.equalsIgnoreCase("kein_handlungsbedarf")));

                if ((aa.isEmpty() || !ueberwFound) && !isException) {
                    errorList.add(
                        "Wenn die Art der Fläche \"altablagerung\" ist, muss mindestens\n  eine Hauptabfallart gesetzt sein und es muss die\n  überwiegende Abfallart angegeben sein.");
                }
            }

            final String bearbeitungsstand = getBearbeitungsstand();

            if ((status.equals("")) && !zuordnung.equalsIgnoreCase("verzeichnisflaeche")) {
                // status was set to "" if it is null to avoid NPEs, so it must be checked, if status = ""
                errorList.add(
                    "Status der Fläche muss gesetzt sein, außer wenn es sich um eine Verzeichnisfläche handelt.");
            } else if (status.equalsIgnoreCase("kein_handlungsbedarf")
                        && !(bearbeitungsstand.equalsIgnoreCase("erfassung")
                            || bearbeitungsstand.equalsIgnoreCase("ga"))) {
                errorList.add(
                    "Wenn der Status \"kein Handlungsbedarf bei der derzeitigen Nutzung\" ist,\n  muss der Bearbeitungsstand \"erfassung\" oder \"ga\" sein.");
            } else if (status.equalsIgnoreCase("verdachtsflaeche")
                        && !(bearbeitungsstand.equalsIgnoreCase("erfassung")
                            || bearbeitungsstand.equalsIgnoreCase("ga"))) {
                errorList.add(
                    "Wenn der Status \"altlastverdächtige Fläche / Verdachtsfläche\" ist,\n  muss der Bearbeitungsstand \"erfassung\" oder \"ga\" sein.");
            } else if (status.equalsIgnoreCase("verdacht_ausgeraeumt")
                        && !(bearbeitungsstand.equalsIgnoreCase("erfassung")
                            || bearbeitungsstand.equalsIgnoreCase("ga"))) {
                errorList.add(
                    "Wenn der Status \"Verdacht ausgeräumt\" ist,\n  muss der Bearbeitungsstand \"erfassung\" oder \"ga\" sein.");
            } else if (status.equalsIgnoreCase("altlast")
                        && !(bearbeitungsstand.equalsIgnoreCase("ga") || bearbeitungsstand.equalsIgnoreCase("su_sp")
                            || bearbeitungsstand.equalsIgnoreCase("sa_laufend"))) {
                errorList.add(
                    "Wenn der Status \"Altlast / schädliche Bodenveränderung (sBv)\" ist,\n  muss der Bearbeitungsstand \"ga\", \"su_sp\" order \"sa_laufend\" sein.");
            } else if (status.equalsIgnoreCase("altlast_mit_ueberwachung")
                        && !(bearbeitungsstand.equalsIgnoreCase("ga") || bearbeitungsstand.equalsIgnoreCase("su_sp"))) {
                errorList.add(
                    "Wenn der Status \"Altlast / sBv mit dauerhafter Beschränkung / Überwachung\" ist,\n  muss der Bearbeitungsstand \"ga\" oder \"su_sp\" sein.");
            } else if (status.equalsIgnoreCase("sanierte_flaeche")
                        && !(bearbeitungsstand.equalsIgnoreCase("sa_abgeschlossen"))) {
                errorList.add(
                    "Wenn der Status \"sanierte Fläche (vollständig dekontaminiert)\" ist,\n  muss der Bearbeitungsstand \"sa_abgeschlossen\" sein.");
            } else if (status.equalsIgnoreCase("sanierte_flaeche_fuer_bestimmte_nutzung")
                        && !(bearbeitungsstand.equalsIgnoreCase("sa_abgeschlossen"))) {
                errorList.add(
                    "Wenn der Status \"sanierte Fläche (gesichert / teilweise dekontaminiert) z.B. für bestimmte Nutzung\" ist,\n  muss der Bearbeitungsstand \"sa_abgeschlossen\" sein.");
            }

            final CidsBean massn = (CidsBean)cidsBean.getProperty("fk_massnahmen");
            final String[] schutzgefaehrdungen = {
                    "ga_boden_mensch",
                    "ga_boden_pflanze",
                    "ga_boden_wasser",
                    "ga_sonstiges"
                };
            final String[] dekonSicherung = {
                    "dm_aushub_deponierung",
                    "dm_aushub_bodenbehandlung",
                    "dm_bodenbeh_ohne_aushub",
                    "dm_pneumatisch",
                    "dm_pump_treat",
                    "dm_in_situ_behandlung",
                    "sm_sicherungsbauwerk",
                    "ea_versiegelung",
                    "ea_oberfl_abdicht",
                    "sm_oberflaechenabdeckung",
                    "sm_vertikale_abdichtung",
                    "sm_immobilisierung",
                    "sm_pneumatisch",
                    "sm_pump_treat",
                    "sm_in_situ_behandlung",
                    "sm_sonstige"
                };
            final String[] sicherung = {
                    "sm_sicherungsbauwerk",
                    "ea_versiegelung",
                    "ea_oberfl_abdicht",
                    "sm_oberflaechenabdeckung",
                    "sm_vertikale_abdichtung",
                    "sm_immobilisierung",
                    "sm_pneumatisch",
                    "sm_pump_treat",
                    "sm_in_situ_behandlung",
                    "sm_sonstige"
                };
            final String[] dekon = {
                    "dm_aushub_deponierung",
                    "dm_aushub_bodenbehandlung",
                    "dm_bodenbeh_ohne_aushub",
                    "dm_pneumatisch",
                    "dm_pump_treat",
                    "dm_in_situ_behandlung"
                };
            int sumSchutzgefaehrdung = 0;
            int sumDekonSicherung = 0;
            int sumSicherung = 0;
            int sumDekon = 0;
            Boolean ueberwachungsMassnahmen = ((massn != null) ? (Boolean)massn.getProperty("ueberwachungs_massnahmen")
                                                               : null);
            Boolean schutzBeschrMassnahmen = ((massn != null) ? (Boolean)massn.getProperty("schutz_beschr_massnahmen")
                                                              : null);

            if (ueberwachungsMassnahmen == null) {
                ueberwachungsMassnahmen = Boolean.FALSE;
            }
            if (schutzBeschrMassnahmen == null) {
                schutzBeschrMassnahmen = Boolean.FALSE;
            }

            if (massn != null) {
                for (final String schutz : schutzgefaehrdungen) {
                    final Boolean value = (Boolean)massn.getProperty(schutz);

                    if ((value != null) && value) {
                        ++sumSchutzgefaehrdung;
                    }
                }

                for (final String dekonV : dekonSicherung) {
                    final Boolean value = (Boolean)massn.getProperty(dekonV);

                    if ((value != null) && value) {
                        ++sumDekonSicherung;
                    }
                }

                for (final String sich : sicherung) {
                    final Boolean value = (Boolean)massn.getProperty(sich);

                    if ((value != null) && value) {
                        ++sumSicherung;
                    }
                }

                for (final String dekonV : dekon) {
                    final Boolean value = (Boolean)massn.getProperty(dekonV);

                    if ((value != null) && value) {
                        ++sumDekon;
                    }
                }
            }

            if (bearbeitungsstand.equals("sa_laufend") || bearbeitungsstand.equals("sa_abgeschlossen")) {
                if (massn == null) {
                    errorList.add(
                        "Wenn der Bearbeitungsstand den Wert \"sa_laufend\" oder \"sa_abgeschlossen\" hat,\n  muss mindestens eine Schutzgutgefährdung gesetzt sein\n  und mindestens eine Dekontaminationsmaßnahme oder Sicherungsmaßnahme gesetzt sein.");
                } else {
                    if (sumSchutzgefaehrdung == 0) {
                        errorList.add(
                            "Es muss mindestens eine Gefährdungsannahme gesetzt sein wenn die Fläche den Bearbeitungsstand \"Sanierung laufend\" oder \"Sanierung abgeschlossen\" hat.");
                    } else if (sumDekonSicherung == 0) {
                        errorList.add(
                            "Es muss mindestens eine Maßnahme zur Dekontaminierung oder Sicherung gesetzt sein wenn die Fläche den Bearbeitungsstand \"Sanierung laufend\" oder \"Sanierung abgeschlossen\" hat.");
                    } else if ((sumSchutzgefaehrdung == 0) || (sumDekonSicherung == 0)) {
                        errorList.add(
                            "Wenn der Bearbeitungsstand den Wert \"sa_laufend\" oder \"sa_abgeschlossen\" hat,\n  muss mindestens eine Schutzgutgefährdung gesetzt sein\n  und mindestens eine Dekontaminationsmaßnahme oder Sicherungsmaßnahme gesetzt sein.");
                    }
                }
            } else if (sumDekon > 0) {
                errorList.add(
                    "Wenn eine Dekontaminationsmaßnahme gesetzt ist, muss der Bearbeitungsstand den Wert \"sa_laufend\" oder \"sa_abgeschlossen\" haben.");
            }

            if ((sumSicherung > 0)
                        && !(bearbeitungsstand.equals("sa_laufend") || bearbeitungsstand.equals("sa_abgeschlossen"))) {
                errorList.add(
                    "Wenn eine Sicherungsmaßnahme gesetzt ist, muss der Bearbeitungsstand \"Sanierung laufend\" oder \"Sanierung abgeschlossen\" sein.");
            }

            if ((status.equalsIgnoreCase("altlast") || status.equalsIgnoreCase("altlast_mit_ueberwachung"))
                        && (bearbeitungsstand.equals("su_sp") || bearbeitungsstand.equals("ga"))
                        && (sumSchutzgefaehrdung == 0)) {
                errorList.add(
                    "Es muss mindestens eine Gefährdungsannahme gesetzt sein, wenn die Fläche den Bearbeitungsstand \"su_sp\" oder \"ga\" hat\nund es sich um eine Altlast oder Altlast mit Überwachung handelt.");
            }

            if ((sumSchutzgefaehrdung > 0)
                        && !(bearbeitungsstand.equals("ga") || bearbeitungsstand.equals("su_sp")
                            || bearbeitungsstand.equals("sa_laufend") || bearbeitungsstand.equals(
                                "sa_abgeschlossen"))) {
                errorList.add(
                    "Wenn eine Schutzgutgefährdung ermittelt wurde, muss der Bearbeitungsstand \"Gefährdungsabschätzung\", \"Sanierungsuntersuchung / -planung\", \"Sanierung laufend\" oder \"Sanierung abgeschlossen\" sein.");
            }

            if (status.equalsIgnoreCase("altlast_mit_ueberwachung")) {
                final String arbeitsstandUeberwachung = (String)cidsBean.getProperty(
                        "fk_arbeitsstand_ueberwachung.schluessel");
                final String arbeitsstandBegrenzung = (String)cidsBean.getProperty(
                        "fk_arbeitsstand_schutzundbegrenzung.schluessel");

                if (((arbeitsstandUeberwachung != null) && arbeitsstandUeberwachung.equalsIgnoreCase("laufend"))
                            && ((arbeitsstandBegrenzung == null)
                                || !arbeitsstandBegrenzung.equalsIgnoreCase("laufend"))) {
                    if (!ueberwachungsMassnahmen) {
                        errorList.add(
                            "Wenn der Status der Fläche \"Altlast mit Überwachung\" ist und Überwachung ausgewählt ist, muss Überwachung aktiv sein.");
                    }
                } else if ((arbeitsstandBegrenzung != null) && arbeitsstandBegrenzung.equalsIgnoreCase("laufend")) {
                    if (!schutzBeschrMassnahmen && !ueberwachungsMassnahmen) {
                        errorList.add(
                            "Wenn der Status der Fläche \"Altlast mit Überwachung\" ist und Beschränkungsmaßnahmen gesetzt sind, müssen Überwachung und Schutz- und Beschränkungsmaßnahmen aktiv sein.");
                    }
                }
            }
        } else if ((zuordnung != null) && zuordnung.equalsIgnoreCase("verzeichnisflaeche")) {
            if (((cidsBean.getProperty("laufende_nummer") != null)
                            && !cidsBean.getProperty("laufende_nummer").equals(""))
                        || ((cidsBean.getProperty("geodaten_id") != null)
                            && !cidsBean.getProperty("geodaten_id").equals(""))
                        || ((cidsBean.getProperty("landesregistriernummer") != null)
                            && !cidsBean.getProperty("landesregistriernummer").equals(""))) {
                final int ans = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                        "Wenn es sich um eine Verzeichnisfläche handelt, dann darf die Hauptnummer, laufende Nummer und FISAlBo-Nr. nicht gesetzt sein.\nSollen diese Nummern automatisch entfernt werden?",
                        "Fehlerhafter Bearbeitungsstand",
                        JOptionPane.YES_NO_OPTION);

                if (ans == JOptionPane.YES_OPTION) {
                    try {
                        cidsBean.setProperty("laufende_nummer", null);
                        cidsBean.setProperty("geodaten_id", null);
                        cidsBean.setProperty("landesregistriernummer", null);
                    } catch (Exception e) {
                        LOG.error("Cannot set laufende_nummer, geodaten_id or landesregistriernummer", e);
                        return false;
                    }
                } else {
                    return false;
                }
            }

            final String erhebungsnummer = (String)cidsBean.getProperty("erhebungsnummer");

            if ((erhebungsnummer == null) || erhebungsnummer.equals("")) {
                errorList.add("Die Erhebungsnummer muss gesetzt sein.");
            } else {
                checkErhebungsnummer(errorList, erhebungsnummer);
            }
        }

        if (errorList.size() > 0) {
            final StringBuilder sb = new StringBuilder("Folgende Fehler wurden festgestellt:\n");
            int i = 0;

            for (final String error : errorList) {
                sb.append(++i).append(". ").append(error).append("\n");
            }

            sb.append(
                "\nFalls Sie das Objekt trotzdem speichern möchten, dann muss es als Entwurf gekennzeichnet werden.");

            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                sb,
                "Fehlerhafter Bearbeitungsstand",
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  errorList        DOCUMENT ME!
     * @param  erhebungsnummer  DOCUMENT ME!
     */
    private void checkErhebungsnummer(final List<String> errorList, final String erhebungsnummer) {
        try {
            final AlboFlaecheNummerUniqueSearch search = new AlboFlaecheNummerUniqueSearch(
                    erhebungsnummer,
                    cidsBean.getMetaObject().getId(),
                    true);

            final ArrayList<ArrayList> result = (ArrayList<ArrayList>)SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(),
                                search,
                                getConnectionContext());

            if ((result != null) && (result.size() > 0)) {
                errorList.add("Diese Erhebungsnummer existiert bereits.");
            }
        } catch (Exception e) {
            LOG.error("Error while checking erhebungsnummer", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TreeSet<CidsBean> getWz() {
        final TreeSet<CidsBean> wzSet = new TreeSet<>(new CidsBeanComparator());
        final List<CidsBean> standorte = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "n_standorte");

        if (standorte != null) {
            for (final CidsBean s : standorte) {
                final List<CidsBean> wirtschaftszweige = CidsBeanSupport.getBeanCollectionFromProperty(
                        s,
                        "arr_wirtschaftszweige");
                if (wirtschaftszweige != null) {
                    wzSet.addAll(wirtschaftszweige);
                }
            }
        }

        return wzSet;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getAa() {
        final List<CidsBean> aaList = new ArrayList<>();
        final CidsBean altablagerung = (CidsBean)cidsBean.getProperty("fk_altablagerung");

        if (altablagerung != null) {
            final List<CidsBean> aa = CidsBeanSupport.getBeanCollectionFromProperty(
                    altablagerung,
                    "n_altablagerung_abfallherkuenfte");

            if (aa != null) {
                aaList.addAll(aa);
            }
        }

        return aaList;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getBearbeitungsstand() {
        final String sanierung = (String)cidsBean.getProperty("fk_arbeitsstand_sanierung.schluessel");
        final String sanierungsplan = (String)cidsBean.getProperty("fk_arbeitsstand_sanierungsplan.schluessel");
        final String sanierungsunter = (String)cidsBean.getProperty(
                "fk_arbeitsstand_sanierungsuntersuchung.schluessel");
        final String gefaehrdungsabsch = (String)cidsBean.getProperty(
                "fk_arbeitsstand_gefaehrdungsabschaetzung.schluessel");

        if ((sanierung != null) && sanierung.equalsIgnoreCase("abgeschlossen")) {
            return "sa_abgeschlossen";
        } else if ((sanierung != null) && sanierung.equalsIgnoreCase("laufend")) {
            return "sa_laufend";
        } else if ((sanierungsplan != null) || (sanierungsunter != null)) {
            return "su_sp";
        } else if (gefaehrdungsabsch != null) {
            return "ga";
        } else {
            return "erfassung";
        }
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return new AlboFlaecheInitializer(cidsBean);
    }

    @Override
    public void afterSaving(final Event event) {
        panMain.afterSaving(event);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class AlboFlaecheInitializer extends DefaultBeanInitializer {

        //~ Constructors -------------------------------------------------------

        // implements BeanInitializerForcePaste //this would allow to use the paste function not only on new object, but
        // also on alreadys existing ones

        /**
         * Creates a new KartierabschnittInitializer object.
         *
         * @param  cidsBean  DOCUMENT ME!
         */
        public AlboFlaecheInitializer(final CidsBean cidsBean) {
            super(cidsBean);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void initializeBean(final CidsBean beanToInit) throws Exception {
            super.initializeBean(beanToInit);

            EventQueue.invokeLater(new Thread("") {

                    @Override
                    public void run() {
                        setCidsBean(beanToInit);
                    }
                });
        }

        @Override
        protected void processSimpleProperty(final CidsBean beanToInit,
                final String propertyName,
                final Object simpleValueToProcess) throws Exception {
            super.processSimpleProperty(beanToInit, propertyName, simpleValueToProcess);
        }

        @Override
        protected void processArrayProperty(final CidsBean beanToInit,
                final String propertyName,
                final Collection<CidsBean> arrayValueToProcess) throws Exception {
            final List<CidsBean> beans = CidsBeanSupport.getBeanCollectionFromProperty(
                    beanToInit,
                    propertyName);
            beans.clear();

            if (propertyName.equalsIgnoreCase("n_standorte")) {
                for (final CidsBean tmp : arrayValueToProcess) {
                    beans.add(CidsBeanSupport.cloneBean(tmp, connectionContext));
                }
            } else if (propertyName.equalsIgnoreCase("dokumente")) {
                for (final CidsBean tmp : arrayValueToProcess) {
                    beans.add(CidsBeanSupport.cloneBean(tmp, connectionContext));
                }
            }
        }

        @Override
        protected void processComplexProperty(final CidsBean beanToInit,
                final String propertyName,
                final CidsBean complexValueToProcess) throws Exception {
            if (complexValueToProcess.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(GEOM_TABLE_NAME)) {
                final CidsBean geomBean = complexValueToProcess.getMetaObject()
                            .getMetaClass()
                            .getEmptyInstance(getConnectionContext())
                            .getBean();
                Geometry g = (Geometry)complexValueToProcess.getProperty(GEOM_FIELD_NAME);

                if (g != null) {
                    g = (Geometry)g.clone();
                }

                geomBean.setProperty(GEOM_FIELD_NAME, g);
                beanToInit.setProperty(propertyName, geomBean);
            } else if (complexValueToProcess.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                            "albo_massnahmen")) {
                final CidsBean maBean = CidsBeanSupport.cloneBean(complexValueToProcess, connectionContext);
                beanToInit.setProperty(propertyName, maBean);
            } else if (complexValueToProcess.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                            "albo_altablagerung")) {
                final CidsBean abBean = CidsBeanSupport.cloneBean(complexValueToProcess, connectionContext);
                final List<CidsBean> beans = complexValueToProcess.getBeanCollectionProperty(
                        "n_altablagerung_abfallherkuenfte");

                if (beans != null) {
                    for (final CidsBean b : beans) {
                        final List<CidsBean> newBeans = abBean.getBeanCollectionProperty(
                                "n_altablagerung_abfallherkuenfte");
                        newBeans.add(CidsBeanSupport.cloneBean(b, connectionContext));
                    }
                }

                beanToInit.setProperty(propertyName, abBean);
            } else {
                // flat copy
                beanToInit.setProperty(propertyName, complexValueToProcess);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class CidsBeanComparator implements Comparator<CidsBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final CidsBean o1, final CidsBean o2) {
            if ((o1 != null) && (o2 != null)) {
                return o1.getPrimaryKeyValue().compareTo(o2.getPrimaryKeyValue());
            } else if ((o1 == null) && (o2 == null)) {
                return 0;
            } else if (o1 != null) {
                return -1;
            } else if (o2 != null) {
                return 1;
            }

            return 0;
        }
    }
}
