/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CoolTabPanel.java
 *
 * Created on 5. November 2007, 16:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.deprecated;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class CoolTabPanel extends JPanel {

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a new instance of CoolTabPanel.
     *
     * @param  g  DOCUMENT ME!
     */

    @Override
    public void paint(final Graphics g) {
        final Graphics2D g2d = (Graphics2D)g;
        final Composite old = g2d.getComposite();
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.setColor(new Color(255, 255, 255));
        g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        g2d.setComposite(AlphaComposite.SrcAtop.derive(0.3f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(old);
        super.paint(g);
    }
}
