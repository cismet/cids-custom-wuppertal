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

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkisconstants.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.netutil.Proxy;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppePicturePanel extends javax.swing.JPanel implements CidsBeanStore, EditorSaveListener, Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppePicturePanel.class);

    private static final ImageIcon ERROR_ICON = new ImageIcon(TreppeEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/file-broken.png"));
    private static final ImageIcon FOLDER_ICON = new ImageIcon(TreppeEditor.class.getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inode-directory.png"));
    private static final Pattern IMAGE_FILE_PATTERN = Pattern.compile(
            ".*\\.(bmp|png|jpg|jpeg|tif|tiff|wbmp)$",
            Pattern.CASE_INSENSITIVE);

    private static final String WEBDAV_DIRECTORY;
    private static final WebDavHelper WEBDAV_HELPER;

    static {
        String directory = null;
        WebDavHelper webdavHelper = null;
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
            String pass = bundle.getString("password");

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }
            final String user = bundle.getString("user");
            directory = bundle.getString("url_treppen");

            webdavHelper = new WebDavHelper(Proxy.fromPreferences(), user, pass, false);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }

        WEBDAV_DIRECTORY = directory;
        WEBDAV_HELPER = webdavHelper;
    }

    private static final int CACHE_SIZE = 20;
    private static final Map<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new LinkedHashMap<String, SoftReference<BufferedImage>>(CACHE_SIZE) {

            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, SoftReference<BufferedImage>> eldest) {
                return size() >= CACHE_SIZE;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final JFileChooser fileChooser = new JFileChooser();
    private final List<CidsBean> removeNewAddedFotoBean = new ArrayList<>();
    private final List<CidsBean> removedFotoBeans = new ArrayList<>();
    private final MappingComponent map = new MappingComponent();
    private final PropertyChangeListener listRepaintListener;
    private final Timer timer;
    private boolean listListenerEnabled = true;
    private boolean resizeListenerEnabled;
    private CidsBean cidsBean;
    private ImageResizeWorker currentResizeWorker;

    private CidsBean fotoCidsBean;
    private BufferedImage image;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddImg;
    private javax.swing.JButton btnNextImg;
    private javax.swing.JButton btnPrevImg;
    private javax.swing.JButton btnRemoveImg;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jspFotoList;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private javax.swing.JLabel lblHeaderFotos;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JList lstFotos;
    private javax.swing.JPanel pnlCtrlBtn;
    private javax.swing.JPanel pnlCtrlButtons;
    private javax.swing.JPanel pnlFoto;
    private de.cismet.tools.gui.RoundedPanel pnlFotos;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderFotos;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel pnlVorschau;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppePicturePanel object.
     */
    public TreppePicturePanel() {
        this(true);
    }

    /**
     * Creates new form TreppePicturePanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppePicturePanel(final boolean editable) {
        initComponents();

        fileChooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.isDirectory() || IMAGE_FILE_PATTERN.matcher(f.getName()).matches();
                }

                @Override
                public String getDescription() {
                    return "Bilddateien";
                }
            });
        fileChooser.setMultiSelectionEnabled(true);

        listRepaintListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    lstFotos.repaint();
                }
            };

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
//                    if (isShowing()) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
//                    } else {
//                        timer.restart();
//                    }
                        }
                    }
                });
        timer.setRepeats(false);

        btnAddImg.setVisible(editable);
        btnRemoveImg.setVisible(editable);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pnlFotos = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderFotos = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderFotos = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jspFotoList = new javax.swing.JScrollPane();
        lstFotos = new javax.swing.JList();
        pnlCtrlButtons = new javax.swing.JPanel();
        btnAddImg = new javax.swing.JButton();
        btnRemoveImg = new javax.swing.JButton();
        pnlMap = new javax.swing.JPanel();
        pnlVorschau = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblVorschau = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pnlFoto = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        pnlCtrlBtn = new javax.swing.JPanel();
        btnPrevImg = new javax.swing.JButton();
        btnNextImg = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlFotos.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlFotos.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlFotos.setLayout(new java.awt.GridBagLayout());

        pnlHeaderFotos.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderFotos.setForeground(new java.awt.Color(51, 51, 51));
        pnlHeaderFotos.setLayout(new java.awt.FlowLayout());

        lblHeaderFotos.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderFotos,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.lblHeaderFotos.text")); // NOI18N
        pnlHeaderFotos.add(lblHeaderFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlFotos.add(pnlHeaderFotos, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jspFotoList.setMinimumSize(new java.awt.Dimension(250, 130));

        lstFotos.setMinimumSize(new java.awt.Dimension(250, 130));
        lstFotos.setPreferredSize(new java.awt.Dimension(250, 130));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.bilder}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstFotos);
        bindingGroup.addBinding(jListBinding);

        lstFotos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFotosValueChanged(evt);
                }
            });
        jspFotoList.setViewportView(lstFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jspFotoList, gridBagConstraints);

        pnlCtrlButtons.setOpaque(false);
        pnlCtrlButtons.setLayout(new java.awt.GridBagLayout());

        btnAddImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png")));    // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnAddImg,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.btnAddImg.text")); // NOI18N
        btnAddImg.setBorderPainted(false);
        btnAddImg.setContentAreaFilled(false);
        btnAddImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlCtrlButtons.add(btnAddImg, gridBagConstraints);

        btnRemoveImg.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png")));    // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnRemoveImg,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.btnRemoveImg.text")); // NOI18N
        btnRemoveImg.setBorderPainted(false);
        btnRemoveImg.setContentAreaFilled(false);
        btnRemoveImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlCtrlButtons.add(btnRemoveImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        jPanel1.add(pnlCtrlButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel2.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlFotos.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(pnlFotos, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setMinimumSize(new java.awt.Dimension(400, 200));
        pnlMap.setPreferredSize(new java.awt.Dimension(400, 200));
        pnlMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(pnlMap, gridBagConstraints);
        pnlMap.add(map, BorderLayout.CENTER);

        pnlVorschau.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        lblVorschau.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorschau,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.lblVorschau.text")); // NOI18N
        semiRoundedPanel2.add(lblVorschau);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlVorschau.add(semiRoundedPanel2, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        pnlFoto.setOpaque(false);
        pnlFoto.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPicture,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.lblPicture.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlFoto.add(lblPicture, gridBagConstraints);

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 60));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlFoto.add(lblBusy, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanel3.add(pnlFoto, gridBagConstraints);

        pnlCtrlBtn.setOpaque(false);
        pnlCtrlBtn.setPreferredSize(new java.awt.Dimension(100, 50));
        pnlCtrlBtn.setLayout(new java.awt.GridBagLayout());

        btnPrevImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png")));              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPrevImg,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.btnPrevImg.text")); // NOI18N
        btnPrevImg.setBorderPainted(false);
        btnPrevImg.setFocusPainted(false);
        btnPrevImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCtrlBtn.add(btnPrevImg, gridBagConstraints);

        btnNextImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png")));             // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnNextImg,
            org.openide.util.NbBundle.getMessage(TreppePicturePanel.class, "TreppePicturePanel.btnNextImg.text")); // NOI18N
        btnNextImg.setBorderPainted(false);
        btnNextImg.setFocusPainted(false);
        btnNextImg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextImgActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCtrlBtn.add(btnNextImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanel3.add(pnlCtrlBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVorschau.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(pnlVorschau, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFotosValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFotosValueChanged
        if (!evt.getValueIsAdjusting() && listListenerEnabled) {
            loadFoto();
        }
    }                                                                                   //GEN-LAST:event_lstFotosValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddImgActionPerformed
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
            final File[] selFiles = fileChooser.getSelectedFiles();
            if ((selFiles != null) && (selFiles.length > 0)) {
                CismetThreadPool.execute(new ImageUploadWorker(Arrays.asList(selFiles)));
            }
        }
    }                                                                             //GEN-LAST:event_btnAddImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveImgActionPerformed
        final Object[] selection = lstFotos.getSelectedValues();
        if ((selection != null) && (selection.length > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Fotos wirklich gelöscht werden?",
                    "Fotos entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    listListenerEnabled = false;
                    final List<Object> removeList = Arrays.asList(selection);
                    final List<CidsBean> fotos = cidsBean.getBeanCollectionProperty("bilder");
                    if (fotos != null) {
                        fotos.removeAll(removeList);
                    }
                    // TODO set the laufende_nr
                    for (int i = 0; i < lstFotos.getModel().getSize(); i++) {
                        final CidsBean foto = (CidsBean)lstFotos.getModel().getElementAt(i);
                        foto.setProperty("laufende_nummer", i + 1);
                    }

                    for (final Object toDeleteObj : removeList) {
                        if (toDeleteObj instanceof CidsBean) {
                            final CidsBean fotoToDelete = (CidsBean)toDeleteObj;
                            final String file = String.valueOf(fotoToDelete.getProperty("url.object_name"));
                            IMAGE_CACHE.remove(file);
                            removedFotoBeans.add(fotoToDelete);
                        }
                    }
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                    showExceptionToUser(ex, this);
                } finally {
                    // TODO check the laufende_nummer attribute
                    listListenerEnabled = true;
                    final int modelSize = lstFotos.getModel().getSize();
                    if (modelSize > 0) {
                        lstFotos.setSelectedIndex(0);
                    } else {
                        image = null;
                        lblPicture.setIcon(FOLDER_ICON);
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemoveImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPrevImgActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() - 1);
    }                                                                              //GEN-LAST:event_btnPrevImgActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextImgActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNextImgActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() + 1);
    }                                                                              //GEN-LAST:event_btnNextImgActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void loadFoto() {
        final Object fotoObj = lstFotos.getSelectedValue();
        if (fotoCidsBean != null) {
            fotoCidsBean.removePropertyChangeListener(listRepaintListener);
        }
        if (fotoObj instanceof CidsBean) {
            fotoCidsBean = (CidsBean)fotoObj;
            fotoCidsBean.addPropertyChangeListener(listRepaintListener);
            final String fileObj = (String)fotoCidsBean.getProperty("url.object_name");
            boolean cacheHit = false;
            if (fileObj != null) {
//                final String[] file = fileObj.toString().split("/");
//                final String object_name = file[file.length - 1];
                final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(fileObj);
                if (cachedImageRef != null) {
                    final BufferedImage cachedImage = cachedImageRef.get();
                    if (cachedImage != null) {
                        cacheHit = true;
                        image = cachedImage;
                        showWait(true);
                        resizeListenerEnabled = true;
                        timer.restart();
                    }
                }
                if (!cacheHit) {
                    CismetThreadPool.execute(new LoadSelectedImageWorker(fileObj));
                }
            }
        } else {
            image = null;
            lblPicture.setIcon(FOLDER_ICON);
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        bindingGroup.bind();

        lstFotos.getModel().addListDataListener(new ListDataListener() {

                @Override
                public void intervalAdded(final ListDataEvent e) {
                    defineButtonStatus();
                }

                @Override
                public void intervalRemoved(final ListDataEvent e) {
                    defineButtonStatus();
                }

                @Override
                public void contentsChanged(final ListDataEvent e) {
                    defineButtonStatus();
                }
            });
        if (lstFotos.getModel().getSize() > 0) {
            lstFotos.setSelectedIndex(0);
        }

        initMap();
    }

    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        final int selectedIdx = lstFotos.getSelectedIndex();
        btnPrevImg.setEnabled(selectedIdx > 0);
        btnNextImg.setEnabled((selectedIdx < (lstFotos.getModel().getSize() - 1)) && (selectedIdx > -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (wait) {
            if (!lblBusy.isBusy()) {
//                cardLayout.show(pnlFoto, "busy");
                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                btnAddImg.setEnabled(false);
                btnRemoveImg.setEnabled(false);
                lstFotos.setEnabled(false);
                btnPrevImg.setEnabled(false);
                btnNextImg.setEnabled(false);
            }
        } else {
//            cardLayout.show(pnlFoto, "preview");
            lblBusy.setBusy(false);
            lblBusy.setVisible(false);
            btnAddImg.setEnabled(true);
            btnRemoveImg.setEnabled(true);
            lstFotos.setEnabled(true);
            defineButtonStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private BufferedImage downloadImageFromWebDAV(final String fileName) throws Exception {
        final InputStream iStream = WEBDAV_HELPER.getFileFromWebDAV(fileName, WEBDAV_DIRECTORY);
        try {
            final ImageInputStream iiStream = ImageIO.createImageInputStream(iStream);
            final Iterator<ImageReader> itReader = ImageIO.getImageReaders(iiStream);
            if (itReader.hasNext()) {
                final ImageReader reader = itReader.next();
                final ProgressMonitor monitor = new ProgressMonitor(this, "Bild wird übertragen...", "", 0, 100);
//            monitor.setMillisToPopup(500);
                reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                        @Override
                        public void sequenceStarted(final ImageReader source, final int minIndex) {
                        }

                        @Override
                        public void sequenceComplete(final ImageReader source) {
                        }

                        @Override
                        public void imageStarted(final ImageReader source, final int imageIndex) {
                            monitor.setProgress(monitor.getMinimum());
                        }

                        @Override
                        public void imageProgress(final ImageReader source, final float percentageDone) {
                            if (monitor.isCanceled()) {
                                try {
                                    iiStream.close();
                                } catch (final IOException ex) {
                                    // NOP
                                }
                            } else {
                                monitor.setProgress(Math.round(percentageDone));
                            }
                        }

                        @Override
                        public void imageComplete(final ImageReader source) {
                            monitor.setProgress(monitor.getMaximum());
                        }

                        @Override
                        public void thumbnailStarted(final ImageReader source,
                                final int imageIndex,
                                final int thumbnailIndex) {
                        }

                        @Override
                        public void thumbnailProgress(final ImageReader source, final float percentageDone) {
                        }

                        @Override
                        public void thumbnailComplete(final ImageReader source) {
                        }

                        @Override
                        public void readAborted(final ImageReader source) {
                            monitor.close();
                        }
                    });

                final ImageReadParam param = reader.getDefaultReadParam();
                reader.setInput(iiStream, true, true);
                final BufferedImage result;
                try {
                    result = reader.read(0, param);
                } finally {
                    reader.dispose();
                    iiStream.close();
                }
                return result;
            } else {
                return null;
            }
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bi         DOCUMENT ME!
     * @param   component  DOCUMENT ME!
     * @param   insetX     DOCUMENT ME!
     * @param   insetY     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image adjustScale(final BufferedImage bi,
            final JComponent component,
            final int insetX,
            final int insetY) {
        final double scalex = (double)component.getWidth() / bi.getWidth();
        final double scaley = (double)component.getHeight() / bi.getHeight();
        final double scale = Math.min(scalex, scaley);
        if (scale <= 1d) {
            return bi.getScaledInstance((int)(bi.getWidth() * scale) - insetX,
                    (int)(bi.getHeight() * scale)
                            - insetY,
                    Image.SCALE_SMOOTH);
        } else {
            return bi;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Fehler beim Übertragen des Bildes!");
        lblPicture.setToolTipText(tooltip);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ex      DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    private static void showExceptionToUser(final Exception ex, final JComponent parent) {
        final ErrorInfo ei = new ErrorInfo(
                "Fehler",
                "Beim Vorgang ist ein Fehler aufgetreten",
                null,
                null,
                ex,
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(parent, ei);
    }
    @Override
    public void editorClosed(final EditorClosedEvent event) {
        if (EditorSaveStatus.SAVE_SUCCESS == event.getStatus()) {
            for (final CidsBean deleteBean : removedFotoBeans) {
                final String fileName = (String)deleteBean.getProperty("url_object_name");
                final StringBuilder fileDir = new StringBuilder();
                fileDir.append(deleteBean.getProperty("url.url_base_id.prot_prefix").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.server").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.path").toString());

                try {
                    WEBDAV_HELPER.deleteFileFromWebDAV(fileName,
                        fileDir.toString());
                    deleteBean.delete();
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        } else {
            for (final CidsBean deleteBean : removeNewAddedFotoBean) {
                final String fileName = (String)deleteBean.getProperty("url.object_name");
                final StringBuilder fileDir = new StringBuilder();
                fileDir.append(deleteBean.getProperty("url.url_base_id.prot_prefix").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.server").toString());
                fileDir.append(deleteBean.getProperty("url.url_base_id.path").toString());
                WEBDAV_HELPER.deleteFileFromWebDAV(fileName,
                    fileDir.toString());
            }
        }
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public void dispose() {
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        try {
            if (cidsBean != null) {
                final Object geoObj = cidsBean.getProperty("geometrie.geo_field");
                if (geoObj instanceof Geometry) {
                    final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                            AlkisConstants.COMMONS.SRS_SERVICE);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ALKISConstatns.Commons.GeoBUffer: " + AlkisConstants.COMMONS.GEO_BUFFER);
                    }
                    final XBoundingBox box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                                AlkisConstants.COMMONS.GEO_BUFFER));
                    final double diagonalLength = Math.sqrt((box.getWidth() * box.getWidth())
                                    + (box.getHeight() * box.getHeight()));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Buffer for map: " + diagonalLength);
                    }
                    final XBoundingBox bufferedBox = new XBoundingBox(box.getGeometry().buffer(diagonalLength));
                    final Runnable mapRunnable = new Runnable() {

                            @Override
                            public void run() {
                                final ActiveLayerModel mappingModel = new ActiveLayerModel();
                                mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                                mappingModel.addHome(new XBoundingBox(
                                        bufferedBox.getX1(),
                                        bufferedBox.getY1(),
                                        bufferedBox.getX2(),
                                        bufferedBox.getY2(),
                                        AlkisConstants.COMMONS.SRS_SERVICE,
                                        true));
                                final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                            AlkisConstants.COMMONS.MAP_CALL_STRING));
                                swms.setName("Treppe");
                                final StyledFeature dsf = new DefaultStyledFeature();
                                dsf.setGeometry(pureGeom);
                                dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));
                                dsf.setLineWidth(3);
                                dsf.setLinePaint(new Color(1, 0, 0, 1f));
                                // add the raster layer to the model
                                mappingModel.addLayer(swms);
                                // set the model
                                map.setMappingModel(mappingModel);
                                // initial positioning of the map
                                final int duration = map.getAnimationDuration();
                                map.setAnimationDuration(0);
                                map.gotoInitialBoundingBox();
                                // interaction mode
                                map.setInteractionMode(MappingComponent.ZOOM);
                                // finally when all configurations are done ...
                                map.unlock();
                                map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                                        @Override
                                        public void mouseClicked(final PInputEvent evt) {
                                            if (evt.getClickCount() > 1) {
                                                final CidsBean bean = cidsBean;
                                                ObjectRendererUtils.switchToCismapMap();
                                                ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                            }
                                        }
                                    });
                                map.setInteractionMode("MUTE");
                                map.getFeatureCollection().addFeature(dsf);
                                map.setAnimationDuration(duration);
                            }
                        };
                    if (EventQueue.isDispatchThread()) {
                        mapRunnable.run();
                    } else {
                        EventQueue.invokeLater(mapRunnable);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error("error while init map", ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageUploadWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        private final Collection<File> fotos;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageUploadWorker object.
         *
         * @param  fotos  DOCUMENT ME!
         */
        public ImageUploadWorker(final Collection<File> fotos) {
            this.fotos = fotos;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            final Collection<CidsBean> newBeans = new ArrayList<>();
            int laufendeNummer = lstFotos.getModel().getSize() + 1;
            for (final File imageFile : fotos) {
                WEBDAV_HELPER.uploadFileToWebDAV(
                    imageFile.getName(),
                    imageFile,
                    WEBDAV_DIRECTORY,
                    TreppePicturePanel.this);

                final MetaClass MB_MC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "url_base");

                final String protPrefix = WEBDAV_DIRECTORY.substring(0, WEBDAV_DIRECTORY.indexOf("://")) + "://";
                final String server = WEBDAV_DIRECTORY.substring(protPrefix.length(),
                        WEBDAV_DIRECTORY.indexOf("/", protPrefix.length()));
                final String path = WEBDAV_DIRECTORY.substring(protPrefix.length() + server.length());

                final String query = String.format(
                        "SELECT %s, %s FROM %s WHERE prot_prefix ILIKE '%s' AND server ILIKE '%s' AND path ILIKE '%s';",
                        MB_MC.getID(),
                        MB_MC.getPrimaryKey(),
                        MB_MC.getTableName(),
                        protPrefix,
                        server,
                        path);
                final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);

                final CidsBean urlBase;
                if (metaObjects.length <= 0) {
                    final CidsBean urlBaseTmp = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "url_base");
                    urlBaseTmp.setProperty("prot_prefix", protPrefix);
                    urlBaseTmp.setProperty("server", server);
                    urlBaseTmp.setProperty("path", path);
                    urlBase = urlBaseTmp.persist();
                } else {
                    urlBase = metaObjects[0].getBean();
                }

                final CidsBean url = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "url");
                url.setProperty("url_base_id", urlBase);
                url.setProperty("object_name", imageFile.getName());

                final CidsBean newFotoBean = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "Treppe_bild");
                newFotoBean.setProperty("laufende_nummer", laufendeNummer);
                newFotoBean.setProperty("name", imageFile.getName());
                newFotoBean.setProperty("url", url);
                newBeans.add(newFotoBean);
                laufendeNummer++;
            }
            return newBeans;
        }

        @Override
        protected void done() {
            try {
                final Collection<CidsBean> newBeans = get();
                if (!newBeans.isEmpty()) {
                    final List<CidsBean> oldBeans = cidsBean.getBeanCollectionProperty("bilder");
                    oldBeans.addAll(newBeans);
                    removeNewAddedFotoBean.addAll(newBeans);
                    lstFotos.setSelectedValue(newBeans.iterator().next(), true);
                } else {
                    lblPicture.setIcon(FOLDER_ICON);
                }
            } catch (final InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (final ExecutionException ex) {
                LOG.error(ex, ex);
            } finally {
                showWait(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String file;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  toLoad  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final String toLoad) {
            this.file = toLoad;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((file != null) && (file.length() > 0)) {
                return downloadImageFromWebDAV(file);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    IMAGE_CACHE.put(file, new SoftReference<>(image));
                    resizeListenerEnabled = true;
                    timer.restart();
                } else {
                    indicateError("Bild konnte nicht geladen werden: Unbekanntes Bildformat");
                }
            } catch (final InterruptedException ex) {
                image = null;
                LOG.warn(ex, ex);
            } catch (final ExecutionException ex) {
                image = null;
                LOG.error(ex, ex);
                String causeMessage = "";
                final Throwable cause = ex.getCause();
                if (cause != null) {
                    causeMessage = cause.getMessage();
                }
                indicateError(causeMessage);
            } finally {
                if (image == null) {
                    showWait(false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
            // TODO image im EDT auslesen und final speichern!
            if (image != null) {
                lblPicture.setText("Wird neu skaliert...");
                lstFotos.setEnabled(false);
            }
//            log.fatal("RESIZE Image!", new Exception());
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
//                if (panButtons.getSize().getWidth() + 10 < panPreview.getSize().getWidth()) {
                // ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panPreview, 20, 20));
                final ImageIcon result = new ImageIcon(adjustScale(image, pnlFoto, 20, 20));
                return result;
//                } else {
//                    return new ImageIcon(image);
//                }
            } else {
                return null;
            }
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    lblPicture.setIcon(result);
                    lblPicture.setText("");
                    lblPicture.setToolTipText(null);
                } catch (final InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (final ExecutionException ex) {
                    LOG.error(ex, ex);
                    lblPicture.setText("Fehler beim Skalieren!");
                } finally {
                    showWait(false);
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }
}
