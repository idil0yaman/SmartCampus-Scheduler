package models;

public class AtamaKaydi {
    private Ders ders;
    private Derslik derslik;
    private ZamanDilimi zamanDilimi;

    public AtamaKaydi(Ders ders, Derslik derslik, ZamanDilimi zamanDilimi) {
        this.ders = ders;
        this.derslik = derslik;
        this.zamanDilimi = zamanDilimi;
    }

    public Ders getDers() { return ders; }
    public Derslik getDerslik() { return derslik; }
    public ZamanDilimi getZamanDilimi() { return zamanDilimi; }

    @Override
    public String toString() {
        return ders.getDersKodu() + " - " + ders.getDersAdi() +
                " | Sınıf: " + derslik.getDerslikKodu() +
                " | Zaman: " + zamanDilimi.toString() +
                " | Hoca: " + ders.getSorumluHoca().getAdSoyad();
    }
}
