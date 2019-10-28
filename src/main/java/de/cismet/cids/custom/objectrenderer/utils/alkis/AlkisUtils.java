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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;

import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Point;

import de.cismet.cids.custom.wunda_blau.search.actions.ServerAlkisSoapAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlkisUtils {

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
    public Point getPointFromAlkisSOAPServerAction(final String pointCode,
            final ConnectionContext connectionContext) throws Exception {
        final ServerActionParameter pointCodeSAP = new ServerActionParameter<String>(
                ServerAlkisSoapAction.RETURN_VALUE.POINT.toString(),
                pointCode);
        final Object body = ServerAlkisSoapAction.RETURN_VALUE.POINT;

        final Point result = (Point)SessionManager.getProxy()
                    .executeTask(
                            ServerAlkisSoapAction.TASKNAME,
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
    public Buchungsblatt getBuchungsblattFromAlkisSOAPServerAction(final String buchungsblattCode,
            final ConnectionContext connectionContext) throws Exception {
        final ServerActionParameter buchungsblattCodeSAP = new ServerActionParameter<>(
                ServerAlkisSoapAction.RETURN_VALUE.BUCHUNGSBLATT.toString(),
                buchungsblattCode);
        final Object body = ServerAlkisSoapAction.RETURN_VALUE.BUCHUNGSBLATT;

        final Buchungsblatt result = (Buchungsblatt)SessionManager.getProxy()
                    .executeTask(
                            ServerAlkisSoapAction.TASKNAME,
                            "WUNDA_BLAU",
                            body,
                            connectionContext,
                            buchungsblattCodeSAP);
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AlkisUtils getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final AlkisUtils INSTANCE = new AlkisUtils();
    }
}
