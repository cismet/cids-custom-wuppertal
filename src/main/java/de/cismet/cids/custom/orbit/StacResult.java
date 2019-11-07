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
package de.cismet.cids.custom.orbit;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */

public class StacResult {

    //~ Instance fields --------------------------------------------------------

    private String stac;
    private String socketChannelId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StacResult object.
     */
    public StacResult() {
    }

    /**
     * Creates a new StacResult object.
     *
     * @param  stac             DOCUMENT ME!
     * @param  socketChannelId  DOCUMENT ME!
     */
    public StacResult(final String stac, final String socketChannelId) {
        this.stac = stac;
        this.socketChannelId = socketChannelId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStac() {
        return stac;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stac  DOCUMENT ME!
     */
    public void setStac(final String stac) {
        this.stac = stac;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSocketChannelId() {
        return socketChannelId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  socketChannelId  DOCUMENT ME!
     */
    public void setSocketChannelId(final String socketChannelId) {
        this.socketChannelId = socketChannelId;
    }
}
