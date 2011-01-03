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
package de.cismet.cids.custom.objecteditors.commons;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class JComboWithStringsFilledFromProperties extends JComboBox {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JComboWithStringsFilledFromProperties object.
     */
    public JComboWithStringsFilledFromProperties() {
        this(null);
    }

    /**
     * Creates a new JComboWithStringsFilledFromProperties object.
     *
     * @param  entryPropertyFile  DOCUMENT ME!
     */
    public JComboWithStringsFilledFromProperties(final String entryPropertyFile) {
        InputStream is = null;
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            is = JComboWithStringsFilledFromProperties.class.getResourceAsStream(entryPropertyFile);
            final Document doc = builder.build(is);
            final List entries = doc.getRootElement().getChildren();
            final Vector<String> v = new Vector<String>();
            for (final Object o : entries) {
                final Element e = (Element)o;
                v.add(e.getText());
            }
            setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
            setModel(new DefaultComboBoxModel(new Vector(Arrays.asList("keine User gefunden"))));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
