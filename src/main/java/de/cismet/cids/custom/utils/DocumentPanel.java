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
package de.cismet.cids.custom.utils;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.graphics.ShadowRenderer;

import org.openide.util.Exceptions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.TransferHandler;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.commons.security.WebDavClient;
import de.cismet.commons.security.WebDavHelper;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.URLSplitter;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.WaitDialog;
import de.cismet.tools.gui.documents.DefaultDocument;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.WebDavDownload;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class DocumentPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(DocumentPanel.class);
    private static final String ICON_RES_PATH = "/de/cismet/cids/custom/utils/";
    private static final ImageIcon NO_PREVIEW = new ImageIcon(DocumentPanel.class.getResource(
                "/de/cismet/cids/custom/utils/nopreview.png"));
    private static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
    public static final int SHADOW_SIZE = 4;
    public static final int INSET = 55;
    public static final int ANIMATION_RATE = 30;
    public static final String EXTENSIONS = "\\.(jpg|jpeg|gif|png|pdf|html|doc|xls|txt)";
    private static final Icon IDLE_ICON;
    private static final Icon[] BUSY_ICONS;

    private static final Map<String, ImageIcon> FILE_TYPE_ICONS;
    private static final ImageIcon UNKNOWN_FORMAT = new ImageIcon(DocumentPanel.class.getResource(
                ICON_RES_PATH
                        + "unknown.png"));
    public static final String EXTENTION_PDF = "pdf";
    public static final String EXTENTION_JPG = "jpg";
    public static final String EXTENTION_JPEG = "jpeg";
    public static final String EXTENTION_GIF = "gif";
    public static final String EXTENTION_PNG = "png";
    public static final String EXTENTION_BMP = "bmp";
    public static final String EXTENTION_HTML = "html";
    public static final String EXTENTION_DOC = "doc";
    public static final String EXTENTION_XLS = "xls";

    static {
        final Class<DocumentPanel> c = DocumentPanel.class;
        FILE_TYPE_ICONS = new HashMap<String, ImageIcon>();
        FILE_TYPE_ICONS.put(EXTENTION_PDF, new ImageIcon(c.getResource(ICON_RES_PATH + "pdf.png")));
        FILE_TYPE_ICONS.put(EXTENTION_JPG, new ImageIcon(c.getResource(ICON_RES_PATH + "image.png")));
        FILE_TYPE_ICONS.put(EXTENTION_JPEG, new ImageIcon(c.getResource(ICON_RES_PATH + "image.png")));
        FILE_TYPE_ICONS.put(EXTENTION_GIF, new ImageIcon(c.getResource(ICON_RES_PATH + "image.png")));
        FILE_TYPE_ICONS.put(EXTENTION_BMP, new ImageIcon(c.getResource(ICON_RES_PATH + "image.png")));
        FILE_TYPE_ICONS.put(EXTENTION_PNG, new ImageIcon(c.getResource(ICON_RES_PATH + "image.png")));
        FILE_TYPE_ICONS.put(EXTENTION_HTML, new ImageIcon(c.getResource(ICON_RES_PATH + "html.png")));
        FILE_TYPE_ICONS.put(EXTENTION_DOC, new ImageIcon(c.getResource(ICON_RES_PATH + "doc.png")));
        FILE_TYPE_ICONS.put(EXTENTION_XLS, new ImageIcon(c.getResource(ICON_RES_PATH + "xls.png")));
    }

    static {
        // Prepare the icons
        BUSY_ICONS = new Icon[15];
        for (int i = 0; i < BUSY_ICONS.length; i++) {
            BUSY_ICONS[i] = new ImageIcon(DocumentPanel.class.getResource(ICON_RES_PATH + "busy-icon" + i + ".png"));
        }
        IDLE_ICON = new ImageIcon(DocumentPanel.class.getResource(ICON_RES_PATH + "idle-icon.png"));
    }

    //~ Instance fields --------------------------------------------------------

    // --
    // private final DefaultListModel docListModel;
    private final Timer busyIconTimer;
    private int busyIconIndex = 0;
    private SwingWorker<ImageIcon, Void> previewWorker;
    private Collection<CidsBean> dokumente = null;
    private boolean inEditMode = false;

    private final WebDavClient webDavClient;
    private final String webDavUrl;
    private final String containerDomain;
    private final String containerTableName;
    private final String containerUrlProperty;
    private final String containerDescriptionProperty;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblAbsolutePath;
    private javax.swing.JLabel lblPreview;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList<String> lstDocList;
    private javax.swing.JMenuItem miDelete;
    private javax.swing.JPanel panList;
    private javax.swing.JPanel panPreviewIntern;
    private javax.swing.JPanel panPreviewScp;
    private javax.swing.JPanel panStatus;
    private javax.swing.JPopupMenu popMenu;
    private javax.swing.JScrollPane scpDocList;
    private javax.swing.JScrollPane scpPreview;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DocumentPanel2 object.
     */
    public DocumentPanel() {
        initComponents();

        this.webDavClient = null;
        this.webDavUrl = null;
        this.containerDomain = null;
        this.containerTableName = null;
        this.containerUrlProperty = null;
        this.containerDescriptionProperty = null;
        busyIconTimer = null;
    }

    /**
     * Creates new form DocumentPanel2.
     *
     * @param  webDavClient                  DOCUMENT ME!
     * @param  webDavUrl                     DOCUMENT ME!
     * @param  containerDomain               DOCUMENT ME!
     * @param  containerTableName            DOCUMENT ME!
     * @param  containerUrlProperty          DOCUMENT ME!
     * @param  containerDescriptionProperty  DOCUMENT ME!
     */
    public DocumentPanel(final WebDavClient webDavClient,
            final String webDavUrl,
            final String containerDomain,
            final String containerTableName,
            final String containerUrlProperty,
            final String containerDescriptionProperty) {
        this.webDavClient = webDavClient;
        this.webDavUrl = webDavUrl;
        this.containerDomain = containerDomain;
        this.containerTableName = containerTableName;
        this.containerUrlProperty = containerUrlProperty;
        this.containerDescriptionProperty = containerDescriptionProperty;

        // docListModel = new DocumentListModel();
        initComponents();
        // Enable "delete"-key to remove selected items from list
        final Action deleteAction = new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (inEditMode) {
                        deleteSelectedListItems();
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Can not remove document because it not in edit mode.");
                        }
                    }
                }
            };

        final InputMap im = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        final KeyStroke delete = KeyStroke.getKeyStroke("DELETE");
        im.put(delete, "delete");
        getActionMap().put("delete", deleteAction);
        // Drag and Drop for the list
        lstDocList.setTransferHandler(new DocumentPanel.DocListTransferHandler());
        lstDocList.setDragEnabled(true);
        // Hand cursor on mouseover for the preview label
        decorateComponentWithMouseOverCursorChange(lblPreview, Cursor.HAND_CURSOR, Cursor.DEFAULT_CURSOR);
        // Set ListCellRenderer that recognizes important filetypes
        lstDocList.setCellRenderer(new DocumentPanel.DocumentListCellRenderer());
        // Configure the spinner animation
        final int busyAnimationRate = ANIMATION_RATE;
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        busyIconIndex = (busyIconIndex + 1) % BUSY_ICONS.length;
                        lblStatus.setIcon(BUSY_ICONS[busyIconIndex]);
                    }
                });
        lblStatus.setIcon(IDLE_ICON);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static URL getURL(final CidsBean url) {
        try {
            final CidsBean urlBase = (CidsBean)url.getProperty("url_base");
            return new URL((String)urlBase.getProperty("prot_prefix")
                            + (String)urlBase.getProperty("server")
                            + (String)urlBase.getProperty("path")
                            + (String)url.getProperty("object_name"));
        } catch (MalformedURLException ex) {
            // TODO log
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bildURL          DOCUMENT ME!
     * @param   maxPixelX        DOCUMENT ME!
     * @param   maxPixelY        DOCUMENT ME!
     * @param   shadowSize       DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   webDavdirectory  DOCUMENT ME!
     * @param   parent           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon loadPicture(final String bildURL,
            final int maxPixelX,
            final int maxPixelY,
            final int shadowSize,
            final WebDavClient webDavClient,
            final String webDavdirectory,
            final Component parent) {
        ImageIcon bild = null;
        if ((bildURL != null) && (bildURL.length() > 0)) {
            final String urlString = bildURL.trim();
            Image buffImage = new DefaultDocument(urlString, urlString, webDavClient, parent, webDavdirectory)
                        .getPreview(maxPixelX, maxPixelY);
            if (buffImage != null) {
                if (shadowSize > 0) {
                    // Static2DTools.getFasterScaledInstance(buffImage, width, height,
                    // RenderingHints.VALUE_INTERPOLATION_BICUBIC, true)
                    buffImage = generateShadow(buffImage, shadowSize);
                }
                bild = new ImageIcon(buffImage);
            }
        }
        return bild;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   in           DOCUMENT ME!
     * @param   shadowPixel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BufferedImage generateShadow(final Image in, final int shadowPixel) {
        final BufferedImage input;
        if (in instanceof BufferedImage) {
            input = (BufferedImage)in;
        } else {
            final BufferedImage temp = new BufferedImage(in.getWidth(null),
                    in.getHeight(null),
                    BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics tg = temp.createGraphics();
            tg.drawImage(in, 0, 0, null);
            tg.dispose();
            input = temp;
        }

        final ShadowRenderer renderer = new ShadowRenderer(shadowPixel, 0.5f, Color.BLACK);
        final BufferedImage shadow = renderer.createShadow(input);
        final BufferedImage result = new BufferedImage(input.getWidth() + (2 * shadowPixel),
                input.getHeight()
                        + (2 * shadowPixel),
                BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D rg = result.createGraphics();
        rg.drawImage(shadow, 0, 0, null);
        rg.drawImage(input, 0, 0, null);
        rg.dispose();
        return result;
    }

    /**
     * Adds a mouse listener to the given component, so that the cursor will change on mouse entered/exited.
     *
     * <p>Hint: Uses the awt.Cursor.XXX constants!</p>
     *
     * @param   toDecorate    DOCUMENT ME!
     * @param   mouseEntered  DOCUMENT ME!
     * @param   mouseExited   DOCUMENT ME!
     *
     * @return  the listener that was added
     */
    public static MouseListener decorateComponentWithMouseOverCursorChange(final JComponent toDecorate,
            final int mouseEntered,
            final int mouseExited) {
        final MouseListener toAdd = new MouseAdapter() {

                @Override
                public void mouseEntered(final MouseEvent e) {
                    toDecorate.setCursor(new Cursor(mouseEntered));
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    toDecorate.setCursor(new Cursor(mouseExited));
                }
            };
        toDecorate.addMouseListener(toAdd);
        return toAdd;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getDokumente() {
        return dokumente;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dokumente  DOCUMENT ME!
     */
    public void setDokumente(final Collection<CidsBean> dokumente) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setDokumente");
        }
        this.dokumente = dokumente;
        firePropertyChange("DocumentPanel.Dokumente", null, dokumente);
//        bindingGroup.unbind();
//        bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     */
    private void downloadSelection() {
        final Object sel = lstDocList.getSelectedValue();
        if (sel instanceof CidsBean) {
            final CidsBean container = (CidsBean)sel;
            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                final String name = (String)container.getProperty(containerDescriptionProperty);
                final String path = getURL((CidsBean)container.getProperty(containerUrlProperty)).getPath();
                final String file = path.substring(path.lastIndexOf("/") + 1);
                final String fserverName = path.substring(path.lastIndexOf("/") + 1);
                String extension = "";
                if (fserverName.lastIndexOf(".") != -1) {
                    extension = fserverName.substring(fserverName.lastIndexOf("."));
                }
                String filename = name;

                if (name.lastIndexOf(".") != -1) {
                    filename = name.substring(0, name.lastIndexOf("."));
                }

                final String path2 = webDavUrl + WebDavHelper.encodeURL(file);
                if (WebDavHelper.isUrlAccessible(
                                webDavClient,
                                webDavUrl
                                + WebDavHelper.encodeURL(file))) {
                    DownloadManager.instance()
                            .add(new WebDavDownload(
                                    webDavClient,
                                    path2,
                                    jobname,
                                    filename
                                    + extension,
                                    filename,
                                    extension));
                } else {
                    DownloadManager.instance()
                            .add(new WebDavDownload(
                                    webDavClient,
                                    path2
                                    + ".thumbnail.jpg",
                                    jobname,
                                    filename
                                    + extension,
                                    filename,
                                    extension));
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   urlString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String addURLtoList(final String urlString) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("addURLToList set: " + getDokumente());
        }
        final String docName = urlString.substring(urlString.lastIndexOf("/") + 1);
        final String description = JOptionPane.showInputDialog(
                DocumentPanel.this,
                "Welche Beschriftung soll der Link haben?",
                docName);
        if ((description != null) && (description.length() > 0)) {
            // docListModel.addElement(DmsUrl.createDmsURLFromLink(urlString, description));
            if (LOG.isDebugEnabled()) {
                LOG.debug("addURLToList: " + getDokumente());
            }
            return description;
        } else {
            // cancel case
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fileList  DOCUMENT ME!
     */
    public void addFiles(final List<DocumentStruct> fileList) {
        if ((fileList != null) && (fileList.size() > 0)) {
            final WaitDialog wd = new WaitDialog(
                    StaticSwingTools.getParentFrame(this),
                    true,
                    "Speichere Dokument",
                    null);
            CismetThreadPool.execute(new DocumentUploadWorker(fileList, wd));
            StaticSwingTools.showDialog(wd);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteSelectedListItems() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteSelectedListItems: " + getDokumente());
        }
        for (final Object sel : lstDocList.getSelectedValuesList()) {
            dokumente.remove(sel);
        }
        final SwingWorker<?, ?> sw = previewWorker;
        if (sw != null) {
            sw.cancel(true);
        }
        lblPreview.setIcon(null);
        lblPreview.setText("");
        lstDocList.setSelectedIndex(lstDocList.getFirstVisibleIndex());
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteSelectedListItems: " + getDokumente());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JList getLstDocList() {
        return lstDocList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lstDocList  DOCUMENT ME!
     */
    public void setLstDocList(final JList lstDocList) {
        this.lstDocList = lstDocList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editable  DOCUMENT ME!
     */
    public void setEditable(final boolean editable) {
        inEditMode = editable;
    }

    @Override
    public void setOpaque(final boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (panList != null) {
            panList.setOpaque(isOpaque);
        }
        if (panStatus != null) {
            panStatus.setOpaque(isOpaque);
        }
        if (panPreviewIntern != null) {
            panPreviewIntern.setOpaque(isOpaque);
        }
        if (panPreviewScp != null) {
            panPreviewScp.setOpaque(isOpaque);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception             DOCUMENT ME!
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public CidsBean createUrlContainerFromLink(final String link, final String description) throws Exception {
        if ((link == null) || (description == null)) {
            throw new NullPointerException();
        }
        final CidsBean container = CidsBean.createNewCidsBeanFromTableName(
                containerDomain,
                containerTableName,
                ConnectionContext.createDummy());
        final CidsBean url = CidsBean.createNewCidsBeanFromTableName(
                containerDomain,
                "url",
                ConnectionContext.createDummy());
        final CidsBean base = CidsBean.createNewCidsBeanFromTableName(
                containerDomain,
                "url_base",
                ConnectionContext.createDummy());
        final URLSplitter splitter = new URLSplitter(link);
        container.setProperty(containerDescriptionProperty, description);
        url.setProperty("url_base_id", base);
        container.setProperty(containerUrlProperty, url);
        base.setProperty("path", splitter.getPath());
        base.setProperty("prot_prefix", splitter.getProt_prefix());
        base.setProperty("server", splitter.getServer());
        url.setProperty("object_name", splitter.getObject_name());
        return container;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        popMenu = new javax.swing.JPopupMenu();
        miDelete = new javax.swing.JMenuItem();
        panList = new javax.swing.JPanel();
        scpDocList = new javax.swing.JScrollPane();
        lstDocList = new javax.swing.JList<>();
        panPreviewScp = new javax.swing.JPanel();
        scpPreview = new javax.swing.JScrollPane();
        panPreviewIntern = new javax.swing.JPanel();
        lblPreview = new javax.swing.JLabel();
        panStatus = new javax.swing.JPanel();
        lblAbsolutePath = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(
            miDelete,
            org.openide.util.NbBundle.getMessage(DocumentPanel.class, "DocumentPanel.miDelete.text")); // NOI18N
        miDelete.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    miDeleteActionPerformed(evt);
                }
            });
        popMenu.add(miDelete);

        setLayout(new java.awt.GridBagLayout());

        panList.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    org.openide.util.NbBundle.getMessage(
                        DocumentPanel.class,
                        "DocumentPanel.panList.border.outsideBorder.title")),
                javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5))); // NOI18N
        panList.setLayout(new java.awt.BorderLayout());

        scpDocList.setMaximumSize(new java.awt.Dimension(200, 250));
        scpDocList.setMinimumSize(new java.awt.Dimension(200, 250));
        scpDocList.setPreferredSize(new java.awt.Dimension(200, 250));

        lstDocList.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lstDocListMousePressed(evt);
                }
                @Override
                public void mouseReleased(final java.awt.event.MouseEvent evt) {
                    lstDocListMouseReleased(evt);
                }
                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstDocListMouseClicked(evt);
                }
            });
        lstDocList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstDocListValueChanged(evt);
                }
            });
        scpDocList.setViewportView(lstDocList);

        panList.add(scpDocList, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(panList, gridBagConstraints);

        panPreviewScp.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    org.openide.util.NbBundle.getMessage(
                        DocumentPanel.class,
                        "DocumentPanel.panPreviewScp.border.outsideBorder.title")),
                javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5))); // NOI18N
        panPreviewScp.setLayout(new java.awt.BorderLayout());

        scpPreview.setMaximumSize(new java.awt.Dimension(225, 250));
        scpPreview.setMinimumSize(new java.awt.Dimension(225, 250));
        scpPreview.setPreferredSize(new java.awt.Dimension(225, 250));

        panPreviewIntern.setOpaque(false);
        panPreviewIntern.setLayout(new java.awt.GridBagLayout());

        lblPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPreview,
            org.openide.util.NbBundle.getMessage(DocumentPanel.class, "DocumentPanel.lblPreview.text")); // NOI18N
        lblPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPreview.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblPreviewMouseClicked(evt);
                }
            });
        panPreviewIntern.add(lblPreview, new java.awt.GridBagConstraints());

        scpPreview.setViewportView(panPreviewIntern);

        panPreviewScp.add(scpPreview, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(panPreviewScp, gridBagConstraints);

        panStatus.setMaximumSize(new java.awt.Dimension(15, 25));
        panStatus.setMinimumSize(new java.awt.Dimension(15, 25));
        panStatus.setOpaque(false);
        panStatus.setPreferredSize(new java.awt.Dimension(15, 25));
        panStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAbsolutePath,
            org.openide.util.NbBundle.getMessage(DocumentPanel.class, "DocumentPanel.lblAbsolutePath.text")); // NOI18N
        panStatus.add(lblAbsolutePath);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStatus,
            org.openide.util.NbBundle.getMessage(DocumentPanel.class, "DocumentPanel.lblStatus.text")); // NOI18N
        panStatus.add(lblStatus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panStatus, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void miDeleteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_miDeleteActionPerformed
        deleteSelectedListItems();
    }                                                                            //GEN-LAST:event_miDeleteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblPreviewMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblPreviewMouseClicked
        if (!evt.isPopupTrigger()) {
            downloadSelection();
        }
    }                                                                          //GEN-LAST:event_lblPreviewMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstDocListMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstDocListMouseClicked
        if ((evt.getClickCount() > 1) && !evt.isPopupTrigger()) {
            downloadSelection();
        }
    }                                                                          //GEN-LAST:event_lstDocListMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstDocListValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstDocListValueChanged
        lblPreview.setIcon(null);
        lblPreview.setText("");
        final Object toCast = lstDocList.getSelectedValue();
        if (toCast != null) {
            if (toCast instanceof CidsBean) {
                final CidsBean container = (CidsBean)toCast;
                final URL url = getURL((CidsBean)container.getProperty(containerUrlProperty));
                final String path = url.getPath();
                final String document = path.substring(path.lastIndexOf("/") + 1);
                if (document != null) {
                    busyIconTimer.start();
                    final SwingWorker<ImageIcon, Void> oldWorker = previewWorker;
                    if (oldWorker != null) {
                        oldWorker.cancel(true);
                    }
                    previewWorker = new PreviewWorker(document, url.toString());
                    THREAD_EXECUTOR.execute(previewWorker);
                }
            }
        }
    }                                                                                     //GEN-LAST:event_lstDocListValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstDocListMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstDocListMousePressed
        if (evt.isPopupTrigger() && !dokumente.isEmpty() && inEditMode) {
            popMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }                                                                          //GEN-LAST:event_lstDocListMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstDocListMouseReleased(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstDocListMouseReleased
        if (evt.isPopupTrigger() && !dokumente.isEmpty() && inEditMode) {
            popMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }                                                                           //GEN-LAST:event_lstDocListMouseReleased

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class DocListTransferHandler extends TransferHandler {

        //~ Methods ------------------------------------------------------------

        @Override
        public int getSourceActions(final JComponent c) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getSourceAction");
            }
