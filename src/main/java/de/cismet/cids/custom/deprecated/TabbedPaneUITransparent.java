/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * TabbedPaneUITransparent.java
 *
 * Created on 5. November 2007, 15:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.deprecated;

/**
 *
 * @author srichter
 */

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TabbedPaneUITransparent extends BasicTabbedPaneUI {

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void paintContentBorder(final Graphics g, final int arg1, final int arg2) {
/*
int tw = tabPane.getBounds().width;
int th = tabPane.getBounds().height;
g.setColor(new Color(0, 0, 0));//, 0));

g.fillRect(0, 0, tw, th);

super.paintContentBorder(g, arg1, arg2);
 */
    }

    @Override
    protected int calculateTabHeight(final int tabPlacement, final int tabIndex, final int fontHeight) {
        final int retValue;
        retValue = super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 15;
        return retValue;
    }

    @Override
    protected void paintTabBackground(final Graphics g,
            final int tabPlacement,
            final int tabIndex,
            final int x,
            final int y,
            final int w,
            final int h,
            final boolean isSelected) {
//        Color bu = g.getColor();
//        g.setColor(Color.BLACK);
//        Rectangle r = rects[tabIndex];
//
//            if (r != null) {
//                g.drawRect(x,y,w,h);
////                g.setColor(Color.WHITE);
////                g.drawRect(x+1,y+1,w-1,h-1);
////                g.clearRect(x+2,y+2,w-2,h-2);
////                g.clearRect(x+1,y+1,w-1,h-1);
//            }
////        }
//        g.setColor(bu);
//        ((Graphics2D)g).setComposite(AlphaComposite.SrcAtop.derive(0.5f));
        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
    }

    @Override
    protected void paintText(final Graphics g,
            final int tabPlacement,
            final Font font,
            final FontMetrics metrics,
            final int tabIndex,
            final String title,
            final Rectangle textRect,
            final boolean isSelected) {
        ((Graphics2D)g).setComposite(AlphaComposite.SrcOver.derive(1.0f));
        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }

    @Override
    protected Insets getContentBorderInsets(final int tabPlacement) {
//        Insets retValue;
//
//        retValue = super.getContentBorderInsets(tabPlacement);
        return new Insets(2, 0, 0, 2);
    }

    @Override
    protected void paintFocusIndicator(final Graphics g,
            final int tabPlacement,
            final Rectangle[] rects,
            final int tabIndex,
            final Rectangle iconRect,
            final Rectangle textRect,
            final boolean isSelected) {
        // super.paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
    }

    @Override
    protected void paintTab(final Graphics g,
            final int tabPlacement,
            final Rectangle[] rects,
            final int tabIndex,
            final Rectangle iconRect,
            final Rectangle textRect) {
        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    }
}
