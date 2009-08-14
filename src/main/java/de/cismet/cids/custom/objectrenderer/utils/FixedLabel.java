/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import javax.swing.JLabel;

/**
 *
 * @author srichter
 */
public class FixedLabel extends JLabel {

    public FixedLabel() {
        super();
        this.size = 100;
    }

    public FixedLabel(int size) {
        super();
        this.size = size;
    }
    private int size;

    @Override
    public void setText(String text) {
        if (text != null) {
            text = text.replace("\n", "<br>");
        }
        text = "<html><table width=\"" + size + "\" border=\"0\"><tr><td>" + text + "</tr></table></html>";
        super.setText(text);
    }
}
