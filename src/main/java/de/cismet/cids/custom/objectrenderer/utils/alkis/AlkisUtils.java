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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.newuser.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.LandParcel;
import de.aedsicad.aaaweb.service.util.Point;

import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.ClientBillingUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisBuchungsblattRenderer;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisBuchungsblattRenderer.Buchungsblattbezirke;
import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.wunda_blau.search.actions.ServerAlkisSoapAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetServerResourceServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlkisUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisUtils.class);

    public static final String LINK_SEPARATOR_TOKEN = "::";

    public static final String PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_NRW =
        "custom.alkis.product.bestandsnachweis_nrw@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM =
        "custom.alkis.product.bestandsnachweis_kom@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM_INTERN =
        "custom.alkis.product.bestandsnachweis_kom_intern@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_STICHSTAGSBEZOGEN_NRW =
        "custom.alkis.product.bestandsnachweis_stichtagsbezogen_nrw@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_GRUNDSTUECKSNACHWEIS_NRW =
        "custom.alkis.product.grundstuecksnachweis_nrw@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_FLURSTUECKSNACHWEIS =
        "custom.alkis.product.flurstuecksnachweis@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_NRW =
        "custom.alkis.product.flurstuecks_eigentumsnachweis_nrw@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_KOM =
        "custom.alkis.product.flurstuecks_eigentumsnachweis_kom@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_KOM_INTERN =
        "custom.alkis.product.flurstuecks_eigentumsnachweis_kom_intern@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_KARTE = "custom.alkis.product.karte@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_ENABLED =
        "baulast.report.bescheinigung_enabled@WUNDA_BLAU";
    public static final String PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_DISABLED =
        "baulast.report.bescheinigung_disabled@WUNDA_BLAU";

    public static final String ALKIS_HTML_PRODUCTS_ENABLED = "custom.alkis.products.html.enabled";
    public static final String ALKIS_EIGENTUEMER = "custom.alkis.buchungsblatt@WUNDA_BLAU";
    static final AlkisBuchungsblattRenderer.Buchungsblattbezirke BUCHUNGSBLATTBEZIRKE;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                Category.STATIC,
                AlkisUtils.class.getSimpleName());

        Buchungsblattbezirke buchungsblattbezirke = null;
        try {
            final Object ret = SessionManager.getSession()
                        .getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                            GetServerResourceServerAction.TASK_NAME,
                            "WUNDA_BLAU",
                            WundaBlauServerResources.ALKIS_BUCHUNTSBLATTBEZIRKE_JSON.getValue(),
                            connectionContext);
            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
            final ObjectMapper mapper = new ObjectMapper();
            buchungsblattbezirke = mapper.readValue((String)ret, Buchungsblattbezirke.class);
        } catch (final Exception ex) {
            LOG.error("Problem while reading the Buchungsblattbezirke.", ex);
            buchungsblattbezirke = new Buchungsblattbezirke();
        }
        BUCHUNGSBLATTBEZIRKE = buchungsblattbezirke;
    }

    //~ Methods ----------------------------------------------------------------

    // --
    /**
     * DOCUMENT ME!
     *
     * @param   bean         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateLinkFromCidsBean(final CidsBean bean, final String description) {
        if ((bean != null) && (description != null)) {
            final int objectID = bean.getMetaObject().getId();
            final StringBuilder result = new StringBuilder("<a href=\"");
//            result.append(bean.getMetaObject().getMetaClass().getID()).append(LINK_SEPARATOR_TOKEN).append(objectID);
            result.append(bean.getMetaObject().getMetaClass().getID()).append(LINK_SEPARATOR_TOKEN).append(objectID);
            result.append("\">");
            result.append(description);
            result.append("</a>");
            return result.toString();
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getBuchungsblattbezirkFromBuchungsblattnummer(final String buchungsblattnummer) {
        try {
            final String bezirksNr = buchungsblattnummer.substring(0, buchungsblattnummer.indexOf("-"));
            final String bezirksname = BUCHUNGSBLATTBEZIRKE.getDistrictNamesMap().get(bezirksNr);
            final StringBuffer b = new StringBuffer(bezirksname).append(" (").append(bezirksNr).append(')');
            return b.toString();
        } catch (Exception e) {
            LOG.error("Error in getBuchungsblattbezirkFromBuchungsblattnummer(" + buchungsblattnummer + ")", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   usageKey           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static String getFertigungsVermerk(final String usageKey, final ConnectionContext connectionContext)
            throws ConnectionException {
        final String fertigungsVermerk;
        final String currentUsageKey = (BillingPopup.getInstance().getCurrentUsage() != null)
            ? BillingPopup.getInstance().getCurrentUsage().getKey() : null;
        if ((usageKey == null) || (usageKey.equals(currentUsageKey))) {
            fertigungsVermerk = SessionManager.getConnection()
                        .getConfigAttr(
                                SessionManager.getSession().getUser(),
                                "custom.alkis.fertigungsVermerk@WUNDA_BLAU",
                                connectionContext);
        } else {
            fertigungsVermerk = null;
        }
        return fertigungsVermerk;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   landParcel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getLandparcelCodeFromParcelBeanObject(final Object landParcel) {
        if (landParcel instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean)landParcel;
            final Object parcelCodeObj = cidsBean.getProperty("alkis_id");
            if (parcelCodeObj != null) {
                return parcelCodeObj.toString();
            }
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsstelle  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LandParcel[] getLandparcelFromBuchungsstelle(final Buchungsstelle buchungsstelle) {
        if ((buchungsstelle.getBuchungsstellen() == null) && (buchungsstelle.getLandParcel() == null)) {
            LOG.warn("getLandparcelFromBuchungsstelle returns null. Problem on landparcel with number:"
                        + buchungsstelle.getSequentialNumber());
            return new LandParcel[0];
        } else if (buchungsstelle.getBuchungsstellen() == null) {
            return buchungsstelle.getLandParcel();
        } else {
            LandParcel[] result = buchungsstelle.getLandParcel();
            for (final Buchungsstelle b : buchungsstelle.getBuchungsstellen()) {
                result = concatArrays(result, getLandparcelFromBuchungsstelle(b));
            }
            return result;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   a  DOCUMENT ME!
     * @param   b  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LandParcel[] concatArrays(LandParcel[] a, LandParcel[] b) {
        if (a == null) {
            a = new LandParcel[0];
        }
        if (b == null) {
            b = new LandParcel[0];
        }
        return (LandParcel[])ArrayUtils.addAll(a, b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   land       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   zaehler    DOCUMENT ME!
     * @param   nenner     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateLandparcelCode(final int land,
            final int gemarkung,
            final int flur,
            final int zaehler,
            final int nenner) {
        final String withoutNenner = generateLandparcelCode(land, gemarkung, flur, zaehler);
        final StringBuilder sb = new StringBuilder(withoutNenner);
        sb.append(String.format("/%04d", nenner));
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   land       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   zaehler    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateLandparcelCode(final int land,
            final int gemarkung,
            final int flur,
            final int zaehler) {
        return String.format("%02d%04d-%03d-%05d", land, gemarkung, flur, zaehler);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   strings    DOCUMENT ME!
     * @param   separator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String arrayToSeparatedString(final String[] strings, final String separator) {
        if (strings != null) {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                result.append(strings[i]);
                if ((i + 1) < strings.length) {
                    result.append(separator);
                }
            }
            return result.toString();
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blatt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getBuchungsartFromBuchungsblatt(final Buchungsblatt blatt) {
        final Buchungsstelle[] buchungsstellen = blatt.getBuchungsstellen();
        if ((buchungsstellen != null) && (buchungsstellen.length > 0)) {
            final ArrayList<Buchungsstelle> alleStellen = new ArrayList<Buchungsstelle>();
            alleStellen.addAll(Arrays.asList(buchungsstellen));
            if (isListOfSameBuchungsart(alleStellen)) {
                return alleStellen.get(0).getBuchungsart();
            } else {
                return "diverse";
            }
        }
        return "";
    }

    /**
     * Check if in list of Buchungsstellen, all Buchungsstellen have the same Buchungsart. Return true if all have the
     * same Buchungsart, false otherwise. The check is realized with adding the buchungsart to a set. As soon the set
     * contains a second buchungsart, false can be returned.
     *
     * @param   buchungsstellen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isListOfSameBuchungsart(final List<Buchungsstelle> buchungsstellen) {
        final Set<String> set = new HashSet<String>();
        for (final Buchungsstelle o : buchungsstellen) {
            if (set.isEmpty()) {
                set.add(o.getBuchungsart());
            } else {
                if (set.add(o.getBuchungsart())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aufteilungsnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String prettyPrintAufteilungsnummer(final String aufteilungsnummer) {
        if (aufteilungsnummer != null) {
            return "ATP Nr. " + aufteilungsnummer;
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fraction  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String prettyPrintFraction(final String fraction) {
        if (fraction != null) {
            return "Anteil " + fraction;
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisPrintAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "navigator.alkis.print@WUNDA_BLAU",
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Print Dialog!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisProductAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "csa://alkisProduct@WUNDA_BLAU",
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Products!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasEigentuemerAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            ALKIS_EIGENTUEMER,
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Buchungsblatt!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisHTMLProductAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            ALKIS_HTML_PRODUCTS_ENABLED,
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis HTML Products!", ex);
        }
        return false;
    }

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
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  ConnectionException DOCUMENT ME!
     */
    public static String createBaulastenFertigungsVermerk(final User user, final ConnectionContext connectionContext)
            throws Exception {
        final String fertigungsVermerk = SessionManager.getConnection()
                    .getConfigAttr(user, "custom.baulasten.fertigungsVermerk@WUNDA_BLAU", connectionContext);
        if (fertigungsVermerk != null) {
            return fertigungsVermerk;
        } else {
            final CidsBean billingLogin = ClientBillingUtils.getInstance().getExternalUser(user, connectionContext);
            if (billingLogin != null) {
                final CidsBean billingKunde = (CidsBean)billingLogin.getProperty("kunde");
                if (billingKunde != null) {
                    return (String)billingKunde.getProperty("name");
                }
            }
            return null;
        }
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
