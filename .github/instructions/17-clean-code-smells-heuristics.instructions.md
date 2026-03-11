---
description: Clean Code Bölüm 17 — Kokular ve Sezgisel Kurallar; C/F/G/J/N/T kategorili kod koku kataloğu ve çözüm rehberi.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 17: Kokular ve Sezgisel Kurallar

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 17: Smells and Heuristics

---

## Kategori Kısaltmaları

| Kısaltma | Alan |
|---|---|
| **C** | Comments (Yorumlar) |
| **E** | Environment (Ortam) |
| **F** | Functions (Fonksiyonlar) |
| **G** | General (Genel) |
| **J** | Java |
| **N** | Names (İsimler) |
| **T** | Tests (Testler) |

---

## C — Yorum Kokuları

**C1 — Uygunsuz Bilgi:** Yorum, kaynak kontrolü, takvim ve yapılandırma sistemlerinde tutulması gereken bilgiyi içermemelidir.
```java
// YANLIŞ: C1
// Son değişiklik: 2024-01-10, yazar: ali@turkcell.com  ← git blame bilgisi
```

**C2 — Geçersiz Yorum:** Kodu yanlış tanımlayan yorum silinmeli ya da güncellenmeli.
```java
// YANLIŞ: C2 — metot fiyat döndürüyor; yorum "stok" diyor
// Mevcut stok miktarını döndürür
public BigDecimal getPrice() { return price; }
```

**C3 — Gereksiz Yorum:** Kodu tekrarlayan yorum.
```java
// YANLIŞ: C3
// Ürünü kaydeder
productRepository.save(product);
```

**C4 — Kötü Yazılmış Yorum:** Dilbilgisi hataları, noktalama eksikliği olan yorum düzeltilmeli ya da silinmeli.

**C5 — Yoruma Alınmış Kod:** Yoruma alınmış kod derhal silinmeli; git geri getirir.
```java
// YANLIŞ: C5
// product.setDiscount(0.10);
productRepository.save(product);
```

---

## E — Ortam Kokuları

**E1 — Birden Fazla Adımda Build:** Build tek komutla gerçekleşmelidir.
```shell
# DOĞRU: Tek komut
mvn clean package
```

**E2 — Birden Fazla Adımda Test:** Tüm testler tek komutla çalışmalıdır.
```shell
# DOĞRU
mvn test
```

---

## F — Fonksiyon Kokuları

**F1 — Çok Parametre:** Fonksiyon 3'ten fazla argüman almamalıdır. Fazlası nesneyle paketlenmeli.
```java
// YANLIŞ: F1
public ProductResponse createProduct(String name, BigDecimal price, int stock, String sku, String category) { ... }

// DOĞRU
public ProductResponse createProduct(ProductRequest request) { ... }
```

**F2 — Çıktı Argümanı:** Çıktı için parametre değiştirmek yerine geri dön.
```java
// YANLIŞ: F2 — listeyi parametre üzerinden değiştiriyor
public void appendToResponse(List<ProductResponse> result, Product product) {
    result.add(toResponse(product));
}

// DOĞRU
public ProductResponse toResponse(Product product) { ... }
```

**F3 — Bayrak Argümanı:** Boolean parametre fonksiyonun iki iş yaptığını söyler. Yasakla.
```java
// YANLIŞ: F3
public void processProduct(Product product, boolean isNew) { ... }
// DOĞRU
public void createProduct(ProductRequest req) { ... }
public void updateProduct(UUID id, ProductRequest req) { ... }
```

**F4 — Ölü Fonksiyon:** Hiçbir yerden çağrılmayan metot silinmeli.

---

## G — Genel Kokular

**G1 — Birden Fazla Dilde Aynı Kaynak Dosya:** Java dosyasında JavaScript ya da XML gömülü olmamalıdır.

**G2 — Belirgin Davranış Uygulanmamış (Principle of Least Surprise):** Fonksiyon adının ima ettiği her davranış gerçekleştirilmelidir.
```java
// YANLIŞ: G2 — getProductById sessizce null dönüyor; exception beklenir
public ProductResponse getProductById(UUID id) {
    return productRepository.findById(id).map(this::toResponse).orElse(null);
}
// DOĞRU
public ProductResponse getProductById(UUID id) {
    return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

**G3 — Sınır Davranışı Test Edilmemiş:** Algoritmanın her köşe durumu için test yazılmalıdır (boş liste, null, sınır değeri).

**G4 — Güvenlik Ayarları Geçersiz Kılınmış:** `@SuppressWarnings`, `// TODO: add security later` gibi geçici bypas'lar bırakılmamalıdır.

**G5 — Tekrar (DRY):** Her bilgi parçasının sistemde tek bir temsili olmalıdır.

