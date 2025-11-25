package beadando.controller;

import beadando.service.MnbService;
import beadando.service.OandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    // --- Szolgáltatások injektálása ---
    @Autowired
    private MnbService mnbService;

    @Autowired
    private OandaService oandaService;

    // --- 1. Főoldal ---
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // --- 2. SOAP Menü (MNB) ---
    @GetMapping("/soap")
    public String soapClient(Model model) {
        return "soap";
    }

    // SOAP űrlap elküldése és grafikon rajzolás
    @PostMapping("/soap/lekerdez")
    public String soapLekerdezes(@RequestParam String deviza,
                                 @RequestParam String startDatum,
                                 @RequestParam String endDatum,
                                 Model model) {

        // 1. Adatok lekérése a generált SOAP klienssel
        String xmlValasz = mnbService.getExchangeRates(startDatum, endDatum, deviza);

        // 2. Adatok feldolgozása a grafikonhoz
        var feldolgozottAdatok = mnbService.feldolgozXml(xmlValasz);

        // 3. Listák összeállítása a Chart.js számára
        StringBuilder datumok = new StringBuilder();
        StringBuilder ertekek = new StringBuilder();

        for (var adat : feldolgozottAdatok) {
            datumok.append("'").append(adat.getDatum()).append("',");
            ertekek.append(adat.getErtek()).append(",");
        }

        // 4. Adatok átadása a HTML oldalnak
        model.addAttribute("mnbAdat", xmlValasz);
        model.addAttribute("chartLabels", datumok.toString());
        model.addAttribute("chartData", ertekek.toString());

        // Hogy az űrlap mezői ne ürüljenek ki:
        model.addAttribute("selectedDeviza", deviza);
        model.addAttribute("selectedStart", startDatum);
        model.addAttribute("selectedEnd", endDatum);

        return "soap";
    }

    // --- 3. Forex Menük (Oanda) ---

    // Forex-Account: Számlainformációk kiírása (EZ MÁR MŰKÖDIK)
    @GetMapping("/forex/account")
    public String forexAccount(Model model) {
        // Lekérjük az adatokat az OandaService-től
        String accountInfo = oandaService.getAccountSummary();

        // Átadjuk a HTML-nek megjelenítésre
        model.addAttribute("accountInfo", accountInfo);

        return "forex-account";
    }

    // --- Üres Forex menük (Később töltjük fel őket) ---

    // Forex-AktÁr: Oldal betöltése
    @GetMapping("/forex/aktar")
    public String forexAktualisAr(Model model) {
        return "forex-aktar";
    }

    // Forex-AktÁr: Űrlap elküldése
    @PostMapping("/forex/aktar")
    public String forexAktualisArLekerdezes(@RequestParam String instrument, Model model) {

        // 1. Ár lekérése az OandaService-től
        String arfolyamAdat = oandaService.getCurrentPrice(instrument);

        // 2. Adatok átadása a HTML-nek
        model.addAttribute("arfolyamAdat", arfolyamAdat);
        model.addAttribute("selectedInstrument", instrument);

        return "forex-aktar";
    }

    // Forex-HistÁr: Oldal betöltése
    @GetMapping("/forex/histar")
    public String forexHistorikusAr(Model model) {
        return "forex-histar";
    }

    // Forex-HistÁr: Lekérdezés
    @PostMapping("/forex/histar")
    public String forexHistorikusArLekerdezes(@RequestParam String instrument,
                                              @RequestParam String granularity,
                                              Model model) {

        String historia = oandaService.getHistoricalPrices(instrument, granularity);

        model.addAttribute("historia", historia);
        model.addAttribute("selectedInstrument", instrument);
        model.addAttribute("selectedGranularity", granularity);

        return "forex-histar";
    }

    // Forex-Nyit: Pozíció nyitás
    @GetMapping("/forex/nyit")
    public String forexPozicioNyitas(Model model) {
        return "forex-nyitas";
    }

    // Forex-Poz: Nyitott pozíciók listázása
    @GetMapping("/forex/poziciok")
    public String forexNyitottPoziciok(Model model) {
        return "forex-poziciok";
    }

    // Forex-Zár: Pozíció zárás
    @GetMapping("/forex/zar")
    public String forexPozicioZaras(Model model) {
        return "forex-zaras";
    }
}