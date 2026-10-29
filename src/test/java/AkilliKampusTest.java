import io.CsvDepolamaServisi;
import io.KaliciDepolamaServisi;
import models.*;
import org.junit.jupiter.api.*;
import patterns.DerslikFactory;
import patterns.VeriDeposu;
import services.DersPlanlayici;
import services.UygulamaServisi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Akilli Kampus Ders Programi Sistemi Birim Testleri.
 * OOP prensipleri, tasarim kaliplari ve cizelgeleme algoritmasi kisitlari test edilir.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AkilliKampusTest {

    private VeriDeposu veriDeposu;

    @BeforeEach
    void baslat() {
        veriDeposu = VeriDeposu.getInstance();
        veriDeposu.tumunuTemizle();
    }

    @AfterEach
    void bitir() {
        veriDeposu.tumunuTemizle();
    }

    // 1. Amfi polimorfik uygunluk testi
    @Test
    @Order(1)
    void test1_AmfiUygunlukKurali() {
        Derslik amfi = new Amfi("A-101", 100);

        // Kapasite yeterli ve lab gerekmiyor -> true
        assertTrue(amfi.uygunMu(80, false));

        // Kapasite yetersiz -> false
        assertFalse(amfi.uygunMu(120, false));

        // Lab gerekiyorsa amfiye atanamaz -> false
        assertFalse(amfi.uygunMu(50, true));
    }

    // 2. Laboratuvar polimorfik uygunluk testi
    @Test
    @Order(2)
    void test2_LaboratuvarUygunlukKurali() {
        Derslik lab = new Laboratuvar("LAB-1", 40);

        // Kapasite yeterli ve lab gerekiyor -> true
        assertTrue(lab.uygunMu(30, true));

        // Kapasite yetersiz -> false
        assertFalse(lab.uygunMu(50, true));

        // Lab gerekmeyen teorik ders -> false
        assertFalse(lab.uygunMu(25, false));
    }

    // 3. Factory Pattern ile nesne uretimi testi
    @Test
    @Order(3)
    void test3_DerslikFactoryUretimi() {
        Derslik d1 = DerslikFactory.olustur("AMFI", "A-201", 120);
        assertNotNull(d1);
        assertTrue(d1 instanceof Amfi);
        assertEquals("A-201", d1.getDerslikKodu());
        assertEquals(120, d1.getKapasite());

        Derslik d2 = DerslikFactory.olustur("laboratuvar", "LAB-2", 35);
        assertNotNull(d2);
        assertTrue(d2 instanceof Laboratuvar);
        assertEquals("LAB-2", d2.getDerslikKodu());

        assertThrows(IllegalArgumentException.class, () -> {
            DerslikFactory.olustur("OFIS", "OF-1", 10);
        });
    }

    // 4. Singleton tekillik testi
    @Test
    @Order(4)
    void test4_VeriDeposuSingletonTekilligi() {
        VeriDeposu r1 = VeriDeposu.getInstance();
        VeriDeposu r2 = VeriDeposu.getInstance();

        assertSame(r1, r2);

        OgretimElemani hoca = new OgretimElemani("Dr. Arda Can", "999", "Dr. Ogr. Uyesi", "Bilgisayar");
        r1.ogretimElemaniEkle(hoca);

        assertEquals(1, r2.getOgretimElemanlari().size());
        assertEquals("Dr. Arda Can", r2.getOgretimElemanlari().get(0).getAdSoyad());
    }

    // 5. Algoritmanin basarili ders atamasi
    @Test
    @Order(5)
    void test5_DersPlanlayiciBasariliAtama() {
        OgretimElemani hoca = new OgretimElemani("Prof. Dr. Ahmet Yilmaz", "101", "Prof. Dr.", "Yazilim");
        Derslik amfi = new Amfi("A-101", 100);
        Ders ders = new Ders("BGM101", "Programlamaya Giris", hoca, 60, false);

        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> sonuc = planlayici.planla(List.of(ders), List.of(amfi));

        assertNotNull(sonuc);
        assertEquals(1, sonuc.size());
        assertEquals("BGM101", sonuc.get(0).getDers().getDersKodu());
        assertEquals("A-101", sonuc.get(0).getDerslik().getDerslikKodu());
    }

    // 6. Ayni hocanin 2 farkli dersinin ayni saate atanmamasi kontrolu
    @Test
    @Order(6)
    void test6_DersPlanlayiciHocaCakismasiOnleme() {
        OgretimElemani ayniHoca = new OgretimElemani("Doc. Dr. Ayse Kaya", "102", "Doc. Dr.", "Matematik");

        Ders ders1 = new Ders("MAT101", "Matematik I", ayniHoca, 50, false);
        Ders ders2 = new Ders("MAT102", "Ayrik Matematik", ayniHoca, 45, false);

        Derslik amfi1 = new Amfi("A-101", 80);
        Derslik amfi2 = new Amfi("A-102", 80);

        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> sonuc = planlayici.planla(List.of(ders1, ders2), List.of(amfi1, amfi2));

        assertEquals(2, sonuc.size());
        assertNotEquals(sonuc.get(0).getZamanDilimi().toString(), sonuc.get(1).getZamanDilimi().toString());
    }

    // 7. Ayni sinifin ayni saate 2 farkli derse atanmamasi kontrolu
    @Test
    @Order(7)
    void test7_DersPlanlayiciDerslikCakismasiOnleme() {
        OgretimElemani h1 = new OgretimElemani("Hoca 1", "H1", "Dr.", "Bilgisayar");
        OgretimElemani h2 = new OgretimElemani("Hoca 2", "H2", "Dr.", "Elektrik");

        Ders ders1 = new Ders("CS101", "Giris", h1, 40, false);
        Ders ders2 = new Ders("EE101", "Devreler", h2, 40, false);

        Derslik tekSinif = new Amfi("TEK-AMFI", 50);

        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> sonuc = planlayici.planla(List.of(ders1, ders2), List.of(tekSinif));

        assertEquals(2, sonuc.size());
        assertEquals("TEK-AMFI", sonuc.get(0).getDerslik().getDerslikKodu());
        assertEquals("TEK-AMFI", sonuc.get(1).getDerslik().getDerslikKodu());
        assertNotEquals(sonuc.get(0).getZamanDilimi().toString(), sonuc.get(1).getZamanDilimi().toString());
    }

    // 8. Kapasite yetersizse ders atanamaz
    @Test
    @Order(8)
    void test8_DersPlanlayiciKapasiteYetersizLigindeAtamaYapilmaz() {
        OgretimElemani hoca = new OgretimElemani("Hoca X", "HX", "Prof.", "Endustri");
        Ders buyukDers = new Ders("END301", "Yoneylem", hoca, 120, false);
        Derslik kucukAmfi = new Amfi("KUCUK-1", 50);

        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> sonuc = planlayici.planla(List.of(buyukDers), List.of(kucukAmfi));

        assertTrue(sonuc.isEmpty());
    }

    // 9. Laboratuvar gereksinimi olan ders sadece laba atanir
    @Test
    @Order(9)
    void test9_DersPlanlayiciLabGereksinimiEslestirme() {
        OgretimElemani hoca = new OgretimElemani("Hoca Y", "HY", "Doc.", "Yazilim");
        Ders labDersi = new Ders("BGM201", "NYP Lab", hoca, 30, true);

        Derslik buyukAmfi = new Amfi("BUYUK-AMFI", 300);
        Derslik lab = new Laboratuvar("BILG-LAB", 40);

        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> sadeceAmfiSonuc = planlayici.planla(List.of(labDersi), List.of(buyukAmfi));
        assertTrue(sadeceAmfiSonuc.isEmpty());

        DersPlanlayici p2 = new DersPlanlayici();
        List<AtamaKaydi> dogruSonuc = p2.planla(List.of(labDersi), List.of(buyukAmfi, lab));
        assertEquals(1, dogruSonuc.size());
        assertEquals("BILG-LAB", dogruSonuc.get(0).getDerslik().getDerslikKodu());
    }

    // 10. CSV kaydetme ve geri yukleme testi
    @Test
    @Order(10)
    void test10_CsvDepolamaKaydetVeYukle() throws IOException {
        KaliciDepolamaServisi csv = new CsvDepolamaServisi();

        OgretimElemani hoca = new OgretimElemani("Mehmet Oz", "777", "Prof. Dr.", "Tip");
        Derslik amfi = new Amfi("TIP-AMFI", 150);
        Ders ders = new Ders("TIP101", "Anatomi", hoca, 100, false);

        veriDeposu.ogretimElemaniEkle(hoca);
        veriDeposu.derslikEkle(amfi);
        veriDeposu.dersEkle(ders);

        csv.kaydet();
        assertTrue(new File("veri").exists());

        veriDeposu.tumunuTemizle();
        assertEquals(0, veriDeposu.getOgretimElemanlari().size());

        csv.yukle();
        assertEquals(1, veriDeposu.getOgretimElemanlari().size());
        assertEquals("Mehmet Oz", veriDeposu.getOgretimElemanlari().get(0).getAdSoyad());
        assertEquals(1, veriDeposu.getDerslikler().size());
        assertEquals(1, veriDeposu.getDersler().size());
        assertEquals("777", veriDeposu.getDersler().get(0).getSorumluHoca().getSicilNo());

        // Test sonrasinda ornek kampüs verilerini olustur
        ornekVerileriHazirla(csv);
    }

    // 11. Facade servis akisi testi
    @Test
    @Order(11)
    void test11_UygulamaServisiFacadeEntegrasyonu() {
        UygulamaServisi servis = new UygulamaServisi(new CsvDepolamaServisi());

        servis.ogretimElemaniEkle("Ali Veli", "555", "Dr.", "Yazilim");
        servis.derslikEkle("AMFI", "F-101", 80);
        servis.dersEkle("YZL101", "Algoritmalar", "555", 60, false);

        assertEquals(1, veriDeposu.getOgretimElemanlari().size());
        assertEquals(1, veriDeposu.getDerslikler().size());
        assertEquals(1, veriDeposu.getDersler().size());

        List<AtamaKaydi> plan = servis.planOlustur();
        assertFalse(plan.isEmpty());
        assertEquals("YZL101", plan.get(0).getDers().getDersKodu());
    }

    // 12. Kalitim ve kapsulleme testi
    @Test
    @Order(12)
    void test12_KalitimVeKapsulleme() {
        OgretimElemani hoca = new OgretimElemani("Fatma Sahin", "303", "Prof.", "Endustri");

        assertEquals("Fatma Sahin", hoca.getAdSoyad());
        assertEquals("303", hoca.getSicilNo());
        assertEquals("Prof.", hoca.getUnvan());
        assertEquals("Endustri", hoca.getBolum());

        hoca.setUnvan("Doc.");
        assertEquals("Doc.", hoca.getUnvan());

        BolumYoneticisi yonetici = new BolumYoneticisi("Kemal Demir", "404", "Bilgisayar Muhendisligi");
        assertEquals("Kemal Demir", yonetici.getAdSoyad());
        assertEquals("Bilgisayar Muhendisligi", yonetici.getYonetilenBolum());
    }

    private void ornekVerileriHazirla(KaliciDepolamaServisi csv) throws IOException {
        veriDeposu.tumunuTemizle();

        OgretimElemani h1 = new OgretimElemani("Prof. Dr. Ahmet Yilmaz", "101", "Prof. Dr.", "Bilgisayar Muh.");
        OgretimElemani h2 = new OgretimElemani("Doc. Dr. Ayse Kaya", "102", "Doc. Dr.", "Yazilim Muh.");
        OgretimElemani h3 = new OgretimElemani("Dr. Ogr. Uyesi Can Oz", "103", "Dr. Ogr. Uyesi", "Temel Bilimler");

        veriDeposu.ogretimElemaniEkle(h1);
        veriDeposu.ogretimElemaniEkle(h2);
        veriDeposu.ogretimElemaniEkle(h3);

        veriDeposu.derslikEkle(new Amfi("A-101", 100));
        veriDeposu.derslikEkle(new Amfi("A-102", 80));
        veriDeposu.derslikEkle(new Laboratuvar("LAB-1", 40));
        veriDeposu.derslikEkle(new Laboratuvar("LAB-2", 35));

        veriDeposu.dersEkle(new Ders("BGM201", "Nesneye Yonelik Programlama", h1, 35, true));
        veriDeposu.dersEkle(new Ders("BGM203", "Veri Yapilari", h1, 50, false));
        veriDeposu.dersEkle(new Ders("MAT101", "Matematik I", h2, 75, false));
        veriDeposu.dersEkle(new Ders("YZM301", "Yazilim Mimarisi", h3, 30, true));

        csv.kaydet();
    }
}
