---
description: Clean Code Bölüm 13 — Eşzamanlılık; SRP, paylaşılan veri koruması, test stratejisi ve Java eşzamanlılık kütüphaneleri.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 13: Eşzamanlılık

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 13: Concurrency

---

## 1. Eşzamanlılık Neden Zordur?

Eşzamanlı kod; yalıtılmış ortamda mükemmel çalışırken, birden fazla thread devreye girdiğinde bozulabilir. Martin'e göre eşzamanlılık şunları yanıltır:

- Performansı her zaman iyileştireceği düşüncesi (bazen iyileştirir, bazen hayır)
- Tasarımı değiştirmediği düşüncesi (ciddi biçimde değiştirir)
- Web framework'ün thread sorunlarını çözeceği düşüncesi (çözmez; uygulama kodunuz da paylaşılan state oluşturabilir)

---

## 2. Eşzamanlılık Mitleri

| Mit | Gerçek |
|---|---|
| "Eşzamanlılık her zaman performansı iyileştirir" | Yalnızca bekleme süresi uzun işleri paralel yürütürken iyileştirir |
| "Tasarım eşzamanlılıkla değişmez" | Ne yapıyı ne davranışı büyük ölçüde değiştirir |
| "Framework thread güvenliğini garanti eder" | Sadece kendi kodunuzu thread-safe yazmanız gerekir |

---

## 3. Eşzamanlılık için SRP (Single Responsibility Principle)

Eşzamanlılık karmaşıklığı kendi başına yeterince büyüktür; iş mantığıyla aynı sınıfa sıkıştırılmamalıdır.

**Kural:** Thread yönetimi ile iş mantığı ayrı sınıflarda olmalıdır.

```java
// YANLIŞ: Thread yönetimi ve iş mantığı aynı sınıfta
@Service
public class ProductSyncService {
    public void syncAll(List<Product> products) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Product product : products) {
            executor.submit(() -> {
                // iş mantığı + thread yönetimi iç içe
                validate(product);
                productRepository.save(product);
            });
        }
        executor.shutdown();
    }
}

// DOĞRU: İş mantığı ve thread yönetimi ayrı sınıflar
@Service
@RequiredArgsConstructor
public class ProductSyncService {
    private final ProductValidator validator;
    private final ProductRepository productRepository;

    public void syncProduct(Product product) { // thread bilmez
        validator.validate(product);
        productRepository.save(product);
    }
}

@Component
@RequiredArgsConstructor
public class ProductSyncOrchestrator {
    private final ProductSyncService syncService;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void syncAll(List<Product> products) {
        products.forEach(p -> executor.submit(() -> syncService.syncProduct(p)));
    }
}
```

---

## 4. Veri Kapsamını Sınırla (Limit the Scope of Data)

İki thread aynı alan nesnesini güncellediğinde kritik bölge (critical section) oluşur. `synchronized` blokların sayısını minimize et; paylaşım yüzeyi küçüldükçe sorun azalır.

**Kural:**
- Paylaşılan veriyi mümkün olduğunca encapsulate et.
- `synchronized` blokları küçük ve odaklı tut.
- Mümkünse immutable nesneler kullan.

```java
// YANLIŞ: Büyük synchronized blok — gereksiz serileştirme
public synchronized ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku()); // okuma — lock gerekmeyebilir
    Product saved = productRepository.save(toEntity(request)); // yazma
    sendNotification(saved); // I/O — lock kesinlikle gerekmez
    return toResponse(saved);
}

// DOĞRU: Lock kapsamını daralelt
public ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku()); // kilitsiz okuma
    Product saved;
    synchronized (this) {
        saved = productRepository.save(toEntity(request)); // yalnızca yazma kilitli
    }
    sendNotification(saved); // kilit dışında
    return toResponse(saved);
}
```

---

## 5. Kopya Veri Kullan (Use Copies of Data)

Paylaşılan veriyi kilitlemek yerine bazen kopyasını kullanmak daha güvenlidir.

```java
// DOĞRU: Snapshot ile thread-safe okuma
public List<ProductResponse> getAllProducts() {
    List<Product> snapshot = List.copyOf(productRepository.findAll()); // değiştirilemez kopya
    return snapshot.stream().map(this::toResponse).toList();
}
```

