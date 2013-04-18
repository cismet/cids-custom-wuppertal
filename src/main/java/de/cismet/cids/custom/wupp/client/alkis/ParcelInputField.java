/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wupp.client.alkis;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * @author mroncoroni
 */
public class ParcelInputField extends javax.swing.JPanel {

    public static final String PROP_CURRENT_PARCEL = "currentParcel"; // NOI18N
    public static final String PROP_DISTRICT_NUMBER = "districtNumber";// NOI18N
    public static final String PROP_PARCEL_NUMBER = "parcelNumber";// NOI18N
    public static final String PROP_PARCEL_NUMERATOR = "parcelNumerator";// NOI18N
    public static final String PROP_PARCEL_DENOMINATOR = "parcelDenominator";// NOI18N
    public static final String PROP_DISTRICT_NAME = "districtName";// NOI18N
    public static final String PROP_VALID_PARCEL_NUMBER = "validParcelNr";// NOI18N
    // --- read-only bindable
    private String currentParcel;
    private String districtNumber;
    private String parcelNumber;
    private String parcelNumerator;
    private String parcelDenominator;
    private String districtName;
    private boolean validParcelNr = false;
    // ---
    private final InputConfig config;
    private boolean overwritten = false;
    private boolean changeFocus = true;
    private boolean writeOver = true;

    public enum BlockType {

        DISTRICT,
        PARCEL,
        PARCEL_NUMERATOR,
        PARCEL_DENOMINATOR
    }

    // Wird vom Designer benötigt, nicht benutzen!
    public ParcelInputField() {
        this(new InputConfig());
    }

