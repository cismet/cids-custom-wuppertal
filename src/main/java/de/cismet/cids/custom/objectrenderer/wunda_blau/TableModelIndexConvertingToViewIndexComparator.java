/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import org.apache.log4j.Logger;

import java.util.Comparator;

import javax.swing.JTable;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class TableModelIndexConvertingToViewIndexComparator implements Comparator<Integer> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TableModelIndexConvertingToViewIndexComparator.class);

    //~ Instance fields --------------------------------------------------------

    private JTable table;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TableModelIndexConvertingToViewIndexComparator object.
     *
     * @param  table  DOCUMENT ME!
     */
    public TableModelIndexConvertingToViewIndexComparator(final JTable table) {
        this.table = table;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int compare(final Integer o1, final Integer o2) {
        int i1 = (o1 instanceof Integer) ? o1.intValue() : 0;
        int i2 = (o2 instanceof Integer) ? o2.intValue() : 0;

        try {
            i1 = table.convertRowIndexToView(i1);
            i2 = table.convertRowIndexToView(i2);
        } catch (final IndexOutOfBoundsException ex) {
            LOG.warn("Error while converting one table model index to table view index.", ex);
        }

        return Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
    }
}
