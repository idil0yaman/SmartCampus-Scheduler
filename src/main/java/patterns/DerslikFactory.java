package patterns;

import models.Amfi;
import models.Derslik;
import models.Laboratuvar;

/**
 * Derslik nesnelerini ihtiyaca göre oluşturan Factory (Fabrika) sınıfı.
 * Yeni bir derslik türü eklendiğinde istemci kodları değiştirmeden
 * nesne üretimini tek bir noktadan yönetmemizi sağlar (Open/Closed Prensibi).
 */
public class DerslikFactory {

    /**
     * Tür bilgisine göre ilgili alt sınıfı üretir.
     *
     * @param tur         "AMFI" veya "LABORATUVAR"
     * @param derslikKodu Derslik kodu (ör: A-101)
     * @param kapasite    Öğrenci kapasitesi
     * @return Oluşturulan Derslik nesnesi
     */
    public static Derslik olustur(String tur, String derslikKodu, int kapasite) {
        if (tur == null) {
            throw new IllegalArgumentException("Derslik türü boş olamaz.");
        }

        switch (tur.trim().toUpperCase()) {
            case "AMFI":
                return new Amfi(derslikKodu, kapasite);
            case "LABORATUVAR":
                return new Laboratuvar(derslikKodu, kapasite);
            default:
                throw new IllegalArgumentException(
                    "Bilinmeyen derslik türü: '" + tur + "'. Geçerli türler: AMFI, LABORATUVAR"
                );
        }
    }
}
