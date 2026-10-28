package patterns;

/*
 * ============================================================
 *  TASARIM KALIBI #2: SINGLETON
 * ============================================================
 *
 *  NEDEN KULLANDIK?
 *  Uygulama boyunca ders, derslik ve öğretim elemanı listelerinin
 *  TEK bir merkezi yerden yönetilmesi gerekir.  Birden fazla örnek
 *  (instance) olsaydı farklı menü ekranları veya servisler birbirinden
 *  habersiz farklı listelerle çalışabilirdi → veri tutarsızlığı.
 *
 *  Singleton, bu merkezi havuzun sadece TEK bir instance'ının var
 *  olmasını garanti eder.  Tüm servisler ve menüler aynı objeye
 *  ulaşır; bu da veriyi tutarlı tutar.
 *
 *  SOLID PRENSİPLERİ:
 *  - Single Responsibility (SRP): Yalnızca uygulama genelindeki
 *    çalışma-zamanı veri listelerini tutar.
 *  - Dependency Inversion (DIP): Üst katmanlar (menü) somut liste
 *    tipine bağlanmak yerine bu merkezi servise bağlanır.
 * ============================================================
 */

import models.Ders;
import models.Derslik;
import models.OgretimElemani;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VeriDeposu {

    // -------------------------------------------------------
    // Singleton garantisi: tek örnek, thread-safe lazy init
    // -------------------------------------------------------
    private static VeriDeposu tekOrnek = null;

    /** Dışarıdan new VeriDeposu() çağrılmasını engeller (Singleton). */
    private VeriDeposu() {
        dersler           = new ArrayList<>();
        derslikler        = new ArrayList<>();
        ogretimElemanlari = new ArrayList<>();
    }

    /**
     * Tek instance'a erişim noktası.
     * İlk çağrıda oluşturur, sonrakinde aynı nesneyi döner.
     */
    public static VeriDeposu getInstance() {
        if (tekOrnek == null) {
            tekOrnek = new VeriDeposu();
        }
        return tekOrnek;
    }

    // -------------------------------------------------------
    // Kapsülleme (Encapsulation): Alanlar private, erişim
    // metodlar aracılığıyla — NYP ilkesi.
    // -------------------------------------------------------
    private final List<Ders>            dersler;
    private final List<Derslik>         derslikler;
    private final List<OgretimElemani>  ogretimElemanlari;

    // --- Ders işlemleri ---
    public void derEkle(Ders d)             { dersler.add(d); }
    public void dersSil(Ders d)             { dersler.remove(d); }
    public List<Ders> getDersler()          { return Collections.unmodifiableList(dersler); }

    // --- Derslik işlemleri ---
    public void derslikEkle(Derslik d)      { derslikler.add(d); }
    public void derslikSil(Derslik d)       { derslikler.remove(d); }
    public List<Derslik> getDerslikler()    { return Collections.unmodifiableList(derslikler); }

    // --- Öğretim Elemanı işlemleri ---
    public void ogretimElemaniEkle(OgretimElemani oe)      { ogretimElemanlari.add(oe); }
    public void ogretimElemaniSil(OgretimElemani oe)       { ogretimElemanlari.remove(oe); }
    public List<OgretimElemani> getOgretimElemanlari()     { return Collections.unmodifiableList(ogretimElemanlari); }

    /**
     * Test veya yükleme sonrası tüm listeleri temizler.
     * Özellikle dosyadan yükleme öncesinde çağrılır.
     */
    public void tumunuTemizle() {
        dersler.clear();
        derslikler.clear();
        ogretimElemanlari.clear();
    }
}
