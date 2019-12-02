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
package de.cismet.cids.custom.wunda_blau.band;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;

import org.openide.util.Exceptions;

import java.awt.Color;

import java.beans.PropertyChangeEvent;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LaufBandMember extends TreppeBandMember {

    //~ Static fields/initializers ---------------------------------------------

    private static Logger LOG = Logger.getLogger(LaufBandMember.class);

    //~ Instance fields --------------------------------------------------------

    private boolean stufenChangedFromBand = false;
    private boolean stufenChangedFromPanel = false;
    private int stufenCount;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LaufBandMember object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public LaufBandMember(final TreppenBand parent) {
        super(parent);
    }

    /**
     * Creates a new LaufBandMember object.
     *
     * @param  parent    DOCUMENT ME!
     * @param  readOnly  DOCUMENT ME!
     */
    public LaufBandMember(final TreppenBand parent, final boolean readOnly) {
        super(parent, readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if (!(this instanceof PodestBandMember)
                    && (evt.getPropertyName().equals("von") || evt.getPropertyName().equals("bis"))) {
            von = (Double)bean.getProperty("position.von");
            bis = (Double)bean.getProperty("position.bis");
            final Integer stufen = (Integer)bean.getProperty("stufen");
            final int stufenNewValue = Math.round((float)(Math.abs(von - bis)));

            if ((stufen == null) || (stufen != stufenNewValue)) {
                try {
                    stufenChangedFromBand = true;
                    bean.setProperty("stufen", stufenNewValue);
                    stufenCount = stufen;
                    stufenChangedFromBand = false;
                } catch (Exception ex) {
                    LOG.error("Cannot set stufen", ex);
                }
            }
        } else if (!(this instanceof PodestBandMember) && evt.getPropertyName().equals("stufen")) {
            if (!stufenChangedFromBand && !dragStart && !stufenChangedFromPanel) {
                final double von = (Double)bean.getProperty("position.von");
                final double bis = (Double)bean.getProperty("position.bis");
                final Integer stufen = (Integer)bean.getProperty("stufen");
                final double bisNew = von + stufen;
                final int stufenold = stufenCount;
                final double oldValue = von + stufenold;

                if ((stufen != null) && (bis != bisNew)) {
                    try {
                        bean.setProperty("position.bis", bisNew);
                    } catch (Exception ex) {
                        LOG.error("Cannot set position bis", ex);
                    }
                    stufenChangedFromPanel = true;
                    final ElementResizedEvent event = new ElementResizedEvent(this, true, oldValue, bisNew);
                    fireElementResized(event);
                    stufenChangedFromPanel = false;
                }
            }
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        super.setCidsBean(cidsBean);
        if (bean.getProperty("stufen") != null) {
            stufenCount = (Integer)bean.getProperty("stufen");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pos  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    protected double roundToNextValidPosition(final double pos) {
        return Math.floor(pos);
    }

    @Override
    protected void determineBackgroundColour() {
//        setDefaultBackground();
//        unselectedBackgroundPainter = new MattePainter(new Color(160, 60, 98));
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
//        setBackgroundPainter(unselectedBackgroundPainter);
        final Color secondColor = new Color(255, 211, 155);
        setBackgroundPainter(new CompoundPainter(
                new MattePainter(secondColor),
                new PinstripePainter(new Color(255, 255, 255), 0, 3, 3)));
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

        setSelected(isSelected);
    }
}
