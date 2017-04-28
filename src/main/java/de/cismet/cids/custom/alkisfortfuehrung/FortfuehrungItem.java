/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.alkisfortfuehrung;

import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@Getter
@AllArgsConstructor
public class FortfuehrungItem implements Comparable<FortfuehrungItem> {

    //~ Instance fields --------------------------------------------------------

    private final Integer anlassId;
    private final String ffn;
    private final String anlass;
    private final Date beginn;
    private final Integer flurstueck_id;
    private final String flurstueck_alt;
    private final String flurstueck_neu;
    @Setter private Integer fortfuehrung_id;
    private final Collection<Geometry> geoms = new ArrayList<>();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIst_abgearbeitet() {
        return fortfuehrung_id != null;
    }

    @Override
    public int compareTo(final FortfuehrungItem o) {
        if (o == null) {
            return 1;
        } else if (getBeginn() != null) {
            return getBeginn().compareTo(o.getBeginn());
        } else if (o.getBeginn() == null) {
            return 0;
        } else {
            return -1;
        }
    }
}
