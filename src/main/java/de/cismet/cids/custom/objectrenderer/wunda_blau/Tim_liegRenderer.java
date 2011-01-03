/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.Date;
import java.util.Locale;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.annotations.CidsRendererTitle;

import de.cismet.cids.custom.deprecated.CoolTabPanel;
import de.cismet.cids.custom.deprecated.JBreakLabel;
import de.cismet.cids.custom.deprecated.JLoadDots;
import de.cismet.cids.custom.deprecated.TabbedPaneUITransparent;

import de.cismet.cids.tools.metaobjectrenderer.CoolPanel;

/**
 * de.cismet.cids.objectrenderer.CoolTIMRenderer.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class Tim_liegRenderer extends CoolPanel implements ChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE = "TIM";
    private static final int ALKIS_INDEX = 1;
    private static final int KARTO_INDEX = 2;
    private static int lastSelected = 0;
    private static Date timer = new Date();

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Hinweise")
    public String hinweise = "";

    @CidsAttribute("angelegt von")
    public String angelegtVon = "";

    @CidsAttribute("angelegt am")
    public Timestamp angelegtAm;

    @CidsAttribute("gel\u00F6scht von")
    public String geloeschtVon = "";

    @CidsAttribute("gel\u00F6scht am")
    public Timestamp geloeschtAm;

    @CidsAttribute("Grund der L\u00F6schung")
    public String grund = "";

    // ALKIS
    @CidsAttribute("ALKIS")
    public Object alkis = null;

    @CidsAttribute("ALKIS.ALKIS relevant")
    public String alkRelevant = "";

    @CidsAttribute("ALKIS.ALKIS entschieden von")
    public String alkEntschiedenVon = "";

    @CidsAttribute("ALKIS.ALKIS entschieden am")
    public Timestamp alkEntschiedenAm = null;

    @CidsAttribute("ALKIS.Priorit\u00E4t")
    public String alkPrio = "";

    @CidsAttribute("ALKIS.Bemerkungen")
    public String alkBemerk = "";

    @CidsAttribute("ALKIS.Topographie")
    public String alkTopo = "";

    @CidsAttribute("ALKIS.Geb\u00E4ude")
    public String alkGeb = "";

    @CidsAttribute("ALKIS.Nutzungsarten")
    public String alkNutz = "";

    @CidsAttribute("ALKIS.Bodensch\u00E4tzung")
    public String alkBoden = "";

    @CidsAttribute("ALKIS.sonstiges")
    public String alkSonstiges = "";

    @CidsAttribute("ALKIS.Vermessung")
    public String alkVermessung = "";

    @CidsAttribute("ALKIS.Feldvergleich")
    public String alkFeldvergleich = "";

    @CidsAttribute("ALKIS.Luftbildauswertung")
    public String alkLuftbild = "";

    @CidsAttribute("ALKIS.Bachverlauf")
    public String alkBach = "";

    @CidsAttribute("ALKIS.sonstige")
    public String alkSonstige = "";

    @CidsAttribute("ALKIS.ALK relevant")
    public String alkALKRel = "";

    @CidsAttribute("ALKIS.ALK \u00FCbernommen von")
    public String alkALKUeberVon = "";

    @CidsAttribute("ALKIS.ALK \u00FCbernommen am")
    public Timestamp alkALKUeberAm = null;

    @CidsAttribute("ALKIS.ALB relevant")
    public String alkALBRel = "";

    @CidsAttribute("ALKIS.ALB \u00FCbernommen von")
    public String alkALBUeberVon = "";

    @CidsAttribute("ALKIS.ALB \u00FCbernommen am")
    public Timestamp alkALBUeberAm = null;

    @CidsAttribute("ALKIS.DGK relevant")
    public String alkDGKRel = "";

    @CidsAttribute("ALKIS.DGK abgeleitet von")
    public String alkDGKAbgelVon = "";

    @CidsAttribute("ALKIS.DGK abgeleitet am")
    public Timestamp alkDGKAbgelAm = null;

    // Kartographie
    @CidsAttribute("Katographie")
    public Object kartographie = null;

    @CidsAttribute("Katographie.Stadtkarte relevant")
    public String kartStadtRel = "";

    @CidsAttribute("Katographie.Stadtkarte entschieden von")
    public String kartStadtEntVon = "";

    @CidsAttribute("Katographie.Stadtkarte entschieden am")
    public Timestamp kartStadtEntAm = null;

    @CidsAttribute("Katographie.Stadtkarte Bemerkungen")
    public String kartStadtBem = "";

    @CidsAttribute("Katographie.Stadtkarte \u00FCbernommen von")
    public String kartStadtUeberVon = "";

    @CidsAttribute("Katographie.Stadtkarte \u00FCbernommen am")
    public Timestamp kartStadtUeberAm = null;

    @CidsAttribute("Katographie.Citypl\u00E4ne relevant")
    public String kartCityRel = "";

    @CidsAttribute("Katographie.Citypl\u00E4ne Bemerkungen")
    public String kartCityBem = "";

    @CidsAttribute("Katographie.Citypl\u00E4ne \u00FCbernommen von")
    public String kartCityUeberVon = "";

    @CidsAttribute("Katographie.Citypl\u00E4ne \u00FCbernommen am")
    public Timestamp kartCityUeberAm = null;

    @CidsAttribute("Katographie.\u00DCbersichtspl\u00E4ne relevant")
    public String kartUebersichtRel = "";

    @CidsAttribute("Katographie.\u00DCbersichtspl\u00E4ne Bemerkungen")
    public String kartUebersichtBem = "";

    @CidsAttribute("Katographie.\u00DCbersichtspl\u00E4ne \u00FCbernommen von")
    public String kartUebersichtUeberVon = "";

    @CidsAttribute("Katographie.\u00DCbersichtspl\u00E4ne \u00FCbernommen am")
    public Timestamp kartUebersichtUeberAm = null;

    @CidsAttribute("Katographie.Freizeitkarte relevant")
    public String kartFreizeitRel = "";

    @CidsAttribute("Katographie.Freizeitkarte Bemerkungen")
    public String kartFreizeitBem = "";

    @CidsAttribute("Katographie.Freizeitkarte \u00FCbernommen von")
    public String kartFreizeitUeberVon = "";

    @CidsAttribute("Katographie.Freizeitkarte \u00FCbernommen am")
    public Timestamp kartFreizeitUeberAm = null;

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geometry = null;

    @CidsRendererTitle
    public String title = "";

    private final Logger log = Logger.getLogger(this.getClass());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblAlkALBRel;
    private javax.swing.JLabel lblAlkALBUeberAm;
    private javax.swing.JLabel lblAlkALBUeberVon;
    private javax.swing.JLabel lblAlkALKRel;
    private javax.swing.JLabel lblAlkALKUeberAm;
    private javax.swing.JLabel lblAlkALKUeberVon;
    private javax.swing.JLabel lblAlkBach;
    private javax.swing.JLabel lblAlkBemerk;
    private javax.swing.JLabel lblAlkBoden;
    private javax.swing.JLabel lblAlkDGKAbgelAm;
    private javax.swing.JLabel lblAlkDGKAbgelVon;
    private javax.swing.JLabel lblAlkDGKRel;
    private javax.swing.JLabel lblAlkEntschiedenAm;
    private javax.swing.JLabel lblAlkEntschiedenVon;
    private javax.swing.JLabel lblAlkFeldvergleich;
    private javax.swing.JLabel lblAlkGeb;
    private javax.swing.JLabel lblAlkLuftbild;
    private javax.swing.JLabel lblAlkNutz;
    private javax.swing.JLabel lblAlkPrio;
    private javax.swing.JLabel lblAlkRelevant;
    private javax.swing.JLabel lblAlkSonstige;
    private javax.swing.JLabel lblAlkSonstiges;
    private javax.swing.JLabel lblAlkTopo;
    private javax.swing.JLabel lblAlkVermessung;
    private javax.swing.JLabel lblAngelAm;
    private javax.swing.JLabel lblAngelVon;
    private javax.swing.JLabel lblGelAm;
    private javax.swing.JLabel lblGelVon;
    private javax.swing.JLabel lblGrund;
    private javax.swing.JLabel lblHinweise;
    private javax.swing.JLabel lblKartCityBem;
    private javax.swing.JLabel lblKartCityRel;
    private javax.swing.JLabel lblKartCityUeberAm;
    private javax.swing.JLabel lblKartCityUeberVon;
    private javax.swing.JLabel lblKartFreiBem;
    private javax.swing.JLabel lblKartFreiRel;
    private javax.swing.JLabel lblKartFreiUeberAm;
    private javax.swing.JLabel lblKartFreiUeberVon;
    private javax.swing.JLabel lblKartStadtBem;
    private javax.swing.JLabel lblKartStadtEntschAm;
    private javax.swing.JLabel lblKartStadtEntschVon;
    private javax.swing.JLabel lblKartStadtRel;
    private javax.swing.JLabel lblKartStadtUeberAm;
    private javax.swing.JLabel lblKartStadtUeberVon;
    private javax.swing.JLabel lblKartUebersichtBem;
    private javax.swing.JLabel lblKartUebersichtRel;
    private javax.swing.JLabel lblKartUebersichtUeberAm;
    private javax.swing.JLabel lblKartUebersichtUeberVon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panAlkis;
    private javax.swing.JPanel panAllgemein;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panKarto;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTabAlkis;
    private javax.swing.JPanel panTabAllgemein;
    private javax.swing.JPanel panTabKarto;
    private javax.swing.JPanel panTitle;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolTIMRenderer.
     */
    public Tim_liegRenderer() {
        initComponents();
        setPanContent(panContent);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
        tabbedPane.addChangeListener(this);
        if ((new Date().getTime() - timer.getTime()) < (60 * 1000L)) {
            tabbedPane.setSelectedIndex(lastSelected);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assignSingle() {
        if (geometry != null) {
            setGeometry(geometry);
        }

        if (title != null) {
            if (title.length() > 50) {
                title = title.substring(0, 50);
                title = title + "...";
            }
            lblTitle.setText(TITLE + " - " + title);
        } else {
            lblTitle.setText(TITLE);
        }

        if (hinweise != null) {
            lblHinweise.setText(hinweise);
        } else {
            lblHinweise.setVisible(false);
            jLabel1.setVisible(false);
        }

        if (angelegtVon != null) {
            lblAngelVon.setText(angelegtVon);
        } else {
            jLabel2.setVisible(false);
            lblAngelVon.setVisible(false);
        }

        if (angelegtAm != null) {
            lblAngelAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(angelegtAm));
        } else {
            jLabel3.setVisible(false);
            lblAngelAm.setVisible(false);
        }

        if (geloeschtVon != null) {
            lblGelVon.setText(geloeschtVon);
        } else {
            jLabel4.setVisible(false);
            lblGelVon.setVisible(false);
        }

        if (geloeschtAm != null) {
            lblGelAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(geloeschtAm));
        } else {
            jLabel5.setVisible(false);
            lblGelAm.setVisible(false);
        }

        if (grund != null) {
            lblGrund.setText(grund);
        } else {
            jLabel6.setVisible(false);
            lblGrund.setVisible(false);
        }

        // ALKIS-Assign
        if (alkis != null) {
            if (alkRelevant != null) {
                lblAlkRelevant.setText(alkRelevant);
            } else {
                lblAlkRelevant.setVisible(false);
                jLabel9.setVisible(false);
            }
            if (alkEntschiedenVon != null) {
                lblAlkEntschiedenVon.setText(alkEntschiedenVon);
            } else {
                lblAlkEntschiedenVon.setVisible(false);
                jLabel10.setVisible(false);
            }
            if (alkEntschiedenAm != null) {
                lblAlkEntschiedenAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        alkEntschiedenAm));
            } else {
                lblAlkEntschiedenAm.setVisible(false);
                jLabel11.setVisible(false);
            }
            if (alkPrio != null) {
                lblAlkPrio.setText(alkPrio);
            } else {
                lblAlkPrio.setVisible(false);
                jLabel12.setVisible(false);
            }
            if (alkBemerk != null) {
                lblAlkBemerk.setText(alkBemerk);
            } else {
                lblAlkBemerk.setVisible(false);
                jLabel13.setVisible(false);
            }
            if (alkTopo != null) {
                lblAlkTopo.setText(alkTopo);
            } else {
                lblAlkTopo.setVisible(false);
                jLabel14.setVisible(false);
            }
            if (alkGeb != null) {
                lblAlkGeb.setText(alkGeb);
            } else {
                lblAlkGeb.setVisible(false);
                jLabel15.setVisible(false);
            }
            if (alkNutz != null) {
                lblAlkNutz.setText(alkNutz);
            } else {
                lblAlkNutz.setVisible(false);
                jLabel16.setVisible(false);
            }
            if (alkBoden != null) {
                lblAlkBoden.setText(alkBoden);
            } else {
                lblAlkBoden.setVisible(false);
                jLabel17.setVisible(false);
            }
            if (alkSonstiges != null) {
                lblAlkSonstiges.setText(alkSonstiges);
            } else {
                lblAlkSonstiges.setVisible(false);
                jLabel18.setVisible(false);
            }
            if (alkVermessung != null) {
                lblAlkVermessung.setText(alkVermessung);
            } else {
                lblAlkVermessung.setVisible(false);
                jLabel19.setVisible(false);
            }
            if (alkFeldvergleich != null) {
                lblAlkFeldvergleich.setText(alkFeldvergleich);
            } else {
                lblAlkFeldvergleich.setVisible(false);
                jLabel20.setVisible(false);
            }
            if (alkLuftbild != null) {
                lblAlkLuftbild.setText(alkLuftbild);
            } else {
                lblAlkLuftbild.setVisible(false);
                jLabel21.setVisible(false);
            }
            if (alkBach != null) {
                lblAlkBach.setText(alkBach);
            } else {
                lblAlkBach.setVisible(false);
                jLabel22.setVisible(false);
            }
            if (alkSonstige != null) {
                lblAlkSonstige.setText(alkSonstige);
            } else {
                lblAlkSonstige.setVisible(false);
                jLabel23.setVisible(false);
            }
            if (alkALKRel != null) {
                lblAlkALKRel.setText(alkALKRel);
            } else {
                lblAlkALKRel.setVisible(false);
                jLabel24.setVisible(false);
            }
            if (alkALKUeberVon != null) {
                lblAlkALKUeberVon.setText(alkALKUeberVon);
            } else {
                lblAlkALKUeberVon.setVisible(false);
                jLabel25.setVisible(false);
            }
            if (alkALKUeberAm != null) {
                lblAlkALKUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        alkALKUeberAm));
            } else {
                lblAlkALKUeberAm.setVisible(false);
                jLabel26.setVisible(false);
            }
            if (alkALBRel != null) {
                lblAlkALBRel.setText(alkALBRel);
            } else {
                lblAlkALBRel.setVisible(false);
                jLabel27.setVisible(false);
            }
            if (alkALBUeberVon != null) {
                lblAlkALBUeberVon.setText(alkALBUeberVon);
            } else {
                lblAlkALBUeberVon.setVisible(false);
                jLabel28.setVisible(false);
            }
            if (alkALBUeberAm != null) {
                lblAlkALBUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        alkALBUeberAm));
            } else {
                lblAlkALBUeberAm.setVisible(false);
                jLabel29.setVisible(false);
            }
            if (alkDGKRel != null) {
                lblAlkDGKRel.setText(alkDGKRel);
            } else {
                lblAlkDGKRel.setVisible(false);
                jLabel30.setVisible(false);
            }
            if (alkDGKAbgelVon != null) {
                lblAlkDGKAbgelVon.setText(alkDGKAbgelVon);
            } else {
                lblAlkDGKAbgelVon.setVisible(false);
                jLabel31.setVisible(false);
            }
            if (alkDGKAbgelAm != null) {
                lblAlkDGKAbgelAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        alkDGKAbgelAm));
            } else {
                lblAlkDGKAbgelAm.setVisible(false);
                jLabel32.setVisible(false);
            }
        } else {
            tabbedPane.removeTabAt(ALKIS_INDEX);
        }

        // Kartographie-Assign
        if (kartographie != null) {
            if (kartStadtRel != null) {
                lblKartStadtRel.setText(kartStadtRel);
            } else {
                lblKartStadtRel.setVisible(false);
                jLabel7.setVisible(false);
            }
            if (kartStadtEntVon != null) {
                lblKartStadtEntschVon.setText(kartStadtEntVon);
            } else {
                lblKartStadtEntschVon.setVisible(false);
                jLabel8.setVisible(false);
            }
            if (kartStadtEntAm != null) {
                lblKartStadtEntschAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        kartStadtEntAm));
            } else {
                lblKartStadtEntschAm.setVisible(false);
                jLabel33.setVisible(false);
            }
            if (kartStadtBem != null) {
                lblKartStadtBem.setText(kartStadtBem);
            } else {
                lblKartStadtBem.setVisible(false);
                jLabel34.setVisible(false);
            }
            if (kartStadtUeberVon != null) {
                lblKartStadtUeberVon.setText(kartStadtUeberVon);
            } else {
                lblKartStadtUeberVon.setVisible(false);
                jLabel35.setVisible(false);
            }
            if (kartStadtUeberAm != null) {
                lblKartStadtUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        kartStadtUeberAm));
            } else {
                lblKartStadtUeberAm.setVisible(false);
                jLabel36.setVisible(false);
            }
            if (kartCityRel != null) {
                lblKartCityRel.setText(kartCityRel);
            } else {
                lblKartCityRel.setVisible(false);
                jLabel37.setVisible(false);
            }
            if (kartCityBem != null) {
                lblKartCityBem.setText(kartCityBem);
            } else {
                lblKartCityBem.setVisible(false);
                jLabel38.setVisible(false);
            }
            if (kartCityUeberVon != null) {
                lblKartCityUeberVon.setText(kartCityUeberVon);
            } else {
                lblKartCityUeberVon.setVisible(false);
                jLabel39.setVisible(false);
            }
            if (kartCityUeberAm != null) {
                lblKartCityUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        kartCityUeberAm));
            } else {
                lblKartCityUeberAm.setVisible(false);
                jLabel40.setVisible(false);
            }
            if (kartUebersichtRel != null) {
                lblKartUebersichtRel.setText(kartUebersichtRel);
            } else {
                lblKartUebersichtRel.setVisible(false);
                jLabel41.setVisible(false);
            }
            if (kartUebersichtBem != null) {
                lblKartUebersichtBem.setText(kartUebersichtBem);
            } else {
                lblKartUebersichtBem.setVisible(false);
                jLabel42.setVisible(false);
            }
            if (kartUebersichtUeberVon != null) {
                lblKartUebersichtUeberVon.setText(kartUebersichtUeberVon);
            } else {
                lblKartUebersichtUeberVon.setVisible(false);
                jLabel43.setVisible(false);
            }
            if (kartUebersichtUeberAm != null) {
                lblKartUebersichtUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        kartUebersichtUeberAm));
            } else {
                lblKartUebersichtUeberAm.setVisible(false);
                jLabel44.setVisible(false);
            }
            if (kartFreizeitRel != null) {
                lblKartFreiRel.setText(kartFreizeitRel);
            } else {
                lblKartFreiRel.setVisible(false);
                jLabel45.setVisible(false);
            }
            if (kartFreizeitBem != null) {
                lblKartFreiBem.setText(kartFreizeitBem);
            } else {
                lblKartFreiBem.setVisible(false);
                jLabel46.setVisible(false);
            }
            if (kartFreizeitUeberVon != null) {
                lblKartFreiUeberVon.setText(kartFreizeitUeberVon);
            } else {
                lblKartFreiUeberVon.setVisible(false);
                jLabel47.setVisible(false);
            }
            if (kartFreizeitUeberAm != null) {
                lblKartFreiUeberAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                        kartFreizeitUeberAm));
            } else {
                lblKartFreiUeberAm.setVisible(false);
                jLabel48.setVisible(false);
            }
        } else if (alkis != null) {
            tabbedPane.removeTabAt(KARTO_INDEX);
        } else {
            tabbedPane.removeTabAt(KARTO_INDEX - 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panInter = new javax.swing.JPanel();
        panContent = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setUI(new TabbedPaneUITransparent());
        panTabAllgemein = new CoolTabPanel();
        panAllgemein = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblHinweise = new JBreakLabel();
        lblAngelVon = new javax.swing.JLabel();
        lblAngelAm = new javax.swing.JLabel();
        lblGelVon = new javax.swing.JLabel();
        lblGelAm = new javax.swing.JLabel();
        lblGrund = new JBreakLabel();
        panTabAlkis = new CoolTabPanel();
        panAlkis = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblAlkRelevant = new javax.swing.JLabel();
        lblAlkEntschiedenVon = new JBreakLabel();
        lblAlkEntschiedenAm = new JBreakLabel();
        lblAlkPrio = new JBreakLabel();
        lblAlkBemerk = new JBreakLabel();
        lblAlkTopo = new JBreakLabel();
        lblAlkGeb = new JBreakLabel();
        lblAlkNutz = new JBreakLabel();
        lblAlkBoden = new JBreakLabel();
        lblAlkSonstiges = new JBreakLabel();
        lblAlkVermessung = new JBreakLabel();
        lblAlkFeldvergleich = new JBreakLabel();
        lblAlkLuftbild = new JBreakLabel();
        lblAlkBach = new JBreakLabel();
        lblAlkSonstige = new JBreakLabel();
        lblAlkALKRel = new javax.swing.JLabel();
        lblAlkALKUeberVon = new JBreakLabel();
        lblAlkALKUeberAm = new JBreakLabel();
        lblAlkALBRel = new javax.swing.JLabel();
        lblAlkALBUeberVon = new JBreakLabel();
        lblAlkALBUeberAm = new JBreakLabel();
        lblAlkDGKRel = new javax.swing.JLabel();
        lblAlkDGKAbgelVon = new JBreakLabel();
        lblAlkDGKAbgelAm = new JBreakLabel();
        panTabKarto = new CoolTabPanel();
        panKarto = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        lblKartStadtRel = new JBreakLabel();
        lblKartStadtEntschVon = new JBreakLabel();
        lblKartStadtEntschAm = new JBreakLabel();
        lblKartStadtBem = new JBreakLabel();
        lblKartStadtUeberVon = new JBreakLabel();
        lblKartStadtUeberAm = new JBreakLabel();
        lblKartCityRel = new JBreakLabel();
        lblKartCityBem = new JBreakLabel();
        lblKartCityUeberVon = new JBreakLabel();
        lblKartCityUeberAm = new JBreakLabel();
        lblKartUebersichtRel = new JBreakLabel();
        lblKartUebersichtBem = new JBreakLabel();
        lblKartUebersichtUeberVon = new JBreakLabel();
        lblKartUebersichtUeberAm = new JBreakLabel();
        lblKartFreiRel = new JBreakLabel();
        lblKartFreiBem = new JBreakLabel();
        lblKartFreiUeberVon = new JBreakLabel();
        lblKartFreiUeberAm = new JBreakLabel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();

        setMinimumSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.BorderLayout());

        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Tim Liegenschaftskarte");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    235,
                    Short.MAX_VALUE)));
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(panTitle, java.awt.BorderLayout.NORTH);

        panInter.setOpaque(false);
        panInter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));
        add(panInter, java.awt.BorderLayout.SOUTH);

        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.BorderLayout());

        tabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 10));
        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabbedPane.setFont(new java.awt.Font("Tahoma", 1, 11));

        panTabAllgemein.setOpaque(false);
        panTabAllgemein.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        panAllgemein.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panAllgemein.setOpaque(false);
        panAllgemein.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Hinweise:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("angelegt von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("angelegt am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("gelöscht von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("gelöscht am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Grund der Löschung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panAllgemein.add(jLabel6, gridBagConstraints);

        lblHinweise.setText("allgemeine Hinweise");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblHinweise, gridBagConstraints);

        lblAngelVon.setText("z.B. Reisiger");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblAngelVon, gridBagConstraints);

        lblAngelAm.setText("2006-03-17 00:00:00.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblAngelAm, gridBagConstraints);

        lblGelVon.setText("z.B. Naust");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblGelVon, gridBagConstraints);

        lblGelAm.setText("2007-06-30 00:00:00.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblGelAm, gridBagConstraints);

        lblGrund.setText("Löschgrund");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panAllgemein.add(lblGrund, gridBagConstraints);

        panTabAllgemein.add(panAllgemein);

        tabbedPane.addTab("Allgemein", panTabAllgemein);

        panTabAlkis.setOpaque(false);
        panTabAlkis.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        panAlkis.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panAlkis.setOpaque(false);
        panAlkis.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("ALKIS relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("ALKIS entschieden von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("ALKIS entschieden am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel11, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Priorität");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel12, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Topographie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel14, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel15, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Nutzungsarten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel16, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setText("Bodenschätzung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel17, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("Sonstiges");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel18, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel19.setText("Vermessung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel19, gridBagConstraints);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel20.setText("Feldvergleich");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel20, gridBagConstraints);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel21.setText("Luftbildauswertung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel21, gridBagConstraints);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel22.setText("Bachverlauf");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel22, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel23.setText("Sonstige");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel23, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel24.setText("ALK relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel24, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel25.setText("ALK übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel25, gridBagConstraints);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel26.setText("ALK übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel26, gridBagConstraints);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel27.setText("ALB relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel27, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel28.setText("ALB übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel28, gridBagConstraints);

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel29.setText("ALB übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel29, gridBagConstraints);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel30.setText("DGK relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel30, gridBagConstraints);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel31.setText("DGK übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel31, gridBagConstraints);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel32.setText("DGK übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panAlkis.add(jLabel32, gridBagConstraints);

        lblAlkRelevant.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkRelevant, gridBagConstraints);

        lblAlkEntschiedenVon.setText("Pietsch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkEntschiedenVon, gridBagConstraints);

        lblAlkEntschiedenAm.setText("2006-01-17");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkEntschiedenAm, gridBagConstraints);

        lblAlkPrio.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkPrio, gridBagConstraints);

        lblAlkBemerk.setText("Neubaugebiet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkBemerk, gridBagConstraints);

        lblAlkTopo.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkTopo, gridBagConstraints);

        lblAlkGeb.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkGeb, gridBagConstraints);

        lblAlkNutz.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkNutz, gridBagConstraints);

        lblAlkBoden.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkBoden, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkSonstiges, gridBagConstraints);

        lblAlkVermessung.setText("in Arbeit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkVermessung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkFeldvergleich, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkLuftbild, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkBach, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkSonstige, gridBagConstraints);

        lblAlkALKRel.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALKRel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALKUeberVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALKUeberAm, gridBagConstraints);

        lblAlkALBRel.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALBRel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALBUeberVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkALBUeberAm, gridBagConstraints);

        lblAlkDGKRel.setText("ja/nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkDGKRel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkDGKAbgelVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panAlkis.add(lblAlkDGKAbgelAm, gridBagConstraints);

        panTabAlkis.add(panAlkis);

        tabbedPane.addTab("ALKIS", panTabAlkis);

        panTabKarto.setOpaque(false);
        panTabKarto.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        panKarto.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panKarto.setOpaque(false);
        panKarto.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Stadkarte relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Stadtkarte entschieden von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel8, gridBagConstraints);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel33.setText("Stadtkarte entschieden am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel33, gridBagConstraints);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel34.setText("Stadtkarte Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel34, gridBagConstraints);

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel35.setText("Stadtkarte übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel35, gridBagConstraints);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel36.setText("Stadtkarte übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel36, gridBagConstraints);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel37.setText("Citypläne relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel37, gridBagConstraints);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel38.setText("Citypläne Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel38, gridBagConstraints);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel39.setText("Citypläne übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel39, gridBagConstraints);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel40.setText("Citypläne übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel40, gridBagConstraints);

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel41.setText("Übersichtspläne relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel41, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel42.setText("Übersichtspläne Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel42, gridBagConstraints);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel43.setText("Übersichtspläne übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel43, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel44.setText("Übersichtspläne übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel44, gridBagConstraints);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel45.setText("Freizeitkarte relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel45, gridBagConstraints);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel46.setText("Freizeitkarte Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel46, gridBagConstraints);

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel47.setText("Freizeitkarte übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel47, gridBagConstraints);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel48.setText("Freizeitkarte übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 30);
        panKarto.add(jLabel48, gridBagConstraints);

        lblKartStadtRel.setText("jLabel49");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtRel, gridBagConstraints);

        lblKartStadtEntschVon.setText("jLabel50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtEntschVon, gridBagConstraints);

        lblKartStadtEntschAm.setText("jLabel51");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtEntschAm, gridBagConstraints);

        lblKartStadtBem.setText("jLabel52");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtBem, gridBagConstraints);

        lblKartStadtUeberVon.setText("jLabel53");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtUeberVon, gridBagConstraints);

        lblKartStadtUeberAm.setText("jLabel54");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartStadtUeberAm, gridBagConstraints);

        lblKartCityRel.setText("jLabel55");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartCityRel, gridBagConstraints);

        lblKartCityBem.setText("jLabel56");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartCityBem, gridBagConstraints);

        lblKartCityUeberVon.setText("jLabel57");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartCityUeberVon, gridBagConstraints);

        lblKartCityUeberAm.setText("jLabel58");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartCityUeberAm, gridBagConstraints);

        lblKartUebersichtRel.setText("jLabel59");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartUebersichtRel, gridBagConstraints);

        lblKartUebersichtBem.setText("jLabel60");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartUebersichtBem, gridBagConstraints);

        lblKartUebersichtUeberVon.setText("jLabel61");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartUebersichtUeberVon, gridBagConstraints);

        lblKartUebersichtUeberAm.setText("jLabel62");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartUebersichtUeberAm, gridBagConstraints);

        lblKartFreiRel.setText("jLabel63");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartFreiRel, gridBagConstraints);

        lblKartFreiBem.setText("jLabel64");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartFreiBem, gridBagConstraints);

        lblKartFreiUeberVon.setText("jLabel65");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartFreiUeberVon, gridBagConstraints);

        lblKartFreiUeberAm.setText("jLabel66");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panKarto.add(lblKartFreiUeberAm, gridBagConstraints);

        panTabKarto.add(panKarto);

        tabbedPane.addTab("Kartographie", panTabKarto);

        panContent.add(tabbedPane, java.awt.BorderLayout.CENTER);

        add(panContent, java.awt.BorderLayout.WEST);

        panMap.setOpaque(false);
        panMap.setLayout(new java.awt.GridBagLayout());

        panSpinner.setMaximumSize(new java.awt.Dimension(100, 100));
        panSpinner.setMinimumSize(new java.awt.Dimension(100, 100));
        panSpinner.setOpaque(false);

        final javax.swing.GroupLayout panSpinnerLayout = new javax.swing.GroupLayout(panSpinner);
        panSpinner.setLayout(panSpinnerLayout);
        panSpinnerLayout.setHorizontalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                100,
                Short.MAX_VALUE));
        panSpinnerLayout.setVerticalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                100,
                Short.MAX_VALUE));

        panMap.add(panSpinner, new java.awt.GridBagConstraints());

        add(panMap, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void stateChanged(final ChangeEvent e) {
        lastSelected = tabbedPane.getSelectedIndex();
        timer = new Date();
    }
}
