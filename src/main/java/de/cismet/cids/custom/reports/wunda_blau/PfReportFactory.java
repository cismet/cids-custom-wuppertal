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
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.tools.GenericByteArrayFactory;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;

import java.util.Collection;

import de.cismet.cids.custom.wunda_blau.search.actions.AbstractPotenzialflaecheReportCreator;
import de.cismet.cids.custom.wunda_blau.search.actions.PfReportDownloadAction;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportCreator;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportCreator.ReportConfiguration;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfReportFactory extends PfMapFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfReportFactory.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private ConnectionContext connectionContext;

    private final AbstractPotenzialflaecheReportCreator creator = new AbstractPotenzialflaecheReportCreator() {

            @Getter private CidsBean flaecheBean;

            /**
             * DOCUMENT ME!
             */
            @Override
            public void initMap() {
                PfReportFactory.this.initMap(getMapConfiguration(PotenzialflaecheReportCreator.Type.PF_DGK));
            }

            /**
             * DOCUMENT ME!
             *
             * @param   steckbrief  DOCUMENT ME!
             *
             * @return  DOCUMENT ME!
             *
             * @throws  Exception  DOCUMENT ME!
             */
            @Override
            public JasperReport getJasperReport(final CidsBean steckbrief) throws Exception {
                final MetaObjectNode mon = (steckbrief != null) ? new MetaObjectNode(steckbrief) : null;
                final Object ret = SessionManager.getProxy()
                            .executeTask(PfReportDownloadAction.TASK_NAME,
                                "WUNDA_BLAU",
                                mon,
                                getConnectionContext());

                if (ret instanceof Exception) {
                    final Exception ex = (Exception)ret;
                    throw ex;
                }
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                } else if (ret instanceof byte[]) {
                    return (JasperReport)JRLoader.loadObject(new ByteArrayInputStream((byte[])ret));
                } else {
                    return null;
                }
            }

            @Override
            public String getConfAttr(final String confAttr) throws Exception {
                return SessionManager.getProxy()
                            .getConfigAttr(
                                SessionManager.getSession().getUser(),
                                confAttr,
                                getConnectionContext());
            }

            @Override
            public Collection<MetaObjectNode> executeSearch(final CidsServerSearch search) throws Exception {
                return SessionManager.getProxy().customServerSearch(search, getConnectionContext());
            }

            @Override
            public BufferedImage loadMapFor(final Type type) throws Exception {
                return generateMap(getMapConfiguration(type));
            }

            @Override
            public MetaObject getMetaObject(final MetaObjectNode mon) throws Exception {
                return SessionManager.getProxy()
                            .getMetaObject(mon.getObjectId(),
                                mon.getClassId(),
                                mon.getDomain(),
                                getConnectionContext());
            }

            @Override
            public ConnectionContext getConnectionContext() {
                return connectionContext;
            }
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PfReportFactory object.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public PfReportFactory() throws Exception {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public byte[] create(final String string) throws Exception {
        final ReportConfiguration conf =
            new ObjectMapper().readValue(string, PotenzialflaecheReportCreator.ReportConfiguration.class);
        return creator.createReport(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        GenericByteArrayFactory.main(
            new String[] {
                "-d",
                "WUNDA_BLAU",
                "-c",
                "http://localhost:9986/callserver/binary",
                "-j",
                "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwIiwic3ViIjoiYWRtaW4iLCJkb21haW4iOiJXVU5EQV9CTEFVIn0.EQHuWjzSZ6HBswulk3lOQWqcvlt3NL9wdeOEGiKzwpo",
                "-C",
                PfReportFactory.class.getCanonicalName(),
                "-P",
                "{\"id\":17,\"templateId\":1,\"cacheDirectory\":\"/tmp/pf\"}"
            });
    }
}
