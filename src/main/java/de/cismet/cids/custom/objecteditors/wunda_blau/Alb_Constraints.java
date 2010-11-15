/*
 *  Copyright (C) 2010 stefan
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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.server.search.CidsServerSearch;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.Alb_BaulastChecker;
import de.cismet.cids.custom.wunda_blau.search.Alb_BaulastblattChecker;
import de.cismet.cids.dynamics.CidsBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author stefan
 */
public class Alb_Constraints {

    public static List<String> getIncorrectBaulastDates(CidsBean blattBean) {
        List<String> result = new ArrayList<String>();
        List<CidsBean> baulastBeans = CidsBeanSupport.getBeanCollectionFromProperty(blattBean, "baulasten");
        if (baulastBeans != null) {
            for (CidsBean baulast : baulastBeans) {
                if (!checkBaulastDates(baulast)) {
                    result.add(baulast.toString());
                }
            }
        }
        return result;
    }

    public static boolean checkBaulastDates(CidsBean baulastBean) {
        Object eintragungsDatumObj = baulastBean.getProperty("eintragungsdatum");
        Object loeschungsDatumObj = baulastBean.getProperty("loeschungsdatum");
        Object befristungsDatumObj = baulastBean.getProperty("befristungsdatum");
        Object geschlossenAmDatumObj = baulastBean.getProperty("geschlossen_am");
        List<Date> testDates = new ArrayList<Date>();
        if (befristungsDatumObj instanceof Date) {
            testDates.add((Date) befristungsDatumObj);
        }
        if (geschlossenAmDatumObj instanceof Date) {
            testDates.add((Date) geschlossenAmDatumObj);
        }
        if (loeschungsDatumObj instanceof Date) {
            testDates.add((Date) loeschungsDatumObj);
        }
        if (testDates.size() > 0) {
            if (eintragungsDatumObj instanceof Date) {
                Date eintragungsDatum = (Date) eintragungsDatumObj;
                for (Date toTest : testDates) {
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

    public static boolean checkUniqueBlattNummer(String blattnummer, int id) throws ConnectionException {
        CidsServerSearch search = new Alb_BaulastblattChecker(blattnummer, id);
        Collection result = SessionManager.getConnection().customServerSearch(SessionManager.getSession().getUser(), search);
        if (result != null && result.size() > 0) {
            Object o = result.iterator().next();
            if (o instanceof List) {
                List<?> innerList = (List<?>) o;
                if (innerList.size() > 0) {
                    Object countObj = innerList.get(0);
                    if (countObj instanceof Long) {
                        long count = (Long) countObj;
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

    public static boolean checkUniqueBaulastNummer(String blattnummer, String laufendeNr, int id) throws ConnectionException {
        CidsServerSearch search = new Alb_BaulastChecker(blattnummer, laufendeNr, id);
        Collection result = SessionManager.getConnection().customServerSearch(SessionManager.getSession().getUser(), search);
        if (result != null && result.size() > 0) {
            Object o = result.iterator().next();
            if (o instanceof List) {
                List<?> innerList = (List<?>) o;
                if (innerList.size() > 0) {
                    Object countObj = innerList.get(0);
                    if (countObj instanceof Long) {
                        long count = (Long) countObj;
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

    public static List<String> getBaulastenOhneBelastestesFlurstueckFromBlatt(CidsBean baulastBlattBean) {
        List<String> result = new ArrayList<String>();
        List<CidsBean> baulastenList = CidsBeanSupport.getBeanCollectionFromProperty(baulastBlattBean, "baulasten");
        if (baulastenList != null) {
            for (CidsBean baulast : baulastenList) {
                if (!checkBaulastHasBelastetesFlurstueck(baulast)) {
                    result.add(baulast.toString());
                }
            }
        } else {
            throw new RuntimeException("Could not get Property 'baulasten' from Blatt Bean!");
        }
        return result;
    }

    public static boolean checkBaulastHasBelastetesFlurstueck(CidsBean baulastBean) {
        if (baulastBean != null) {
            List<CidsBean> fsList = CidsBeanSupport.getBeanCollectionFromProperty(baulastBean, "flurstuecke_belastet");
            return fsList != null && fsList.size() > 0;
        }
        return false;
    }
}
