/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import org.apache.log4j.Logger;

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
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AlkisPointSearchTooltip extends PNode {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisPointSearchTooltip.class);

    private static final Color COLOR_BACKGROUND = new Color(255, 255, 222, 200);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MetaSearchTooltip object.
     *
     * @param  icoAlkisPoint  searchTopics DOCUMENT ME!
     */
    public AlkisPointSearchTooltip(final ImageIcon icoAlkisPoint) {
        double leftMargin = 5;
        double offsetX = leftMargin;
        double offsetY = 5;
        double totalWidth = offsetX;
        double totalHeight = offsetY;

        final Collection<PNode> nodesToAdd = new LinkedList<PNode>();

        if ((icoAlkisPoint != null) && (icoAlkisPoint.getImage() != null)) {
            final PImage imgHeader = new PImage(icoAlkisPoint.getImage());
            imgHeader.setOffset(offsetX, offsetY);

            offsetX += icoAlkisPoint.getIconWidth() + 5;
            leftMargin = offsetX;
            totalWidth = offsetX;
            totalHeight += imgHeader.getHeight();

            nodesToAdd.add(imgHeader);
        }

        final PText lblHeader = new PText(NbBundle.getMessage(
                    AlkisPointSearchTooltip.class,
                    "AlkisPointSearchTooltip.AlkisPointSearchTooltip(ImageIcon).lblHeader"));
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
                    AlkisPointSearchTooltip.class,
                    "AlkisPointSearchTooltip.AlkisPointSearchTooltip(ImageIcon).lblFilter"));
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
