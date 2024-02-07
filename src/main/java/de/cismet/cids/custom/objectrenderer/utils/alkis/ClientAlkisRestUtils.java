/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 thorsten
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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;

import de.aedsicad.aaaweb.rest.model.Buchungsblatt;
import de.aedsicad.aaaweb.rest.model.Point;

import de.cismet.cids.custom.wunda_blau.search.actions.AlkisRestAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ClientAlkisRestUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClientAlkisRestUtils.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   pointCode          DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Point getPoint(final String pointCode, final ConnectionContext connectionContext) throws Exception {
        final ServerActionParameter pointCodeSAP = new ServerActionParameter<>(AlkisRestAction.RETURN_VALUE.POINT
                        .toString(),
                pointCode);
        final Object body = AlkisRestAction.RETURN_VALUE.POINT;

        final Point result = (Point)SessionManager.getProxy()
                    .executeTask(
                            AlkisRestAction.TASKNAME,
                            "WUNDA_BLAU",
                            body,
                            connectionContext,
                            pointCodeSAP);
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattCode  DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Buchungsblatt getBuchungsblatt(final String buchungsblattCode,
            final ConnectionContext connectionContext) throws Exception {
        final ServerActionParameter buchungsblattCodeSAP = new ServerActionParameter<>(
                AlkisRestAction.RETURN_VALUE.BUCHUNGSBLATT.toString(),
                buchungsblattCode);
        final Object body = AlkisRestAction.RETURN_VALUE.BUCHUNGSBLATT;

        final Buchungsblatt result = (Buchungsblatt)SessionManager.getProxy()
                    .executeTask(
                            AlkisRestAction.TASKNAME,
                            "WUNDA_BLAU",
                            body,
                            connectionContext,
                            buchungsblattCodeSAP);
        return result;
    }
}
