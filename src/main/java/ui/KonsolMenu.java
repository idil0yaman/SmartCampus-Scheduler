package ui;

import io.CsvDepolamaServisi;
import services.UygulamaServisi;
import java.util.Scanner;

public class KonsolMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static UygulamaServisi servis;

    public static void main(String[] args) {
        servis = new UygulamaServisi(new CsvDepolamaServisi());
        servis.baslat();

        boolean calisiyorMu = true;
        System.out.println(">>> Akıllı Kampüs Sistemi Başlatıldı <<<");

        while (calisiyorMu) {
            menuGoster();
            int secim = tamSayiOku("Seçiminiz: ");

            switch (secim) {
                case 1:
                    ogretimElemaniEkleFormu();
                    break;
                case 2:
                    System.out.println("\n--- KAYITLI ÖĞRETİM ELEMANLARI ---");
                    servis.ogretimElemanlariListele();
                    break;
                case 3:
                    derslikEkleFormu();
                    break;
                case 4:
                    System.out.println("\n--- KAYITLI DERSLİKLER ---");
                    servis.dersliklerListele();
                    break;
                case 5:
                    dersEkleFormu();
                    break;
                case 6:
                    System.out.println("\n--- KAYITLI DERSLER ---");
                    servis.derslerListele();
                    break;
                case 7:
                    System.out.println("\n--- DERS PROGRAMI OLUŞTURULUYOR ---");
                    servis.planOlustur();
                    break;
                case 8:
                    servis.manuelKaydet();
                    break;
                case 9:
                    servis.manuelYukle();
                    break;
                case 0:
                    servis.kapat();
                    calisiyorMu = false;
                    System.out.println("\n[+] Veriler diske kaydedildi. Sistemden çıkılıyor. İyi günler!");
                    break;
                default:
                    System.out.println("\n[!] Geçersiz seçim. Lütfen 0 ile 9 arasında bir değer giriniz.");
            }
        }

        scanner.close();
    }

    private static void menuGoster() {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║    AKILLI KAMPÜS DERS PROGRAMI SİSTEMİ  ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║  1) Öğretim Elemanı Ekle                ║");
        System.out.println("║  2) Öğretim Elemanlarını Listele        ║");
        System.out.println("║  3) Derslik Ekle (Amfi / Laboratuvar)   ║");
        System.out.println("║  4) Derslikleri Listele                 ║");
        System.out.println("║  5) Ders Ekle                           ║");
        System.out.println("║  6) Dersleri Listele                    ║");
        System.out.println("║  7) Ders Programı Oluştur               ║");
        System.out.println("║  8) Verileri Kaydet (Manuel)            ║");
        System.out.println("║  9) Verileri Diskten Yükle              ║");
        System.out.println("║  0) Çıkış                               ║");
        System.out.println("╚═════════════════════════════════════════╝");
    }

    private static void ogretimElemaniEkleFormu() {
        System.out.println("\n--- ÖĞRETİM ELEMANI EKLE ---");
        String ad = metinOku("Ad Soyad: ");
        String sicil = metinOku("Sicil No: ");
        String unvan = metinOku("Unvan (ör. Prof. Dr., Doç. Dr.): ");
        String bolum = metinOku("Bölüm: ");

        servis.ogretimElemaniEkle(ad, sicil, unvan, bolum);
        System.out.println("[+] Öğretim elemanı başarıyla eklendi.");
    }

    private static void derslikEkleFormu() {
        System.out.println("\n--- DERSLİK EKLE ---");
        String tur = metinOku("Tür (AMFI / LABORATUVAR): ").toUpperCase();

        while (!tur.equals("AMFI") && !tur.equals("LABORATUVAR")) {
            System.out.println("[!] Hatalı tür! Sadece 'AMFI' veya 'LABORATUVAR' girebilirsiniz.");
            tur = metinOku("Tür (AMFI / LABORATUVAR): ").toUpperCase();
        }

        String kod = metinOku("Derslik Kodu (ör. A-101, LAB-1): ");
        int kapasite = tamSayiOku("Kapasite: ");

        servis.derslikEkle(tur, kod, kapasite);
        System.out.println("[+] " + tur + " türündeki derslik başarıyla eklendi.");
    }

    private static void dersEkleFormu() {
        System.out.println("\n--- DERS EKLE ---");
        String dersKodu = metinOku("Ders Kodu (ör. BGM201): ");
        String dersAdi = metinOku("Ders Adı: ");
        String hocaSicil = metinOku("Sorumlu Hoca Sicil No: ");
        int sayi = tamSayiOku("Öğrenci Sayısı: ");
        boolean labMi = booleanOku("Laboratuvar Gerekli mi? (E/H veya true/false): ");

        servis.dersEkle(dersKodu, dersAdi, hocaSicil, sayi, labMi);
        System.out.println("[+] Ders kaydı oluşturuldu.");
    }

    private static String metinOku(String mesaj) {
        System.out.print(mesaj);
        String girdi = scanner.nextLine().trim();
        while (girdi.isEmpty()) {
            System.out.print("[!] Boş bırakılamaz. " + mesaj);
            girdi = scanner.nextLine().trim();
        }
        return girdi;
    }

    private static int tamSayiOku(String mesaj) {
        while (true) {
            try {
                System.out.print(mesaj);
                String girdi = scanner.nextLine().trim();
                return Integer.parseInt(girdi);
            } catch (NumberFormatException e) {
                System.out.println("[!] Hata: Lütfen geçerli bir tam sayı giriniz.");
            }
        }
    }

    private static boolean booleanOku(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String girdi = scanner.nextLine().trim().toLowerCase();
            if (girdi.equals("true") || girdi.equals("e") || girdi.equals("evet")) {
                return true;
            } else if (girdi.equals("false") || girdi.equals("h") || girdi.equals("hayır") || girdi.equals("hayir")) {
                return false;
            } else {
                System.out.println("[!] Hata: Lütfen 'E' (Evet) veya 'H' (Hayır) giriniz.");
            }
        }
    }
}
