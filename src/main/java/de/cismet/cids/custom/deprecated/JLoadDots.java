/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.deprecated;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Der JLoadSpinner erzeugt einen zeitlich unbestimmten Ladeanzeiger im Mac-Stil. Wird z.B. in Renderern beim Laden
 * eines Kartenausschnitts verwendet.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class JLoadDots extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final int ALL_COUNT = 16;
    private static final int DOT_COUNT = 7;
    private static final int RADIUS = 2;
    private static final int ANIM_DUR = 600;
    private static final float ALPHA = 1.0f;
    private static final double ANGLE = Math.toRadians(360.0d / ALL_COUNT);
    private static final Point[] dots = new Point[DOT_COUNT];
    private static final Color HL_COLOR1 = Color.BLACK;
    private static final Color HL_COLOR2 = new Color(40, 40, 40);
    private static final Color HL_COLOR3 = new Color(80, 80, 80);

    //~ Instance fields --------------------------------------------------------

    private int lastHeight;
    private int lastWidth;
    private int counter;
    private Point mid;
    private Timer rotationTimer;
    private BufferedImage cacheImage;

    // Animation-Parameter
    private float alpha;
    private Timer fadeTimer;
    private long animStartTime;

    //~ Constructors -----------------------------------------------------------

    /**
     * Erzeugt einen neuen JLoadSpinner.
     */
    public JLoadDots() {
        setOpaque(false);
        lastHeight = 0;
        lastWidth = 0;
        counter = 0;
        alpha = ALPHA;
        mid = null;
        cacheImage = null;
        // Timer fuer drehende Punkte initialisieren und starten
        rotationTimer = new Timer(50, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        counter++;
                        if (counter > (ALL_COUNT - 1)) {
                            counter = 0;
                        }
                        repaint(0, 0, getWidth(), getHeight());
                    }
                });
        rotationTimer.setCoalesce(false);
        rotationTimer.start();

        // Timer zum Ausfaden initialisieren
        fadeTimer = new Timer(25, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final long currentTime = System.nanoTime() / 1000000;
                        final long totalTime = currentTime - animStartTime;
                        if (totalTime > ANIM_DUR) {
                            animStartTime = currentTime;
                        }
                        final float fraction = (float)totalTime / ANIM_DUR;
                        alpha = Math.min(ALPHA, Math.abs(1 - fraction));
                        if (alpha <= 0.1f) {
                            setVisible(false);
                        }
                        repaint(0, 0, getWidth(), getHeight());
                    }
                });
        fadeTimer.setCoalesce(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Ueberschriebene Zeichenmethode des JPanels.
     *
     * @param  g  Graphics-Objekt auf dem gezeichnet wird
     */
    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D)g;
        // CacheImage erstellen, das spaeter nur noch gedreht wird
        if ((cacheImage == null) || ((lastHeight != getHeight()) || (lastWidth != getWidth()))) {
            lastHeight = getHeight();
            lastWidth = getWidth();
            mid = new Point(lastWidth / 2, lastHeight / 2);
            cacheImage = new BufferedImage(lastWidth, lastHeight, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D cg = cacheImage.createGraphics();
            cg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            cg.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            // Verhaeltnis Breite/Hoehe pruefen
            if (lastWidth > lastHeight) {
                createPoints(lastHeight / 2);
            } else {
                createPoints(lastWidth / 2);
            }
            // Punkte zeichnen
            cg.setColor(HL_COLOR3);
            cg.fillOval(dots[0].x - (RADIUS / 2), dots[0].y - (RADIUS / 2), RADIUS, RADIUS);
            cg.fillOval(dots[1].x - RADIUS, dots[1].y - RADIUS, 2 * RADIUS, 2 * RADIUS);
            cg.setColor(HL_COLOR2);
            cg.fillOval(dots[2].x - (3 * RADIUS / 2), dots[2].y - (3 * RADIUS / 2), 3 * RADIUS, 3 * RADIUS);
            cg.fillOval(dots[3].x - (2 * RADIUS), dots[3].y - (2 * RADIUS), 4 * RADIUS, 4 * RADIUS);
            cg.setColor(HL_COLOR1);
            cg.fillOval(dots[4].x - (5 * RADIUS / 2), dots[4].y - (5 * RADIUS / 2), 5 * RADIUS, 5 * RADIUS);
            cg.fillOval(dots[5].x - (3 * RADIUS), dots[5].y - (3 * RADIUS), 6 * RADIUS, 6 * RADIUS);
            cg.fillOval(dots[6].x - (3 * RADIUS), dots[6].y - (3 * RADIUS), 6 * RADIUS, 6 * RADIUS);
            cg.dispose();
        }
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2d.rotate(counter * ANGLE, mid.getX(), mid.getY());
        g2d.drawImage(cacheImage, 0, 0, null);
        g2d.dispose();
    }

    /**
     * Methode zum Erstellen der einzelnen Punkte auf beiden Kreisen. Die jeweiligen Punkte des inneren und des
     * aeusseren Kreises werden in der paint-Methode miteinander verbunden und bilden den Linienkreis.
     *
     * @param  radius  der Radius des Kreises
     */
    private void createPoints(final int radius) {
        final double r = radius * 3 / 4;
        for (int i = 0; i < DOT_COUNT; i++) {
            dots[i] = new Point(new Double(mid.x + (r * Math.cos(i * ANGLE))).intValue(),
                    new Double(mid.y + (r * Math.sin(i * ANGLE))).intValue());
        }
    }

    /**
     * Ueberschriebene setVisible-Methode des JPanels. Wird der JLoadSpinner versteckt so stoppt der Timer. Dieser wird
     * wieder gestartet, sobald der JLoadSpinner wieder angezeigt wird.
     *
     * @param  aFlag  boolean-Parameter ob der JLoadSpinner sichtbar gesetzt wird oder nicht
     */
    @Override
    public void setVisible(final boolean aFlag) {
        if (aFlag == false) {
            if (fadeTimer.isRunning()) {
                fadeTimer.stop();
                rotationTimer.stop();
                super.setVisible(aFlag);
            } else {
                animStartTime = System.nanoTime() / 1000000;
                fadeTimer.start();
            }
        } else {
            if (!rotationTimer.isRunning()) {
                rotationTimer.start();
            }
            alpha = ALPHA;
            super.setVisible(aFlag);
        }
    }
}
