import models.*;
import services.DersPlanlayici;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Test Verilerini (Dummy Data) Oluşturma
        OgretimElemani hoca1 = new OgretimElemani("Prof. Dr. Ahmet Yılmaz", "101", "Yazılım Mühendisliği", "Bilgisayar");
        OgretimElemani hoca2 = new OgretimElemani("Doç. Dr. Ayşe Kaya", "102", "Matematik", "Temel Bilimler");

        List<Derslik> derslikler = new ArrayList<>();
        derslikler.add(new Amfi("A-101", 100));       // Amfi: Teorik dersler için, kapasite 100
        derslikler.add(new Laboratuvar("LAB-1", 40)); // Laboratuvar: Pratik dersler için, kapasite 40

        List<Ders> dersler = new ArrayList<>();
        // Senaryo 1: Lab gerektiren 30 kişilik ders (LAB-1'e atanmalı)
        dersler.add(new Ders("BGM201", "Nesneye Yönelik Programlama", hoca1, 30, true));

        // Senaryo 2: Lab gerektirmeyen 80 kişilik teorik ders (A-101'e atanmalı)
        dersler.add(new Ders("MAT101", "Matematik-I", hoca2, 80, false));

        // Senaryo 3: Lab gerektiren ama kapasitesi yetmeyen ders (Atanamayıp uyarı vermeli)
        dersler.add(new Ders("BGM203", "Veri Yapıları", hoca1, 50, true));

        // 2. Algoritmayı Çalıştırma
        DersPlanlayici planlayici = new DersPlanlayici();
        List<AtamaKaydi> program = planlayici.planla(dersler, derslikler);

        // 3. Sonuçları Konsola Yazdırma
        System.out.println("--- OLUŞTURULAN DERS PROGRAMI ---");
        for (AtamaKaydi kayit : program) {
            System.out.println(kayit.toString());
        }
    }
}
