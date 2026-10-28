package io;

/*
 * ============================================================
 *  KALICI VERİ SAKLAMA — CSV Tabanlı File I/O
 * ============================================================
 *
 *  Bu sınıf KaliciDepolamaServisi arayüzünü CSV formatı ile
 *  somutlaştırır.  Her varlık türü için ayrı bir ".csv" dosyası
 *  yönetilir:
 *    - ogretim_elemanlari.csv
 *    - derslikler.csv
 *    - dersler.csv
 *
 *  SOLID PRENSİPLERİ:
 *  - Single Responsibility (SRP): Yalnızca CSV okuma/yazma
 *    işleminden sorumludur.  İş kurallarına müdahil olmaz.
 *  - Open/Closed (OCP): Yeni alan eklemek için sadece bu sınıfın
 *    ilgili parse/format metodunu değiştirmek yeterlidir.
 *  - Liskov Substitution (LSP): KaliciDepolamaServisi tipinde
 *    kullanılan her yerde CsvDepolamaServisi sorunsuz çalışır.
 *  - Dependency Inversion (DIP): Menü / servis katmanı bu sınıfı
 *    arayüz tipiyle referans alır → soyuta bağımlılık.
 *
 *  NYP PRENSİPLERİ:
 *  - Kapsülleme: Dosya yolları private, dışarıya sızma yok.
 *  - Çok Biçimlilik: kaydet/yukle metotları @Override aracılığıyla
 *    polimorfik olarak çağrılabilir.
 * ============================================================
 */

