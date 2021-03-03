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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboReportFlaecheDataSourceScriptlet {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   beans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String beansToString(final Collection<CidsBean> beans) {
        return beansToString(beans, ", ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBeans  DOCUMENT ME!
     * @param   separator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String beansToString(final Collection<CidsBean> cidsBeans, final String separator) {
        final List<String> strings = new ArrayList<>();
        if (cidsBeans != null) {
            for (final CidsBean cidsBean : cidsBeans) {
                if (cidsBean != null) {
                    strings.add(cidsBean.toString());
                }
            }
        }
        return String.join(separator, strings);
    }
}
