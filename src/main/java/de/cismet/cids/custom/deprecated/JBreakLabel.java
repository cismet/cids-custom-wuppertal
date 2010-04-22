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
 *
 * @author srichter
 */
public class JBreakLabel extends JLabel {
    public static final String OPEN_TAG = "<html>";
    public static final String END_TAG = "</html>";
    public static final String BREAK = "<br>";
    public static final String DIV = "-";
    private boolean strict;
    private int lim;

    /** Creates a new instance of JBreakLabel */
    public JBreakLabel() {
        this("", 50, true);
    }

    public JBreakLabel(int lim, boolean strict) {
        this("", lim, strict);
    }

    public JBreakLabel(String text, int lim, boolean strict) {
        super();
        super.setText(process(text, lim, strict));
        this.lim = lim;
        this.strict = strict;
    }

    private String process(String s, int lim, boolean strict) {
        if (s.length() > lim) {
            String[] subs = s.split("\\s");
            StringBuffer sb = new StringBuffer();
            int lastbreak = 0;
            for (String act : subs) {
                if (((sb.length() - lastbreak + act.length()) <= lim) || sb.length() == 0) {
                    sb.append(" ");
                    sb.append(act);
                } else if (act.length() <= lim || !strict) {
                    sb.append(BREAK);
                    lastbreak = sb.length();
                    if (act.substring(1, 1).matches("\\s")) {
                        act = act.substring(1, act.length());
                    }
                    sb.append(act);
                } else {
                    int part = 0;
                    while (act.length() >= part + lim - 1) {
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

    private String process(String s) {
        return process(s, this.lim, this.strict);
    }

    @Override
    public void setText(String text) {
        super.setText(process(text, lim, strict));
    }

    public void setLim(int lim) {
        this.lim = lim;
        super.setText(process(super.getText()));
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
        super.setText(process(super.getText()));
    }
}
