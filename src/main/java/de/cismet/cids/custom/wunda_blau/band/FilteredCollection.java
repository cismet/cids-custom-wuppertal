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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FilteredCollection implements Collection<CidsBean> {

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> internalCollection;
    private Side side;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FilteredCollection object.
     *
     * @param  side  DOCUMENT ME!
     */
    public FilteredCollection(final Side side) {
        this.side = side;
    }

    /**
     * Creates a new FilteredCollection object.
     *
     * @param  internCollection  DOCUMENT ME!
     * @param  side              DOCUMENT ME!
     */
    public FilteredCollection(final Collection<CidsBean> internCollection, final Side side) {
        this.internalCollection = internCollection;
        this.side = side;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  col  DOCUMENT ME!
     */
    public void setCollection(final Collection<CidsBean> col) {
        internalCollection = col;
    }

    @Override
    public int size() {
        int count = 0;

        for (final CidsBean b : internalCollection) {
            if (isValidCidsBean(b)) {
                ++count;
            }
        }

        return count;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidCidsBean(final CidsBean bean) {
        final Integer sideInt = (Integer)bean.getProperty("position.wo");

        return (sideInt == null) || (side == Side.BOTH)
                    || ((side.ordinal() == sideInt) || (sideInt == Side.BOTH.ordinal()));
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(final Object o) {
        for (final CidsBean b : internalCollection) {
            if (isValidCidsBean(b) && b.equals(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<CidsBean> iterator() {
        return Arrays.asList(toArray(new CidsBean[size()])).iterator();
    }

    @Override
    public Object[] toArray() {
        final CidsBean[] array = new CidsBean[size()];
        int index = 0;

        for (final CidsBean b : internalCollection) {
            if (isValidCidsBean(b)) {
                array[index++] = b;
            }
        }

        return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final int size = size();
        int index = 0;
        T[] array;

        if (size >= a.length) {
            array = a;
        } else {
            array = (T[])new Object[size];
        }

        for (final CidsBean b : internalCollection) {
            if (isValidCidsBean(b)) {
                array[index++] = (T)b;
            }
        }

        if (array.length > index) {
            array[index] = null;
        }

        return array;
    }

    @Override
    public boolean add(final CidsBean e) {
        return internalCollection.add(e);
    }

    @Override
    public boolean remove(final Object o) {
        return internalCollection.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends CidsBean> c) {
        return internalCollection.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return internalCollection.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        boolean changed = false;

        for (final CidsBean e : new ArrayList<CidsBean>(internalCollection)) {
            if (!c.contains(e)) {
                internalCollection.remove(e);
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        internalCollection.clear();
    }
}
