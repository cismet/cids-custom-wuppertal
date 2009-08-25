/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.utils.ClassCacheMultiple;
import de.cismet.tools.gui.documents.DefaultDocument;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.graphics.ShadowRenderer;
import org.openide.util.Exceptions;

/**
 *
 * @author flughafen
 */
public class ObjectRendererUIUtils {

    public enum DateDiff {

        MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
    };
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ObjectRendererUIUtils.class);

    public static final MetaObject[] getLightweightMetaObjectsForTable(String tabName, final String[] fields) {
        return getLightweightMetaObjectsForTable(tabName, fields, null);

    }

    public static final MetaObject[] getLightweightMetaObjectsForTable(String tabName, final String[] fields, AbstractAttributeRepresentationFormater formatter) {
        if (formatter == null) {
            formatter = new AbstractAttributeRepresentationFormater() {

                @Override
                public String getRepresentation() {
                    final StringBuffer sb = new StringBuffer();
                    for (final String attribute : fields) {
                        sb.append(getAttribute(attribute.toLowerCase())).append(" ");
                    }
                    return sb.toString().trim();
                }
            };
        }
        try {
            final User user = SessionManager.getSession().getUser();
            final MetaClass mc = ClassCacheMultiple.getMetaClass(user.getDomain(), tabName);
            return SessionManager.getProxy().getAllLightweightMetaObjectsForClass(mc.getID(), user, fields, formatter);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return new MetaObject[0];
    }

    public static BufferedImage generateShadow(final Image in, final int shadowPixel) {
        if (in == null) {
            return null;
        }
        final BufferedImage input;
        if (in instanceof BufferedImage) {
            input = (BufferedImage) in;
        } else {
            final BufferedImage temp = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics tg = temp.createGraphics();
            tg.drawImage(in, 0, 0, null);
            tg.dispose();
            input = temp;
        }
        if (shadowPixel < 1) {
            return input;
        }
        final ShadowRenderer renderer = new ShadowRenderer(shadowPixel, 0.5f, Color.BLACK);
        final BufferedImage shadow = renderer.createShadow(input);
        final BufferedImage result = new BufferedImage(input.getWidth() + 2 * shadowPixel,
                input.getHeight() + 2 * shadowPixel, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D rg = result.createGraphics();
        rg.drawImage(shadow, 0, 0, null);
        rg.drawImage(input, 0, 0, null);
        rg.dispose();
        return result;
    }

    /**
     * Starts a background thread with loads the picture from the url, resizes it to the given maximums,
     * adds a dropshadow of the given length and then sets the whole picture on a given JLabel.
     *
     * Can be called from ANY thread, no matter if EDT or not!
     *
     * @param bildURL
     * @param maxPixelX
     * @param maxPixelY
     * @param shadowSize
     * @param toSet
     */
    public static void loadPictureAndSet(final String bildURL, final int maxPixelX, final int maxPixelY, final int shadowSize, final JLabel toSet) {
        if (bildURL != null && toSet != null) {
            final Thread loader = new Thread() {

                @Override
                public void run() {
                    try {
                        final ImageIcon finBild = loadPicture(bildURL, maxPixelX, maxPixelY, shadowSize);
                        if (finBild != null) {
                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    if (finBild != null) {
                                        toSet.setIcon(finBild);
                                    } else {
                                        toSet.setVisible(false);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        log.error("Exeption when loading picture " + bildURL + " : " + e, e);
                    }
                }
            };
            loader.start();
        }
    }

    /**
     * Starts a background thread with loads the picture from the url, resizes it to the given maximums,
     * adds a dropshadow of the given length and then sets the whole picture on a given JButton.
     *
     * Can be called from ANY thread, no matter if EDT or not!
     *
     * @param bildURL
     * @param maxPixelX
     * @param maxPixelY
     * @param shadowSize
     * @param toSet
     */
    public static void loadPictureAndSet(final String bildURL, final int maxPixelX, final int maxPixelY, final int shadowSize, final JButton toSet) {
        if (bildURL != null && toSet != null) {
            final Thread loader = new Thread() {

                @Override
                public void run() {
                    final ImageIcon finBild = loadPicture(bildURL, maxPixelX, maxPixelY, shadowSize);
                    if (finBild != null) {
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                if (finBild != null) {
                                    toSet.setIcon(finBild);
                                } else {
                                    toSet.setVisible(false);
                                }
                            }
                        });
                    }
                }
            };
            loader.start();
        }
    }

    /**
     * Starts a background thread with loads the picture from the url, resizes it to the given maximums,
     * adds a dropshadow of the given length.
     *
     * @param bildURL
     * @param maxPixelX
     * @param maxPixelY
     * @param shadowSize
     * @return ImageIcon with the loaded picture
     */
    public static ImageIcon loadPicture(final String bildURL, final int maxPixelX, final int maxPixelY, final int shadowSize) {
        ImageIcon bild = null;
        if (bildURL != null && bildURL.length() > 0) {
            final String urlString = bildURL.trim();

            Image buffImage = new DefaultDocument(urlString, urlString).getPreview(maxPixelX, maxPixelY);
            if (buffImage != null) {
                //Static2DTools.getFasterScaledInstance(buffImage, width, height, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true)
                if (shadowSize > 0) {
                    buffImage = generateShadow(buffImage, shadowSize);
                }
                bild = new ImageIcon(buffImage);
                return bild;
            }
        }
        return null;
    }

    /**
     * Adds a mouse listener to the given component, so that the cursor will change on mouse entered/exited.
     *
     * Hint: Uses the awt.Cursor.XXX constants!
     *
     * @param toDecorate
     * @param mouseEntered
     * @param mouseExited
     * @return the listener that was added
     */
    public static MouseListener decorateComponentWithMouseOverCursorChange(final JComponent toDecorate, final int mouseEntered, final int mouseExited) {
        final MouseListener toAdd = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                toDecorate.setCursor(new Cursor(mouseEntered));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toDecorate.setCursor(new Cursor(mouseExited));
            }
        };
        toDecorate.addMouseListener(toAdd);
        return toAdd;
    }

    public static void openURL(final String url) {
        if (url == null) {
            return;
        }
        String gotoUrl = url;
        try {
            de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
        } catch (Exception e2) {
            log.warn("das 1te Mal ging schief.Fehler beim Oeffnen von:" + gotoUrl + "\nLetzter Versuch", e2);
            try {
                gotoUrl = gotoUrl.replaceAll("\\\\", "/");
                gotoUrl = gotoUrl.replaceAll(" ", "%20");
                de.cismet.tools.BrowserLauncher.openURL("file:///" + gotoUrl);
            } catch (Exception e3) {
                log.error("Auch das 2te Mal ging schief.Fehler beim Oeffnen von:file://" + gotoUrl, e3);
            }
        }
    }

    /**
     * Makes the parameter table alphanumerically sortable.
     *
     * @param tbl
     */
    public static final void decorateTableWithSorter(JTable tbl) {
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tbl.getModel());
//        sorter.setSortsOnUpdates(true);
        for (int i = 0; i < tbl.getColumnCount(); ++i) {
            sorter.setComparator(i, AlphanumComparator.getInstance());
        }
        tbl.setRowSorter(sorter);
        tbl.getTableHeader().addMouseListener(new TableHeaderUnsortMouseAdapter(tbl));

    }

    public static final String getUrlFromBean(CidsBean bean, String suffix) {
        final Object obj = bean.getProperty("url_base_id");
        if (obj instanceof CidsBean) {
            final CidsBean urlBase = (CidsBean) obj;
            final StringBuffer bildURL = new StringBuffer(urlBase.getProperty("prot_prefix").toString());
            bildURL.append(urlBase.getProperty("server").toString());
            bildURL.append(urlBase.getProperty("path").toString());
            bildURL.append(bean.getProperty("object_name").toString());
            if (suffix != null) {
                bildURL.append(suffix);
            }
            return bildURL.toString();
        }
        return null;
    }
}

/**
 * MouseAdapter for remove sorting from the table when perfoming a right-clck
 * on the header
 *
 * @author srichter
 */
final class TableHeaderUnsortMouseAdapter extends MouseAdapter {

    public TableHeaderUnsortMouseAdapter(JTable tbl) {
        this.tbl = tbl;
    }
    private JTable tbl;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            tbl.getRowSorter().setSortKeys(null);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            tbl.getRowSorter().setSortKeys(null);
        }
    }
}
