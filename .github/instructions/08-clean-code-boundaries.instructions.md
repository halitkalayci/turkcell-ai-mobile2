---
description: Clean Code Bölüm 8 — Sınırlar; 3rd-party kütüphane entegrasyonu, learning test ve adapter/wrapper kullanımı.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 8: Sınırlar

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 8: Boundaries

---

## 1. Sınır Nedir?

Sistemimizin kendi yazdığımız kodla başkalarının (3rd-party kütüphane, dış servis, framework) yazdığı kod arasındaki ayrım çizgisidir. Bu çizgi iyi yönetilmezse dış kod iç mimarimizi kirletir.

> **"Dışarıdan satın alınan, indirilen ya da başka takımlar tarafından üretilen kod sınırlarında sorunlar yaşıyoruz."**

---

## 2. 3rd-Party Kodunu Sarma (Using Third-Party Code)

Dış kütüphanelerin geniş API'si kendi ihtiyacımızdan fazlasını sunar. Bu fazlalık karmaşıklık yaratır ve değişime karşı kırılganlık üretir.

**Kural:** `Map`, `List` gibi koleksiyon tiplerini ya da herhangi bir 3rd-party nesneyi doğrudan dışarıya açma; wrapper/adapter ile sınır çiz.

```java
// YANLIŞ: Map doğrudan metodun dışına taşıyor
public Map<UUID, Product> getProductMap() {
    return productRepository.findAllAsMap(); // dışarı açık Map
}

// Çağıran istemediği metodlara erişebilir: clear(), putAll() ...

// DOĞRU: Wrapper sınıfla kapsülle
public class ProductCatalog {
    private final Map<UUID, Product> catalog = new HashMap<>();

    public void addProduct(Product product) {
        catalog.put(product.getId(), product);
    }

    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(catalog.get(id));
    }

    public List<Product> all() {
        return List.copyOf(catalog.values());
    }
}
```

---

## 3. Sınırları Araştırma (Exploring and Learning Boundaries)

Yeni bir 3rd-party kütüphaneyi doğrudan üretim koduna entegre etmek risklidir. Önce kütüphaneyi **öğrenme testiyle** (learning test) keşfet.

**Kural:** Learning testleri yazarak kütüphanenin davranışını belgele; öğrenme sürecinde üretim kodu kirlenmesin.

```java
// Örnek: Spring RestTemplate davranışını öğrenme testi
@Test
public void learningTest_restTemplateThrowsOnNon2xx() {
    RestTemplate restTemplate = new RestTemplate();
    assertThrows(HttpClientErrorException.class, () ->
        restTemplate.getForObject("https://httpbin.org/status/404", String.class)
    );
}

// Bu test:
// 1) Kütüphanenin davranışını belgeler
// 2) Kütüphane güncellendiğinde hâlâ doğru çalıştığını doğrular
// 3) Üretim kodunu kirletmez
```

---

## 4. Henüz Var Olmayan Kodu Kullanma (Using Code That Does Not Yet Exist)

Bazen sistemin bir parçası henüz yazılmamıştır. Bilinen sınıra kadar kendi istediğimiz arayüzü tanımlarız, gerçek implementasyon gelince adaptör yazarız.

```java
// Senaryo: Harici stok servisi henüz hazır değil

// 1) Kendi istediğimiz arayüzü tanımlıyoruz
public interface StockService {
    int getAvailableStock(String sku);
    void reserveStock(String sku, int quantity);
}

// 2) Geçici stub ile geliştirmeye devam ediyoruz
public class StockServiceStub implements StockService {
    @Override
    public int getAvailableStock(String sku) { return 100; }

    @Override
    public void reserveStock(String sku, int quantity) { /* no-op */ }
}

// 3) Gerçek servis gelince adaptör yazılır
public class ExternalStockServiceAdapter implements StockService {
    private final ExternalStockClient client;

    @Override
    public int getAvailableStock(String sku) {
        return client.queryInventory(sku).getQuantity();
    }

    @Override
    public void reserveStock(String sku, int quantity) {
        client.reserve(new ReservationPayload(sku, quantity));
    }
}
```

---

## 5. Temiz Sınırlar (Clean Boundaries)

Değişim sınırda olur. İyi tasarlanmış bir yazılım, değişimlere en az hasarla adapte olur. Sınırımızdaki kodu minimize ederek ve adaptör katmanına hapsederek değişimin yayılmasını önleriz.

**Kural:**
- 3rd-party tiplerini alan parametreleri ve dönüş tiplerini API yüzeyine **çıkarma**.
- Sınıra ait kodu tek bir sınıfa ya da pakete topla.
- Üretim kodu, sınır nesnelerini mümkün olduğunca az yerde referans almalıdır.

```java
// YANLIŞ: Jackson ObjectNode doğrudan iş katmanına sızıyor
@Service
public class ProductImportService {
    public void importFromJson(ObjectNode node) { // Jackson tipi sızdı
        String name = node.get("name").asText();
        ...
    }
}

// DOĞRU: Jackson iş katmanına sızmıyor; sınırda dönüşüm yapılıyor
@Service
public class ProductImportService {
    public void importFromRequest(ProductRequest request) { // kendi tipimiz
        ...
    }
}

@Component
public class ProductJsonBoundary {
    private final ObjectMapper mapper;

    public ProductRequest parse(String json) {
        try {
            return mapper.readValue(json, ProductRequest.class);
        } catch (JsonProcessingException e) {
            throw new InvalidProductDataException("Geçersiz JSON formatı", e);
        }
    }
}
```

---

## 6. Adapter Deseni ile Sınır Yönetimi

Dış sistemlerin interface'leri kendi tasarım kararlarımızdan farklıdır. Adapter deseni bu farkı yönetir.

```java
// Dış ödeme kütüphanesinin API'si (bizim kontrolümüzde değil)
public class ThirdPartyPaymentGateway {
    public PaymentResult processCharge(String cardToken, long amountInCents, String currency) { ... }
}

// Bizim istediğimiz arayüz
public interface PaymentService {
    void chargeCustomer(UUID customerId, BigDecimal amount);
}

// Adapter: Dış API'yi bizim arayüzümüze uyarlar
@Component
public class PaymentGatewayAdapter implements PaymentService {
    private final ThirdPartyPaymentGateway gateway;
    private final CustomerRepository customerRepository;

    @Override
    public void chargeCustomer(UUID customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        long amountInCents = amount.movePointRight(2).longValueExact();
        PaymentResult result = gateway.processCharge(customer.getCardToken(), amountInCents, "TRY");
        if (!result.isSuccess()) {
            throw new PaymentFailedException(result.getErrorMessage());
        }
    }
}
```

---

## 7. Özet — Bölüm 8 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Map/Koleksiyon sarma | Dış koleksiyonları doğrudan açma; wrapper yaz |
| Learning testi | 3rd-party kütüphaneyi önce izole testle öğren |
| Bilinmeyen sınır | Kendi istediğin arayüzü tanımla; stub → adaptör |
| Sınır kodu izolasyonu | 3rd-party tipleri tek pakette/sınıfta tut |
| Adaptör deseni | Dış API'yi kendi arayüzüne uyarla |
| API yüzeyine sızdırma | 3rd-party tipler parametre/dönüş tipine giremez |
