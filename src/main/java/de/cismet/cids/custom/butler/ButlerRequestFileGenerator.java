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

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerRequestFileGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final String butler1URL = "//s102x002/arcgisserver$/BUTLER/AUFTRAG";
    private static ButlerRequestFileGenerator instance;
    private static Logger log = Logger.getLogger(ButlerRequestFileGenerator.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ButlerRequestFileGenerator object.
     */
    private ButlerRequestFileGenerator() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ButlerRequestFileGenerator getInstance() {
        if (instance == null) {
            instance = new ButlerRequestFileGenerator();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     */
    private void sendButler1Request() {
        final File f = new File(butler1URL);
        if (!f.canRead() || !f.canWrite()) {
            System.out.println("kann nicht auf server zugreifen..");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final ButlerRequestFileGenerator butlerGen = ButlerRequestFileGenerator.getInstance();
        butlerGen.sendButler1Request();
    }
}
