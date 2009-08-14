/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.objecteditors.commons;

import java.awt.Component; 
import java.util.Arrays;  
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author thorsten
 */
public class JaNeinNullCombo extends JComboBox{
    DefaultListCellRenderer dlcr=new DefaultListCellRenderer();
    
    public JaNeinNullCombo() {
        super(new DefaultComboBoxModel(new Vector(Arrays.asList("Ja","Nein",null))));
        setRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value!=null) {
                    return dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                else {
                    JLabel nullLabel=(JLabel)dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    nullLabel.setText("nicht ausgewählt");
                    return nullLabel;
                }
            }
        });
        setSelectedItem(null);
    }

}
