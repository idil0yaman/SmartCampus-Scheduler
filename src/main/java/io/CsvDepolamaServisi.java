package io;

import models.Amfi;
import models.Ders;
import models.Derslik;
import models.Laboratuvar;
import models.OgretimElemani;
import patterns.DerslikFactory;
import patterns.VeriDeposu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Verileri CSV formatında diske kaydeden ve diskten okuyan depolama servisi.
 */
public class CsvDepolamaServisi implements KaliciDepolamaServisi {

    private static final String VERI_KLASORU = "veri";
    private static final String HOCA_DOSYASI = VERI_KLASORU + "/ogretim_elemanlari.csv";
    private static final String DERSLIK_DOSYASI = VERI_KLASORU + "/derslikler.csv";
    private static final String DERS_DOSYASI = VERI_KLASORU + "/dersler.csv";
    private static final String AYRAC = ";";

    private static final String HOCA_BASLIK = "adSoyad;sicilNo;unvan;bolum";
    private static final String DERSLIK_BASLIK = "tur;derslikKodu;kapasite";
    private static final String DERS_BASLIK = "dersKodu;dersAdi;sorumluHocaSicilNo;ogrenciSayisi;laboratuvarGerekliMi";

    @Override
    public void kaydet() throws IOException {
        klasorOlustur();
        VeriDeposu depo = VeriDeposu.getInstance();

        hocalariKaydet(depo.getOgretimElemanlari());
        derslikleriKaydet(depo.getDerslikler());
        dersleriKaydet(depo.getDersler(), depo.getOgretimElemanlari());

        System.out.println("[Bilgi] Veriler basariyla CSV dosyalarina kaydedildi.");
    }

    @Override
    public void yukle() throws IOException {
        VeriDeposu depo = VeriDeposu.getInstance();
        depo.tumunuTemizle();

        if (Files.exists(Paths.get(HOCA_DOSYASI))) {
            hocalariYukle(depo);
        }
        if (Files.exists(Paths.get(DERSLIK_DOSYASI))) {
            derslikleriYukle(depo);
        }
        if (Files.exists(Paths.get(DERS_DOSYASI))) {
            dersleriYukle(depo);
        }

        System.out.println("[Bilgi] Veriler CSV dosyalarindan basariyla yuklendi.");
    }

    private void klasorOlustur() throws IOException {
        Files.createDirectories(Paths.get(VERI_KLASORU));
    }

    private void hocalariKaydet(List<OgretimElemani> liste) throws IOException {
        try (BufferedWriter yazar = yeniYazici(HOCA_DOSYASI)) {
            yazar.write(HOCA_BASLIK);
            yazar.newLine();
            for (OgretimElemani oe : liste) {
                yazar.write(
                    kacis(oe.getAdSoyad()) + AYRAC +
                    kacis(oe.getSicilNo()) + AYRAC +
                    kacis(oe.getUnvan()) + AYRAC +
                    kacis(oe.getBolum())
                );
                yazar.newLine();
            }
        }
    }

    private void derslikleriKaydet(List<Derslik> liste) throws IOException {
        try (BufferedWriter yazar = yeniYazici(DERSLIK_DOSYASI)) {
            yazar.write(DERSLIK_BASLIK);
            yazar.newLine();
            for (Derslik d : liste) {
                String tur = (d instanceof Laboratuvar) ? "LABORATUVAR" : "AMFI";
                yazar.write(
                    kacis(tur) + AYRAC +
                    kacis(d.getDerslikKodu()) + AYRAC +
                    d.getKapasite()
                );
                yazar.newLine();
            }
        }
    }

    private void dersleriKaydet(List<Ders> dersler, List<OgretimElemani> hocalar) throws IOException {
        try (BufferedWriter yazar = yeniYazici(DERS_DOSYASI)) {
            yazar.write(DERS_BASLIK);
            yazar.newLine();
            for (Ders d : dersler) {
                yazar.write(
                    kacis(d.getDersKodu()) + AYRAC +
                    kacis(d.getDersAdi()) + AYRAC +
                    kacis(d.getSorumluHoca().getSicilNo()) + AYRAC +
                    d.getOgrenciSayisi() + AYRAC +
                    d.isLaboratuvarGerekliMi()
                );
                yazar.newLine();
            }
        }
    }

    private void hocalariYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(HOCA_DOSYASI)) {
            String satir = okuyucu.readLine(); // Baslik satiri
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 4) continue;

                OgretimElemani oe = new OgretimElemani(
                    parcalar[0].trim(),
                    parcalar[1].trim(),
                    parcalar[2].trim(),
                    parcalar[3].trim()
                );
                depo.ogretimElemaniEkle(oe);
            }
        }
    }

    private void derslikleriYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(DERSLIK_DOSYASI)) {
            String satir = okuyucu.readLine(); // Baslik satiri
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 3) continue;

                String tur = parcalar[0].trim();
                String derslikKodu = parcalar[1].trim();
                int kapasite = Integer.parseInt(parcalar[2].trim());

                Derslik derslik = DerslikFactory.olustur(tur, derslikKodu, kapasite);
                depo.derslikEkle(derslik);
            }
        }
    }

    private void dersleriYukle(VeriDeposu depo) throws IOException {
        try (BufferedReader okuyucu = yeniOkuyucu(DERS_DOSYASI)) {
            String satir = okuyucu.readLine(); // Baslik satiri
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] parcalar = satir.split(AYRAC, -1);
                if (parcalar.length < 5) continue;

                String dersKodu = parcalar[0].trim();
                String dersAdi = parcalar[1].trim();
                String sicilNo = parcalar[2].trim();
                int ogrenciSayisi = Integer.parseInt(parcalar[3].trim());
                boolean labGerekliMi = Boolean.parseBoolean(parcalar[4].trim());

                OgretimElemani hoca = depo.getOgretimElemanlari().stream()
                    .filter(oe -> oe.getSicilNo().equals(sicilNo))
                    .findFirst()
                    .orElse(null);

                if (hoca == null) {
                    System.err.println("[Uyari] '" + dersKodu + "' dersinin hocasi bulunamadi, atlaniyor.");
                    continue;
                }

                Ders ders = new Ders(dersKodu, dersAdi, hoca, ogrenciSayisi, labGerekliMi);
                depo.dersEkle(ders);
            }
        }
    }

    private BufferedWriter yeniYazici(String yol) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(yol), StandardCharsets.UTF_8));
    }

    private BufferedReader yeniOkuyucu(String yol) throws IOException {
        return new BufferedReader(new InputStreamReader(
            new FileInputStream(yol), StandardCharsets.UTF_8));
    }

    private String kacis(String deger) {
        if (deger == null) return "";
        if (deger.contains(AYRAC) || deger.contains("\"")) {
            return "\"" + deger.replace("\"", "\"\"") + "\"";
        }
        return deger;
    }
}
