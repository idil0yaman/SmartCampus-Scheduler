package models;

public class Laboratuvar extends Derslik {

    public Laboratuvar(String derslikKodu, int kapasite) {
        super(derslikKodu, kapasite);
    }

    @Override
    public boolean uygunMu(int ogrenciSayisi, boolean laboratuvarGerekliMi) {
        // Kapasite yetiyorsa VE laboratuvar gerekiyorsa uygundur
        return (this.getKapasite() >= ogrenciSayisi) && laboratuvarGerekliMi;
    }
}