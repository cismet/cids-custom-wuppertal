/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils;

import java.text.Collator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SortedListModel extends AbstractListModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Comparator LIST_COMPARATOR = new Comparator() {

            @Override
            public int compare(final Object o1, final Object o2) {
                final String str1 = String.valueOf(o1);
                final String str2 = String.valueOf(o2);
                return Collator.getInstance().compare(str1, str2);
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final TreeSet model;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SortedListModel object.
     */
    public SortedListModel() {
        model = new TreeSet<Object>(LIST_COMPARATOR);
    }

    //~ Methods ----------------------------------------------------------------

    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        return model.size();
    }

    @Override
    public Object getElementAt(final int index) {
        // Return the appropriate element
        return model.toArray()[index];
    }
    /**
     * Other methods.
     *
     * @param  element  DOCUMENT ME!
     */
    public void addElement(final Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  elements  DOCUMENT ME!
     */
    public void addAll(final Object[] elements) {
        final Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   element  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean contains(final Object element) {
        return model.contains(element);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object firstElement() {
        // Return the appropriate element
        return model.first();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Iterator iterator() {
        return model.iterator();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object lastElement() {
        // Return the appropriate element
        return model.last();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   element  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeElement(final Object element) {
        final boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}
