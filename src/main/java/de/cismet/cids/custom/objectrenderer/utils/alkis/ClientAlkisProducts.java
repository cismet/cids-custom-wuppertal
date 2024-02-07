/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.utils.alkis.AlkisConf;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class ClientAlkisProducts extends AlkisProducts {

    //~ Static fields/initializers ---------------------------------------------

    private static ClientAlkisProducts INSTANCE;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ClientAlkisProducts object.
     *
     * @param   alkisConf               DOCUMENT ME!
     * @param   productProperties       DOCUMENT ME!
     * @param   formats                 DOCUMENT ME!
     * @param   produktbeschreibungXml  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public ClientAlkisProducts(final AlkisConf alkisConf,
            final Properties productProperties,
            final Properties formats,
            final String produktbeschreibungXml) throws Exception {
        super(alkisConf, productProperties, formats, produktbeschreibungXml);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public static ClientAlkisProducts getInstance() {
        if (INSTANCE == null) {
            try {
                final ConnectionContext connectionContext = ConnectionContext.create(
                        AbstractConnectionContext.Category.STATIC,
                        ClientAlkisProducts.class.getSimpleName());
                final Properties productsProperties = new Properties();
                final Object productsRet = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.ALKIS_PRODUCTS_PROPERTIES.getValue(),
                                connectionContext);
                if (productsRet instanceof Exception) {
                    throw new Exception("error while loading server resource "
                                + WundaBlauServerResources.ALKIS_PRODUCTS_PROPERTIES,
                        (Exception)productsRet);
                }
                productsProperties.load(new StringReader((String)productsRet));

                final Properties formatsProperties = new Properties();
                final Object formatsRet = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.ALKIS_FORMATS_PROPERTIES.getValue(),
                                connectionContext);
                if (formatsRet instanceof Exception) {
                    throw new Exception("error while loading server resource "
                                + WundaBlauServerResources.ALKIS_FORMATS_PROPERTIES,
                        (Exception)formatsRet);
                }
                formatsProperties.load(new StringReader((String)formatsRet));

                final Object beschreibungRet = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.ALKIS_PRODUKTBESCHREIBUNG_XML.getValue(),
                                connectionContext);
                if (beschreibungRet instanceof Exception) {
                    throw new Exception("error while loading server resource "
                                + WundaBlauServerResources.ALKIS_PRODUKTBESCHREIBUNG_XML.getValue(),
                        (Exception)beschreibungRet);
                }

                INSTANCE = new ClientAlkisProducts(
                        ClientAlkisConf.getInstance(),
                        productsProperties,
                        formatsProperties,
                        (String)beschreibungRet);
            } catch (final Exception ex) {
                throw new RuntimeException("Error while parsing Alkis Product Description!", ex);
            }
        }
        return INSTANCE;
    }
}
