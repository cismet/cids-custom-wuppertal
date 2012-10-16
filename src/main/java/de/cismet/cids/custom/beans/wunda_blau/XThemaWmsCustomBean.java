/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class XThemaWmsCustomBean extends CidsBean {

    //~ Instance fields --------------------------------------------------------

    int id;
    String wms_name;
    String sorter;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSorter() {
        return sorter + "JAJAJA";
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sorter  DOCUMENT ME!
     */
    public void setSorter(final String sorter) {
        this.sorter = sorter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getWms_name() {
        return wms_name + " JAJAJAJa";
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wms_name  DOCUMENT ME!
     */
    public void setWms_name(final String wms_name) {
        this.wms_name = wms_name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("started");
        final CidsBean cb = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "leo",
                "GEOM",
                454169);

        System.out.println(cb.getProperty("geo_field"));
        System.out.println("fertich");
        System.exit(0);
    }

    @Override
    public String toString() {
        return "lala";
    }
}
