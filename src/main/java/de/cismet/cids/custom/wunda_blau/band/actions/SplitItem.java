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
package de.cismet.cids.custom.wunda_blau.band.actions;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;

import de.cismet.cids.custom.wunda_blau.band.Side;
import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppenBand;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.jbands.BandMemberEvent;

import static javax.swing.Action.SHORT_DESCRIPTION;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SplitItem extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SplitItem.class);

    //~ Instance fields --------------------------------------------------------

    private TreppeBandMember member;
    private boolean initialised = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SplitItem object.
     */
    public SplitItem() {
        setEnabled(false);
    }

    /**
     * Creates a new SplitItem object.
     *
     * @param  member  DOCUMENT ME!
     */
    public SplitItem(final TreppeBandMember member) {
        init(member);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  member  DOCUMENT ME!
     */
    public void init(final TreppeBandMember member) {
        this.member = member;
        putValue(SHORT_DESCRIPTION, "teilen");
        putValue(NAME, "teilen");
        setEnabled(true);
        this.initialised = true;
    }

    /**
     * DOCUMENT ME!
     */
    public void deactivate() {
        setEnabled(false);
        initialised = false;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (!initialised) {
            return;
        }
        final double widthPerPixel = (member.getMax() - member.getMin()) / member.getBounds().getWidth();
        final int pos = (int)(member.getMin() + (member.getMouseClickedXPosition() * widthPerPixel));
        try {
            final double endStation = (Double)member.getCidsBean().getProperty("position.bis");
            Integer sideInt = (Integer)member.getCidsBean().getProperty("position.wo");

            if (sideInt == null) {
                sideInt = Side.BOTH.ordinal();
            }

            final Side side = Side.values()[sideInt];

            member.getCidsBean().setProperty("position.bis", (double)pos);
            member.getParentBand().addMember(cloneBean(member.getCidsBean()), pos, endStation, side);
            member.getParentBand().refresh();
            final BandMemberEvent e = new BandMemberEvent();
            e.setSelectionLost(true);
            member.fireBandMemberChanged(e);
        } catch (Exception e) {
            LOG.error("Error while splitting station.", e);
        }
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
    private CidsBean cloneBean(final CidsBean bean) throws Exception {
        return cloneCidsBean(bean, false);
    }

    /**
     * Creates a clone of the given bean.
     *
     * @param   bean        the bean to clone
     * @param   cloneBeans  true, iff a deep copy of the sub beans should be created
     *
     * @return  a clone of the given bean
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean cloneCidsBean(final CidsBean bean, final boolean cloneBeans) throws Exception {
        if (bean == null) {
            return null;
        }
        final CidsBean clone = bean.getMetaObject().getMetaClass().getEmptyInstance().getBean();

        for (final String propName : bean.getPropertyNames()) {
            if (!propName.toLowerCase().equals("id")) {
                final Object o = bean.getProperty(propName);

                if (o instanceof CidsBean) {
                    if (cloneBeans) {
                        clone.setProperty(propName, cloneCidsBean((CidsBean)o));
                    } else {
                        clone.setProperty(propName, (CidsBean)o);
                    }
                } else if (o instanceof Collection) {
                    final List<CidsBean> list = (List<CidsBean>)o;
                    final List<CidsBean> newList = new ArrayList<CidsBean>();

                    for (final CidsBean tmpBean : list) {
                        if (cloneBeans) {
                            newList.add(cloneCidsBean(tmpBean));
                        } else {
                            newList.add(tmpBean);
                        }
                    }
                    clone.setProperty(propName, newList);
                } else if (o instanceof Geometry) {
                    clone.setProperty(propName, ((Geometry)o).clone());
                } else if (o instanceof Long) {
                    clone.setProperty(propName, new Long(o.toString()));
                } else if (o instanceof Double) {
                    clone.setProperty(propName, new Double(o.toString()));
                } else if (o instanceof Integer) {
                    clone.setProperty(propName, new Integer(o.toString()));
                } else if (o instanceof Boolean) {
                    clone.setProperty(propName, new Boolean(o.toString()));
                } else if (o instanceof String) {
                    clone.setProperty(propName, o);
                } else {
                    if (o != null) {
                        LOG.error("unknown property type: " + o.getClass().getName());
                    }
                    clone.setProperty(propName, o);
                }
            }
        }

        return clone;
    }

    /**
     * cloneCidsBean(CidsBean bean) was tested and works with the type geom. Objects which have properties of a type
     * that is not considered by the method, will not be returned as deep copy. The results of this method can be used
     * as a deep copy, if we assume, that the properties, which are not of the type CidsBean, will not be changed in the
     * future, but only replaced by other objects.
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  a deep copy of the given object
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean cloneCidsBean(final CidsBean bean) throws Exception {
        return cloneCidsBean(bean, true);
    }
}
