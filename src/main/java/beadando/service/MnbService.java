package beadando.service;

import hu.mnb.arfolyamok.MNBArfolyamServiceSoap;
import hu.mnb.arfolyamok.MNBArfolyamServiceSoapImpl;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MnbService {

    public String getExchangeRates(String startDate, String endDate, String currencyPair) {
        try {
            // Ez a generált kód hívása (Működik, ne bántsuk!)
            MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
            MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
            String response = service.getExchangeRates(startDate, endDate, currencyPair);

            // Kiírjuk a konzolra, hogy lássuk, megjött
            System.out.println("MNB XML VÁLASZ: " + response);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Hiba: " + e.getMessage();
        }
    }

    // --- XML Feldolgozó (ITT VOLT A HIBA, EZT JAVÍTOTTUK) ---
    public static class MnbAdat {
        private String datum;
        private String ertek;
        public MnbAdat(String datum, String ertek) { this.datum = datum; this.ertek = ertek; }
        public String getDatum() { return datum; }
        public String getErtek() { return ertek; }
    }

    public List<MnbAdat> feldolgozXml(String xmlValasz) {
        List<MnbAdat> adatok = new ArrayList<>();
        try {
            if (xmlValasz == null || !xmlValasz.contains("Day")) return adatok;

            // A válasz így néz ki: ... <Day date="2023-01-10"><Rate ...>402,88</Rate></Day> ...
            String[] lines = xmlValasz.split("date=\"");

            for (int i = 1; i < lines.length; i++) {
                String resz = lines[i];

                // 1. Dátum kivágása (ez eddig is jó volt)
                String datum = resz.substring(0, 10);

                // 2. Érték kivágása (EZT JAVÍTOTTUK)
                // Megkeressük a "<Rate" szöveget
                int rateTagStart = resz.indexOf("<Rate");
                if (rateTagStart != -1) {
                    // Megkeressük a Rate címke végét (">")
                    int ertekStart = resz.indexOf(">", rateTagStart) + 1;
                    // Megkeressük a lezáró címkét ("</Rate>")
                    int ertekEnd = resz.indexOf("</Rate>", ertekStart);

                    if (ertekStart > 0 && ertekEnd > ertekStart) {
                        // Kivágjuk a számot és a vesszőt pontra cseréljük (Chart.js miatt)
                        String ertekStr = resz.substring(ertekStart, ertekEnd).replace(",", ".");
                        adatok.add(new MnbAdat(datum, ertekStr));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Parse hiba: " + e.getMessage());
        }

        return adatok;
    }
}