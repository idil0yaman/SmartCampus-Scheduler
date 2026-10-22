package models;

public class OgretimElemani extends Kullanici {
    private String unvan;
    private String bolum;

    public OgretimElemani(String adSoyad, String sicilNo, String unvan, String bolum) {
        super(adSoyad, sicilNo); // Üst sınıfın kurucusunu (constructor) çağırır
        this.unvan = unvan;
        this.bolum = bolum;
    }

    public String getUnvan() { return unvan; }
    public void setUnvan(String unvan) { this.unvan = unvan; }

    public String getBolum() { return bolum; }
    public void setBolum(String bolum) { this.bolum = bolum; }
}
