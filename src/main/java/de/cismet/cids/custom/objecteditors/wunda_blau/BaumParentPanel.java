/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 *
 * @author Sandra
 */
public interface BaumParentPanel extends ConnectionContextProvider,
        CidsBeanStore{

    /**
     *
     * @return 
     */
    public boolean isEditor();
}
