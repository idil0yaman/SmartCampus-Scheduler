package models;

public class BolumYoneticisi extends Kullanici {
    private String yonetilenBolum;

    public BolumYoneticisi(String adSoyad, String sicilNo, String yonetilenBolum) {
        super(adSoyad, sicilNo);
        this.yonetilenBolum = yonetilenBolum;
    }

    public String getYonetilenBolum() {
        return yonetilenBolum;
    }

    public void setYonetilenBolum(String yonetilenBolum) {
        this.yonetilenBolum = yonetilenBolum;
    }
}
