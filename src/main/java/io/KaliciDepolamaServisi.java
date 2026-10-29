package io;

import java.io.IOException;

/**
 * Verilerin kalıcı olarak saklanması ve geri yüklenmesi için
 * kullanılan arayüz. Bu sayede servis katmanı doğrudan somut dosya
 * sınıflarına bağlanmak yerine bu sözleşmeye bağlanır (Dependency Inversion).
 */
public interface KaliciDepolamaServisi {

    /**
     * Verileri kalıcı ortama kaydeder.
     * @throws IOException G/Ç hatası durumunda
     */
    void kaydet() throws IOException;

    /**
     * Kalıcı ortamdan verileri okuyup belleğe yükler.
     * @throws IOException G/Ç hatası durumunda
     */
    void yukle() throws IOException;
}
