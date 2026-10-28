package services;

/*
 * ============================================================
 *  FAÇADE / UYGULAMA SERVİSİ — ApplicationService
 * ============================================================
 *
 *  Bu sınıf, 3. aşamadaki konsol menüsünün DOĞRUDAN çağıracağı
 *  tek giriş noktasıdır.  Menü kodu yalnızca bu sınıfı bilir;
 *  alt sistemleri (DersPlanlayici, VeriDeposu, CsvDepolamaServisi)
 *  bilmek zorunda kalmaz.
 *
 *  SOLID PRENSİPLERİ:
 *  - Single Responsibility (SRP): Her public metot tek bir iş
 *    yapar (ders ekle, listele, planla, kaydet, yükle...).
 *  - Dependency Inversion (DIP): IO katmanı KaliciDepolamaServisi
 *    arayüzü aracılığıyla enjekte edilir (constructor injection).
 *    Menü kodu CsvDepolamaServisi'ni doğrudan kullanmaz.
 *  - Open/Closed (OCP): Yeni bir iş kuralı eklemek için bu
 *    sınıfa yeni metot eklenir; mevcut metodlar değişmez.
 *
 *  NYP PRENSİPLERİ:
 *  - Kapsülleme: Alt sistem referansları private.
 *  - Soyutlama: Menü, karmaşık iş mantığını görmez.
 * ============================================================
 */

