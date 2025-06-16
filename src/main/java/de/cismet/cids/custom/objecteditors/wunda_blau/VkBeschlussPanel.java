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
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Date;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.VkDocumentLoader;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class VkBeschlussPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VkBeschlussPanel.class);

    public static final String FIELD__DATE = "datum";
    public static final String FIELD__ANZEIGE = "anzeige";
    public static final String FIELD__URL = "url";
    public static final String FIELD__DATUM = "datum";
    public static final String FIELD__FK_VORHABEN = "fk_vorhaben";

    public static final String TABLE__NAME = "vk_vorhaben_beschluesse";

    public static final String BUNDLE_NODATE = "VkBeschlussPanel.isOkForSaving().noDatum";
    public static final String BUNDLE_NOURL = "VkBeschlussPanel.isOkForSaving().noUrl";
    public static final String BUNDLE_NOANZEIGE = "VkBeschlussPanel.isOkForSaving().noAnzeige";
    public static final String BUNDLE_ANZEIGE = "VkBeschlussPanel.isOkForSaving().anzeige";
    public static final String BUNDLE_ANZEIGE_EMPTY = "VkBeschlussPanel.isOkForSaving().anzeigeEmpty";
    public static final String BUNDLE_WHICH = "VkBeschlussPanel.isOkForSaving().welcherBeschluss";
    public static final String BUNDLE_PANE_PREFIX = "VkBeschlussPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "VkBeschlussPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "VkBeschlussPanel.isOkForSaving().JOptionPane.title";

    public static final String BUNDLE_NOSAVE_MESSAGE = "VkBeschlussPanel.noSave().message";
    public static final String BUNDLE_NOSAVE_TITLE = "VkBeschlussPanel.noSave().title";

    private static final int MAX_ZEICHEN = 75;

    @Getter @Setter private static Exception errorNoSave = null;

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    @Getter private final VkDocumentLoader vkDocumentLoader;
    private CidsBean cidsBean;
    private String saveAnzeige;
    private String saveUrl;
    private Date saveDatum;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));

    private SwingWorker worker_url;

    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__ANZEIGE: {
                        if (evt.getNewValue() != saveAnzeige) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__DATUM: {
                        if (evt.getNewValue() != saveDatum) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__URL: {
                        if (evt.getNewValue() != saveUrl) {
                            setChangeFlag();
                            checkBeschluss();
                        }
                        break;
                    }
                    default: {
                        setChangeFlag();
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    DefaultBindableDateChooser dcDatum;
    JLabel lblAnzeige;
    JLabel lblDatum;
    JLabel lblUrl;
    JLabel lblUrlCheck;
    JPanel panBeschluss;
    JPanel panDaten;
    JPanel panFillerUnten4;
    JPanel panUrl;
    JTextField txtAnzeige;
    JTextField txtUrl;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VkBeschlussPanel object.
     */
    public VkBeschlussPanel() {
        this(null);
    }

    /**
     * Creates new VkBeschlussPanel.
     *
     * @param  vdlInstance  DOCUMENT ME!
     */
    public VkBeschlussPanel(final VkDocumentLoader vdlInstance) {
        this.vkDocumentLoader = vdlInstance;
        if (vdlInstance != null) {
            this.editor = vdlInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

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
        panBeschluss = new JPanel();
        lblDatum = new JLabel();
        dcDatum = new DefaultBindableDateChooser();
        lblAnzeige = new JLabel();
        txtAnzeige = new JTextField();
        lblUrl = new JLabel();
        txtUrl = new JTextField();
        panUrl = new JPanel();
        lblUrlCheck = new JLabel();
        panFillerUnten4 = new JPanel();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panDaten.setName("panDaten"); // NOI18N
        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        panBeschluss.setMinimumSize(new Dimension(100, 10));
        panBeschluss.setName("panBeschluss"); // NOI18N
        panBeschluss.setOpaque(false);
        panBeschluss.setPreferredSize(new Dimension(520, 270));
        panBeschluss.setLayout(new GridBagLayout());

        lblDatum.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblDatum,
            NbBundle.getMessage(VkBeschlussPanel.class, "VkBeschlussPanel.lblDatum.text")); // NOI18N
        lblDatum.setName("lblDatum");                                                       // NOI18N
        lblDatum.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panBeschluss.add(lblDatum, gridBagConstraints);

        dcDatum.setEnabled(false);
        dcDatum.setName("dcDatum"); // NOI18N

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum}"),
                dcDatum,
                BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        panBeschluss.add(dcDatum, gridBagConstraints);

        lblAnzeige.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblAnzeige,
            NbBundle.getMessage(VkBeschlussPanel.class, "VkBeschlussPanel.lblAnzeige.text")); // NOI18N
        lblAnzeige.setToolTipText(NbBundle.getMessage(
                VkBeschlussPanel.class,
                "VkBeschlussPanel.lblAnzeige.toolTipText"));                                  // NOI18N
        lblAnzeige.setName("lblAnzeige");                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panBeschluss.add(lblAnzeige, gridBagConstraints);

        txtAnzeige.setEnabled(false);
        txtAnzeige.setName("txtAnzeige"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzeige}"),
                txtAnzeige,
                BeanProperty.create("text"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panBeschluss.add(txtAnzeige, gridBagConstraints);

        lblUrl.setFont(new Font("Tahoma", 1, 11));                                                                       // NOI18N
        Mnemonics.setLocalizedText(lblUrl, NbBundle.getMessage(VkBeschlussPanel.class, "VkBeschlussPanel.lblUrl.text")); // NOI18N
        lblUrl.setName("lblUrl");                                                                                        // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panBeschluss.add(lblUrl, gridBagConstraints);

        txtUrl.setEnabled(false);
        txtUrl.setName("txtUrl"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.url}"),
                txtUrl,
                BeanProperty.create("text"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panBeschluss.add(txtUrl, gridBagConstraints);

        panUrl.setName("panUrl"); // NOI18N
        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        lblUrlCheck.setName("lblUrlCheck");                                                                  // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panBeschluss.add(panUrl, gridBagConstraints);

        panFillerUnten4.setName(""); // NOI18N
        panFillerUnten4.setOpaque(false);

        final GroupLayout panFillerUnten4Layout = new GroupLayout(panFillerUnten4);
        panFillerUnten4.setLayout(panFillerUnten4Layout);
        panFillerUnten4Layout.setHorizontalGroup(panFillerUnten4Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten4Layout.setVerticalGroup(panFillerUnten4Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panBeschluss.add(panFillerUnten4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDaten.add(panBeschluss, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panDaten, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

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

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!isEditor()) {
            RendererTools.makeReadOnly(dcDatum);
            RendererTools.makeReadOnly(txtAnzeige);
            RendererTools.makeReadOnly(txtUrl);
            dcDatum.setEnabled(true);
            txtAnzeige.setEnabled(true);
            txtUrl.setEnabled(true);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void checkBeschluss() {
        lblUrlCheck.setIcon(statusFalsch);
        lblUrlCheck.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        if ((txtUrl.getText() != null) && (txtUrl.getText().length() > 10)) {
            EventQueue.invokeLater(new Thread("checkBeschlussThread") {

                    @Override
                    public void run() {
                        checkUrl(txtUrl.getText(), lblUrlCheck);
                    }
                });
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
                        if (isCancelled()) {
                            return;
                        }
                        check = get();
                        if (check) {
                            showLabel.setIcon(statusOk);
                            showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            showLabel.setIcon(statusFalsch);
                            showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("URL Check Problem in Worker.", e);
                    }
                }
            };
        if ((worker_url != null) && !worker_url.isCancelled()) {
            worker_url.cancel(true);
        }
        worker_url = worker;
        worker_url.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        if ((getVkDocumentLoader() != null)
                    && (getVkDocumentLoader().getParentOrganizer() != null)
                    && (getVkDocumentLoader().getParentOrganizer().getCidsBean() != null)) {
            getVkDocumentLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return ((vkDocumentLoader != null) && (vkDocumentLoader.getParentOrganizer() != null))
            ? vkDocumentLoader.getParentOrganizer().getConnectionContext() : null;
    }

    @Override
    public void dispose() {
        vkDocumentLoader.clearAllMaps();
        bindingGroup.unbind();
        if (isEditor()) {
            if (getCidsBean() != null) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
        }
        cidsBean = null;
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveAnzeige = (getCidsBean().getProperty(FIELD__ANZEIGE) != null)
            ? ((String)getCidsBean().getProperty(FIELD__ANZEIGE)) : null;
        saveUrl = (getCidsBean().getProperty(FIELD__URL) != null) ? ((String)getCidsBean().getProperty(FIELD__URL))
                                                                  : null;
        saveDatum = (getCidsBean().getProperty(FIELD__DATUM) != null) ? (Date)getCidsBean().getProperty(FIELD__DATUM)
                                                                      : null;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))) {
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            try {
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if ((getCidsBean() != null) && isEditor()) {
                    setSaveValues();
                }
                if (getCidsBean() != null) {
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
                }
                bindingGroup.bind();
                checkBeschluss();

                if (isEditor() && (getCidsBean() != null)) {
                    getCidsBean().addPropertyChangeListener(changeListener);
                }
            } catch (final Exception ex) {
                LOG.warn("problem in setCidsBean.", ex);
                if (isEditor()) {
                    setErrorNoSave(ex);
                    noSave();
                }
            }
        }
        setReadOnly();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void noSave() {
        final ErrorInfo info = new ErrorInfo(
                NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_NOSAVE_TITLE),
                NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_NOSAVE_MESSAGE),
                null,
                null,
                getErrorNoSave(),
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(VkBeschlussPanel.this, info);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        dcDatum.setEnabled(edit);
        txtAnzeige.setEnabled(edit);
        txtUrl.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   saveBeschlussBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkForSaving(final CidsBean saveBeschlussBean) {
        if (getErrorNoSave() != null) {
            noSave();
            return false;
        } else {
            boolean save = true;
            final StringBuilder errorMessage = new StringBuilder();

            // datum vorhanden
            try {
                if ((saveBeschlussBean.getProperty(FIELD__DATUM) == null)
                            || saveBeschlussBean.getProperty(FIELD__DATUM).toString().trim().isEmpty()) {
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_NODATE));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Datum not given.", ex);
                save = false;
            }

            // url vorhanden
            try {
                if ((saveBeschlussBean.getProperty(FIELD__URL) == null)
                            || saveBeschlussBean.getProperty(FIELD__URL).toString().trim().isEmpty()) {
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_NOURL));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("url not given.", ex);
                save = false;
            }

            // Zeichenanzahl Anzeige
            try {
                if ((saveBeschlussBean.getProperty(FIELD__ANZEIGE) != null)
                            && !saveBeschlussBean.getProperty(FIELD__ANZEIGE).toString().isEmpty()) {
                    if (saveBeschlussBean.getProperty(FIELD__ANZEIGE).toString().length() > MAX_ZEICHEN) {
                        LOG.warn("Long Anzeige specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_ANZEIGE));
                        save = false;
                    } else {
                        if (saveBeschlussBean.getProperty(FIELD__ANZEIGE).toString().trim().isEmpty()) {
                            LOG.warn("Empty Anzeige specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_ANZEIGE_EMPTY));
                            save = false;
                        }
                    }
                } else {
                    LOG.warn("No Anzeige specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_NOANZEIGE));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("anzeige too long.", ex);
                save = false;
            }

            if (errorMessage.length() > 0) {
                if (vkDocumentLoader.getParentOrganizer() instanceof VkVorhabenEditor) {
                    errorMessage.append(NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_WHICH))
                            .append(saveBeschlussBean.getPrimaryKeyValue());
                }
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_PANE_PREFIX)
                            + errorMessage.toString()
                            + NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_PANE_SUFFIX),
                    NbBundle.getMessage(VkBeschlussPanel.class, BUNDLE_PANE_TITLE),
                    JOptionPane.WARNING_MESSAGE);
            }
            return save;
        }
    }
}
