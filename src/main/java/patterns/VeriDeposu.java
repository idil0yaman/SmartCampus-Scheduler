package patterns;

import models.Ders;
import models.Derslik;
import models.OgretimElemani;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uygulama genelindeki verilerin bellekte tek bir merkezde tutulmasını
 * sağlayan Singleton sınıfı. Bu sayede servisler ve menü aynı veri kümesi
 * üzerinde tutarlı bir şekilde işlem yapar.
 */
public class VeriDeposu {

    private static VeriDeposu tekOrnek = null;

    private final List<Ders> dersler;
    private final List<Derslik> derslikler;
    private final List<OgretimElemani> ogretimElemanlari;

    // Singleton gereği yapıcı private tutulur
    private VeriDeposu() {
        dersler = new ArrayList<>();
        derslikler = new ArrayList<>();
        ogretimElemanlari = new ArrayList<>();
    }

    public static VeriDeposu getInstance() {
        if (tekOrnek == null) {
            tekOrnek = new VeriDeposu();
        }
        return tekOrnek;
    }

    // Ders işlemleri
    public void dersEkle(Ders d) {
        dersler.add(d);
    }

    // Geriye dönük uyumluluk için
    public void derEkle(Ders d) {
        dersEkle(d);
    }

    public void dersSil(Ders d) {
        dersler.remove(d);
    }

    public List<Ders> getDersler() {
        return Collections.unmodifiableList(dersler);
    }

    // Derslik işlemleri
    public void derslikEkle(Derslik d) {
        derslikler.add(d);
    }

    public void derslikSil(Derslik d) {
        derslikler.remove(d);
    }

    public List<Derslik> getDerslikler() {
        return Collections.unmodifiableList(derslikler);
    }

    // Öğretim elemanı işlemleri
    public void ogretimElemaniEkle(OgretimElemani oe) {
        ogretimElemanlari.add(oe);
    }

    public void ogretimElemaniSil(OgretimElemani oe) {
        ogretimElemanlari.remove(oe);
    }

    public List<OgretimElemani> getOgretimElemanlari() {
        return Collections.unmodifiableList(ogretimElemanlari);
    }

    // Test ve dosya yükleme öncesi listeleri sıfırlama
    public void tumunuTemizle() {
        dersler.clear();
        derslikler.clear();
        ogretimElemanlari.clear();
    }
}
