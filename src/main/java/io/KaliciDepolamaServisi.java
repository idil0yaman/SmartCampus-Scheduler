package io;

/*
 * ============================================================
 *  SOYUTLAMA (Abstraction) — NYP & SOLID
 * ============================================================
 *
 *  SOLID — Interface Segregation Principle (ISP):
 *  Bu arayüz yalnızca "kaydet" ve "yukle" metodlarını tanımlar.
 *  Her servis kendi ihtiyacına göre yalnızca bu sözleşmeyi
 *  uygular; gereksiz metotlara bağımlı olmaz.
 *
 *  SOLID — Dependency Inversion Principle (DIP):
 *  Üst katman sınıflar (servisler, menüler) somut CSV/JSON
 *  sınıflarına değil, bu soyut arayüze bağlanır.  Böylece
 *  kayıt formatı değiştiğinde üst katman kodu değişmez;
 *  sadece bu arayüzü uygulayan yeni bir sınıf yazılır.
 * ============================================================
 */

/**
 * Kalıcı veri saklama için genel sözleşme (contract).
 * Tüm okuma/yazma sınıfları bu arayüzü uygulamalıdır.
 */
public interface KaliciDepolamaServisi {

    /**
     * Uygulama verilerini kalıcı ortama yazar.
     *
     * @throws java.io.IOException yazma hatası durumunda
     */
    void kaydet() throws java.io.IOException;

    /**
     * Kalıcı ortamdan verileri okuyup belleğe yükler.
     *
     * @throws java.io.IOException okuma hatası durumunda
     */
    void yukle() throws java.io.IOException;
}
