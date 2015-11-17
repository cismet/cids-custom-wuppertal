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
package de.cismet.cids.custom.objectrenderer.aggregationhandler;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationHandler;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsBeanAggregationHandler.class)
public class BaulastblattAggregationHandler implements CidsBeanAggregationHandler {

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean consume() {
        return true;
    }

    @Override
    public String getSourceMetaClassTablename() {
        return "alb_baulastblatt";
    }

    @Override
    public String getTargetMetaClassTablename() {
        return "alb_baulast";
    }

    @Override
    public Collection<CidsBean> getAggregatedBeans(final CidsBean cidsBean) {
        final Collection<CidsBean> sharedCidsBeans = new ArrayList<CidsBean>();
        sharedCidsBeans.addAll(cidsBean.getBeanCollectionProperty("baulasten"));
        return sharedCidsBeans;
    }
}
