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

import java.util.ArrayList;

import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

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
    BandMemberMouseListeningComponent,
    CidsBeanDropListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(DummyBandMember.class);

    //~ Instance fields --------------------------------------------------------

    private boolean selected;
    private Painter unselectedBackgroundPainter;
    private Painter selectedBackgroundPainter;
    private String colorProperty;
    private String lineFieldName = "linie";
    private TreppenBand parent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MassnahmenBandMember.
     *
     * @param  parent  DOCUMENT ME!
     */
    public DummyBandMember(final TreppenBand parent) {
        setMinimumSize(new Dimension(1, 7));
        setPreferredSize(getMinimumSize());
        determineBackgroundColour();
        this.parent = parent;

        if (parent instanceof StuetzmauerBand) {
            setToolTipText("Mauer auf dieses Element ziehen, um eine neue Stützmauer zu erzeugen.");
            try {
                new CidsBeanDropTarget(this);
            } catch (final Exception ex) {
                LOG.warn("error while init CidsBeanDropTarget", ex);
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void determineBackgroundColour() {
        unselectedBackgroundPainter = new CompoundPainter(new MattePainter(new Color(215, 215, 215)));
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
        setBackgroundPainter(unselectedBackgroundPainter);

        setSelected(isSelected());
//        final Color secondColor = new Color(200, 200, 200);
//        setBackgroundPainter(new CompoundPainter(
//                new MattePainter(secondColor),
//                new PinstripePainter(new Color(255, 255, 255), 45, 2, 5)));
//        unselectedBackgroundPainter = getBackgroundPainter();
//        selectedBackgroundPainter = new CompoundPainter(
//                unselectedBackgroundPainter,
//                new RectanglePainter(
//                    3,
//                    3,
//                    3,
//                    3,
//                    3,
//                    3,
//                    true,
//                    new Color(100, 100, 100, 100),
//                    2f,
//                    new Color(50, 50, 50, 100)));
//
//        setSelected(isSelected());
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JPanel getObjectChooser() {
        return new ObjectChooserPanel(parent.getAllowedObjectNames(),
                parent.getAllowedObjectTableNames(),
                parent,
                from,
                to);
    }

    @Override
    public void beansDropped(final ArrayList<CidsBean> droppedBeans) {
        try {
            if (droppedBeans.size() > 1) {
            }
            final CidsBean droppedBean = droppedBeans.get(0);
            if (droppedBean != null) {
                if (droppedBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("mauer")) {
                    try {
                        final CidsBean objectBean = TreppenBand.createNewCidsBeanFromTableName("TREPPE_STUETZMAUER");
                        objectBean.setProperty("mauer", droppedBean.getProperty("id"));
                        final TreppeBandMember bandMember;

                        bandMember = parent.addMember(objectBean, from, to, parent.getSide());
                        final ElementResizedEvent event = new ElementResizedEvent(bandMember, false, 100000, 100000);
                        bandMember.fireElementResized(event);

                        parent.getParent().setSelectedMember(bandMember);
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (final Exception ex) {
            final String message = "Fehler beim Erzeugen der Stützmauer.";
            LOG.error(message, ex);
            ObjectRendererUtils.showExceptionWindowToUser(message, ex, this);
        }
    }
}
