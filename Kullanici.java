package models;

public abstract class Kullanici {
    private String adSoyad;
    private String sicilNo;

    public Kullanici(String adSoyad, String sicilNo) {
        this.adSoyad = adSoyad;
        this.sicilNo = sicilNo;
    }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getSicilNo() { return sicilNo; }
    public void setSicilNo(String sicilNo) { this.sicilNo = sicilNo; }
}