---

## 6. Thread'leri Bağımsız Yap (Threads Should Be as Independent as Possible)

Her thread kendi veri dünyasını taşımalı; paylaşılan kaynağa mümkün olduğunca az dokunmalıdır.

```java
// DOĞRU: Her thread kendi request/response döngüsünü kapatır (Spring request scope)
// Spring zaten her HTTP isteğini ayrı thread'de bağımsız olarak işler
// Servis metodları stateless kalmalıdır

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository; // Spring bean — thread-safe

    // Metot parametresi üzerinden çalışıyor — paylaşılan state yok
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        assertSkuIsUnique(request.getSku());
        return toResponse(productRepository.save(toEntity(request)));
    }
}
```

---

## 7. Java Eşzamanlılık Kütüphanesi Tavsiyeleri

**Kural:** Temel `synchronized` yerine `java.util.concurrent` kütüphanesini tercih et.

| Durum | Kullanılacak Araç |
|---|---|
| Thread-safe map | `ConcurrentHashMap` |
| Üretici-tüketici kuyruğu | `BlockingQueue` |
| Atomik sayaçlar | `AtomicInteger`, `AtomicLong` |
| Birden fazla thread bekle | `CountDownLatch`, `CyclicBarrier` |
| Thread havuzu | `ExecutorService`, `@Async` |
| Komplex kilit | `ReentrantLock` |

```java
// YANLIŞ: synchronized ile thread-safe map
private final Map<String, Product> cache = new HashMap<>();
public synchronized Product getFromCache(String sku) { return cache.get(sku); }
public synchronized void putToCache(String sku, Product product) { cache.put(sku, product); }

// DOĞRU: ConcurrentHashMap
private final Map<String, Product> cache = new ConcurrentHashMap<>();
public Product getFromCache(String sku) { return cache.get(sku); }
public void putToCache(String sku, Product product) { cache.put(sku, product); }
```

---

## 8. Eşzamanlı Kod Test Etme Tavsiyeleri

**Kural:** Eşzamanlı kodu yalıtılmış birim testlerle yazmak yeterli değildir. Ek stratejiler gerekir.

| Strateji | Açıklama |
|---|---|
| İş mantığını ayrıştır | Thread bağımsız kodun birim testini yaz |
| Yük testi | Birden fazla thread ile stres altında çalıştır |
| Farklı ortam | Tek CPU, çok CPU farklı davranır; her ikisinde test et |
| Çoklu iterasyon | Yarış koşulları (race condition) nadir tetiklenir; testleri döngüde çalıştır |
| `Thread.yield()` | Test sırasında iş parçacığı geçişini tetikle |

```java
// Eşzamanlı bug'ı yakalamak için stress test örneği
@Test
@RepeatedTest(100) // 100 kez çalıştır, race condition tetiklenebilir
public void concurrentSkuCheck_doesNotAllowDuplicate() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
    List<Exception> errors = new CopyOnWriteArrayList<>();

    Runnable task = () -> {
        try {
            productService.createProduct(aRequest().withSku("RACE-SKU").build());
        } catch (SkuAlreadyExistsException e) {
            errors.add(e);
        } finally {
            latch.countDown();
        }
    };

    new Thread(task).start();
    new Thread(task).start();
    latch.await();

    // Tam olarak 1 kayıt oluşturulmuş olmalı
    assertThat(productRepository.countBySku("RACE-SKU")).isEqualTo(1);
}
```

---

## 9. Özet — Bölüm 13 Temel Kurallar

| Kural | Açıklama |
|---|---|
| SRP for concurrency | Thread yönetimi ve iş mantığı ayrı sınıfta |
| Paylaşım yüzeyini küçült | Encapsulate; synchronized blok minimal |
| Immutable tercih | Değiştirilemez nesnelerde race condition olmaz |
| java.util.concurrent | Temel synchronized yerine yüksek seviye araçlar |
| Stateless service | Spring servisleri stateless olmalıdır |
| Yük testi | Birim test + stres test kombinasyonu |
| Tekrarlı test | Race condition için testleri döngüde çalıştır |
