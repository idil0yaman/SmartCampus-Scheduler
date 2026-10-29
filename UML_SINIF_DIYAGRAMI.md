# Akıllı Kampüs Ders Programı Sistemi - UML Sınıf Diyagramı

Bu doküman, projenin mimari yapısını, sınıflar arası ilişkileri, uygulanan nesneye yönelik programlama ilkelerini ve tasarım kalıplarını açıklamak üzere hazırlanmıştır.

---

## 1. UML Sınıf Diyagramı (Mermaid)

```mermaid
classDiagram
    direction TB

    class Kullanici {
        <<abstract>>
        -String adSoyad
        -String sicilNo
        +Kullanici(adSoyad: String, sicilNo: String)
        +getAdSoyad() String
        +setAdSoyad(adSoyad: String) void
        +getSicilNo() String
        +setSicilNo(sicilNo: String) void
    }

    class OgretimElemani {
        -String unvan
        -String bolum
        +OgretimElemani(adSoyad: String, sicilNo: String, unvan: String, bolum: String)
        +getUnvan() String
        +setUnvan(unvan: String) void
        +getBolum() String
        +setBolum(bolum: String) void
    }

    class BolumYoneticisi {
        -String yonetilenBolum
        +BolumYoneticisi(adSoyad: String, sicilNo: String, yonetilenBolum: String)
        +getYonetilenBolum() String
        +setYonetilenBolum(yonetilenBolum: String) void
    }

    class Derslik {
        <<abstract>>
        -String derslikKodu
        -int kapasite
        +Derslik(derslikKodu: String, kapasite: int)
        +uygunMu(ogrenciSayisi: int, laboratuvarGerekliMi: boolean)* boolean
        +getDerslikKodu() String
        +setDerslikKodu(derslikKodu: String) void
        +getKapasite() int
        +setKapasite(kapasite: int) void
    }

    class Amfi {
        +Amfi(derslikKodu: String, kapasite: int)
        +uygunMu(ogrenciSayisi: int, laboratuvarGerekliMi: boolean) boolean
    }

    class Laboratuvar {
        +Laboratuvar(derslikKodu: String, kapasite: int)
        +uygunMu(ogrenciSayisi: int, laboratuvarGerekliMi: boolean) boolean
    }

    class Ders {
        -String dersKodu
        -String dersAdi
        -OgretimElemani sorumluHoca
        -int ogrenciSayisi
        -boolean laboratuvarGerekliMi
        +Ders(dersKodu: String, dersAdi: String, sorumluHoca: OgretimElemani, ogrenciSayisi: int, lab: boolean)
        +getDersKodu() String
        +getDersAdi() String
        +getSorumluHoca() OgretimElemani
        +getOgrenciSayisi() int
        +isLaboratuvarGerekliMi() boolean
    }

    class ZamanDilimi {
        -String gun
        -String saatAraligi
        +ZamanDilimi(gun: String, saatAraligi: String)
        +getGun() String
        +setGun(gun: String) void
        +getSaatAraligi() String
        +setSaatAraligi(saatAraligi: String) void
        +toString() String
    }

    class AtamaKaydi {
        -Ders ders
        -Derslik derslik
        -ZamanDilimi zamanDilimi
        +AtamaKaydi(ders: Ders, derslik: Derslik, zamanDilimi: ZamanDilimi)
        +getDers() Ders
        +getDerslik() Derslik
        +getZamanDilimi() ZamanDilimi
        +toString() String
    }

    class DerslikFactory {
        <<Factory>>
        +olustur(tur: String, derslikKodu: String, kapasite: int)$ Derslik
    }

    class VeriDeposu {
        <<Singleton>>
        -VeriDeposu tekOrnek$
        -List~Ders~ dersler
        -List~Derslik~ derslikler
        -List~OgretimElemani~ ogretimElemanlari
        -VeriDeposu()
        +getInstance()$ VeriDeposu
        +dersEkle(d: Ders) void
        +dersSil(d: Ders) void
        +getDersler() List~Ders~
        +derslikEkle(d: Derslik) void
        +derslikSil(d: Derslik) void
        +getDerslikler() List~Derslik~
        +ogretimElemaniEkle(oe: OgretimElemani) void
        +ogretimElemaniSil(oe: OgretimElemani) void
        +getOgretimElemanlari() List~OgretimElemani~
        +tumunuTemizle() void
    }

    class DersPlanlayici {
        <<Algorithm>>
        -List~AtamaKaydi~ atamaListesi
        -Set~String~ doluHocaZamanlari
        -Set~String~ doluDerslikZamanlari
        -List~ZamanDilimi~ haftalikSaatler
        +DersPlanlayici()
        +planla(dersler: List~Ders~, derslikler: List~Derslik~) List~AtamaKaydi~
        -varsayilanZamanDilimleriniOlustur() List~ZamanDilimi~
    }

    class UygulamaServisi {
        <<Facade>>
        -KaliciDepolamaServisi depolamaServisi
        -VeriDeposu veriDeposu
        -DersPlanlayici planlayici
        +UygulamaServisi(depolamaServisi: KaliciDepolamaServisi)
        +baslat() void
        +kapat() void
        +ogretimElemaniEkle(adSoyad: String, sicilNo: String, unvan: String, bolum: String) void
        +ogretimElemanlariListele() void
        +derslikEkle(tur: String, derslikKodu: String, kapasite: int) void
        +dersliklerListele() void
        +dersEkle(dersKodu: String, dersAdi: String, sorumluHocaSicilNo: String, sayi: int, labMi: boolean) void
        +derslerListele() void
        +planOlustur() List~AtamaKaydi~
        +manuelKaydet() void
        +manuelYukle() void
    }

    class KaliciDepolamaServisi {
        <<interface>>
        +kaydet()* void
        +yukle()* void
    }

    class CsvDepolamaServisi {
        -String VERI_KLASORU$
        -String HOCA_DOSYASI$
        -String DERSLIK_DOSYASI$
        -String DERS_DOSYASI$
        -String AYRAC$
        +kaydet() void
        +yukle() void
    }

    class KonsolMenu {
        +main(args: String[])$ void
    }

    class Main {
        +main(args: String[])$ void
    }

    %% Kalıtım
    Kullanici <|-- OgretimElemani
    Kullanici <|-- BolumYoneticisi
    Derslik <|-- Amfi
    Derslik <|-- Laboratuvar

    %% Arayüz
    KaliciDepolamaServisi <|.. CsvDepolamaServisi

    %% İlişkiler
    Ders --> OgretimElemani : sorumluHoca
    AtamaKaydi --> Ders : ders
    AtamaKaydi --> Derslik : derslik
    AtamaKaydi --> ZamanDilimi : zamanDilimi

    VeriDeposu o-- Ders
    VeriDeposu o-- Derslik
    VeriDeposu o-- OgretimElemani

    UygulamaServisi --> KaliciDepolamaServisi
    UygulamaServisi --> VeriDeposu
    UygulamaServisi --> DersPlanlayici

    KonsolMenu --> UygulamaServisi
    Main --> KonsolMenu

    DerslikFactory ..> Derslik : creates
    CsvDepolamaServisi ..> VeriDeposu : uses
    CsvDepolamaServisi ..> DerslikFactory : uses
```

---

## 2. Tasarım ve Mimari Kararları

- **Kalıtım ve Polimorfizm:** `Derslik` sınıfı soyut bırakılarak `Amfi` ve `Laboratuvar` alt sınıflarına uygunluk mantığı devredilmiştir. Algoritma somut tipleri sorgulamadan `derslik.uygunMu(...)` üzerinden polimorfik olarak çalışır.
- **Factory Pattern:** Derslik nesnelerinin üretimi `DerslikFactory` sınıfına verilerek istemci sınıfların doğrudan `new` anahtar kelimesi ile sınıflara bağlanması engellenmiştir.
- **Singleton Pattern:** Uygulama çalışma zamanındaki verilerin tek bir merkezden (`VeriDeposu`) yönetilmesi sağlanmıştır.
- **Façade Pattern:** Konsol menüsünün alt sistemleri ayrı ayrı yönetmek yerine sadece `UygulamaServisi` ile haberleşmesi sağlanarak gevşek bağlılık (loose coupling) elde edilmiştir.
- **File I/O:** `KaliciDepolamaServisi` arayüzü ile dosya saklama soyutlanmış, `CsvDepolamaServisi` ile CSV formatında kalıcı depolama sağlanmıştır.
