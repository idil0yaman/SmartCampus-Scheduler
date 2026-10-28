package patterns;

/*
 * ============================================================
 *  TASARIM KALIBI #1: FACTORY METHOD (Fabrika Metodu)
 * ============================================================
 *
 *  NEDEN KULLANDIK?
 *  İdil'in yazdığı Derslik (soyut), Amfi ve Laboratuvar sınıfları
 *  nesne oluşturma mantığını istemci koduna (Main, menü vs.) açık
 *  bırakmaktadır.  Gelecekte yeni bir derslik türü (ör. "Toplantı
 *  Salonu") eklendiğinde her yerdeki "new" çağrısını bulmak ve
 *  değiştirmek zorunda kalırız.
 *
 *  Factory Method kalıbı ile nesne üretim sorumluluğunu TEK bir
 *  noktaya (bu sınıfa) taşıyoruz.  Yeni tür geldiğinde sadece bu
 *  dosyaya bir "case" eklenir; geri kalan kod değişmez.
 *
 *  SOLID PRENSİPLERİ:
 *  - Open/Closed (OCP): Sistemi kapatmadan (mevcut kodu değiştirmeden)
 *    yeni derslik türü eklenebilir.
 *  - Single Responsibility (SRP): Yalnızca derslik nesnesi üretmekten
 *    sorumludur.
 * ============================================================
 */

import models.Amfi;
import models.Derslik;
import models.Laboratuvar;

public class DerslikFactory {

    /**
     * Verilen tür bilgisine göre doğru Derslik alt sınıfını üretir.
     *
     * @param tur          "AMFI" veya "LABORATUVAR" (büyük/küçük harf duyarsız)
     * @param derslikKodu  Dersliğin benzersiz kodu (ör. "A-101")
     * @param kapasite     Dersliğin maksimum öğrenci kapasitesi
     * @return             İlgili Derslik nesnesi
     * @throws IllegalArgumentException bilinmeyen tür girildiğinde
     */
    public static Derslik olustur(String tur, String derslikKodu, int kapasite) {
        // OCP: Yeni tür için sadece buraya yeni case eklenir.
        switch (tur.toUpperCase()) {
            case "AMFI":
                return new Amfi(derslikKodu, kapasite);
            case "LABORATUVAR":
                return new Laboratuvar(derslikKodu, kapasite);
            default:
                throw new IllegalArgumentException(
                    "Bilinmeyen derslik türü: '" + tur + "'. " +
                    "Geçerli türler: AMFI, LABORATUVAR"
                );
        }
    }
}
