package beadando.controller;

import beadando.service.MnbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private MnbService mnbService;

    // 1. Főoldal menü
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 2. SOAP menü (MNB) - Az oldal betöltése
    @GetMapping("/soap")
    public String soapClient(Model model) {
        return "soap";
    }

    // SOAP űrlap elküldése (Adatok lekérése és feldolgozása a grafikonhoz)
    @PostMapping("/soap/lekerdez")
    public String soapLekerdezes(@RequestParam String deviza,
                                 @RequestParam String startDatum,
                                 @RequestParam String endDatum,
                                 Model model) {

        // 1. Nyers adat lekérése az MNB-től
        String xmlValasz = mnbService.getExchangeRates(startDatum, endDatum, deviza);

        // 2. Adat feldolgozása (Itt hívjuk meg azt, amit az MnbService-be írtunk!)
        // Ha itt piros a 'feldolgozXml', akkor az MnbService-ből hiányzik a kód!
        var feldolgozottAdatok = mnbService.feldolgozXml(xmlValasz);

        // 3. Adatok szétválogatása a Chart.js formátumára
        StringBuilder datumok = new StringBuilder();
        StringBuilder ertekek = new StringBuilder();

        for (var adat : feldolgozottAdatok) {
            // A JS-nek 'YYYY-MM-DD', formátum kell, aposztrófok között
            datumok.append("'").append(adat.getDatum()).append("',");
            // Az értékeket csak simán vesszővel elválasztva fűzzük össze
            ertekek.append(adat.getErtek()).append(",");
        }

        // 4. Adatok átadása a HTML oldalnak (Thymeleaf változók)
        model.addAttribute("mnbAdat", xmlValasz); // A nyers válasz (ha ki akarjuk írni szövegesen is)
        model.addAttribute("chartLabels", datumok.toString()); // X tengely (Dátumok)
        model.addAttribute("chartData", ertekek.toString());   // Y tengely (Árfolyamok)

        // Visszaküldjük a beírt adatokat is, hogy ne tűnjenek el a mezőkből
        model.addAttribute("selectedDeviza", deviza);
        model.addAttribute("selectedStart", startDatum);
        model.addAttribute("selectedEnd", endDatum);

        return "soap";
    }

    // --- Forex menük (Egyelőre csak üres oldalak) ---

    @GetMapping("/forex/account")
    public String forexAccount(Model model) { return "forex-account"; }

    @GetMapping("/forex/aktar")
    public String forexAktualisAr(Model model) { return "forex-aktar"; }

    @GetMapping("/forex/histar")
    public String forexHistorikusAr(Model model) { return "forex-histar"; }

    @GetMapping("/forex/nyit")
    public String forexPozicioNyitas(Model model) { return "forex-nyitas"; }

    @GetMapping("/forex/poziciok")
    public String forexNyitottPoziciok(Model model) { return "forex-poziciok"; }

    @GetMapping("/forex/zar")
    public String forexPozicioZaras(Model model) { return "forex-zaras"; }
}