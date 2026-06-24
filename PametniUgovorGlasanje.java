import java.util.HashMap;
import java.util.Map;

public class PametniUgovorGlasanje {

    public static class Kandidat {
        int id;
        String ime;
        int brojGlasova;

        public Kandidat(int id, String ime) {
            this.id = id;
            this.ime = ime;
            this.brojGlasova = 0;
        }
    }

    public static class Birac {
        boolean registrovan = false;
        boolean glasao = false;
        int glasaoZaKandidataId = 0;
    }

    private final String administratorAdresa;
    private final Map<String, Birac> biraci = new HashMap<>();
    private final Map<Integer, Kandidat> kandidati = new HashMap<>();
    private int ukupanBrojKandidata = 0;

    public PametniUgovorGlasanje(String kreatorAdresa, String[] imenaKandidata) {
        this.administratorAdresa = kreatorAdresa;
        for (String ime : imenaKandidata) {
            dodajKandidata(ime);
        }
        System.out.println("Pametni ugovor je uspesno postavljen na mrezu od strane administratora: " + kreatorAdresa);
    }

    private void dodajKandidata(String ime) {
        ukupanBrojKandidata++;
        kandidati.put(ukupanBrojKandidata, new Kandidat(ukupanBrojKandidata, ime));
    }

    public void registrujBiraca(String pozivalacAdresa, String biracAdresa) {
        if (!pozivalacAdresa.equals(administratorAdresa)) {
            throw new SecurityException("GRESKA: Samo administrator moze da registruje birace!");
        }
        if (biraci.containsKey(biracAdresa) && biraci.get(biracAdresa).registrovan) {
            throw new IllegalArgumentException("GRESKA: Birac je vec registrovan.");
        }

        Birac noviBirac = new Birac();
        noviBirac.registrovan = true;
        biraci.put(biracAdresa, noviBirac);
        System.out.println("[Event] Birac registrovan: " + biracAdresa);
    }

    public void glasaj(String pozivalacAdresa, int kandidatId) {
        Birac birac = biraci.get(pozivalacAdresa);

        if (birac == null || !birac.registrovan) {
            throw new IllegalStateException("GRESKA: Adresa " + pozivalacAdresa + " nema pravo glasa (nije registrovana).");
        }
        if (birac.glasao) {
            throw new IllegalStateException("GRESKA: Adresa " + pozivalacAdresa + " je vec iskoristila pravo glasa.");
        }
        if (!kandidati.containsKey(kandidatId)) {
            throw new IllegalArgumentException("GRESKA: Kandidat sa ID-jem " + kandidatId + " ne postoji.");
        }

        birac.glasao = true;
        birac.glasaoZaKandidataId = kandidatId;
        kandidati.get(kandidatId).brojGlasova++;

        System.out.println("[Event] Uspesno glasanje! Adresa " + pozivalacAdresa + " je glasala za kandidata ID: " + kandidatId);
    }

    public void prikaziPobednika() {
        Kandidat pobednik = null;
        int najviseGlasova = -1;

        for (Kandidat k : kandidati.values()) {
            if (k.brojGlasova > najviseGlasova) {
                najviseGlasova = k.brojGlasova;
                pobednik = k;
            }
        }

        if (pobednik != null && najviseGlasova > 0) {
            System.out.println("\nTRENUTNI POBEDNIK: " + pobednik.ime + " sa osvojenih " + pobednik.brojGlasova + " glasova.");
        } else {
            System.out.println("\nJos uvek nema glasova.");
        }
    }

    public static void main(String[] args) {
        String admin = "0xAdmin99999999999999999999999999999999";
        String birac1 = "0xUser11111111111111111111111111111111";
        String birac2 = "0xUser22222222222222222222222222222222";
        String haker = "0xHackerXxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

        String[] kandidatiLista = {"Kandidat A", "Kandidat B", "Kandidat C"};

        PametniUgovorGlasanje ugovor = new PametniUgovorGlasanje(admin, kandidatiLista);

        System.out.println("\n--- SIMULACIJA TRANSAKCIJA ---");

        try {
            ugovor.registrujBiraca(admin, birac1);
            ugovor.registrujBiraca(admin, birac2);

            ugovor.glasaj(birac1, 1); // Birac 1 glasa za Kandidata A
            ugovor.glasaj(birac2, 2); // Birac 2 glasa za Kandidata B

            ugovor.prikaziPobednika();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