**G6 — Yanlış Soyutlama Seviyesinde Kod:** Yüksek seviye soyutlama ile düşük seviye detaylar ayrı tutulmalıdır.
```java
// YANLIŞ: G6 — interface içinde uygulama detayı
public interface ProductService {
    List<ProductResponse> getAllProducts();
    // Bu SQL detayı arayüze ait değil:
    default String buildQuery() { return "SELECT * FROM products"; }
}
```

**G7 — Temel Sınıfın Türetilenlerden Haberdar Olması:** Base class, alt sınıfını import etmemelidir.

**G8 — Çok Fazla Bilgi:** Bir modül diğerine minimum bilgi sunmalıdır (düşük coupling). Az metot, az değişken, az parametre.

**G9 — Ölü Kod:** Çalışmayan, ulaşılamayan veya hiç çağrılmayan kod silinmelidir.
```java
// YANLIŞ: G9
public void legacyImport() { /* artık kullanılmıyor */ }
```

**G10 — Dikey Ayrım:** Değişkenler ve fonksiyonlar kullanıldıkları yere yakın tanımlanmalıdır.

**G11 — Tutarsızlık:** Benzer işler benzer şekilde yapılmalıdır. Bir yerde `get`, başka yerde `retrieve` kullanma.

**G12 — Clutter (Gürültü):** Kullanılmayan değişken, metot, yorum, import temizlenmelidir.

**G13 — Yapay Bağımlılık:** Doğrudan bağımlılığı olmayan şeyler aynı sınıfa konmamalıdır.

**G14 — Feature Envy:** Bir metot, kendi sınıfından çok başka bir sınıfın verilerini kullanıyorsa oraya taşınmalıdır.
```java
// YANLIŞ: G14 — OrderService, Product'un metodlarını çok kullanıyor
public class OrderService {
    public BigDecimal calculateTotal(Order order) {
        return order.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(order.getProduct().getTaxRate()))
                .add(order.getProduct().getShippingCost()); // hep Product'a erişiyor
    }
}
// DOĞRU: Bu sorumluluk Product'a ait
public class Product {
    public BigDecimal getFinalPrice() { ... }
}
```

**G15 — Seçici Argümanlar:** Seçici boolean, int, null argümanlar bayrak argümanıdır; listeyi birden fazla metoda böl.

**G16 — Belirsiz Niyet:** Sihirli sayılar, kısaltmalar, Macar notasyonu niyeti gizler.
```java
// YANLIŞ: G16
int d = 86400; // saniye mi, milisaniye mi?
// DOĞRU
static final int SECONDS_PER_DAY = 86_400;
```

**G17 — Yanlış Konumdaki Sorumluluk:** Kod, okuyucunun beklediği yerde bulunmalıdır. `Math.PI` nereye ait?

**G18 — Uygunsuz Static:** Sınıf olmadan da anlam taşıyan metotları static yapma.

**G19 — Açıklayıcı Değişken Kullan:**
```java
// YANLIŞ: G19
if (product.getStock() > 0 && product.isActive() && product.getPrice().compareTo(BigDecimal.ZERO) > 0) { ... }

// DOĞRU
boolean isAvailable = product.getStock() > 0;
boolean isActive = product.isActive();
boolean hasPricing = product.getPrice().compareTo(BigDecimal.ZERO) > 0;
if (isAvailable && isActive && hasPricing) { ... }
```

**G20 — İşlev İsmine Kodlama:** Fonksiyon adı ne yapıldığını anlatır; nasıl yapıldığını değil.

**G21 — Algoritmaları Anla:** Tanımlayamadığın bir algoritmayı implemente etme; önce anla, sonra yaz.

**G22 — Mantıksal Bağımlılığı Fiziksel Yap:** Bir modül başka bir modüle bağımlıysa bunu açıkça ifade etsin.

**G23 — If/Else Yerine Polimorfizm:**
```java
// YANLIŞ: G23
if (product.getType().equals("DIGITAL")) { ... }
else if (product.getType().equals("PHYSICAL")) { ... }

// DOĞRU: Polimorfizm
product.process(); // Digital ve Physical kendi implement eder
```

**G24 — Standart Kuralları Takip Et:** Takım standartları kodun her yerinde tutarlı uygulanır.

**G25 — Sihirli Sayı Yerine İsimlendirilmiş Sabit:**
```java
static final int MAX_RETRY_COUNT = 3;
static final BigDecimal VAT_RATE = new BigDecimal("0.18");
```

**G26 — Koşulları Kapsülle:**
```java
// YANLIŞ
if (product.getStock() > 0 && !product.isDiscontinued()) { ... }
// DOĞRU
if (product.isAvailableForPurchase()) { ... }
```

**G27 — Negatif Koşuldan Kaçın:**
```java
// YANLIŞ
if (!product.isNotAvailable()) { ... }
// DOĞRU
if (product.isAvailable()) { ... }
```

