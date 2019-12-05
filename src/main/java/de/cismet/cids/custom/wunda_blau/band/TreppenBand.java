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

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanCollectionStore;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.jbands.BandEvent;
import de.cismet.tools.gui.jbands.BandMemberEvent;
import de.cismet.tools.gui.jbands.DefaultBand;
import de.cismet.tools.gui.jbands.JBand;
import de.cismet.tools.gui.jbands.interfaces.BandListener;
import de.cismet.tools.gui.jbands.interfaces.BandMember;
import de.cismet.tools.gui.jbands.interfaces.BandMemberListener;
import de.cismet.tools.gui.jbands.interfaces.BandModificationProvider;
import de.cismet.tools.gui.jbands.interfaces.DisposableBand;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class TreppenBand extends DefaultBand implements CidsBeanCollectionStore,
    BandModificationProvider,
    BandMemberListener,
    EditorSaveListener,
    DisposableBand,
    ConnectionContextStore,
    ElementResizedListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppenBand.class);

    //~ Instance fields --------------------------------------------------------

    protected Side side;

    protected Collection<CidsBean> objectBeans = new ArrayList<CidsBean>();
    protected String objectTableName = null;
    protected String positionField = "position";
    protected boolean readOnly = false;
    protected Double fixMin = null;
    protected Double fixMax = null;
    protected final List<ElementResizedListener> elementResizeListener = new ArrayList<ElementResizedListener>();
    protected final JBand parent;

    private ConnectionContext connectionContext;
    private final List<BandListener> listenerList = new ArrayList<BandListener>();

    private final List<CidsBean> beansToDelete = new ArrayList<CidsBean>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppenBand object.
     *
     * @param  side    DOCUMENT ME!
     * @param  title   DOCUMENT ME!
     * @param  parent  DOCUMENT ME!
     */
    public TreppenBand(final Side side, final String title, final JBand parent) {
        super(title);
        this.side = side;
        this.parent = parent;
        this.objectBeans = new FilteredCollection(side);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext cc) {
        this.connectionContext = cc;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  side  DOCUMENT ME!
     */
    public void setSide(final Side side) {
        this.side = side;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Side getSide() {
        return this.side;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JBand getParent() {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  readOnly  DOCUMENT ME!
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return objectBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   member  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected double getNextGreaterElementStart(final TreppeBandMember member) {
        double next = Double.MAX_VALUE;

        for (int i = 0; i < members.size(); ++i) {
            final BandMember m = members.get(i);

            if ((m != member) && !(m instanceof DummyBandMember)) {
                if ((m.getMin() >= member.getMin()) && (m.getMin() < next)) {
                    next = m.getMin();
                }
            }
        }

        return next;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   member  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected double getNextLessElementEnd(final TreppeBandMember member) {
        double next = Double.MIN_VALUE;

        for (int i = 0; i < members.size(); ++i) {
            final BandMember m = members.get(i);

            if ((m != member) && !(m instanceof DummyBandMember)) {
                if ((m.getMax() <= member.getMin()) && (m.getMax() > next)) {
                    next = m.getMax();
                }
            }
        }

        return next;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   member  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BandMember getNextLessElement(final TreppeBandMember member) {
        BandMember nextMember = null;

        for (int i = 0; i < members.size(); ++i) {
            final BandMember m = members.get(i);

            if ((m != member) && !(m instanceof DummyBandMember)) {
                if ((m.getMax() == member.getMin())) {
                    nextMember = m;
                }
            }
        }

        return nextMember;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   member  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BandMember getNextGreaterElement(final TreppeBandMember member) {
        BandMember nextMember = null;

        for (int i = 0; i < members.size(); ++i) {
            final BandMember m = members.get(i);

            if ((m != member) && !(m instanceof DummyBandMember)) {
                if ((m.getMin() == member.getMax())) {
                    nextMember = m;
                }
            }
        }

        return nextMember;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        disposeAllMember();
        objectBeans = new FilteredCollection(((beans != null) ? beans : new ArrayList<CidsBean>()), side);
        super.removeAllMember();

        for (final CidsBean massnahme : objectBeans) {
            final TreppeBandMember m = createBandMemberFromBean(massnahme);
            m.setReadOnly(readOnly);
            m.addBandMemberListener(this);
            m.setCidsBean(massnahme);
            addMember(m);
        }

        addDummies();
        fireBandChanged(new BandEvent());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  start   DOCUMENT ME!
     * @param  amount  DOCUMENT ME!
     */
    public void moveAllMember(final double start, final int amount) {
        for (final CidsBean bean : objectBeans) {
            if ((Double)bean.getProperty("position.von") >= start) {
                try {
                    bean.setProperty("position.von", (Double)bean.getProperty("position.von") + amount);
                    bean.setProperty("position.bis", (Double)bean.getProperty("position.bis") + amount);
                } catch (Exception ex) {
                    LOG.error("Cannot move bean", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BandMember getMemberByBean(final CidsBean bean) {
        for (int i = 0; i < members.size(); ++i) {
            final BandMember member = members.get(i);

            if (member instanceof TreppeBandMember) {
                final CidsBean memberBean = ((TreppeBandMember)member).getCidsBean();

                if (memberBean.equals(bean)) {
                    return member;
                }
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    protected void addDummies() {
        final List<BandMember> orderedMembers = new ArrayList<BandMember>(members);
        Collections.sort(orderedMembers, new Comparator<BandMember>() {

                @Override
                public int compare(final BandMember o1, final BandMember o2) {
                    return (int)Math.signum(o1.getMin() - o2.getMin());
                }
            });

        double last = (hasDummyAfterEnd() ? -1 : 0);

        for (int i = 0; i < orderedMembers.size(); ++i) {
            if (orderedMembers.get(i).getMin() > last) {
                final DummyBandMember dummy = new DummyBandMember(this);
                dummy.setFrom(last);
                dummy.setTo(orderedMembers.get(i).getMin());
                addMember(dummy);
            }

            if (last < orderedMembers.get(i).getMax()) {
                last = orderedMembers.get(i).getMax();
            }
        }

        final double maxValue = (hasDummyAfterEnd() ? (parent.getMaxValue()) : (parent.getMaxValue() - 1));

        if (last < maxValue) {
            final DummyBandMember dummy = new DummyBandMember(this);
            dummy.setFrom(last);
            dummy.setTo(maxValue);
            addMember(dummy);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean hasDummyAfterEnd() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectBean  DOCUMENT ME!
     * @param   start       startStation DOCUMENT ME!
     * @param   end         endStation endValue DOCUMENT ME!
     * @param   side        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TreppeBandMember addMember(final CidsBean objectBean,
            final double start,
            final double end,
            final Side side) {
        try {
            final CidsBean position = createNewCidsBeanFromTableName("treppe_position");
            position.setProperty("von", start);
            position.setProperty("bis", end);
            position.setProperty("wo", side.ordinal());
            objectBean.setProperty(positionField, position);

            final TreppeBandMember m = refresh(objectBean, true);
            // set the von and bis values again. This is required, when any object property dependens on the object
            // length
            position.setProperty("von", start);
            position.setProperty("bis", end);
            objectBeans.add(objectBean);
            fireBandChanged(new BandEvent());

            return m;
        } catch (Exception e) {
            LOG.error("error while creating new station.", e);
            return null;
        }
    }

    @Override
    public void addMember(final Double startStation,
            final Double endStation,
            final Double minStart,
            final Double maxEnd,
            final List<BandMember> members) {
        // nothing to do. The default method to add a new object is not used
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bean    DOCUMENT ME!
     * @param  value   DOCUMENT ME!
     * @param  isFrom  DOCUMENT ME!
     */
    public void splitStation(final CidsBean bean, final double value, final boolean isFrom) {
        // todo implemented, if required
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Double getFixObjectLength();

    @Override
    public void addMember(final BandMember m) {
        super.addMember(m);

        if (m instanceof TreppeBandMember) {
            ((TreppeBandMember)m).addElementResizedListener(this);
        }
    }

    @Override
    public void elementResized(final ElementResizedEvent e) {
        fireElementResized(e);
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
    private void fireElementResized(final ElementResizedEvent e) {
        for (int i = 0; i < this.elementResizeListener.size(); ++i) {
            this.elementResizeListener.get(i).elementResized(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tableName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewCidsBeanFromTableName(final String tableName) throws Exception {
        if (tableName != null) {
            final MetaClass metaClass = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", tableName);
            if (metaClass != null) {
                return metaClass.getEmptyInstance().getBean();
            }
        }
        throw new Exception("Could not find MetaClass for table " + tableName);
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        refresh(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  withDummies  DOCUMENT ME!
     */
    public void refresh(final boolean withDummies) {
        refresh(null, false, withDummies);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   special  DOCUMENT ME!
     * @param   add      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TreppeBandMember refresh(final CidsBean special, final boolean add) {
        return refresh(special, add, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   special      DOCUMENT ME!
     * @param   add          DOCUMENT ME!
     * @param   withDummies  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TreppeBandMember refresh(final CidsBean special, final boolean add, final boolean withDummies) {
        disposeAllMember();
        super.removeAllMember();

        for (final CidsBean objectBean : objectBeans) {
            if (add || (objectBean != special)) {
                final TreppeBandMember m = createBandMemberFromBean(objectBean);
                m.setReadOnly(readOnly);
                m.setCidsBean(objectBean);
                m.addBandMemberListener(this);
                addMember(m);
            }
        }

        if (add) {
            final TreppeBandMember m = createBandMemberFromBean(special);
            m.setReadOnly(readOnly);
            m.setCidsBean(special);
            m.addBandMemberListener(this);
            addMember(m);
            if (withDummies) {
                addDummies();
            }
            return m;
        }

        if (withDummies) {
            addDummies();
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract TreppeBandMember createBandMemberFromBean(CidsBean bean);

    @Override
    public void addBandListener(final BandListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void removeBandListener(final BandListener listener) {
        listenerList.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    public void fireBandChanged(final BandEvent e) {
        for (final BandListener l : listenerList) {
            l.bandChanged(e);
        }
    }

    @Override
    public void bandMemberChanged(final BandMemberEvent e) {
        final BandEvent ev = new BandEvent();
        if ((e != null)) {
            if (e.isSelectionLost()) {
                ev.setSelectionLost(true);
            }
            ev.setModelChanged(e.isModelChanged());
        }
        fireBandChanged(ev);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  member  DOCUMENT ME!
     */
    public void deleteMember(final TreppeBandMember member) {
        member.dispose();
        final CidsBean memberBean = member.getCidsBean();
        refresh(memberBean, false);
        objectBeans.remove(memberBean);
        beansToDelete.add(memberBean);

        final BandEvent e = new BandEvent();
        e.setSelectionLost(true);
        fireBandChanged(e);

        if ((member instanceof LaufBandMember) || (member instanceof PodestBandMember)) {
            final ElementResizedEvent event = new ElementResizedEvent(member, true, member.getMax(), member.getMin());
            fireElementResized(event);
        } else {
            final ElementResizedEvent event = new ElementResizedEvent(
                    member,
                    true,
                    0,
                    0);
            event.setRefreshDummiesOnly(true);
            fireElementResized(event);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  status DOCUMENT ME!
     */
    @Override
    public void editorClosed(final EditorClosedEvent event) {
        if (event.getStatus() == EditorSaveStatus.SAVE_SUCCESS) {
            // all as delete marked band member will be deleted in the database
            CismetThreadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        for (final CidsBean tmp : beansToDelete) {
                            try {
                                tmp.delete();
                                tmp.persist();
                            } catch (Exception e) {
                                LOG.error("Cannot delete bean.", e);
                            }
                        }
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public void removeAllMember() {
//        disposeAllMember();
//        objectBeans.clear();
//        super.removeAllMember();
//        refresh(null, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  min  DOCUMENT ME!
     */
    @Override
    public void setMin(final Double min) {
        this.fixMin = min;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    @Override
    public void setMax(final Double max) {
        this.fixMax = max;
    }

    @Override
    public double getMin() {
        if (fixMin != null) {
            return fixMin;
        } else {
            return super.getMin();
        }
    }

    @Override
    public double getMax() {
        if (fixMax != null) {
            return fixMax;
        } else {
            return super.getMax();
        }
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        disposeAllMember();
    }

    /**
     * DOCUMENT ME!
     */
    private void disposeAllMember() {
        for (int i = 0; i < getNumberOfMembers(); ++i) {
            final BandMember member = getMember(i);

            if (member instanceof TreppeBandMember) {
                ((TreppeBandMember)member).dispose();
                ((TreppeBandMember)member).removeElementResizedListener(this);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract String[] getAllowedObjectNames();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract String[] getAllowedObjectTableNames();
}
