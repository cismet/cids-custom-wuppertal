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
package de.cismet.cids.custom.reports.wunda_blau;

import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.objecteditors.utils.AlboProperties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class AlboReportVorgangParametersGenerator implements JasperReportDownload.JasperReportParametersGenerator,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AlboReportVorgangParametersGenerator.class);
    private static final AlboProperties PROPERTIES = AlboProperties.getInstance();

    //~ Instance fields --------------------------------------------------------

    private final CidsBean vorgangBean;
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboReportVorgangParametersGenerator object.
     *
     * @param  vorgangBean        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public AlboReportVorgangParametersGenerator(final CidsBean vorgangBean, final ConnectionContext connectionContext) {
        this.vorgangBean = vorgangBean;
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return vorgangBean;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public Map generateParamters() {
        final Map<String, Object> parameters = new HashMap<>();
        return parameters;
    }
}
