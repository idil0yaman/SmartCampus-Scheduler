package services;

import models.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DersPlanlayici {
    private List<AtamaKaydi> atamaListesi;

    // Çakışmaları O(1) karmaşıklığında hızlıca kontrol etmek için Hash tabloları (Set) kullanıyoruz
    private Set<String> doluHocaZamanlari;
    private Set<String> doluDerslikZamanlari;
    private List<ZamanDilimi> haftalikSaatler;

    public DersPlanlayici() {
        this.atamaListesi = new ArrayList<>();
        this.doluHocaZamanlari = new HashSet<>();
        this.doluDerslikZamanlari = new HashSet<>();
        this.haftalikSaatler = varsayilanZamanDilimleriniOlustur();
    }

    public List<AtamaKaydi> planla(List<Ders> dersler, List<Derslik> derslikler) {
        for (Ders ders : dersler) {
            boolean atandiMi = false;

            for (Derslik derslik : derslikler) {
                if (atandiMi) break;

                // 1. KONTROL (Çok Biçimlilik Devrede): Sınıfın tipi ve kapasitesi bu ders için uygun mu?
                if (!derslik.uygunMu(ders.getOgrenciSayisi(), ders.isLaboratuvarGerekliMi())) {
                    continue; // Uygun değilse diğer dersliğe geç
                }

                for (ZamanDilimi zaman : haftalikSaatler) {
                    // Çakışma kontrolü için benzersiz anahtarlar üretiyoruz
                    String hocaZamanAnahtari = ders.getSorumluHoca().getSicilNo() + "_" + zaman.toString();
                    String derslikZamanAnahtari = derslik.getDerslikKodu() + "_" + zaman.toString();

                    // 2. ve 3. KONTROL: Hoca ve Derslik o saatte boş mu?
                    if (!doluHocaZamanlari.contains(hocaZamanAnahtari) && !doluDerslikZamanlari.contains(derslikZamanAnahtari)) {

                        // Tüm kısıtlar sağlandı, eşleşme başarılı!
                        AtamaKaydi yeniAtama = new AtamaKaydi(ders, derslik, zaman);
                        atamaListesi.add(yeniAtama);

                        // Kaynakları o saat için "meşgul" olarak işaretle (Hash tablolarına ekle)
                        doluHocaZamanlari.add(hocaZamanAnahtari);
                        doluDerslikZamanlari.add(derslikZamanAnahtari);

                        atandiMi = true;
                        break; // Bu dersin ataması bitti, sıradaki derse geçebiliriz
                    }
                }
            }

            if (!atandiMi) {
                System.out.println("Sistem Uyarısı: '" + ders.getDersAdi() + "' dersi için uygun sınıf kapasitesi veya boş zaman bulunamadı!");
            }
        }
        return atamaListesi;
    }

    // Algoritmanın kullanacağı haftalık şablonu oluşturan yardımcı metot
    private List<ZamanDilimi> varsayilanZamanDilimleriniOlustur() {
        List<ZamanDilimi> zamanlar = new ArrayList<>();
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        String[] saatler = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};

        for (String gun : gunler) {
            for (String saat : saatler) {
                zamanlar.add(new ZamanDilimi(gun, saat));
            }
        }
        return zamanlar;
    }
}