**G28 — Fonksiyon Tek İş Yapmalı:** G28 = F bölümünü teyit eder.

**G29 — Gizli Temporal Bağlantı:** Fonksiyonların belirli sırada çağrılması gerekiyorsa bu sıra API'de görünür olmalıdır.

**G30 — Tutarsız Koşullar:** Koşul ifadelerinde aynı kavrama aynı ifade kullanılmalı.

**G31 — Sınır Koşulu Kapsülle:** Sınır koşulları (n+1, n-1) tek yerde hesaplanmalı.

**G32 — Fonksiyonlar Yalnızca Bir Soyutlama Seviyesi Aşağıya:** Her fonksiyon bir seviye alt soyutlamayı çağırmalıdır.

**G33 — Konfigürasyonu Yüksek Tutularda Tut:** Sabitler, varsayılan değerler en yüksek soyutlama seviyesinde tutulur.

**G34 — Transitive Navigation Kaçın (Law of Demeter):** `a.getB().getC()` zincirleme erişimden kaçın.

---

## J — Java Kokuları

**J1 — Wildcard Import Kullanma:**
```java
// YANLIŞ: J1
import java.util.*;
// DOĞRU
import java.util.List;
import java.util.UUID;
```

**J2 — Long Import Listesi:** 5'ten fazla aynı paketin sınıfı kullanılıyorsa wildcard düşünülebilir (genel kural ile çelişir; takım kararı).

**J3 — Constants Enum Yerine static final:** Enum kullan; string/int sabit yerine.
```java
// YANLIŞ: J3
public static final String STATUS_ACTIVE = "ACTIVE";
// DOĞRU
public enum ProductStatus { ACTIVE, INACTIVE, DISCONTINUED }
```

---

## N — İsim Kokuları

**N1 — Açıklayıcı İsimler:** → Bkz. `00-clean-code-naming.instructions.md`

**N2 — Uygun Soyutlama Seviyesinde İsim:** Metodun soyutlama seviyesine uygun isim ver; uygulama detayı isime girmesin.

**N3 — Mümkün Olduğunca Standart İsim:** Tasarım deseni adlarını kullan: `Factory`, `Strategy`, `Adapter`, `Repository`.

**N4 — Net Kesinliği:** Belirsiz isim (`handle`, `process`, `manage`) yerine spesifik (`parseProductFromCsv`, `validateSkuFormat`).

**N5 — Uzun Kapsam — Uzun İsim:** Geniş kapsamda kullanılan değişken uzun isme, döngü sayacı kısa isme sahip olabilir.

**N6 — Kodlamadan Kaçın:** Macar notasyonu ve `I` ön eki yasak.

**N7 — İsim Yan Etki Gizlemesin:**
```java
// YANLIŞ: N7 — isim sadece "get" ama session da açıyor
public Session getActiveSession(User user) {
    Session s = sessionPool.acquire();  // yan etki: kaynak tahsisi
    return s;
}
// DOĞRU
public Session openSession(User user) { ... }
```

---

## T — Test Kokuları

**T1 — Yetersiz Test:** Her olası dalı ve sınır durumu test edilmelidir.

**T2 — Kapsam Analizi Kullan:** Hangi satırların test edilmediğini görmek için coverage raporu kullan.

**T3 — Trivial Testleri Atlatma:** Önemsiz görünen testler belgeleme görevi görür; atlatma.

**T4 — Görmezden Gelinen Test Sinyaldir:** `@Disabled` test, belirsizlik anlamına gelir; açıkla.

**T5 — Sınır Durumlarını Test Et:** Boş liste, null, min/max değer, sıfır.

**T6 — Bug'ı Bulunca Test Yaz:** Bug düzeltmeden önce onu gösteren test yaz.

**T7 — Başarısızlık Kalıplarına Bak:** Başarısız testler gruplanmış belirli girdilerle ilgiliyse bu bir iz verir.

**T8 — Kapsam İpuçlarına Bak:** Test kapsamı düşük olan bölgeler sorun barındırabilir.

**T9 — Testler Hızlı Olmalı:** Yavaş test çalıştırılmaz; çalıştırılmayan test değersizdir.

---

## Özet — Bölüm 17 Kategori Tablosu

| Kategori | Temel Kural |
|---|---|
| C (Yorumlar) | Yoruma alınmış kod, geçersiz yorum → sil |
| E (Ortam) | Build ve test tek komutla yapılır |
| F (Fonksiyonlar) | Max 3 parametre, tek iş, bayrak argümanı yasak |
| G (Genel) | DRY, polimorfizm, isimlendirilmiş sabit, negatif koşul yasak |
| J (Java) | Wildcard import yasak; String sabit → enum |
| N (İsimler) | Açıklayıcı, tutarlı, kodlama içermez |
| T (Testler) | Sınır durumlar, hızlı, yüksek kapsam |
