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
package de.cismet.cids.custom.clientutils;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class HeapSpaceFucker {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HeapSpaceFucker object.
     */
    public HeapSpaceFucker() {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        System.out.println("HeapSpaceFucker");
                        System.out.println("HSF:: totalMemory:" + Runtime.getRuntime().totalMemory());
                        Thread.sleep(1000);
                        System.out.println("3 ...");
                        Thread.sleep(1000);
                        System.out.println("2 ...");
                        Thread.sleep(1000);
                        System.out.println("1 ...");

                        final ArrayList bomber = new ArrayList();
                        while (true) {
                            final byte[] tick = new byte[1024 * 1024 * 100];
                            bomber.add(tick);
                            System.out.println("HSF::  freeMemory:" + Runtime.getRuntime().freeMemory());
                        }
                    } catch (Exception e) {
                    }
                }
            };
        new Thread(r).start();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        new HeapSpaceFucker();
    }
}
