package models;

public class Amfi extends Derslik {

    public Amfi(String derslikKodu, int kapasite) {
        super(derslikKodu, kapasite);
    }

    @Override
    public boolean uygunMu(int ogrenciSayisi, boolean laboratuvarGerekliMi) {
        // Kapasite yetiyorsa VE laboratuvar gerekmiyorsa uygundur
        return (this.getKapasite() >= ogrenciSayisi) && !laboratuvarGerekliMi;
    }
}