    /**
     * Creates new form Flurstueckseingabe
     */
    public ParcelInputField(final InputConfig config) {
        this.config = config;
        initComponents();

        txtDistrict.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                txtDistrict.setForeground(Color.BLACK);

                if (str == null) {
                    return;
                }
                str = str.replaceAll("[^-öÖüÜäÄßa-zA-Z0-9_%]", "");
                String newStr = str;
                int maxLen = config.getMaxLenTxtArea();
                if ((txtDistrict.getDocument().getText(0, txtDistrict.getDocument().getLength()) + str).matches("^05.*")) {
                    maxLen += 2;
                }
                if (str.length() > 0 && (str.length() > maxLen - offs || str.substring(0, Math.min(maxLen + 1 - offs, str.length())).contains(config.getDelimiter1String()))) {
                    int pos = maxLen - offs;
                    int posDel = str.indexOf(config.getDelimiter1());
                    if (posDel >= 0 && posDel < pos) {
                        pos = posDel;
                    }

                    while ((str.indexOf('_') >= 0 && str.indexOf('_') < pos) || (str.indexOf('%') >= 0 && str.indexOf('%') < pos)) {
                        str = str.replaceFirst("[_%]", "");
                        posDel = str.indexOf(config.getDelimiter1());
                        if (posDel >= 0 && posDel < pos) {
                            pos = posDel;
                        }
                    }
                    if (changeFocus) {
                        txtParcel.requestFocusInWindow();
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
                            txtParcel.setText(writeOverStr);
                        }
                    }
                } else {
                    newStr = newStr.replaceFirst("[_%]", "");
                }
                if (!newStr.isEmpty()) {
                    if (txtDistrict.getDocument().getLength() == 0) {
                        if (Character.isDigit(newStr.charAt(0))) {
                            newStr = newStr.replaceAll("[^0-9]", "");
                        } else if (Character.isLetter(newStr.charAt(0))) {
                            newStr = newStr.replaceAll("[^öÖüÜäÄßa-zA-Z0-9]", "").substring(0, Math.min(Math.max(2 - offs, 0), newStr.length()));
                            if (validDistrict(txtDistrict.getDocument().getText(0, txtDistrict.getDocument().getLength()) + newStr)) {
                                if (changeFocus && offs + newStr.length() >= 2) {
                                    txtParcel.requestFocusInWindow();
                                }
                            } else {
                                txtDistrict.setForeground(Color.red);
                            }

                        }
                    } else if (txtDistrict.getDocument().getText(0, txtDistrict.getDocument().getLength()).matches("^[0-9]*")) {
                        newStr = newStr.replaceAll("[^0-9]", "");
                    } else if (txtDistrict.getDocument().getText(0, txtDistrict.getDocument().getLength()).matches("^[Ã¶Ã–Ã¼ÃœÃ¤Ã„ÃŸa-zA-Z]*")) {
                        newStr = newStr.replaceAll("[^öÖüÜäÄßa-zA-Z0-9]", "").substring(0, Math.min(Math.max(2 - offs, 0), newStr.length()));

                        if (validDistrict(txtDistrict.getDocument().getText(0, txtDistrict.getDocument().getLength()) + newStr)) {
                            if (changeFocus && offs + newStr.length() >= 2) {
                                txtParcel.requestFocusInWindow();
                            }
                        } else {
                            txtDistrict.setForeground(Color.red);
                        }

                    }
                }
                super.insertString(offs, newStr, a);
                updateParcel();
            }

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                txtDistrict.setForeground(Color.BLACK);
                super.remove(offs, len);
            }
        });

        txtParcel.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

                if (str == null) {
                    return;
                }
                str = str.replaceAll("[^-0-9_%]", "");
                String newStr = str;
                if (str.length() > 0 && (str.length() > config.getMaxLenTxtLand() - offs || str.substring(0, Math.min(config.getMaxLenTxtLand() + 1 - offs, str.length())).contains(config.getDelimiter1String()))) {
                    int pos = str.indexOf(config.getDelimiter1());
                    if (pos < 0 || pos > config.getMaxLenTxtLand() - offs) {
                        pos = config.getMaxLenTxtLand() - offs;
                    }
                    if (changeFocus) {
                        txtLandParcelNumerator.requestFocusInWindow();
                    }

                    newStr = str.substring(0, pos);

                    if (writeOver) {
                        String writeOverStr;
                        if (str.indexOf(config.getDelimiter1String()) == pos) {
                            writeOverStr = str.substring(pos + 1);
                        } else {
                            writeOverStr = str.substring(pos);
                        }
                        if (!writeOverStr.isEmpty()) {
                            overwritten = true;
                            txtLandParcelNumerator.setText(writeOverStr);
                        }
                    }

                }
                super.insertString(offs, newStr, a);
                updateParcel();
            }
        });

        txtLandParcelNumerator.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

                if (str == null) {
                    return;
                }
                str = str.replaceAll("[^-0-9_%]", "");
                String newStr = str;
                if (str.length() > 0
                        && (str.length() > config.getMaxLenTxtLandParcelNumerator() - offs
                        || str.substring(0, Math.min(config.getMaxLenTxtLandParcelNumerator() + 1 - offs, str.length())).contains(config.getDelimiter1String())
                        || str.substring(0, Math.min(config.getMaxLenTxtLandParcelNumerator() + 1 - offs, str.length())).contains(config.getDelimiter2String()))) {
                    overwritten = true;
                    int pos = str.indexOf(config.getDelimiter1());
                    if (pos < 0 || (pos > str.indexOf(config.getDelimiter2()) && str.indexOf(config.getDelimiter2()) >= 0)) {
                        pos = str.indexOf(config.getDelimiter2());
                    }
                    if (pos < 0 || (pos > config.getMaxLenTxtLandParcelNumerator() - offs && config.getMaxLenTxtLandParcelNumerator() - offs >= 0)) {
                        pos = config.getMaxLenTxtLandParcelNumerator() - offs;
                    }
                    if (changeFocus) {
                        txtLandParcelDenominator.requestFocusInWindow();
                    }
                    newStr = str.substring(0, pos);

                    if (writeOver) {
                        String writeOverStr;
                        if (str.indexOf(config.getDelimiter1String()) == pos
                                || str.indexOf(config.getDelimiter2String()) == pos) {
                            writeOverStr = str.substring(pos + 1);
                        } else {
                            writeOverStr = str.substring(pos);
                        }
                        if (!writeOverStr.isEmpty()) {
                            overwritten = true;
                            txtLandParcelDenominator.setText(writeOverStr);
                        }
                    }

                }
                super.insertString(offs, newStr, a);
                updateParcel();
            }
        });

        txtLandParcelDenominator.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null || str.isEmpty()) {
                    return;
                }
                str = str.replaceAll("[^0-9_%]", "");
                if (offs + str.length() > config.getMaxLenTxtLandParcelDenominator()) {
                    str = str.substring(0, config.getMaxLenTxtLandParcelDenominator() - offs);
                }
                super.insertString(offs, str, a);
                updateParcel();
            }
        });
    }

    public boolean getValidParcelNr() {
        return validParcelNr;
    }

    private void checkValidParcelNr() {
        String parts[] = currentParcel.split("[-/]");
        if (parts.length < 3 || parts.length > 4) {
            validParcelNr = false;
            return;
        }
        if (parts[0].matches("^05")) {
            if (!parts[0].matches("^05[öÖüÜäÄßa-zA-Z0-9]{" + config.getMaxLenTxtArea() + "}$")) {
                validParcelNr = false;
                return;
            }
        } else {
            if (!parts[0].matches("^[öÖüÜäÄßa-zA-Z0-9]{" + config.getMaxLenTxtArea() + "}$")) {
                validParcelNr = false;
                return;
            }
        }
        if (parts[1].contains("%")) {
            if (!parts[1].matches("^[0-9_%]{1," + config.getMaxLenTxtLand() + "}$")) {
                validParcelNr = false;
                return;
            }
        } else {
            if (!parts[1].matches("^[0-9_]{" + config.getMaxLenTxtLand() + "}$")) {
                validParcelNr = false;
                return;
            }
        }
        if (parts[2].contains("%")) {
            if (!parts[2].matches("^[0-9_%]{1," + config.getMaxLenTxtLandParcelNumerator() + "}$")) {
                validParcelNr = false;
                return;
            }
        } else {
            if (!parts[2].matches("^[0-9_]{" + config.getMaxLenTxtLandParcelNumerator() + "}$")) {
                validParcelNr = false;
                return;
            }
        }
        if (parts.length == 4) {
            if (parts[3].contains("%")) {
                if (!parts[3].matches("^[0-9_%]{1," + config.getMaxLenTxtLandParcelDenominator() + "}$")) {
                    validParcelNr = false;
                    return;
                }
            } else {
                if (!parts[3].matches("^[0-9_]{" + config.getMaxLenTxtLandParcelDenominator() + "}$")) {
                    validParcelNr = false;
                    return;
                }
            }
        }
        validParcelNr = true;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getDistrictNumber() {
        return districtNumber;
    }

    public void setDistrictNumber(String districtNumber) {
        changeFocus = false;
        writeOver = false;
        txtDistrict.setText(districtNumber);
        changeFocus = true;
        writeOver = true;
        finishDistrict();
    }

    public String getCurrentParcel() {
        return currentParcel;
    }

    public void setCurrentParcel(String currentParcel) {
        changeFocus = false;
        txtDistrict.setText(currentParcel);
        finishDistrict();
        finishParcel();
        finishParcelNumerator();
        finishParcelDenominator();
        changeFocus = true;
    }

    public String getParcelDenominator() {
        return parcelDenominator;
    }

    public void setParcelDenominator(String parcelDenominator) {
        changeFocus = false;
        writeOver = false;
        txtLandParcelDenominator.setText(parcelDenominator);
        finishParcelDenominator();
        changeFocus = true;
        writeOver = true;
    }

    public String getParcelNumber() {
        return parcelNumber;
    }

    public void setParcelNumber(String parcelNumber) {
        changeFocus = false;
        writeOver = false;
        txtParcel.setText(parcelNumber);
        finishParcel();
        changeFocus = true;
        writeOver = true;
    }

    public String getParcelNumerator() {
        return parcelNumerator;
    }

    public void setParcelNumerator(String parcelNumerator) {
        changeFocus = false;
        writeOver = false;
        txtLandParcelNumerator.setText(parcelNumerator);
        finishParcelNumerator();
        changeFocus = true;
        writeOver = true;
    }

    private void addLeadingZeroes(Document document, int maxLen, int offs) {
        if (document.getLength() < maxLen) {
            for (int i = document.getLength(); i < maxLen; i++) {
                try {
                    document.insertString(offs, "0", null);
                } catch (final BadLocationException ex) {
                    Logger.getLogger(ParcelInputField.class.getName()).log(Level.INFO, "Leading zero could not be inserted", ex);
                }
            }
        }
    }

    private void addLeadingZeroes(Document document, int maxLen) {
        addLeadingZeroes(document, maxLen, 0);
    }

    private void updateParcel() {
        String oldParcel = currentParcel;
        StringBuilder sb = new StringBuilder();

        if (txtDistrict.getText() != null && !txtDistrict.getText().isEmpty()) {
            sb.append(txtDistrict.getText());
        }
        if (txtParcel.getText() != null && !txtParcel.getText().isEmpty()) {
            sb.append(config.getDelimiter1String()).append(txtParcel.getText());
        }
        if (txtLandParcelNumerator.getText() != null && !txtLandParcelNumerator.getText().isEmpty()) {
            sb.append(config.getDelimiter1String()).append(txtLandParcelNumerator.getText());
        }
        if (txtLandParcelDenominator.getText() != null && !txtLandParcelDenominator.getText().isEmpty()) {
            sb.append(config.getDelimiter2String()).append(txtLandParcelDenominator.getText());
        }

        currentParcel = sb.toString();
        super.firePropertyChange(PROP_CURRENT_PARCEL, oldParcel, currentParcel);
        boolean oldValid = validParcelNr;
        checkValidParcelNr();
        if (oldValid != validParcelNr) {
            super.firePropertyChange(PROP_VALID_PARCEL_NUMBER, oldValid, validParcelNr);
        }
    }

    private void fireAreaBlockFinished(BlockType blockNr) {
        String oldValue;
        switch (blockNr) {
            case DISTRICT:
                oldValue = districtNumber;
                districtNumber = txtDistrict.getText();
                super.firePropertyChange(PROP_DISTRICT_NUMBER, oldValue, districtNumber);
                break;
            case PARCEL:
                oldValue = parcelNumber;
                parcelNumber = txtParcel.getText();
                super.firePropertyChange(PROP_PARCEL_NUMBER, oldValue, parcelNumber);
                break;
            case PARCEL_NUMERATOR:
                oldValue = parcelNumerator;
                parcelNumerator = txtLandParcelNumerator.getText();
                super.firePropertyChange(PROP_PARCEL_NUMERATOR, oldValue, parcelNumerator);
                break;
            case PARCEL_DENOMINATOR:
                oldValue = parcelDenominator;
                parcelDenominator = txtLandParcelDenominator.getText();
                super.firePropertyChange(PROP_PARCEL_DENOMINATOR, oldValue, parcelDenominator);
                break;
        }
    }

    private Integer getDistrictNrFromAbrv(String districtAbrv) {
        return config.getConversionMap().get(districtAbrv.substring(0, Math.min(2, districtAbrv.length())).toLowerCase());
    }

    private boolean validDistrict(String districtAbrv) {
        return getDistrictNrFromAbrv(districtAbrv) != null;
    }

    private void finishDistrict() {
        String text = txtDistrict.getText();
        if (text.matches("^[öÖüÜäÄßa-zA-Z]+$")) {
            if (validDistrict(text)) {
                txtDistrict.setText(getDistrictNrFromAbrv(text).toString());
            } else {
                txtDistrict.setForeground(Color.red);
            }
        }
        if (text.matches("^[0-9]+$") || text.isEmpty()) {
            if (text.matches("^05.*")) {
                addLeadingZeroes(txtDistrict.getDocument(), config.getMaxLenTxtArea() + 2, 2);
            } else {
                addLeadingZeroes(txtDistrict.getDocument(), config.getMaxLenTxtArea());
            }
        }
        int intArea;
        final String oldAreaName = districtName;
        try {
            if(txtDistrict.getText().length() > config.getMaxLenTxtArea())
                intArea = Integer.parseInt(txtDistrict.getText().substring(2));
            else
                intArea = Integer.parseInt(txtDistrict.getText().substring(0));
            districtName = config.getAreaClearMap().get(intArea);
        } catch (final NumberFormatException e) {
            districtName = null;
            // dont care, simply unknownn district, maybe insert log
        }
        firePropertyChange(PROP_DISTRICT_NAME, oldAreaName, districtName);
        if (districtName != null) {
            fireAreaBlockFinished(BlockType.DISTRICT);
        }
    }

    private void finishParcel() {
        if (!txtParcel.getText().contains("%")) {
            addLeadingZeroes(txtParcel.getDocument(), config.getMaxLenTxtLand());
        }
        fireAreaBlockFinished(BlockType.PARCEL);
    }

    private void finishParcelNumerator() {
        if (!txtLandParcelNumerator.getText().contains("%")) {
            addLeadingZeroes(txtLandParcelNumerator.getDocument(), config.getMaxLenTxtLandParcelNumerator());
        }
        fireAreaBlockFinished(BlockType.PARCEL_NUMERATOR);
    }

    private void finishParcelDenominator() {
        String text = txtLandParcelDenominator.getText();
        if (text != null && !text.isEmpty() && !txtLandParcelDenominator.getText().contains("%")) {
            addLeadingZeroes(txtLandParcelDenominator.getDocument(), config.getMaxLenTxtLandParcelDenominator());
        }
        fireAreaBlockFinished(BlockType.PARCEL_DENOMINATOR);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtDistrict = new javax.swing.JTextField(config.getMaxLenTxtArea()+2);
        lblDelimitier1 = new javax.swing.JLabel(config.getDelimiter1String());
        txtParcel = new javax.swing.JTextField(config.getMaxLenTxtLand());
        lblDelimitier2 = new javax.swing.JLabel(config.getDelimiter1String());
        txtLandParcelNumerator = new javax.swing.JTextField(config.getMaxLenTxtLandParcelNumerator());
        lblDelimitier3 = new javax.swing.JLabel(config.getDelimiter2String());
        txtLandParcelDenominator = new javax.swing.JTextField(config.getMaxLenTxtLandParcelDenominator());

        setLayout(new java.awt.GridBagLayout());

        txtDistrict.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDistrict.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDistrictFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistrictFocusLost(evt);
            }
        });
        add(txtDistrict, new java.awt.GridBagConstraints());

        lblDelimitier1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(lblDelimitier1, gridBagConstraints);

        txtParcel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtParcel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtParcelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtParcelFocusLost(evt);
            }
        });
        add(txtParcel, new java.awt.GridBagConstraints());

        lblDelimitier2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(lblDelimitier2, gridBagConstraints);

        txtLandParcelNumerator.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLandParcelNumerator.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLandParcelNumeratorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtLandParcelNumeratorFocusLost(evt);
            }
        });
        add(txtLandParcelNumerator, new java.awt.GridBagConstraints());

        lblDelimitier3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(lblDelimitier3, gridBagConstraints);

        txtLandParcelDenominator.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLandParcelDenominator.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLandParcelDenominatorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtLandParcelDenominatorFocusLost(evt);
            }
        });
        add(txtLandParcelDenominator, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void txtDistrictFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDistrictFocusLost
        finishDistrict();
        txtDistrict.setCaretPosition(0);
    }//GEN-LAST:event_txtDistrictFocusLost

    private void txtParcelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParcelFocusLost
        finishParcel();
        txtParcel.setCaretPosition(0);
    }//GEN-LAST:event_txtParcelFocusLost

    private void txtLandParcelNumeratorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLandParcelNumeratorFocusLost
        finishParcelNumerator();
        txtLandParcelNumerator.setCaretPosition(0);
    }//GEN-LAST:event_txtLandParcelNumeratorFocusLost

    private void txtLandParcelDenominatorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLandParcelDenominatorFocusLost
        finishParcelDenominator();
        txtLandParcelDenominator.setCaretPosition(0);
    }//GEN-LAST:event_txtLandParcelDenominatorFocusLost

    private void txtLandParcelDenominatorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLandParcelDenominatorFocusGained
        final int textLenght = txtLandParcelDenominator.getText().length();
        if (overwritten) {
            overwritten = false;
            txtLandParcelDenominator.getHighlighter().removeAllHighlights();
            txtLandParcelDenominator.setCaretPosition(textLenght);
        } else {
            txtLandParcelDenominator.setCaretPosition(0);
            txtLandParcelDenominator.moveCaretPosition(textLenght);
        }
    }//GEN-LAST:event_txtLandParcelDenominatorFocusGained

    private void txtLandParcelNumeratorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLandParcelNumeratorFocusGained
        final int textLength = txtLandParcelNumerator.getText().length();
        if (overwritten) {
            overwritten = false;
            txtLandParcelNumerator.getHighlighter().removeAllHighlights();
            txtLandParcelNumerator.setCaretPosition(textLength);
        } else {
            txtLandParcelNumerator.setCaretPosition(0);
            txtLandParcelNumerator.moveCaretPosition(textLength);
        }
    }//GEN-LAST:event_txtLandParcelNumeratorFocusGained

    private void txtParcelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParcelFocusGained
        final int textLength = txtParcel.getText().length();
        if (overwritten) {
            overwritten = false;
            txtParcel.getHighlighter().removeAllHighlights();
            txtParcel.setCaretPosition(textLength);
        } else {
            txtParcel.setCaretPosition(0);
            txtParcel.moveCaretPosition(textLength);
        }
    }//GEN-LAST:event_txtParcelFocusGained

    private void txtDistrictFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDistrictFocusGained
        final int textLength = txtDistrict.getText().length();
        if (overwritten) {
            overwritten = false;
            txtDistrict.getHighlighter().removeAllHighlights();
            txtDistrict.setCaretPosition(textLength);
        } else {
            txtDistrict.setCaretPosition(0);
            txtDistrict.moveCaretPosition(textLength);
        }
    }//GEN-LAST:event_txtDistrictFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDelimitier1;
    private javax.swing.JLabel lblDelimitier2;
    private javax.swing.JLabel lblDelimitier3;
    private javax.swing.JTextField txtDistrict;
    private javax.swing.JTextField txtLandParcelDenominator;
    private javax.swing.JTextField txtLandParcelNumerator;
    private javax.swing.JTextField txtParcel;
    // End of variables declaration//GEN-END:variables
}
