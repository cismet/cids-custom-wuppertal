/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.Alb_BaulastChecker;
import de.cismet.cids.custom.wunda_blau.search.server.Alb_BaulastblattChecker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class Alb_Constraints {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   blattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> getIncorrectBaulastDates(final CidsBean blattBean) {
        final List<String> result = new ArrayList<String>();
        final List<CidsBean> baulastBeans = CidsBeanSupport.getBeanCollectionFromProperty(blattBean, "baulasten");
        if (baulastBeans != null) {
            for (final CidsBean baulast : baulastBeans) {
                if (!checkBaulastDates(baulast)) {
                    result.add(baulast.toString());
                }
            }
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkBaulastDates(final CidsBean baulastBean) {
        final Object eintragungsDatumObj = baulastBean.getProperty("eintragungsdatum");
        final Object loeschungsDatumObj = baulastBean.getProperty("loeschungsdatum");
        final Object befristungsDatumObj = baulastBean.getProperty("befristungsdatum");
        final Object geschlossenAmDatumObj = baulastBean.getProperty("geschlossen_am");
        final List<Date> testDates = new ArrayList<Date>();
        if (befristungsDatumObj instanceof Date) {
            testDates.add((Date)befristungsDatumObj);
        }
        if (geschlossenAmDatumObj instanceof Date) {
            testDates.add((Date)geschlossenAmDatumObj);
        }
        if (loeschungsDatumObj instanceof Date) {
            testDates.add((Date)loeschungsDatumObj);
        }
        if (testDates.size() > 0) {
            if (eintragungsDatumObj instanceof Date) {
                final Date eintragungsDatum = (Date)eintragungsDatumObj;
                for (final Date toTest : testDates) {
                    if (toTest.compareTo(eintragungsDatum) < 0) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer  DOCUMENT ME!
     * @param   id           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     * @throws  RuntimeException     DOCUMENT ME!
     */
    public static boolean checkUniqueBlattNummer(final String blattnummer, final int id) throws ConnectionException {
        final CidsServerSearch search = new Alb_BaulastblattChecker(blattnummer, id);
        final Collection result = SessionManager.getConnection()
                    .customServerSearch(SessionManager.getSession().getUser(), search);
        if ((result != null) && (result.size() > 0)) {
            final Object o = result.iterator().next();
            if (o instanceof List) {
                final List<?> innerList = (List<?>)o;
                if (innerList.size() > 0) {
                    final Object countObj = innerList.get(0);
                    if (countObj instanceof Long) {
                        final long count = (Long)countObj;
                        if (count < 1) {
//                                log.debug("blattnummer is unique");
                            return true;
                        } else {
//                                log.debug("blattnummer is not unique");
//                                JOptionPane.showMessageDialog(this, "Die Blattnummer " + blattnummer + " existiert bereits! Bitte geben Sie eine andere Blattnummer ein.");
                            return false;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Unbekannter Fehler beim Speichern!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer  DOCUMENT ME!
     * @param   laufendeNr   DOCUMENT ME!
     * @param   id           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     * @throws  RuntimeException     DOCUMENT ME!
     */
    public static boolean checkUniqueBaulastNummer(final String blattnummer, final String laufendeNr, final int id)
            throws ConnectionException {
        final CidsServerSearch search = new Alb_BaulastChecker(blattnummer, laufendeNr, id);
        final Collection result = SessionManager.getConnection()
                    .customServerSearch(SessionManager.getSession().getUser(), search);
        if ((result != null) && (result.size() > 0)) {
            final Object o = result.iterator().next();
            if (o instanceof List) {
                final List<?> innerList = (List<?>)o;
                if (innerList.size() > 0) {
                    final Object countObj = innerList.get(0);
                    if (countObj instanceof Long) {
                        final long count = (Long)countObj;
                        if (count < 1) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Unbekannter Fehler beim Speichern!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastBlattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public static List<String> getBaulastenOhneBelastestesFlurstueckFromBlatt(final CidsBean baulastBlattBean) {
        final List<String> result = new ArrayList<String>();
        final List<CidsBean> baulastenList = CidsBeanSupport.getBeanCollectionFromProperty(
                baulastBlattBean,
                "baulasten");
        if (baulastenList != null) {
            for (final CidsBean baulast : baulastenList) {
                if (!checkBaulastHasBelastetesFlurstueck(baulast)) {
                    result.add(baulast.toString());
                }
            }
        } else {
            throw new RuntimeException("Could not get Property 'baulasten' from Blatt Bean!");
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkBaulastHasBelastetesFlurstueck(final CidsBean baulastBean) {
        if (baulastBean != null) {
            final List<CidsBean> fsList = CidsBeanSupport.getBeanCollectionFromProperty(
                    baulastBean,
                    "flurstuecke_belastet");
            return (fsList != null) && (fsList.size() > 0);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkEintragungsdatum(final CidsBean baulastBean) {
        final Object eintragungsDatumObj = baulastBean.getProperty("eintragungsdatum");

        return (eintragungsDatumObj != null) && (eintragungsDatumObj instanceof Date);
    }
}
