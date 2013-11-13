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
package de.cismet.cids.custom.wupp.client.alkis;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractInputField extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_DISTRICT_NAME = "districtName";     // NOI18N
    public static final String PROP_DISTRICT_NUMBER = "districtNumber"; // NOI18N

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum BlockType {

        //~ Enum constants -----------------------------------------------------

        DISTRICT, PARCEL, PARCEL_NUMERATOR, PARCEL_DENOMINATOR, BUCHUNGSBLATTNUMMER
    }

    //~ Instance fields --------------------------------------------------------

    boolean changeFocus = true;
    boolean writeOver = true;

    // --- bindable
    private String districtNumber;
    // ---
    // --- read-only bindable
    private String districtName;
    private boolean validParcelNr = false;
    // ---
    private final AbstractInputFieldConfig config;
    private boolean overwritten = false;

    private JTextField txtDistrict;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractInputField object.
     *
     * @param  config  DOCUMENT ME!
     */
    public AbstractInputField(final AbstractInputFieldConfig config) {
        this.config = config;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AbstractInputFieldConfig getConfig() {
        return config;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean getValidParcelNr() {
        return validParcelNr;
    }

    /**
     * DOCUMENT ME!
     */
    abstract void updateResult();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDistrictName() {
        return districtName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDistrictNumber() {
        return districtNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  districtNumber  DOCUMENT ME!
     */
    public void setDistrictNumber(final String districtNumber) {
        this.districtNumber = districtNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  districtNumber  DOCUMENT ME!
     */
    public void setDistrictNumberInTxtDistrict(final String districtNumber) {
        changeFocus = false;
        writeOver = false;
        txtDistrict.setText(districtNumber);
        changeFocus = true;
        writeOver = true;
        finishDistrict();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  DOCUMENT ME!
     * @param  maxLen    DOCUMENT ME!
     * @param  offs      DOCUMENT ME!
     */
    void addLeadingZeroes(final Document document, final int maxLen, final int offs) {
        if (document.getLength() < maxLen) {
            for (int i = document.getLength(); i < maxLen; i++) {
                try {
                    document.insertString(offs, "0", null);
                } catch (final BadLocationException ex) {
                    Logger.getLogger(ParcelInputField.class.getName())
                            .log(Level.INFO, "Leading zero could not be inserted", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  DOCUMENT ME!
     * @param  maxLen    DOCUMENT ME!
     */
    void addLeadingZeroes(final Document document, final int maxLen) {
        addLeadingZeroes(document, maxLen, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   districtAbrv  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getDistrictNrFromAbrv(final String districtAbrv) {
        return config.getConversionMap()
                    .get(districtAbrv.substring(0, Math.min(2, districtAbrv.length())).toLowerCase());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   districtAbrv  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean validDistrict(final String districtAbrv) {
        return getDistrictNrFromAbrv(districtAbrv) != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTextField getTxtDistrict() {
        return txtDistrict;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  txtDistrict  DOCUMENT ME!
     */
    public void setTxtDistrict(final JTextField txtDistrict) {
        this.txtDistrict = txtDistrict;
    }

    /**
     * DOCUMENT ME!
     */
    void finishDistrict() {
        String text = txtDistrict.getText();
        if (text.matches("^[öÖüÜäÄßa-zA-Z]+$")) {
            if (validDistrict(text)) {
                txtDistrict.setText(getDistrictNrFromAbrv(text).toString());
                text = txtDistrict.getText();
            } else {
                txtDistrict.setForeground(Color.red);
            }
        }
        if (text.matches("^[0-9]+$") || text.isEmpty()) {
            if (!text.matches("^05.*")) // Leading 05 in districtNr
            {
                txtDistrict.setText("05" + text);
            }
            addLeadingZeroes(txtDistrict.getDocument(), config.getMaxLenDistrictNumberField() + 2, 2);
        }
        int intArea;
        final String oldAreaName = districtName;
        try {
            if (txtDistrict.getText().length() > config.getMaxLenDistrictNumberField()) {
                intArea = Integer.parseInt(txtDistrict.getText().substring(2));
            } else {
                intArea = Integer.parseInt(txtDistrict.getText().substring(0));
            }
            districtName = config.getDistrictNamesMap().get(intArea);
        } catch (final NumberFormatException e) {
            districtName = null;
            // dont care, simply unknownn district, maybe insert log
        }
        firePropertyChange(PROP_DISTRICT_NAME, oldAreaName, getDistrictName());
        if (getDistrictName() != null) {
            fireAreaBlockFinished(ParcelInputField.BlockType.DISTRICT);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  blockNr  DOCUMENT ME!
     */
    abstract void fireAreaBlockFinished(final BlockType blockNr);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOverwritten() {
        return overwritten;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  overwritten  DOCUMENT ME!
     */
    public void setOverwritten(final boolean overwritten) {
        this.overwritten = overwritten;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DistrictPlainDocument extends PlainDocument {

        //~ Instance fields ----------------------------------------------------

        private JTextField textField;
        private JTextField nextTextField;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DistrictPlainDocument object.
         *
         * @param  textField      DOCUMENT ME!
         * @param  nextTextField  DOCUMENT ME!
         */
        public DistrictPlainDocument(final JTextField textField, final JTextField nextTextField) {
            this.textField = textField;
            this.nextTextField = nextTextField;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertString(final int offs, String str, final AttributeSet a) throws BadLocationException {
            textField.setForeground(Color.BLACK);

            if (str == null) {
                return;
            }
            // removes all characters except "-_%", digits, alpha charactes and german vowel mutuations
            str = str.replaceAll("[^-\\u00F6\\u00D6\\u00FC\\u00DC\\u00E4\\u00C4\\u00dfa-zA-Z0-9_%]", "");
            String newStr = str;
            int maxLen = config.getMaxLenDistrictNumberField();
            if ((textField.getDocument().getText(0, textField.getDocument().getLength()) + str).matches(
                            "^05.*")) {
                maxLen += 2;
            }
            if ((str.length() > 0)
                        && ((str.length() > (maxLen - offs))
                            || str.substring(0, Math.min(maxLen + 1 - offs, str.length())).contains(
                                config.getDelimiter1AsString()))) {
                int pos = maxLen - offs;
                int posDel = str.indexOf(config.getDelimiter1());
                if ((posDel >= 0) && (posDel < pos)) {
                    pos = posDel;
                }

                while (((str.indexOf('_') >= 0) && (str.indexOf('_') < pos))
                            || ((str.indexOf('%') >= 0) && (str.indexOf('%') < pos))) {
                    str = str.replaceFirst("[_%]", "");
                    posDel = str.indexOf(config.getDelimiter1());
                    if ((posDel >= 0) && (posDel < pos)) {
                        pos = posDel;
                    }
                }
                if (changeFocus) {
                    nextTextField.requestFocusInWindow();
                }

                newStr = str.substring(0, pos);

                if (writeOver) {
                    String writeOverStr;
                    if (str.indexOf(config.getDelimiter1()) == pos) {
                        writeOverStr = str.substring(pos + 1);
                    } else {
                        writeOverStr = str.substring(pos);
                    }
                    if (!writeOverStr.isEmpty()) {
                        overwritten = true;
                        nextTextField.setText(writeOverStr);
                    }
                }
            } else {
                newStr = newStr.replaceFirst("[_%]", "");
            }
            if (!newStr.isEmpty()) {
                if (textField.getDocument().getLength() == 0) {
                    if (Character.isDigit(newStr.charAt(0))) {
                        newStr = newStr.replaceAll("[^0-9]", "");
                    } else if (Character.isLetter(newStr.charAt(0))) {
                        // removes all characters except alpha charactes and german vowel mutuations
                        newStr = newStr.replaceAll(
                                "[^\\u00F6\\u00D6\\u00FC\\u00DC\\u00E4\\u00C4\\u00dfa-zA-Z]",
                                "");
                        newStr = newStr.substring(0, Math.min(Math.max(2 - offs, 0), newStr.length()));
                        if (validDistrict(
                                        textField.getDocument().getText(
                                            0,
                                            textField.getDocument().getLength())
                                        + newStr)) {
                            if (changeFocus && ((offs + newStr.length()) >= 2)) {
                                nextTextField.requestFocusInWindow();
                            }
                        } else {
                            textField.setForeground(Color.red);
                        }
                    }
                } else if (textField.getDocument().getText(0, textField.getDocument().getLength()).matches(
                                "^[0-9]*")) {
                    newStr = newStr.replaceAll("[^0-9]", ""); // checks if the entered text only contains
                    // alpha charactes and german vowel mutuations
                } else if (textField.getDocument().getText(0, textField.getDocument().getLength()).matches(
                                "^[\\u00F6\\u00D6\\u00FC\\u00DC\\u00E4\\u00C4\\u00dfa-zA-Z]*")) {
                    // removes all characters except alpha charactes and german vowel mutuations
                    newStr = newStr.replaceAll(
                            "[^\\u00F6\\u00D6\\u00FC\\u00DC\\u00E4\\u00C4\\u00dfa-zA-Z]",
                            "");
                    newStr = newStr.substring(0, Math.min(Math.max(2 - offs, 0), newStr.length()));

                    if (validDistrict(
                                    textField.getDocument().getText(0, textField.getDocument().getLength())
                                    + newStr)) {
                        if (changeFocus && ((offs + newStr.length()) >= 2)) {
                            nextTextField.requestFocusInWindow();
                        }
                    } else {
                        textField.setForeground(Color.red);
                    }
                }
            }
            super.insertString(offs, newStr, a);
            updateResult();
        }

        @Override
        public void remove(final int offs, final int len) throws BadLocationException {
            textField.setForeground(Color.BLACK);
            super.remove(offs, len);
        }
    }
}
