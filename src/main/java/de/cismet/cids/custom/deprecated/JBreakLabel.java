/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * JBreakLabel.java
 *
 * Created on 7. November 2007, 12:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.deprecated;

import javax.swing.JLabel;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class JBreakLabel extends JLabel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String OPEN_TAG = "<html>";
    public static final String END_TAG = "</html>";
    public static final String BREAK = "<br>";
    public static final String DIV = "-";

    //~ Instance fields --------------------------------------------------------

    private boolean strict;
    private int lim;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of JBreakLabel.
     */
    public JBreakLabel() {
        this("", 50, true);
    }

    /**
     * Creates a new JBreakLabel object.
     *
     * @param  lim     DOCUMENT ME!
     * @param  strict  DOCUMENT ME!
     */
    public JBreakLabel(final int lim, final boolean strict) {
        this("", lim, strict);
    }

    /**
     * Creates a new JBreakLabel object.
     *
     * @param  text    DOCUMENT ME!
     * @param  lim     DOCUMENT ME!
     * @param  strict  DOCUMENT ME!
     */
    public JBreakLabel(final String text, final int lim, final boolean strict) {
        super();
        super.setText(process(text, lim, strict));
        this.lim = lim;
        this.strict = strict;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   s       DOCUMENT ME!
     * @param   lim     DOCUMENT ME!
     * @param   strict  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String process(final String s, final int lim, final boolean strict) {
        if (s.length() > lim) {
            final String[] subs = s.split("\\s");
            final StringBuffer sb = new StringBuffer();
            int lastbreak = 0;
            for (String act : subs) {
                if (((sb.length() - lastbreak + act.length()) <= lim) || (sb.length() == 0)) {
                    sb.append(" ");
                    sb.append(act);
                } else if ((act.length() <= lim) || !strict) {
                    sb.append(BREAK);
                    lastbreak = sb.length();
                    if (act.substring(1, 1).matches("\\s")) {
                        act = act.substring(1, act.length());
                    }
                    sb.append(act);
                } else {
                    int part = 0;
                    while (act.length() >= (part + lim - 1)) {
                        sb.append(BREAK);
                        sb.append(act.substring(part, part = part + lim - 1));
                        sb.append(DIV);
                    }
                    sb.append(BREAK);
                    lastbreak = sb.length();
                    sb.append(act.substring(part));
                }
            }
            sb.deleteCharAt(0);
            sb.insert(0, OPEN_TAG);
            sb.append(END_TAG);
            return sb.toString();
        }
        return s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String process(final String s) {
        return process(s, this.lim, this.strict);
    }

    @Override
    public void setText(final String text) {
        super.setText(process(text, lim, strict));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lim  DOCUMENT ME!
     */
    public void setLim(final int lim) {
        this.lim = lim;
        super.setText(process(super.getText()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strict  DOCUMENT ME!
     */
    public void setStrict(final boolean strict) {
        this.strict = strict;
        super.setText(process(super.getText()));
    }
}
