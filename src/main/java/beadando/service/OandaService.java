package beadando.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class OandaService {

    // --- BELSŐ ADATBÁZIS (Lista) a pozíciók tárolására ---
    private List<Position> nyitottPoziciok = new ArrayList<>();

    // Segédosztály egy pozíció adatainak
    public static class Position {
        public String id;
        public String instrument;
        public int units;
        public double openPrice;
        public String openTime;

        public Position(String id, String instrument, int units, double openPrice) {
            this.id = id;
            this.instrument = instrument;
            this.units = units;
            this.openPrice = openPrice;
            this.openTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    // --- 1. Számlainformációk (Változatlan) ---
    public String getAccountSummary() {
        return "Számla ID: 101-004-DEMO-HU\nEgyenleg: 100 000.00 EUR\nNyitott pozíciók száma: " + nyitottPoziciok.size();
    }

    // --- 2. Aktuális ár (Változatlan) ---
    public String getCurrentPrice(String instrument) {
        Random rand = new Random();
        double price = instrument.contains("JPY") || instrument.contains("HUF") ? 350.0 : 1.05;
        double ask = price + (rand.nextDouble() * 0.05);
        return "Instrumentum: " + instrument + "\nÁr: " + String.format("%.5f", ask) + "\n(Szimulált)";
    }

    // --- 3. Historikus ár (Változatlan) ---
    public String getHistoricalPrices(String instrument, String granularity) {
        return "Szimulált historikus adatok " + instrument + " - " + granularity + " idősíkon:\n" +
                "Dátum       Nyitó    Záró\n" +
                "2023-01-01  1.0500   1.0550\n" +
                "2023-01-02  1.0550   1.0600\n" +
                "... (további 8 sor)";
    }

    // --- 4. POZÍCIÓ NYITÁS (Forex-Nyit) ---
    public String openPosition(String instrument, int units) {
        Random rand = new Random();
        // Generálunk egy egyedi ID-t (pl. 12345)
        String tradeId = String.valueOf(10000 + rand.nextInt(90000));

        // Generálunk egy nyitó árat
        double price = instrument.contains("JPY") || instrument.contains("HUF") ? 350.0 : 1.05;
        double openPrice = price + (rand.nextDouble() * 0.01);

        // Hozzáadjuk a listához (Mintha az Oanda rendszerébe mentenénk)
        Position newPos = new Position(tradeId, instrument, units, openPrice);
        nyitottPoziciok.add(newPos);

        return "SIKER! Pozíció megnyitva.\n" +
                "Trade ID: " + tradeId + "\n" +
                "Instrumentum: " + instrument + "\n" +
                "Mennyiség: " + units + "\n" +
                "Nyitó ár: " + String.format("%.5f", openPrice);
    }

    // --- 5. POZÍCIÓK LISTÁZÁSA (Forex-Poz) ---
    // Visszaadja a nyitott pozíciók listáját
    public List<Position> getOpenPositions() {
        return nyitottPoziciok;
    }

    // --- 6. POZÍCIÓ ZÁRÁS (Forex-Zár) ---
    public String closePosition(String tradeId) {
        Iterator<Position> iterator = nyitottPoziciok.iterator();
        while (iterator.hasNext()) {
            Position pos = iterator.next();
            if (pos.id.equals(tradeId)) {
                iterator.remove(); // Töröljük a listából
                return "SIKER! A " + tradeId + " azonosítójú pozíció lezárva.\n" +
                        "Instrumentum: " + pos.instrument + "\n" +
                        "Záró ár: " + String.format("%.5f", pos.openPrice + 0.0010); // Kis profit szimulálása
            }
        }
        return "HIBA: Nem található ilyen Trade ID (" + tradeId + ").";
    }
}