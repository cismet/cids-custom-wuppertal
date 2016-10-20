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
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.Product;
import de.cismet.cids.custom.wunda_blau.search.actions.NasZaehlObjekteServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMeasurementPointSearchStatement.Pointtype;
import de.cismet.cids.custom.wunda_blau.search.server.NasPointSearch;

import de.cismet.cids.server.actions.ServerActionParameter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class NasFeeCalculator {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(NasFeeCalculator.class);
    private static final String PG_POINTS = "eapkt_1000";
    private static final String PG_FLURSTUECK = "eaflst_1000";
    private static final String PG_EIGENTUEMER = "eaeig_1000";
    private static final String PG_GEBAEUDE = "eageb_1000";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   amount      template DOCUMENT ME!
     * @param   productKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     *
     * @param   amount      g DOCUMENT ME!
     * @param   productKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getFeeForPoints(final int amount, final String productKey) {
        final double basePrice = getPriceForProduct(productKey, PG_POINTS);
        return calculateFeeWithDiscount(amount, basePrice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   amount      DOCUMENT ME!
     * @param   productKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getFeeForFlurstuecke(final int amount, final String productKey) {
        final double basePrice = getPriceForProduct(productKey, PG_FLURSTUECK);
        return calculateFeeWithDiscount(amount, basePrice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   amount      DOCUMENT ME!
     * @param   productKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getFeeForGebaeude(final int amount, final String productKey) {
        final double basePrice = getPriceForProduct(productKey, PG_GEBAEUDE);
        return calculateFeeWithDiscount(amount, basePrice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   amount      DOCUMENT ME!
     * @param   productKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getFeeForEigentuemer(final int amount, final String productKey) {
        final double basePrice = getPriceForProduct(productKey, PG_EIGENTUEMER);
        return calculateFeeWithDiscount(amount, basePrice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getPointAmount(final Geometry g) throws ConnectionException {
        final ArrayList<Pointtype> pointtypes = new ArrayList<Pointtype>();
        pointtypes.add(Pointtype.AUFNAHMEPUNKTE);
        pointtypes.add(Pointtype.SONSTIGE_VERMESSUNGSPUNKTE);
        pointtypes.add(Pointtype.GRENZPUNKTE);
        pointtypes.add(Pointtype.BESONDERE_GEBAEUDEPUNKTE);
        pointtypes.add(Pointtype.BESONDERE_BAUWERKSPUNKTE);
        pointtypes.add(Pointtype.BESONDERE_TOPOGRAPHISCHE_PUNKTE);
        final NasPointSearch search = new NasPointSearch(
                null,
                pointtypes,
                null,
                g);
        final ArrayList<Integer> c = (ArrayList<Integer>)SessionManager.getProxy()
                    .customServerSearch(SessionManager.getSession().getUser(), search);
        return c.get(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getFlurstueckAmount(final Geometry g) throws ConnectionException {
        return getAmount(NasZaehlObjekteServerAction.NasSearchType.FLURSTUECKE, g);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   searchType  DOCUMENT ME!
     * @param   geom        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getAmount(final NasZaehlObjekteServerAction.NasSearchType searchType, final Geometry geom)
            throws ConnectionException {
        final ServerActionParameter sapType = new ServerActionParameter<NasZaehlObjekteServerAction.NasSearchType>(
                NasZaehlObjekteServerAction.Parameter.SEARCH_TYPE.toString(),
                searchType);
        final ServerActionParameter sapGeom = new ServerActionParameter<Geometry>(
                NasZaehlObjekteServerAction.Parameter.GEOMETRY.toString(),
                geom);
        final ArrayList<Integer> c = (ArrayList<Integer>)SessionManager.getProxy()
                    .executeTask(NasZaehlObjekteServerAction.TASK_NAME, "WUNDA_BLAU", null, sapType, sapGeom);
        return c.get(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getGebaeudeAmount(final Geometry g) throws ConnectionException {
        return getAmount(NasZaehlObjekteServerAction.NasSearchType.GEBAEUDE, g);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getDachPunkteAmount(final Geometry g) throws ConnectionException {
        return getAmount(NasZaehlObjekteServerAction.NasSearchType.DACHPUNKTE, g);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static int getBodenPunkteAmount(final Geometry g) throws ConnectionException {
        return getAmount(NasZaehlObjekteServerAction.NasSearchType.BODENPUNKTE, g);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product       DOCUMENT ME!
     * @param   productGroup  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static double getPriceForProduct(final String product, final String productGroup) {
        final HashMap<String, Product> products = BillingPopup.getProducts();
        Double d = 0d;
        if (products == null) {
            LOG.warn("Could not get the list of billing products");
            return d;
        }

        final Product p = products.get(product);
        if (p == null) {
            LOG.warn("Could not find an entry for the product " + product + " in the billing.json");
            return d;
        }
        if (p.getPrices() != null) {
            d = p.getPrices().get(productGroup);
            if (d == null) {
                LOG.warn("Could not find a price for the product " + product + " and productGroup " + productGroup
                            + " in the billing.json");
                return 0d;
            }
        }
        return d;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   amount     DOCUMENT ME!
     * @param   basePrice  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static double calculateFeeWithDiscount(int amount, final double basePrice) {
        double fee = 0;
        if (amount > 1000000) {
            final int tmpPoints = amount - 1000000;
            fee += tmpPoints * basePrice * 0.0625;
            amount = 1000000;
        }
        if (amount > 100000) {
            final int tmpPpoints = amount - 100000;
            fee += tmpPpoints * basePrice * 0.125;
            amount = 100000;
        }
        if (amount > 10000) {
            final int tmpPoints = amount - 10000;
            fee += tmpPoints * basePrice * 0.25d;
            amount = 10000;
        }
        if (amount > 1000) {
            final int tmpPoints = amount - 1000;
            fee += tmpPoints * basePrice * 0.5d;
            amount = 1000;
        }
        fee += amount * basePrice;
        return fee;
    }
}
