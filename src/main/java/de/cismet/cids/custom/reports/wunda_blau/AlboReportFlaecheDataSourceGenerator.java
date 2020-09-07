/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.reports.wunda_blau;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Collection;
import java.util.LinkedList;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboReportFlaecheDataSourceGenerator implements JasperReportDownload.JasperReportDataSourceGenerator,
    ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private final CidsBean flaecheBean;
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboReportFlaecheDataSourceGenerator object.
     *
     * @param  flaecheBean        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public AlboReportFlaecheDataSourceGenerator(final CidsBean flaecheBean, final ConnectionContext connectionContext) {
        this.flaecheBean = flaecheBean;
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JRDataSource generateDataSource() {
        final Collection<CidsBean> flaecheBeans = new LinkedList<>();
        flaecheBeans.add(flaecheBean);
        return new JRBeanCollectionDataSource(flaecheBeans);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
