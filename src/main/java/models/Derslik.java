package models;

public abstract class Derslik {
    private String derslikKodu;
    private int kapasite;

    public Derslik(String derslikKodu, int kapasite) {
        this.derslikKodu = derslikKodu;
        this.kapasite = kapasite;
    }

    // Alt sınıfların kendi mantıklarına göre doldurmak zorunda olduğu soyut metot
    public abstract boolean uygunMu(int ogrenciSayisi, boolean laboratuvarGerekliMi);

    public String getDerslikKodu() { return derslikKodu; }
    public void setDerslikKodu(String derslikKodu) { this.derslikKodu = derslikKodu; }

    public int getKapasite() { return kapasite; }
    public void setKapasite(int kapasite) { this.kapasite = kapasite; }
}