import io.KaliciDepolamaServisi;
import models.*;
import patterns.DerslikFactory;
import patterns.VeriDeposu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UygulamaServisi {

    // -------------------------------------------------------
    // DIP: Somut sınıfa değil, arayüze bağımlılık.
    // -------------------------------------------------------
    private final KaliciDepolamaServisi depolamaServisi;
    private final VeriDeposu           veriDeposu;
    private final DersPlanlayici       planlayici;

    /**
     * Constructor Injection: Hangi depolama stratejisinin kullanılacağı
     * dışarıdan verilir.  Bu sayede CsvDepolamaServisi yerine ileride
     * JsonDepolamaServisi enjekte edilerek birim test veya format
     * değişikliği kolayca yapılabilir.
     *
     * @param depolamaServisi Kullanılacak IO stratejisi
     */
    public UygulamaServisi(KaliciDepolamaServisi depolamaServisi) {
        this.depolamaServisi = depolamaServisi;
        this.veriDeposu      = VeriDeposu.getInstance();  // Singleton erişim
        this.planlayici      = new DersPlanlayici();
    }

    // ============================================================
    //  BAŞLATMA / KAPATMA
    // ============================================================

    /**
     * Uygulama başlangıcında çağrılır.
     * Kaydedilmiş CSV verilerini belleğe yükler.
     */
    public void baslat() {
        try {
            depolamaServisi.yukle();
        } catch (IOException e) {
            System.out.println("[Servis] Veri dosyası bulunamadı, boş sistemle başlanıyor.");
        }
    }

    /**
     * Uygulama kapanmadan önce çağrılır.
     * Bellekteki verileri CSV dosyalarına yazar.
     */
    public void kapat() {
        try {
            depolamaServisi.kaydet();
        } catch (IOException e) {
            System.err.println("[Servis] Kaydetme hatası: " + e.getMessage());
        }
    }

    // ============================================================
    //  ÖĞRETİM ELEMANI İŞLEMLERİ
    // ============================================================

    /**
     * Yeni öğretim elemanı oluşturup depoya ekler.
     * Menü bu metodu switch-case içinden çağırır.
     */
    public void ogretimElemaniEkle(String adSoyad, String sicilNo, String unvan, String bolum) {
        OgretimElemani oe = new OgretimElemani(adSoyad, sicilNo, unvan, bolum);
        veriDeposu.ogretimElemaniEkle(oe);
        System.out.println("[OK] Öğretim elemanı eklendi: " + adSoyad);
    }

    /** Tüm öğretim elemanlarını ekrana listeler. */
    public void ogretimElemanlariListele() {
        List<OgretimElemani> liste = veriDeposu.getOgretimElemanlari();
        if (liste.isEmpty()) {
            System.out.println("Kayıtlı öğretim elemanı bulunmuyor.");
            return;
        }
        System.out.println("--- ÖĞRETİM ELEMANLARI ---");
        for (int i = 0; i < liste.size(); i++) {
            OgretimElemani oe = liste.get(i);
            System.out.printf("  %d) %s %s | Bölüm: %s | SicilNo: %s%n",
                i + 1, oe.getUnvan(), oe.getAdSoyad(), oe.getBolum(), oe.getSicilNo());
        }
    }

    // ============================================================
    //  DERSLİK İŞLEMLERİ
    // ============================================================

    /**
     * Factory Pattern aracılığıyla yeni derslik oluşturup depoya ekler.
     *
     * @param tur  "AMFI" veya "LABORATUVAR"
     */
    public void derslikEkle(String tur, String derslikKodu, int kapasite) {
        try {
            // Factory Method devreye giriyor — nesne üretim bilgisi dağılmıyor
            Derslik derslik = DerslikFactory.olustur(tur, derslikKodu, kapasite);
            veriDeposu.derslikEkle(derslik);
            System.out.println("[OK] Derslik eklendi: " + derslikKodu + " (" + tur + ", " + kapasite + " kişilik)");
        } catch (IllegalArgumentException e) {
            System.err.println("[HATA] " + e.getMessage());
        }
    }

    /** Tüm derslikleri ekrana listeler. */
    public void dersliklerListele() {
        List<Derslik> liste = veriDeposu.getDerslikler();
        if (liste.isEmpty()) {
            System.out.println("Kayıtlı derslik bulunmuyor.");
            return;
        }
        System.out.println("--- DERSLİKLER ---");
        for (int i = 0; i < liste.size(); i++) {
            Derslik d = liste.get(i);
            String tur = (d instanceof Laboratuvar) ? "Laboratuvar" : "Amfi";
            System.out.printf("  %d) [%s] %s — Kapasite: %d%n",
                i + 1, tur, d.getDerslikKodu(), d.getKapasite());
        }
    }

    // ============================================================
    //  DERS İŞLEMLERİ
    // ============================================================

    /**
     * Sicil numarasına göre hocayı bulup yeni ders oluşturur.
     *
     * @param sorumluHocaSicilNo Mevcut öğretim elemanının sicil numarası
     */
    public void dersEkle(String dersKodu, String dersAdi, String sorumluHocaSicilNo,
                         int ogrenciSayisi, boolean laboratuvarGerekliMi) {

        OgretimElemani hoca = veriDeposu.getOgretimElemanlari().stream()
            .filter(oe -> oe.getSicilNo().equals(sorumluHocaSicilNo))
            .findFirst()
            .orElse(null);

        if (hoca == null) {
            System.err.println("[HATA] Sicil numarası '" + sorumluHocaSicilNo +
                "' olan öğretim elemanı bulunamadı.");
            return;
        }

        Ders ders = new Ders(dersKodu, dersAdi, hoca, ogrenciSayisi, laboratuvarGerekliMi);
        veriDeposu.derEkle(ders);
        System.out.println("[OK] Ders eklendi: " + dersKodu + " - " + dersAdi);
    }

    /** Tüm dersleri ekrana listeler. */
    public void derslerListele() {
        List<Ders> liste = veriDeposu.getDersler();
        if (liste.isEmpty()) {
            System.out.println("Kayıtlı ders bulunmuyor.");
            return;
        }
        System.out.println("--- DERSLER ---");
        for (int i = 0; i < liste.size(); i++) {
            Ders d = liste.get(i);
            System.out.printf("  %d) [%s] %s | Hoca: %s | Öğrenci: %d | Lab: %s%n",
                i + 1, d.getDersKodu(), d.getDersAdi(),
                d.getSorumluHoca().getAdSoyad(),
                d.getOgrenciSayisi(),
                d.isLaboratuvarGerekliMi() ? "Evet" : "Hayır");
        }
    }

    // ============================================================
    //  PLANLAMA
    // ============================================================

    /**
     * İdil'in DersPlanlayici algoritmasını çalıştırır ve sonucu ekrana yazar.
     * Menüden "Planı Oluştur" seçeneğiyle tetiklenir.
     */
    public List<AtamaKaydi> planOlustur() {
        List<Ders>    dersler    = new ArrayList<>(veriDeposu.getDersler());
        List<Derslik> derslikler = new ArrayList<>(veriDeposu.getDerslikler());

        if (dersler.isEmpty() || derslikler.isEmpty()) {
            System.out.println("[Uyarı] Plan oluşturmak için en az bir ders ve bir derslik gereklidir.");
            return List.of();
        }

        // DersPlanlayici'yi her seferinde temiz state ile çalıştır
        DersPlanlayici temizPlanlayici = new DersPlanlayici();
        List<AtamaKaydi> plan = temizPlanlayici.planla(dersler, derslikler);

        System.out.println("\n--- OLUŞTURULAN DERS PROGRAMI ---");
        if (plan.isEmpty()) {
            System.out.println("  Hiçbir ders atanamadı!");
        } else {
            for (AtamaKaydi kayit : plan) {
                System.out.println("  " + kayit.toString());
            }
        }
        return plan;
    }

    // ============================================================
    //  MANUEL KAYDET / YÜKLE
    // ============================================================

    /** Menüden "Kaydet" seçildiğinde çağrılır. */
    public void manuelKaydet() {
        kapat();
    }

    /** Menüden "Diskten Yükle" seçildiğinde çağrılır. */
    public void manuelYukle() {
        baslat();
    }
}
