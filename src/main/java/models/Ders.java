package models;

public class Ders {
    private String dersKodu;
    private String dersAdi;
    private OgretimElemani sorumluHoca;
    private int ogrenciSayisi;
    private boolean laboratuvarGerekliMi;

    public Ders(String dersKodu, String dersAdi, OgretimElemani sorumluHoca, int ogrenciSayisi, boolean laboratuvarGerekliMi) {
        this.dersKodu = dersKodu;
        this.dersAdi = dersAdi;
        this.sorumluHoca = sorumluHoca;
        this.ogrenciSayisi = ogrenciSayisi;
        this.laboratuvarGerekliMi = laboratuvarGerekliMi;
    }

    public String getDersKodu() { return dersKodu; }
    public String getDersAdi() { return dersAdi; }
    public OgretimElemani getSorumluHoca() { return sorumluHoca; }
    public int getOgrenciSayisi() { return ogrenciSayisi; }
    public boolean isLaboratuvarGerekliMi() { return laboratuvarGerekliMi; }
}
