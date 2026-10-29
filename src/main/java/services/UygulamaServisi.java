package services;

import io.KaliciDepolamaServisi;
import models.*;
import patterns.DerslikFactory;
import patterns.VeriDeposu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Konsol arayüzü ile alt sistemler (VeriDeposu, DersPlanlayici, KaliciDepolamaServisi)
 * arasındaki iletişimi sağlayan Façade (Ön Yüz) servis sınıfı.
 */
public class UygulamaServisi {

    private final KaliciDepolamaServisi depolamaServisi;
    private final VeriDeposu veriDeposu;
    private final DersPlanlayici planlayici;

    public UygulamaServisi(KaliciDepolamaServisi depolamaServisi) {
        this.depolamaServisi = depolamaServisi;
        this.veriDeposu = VeriDeposu.getInstance();
        this.planlayici = new DersPlanlayici();
    }

    // Uygulama baslangicinda verileri diskten yukler
    public void baslat() {
        try {
            depolamaServisi.yukle();
        } catch (IOException e) {
            System.out.println("[Bilgi] Veri dosyasi bulunamadi, bos sistemle baslatiliyor.");
        }
    }

    // Uygulama kapanirken verileri diske kaydeder
    public void kapat() {
        try {
            depolamaServisi.kaydet();
        } catch (IOException e) {
            System.err.println("[Hata] Kaydetme hatasi: " + e.getMessage());
        }
    }

    // Ogretim elemani islemleri
    public void ogretimElemaniEkle(String adSoyad, String sicilNo, String unvan, String bolum) {
        OgretimElemani oe = new OgretimElemani(adSoyad, sicilNo, unvan, bolum);
        veriDeposu.ogretimElemaniEkle(oe);
        System.out.println("[+] Ogretim elemani eklendi: " + adSoyad);
    }

    public void ogretimElemanlariListele() {
        List<OgretimElemani> liste = veriDeposu.getOgretimElemanlari();
        if (liste.isEmpty()) {
            System.out.println("Kayitli ogretim elemani bulunmuyor.");
            return;
        }
        System.out.println("--- OGRETIM ELEMANLARI ---");
        for (int i = 0; i < liste.size(); i++) {
            OgretimElemani oe = liste.get(i);
            System.out.printf("  %d) %s %s | Bolum: %s | SicilNo: %s%n",
                i + 1, oe.getUnvan(), oe.getAdSoyad(), oe.getBolum(), oe.getSicilNo());
        }
    }

    // Derslik islemleri
    public void derslikEkle(String tur, String derslikKodu, int kapasite) {
        try {
            Derslik derslik = DerslikFactory.olustur(tur, derslikKodu, kapasite);
            veriDeposu.derslikEkle(derslik);
            System.out.println("[+] Derslik eklendi: " + derslikKodu + " (" + tur + ", " + kapasite + " kisilik)");
        } catch (IllegalArgumentException e) {
            System.err.println("[Hata] " + e.getMessage());
        }
    }

    public void dersliklerListele() {
        List<Derslik> liste = veriDeposu.getDerslikler();
        if (liste.isEmpty()) {
            System.out.println("Kayitli derslik bulunmuyor.");
            return;
        }
        System.out.println("--- DERSLIKLER ---");
        for (int i = 0; i < liste.size(); i++) {
            Derslik d = liste.get(i);
            String tur = (d instanceof Laboratuvar) ? "Laboratuvar" : "Amfi";
            System.out.printf("  %d) [%s] %s - Kapasite: %d%n",
                i + 1, tur, d.getDerslikKodu(), d.getKapasite());
        }
    }

    // Ders islemleri
    public void dersEkle(String dersKodu, String dersAdi, String sorumluHocaSicilNo,
                         int ogrenciSayisi, boolean laboratuvarGerekliMi) {

        OgretimElemani hoca = veriDeposu.getOgretimElemanlari().stream()
            .filter(oe -> oe.getSicilNo().equals(sorumluHocaSicilNo))
            .findFirst()
            .orElse(null);

        if (hoca == null) {
            System.err.println("[Hata] Sicil numarasi '" + sorumluHocaSicilNo + "' olan ogretim elemani bulunamadi.");
            return;
        }

        Ders ders = new Ders(dersKodu, dersAdi, hoca, ogrenciSayisi, laboratuvarGerekliMi);
        veriDeposu.dersEkle(ders);
        System.out.println("[+] Ders eklendi: " + dersKodu + " - " + dersAdi);
    }

    public void derslerListele() {
        List<Ders> liste = veriDeposu.getDersler();
        if (liste.isEmpty()) {
            System.out.println("Kayitli ders bulunmuyor.");
            return;
        }
        System.out.println("--- DERSLER ---");
        for (int i = 0; i < liste.size(); i++) {
            Ders d = liste.get(i);
            System.out.printf("  %d) [%s] %s | Hoca: %s | Ogrenci: %d | Lab: %s%n",
                i + 1, d.getDersKodu(), d.getDersAdi(),
                d.getSorumluHoca().getAdSoyad(),
                d.getOgrenciSayisi(),
                d.isLaboratuvarGerekliMi() ? "Evet" : "Hayir");
        }
    }

    // Ders programini planlama
    public List<AtamaKaydi> planOlustur() {
        List<Ders> dersler = new ArrayList<>(veriDeposu.getDersler());
        List<Derslik> derslikler = new ArrayList<>(veriDeposu.getDerslikler());

        if (dersler.isEmpty() || derslikler.isEmpty()) {
            System.out.println("[Uyari] Plan olusturmak icin en az bir ders ve bir derslik bulunmalidir.");
            return List.of();
        }

        DersPlanlayici tPlanlayici = new DersPlanlayici();
        List<AtamaKaydi> plan = tPlanlayici.planla(dersler, derslikler);

        System.out.println("\n--- OLUSTURULAN DERS PROGRAMI ---");
        if (plan.isEmpty()) {
            System.out.println("  Hicbir ders atanamadi!");
        } else {
            for (AtamaKaydi kayit : plan) {
                System.out.println("  " + kayit.toString());
            }
        }
        return plan;
    }

    public void manuelKaydet() {
        kapat();
    }

    public void manuelYukle() {
        baslat();
    }
}
