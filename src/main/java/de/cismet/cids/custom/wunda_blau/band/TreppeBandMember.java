/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.band;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.*;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.tools.gui.jbands.BandMemberEvent;
import de.cismet.tools.gui.jbands.JBandCursorManager;
import de.cismet.tools.gui.jbands.interfaces.BandMember;
import de.cismet.tools.gui.jbands.interfaces.BandMemberListener;
import de.cismet.tools.gui.jbands.interfaces.BandMemberSelectable;
import de.cismet.tools.gui.jbands.interfaces.ModifiableBandMember;
import de.cismet.tools.gui.jbands.interfaces.Section;
import de.cismet.tools.gui.jbands.interfaces.StationaryBandMemberMouseListeningComponent;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class TreppeBandMember extends JXPanel implements ModifiableBandMember,
    Section,
    CidsBeanStore,
    StationaryBandMemberMouseListeningComponent,
    BandMemberSelectable,
    PropertyChangeListener,
    ActionListener,
    PopupMenuListener {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(TreppeBandMember.class);

    //~ Instance fields --------------------------------------------------------

    protected PinstripePainter stripes = new PinstripePainter();
    protected Painter unselectedBackgroundPainter = null;
    protected Painter selectedBackgroundPainter = null;
    protected CidsBean bean;
    protected boolean isSelected = false;
    protected JPopupMenu popup = new JPopupMenu();
    protected boolean newMode = false;
    protected int mouseClickedXPosition = 0;
    protected double oldStationValue;
    protected List<ElementResizedListener> elementResizeListener = new ArrayList<ElementResizedListener>();
    protected boolean dragStart = false;

    double von = 0;
    double bis = 0;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labText;
    // End of variables declaration//GEN-END:variables

    private CidsBean position;
    private int dragSide = 0;
    private JMenuItem deleteItem = new JMenuItem("löschen");
    private JMenuItem splitItem = new JMenuItem("teilen");
    private TreppenBand parent;
    private List<BandMemberListener> listenerList = new ArrayList<BandMemberListener>();
    private boolean readOnly;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeBandMember object.
     */
    public TreppeBandMember() {
        this(null);
    }

    /**
     * Creates new form MassnahmenBandMember.
     *
     * @param  parent  DOCUMENT ME!
     */
    public TreppeBandMember(final TreppenBand parent) {
        this(parent, false);
    }

    /**
     * Creates new form MassnahmenBandMember.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public TreppeBandMember(final TreppenBand parent, final boolean readOnly) {
        this.readOnly = readOnly;
        initComponents();
        stripes.setPaint(new Color(200, 200, 200, 200));
        stripes.setSpacing(5.0);
        setAlpha(0.8f);
        this.parent = parent;
        popup.addPopupMenuListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getBandMemberComponent() {
        return this;
    }

    @Override
    public double getMax() {
        return (von < bis) ? bis : von;
    }

    @Override
    public double getMin() {
        return (von < bis) ? von : bis;
    }

    @Override
    public double getFrom() {
        return von;
    }

    @Override
    public double getTo() {
        return bis;
    }

    @Override
    public CidsBean getCidsBean() {
        return bean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setText(final String text) {
        labText.setText(text);
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        removeOldListener();
        bean = cidsBean;
        popup.removeAll();
        configurePopupMenu();
        if (bean.getProperty("position") != null) {
            von = (Double)bean.getProperty("position.von");
            bis = (Double)bean.getProperty("position.bis");
        } else {
            von = 0;
            bis = 1;
        }
        bean.addPropertyChangeListener(this);
        final CidsBean linieBean = (CidsBean)bean.getProperty("position");

        if (linieBean != null) {
            linieBean.addPropertyChangeListener(this);
        }
        manageStationListener(cidsBean);
        determineBackgroundColour();
    }

    /**
     * DOCUMENT ME!
     */
    protected void setDefaultBackground() {
        unselectedBackgroundPainter = new MattePainter(new Color(229, 0, 0));
        selectedBackgroundPainter = new CompoundPainter(
                unselectedBackgroundPainter,
                new RectanglePainter(
                    3,
                    3,
                    3,
                    3,
                    3,
                    3,
                    true,
                    new Color(100, 100, 100, 100),
                    2f,
                    new Color(50, 50, 50, 100)));
        if (isSelected) {
            setBackgroundPainter(selectedBackgroundPainter);
        } else {
            setBackgroundPainter(unselectedBackgroundPainter);
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void configurePopupMenu() {
        splitItem.addActionListener(this);
        popup.add(splitItem);
        popup.addSeparator();
        deleteItem.addActionListener(this);
        popup.add(deleteItem);
    }

    /**
     * DOCUMENT ME!
     */
    private void removeOldListener() {
        if (bean != null) {
            bean.removePropertyChangeListener(this);
            final CidsBean oldPosition = (CidsBean)bean.getProperty("position");
            if (oldPosition != null) {
                oldPosition.removePropertyChangeListener(this);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected abstract void determineBackgroundColour();

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void manageStationListener(final CidsBean cidsBean) {
        position = (CidsBean)cidsBean.getProperty("position");

        if (position != null) {
            position.addPropertyChangeListener(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        labText = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.GridBagLayout());

        labText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(labText, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void mouseClicked(final MouseEvent e) {
        if ((e.getClickCount() == 2) && (bean != null)) {
            // todo: implement if required final Geometry g = (Geometry)(bean.getProperty(lineFieldName +
            // ".geom.geo_field")); final MappingComponent mc = CismapBroker.getInstance().getMappingComponent(); final
            // XBoundingBox xbb = new XBoundingBox(g);
            //
            // mc.gotoBoundingBoxWithHistory(new XBoundingBox( g.getEnvelope().buffer((xbb.getWidth() + xbb.getHeight())
            // / 2 * 0.1))); final DefaultStyledFeature dsf = new DefaultStyledFeature(); dsf.setGeometry(g);
            // dsf.setCanBeSelected(false); dsf.setLinePaint(Color.YELLOW); dsf.setLineWidth(6); final PFeature
            // highlighter = new PFeature(dsf, mc); mc.getHighlightingLayer().addChild(highlighter);
            // highlighter.animateToTransparency(0.1f, 2000); de.cismet.tools.CismetThreadPool.execute(new
            // javax.swing.SwingWorker<Void, Void>() {
            //
            // @Override protected Void doInBackground() throws Exception { Thread.currentThread().sleep(2500); return
            // null; }
            //
            // @Override protected void done() { try { mc.getHighlightingLayer().removeChild(highlighter); } catch
            // (Exception e) { } } });
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (JBandCursorManager.getInstance().isLocked()) {
            JBandCursorManager.getInstance().setCursor(this);
        }
        setAlpha(1f);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        setAlpha(0.8f);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger() && isSelected) {
            showPopupMenu(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (dragStart) {
            dragStart = false;
            final double newValue = (Double)position.getProperty("bis");

            final ElementResizedEvent event = new ElementResizedEvent(this, true, oldStationValue, newValue);
            fireElementResized(event);

            JBandCursorManager.getInstance().setLocked(false);
        }
        if (e.isPopupTrigger() && isSelected) {
            showPopupMenu(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (JBandCursorManager.getInstance().isLocked()) {
            JBandCursorManager.getInstance().setCursor(this);
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e, final double station) {
        if (JBandCursorManager.getInstance().isLocked()) {
            JBandCursorManager.getInstance().setCursor(this);
        }

        if (!dragStart) {
            if (JBandCursorManager.getInstance().getCursor().equals(
                            Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))) {
                dragSide = 1;
                dragStart = true;
                oldStationValue = (Double)position.getProperty("von");
                JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                JBandCursorManager.getInstance().setLocked(true);
                JBandCursorManager.getInstance().setCursor(this);
            } else if (JBandCursorManager.getInstance().getCursor().equals(
                            Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))) {
                dragSide = 2;
                dragStart = true;
                oldStationValue = (Double)position.getProperty("bis");
                JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                JBandCursorManager.getInstance().setLocked(true);
                JBandCursorManager.getInstance().setCursor(this);
            }
        } else {
            if (dragSide == 1) {
                // nothing to do try { final double nextPos = roundToNextValidPosition(station);
                // position.setProperty("von", nextPos); } catch (Exception ex) { LOG.error("Error while setting new
                // station value.", ex); } if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) ==
                // MouseEvent.SHIFT_DOWN_MASK) { parent.splitStation(bean, oldStationValue, true); }
            } else {
                try {
                    final double nextPos = roundToNextValidPosition(station);
                    position.setProperty("bis", nextPos);
                } catch (Exception ex) {
                    LOG.error("Error while setting new station value.", ex);
                }

                if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK) {
                    parent.splitStation(bean, oldStationValue, false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pos  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected double roundToNextValidPosition(final double pos) {
        return Math.floor(pos * 3) / 3;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (!JBandCursorManager.getInstance().isLocked()) {
            if (isSelected && !isReadOnly()) {
                if (e.getX() < 5) {
                    JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    JBandCursorManager.getInstance().setCursor(this);
                } else if (e.getX() > (getWidth() - 5)) {
                    JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    JBandCursorManager.getInstance().setCursor(this);
                } else {
                    JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    JBandCursorManager.getInstance().setCursor(this);
                }
            } else {
                JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                JBandCursorManager.getInstance().setCursor(this);
            }
        } else {
            JBandCursorManager.getInstance().setCursor(this);
        }
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(final boolean selection) {
        isSelected = selection;
        if (!isSelected) {
            setBackgroundPainter(unselectedBackgroundPainter);
        } else {
            setBackgroundPainter(selectedBackgroundPainter);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addElementResizedListener(final ElementResizedListener listener) {
        this.elementResizeListener.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeElementResizedListener(final ElementResizedListener listener) {
        this.elementResizeListener.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    protected void fireElementResized(final ElementResizedEvent e) {
        for (int i = 0; i < this.elementResizeListener.size(); ++i) {
            this.elementResizeListener.get(i).elementResized(e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setNewMode() {
        newMode = true;
        popup.show(this, (getWidth() / 2), (getHeight() / 2));
    }

    @Override
    public BandMember getBandMember() {
        return this;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("von")) {
            von = (Double)bean.getProperty("position.von");
            fireBandMemberChanged(false);
        } else if (evt.getPropertyName().equals("bis")) {
            bis = (Double)bean.getProperty("position.bis");
            fireBandMemberChanged(false);
        } else if (evt.getPropertyName().equals("position")) {
            if (position != null) {
                position.removePropertyChangeListener(this);
            }
            position = (CidsBean)evt.getNewValue();
            if (position != null) {
                position.addPropertyChangeListener(this);
            }
            if (bean.getProperty("position.von") != null) {
                von = (Double)bean.getProperty("position.von");
            }
            if (bean.getProperty("position.bis") != null) {
                bis = (Double)bean.getProperty("position.bis");
            }
            fireBandMemberChanged(false);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == deleteItem) {
            deleteMember();
        } else if (e.getSource() == splitItem) {
            splitMember();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void splitMember() {
        // todo implement if required final double widthPerPixel = (getMax() - getMin()) / getBounds().getWidth(); final
        // int pos = (int)(getMin() + (mouseClickedXPosition * widthPerPixel)); try { final CidsBean endStation =
        // (CidsBean)bean.getProperty(lineFieldName + ".bis"); final CidsBean route =
        // (CidsBean)endStation.getProperty("route"); CidsBean newStation =
        // LinearReferencingHelper.createStationBeanFromRouteBean(route, (double)pos); try { newStation =
        // newStation.persist(); } catch (Exception e) { LOG.error("Error while persist station", e); } final
        // LinearReferencedPointFeature pointFeature = LinearReferencingSingletonInstances.FEATURE_REGISTRY
        // .addStationFeature( newStation);
        //
        // bean.setProperty(lineFieldName + ".bis", newStation); newStation.setProperty("wert",
        // newStation.getProperty("wert")); parent.addMember(cloneBean(bean), newStation, endStation);
        // LinearReferencingSingletonInstances.FEATURE_REGISTRY.removeStationFeature(endStation); final
        // BandMemberEvent e = new BandMemberEvent(); e.setSelectionLost(true); fireBandMemberChanged(e); } catch
        // (Exception e) { LOG.error("Error while splitting station.", e); }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract CidsBean cloneBean(final CidsBean bean) throws Exception;

    /**
     * DOCUMENT ME!
     */
    private void deleteMember() {
        parent.deleteMember(this);
    }

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) {
        if (newMode) {
            deleteMember();
        }
    }

    @Override
    public void addBandMemberListener(final BandMemberListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void removeBandMemberListener(final BandMemberListener listener) {
        listenerList.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  modelChanged  DOCUMENT ME!
     */
    public void fireBandMemberChanged(final boolean modelChanged) {
        for (final BandMemberListener l : listenerList) {
            l.bandMemberChanged(new BandMemberEvent(modelChanged));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    public void fireBandMemberChanged(final BandMemberEvent e) {
        for (final BandMemberListener l : listenerList) {
            l.bandMemberChanged(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     */
    private void showPopupMenu(final int x, final int y) {
        mouseClickedXPosition = x;
        popup.show(this, x, y);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TreppenBand getParentBand() {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  readOnly  the readOnly to set
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
        removeOldListener();
    }
}
