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

import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.cismet.cids.client.tools.WebDavTunnelHelper;

import de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEditor;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.netutil.ProxyHandler;

import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Getter
public class TreppenReportBean extends ReportBeanWithMapAndTwoWebDavImages {

    //~ Static fields/initializers ---------------------------------------------

    private static final WebDavTunnelHelper WEBDAV_HELPER;
    private static final String WEBDAV_DIRECTORY;

    static {
        final ResourceBundle webDavBundle = ResourceBundle.getBundle("WebDav");
        String pass = webDavBundle.getString("password");

        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
            pass = PasswordEncrypter.decryptString(pass);
        }

        final String webDavPassword = pass;
        final String webDavUser = webDavBundle.getString("user");
        WEBDAV_HELPER = new WebDavTunnelHelper(
                "WUNDA_BLAU",
                ProxyHandler.getInstance().getProxy(),
                webDavUser,
                webDavPassword,
                false);
        WEBDAV_DIRECTORY = webDavBundle.getString("url_treppen");
    }

    //~ Instance fields --------------------------------------------------------

    private final Collection<StuetzMauerBean> stuetzmauern = new ArrayList<>();
    private final Double zustandStuetzmauern;
    private final Double kostenStuetzmauern;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  treppe             DOCUMENT ME!
     * @param  editor             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppenReportBean(final CidsBean treppe,
            final TreppeEditor editor,
            final ConnectionContext connectionContext) {
        super(
            treppe,
            java.util.ResourceBundle.getBundle(
                "de/cismet/cids/custom/reports/wunda_blau/MauernReport").getString("map_url"),
            WEBDAV_HELPER,
            WEBDAV_DIRECTORY,
            connectionContext);

        zustandStuetzmauern = editor.getZustandStuetzmauern();
        kostenStuetzmauern = editor.getKostenStuetzmauern();

        for (final CidsBean treppeMauerBean : editor.getMauerBeans().keySet()) {
            final CidsBean mauerBean = editor.getMauerBeans().get(treppeMauerBean);

            final Boolean bauwerk = true;
            final String name = (String)mauerBean.getProperty("lagebezeichnung");
            final String art = (String)mauerBean.getProperty("materialtyp.name");
            final String wo = (String)treppeMauerBean.getProperty("wo");
            final StuetzMauerBean stuetzmauer = new StuetzMauerBean(bauwerk, name, art, wo);
            stuetzmauern.add(stuetzmauer);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getDavFile(final CidsBean cidsBean) {
        return (String)cidsBean.getProperty("url.object_name");
    }

    @Override
    protected List<CidsBean> getImageBeans() {
        return (getCidsBean() != null) ? getCidsBean().getBeanCollectionProperty("bilder").stream().filter((b) -> {
                        return b.getProperty("laufende_nummer") != null;
                    }).sorted(
                        Comparator.comparing((b) -> { return (Integer)b.getProperty("laufende_nummer"); },
                            Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList()) : null;
    }

    @Override
    protected Geometry getGeometry() {
        return (getCidsBean() != null) ? (Geometry)getCidsBean().getProperty("geometrie.geo_field") : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getTreppe() {
        return getCidsBean();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public class StuetzMauerBean {

        //~ Instance fields ----------------------------------------------------

        private final Boolean treppenbauwerk;
        private final String name;
        private final String art;
        private final String wo;
    }
}