//---> Drag disabled
//            if (c == lstDocList) {
//                return DnDConstants.ACTION_COPY;
//            }
            return DnDConstants.ACTION_NONE;
        }

        @Override
        protected Transferable createTransferable(final JComponent c) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("createTransferable");
            }
            if (c == lstDocList) {
                return new Transferable() {

                        @Override
                        public DataFlavor[] getTransferDataFlavors() {
                            return new DataFlavor[] { DataFlavor.javaFileListFlavor };
                        }

                        @Override
                        public boolean isDataFlavorSupported(final DataFlavor flavor) {
                            return DataFlavor.javaFileListFlavor.equals(flavor);
                        }

                        @Override
                        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException,
                            IOException {
                            final Object[] vals = lstDocList.getSelectedValues();
                            final List<CidsBean> urlList = new ArrayList<>();
                            for (final Object o : vals) {
                                if (o instanceof CidsBean) {
                                    urlList.add((CidsBean)o);
                                }
                            }
                            return urlList;
                        }
                    };
            }
            return super.createTransferable(c);
        }

        @Override
        public boolean canImport(final TransferHandler.TransferSupport support) {
            if (!inEditMode) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Application is not in edit mode, no drag & drop possible");
                }
                return false;
            }
            final DataFlavor[] flavs = support.getDataFlavors();
            for (final DataFlavor df : flavs) {
                if (df.equals(DataFlavor.javaFileListFlavor) || df.equals(DataFlavor.stringFlavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean importData(final TransferHandler.TransferSupport e) {
            try {
                final Transferable tr = e.getTransferable();
                final DataFlavor[] flavors = tr.getTransferDataFlavors();
                for (int i = 0; i < flavors.length; ++i) {
                    if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
                        // CAST
                        final Object possibleFileList = tr.getTransferData(flavors[i]);
                        if (possibleFileList instanceof List) {
                            final List mp = (List)possibleFileList;
//                            lstDocList.setEnabled(false);
                            // TODO add to list, erst File -> DmsUrl, dann adden
                            final List<DocumentStruct> docList = new ArrayList<>();
                            for (final Object o : mp) {
                                if (o instanceof File) {
                                    final File f = (File)o;
                                    final String desc = addURLtoList(f.toURI().toString());

                                    if (desc != null) {
                                        docList.add(new DocumentStruct(desc, f));
                                    }
                                }
                            }
                            addFiles(docList);
                            return true;
                        }
                    }
                }
                for (int i = 0; i < flavors.length; ++i) {
                    if (flavors[i].equals(DataFlavor.stringFlavor)) {
                        // CAST
                        final List<DocumentStruct> docList = new ArrayList<DocumentStruct>();
                        final String urls = (String)tr.getTransferData(DataFlavor.stringFlavor);
                        final StringTokenizer tokens = new StringTokenizer(urls);
                        while (tokens.hasMoreTokens()) {
                            final String urlString = tokens.nextToken();
                            try {
                                final File f = new File(new URL(urlString).toURI());

                                if (f.exists()) {
                                    final String desc = addURLtoList(urlString);

                                    if (desc != null) {
                                        docList.add(new DocumentStruct(desc, f));
                                    }
                                }
                            } catch (MalformedURLException ex) {
                                LOG.error("malformed url", ex);
                            }
                        }

                        addFiles(docList);
                        return true;
                    }
                }
            } catch (Throwable t) {
            }
            // Ein Problem ist aufgetreten
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class PreviewWorker extends SwingWorker<ImageIcon, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String document;
        private final String absPath;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PreviewWorker object.
         *
         * @param  document  DOCUMENT ME!
         * @param  absPath   DOCUMENT ME!
         */
        public PreviewWorker(final String document, final String absPath) {
            this.document = document;
            this.absPath = absPath;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            // smoothen fast list selection, only starting the procedure for a definite selection
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                // ignore
            }

            if ((document != null) && !isCancelled()) {
                lblPreview.setText("loading...");
                // setText-methods are threadsafe!
                return loadPicture(
                        document,
                        panPreviewScp.getWidth()
                                - INSET
                                - SHADOW_SIZE,
                        panPreviewScp.getHeight()
                                - INSET
                                - SHADOW_SIZE,
                        SHADOW_SIZE,
                        webDavClient,
                        webDavUrl,
                        DocumentPanel.this);
            }
            return null;
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                lblAbsolutePath.setText("");
                lblAbsolutePath.setToolTipText("");
                ImageIcon icon = null;
                try {
                    icon = get();
                } catch (InterruptedException ex) {
                    // todo/nothing
                } catch (ExecutionException ex) {
                    // todo/nothing
                }
                if (document != null) {
                    if (icon != null) {
                        lblPreview.setSize(icon.getIconHeight() + 1, icon.getIconWidth() + 1);
                        lblPreview.setIcon(icon);
                        lblPreview.setText("");
                    } else if (document != null) {
                        lblPreview.setIcon(NO_PREVIEW);
                        lblPreview.setSize(NO_PREVIEW.getIconWidth(), NO_PREVIEW.getIconHeight());
                        lblPreview.setText("<html>Could not create preview.<br>Click to open File!</html>");
                    }
                } else {
                    lblPreview.setIcon(null);
                    lblPreview.setText("");
                    lblPreview.setSize(0, 0);
                }
                busyIconTimer.stop();
                lblStatus.setIcon(IDLE_ICON);
            }
            previewWorker = null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DocumentStruct {

        //~ Instance fields ----------------------------------------------------

        private String name;
        private File file;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DocumentStruct object.
         *
         * @param  name  DOCUMENT ME!
         * @param  file  DOCUMENT ME!
         */
        public DocumentStruct(final String name, final File file) {
            this.name = name;
            this.file = file;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  the name
         */
        public String getName() {
            return name;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  name  the name to set
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the file
         */
        public File getFile() {
            return file;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  file  the file to set
         */
        public void setFile(final File file) {
            this.file = file;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class DocumentUploadWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final String FILE_PREFIX = "DOC-";

        //~ Instance fields ----------------------------------------------------

        private final Collection<DocumentStruct> docs;
        private final WaitDialog wd;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageUploadWorker object.
         *
         * @param  docs  fotos DOCUMENT ME!
         * @param  wd    DOCUMENT ME!
         */
        public DocumentUploadWorker(final Collection<DocumentStruct> docs, final WaitDialog wd) {
            this.docs = docs;
            this.wd = wd;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            try {
                final Collection<CidsBean> newBeans = new ArrayList<>();
                for (final DocumentStruct doc : docs) {
                    final File imageFile = doc.getFile();
                    final String webFileName = WebDavHelper.generateWebDAVFileName(FILE_PREFIX, imageFile);
                    WebDavHelper.uploadFileToWebDAV(
                        webFileName,
                        imageFile,
                        webDavUrl,
                        webDavClient,
                        DocumentPanel.this);
                    newBeans.add(createUrlContainerFromLink(webDavUrl + webFileName, doc.getName()));

                    try {
                        final BufferedImage img = ImageIO.read(new BufferedInputStream(new FileInputStream(imageFile)));
                        final double ratio = img.getWidth() / (double)img.getHeight();

                        final int newHeight = (int)(300 / ratio);
                        final Image scaledImg = img.getScaledInstance(300, newHeight, Image.SCALE_SMOOTH);
                        final BufferedImage thumbnail = new BufferedImage(300, newHeight, BufferedImage.TYPE_INT_RGB);
                        thumbnail.createGraphics().drawImage(scaledImg, 0, 0, null);

                        final String[] fileNameSplit = webFileName.split("\\.");
                        final String endung = fileNameSplit[fileNameSplit.length - 1];

                        final File tempFile = File.createTempFile(webFileName, endung);
                        ImageIO.write(thumbnail, endung, new FileOutputStream(tempFile));

                        WebDavHelper.uploadFileToWebDAV(
                            webFileName
                                    + ".thumbnail."
                                    + endung,
                            tempFile,
                            webDavUrl,
                            webDavClient,
                            null);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
                return newBeans;
            } finally {
                while (!wd.isVisible()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // nothing to do
                    }
                }

                wd.setVisible(false);
                wd.dispose();
            }
        }

        @Override
        protected void done() {
            try {
                final Collection<CidsBean> newBeans = get();

                if (!newBeans.isEmpty()) {
                    dokumente.addAll(newBeans);
                }
            } catch (InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (Exception ex) {
                LOG.error(ex, ex);
                final ErrorInfo ei = new ErrorInfo(
                        "Fehler",
                        "Beim Hochladen des Dokumentes ist ein Fehler aufgetreten.",
                        null,
                        null,
                        ex,
                        Level.SEVERE,
                        null);
                JXErrorPane.showDialog(DocumentPanel.this, ei);
            } finally {
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DocumentListCellRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final Color colorOdd;
        private final Color colorEven;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DocumentListCellRenderer object.
         */
        public DocumentListCellRenderer() {
            colorOdd = new Color(235, 235, 235);
            colorEven = new Color(215, 215, 215);
        }

        /**
         * Creates a new DocumentListCellRenderer object.
         *
         * @param  colorOdd   DOCUMENT ME!
         * @param  colorEven  DOCUMENT ME!
         */
        public DocumentListCellRenderer(final Color colorOdd, final Color colorEven) {
            this.colorOdd = colorOdd;
            this.colorEven = colorEven;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            ImageIcon imageIcon = null;
            if (value instanceof CidsBean) {
                try {
                    final CidsBean container = (CidsBean)value;
                    final URL url = getURL((CidsBean)container.getProperty(containerUrlProperty));
                    final URI file = url.toURI();
                    value = (String)container.getProperty(containerDescriptionProperty);
                    if (file != null) {
                        final String[] tmp = file.toString().split("\\.");
                        if ((tmp != null) && (tmp.length > 0)) {
                            final String extension = tmp[tmp.length - 1];
                            imageIcon = FILE_TYPE_ICONS.get(extension);
                        }
                    }
                } catch (URISyntaxException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (!isSelected) {
                c.setBackground(((index % 2) == 1) ? colorOdd : colorEven);
            }
            setIcon((imageIcon != null) ? imageIcon : UNKNOWN_FORMAT);
            return c;
        }
    }
}
