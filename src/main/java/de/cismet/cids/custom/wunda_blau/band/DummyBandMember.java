/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.band;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.JBandCursorManager;
import de.cismet.tools.gui.jbands.interfaces.BandMember;
import de.cismet.tools.gui.jbands.interfaces.BandMemberMouseListeningComponent;
import de.cismet.tools.gui.jbands.interfaces.BandMemberSelectable;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DummyBandMember extends AbschnittsinfoMember implements BandMemberSelectable,
    BandMemberMouseListeningComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(DummyBandMember.class);

    //~ Instance fields --------------------------------------------------------

    private boolean selected;
    private Painter unselectedBackgroundPainter;
    private Painter selectedBackgroundPainter;
    private String colorProperty;
    private String lineFieldName = "linie";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MassnahmenBandMember.
     */
    public DummyBandMember() {
        setMinimumSize(new Dimension(1, 7));
        setPreferredSize(getMinimumSize());
        determineBackgroundColour();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void determineBackgroundColour() {
        final Color secondColor = new Color(200, 200, 200);
        setBackgroundPainter(new CompoundPainter(
                new MattePainter(secondColor),
                new PinstripePainter(new Color(255, 255, 255), 45, 2, 5)));
        unselectedBackgroundPainter = getBackgroundPainter();
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

        setSelected(isSelected());
    }
    /**
     * DOCUMENT ME!
     */
    private void setDefaultBackground() {
        setBackgroundPainter(new MattePainter(new Color(229, 0, 0)));
        unselectedBackgroundPainter = getBackgroundPainter();
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
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(final boolean selection) {
        this.selected = selection;

        if (selected) {
            setBackgroundPainter(selectedBackgroundPainter);
        } else {
            setBackgroundPainter(unselectedBackgroundPainter);
        }
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public BandMember getBandMember() {
        return this;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
//        if ((e.getClickCount() == 2) && (cidsBean != null)) {
//            final Geometry g = (Geometry)(cidsBean.getProperty(lineFieldName + ".geom.geo_field"));
//            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
//            final XBoundingBox xbb = new XBoundingBox(g);
//
//            mc.gotoBoundingBoxWithHistory(new XBoundingBox(
//                    g.getEnvelope().buffer((xbb.getWidth() + xbb.getHeight()) / 2 * 0.1)));
//            final DefaultStyledFeature dsf = new DefaultStyledFeature();
//            dsf.setGeometry(g);
//            dsf.setCanBeSelected(false);
//            dsf.setLinePaint(Color.YELLOW);
//            dsf.setLineWidth(6);
//            final PFeature highlighter = new PFeature(dsf, mc);
//            mc.getHighlightingLayer().addChild(highlighter);
//            highlighter.animateToTransparency(0.1f, 2000);
//            de.cismet.tools.CismetThreadPool.execute(new javax.swing.SwingWorker<Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground() throws Exception {
//                        Thread.currentThread().sleep(2500);
//                        return null;
//                    }
//
//                    @Override
//                    protected void done() {
//                        try {
//                            mc.getHighlightingLayer().removeChild(highlighter);
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
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
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (!JBandCursorManager.getInstance().isLocked()) {
            JBandCursorManager.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JBandCursorManager.getInstance().setCursor(this);
        } else {
            JBandCursorManager.getInstance().setCursor(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the colorProperty
     */
    public String getColorProperty() {
        return colorProperty;
    }
}
