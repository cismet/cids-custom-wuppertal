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
package de.cismet.cids.custom.wunda_blau.search;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ImageIcon;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class Sb_StadtbildSearchTooltip extends PNode {

    //~ Static fields/initializers ---------------------------------------------

    private static final Color COLOR_BACKGROUND = new Color(255, 255, 222, 200);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_StadtbildSearchTooltip object.
     *
     * @param  icoStadtbildserie  DOCUMENT ME!
     */
    public Sb_StadtbildSearchTooltip(final ImageIcon icoStadtbildserie) {
        double leftMargin = 5;
        double offsetX = leftMargin;
        double offsetY = 5;
        double totalWidth = offsetX;
        double totalHeight = offsetY;

        final Collection<PNode> nodesToAdd = new LinkedList<PNode>();

        if ((icoStadtbildserie != null) && (icoStadtbildserie.getImage() != null)) {
            final PImage imgHeader = new PImage(icoStadtbildserie.getImage());
            imgHeader.setOffset(offsetX, offsetY);

            offsetX += icoStadtbildserie.getIconWidth() + 5;
            leftMargin = offsetX;
            totalWidth = offsetX;
            totalHeight += imgHeader.getHeight();

            nodesToAdd.add(imgHeader);
        }

        final PText lblHeader = new PText(NbBundle.getMessage(
                    Sb_StadtbildSearchTooltip.class,
                    "Sb_StadtbildSearchTooltip.Sb_StadtbildSearchTooltip(ImageIcon).lblHeader"));
        final Font defaultFont = lblHeader.getFont();
        final Font boldDefaultFont = new Font(defaultFont.getName(),
                defaultFont.getStyle()
                        + Font.BOLD,
                defaultFont.getSize());

        lblHeader.setFont(boldDefaultFont);
        lblHeader.setOffset(offsetX, offsetY);
        totalWidth = Math.max(totalWidth, offsetX + lblHeader.getWidth());
        totalHeight = Math.max(totalHeight, offsetY + lblHeader.getHeight());
        offsetX = leftMargin;
        offsetY += 2 * lblHeader.getHeight();

        nodesToAdd.add(lblHeader);

        final PText lblFilter = new PText(NbBundle.getMessage(
                    Sb_StadtbildSearchTooltip.class,
                    "Sb_StadtbildSearchTooltip.Sb_StadtbildSearchTooltip(ImageIcon).lblFilter"));
        lblFilter.setOffset(offsetX, offsetY);
        totalWidth = Math.max(totalWidth, offsetX + lblFilter.getWidth());
        totalHeight = Math.max(totalHeight, offsetY + lblFilter.getHeight());

        nodesToAdd.add(lblFilter);

        final PPath background = new PPath(new RoundRectangle2D.Double(
                    0,
                    0,
                    totalWidth
                            + 20,
                    totalHeight
                            + 10,
                    10,
                    10));
        background.setPaint(COLOR_BACKGROUND);

        for (final PNode nodeToAdd : nodesToAdd) {
            background.addChild(nodeToAdd);
        }

        setTransparency(0.85f);
        addChild(background);
    }
}
