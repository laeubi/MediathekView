/*
 * MediathekView
 * Copyright (C) 2008 W. Xaver
 * W.Xaver[at]googlemail.com
 * http://zdfmediathk.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.tool;

import java.io.File;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.Iterator;
import javax.swing.JOptionPane;
import mediathek.Main;
import mediathek.controller.io.IoXmlLesen;
import mediathek.daten.DDaten;
import mediathek.daten.DatenPgruppe;
import mediathek.daten.DatenProg;
import mediathek.gui.dialog.DialogHilfe;
import mediathek.file.GetFile;

public class GuiFunktionenProgramme {

    private static final String PIPE = "| ";
    private static final String LEER = "      ";
    private static final String PFEIL = " -> ";
    private static final String PFAD_LINUX_VLC = "/usr/bin/vlc";
    private static final String PFAD_MAC_VLC = "/Applications/VLC.app/Contents/MacOS/VLC";
    private static final String PFAD_LINUX_FLV = "/usr/bin/flvstreamer";
    private static final String PFAD_WINDOWS_FLV = "bin\\flvstreamer_win32_latest.exe";
    // MusterPgruppen

    public static String getPath() {
        String pFilePath = "pFile";
        File propFile = new File(pFilePath);
        if (!propFile.exists()) {
            try {
                CodeSource cS = Main.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(cS.getLocation().toURI().getPath());
                String jarDir = jarFile.getParentFile().getPath();
                propFile = new File(jarDir + File.separator + pFilePath);
            } catch (Exception ex) {
            }
        }
        return propFile.getAbsolutePath().replace(pFilePath, "");
    }

    private static String getWindowsVlcPath() {
        //Für Windows den Pfad des VLC ermitteln
        //sonst den deutschen Defaultpfad für Programme verwenden verwenden
        final String PFAD_WIN_VLC_DEFAULT = "C:\\Programme\\VideoLAN\\VLC\\vlc.exe";
        final String PFAD_WIN_VLC = "\\VideoLAN\\VLC\\vlc.exe";
        String vlcPfad = "";
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                if (System.getenv("ProgramFiles") != null) {
                    vlcPfad = System.getenv("ProgramFiles") + PFAD_WIN_VLC;
                    if (new File(vlcPfad).exists()) {
                        return vlcPfad;
                    }
                }
            }
            if (System.getenv("ProgramFiles(x86)") != null) {
                vlcPfad = System.getenv("ProgramFiles(x86)") + PFAD_WIN_VLC;
                if (new File(vlcPfad).exists()) {
                    return vlcPfad;
                }
            }
        } catch (Exception ex) {
        }
        return PFAD_WIN_VLC_DEFAULT;
    }

    public static String getPfadVlc() {
        String pfad = "";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            pfad = getWindowsVlcPath();
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            pfad = PFAD_LINUX_VLC;
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            pfad = PFAD_MAC_VLC;
        }
        if (new File(pfad).exists()) {
            return pfad;
        } else {
            return "";
        }
    }

    public static String getPfadFlv() {
        String pfad = "";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            pfad = GuiFunktionenProgramme.getPath() + PFAD_WINDOWS_FLV;
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            pfad = PFAD_LINUX_FLV;
        }
        if (new File(pfad).exists()) {
            return pfad;
        } else {
            return "";
        }
    }

    public static boolean addStandardprogramme(DDaten ddaten, String pfadVLC, String pfadFlv, String zielpfad) {
        boolean ret = false;
        String MUSTER_PFAD_VLC = "PFAD_VLC";
        String MUSTER_PFAD_FLV = "PFAD_FLVSTREAMER";
        DatenPgruppe[] pGruppe;
        InputStream datei;
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            datei = new GetFile().getLinux();
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            datei = new GetFile().getMac();
        } else {
            datei = new GetFile().getWindows();
        }
        pGruppe = IoXmlLesen.importPgruppe(datei, true);
        if (pGruppe == null) {
            JOptionPane.showMessageDialog(null, "Die Programme konnten nicht importiert werden!",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        } else {
            // anpassen
            for (int p = 0; p < pGruppe.length; ++p) {
                DatenPgruppe pg = pGruppe[p];
                if (!zielpfad.equals("")) {
                    pg.arr[DatenPgruppe.PROGRAMMGRUPPE_ZIEL_PFAD_NR] = zielpfad;
                }
                for (int i = 0; i < pg.getListeProg().size(); ++i) {
                    DatenProg prog = pg.getProg(i);
                    if (!pfadVLC.equals("")) {
                        prog.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR] = prog.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR].replace(MUSTER_PFAD_VLC, pfadVLC);
                        prog.arr[DatenProg.PROGRAMM_SCHALTER_NR] = prog.arr[DatenProg.PROGRAMM_SCHALTER_NR].replace(MUSTER_PFAD_VLC, pfadVLC);
                    }
                    if (!pfadFlv.equals("")) {
                        prog.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR] = prog.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR].replace(MUSTER_PFAD_FLV, pfadFlv);
                        prog.arr[DatenProg.PROGRAMM_SCHALTER_NR] = prog.arr[DatenProg.PROGRAMM_SCHALTER_NR].replace(MUSTER_PFAD_FLV, pfadFlv);
                    }
                }
            }
            // und jetzt in die Liste der PGruppen schreiben
            ddaten.listePgruppe.addPgruppe(pGruppe);
            JOptionPane.showMessageDialog(null, "Die Programme wurden hinzugefügt!",
                    "", JOptionPane.INFORMATION_MESSAGE);
            ret = true;
        }
        return ret;
    }

    public static boolean praefixTesten(String str, String uurl, boolean praefix) {
        //prüfen ob url beginnt/endet mit einem Argument in str
        //wenn str leer dann true
        boolean ret = false;
        String url = uurl.toLowerCase();
        String s1 = "";
        if (str.equals("")) {
            ret = true;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) != ',') {
                    s1 += str.charAt(i);
                }
                if (str.charAt(i) == ',' || i >= str.length() - 1) {
                    if (praefix) {
                        //Präfix prüfen
                        if (url.startsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    } else {
                        //Suffix prüfen
                        if (url.endsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    }
                    s1 = "";
                }
            }
        }
        return ret;
    }

    public static boolean checkPfadBeschreibbar(String pfad) {
        boolean ret = false;
        File testPfad = new File(pfad);
        try {
            if (pfad.equals("")) {
            } else if (!testPfad.isDirectory()) {
            } else {
                if (testPfad.canWrite()) {
                    File tmpFile = File.createTempFile("mediathek", "tmp", testPfad);
                    tmpFile.delete();
                    ret = true;
                }
            }
        } catch (Exception ex) {
        }
        return ret;
    }

    public static boolean programmePruefen(DDaten daten) {
        boolean ret = true;
        String text = "";
        Iterator<DatenPgruppe> itPgruppe = daten.listePgruppe.iterator();
        DatenPgruppe datenPgruppe;
        DatenProg datenProg;
        while (itPgruppe.hasNext()) {
            datenPgruppe = itPgruppe.next();
            ret = true;
            if (!datenPgruppe.isFreeLine() && !datenPgruppe.isLable()) {
                // nur wenn kein Lable oder freeline
                text += "++++++++++++++++++++++++++++++++++++++++++++" + "\n";
                text += PIPE + "Programmgruppe: " + datenPgruppe.arr[DatenPgruppe.PROGRAMMGRUPPE_NAME_NR] + "\n";
                String zielPfad = datenPgruppe.arr[DatenPgruppe.PROGRAMMGRUPPE_ZIEL_PFAD_NR];
                if (zielPfad.equals("")) {
                    // beim nur Abspielen wird er nicht gebraucht
                    if (datenPgruppe.needsPath()) {
                        ret = false;
                        text += PIPE + LEER + "Zielpfad fehlt!\n";
                    }
                } else {
                    File pfad = new File(zielPfad);
                    if (!pfad.isDirectory()) {
                        ret = false;
                        text += PIPE + LEER + "Falscher Zielpfad!\n";
                        text += PIPE + LEER + PFEIL + "Zielpfad \"" + zielPfad + "\" ist kein Verzeichnis!" + "\n";
                    } else {
                        // Pfad beschreibbar?
                        if (!checkPfadBeschreibbar(zielPfad)) {
                            //da Pfad-leer und "kein" Pfad schon abgeprüft
                            ret = false;
                            text += PIPE + LEER + "Falscher Zielpfad!\n";
                            text += PIPE + LEER + PFEIL + "Zielpfad \"" + zielPfad + "\" nicht beschreibbar!" + "\n";
                        }
                    }
                }
                Iterator<DatenProg> itProg = datenPgruppe.getListeProg().iterator();
                while (itProg.hasNext()) {
                    datenProg = itProg.next();
                    // Programmpfad prüfen
                    //if (!new File(datenProg.arr[Konstanten.PROGRAMM_PROGRAMMPFAD_NR]).exists()) {
                    if (!new File(datenProg.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR]).canExecute()) {
                        // Programme prüfen
                        ret = false;
                        text += PIPE + LEER + "Falscher Programmpfad!\n";
                        text += PIPE + LEER + PFEIL + "Programmname: " + datenProg.arr[DatenProg.PROGRAMM_NAME_NR] + "\n";
                        text += PIPE + LEER + LEER + "Pfad: " + datenProg.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR] + "\n";
                        if (!datenProg.arr[DatenProg.PROGRAMM_PROGRAMMPFAD_NR].contains(File.separator)) {
                            text += PIPE + LEER + PFEIL + "Wenn das Programm nicht im Systempfad liegt, " + "\n";
                            text += PIPE + LEER + LEER + "wird der Start nicht klappen!" + "\n";
                        }
                    }
                }
                if (ret) {
                    //sollte alles passen
                    text += PIPE + PFEIL + "Ok!" + "\n";
                }
                text += "++++++++++++++++++++++++++++++++++++++++++++" + "\n\n\n";
            }
        }
        new DialogHilfe(null, true, text).setVisible(true);
        return ret;
    }
}