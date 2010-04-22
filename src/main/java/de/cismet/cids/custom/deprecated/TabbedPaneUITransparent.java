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
import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

public class TabbedPaneUITransparent extends WindowsTabbedPaneUI {
    
    @Override
    protected void paintContentBorder(Graphics g, int arg1, int arg2) {
/*
int tw = tabPane.getBounds().width;
int th = tabPane.getBounds().height;
g.setColor(new Color(0, 0, 0));//, 0));
 
g.fillRect(0, 0, tw, th);
 
super.paintContentBorder(g, arg1, arg2);
 */
    }
    
    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        int retValue;
        retValue = super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 15;
        return retValue;
    }
    
    
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
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
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        ((Graphics2D)g).setComposite(AlphaComposite.SrcOver.derive(1.0f));
        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
//        Insets retValue;
//        
//        retValue = super.getContentBorderInsets(tabPlacement);
        return new Insets(2, 0, 0, 2);
    }
    
    

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        //super.paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
