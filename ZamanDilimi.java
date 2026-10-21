package models;

public class ZamanDilimi {
    private String gun;
    private String saatAraligi;

    public ZamanDilimi(String gun, String saatAraligi) {
        this.gun = gun;
        this.saatAraligi = saatAraligi;
    }

    public String getGun() { return gun; }
    public void setGun(String gun) { this.gun = gun; }

    public String getSaatAraligi() { return saatAraligi; }
    public void setSaatAraligi(String saatAraligi) { this.saatAraligi = saatAraligi; }

    @Override
    public String toString() {
        return gun + " " + saatAraligi;
    }
}