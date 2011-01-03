/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import java.text.Collator;

import java.util.*;

import javax.swing.*;

import de.cismet.tools.collections.TypeSafeCollections;

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
        model = TypeSafeCollections.newTreeSet(LIST_COMPARATOR);
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