import models.Amfi;
import models.Ders;
import models.Derslik;
import models.Laboratuvar;
import models.OgretimElemani;
import patterns.VeriDeposu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class CsvDepolamaServisi implements KaliciDepolamaServisi {

    // -------------------------------------------------------
    // Kapsülleme: Dosya yolları dışarıya kapalı
    // -------------------------------------------------------
    private static final String VERI_KLASORU           = "veri";
    private static final String HOCA_DOSYASI           = VERI_KLASORU + "/ogretim_elemanlari.csv";
    private static final String DERSLIK_DOSYASI        = VERI_KLASORU + "/derslikler.csv";
    private static final String DERS_DOSYASI           = VERI_KLASORU + "/dersler.csv";

    private static final String AYRAC                  = ";";

    // CSV başlık satırları — okunabilirlik için
    private static final String HOCA_BASLIK    = "adSoyad;sicilNo;unvan;bolum";
    private static final String DERSLIK_BASLIK = "tur;derslikKodu;kapasite";
    private static final String DERS_BASLIK    = "dersKodu;dersAdi;sorumluHocaSicilNo;ogrenciSayisi;laboratuvarGerekliMi";

    // -------------------------------------------------------
    // KaliciDepolamaServisi — kaydet()
    // -------------------------------------------------------

    /**
     * Singleton VeriDeposu'ndaki tüm verileri CSV dosyalarına yazar.
     * Program kapandığında çağrılarak verinin kalıcılaşmasını sağlar.
     */
    @Override
    public void kaydet() throws IOException {
        klasorOlustur();

        VeriDeposu depo = VeriDeposu.getInstance();

        hocalariKaydet(depo.getOgretimElemanlari());
        derslikleriKaydet(depo.getDerslikler());
        dersleriKaydet(depo.getDersler(), depo.getOgretimElemanlari());

        System.out.println("[IO] Veriler başarıyla kaydedildi: " + Paths.get(VERI_KLASORU).toAbsolutePath());
    }

    // -------------------------------------------------------
    // KaliciDepolamaServisi — yukle()
    // -------------------------------------------------------

    /**
     * CSV dosyalarından verileri okuyup Singleton VeriDeposu'na yükler.
     * Program başladığında çağrılarak önceki oturumun verisini geri getirir.
     */
    @Override
    public void yukle() throws IOException {
        VeriDeposu depo = VeriDeposu.getInstance();
        depo.tumunuTemizle();  // Çift yüklemeyi önlemek için önce temizle

        if (Files.exists(Paths.get(HOCA_DOSYASI))) {
            hocalariYukle(depo);
        }
        if (Files.exists(Paths.get(DERSLIK_DOSYASI))) {
            derslikleriYukle(depo);
        }
        if (Files.exists(Paths.get(DERS_DOSYASI))) {
            dersleriYukle(depo);
        }

        System.out.println("[IO] Veriler başarıyla yüklendi.");
    }

    // ============================================================
    //  YAZMA YARDIMCI METODLARI
    // ============================================================

    private void klasorOlustur() throws IOException {
        Files.createDirectories(Paths.get(VERI_KLASORU));
    }

    /** Öğretim elemanlarını CSV formatında yazar. */
    private void hocalariKaydet(List<OgretimElemani> liste) throws IOException {
        try (BufferedWriter yazar = yeniYazici(HOCA_DOSYASI)) {
            yazar.write(HOCA_BASLIK);
            yazar.newLine();
            for (OgretimElemani oe : liste) {
                // Format: adSoyad;sicilNo;unvan;bolum
                yazar.write(
                    kacis(oe.getAdSoyad())  + AYRAC +
                    kacis(oe.getSicilNo())  + AYRAC +
                    kacis(oe.getUnvan())    + AYRAC +
                    kacis(oe.getBolum())
                );
                yazar.newLine();
            }
        }
    }

    /** Derslik listesini CSV formatında yazar. */
    private void derslikleriKaydet(List<Derslik> liste) throws IOException {
        try (BufferedWriter yazar = yeniYazici(DERSLIK_DOSYASI)) {
            yazar.write(DERSLIK_BASLIK);
            yazar.newLine();
            for (Derslik d : liste) {
                // Çok Biçimlilik: instanceof ile alt tür belirlenir
                String tur = (d instanceof Laboratuvar) ? "LABORATUVAR" : "AMFI";
                yazar.write(
                    kacis(tur)                        + AYRAC +
                    kacis(d.getDerslikKodu())         + AYRAC +
                    d.getKapasite()
                );
                yazar.newLine();
            }
        }
    }

    /** Ders listesini CSV formatında yazar.
     *  Hoca referansı olarak sicilNo kullanılır (foreign key mantığı). */
    private void dersleriKaydet(List<Ders> dersler, List<OgretimElemani> bocalar) throws IOException {
        try (BufferedWriter yazar = yeniYazici(DERS_DOSYASI)) {
            yazar.write(DERS_BASLIK);
            yazar.newLine();
            for (Ders d : dersler) {
                yazar.write(
                    kacis(d.getDersKodu())                              + AYRAC +
                    kacis(d.getDersAdi())                               + AYRAC +
                    kacis(d.getSorumluHoca().getSicilNo())              + AYRAC +
                    d.getOgrenciSayisi()                                + AYRAC +
                    d.isLaboratuvarGerekliMi()
                );
                yazar.newLine();
            }
        }
    }

    // ============================================================
    //  OKUMA YARDIMCI METODLARI
    // ============================================================

    /** CSV'den öğretim elemanlarını okur ve depoya ekler. */
    private void hocalariYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(HOCA_DOSYASI)) {
            String satir = okuyucu.readLine(); // Başlık satırını atla
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 4) continue;

                OgretimElemani oe = new OgretimElemani(
                    parcalar[0].trim(),  // adSoyad
                    parcalar[1].trim(),  // sicilNo
                    parcalar[2].trim(),  // unvan
                    parcalar[3].trim()   // bolum
                );
                depo.ogretimElemaniEkle(oe);
            }
        }
    }

    /** CSV'den derslik nesnelerini okur; Factory Pattern ile oluşturur. */
    private void derslikleriYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(DERSLIK_DOSYASI)) {
            String satir = okuyucu.readLine(); // Başlık satırını atla
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 3) continue;

                String tur          = parcalar[0].trim();
                String derslikKodu  = parcalar[1].trim();
                int    kapasite     = Integer.parseInt(parcalar[2].trim());

                // Factory Method kalıbı burada devreye giriyor!
                Derslik derslik = patterns.DerslikFactory.olustur(tur, derslikKodu, kapasite);
                depo.derslikEkle(derslik);
            }
        }
    }

    /** CSV'den dersleri okur; hocayı sicilNo ile depoda arar. */
    private void dersleriYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(DERS_DOSYASI)) {
            String satir = okuyucu.readLine(); // Başlık satırını atla
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 5) continue;

                String  dersKodu          = parcalar[0].trim();
                String  dersAdi           = parcalar[1].trim();
                String  sicilNo           = parcalar[2].trim();
                int     ogrenciSayisi     = Integer.parseInt(parcalar[3].trim());
                boolean labGerekliMi      = Boolean.parseBoolean(parcalar[4].trim());

                // Hocayı depoda sicilNo ile bul (ilişki çözümlemesi)
                OgretimElemani hoca = depo.getOgretimElemanlari().stream()
                    .filter(oe -> oe.getSicilNo().equals(sicilNo))
                    .findFirst()
                    .orElse(null);

                if (hoca == null) {
                    System.err.println("[IO] Uyarı: '" + dersKodu +
                        "' dersinin hocası (sicilNo=" + sicilNo + ") bulunamadı, ders atlandı.");
                    continue;
                }

                Ders ders = new Ders(dersKodu, dersAdi, hoca, ogrenciSayisi, labGerekliMi);
                depo.derEkle(ders);
            }
        }
    }

    // ============================================================
    //  DOSYA YARDIMCI METODLARI
    // ============================================================

    private BufferedWriter yeniYazici(String yol) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(yol), StandardCharsets.UTF_8));
    }

    private BufferedReader yeniOkuyucu(String yol) throws IOException {
        return new BufferedReader(new InputStreamReader(
            new FileInputStream(yol), StandardCharsets.UTF_8));
    }

    /**
     * Ayraç karakterini içeren metni tırnak içine alarak kaçış yapar.
     * Basit CSV güvenliği sağlar.
     */
    private String kacis(String deger) {
        if (deger == null) return "";
        if (deger.contains(AYRAC) || deger.contains("\"")) {
            return "\"" + deger.replace("\"", "\"\"") + "\"";
        }
        return deger;
    }
}
