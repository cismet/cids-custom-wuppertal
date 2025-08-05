/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.alkisfortfuehrung;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.table.AbstractTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FortfuehrungenTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FortfuehrungenTableModel.class);

    private static final String[] COLUMN_NAMES = {
            "Datum",
            "FFN",
            "Art",
            ""
        };

    private static final Class[] COLUMN_CLASSES = {
            Date.class,
            String.class,
            String.class,
            String.class
        };

    //~ Instance fields --------------------------------------------------------

    private FortfuehrungItem[] items = new FortfuehrungItem[0];

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FortfuehrungenTableModel object.
     */
    public FortfuehrungenTableModel() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  items  DOCUMENT ME!
     */
    public void setItems(final FortfuehrungItem[] items) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = new FortfuehrungItem[0];
        }
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FortfuehrungItem getItem(final int rowIndex) {
        return items[rowIndex];
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final FortfuehrungItem item = getItem(rowIndex);

        if (item == null) {
            return null;
        }

        switch (columnIndex) {
            case 0: {
                try {
                    final Date date = item.getBeginn();
                    if (date != null) {
//                        return new SimpleDateFormat("dd.MM.yyyy").format(date);
                        return date;
                    } else {
                        return "";
                    }
                } catch (Exception e) {
                    LOG.warn("exception in tablemodel", e);
                    return "";
                }
            }
            case 1: {
                try {
                    return item.getFfn();
                } catch (Exception e) {
                    LOG.warn("exception in tablemodel", e);
                    return "";
                }
            }
            case 2: {
                try {
                    return item.getAnlass();
                } catch (Exception e) {
                    LOG.warn("exception in tablemodel", e);
                    return "";
                }
            }
            case 3: {
                try {
                    final String flurstueck_alt = item.getFlurstueckAlt();
                    final String flurstueck_neu = item.getFlurstueckNeu();
                    if (flurstueck_alt.equals(flurstueck_neu)) {
                        return flurstueck_alt;
                    } else {
                        return flurstueck_alt + " => " + flurstueck_neu;
                    }
                } catch (Exception e) {
                    LOG.warn("exception in tablemodel", e);
                    return "";
                }
            }
            default: {
            }
            break;
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public int getRowCount() {
        return items.length;
    }
}
