/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.commons;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author thorsten
 */
public class JComboWithStringsFilledFromProperties extends JComboBox {

    public JComboWithStringsFilledFromProperties() {
        this(null);
    }
    public JComboWithStringsFilledFromProperties(String entryPropertyFile) {

        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(JComboWithStringsFilledFromProperties.class.getResourceAsStream(entryPropertyFile));
            List entries = doc.getRootElement().getChildren();
            Vector<String> v = new Vector<String>();
            for (Object o : entries) {
                Element e = (Element) o;
                v.add(e.getText());
            }
            setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
            setModel(new DefaultComboBoxModel(new Vector(Arrays.asList("keine User gefunden"))));
        }
    }

    
}
